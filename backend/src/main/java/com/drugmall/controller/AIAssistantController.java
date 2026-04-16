package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.dto.AIChatDTO;
import com.drugmall.dto.SymptomTestDTO;
import com.drugmall.service.AIAssistantService;
import com.drugmall.vo.AIChatVO;
import com.drugmall.vo.FileUploadVO;
import com.drugmall.vo.SymptomTestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * AI助手控制器
 * 提供AI智能对话接口
 *
 * @author DrugMall Team
 */
@Slf4j
@RestController
@RequestMapping("/ai")
@Tag(name = "AI助手", description = "AI智能助手相关接口")
public class AIAssistantController {

    @Autowired
    private AIAssistantService aiAssistantService;

    /**
     * 发送消息给AI助手
     */
    @PostMapping("/chat")
    @Operation(summary = "AI对话", description = "发送消息给AI助手，获取智能回复")
    public Result<AIChatVO> chat(
            @Parameter(description = "对话请求") @RequestBody @Validated AIChatDTO chatDTO) {
        log.info("AI对话请求 - 用户消息: {}", chatDTO.getMessage());

        AIChatVO response = aiAssistantService.chat(chatDTO);

        log.info("AI对话响应 - 会话ID: {}, 推荐药品数: {}",
                response.getSessionId(),
                response.getDrugs() != null ? response.getDrugs().size() : 0);

        return Result.success(response);
    }

    /**
     * 清除会话历史
     */
    @DeleteMapping("/session/{sessionId}")
    @Operation(summary = "清除会话", description = "清除指定会话的历史记录")
    public Result<Void> clearSession(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {
        log.info("清除会话 - 会话ID: {}", sessionId);

        aiAssistantService.clearSession(sessionId);

        return Result.success();
    }

    /**
     * 上传文件（处方/药品图片）
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传处方或药品图片进行识别")
    public Result<FileUploadVO> uploadFile(
            @Parameter(description = "上传的文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "文件意图") @RequestParam(value = "purpose", defaultValue = "medical") String purpose) {
        log.info("文件上传 - 文件名: {}, 大小: {}, 意图: {}", 
                file.getOriginalFilename(), file.getSize(), purpose);

        FileUploadVO response = aiAssistantService.uploadFile(file, purpose);

        log.info("文件上传成功 - 文件ID: {}", response.getId());

        return Result.success(response);
    }

    /**
     * 获取文件解析内容
     */
    @GetMapping("/file/{fileId}")
    @Operation(summary = "获取文件内容", description = "获取上传文件的解析内容")
    public Result<FileUploadVO> getFileContent(
            @Parameter(description = "文件ID") @PathVariable String fileId) {
        log.info("获取文件内容 - 文件ID: {}", fileId);

        FileUploadVO response = aiAssistantService.getFileContent(fileId);

        return Result.success(response);
    }

    /**
     * 症状自测
     */
    @PostMapping("/symptom-test")
    @Operation(summary = "症状自测", description = "根据症状进行智能分析")
    public Result<SymptomTestVO> symptomTest(
            @Parameter(description = "症状自测请求") @RequestBody @Validated SymptomTestDTO symptomTestDTO) {
        log.info("症状自测 - 症状: {}", symptomTestDTO.getSymptoms());

        SymptomTestVO response = aiAssistantService.symptomTest(symptomTestDTO);

        log.info("症状自测完成 - 紧急程度: {}, 是否需要就医: {}", 
                response.getUrgencyLevel(), response.getNeedDoctor());

        return Result.success(response);
    }
}
