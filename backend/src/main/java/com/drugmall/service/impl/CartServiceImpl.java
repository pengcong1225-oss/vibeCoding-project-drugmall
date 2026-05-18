package com.drugmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drugmall.dto.*;
import com.drugmall.entity.CartItem;
import com.drugmall.entity.Drug;
import com.drugmall.mapper.CartItemMapper;
import com.drugmall.mapper.DrugMapper;
import com.drugmall.service.CartService;
import com.drugmall.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车服务实现
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private DrugMapper drugMapper;

    @Override
    public List<CartItemVO> getCartList(String userId) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        try {
            wrapper.eq(CartItem::getUserId, Long.parseLong(userId))
                   .orderByDesc(CartItem::getCreateTime);
        } catch (NumberFormatException e) {
            log.warn("无效的用户ID: {}", userId);
            return new ArrayList<>();
        }
        
        List<CartItem> cartItems = cartItemMapper.selectList(wrapper);
        return cartItems.stream()
                .map(this::convertToCartItemVO)
                .collect(Collectors.toList());
    }

    @Override
    public CartItemVO addToCart(String userId, AddToCartDTO addDTO) {
        log.info("添加商品到购物车: userId={}, drugId={}", userId, addDTO.getDrugId());

        Long userIdLong;
        Long productIdLong;
        try {
            userIdLong = Long.parseLong(userId);
            productIdLong = Long.parseLong(addDTO.getDrugId());
        } catch (NumberFormatException e) {
            throw new RuntimeException("无效的ID格式");
        }

        // 检查是否已存在该商品
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userIdLong)
               .eq(CartItem::getProductId, productIdLong);
        CartItem existingItem = cartItemMapper.selectOne(wrapper);

        if (existingItem != null) {
            // 已存在，更新数量
            existingItem.setQuantity(existingItem.getQuantity() + addDTO.getQuantity());
            cartItemMapper.updateById(existingItem);
            return convertToCartItemVO(existingItem);
        }

        // 从数据库获取药品信息
        Drug drug = drugMapper.selectById(productIdLong);
        if (drug == null) {
            throw new RuntimeException("药品不存在");
        }

        // 创建新的购物车项
        CartItem cartItem = new CartItem();
        cartItem.setUserId(userIdLong);
        cartItem.setProductId(productIdLong);
        cartItem.setQuantity(addDTO.getQuantity());
        cartItem.setSelected(true);
        cartItem.setCreateTime(LocalDateTime.now());

        cartItemMapper.insert(cartItem);
        return convertToCartItemVOWithDrug(cartItem, drug);
    }

    @Override
    public CartItemVO updateCartItem(String userId, String itemId, UpdateCartDTO updateDTO) {
        Long itemIdLong;
        try {
            itemIdLong = Long.parseLong(itemId);
        } catch (NumberFormatException e) {
            log.warn("无效的购物车ID: {}", itemId);
            return null;
        }

        CartItem cartItem = cartItemMapper.selectById(itemIdLong);
        if (cartItem == null) {
            return null;
        }

        if (updateDTO.getQuantity() != null) {
            cartItem.setQuantity(updateDTO.getQuantity());
        }
        if (updateDTO.getIsSelected() != null) {
            cartItem.setSelected(updateDTO.getIsSelected());
        }

        cartItemMapper.updateById(cartItem);
        return convertToCartItemVO(cartItem);
    }

    @Override
    public void deleteCartItem(String userId, String itemId) {
        try {
            cartItemMapper.deleteById(Long.parseLong(itemId));
        } catch (NumberFormatException e) {
            log.warn("无效的购物车ID: {}", itemId);
        }
        log.info("删除购物车项: {}", itemId);
    }

    @Override
    public void batchRemoveCartItems(String userId, List<String> itemIds) {
        List<Long> ids = itemIds.stream()
                .map(id -> {
                    try {
                        return Long.parseLong(id);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(id -> id != null)
                .collect(Collectors.toList());
        
        if (!ids.isEmpty()) {
            cartItemMapper.deleteBatchIds(ids);
        }
        log.info("批量删除购物车项: {}", itemIds);
    }

    @Override
    public void clearCart(String userId) {
        Long userIdLong;
        try {
            userIdLong = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            log.warn("无效的用户ID: {}", userId);
            return;
        }
        
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userIdLong);
        cartItemMapper.delete(wrapper);
        log.info("清空购物车: {}", userId);
    }

    @Override
    public CartStatsVO getCartStats(String userId) {
        List<CartItemVO> carts = getCartList(userId);
        CartStatsVO stats = new CartStatsVO();

        int totalCount = carts.size();
        int totalQuantity = carts.stream().mapToInt(CartItemVO::getQuantity).sum();
        int selectedCount = (int) carts.stream().filter(CartItemVO::getIsSelected).count();
        int selectedQuantity = carts.stream()
                .filter(CartItemVO::getIsSelected)
                .mapToInt(CartItemVO::getQuantity)
                .sum();

        BigDecimal totalAmount = carts.stream()
                .map(c -> c.getPrice().multiply(new BigDecimal(c.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal selectedAmount = carts.stream()
                .filter(CartItemVO::getIsSelected)
                .map(c -> c.getPrice().multiply(new BigDecimal(c.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal originalAmount = carts.stream()
                .filter(CartItemVO::getIsSelected)
                .map(c -> c.getOriginalPrice() != null ?
                        c.getOriginalPrice().multiply(new BigDecimal(c.getQuantity())) :
                        c.getPrice().multiply(new BigDecimal(c.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean hasRxItem = carts.stream().anyMatch(CartItemVO::getIsRx);

        stats.setTotalCount(totalCount);
        stats.setTotalQuantity(totalQuantity);
        stats.setSelectedCount(selectedCount);
        stats.setSelectedQuantity(selectedQuantity);
        stats.setTotalAmount(totalAmount);
        stats.setSelectedAmount(selectedAmount);
        stats.setOriginalAmount(originalAmount);
        stats.setDiscountAmount(originalAmount.subtract(selectedAmount));
        stats.setHasRxItem(hasRxItem);

        return stats;
    }

    @Override
    public void selectCartItem(String userId, String itemId, Boolean isSelected) {
        try {
            CartItem cartItem = cartItemMapper.selectById(Long.parseLong(itemId));
            if (cartItem != null) {
                cartItem.setSelected(isSelected);
                cartItemMapper.updateById(cartItem);
            }
        } catch (NumberFormatException e) {
            log.warn("无效的购物车ID: {}", itemId);
        }
        log.info("选择购物车项: {}, isSelected={}", itemId, isSelected);
    }

    @Override
    public void selectAllCartItems(String userId, Boolean isSelected) {
        Long userIdLong;
        try {
            userIdLong = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            log.warn("无效的用户ID: {}", userId);
            return;
        }

        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userIdLong);
        List<CartItem> cartItems = cartItemMapper.selectList(wrapper);
        
        for (CartItem item : cartItems) {
            item.setSelected(isSelected);
            cartItemMapper.updateById(item);
        }
        log.info("全选购物车: isSelected={}", isSelected);
    }

    @Override
    public CartItemVO updateCartItemQuantity(String userId, String itemId, Integer quantity) {
        Long itemIdLong;
        try {
            itemIdLong = Long.parseLong(itemId);
        } catch (NumberFormatException e) {
            log.warn("无效的购物车ID: {}", itemId);
            return null;
        }

        CartItem cartItem = cartItemMapper.selectById(itemIdLong);
        if (cartItem == null) {
            return null;
        }
        
        cartItem.setQuantity(quantity);
        cartItemMapper.updateById(cartItem);
        
        log.info("更新购物车数量: {}, quantity={}", itemId, quantity);
        return convertToCartItemVO(cartItem);
    }

    @Override
    public CartValidationResultVO validateCart(String userId) {
        List<CartItemVO> carts = getCartList(userId);
        CartValidationResultVO result = new CartValidationResultVO();

        List<CartValidationResultVO.InvalidItemVO> invalidItems = new ArrayList<>();
        List<CartValidationResultVO.ChangedItemVO> changedItems = new ArrayList<>();

        for (CartItemVO item : carts) {
            // 检查库存
            if (item.getQuantity() > item.getStock()) {
                CartValidationResultVO.InvalidItemVO invalid = new CartValidationResultVO.InvalidItemVO();
                invalid.setItemId(item.getId());
                invalid.setDrugId(item.getDrugId());
                invalid.setName(item.getName());
                invalid.setReason("库存不足");
                invalidItems.add(invalid);
            }
        }

        result.setValid(invalidItems.isEmpty() && changedItems.isEmpty());
        result.setInvalidItems(invalidItems);
        result.setChangedItems(changedItems);
        return result;
    }

    @Override
    public CartCheckoutInfoVO getCheckoutInfo(String userId, List<String> cartItemIds) {
        log.info("获取结算信息: {}", cartItemIds);
        
        List<CartItemVO> allCarts = getCartList(userId);
        List<CartItemVO> selectedCarts = allCarts.stream()
                .filter(c -> cartItemIds.contains(c.getId()))
                .collect(Collectors.toList());

        BigDecimal totalAmount = selectedCarts.stream()
                .map(c -> c.getPrice().multiply(new BigDecimal(c.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal originalAmount = selectedCarts.stream()
                .map(c -> c.getOriginalPrice() != null ?
                        c.getOriginalPrice().multiply(new BigDecimal(c.getQuantity())) :
                        c.getPrice().multiply(new BigDecimal(c.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal deliveryFee = totalAmount.compareTo(new BigDecimal("99")) >= 0 ? BigDecimal.ZERO : new BigDecimal("5");

        CartCheckoutInfoVO checkout = new CartCheckoutInfoVO();
        checkout.setItems(selectedCarts);
        checkout.setTotalAmount(totalAmount);
        checkout.setOriginalAmount(originalAmount);
        checkout.setDiscountAmount(originalAmount.subtract(totalAmount));
        checkout.setDeliveryFee(deliveryFee);
        checkout.setPayableAmount(totalAmount.add(deliveryFee));

        // 可用优惠券
        List<CartCheckoutInfoVO.AvailableCouponVO> availableCoupons = new ArrayList<>();
        CartCheckoutInfoVO.AvailableCouponVO coupon1 = new CartCheckoutInfoVO.AvailableCouponVO();
        coupon1.setId("1");
        coupon1.setName("满100减20");
        coupon1.setValue(new BigDecimal("20"));
        coupon1.setMinAmount(new BigDecimal("100"));
        availableCoupons.add(coupon1);

        CartCheckoutInfoVO.AvailableCouponVO coupon2 = new CartCheckoutInfoVO.AvailableCouponVO();
        coupon2.setId("2");
        coupon2.setName("满200减50");
        coupon2.setValue(new BigDecimal("50"));
        coupon2.setMinAmount(new BigDecimal("200"));
        availableCoupons.add(coupon2);

        checkout.setAvailableCoupons(availableCoupons);
        checkout.setUnavailableCoupons(new ArrayList<>());

        // 默认地址
        CartCheckoutInfoVO.DefaultAddressVO address = new CartCheckoutInfoVO.DefaultAddressVO();
        address.setId("1");
        address.setName("张三");
        address.setPhone("13800138000");
        address.setFullAddress("北京市北京市朝阳区某某小区1号楼1单元101室");
        checkout.setDefaultAddress(address);

        return checkout;
    }

    @Override
    public void mergeCart(String userId, MergeCartDTO mergeDTO) {
        log.info("合并购物车: {} 项", mergeDTO.getItems().size());
    }

    private CartItemVO convertToCartItemVO(CartItem item) {
        if (item == null) {
            return null;
        }
        
        // 获取药品信息
        Drug drug = drugMapper.selectById(item.getProductId());
        if (drug != null) {
            return convertToCartItemVOWithDrug(item, drug);
        }
        
        // 如果没有找到药品，返回基本信息
        CartItemVO vo = new CartItemVO();
        vo.setId(String.valueOf(item.getId()));
        vo.setDrugId(String.valueOf(item.getProductId()));
        vo.setQuantity(item.getQuantity());
        vo.setIsSelected(item.getSelected());
        return vo;
    }

    private CartItemVO convertToCartItemVOWithDrug(CartItem item, Drug drug) {
        CartItemVO vo = new CartItemVO();
        vo.setId(String.valueOf(item.getId()));
        vo.setDrugId(String.valueOf(item.getProductId()));
        vo.setName(drug.getProductName());
        vo.setSpecification(drug.getSpecification());
        vo.setManufacturer(drug.getManufacturer());
        vo.setPrice(drug.getPrice());
        vo.setOriginalPrice(drug.getOriginalPrice());
        vo.setQuantity(item.getQuantity());
        vo.setImage(drug.getMainImage());
        vo.setIsRx(drug.getIsRx());
        vo.setIsSelected(item.getSelected());
        vo.setStock(drug.getStock());
        vo.setCategoryId(drug.getCategoryId() != null ? String.valueOf(drug.getCategoryId()) : null);
        vo.setCategoryName("");
        return vo;
    }
}
