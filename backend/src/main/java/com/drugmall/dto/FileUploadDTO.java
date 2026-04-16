package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

/**
 * 文件上传DTO
 *
 * @author DrugMall Team
 */
@Data
@Schema(description = "文件上传请求")
public class FileUploadDTO {

    @NotNull(message = "文件不能为空")
    @Schema(description = "上传的文件", required = true)
    private MultipartFile file;

    @Schema(description = "文件意图：medical-医疗识别", example = "medical")
    private String purpose = "medical";
}
