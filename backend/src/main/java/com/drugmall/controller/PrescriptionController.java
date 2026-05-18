package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.dto.CreatePrescriptionDTO;
import com.drugmall.service.PrescriptionService;
import com.drugmall.vo.DoctorPrescriptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医生端处方管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/v1/prescriptions")
@Tag(name = "医生处方管理", description = "医生端处方创建、查询相关接口")
@Validated
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    /**
     * 从请求头获取当前医生ID
     * TODO: 后续集成JWT认证后，从Token中解析医生ID
     */
    private String getCurrentDoctorId(@RequestHeader(value = "X-Doctor-Id", required = false) String doctorId) {
        // 如果没有提供doctorId，使用默认值（仅用于开发测试）
        return doctorId != null ? doctorId : "DOC001";
    }

    @GetMapping
    @Operation(summary = "获取处方列表", description = "获取医生的处方列表，支持按状态筛选")
    public Result<List<DoctorPrescriptionVO>> listPrescriptions(
            @Parameter(description = "状态: all/pending/approved/rejected")
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestHeader(value = "X-Doctor-Id", required = false) String doctorId) {
        String currentDoctorId = getCurrentDoctorId(doctorId);
        log.debug("获取处方列表: doctorId={}, status={}", currentDoctorId, status);
        return Result.success(prescriptionService.listPrescriptions(currentDoctorId, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取处方详情", description = "获取指定处方的详细信息")
    public Result<DoctorPrescriptionVO> getPrescriptionDetail(
            @Parameter(description = "处方ID") @PathVariable String id,
            @RequestHeader(value = "X-Doctor-Id", required = false) String doctorId) {
        String currentDoctorId = getCurrentDoctorId(doctorId);
        log.debug("获取处方详情: doctorId={}, prescriptionId={}", currentDoctorId, id);
        return Result.success(prescriptionService.getPrescriptionDetail(currentDoctorId, id));
    }

    @PostMapping
    @Operation(summary = "创建处方", description = "医生创建新处方")
    public Result<DoctorPrescriptionVO> createPrescription(
            @Valid @RequestBody CreatePrescriptionDTO createDTO,
            @RequestHeader(value = "X-Doctor-Id", required = false) String doctorId) {
        String currentDoctorId = getCurrentDoctorId(doctorId);
        log.info("创建处方: doctorId={}, patientId={}, consultationId={}", 
                currentDoctorId, createDTO.getPatientId(), createDTO.getConsultationId());
        return Result.success(prescriptionService.createPrescription(currentDoctorId, createDTO));
    }
}
