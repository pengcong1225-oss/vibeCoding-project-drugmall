package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import com.drugmall.admin.entity.Department;
import com.drugmall.admin.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public Result<List<Department>> getDepartmentList() {
        return Result.success(departmentService.getDepartmentList());
    }

    @PostMapping
    public Result<Void> createDepartment(@RequestBody Department department) {
        departmentService.saveDepartment(department);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        department.setId(id);
        departmentService.updateDepartment(department);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return Result.success();
    }
}
