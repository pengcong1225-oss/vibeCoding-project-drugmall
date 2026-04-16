package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.DrugFAQ;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 药品FAQ Mapper接口
 */
@Mapper
public interface DrugFAQMapper extends BaseMapper<DrugFAQ> {

    /**
     * 根据药品ID查询FAQ列表
     */
    List<DrugFAQ> selectByDrugId(@Param("drugId") String drugId);
}
