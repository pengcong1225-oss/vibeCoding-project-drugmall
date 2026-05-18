package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.ConsultationMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问诊消息Mapper接口
 */
@Mapper
public interface ConsultationMessageMapper extends BaseMapper<ConsultationMessage> {
}
