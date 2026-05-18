package com.drugmall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drugmall.admin.common.Result;
import com.drugmall.admin.entity.Category;
import com.drugmall.admin.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryMapper categoryMapper;

    @GetMapping
    public Result<List<Category>> getCategoryList() {
        List<Category> categories = categoryMapper.selectList(
            new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort)
        );
        return Result.success(categories);
    }

    @PostMapping
    public Result<Void> createCategory(@RequestBody Category category) {
        categoryMapper.insert(category);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryMapper.updateById(category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryMapper.deleteById(id);
        return Result.success();
    }
}
