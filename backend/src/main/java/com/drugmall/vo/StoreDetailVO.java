package com.drugmall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 药店详情VO
 *
 * @author DrugMall Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDetailVO {

    /** 药店ID */
    private String id;

    /** 药店名称 */
    private String name;

    /** 药店Logo */
    private String logo;

    /** Logo文字 */
    private String logoText;

    /** Logo背景色 */
    private String logoColor;

    /** 评分 */
    private Double rating;

    /** 月销量 */
    private Integer monthlySales;

    /** 距离（公里） */
    private Double distance;

    /** 配送时间（分钟） */
    private Integer deliveryTime;

    /** 标签列表 */
    private List<StoreListVO.TagItem> tags;

    /** 地址 */
    private String address;

    /** 电话 */
    private String phone;

    /** 是否营业中 */
    private Boolean isOpen;

    /** 营业时间 */
    private String businessHours;

    /** 部分在售商品 */
    private List<StoreListVO.SimpleDrug> products;

    /** 药店简介 */
    private String description;

    /** 经营范围 */
    private String businessScope;

    /** 资质认证 */
    private List<String> certifications;

    /** 服务承诺 */
    private List<String> servicePromises;

    /** 在售商品总数 */
    private Integer totalProducts;

    /** 完整商品列表 */
    private List<StoreDrugVO> drugs;
}
