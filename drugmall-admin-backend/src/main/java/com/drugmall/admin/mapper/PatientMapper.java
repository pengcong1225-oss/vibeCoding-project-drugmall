package com.drugmall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.admin.entity.Patient;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
}
