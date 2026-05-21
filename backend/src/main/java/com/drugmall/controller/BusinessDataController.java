package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.service.BusinessDataService;
import com.drugmall.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/business")
@Tag(name = "业务数据", description = "业务数据字典接口")
public class BusinessDataController {

    @Autowired
    private BusinessDataService businessDataService;

    @GetMapping("/departments")
    @Operation(summary = "获取科室列表", description = "获取所有启用的科室")
    public Result<List<DepartmentVO>> getDepartments() {
        return Result.success(businessDataService.getDepartments());
    }

    @GetMapping("/departments/{code}/config")
    @Operation(summary = "获取科室配置", description = "获取指定科室的详细配置信息")
    public Result<DepartmentConfigVO> getDepartmentConfig(
            @Parameter(description = "科室编码") @PathVariable String code) {
        return Result.success(businessDataService.getDepartmentConfig(code));
    }

    @GetMapping("/department-tags")
    @Operation(summary = "获取科室标签", description = "获取科室筛选标签列表")
    public Result<List<DepartmentTagVO>> getDepartmentTags() {
        return Result.success(businessDataService.getDepartmentTags());
    }

    @GetMapping("/dict/{typeCode}")
    @Operation(summary = "获取字典数据", description = "根据字典类型编码获取字典数据列表")
    public Result<List<DictDataVO>> getDictData(
            @Parameter(description = "字典类型编码") @PathVariable String typeCode) {
        return Result.success(businessDataService.getDictData(typeCode));
    }

    @GetMapping("/payment-methods")
    @Operation(summary = "获取支付方式", description = "获取所有启用的支付方式")
    public Result<List<PaymentMethodVO>> getPaymentMethods() {
        return Result.success(businessDataService.getPaymentMethods());
    }

    @GetMapping("/service-shortcuts")
    @Operation(summary = "获取服务快捷入口", description = "获取首页服务快捷入口列表")
    public Result<List<ServiceShortcutVO>> getServiceShortcuts() {
        return Result.success(businessDataService.getServiceShortcuts());
    }

    @GetMapping("/consultation-steps")
    @Operation(summary = "获取问诊流程步骤", description = "获取问诊流程步骤列表")
    public Result<List<ConsultationStepVO>> getConsultationSteps() {
        return Result.success(businessDataService.getConsultationSteps());
    }
}
