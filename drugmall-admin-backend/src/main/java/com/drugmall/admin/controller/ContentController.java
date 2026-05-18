package com.drugmall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drugmall.admin.common.Result;
import com.drugmall.admin.entity.Banner;
import com.drugmall.admin.entity.Notice;
import com.drugmall.admin.mapper.BannerMapper;
import com.drugmall.admin.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/content")
@RequiredArgsConstructor
public class ContentController {

    private final BannerMapper bannerMapper;
    private final NoticeMapper noticeMapper;

    // ========== Banner ==========
    @GetMapping("/banners")
    public Result<Map<String, Object>> getBanners() {
        List<Banner> list = bannerMapper.selectList(
            new LambdaQueryWrapper<Banner>()
                .eq(Banner::getIsDeleted, 0)
                .orderByAsc(Banner::getSort)
        );
        return Result.success(Map.of("list", list, "total", list.size()));
    }

    @PostMapping("/banners")
    public Result<Void> createBanner(@RequestBody Banner banner) {
        banner.setSort(0);
        banner.setStatus(1);
        banner.setIsDeleted(0);
        bannerMapper.insert(banner);
        return Result.success();
    }

    @PutMapping("/banners/{id}")
    public Result<Void> updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        banner.setId(id);
        bannerMapper.updateById(banner);
        return Result.success();
    }

    @DeleteMapping("/banners/{id}")
    public Result<Void> deleteBanner(@PathVariable Long id) {
        Banner banner = new Banner();
        banner.setId(id);
        banner.setIsDeleted(1);
        bannerMapper.updateById(banner);
        return Result.success();
    }

    @PatchMapping("/banners/{id}/status")
    public Result<Void> updateBannerStatus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Banner banner = new Banner();
        banner.setId(id);
        banner.setStatus((Integer) body.get("status"));
        bannerMapper.updateById(banner);
        return Result.success();
    }

    @PutMapping("/banners/sort")
    public Result<Void> updateBannerSort(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sortList = (List<Map<String, Object>>) body.get("sortList");
        for (Map<String, Object> item : sortList) {
            Banner banner = new Banner();
            banner.setId(((Number) item.get("id")).longValue());
            banner.setSort(((Number) item.get("sort")).intValue());
            bannerMapper.updateById(banner);
        }
        return Result.success();
    }

    // ========== Notice ==========
    @GetMapping("/notices")
    public Result<Map<String, Object>> getNotices(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<Notice>()
            .eq(Notice::getIsDeleted, 0);
        
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Notice::getType, type);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Notice::getTitle, keyword)
                   .or()
                   .like(Notice::getContent, keyword);
        }
        wrapper.orderByDesc(Notice::getCreateTime);
        
        Page<Notice> page = new Page<>(pageNum, pageSize);
        Page<Notice> result = noticeMapper.selectPage(page, wrapper);
        
        return Result.success(Map.of(
            "list", result.getRecords(),
            "total", result.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PostMapping("/notices")
    public Result<Void> createNotice(@RequestBody Notice notice) {
        notice.setStatus(1);
        notice.setIsDeleted(0);
        noticeMapper.insert(notice);
        return Result.success();
    }

    @PutMapping("/notices/{id}")
    public Result<Void> updateNotice(@PathVariable Long id, @RequestBody Notice notice) {
        notice.setId(id);
        noticeMapper.updateById(notice);
        return Result.success();
    }

    @DeleteMapping("/notices/{id}")
    public Result<Void> deleteNotice(@PathVariable Long id) {
        Notice notice = new Notice();
        notice.setId(id);
        notice.setIsDeleted(1);
        noticeMapper.updateById(notice);
        return Result.success();
    }

    @PatchMapping("/notices/{id}/top")
    public Result<Void> toggleNoticeTop(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        // TODO: 实现置顶功能，需要在notice表中添加is_top字段
        return Result.success();
    }

    @GetMapping("/notices/stats")
    public Result<Map<String, Integer>> getNoticeStats() {
        long total = noticeMapper.selectCount(new LambdaQueryWrapper<Notice>().eq(Notice::getIsDeleted, 0));
        long published = noticeMapper.selectCount(
            new LambdaQueryWrapper<Notice>().eq(Notice::getIsDeleted, 0).eq(Notice::getStatus, 1)
        );
        long draft = noticeMapper.selectCount(
            new LambdaQueryWrapper<Notice>().eq(Notice::getIsDeleted, 0).eq(Notice::getStatus, 0)
        );
        
        return Result.success(Map.of(
            "total", (int) total,
            "published", (int) published,
            "top", 0,
            "draft", (int) draft
        ));
    }
}
