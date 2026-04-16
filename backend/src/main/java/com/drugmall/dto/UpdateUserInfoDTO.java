package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新用户信息DTO
 */
@Data
@Schema(description = "更新用户信息请求参数")
public class UpdateUserInfoDTO {

    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "生日", example = "1990-01-01")
    private String birthday;

    @Schema(description = "性别：0-未知，1-男，2-女", example = "1")
    private Integer gender;
}
