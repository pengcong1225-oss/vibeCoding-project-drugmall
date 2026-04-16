package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin/auth")
public class AuthController {

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.getOrDefault("username", "");
        String password = params.getOrDefault("password", "");

        if ("admin".equals(username) && "123456".equals(password)) {
            Map<String, Object> data = new HashMap<>();
            data.put("token", "mock_admin_token_" + UUID.randomUUID().toString().replace("-", ""));

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
