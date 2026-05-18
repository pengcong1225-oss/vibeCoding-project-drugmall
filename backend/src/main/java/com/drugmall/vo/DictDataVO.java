package com.drugmall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDataVO {
    private String label;
    private String value;
    private Integer sortOrder;
    private Integer isDefault;
}
