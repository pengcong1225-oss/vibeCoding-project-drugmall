package com.drugmall.service.impl;

import com.drugmall.common.BusinessException;
import com.drugmall.common.ResultCode;
import com.drugmall.config.MockDataService;
import com.drugmall.dto.SendMessageDTO;
import com.drugmall.service.ConsultationService;
import com.drugmall.vo.ConsultationDetailVO;
import com.drugmall.vo.ConsultationVO;
import com.drugmall.vo.MessageVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 问诊服务实现
 */
@Slf4j
@Service
public class ConsultationServiceImpl implements ConsultationService {

    @Autowired
    private MockDataService mockDataService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public List<ConsultationVO> listConsultations(String doctorId, String status) {
        JsonNode doctorData = mockDataService.getDoctorData();
        List<ConsultationVO> result = new ArrayList<>();

        if (doctorData == null || !doctorData.has("consultations")) {
            return result;
        }

        for (JsonNode c : doctorData.get("consultations")) {
            String consultationStatus = c.get("status").asText();
            if (status != null && !status.isEmpty() && !status.equals("all") && !status.equals(consultationStatus)) {
                continue;
            }
            result.add(convertToConsultationVO(c));
        }

        return result;
    }

    @Override
    public ConsultationDetailVO getConsultationDetail(String doctorId, String consultationId) {
        JsonNode doctorData = mockDataService.getDoctorData();
        if (doctorData == null || !doctorData.has("consultations")) {
            throw new BusinessException(ResultCode.CONSULTATION_NOT_FOUND);
        }

        for (JsonNode c : doctorData.get("consultations")) {
            if (c.get("id").asText().equals(consultationId)) {
                ConsultationDetailVO detail = convertToDetailVO(c);

                // 加载消息
                if (doctorData.has("messages") && doctorData.get("messages").has(consultationId)) {
                    List<MessageVO> messages = new ArrayList<>();
                    for (JsonNode m : doctorData.get("messages").get(consultationId)) {
                        messages.add(convertToMessageVO(m, consultationId));
                    }
                    detail.setMessages(messages);
                } else {
                    detail.setMessages(new ArrayList<>());
                }

                return detail;
            }
        }

        throw new BusinessException(ResultCode.CONSULTATION_NOT_FOUND);
    }

    @Override
    public boolean startConsultation(String doctorId, String consultationId) {
        JsonNode doctorData = mockDataService.getDoctorData();
        if (doctorData == null || !doctorData.has("consultations")) {
            throw new BusinessException(ResultCode.CONSULTATION_NOT_FOUND);
        }

        for (JsonNode c : doctorData.get("consultations")) {
            if (c.get("id").asText().equals(consultationId)) {
                String status = c.get("status").asText();
                if ("completed".equals(status) || "closed".equals(status)) {
                    throw new BusinessException(ResultCode.CONSULTATION_ALREADY_CLOSED);
                }
                // 更新状态为 processing (in-memory)
                ((com.fasterxml.jackson.databind.node.ObjectNode) c).put("status", "processing");
                log.info("开始问诊: {}", consultationId);
                return true;
            }
        }

        throw new BusinessException(ResultCode.CONSULTATION_NOT_FOUND);
    }

    @Override
    public boolean endConsultation(String doctorId, String consultationId) {
        JsonNode doctorData = mockDataService.getDoctorData();
        if (doctorData == null || !doctorData.has("consultations")) {
            throw new BusinessException(ResultCode.CONSULTATION_NOT_FOUND);
        }

        for (JsonNode c : doctorData.get("consultations")) {
            if (c.get("id").asText().equals(consultationId)) {
                ((com.fasterxml.jackson.databind.node.ObjectNode) c).put("status", "completed");
                log.info("结束问诊: {}", consultationId);
                return true;
            }
        }

        throw new BusinessException(ResultCode.CONSULTATION_NOT_FOUND);
    }

    @Override
    public List<MessageVO> getMessages(String doctorId, String consultationId) {
        JsonNode doctorData = mockDataService.getDoctorData();
        List<MessageVO> messages = new ArrayList<>();

        if (doctorData != null && doctorData.has("messages") && doctorData.get("messages").has(consultationId)) {
            for (JsonNode m : doctorData.get("messages").get(consultationId)) {
                messages.add(convertToMessageVO(m, consultationId));
            }
        }

        return messages;
    }

    @Override
    public MessageVO sendMessage(String doctorId, String consultationId, SendMessageDTO sendMessageDTO) {
        JsonNode doctorData = mockDataService.getDoctorData();

        // 创建新消息
        ObjectNode newMessage = objectMapper.createObjectNode();
        String messageId = "M" + System.currentTimeMillis();
        newMessage.put("id", messageId);
        newMessage.put("consultationId", consultationId);
        newMessage.put("sender", "doctor");
        newMessage.put("type", sendMessageDTO.getType());
        newMessage.put("content", sendMessageDTO.getContent());
        newMessage.put("time", LocalDateTime.now().format(TIME_FORMATTER));
        newMessage.put("status", "sent");

        // 添加到 messages 中
        if (doctorData != null && doctorData.has("messages")) {
            JsonNode messagesNode = doctorData.get("messages");
            if (messagesNode.has(consultationId)) {
                ((ArrayNode) messagesNode.get(consultationId)).add(newMessage);
            } else {
                ArrayNode newArray = objectMapper.createArrayNode();
                newArray.add(newMessage);
                ((ObjectNode) messagesNode).set(consultationId, newArray);
            }
        }

        return convertToMessageVO(newMessage, consultationId);
    }

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    private ConsultationVO convertToConsultationVO(JsonNode c) {
        ConsultationVO vo = new ConsultationVO();
        vo.setId(c.get("id").asText());
        vo.setPatientId(c.get("patientId").asText());
        vo.setPatientName(c.get("patientName").asText());
        vo.setPatientAge(c.get("patientAge").asInt());
        vo.setPatientGender(c.get("patientGender").asText());
        vo.setPatientAvatar(c.get("patientAvatar").asText());
        vo.setType(c.get("type").asText());
        vo.setStatus(c.get("status").asText());
        vo.setSymptom(c.get("symptom").asText());
        vo.setWaitTime(c.get("waitTime").asText());
        vo.setRemainingTime(c.get("remainingTime").asText());
        vo.setIsUrgent(c.get("isUrgent").asBoolean());
        vo.setIsRx(c.get("isRx").asBoolean());
        vo.setCreateTime(c.get("createTime").asText());
        return vo;
    }

    private ConsultationDetailVO convertToDetailVO(JsonNode c) {
        ConsultationDetailVO vo = new ConsultationDetailVO();
        vo.setId(c.get("id").asText());
        vo.setPatientId(c.get("patientId").asText());
        vo.setPatientName(c.get("patientName").asText());
        vo.setPatientAge(c.get("patientAge").asInt());
        vo.setPatientGender(c.get("patientGender").asText());
        vo.setPatientAvatar(c.get("patientAvatar").asText());
        vo.setType(c.get("type").asText());
        vo.setStatus(c.get("status").asText());
        vo.setSymptom(c.get("symptom").asText());
        vo.setWaitTime(c.get("waitTime").asText());
        vo.setRemainingTime(c.get("remainingTime").asText());
        vo.setIsUrgent(c.get("isUrgent").asBoolean());
        vo.setIsRx(c.get("isRx").asBoolean());
        vo.setCreateTime(c.get("createTime").asText());
        return vo;
    }

    private MessageVO convertToMessageVO(JsonNode m, String consultationId) {
        MessageVO vo = new MessageVO();
        vo.setId(m.get("id").asText());
        vo.setConsultationId(consultationId);
        vo.setSender(m.get("sender").asText());
        vo.setType(m.get("type").asText());
        vo.setContent(m.get("content").asText());
        vo.setTime(m.get("time").asText());
        if (m.has("status") && !m.get("status").isNull()) {
            vo.setStatus(m.get("status").asText());
        }
        return vo;
    }
}
