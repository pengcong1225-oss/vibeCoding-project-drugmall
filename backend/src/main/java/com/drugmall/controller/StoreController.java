package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.service.StoreService;
import com.drugmall.vo.StoreDetailVO;
import com.drugmall.vo.StoreDrugVO;
import com.drugmall.vo.StoreListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 门店/药店控制器
 * 提供药店列表、详情、药品查询等接口
 *
 * @author DrugMall Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/v1/stores")
@Tag(name = "门店/药店管理", description = "药店相关接口")
public class StoreController {

    @Autowired
    private StoreService storeService;

    /**
     * 获取药店列表
     *
     * @return 药店列表
     */
    @GetMapping
    @Operation(summary = "获取药店列表", description = "获取附近药店列表，包含基本信息和在售商品")
    public Result<List<StoreListVO>> getStoreList() {
        return Result.success(storeService.getStoreList());
    }

    /**
     * 获取药店详情（含药品列表）
     *
     * @param id 药店ID
     * @return 药店详情，包含完整信息和在售商品列表
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取药店详情", description = "根据药店ID获取详细信息，包含在售商品列表、资质认证等")
    public Result<StoreDetailVO> getStoreDetail(
            @Parameter(description = "药店ID") @PathVariable String id) {
        StoreDetailVO detail = storeService.getStoreDetail(id);
        if (detail == null) {
            return Result.error(404, "未找到该药店");
        }
        return Result.success(detail);
    }

    /**
     * 获取门店药品列表
     *
     * @param id 药店ID
     * @return 该药店的在售药品列表
     */
    @GetMapping("/{id}/drugs")
    @Operation(summary = "获取门店药品列表", description = "获取指定药店的在售药品列表")
    public Result<List<StoreDrugVO>> getStoreDrugs(
            @Parameter(description = "药店ID") @PathVariable String id) {
        return Result.success(storeService.getStoreDrugs(id));
    }
}
