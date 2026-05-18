package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.dto.SendMessageDTO;
import com.drugmall.entity.Consultation;
import com.drugmall.entity.Drug;
import com.drugmall.mapper.ConsultationMapper;
import com.drugmall.mapper.DrugMapper;
import com.drugmall.service.ConsultationService;
import com.drugmall.service.DrugService;
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
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;

/**
 * 问诊管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/v1/consultations")
@Tag(name = "问诊管理", description = "问诊接诊、聊天、状态管理相关接口")
@Validated
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private ConsultationMapper consultationMapper;

    @Autowired
    private DrugMapper drugMapper;

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
    @Operation(summary = "开始问诊", description = "医生开始接诊（同accept）")
    public Result<Boolean> startConsultation(
            @Parameter(description = "问诊ID") @PathVariable String id) {
        return Result.success(consultationService.startConsultation(CURRENT_DOCTOR_ID, id));
    }

    @PostMapping("/{id}/accept")
    @Operation(summary = "接诊", description = "医生接诊患者的问诊请求")
    public Result<Map<String, Object>> acceptConsultation(
            @Parameter(description = "问诊ID") @PathVariable String id) {
        
        consultationService.acceptConsultation(CURRENT_DOCTOR_ID, id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("consultationId", id);
        result.put("status", "processing");
        
        log.info("医生接诊成功: consultationId={}, doctorId={}", id, CURRENT_DOCTOR_ID);
        return Result.success(result);
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "拒绝接诊", description = "医生拒绝接诊")
    public Result<Map<String, Object>> rejectConsultation(
            @Parameter(description = "问诊ID") @PathVariable String id,
            @RequestBody Map<String, String> body) {
        
        String reason = body.getOrDefault("reason", "无法接诊");
        consultationService.rejectConsultation(CURRENT_DOCTOR_ID, id, reason);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("consultationId", id);
        result.put("status", "closed");
        
        log.info("医生拒绝接诊: consultationId={}, doctorId={}, reason={}", id, CURRENT_DOCTOR_ID, reason);
        return Result.success(result);
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

    @GetMapping("/{id}/requested-drugs")
    @Operation(summary = "获取患者申请的药品", description = "获取患者在发起问诊时申请的药品列表")
    public Result<List<Drug>> getRequestedDrugs(
            @Parameter(description = "问诊ID") @PathVariable String id) {
        log.info("获取问诊关联的药品: consultationId={}", id);
        
        // 从数据库查询问诊记录
        Consultation consultation = consultationMapper.selectById(id);
        if (consultation == null) {
            return Result.error("问诊不存在");
        }
        
        List<Drug> drugs = new ArrayList<>();
        
        // 解析 requestedDrugIds (JSON数组格式: ["drugId1", "drugId2"])
        String requestedDrugIds = consultation.getRequestedDrugIds();
        if (requestedDrugIds != null && !requestedDrugIds.isEmpty()) {
            try {
                // 简单解析JSON数组，提取drugId
                // 格式: ["123", "456"]
                String cleaned = requestedDrugIds.replace("[", "").replace("]", "").replace("\"", "");
                String[] drugIdArray = cleaned.split(",");
                
                for (String drugIdStr : drugIdArray) {
                    drugIdStr = drugIdStr.trim();
                    if (!drugIdStr.isEmpty()) {
                        // 查询药品详情
                        Drug drug = drugMapper.selectById(drugIdStr);
                        if (drug != null) {
                            drugs.add(drug);
                        }
                    }
                }
                
                log.info("成功获取 {} 个患者申请的药品", drugs.size());
            } catch (Exception e) {
                log.error("解析 requestedDrugIds 失败: {}", requestedDrugIds, e);
            }
        }
        
        return Result.success(drugs);
    }
}
