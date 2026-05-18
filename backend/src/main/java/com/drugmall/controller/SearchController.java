package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.entity.Drug;
import com.drugmall.mapper.DrugMapper;
import com.drugmall.service.DrugService;
import com.drugmall.service.SearchService;
import com.drugmall.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/v1/search")
@Tag(name = "搜索服务", description = "药品搜索相关接口")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private DrugMapper drugMapper;

    @Autowired
    private DrugService drugService;

    @GetMapping("/drugs")
    @Operation(summary = "搜索药品", description = "根据关键词、分类、品牌等条件搜索药品")
    public Result<PageResultVO<DrugVO>> searchDrugs(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "分类ID") @RequestParam(required = false) String categoryId,
            @Parameter(description = "品牌ID") @RequestParam(required = false) String brandId,
            @Parameter(description = "最低价格") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "最高价格") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "是否处方药") @RequestParam(required = false) Boolean isRx,
            @Parameter(description = "排序方式") @RequestParam(defaultValue = "default") String sort,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {

        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            String kw = keyword.toLowerCase();
            wrapper.and(w -> w.like(Drug::getProductName, kw)
                    .or()
                    .like(Drug::getDescription, kw)
                    .or()
                    .like(Drug::getManufacturer, kw));
        }

        if (categoryId != null && !categoryId.isEmpty() && !categoryId.startsWith("cat_all")) {
            try {
                Long catId = Long.parseLong(categoryId);
                wrapper.eq(Drug::getCategoryId, catId);
            } catch (NumberFormatException e) {
                log.warn("无效的分类ID: {}", categoryId);
            }
        }

        if (minPrice != null) {
            wrapper.ge(Drug::getPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(Drug::getPrice, maxPrice);
        }

        if (isRx != null) {
            wrapper.eq(Drug::getIsRx, isRx);
        }

        wrapper.eq(Drug::getStatus, 1);

        switch (sort != null ? sort : "default") {
            case "price_asc":
                wrapper.orderByAsc(Drug::getPrice);
                break;
            case "price_desc":
                wrapper.orderByDesc(Drug::getPrice);
                break;
            case "sales":
                wrapper.orderByDesc(Drug::getSales);
                break;
            case "new":
                wrapper.orderByDesc(Drug::getCreateTime);
                break;
            default:
                wrapper.orderByDesc(Drug::getSales);
                break;
        }

        int fromIndex = (page - 1) * size;
        wrapper.last("LIMIT " + size + " OFFSET " + fromIndex);

        List<Drug> drugs = drugMapper.selectList(wrapper);

        LambdaQueryWrapper<Drug> countWrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            String kw = keyword.toLowerCase();
            countWrapper.and(w -> w.like(Drug::getProductName, kw)
                    .or()
                    .like(Drug::getDescription, kw)
                    .or()
                    .like(Drug::getManufacturer, kw));
        }
        if (categoryId != null && !categoryId.isEmpty() && !categoryId.startsWith("cat_all")) {
            try {
                Long catId = Long.parseLong(categoryId);
                countWrapper.eq(Drug::getCategoryId, catId);
            } catch (NumberFormatException e) {
            }
        }
        if (minPrice != null) {
            countWrapper.ge(Drug::getPrice, minPrice);
        }
        if (maxPrice != null) {
            countWrapper.le(Drug::getPrice, maxPrice);
        }
        if (isRx != null) {
            countWrapper.eq(Drug::getIsRx, isRx);
        }
        countWrapper.eq(Drug::getStatus, 1);
        Long total = drugMapper.selectCount(countWrapper);

        List<DrugVO> voList = drugs.stream()
                .map(this::convertToDrugVO)
                .collect(Collectors.toList());

        return Result.success(PageResultVO.of(voList, total, page, size));
    }

    @GetMapping("/suggestions")
    @Operation(summary = "获取搜索建议", description = "根据输入关键词返回搜索建议列表")
    public Result<List<SearchSuggestionVO>> getSearchSuggestions(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {

        List<SearchSuggestionVO> suggestions = searchService.getSearchSuggestions(keyword);

        if (suggestions == null || suggestions.isEmpty()) {
            suggestions = new ArrayList<>();
        }

        return Result.success(suggestions);
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门搜索", description = "获取当前热门搜索关键词列表")
    public Result<List<HotSearchVO>> getHotSearches(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {

        List<HotSearchVO> hotSearches = searchService.getHotSearches(limit);
        return Result.success(hotSearches);
    }

    @GetMapping("/filters")
    @Operation(summary = "获取筛选条件", description = "获取搜索页面的筛选条件（分类、品牌、价格区间）")
    public Result<Map<String, Object>> getSearchFilters() {

        Map<String, Object> filters = searchService.getSearchFilters();
        return Result.success(filters);
    }

    @GetMapping("/history")
    @Operation(summary = "获取搜索历史", description = "获取当前用户的搜索历史")
    public Result<List<String>> getSearchHistory(
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId) {

        List<String> history = searchService.getSearchHistory(userId);
        return Result.success(history);
    }

    @DeleteMapping("/history")
    @Operation(summary = "删除搜索历史", description = "删除指定关键词的搜索历史")
    public Result<Void> deleteSearchHistory(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {

        if (keyword != null && !keyword.isEmpty()) {
            searchService.deleteSearchHistory(userId, keyword);
        } else {
            searchService.clearSearchHistory(userId);
        }
        return Result.success();
    }

    @DeleteMapping("/history/all")
    @Operation(summary = "清空搜索历史", description = "清空当前用户的所有搜索历史")
    public Result<Void> clearSearchHistory(
            @Parameter(description = "用户ID") @RequestParam Long userId) {

        searchService.clearSearchHistory(userId);
        return Result.success();
    }

    private DrugVO convertToDrugVO(Drug drug) {
        if (drug == null) {
            return null;
        }
        DrugVO vo = new DrugVO();
        vo.setId(String.valueOf(drug.getId()));
        vo.setName(drug.getProductName());
        vo.setGenericName(drug.getGenericName());
        vo.setBrand(drug.getBrand());
        vo.setSpecification(drug.getSpecification());
        vo.setManufacturer(drug.getManufacturer());
        vo.setPrice(drug.getPrice());
        vo.setOriginalPrice(drug.getOriginalPrice());
        vo.setImage(drug.getMainImage());
        vo.setIsRx(drug.getIsRx());
        vo.setIsNationalEssential(drug.getIsNationalEssential());
        vo.setSales(drug.getSales() != null ? drug.getSales() : 0);
        vo.setStock(drug.getStock() != null ? drug.getStock() : 0);
        vo.setDisease(drug.getDisease());
        vo.setUsage(drug.getUsage());
        vo.setDescription(drug.getDescription());
        vo.setStatus(drug.getStatus());
        return vo;
    }
}
