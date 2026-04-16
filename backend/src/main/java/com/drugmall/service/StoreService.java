package com.drugmall.service;

import com.drugmall.vo.StoreDetailVO;
import com.drugmall.vo.StoreDrugVO;
import com.drugmall.vo.StoreListVO;

import java.util.List;

/**
 * 门店/药店服务接口
 *
 * @author DrugMall Team
 * @since 1.0.0
 */
public interface StoreService {

    /**
     * 获取药店列表
     *
     * @return 药店列表
     */
    List<StoreListVO> getStoreList();

    /**
     * 获取药店详情（含药品列表）
     *
     * @param storeId 药店ID
     * @return 药店详情
     */
    StoreDetailVO getStoreDetail(String storeId);

    /**
     * 获取门店药品列表
     *
     * @param storeId 药店ID
     * @return 药品列表
     */
    List<StoreDrugVO> getStoreDrugs(String storeId);
}
