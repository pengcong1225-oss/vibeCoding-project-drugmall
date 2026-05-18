package com.drugmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drugmall.entity.SearchFilter;
import com.drugmall.entity.SearchHistory;
import com.drugmall.entity.SearchSuggestion;
import com.drugmall.mapper.*;
import com.drugmall.service.SearchService;
import com.drugmall.vo.HotSearchVO;
import com.drugmall.vo.SearchSuggestionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SearchHistoryMapper searchHistoryMapper;

    @Autowired
    private HotSearchMapper hotSearchMapper;

    @Autowired
    private SearchSuggestionMapper searchSuggestionMapper;

    @Autowired
    private SearchFilterMapper searchFilterMapper;

    @Override
    @Transactional
    public void saveSearchHistory(Long userId, String keyword, String searchType, Integer resultCount) {
        if (userId == null || keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        SearchHistory history = new SearchHistory();
        history.setUserId(userId);
        history.setKeyword(keyword.trim());
        history.setSearchType(searchType != null ? searchType : "drug");
        history.setResultCount(resultCount != null ? resultCount : 0);
        history.setCreateTime(LocalDateTime.now());
        history.setIsDeleted(0);

        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId)
               .eq(SearchHistory::getKeyword, keyword.trim());
        SearchHistory existing = searchHistoryMapper.selectOne(wrapper);

        if (existing != null) {
            existing.setCreateTime(LocalDateTime.now());
            existing.setResultCount(resultCount != null ? resultCount : 0);
            searchHistoryMapper.updateById(existing);
        } else {
            searchHistoryMapper.insert(history);
        }
    }

    @Override
    public List<String> getSearchHistory(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId)
               .eq(SearchHistory::getIsDeleted, 0)
               .orderByDesc(SearchHistory::getCreateTime)
               .last("LIMIT 10");

        List<SearchHistory> histories = searchHistoryMapper.selectList(wrapper);
        return histories.stream()
                .map(SearchHistory::getKeyword)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSearchHistory(Long userId, String keyword) {
        if (userId == null || keyword == null) {
            return;
        }

        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId)
               .eq(SearchHistory::getKeyword, keyword);
        searchHistoryMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void clearSearchHistory(Long userId) {
        if (userId == null) {
            return;
        }

        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId);
        searchHistoryMapper.delete(wrapper);
    }

    @Override
    public List<HotSearchVO> getHotSearches(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        List<com.drugmall.entity.HotSearch> hotList = hotSearchMapper.selectHotList(limit);

        return hotList.stream().map(hot -> {
            HotSearchVO vo = new HotSearchVO();
            vo.setKeyword(hot.getKeyword());
            vo.setHeat(hot.getSearchCount());
            vo.setIsNew(hot.getIsNew() != null && hot.getIsNew() == 1);
            vo.setIsHot(hot.getIsHot() != null && hot.getIsHot() == 1);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SearchSuggestionVO> getSearchSuggestions(String keyword) {
        List<SearchSuggestionVO> suggestions = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return suggestions;
        }

        String kw = keyword.trim().toLowerCase();

        LambdaQueryWrapper<SearchSuggestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.like(SearchSuggestion::getKeyword, kw)
                        .or()
                        .like(SearchSuggestion::getKeywordPinyin, kw))
               .eq(SearchSuggestion::getStatus, 1)
               .eq(SearchSuggestion::getIsDeleted, 0)
               .orderByDesc(SearchSuggestion::getSearchCount)
               .last("LIMIT 10");

        List<SearchSuggestion> list = searchSuggestionMapper.selectList(wrapper);

        for (SearchSuggestion s : list) {
            SearchSuggestionVO vo = new SearchSuggestionVO();
            vo.setKeyword(s.getKeyword());
            vo.setType(s.getType());
            vo.setCount(s.getSearchCount());
            suggestions.add(vo);
        }

        return suggestions;
    }

    @Override
    public Map<String, Object> getSearchFilters() {
        Map<String, Object> filters = new HashMap<>();

        List<SearchFilter> categories = searchFilterMapper.selectCategories();
        List<SearchFilter> brands = searchFilterMapper.selectBrands();
        List<SearchFilter> priceRanges = searchFilterMapper.selectPriceRanges();

        List<Map<String, String>> categoryList = new ArrayList<>();
        for (SearchFilter c : categories) {
            Map<String, String> item = new HashMap<>();
            item.put("id", c.getValue());
            item.put("name", c.getName());
            categoryList.add(item);
        }
        filters.put("categories", categoryList);

        List<Map<String, String>> brandList = new ArrayList<>();
        for (SearchFilter b : brands) {
            Map<String, String> item = new HashMap<>();
            item.put("id", b.getValue());
            item.put("name", b.getName());
            brandList.add(item);
        }
        filters.put("brands", brandList);

        List<Map<String, Object>> priceRangeList = new ArrayList<>();
        for (SearchFilter p : priceRanges) {
            Map<String, Object> range = new HashMap<>();
            range.put("label", p.getName());
            range.put("value", p.getValue());

            String val = p.getValue();
            if (val != null && val.startsWith("price_")) {
                if (val.equals("price_all")) {
                    range.put("min", 0);
                    range.put("max", Integer.MAX_VALUE);
                } else if (val.equals("price_0_10")) {
                    range.put("min", 0);
                    range.put("max", 10);
                } else if (val.equals("price_10_30")) {
                    range.put("min", 10);
                    range.put("max", 30);
                } else if (val.equals("price_30_50")) {
                    range.put("min", 30);
                    range.put("max", 50);
                } else if (val.equals("price_50_100")) {
                    range.put("min", 50);
                    range.put("max", 100);
                } else if (val.equals("price_100_plus")) {
                    range.put("min", 100);
                    range.put("max", Integer.MAX_VALUE);
                }
            }
            priceRangeList.add(range);
        }
        filters.put("priceRanges", priceRangeList);

        return filters;
    }

    @Override
    @Transactional
    public void incrementSearchCount(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        try {
            hotSearchMapper.incrementSearchCount(keyword.trim());
        } catch (Exception e) {
            log.warn("更新热门搜索失败: keyword={}", keyword);
        }
    }

    @Override
    public List<SearchSuggestion> getSuggestionsByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String kw = keyword.trim().toLowerCase();
        LambdaQueryWrapper<SearchSuggestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.like(SearchSuggestion::getKeyword, kw)
                        .or()
                        .like(SearchSuggestion::getKeywordPinyin, kw))
               .eq(SearchSuggestion::getStatus, 1)
               .eq(SearchSuggestion::getIsDeleted, 0)
               .orderByDesc(SearchSuggestion::getSearchCount)
               .last("LIMIT 5");

        return searchSuggestionMapper.selectList(wrapper);
    }
}
