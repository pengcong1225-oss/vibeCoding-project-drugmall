package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.BrowseHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 浏览历史Mapper接口
 */
@Mapper
public interface BrowseHistoryMapper extends BaseMapper<BrowseHistory> {

    /**
     * 根据用户ID查询浏览历史
     */
    List<BrowseHistory> selectByUserId(@Param("userId") String userId);
}
