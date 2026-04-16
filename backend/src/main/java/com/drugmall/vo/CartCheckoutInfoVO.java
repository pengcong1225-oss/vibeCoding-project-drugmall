package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车结算信息VO
 */
@Data
@Schema(description = "购物车结算信息")
public class CartCheckoutInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "商品列表")
    private List<CartItemVO> items;

    @Schema(description = "商品总金额")
    private BigDecimal totalAmount;

    @Schema(description = "运费")
    private BigDecimal deliveryFee;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "应付金额")
    private BigDecimal payableAmount;
    
    @Schema(description = "商品原始金额")
    private BigDecimal originalAmount;

    @Schema(description = "可用优惠券列表")
    private List<AvailableCouponVO> availableCoupons;

    @Schema(description = "不可用优惠券列表")
    private List<UnavailableCouponVO> unavailableCoupons;

    @Schema(description = "默认地址")
    private DefaultAddressVO defaultAddress;

    @Data
    @Schema(description = "可用优惠券")
    public static class AvailableCouponVO implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "优惠券ID")
        private String id;

        @Schema(description = "名称")
        private String name;

        @Schema(description = "面值")
        private BigDecimal value;

        @Schema(description = "最低消费金额")
        private BigDecimal minAmount;
    }

    @Data
    @Schema(description = "不可用优惠券")
    public static class UnavailableCouponVO implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "优惠券ID")
        private String id;

        @Schema(description = "名称")
        private String name;

        @Schema(description = "不可用原因")
        private String reason;
    }

    @Data
    @Schema(description = "默认地址")
    public static class DefaultAddressVO implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "地址ID")
        private String id;

        @Schema(description = "收货人姓名")
        private String name;

        @Schema(description = "手机号")
        private String phone;

        @Schema(description = "完整地址")
        private String fullAddress;
    }
}
