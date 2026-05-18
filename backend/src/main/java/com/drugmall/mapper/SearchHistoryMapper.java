package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.SearchHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SearchHistoryMapper extends BaseMapper<SearchHistory> {

    List<SearchHistory> selectByUserId(@Param("userId") Long userId);

    void deleteByUserId(@Param("userId") Long userId);

    void deleteByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
}
