package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 购物车项Mapper接口
 */
@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {

    /**
     * 根据用户ID查询购物车列表
     */
    List<CartItem> selectByUserId(String userId);
}
