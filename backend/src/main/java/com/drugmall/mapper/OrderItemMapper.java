package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单商品项Mapper接口
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    /**
     * 根据订单ID查询商品项列表
     */
    List<OrderItem> selectByOrderId(@Param("orderId") String orderId);
}
