package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import com.drugmall.admin.config.MockDataService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderController {

    private final MockDataService mockDataService;

    @GetMapping
    public Result<ObjectNode> getOrderList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        ArrayNode orders = (ArrayNode) mockDataService.get("orders", "orders");
        if (orders == null) return Result.success(mockDataService.getObjectMapper().createObjectNode());

        ArrayNode filtered = mockDataService.filterByKeyword(orders, keyword, "orderNo", "username", "nickname", "phone");
        filtered = mockDataService.filterByIntField(filtered, "status", status);
        return Result.success(mockDataService.paginate(filtered, pageNum, pageSize));
    }

    @GetMapping("/stats")
    public Result<JsonNode> getOrderStats() {
        return Result.success(mockDataService.get("orders", "stats"));
    }

    @GetMapping("/{id}")
    public Result<JsonNode> getOrderDetail(@PathVariable String id) {
        ArrayNode orders = (ArrayNode) mockDataService.get("orders", "orders");
        if (orders != null) {
            for (JsonNode o : orders) {
                if (id.equals(o.get("id").asText())) {
                    return Result.success(o);
                }
            }
        }
        return Result.error(404, "订单不存在");
    }

    @PostMapping("/{id}/confirm")
    public Result<Void> confirmOrder(@PathVariable String id) {
        return Result.success();
    }

    @PostMapping("/ship")
    public Result<Void> shipOrder(@RequestBody JsonNode body) {
        return Result.success();
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable String id, @RequestBody(required = false) JsonNode body) {
        return Result.success();
    }

    @PostMapping("/refund")
    public Result<Void> handleRefund(@RequestBody JsonNode body) {
        return Result.success();
    }

    @GetMapping("/{orderId}/traces")
    public Result<Object> getLogisticsTraces(@PathVariable String orderId) {
        return Result.success(java.util.List.of(
            java.util.Map.of("time", "2024-03-22 09:00:00", "status", "已签收", "desc", "您的包裹已由本人签收"),
            java.util.Map.of("time", "2024-03-21 14:00:00", "status", "派送中", "desc", "快递员正在派送中"),
            java.util.Map.of("time", "2024-03-21 08:00:00", "status", "到达", "desc", "到达目的地城市"),
            java.util.Map.of("time", "2024-03-20 18:00:00", "status", "运输中", "desc", "包裹正在运输中"),
            java.util.Map.of("time", "2024-03-20 14:00:00", "status", "已发货", "desc", "商家已发货")
        ));
    }
}
