package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.StoreCertification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StoreCertificationMapper extends BaseMapper<StoreCertification> {

    List<StoreCertification> selectByStoreId(@Param("storeId") Long storeId);
}
