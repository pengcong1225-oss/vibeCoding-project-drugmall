package com.drugmall.service.impl;

import com.drugmall.common.BusinessException;
import com.drugmall.common.ResultCode;
import com.drugmall.config.MockDataService;
import com.drugmall.dto.DoctorLoginDTO;
import com.drugmall.dto.DoctorProfileUpdateDTO;
import com.drugmall.service.DoctorService;
import com.drugmall.vo.*;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 医生服务实现
 */
@Slf4j
@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private MockDataService mockDataService;

    private static final String CURRENT_DOCTOR_ID = "DOC001";

    @Override
    public LoginResultVO login(DoctorLoginDTO loginDTO) {
        log.info("医生登录: {}", loginDTO.getPhone());

        JsonNode doctorData = mockDataService.getDoctorData();
        if (doctorData == null || !doctorData.has("doctor")) {
            throw new BusinessException(ResultCode.DOCTOR_NOT_FOUND);
        }

        JsonNode doctor = doctorData.get("doctor");
        String phone = doctor.get("phone").asText();
        String password = doctor.get("password").asText();

        if (!phone.equals(loginDTO.getPhone()) || !password.equals(loginDTO.getPassword())) {
            throw new BusinessException(ResultCode.DOCTOR_LOGIN_FAILED);
        }

        DoctorInfoVO doctorInfo = convertToDoctorInfoVO(doctor);

        LoginResultVO result = new LoginResultVO();
        result.setToken("doctor_token_" + UUID.randomUUID().toString().replace("-", ""));
        result.setUserInfo(convertToDoctorUserInfoVO(doctor));
        result.setExpiresIn(7200L);
        return result;
    }

    @Override
    public DoctorInfoVO getProfile(String doctorId) {
        JsonNode doctorData = mockDataService.getDoctorData();
        if (doctorData == null || !doctorData.has("doctor")) {
            throw new BusinessException(ResultCode.DOCTOR_NOT_FOUND);
        }
        return convertToDoctorInfoVO(doctorData.get("doctor"));
    }

    @Override
    public DoctorInfoVO updateProfile(String doctorId, DoctorProfileUpdateDTO updateDTO) {
        log.info("更新医生信息: {}", updateDTO);
        // Mock: 直接返回当前信息
        return getProfile(doctorId);
    }

    @Override
    public DoctorStatsVO getStats(String doctorId) {
        JsonNode doctorData = mockDataService.getDoctorData();
        DoctorStatsVO stats = new DoctorStatsVO();

        if (doctorData != null && doctorData.has("consultations")) {
            JsonNode consultations = doctorData.get("consultations");
            int pending = 0, processing = 0, completed = 0;

            for (JsonNode c : consultations) {
                String status = c.get("status").asText();
                switch (status) {
                    case "pending" -> pending++;
                    case "processing" -> processing++;
                    case "completed" -> completed++;
                }
            }

            stats.setPending(pending);
            stats.setProcessing(processing);
            stats.setCompleted(completed);
        }

        // 模拟今日收入
        if (doctorData != null && doctorData.has("income")) {
            stats.setIncome(doctorData.get("income").get("todayIncome").asDouble());
        }

        return stats;
    }

    @Override
    public DoctorTodoCountVO getTodoCount(String doctorId) {
        DoctorStatsVO stats = getStats(doctorId);
        DoctorTodoCountVO todoCount = new DoctorTodoCountVO();

        // 待办 = 待接诊 + 待审核处方
        int pendingConsultations = stats.getPending() != null ? stats.getPending() : 0;
        int pendingPrescriptions = countPendingPrescriptions();
        todoCount.setTodoCount(pendingConsultations + pendingPrescriptions);

        // 模拟未读消息
        todoCount.setUnreadCount(3);
        return todoCount;
    }

    @Override
    public List<Object> getSchedule(String doctorId) {
        JsonNode doctorData = mockDataService.getDoctorData();
        List<Object> scheduleList = new ArrayList<>();

        if (doctorData != null && doctorData.has("schedule")) {
            for (JsonNode s : doctorData.get("schedule")) {
                scheduleList.add(s);
            }
        }
        return scheduleList;
    }

    @Override
    public Object getLicense(String doctorId) {
        JsonNode doctorData = mockDataService.getDoctorData();
        if (doctorData != null && doctorData.has("doctor")) {
            JsonNode doctor = doctorData.get("doctor");
            return new Object() {
                public final String licenseNumber = doctor.get("licenseNumber").asText();
                public final String licenseType = doctor.get("licenseType").asText();
                public final String licenseExpiry = doctor.get("licenseExpiry").asText();
                public final String hospitalLevel = doctor.get("hospitalLevel").asText();
                public final int yearsOfExperience = doctor.get("yearsOfExperience").asInt();
            };
        }
        return null;
    }

    private int countPendingPrescriptions() {
        JsonNode doctorData = mockDataService.getDoctorData();
        if (doctorData != null && doctorData.has("prescriptions")) {
            int count = 0;
            for (JsonNode p : doctorData.get("prescriptions")) {
                if ("pending".equals(p.get("status").asText())) {
                    count++;
                }
            }
            return count;
        }
        return 0;
    }

    private DoctorInfoVO convertToDoctorInfoVO(JsonNode doctor) {
        DoctorInfoVO vo = new DoctorInfoVO();
        vo.setId(doctor.get("id").asText());
        vo.setName(doctor.get("name").asText());
        vo.setAvatar(doctor.get("avatar").asText());
        vo.setTitle(doctor.get("title").asText());
        vo.setHospital(doctor.get("hospital").asText());
        vo.setDepartment(doctor.get("department").asText());
        vo.setIsCertified(doctor.get("isCertified").asBoolean());
        vo.setRating(doctor.get("rating").asDouble());
        vo.setServiceCount(doctor.get("serviceCount").asInt());
        vo.setResponseTime(doctor.get("responseTime").asInt());
        vo.setIntroduction(doctor.has("introduction") && !doctor.get("introduction").isNull()
                ? doctor.get("introduction").asText() : "");

        if (doctor.has("specialties") && doctor.get("specialties").isArray()) {
            List<String> specialties = new ArrayList<>();
            for (JsonNode s : doctor.get("specialties")) {
                specialties.add(s.asText());
            }
            vo.setSpecialties(specialties);
        }
        return vo;
    }

    private com.drugmall.vo.UserInfoVO convertToDoctorUserInfoVO(JsonNode doctor) {
        com.drugmall.vo.UserInfoVO vo = new com.drugmall.vo.UserInfoVO();
        vo.setId(doctor.get("id").asText());
        vo.setNickname(doctor.get("name").asText());
        vo.setAvatar(doctor.get("avatar").asText());
        vo.setPhone(doctor.get("phone").asText());
        return vo;
    }
}
