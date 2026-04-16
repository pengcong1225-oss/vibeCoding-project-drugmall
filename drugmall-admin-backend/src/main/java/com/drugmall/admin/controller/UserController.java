package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import com.drugmall.admin.config.MockDataService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final MockDataService mockDataService;

    @GetMapping
    public Result<ObjectNode> getUserList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        ArrayNode users = (ArrayNode) mockDataService.get("admin-users");
        if (users == null) return Result.success(mockDataService.getObjectMapper().createObjectNode());

        ArrayNode filtered = mockDataService.filterByKeyword(users, keyword, "username", "nickname", "phone");
        filtered = mockDataService.filterByIntField(filtered, "status", status);
        return Result.success(mockDataService.paginate(filtered, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<JsonNode> getUserDetail(@PathVariable String id) {
        ArrayNode users = (ArrayNode) mockDataService.get("admin-users");
        if (users != null) {
            for (JsonNode user : users) {
                if (id.equals(user.get("id").asText())) {
                    return Result.success(user);
                }
            }
        }
        return Result.error(404, "用户不存在");
    }

    @PostMapping
    public Result<Object> createUser(@RequestBody JsonNode body) {
        return Result.success(java.util.Map.of("id", String.valueOf(System.currentTimeMillis())));
    }

    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable String id, @RequestBody JsonNode body) {
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable String id) {
        return Result.success();
    }

    @PatchMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable String id, @RequestBody JsonNode body) {
        return Result.success();
    }

    @GetMapping("/{id}/orders")
    public Result<ObjectNode> getUserOrders(
            @PathVariable String id,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        ArrayNode orders = (ArrayNode) mockDataService.get("orders", "orders");
        if (orders == null) {
            return Result.success(mockDataService.paginate(
                    mockDataService.getObjectMapper().createArrayNode(), pageNum, pageSize));
        }
        ArrayNode userOrders = mockDataService.filterByField(orders, "userId", id);
        return Result.success(mockDataService.paginate(userOrders, pageNum, pageSize));
    }

    @GetMapping("/auth/list")
    public Result<ObjectNode> getAuthList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        ArrayNode records = (ArrayNode) mockDataService.get("user-auth", "records");
        if (records == null) return Result.success(mockDataService.getObjectMapper().createObjectNode());

        ArrayNode filtered = mockDataService.filterByKeyword(records, keyword, "username", "realName", "phone");
        filtered = mockDataService.filterByIntField(filtered, "status", status);
        return Result.success(mockDataService.paginate(filtered, pageNum, pageSize));
    }

    @GetMapping("/auth/stats")
    public Result<JsonNode> getAuthStats() {
        return Result.success(mockDataService.get("user-auth", "stats"));
    }

    @PostMapping("/auth/{id}/audit")
    public Result<Void> auditAuth(@PathVariable String id, @RequestBody JsonNode body) {
        return Result.success();
    }
}
