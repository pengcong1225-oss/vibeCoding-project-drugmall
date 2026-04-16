package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 医生资料更新DTO
 */
@Data
@Schema(description = "医生资料更新请求参数")
public class DoctorProfileUpdateDTO {

    @Schema(description = "医生姓名", example = "张医生")
    private String name;

    @Schema(description = "职称", example = "主任医师")
    private String title;

    @Schema(description = "医院", example = "北京协和医院")
    private String hospital;

    @Schema(description = "科室", example = "心内科")
    private String department;

    @Schema(description = "个人简介")
    private String introduction;

    @Schema(description = "头像URL")
    private String avatar;
}
