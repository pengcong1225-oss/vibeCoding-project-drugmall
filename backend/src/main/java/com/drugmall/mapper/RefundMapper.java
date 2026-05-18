package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.Refund;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 退款Mapper接口
 */
@Mapper
public interface RefundMapper extends BaseMapper<Refund> {

    @Select("SELECT * FROM dm_refund WHERE order_id = #{orderId} AND is_deleted = 0 LIMIT 1")
    Refund selectByOrderId(@Param("orderId") String orderId);
}
