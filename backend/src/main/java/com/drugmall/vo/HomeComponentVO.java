package com.drugmall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 首页组件实例VO
 * 表示模块中的具体组件，如Banner中的单张图片、商品卡片等
 *
 * @author DrugMall Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeComponentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 组件唯一标识
     */
    private String componentId;

    /**
     * 组件类型（banner_image/product_card/category_icon/ad_banner/
     *          pharmacy_card/test_item/department_card等）
     */
    private String componentType;

    /**
     * 组件配置参数（样式、尺寸、颜色等）
     */
    private Map<String, Object> config;

    /**
     * 组件数据内容（业务数据）
     */
    private Object data;

    /**
     * 埋点追踪ID（用于点击事件统计）
     */
    private String trackId;
}
