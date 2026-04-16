package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车项VO
 */
@Data
@Schema(description = "购物车商品项")
public class CartItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "购物车项ID")
    private String id;

    @Schema(description = "药品ID")
    private String drugId;

    @Schema(description = "药品名称")
    private String name;

    @Schema(description = "规格")
    private String specification;

    @Schema(description = "生产厂家")
    private String manufacturer;

    @Schema(description = "单价")
    private BigDecimal price;

    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "图片")
    private String image;

    @Schema(description = "图片颜色")
    private String imageColor;

    @Schema(description = "图片文字")
    private String imageText;

    @Schema(description = "病症")
    private String disease;

    @Schema(description = "用法用量")
    private String usage;

    @Schema(description = "是否处方药")
    private Boolean isRx;

    @Schema(description = "是否选中")
    private Boolean isSelected;

    @Schema(description = "库存")
    private Integer stock;

    @Schema(description = "库存预警值")
    private Integer warningStock;

    @Schema(description = "分类ID")
    private String categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "标签")
    private List<String> tags;
}
