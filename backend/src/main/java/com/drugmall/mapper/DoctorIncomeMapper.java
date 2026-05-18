package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.DoctorIncome;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医生收入Mapper接口
 */
@Mapper
public interface DoctorIncomeMapper extends BaseMapper<DoctorIncome> {
}
