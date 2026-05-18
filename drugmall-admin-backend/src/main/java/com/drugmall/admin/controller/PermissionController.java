package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/permissions")
@RequiredArgsConstructor
public class PermissionController {

    @GetMapping("/roles")
    public Result<Map<String, Object>> getRoleList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        return Result.success(Map.of(
            "list", List.of(
                Map.of("id", 1, "name", "超级管理员", "code", "super_admin", "description", "拥有所有权限", "status", 1),
                Map.of("id", 2, "name", "运营管理员", "code", "operator", "description", "运营管理权限", "status", 1),
                Map.of("id", 3, "name", "客服管理员", "code", "customer_service", "description", "客服管理权限", "status", 1)
            ),
            "total", 3,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PostMapping("/roles")
    public Result<Void> createRole(@RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @PutMapping("/roles/{id}")
    public Result<Void> updateRole(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @DeleteMapping("/roles/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        return Result.success();
    }

    @GetMapping("/tree")
    public Result<List<Map<String, Object>>> getPermissionTree() {
        return Result.success(List.of(
            Map.of(
                "id", 1,
                "name", "系统管理",
                "children", List.of(
                    Map.of("id", 11, "name", "用户管理", "code", "system:user"),
                    Map.of("id", 12, "name", "角色管理", "code", "system:role"),
                    Map.of("id", 13, "name", "权限管理", "code", "system:permission")
                )
            ),
            Map.of(
                "id", 2,
                "name", "商品管理",
                "children", List.of(
                    Map.of("id", 21, "name", "药品管理", "code", "product:drug"),
                    Map.of("id", 22, "name", "分类管理", "code", "product:category"),
                    Map.of("id", 23, "name", "品牌管理", "code", "product:brand")
                )
            ),
            Map.of(
                "id", 3,
                "name", "订单管理",
                "children", List.of(
                    Map.of("id", 31, "name", "订单列表", "code", "order:list"),
                    Map.of("id", 32, "name", "退款管理", "code", "order:refund")
                )
            )
        ));
    }
}
