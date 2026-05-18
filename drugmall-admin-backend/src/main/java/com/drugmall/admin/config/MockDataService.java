package com.drugmall.admin.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@ConditionalOnProperty(name = "drugmall.mock.enabled", havingValue = "true")
public class MockDataService {

    @Value("classpath:mock-data/admin-users.json")
    private Resource adminUsersResource;

    @Value("classpath:mock-data/products.json")
    private Resource productsResource;

    @Value("classpath:mock-data/categories.json")
    private Resource categoriesResource;

    @Value("classpath:mock-data/brands.json")
    private Resource brandsResource;

    @Value("classpath:mock-data/orders.json")
    private Resource ordersResource;

    @Value("classpath:mock-data/dashboard.json")
    private Resource dashboardResource;

    @Value("classpath:mock-data/finance.json")
    private Resource financeResource;

    @Value("classpath:mock-data/content-banners.json")
    private Resource bannersResource;

    @Value("classpath:mock-data/content-articles.json")
    private Resource articlesResource;

    @Value("classpath:mock-data/content-notices.json")
    private Resource noticesResource;

    @Value("classpath:mock-data/settings.json")
    private Resource settingsResource;

    @Value("classpath:mock-data/user-auth.json")
    private Resource userAuthResource;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, JsonNode> cache = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            log.info("开始加载管理后台模拟数据...");
            loadData("admin-users", adminUsersResource);
            loadData("products", productsResource);
            loadData("categories", categoriesResource);
            loadData("brands", brandsResource);
            loadData("orders", ordersResource);
            loadData("dashboard", dashboardResource);
            loadData("finance", financeResource);
            loadData("banners", bannersResource);
            loadData("articles", articlesResource);
            loadData("notices", noticesResource);
            loadData("settings", settingsResource);
            loadData("user-auth", userAuthResource);
            log.info("管理后台模拟数据加载完成，共 {} 个文件", cache.size());
        } catch (Exception e) {
            log.error("加载模拟数据失败", e);
        }
    }

    private void loadData(String key, Resource resource) throws IOException {
        if (resource != null && resource.exists()) {
            cache.put(key, objectMapper.readTree(resource.getInputStream()));
            log.info("加载: {} - {}", key, resource.getFilename());
        } else {
            log.warn("文件不存在: {}", key);
        }
    }

    public JsonNode get(String key) {
        return cache.get(key);
    }

    public JsonNode get(String key, String field) {
        JsonNode data = cache.get(key);
        return (data != null && data.has(field)) ? data.get(field) : null;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * 对 ArrayNode 进行分页
     */
    public ObjectNode paginate(ArrayNode array, int pageNum, int pageSize) {
        int total = array.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        int pages = (int) Math.ceil((double) total / pageSize);

        ArrayNode pageList = objectMapper.createArrayNode();
        for (int i = start; i < end; i++) {
            pageList.add(array.get(i));
        }

        ObjectNode result = objectMapper.createObjectNode();
        result.set("list", pageList);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("pages", pages);
        return result;
    }

    /**
     * 在 ArrayNode 中按关键词搜索（匹配指定字段）
     */
    public ArrayNode filterByKeyword(ArrayNode array, String keyword, String... fields) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return array;
        }
        String kw = keyword.trim().toLowerCase();
        ArrayNode filtered = objectMapper.createArrayNode();
        for (JsonNode node : array) {
            for (String field : fields) {
                if (node.has(field) && node.get(field).asText("").toLowerCase().contains(kw)) {
                    filtered.add(node);
                    break;
                }
            }
        }
        return filtered;
    }

    /**
     * 按字段精确匹配过滤
     */
    public ArrayNode filterByField(ArrayNode array, String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            return array;
        }
        ArrayNode filtered = objectMapper.createArrayNode();
        for (JsonNode node : array) {
            if (node.has(field) && node.get(field).asText("").equals(value)) {
                filtered.add(node);
            }
        }
        return filtered;
    }

    /**
     * 按数值字段过滤
     */
    public ArrayNode filterByIntField(ArrayNode array, String field, Integer value) {
        if (value == null) {
            return array;
        }
        ArrayNode filtered = objectMapper.createArrayNode();
        for (JsonNode node : array) {
            if (node.has(field) && node.get(field).asInt() == value) {
                filtered.add(node);
            }
        }
        return filtered;
    }
}
