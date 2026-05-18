package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.SearchFilter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SearchFilterMapper extends BaseMapper<SearchFilter> {

    @Select("SELECT * FROM dm_search_filter WHERE filter_type = #{filterType} AND status = 1 AND is_deleted = 0 ORDER BY sort_order")
    List<SearchFilter> selectByFilterType(@Param("filterType") String filterType);

    @Select("SELECT * FROM dm_search_filter WHERE filter_type = 'category' AND status = 1 AND is_deleted = 0 ORDER BY sort_order")
    List<SearchFilter> selectCategories();

    @Select("SELECT * FROM dm_search_filter WHERE filter_type = 'brand' AND status = 1 AND is_deleted = 0 ORDER BY sort_order")
    List<SearchFilter> selectBrands();

    @Select("SELECT * FROM dm_search_filter WHERE filter_type = 'price' AND status = 1 AND is_deleted = 0 ORDER BY sort_order")
    List<SearchFilter> selectPriceRanges();
}
