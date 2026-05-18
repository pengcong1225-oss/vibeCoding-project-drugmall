package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.HotSearch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface HotSearchMapper extends BaseMapper<HotSearch> {

    @Select("SELECT * FROM dm_hot_search WHERE status = 1 AND is_deleted = 0 ORDER BY sort_order ASC LIMIT #{limit}")
    List<HotSearch> selectHotList(@Param("limit") Integer limit);

    @Update("UPDATE dm_hot_search SET search_count = search_count + 1, daily_count = daily_count + 1, weekly_count = weekly_count + 1 WHERE keyword = #{keyword}")
    void incrementSearchCount(@Param("keyword") String keyword);

    @Select("SELECT * FROM dm_hot_search WHERE keyword = #{keyword} AND is_deleted = 0 LIMIT 1")
    List<HotSearch> selectByKeyword(@Param("keyword") String keyword);
}
