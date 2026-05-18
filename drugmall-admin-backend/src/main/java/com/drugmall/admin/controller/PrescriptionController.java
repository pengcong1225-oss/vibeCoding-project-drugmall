package com.drugmall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drugmall.admin.common.Result;
import com.drugmall.admin.entity.Prescription;
import com.drugmall.admin.mapper.PrescriptionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/admin/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionMapper prescriptionMapper;

    @GetMapping
    public Result<Map<String, Object>> getPrescriptionList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        
        LambdaQueryWrapper<Prescription> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(Prescription::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Prescription::getId, keyword);
        }
        wrapper.orderByDesc(Prescription::getCreateTime);
        
        Page<Prescription> page = new Page<>(pageNum, pageSize);
        Page<Prescription> result = prescriptionMapper.selectPage(page, wrapper);
        
        return Result.success(Map.of(
            "list", result.getRecords(),
            "total", result.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @GetMapping("/{id}")
    public Result<Prescription> getPrescriptionDetail(@PathVariable String id) {
        Prescription prescription = prescriptionMapper.selectById(id);
        if (prescription == null) {
            return Result.error(404, "处方不存在");
        }
        return Result.success(prescription);
    }

    @PutMapping("/{id}/audit")
    public Result<Void> auditPrescription(@PathVariable String id, @RequestBody Map<String, Object> body) {
        log.info("审核处方: id={}, result={}", id, body.get("result"));
        
        Prescription prescription = prescriptionMapper.selectById(id);
        if (prescription == null) {
            return Result.error(404, "处方不存在");
        }
        
        // 验证状态：只有pending状态的处方才能审核
        if (!"pending".equals(prescription.getStatus())) {
            return Result.error(400, "该处方已审核，无法重复操作");
        }
        
        String result = (String) body.get("result");
        if (!"approved".equals(result) && !"rejected".equals(result)) {
            return Result.error(400, "审核结果必须为approved或rejected");
        }
        
        Prescription updatePrescription = new Prescription();
        updatePrescription.setId(id);
        
        if ("approved".equals(result)) {
            updatePrescription.setStatus("approved");
            log.info("处方审核通过: id={}", id);
        } else {
            String reason = (String) body.get("reason");
            if (!StringUtils.hasText(reason)) {
                return Result.error(400, "驳回时必须提供原因");
            }
            updatePrescription.setStatus("rejected");
            updatePrescription.setRejectReason(reason);
            log.info("处方审核驳回: id={}, reason={}", id, reason);
        }
        
        updatePrescription.setUpdateTime(LocalDateTime.now());
        prescriptionMapper.updateById(updatePrescription);
        
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancelPrescription(@PathVariable String id) {
        Prescription prescription = new Prescription();
        prescription.setId(id);
        prescription.setStatus("cancelled");
        prescriptionMapper.updateById(prescription);
        return Result.success();
    }

    @GetMapping("/audit")
    public Result<Map<String, Object>> getAuditList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        LambdaQueryWrapper<Prescription> wrapper = new LambdaQueryWrapper<Prescription>()
            .eq(Prescription::getStatus, "pending")
            .orderByAsc(Prescription::getCreateTime);
        
        Page<Prescription> page = new Page<>(pageNum, pageSize);
        Page<Prescription> result = prescriptionMapper.selectPage(page, wrapper);
        
        return Result.success(Map.of(
            "list", result.getRecords(),
            "total", result.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @GetMapping("/templates")
    public Result<Map<String, Object>> getTemplateList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        return Result.success(Map.of(
            "list", List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PostMapping("/templates")
    public Result<Void> createTemplate(@RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @PutMapping("/templates/{id}")
    public Result<Void> updateTemplate(@PathVariable String id, @RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @DeleteMapping("/templates/{id}")
    public Result<Void> deleteTemplate(@PathVariable String id) {
        return Result.success();
    }

    @PostMapping("/templates/{id}/copy")
    public Result<Void> copyTemplate(@PathVariable String id) {
        return Result.success();
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getPrescriptionStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        LambdaQueryWrapper<Prescription> wrapper = new LambdaQueryWrapper<>();
        
        // 日期范围过滤
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(Prescription::getCreateTime, LocalDateTime.parse(startDate + "T00:00:00"));
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(Prescription::getCreateTime, LocalDateTime.parse(endDate + "T23:59:59"));
        }
        
        List<Prescription> allPrescriptions = prescriptionMapper.selectList(wrapper);
        long total = allPrescriptions.size();
        long pending = allPrescriptions.stream().filter(p -> "pending".equals(p.getStatus())).count();
        long approved = allPrescriptions.stream().filter(p -> "approved".equals(p.getStatus())).count();
        long rejected = allPrescriptions.stream().filter(p -> "rejected".equals(p.getStatus())).count();
        
        // 计算总金额
        double totalAmount = allPrescriptions.stream()
                .mapToDouble(p -> p.getTotalAmount() != null ? p.getTotalAmount().doubleValue() : 0)
                .sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("approved", approved);
        stats.put("rejected", rejected);
        stats.put("totalAmount", totalAmount);
        stats.put("approvalRate", total > 0 ? (double) approved / total * 100 : 0);
        
        log.debug("处方统计: total={}, pending={}, approved={}, rejected={}", 
                total, pending, approved, rejected);
        
        return Result.success(stats);
    }

    @GetMapping("/stats/trend")
    public Result<List<Map<String, Object>>> getPrescriptionTrend(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        return Result.success(List.of());
    }

    @GetMapping("/stats/departments")
    public Result<List<Map<String, Object>>> getPrescriptionDeptStats() {
        return Result.success(List.of());
    }

    @GetMapping("/stats/doctors")
    public Result<List<Map<String, Object>>> getPrescriptionDoctorStats() {
        return Result.success(List.of());
    }

    @GetMapping("/stats/drugs")
    public Result<List<Map<String, Object>>> getPrescriptionDrugStats() {
        return Result.success(List.of());
    }
}
