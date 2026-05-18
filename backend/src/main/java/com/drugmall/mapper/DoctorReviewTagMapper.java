package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.DoctorReviewTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DoctorReviewTagMapper extends BaseMapper<DoctorReviewTag> {

    List<DoctorReviewTag> selectByDoctorId(@Param("doctorId") String doctorId);
}
