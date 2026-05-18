package com.drugmall.service;

import com.drugmall.dto.DrugQueryDTO;
import com.drugmall.vo.*;

import java.util.List;

/**
 * 药品服务接口
 */
public interface DrugService {

    /**
     * 获取药品列表
     */
    PageResultVO<DrugVO> getDrugList(DrugQueryDTO queryDTO);

    /**
     * 获取药品详情
     */
    DrugDetailVO getDrugDetail(String drugId);

    /**
     * 获取药品分类
     */
    List<DrugCategoryVO> getCategories();

    /**
     * 获取热门药品
     */
    List<DrugVO> getHotDrugs(Integer limit);

    /**
     * 获取新品药品
     */
    List<DrugVO> getNewDrugs(Integer limit);

    /**
     * 获取推荐药品
     */
    List<DrugVO> getRecommendedDrugs(String userId, Integer limit);

    /**
     * 获取相关药品
     */
    List<DrugVO> getRelatedDrugs(String drugId, Integer limit);

    /**
     * 获取药品评价
     */
    PageResultVO<DrugReviewVO> getDrugReviews(String drugId, Integer page, Integer size);

    /**
     * 获取药品FAQ
     */
    List<DrugFAQVO> getDrugFAQs(String drugId);

    /**
     * 获取药品在售门店列表
     */
    List<DrugStoreVO> getDrugStores(String drugId);

    /**
     * 获取搜索建议
     */
    List<SearchSuggestionVO> getSearchSuggestions(String keyword);

    /**
     * 获取热门搜索
     */
    List<HotSearchVO> getHotSearches(Integer limit);
}
