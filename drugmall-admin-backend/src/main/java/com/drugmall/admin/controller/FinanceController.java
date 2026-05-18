package com.drugmall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drugmall.admin.common.Result;
import com.drugmall.admin.entity.Coupon;
import com.drugmall.admin.entity.Order;
import com.drugmall.admin.mapper.CouponMapper;
import com.drugmall.admin.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final OrderMapper orderMapper;
    private final CouponMapper couponMapper;

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime weekStart = LocalDate.now().minusWeeks(1).atStartOfDay();
        LocalDateTime monthStart = LocalDate.now().minusMonths(1).atStartOfDay();

        // 今日收入
        List<Order> todayOrders = orderMapper.selectList(
            new LambdaQueryWrapper<Order>()
                .ge(Order::getCreateTime, todayStart)
                .eq(Order::getIsDeleted, 0)
                .in(Order::getStatus, 1, 2, 3, 4)
        );
        BigDecimal todayIncome = todayOrders.stream()
            .map(Order::getPayAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 本周收入
        List<Order> weekOrders = orderMapper.selectList(
            new LambdaQueryWrapper<Order>()
                .ge(Order::getCreateTime, weekStart)
                .eq(Order::getIsDeleted, 0)
                .in(Order::getStatus, 1, 2, 3, 4)
        );
        BigDecimal weekIncome = weekOrders.stream()
            .map(Order::getPayAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 本月收入
        List<Order> monthOrders = orderMapper.selectList(
            new LambdaQueryWrapper<Order>()
                .ge(Order::getCreateTime, monthStart)
                .eq(Order::getIsDeleted, 0)
                .in(Order::getStatus, 1, 2, 3, 4)
        );
        BigDecimal monthIncome = monthOrders.stream()
            .map(Order::getPayAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 总收入
        List<Order> allOrders = orderMapper.selectList(
            new LambdaQueryWrapper<Order>()
                .eq(Order::getIsDeleted, 0)
                .in(Order::getStatus, 1, 2, 3, 4)
        );
        BigDecimal totalIncome = allOrders.stream()
            .map(Order::getPayAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Result.success(Map.of(
            "todayIncome", todayIncome,
            "weekIncome", weekIncome,
            "monthIncome", monthIncome,
            "totalIncome", totalIncome,
            "pendingWithdrawal", 0.00,
            "completedWithdrawal", 0.00
        ));
    }

    @GetMapping("/transactions")
    public Result<Map<String, Object>> getTransactions(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
            .eq(Order::getIsDeleted, 0);
        
        if (status != null && !status.isEmpty()) {
            try {
                wrapper.eq(Order::getStatus, Integer.parseInt(status));
            } catch (NumberFormatException e) {
                // ignore invalid status
            }
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Order::getOrderNo, keyword);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        
        Page<Order> page = new Page<>(pageNum, pageSize);
        Page<Order> result = orderMapper.selectPage(page, wrapper);
        
        // 转换为交易记录格式
        List<Map<String, Object>> transactions = result.getRecords().stream().map(order -> {
            return Map.<String, Object>of(
                "id", order.getId(),
                "orderNo", order.getOrderNo(),
                "userId", order.getUserId(),
                "amount", order.getPayAmount(),
                "status", order.getStatus(),
                "payType", order.getPayType() != null ? order.getPayType() : 0,
                "createTime", order.getCreateTime()
            );
        }).collect(Collectors.toList());
        
        return Result.success(Map.of(
            "list", transactions,
            "total", result.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @GetMapping("/transactions/{id}")
    public Result<Order> getTransactionDetail(@PathVariable Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null || order.getIsDeleted() == 1) {
            return Result.error(404, "订单不存在");
        }
        return Result.success(order);
    }

    @GetMapping("/withdrawals")
    public Result<Map<String, Object>> getWithdrawals(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        // TODO: 实现提现列表，需要创建提现表
        return Result.success(Map.of("list", Collections.emptyList(), "total", 0, "pageNum", pageNum, "pageSize", pageSize));
    }

    @GetMapping("/withdrawals/stats")
    public Result<Map<String, Object>> getWithdrawalStats() {
        // TODO: 实现提现统计
        return Result.success(Map.of(
            "todayAmount", 0.00, "todayCount", 0,
            "weekAmount", 0.00, "weekCount", 0,
            "monthAmount", 0.00, "monthCount", 0,
            "totalAmount", 0.00, "totalCount", 0
        ));
    }

    @PostMapping("/withdrawals/{id}/audit")
    public Result<Void> auditWithdrawal(@PathVariable String id, @RequestBody Map<String, Object> body) {
        // TODO: 实现提现审核
        return Result.success();
    }
}
