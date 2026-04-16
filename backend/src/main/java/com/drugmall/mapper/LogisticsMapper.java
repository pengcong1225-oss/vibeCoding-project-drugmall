package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.Logistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物流信息Mapper接口
 */
@Mapper
public interface LogisticsMapper extends BaseMapper<Logistics> {

    /**
     * 根据订单ID查询物流信息
     */
    List<Logistics> selectByOrderId(@Param("orderId") String orderId);
}
