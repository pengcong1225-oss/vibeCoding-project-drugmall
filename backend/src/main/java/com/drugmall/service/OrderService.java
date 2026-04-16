package com.drugmall.service;

import com.drugmall.dto.*;
import com.drugmall.vo.*;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 获取订单列表
     */
    PageResultVO<OrderVO> getOrderList(String userId, OrderQueryDTO queryDTO);

    /**
     * 创建订单
     */
    OrderVO createOrder(String userId, CreateOrderDTO createDTO);

    /**
     * 获取订单详情
     */
    OrderVO getOrderDetail(String userId, String orderId);

    /**
     * 取消订单
     */
    void cancelOrder(String userId, String orderId);

    /**
     * 删除订单
     */
    void deleteOrder(String userId, String orderId);

    /**
     * 确认收货
     */
    void confirmOrder(String userId, String orderId);

    /**
     * 再次购买
     */
    void reorder(String userId, String orderId);

    /**
     * 支付订单
     */
    PayResultVO payOrder(String userId, PayOrderDTO payDTO);

    /**
     * 获取支付状态
     */
    String getPayStatus(String userId, String orderId);

    /**
     * 获取物流信息
     */
    List<OrderVO.LogisticsInfoVO> getLogisticsInfo(String userId, String orderId);

    /**
     * 申请退款
     */
    RefundInfoVO applyRefund(String userId, RefundApplyDTO refundDTO);

    /**
     * 获取退款信息
     */
    RefundInfoVO getRefundInfo(String userId, String orderId);

    /**
     * 取消退款
     */
    void cancelRefund(String userId, String orderId);

    /**
     * 获取待评价列表
     */
    List<OrderItemVO> getPendingReviews(String userId);

    /**
     * 提交评价
     */
    void submitReview(String userId, SubmitReviewDTO reviewDTO);

    /**
     * 获取订单统计
     */
    OrderStatsVO getOrderStatistics(String userId);

    /**
     * 获取订单状态数量
     */
    List<OrderStatusCountVO> getOrderStatusCounts(String userId);
}
