package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车验证结果VO
 */
@Data
@Schema(description = "购物车验证结果")
public class CartValidationResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "是否有效")
    private Boolean valid;

    @Schema(description = "无效商品列表")
    private List<InvalidItemVO> invalidItems;

    @Schema(description = "价格变动商品列表")
    private List<ChangedItemVO> changedItems;

    @Data
    @Schema(description = "无效商品项")
    public static class InvalidItemVO implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "购物车项ID")
        private String itemId;
        
        @Schema(description = "药品ID")
        private String drugId;
        
        @Schema(description = "药品名称")
        private String name;

        @Schema(description = "原因：stock_insufficient-库存不足，price_changed-价格变动，offline-已下架，rx_requirement-需要处方")
        private String reason;

        @Schema(description = "提示信息")
        private String message;
    }

    @Data
    @Schema(description = "价格变动商品项")
    public static class ChangedItemVO implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "购物车项ID")
        private String itemId;

        @Schema(description = "旧价格")
        private BigDecimal oldPrice;

        @Schema(description = "新价格")
        private BigDecimal newPrice;

        @Schema(description = "旧库存")
        private Integer oldStock;

        @Schema(description = "新库存")
        private Integer newStock;
    }
}
