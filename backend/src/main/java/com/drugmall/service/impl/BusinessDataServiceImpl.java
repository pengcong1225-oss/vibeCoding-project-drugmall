package com.drugmall.service.impl;

import com.alibaba.fastjson2.JSON;
import com.drugmall.service.BusinessDataService;
import com.drugmall.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BusinessDataServiceImpl implements BusinessDataService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<DepartmentVO> getDepartments() {
        String sql = "SELECT code, name, icon, tag, tag_type, sort FROM dm_department WHERE status = 1 AND is_deleted = 0 ORDER BY sort ASC";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return rows.stream().map(row -> {
            String icon = (String) row.get("icon");
            // 根据icon字段构建图片URL
            String iconUrl = null;
            if (icon != null && !icon.isEmpty()) {
                iconUrl = "/images/categories/" + icon + ".png";
            }
            return DepartmentVO.builder()
                    .code((String) row.get("code"))
                    .name((String) row.get("name"))
                    .icon(icon)
                    .iconUrl(iconUrl)
                    .tag((String) row.get("tag"))
                    .tagType((String) row.get("tag_type"))
                    .sortOrder(toInt(row.get("sort")))
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public DepartmentConfigVO getDepartmentConfig(String departmentCode) {
        String sql = "SELECT * FROM dm_department_config WHERE department_code = ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, departmentCode);
        if (rows.isEmpty()) {
            return null;
        }
        Map<String, Object> row = rows.get(0);
        List<String> quickSymptoms = parseJsonArray((String) row.get("quick_symptoms"));
        return DepartmentConfigVO.builder()
                .departmentCode((String) row.get("department_code"))
                .price(toBigDecimal(row.get("price")))
                .originalPrice(toBigDecimal(row.get("original_price")))
                .subsidy(toBigDecimal(row.get("subsidy")))
                .symptoms((String) row.get("symptoms"))
                .responseTime(toInt(row.get("response_time")))
                .answerTime(toInt(row.get("answer_time")))
                .example((String) row.get("example"))
                .quickSymptoms(quickSymptoms)
                .build();
    }

    @Override
    public List<DepartmentTagVO> getDepartmentTags() {
        String sql = "SELECT code, label, sort_order FROM dm_department_tag WHERE status = 1 ORDER BY sort_order ASC";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return rows.stream().map(row -> DepartmentTagVO.builder()
                .code((String) row.get("code"))
                .label((String) row.get("label"))
                .sortOrder(toInt(row.get("sort_order")))
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<DictDataVO> getDictData(String typeCode) {
        String sql = "SELECT dict_label as label, dict_value as value, dict_sort as sort_order, is_default FROM dm_dict_data WHERE dict_type = ? AND status = 1 ORDER BY dict_sort ASC";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, typeCode);
        return rows.stream().map(row -> DictDataVO.builder()
                .label((String) row.get("label"))
                .value((String) row.get("value"))
                .sortOrder(toInt(row.get("sort_order")))
                .isDefault(toInt(row.get("is_default")))
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<PaymentMethodVO> getPaymentMethods() {
        String sql = "SELECT code, name, description, icon, sort_order FROM dm_payment_method WHERE status = 1 ORDER BY sort_order ASC";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return rows.stream().map(row -> PaymentMethodVO.builder()
                .code((String) row.get("code"))
                .name((String) row.get("name"))
                .description((String) row.get("description"))
                .icon((String) row.get("icon"))
                .sortOrder(toInt(row.get("sort_order")))
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<ServiceShortcutVO> getServiceShortcuts() {
        String sql = "SELECT name, subtitle, doctor_avatar, sort_order FROM dm_service_shortcut WHERE status = 1 ORDER BY sort_order ASC";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return rows.stream().map(row -> ServiceShortcutVO.builder()
                .name((String) row.get("name"))
                .subtitle((String) row.get("subtitle"))
                .doctorAvatar((String) row.get("doctor_avatar"))
                .sortOrder(toInt(row.get("sort_order")))
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<ConsultationStepVO> getConsultationSteps() {
        String sql = "SELECT step, name, description, sort_order FROM dm_consultation_step WHERE status = 1 ORDER BY sort_order ASC";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return rows.stream().map(row -> ConsultationStepVO.builder()
                .step(toInt(row.get("step")))
                .name((String) row.get("name"))
                .description((String) row.get("description"))
                .sortOrder(toInt(row.get("sort_order")))
                .build()).collect(Collectors.toList());
    }

    private Integer toInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) return ((Number) value).intValue();
        return Integer.parseInt(value.toString());
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        return new BigDecimal(value.toString());
    }

    private List<String> parseJsonArray(String json) {
        if (json == null || json.isEmpty()) return new ArrayList<>();
        try {
            return JSON.parseArray(json, String.class);
        } catch (Exception e) {
            log.warn("解析JSON数组失败: {}", json, e);
            return new ArrayList<>();
        }
    }
}
