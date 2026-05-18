package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dm_product")
public class Drug implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    @TableField("product_code")
    private String productCode;
    
    @TableField("product_name")
    private String productName;
    
    @TableField("generic_name")
    private String genericName;
    
    @TableField("category_id")
    private Long categoryId;
    
    @TableField("brand_id")
    private Long brandId;
    
    @TableField("main_image")
    private String mainImage;
    
    private String images;
    
    private String specification;
    
    private String unit;
    
    private String manufacturer;
    
    @TableField("brand")
    private String brand;
    
    @TableField("approval_number")
    private String approvalNumber;
    
    @TableField("bar_code")
    private String barCode;
    
    @TableField("medical_insurance_code")
    private String medicalInsuranceCode;
    
    @TableField("is_long_prescription")
    private Boolean isLongPrescription;
    
    @TableField("insurance_category")
    private String insuranceCategory;
    
    private BigDecimal price;
    
    @TableField("original_price")
    private BigDecimal originalPrice;
    
    private Integer stock;
    
    @TableField("warning_stock")
    private Integer warningStock;
    
    private Integer sales;
    
    @TableField("is_rx")
    private Boolean isRx;
    
    @TableField("is_national_essential")
    private Boolean isNationalEssential;
    
    @TableField("`usage`")
    private String usage;
    
    private String disease;
    
    private String contraindications;
    
    private String precautions;
    
    @TableField("adverse_reactions")
    private String adverseReactions;
    
    private String storage;
    
    private String validity;
    
    private String ingredients;
    
    private String appearance;
    
    @TableField("drug_interactions")
    private String drugInteractions;
    
    private String description;
    
    private Integer status;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
    
    @TableLogic(value = "0", delval = "1")
    @TableField("is_deleted")
    private Integer isDeleted;
}
