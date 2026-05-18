package com.drugmall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drugmall.admin.entity.Doctor;
import com.drugmall.admin.mapper.DoctorMapper;
import com.drugmall.admin.service.DoctorService;
import com.drugmall.admin.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorMapper doctorMapper;

    @Override
    public PageResult<Doctor> getDoctorList(int pageNum, int pageSize, String keyword, Integer status) {
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Doctor::getName, keyword)
                    .or().like(Doctor::getHospital, keyword)
                    .or().like(Doctor::getDepartment, keyword));
        }

        if (status != null) {
            wrapper.eq(Doctor::getStatus, status);
        }

        wrapper.eq(Doctor::getIsDeleted, 0);
        wrapper.orderByDesc(Doctor::getCreateTime);

        Page<Doctor> page = new Page<>(pageNum, pageSize);
        Page<Doctor> result = doctorMapper.selectPage(page, wrapper);

        return PageResult.of(result.getRecords(), result.getTotal(), pageNum, pageSize);
    }

    @Override
    public Doctor getDoctorDetail(Long id) {
        return doctorMapper.selectById(id);
    }
}
