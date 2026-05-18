package com.drugmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drugmall.common.BusinessException;
import com.drugmall.common.ResultCode;
import com.drugmall.dto.CreatePrescriptionDTO;
import com.drugmall.dto.WithdrawApplyDTO;
import com.drugmall.entity.Prescription;
import com.drugmall.entity.PrescriptionItem;
import com.drugmall.entity.Consultation;
import com.drugmall.entity.DoctorIncome;
import com.drugmall.entity.Drug;
import com.drugmall.entity.Patient;
import com.drugmall.entity.User;
import com.drugmall.mapper.DrugMapper;
import com.drugmall.mapper.PrescriptionMapper;
import com.drugmall.mapper.PrescriptionItemMapper;
import com.drugmall.mapper.ConsultationMapper;
import com.drugmall.mapper.DoctorIncomeMapper;
import com.drugmall.mapper.PatientMapper;
import com.drugmall.mapper.UserMapper;
import com.drugmall.service.PrescriptionService;
import com.drugmall.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 处方/患者/收入服务实现
 */
@Slf4j
@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Autowired
    private PrescriptionItemMapper prescriptionItemMapper;

    @Autowired
    private ConsultationMapper consultationMapper;

    @Autowired
    private DoctorIncomeMapper doctorIncomeMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DrugMapper drugMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<DoctorPrescriptionVO> listPrescriptions(String doctorId, String status) {
        LambdaQueryWrapper<Prescription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Prescription::getDoctorId, doctorId);

        if (status != null && !status.isEmpty() && !status.equals("all")) {
            wrapper.eq(Prescription::getStatus, status);
        }

        wrapper.orderByDesc(Prescription::getCreateTime);

        List<Prescription> prescriptions = prescriptionMapper.selectList(wrapper);
        return prescriptions.stream()
                .map(this::convertToPrescriptionVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorPrescriptionVO> listPatientPrescriptions(String userId, String status) {
        // 查找该用户的所有就诊人ID
        LambdaQueryWrapper<Patient> patientQuery = new LambdaQueryWrapper<>();
        patientQuery.eq(Patient::getUserId, parsePatientId(userId))
                    .eq(Patient::getIsDeleted, false);
        List<Patient> patients = patientMapper.selectList(patientQuery);
        List<Long> patientIds = patients.stream().map(Patient::getId).collect(Collectors.toList());
        if (patientIds.isEmpty()) {
            patientIds.add(0L); // 无就诊人时查不到数据
        }

        LambdaQueryWrapper<Prescription> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Prescription::getPatientId, patientIds);

        if (status != null && !status.isEmpty() && !status.equals("all")) {
            wrapper.eq(Prescription::getStatus, status);
        }

        wrapper.orderByDesc(Prescription::getCreateTime);

        List<Prescription> prescriptions = prescriptionMapper.selectList(wrapper);
        return prescriptions.stream()
                .map(this::convertToPrescriptionVO)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorPrescriptionVO getPrescriptionDetail(String doctorId, String prescriptionId) {
        Prescription prescription = prescriptionMapper.selectById(prescriptionId);
        if (prescription == null) {
            throw new BusinessException(ResultCode.PRESCRIPTION_NOT_FOUND);
        }
        
        DoctorPrescriptionVO vo = convertToPrescriptionVO(prescription);
        
        // 加载处方明细
        LambdaQueryWrapper<PrescriptionItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(PrescriptionItem::getPrescriptionId, prescriptionId);
        List<PrescriptionItem> items = prescriptionItemMapper.selectList(itemWrapper);
        
        List<DoctorPrescriptionVO.DrugItemVO> drugs = items.stream().map(item -> {
            DoctorPrescriptionVO.DrugItemVO drugVO = new DoctorPrescriptionVO.DrugItemVO();
            drugVO.setId(String.valueOf(item.getId()));
            drugVO.setName(item.getProductName());
            drugVO.setSpec(item.getSpecification());
            drugVO.setUnit("");
            drugVO.setPrice(item.getPrice());
            drugVO.setQuantity(item.getQuantity());
            drugVO.setDosage(item.getDosage());
            drugVO.setFrequency(item.getFrequency());
            drugVO.setDuration(item.getDuration());
            drugVO.setRemark("");
            return drugVO;
        }).collect(Collectors.toList());
        
        vo.setDrugs(drugs);
        return vo;
    }

    @Override
    public DoctorPrescriptionVO createPrescription(String doctorId, CreatePrescriptionDTO createDTO) {
        if (createDTO.getDrugs() == null || createDTO.getDrugs().isEmpty()) {
            throw new BusinessException(ResultCode.PRESCRIPTION_DRUG_EMPTY);
        }

        String now = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String prescriptionId = "PRES" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CreatePrescriptionDTO.PrescriptionDrugDTO drugDTO : createDTO.getDrugs()) {
            if (drugDTO.getPrice() != null && drugDTO.getQuantity() != null) {
                totalAmount = totalAmount.add(drugDTO.getPrice().multiply(BigDecimal.valueOf(drugDTO.getQuantity())));
            }
        }

        Prescription prescription = new Prescription();
        prescription.setId(prescriptionId);
        prescription.setDoctorId(doctorId);
        Long patientId = createDTO.getPatientId() != null ? Long.valueOf(createDTO.getPatientId()) : null;
        prescription.setPatientId(patientId);
        // 从就诊人获取 userId
        if (patientId != null) {
            Patient patient = patientMapper.selectById(patientId);
            if (patient != null) {
                prescription.setUserId(patient.getUserId());
            }
        }
        prescription.setConsultationId(createDTO.getConsultationId());
        prescription.setDiagnosis(createDTO.getDiagnosis());
        prescription.setStatus("pending");
        prescription.setTotalAmount(totalAmount);
        prescription.setCreateTime(LocalDateTime.now());
        
        prescriptionMapper.insert(prescription);

        // 插入处方明细
        for (CreatePrescriptionDTO.PrescriptionDrugDTO drugDTO : createDTO.getDrugs()) {
            PrescriptionItem item = new PrescriptionItem();
            item.setPrescriptionId(prescriptionId);
            // 按名称查找药品获取 productId
            Long productId = 0L;
            if (drugDTO.getName() != null) {
                LambdaQueryWrapper<Drug> drugQuery = new LambdaQueryWrapper<>();
                drugQuery.eq(Drug::getProductName, drugDTO.getName())
                         .eq(Drug::getIsDeleted, 0)
                         .last("LIMIT 1");
                Drug drug = drugMapper.selectOne(drugQuery);
                productId = drug != null ? drug.getId() : 0L;
            }
            item.setProductId(productId);
            item.setProductName(drugDTO.getName());
            item.setSpecification(drugDTO.getSpec());
            item.setQuantity(drugDTO.getQuantity());
            item.setDosage(drugDTO.getDosage());
            item.setFrequency(drugDTO.getFrequency());
            item.setDuration(drugDTO.getDuration());
            item.setPrice(drugDTO.getPrice());
            item.setCreateTime(LocalDateTime.now());
            
            prescriptionItemMapper.insert(item);
        }

        log.info("创建处方: {}, 总金额: {}", prescriptionId, totalAmount);
        return convertToPrescriptionVO(prescription);
    }

    @Override
    public List<PatientDetailVO> listPatients(String doctorId, String keyword) {
        // 通过问诊记录获取患者列表
        LambdaQueryWrapper<Consultation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Consultation::getDoctorId, doctorId)
               .select(Consultation::getPatientId)
               .groupBy(Consultation::getPatientId);
        
        List<Consultation> consultations = consultationMapper.selectList(wrapper);
        
        List<PatientDetailVO> result = new ArrayList<>();
        for (Consultation c : consultations) {
            if (c.getPatientId() != null) {
                PatientDetailVO vo = new PatientDetailVO();
                vo.setId(String.valueOf(c.getPatientId()));
                vo.setName("患者" + c.getPatientId());
                vo.setAge(0);
                vo.setGender("");
                vo.setPhone("");
                vo.setAvatar("");
                vo.setLastVisit(c.getCreateTime() != null ? c.getCreateTime().toString() : "");
                vo.setVisitCount(1);
                vo.setIsVip(false);
                result.add(vo);
            }
        }
        
        return result;
    }

    @Override
    public PatientDetailVO getPatientDetail(String doctorId, String patientId) {
        // 从数据库查询患者信息
        Patient patient = patientMapper.selectById(parsePatientId(patientId));
        
        PatientDetailVO vo = new PatientDetailVO();
        if (patient != null) {
            vo.setId(String.valueOf(patient.getId()));
            vo.setName(patient.getName() != null ? patient.getName() : "患者" + patientId);
            
            // 计算年龄
            if (patient.getBirthday() != null) {
                vo.setAge(java.time.Period.between(patient.getBirthday(), java.time.LocalDate.now()).getYears());
            } else {
                vo.setAge(0);
            }
            
            // 性别
            if (patient.getGender() != null) {
                vo.setGender(patient.getGender() == 1 ? "男" : "女");
            } else {
                vo.setGender("未知");
            }
            
            // 从患者表获取过敏史和病史
            vo.setAllergies(patient.getAllergyHistory() != null ? patient.getAllergyHistory() : "");
            vo.setMedicalHistory(patient.getMedicalHistory() != null ? patient.getMedicalHistory() : "");
            
            // 统计该医生的问诊次数
            LambdaQueryWrapper<Consultation> consultationWrapper = new LambdaQueryWrapper<>();
            consultationWrapper.eq(Consultation::getDoctorId, doctorId)
                              .eq(Consultation::getPatientId, patient.getId());
            long visitCount = consultationMapper.selectCount(consultationWrapper);
            vo.setVisitCount((int) visitCount);
            
            // 查询最后就诊时间
            LambdaQueryWrapper<Consultation> lastVisitWrapper = new LambdaQueryWrapper<>();
            lastVisitWrapper.eq(Consultation::getDoctorId, doctorId)
                           .eq(Consultation::getPatientId, patient.getId())
                           .orderByDesc(Consultation::getCreateTime)
                           .last("LIMIT 1");
            Consultation lastConsultation = consultationMapper.selectOne(lastVisitWrapper);
            if (lastConsultation != null && lastConsultation.getCreateTime() != null) {
                vo.setLastVisit(lastConsultation.getCreateTime().format(DATE_TIME_FORMATTER));
            } else {
                vo.setLastVisit("");
            }
            
            // 从用户表获取头像（通过user_id关联）
            if (patient.getUserId() != null) {
                User user = userMapper.selectById(patient.getUserId());
                if (user != null) {
                    vo.setAvatar(user.getAvatar() != null ? user.getAvatar() : "");
                    // 如果患者表没有电话，从用户表获取
                    if (patient.getPhone() == null || patient.getPhone().isEmpty()) {
                        vo.setPhone(user.getPhone() != null ? user.getPhone() : "");
                    }
                } else {
                    vo.setAvatar("");
                    vo.setPhone(patient.getPhone() != null ? patient.getPhone() : "");
                }
            } else {
                vo.setAvatar("");
                vo.setPhone(patient.getPhone() != null ? patient.getPhone() : "");
            }
            
            vo.setIsVip(false);
            vo.setTags(new ArrayList<>()); // TODO: 从标签表获取
            vo.setDiagnosis(new ArrayList<>()); // TODO: 从诊断记录获取
        } else {
            // 患者不存在，返回默认值
            vo.setId(patientId);
            vo.setName("患者" + patientId);
            vo.setAge(0);
            vo.setGender("未知");
            vo.setPhone("");
            vo.setAvatar("");
            vo.setLastVisit("");
            vo.setVisitCount(0);
            vo.setIsVip(false);
            vo.setAllergies("");
            vo.setMedicalHistory("");
            vo.setTags(new ArrayList<>());
            vo.setDiagnosis(new ArrayList<>());
        }
        
        return vo;
    }

    @Override
    public List<MedicalRecordVO> getMedicalRecords(String doctorId, String patientId) {
        // 从问诊记录中获取病历
        LambdaQueryWrapper<Consultation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Consultation::getDoctorId, doctorId)
               .eq(Consultation::getPatientId, parsePatientId(patientId))
               .orderByDesc(Consultation::getCreateTime);
        
        List<Consultation> consultations = consultationMapper.selectList(wrapper);
        
        return consultations.stream().map(c -> {
            MedicalRecordVO vo = new MedicalRecordVO();
            vo.setId(c.getId());
            vo.setPatientId(String.valueOf(c.getPatientId()));
            vo.setDate(c.getCreateTime() != null ? c.getCreateTime().toString() : "");
            vo.setType(c.getType());
            vo.setDiagnosis("");
            vo.setPrescription("");
            vo.setNotes(c.getSymptom());
            vo.setDoctor("");
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public IncomeOverviewVO getIncomeOverview(String doctorId) {
        IncomeOverviewVO vo = new IncomeOverviewVO();
        
        // 从医生收入表统计数据
        LambdaQueryWrapper<DoctorIncome> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DoctorIncome::getDoctorId, doctorId);
        List<DoctorIncome> incomes = doctorIncomeMapper.selectList(wrapper);
        
        double balance = 0;
        double monthIncome = 0;
        double totalIncome = 0;
        double todayIncome = 0;
        double weekIncome = 0;
        double pendingSettlement = 0;
        double totalWithdraw = 0;
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.minusDays(7);
        LocalDateTime monthStart = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        
        for (DoctorIncome income : incomes) {
            double amount = income.getAmount() != null ? income.getAmount().doubleValue() : 0;
            totalIncome += amount;
            
            if ("settled".equals(income.getStatus())) {
                balance += amount;
                
                if (income.getSettleTime() != null) {
                    if (income.getSettleTime().isAfter(todayStart)) {
                        todayIncome += amount;
                    }
                    if (income.getSettleTime().isAfter(weekStart)) {
                        weekIncome += amount;
                    }
                    if (income.getSettleTime().isAfter(monthStart)) {
                        monthIncome += amount;
                    }
                }
            } else if ("pending".equals(income.getStatus())) {
                pendingSettlement += amount;
            }
        }
        
        vo.setBalance(balance);
        vo.setMonthIncome(monthIncome);
        vo.setMonthIncomeRatio(0.0);
        vo.setTotalIncome(totalIncome);
        vo.setTodayIncome(todayIncome);
        vo.setWeekIncome(weekIncome);
        vo.setPendingSettlement(pendingSettlement);
        vo.setTotalWithdraw(totalWithdraw);
        
        return vo;
    }

    @Override
    public List<IncomeRecordVO> getIncomeList(String doctorId) {
        LambdaQueryWrapper<DoctorIncome> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DoctorIncome::getDoctorId, doctorId)
               .orderByDesc(DoctorIncome::getCreateTime);
        
        List<DoctorIncome> incomes = doctorIncomeMapper.selectList(wrapper);
        
        return incomes.stream().map(income -> {
            IncomeRecordVO vo = new IncomeRecordVO();
            vo.setId(String.valueOf(income.getId()));
            vo.setType(income.getType());
            vo.setTypeIcon("");
            vo.setAmount(income.getAmount() != null ? income.getAmount().doubleValue() : 0.0);
            vo.setPatientName("");
            vo.setSource("");
            vo.setTime(income.getCreateTime() != null ? income.getCreateTime().toString() : "");
            vo.setStatus(income.getStatus());
            vo.setInquiryId(income.getConsultationId());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<IncomeTrendVO> getIncomeTrend(String doctorId, String dimension) {
        // TODO: 根据日期维度统计收入趋势
        List<IncomeTrendVO> result = new ArrayList<>();
        return result;
    }

    @Override
    public List<IncomeCompositionVO> getIncomeComposition(String doctorId) {
        // TODO: 统计收入构成
        List<IncomeCompositionVO> result = new ArrayList<>();
        return result;
    }

    @Override
    public List<WithdrawRecordVO> getWithdrawList(String doctorId) {
        // TODO: 从提现记录表获取
        List<WithdrawRecordVO> result = new ArrayList<>();
        return result;
    }

    @Override
    public boolean applyWithdraw(String doctorId, WithdrawApplyDTO withdrawDTO) {
        log.info("申请提现: 金额={}", withdrawDTO.getAmount());
        // TODO: 创建提现记录
        return true;
    }

    private DoctorPrescriptionVO convertToPrescriptionVO(Prescription p) {
        if (p == null) {
            return null;
        }

        DoctorPrescriptionVO vo = new DoctorPrescriptionVO();
        vo.setId(p.getId());
        vo.setPatientId(p.getPatientId() != null ? String.valueOf(p.getPatientId()) : "");

        // 填充患者信息
        if (p.getPatientId() != null) {
            Patient patient = patientMapper.selectById(p.getPatientId());
            if (patient != null) {
                vo.setPatientName(patient.getName() != null ? patient.getName() : "");
                if (patient.getBirthday() != null) {
                    vo.setPatientAge(java.time.Period.between(patient.getBirthday(), java.time.LocalDate.now()).getYears());
                } else {
                    vo.setPatientAge(0);
                }
                vo.setPatientGender(patient.getGender() != null ? (patient.getGender() == 1 ? "男" : "女") : "");
            } else {
                vo.setPatientName("");
                vo.setPatientAge(0);
                vo.setPatientGender("");
            }
        } else {
            vo.setPatientName("");
            vo.setPatientAge(0);
            vo.setPatientGender("");
        }
        vo.setConsultationId(p.getConsultationId());
        vo.setDiagnosis(p.getDiagnosis());
        vo.setTotalAmount(p.getTotalAmount() != null ? p.getTotalAmount() : BigDecimal.ZERO);
        vo.setStatus(p.getStatus());
        vo.setStatusText(getStatusText(p.getStatus()));
        vo.setCreateTime(p.getCreateTime() != null ? p.getCreateTime().format(DATE_TIME_FORMATTER) : "");
        vo.setPharmacist("");
        vo.setReviewTime("");
        vo.setRejectReason(p.getRejectReason());
        vo.setDrugs(new ArrayList<>());

        // 填充关联的问诊会话信息
        if (p.getConsultationId() != null && !p.getConsultationId().isEmpty()) {
            Consultation consultation = consultationMapper.selectById(p.getConsultationId());
            if (consultation != null) {
                vo.setConsultationStatus(getConsultationStatusText(consultation.getStatus()));
                vo.setConsultationSymptom(consultation.getSymptom());
                vo.setConsultationType(consultation.getType());
            }
        }

        return vo;
    }

    private String getStatusText(String status) {
        switch (status) {
            case "pending":
                return "待审核";
            case "approved":
                return "已通过";
            case "rejected":
                return "已拒绝";
            default:
                return status;
        }
    }

    private String getConsultationStatusText(String status) {
        switch (status) {
            case "pending":
                return "待接诊";
            case "processing":
                return "进行中";
            case "completed":
                return "已完成";
            case "closed":
                return "已关闭";
            default:
                return status;
        }
    }

    private Long parsePatientId(String patientId) {
        try {
            return Long.valueOf(patientId.replace("USER", "").replace("PATIENT", ""));
        } catch (NumberFormatException e) {
            log.warn("无法解析 patientId: {}, 使用默认值 1", patientId);
            return 1L;
        }
    }
}
