package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 优惠券Mapper接口
 */
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

    /**
     * 根据用户ID查询优惠券列表
     */
    List<Coupon> selectByUserId(@Param("userId") String userId);

    /**
     * 根据用户ID和状态查询优惠券列表
     */
    List<Coupon> selectByUserIdAndStatus(@Param("userId") String userId, @Param("status") String status);
}
