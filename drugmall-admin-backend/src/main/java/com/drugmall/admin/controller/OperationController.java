package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/operation")
@RequiredArgsConstructor
public class OperationController {

    @GetMapping("/complaints")
    public Result<Map<String, Object>> getComplaintList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        
        return Result.success(Map.of(
            "list", List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PutMapping("/complaints/{id}/handle")
    public Result<Void> handleComplaint(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @GetMapping({"/feedbacks", "/feedback"})
    public Result<Map<String, Object>> getFeedbackList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        
        return Result.success(Map.of(
            "list", List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PutMapping("/feedbacks/{id}/reply")
    public Result<Void> replyFeedback(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @GetMapping("/symptoms/questions")
    public Result<Map<String, Object>> getSymptomQuestionList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        return Result.success(Map.of(
            "list", List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PostMapping("/symptoms/questions")
    public Result<Void> saveSymptomQuestion(@RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @GetMapping("/ai/configs")
    public Result<Map<String, Object>> getAIConfigList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        return Result.success(Map.of(
            "list", List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PostMapping("/ai/configs")
    public Result<Void> saveAIConfig(@RequestBody Map<String, Object> data) {
        return Result.success();
    }
}
