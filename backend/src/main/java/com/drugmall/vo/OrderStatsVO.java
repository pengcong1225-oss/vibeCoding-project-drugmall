package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单统计VO
 */
@Data
@Schema(description = "订单统计信息")
public class OrderStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单总数")
    private Integer totalCount;

    @Schema(description = "待支付数量")
    private Integer pendingPayment;

    @Schema(description = "待发货数量")
    private Integer pendingShipment;

    @Schema(description = "待收货数量")
    private Integer pendingReceipt;

    @Schema(description = "待评价数量")
    private Integer pendingReview;

    @Schema(description = "售后数量")
    private Integer afterSale;

    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;
}
