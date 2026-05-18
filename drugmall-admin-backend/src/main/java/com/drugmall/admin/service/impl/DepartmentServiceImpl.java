package com.drugmall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drugmall.admin.entity.Department;
import com.drugmall.admin.mapper.DepartmentMapper;
import com.drugmall.admin.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentMapper departmentMapper;

    @Override
    public List<Department> getDepartmentList() {
        return departmentMapper.selectList(
            new LambdaQueryWrapper<Department>()
                .eq(Department::getIsDeleted, 0)
                .orderByAsc(Department::getSort)
        );
    }

    @Override
    public boolean saveDepartment(Department department) {
        return departmentMapper.insert(department) > 0;
    }

    @Override
    public boolean updateDepartment(Department department) {
        return departmentMapper.updateById(department) > 0;
    }

    @Override
    public boolean deleteDepartment(Long id) {
        Department department = new Department();
        department.setId(id);
        department.setIsDeleted(1);
        return departmentMapper.updateById(department) > 0;
    }
}
