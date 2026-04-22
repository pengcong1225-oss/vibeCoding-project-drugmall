package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.dto.SendMessageDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/patient/consultations")
@Tag(name = "患者问诊", description = "患者端发起问诊、查看问诊、聊天相关接口")
@Validated
public class PatientConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private DoctorService doctorService;

    private static final String CURRENT_PATIENT_ID = "USER001";

    @GetMapping("/doctors")
    @Operation(summary = "获取在线医生列表", description = "患者查看可问诊的医生列表")
    public Result<List<DoctorInfoVO>> listDoctors(
            @Parameter(description = "科室") @RequestParam(required = false) String department,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        DoctorInfoVO doctor = doctorService.getProfile("DOC001");
        List<DoctorInfoVO> doctors = new ArrayList<>();
        if (doctor != null) {
            doctors.add(doctor);
        }
        return Result.success(doctors);
    }

    @GetMapping
    @Operation(summary = "获取我的问诊列表", description = "患者查看自己的问诊列表")
    public Result<List<ConsultationVO>> listMyConsultations(
            @Parameter(description = "状态: all/pending/processing/completed/closed")
            @RequestParam(required = false, defaultValue = "all") String status) {
        List<ConsultationVO> all = consultationService.listConsultations("DOC001", status);
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
            @Parameter(description = "问诊类型") @RequestParam(defaultValue = "image") String type,
            @Parameter(description = "症状描述") @RequestParam String symptom) {
        ConsultationVO vo = new ConsultationVO();
        vo.setId("C" + System.currentTimeMillis());
        vo.setPatientId(CURRENT_PATIENT_ID);
        vo.setPatientName("当前用户");
        vo.setPatientAge(30);
        vo.setPatientGender("男");
        vo.setPatientAvatar("");
        vo.setType(type);
        vo.setStatus("pending");
        vo.setSymptom(symptom);
        vo.setWaitTime("0分钟");
        vo.setRemainingTime("30:00");
        vo.setIsUrgent(false);
        vo.setIsRx(false);
        vo.setCreateTime(java.time.LocalDateTime.now().toString());
        return Result.success(vo);
    }

    @GetMapping("/{id}/messages")
    @Operation(summary = "获取聊天记录", description = "患者获取问诊聊天记录")
    public Result<List<MessageVO>> getMessages(
            @Parameter(description = "问诊ID") @PathVariable String id) {
        return Result.success(consultationService.getMessages("DOC001", id));
    }

    @PostMapping("/{id}/messages")
    @Operation(summary = "发送消息", description = "患者发送聊天消息")
    public Result<MessageVO> sendMessage(
            @Parameter(description = "问诊ID") @PathVariable String id,
            @Valid @RequestBody SendMessageDTO sendMessageDTO) {
        MessageVO msg = consultationService.sendMessage("DOC001", id, sendMessageDTO);
        if (msg != null) {
            msg.setSender("patient");
        }
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
        List<Map<String, Object>> reviews = new ArrayList<>();
        Map<String, Object> review = new HashMap<>();
        review.put("id", "R001");
        review.put("userName", "用**");
        review.put("type", "图文问诊");
        review.put("date", "2024-12-01");
        review.put("satisfaction", "very_satisfied");
        review.put("satisfactionText", "非常满意");
        review.put("content", "医生很专业，解答详细");
        review.put("tags", Arrays.asList("专业", "耐心"));
        reviews.add(review);
        return Result.success(reviews);
    }

    @GetMapping("/doctors/{id}/review-tags")
    @Operation(summary = "获取医生评价标签", description = "患者查看医生评价标签统计")
    public Result<List<Map<String, Object>>> getDoctorReviewTags(
            @Parameter(description = "医生ID") @PathVariable String id) {
        List<Map<String, Object>> tags = new ArrayList<>();
        String[] tagNames = {"专业", "耐心", "回复快", "态度好", "建议有效"};
        int[] counts = {128, 96, 85, 72, 65};
        for (int i = 0; i < tagNames.length; i++) {
            Map<String, Object> tag = new HashMap<>();
            tag.put("name", tagNames[i]);
            tag.put("count", counts[i]);
            tags.add(tag);
        }
        return Result.success(tags);
    }

    @GetMapping("/{id}/acceptance")
    @Operation(summary = "检查医生接诊状态", description = "患者检查医生是否已接诊")
    public Result<Map<String, Object>> checkDoctorAcceptance(
            @Parameter(description = "问诊ID") @PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        result.put("accepted", false);
        result.put("doctorId", "DOC001");
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
}
