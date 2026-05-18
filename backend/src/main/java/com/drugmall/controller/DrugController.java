package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.dto.DrugQueryDTO;
import com.drugmall.service.DrugService;
import com.drugmall.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 药品控制器
 */
@RestController
@RequestMapping("/v1/drugs")
@Tag(name = "药品管理", description = "药品相关接口")
@Validated
public class DrugController {

    @Autowired
    private DrugService drugService;

    @GetMapping
    @Operation(summary = "获取药品列表", description = "获取药品列表，支持分页和筛选")
    public Result<PageResultVO<DrugVO>> getDrugList(DrugQueryDTO queryDTO) {
        return Result.success(drugService.getDrugList(queryDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取药品详情", description = "获取指定药品详细信息")
    public Result<DrugDetailVO> getDrugDetail(
            @Parameter(description = "药品ID") @PathVariable String id) {
        return Result.success(drugService.getDrugDetail(id));
    }

    @GetMapping("/categories")
    @Operation(summary = "获取药品分类", description = "获取所有药品分类")
    public Result<List<DrugCategoryVO>> getCategories() {
        return Result.success(drugService.getCategories());
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门药品", description = "获取销量最高的热门药品")
    public Result<List<DrugVO>> getHotDrugs(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "8") Integer limit) {
        return Result.success(drugService.getHotDrugs(limit));
    }

    @GetMapping("/new")
    @Operation(summary = "获取新品药品", description = "获取最新上架的药品")
    public Result<List<DrugVO>> getNewDrugs(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "8") Integer limit) {
        return Result.success(drugService.getNewDrugs(limit));
    }

    @GetMapping("/recommended")
    @Operation(summary = "获取推荐药品", description = "获取个性化推荐药品")
    public Result<List<DrugVO>> getRecommendedDrugs(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "8") Integer limit) {
        return Result.success(drugService.getRecommendedDrugs(null, limit));
    }

    @GetMapping("/related")
    @Operation(summary = "获取相关药品", description = "获取与指定药品相关的药品")
    public Result<List<DrugVO>> getRelatedDrugs(
            @Parameter(description = "药品ID") @RequestParam String drugId,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "4") Integer limit) {
        return Result.success(drugService.getRelatedDrugs(drugId, limit));
    }

    @GetMapping("/{id}/reviews")
    @Operation(summary = "获取药品评价", description = "获取指定药品的评价列表")
    public Result<PageResultVO<DrugReviewVO>> getDrugReviews(
            @Parameter(description = "药品ID") @PathVariable String id,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(drugService.getDrugReviews(id, page, size));
    }

    @GetMapping("/{id}/faqs")
    @Operation(summary = "获取药品FAQ", description = "获取指定药品的常见问题")
    public Result<List<DrugFAQVO>> getDrugFAQs(
            @Parameter(description = "药品ID") @PathVariable String id) {
        return Result.success(drugService.getDrugFAQs(id));
    }

    @GetMapping("/{id}/stores")
    @Operation(summary = "获取药品在售门店", description = "获取指定药品的在售门店列表")
    public Result<List<DrugStoreVO>> getDrugStores(
            @Parameter(description = "药品ID") @PathVariable String id) {
        return Result.success(drugService.getDrugStores(id));
    }

    @GetMapping("/search/suggestions")
    @Operation(summary = "搜索建议", description = "根据关键词获取搜索建议")
    public Result<List<SearchSuggestionVO>> getSearchSuggestions(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        return Result.success(drugService.getSearchSuggestions(keyword));
    }

    @GetMapping("/search/hot")
    @Operation(summary = "热门搜索", description = "获取热门搜索关键词")
    public Result<List<HotSearchVO>> getHotSearches(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(drugService.getHotSearches(limit));
    }
}
