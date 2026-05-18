package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("dm_dict_data")
public class DictData implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("type_code")
    private String typeCode;
    
    @TableField("label")
    private String label;
    
    @TableField("value")
    private String value;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField("is_default")
    private Integer isDefault;
    
    private Integer status;
    
    private String remark;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
