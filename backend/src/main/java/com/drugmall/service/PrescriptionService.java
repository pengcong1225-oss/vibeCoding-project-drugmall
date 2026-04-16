package com.drugmall.service;

import com.drugmall.dto.CreatePrescriptionDTO;
import com.drugmall.dto.WithdrawApplyDTO;
import com.drugmall.vo.*;

import java.util.List;

/**
 * 处方/患者/收入服务接口
 */
public interface PrescriptionService {

    /**
     * 获取处方列表
     */
    List<DoctorPrescriptionVO> listPrescriptions(String doctorId, String status);

    /**
     * 获取处方详情
     */
    DoctorPrescriptionVO getPrescriptionDetail(String doctorId, String prescriptionId);

    /**
     * 创建处方
     */
    DoctorPrescriptionVO createPrescription(String doctorId, CreatePrescriptionDTO createDTO);

    /**
     * 获取患者列表
     */
    List<PatientDetailVO> listPatients(String doctorId, String keyword);

    /**
     * 获取患者详情
     */
    PatientDetailVO getPatientDetail(String doctorId, String patientId);

    /**
     * 获取病历记录
     */
    List<MedicalRecordVO> getMedicalRecords(String doctorId, String patientId);

    /**
     * 获取收入总览
     */
    IncomeOverviewVO getIncomeOverview(String doctorId);

    /**
     * 获取收入明细
     */
    List<IncomeRecordVO> getIncomeList(String doctorId);

    /**
     * 获取收入趋势
     */
    List<IncomeTrendVO> getIncomeTrend(String doctorId, String dimension);

    /**
     * 获取收入构成
     */
    List<IncomeCompositionVO> getIncomeComposition(String doctorId);

    /**
     * 获取提现记录
     */
    List<WithdrawRecordVO> getWithdrawList(String doctorId);

    /**
     * 申请提现
     */
    boolean applyWithdraw(String doctorId, WithdrawApplyDTO withdrawDTO);
}
