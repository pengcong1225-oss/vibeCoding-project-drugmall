package com.drugmall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 门店药品VO
 *
 * @author DrugMall Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDrugVO {

    /** 商品ID */
    private String id;

    /** 商品名称 */
    private String name;

    /** 规格 */
    private String specification;

    /** 生产厂家 */
    private String manufacturer;

    /** 售价 */
    private BigDecimal price;

    /** 原价 */
    private BigDecimal originalPrice;

    /** 库存 */
    private Integer stock;

    /** 是否处方药 */
    private Boolean isRx;

    /** 批准文号 */
    private String approvalNumber;

    /** 商品图片 */
    private String image;

    /** 图片背景色 */
    private String imageColor;

    /** 图片文字 */
    private String imageText;

    /** 销量 */
    private Integer sales;

    /** 折扣 */
    private Integer discount;

    /** 配送时间（分钟） */
    private Integer deliveryTime;

    /** 分类 */
    private String category;

    /** 标签 */
    private List<String> tags;
}
