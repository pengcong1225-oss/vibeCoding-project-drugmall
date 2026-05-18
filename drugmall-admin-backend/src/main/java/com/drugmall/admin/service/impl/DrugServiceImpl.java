package com.drugmall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drugmall.admin.entity.Drug;
import com.drugmall.admin.mapper.DrugMapper;
import com.drugmall.admin.service.DrugService;
import com.drugmall.admin.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DrugServiceImpl implements DrugService {

    private final DrugMapper drugMapper;

    @Override
    public PageResult<Drug> getDrugList(int pageNum, int pageSize, String keyword, Integer status) {
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Drug::getProductName, keyword)
                    .or().like(Drug::getManufacturer, keyword));
        }

        if (status != null) {
            wrapper.eq(Drug::getStatus, status);
        }

        wrapper.eq(Drug::getIsDeleted, 0);
        wrapper.orderByDesc(Drug::getCreateTime);

        Page<Drug> page = new Page<>(pageNum, pageSize);
        Page<Drug> result = drugMapper.selectPage(page, wrapper);

        return PageResult.of(result.getRecords(), result.getTotal(), pageNum, pageSize);
    }

    @Override
    public Drug getDrugDetail(Long id) {
        return drugMapper.selectById(id);
    }

    @Override
    public boolean saveDrug(Drug drug) {
        return drugMapper.insert(drug) > 0;
    }

    @Override
    public boolean updateDrug(Drug drug) {
        return drugMapper.updateById(drug) > 0;
    }

    @Override
    public boolean deleteDrug(Long id) {
        Drug drug = new Drug();
        drug.setId(id);
        drug.setIsDeleted(1);
        return drugMapper.updateById(drug) > 0;
    }
}
