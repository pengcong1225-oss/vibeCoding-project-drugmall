package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.dto.AddressDTO;
import com.drugmall.dto.ParseAddressDTO;
import com.drugmall.service.UserService;
import com.drugmall.vo.AddressVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 地址控制器（独立路径版本）
 */
@RestController
@RequestMapping("/v1/addresses")
@Tag(name = "地址管理", description = "收货地址相关接口")
@Validated
public class AddressController {

    @Autowired
    private UserService userService;

    private static final String CURRENT_USER_ID = "1";

    @GetMapping
    @Operation(summary = "获取地址列表", description = "获取当前用户的收货地址列表")
    public Result<List<AddressVO>> getAddressList() {
        return Result.success(userService.getAddressList(CURRENT_USER_ID));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取地址详情", description = "获取指定地址详情")
    public Result<AddressVO> getAddressDetail(
            @Parameter(description = "地址ID") @PathVariable String id) {
        return Result.success(userService.getAddressDetail(CURRENT_USER_ID, id));
    }

    @GetMapping("/default")
    @Operation(summary = "获取默认地址", description = "获取当前用户的默认地址")
    public Result<AddressVO> getDefaultAddress() {
        return Result.success(userService.getDefaultAddress(CURRENT_USER_ID));
    }

    @PostMapping
    @Operation(summary = "添加地址", description = "添加新的收货地址")
    public Result<AddressVO> addAddress(@Valid @RequestBody AddressDTO addressDTO) {
        return Result.success(userService.addAddress(CURRENT_USER_ID, addressDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新地址", description = "更新收货地址")
    public Result<AddressVO> updateAddress(
            @Parameter(description = "地址ID") @PathVariable String id,
            @Valid @RequestBody AddressDTO addressDTO) {
        return Result.success(userService.updateAddress(CURRENT_USER_ID, id, addressDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除地址", description = "删除指定地址")
    public Result<Void> deleteAddress(
            @Parameter(description = "地址ID") @PathVariable String id) {
        userService.deleteAddress(CURRENT_USER_ID, id);
        return Result.success();
    }

    @PutMapping("/{id}/default")
    @Operation(summary = "设置默认地址", description = "设置指定地址为默认")
    public Result<Void> setDefaultAddress(
            @Parameter(description = "地址ID") @PathVariable String id) {
        userService.setDefaultAddress(CURRENT_USER_ID, id);
        return Result.success();
    }

    @PostMapping("/parse")
    @Operation(summary = "智能解析地址", description = "智能解析地址文本")
    public Result<AddressVO> parseAddress(@Valid @RequestBody ParseAddressDTO parseDTO) {
        return Result.success(userService.parseAddress(parseDTO.getAddressText()));
    }
}
