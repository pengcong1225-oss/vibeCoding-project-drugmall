package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/help-center")
@RequiredArgsConstructor
public class HelpCenterController {

    @GetMapping("/list")
    public Result<Map<String, Object>> getHelpCenterList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String category) {
        
        return Result.success(Map.of(
            "list", List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PostMapping
    public Result<Void> createHelpCenter(@RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateHelpCenter(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteHelpCenter(@PathVariable Long id) {
        return Result.success();
    }
}
