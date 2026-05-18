package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import com.drugmall.admin.entity.Order;
import com.drugmall.admin.service.OrderService;
import com.drugmall.admin.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public Result<PageResult<Order>> getOrderList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        PageResult<Order> result = orderService.getOrderList(pageNum, pageSize, keyword, status);
        return Result.success(result);
    }

    @GetMapping("/stats")
    public Result<Map<String, Long>> getOrderStats() {
        long pending = orderService.getOrderList(1, 1, null, 0).getTotal();
        long paid = orderService.getOrderList(1, 1, null, 1).getTotal();
        long shipped = orderService.getOrderList(1, 1, null, 2).getTotal();
        long completed = orderService.getOrderList(1, 1, null, 4).getTotal();
        long cancelled = orderService.getOrderList(1, 1, null, -1).getTotal();
        
        return Result.success(Map.of(
            "pending", pending,
            "paid", paid,
            "shipped", shipped,
            "completed", completed,
            "cancelled", cancelled
        ));
    }

    @GetMapping("/{id}")
    public Result<Order> getOrderDetail(@PathVariable Long id) {
        Order order = orderService.getOrderDetail(id);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        return Result.success(order);
    }

    @PostMapping("/{id}/confirm")
    public Result<Void> confirmOrder(@PathVariable Long id) {
        // TODO: 实现确认订单逻辑
        return Result.success();
    }

    @PostMapping("/ship")
    public Result<Void> shipOrder(@RequestBody ShipRequest request) {
        // TODO: 实现发货逻辑
        return Result.success();
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id, @RequestBody(required = false) CancelRequest request) {
        // TODO: 实现取消订单逻辑
        return Result.success();
    }

    @PostMapping("/refund")
    public Result<Void> handleRefund(@RequestBody RefundRequest request) {
        // TODO: 实现退款处理逻辑
        return Result.success();
    }

    @GetMapping("/{orderId}/traces")
    public Result<?> getLogisticsTraces(@PathVariable String orderId) {
        // TODO: 实现物流轨迹查询
        return Result.success(java.util.List.of());
    }

    static class ShipRequest {
        private Long orderId;
        private String logisticsCompany;
        private String logisticsNo;
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getLogisticsCompany() { return logisticsCompany; }
        public void setLogisticsCompany(String logisticsCompany) { this.logisticsCompany = logisticsCompany; }
        public String getLogisticsNo() { return logisticsNo; }
        public void setLogisticsNo(String logisticsNo) { this.logisticsNo = logisticsNo; }
    }

    static class CancelRequest {
        private String reason;
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    static class RefundRequest {
        private Long orderId;
        private String action;
        private String reason;
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
