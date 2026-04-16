package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车统计VO
 */
@Data
@Schema(description = "购物车统计信息")
public class CartStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "商品种类总数")
    private Integer totalCount;

    @Schema(description = "商品总数量")
    private Integer totalQuantity;

    @Schema(description = "选中商品种类数")
    private Integer selectedCount;

    @Schema(description = "选中商品数量")
    private Integer selectedQuantity;

    @Schema(description = "商品总金额")
    private BigDecimal totalAmount;

    @Schema(description = "选中商品金额")
    private BigDecimal selectedAmount;

    @Schema(description = "原价总金额")
    private BigDecimal originalAmount;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "是否包含处方药")
    private Boolean hasRxItem;
}
