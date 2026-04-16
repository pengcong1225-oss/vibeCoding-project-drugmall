package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.dto.WithdrawApplyDTO;
import com.drugmall.service.PrescriptionService;
import com.drugmall.vo.*;
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
 * 医生端收入管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/v1/income")
@Tag(name = "收入管理", description = "医生收入查询、提现相关接口")
@Validated
public class IncomeController {

    @Autowired
    private PrescriptionService prescriptionService;

    private static final String CURRENT_DOCTOR_ID = "DOC001";

    @GetMapping("/overview")
    @Operation(summary = "获取收入总览", description = "获取医生的收入总览数据")
    public Result<IncomeOverviewVO> getOverview() {
        return Result.success(prescriptionService.getIncomeOverview(CURRENT_DOCTOR_ID));
    }

    @GetMapping("/list")
    @Operation(summary = "获取收入明细", description = "获取医生的收入明细列表")
    public Result<List<IncomeRecordVO>> getIncomeList() {
        return Result.success(prescriptionService.getIncomeList(CURRENT_DOCTOR_ID));
    }

    @GetMapping("/trend")
    @Operation(summary = "获取收入趋势", description = "获取收入趋势数据")
    public Result<List<IncomeTrendVO>> getTrend(
            @Parameter(description = "时间维度: week/month/year")
            @RequestParam(required = false, defaultValue = "week") String dimension) {
        return Result.success(prescriptionService.getIncomeTrend(CURRENT_DOCTOR_ID, dimension));
    }

    @GetMapping("/composition")
    @Operation(summary = "获取收入构成", description = "获取各收入类型的占比")
    public Result<List<IncomeCompositionVO>> getComposition() {
        return Result.success(prescriptionService.getIncomeComposition(CURRENT_DOCTOR_ID));
    }

    @GetMapping("/withdraw/list")
    @Operation(summary = "获取提现记录", description = "获取医生的提现记录列表")
    public Result<List<WithdrawRecordVO>> getWithdrawList() {
        return Result.success(prescriptionService.getWithdrawList(CURRENT_DOCTOR_ID));
    }

    @PostMapping("/withdraw")
    @Operation(summary = "申请提现", description = "提交提现申请")
    public Result<Boolean> applyWithdraw(@Valid @RequestBody WithdrawApplyDTO withdrawDTO) {
        return Result.success(prescriptionService.applyWithdraw(CURRENT_DOCTOR_ID, withdrawDTO));
    }
}
