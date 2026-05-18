package com.drugmall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drugmall.admin.common.Result;
import com.drugmall.admin.entity.Admin;
import com.drugmall.admin.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AdminMapper adminMapper;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.getOrDefault("username", "");
        String password = params.getOrDefault("password", "");

        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, username);
        Admin admin = adminMapper.selectOne(wrapper);

        if (admin != null && admin.getPassword().equals(password) && admin.getStatus() == 1) {
            admin.setUpdateTime(LocalDateTime.now());
            adminMapper.updateById(admin);

            Map<String, Object> data = new HashMap<>();
            data.put("token", "admin_token_" + UUID.randomUUID().toString().replace("-", ""));

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", String.valueOf(admin.getId()));
            userInfo.put("username", admin.getUsername());
            userInfo.put("nickname", admin.getNickname() != null ? admin.getNickname() : admin.getUsername());
            userInfo.put("avatar", admin.getAvatar() != null ? admin.getAvatar() : "");
            userInfo.put("email", admin.getEmail() != null ? admin.getEmail() : "");
            userInfo.put("phone", admin.getPhone() != null ? admin.getPhone() : "");
            userInfo.put("status", admin.getStatus());
            userInfo.put("roles", List.of("admin"));
            userInfo.put("permissions", List.of("*"));
            userInfo.put("createTime", admin.getCreateTime() != null ? admin.getCreateTime().toString() : "");
            userInfo.put("lastLoginTime", admin.getUpdateTime() != null ? admin.getUpdateTime().toString() : "");
            data.put("userInfo", userInfo);
            data.put("permissions", List.of("*"));
            data.put("roles", List.of("admin"));

            return Result.success(data);
        }

        return Result.error(401, "用户名或密码错误");
    }

    @GetMapping("/userinfo")
    public Result<Map<String, Object>> getUserInfo(@RequestHeader(value = "Authorization", required = false) String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) {
            return Result.error(401, "未登录或token已过期");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", "admin_001");
        userInfo.put("username", "admin");
        userInfo.put("nickname", "系统管理员");
        userInfo.put("avatar", "https://api.dicebear.com/7.x/avataaars/svg?seed=admin");
        userInfo.put("email", "admin@drugmall.com");
        userInfo.put("phone", "13800000000");
        userInfo.put("status", 1);
        userInfo.put("roles", List.of("admin"));
        userInfo.put("permissions", List.of("*"));
        userInfo.put("createTime", "2024-01-01 00:00:00");
        userInfo.put("lastLoginTime", "2024-03-20 09:00:00");

        return Result.success(userInfo);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}
