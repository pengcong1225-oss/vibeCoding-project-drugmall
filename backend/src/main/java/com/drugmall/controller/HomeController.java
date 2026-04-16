package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.service.HomeService;
import com.drugmall.vo.HomePageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页配置控制器
 * 提供首页渲染数据接口和管理端配置接口
 *
 * @author DrugMall Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/v1/home")
@Tag(name = "首页配置", description = "首页模块化配置相关接口")
public class HomeController {

    @Autowired
    private HomeService homeService;

    /**
     * 获取首页渲染配置（C端用户使用）
     * 返回完整的首页模块配置，包含所有可见的模块和组件数据
     *
     * @return 首页完整配置VO，包含页面配置和所有模块列表
     */
    @GetMapping("/render/page")
    @Operation(summary = "获取首页渲染配置", description = "C端用户获取完整的首页渲染配置，包含所有可见模块及其组件数据")
    public Result<HomePageVO> getHomePageRender() {
        return Result.success(homeService.getHomePageRender());
    }

    /**
     * 获取首页配置列表（管理端使用）
     * 返回所有可用的首页配置方案列表
     *
     * @return 首页配置列表
     */
    @GetMapping("/config/list")
    @Operation(summary = "获取首页配置列表", description = "管理端获取所有可用的首页配置方案列表")
    public Result<List<HomePageVO>> getConfigList() {
        return Result.success(homeService.getConfigList());
    }
}
