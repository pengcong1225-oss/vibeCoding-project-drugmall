package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.Drug;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 药品Mapper接口
 */
@Mapper
public interface DrugMapper extends BaseMapper<Drug> {

    /**
     * 根据分类ID查询药品列表
     */
    List<Drug> selectByCategoryId(@Param("categoryId") String categoryId);

    /**
     * 搜索药品
     */
    List<Drug> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 查询热门药品
     */
    List<Drug> selectHotDrugs(@Param("limit") Integer limit);

    /**
     * 查询新品药品
     */
    List<Drug> selectNewDrugs(@Param("limit") Integer limit);

    /**
     * 根据关键词查询相关药品（用于AI推荐）
     */
    List<Drug> selectRelatedDrugs(@Param("keyword") String keyword, @Param("limit") Integer limit);
}
