package com.drugmall.admin.service;

import com.drugmall.admin.entity.Drug;
import com.drugmall.admin.vo.PageResult;

public interface DrugService {
    PageResult<Drug> getDrugList(int pageNum, int pageSize, String keyword, Integer status);
    Drug getDrugDetail(Long id);
    boolean saveDrug(Drug drug);
    boolean updateDrug(Drug drug);
    boolean deleteDrug(Long id);
}
