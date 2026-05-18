package com.drugmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drugmall.common.BusinessException;
import com.drugmall.common.ResultCode;
import com.drugmall.dto.SendMessageDTO;
import com.drugmall.entity.Consultation;
import com.drugmall.entity.ConsultationMessage;
import com.drugmall.entity.Patient;
import com.drugmall.mapper.ConsultationMapper;
import com.drugmall.mapper.ConsultationMessageMapper;
import com.drugmall.mapper.PatientMapper;
import com.drugmall.service.ConsultationService;
import com.drugmall.vo.ConsultationDetailVO;
import com.drugmall.vo.ConsultationVO;
import com.drugmall.vo.MessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 问诊服务实现
 */
@Slf4j
@Service
public class ConsultationServiceImpl implements ConsultationService {

    @Autowired
    private ConsultationMapper consultationMapper;

    @Autowired
    private ConsultationMessageMapper consultationMessageMapper;
    
    @Autowired
    private PatientMapper patientMapper;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public List<ConsultationVO> listConsultations(String doctorId, String status) {
        LambdaQueryWrapper<Consultation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Consultation::getDoctorId, doctorId);
        
        // 根据状态筛选
        if (status != null && !status.isEmpty() && !status.equals("all")) {
            wrapper.eq(Consultation::getStatus, status);
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(Consultation::getCreateTime);
        
        List<Consultation> consultations = consultationMapper.selectList(wrapper);
        return consultations.stream()
                .map(this::convertToConsultationVO)
                .collect(Collectors.toList());
    }

    @Override
    public ConsultationDetailVO getConsultationDetail(String doctorId, String consultationId) {
        Consultation consultation = consultationMapper.selectById(consultationId);
        if (consultation == null) {
            throw new BusinessException(ResultCode.CONSULTATION_NOT_FOUND);
        }
        
        ConsultationDetailVO detail = convertToDetailVO(consultation);
        
        // 加载消息
        LambdaQueryWrapper<ConsultationMessage> msgWrapper = new LambdaQueryWrapper<>();
        msgWrapper.eq(ConsultationMessage::getConsultationId, consultationId)
                  .orderByAsc(ConsultationMessage::getCreateTime);
        List<ConsultationMessage> messages = consultationMessageMapper.selectList(msgWrapper);
        
        detail.setMessages(messages.stream()
                .map(m -> convertToMessageVO(m, consultationId))
                .collect(Collectors.toList()));
        
        return detail;
    }

    @Override
    public boolean startConsultation(String doctorId, String consultationId) {
        Consultation consultation = consultationMapper.selectById(consultationId);
        if (consultation == null) {
            throw new BusinessException(ResultCode.CONSULTATION_NOT_FOUND);
        }
        
        String status = consultation.getStatus();
        if ("completed".equals(status) || "closed".equals(status)) {
            throw new BusinessException(ResultCode.CONSULTATION_ALREADY_CLOSED);
        }
        
        // 更新状态为 processing
        consultation.setStatus("processing");
        consultation.setStartTime(LocalDateTime.now());
        consultationMapper.updateById(consultation);
        
        log.info("开始问诊: {}", consultationId);
        return true;
    }

    @Override
    public boolean endConsultation(String doctorId, String consultationId) {
        Consultation consultation = consultationMapper.selectById(consultationId);
        if (consultation == null) {
            throw new BusinessException(ResultCode.CONSULTATION_NOT_FOUND);
        }
        
        consultation.setStatus("completed");
        consultation.setEndTime(LocalDateTime.now());
        consultationMapper.updateById(consultation);
        
        log.info("结束问诊: {}", consultationId);
        return true;
    }

    @Override
    public List<MessageVO> getMessages(String doctorId, String consultationId) {
        LambdaQueryWrapper<ConsultationMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConsultationMessage::getConsultationId, consultationId)
               .orderByAsc(ConsultationMessage::getCreateTime);
        
        List<ConsultationMessage> messages = consultationMessageMapper.selectList(wrapper);
        return messages.stream()
                .map(m -> convertToMessageVO(m, consultationId))
                .collect(Collectors.toList());
    }

    @Override
    public MessageVO sendMessage(String senderId, String consultationId, SendMessageDTO sendMessageDTO) {
        ConsultationMessage message = new ConsultationMessage();
        message.setConsultationId(consultationId);
        message.setSenderType(sendMessageDTO.getSenderType() != null ? sendMessageDTO.getSenderType() : "doctor");
        message.setSenderId(senderId);
        message.setType(sendMessageDTO.getType());
        message.setContent(sendMessageDTO.getContent());
        message.setCreateTime(LocalDateTime.now());

        consultationMessageMapper.insert(message);

        log.info("发送消息: consultationId={}, sender={}, type={}", consultationId, message.getSenderType(), sendMessageDTO.getType());
        return convertToMessageVO(message, consultationId);
    }

    private ConsultationVO convertToConsultationVO(Consultation c) {
        if (c == null) {
            return null;
        }
        
        ConsultationVO vo = new ConsultationVO();
        vo.setId(c.getId());
        vo.setPatientId(c.getPatientId() != null ? String.valueOf(c.getPatientId()) : "");
        
        // 从患者表查询真实信息
        if (c.getPatientId() != null) {
            try {
                Patient patient = patientMapper.selectById(c.getPatientId());
                if (patient != null) {
                    vo.setPatientName(patient.getName() != null ? patient.getName() : "患者" + c.getPatientId());
                    vo.setPatientAge(patient.getBirthday() != null ? 
                        java.time.Period.between(patient.getBirthday(), java.time.LocalDate.now()).getYears() : 0);
                    vo.setPatientGender(patient.getGender() != null ? 
                        (patient.getGender() == 1 ? "男" : "女") : "未知");
                } else {
                    vo.setPatientName("患者" + c.getPatientId());
                    vo.setPatientAge(0);
                    vo.setPatientGender("未知");
                }
            } catch (Exception e) {
                log.warn("查询患者信息失败: patientId={}", c.getPatientId(), e);
                vo.setPatientName("患者" + c.getPatientId());
                vo.setPatientAge(0);
                vo.setPatientGender("未知");
            }
        } else {
            vo.setPatientName("未知患者");
            vo.setPatientAge(0);
            vo.setPatientGender("未知");
        }
        
        vo.setPatientAvatar(""); // TODO: 从用户表查询头像
        vo.setType(c.getType());
        vo.setStatus(c.getStatus());
        vo.setSymptom(c.getSymptom());
        vo.setWaitTime("");
        vo.setRemainingTime("");
        vo.setIsUrgent(c.getIsUrgent() != null && c.getIsUrgent() == 1);
        vo.setIsRx(c.getIsRx() != null && c.getIsRx() == 1);
        vo.setCreateTime(c.getCreateTime() != null ? c.getCreateTime().toString() : "");
        return vo;
    }

    private ConsultationDetailVO convertToDetailVO(Consultation c) {
        if (c == null) {
            return null;
        }
        
        ConsultationDetailVO vo = new ConsultationDetailVO();
        vo.setId(c.getId());
        vo.setDoctorId(c.getDoctorId());
        vo.setPatientId(c.getPatientId() != null ? String.valueOf(c.getPatientId()) : "");
        
        // 从患者表获取user_id（用于IM通讯）
        if (c.getPatientId() != null) {
            try {
                Patient patient = patientMapper.selectById(c.getPatientId());
                if (patient != null && patient.getUserId() != null) {
                    vo.setPatientUserId(String.valueOf(patient.getUserId()));
                } else {
                    vo.setPatientUserId(vo.getPatientId()); // fallback到patientId
                }
            } catch (Exception e) {
                log.warn("查询患者user_id失败: patientId={}", c.getPatientId(), e);
                vo.setPatientUserId(vo.getPatientId());
            }
        } else {
            vo.setPatientUserId("");
        }
        
        vo.setPatientName("");
        vo.setPatientAge(0);
        vo.setPatientGender("");
        vo.setPatientAvatar("");
        vo.setType(c.getType());
        vo.setStatus(c.getStatus());
        vo.setSymptom(c.getSymptom());
        vo.setWaitTime("");
        vo.setRemainingTime("");
        vo.setIsUrgent(c.getIsUrgent() != null && c.getIsUrgent() == 1);
        vo.setIsRx(c.getIsRx() != null && c.getIsRx() == 1);
        vo.setCreateTime(c.getCreateTime() != null ? c.getCreateTime().toString() : "");
        return vo;
    }

    private MessageVO convertToMessageVO(ConsultationMessage m, String consultationId) {
        if (m == null) {
            return null;
        }
        
        MessageVO vo = new MessageVO();
        vo.setId(String.valueOf(m.getId()));
        vo.setConsultationId(consultationId);
        vo.setSender(m.getSenderType());
        vo.setType(m.getType());
        vo.setContent(m.getContent());
        vo.setTime(m.getCreateTime() != null ? m.getCreateTime().format(TIME_FORMATTER) : "");
        vo.setStatus("sent");
        return vo;
    }

    @Override
    public List<ConsultationVO> listPatientConsultations(String patientId, String status) {
        LambdaQueryWrapper<Consultation> wrapper = new LambdaQueryWrapper<>();
        
        // 根据患者ID查询（支持 userId 和 patientId）
        if (patientId != null && !patientId.isEmpty()) {
            try {
                // 尝试解析为 userId (格式: USER123)
                if (patientId.startsWith("USER")) {
                    Long userId = Long.valueOf(patientId.replace("USER", ""));
                    // 查询该用户及其下属所有就诊人的咨询
                    wrapper.eq(Consultation::getUserId, userId);
                    log.info("查询用户 {} 的所有咨询会话（包括下属就诊人）", userId);
                } else {
                    // 直接是 patientId
                    Long pId = Long.valueOf(patientId);
                    wrapper.eq(Consultation::getPatientId, pId);
                    log.info("查询就诊人 {} 的咨询会话", pId);
                }
            } catch (NumberFormatException e) {
                log.warn("无效的患者ID: {}", patientId);
                return new ArrayList<>();
            }
        }
        
        // 根据状态筛选
        if (status != null && !status.isEmpty() && !status.equals("all")) {
            wrapper.eq(Consultation::getStatus, status);
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(Consultation::getCreateTime);
        
        List<Consultation> consultations = consultationMapper.selectList(wrapper);
        log.info("查询到 {} 条咨询记录", consultations.size());
        
        return consultations.stream()
                .map(this::convertToConsultationVO)
                .collect(Collectors.toList());
    }

    @Override
    public ConsultationVO createConsultation(String patientId, String doctorId, String type, String symptom) {
        Consultation consultation = new Consultation();
        consultation.setId("CONS" + System.currentTimeMillis());

        Long userId;
        try {
            userId = Long.valueOf(patientId.replace("USER", ""));
        } catch (NumberFormatException e) {
            userId = 1L;
        }
        consultation.setUserId(userId);

        // 查找用户的默认就诊人作为 patient_id
        Long patientDetailId = getDefaultPatientId(userId);
        consultation.setPatientId(patientDetailId);
        consultation.setDoctorId(doctorId);
        consultation.setType(type);
        consultation.setStatus("pending");
        consultation.setSymptom(symptom);
        consultation.setIsUrgent(0);
        consultation.setIsRx(0);
        consultation.setCreateTime(LocalDateTime.now());

        consultationMapper.insert(consultation);

        log.info("创建问诊: {}, 用户: {}, 就诊人: {}, 医生: {}", consultation.getId(), userId, patientDetailId, doctorId);

        return convertToConsultationVO(consultation);
    }

    private Long getDefaultPatientId(Long userId) {
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getUserId, userId)
               .eq(Patient::getIsDefault, true)
               .eq(Patient::getIsDeleted, false);
        Patient defaultPatient = patientMapper.selectOne(wrapper);
        if (defaultPatient != null) {
            return defaultPatient.getId();
        }
        // 如果没有默认就诊人，取第一个
        LambdaQueryWrapper<Patient> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.eq(Patient::getUserId, userId)
                .eq(Patient::getIsDeleted, false)
                .orderByAsc(Patient::getCreateTime);
        Patient firstPatient = patientMapper.selectOne(wrapper2);
        return firstPatient != null ? firstPatient.getId() : 1L;
    }

    @Override
    public ConsultationVO createConsultationForPrescription(String patientId, String doctorId,
                                                             String drugId, Long patientDetailId,
                                                             String diseases, String symptoms) {
        Consultation consultation = new Consultation();
        consultation.setId("CONS" + System.currentTimeMillis());
        
        try {
            consultation.setUserId(Long.valueOf(patientId.replace("USER", "")));
        } catch (NumberFormatException e) {
            consultation.setUserId(1L); // 默认值
        }
        
        consultation.setDoctorId(doctorId);
        consultation.setPatientId(patientDetailId);
        consultation.setType("处方购药");
        consultation.setStatus("pending");
        consultation.setIsRx(1);
        consultation.setSymptom("处方药申请 - 疾病: " + diseases + ", 症状: " + (symptoms != null ? symptoms : ""));
        
        // 保存患者申请的药品ID列表(JSON数组格式)
        if (drugId != null && !drugId.isEmpty()) {
            consultation.setRequestedDrugIds("[\"" + drugId + "\"]");
            log.info("保存患者申请的药品ID: {}", drugId);
        }
        
        consultation.setIsUrgent(0);
        consultation.setIsRx(1); // 处方药申请
        consultation.setFee(new java.math.BigDecimal("19.90"));
        consultation.setCreateTime(LocalDateTime.now());
        
        consultationMapper.insert(consultation);
        
        log.info("创建处方药问诊: {}, 患者: {}, 医生: {}, 药品: {}", 
                 consultation.getId(), patientId, doctorId, drugId);
        
        return convertToConsultationVO(consultation);
    }

    @Override
    public void acceptConsultation(String doctorId, String consultationId) {
        Consultation consultation = consultationMapper.selectById(consultationId);
        if (consultation == null) {
            throw new BusinessException(ResultCode.CONSULTATION_NOT_FOUND);
        }
        
        if (!"pending".equals(consultation.getStatus())) {
            throw new BusinessException(ResultCode.CONSULTATION_ALREADY_CLOSED);
        }
        
        // 更新状态为 processing（已接诊）
        consultation.setStatus("processing");
        consultation.setStartTime(LocalDateTime.now());
        consultationMapper.updateById(consultation);
        
        log.info("医生接诊: consultationId={}, doctorId={}", consultationId, doctorId);
    }

    @Override
    public void rejectConsultation(String doctorId, String consultationId, String reason) {
        Consultation consultation = consultationMapper.selectById(consultationId);
        if (consultation == null) {
            throw new BusinessException(ResultCode.CONSULTATION_NOT_FOUND);
        }
        
        if (!"pending".equals(consultation.getStatus())) {
            throw new BusinessException(ResultCode.CONSULTATION_ALREADY_CLOSED);
        }
        
        // 更新状态为 closed（已关闭）
        consultation.setStatus("closed");
        consultation.setEndTime(LocalDateTime.now());
        consultation.setRejectReason(reason);
        consultationMapper.updateById(consultation);
        
        log.info("医生拒绝接诊: consultationId={}, doctorId={}, reason={}", 
                 consultationId, doctorId, reason);
    }

    @Override
    public void cancelConsultation(String patientId, String consultationId) {
        Consultation consultation = consultationMapper.selectById(consultationId);
        if (consultation == null) {
            throw new BusinessException(ResultCode.CONSULTATION_NOT_FOUND);
        }
        
        // 只有待接诊状态的问诊可以取消
        if (!"pending".equals(consultation.getStatus())) {
            throw new BusinessException(ResultCode.CONSULTATION_ALREADY_CLOSED);
        }
        
        // 验证患者ID
        try {
            Long userId = Long.valueOf(patientId.replace("USER", ""));
            if (!userId.equals(consultation.getUserId())) {
                throw new BusinessException(ResultCode.UNAUTHORIZED);
            }
        } catch (NumberFormatException e) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }
        
        // 更新状态为 cancelled（已取消）
        consultation.setStatus("cancelled");
        consultation.setEndTime(LocalDateTime.now());
        consultationMapper.updateById(consultation);
        
        log.info("患者取消问诊: consultationId={}, patientId={}", consultationId, patientId);
    }

    @Override
    public void remindDoctor(String patientId, String consultationId) {
        Consultation consultation = consultationMapper.selectById(consultationId);
        if (consultation == null) {
            throw new BusinessException(ResultCode.CONSULTATION_NOT_FOUND);
        }
        
        // 只有待接诊状态的问诊可以提醒
        if (!"pending".equals(consultation.getStatus())) {
            throw new BusinessException(ResultCode.CONSULTATION_ALREADY_CLOSED);
        }
        
        // 验证患者ID
        try {
            Long userId = Long.valueOf(patientId.replace("USER", ""));
            if (!userId.equals(consultation.getUserId())) {
                throw new BusinessException(ResultCode.UNAUTHORIZED);
            }
        } catch (NumberFormatException e) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }
        
        // TODO: 实际项目中应该发送通知给医生（站内信、推送等）
        // 这里仅记录日志
        log.info("患者提醒医生接诊: consultationId={}, patientId={}, doctorId={}", 
                 consultationId, patientId, consultation.getDoctorId());
    }
}
