package com.drugmall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 处方列表VO
 *
 * @author DrugMall Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionVO {

    /** 处方ID */
    private String id;

    /** 处方编号 */
    private String prescriptionNo;

    /** 患者姓名 */
    private String patientName;

    /** 患者年龄 */
    private Integer patientAge;

    /** 患者性别 */
    private String patientGender;

    /** 诊断结果 */
    private String diagnosis;

    /** 医生姓名 */
    private String doctorName;

    /** 医生职称 */
    private String doctorTitle;

    /** 科室 */
    private String department;

    /** 就诊医院 */
    private String hospital;

    /** 处方状态：pending-待审核, approved-已通过, rejected-已拒绝, expired-已过期, used-已使用 */
    private String status;

    /** 状态文本 */
    private String statusText;

    /** 开具时间 */
    private LocalDateTime createTime;

    /** 有效期至 */
    private LocalDateTime expireTime;

    /** 药品数量 */
    private Integer drugCount;
}
