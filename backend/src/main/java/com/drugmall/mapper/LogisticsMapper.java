package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.Logistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LogisticsMapper extends BaseMapper<Logistics> {

    @Select("SELECT * FROM dm_logistics WHERE order_id = #{orderId} ORDER BY create_time DESC")
    List<Logistics> selectByOrderId(@Param("orderId") String orderId);
}
