package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单商品项VO
 */
@Data
@Schema(description = "订单商品项")
public class OrderItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "商品项ID")
    private String id;

    @Schema(description = "订单ID")
    private String orderId;

    @Schema(description = "药品ID")
    private String drugId;

    @Schema(description = "药品名称")
    private String name;

    @Schema(description = "规格")
    private String specification;

    @Schema(description = "生产厂家")
    private String manufacturer;

    @Schema(description = "图片")
    private String image;

    @Schema(description = "图片颜色")
    private String imageColor;

    @Schema(description = "图片文字")
    private String imageText;

    @Schema(description = "单价")
    private BigDecimal price;

    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "是否处方药")
    private Boolean isRx;

    @Schema(description = "病症")
    private String disease;

    @Schema(description = "用法用量")
    private String usage;

    @Schema(description = "小计金额")
    private BigDecimal subtotal;

    @Schema(description = "评价状态：pending-待评价，completed-已评价")
    private String reviewStatus;

    @Schema(description = "评价ID")
    private String reviewId;
}
