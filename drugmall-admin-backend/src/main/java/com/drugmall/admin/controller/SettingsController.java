package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import com.drugmall.admin.config.MockDataService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final MockDataService mockDataService;

    @GetMapping("/basic")
    public Result<JsonNode> getBasicSettings() {
        return Result.success(mockDataService.get("settings", "basic"));
    }

    @PutMapping("/basic")
    public Result<Void> saveBasicSettings(@RequestBody JsonNode body) {
        return Result.success();
    }

    @GetMapping("/payment")
    public Result<JsonNode> getPaymentSettings() {
        return Result.success(mockDataService.get("settings", "payment"));
    }

    @PutMapping("/payment")
    public Result<Void> savePaymentSettings(@RequestBody JsonNode body) {
        return Result.success();
    }
}
