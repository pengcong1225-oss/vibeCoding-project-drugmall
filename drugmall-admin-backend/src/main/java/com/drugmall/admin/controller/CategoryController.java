package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import com.drugmall.admin.config.MockDataService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final MockDataService mockDataService;

    @GetMapping
    public Result<JsonNode> getCategoryList() {
        return Result.success(mockDataService.get("categories"));
    }

    @PostMapping
    public Result<Object> createCategory(@RequestBody JsonNode body) {
        return Result.success(java.util.Map.of("id", String.valueOf(System.currentTimeMillis())));
    }

    @PutMapping("/{id}")
    public Result<Void> updateCategory(@PathVariable String id, @RequestBody JsonNode body) {
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable String id) {
        return Result.success();
    }
}
