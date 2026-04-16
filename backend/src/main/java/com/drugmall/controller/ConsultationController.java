package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.dto.SendMessageDTO;
import com.drugmall.service.ConsultationService;
import com.drugmall.vo.ConsultationDetailVO;
import com.drugmall.vo.ConsultationVO;
import com.drugmall.vo.MessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 问诊管理控制器
 */
@RestController
@RequestMapping("/v1/consultations")
@Tag(name = "问诊管理", description = "问诊接诊、聊天、状态管理相关接口")
@Validated
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    private static final String CURRENT_DOCTOR_ID = "DOC001";

    @GetMapping
    @Operation(summary = "获取问诊列表", description = "获取医生的问诊列表，支持按状态筛选")
    public Result<List<ConsultationVO>> listConsultations(
            @Parameter(description = "状态: all/pending/processing/completed/closed")
            @RequestParam(required = false, defaultValue = "all") String status) {
        return Result.success(consultationService.listConsultations(CURRENT_DOCTOR_ID, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取问诊详情", description = "获取指定问诊的详细信息")
    public Result<ConsultationDetailVO> getConsultationDetail(
            @Parameter(description = "问诊ID") @PathVariable String id) {
        return Result.success(consultationService.getConsultationDetail(CURRENT_DOCTOR_ID, id));
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "开始问诊", description = "医生开始接诊")
    public Result<Boolean> startConsultation(
            @Parameter(description = "问诊ID") @PathVariable String id) {
        return Result.success(consultationService.startConsultation(CURRENT_DOCTOR_ID, id));
    }

    @PostMapping("/{id}/end")
    @Operation(summary = "结束问诊", description = "医生结束当前问诊")
    public Result<Boolean> endConsultation(
            @Parameter(description = "问诊ID") @PathVariable String id) {
        return Result.success(consultationService.endConsultation(CURRENT_DOCTOR_ID, id));
    }

    @GetMapping("/{id}/messages")
    @Operation(summary = "获取消息列表", description = "获取指定问诊的聊天记录")
    public Result<List<MessageVO>> getMessages(
            @Parameter(description = "问诊ID") @PathVariable String id) {
        return Result.success(consultationService.getMessages(CURRENT_DOCTOR_ID, id));
    }

    @PostMapping("/{id}/messages")
    @Operation(summary = "发送消息", description = "医生发送聊天消息")
    public Result<MessageVO> sendMessage(
            @Parameter(description = "问诊ID") @PathVariable String id,
            @Valid @RequestBody SendMessageDTO sendMessageDTO) {
        return Result.success(consultationService.sendMessage(CURRENT_DOCTOR_ID, id, sendMessageDTO));
    }
}
