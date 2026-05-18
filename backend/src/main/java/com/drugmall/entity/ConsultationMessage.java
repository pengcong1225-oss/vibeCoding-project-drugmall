package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 问诊消息实体类
 */
@Data
@TableName("dm_consultation_message")
public class ConsultationMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String consultationId;
    private String senderType;
    private String senderId;
    private String type;
    private String content;
    private LocalDateTime createTime;
}
