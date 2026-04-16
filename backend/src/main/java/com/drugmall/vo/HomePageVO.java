package com.drugmall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 首页配置响应VO
 * 包含页面整体配置和所有模块列表
 *
 * @author DrugMall Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomePageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页面唯一标识
     */
    private String pageId;

    /**
     * 页面名称
     */
    private String pageName;

    /**
     * 页面描述
     */
    private String description;

    /**
     * 页面版本号
     */
    private String version;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 页面全局配置（如：主题色、背景图等）
     */
    private Map<String, Object> pageConfig;

    /**
     * 模块列表（按 sortOrder 排序）
     */
    private List<HomeSectionVO> sections;
}
