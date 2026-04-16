package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import com.drugmall.admin.config.MockDataService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/content")
@RequiredArgsConstructor
public class ContentController {

    private final MockDataService mockDataService;

    // ========== Banner ==========
    @GetMapping("/banners")
    public Result<JsonNode> getBanners() {
        return Result.success(mockDataService.get("banners"));
    }

    @PostMapping("/banners")
    public Result<Object> createBanner(@RequestBody JsonNode body) {
        return Result.success(Map.of("id", String.valueOf(System.currentTimeMillis())));
    }

    @PutMapping("/banners/{id}")
    public Result<Void> updateBanner(@PathVariable String id, @RequestBody JsonNode body) {
        return Result.success();
    }

    @DeleteMapping("/banners/{id}")
    public Result<Void> deleteBanner(@PathVariable String id) {
        return Result.success();
    }

    @PatchMapping("/banners/{id}/status")
    public Result<Void> updateBannerStatus(@PathVariable String id, @RequestBody JsonNode body) {
        return Result.success();
    }

    @PutMapping("/banners/sort")
    public Result<Void> updateBannerSort(@RequestBody JsonNode body) {
        return Result.success();
    }

    // ========== Article ==========
    @GetMapping("/articles")
    public Result<ObjectNode> getArticles(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        ArrayNode articles = (ArrayNode) mockDataService.get("articles", "articles");
        if (articles == null) return Result.success(mockDataService.getObjectMapper().createObjectNode());

        ArrayNode filtered = mockDataService.filterByKeyword(articles, keyword, "title", "summary");
        filtered = mockDataService.filterByField(filtered, "categoryId", categoryId);
        filtered = mockDataService.filterByIntField(filtered, "status", status);
        return Result.success(mockDataService.paginate(filtered, pageNum, pageSize));
    }

    @GetMapping("/articles/{id}")
    public Result<JsonNode> getArticleDetail(@PathVariable String id) {
        ArrayNode articles = (ArrayNode) mockDataService.get("articles", "articles");
        if (articles != null) {
            for (JsonNode a : articles) {
                if (id.equals(a.get("id").asText())) return Result.success(a);
            }
        }
        return Result.error(404, "文章不存在");
    }

    @PostMapping("/articles")
    public Result<Object> createArticle(@RequestBody JsonNode body) {
        return Result.success(Map.of("id", String.valueOf(System.currentTimeMillis())));
    }

    @PutMapping("/articles/{id}")
    public Result<Void> updateArticle(@PathVariable String id, @RequestBody JsonNode body) {
        return Result.success();
    }

    @DeleteMapping("/articles/{id}")
    public Result<Void> deleteArticle(@PathVariable String id) {
        return Result.success();
    }

    @GetMapping("/articles/categories")
    public Result<JsonNode> getArticleCategories() {
        return Result.success(mockDataService.get("articles", "categories"));
    }

    @GetMapping("/articles/stats")
    public Result<Object> getArticleStats() {
        ArrayNode articles = (ArrayNode) mockDataService.get("articles", "articles");
        int total = 0, published = 0, recommended = 0, draft = 0;
        if (articles != null) {
            total = articles.size();
            for (JsonNode a : articles) {
                if (a.get("status").asInt() == 1) published++;
                if (a.has("isRecommend") && a.get("isRecommend").asInt() == 1) recommended++;
                if (a.get("status").asInt() == 0) draft++;
            }
        }
        return Result.success(Map.of("total", total, "published", published, "recommended", recommended, "draft", draft));
    }

    // ========== Notice ==========
    @GetMapping("/notices")
    public Result<ObjectNode> getNotices(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        ArrayNode notices = (ArrayNode) mockDataService.get("notices");
        if (notices == null) return Result.success(mockDataService.getObjectMapper().createObjectNode());

        ArrayNode filtered = mockDataService.filterByKeyword(notices, keyword, "title", "content");
        filtered = mockDataService.filterByField(filtered, "type", type);
        return Result.success(mockDataService.paginate(filtered, pageNum, pageSize));
    }

    @PostMapping("/notices")
    public Result<Object> createNotice(@RequestBody JsonNode body) {
        return Result.success(Map.of("id", String.valueOf(System.currentTimeMillis())));
    }

    @PutMapping("/notices/{id}")
    public Result<Void> updateNotice(@PathVariable String id, @RequestBody JsonNode body) {
        return Result.success();
    }

    @DeleteMapping("/notices/{id}")
    public Result<Void> deleteNotice(@PathVariable String id) {
        return Result.success();
    }

    @PatchMapping("/notices/{id}/top")
    public Result<Void> toggleNoticeTop(@PathVariable String id, @RequestBody JsonNode body) {
        return Result.success();
    }

    @GetMapping("/notices/stats")
    public Result<Object> getNoticeStats() {
        ArrayNode notices = (ArrayNode) mockDataService.get("notices");
        int total = 0, published = 0, top = 0, draft = 0;
        if (notices != null) {
            total = notices.size();
            for (JsonNode n : notices) {
                if (n.get("status").asInt() == 1) published++;
                if (n.has("isTop") && n.get("isTop").asInt() == 1) top++;
                if (n.get("status").asInt() == 0) draft++;
            }
        }
        return Result.success(Map.of("total", total, "published", published, "top", top, "draft", draft));
    }
}
