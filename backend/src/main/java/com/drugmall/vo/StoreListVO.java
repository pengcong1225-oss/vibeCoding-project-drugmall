package com.drugmall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 药店列表VO
 *
 * @author DrugMall Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreListVO {

    /** 药店ID */
    private String id;

    /** 药店名称 */
    private String name;

    /** 药店Logo */
    private String logo;

    /** Logo文字（用于前端显示） */
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
    private List<TagItem> tags;

    /** 地址 */
    private String address;

    /** 电话 */
    private String phone;

    /** 是否营业中 */
    private Boolean isOpen;

    /** 营业时间 */
    private String businessHours;

    /** 部分在售商品 */
    private List<SimpleDrug> products;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagItem {
        private String text;
        private String type; // primary, success, warning, danger, info
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleDrug {
        private String id;
        private String name;
        private BigDecimal price;
        private String bgColor;
    }
}
