package com.drugmall.service.impl;

import com.drugmall.common.BusinessException;
import com.drugmall.common.ResultCode;
import com.drugmall.config.MockDataService;
import com.drugmall.dto.CreatePrescriptionDTO;
import com.drugmall.dto.WithdrawApplyDTO;
import com.drugmall.service.PrescriptionService;
import com.drugmall.vo.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 处方/患者/收入服务实现
 */
@Slf4j
@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    @Autowired
    private MockDataService mockDataService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<DoctorPrescriptionVO> listPrescriptions(String doctorId, String status) {
        JsonNode doctorData = mockDataService.getDoctorData();
        List<DoctorPrescriptionVO> result = new ArrayList<>();

        if (doctorData == null || !doctorData.has("prescriptions")) {
            return result;
        }

        for (JsonNode p : doctorData.get("prescriptions")) {
            String prescriptionStatus = p.get("status").asText();
            if (status != null && !status.isEmpty() && !status.equals("all") && !status.equals(prescriptionStatus)) {
                continue;
            }
            result.add(convertToPrescriptionVO(p));
        }

        return result;
    }

    @Override
    public DoctorPrescriptionVO getPrescriptionDetail(String doctorId, String prescriptionId) {
        JsonNode doctorData = mockDataService.getDoctorData();
        if (doctorData == null || !doctorData.has("prescriptions")) {
            throw new BusinessException(ResultCode.PRESCRIPTION_NOT_FOUND);
        }

        for (JsonNode p : doctorData.get("prescriptions")) {
            if (p.get("id").asText().equals(prescriptionId)) {
                return convertToPrescriptionVO(p);
            }
        }

        throw new BusinessException(ResultCode.PRESCRIPTION_NOT_FOUND);
    }

    @Override
    public DoctorPrescriptionVO createPrescription(String doctorId, CreatePrescriptionDTO createDTO) {
        if (createDTO.getDrugs() == null || createDTO.getDrugs().isEmpty()) {
            throw new BusinessException(ResultCode.PRESCRIPTION_DRUG_EMPTY);
        }

        JsonNode doctorData = mockDataService.getDoctorData();
        String now = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String prescriptionId = "PRES" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        ObjectNode newPrescription = objectMapper.createObjectNode();
        newPrescription.put("id", prescriptionId);
        newPrescription.put("doctorId", doctorId);
        newPrescription.put("patientId", createDTO.getPatientId());
        newPrescription.put("patientName", createDTO.getPatientName() != null ? createDTO.getPatientName() : "");
        newPrescription.put("patientAge", createDTO.getPatientAge() != null ? createDTO.getPatientAge() : 0);
        newPrescription.put("patientGender", createDTO.getPatientGender() != null ? createDTO.getPatientGender() : "");
        newPrescription.put("consultationId", createDTO.getConsultationId() != null ? createDTO.getConsultationId() : "");
        newPrescription.put("diagnosis", createDTO.getDiagnosis());
        newPrescription.put("status", "pending");
        newPrescription.put("statusText", "待审核");
        newPrescription.put("createTime", now);
        newPrescription.put("pharmacist", "");
        newPrescription.put("reviewTime", "");
        newPrescription.put("rejectReason", "");

        // 构建药品数组并计算总金额
        ArrayNode drugsArray = objectMapper.createArrayNode();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CreatePrescriptionDTO.PrescriptionDrugDTO drugDTO : createDTO.getDrugs()) {
            ObjectNode drugNode = objectMapper.createObjectNode();
            drugNode.put("id", drugDTO.getId() != null ? drugDTO.getId() : "D" + System.currentTimeMillis());
            drugNode.put("name", drugDTO.getName());
            drugNode.put("spec", drugDTO.getSpec() != null ? drugDTO.getSpec() : "");
            drugNode.put("unit", drugDTO.getUnit() != null ? drugDTO.getUnit() : "");
            drugNode.put("price", drugDTO.getPrice() != null ? drugDTO.getPrice().doubleValue() : 0.0);
            drugNode.put("quantity", drugDTO.getQuantity() != null ? drugDTO.getQuantity() : 1);
            drugNode.put("dosage", drugDTO.getDosage() != null ? drugDTO.getDosage() : "");
            drugNode.put("frequency", drugDTO.getFrequency() != null ? drugDTO.getFrequency() : "");
            drugNode.put("duration", drugDTO.getDuration() != null ? drugDTO.getDuration() : "");
            drugNode.put("remark", drugDTO.getRemark() != null ? drugDTO.getRemark() : "");

            if (drugDTO.getPrice() != null && drugDTO.getQuantity() != null) {
                totalAmount = totalAmount.add(drugDTO.getPrice().multiply(BigDecimal.valueOf(drugDTO.getQuantity())));
            }

            drugsArray.add(drugNode);
        }

        newPrescription.set("drugs", drugsArray);
        newPrescription.put("totalAmount", totalAmount.doubleValue());

        // 添加到处方列表
        if (doctorData != null && doctorData.has("prescriptions")) {
            ((ArrayNode) doctorData.get("prescriptions")).insert(0, newPrescription);
        }

        log.info("创建处方: {}, 总金额: {}", prescriptionId, totalAmount);
        return convertToPrescriptionVO(newPrescription);
    }

    @Override
    public List<PatientDetailVO> listPatients(String doctorId, String keyword) {
        JsonNode doctorData = mockDataService.getDoctorData();
        List<PatientDetailVO> result = new ArrayList<>();

        if (doctorData == null || !doctorData.has("patients")) {
            return result;
        }

        for (JsonNode p : doctorData.get("patients")) {
            String name = p.get("name").asText();
            if (keyword != null && !keyword.isEmpty() && !name.contains(keyword)) {
                continue;
            }
            result.add(convertToPatientVO(p));
        }

        return result;
    }

    @Override
    public PatientDetailVO getPatientDetail(String doctorId, String patientId) {
        JsonNode doctorData = mockDataService.getDoctorData();
        if (doctorData == null || !doctorData.has("patients")) {
            throw new BusinessException(ResultCode.PATIENT_NOT_FOUND);
        }

        for (JsonNode p : doctorData.get("patients")) {
            if (p.get("id").asText().equals(patientId)) {
                return convertToPatientVO(p);
            }
        }

        throw new BusinessException(ResultCode.PATIENT_NOT_FOUND);
    }

    @Override
    public List<MedicalRecordVO> getMedicalRecords(String doctorId, String patientId) {
        JsonNode doctorData = mockDataService.getDoctorData();
        List<MedicalRecordVO> result = new ArrayList<>();

        if (doctorData != null && doctorData.has("medicalRecords") && doctorData.get("medicalRecords").has(patientId)) {
            for (JsonNode r : doctorData.get("medicalRecords").get(patientId)) {
                MedicalRecordVO vo = new MedicalRecordVO();
                vo.setId(r.get("id").asText());
                vo.setPatientId(r.get("patientId").asText());
                vo.setDate(r.get("date").asText());
                vo.setType(r.get("type").asText());
                vo.setDiagnosis(r.get("diagnosis").asText());
                vo.setPrescription(r.has("prescription") && !r.get("prescription").isNull() ? r.get("prescription").asText() : "");
                vo.setNotes(r.get("notes").asText());
                vo.setDoctor(r.get("doctor").asText());
                result.add(vo);
            }
        }

        return result;
    }

    @Override
    public IncomeOverviewVO getIncomeOverview(String doctorId) {
        JsonNode doctorData = mockDataService.getDoctorData();
        IncomeOverviewVO vo = new IncomeOverviewVO();

        if (doctorData != null && doctorData.has("income")) {
            JsonNode income = doctorData.get("income");
            vo.setBalance(income.get("balance").asDouble());
            vo.setMonthIncome(income.get("monthIncome").asDouble());
            vo.setMonthIncomeRatio(income.get("monthIncomeRatio").asDouble());
            vo.setTotalIncome(income.get("totalIncome").asDouble());
            vo.setTodayIncome(income.get("todayIncome").asDouble());
            vo.setWeekIncome(income.get("weekIncome").asDouble());
            vo.setPendingSettlement(income.get("pendingSettlement").asDouble());
            vo.setTotalWithdraw(income.get("totalWithdraw").asDouble());
        }

        return vo;
    }

    @Override
    public List<IncomeRecordVO> getIncomeList(String doctorId) {
        JsonNode doctorData = mockDataService.getDoctorData();
        List<IncomeRecordVO> result = new ArrayList<>();

        if (doctorData == null || !doctorData.has("incomeRecords")) {
            return result;
        }

        for (JsonNode r : doctorData.get("incomeRecords")) {
            IncomeRecordVO vo = new IncomeRecordVO();
            vo.setId(r.get("id").asText());
            vo.setType(r.get("type").asText());
            vo.setTypeIcon(r.has("typeIcon") ? r.get("typeIcon").asText() : "");
            vo.setAmount(r.get("amount").asDouble());
            vo.setPatientName(r.get("patientName").asText());
            vo.setSource(r.get("source").asText());
            vo.setTime(r.get("time").asText());
            vo.setStatus(r.get("status").asText());
            vo.setInquiryId(r.has("inquiryId") ? r.get("inquiryId").asText() : "");
            result.add(vo);
        }

        return result;
    }

    @Override
    public List<IncomeTrendVO> getIncomeTrend(String doctorId, String dimension) {
        JsonNode doctorData = mockDataService.getDoctorData();
        List<IncomeTrendVO> result = new ArrayList<>();

        if (doctorData != null && doctorData.has("trendData")) {
            for (JsonNode t : doctorData.get("trendData")) {
                IncomeTrendVO vo = new IncomeTrendVO();
                vo.setDate(t.get("date").asText());
                vo.setIncome(t.get("income").asInt());
                vo.setQuantity(t.get("quantity").asInt());
                result.add(vo);
            }
        }

        return result;
    }

    @Override
    public List<IncomeCompositionVO> getIncomeComposition(String doctorId) {
        JsonNode doctorData = mockDataService.getDoctorData();
        List<IncomeCompositionVO> result = new ArrayList<>();

        if (doctorData != null && doctorData.has("compositionData")) {
            for (JsonNode c : doctorData.get("compositionData")) {
                IncomeCompositionVO vo = new IncomeCompositionVO();
                vo.setType(c.get("type").asText());
                vo.setAmount(c.get("amount").asInt());
                vo.setPercentage(c.get("percentage").asDouble());
                result.add(vo);
            }
        }

        return result;
    }

    @Override
    public List<WithdrawRecordVO> getWithdrawList(String doctorId) {
        JsonNode doctorData = mockDataService.getDoctorData();
        List<WithdrawRecordVO> result = new ArrayList<>();

        if (doctorData == null || !doctorData.has("withdrawRecords")) {
            return result;
        }

        for (JsonNode w : doctorData.get("withdrawRecords")) {
            WithdrawRecordVO vo = new WithdrawRecordVO();
            vo.setWithdrawId(w.get("withdrawId").asText());
            vo.setAmount(w.get("amount").asDouble());
            vo.setMethod(w.get("method").asText());
            vo.setMethodIcon(w.has("methodIcon") ? w.get("methodIcon").asText() : "");
            vo.setMethodName(w.get("methodName").asText());
            vo.setStatus(w.get("status").asText());
            vo.setStatusText(w.get("statusText").asText());
            vo.setApplyTime(w.get("applyTime").asText());
            vo.setArrivalTime(w.has("arrivalTime") && !w.get("arrivalTime").isNull() ? w.get("arrivalTime").asText() : "");
            vo.setRejectReason(w.has("rejectReason") && !w.get("rejectReason").isNull() ? w.get("rejectReason").asText() : "");
            result.add(vo);
        }

        return result;
    }

    @Override
    public boolean applyWithdraw(String doctorId, WithdrawApplyDTO withdrawDTO) {
        JsonNode doctorData = mockDataService.getDoctorData();
        String now = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String withdrawId = "WIT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        ObjectNode newWithdraw = objectMapper.createObjectNode();
        newWithdraw.put("withdrawId", withdrawId);
        newWithdraw.put("doctorId", doctorId);
        newWithdraw.put("amount", withdrawDTO.getAmount().doubleValue());
        newWithdraw.put("method", withdrawDTO.getMethod());
        newWithdraw.put("methodIcon", "bank".equals(withdrawDTO.getMethod()) ? "icon-bank" : "icon-alipay");
        newWithdraw.put("methodName", withdrawDTO.getMethodName() != null ? withdrawDTO.getMethodName() : "");
        newWithdraw.put("status", "processing");
        newWithdraw.put("statusText", "处理中");
        newWithdraw.put("applyTime", now);
        newWithdraw.put("arrivalTime", "");
        newWithdraw.put("rejectReason", "");

        if (doctorData != null && doctorData.has("withdrawRecords")) {
            ((ArrayNode) doctorData.get("withdrawRecords")).insert(0, newWithdraw);
        }

        log.info("申请提现: {}, 金额: {}", withdrawId, withdrawDTO.getAmount());
        return true;
    }

    private DoctorPrescriptionVO convertToPrescriptionVO(JsonNode p) {
        DoctorPrescriptionVO vo = new DoctorPrescriptionVO();
        vo.setId(p.get("id").asText());
        vo.setPatientId(p.get("patientId").asText());
        vo.setPatientName(p.get("patientName").asText());
        vo.setPatientAge(p.get("patientAge").asInt());
        vo.setPatientGender(p.get("patientGender").asText());
        vo.setConsultationId(p.get("consultationId").asText());
        vo.setDiagnosis(p.get("diagnosis").asText());
        vo.setTotalAmount(p.get("totalAmount").asDouble() > 0 ? BigDecimal.valueOf(p.get("totalAmount").asDouble()) : BigDecimal.ZERO);
        vo.setStatus(p.get("status").asText());
        vo.setStatusText(p.get("statusText").asText());
        vo.setCreateTime(p.get("createTime").asText());
        vo.setPharmacist(p.has("pharmacist") ? p.get("pharmacist").asText() : "");
        vo.setReviewTime(p.has("reviewTime") ? p.get("reviewTime").asText() : "");
        vo.setRejectReason(p.has("rejectReason") ? p.get("rejectReason").asText() : "");

        // 解析药品列表
        if (p.has("drugs") && p.get("drugs").isArray()) {
            List<DoctorPrescriptionVO.DrugItemVO> drugs = new ArrayList<>();
            for (JsonNode d : p.get("drugs")) {
                DoctorPrescriptionVO.DrugItemVO drugVO = new DoctorPrescriptionVO.DrugItemVO();
                drugVO.setId(d.get("id").asText());
                drugVO.setName(d.get("name").asText());
                drugVO.setSpec(d.has("spec") ? d.get("spec").asText() : "");
                drugVO.setUnit(d.has("unit") ? d.get("unit").asText() : "");
                drugVO.setPrice(d.has("price") && d.get("price").asDouble() > 0 ? BigDecimal.valueOf(d.get("price").asDouble()) : BigDecimal.ZERO);
                drugVO.setQuantity(d.has("quantity") ? d.get("quantity").asInt() : 1);
                drugVO.setDosage(d.has("dosage") ? d.get("dosage").asText() : "");
                drugVO.setFrequency(d.has("frequency") ? d.get("frequency").asText() : "");
                drugVO.setDuration(d.has("duration") ? d.get("duration").asText() : "");
                drugVO.setRemark(d.has("remark") ? d.get("remark").asText() : "");
                drugs.add(drugVO);
            }
            vo.setDrugs(drugs);
        }

        return vo;
    }

    private PatientDetailVO convertToPatientVO(JsonNode p) {
        PatientDetailVO vo = new PatientDetailVO();
        vo.setId(p.get("id").asText());
        vo.setName(p.get("name").asText());
        vo.setAge(p.get("age").asInt());
        vo.setGender(p.get("gender").asText());
        vo.setPhone(p.get("phone").asText());
        vo.setAvatar(p.get("avatar").asText());
        vo.setLastVisit(p.get("lastVisit").asText());
        vo.setVisitCount(p.get("visitCount").asInt());
        vo.setIsVip(p.get("isVip").asBoolean());
        vo.setAllergies(p.has("allergies") && !p.get("allergies").isNull() ? p.get("allergies").asText() : null);
        vo.setMedicalHistory(p.has("medicalHistory") && !p.get("medicalHistory").isNull() ? p.get("medicalHistory").asText() : null);

        if (p.has("tags") && p.get("tags").isArray()) {
            List<String> tags = new ArrayList<>();
            for (JsonNode t : p.get("tags")) tags.add(t.asText());
            vo.setTags(tags);
        }
        if (p.has("diagnosis") && p.get("diagnosis").isArray()) {
            List<String> diagnosis = new ArrayList<>();
            for (JsonNode d : p.get("diagnosis")) diagnosis.add(d.asText());
            vo.setDiagnosis(diagnosis);
        }

        return vo;
    }
}
