package com.drugmall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 问诊详情VO
 *
 * @author DrugMall Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDetailVO {

    /** 问诊ID */
    private String id;

    /** 患者姓名 */
    private String patientName;

    /** 医生信息 */
    private InquiryVO.DoctorInfo doctor;

    /** 科室 */
    private String department;

    /** 主诉症状 */
    private String chiefComplaint;

    /** 问诊类型 */
    private String inquiryType;

    /** 问诊类型文本 */
    private String inquiryTypeText;

    /** 状态 */
    private String status;

    /** 状态文本 */
    private String statusText;

    /** 诊断结果 */
    private String diagnosis;

    /** 咨询费用 */
    private BigDecimal fee;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 完成时间 */
    private LocalDateTime completeTime;

    /** 聊天消息数 */
    private Integer messageCount;

    /** 详细病情描述 */
    private String description;

    /** 图片列表 */
    private List<String> images;

    /** 医生建议 */
    private String doctorAdvice;

    /** 是否开具处方 */
    private Boolean hasPrescription;

    /** 处方ID */
    private String prescriptionId;

    /** 用药指导 */
    private String medicationGuide;

    /** 生活建议 */
    private String lifestyleAdvice;

    /** 是否需要复诊 */
    private Boolean needFollowUp;

    /** 复诊时间建议 */
    private String followUpAdvice;

    /** 聊天记录 */
    private List<ChatMessage> chatHistory;

    /** 用户评价 */
    private ReviewInfo review;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessage {
        private String id;
        private String senderType;
        private String senderName;
        private String content;
        private LocalDateTime sendTime;
        private String messageType;
        private List<String> images;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewInfo {
        private Integer rating;
        private String content;
        private List<String> tags;
        private LocalDateTime reviewTime;
        private Boolean isAnonymous;
    }
}
