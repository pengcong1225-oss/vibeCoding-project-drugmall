package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import com.drugmall.admin.config.MockDataService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final MockDataService mockDataService;

    @GetMapping("/overview")
    public Result<JsonNode> getOverview() {
        return Result.success(mockDataService.get("dashboard", "overview"));
    }

    @GetMapping("/gmv-trend")
    public Result<JsonNode> getGmvTrend(@RequestParam(defaultValue = "month") String timeRange) {
        return Result.success(mockDataService.get("dashboard", "gmvTrend"));
    }

    @GetMapping("/order-source")
    public Result<JsonNode> getOrderSource() {
        return Result.success(mockDataService.get("dashboard", "orderSource"));
    }
}
