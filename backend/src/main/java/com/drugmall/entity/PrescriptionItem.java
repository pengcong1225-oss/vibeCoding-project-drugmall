package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 处方明细实体类
 */
@Data
@TableName("dm_prescription_item")
public class PrescriptionItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String prescriptionId;
    private Long productId;
    private String productName;
    private String specification;
    private Integer quantity;
    private String dosage;
    private String frequency;
    private String duration;
    private BigDecimal price;
    private LocalDateTime createTime;
}
