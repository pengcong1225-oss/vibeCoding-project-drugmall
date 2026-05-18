package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.DoctorSchedule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医生排班Mapper接口
 */
@Mapper
public interface DoctorScheduleMapper extends BaseMapper<DoctorSchedule> {
}
