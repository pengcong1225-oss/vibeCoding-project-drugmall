package com.drugmall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drugmall.admin.common.Result;
import com.drugmall.admin.entity.Consultation;
import com.drugmall.admin.entity.Doctor;
import com.drugmall.admin.entity.Patient;
import com.drugmall.admin.entity.User;
import com.drugmall.admin.mapper.ConsultationMapper;
import com.drugmall.admin.mapper.DoctorMapper;
import com.drugmall.admin.mapper.PatientMapper;
import com.drugmall.admin.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/consultations")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationMapper consultationMapper;
    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;
    private final UserMapper userMapper;

    @GetMapping
    public Result<Map<String, Object>> getConsultationList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        
        LambdaQueryWrapper<Consultation> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(Consultation::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Consultation::getId, keyword);
        }
        wrapper.orderByDesc(Consultation::getCreateTime);
        
        Page<Consultation> page = new Page<>(pageNum, pageSize);
        Page<Consultation> result = consultationMapper.selectPage(page, wrapper);
        
        return Result.success(Map.of(
            "list", result.getRecords(),
            "total", result.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getConsultationDetail(@PathVariable String id) {
        Consultation consultation = consultationMapper.selectById(id);
        if (consultation == null) {
            return Result.error(404, "问诊不存在");
        }
        
        Map<String, Object> detail = Map.of(
            "consultation", consultation,
            "doctor", doctorMapper.selectById(consultation.getDoctorId()),
            "patient", patientMapper.selectById(consultation.getPatientId()),
            "user", userMapper.selectById(consultation.getUserId()),
            "messages", List.of()
        );
        
        return Result.success(detail);
    }

    @PutMapping("/{id}/assign")
    public Result<Void> assignConsultation(@PathVariable String id, @RequestBody Map<String, String> body) {
        Consultation consultation = new Consultation();
        consultation.setId(id);
        consultation.setDoctorId(body.get("doctorId"));
        consultationMapper.updateById(consultation);
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancelConsultation(@PathVariable String id) {
        Consultation consultation = new Consultation();
        consultation.setId(id);
        consultation.setStatus("cancelled");
        consultationMapper.updateById(consultation);
        return Result.success();
    }

    @GetMapping("/assign-rules")
    public Result<Map<String, Object>> getAssignRules() {
        return Result.success(Map.of(
            "autoAssign", true,
            "assignStrategy", "round_robin",
            "maxConsultations", 10
        ));
    }

    @PutMapping("/assign-rules")
    public Result<Void> updateAssignRules(@RequestBody Map<String, Object> body) {
        return Result.success();
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getConsultationStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        List<Consultation> allConsultations = consultationMapper.selectList(null);
        long total = allConsultations.size();
        long pending = allConsultations.stream().filter(c -> "pending".equals(c.getStatus())).count();
        long completed = allConsultations.stream().filter(c -> "completed".equals(c.getStatus())).count();
        
        return Result.success(Map.of(
            "total", total,
            "pending", pending,
            "completed", completed,
            "cancelled", total - pending - completed
        ));
    }

    @GetMapping("/stats/trend")
    public Result<List<Map<String, Object>>> getConsultationTrend(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        return Result.success(List.of());
    }

    @GetMapping("/stats/departments")
    public Result<List<Map<String, Object>>> getConsultationDeptStats() {
        return Result.success(List.of());
    }

    @GetMapping("/stats/doctors")
    public Result<List<Map<String, Object>>> getConsultationDoctorStats() {
        return Result.success(List.of());
    }

    @GetMapping("/stats/hourly")
    public Result<List<Map<String, Object>>> getConsultationHourlyStats() {
        return Result.success(List.of());
    }

    @GetMapping("/exceptions")
    public Result<Map<String, Object>> getExceptionList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String type) {
        
        return Result.success(Map.of(
            "list", List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PutMapping("/exceptions/{id}/handle")
    public Result<Void> handleException(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return Result.success();
    }

    @GetMapping("/exceptions/stats")
    public Result<Map<String, Object>> getExceptionStats() {
        return Result.success(Map.of(
            "timeoutCount", 0,
            "complaintCount", 0,
            "refundCount", 0
        ));
    }
}
