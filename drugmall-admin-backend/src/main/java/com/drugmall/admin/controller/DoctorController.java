package com.drugmall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drugmall.admin.common.Result;
import com.drugmall.admin.entity.Department;
import com.drugmall.admin.entity.Doctor;
import com.drugmall.admin.mapper.DoctorMapper;
import com.drugmall.admin.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorMapper doctorMapper;
    private final DepartmentService departmentService;

    @GetMapping
    public Result<Map<String, Object>> getDoctorList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<Doctor>()
            .eq(Doctor::getIsDeleted, 0);
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Doctor::getName, keyword)
                    .or().like(Doctor::getPhone, keyword));
        }
        if (status != null) {
            wrapper.eq(Doctor::getStatus, status);
        }
        wrapper.orderByDesc(Doctor::getCreateTime);
        
        Page<Doctor> page = new Page<>(pageNum, pageSize);
        Page<Doctor> result = doctorMapper.selectPage(page, wrapper);
        
        return Result.success(Map.of(
            "list", result.getRecords(),
            "total", result.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @GetMapping("/{id}")
    public Result<Doctor> getDoctorDetail(@PathVariable String id) {
        Doctor doctor = doctorMapper.selectById(id);
        if (doctor == null || doctor.getIsDeleted() == 1) {
            return Result.error(404, "医生不存在");
        }
        return Result.success(doctor);
    }

    @PutMapping("/{id}")
    public Result<Void> updateDoctor(@PathVariable String id, @RequestBody Doctor doctor) {
        doctor.setId(id);
        doctorMapper.updateById(doctor);
        return Result.success();
    }

    @PatchMapping("/{id}/status")
    public Result<Void> updateDoctorStatus(@PathVariable String id, @RequestBody Map<String, Integer> body) {
        Doctor doctor = new Doctor();
        doctor.setId(id);
        doctor.setStatus(body.get("status"));
        doctorMapper.updateById(doctor);
        return Result.success();
    }

    @GetMapping("/audit")
    public Result<Map<String, Object>> getAuditList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status) {
        
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<Doctor>()
            .eq(Doctor::getIsDeleted, 0)
            .eq(Doctor::getIsCertified, 0);
        
        if (status != null) {
            wrapper.eq(Doctor::getStatus, status);
        }
        wrapper.orderByDesc(Doctor::getCreateTime);
        
        Page<Doctor> page = new Page<>(pageNum, pageSize);
        Page<Doctor> result = doctorMapper.selectPage(page, wrapper);
        
        return Result.success(Map.of(
            "list", result.getRecords(),
            "total", result.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PutMapping("/audit/{id}")
    public Result<Void> auditDoctor(@PathVariable String id, @RequestBody Map<String, Object> body) {
        Doctor doctor = new Doctor();
        doctor.setId(id);
        String result = (String) body.get("result");
        if ("approved".equals(result)) {
            doctor.setIsCertified(1);
            doctor.setStatus(1);
        } else {
            doctor.setStatus(0);
        }
        doctorMapper.updateById(doctor);
        return Result.success();
    }

    @GetMapping("/reviews")
    public Result<Map<String, Object>> getReviewList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Double rating) {
        
        return Result.success(Map.of(
            "list", List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @GetMapping("/reviews/negative")
    public Result<Map<String, Object>> getNegativeReviews(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        return Result.success(Map.of(
            "list", List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @GetMapping("/{id}/schedule")
    public Result<List<Map<String, Object>>> getDoctorSchedule(@PathVariable String id) {
        return Result.success(List.of());
    }

    @PostMapping("/{id}/schedule")
    public Result<Void> createSchedule(@PathVariable String id, @RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @PutMapping("/{id}/schedule/{scheduleId}")
    public Result<Void> updateSchedule(@PathVariable String id, @PathVariable Long scheduleId, @RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @DeleteMapping("/{id}/schedule/{scheduleId}")
    public Result<Void> deleteSchedule(@PathVariable String id, @PathVariable Long scheduleId) {
        return Result.success();
    }

    @GetMapping("/{id}/stats")
    public Result<Map<String, Object>> getDoctorStats(@PathVariable String id) {
        return Result.success(Map.of(
            "consultationCount", 0,
            "prescriptionCount", 0,
            "rating", 5.0,
            "income", 0.00
        ));
    }
}
