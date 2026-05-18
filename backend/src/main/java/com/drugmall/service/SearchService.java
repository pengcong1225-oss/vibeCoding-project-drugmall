package com.drugmall.service;

import com.drugmall.entity.SearchFilter;
import com.drugmall.entity.SearchHistory;
import com.drugmall.entity.SearchSuggestion;
import com.drugmall.vo.HotSearchVO;
import com.drugmall.vo.SearchSuggestionVO;

import java.util.List;
import java.util.Map;

public interface SearchService {

    void saveSearchHistory(Long userId, String keyword, String searchType, Integer resultCount);

    List<String> getSearchHistory(Long userId);

    void deleteSearchHistory(Long userId, String keyword);

    void clearSearchHistory(Long userId);

    List<HotSearchVO> getHotSearches(Integer limit);

    List<SearchSuggestionVO> getSearchSuggestions(String keyword);

    Map<String, Object> getSearchFilters();

    void incrementSearchCount(String keyword);

    List<SearchSuggestion> getSuggestionsByKeyword(String keyword);
}
