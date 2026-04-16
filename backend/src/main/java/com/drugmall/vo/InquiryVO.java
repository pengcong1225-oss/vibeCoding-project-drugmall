package com.drugmall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 问诊记录列表VO
 *
 * @author DrugMall Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryVO {

    /** 问诊ID */
    private String id;

    /** 患者姓名 */
    private String patientName;

    /** 医生信息 */
    private DoctorInfo doctor;

    /** 科室 */
    private String department;

    /** 主诉症状 */
    private String chiefComplaint;

    /** 问诊类型：text-图文问诊, video-视频问诊, phone-电话问诊 */
    private String inquiryType;

    /** 问诊类型文本 */
    private String inquiryTypeText;

    /** 状态：waiting-待接诊, in_progress-问诊中, completed-已完成, cancelled-已取消 */
    private String status;

    /** 状态文本 */
    private String statusText;

    /** 诊断结果（完成后） */
    private String diagnosis;

    /** 咨询费用 */
    private BigDecimal fee;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 完成时间 */
    private LocalDateTime completeTime;

    /** 聊天消息数 */
    private Integer messageCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorInfo {
        private String id;
        private String name;
        private String title;      // 职称：主任医师、副主任医师、主治医师等
        private String avatar;     // 头像URL
        private String avatarColor; // 头像背景色
        private String avatarText;  // 头像文字
        private String hospital;   // 医院
        private String department;// 科室
        private Double rating;    // 评分
        private Integer consultationCount; // 咨询次数
        private List<String> specialties; // 擅长领域
    }
}
