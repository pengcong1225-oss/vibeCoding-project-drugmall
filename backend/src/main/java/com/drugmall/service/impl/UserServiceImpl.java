package com.drugmall.service.impl;

import com.drugmall.config.MockDataService;
import com.drugmall.dto.*;
import com.drugmall.service.UserService;
import com.drugmall.vo.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private MockDataService mockDataService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String CURRENT_USER_ID = "1";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LoginResultVO login(LoginDTO loginDTO) {
        log.info("用户登录: {}", loginDTO.getPhone());

        // 查找用户
        JsonNode rootData = mockDataService.getUsers();
        if (rootData != null && rootData.has("users")) {
            JsonNode usersData = rootData.get("users");
            if (usersData.isArray()) {
                for (JsonNode user : usersData) {
                    if (user.get("phone").asText().equals(loginDTO.getPhone())) {
                        LoginResultVO result = new LoginResultVO();
                        result.setToken("mock_token_" + UUID.randomUUID().toString().replace("-", ""));
                        result.setUserInfo(convertToUserInfoVO(user));
                        result.setExpiresIn(7200L);
                        return result;
                    }
                }
            }
        }

        // 默认返回
        LoginResultVO result = new LoginResultVO();
        result.setToken("mock_token_" + UUID.randomUUID().toString().replace("-", ""));
        result.setUserInfo(getUserInfo(CURRENT_USER_ID));
        result.setExpiresIn(7200L);
        return result;
    }

    @Override
    public void sendCode(SendCodeDTO sendCodeDTO) {
        log.info("发送验证码到: {}, 类型: {}", sendCodeDTO.getPhone(), sendCodeDTO.getType());
    }

    @Override
    public void logout(String userId) {
        log.info("用户登出: {}", userId);
    }

    @Override
    public UserInfoVO getUserInfo(String userId) {
        JsonNode rootData = mockDataService.getUsers();
        if (rootData != null && rootData.has("users")) {
            JsonNode usersData = rootData.get("users");
            if (usersData.isArray()) {
                for (JsonNode user : usersData) {
                    if (user.get("id").asText().equals(userId)) {
                        return convertToUserInfoVO(user);
                    }
                }
            }
        }
        return createDefaultUserInfo();
    }

    @Override
    public UserInfoVO updateUserInfo(String userId, UpdateUserInfoDTO updateDTO) {
        log.info("更新用户信息: {}", userId);
        UserInfoVO userInfo = getUserInfo(userId);
        if (updateDTO.getNickname() != null) {
            userInfo.setNickname(updateDTO.getNickname());
        }
        if (updateDTO.getAvatar() != null) {
            userInfo.setAvatar(updateDTO.getAvatar());
        }
        if (updateDTO.getEmail() != null) {
            userInfo.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getBirthday() != null) {
            userInfo.setBirthday(updateDTO.getBirthday());
        }
        if (updateDTO.getGender() != null) {
            userInfo.setGender(updateDTO.getGender());
        }
        return userInfo;
    }

    @Override
    public String uploadAvatar(String userId, String avatarBase64) {
        log.info("上传头像: {}", userId);
        return "https://example.com/avatar/" + userId + ".jpg";
    }

    @Override
    public void realNameAuth(String userId, RealNameAuthDTO authDTO) {
        log.info("实名认证: {}, 姓名: {}", userId, authDTO.getRealName());
    }

    @Override
    public Boolean checkPhone(String phone) {
        JsonNode rootData = mockDataService.getUsers();
        if (rootData != null && rootData.has("users")) {
            JsonNode usersData = rootData.get("users");
            if (usersData.isArray()) {
                for (JsonNode user : usersData) {
                    if (user.get("phone").asText().equals(phone)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // ============== 就诊人管理 ==============

    @Override
    public List<PatientVO> getPatientList(String userId) {
        JsonNode patientsData = mockDataService.getPatients();
        List<PatientVO> patients = new ArrayList<>();
        if (patientsData != null && patientsData.isArray()) {
            for (JsonNode patient : patientsData) {
                if (patient.get("userId").asText().equals(userId)) {
                    patients.add(convertToPatientVO(patient));
                }
            }
        }
        return patients;
    }

    @Override
    public PatientVO getDefaultPatient(String userId) {
        return getPatientList(userId).stream()
                .filter(PatientVO::getIsDefault)
                .findFirst()
                .orElse(null);
    }

    @Override
    public PatientVO addPatient(String userId, PatientDTO patientDTO) {
        log.info("添加就诊人: {}", patientDTO.getName());
        PatientVO patient = new PatientVO();
        patient.setId(UUID.randomUUID().toString().replace("-", ""));
        patient.setName(patientDTO.getName());
        patient.setGender(patientDTO.getGender());
        patient.setAge(patientDTO.getAge());
        patient.setIdCard(patientDTO.getIdCard());
        patient.setPhone(patientDTO.getPhone());
        patient.setRelationship(patientDTO.getRelationship());
        patient.setBirthday(patientDTO.getBirthday());
        patient.setAddress(patientDTO.getAddress());
        patient.setAllergyHistory(patientDTO.getAllergyHistory());
        patient.setMedicalHistory(patientDTO.getMedicalHistory());
        patient.setIsDefault(patientDTO.getIsDefault() != null ? patientDTO.getIsDefault() : false);
        return patient;
    }

    @Override
    public PatientVO updatePatient(String userId, String patientId, PatientDTO patientDTO) {
        log.info("更新就诊人: {}", patientId);
        PatientVO patient = addPatient(userId, patientDTO);
        patient.setId(patientId);
        return patient;
    }

    @Override
    public void deletePatient(String userId, String patientId) {
        log.info("删除就诊人: {}", patientId);
    }

    @Override
    public void setDefaultPatient(String userId, String patientId) {
        log.info("设置默认就诊人: {}", patientId);
    }

    // ============== 地址管理 ==============

    @Override
    public List<AddressVO> getAddressList(String userId) {
        JsonNode addressesData = mockDataService.getAddresses();
        List<AddressVO> addresses = new ArrayList<>();
        if (addressesData != null && addressesData.isArray()) {
            for (JsonNode address : addressesData) {
                if (address.get("userId").asText().equals(userId)) {
                    addresses.add(convertToAddressVO(address));
                }
            }
        }
        return addresses;
    }

    @Override
    public AddressVO getAddressDetail(String userId, String addressId) {
        return getAddressList(userId).stream()
                .filter(a -> a.getId().equals(addressId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public AddressVO getDefaultAddress(String userId) {
        return getAddressList(userId).stream()
                .filter(AddressVO::getIsDefault)
                .findFirst()
                .orElse(null);
    }

    @Override
    public AddressVO addAddress(String userId, AddressDTO addressDTO) {
        log.info("添加地址: {}", addressDTO.getDetail());
        AddressVO address = new AddressVO();
        address.setId(UUID.randomUUID().toString().replace("-", ""));
        address.setName(addressDTO.getName());
        address.setPhone(addressDTO.getPhone());
        address.setProvince(addressDTO.getProvince());
        address.setCity(addressDTO.getCity());
        address.setDistrict(addressDTO.getDistrict());
        address.setDetail(addressDTO.getDetail());
        address.setFullAddress(addressDTO.getProvince() + addressDTO.getCity() +
                addressDTO.getDistrict() + addressDTO.getDetail());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setTag(addressDTO.getTag());
        address.setIsDefault(addressDTO.getIsDefault() != null ? addressDTO.getIsDefault() : false);
        return address;
    }

    @Override
    public AddressVO updateAddress(String userId, String addressId, AddressDTO addressDTO) {
        log.info("更新地址: {}", addressId);
        AddressVO address = addAddress(userId, addressDTO);
        address.setId(addressId);
        return address;
    }

    @Override
    public void deleteAddress(String userId, String addressId) {
        log.info("删除地址: {}", addressId);
    }

    @Override
    public void setDefaultAddress(String userId, String addressId) {
        log.info("设置默认地址: {}", addressId);
    }

    @Override
    public AddressVO parseAddress(String addressText) {
        log.info("智能解析地址: {}", addressText);
        AddressVO address = new AddressVO();
        address.setName("张三");
        address.setPhone("13800138000");
        address.setProvince("北京市");
        address.setCity("北京市");
        address.setDistrict("朝阳区");
        address.setDetail("某某小区1号楼1单元101室");
        address.setFullAddress("北京市北京市朝阳区某某小区1号楼1单元101室");
        return address;
    }

    // ============== 优惠券管理 ==============

    @Override
    public List<CouponVO> getCouponList(String userId, String status) {
        JsonNode couponsData = mockDataService.getCoupons();
        List<CouponVO> coupons = new ArrayList<>();
        if (couponsData != null && couponsData.isArray()) {
            for (JsonNode coupon : couponsData) {
                if (coupon.get("userId").asText().equals(userId)) {
                    CouponVO vo = convertToCouponVO(coupon);
                    if (status == null || vo.getStatus().equals(status)) {
                        coupons.add(vo);
                    }
                }
            }
        }
        return coupons;
    }

    @Override
    public void receiveCoupon(String userId, String couponId) {
        log.info("领取优惠券: {}", couponId);
    }

    @Override
    public List<CouponVO> getAvailableCoupons(String userId, String amount) {
        return getCouponList(userId, "unused").stream()
                .filter(c -> c.getStatus().equals("unused"))
                .collect(Collectors.toList());
    }

    // ============== 浏览历史 ==============

    @Override
    public List<BrowseHistoryVO> getBrowseHistory(String userId, Integer page, Integer size) {
        List<BrowseHistoryVO> history = new ArrayList<>();
        // 模拟浏览历史数据
        BrowseHistoryVO item1 = new BrowseHistoryVO();
        item1.setId("1");
        item1.setDrugId("1");
        item1.setName("阿莫西林胶囊");
        item1.setImage("");
        item1.setPrice(new BigDecimal("12.50"));
        item1.setBrowseTime(LocalDateTime.now().minusHours(2));
        history.add(item1);

        BrowseHistoryVO item2 = new BrowseHistoryVO();
        item2.setId("2");
        item2.setDrugId("2");
        item2.setName("布洛芬缓释胶囊");
        item2.setImage("");
        item2.setPrice(new BigDecimal("15.80"));
        item2.setBrowseTime(LocalDateTime.now().minusDays(1));
        history.add(item2);

        return history;
    }

    @Override
    public void addBrowseHistory(String userId, BrowseHistoryDTO historyDTO) {
        log.info("添加浏览历史: {}", historyDTO.getDrugId());
    }

    @Override
    public void clearBrowseHistory(String userId) {
        log.info("清空浏览历史: {}", userId);
    }

    // ============== 转换方法 ==============

    private UserInfoVO convertToUserInfoVO(JsonNode user) {
        if (user == null) {
            return createDefaultUserInfo();
        }
        UserInfoVO vo = new UserInfoVO();
        vo.setId(getTextValue(user, "id", CURRENT_USER_ID));
        vo.setPhone(getTextValue(user, "phone", ""));
        vo.setNickname(getTextValue(user, "nickname", "用户" + CURRENT_USER_ID));
        vo.setAvatar(getTextValue(user, "avatar", ""));
        vo.setEmail(getTextValue(user, "email", null));
        vo.setBirthday(getTextValue(user, "birthday", null));
        vo.setGender(getIntValue(user, "gender", 0));
        vo.setRealName(getTextValue(user, "realName", null));
        vo.setIdCard(getTextValue(user, "idCard", null));
        vo.setIsRealNameAuth(getBooleanValue(user, "isRealNameAuth", false));
        vo.setBalance(getDecimalValue(user, "balance", BigDecimal.ZERO));
        vo.setPoints(getIntValue(user, "points", 0));
        return vo;
    }

    private String getTextValue(JsonNode node, String field, String defaultValue) {
        if (node.has(field) && !node.get(field).isNull()) {
            return node.get(field).asText();
        }
        return defaultValue;
    }

    private Integer getIntValue(JsonNode node, String field, Integer defaultValue) {
        if (node.has(field) && !node.get(field).isNull()) {
            return node.get(field).asInt();
        }
        return defaultValue;
    }

    private Boolean getBooleanValue(JsonNode node, String field, Boolean defaultValue) {
        if (node.has(field) && !node.get(field).isNull()) {
            return node.get(field).asBoolean();
        }
        return defaultValue;
    }

    private BigDecimal getDecimalValue(JsonNode node, String field, BigDecimal defaultValue) {
        if (node.has(field) && !node.get(field).isNull()) {
            return new BigDecimal(node.get(field).asText());
        }
        return defaultValue;
    }

    private UserInfoVO createDefaultUserInfo() {
        UserInfoVO vo = new UserInfoVO();
        vo.setId(CURRENT_USER_ID);
        vo.setPhone("13800138000");
        vo.setNickname("用户" + CURRENT_USER_ID);
        vo.setAvatar("");
        vo.setIsRealNameAuth(false);
        vo.setBalance(new BigDecimal("1000.00"));
        vo.setPoints(500);
        return vo;
    }

    private PatientVO convertToPatientVO(JsonNode patient) {
        if (patient == null) {
            return null;
        }
        PatientVO vo = new PatientVO();
        vo.setId(getTextValue(patient, "id", ""));
        vo.setName(getTextValue(patient, "name", ""));
        vo.setGender(getIntValue(patient, "gender", 1));
        vo.setAge(getIntValue(patient, "age", 30));
        vo.setBirthday(getTextValue(patient, "birthday", null));
        vo.setIdCard(getTextValue(patient, "idCard", ""));
        vo.setPhone(getTextValue(patient, "phone", ""));
        vo.setRelationship(getTextValue(patient, "relationship", "本人"));
        vo.setAddress(getTextValue(patient, "address", null));
        vo.setAllergyHistory(getTextValue(patient, "allergyHistory", null));
        vo.setMedicalHistory(getTextValue(patient, "medicalHistory", null));
        vo.setIsDefault(getBooleanValue(patient, "isDefault", false));
        return vo;
    }

    private AddressVO convertToAddressVO(JsonNode address) {
        if (address == null) {
            return null;
        }
        AddressVO vo = new AddressVO();
        vo.setId(getTextValue(address, "id", ""));
        vo.setName(getTextValue(address, "name", ""));
        vo.setPhone(getTextValue(address, "phone", ""));
        vo.setProvince(getTextValue(address, "province", ""));
        vo.setCity(getTextValue(address, "city", ""));
        vo.setDistrict(getTextValue(address, "district", ""));
        vo.setDetail(getTextValue(address, "detail", ""));
        String province = getTextValue(address, "province", "");
        String city = getTextValue(address, "city", "");
        String district = getTextValue(address, "district", "");
        String detail = getTextValue(address, "detail", "");
        vo.setFullAddress(province + city + district + detail);
        vo.setPostalCode(getTextValue(address, "postalCode", null));
        vo.setTag(getTextValue(address, "tag", null));
        vo.setIsDefault(getBooleanValue(address, "isDefault", false));
        return vo;
    }

    private CouponVO convertToCouponVO(JsonNode coupon) {
        if (coupon == null) {
            return null;
        }
        CouponVO vo = new CouponVO();
        vo.setId(getTextValue(coupon, "id", ""));
        vo.setName(getTextValue(coupon, "name", ""));
        vo.setType(getTextValue(coupon, "type", ""));
        vo.setValue(getDecimalValue(coupon, "value", BigDecimal.ZERO));
        vo.setMinAmount(coupon.has("minAmount") && !coupon.get("minAmount").isNull() ?
                new BigDecimal(coupon.get("minAmount").asText()) : BigDecimal.ZERO);
        vo.setStatus(getTextValue(coupon, "status", ""));
        vo.setDescription(getTextValue(coupon, "description", ""));
        vo.setScope(getTextValue(coupon, "scope", "all"));

        if (coupon.has("startTime") && !coupon.get("startTime").isNull()) {
            try {
                vo.setStartTime(LocalDateTime.parse(coupon.get("startTime").asText(), DATE_TIME_FORMATTER));
            } catch (Exception e) {
                vo.setStartTime(LocalDateTime.now());
            }
        }
        if (coupon.has("endTime") && !coupon.get("endTime").isNull()) {
            try {
                vo.setEndTime(LocalDateTime.parse(coupon.get("endTime").asText(), DATE_TIME_FORMATTER));
            } catch (Exception e) {
                vo.setEndTime(LocalDateTime.now().plusDays(30));
            }
        }
        return vo;
    }
}
