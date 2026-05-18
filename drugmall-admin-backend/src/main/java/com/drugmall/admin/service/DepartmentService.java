package com.drugmall.admin.service;

import com.drugmall.admin.entity.Department;

import java.util.List;

public interface DepartmentService {
    List<Department> getDepartmentList();
    boolean saveDepartment(Department department);
    boolean updateDepartment(Department department);
    boolean deleteDepartment(Long id);
}
