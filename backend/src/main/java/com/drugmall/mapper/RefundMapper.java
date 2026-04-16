package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.Refund;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 退款Mapper接口
 */
@Mapper
public interface RefundMapper extends BaseMapper<Refund> {

    /**
     * 根据订单ID查询退款信息
     */
    Refund selectByOrderId(@Param("orderId") String orderId);
}
