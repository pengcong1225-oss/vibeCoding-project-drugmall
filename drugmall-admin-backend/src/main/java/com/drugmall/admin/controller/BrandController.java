package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import com.drugmall.admin.config.MockDataService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/brands")
@RequiredArgsConstructor
public class BrandController {

    private final MockDataService mockDataService;

    @GetMapping
    public Result<ObjectNode> getBrandList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        ArrayNode brands = (ArrayNode) mockDataService.get("brands");
        if (brands == null) return Result.success(mockDataService.getObjectMapper().createObjectNode());

        ArrayNode filtered = mockDataService.filterByKeyword(brands, keyword, "brandName");
        return Result.success(mockDataService.paginate(filtered, pageNum, pageSize));
    }

    @PostMapping
    public Result<Object> createBrand(@RequestBody JsonNode body) {
        return Result.success(java.util.Map.of("id", String.valueOf(System.currentTimeMillis())));
    }

    @PutMapping("/{id}")
    public Result<Void> updateBrand(@PathVariable String id, @RequestBody JsonNode body) {
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBrand(@PathVariable String id) {
        return Result.success();
    }
}
