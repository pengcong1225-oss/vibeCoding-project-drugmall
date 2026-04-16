package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.service.PrescriptionService;
import com.drugmall.vo.MedicalRecordVO;
import com.drugmall.vo.PatientDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 患者管理控制器
 */
@RestController
@RequestMapping("/v1/patients")
@Tag(name = "患者管理", description = "患者列表、详情、病历管理相关接口")
@Validated
public class PatientController {

    @Autowired
    private PrescriptionService prescriptionService;

    private static final String CURRENT_DOCTOR_ID = "DOC001";

    @GetMapping
    @Operation(summary = "获取患者列表", description = "获取医生的患者列表，支持关键词搜索")
    public Result<List<PatientDetailVO>> listPatients(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        return Result.success(prescriptionService.listPatients(CURRENT_DOCTOR_ID, keyword));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取患者详情", description = "获取指定患者的详细信息")
    public Result<PatientDetailVO> getPatientDetail(
            @Parameter(description = "患者ID") @PathVariable String id) {
        return Result.success(prescriptionService.getPatientDetail(CURRENT_DOCTOR_ID, id));
    }

    @GetMapping("/{id}/records")
    @Operation(summary = "获取病历记录", description = "获取指定患者的历史病历")
    public Result<List<MedicalRecordVO>> getMedicalRecords(
            @Parameter(description = "患者ID") @PathVariable String id) {
        return Result.success(prescriptionService.getMedicalRecords(CURRENT_DOCTOR_ID, id));
    }
}
