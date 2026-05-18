package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class SettingsController {

    @GetMapping("/basic")
    public Result<Map<String, Object>> getBasicSettings() {
        return Result.success(Map.of(
            "siteName", "DrugMall药品电商平台",
            "siteLogo", "",
            "customerServicePhone", "400-888-8888"
        ));
    }

    @PutMapping("/basic")
    public Result<Void> saveBasicSettings(@RequestBody Map<String, Object> body) {
        return Result.success();
    }

    @GetMapping("/payment")
    public Result<Map<String, Object>> getPaymentSettings() {
        return Result.success(Map.of(
            "wechatPayEnabled", true,
            "alipayEnabled", true,
            "bankCardEnabled", false
        ));
    }

    @PutMapping("/payment")
    public Result<Void> savePaymentSettings(@RequestBody Map<String, Object> body) {
        return Result.success();
    }
}
