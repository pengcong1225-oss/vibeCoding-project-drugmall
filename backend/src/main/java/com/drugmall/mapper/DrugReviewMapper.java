package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.DrugReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 药品评价Mapper接口
 */
@Mapper
public interface DrugReviewMapper extends BaseMapper<DrugReview> {

    /**
     * 根据药品ID查询评价列表
     */
    List<DrugReview> selectByDrugId(@Param("drugId") String drugId);
}
