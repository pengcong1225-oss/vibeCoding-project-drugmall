package com.drugmall.service;

import com.drugmall.dto.AIChatDTO;
import com.drugmall.dto.SymptomTestDTO;
import com.drugmall.vo.AIChatVO;
import com.drugmall.vo.FileUploadVO;
import com.drugmall.vo.SymptomTestVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * AI助手服务接口
 *
 * @author DrugMall Team
 */
public interface AIAssistantService {

    /**
     * 发送消息给AI助手
     *
     * @param chatDTO 对话请求
     * @return AI响应
     */
    AIChatVO chat(AIChatDTO chatDTO);

    /**
     * 清除会话历史
     *
     * @param sessionId 会话ID
     */
    void clearSession(String sessionId);

    /**
     * 上传文件（处方/药品图片）
     *
     * @param file 文件
     * @param purpose 文件意图
     * @return 文件上传结果
     */
    FileUploadVO uploadFile(MultipartFile file, String purpose);

    /**
     * 获取文件解析内容
     *
     * @param fileId 文件ID
     * @return 文件解析结果
     */
    FileUploadVO getFileContent(String fileId);

    /**
     * 症状自测
     *
     * @param symptomTestDTO 症状自测请求
     * @return 自测结果
     */
    SymptomTestVO symptomTest(SymptomTestDTO symptomTestDTO);
}
