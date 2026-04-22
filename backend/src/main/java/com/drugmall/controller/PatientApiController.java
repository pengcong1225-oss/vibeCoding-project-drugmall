package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.service.UserService;
import com.drugmall.vo.PatientVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/patient")
@Tag(name = "患者端通用", description = "患者端通用接口")
@Validated
public class PatientApiController {

    @Autowired
    private UserService userService;

    private static final String CURRENT_USER_ID = "1";

    @GetMapping("/profiles")
    @Operation(summary = "获取患者档案列表", description = "获取当前用户的患者档案，用于问诊选择")
    public Result<List<PatientVO>> getPatientProfiles() {
        return Result.success(userService.getPatientList(CURRENT_USER_ID));
    }
}
