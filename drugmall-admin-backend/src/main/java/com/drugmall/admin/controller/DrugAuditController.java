package com.drugmall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drugmall.admin.common.Result;
import com.drugmall.admin.entity.Drug;
import com.drugmall.admin.mapper.DrugMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/drug-audit")
@RequiredArgsConstructor
public class DrugAuditController {

    private final DrugMapper drugMapper;

    @GetMapping
    public Result<Map<String, Object>> getDrugAuditList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status) {
        
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<Drug>()
            .eq(Drug::getIsDeleted, 0);
        
        if (status != null && status == 0) {
            wrapper.eq(Drug::getStatus, 0);
        }
        wrapper.orderByDesc(Drug::getCreateTime);
        
        Page<Drug> page = new Page<>(pageNum, pageSize);
        Page<Drug> result = drugMapper.selectPage(page, wrapper);
        
        return Result.success(Map.of(
            "list", result.getRecords(),
            "total", result.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PutMapping("/{id}/audit")
    public Result<Void> auditDrug(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Drug drug = new Drug();
        drug.setId(id);
        String result = (String) body.get("result");
        if ("approved".equals(result)) {
            drug.setStatus(1);
        } else {
            drug.setStatus(-1);
        }
        drugMapper.updateById(drug);
        return Result.success();
    }
}
