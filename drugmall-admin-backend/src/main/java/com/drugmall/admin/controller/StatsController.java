package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import com.drugmall.admin.entity.Order;
import com.drugmall.admin.entity.User;
import com.drugmall.admin.mapper.OrderMapper;
import com.drugmall.admin.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
public class StatsController {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    @GetMapping("/prescription")
    public Result<Map<String, Object>> getPrescriptionStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        return Result.success(Map.of(
            "total", 0,
            "pending", 0,
            "approved", 0,
            "rejected", 0
        ));
    }

    @GetMapping("/consultation")
    public Result<Map<String, Object>> getConsultationStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        return Result.success(Map.of(
            "total", 0,
            "pending", 0,
            "completed", 0,
            "cancelled", 0
        ));
    }

    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverviewStats() {
        long userCount = userMapper.selectCount(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>().eq(User::getIsDeleted, 0));
        long orderCount = orderMapper.selectCount(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>().eq(Order::getIsDeleted, 0));
        
        return Result.success(Map.of(
            "totalUsers", userCount,
            "totalOrders", orderCount,
            "todayOrders", 0,
            "todayRevenue", 0.00,
            "pendingConsultations", 0,
            "pendingPrescriptions", 0
        ));
    }
}
