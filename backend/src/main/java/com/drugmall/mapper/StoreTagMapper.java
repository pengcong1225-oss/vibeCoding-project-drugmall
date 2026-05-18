package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.StoreTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StoreTagMapper extends BaseMapper<StoreTag> {

    List<StoreTag> selectByStoreId(@Param("storeId") Long storeId);
}
