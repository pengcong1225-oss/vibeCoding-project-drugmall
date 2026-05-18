package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.SearchSuggestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SearchSuggestionMapper extends BaseMapper<SearchSuggestion> {

    @Select("SELECT * FROM dm_search_suggestion WHERE keyword LIKE CONCAT('%',#{keyword},'%') AND status = 1 AND is_deleted = 0 ORDER BY search_count DESC")
    List<SearchSuggestion> selectByKeyword(@Param("keyword") String keyword);

    @Select("SELECT * FROM dm_search_suggestion WHERE type = #{type} AND status = 1 AND is_deleted = 0 ORDER BY search_count DESC")
    List<SearchSuggestion> selectByType(@Param("type") String type);

    @Select("SELECT * FROM dm_search_suggestion WHERE status = 1 AND is_deleted = 0 ORDER BY search_count DESC LIMIT #{limit}")
    List<SearchSuggestion> selectHotList(@Param("limit") Integer limit);
}
