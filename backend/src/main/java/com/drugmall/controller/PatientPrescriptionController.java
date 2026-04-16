package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.service.PrescriptionService;
import com.drugmall.vo.DoctorPrescriptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/patient/prescriptions")
@Tag(name = "患者处方", description = "患者端查看处方相关接口")
@Validated
public class PatientPrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    private static final String CURRENT_PATIENT_ID = "USER001";

    @GetMapping
    @Operation(summary = "获取我的处方列表", description = "患者查看自己的处方列表")
    public Result<List<DoctorPrescriptionVO>> listMyPrescriptions(
            @Parameter(description = "状态: all/pending/approved/rejected")
            @RequestParam(required = false, defaultValue = "all") String status) {
        List<DoctorPrescriptionVO> all = prescriptionService.listPrescriptions("DOC001", status);
        return Result.success(all);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取处方详情", description = "患者查看处方详情")
    public Result<DoctorPrescriptionVO> getPrescriptionDetail(
            @Parameter(description = "处方ID") @PathVariable String id) {
        return Result.success(prescriptionService.getPrescriptionDetail("DOC001", id));
    }
}
