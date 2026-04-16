package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 医生待办事项VO
 */
@Data
@Schema(description = "医生待办事项")
public class DoctorTodoCountVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "待办事项数量")
    private Integer todoCount;

    @Schema(description = "未读消息数量")
    private Integer unreadCount;
}
