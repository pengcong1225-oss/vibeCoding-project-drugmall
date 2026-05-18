package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.dto.DoctorLoginDTO;
import com.drugmall.dto.DoctorProfileUpdateDTO;
import com.drugmall.service.DoctorService;
import com.drugmall.vo.DoctorInfoVO;
import com.drugmall.vo.DoctorStatsVO;
import com.drugmall.vo.DoctorTodoCountVO;
import com.drugmall.vo.LoginResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 医生管理控制器
 */
@RestController
@RequestMapping("/v1/doctor")
@Tag(name = "医生管理", description = "医生登录、资料、统计相关接口")
@Validated
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    private static final String CURRENT_DOCTOR_ID = "DOC001";

    @PostMapping("/login")
    @Operation(summary = "医生登录", description = "使用手机号和密码登录")
    public Result<LoginResultVO> login(@Valid @RequestBody DoctorLoginDTO loginDTO) {
        return Result.success(doctorService.login(loginDTO));
    }

    @GetMapping("/profile")
    @Operation(summary = "获取医生信息", description = "获取当前登录医生信息")
    public Result<DoctorInfoVO> getProfile() {
        return Result.success(doctorService.getProfile(CURRENT_DOCTOR_ID));
    }

    @PutMapping("/profile")
    @Operation(summary = "更新医生信息", description = "更新当前登录医生信息")
    public Result<DoctorInfoVO> updateProfile(@RequestBody DoctorProfileUpdateDTO updateDTO) {
        return Result.success(doctorService.updateProfile(CURRENT_DOCTOR_ID, updateDTO));
    }

    @GetMapping("/stats")
    @Operation(summary = "获取今日统计", description = "获取今日问诊统计和收入")
    public Result<DoctorStatsVO> getStats() {
        return Result.success(doctorService.getStats(CURRENT_DOCTOR_ID));
    }

    @GetMapping("/todo-count")
    @Operation(summary = "获取待办事项", description = "获取待办和未读消息数量")
    public Result<DoctorTodoCountVO> getTodoCount() {
        return Result.success(doctorService.getTodoCount(CURRENT_DOCTOR_ID));
    }

    @GetMapping("/prescription-count")
    @Operation(summary = "获取待审核处方数量", description = "获取医生待审核的处方数量")
    public Result<Map<String, Integer>> getPendingPrescriptionCount() {
        int count = doctorService.countPendingPrescriptions(CURRENT_DOCTOR_ID);
        Map<String, Integer> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }

    @GetMapping("/schedule")
    @Operation(summary = "获取排班信息", description = "获取医生排班信息")
    public Result<List<Object>> getSchedule() {
        return Result.success(doctorService.getSchedule(CURRENT_DOCTOR_ID));
    }

    @PutMapping("/schedule")
    @Operation(summary = "更新排班信息", description = "更新医生排班信息")
    public Result<Boolean> updateSchedule(
            @Parameter(description = "排班数据") @RequestBody List<Object> schedule) {
        return Result.success(true);
    }

    @GetMapping("/license")
    @Operation(summary = "获取执业信息", description = "获取医生执业资质信息")
    public Result<Object> getLicense() {
        return Result.success(doctorService.getLicense(CURRENT_DOCTOR_ID));
    }

    @PutMapping("/license")
    @Operation(summary = "更新执业信息", description = "更新医生执业资质信息")
    public Result<Boolean> updateLicense(
            @Parameter(description = "执业信息") @RequestBody Object license) {
        return Result.success(true);
    }
}
