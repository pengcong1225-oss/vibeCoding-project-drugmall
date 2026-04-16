package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 文件上传VO
 *
 * @author DrugMall Team
 */
@Data
@Schema(description = "文件上传响应")
public class FileUploadVO {

    @Schema(description = "文件ID")
    private String id;

    @Schema(description = "文件名")
    private String filename;

    @Schema(description = "文件大小（字节）")
    private Integer bytes;

    @Schema(description = "创建时间（Unix时间戳）")
    private Long createdAt;

    @Schema(description = "文件意图")
    private String purpose;

    @Schema(description = "解析状态：init-待解析、parsing-解析中、online-解析成功、fail-解析失败、unsafe-未通过安全检查")
    private String status;

    @Schema(description = "解析内容")
    private String content;

    @Schema(description = "识别结果类型：prescription-处方、drug-药品、other-其他")
    private String recognizeType;

    @Schema(description = "识别结果详情")
    private Object recognizeResult;
}
