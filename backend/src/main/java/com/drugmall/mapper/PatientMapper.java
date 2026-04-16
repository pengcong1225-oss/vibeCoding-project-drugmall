package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.Patient;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 就诊人Mapper接口
 */
@Mapper
public interface PatientMapper extends BaseMapper<Patient> {

    /**
     * 根据用户ID查询就诊人列表
     */
    List<Patient> selectByUserId(String userId);
}
