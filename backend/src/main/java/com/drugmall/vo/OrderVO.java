package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单VO
 */
@Data
@Schema(description = "订单信息")
public class OrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单ID")
    private String id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "订单状态：pending-待支付，paid-已支付，confirmed-已确认，shipped-配送中，delivered-已送达，completed-已完成，cancelled-已取消，refunding-退款中，refunded-已退款")
    private String status;

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "商品列表")
    private List<OrderItemVO> items;

    @Schema(description = "商品总数量")
    private Integer totalQuantity;

    @Schema(description = "商品总金额")
    private BigDecimal totalAmount;

    @Schema(description = "药品金额")
    private BigDecimal drugAmount;

    @Schema(description = "运费")
    private BigDecimal deliveryFee;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "优惠券抵扣金额")
    private BigDecimal couponAmount;

    @Schema(description = "应付金额")
    private BigDecimal payableAmount;

    @Schema(description = "实付金额")
    private BigDecimal paidAmount;

    @Schema(description = "配送方式：delivery-快递配送，self_pickup-到店自提，same_day-当日达")
    private String deliveryType;

    @Schema(description = "收货人姓名")
    private String receiverName;

    @Schema(description = "收货人手机号")
    private String receiverPhone;

    @Schema(description = "收货地址")
    private String receiverAddress;

    @Schema(description = "地址ID")
    private String addressId;

    @Schema(description = "自提门店信息")
    private PickupStoreVO pickupStore;

    @Schema(description = "自提码")
    private String pickupCode;

    @Schema(description = "支付方式：wechat-微信支付，alipay-支付宝，balance-余额支付")
    private String payType;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "物流单号")
    private String logisticsNo;

    @Schema(description = "物流公司")
    private String logisticsCompany;

    @Schema(description = "物流信息列表")
    private List<LogisticsInfoVO> logisticsInfo;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "确认时间")
    private LocalDateTime confirmTime;

    @Schema(description = "发货时间")
    private LocalDateTime deliveryTime;

    @Schema(description = "完成时间")
    private LocalDateTime completeTime;

    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "处方ID")
    private String prescriptionId;

    @Schema(description = "是否需要处方审核")
    private Boolean needRxReview;

    @Schema(description = "处方审核状态：pending-待审核，approved-已通过，rejected-已拒绝")
    private String rxReviewStatus;

    @Data
    @Schema(description = "自提门店信息")
    public static class PickupStoreVO implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "门店ID")
        private String id;

        @Schema(description = "门店名称")
        private String name;

        @Schema(description = "门店地址")
        private String address;

        @Schema(description = "门店电话")
        private String phone;

        @Schema(description = "营业时间")
        private String businessHours;
    }

    @Data
    @Schema(description = "物流信息")
    public static class LogisticsInfoVO implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "时间")
        private String time;

        @Schema(description = "内容")
        private String content;

        @Schema(description = "状态")
        private String status;
    }
}
