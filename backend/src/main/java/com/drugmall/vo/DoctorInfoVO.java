package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 医生信息VO
 */
@Data
@Schema(description = "医生信息")
public class DoctorInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "医生ID")
    private String id;

    @Schema(description = "医生姓名")
    private String name;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "职称")
    private String title;

    @Schema(description = "医院")
    private String hospital;

    @Schema(description = "科室")
    private String department;

    @Schema(description = "是否已认证")
    private Boolean isCertified;

    @Schema(description = "评分")
    private Double rating;

    @Schema(description = "服务次数")
    private Integer serviceCount;

    @Schema(description = "平均响应时间(分钟)")
    private Integer responseTime;

    @Schema(description = "个人简介")
    private String introduction;

    @Schema(description = "专长")
    private List<String> specialties;

    @Schema(description = "是否在线")
    private Boolean isOnline;

    @Schema(description = "可开处方")
    private Boolean canPrescribe;

    @Schema(description = "等待时间(秒)")
    private Integer waitTime;

    @Schema(description = "价格")
    private Double price;

    @Schema(description = "接诊量显示文本")
    private String consultCount;
}
