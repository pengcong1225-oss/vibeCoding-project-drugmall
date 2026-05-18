package com.drugmall.service;

import com.drugmall.dto.DoctorLoginDTO;
import com.drugmall.dto.DoctorProfileUpdateDTO;
import com.drugmall.vo.DoctorInfoVO;
import com.drugmall.vo.DoctorStatsVO;
import com.drugmall.vo.DoctorTodoCountVO;
import com.drugmall.vo.LoginResultVO;

import java.util.List;

/**
 * 医生服务接口
 */
public interface DoctorService {

    /**
     * 医生登录
     */
    LoginResultVO login(DoctorLoginDTO loginDTO);

    /**
     * 获取医生信息
     */
    DoctorInfoVO getProfile(String doctorId);

    /**
     * 获取医生列表
     */
    List<DoctorInfoVO> listDoctors(String department, String keyword);

    /**
     * 更新医生信息
     */
    DoctorInfoVO updateProfile(String doctorId, DoctorProfileUpdateDTO updateDTO);

    /**
     * 获取今日统计
     */
    DoctorStatsVO getStats(String doctorId);

    /**
     * 获取待办事项数量
     */
    DoctorTodoCountVO getTodoCount(String doctorId);

    /**
     * 获取待审核处方数量
     */
    int countPendingPrescriptions(String doctorId);

    /**
     * 获取排班信息
     */
    List<Object> getSchedule(String doctorId);

    /**
     * 获取执业信息
     */
    Object getLicense(String doctorId);

    /**
     * 为处方购药自动分配医生（在线且可开方）
     */
    String assignDoctorForPrescription(String drugId);
}
