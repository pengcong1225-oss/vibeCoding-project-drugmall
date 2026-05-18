package com.drugmall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drugmall.admin.common.Result;
import com.drugmall.admin.entity.Store;
import com.drugmall.admin.mapper.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreMapper storeMapper;

    @GetMapping
    public Result<Map<String, Object>> getStoreList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<Store>()
            .eq(Store::getIsDeleted, 0);
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Store::getStoreName, keyword);
        }
        if (status != null) {
            wrapper.eq(Store::getStatus, status);
        }
        wrapper.orderByDesc(Store::getCreateTime);
        
        Page<Store> page = new Page<>(pageNum, pageSize);
        Page<Store> result = storeMapper.selectPage(page, wrapper);
        
        return Result.success(Map.of(
            "list", result.getRecords(),
            "total", result.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @GetMapping("/{id}")
    public Result<Store> getStoreDetail(@PathVariable Long id) {
        Store store = storeMapper.selectById(id);
        if (store == null || store.getIsDeleted() == 1) {
            return Result.error(404, "药店不存在");
        }
        return Result.success(store);
    }

    @PutMapping("/{id}")
    public Result<Void> updateStore(@PathVariable Long id, @RequestBody Store store) {
        store.setId(id);
        storeMapper.updateById(store);
        return Result.success();
    }

    @PatchMapping("/{id}/status")
    public Result<Void> updateStoreStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Store store = new Store();
        store.setId(id);
        store.setStatus(body.get("status"));
        storeMapper.updateById(store);
        return Result.success();
    }

    @GetMapping("/audit")
    public Result<Map<String, Object>> getAuditList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<Store>()
            .eq(Store::getIsDeleted, 0)
            .eq(Store::getStatus, 0)
            .orderByDesc(Store::getCreateTime);
        
        Page<Store> page = new Page<>(pageNum, pageSize);
        Page<Store> result = storeMapper.selectPage(page, wrapper);
        
        return Result.success(Map.of(
            "list", result.getRecords(),
            "total", result.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PutMapping("/audit/{id}")
    public Result<Void> auditStore(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Store store = new Store();
        store.setId(id);
        String result = (String) body.get("result");
        if ("approved".equals(result)) {
            store.setStatus(1);
        } else {
            store.setStatus(-1);
        }
        storeMapper.updateById(store);
        return Result.success();
    }

    @GetMapping("/{id}/drugs")
    public Result<Map<String, Object>> getStoreDrugs(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        return Result.success(Map.of(
            "list", java.util.List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PostMapping("/{id}/drugs")
    public Result<Void> addStoreDrug(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @PutMapping("/{id}/drugs/{drugId}")
    public Result<Void> updateStoreDrug(@PathVariable Long id, @PathVariable Long drugId, @RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @DeleteMapping("/{id}/drugs/{drugId}")
    public Result<Void> removeStoreDrug(@PathVariable Long id, @PathVariable Long drugId) {
        return Result.success();
    }
}
