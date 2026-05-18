package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import com.drugmall.admin.entity.User;
import com.drugmall.admin.service.UserService;
import com.drugmall.admin.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Result<PageResult<User>> getUserList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        PageResult<User> result = userService.getUserList(pageNum, pageSize, keyword, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<User> getUserDetail(@PathVariable Long id) {
        User user = userService.getUserDetail(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        return Result.success(user);
    }

    @PostMapping
    public Result<Long> createUser(@RequestBody User user) {
        // TODO: 实现创建用户逻辑，需要密码加密等
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody User user) {
        // TODO: 实现更新用户逻辑
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        // TODO: 实现删除用户逻辑（逻辑删除）
        return Result.success();
    }

    @PatchMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        boolean success = userService.updateUserStatus(id, request.getStatus());
        if (!success) {
            return Result.error(500, "更新失败");
        }
        return Result.success();
    }

    @GetMapping("/{id}/orders")
    public Result<PageResult<?>> getUserOrders(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        // TODO: 实现获取用户订单逻辑
        return Result.success(PageResult.of(java.util.Collections.emptyList(), 0, pageNum, pageSize));
    }

    @GetMapping("/auth/list")
    public Result<PageResult<?>> getAuthList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        // TODO: 实现实名认证列表
        return Result.success(PageResult.of(java.util.Collections.emptyList(), 0, pageNum, pageSize));
    }

    @GetMapping("/auth/stats")
    public Result<?> getAuthStats() {
        // TODO: 实现认证统计
        return Result.success(java.util.Map.of("pending", 0, "passed", 0, "rejected", 0, "total", 0));
    }

    @PostMapping("/auth/{id}/audit")
    public Result<Void> auditAuth(@PathVariable Long id, @RequestBody AuditRequest request) {
        // TODO: 实现审核逻辑
        return Result.success();
    }

    // 内部类用于接收请求体
    static class StatusUpdateRequest {
        private Integer status;
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
    }

    static class AuditRequest {
        private Integer action;
        private String reason;
        public Integer getAction() { return action; }
        public void setAction(Integer action) { this.action = action; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
