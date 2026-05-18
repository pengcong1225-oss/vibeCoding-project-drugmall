package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.PrescriptionItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 处方明细Mapper接口
 */
@Mapper
public interface PrescriptionItemMapper extends BaseMapper<PrescriptionItem> {
}
