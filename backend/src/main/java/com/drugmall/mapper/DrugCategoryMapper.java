package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.DrugCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 药品分类Mapper接口
 */
@Mapper
public interface DrugCategoryMapper extends BaseMapper<DrugCategory> {

    /**
     * 查询所有启用的分类
     */
    List<DrugCategory> selectAllActive();
}
