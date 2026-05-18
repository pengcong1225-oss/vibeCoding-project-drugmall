package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import com.drugmall.admin.entity.Drug;
import com.drugmall.admin.service.DrugService;
import com.drugmall.admin.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductController {

    private final DrugService drugService;

    @GetMapping
    public Result<PageResult<Drug>> getProductList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        PageResult<Drug> result = drugService.getDrugList(pageNum, pageSize, keyword, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Drug> getProductDetail(@PathVariable Long id) {
        Drug drug = drugService.getDrugDetail(id);
        if (drug == null) {
            return Result.error(404, "药品不存在");
        }
        return Result.success(drug);
    }

    @PostMapping
    public Result<Void> createProduct(@RequestBody Drug drug) {
        boolean success = drugService.saveDrug(drug);
        if (!success) {
            return Result.error(500, "创建失败");
        }
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateProduct(@PathVariable Long id, @RequestBody Drug drug) {
        drug.setId(id);
        boolean success = drugService.updateDrug(drug);
        if (!success) {
            return Result.error(500, "更新失败");
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        boolean success = drugService.deleteDrug(id);
        if (!success) {
            return Result.error(500, "删除失败");
        }
        return Result.success();
    }

    @PatchMapping("/{id}/status")
    public Result<Void> updateProductStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        Drug drug = new Drug();
        drug.setId(id);
        drug.setStatus(request.getStatus());
        boolean success = drugService.updateDrug(drug);
        if (!success) {
            return Result.error(500, "更新失败");
        }
        return Result.success();
    }

    static class StatusUpdateRequest {
        private Integer status;
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
    }
}
