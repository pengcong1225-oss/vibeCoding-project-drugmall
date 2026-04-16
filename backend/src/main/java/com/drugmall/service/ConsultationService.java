package com.drugmall.service;

import com.drugmall.dto.SendMessageDTO;
import com.drugmall.vo.ConsultationDetailVO;
import com.drugmall.vo.ConsultationVO;
import com.drugmall.vo.MessageVO;

import java.util.List;

/**
 * 问诊服务接口
 */
public interface ConsultationService {

    /**
     * 获取问诊列表
     */
    List<ConsultationVO> listConsultations(String doctorId, String status);

    /**
     * 获取问诊详情
     */
    ConsultationDetailVO getConsultationDetail(String doctorId, String consultationId);

    /**
     * 开始问诊
     */
    boolean startConsultation(String doctorId, String consultationId);

    /**
     * 结束问诊
     */
    boolean endConsultation(String doctorId, String consultationId);

    /**
     * 获取消息列表
     */
    List<MessageVO> getMessages(String doctorId, String consultationId);

    /**
     * 发送消息
     */
    MessageVO sendMessage(String doctorId, String consultationId, SendMessageDTO sendMessageDTO);
}
