package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/home")
@RequiredArgsConstructor
public class HomeConfigController {

    @GetMapping("/global")
    public Result<Map<String, Object>> getHomeGlobalConfig() {
        return Result.success(Map.of(
            "siteName", "DrugMall",
            "siteLogo", "",
            "siteDescription", "互联网药品电商平台",
            "contactPhone", "400-123-4567",
            "contactEmail", "service@drugmall.com"
        ));
    }

    @PutMapping("/global")
    public Result<Void> saveHomeGlobalConfig(@RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @GetMapping("/tabs")
    public Result<Map<String, Object>> getTabList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        return Result.success(Map.of(
            "list", List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PostMapping("/tabs")
    public Result<Void> createTab(@RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @PutMapping("/tabs/{id}")
    public Result<Void> updateTab(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @DeleteMapping("/tabs/{id}")
    public Result<Void> deleteTab(@PathVariable Long id) {
        return Result.success();
    }

    @PutMapping("/tabs/sort")
    public Result<Void> updateTabSort(@RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @GetMapping("/sections")
    public Result<Map<String, Object>> getSectionList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        return Result.success(Map.of(
            "list", List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PostMapping("/sections")
    public Result<Void> createSection(@RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @PutMapping("/sections/{id}")
    public Result<Void> updateSection(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @DeleteMapping("/sections/{id}")
    public Result<Void> deleteSection(@PathVariable Long id) {
        return Result.success();
    }

    @PostMapping("/sections/{id}/copy")
    public Result<Void> copySection(@PathVariable Long id) {
        return Result.success();
    }

    @GetMapping("/kingkong")
    public Result<Map<String, Object>> getKingKongList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        return Result.success(Map.of(
            "list", List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PostMapping("/kingkong")
    public Result<Void> createKingKong(@RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @PutMapping("/kingkong/{id}")
    public Result<Void> updateKingKong(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @DeleteMapping("/kingkong/{id}")
    public Result<Void> deleteKingKong(@PathVariable Long id) {
        return Result.success();
    }

    @GetMapping("/adslots")
    public Result<Map<String, Object>> getAdSlotList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        return Result.success(Map.of(
            "list", List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PostMapping("/adslots")
    public Result<Void> createAdSlot(@RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @PutMapping("/adslots/{id}")
    public Result<Void> updateAdSlot(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @DeleteMapping("/adslots/{id}")
    public Result<Void> deleteAdSlot(@PathVariable Long id) {
        return Result.success();
    }

    @GetMapping("/banners")
    public Result<Map<String, Object>> getHomeBannerList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        return Result.success(Map.of(
            "list", List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PostMapping("/banners")
    public Result<Void> createHomeBanner(@RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @PutMapping("/banners/{id}")
    public Result<Void> updateHomeBanner(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @DeleteMapping("/banners/{id}")
    public Result<Void> deleteHomeBanner(@PathVariable Long id) {
        return Result.success();
    }

    @GetMapping("/releases")
    public Result<Map<String, Object>> getReleaseList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        return Result.success(Map.of(
            "list", List.of(),
            "total", 0,
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PostMapping("/releases/publish")
    public Result<Void> publishRelease(@RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @PutMapping("/releases/{id}/rollback")
    public Result<Void> rollbackRelease(@PathVariable Long id) {
        return Result.success();
    }

    @DeleteMapping("/releases/{id}")
    public Result<Void> deleteRelease(@PathVariable Long id) {
        return Result.success();
    }

    @GetMapping("/releases/summary")
    public Result<Map<String, Object>> getReleaseSummary() {
        return Result.success(Map.of(
            "totalReleases", 0,
            "currentVersion", "v1.0.0"
        ));
    }
}
