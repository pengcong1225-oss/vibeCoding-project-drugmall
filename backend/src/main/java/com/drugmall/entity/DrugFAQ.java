package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 药品FAQ实体
 */
@Data
@TableName("dm_drug_faq")
public class DrugFAQ implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String drugId;

    private String question;

    private String answer;

    private Integer sort;
}
