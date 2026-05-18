package com.drugmall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drugmall.admin.common.Result;
import com.drugmall.admin.entity.Brand;
import com.drugmall.admin.mapper.BrandMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandMapper brandMapper;

    @GetMapping
    public Result<Map<String, Object>> getBrandList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        
        LambdaQueryWrapper<Brand> wrapper = new LambdaQueryWrapper<Brand>()
            .eq(Brand::getIsDeleted, 0);
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Brand::getName, keyword);
        }
        wrapper.orderByAsc(Brand::getSortOrder);
        
        Page<Brand> page = new Page<>(pageNum, pageSize);
        Page<Brand> result = brandMapper.selectPage(page, wrapper);
        
        return Result.success(Map.of(
            "list", result.getRecords(),
            "total", result.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        ));
    }

    @PostMapping
    public Result<Void> createBrand(@RequestBody Brand brand) {
        brand.setStatus(1);
        brand.setIsDeleted(0);
        brand.setSortOrder(0);
        brandMapper.insert(brand);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateBrand(@PathVariable Long id, @RequestBody Brand brand) {
        brand.setId(id);
        brandMapper.updateById(brand);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBrand(@PathVariable Long id) {
        Brand brand = new Brand();
        brand.setId(id);
        brand.setIsDeleted(1);
        brandMapper.updateById(brand);
        return Result.success();
    }
}
