package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.StorePromise;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StorePromiseMapper extends BaseMapper<StorePromise> {

    List<StorePromise> selectByStoreId(@Param("storeId") Long storeId);
}
