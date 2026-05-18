package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.service.PrescriptionService;
import com.drugmall.vo.DoctorPrescriptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/patient/prescriptions")
@Tag(name = "患者处方", description = "患者端查看处方相关接口")
@Validated
public class PatientPrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    /**
     * 从请求头获取当前患者ID
     * TODO: 后续集成JWT认证后，从Token中解析患者ID
     */
    private String getCurrentPatientId(@RequestHeader(value = "X-Patient-Id", required = false) String patientId) {
        // 如果没有提供patientId，使用默认值（仅用于开发测试）
        return patientId != null ? patientId : "USER001";
    }

    @GetMapping
    @Operation(summary = "获取我的处方列表", description = "患者查看自己的处方列表")
    public Result<List<DoctorPrescriptionVO>> listMyPrescriptions(
            @Parameter(description = "状态: all/pending/approved/rejected")
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestHeader(value = "X-Patient-Id", required = false) String patientId) {
        String currentPatientId = getCurrentPatientId(patientId);
        log.debug("获取患者处方列表: patientId={}, status={}", currentPatientId, status);

        List<DoctorPrescriptionVO> all = prescriptionService.listPatientPrescriptions(currentPatientId, status);
        return Result.success(all);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取处方详情", description = "患者查看处方详情")
    public Result<DoctorPrescriptionVO> getPrescriptionDetail(
            @Parameter(description = "处方ID") @PathVariable String id,
            @RequestHeader(value = "X-Patient-Id", required = false) String patientId) {
        String currentPatientId = getCurrentPatientId(patientId);
        log.debug("获取处方详情: patientId={}, prescriptionId={}", currentPatientId, id);

        return Result.success(prescriptionService.getPrescriptionDetail(currentPatientId, id));
    }
}
