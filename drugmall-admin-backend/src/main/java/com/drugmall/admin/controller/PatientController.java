package com.drugmall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drugmall.admin.common.Result;
import com.drugmall.admin.entity.Patient;
import com.drugmall.admin.mapper.PatientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientMapper patientMapper;

    @GetMapping
    public Result<Map<String, Object>> getPatientList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long userId) {
        
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<Patient>()
            .eq(Patient::getIsDeleted, 0);
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Patient::getName, keyword);
        }
        if (userId != null) {
            wrapper.eq(Patient::getUserId, userId);
        }
        wrapper.orderByDesc(Patient::getCreateTime);
        
        Page<Patient> page = new Page<>(pageNum, pageSize);
        Page<Patient> result = patientMapper.selectPage(page, wrapper);
        
        return Result.success(Map.of(
            "list", result.getRecords(),
            "total", result.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }
}
