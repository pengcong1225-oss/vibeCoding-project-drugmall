package com.drugmall.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 模拟数据服务
 * 加载所有JSON模拟数据文件
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "drugmall.mock.enabled", havingValue = "true")
public class MockDataService {

    // 用户相关数据
    @Value("classpath:mock-data/users.json")
    private Resource usersResource;

    // 就诊人数据
    @Value("classpath:mock-data/patients.json")
    private Resource patientsResource;

    // 地址数据
    @Value("classpath:mock-data/addresses.json")
    private Resource addressesResource;

    // 购物车数据
    @Value("classpath:mock-data/carts.json")
    private Resource cartsResource;

    // 订单数据
    @Value("classpath:mock-data/orders.json")
    private Resource ordersResource;

    // 优惠券数据
    @Value("classpath:mock-data/coupons.json")
    private Resource couponsResource;

    // FAQ数据
    @Value("classpath:mock-data/faqs.json")
    private Resource faqsResource;

    // 评价数据
    @Value("classpath:mock-data/reviews.json")
    private Resource reviewsResource;

    // 物流数据
    @Value("classpath:mock-data/logistics.json")
    private Resource logisticsResource;

    // 退款数据
    @Value("classpath:mock-data/refunds.json")
    private Resource refundsResource;

    // 医生端数据
    @Value("classpath:mock-data/doctor-data.json")
    private Resource doctorDataResource;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, JsonNode> mockDataCache = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            log.info("开始加载模拟数据...");

            // 加载用户相关数据
            loadMockData("users", usersResource);
            loadMockData("patients", patientsResource);
            loadMockData("addresses", addressesResource);

            // 加载购物车数据
            loadMockData("carts", cartsResource);

            // 加载订单相关数据
            loadMockData("orders", ordersResource);
            loadMockData("logistics", logisticsResource);
            loadMockData("refunds", refundsResource);

            // 加载优惠券数据
            loadMockData("coupons", couponsResource);

            // 加载药品相关数据
            loadMockData("faqs", faqsResource);
            loadMockData("reviews", reviewsResource);

            // 加载医生端数据
            loadMockData("doctor-data", doctorDataResource);

            log.info("模拟数据加载完成，共加载 {} 个数据文件", mockDataCache.size());
        } catch (Exception e) {
            log.error("加载模拟数据失败", e);
        }
    }

    private void loadMockData(String key, Resource resource) throws IOException {
        if (resource != null && resource.exists()) {
            JsonNode jsonNode = objectMapper.readTree(resource.getInputStream());
            mockDataCache.put(key, jsonNode);
            log.info("加载模拟数据: {} - {}", key, resource.getFilename());
        } else {
            log.warn("模拟数据文件不存在: {}", key);
        }
    }

    /**
     * 获取指定类型的数据
     */
    public JsonNode getData(String dataType) {
        return mockDataCache.get(dataType);
    }

    /**
     * 获取指定类型数据中的特定字段
     */
    public JsonNode getData(String dataType, String fieldName) {
        JsonNode data = mockDataCache.get(dataType);
        if (data != null && data.has(fieldName)) {
            return data.get(fieldName);
        }
        return null;
    }

    // ============== 便捷方法 ==============

    public JsonNode getUsers() {
        return mockDataCache.get("users");
    }

    public JsonNode getPatients() {
        return mockDataCache.get("patients");
    }

    public JsonNode getAddresses() {
        return mockDataCache.get("addresses");
    }

    public JsonNode getCarts() {
        return mockDataCache.get("carts");
    }

    public JsonNode getOrders() {
        return mockDataCache.get("orders");
    }

    public JsonNode getCoupons() {
        return mockDataCache.get("coupons");
    }

    public JsonNode getFaqs() {
        return mockDataCache.get("faqs");
    }

    public JsonNode getReviews() {
        return mockDataCache.get("reviews");
    }

    public JsonNode getLogistics() {
        return mockDataCache.get("logistics");
    }

    public JsonNode getRefunds() {
        return mockDataCache.get("refunds");
    }

    public JsonNode getDoctorData() {
        return mockDataCache.get("doctor-data");
    }

    // ============== IM相关便捷方法 ==============

    /**
     * 获取IM会话数据
     * @param userId IM用户ID（如patient_1, doctor_DOC001）
     * @return 该用户的会话列表
     */
    public JsonNode getIMConversations(String userId) {
        JsonNode doctorData = getDoctorData();
        if (doctorData != null && doctorData.has("imConversations")) {
            JsonNode imConversations = doctorData.get("imConversations");
            if (imConversations.has(userId)) {
                return imConversations.get(userId);
            }
        }
        return null;
    }

    /**
     * 获取IM消息数据
     * @param conversationId 会话ID（如C2C_doctor_DOC001）
     * @return 该会话的消息列表
     */
    public JsonNode getIMMessages(String conversationId) {
        JsonNode doctorData = getDoctorData();
        if (doctorData != null && doctorData.has("imMessages")) {
            JsonNode imMessages = doctorData.get("imMessages");
            if (imMessages.has(conversationId)) {
                return imMessages.get(conversationId);
            }
        }
        return null;
    }
}
