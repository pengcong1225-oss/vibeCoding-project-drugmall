package com.drugmall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drugmall.admin.common.Result;
import com.drugmall.admin.entity.SystemConfig;
import com.drugmall.admin.mapper.SystemConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/system-config")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigMapper systemConfigMapper;

    @GetMapping
    public Result<Map<String, Object>> getConfigList() {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        List<SystemConfig> configs = systemConfigMapper.selectList(wrapper);
        
        Map<String, Object> configMap = configs.stream()
            .collect(Collectors.toMap(SystemConfig::getConfigKey, SystemConfig::getConfigValue));
        
        return Result.success(Map.of(
            "list", configs,
            "configMap", configMap
        ));
    }

    @PutMapping
    public Result<Void> updateConfig(@RequestBody Map<String, String> body) {
        String key = body.get("key");
        String value = body.get("value");
        
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, key);
        SystemConfig config = systemConfigMapper.selectOne(wrapper);
        
        if (config != null) {
            config.setConfigValue(value);
            systemConfigMapper.updateById(config);
        }
        
        return Result.success();
    }

    @GetMapping("/basic")
    public Result<Map<String, Object>> getBasicConfig() {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SystemConfig::getConfigKey, "site_name", "site_logo", "site_description", "contact_phone", "contact_email");
        List<SystemConfig> configs = systemConfigMapper.selectList(wrapper);
        
        Map<String, String> configMap = configs.stream()
            .collect(Collectors.toMap(SystemConfig::getConfigKey, SystemConfig::getConfigValue));
        
        return Result.success(Map.of(
            "siteName", configMap.getOrDefault("site_name", "DrugMall"),
            "siteLogo", configMap.getOrDefault("site_logo", ""),
            "siteDescription", configMap.getOrDefault("site_description", ""),
            "contactPhone", configMap.getOrDefault("contact_phone", ""),
            "contactEmail", configMap.getOrDefault("contact_email", "")
        ));
    }

    @GetMapping("/payment")
    public Result<Map<String, Object>> getPaymentConfig() {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SystemConfig::getConfigKey, "payment_alipay", "payment_wechat", "payment_balance");
        List<SystemConfig> configs = systemConfigMapper.selectList(wrapper);
        
        Map<String, String> configMap = configs.stream()
            .collect(Collectors.toMap(SystemConfig::getConfigKey, SystemConfig::getConfigValue));
        
        return Result.success(Map.of(
            "alipayEnabled", "1".equals(configMap.getOrDefault("payment_alipay", "0")),
            "wechatEnabled", "1".equals(configMap.getOrDefault("payment_wechat", "0")),
            "balanceEnabled", "1".equals(configMap.getOrDefault("payment_balance", "0"))
        ));
    }

    @GetMapping("/order")
    public Result<Map<String, Object>> getOrderConfig() {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SystemConfig::getConfigKey, "min_order_amount", "free_delivery_amount", "auto_confirm_hours", "refund_timeout_hours");
        List<SystemConfig> configs = systemConfigMapper.selectList(wrapper);
        
        Map<String, String> configMap = configs.stream()
            .collect(Collectors.toMap(SystemConfig::getConfigKey, SystemConfig::getConfigValue));
        
        return Result.success(Map.of(
            "minOrderAmount", configMap.getOrDefault("min_order_amount", "29.00"),
            "freeDeliveryAmount", configMap.getOrDefault("free_delivery_amount", "99.00"),
            "autoConfirmHours", configMap.getOrDefault("auto_confirm_hours", "24"),
            "refundTimeoutHours", configMap.getOrDefault("refund_timeout_hours", "48")
        ));
    }
}
