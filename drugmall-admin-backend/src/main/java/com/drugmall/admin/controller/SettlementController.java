package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/settlement")
@RequiredArgsConstructor
public class SettlementController {

    @GetMapping
    public Result<Map<String, Object>> getSettlementList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        
        return Result.success(Map.of(
            "list", List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PutMapping("/{id}/audit")
    public Result<Void> auditSettlement(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return Result.success();
    }
}
