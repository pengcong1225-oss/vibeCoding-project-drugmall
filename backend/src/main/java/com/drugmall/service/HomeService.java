package com.drugmall.service;

import com.drugmall.vo.HomePageVO;

import java.util.List;

/**
 * 首页配置服务接口
 * 提供首页渲染数据、配置管理等功能
 *
 * @author DrugMall Team
 * @since 1.0.0
 */
public interface HomeService {

    /**
     * 获取首页渲染配置（C端用户使用）
     * 返回完整的首页模块配置，包含所有可见的模块和组件数据
     *
     * @return 首页配置VO
     */
    HomePageVO getHomePageRender();

    /**
     * 获取首页配置列表（管理端使用）
     * 返回所有可用的首页配置方案列表
     *
     * @return 首页配置列表
     */
    List<HomePageVO> getConfigList();
}
