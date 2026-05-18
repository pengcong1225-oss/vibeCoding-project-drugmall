package com.drugmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drugmall.common.BusinessException;
import com.drugmall.common.ResultCode;
import com.drugmall.dto.DoctorLoginDTO;
import com.drugmall.dto.DoctorProfileUpdateDTO;
import com.drugmall.entity.Doctor;
import com.drugmall.entity.Consultation;
import com.drugmall.entity.Prescription;
import com.drugmall.entity.DoctorSchedule;
import com.drugmall.entity.DoctorExt;
import com.drugmall.mapper.DoctorMapper;
import com.drugmall.mapper.ConsultationMapper;
import com.drugmall.mapper.PrescriptionMapper;
import com.drugmall.mapper.DoctorScheduleMapper;
import com.drugmall.mapper.DoctorExtMapper;
import com.drugmall.service.DoctorService;
import com.drugmall.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 医生服务实现
 */
@Slf4j
@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private ConsultationMapper consultationMapper;

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Autowired
    private DoctorScheduleMapper doctorScheduleMapper;

    @Autowired
    private DoctorExtMapper doctorExtMapper;

    @Override
    public LoginResultVO login(DoctorLoginDTO loginDTO) {
        log.info("医生登录: {}", loginDTO.getPhone());

        // 根据手机号查找医生
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Doctor::getPhone, loginDTO.getPhone());
        Doctor doctor = doctorMapper.selectOne(wrapper);

        if (doctor == null) {
            throw new BusinessException(ResultCode.DOCTOR_NOT_FOUND);
        }

        // 验证密码（实际应该加密比对）
        if (!doctor.getPassword().equals(loginDTO.getPassword())) {
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
        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null) {
            throw new BusinessException(ResultCode.DOCTOR_NOT_FOUND);
        }
        return convertToDoctorInfoVO(doctor);
    }

    @Override
    public List<DoctorInfoVO> listDoctors(String department, String keyword) {
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Doctor::getStatus, 1);
        wrapper.eq(Doctor::getIsDeleted, 0);

        if (department != null && !department.isEmpty()) {
            wrapper.eq(Doctor::getDepartment, department);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Doctor::getName, keyword)
                    .or().like(Doctor::getHospital, keyword)
                    .or().like(Doctor::getSpecialties, keyword));
        }
        wrapper.orderByDesc(Doctor::getRating);

        List<Doctor> doctors = doctorMapper.selectList(wrapper);
        return doctors.stream()
                .map(this::convertToDoctorInfoVO)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorInfoVO updateProfile(String doctorId, DoctorProfileUpdateDTO updateDTO) {
        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null) {
            throw new BusinessException(ResultCode.DOCTOR_NOT_FOUND);
        }

        if (updateDTO.getName() != null) {
            doctor.setName(updateDTO.getName());
        }
        if (updateDTO.getAvatar() != null) {
            doctor.setAvatar(updateDTO.getAvatar());
        }
        if (updateDTO.getTitle() != null) {
            doctor.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getHospital() != null) {
            doctor.setHospital(updateDTO.getHospital());
        }
        if (updateDTO.getDepartment() != null) {
            doctor.setDepartment(updateDTO.getDepartment());
        }
        if (updateDTO.getIntroduction() != null) {
            doctor.setIntroduction(updateDTO.getIntroduction());
        }
        if (updateDTO.getSpecialties() != null) {
            doctor.setSpecialties(String.join(",", updateDTO.getSpecialties()));
        }

        doctorMapper.updateById(doctor);
        log.info("更新医生信息: {}", doctorId);
        return convertToDoctorInfoVO(doctor);
    }

    @Override
    public DoctorStatsVO getStats(String doctorId) {
        DoctorStatsVO stats = new DoctorStatsVO();

        // 统计问诊数量
        LambdaQueryWrapper<Consultation> consultationWrapper = new LambdaQueryWrapper<>();
        consultationWrapper.eq(Consultation::getDoctorId, doctorId);
        List<Consultation> consultations = consultationMapper.selectList(consultationWrapper);

        int pending = 0, processing = 0, completed = 0;
        for (Consultation c : consultations) {
            String status = c.getStatus();
            switch (status) {
                case "pending":
                    pending++;
                    break;
                case "processing":
                    processing++;
                    break;
                case "completed":
                    completed++;
                    break;
            }
        }

        stats.setPending(pending);
        stats.setProcessing(processing);
        stats.setCompleted(completed);

        // TODO: 从收入表获取今日收入
        stats.setIncome(0.0);

        return stats;
    }

    @Override
    public DoctorTodoCountVO getTodoCount(String doctorId) {
        DoctorStatsVO stats = getStats(doctorId);
        DoctorTodoCountVO todoCount = new DoctorTodoCountVO();

        // 待办 = 待接诊 + 待审核处方
        int pendingConsultations = stats.getPending() != null ? stats.getPending() : 0;
        int pendingPrescriptions = countPendingPrescriptions(doctorId);
        todoCount.setTodoCount(pendingConsultations + pendingPrescriptions);

        // TODO: 从消息表获取未读消息
        todoCount.setUnreadCount(3);
        return todoCount;
    }

    @Override
    public List<Object> getSchedule(String doctorId) {
        LambdaQueryWrapper<DoctorSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DoctorSchedule::getDoctorId, doctorId)
               .orderByAsc(DoctorSchedule::getDayOfWeek);
        
        List<DoctorSchedule> schedules = doctorScheduleMapper.selectList(wrapper);
        
        // 转换为前端需要的格式
        List<Object> scheduleList = new ArrayList<>();
        for (DoctorSchedule schedule : schedules) {
            scheduleList.add(schedule);
        }
        return scheduleList;
    }

    @Override
    public Object getLicense(String doctorId) {
        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null) {
            return null;
        }
        
        return new Object() {
            public final String licenseNumber = doctor.getLicenseNo() != null ? doctor.getLicenseNo() : "";
            public final String licenseType = "执业医师";
            public final String licenseExpiry = "";
            public final String hospitalLevel = doctor.getHospital() != null ? "三甲" : "";
            public final int yearsOfExperience = 10;
        };
    }

    @Override
    public String assignDoctorForPrescription(String drugId) {
        // 查找在线且可开方的医生，随机分配
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Doctor::getStatus, 1)
               .eq(Doctor::getIsDeleted, 0)
               .eq(Doctor::getIsCertified, 1);
        List<Doctor> doctors = doctorMapper.selectList(wrapper);
        if (doctors != null && !doctors.isEmpty()) {
            // 简单轮询：取列表中的第一个
            return doctors.get(0).getId();
        }
        return "DOC001"; // fallback
    }

    public int countPendingPrescriptions(String doctorId) {
        LambdaQueryWrapper<Prescription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Prescription::getDoctorId, doctorId)
               .eq(Prescription::getStatus, "pending");
        Long count = prescriptionMapper.selectCount(wrapper);
        return count != null ? count.intValue() : 0;
    }

    private DoctorInfoVO convertToDoctorInfoVO(Doctor doctor) {
        if (doctor == null) {
            return null;
        }

        DoctorInfoVO vo = new DoctorInfoVO();
        vo.setId(doctor.getId());
        vo.setName(doctor.getName());
        vo.setAvatar(doctor.getAvatar());
        vo.setTitle(doctor.getTitle());
        vo.setHospital(doctor.getHospital());
        vo.setDepartment(doctor.getDepartment());
        vo.setIsCertified(doctor.getIsCertified() != null && doctor.getIsCertified() == 1);
        vo.setRating(doctor.getRating() != null ? doctor.getRating().doubleValue() : 5.0);
        vo.setServiceCount(doctor.getServiceCount() != null ? doctor.getServiceCount() : 0);
        vo.setResponseTime(doctor.getResponseTime() != null ? doctor.getResponseTime() : 30);
        vo.setIntroduction(doctor.getIntroduction() != null ? doctor.getIntroduction() : "");

        if (doctor.getSpecialties() != null && !doctor.getSpecialties().isEmpty()) {
            vo.setSpecialties(Arrays.asList(doctor.getSpecialties().split(",")));
        } else {
            vo.setSpecialties(new ArrayList<>());
        }

        LambdaQueryWrapper<DoctorExt> extWrapper = new LambdaQueryWrapper<>();
        extWrapper.eq(DoctorExt::getDoctorId, doctor.getId());
        DoctorExt ext = doctorExtMapper.selectOne(extWrapper);
        if (ext != null) {
            vo.setIsOnline(ext.getIsOnline() != null && ext.getIsOnline() == 1);
            vo.setCanPrescribe(ext.getCanPrescribe() != null && ext.getCanPrescribe() == 1);
            vo.setWaitTime(ext.getWaitTime());
            vo.setConsultCount(ext.getConsultCount());
        }

        return vo;
    }

    private com.drugmall.vo.UserInfoVO convertToDoctorUserInfoVO(Doctor doctor) {
        if (doctor == null) {
            return null;
        }
        
        com.drugmall.vo.UserInfoVO vo = new com.drugmall.vo.UserInfoVO();
        vo.setId(doctor.getId());
        vo.setNickname(doctor.getName());
        vo.setAvatar(doctor.getAvatar());
        vo.setPhone(doctor.getPhone());
        return vo;
    }
}
