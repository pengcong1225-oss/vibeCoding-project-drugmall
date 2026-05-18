package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.DoctorReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DoctorReviewMapper extends BaseMapper<DoctorReview> {

    List<Map<String, Object>> selectByDoctorId(@Param("doctorId") String doctorId, @Param("tag") String tag, @Param("offset") int offset, @Param("limit") int limit);

    Long countByDoctorId(@Param("doctorId") String doctorId, @Param("tag") String tag);
}
