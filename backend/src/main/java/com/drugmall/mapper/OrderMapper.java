package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据用户ID查询订单列表
     */
    List<Order> selectByUserId(@Param("userId") String userId);

    /**
     * 根据用户ID和状态查询订单列表
     */
    List<Order> selectByUserIdAndStatus(@Param("userId") String userId, @Param("status") String status);
}
