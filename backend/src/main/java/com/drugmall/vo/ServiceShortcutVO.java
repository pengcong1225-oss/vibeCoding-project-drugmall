package com.drugmall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceShortcutVO {
    private String name;
    private String subtitle;
    private String image;
    private String doctorAvatar;
    private String linkUrl;
    private Integer sortOrder;
}
