package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("dm_doctor_review_tag")
public class DoctorReviewTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String doctorId;

    private String tagName;

    private String tagType;

    private Integer tagCount;

    private Integer status;

    private Integer isDeleted;
}
