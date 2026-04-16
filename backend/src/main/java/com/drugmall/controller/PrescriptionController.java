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

    private static final String CURRENT_DOCTOR_ID = "DOC001";

    @GetMapping
    @Operation(summary = "获取处方列表", description = "获取医生的处方列表，支持按状态筛选")
    public Result<List<DoctorPrescriptionVO>> listPrescriptions(
            @Parameter(description = "状态: all/pending/approved/rejected")
            @RequestParam(required = false, defaultValue = "all") String status) {
        return Result.success(prescriptionService.listPrescriptions(CURRENT_DOCTOR_ID, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取处方详情", description = "获取指定处方的详细信息")
    public Result<DoctorPrescriptionVO> getPrescriptionDetail(
            @Parameter(description = "处方ID") @PathVariable String id) {
        return Result.success(prescriptionService.getPrescriptionDetail(CURRENT_DOCTOR_ID, id));
    }

    @PostMapping
    @Operation(summary = "创建处方", description = "医生创建新处方")
    public Result<DoctorPrescriptionVO> createPrescription(
            @Valid @RequestBody CreatePrescriptionDTO createDTO) {
        return Result.success(prescriptionService.createPrescription(CURRENT_DOCTOR_ID, createDTO));
    }
}
