package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.dto.*;
import com.drugmall.service.OrderService;
import com.drugmall.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/v1/orders")
@Tag(name = "订单管理", description = "订单相关接口")
@Validated
public class OrderController {

    @Autowired
    private OrderService orderService;

    private static final String CURRENT_USER_ID = "1";

    @GetMapping
    @Operation(summary = "获取订单列表", description = "获取当前用户的订单列表")
    public Result<PageResultVO<OrderVO>> getOrderList(OrderQueryDTO queryDTO) {
        return Result.success(orderService.getOrderList(CURRENT_USER_ID, queryDTO));
    }

    @PostMapping
    @Operation(summary = "创建订单", description = "创建新订单")
    public Result<OrderVO> createOrder(@Valid @RequestBody CreateOrderDTO createDTO) {
        return Result.success(orderService.createOrder(CURRENT_USER_ID, createDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "获取指定订单详情")
    public Result<OrderVO> getOrderDetail(
            @Parameter(description = "订单ID") @PathVariable String id) {
        return Result.success(orderService.getOrderDetail(CURRENT_USER_ID, id));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消订单", description = "取消指定订单")
    public Result<Void> cancelOrder(
            @Parameter(description = "订单ID") @PathVariable String id) {
        orderService.cancelOrder(CURRENT_USER_ID, id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除订单", description = "删除指定订单")
    public Result<Void> deleteOrder(
            @Parameter(description = "订单ID") @PathVariable String id) {
        orderService.deleteOrder(CURRENT_USER_ID, id);
        return Result.success();
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "确认收货", description = "确认收到订单商品")
    public Result<Void> confirmOrder(
            @Parameter(description = "订单ID") @PathVariable String id) {
        orderService.confirmOrder(CURRENT_USER_ID, id);
        return Result.success();
    }

    @PostMapping("/{id}/reorder")
    @Operation(summary = "再次购买", description = "基于历史订单再次购买")
    public Result<Void> reorder(
            @Parameter(description = "订单ID") @PathVariable String id) {
        orderService.reorder(CURRENT_USER_ID, id);
        return Result.success();
    }

    @PostMapping("/pay")
    @Operation(summary = "支付订单", description = "支付指定订单")
    public Result<PayResultVO> payOrder(@Valid @RequestBody PayOrderDTO payDTO) {
        return Result.success(orderService.payOrder(CURRENT_USER_ID, payDTO));
    }

    @GetMapping("/{id}/pay-status")
    @Operation(summary = "获取支付状态", description = "获取订单支付状态")
    public Result<String> getPayStatus(
            @Parameter(description = "订单ID") @PathVariable String id) {
        return Result.success(orderService.getPayStatus(CURRENT_USER_ID, id));
    }

    @GetMapping("/{id}/logistics")
    @Operation(summary = "获取物流信息", description = "获取订单物流信息")
    public Result<List<OrderVO.LogisticsInfoVO>> getLogisticsInfo(
            @Parameter(description = "订单ID") @PathVariable String id) {
        return Result.success(orderService.getLogisticsInfo(CURRENT_USER_ID, id));
    }

    @PostMapping("/refund")
    @Operation(summary = "申请退款", description = "申请订单退款")
    public Result<RefundInfoVO> applyRefund(@Valid @RequestBody RefundApplyDTO refundDTO) {
        return Result.success(orderService.applyRefund(CURRENT_USER_ID, refundDTO));
    }

    @GetMapping("/{id}/refund")
    @Operation(summary = "获取退款信息", description = "获取订单退款信息")
    public Result<RefundInfoVO> getRefundInfo(
            @Parameter(description = "订单ID") @PathVariable String id) {
        return Result.success(orderService.getRefundInfo(CURRENT_USER_ID, id));
    }

    @PutMapping("/{id}/refund/cancel")
    @Operation(summary = "取消退款", description = "取消退款申请")
    public Result<Void> cancelRefund(
            @Parameter(description = "订单ID") @PathVariable String id) {
        orderService.cancelRefund(CURRENT_USER_ID, id);
        return Result.success();
    }

    @GetMapping("/pending-reviews")
    @Operation(summary = "获取待评价列表", description = "获取待评价的订单商品列表")
    public Result<List<OrderItemVO>> getPendingReviews() {
        return Result.success(orderService.getPendingReviews(CURRENT_USER_ID));
    }

    @PostMapping("/review")
    @Operation(summary = "提交评价", description = "提交订单商品评价")
    public Result<Void> submitReview(@Valid @RequestBody SubmitReviewDTO reviewDTO) {
        orderService.submitReview(CURRENT_USER_ID, reviewDTO);
        return Result.success();
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取订单统计", description = "获取用户订单统计数据")
    public Result<OrderStatsVO> getOrderStatistics() {
        return Result.success(orderService.getOrderStatistics(CURRENT_USER_ID));
    }

    @GetMapping("/status-counts")
    @Operation(summary = "获取订单状态数量", description = "获取各状态订单数量")
    public Result<List<OrderStatusCountVO>> getOrderStatusCounts() {
        return Result.success(orderService.getOrderStatusCounts(CURRENT_USER_ID));
    }
}
