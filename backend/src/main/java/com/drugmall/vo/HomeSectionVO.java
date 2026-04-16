package com.drugmall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 首页模块配置VO
 * 表示首页中的一个功能模块，如搜索栏、Banner轮播、服务网格等
 *
 * @author DrugMall Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeSectionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模块唯一标识
     */
    private String sectionId;

    /**
     * 模块类型（search_bar/tab_navigation/service_grid/banner_subsidy/doctor_banner/
     *          nearby_pharmacy/waterfall_layout/doctor_department/test_items/
     *          chronic_category/tcm_category）
     */
    private String sectionType;

    /**
     * 模块标题
     */
    private String title;

    /**
     * 布局方式（full_width/half_width/custom）
     */
    private String layout;

    /**
     * 是否可见
     */
    private Boolean visible;

    /**
     * 排序序号（越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 模块级配置（JSON格式存储的额外配置）
     */
    private Map<String, Object> config;

    /**
     * 模块包含的组件列表
     */
    private List<HomeComponentVO> components;
}
