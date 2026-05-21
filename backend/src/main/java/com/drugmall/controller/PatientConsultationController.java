package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.common.UserContext;
import com.drugmall.dto.PrescriptionApplyDTO;
import com.drugmall.dto.SendMessageDTO;
import com.drugmall.entity.DoctorReviewTag;
import com.drugmall.mapper.DoctorReviewMapper;
import com.drugmall.mapper.DoctorReviewTagMapper;
import com.drugmall.service.ConsultationService;
import com.drugmall.service.DoctorService;
import com.drugmall.vo.ConsultationDetailVO;
import com.drugmall.vo.ConsultationVO;
import com.drugmall.vo.DoctorInfoVO;
import com.drugmall.vo.MessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/patient/consultations")
@Tag(name = "患者问诊", description = "患者端发起问诊、查看问诊、聊天相关接口")
@Validated
@Slf4j
public class PatientConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorReviewMapper doctorReviewMapper;

    @Autowired
    private DoctorReviewTagMapper doctorReviewTagMapper;

    @GetMapping("/doctors")
    @Operation(summary = "获取在线医生列表", description = "患者查看可问诊的医生列表")
    public Result<List<DoctorInfoVO>> listDoctors(
            @Parameter(description = "科室") @RequestParam(required = false) String department,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        List<DoctorInfoVO> doctors = doctorService.listDoctors(department, keyword);
        return Result.success(doctors);
    }

    @GetMapping
    @Operation(summary = "获取我的问诊列表", description = "患者查看自己的问诊列表")
    public Result<List<ConsultationVO>> listMyConsultations(
            @Parameter(description = "状态: all/pending/processing/completed/closed")
            @RequestParam(required = false, defaultValue = "all") String status) {
        
        // 从用户上下文获取真实用户ID
        String patientId = UserContext.getCurrentUserIdString();
        log.info("获取用户咨询列表: patientId={}, status={}", patientId, status);
        
        List<ConsultationVO> all = consultationService.listPatientConsultations(patientId, status);
        return Result.success(all);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取问诊详情", description = "患者查看问诊详情")
    public Result<ConsultationDetailVO> getConsultationDetail(
            @Parameter(description = "问诊ID") @PathVariable String id) {
        return Result.success(consultationService.getConsultationDetail("DOC001", id));
    }

    @PostMapping
    @Operation(summary = "发起问诊", description = "患者发起新的问诊")
    public Result<ConsultationVO> createConsultation(
            @Parameter(description = "医生ID") @RequestParam String doctorId,
            @Parameter(description = "问诊类型") @RequestParam(defaultValue = "text") String type,
            @Parameter(description = "症状描述") @RequestParam String symptom,
            @Parameter(description = "患病时长") @RequestParam(required = false) String duration,
            @Parameter(description = "过敏史") @RequestParam(required = false) String allergies,
            @Parameter(description = "用药史") @RequestParam(required = false) String medication) {

        String patientId = UserContext.getCurrentUserIdString();
        log.info("患者发起问诊: patientId={}, doctorId={}, type={}, symptom={}, duration={}, allergies={}, medication={}",
                patientId, doctorId, type, symptom, duration, allergies, medication);

        // 将预问诊信息拼接进 symptom 字段
        StringBuilder fullSymptom = new StringBuilder(symptom);
        if (duration != null && !duration.isEmpty()) fullSymptom.append(" | 时长:").append(duration);
        if (allergies != null && !allergies.isEmpty()) fullSymptom.append(" | 过敏:").append(allergies);
        if (medication != null && !medication.isEmpty()) fullSymptom.append(" | 用药:").append(medication);

        ConsultationVO vo = consultationService.createConsultation(patientId, doctorId, type, fullSymptom.toString());
        return Result.success(vo);
    }

    @GetMapping("/{id}/messages")
    @Operation(summary = "获取聊天记录", description = "患者获取问诊聊天记录")
    public Result<List<MessageVO>> getMessages(
            @Parameter(description = "问诊ID") @PathVariable String id) {
        // 从用户上下文获取真实用户ID
        String patientId = UserContext.getCurrentUserIdString();
        
        // 从问诊记录中获取医生ID
        ConsultationDetailVO detail = consultationService.getConsultationDetail(patientId, id);
        String doctorId = detail != null ? detail.getDoctorId() : "DOC001";
        return Result.success(consultationService.getMessages(doctorId, id));
    }

    @PostMapping("/{id}/messages")
    @Operation(summary = "发送消息", description = "患者发送聊天消息")
    public Result<MessageVO> sendMessage(
            @Parameter(description = "问诊ID") @PathVariable String id,
            @Valid @RequestBody SendMessageDTO sendMessageDTO) {
        
        // 从用户上下文获取真实用户ID
        String patientId = UserContext.getCurrentUserIdString();

        // 从问诊记录中获取医生ID
        ConsultationDetailVO detail = consultationService.getConsultationDetail(patientId, id);
        String doctorId = detail != null ? detail.getDoctorId() : "DOC001";
        sendMessageDTO.setSenderType("patient");
        MessageVO msg = consultationService.sendMessage(patientId, id, sendMessageDTO);
        return Result.success(msg);
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "开始问诊", description = "患者确认开始问诊")
    public Result<Boolean> startConsultation(
            @Parameter(description = "问诊ID") @PathVariable String id) {
        return Result.success(true);
    }

    @PostMapping("/{id}/end")
    @Operation(summary = "结束问诊", description = "患者结束问诊")
    public Result<Boolean> endConsultation(
            @Parameter(description = "问诊ID") @PathVariable String id) {
        return Result.success(true);
    }

    @GetMapping("/doctors/{id}")
    @Operation(summary = "获取医生详情", description = "患者查看医生详细信息")
    public Result<DoctorInfoVO> getDoctorDetail(
            @Parameter(description = "医生ID") @PathVariable String id) {
        return Result.success(doctorService.getProfile(id));
    }

    @GetMapping("/doctors/{id}/reviews")
    @Operation(summary = "获取医生评价列表", description = "患者查看医生评价")
    public Result<List<Map<String, Object>>> getDoctorReviews(
            @Parameter(description = "医生ID") @PathVariable String id,
            @Parameter(description = "标签") @RequestParam(required = false) String tag,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        int offset = (page - 1) * size;
        List<Map<String, Object>> reviews = doctorReviewMapper.selectByDoctorId(id, tag, offset, size);
        return Result.success(reviews);
    }

    @GetMapping("/doctors/{id}/review-tags")
    @Operation(summary = "获取医生评价标签", description = "患者查看医生评价标签统计")
    public Result<List<Map<String, Object>>> getDoctorReviewTags(
            @Parameter(description = "医生ID") @PathVariable String id) {
        List<DoctorReviewTag> tags = doctorReviewTagMapper.selectByDoctorId(id);
        return Result.success(tags.stream().map(t -> {
            Map<String, Object> tag = new HashMap<>();
            tag.put("name", t.getTagName());
            tag.put("count", t.getTagCount());
            return tag;
        }).collect(Collectors.toList()));
    }

    @GetMapping("/{id}/acceptance")
    @Operation(summary = "检查医生接诊状态", description = "患者检查医生是否已接诊")
    public Result<Map<String, Object>> checkDoctorAcceptance(
            @Parameter(description = "问诊ID") @PathVariable String id) {
        
        // 从用户上下文获取真实用户ID
        String patientId = UserContext.getCurrentUserIdString();
        
        ConsultationDetailVO detail = consultationService.getConsultationDetail(patientId, id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("accepted", "processing".equals(detail.getStatus()));
        result.put("status", detail.getStatus());
        result.put("doctorId", detail.getDoctorId());
        
        return Result.success(result);
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "支付问诊", description = "患者支付问诊费用")
    public Result<Map<String, Object>> payConsultation(
            @Parameter(description = "问诊ID") @PathVariable String id,
            @RequestBody(required = false) Map<String, String> payData) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("consultationId", id);
        result.put("status", "paid");
        return Result.success(result);
    }

    @PostMapping("/apply-prescription")
    @Operation(summary = "申请处方药", description = "患者申请处方药，系统自动分配医生")
    public Result<Map<String, Object>> applyPrescription(@Valid @RequestBody PrescriptionApplyDTO applyDTO) {
        log.info("收到处方药申请: drugId={}, patientId={}", applyDTO.getDrugId(), applyDTO.getPatientId());

        String patientId = UserContext.getCurrentUserIdString();

        // 自动分配医生：根据药品分类或随机分配在线的可开方医生
        String assignedDoctorId = doctorService.assignDoctorForPrescription(applyDTO.getDrugId());
        log.info("处方购药自动分配医生: {}", assignedDoctorId);

        ConsultationVO consultation = consultationService.createConsultationForPrescription(
            patientId,
            assignedDoctorId,
            applyDTO.getDrugId(),
            Long.valueOf(applyDTO.getPatientId()),
            applyDTO.getDiseases(),
            applyDTO.getSymptoms()
        );

        Map<String, Object> result = new HashMap<>();
        result.put("consultationId", consultation.getId());
        result.put("status", consultation.getStatus());
        result.put("doctorId", assignedDoctorId);
        
        log.info("处方药申请成功: consultationId={}", consultation.getId());
        return Result.success(result);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消问诊", description = "患者取消待接诊的问诊")
    public Result<Boolean> cancelConsultation(
            @Parameter(description = "问诊ID") @PathVariable String id) {
        
        // 从用户上下文获取真实用户ID
        String patientId = UserContext.getCurrentUserIdString();
        
        log.info("患者取消问诊: consultationId={}, patientId={}", id, patientId);
        consultationService.cancelConsultation(patientId, id);
        return Result.success(true);
    }

    @PostMapping("/{id}/remind")
    @Operation(summary = "提醒医生", description = "患者提醒医生接诊")
    public Result<Boolean> remindDoctor(
            @Parameter(description = "问诊ID") @PathVariable String id) {
        
        // 从用户上下文获取真实用户ID
        String patientId = UserContext.getCurrentUserIdString();
        
        log.info("患者提醒医生: consultationId={}, patientId={}", id, patientId);
        consultationService.remindDoctor(patientId, id);
        return Result.success(true);
    }
}
