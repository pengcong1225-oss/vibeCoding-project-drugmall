package com.drugmall.admin.service;

import com.drugmall.admin.entity.Doctor;
import com.drugmall.admin.vo.PageResult;

public interface DoctorService {
    PageResult<Doctor> getDoctorList(int pageNum, int pageSize, String keyword, Integer status);
    Doctor getDoctorDetail(Long id);
}
