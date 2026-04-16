package com.drugmall.service.impl;

import com.drugmall.config.MockDataService;
import com.drugmall.dto.*;
import com.drugmall.service.CartService;
import com.drugmall.vo.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 购物车服务实现
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private MockDataService mockDataService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<CartItemVO> getCartList(String userId) {
        JsonNode cartsData = mockDataService.getCarts();
        List<CartItemVO> carts = new ArrayList<>();
        if (cartsData != null && cartsData.isArray()) {
            for (JsonNode item : cartsData) {
                if (item.get("userId").asText().equals(userId)) {
                    carts.add(convertToCartItemVO(item));
                }
            }
        }
        return carts;
    }

    @Override
    public CartItemVO addToCart(String userId, AddToCartDTO addDTO) {
        log.info("添加商品到购物车: userId={}, drugId={}", userId, addDTO.getDrugId());

        // 从药品数据中获取药品信息
        CartItemVO item = new CartItemVO();
        item.setId(UUID.randomUUID().toString().replace("-", ""));
        item.setDrugId(addDTO.getDrugId());
        item.setName("模拟药品");
        item.setSpecification("0.25g*24粒");
        item.setManufacturer("模拟厂家");
        item.setPrice(new BigDecimal("12.50"));
        item.setOriginalPrice(new BigDecimal("18.00"));
        item.setQuantity(addDTO.getQuantity());
        item.setImage("");
        item.setImageColor("#00b578");
        item.setImageText("药品");
        item.setDisease(addDTO.getDisease());
        item.setUsage(addDTO.getUsage());
        item.setIsRx(false);
        item.setIsSelected(true);
        item.setStock(100);
        item.setWarningStock(10);
        item.setCategoryId("1");
        item.setCategoryName("感冒药");
        return item;
    }

    @Override
    public CartItemVO updateCartItem(String userId, String itemId, UpdateCartDTO updateDTO) {
        log.info("更新购物车项: {}", itemId);
        List<CartItemVO> carts = getCartList(userId);
        CartItemVO item = carts.stream()
                .filter(c -> c.getId().equals(itemId))
                .findFirst()
                .orElse(null);
        if (item != null) {
            if (updateDTO.getQuantity() != null) {
                item.setQuantity(updateDTO.getQuantity());
            }
            if (updateDTO.getIsSelected() != null) {
                item.setIsSelected(updateDTO.getIsSelected());
            }
            if (updateDTO.getDisease() != null) {
                item.setDisease(updateDTO.getDisease());
            }
            if (updateDTO.getUsage() != null) {
                item.setUsage(updateDTO.getUsage());
            }
        }
        return item;
    }

    @Override
    public void deleteCartItem(String userId, String itemId) {
        log.info("删除购物车项: {}", itemId);
    }

    @Override
    public void batchRemoveCartItems(String userId, List<String> itemIds) {
        log.info("批量删除购物车项: {}", itemIds);
    }

    @Override
    public void clearCart(String userId) {
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
        log.info("选择购物车项: {}, isSelected={}", itemId, isSelected);
    }

    @Override
    public void selectAllCartItems(String userId, Boolean isSelected) {
        log.info("全选购物车: isSelected={}", isSelected);
    }

    @Override
    public CartItemVO updateCartItemQuantity(String userId, String itemId, Integer quantity) {
        log.info("更新购物车数量: {}, quantity={}", itemId, quantity);
        UpdateCartDTO dto = new UpdateCartDTO();
        dto.setQuantity(quantity);
        return updateCartItem(userId, itemId, dto);
    }

    @Override
    public CartValidationResultVO validateCart(String userId) {
        log.info("验证购物车: {}", userId);
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

    private CartItemVO convertToCartItemVO(JsonNode item) {
        if (item == null) {
            return null;
        }
        CartItemVO vo = new CartItemVO();
        vo.setId(getTextValue(item, "id", ""));
        vo.setDrugId(getTextValue(item, "drugId", ""));
        vo.setName(getTextValue(item, "drugName", ""));
        vo.setSpecification(getTextValue(item, "specification", ""));
        vo.setManufacturer(getTextValue(item, "manufacturer", ""));
        vo.setPrice(getDecimalValue(item, "price", BigDecimal.ZERO));
        vo.setOriginalPrice(item.has("originalPrice") && !item.get("originalPrice").isNull() ? 
                new BigDecimal(item.get("originalPrice").asText()) : null);
        vo.setQuantity(getIntValue(item, "quantity", 1));
        vo.setImage(getTextValue(item, "image", ""));
        vo.setImageColor(getTextValue(item, "imageColor", ""));
        vo.setImageText(getTextValue(item, "imageText", ""));
        vo.setIsSelected(getBooleanValue(item, "selected", true));
        vo.setStock(getIntValue(item, "stock", 100));
        vo.setWarningStock(getIntValue(item, "warningStock", 10));
        vo.setIsRx(getBooleanValue(item, "isRx", false));
        vo.setCategoryId(getTextValue(item, "categoryId", null));
        vo.setCategoryName(getTextValue(item, "categoryName", null));
        vo.setDisease(getTextValue(item, "disease", null));
        vo.setUsage(getTextValue(item, "usage", null));
        return vo;
    }

    private String getTextValue(JsonNode node, String field, String defaultValue) {
        if (node.has(field) && !node.get(field).isNull()) {
            return node.get(field).asText();
        }
        return defaultValue;
    }

    private Integer getIntValue(JsonNode node, String field, Integer defaultValue) {
        if (node.has(field) && !node.get(field).isNull()) {
            return node.get(field).asInt();
        }
        return defaultValue;
    }

    private Boolean getBooleanValue(JsonNode node, String field, Boolean defaultValue) {
        if (node.has(field) && !node.get(field).isNull()) {
            return node.get(field).asBoolean();
        }
        return defaultValue;
    }

    private BigDecimal getDecimalValue(JsonNode node, String field, BigDecimal defaultValue) {
        if (node.has(field) && !node.get(field).isNull()) {
            return new BigDecimal(node.get(field).asText());
        }
        return defaultValue;
    }
}
