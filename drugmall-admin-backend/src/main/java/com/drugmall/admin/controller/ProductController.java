package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import com.drugmall.admin.config.MockDataService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductController {

    private final MockDataService mockDataService;

    @GetMapping
    public Result<ObjectNode> getProductList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String brandId,
            @RequestParam(required = false) Integer isRx,
            @RequestParam(required = false) Integer status) {
        ArrayNode products = (ArrayNode) mockDataService.get("products");
        if (products == null) return Result.success(mockDataService.getObjectMapper().createObjectNode());

        ArrayNode filtered = mockDataService.filterByKeyword(products, keyword, "productName", "productCode", "manufacturer");
        filtered = mockDataService.filterByField(filtered, "categoryId", categoryId);
        filtered = mockDataService.filterByField(filtered, "brandId", brandId);
        filtered = mockDataService.filterByIntField(filtered, "isRx", isRx);
        filtered = mockDataService.filterByIntField(filtered, "status", status);
        return Result.success(mockDataService.paginate(filtered, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<JsonNode> getProductDetail(@PathVariable String id) {
        ArrayNode products = (ArrayNode) mockDataService.get("products");
        if (products != null) {
            for (JsonNode p : products) {
                if (id.equals(p.get("id").asText())) {
                    return Result.success(p);
                }
            }
        }
        return Result.error(404, "药品不存在");
    }

    @PostMapping
    public Result<Object> createProduct(@RequestBody JsonNode body) {
        return Result.success(java.util.Map.of("id", String.valueOf(System.currentTimeMillis())));
    }

    @PutMapping("/{id}")
    public Result<Void> updateProduct(@PathVariable String id, @RequestBody JsonNode body) {
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable String id) {
        return Result.success();
    }

    @PatchMapping("/{id}/status")
    public Result<Void> updateProductStatus(@PathVariable String id, @RequestBody JsonNode body) {
        return Result.success();
    }
}
