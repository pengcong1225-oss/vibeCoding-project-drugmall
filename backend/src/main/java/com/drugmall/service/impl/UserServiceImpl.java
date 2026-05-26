package com.drugmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drugmall.dto.*;
import com.drugmall.entity.User;
import com.drugmall.entity.Patient;
import com.drugmall.entity.Address;
import com.drugmall.entity.Coupon;
import com.drugmall.entity.BrowseHistory;
import com.drugmall.mapper.UserMapper;
import com.drugmall.mapper.PatientMapper;
import com.drugmall.mapper.AddressMapper;
import com.drugmall.mapper.CouponMapper;
import com.drugmall.mapper.BrowseHistoryMapper;
import com.drugmall.service.UserService;
import com.drugmall.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private UserMapper userMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private BrowseHistoryMapper browseHistoryMapper;

    private static final String CURRENT_USER_ID = "1";

    @Override
    public LoginResultVO login(LoginDTO loginDTO) {
        log.info("用户登录: {}", loginDTO.getPhone());

        // 根据手机号查找用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, loginDTO.getPhone());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            LoginResultVO result = new LoginResultVO();
            result.setToken(null);
            result.setUserInfo(null);
            result.setExpiresIn(0L);
            return result;
        }

        LoginResultVO result = new LoginResultVO();
        result.setToken("token_" + UUID.randomUUID().toString().replace("-", ""));
        result.setUserInfo(convertToUserInfoVO(user));
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
        User user = userMapper.selectById(userId);
        if (user == null) {
            return createDefaultUserInfo();
        }
        return convertToUserInfoVO(user);
    }

    @Override
    public UserInfoVO updateUserInfo(String userId, UpdateUserInfoDTO updateDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return createDefaultUserInfo();
        }

        if (updateDTO.getNickname() != null) {
            user.setNickname(updateDTO.getNickname());
        }
        if (updateDTO.getAvatar() != null) {
            user.setAvatar(updateDTO.getAvatar());
        }
        if (updateDTO.getEmail() != null) {
            user.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getBirthday() != null && !updateDTO.getBirthday().isEmpty()) {
            try {
                user.setBirthday(LocalDate.parse(updateDTO.getBirthday()));
            } catch (Exception e) {
                log.warn("解析生日失败: {}", updateDTO.getBirthday(), e);
            }
        }
        if (updateDTO.getGender() != null) {
            user.setGender(updateDTO.getGender());
        }

        userMapper.updateById(user);
        return convertToUserInfoVO(user);
    }

    @Override
    public String uploadAvatar(String userId, org.springframework.web.multipart.MultipartFile file) {
        log.info("上传头像: {}, 文件名: {}, 大小: {}bytes", userId, file.getOriginalFilename(), file.getSize());
        return "https://example.com/avatar/" + userId + ".jpg";
    }

    @Override
    public void realNameAuth(String userId, RealNameAuthDTO authDTO) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setRealName(authDTO.getRealName());
            user.setIdCard(authDTO.getIdCard());
            user.setIsRealNameAuth(true);
            userMapper.updateById(user);
        }
        log.info("实名认证: {}, 姓名: {}", userId, authDTO.getRealName());
    }

    @Override
    public Boolean checkPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        Long count = userMapper.selectCount(wrapper);
        return count > 0;
    }

    // ============== 就诊人管理 ==============

    @Override
    public List<PatientVO> getPatientList(String userId) {
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getUserId, Long.parseLong(userId))
               .orderByDesc(Patient::getIsDefault)
               .orderByDesc(Patient::getCreateTime);
        
        List<Patient> patients = patientMapper.selectList(wrapper);
        return patients.stream()
                .map(this::convertToPatientVO)
                .collect(Collectors.toList());
    }

    @Override
    public PatientVO getDefaultPatient(String userId) {
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getUserId, Long.parseLong(userId))
               .eq(Patient::getIsDefault, true);
        
        Patient patient = patientMapper.selectOne(wrapper);
        return patient != null ? convertToPatientVO(patient) : null;
    }

    @Override
    public PatientVO addPatient(String userId, PatientDTO patientDTO) {
        Patient patient = new Patient();
        BeanUtils.copyProperties(patientDTO, patient);
        patient.setUserId(Long.parseLong(userId));
        
        // 如果设置为默认，先取消其他默认就诊人
        if (patientDTO.getIsDefault() != null && patientDTO.getIsDefault()) {
            LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Patient::getUserId, Long.parseLong(userId))
                   .eq(Patient::getIsDefault, true);
            List<Patient> existingDefaults = patientMapper.selectList(wrapper);
            for (Patient p : existingDefaults) {
                p.setIsDefault(false);
                patientMapper.updateById(p);
            }
        }
        
        patientMapper.insert(patient);
        return convertToPatientVO(patient);
    }

    @Override
    public PatientVO updatePatient(String userId, String patientId, PatientDTO patientDTO) {
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            return null;
        }

        BeanUtils.copyProperties(patientDTO, patient);
        
        // 如果设置为默认，先取消其他默认就诊人
        if (patientDTO.getIsDefault() != null && patientDTO.getIsDefault()) {
            LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Patient::getUserId, Long.parseLong(userId))
                   .eq(Patient::getIsDefault, true)
                   .ne(Patient::getId, Long.parseLong(patientId));
            List<Patient> existingDefaults = patientMapper.selectList(wrapper);
            for (Patient p : existingDefaults) {
                p.setIsDefault(false);
                patientMapper.updateById(p);
            }
        }
        
        patientMapper.updateById(patient);
        return convertToPatientVO(patient);
    }

    @Override
    public void deletePatient(String userId, String patientId) {
        patientMapper.deleteById(patientId);
        log.info("删除就诊人: {}", patientId);
    }

    @Override
    public void setDefaultPatient(String userId, String patientId) {
        // 先取消所有默认
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getUserId, Long.parseLong(userId))
               .eq(Patient::getIsDefault, true);
        List<Patient> existingDefaults = patientMapper.selectList(wrapper);
        for (Patient p : existingDefaults) {
            p.setIsDefault(false);
            patientMapper.updateById(p);
        }
        
        // 设置新的默认就诊人
        Patient patient = patientMapper.selectById(patientId);
        if (patient != null) {
            patient.setIsDefault(true);
            patientMapper.updateById(patient);
        }
        
        log.info("设置默认就诊人: {}", patientId);
    }

    // ============== 地址管理 ==============

    @Override
    public List<AddressVO> getAddressList(String userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, Long.parseLong(userId))
               .orderByDesc(Address::getIsDefault)
               .orderByDesc(Address::getCreateTime);
        
        List<Address> addresses = addressMapper.selectList(wrapper);
        return addresses.stream()
                .map(this::convertToAddressVO)
                .collect(Collectors.toList());
    }

    @Override
    public AddressVO getAddressDetail(String userId, String addressId) {
        Address address = addressMapper.selectById(addressId);
        return address != null ? convertToAddressVO(address) : null;
    }

    @Override
    public AddressVO getDefaultAddress(String userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, Long.parseLong(userId))
               .eq(Address::getIsDefault, true);
        
        Address address = addressMapper.selectOne(wrapper);
        return address != null ? convertToAddressVO(address) : null;
    }

    @Override
    public AddressVO addAddress(String userId, AddressDTO addressDTO) {
        Address address = new Address();
        BeanUtils.copyProperties(addressDTO, address);
        address.setUserId(Long.parseLong(userId));
        
        // 如果设置为默认，先取消其他默认地址
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault()) {
            LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Address::getUserId, Long.parseLong(userId))
                   .eq(Address::getIsDefault, true);
            List<Address> existingDefaults = addressMapper.selectList(wrapper);
            for (Address a : existingDefaults) {
                a.setIsDefault(false);
                addressMapper.updateById(a);
            }
        }
        
        addressMapper.insert(address);
        return convertToAddressVO(address);
    }

    @Override
    public AddressVO updateAddress(String userId, String addressId, AddressDTO addressDTO) {
        Address address = addressMapper.selectById(addressId);
        if (address == null) {
            return null;
        }

        BeanUtils.copyProperties(addressDTO, address);
        
        // 如果设置为默认，先取消其他默认地址
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault()) {
            LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Address::getUserId, Long.parseLong(userId))
                   .eq(Address::getIsDefault, true)
                   .ne(Address::getId, Long.parseLong(addressId));
            List<Address> existingDefaults = addressMapper.selectList(wrapper);
            for (Address a : existingDefaults) {
                a.setIsDefault(false);
                addressMapper.updateById(a);
            }
        }
        
        addressMapper.updateById(address);
        return convertToAddressVO(address);
    }

    @Override
    public void deleteAddress(String userId, String addressId) {
        addressMapper.deleteById(addressId);
        log.info("删除地址: {}", addressId);
    }

    @Override
    public void setDefaultAddress(String userId, String addressId) {
        // 先取消所有默认
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, Long.parseLong(userId))
               .eq(Address::getIsDefault, true);
        List<Address> existingDefaults = addressMapper.selectList(wrapper);
        for (Address a : existingDefaults) {
            a.setIsDefault(false);
            addressMapper.updateById(a);
        }
        
        // 设置新的默认地址
        Address address = addressMapper.selectById(addressId);
        if (address != null) {
            address.setIsDefault(true);
            addressMapper.updateById(address);
        }
        
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
        List<Coupon> coupons;
        
        if (StringUtils.hasText(status)) {
            coupons = couponMapper.selectByUserIdAndStatus(userId, status);
        } else {
            coupons = couponMapper.selectByUserId(userId);
        }
        
        return coupons.stream()
                .map(this::convertToCouponVO)
                .collect(Collectors.toList());
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
        int p = page != null ? page : 1;
        int s = size != null ? size : 10;

        LambdaQueryWrapper<BrowseHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowseHistory::getUserId, Long.parseLong(userId))
               .orderByDesc(BrowseHistory::getBrowseTime);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<BrowseHistory> historyPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(p, s);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<BrowseHistory> resultPage =
                browseHistoryMapper.selectPage(historyPage, wrapper);

        return resultPage.getRecords().stream()
                .map(this::convertToBrowseHistoryVO)
                .collect(Collectors.toList());
    }

    @Override
    public Long getBrowseHistoryCount(String userId) {
        LambdaQueryWrapper<BrowseHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowseHistory::getUserId, Long.parseLong(userId));
        return browseHistoryMapper.selectCount(wrapper);
    }

    @Override
    public void addBrowseHistory(String userId, BrowseHistoryDTO historyDTO) {
        BrowseHistory history = new BrowseHistory();
        history.setUserId(Long.parseLong(userId));
        history.setProductId(Long.parseLong(historyDTO.getProductId()));
        history.setBrowseTime(LocalDateTime.now());
        browseHistoryMapper.insert(history);
        log.info("添加浏览历史: {}", historyDTO.getProductId());
    }

    @Override
    public void clearBrowseHistory(String userId) {
        LambdaQueryWrapper<BrowseHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowseHistory::getUserId, Long.parseLong(userId));
        browseHistoryMapper.delete(wrapper);
        log.info("清空浏览历史: {}", userId);
    }

    // ============== 转换方法 ==============

    private UserInfoVO convertToUserInfoVO(User user) {
        if (user == null) {
            return createDefaultUserInfo();
        }
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
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

    private PatientVO convertToPatientVO(Patient patient) {
        if (patient == null) {
            return null;
        }
        PatientVO vo = new PatientVO();
        BeanUtils.copyProperties(patient, vo);
        // 手动设置ID（Long转String）
        vo.setId(patient.getId() != null ? String.valueOf(patient.getId()) : null);
        return vo;
    }

    private AddressVO convertToAddressVO(Address address) {
        if (address == null) {
            return null;
        }
        AddressVO vo = new AddressVO();
        BeanUtils.copyProperties(address, vo);
        // 拼接完整地址
        vo.setFullAddress(address.getProvince() + address.getCity() + 
                address.getDistrict() + address.getDetail());
        return vo;
    }

    private CouponVO convertToCouponVO(Coupon coupon) {
        if (coupon == null) {
            return null;
        }
        CouponVO vo = new CouponVO();
        BeanUtils.copyProperties(coupon, vo);
        return vo;
    }

    private BrowseHistoryVO convertToBrowseHistoryVO(BrowseHistory history) {
        if (history == null) {
            return null;
        }
        BrowseHistoryVO vo = new BrowseHistoryVO();
        BeanUtils.copyProperties(history, vo);
        return vo;
    }
}
