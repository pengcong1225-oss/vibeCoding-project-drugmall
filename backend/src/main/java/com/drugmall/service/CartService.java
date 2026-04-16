package com.drugmall.service;

import com.drugmall.dto.*;
import com.drugmall.vo.*;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {

    /**
     * 获取购物车列表
     */
    List<CartItemVO> getCartList(String userId);

    /**
     * 添加商品到购物车
     */
    CartItemVO addToCart(String userId, AddToCartDTO addDTO);

    /**
     * 更新购物车商品
     */
    CartItemVO updateCartItem(String userId, String itemId, UpdateCartDTO updateDTO);

    /**
     * 删除购物车商品
     */
    void deleteCartItem(String userId, String itemId);

    /**
     * 批量删除购物车商品
     */
    void batchRemoveCartItems(String userId, List<String> itemIds);

    /**
     * 清空购物车
     */
    void clearCart(String userId);

    /**
     * 获取购物车统计
     */
    CartStatsVO getCartStats(String userId);

    /**
     * 选择/取消选择商品
     */
    void selectCartItem(String userId, String itemId, Boolean isSelected);

    /**
     * 全选/取消全选
     */
    void selectAllCartItems(String userId, Boolean isSelected);

    /**
     * 更新商品数量
     */
    CartItemVO updateCartItemQuantity(String userId, String itemId, Integer quantity);

    /**
     * 验证购物车
     */
    CartValidationResultVO validateCart(String userId);

    /**
     * 获取结算信息
     */
    CartCheckoutInfoVO getCheckoutInfo(String userId, List<String> cartItemIds);

    /**
     * 合并购物车
     */
    void mergeCart(String userId, MergeCartDTO mergeDTO);
}
