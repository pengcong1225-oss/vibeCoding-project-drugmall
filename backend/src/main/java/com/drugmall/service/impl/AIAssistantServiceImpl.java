package com.drugmall.service.impl;

import com.drugmall.common.AIConstants;
import com.drugmall.common.BusinessException;
import com.drugmall.common.ResultCode;
import com.drugmall.config.AIConfig;
import com.drugmall.dto.AIChatDTO;
import com.drugmall.dto.SymptomTestDTO;
import com.drugmall.entity.Drug;
import com.drugmall.mapper.DrugMapper;
import com.drugmall.service.AIAssistantService;
import com.drugmall.vo.AIChatVO;
import com.drugmall.vo.FileUploadVO;
import com.drugmall.vo.SymptomTestVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI助手服务实现类
 * 集成百川大模型API
 *
 * @author DrugMall Team
 */
@Slf4j
@Service
public class AIAssistantServiceImpl implements AIAssistantService {

    @Autowired
    private AIConfig aiConfig;

    @Autowired
    private DrugMapper drugMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 会话历史缓存（生产环境建议使用Redis）
     */
    private final Map<String, List<Map<String, String>>> sessionHistoryMap = new ConcurrentHashMap<>();

    @Override
    public AIChatVO chat(AIChatDTO chatDTO) {
        try {
            // 1. 获取或创建会话历史
            String sessionId = chatDTO.getSessionId();
            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = UUID.randomUUID().toString();
            }

            List<Map<String, String>> history = sessionHistoryMap.computeIfAbsent(
                    sessionId, k -> new ArrayList<>()
            );

            // 2. 构建请求消息
            List<Map<String, String>> messages = buildMessages(history, chatDTO.getMessage());

            // 3. 调用百川API
            String aiResponse = callBaichuanAPI(messages);

            // 4. 更新会话历史
            history.add(createMessage(AIConstants.ROLE_USER, chatDTO.getMessage()));
            history.add(createMessage(AIConstants.ROLE_ASSISTANT, aiResponse));

            // 限制历史长度
            if (history.size() > AIConstants.MAX_HISTORY_LENGTH) {
                history = history.subList(history.size() - AIConstants.MAX_HISTORY_LENGTH, history.size());
                sessionHistoryMap.put(sessionId, history);
            }

            // 5. 构建响应
            AIChatVO response = new AIChatVO();
            response.setContent(aiResponse);
            response.setSessionId(sessionId);

            // 6. 从AI回复中提取药品名称并推荐
            List<AIChatVO.RecommendedDrug> recommendedDrugs = recommendDrugsFromAIResponse(aiResponse);
            response.setDrugs(recommendedDrugs);
            response.setShowActions(!recommendedDrugs.isEmpty());

            return response;

        } catch (Exception e) {
            log.error("AI对话失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "AI助手暂时无法响应，请稍后再试");
        }
    }

    @Override
    public void clearSession(String sessionId) {
        if (sessionId != null) {
            sessionHistoryMap.remove(sessionId);
        }
    }

    /**
     * 构建消息列表
     */
    private List<Map<String, String>> buildMessages(List<Map<String, String>> history, String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 添加系统提示词
        messages.add(createMessage(AIConstants.ROLE_SYSTEM, aiConfig.getSystemPrompt()));

        // 添加历史对话
        messages.addAll(history);

        // 添加当前用户消息
        messages.add(createMessage(AIConstants.ROLE_USER, userMessage));

        return messages;
    }

    /**
     * 创建消息对象
     */
    private Map<String, String> createMessage(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    /**
     * 调用百川API
     */
    private String callBaichuanAPI(List<Map<String, String>> messages) {
        try {
            // 构建请求体
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", aiConfig.getModel());
            requestBody.put("stream", false);
            requestBody.put("temperature", aiConfig.getTemperature());
            requestBody.put("top_p", aiConfig.getTopP());
            requestBody.put("top_k", aiConfig.getTopK());
            requestBody.put("max_tokens", aiConfig.getMaxTokens());

            // 添加消息数组
            ArrayNode messagesArray = requestBody.putArray("messages");
            for (Map<String, String> message : messages) {
                ObjectNode messageNode = messagesArray.addObject();
                messageNode.put("role", message.get("role"));
                messageNode.put("content", message.get("content"));
            }

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(AIConstants.HEADER_AUTHORIZATION, AIConstants.BEARER_PREFIX + aiConfig.getApiKey());

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                    aiConfig.getApiUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                JsonNode choices = responseJson.path("choices");

                if (choices.isArray() && choices.size() > 0) {
                    JsonNode messageNode = choices.get(0).path("message");
                    String content = messageNode.path("content").asText();

                    // 记录Token使用情况
                    JsonNode usage = responseJson.path("usage");
                    log.info("AI对话完成 - Prompt Tokens: {}, Completion Tokens: {}, Total Tokens: {}",
                            usage.path("prompt_tokens").asInt(),
                            usage.path("completion_tokens").asInt(),
                            usage.path("total_tokens").asInt());

                    return content;
                }
            }

            throw new BusinessException(ResultCode.SYSTEM_ERROR, "AI服务响应异常");

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用百川API失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "AI服务调用失败");
        }
    }

    /**
     * 从AI回复中提取药品名称并推荐
     */
    private List<AIChatVO.RecommendedDrug> recommendDrugsFromAIResponse(String aiResponse) {
        try {
            // 从AI回复中提取药品名称
            Set<String> drugNames = extractDrugNames(aiResponse);
            
            if (drugNames.isEmpty()) {
                return Collections.emptyList();
            }
            
            // 根据提取的药品名称查询数据库
            List<Drug> drugs = searchDrugsByNames(new ArrayList<>(drugNames));
            
            if (drugs == null || drugs.isEmpty()) {
                return Collections.emptyList();
            }
            
            List<AIChatVO.RecommendedDrug> recommendedDrugs = new ArrayList<>();
            for (Drug drug : drugs) {
                AIChatVO.RecommendedDrug recommendedDrug = new AIChatVO.RecommendedDrug();
                recommendedDrug.setId(drug.getId().toString());
                recommendedDrug.setName(drug.getProductName());
                recommendedDrug.setImage(drug.getMainImage());
                recommendedDrug.setPrice(drug.getPrice() != null ? drug.getPrice().doubleValue() : 0.0);
                recommendedDrug.setSpec(drug.getSpecification());
                recommendedDrug.setManufacturer(drug.getManufacturer());
                recommendedDrug.setIsRx(drug.getIsRx() != null && drug.getIsRx());
                recommendedDrugs.add(recommendedDrug);
            }
            
            return recommendedDrugs;
            
        } catch (Exception e) {
            log.error("从AI回复推荐药品失败", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 从AI回复中提取药品名称
     */
    private Set<String> extractDrugNames(String aiResponse) {
        Set<String> drugNames = new LinkedHashSet<>();
        
        // 常见药品关键词模式
        String[] patterns = {
            "可选用(.*?)(?:缓解|治疗|帮助|止咳|祛痰|退烧|止痛|消炎|抗过敏)",
            "可使用(.*?)(?:缓解|治疗|帮助|止咳|祛痰|退烧|止痛|消炎|抗过敏)",
            "推荐使用(.*?)(?:缓解|治疗|帮助|止咳|祛痰|退烧|止痛|消炎|抗过敏)",
            "建议服用(.*?)(?:缓解|治疗|帮助|止咳|祛痰|退烧|止痛|消炎|抗过敏)",
            "可选择(.*?)(?:缓解|治疗|帮助|止咳|祛痰|退烧|止痛|消炎|抗过敏)",
            "服用(.*?)(?:胶囊|膏|颗粒|片|口服液|糖浆|散|丸|栓|贴|喷雾)",
            "选用(.*?)(?:胶囊|膏|颗粒|片|口服液|糖浆|散|丸|栓|贴|喷雾)"
        };
        
        for (String pattern : patterns) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(aiResponse);
            while (m.find()) {
                String drugName = m.group(1).trim();
                // 清理药品名称
                drugName = drugName.replaceAll("[，。、；：,.;:]", "").trim();
                if (drugName.length() > 1 && drugName.length() < 30) {
                    drugNames.add(drugName);
                }
            }
        }
        
        // 如果没有提取到，尝试从常见药品名称中匹配
        if (drugNames.isEmpty()) {
            String[] commonDrugs = {
                "布洛芬", "对乙酰氨基酚", "阿莫西林", "头孢", "氯雷他定",
                "蒙脱石散", "京都念慈菴", "川贝枇杷膏", "咳特灵", "感冒灵",
                "连花清瘟", "板蓝根", "维生素C", "奥美拉唑", "多潘立酮",
                "双黄连", "小柴胡", "藿香正气", "健胃消食片", "复方甘草片"
            };
            
            for (String drug : commonDrugs) {
                if (aiResponse.contains(drug)) {
                    drugNames.add(drug);
                }
            }
        }
        
        log.info("从AI回复中提取到药品名称: {}", drugNames);
        return drugNames;
    }
    
    /**
     * 根据药品名称列表搜索药品
     */
    private List<Drug> searchDrugsByNames(List<String> drugNames) {
        try {
            List<Drug> allDrugs = new ArrayList<>();
            
            for (String drugName : drugNames) {
                com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Drug> queryWrapper =
                        new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
                
                queryWrapper.like(Drug::getProductName, drugName)
                        .or()
                        .like(Drug::getDescription, drugName);
                
                queryWrapper.eq(Drug::getStatus, 1);
                queryWrapper.orderByDesc(Drug::getSales);
                queryWrapper.last("LIMIT 2");
                
                List<Drug> drugs = drugMapper.selectList(queryWrapper);
                if (drugs != null && !drugs.isEmpty()) {
                    allDrugs.addAll(drugs);
                }
            }
            
            // 去重（按药品ID）
            return allDrugs.stream()
                    .collect(java.util.stream.Collectors.toMap(
                            Drug::getId,
                            drug -> drug,
                            (existing, replacement) -> existing
                    ))
                    .values()
                    .stream()
                    .limit(5)
                    .collect(java.util.stream.Collectors.toList());
            
        } catch (Exception e) {
            log.error("根据药品名称搜索失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 推荐相关药品（保留原有方法作为备用）
     */
    private List<AIChatVO.RecommendedDrug> recommendDrugs(String userMessage) {
        try {
            // 使用MyBatis Plus查询相关药品
            List<Drug> drugs = searchRelatedDrugs(userMessage, 3);

            if (drugs == null || drugs.isEmpty()) {
                return Collections.emptyList();
            }

            List<AIChatVO.RecommendedDrug> recommendedDrugs = new ArrayList<>();
            for (Drug drug : drugs) {
                AIChatVO.RecommendedDrug recommendedDrug = new AIChatVO.RecommendedDrug();
                recommendedDrug.setId(drug.getId().toString());
                recommendedDrug.setName(drug.getProductName());
                recommendedDrug.setImage(drug.getMainImage());
                recommendedDrug.setPrice(drug.getPrice() != null ? drug.getPrice().doubleValue() : 0.0);
                recommendedDrug.setSpec(drug.getSpecification());
                recommendedDrug.setManufacturer(drug.getManufacturer());
                recommendedDrug.setIsRx(drug.getIsRx() != null && drug.getIsRx());
                recommendedDrugs.add(recommendedDrug);
            }

            return recommendedDrugs;

        } catch (Exception e) {
            log.error("推荐药品失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 搜索相关药品
     */
    private List<Drug> searchRelatedDrugs(String keyword, int limit) {
        try {
            // 使用MyBatis Plus的LambdaQueryWrapper进行模糊查询
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Drug> queryWrapper =
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();

            // 构建查询条件：名称、分类、疾病包含关键词
            queryWrapper.and(wrapper -> wrapper
                    .like(Drug::getProductName, keyword)
                    .or()
                    .like(Drug::getDescription, keyword)
            );

            // 只查询上架的药品
            queryWrapper.eq(Drug::getStatus, 1);

            // 按销量排序，取前N个
            queryWrapper.orderByDesc(Drug::getSales);
            queryWrapper.last("LIMIT " + limit);

            return drugMapper.selectList(queryWrapper);

        } catch (Exception e) {
            log.error("搜索药品失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    public FileUploadVO uploadFile(MultipartFile file, String purpose) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                throw new BusinessException(ResultCode.VALIDATE_FAILED, "文件不能为空");
            }

            // 构建multipart请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set(AIConstants.HEADER_AUTHORIZATION, AIConstants.BEARER_PREFIX + aiConfig.getApiKey());

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
            body.add("purpose", purpose);

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

            // 发送请求到百川API
            String uploadUrl = aiConfig.getApiUrl().replace("/chat/completions", "/files");
            ResponseEntity<String> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                
                FileUploadVO fileUploadVO = new FileUploadVO();
                fileUploadVO.setId(responseJson.path("id").asText());
                fileUploadVO.setFilename(responseJson.path("filename").asText());
                fileUploadVO.setBytes(responseJson.path("bytes").asInt());
                fileUploadVO.setCreatedAt(responseJson.path("created_at").asLong());
                fileUploadVO.setPurpose(responseJson.path("purpose").asText());
                
                return fileUploadVO;
            }

            throw new BusinessException(ResultCode.SYSTEM_ERROR, "文件上传失败");

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("上传文件失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "文件上传失败");
        }
    }

    @Override
    public FileUploadVO getFileContent(String fileId) {
        try {
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set(AIConstants.HEADER_AUTHORIZATION, AIConstants.BEARER_PREFIX + aiConfig.getApiKey());

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 发送请求获取解析内容
            String contentUrl = aiConfig.getApiUrl().replace("/chat/completions", "/files/" + fileId + "/parsed-content");
            ResponseEntity<String> response = restTemplate.exchange(
                    contentUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                
                FileUploadVO fileUploadVO = new FileUploadVO();
                fileUploadVO.setId(fileId);
                fileUploadVO.setStatus(responseJson.path("status").asText());
                fileUploadVO.setContent(responseJson.path("content").asText());
                
                // 识别内容类型
                String content = fileUploadVO.getContent();
                if (content != null) {
                    if (content.contains("处方") || content.contains("用法") || content.contains("用量")) {
                        fileUploadVO.setRecognizeType("prescription");
                    } else if (content.contains("药品") || content.contains("规格") || content.contains("批准文号")) {
                        fileUploadVO.setRecognizeType("drug");
                    } else {
                        fileUploadVO.setRecognizeType("other");
                    }
                }
                
                return fileUploadVO;
            }

            throw new BusinessException(ResultCode.SYSTEM_ERROR, "获取文件内容失败");

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取文件内容失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "获取文件内容失败");
        }
    }

    @Override
    public SymptomTestVO symptomTest(SymptomTestDTO symptomTestDTO) {
        try {
            // 构建症状描述
            StringBuilder symptomDesc = new StringBuilder();
            symptomDesc.append("症状自测：\n");
            symptomDesc.append("症状：").append(String.join("、", symptomTestDTO.getSymptoms())).append("\n");
            
            if (symptomTestDTO.getBodyPart() != null) {
                symptomDesc.append("部位：").append(symptomTestDTO.getBodyPart()).append("\n");
            }
            if (symptomTestDTO.getDuration() != null) {
                symptomDesc.append("持续时间：").append(symptomTestDTO.getDuration()).append("\n");
            }
            if (symptomTestDTO.getSeverity() != null) {
                symptomDesc.append("严重程度：").append(symptomTestDTO.getSeverity()).append("\n");
            }
            if (symptomTestDTO.getDescription() != null) {
                symptomDesc.append("其他描述：").append(symptomTestDTO.getDescription()).append("\n");
            }

            symptomDesc.append("\n请根据以上症状进行分析，给出可能的情况、建议和注意事项。");

            // 调用AI进行分析
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(createMessage(AIConstants.ROLE_SYSTEM, 
                "你是一位专业的医疗助手，请根据用户描述的症状进行初步分析。" +
                "请给出可能的情况、建议和注意事项。" +
                "注意：你的分析仅供参考，不能替代医生的诊断。" +
                "如果症状严重，请建议用户及时就医。"));
            messages.add(createMessage(AIConstants.ROLE_USER, symptomDesc.toString()));

            String aiResponse = callBaichuanAPI(messages);

            // 构建响应
            SymptomTestVO result = new SymptomTestVO();
            result.setAdvice(aiResponse);
            
            // 推荐药品
            String symptomKeywords = String.join(" ", symptomTestDTO.getSymptoms());
            List<AIChatVO.RecommendedDrug> recommendedDrugs = recommendDrugs(symptomKeywords);
            result.setRecommendedDrugs(recommendedDrugs);
            
            // 设置注意事项
            List<String> precautions = new ArrayList<>();
            precautions.add("以上分析仅供参考，不能替代医生诊断");
            precautions.add("如症状持续或加重，请及时就医");
            precautions.add("用药前请仔细阅读说明书或咨询医生");
            result.setPrecautions(precautions);
            
            // 设置下一步建议
            List<String> nextSteps = new ArrayList<>();
            nextSteps.add("观察症状变化");
            if (!recommendedDrugs.isEmpty()) {
                nextSteps.add("考虑使用推荐药品缓解症状");
            }
            nextSteps.add("如症状严重，建议咨询医生");
            result.setNextSteps(nextSteps);
            
            // 判断是否需要就医
            boolean needDoctor = symptomTestDTO.getSeverity() != null && 
                (symptomTestDTO.getSeverity().equals("重度") || symptomTestDTO.getSeverity().equals("严重"));
            result.setNeedDoctor(needDoctor);
            
            // 设置紧急程度
            if (needDoctor) {
                result.setUrgencyLevel("高");
            } else if (symptomTestDTO.getSeverity() != null && symptomTestDTO.getSeverity().equals("中度")) {
                result.setUrgencyLevel("中");
            } else {
                result.setUrgencyLevel("低");
            }
            
            return result;

        } catch (Exception e) {
            log.error("症状自测失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "症状自测失败");
        }
    }
}
