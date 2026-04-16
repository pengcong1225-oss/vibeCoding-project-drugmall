package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.dto.*;
import com.drugmall.service.CartService;
import com.drugmall.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/v1/cart")
@Tag(name = "购物车管理", description = "购物车相关接口")
@Validated
public class CartController {

    @Autowired
    private CartService cartService;

    private static final String CURRENT_USER_ID = "1";

    @GetMapping
    @Operation(summary = "获取购物车列表", description = "获取当前用户的购物车列表")
    public Result<List<CartItemVO>> getCartList() {
        return Result.success(cartService.getCartList(CURRENT_USER_ID));
    }

    @PostMapping
    @Operation(summary = "添加商品到购物车", description = "添加药品到购物车")
    public Result<CartItemVO> addToCart(@Valid @RequestBody AddToCartDTO addDTO) {
        return Result.success(cartService.addToCart(CURRENT_USER_ID, addDTO));
    }

    @PutMapping("/{itemId}")
    @Operation(summary = "更新购物车商品", description = "更新购物车商品信息")
    public Result<CartItemVO> updateCartItem(
            @Parameter(description = "购物车项ID") @PathVariable String itemId,
            @RequestBody UpdateCartDTO updateDTO) {
        return Result.success(cartService.updateCartItem(CURRENT_USER_ID, itemId, updateDTO));
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "删除购物车商品", description = "删除指定购物车商品")
    public Result<Void> deleteCartItem(
            @Parameter(description = "购物车项ID") @PathVariable String itemId) {
        cartService.deleteCartItem(CURRENT_USER_ID, itemId);
        return Result.success();
    }

    @PostMapping("/batch-remove")
    @Operation(summary = "批量删除购物车商品", description = "批量删除购物车商品")
    public Result<Void> batchRemoveCartItems(
            @Valid @RequestBody BatchRemoveCartDTO batchDTO) {
        cartService.batchRemoveCartItems(CURRENT_USER_ID, batchDTO.getItemIds());
        return Result.success();
    }

    @DeleteMapping
    @Operation(summary = "清空购物车", description = "清空当前用户的购物车")
    public Result<Void> clearCart() {
        cartService.clearCart(CURRENT_USER_ID);
        return Result.success();
    }

    @GetMapping("/stats")
    @Operation(summary = "获取购物车统计", description = "获取购物车统计信息")
    public Result<CartStatsVO> getCartStats() {
        return Result.success(cartService.getCartStats(CURRENT_USER_ID));
    }

    @PutMapping("/{itemId}/select")
    @Operation(summary = "选择/取消选择商品", description = "设置购物车商品选中状态")
    public Result<Void> selectCartItem(
            @Parameter(description = "购物车项ID") @PathVariable String itemId,
            @Parameter(description = "是否选中") @RequestParam Boolean isSelected) {
        cartService.selectCartItem(CURRENT_USER_ID, itemId, isSelected);
        return Result.success();
    }

    @PutMapping("/select-all")
    @Operation(summary = "全选/取消全选", description = "设置所有购物车商品选中状态")
    public Result<Void> selectAllCartItems(
            @Parameter(description = "是否全选") @RequestParam Boolean isSelected) {
        cartService.selectAllCartItems(CURRENT_USER_ID, isSelected);
        return Result.success();
    }

    @PutMapping("/{itemId}/quantity")
    @Operation(summary = "更新商品数量", description = "更新购物车商品数量")
    public Result<CartItemVO> updateCartItemQuantity(
            @Parameter(description = "购物车项ID") @PathVariable String itemId,
            @Parameter(description = "数量") @RequestParam @Min(1) Integer quantity) {
        return Result.success(cartService.updateCartItemQuantity(CURRENT_USER_ID, itemId, quantity));
    }

    @GetMapping("/validate")
    @Operation(summary = "验证购物车", description = "验证购物车商品有效性")
    public Result<CartValidationResultVO> validateCart() {
        return Result.success(cartService.validateCart(CURRENT_USER_ID));
    }

    @GetMapping("/checkout")
    @Operation(summary = "获取结算信息", description = "获取购物车结算信息")
    public Result<CartCheckoutInfoVO> getCheckoutInfo(
            @Parameter(description = "购物车项ID列表") @RequestParam @NotEmpty List<String> itemIds) {
        return Result.success(cartService.getCheckoutInfo(CURRENT_USER_ID, itemIds));
    }

    @PostMapping("/merge")
    @Operation(summary = "合并购物车", description = "合并本地购物车数据")
    public Result<Void> mergeCart(@Valid @RequestBody MergeCartDTO mergeDTO) {
        cartService.mergeCart(CURRENT_USER_ID, mergeDTO);
        return Result.success();
    }
}
