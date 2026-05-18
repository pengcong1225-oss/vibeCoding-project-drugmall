package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/logs")
@RequiredArgsConstructor
public class LogController {

    @GetMapping("/operations")
    public Result<Map<String, Object>> getOperationLogs(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type) {
        
        return Result.success(Map.of(
            "list", List.of(
                Map.of("id", 1, "adminName", "admin", "action", "登录系统", "ip", "192.168.1.100", "type", "login", "createTime", LocalDateTime.now()),
                Map.of("id", 2, "adminName", "admin", "action", "修改药品信息", "ip", "192.168.1.100", "type", "update", "createTime", LocalDateTime.now()),
                Map.of("id", 3, "adminName", "operator", "action", "审核订单", "ip", "192.168.1.101", "type", "audit", "createTime", LocalDateTime.now())
            ),
            "total", 3,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @GetMapping("/operations/export")
    public Result<String> exportOperationLogs(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        return Result.success("/exports/operation_logs.xlsx");
    }

    @GetMapping("/login")
    public Result<Map<String, Object>> getLoginLogs(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        return Result.success(Map.of(
            "list", List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @GetMapping("/error")
    public Result<Map<String, Object>> getErrorLogs(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        return Result.success(Map.of(
            "list", List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }
}
