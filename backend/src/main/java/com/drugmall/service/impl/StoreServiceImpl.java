package com.drugmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drugmall.entity.*;
import com.drugmall.mapper.*;
import com.drugmall.service.StoreService;
import com.drugmall.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreMapper storeMapper;

    @Autowired
    private StoreTagMapper storeTagMapper;

    @Autowired
    private StoreCertificationMapper storeCertificationMapper;

    @Autowired
    private StorePromiseMapper storePromiseMapper;

    @Autowired
    private StoreInventoryMapper storeInventoryMapper;

    @Autowired
    private DrugMapper drugMapper;

    @Override
    public List<StoreListVO> getStoreList() {
        log.info("获取药店列表 from database");
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getStatus, 1)
               .orderByAsc(Store::getSortOrder);
        List<Store> stores = storeMapper.selectList(wrapper);

        return stores.stream()
                .map(this::convertToStoreListVO)
                .collect(Collectors.toList());
    }

    @Override
    public StoreDetailVO getStoreDetail(String storeId) {
        log.info("获取药店详情 from database: storeId={}", storeId);
        Long id;
        try {
            id = Long.parseLong(storeId);
        } catch (NumberFormatException e) {
            log.warn("无效的门店ID: {}", storeId);
            return null;
        }

        Store store = storeMapper.selectById(id);
        if (store == null || store.getStatus() != 1) {
            return null;
        }

        StoreDetailVO detail = convertToStoreDetailVO(store);
        detail.setDrugs(getStoreDrugs(storeId));
        return detail;
    }

    @Override
    public List<StoreDrugVO> getStoreDrugs(String storeId) {
        log.info("获取门店药品 from database: storeId={}", storeId);
        Long id;
        try {
            id = Long.parseLong(storeId);
        } catch (NumberFormatException e) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<StoreInventory> invWrapper = new LambdaQueryWrapper<>();
        invWrapper.eq(StoreInventory::getStoreId, id)
                  .eq(StoreInventory::getIsAvailable, true);
        List<StoreInventory> inventories = storeInventoryMapper.selectList(invWrapper);

        if (inventories.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> productIds = inventories.stream()
                .map(StoreInventory::getProductId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<Drug> drugWrapper = new LambdaQueryWrapper<>();
        drugWrapper.in(Drug::getId, productIds)
                   .eq(Drug::getStatus, 1);
        List<Drug> drugs = drugMapper.selectList(drugWrapper);

        return inventories.stream()
                .map(inv -> {
                    Drug drug = drugs.stream()
                            .filter(d -> d.getId().equals(inv.getProductId()))
                            .findFirst()
                            .orElse(null);
                    if (drug == null) {
                        return null;
                    }
                    return convertToStoreDrugVO(drug, inv);
                })
                .filter(v -> v != null)
                .collect(Collectors.toList());
    }

    private StoreListVO convertToStoreListVO(Store store) {
        List<StoreTag> tags = storeTagMapper.selectByStoreId(store.getId());
        List<StoreInventory> inventories = getStoreInventories(String.valueOf(store.getId()));
        List<Drug> products = getProductsForStore(inventories);

        List<StoreListVO.TagItem> tagItems = tags.stream()
                .map(t -> StoreListVO.TagItem.builder()
                        .text(t.getTagText())
                        .type(t.getTagType())
                        .build())
                .collect(Collectors.toList());

        List<StoreListVO.SimpleDrug> simpleDrugs = products.stream()
                .limit(3)
                .map(d -> StoreListVO.SimpleDrug.builder()
                        .id(String.valueOf(d.getId()))
                        .name(d.getProductName())
                        .price(d.getPrice())
                        .bgColor("#E3F2FD")
                        .build())
                .collect(Collectors.toList());

        return StoreListVO.builder()
                .id(String.valueOf(store.getId()))
                .name(store.getStoreName())
                .logo(store.getLogo())
                .logoText(store.getLogoText())
                .logoColor(store.getLogoColor())
                .rating(store.getRating() != null ? store.getRating().doubleValue() : 0.0)
                .monthlySales(store.getMonthlySales())
                .distance(0.8)
                .deliveryTime(store.getDeliveryTime())
                .address(store.getAddress())
                .phone(store.getPhone())
                .isOpen(store.getIsOpen() != null && store.getIsOpen() == 1)
                .businessHours(store.getIs24hours() != null && store.getIs24hours() == 1 ? "24小时营业" : store.getBusinessHours())
                .tags(tagItems)
                .products(simpleDrugs)
                .build();
    }

    private StoreDetailVO convertToStoreDetailVO(Store store) {
        List<StoreTag> tags = storeTagMapper.selectByStoreId(store.getId());
        List<StoreCertification> certifications = storeCertificationMapper.selectByStoreId(store.getId());
        List<StorePromise> promises = storePromiseMapper.selectByStoreId(store.getId());

        List<StoreListVO.TagItem> tagItems = tags.stream()
                .map(t -> StoreListVO.TagItem.builder()
                        .text(t.getTagText())
                        .type(t.getTagType())
                        .build())
                .collect(Collectors.toList());

        List<String> certNames = certifications.stream()
                .map(StoreCertification::getCertName)
                .collect(Collectors.toList());

        List<String> promiseTexts = promises.stream()
                .map(StorePromise::getPromiseText)
                .collect(Collectors.toList());

        return StoreDetailVO.builder()
                .id(String.valueOf(store.getId()))
                .name(store.getStoreName())
                .logo(store.getLogo())
                .logoText(store.getLogoText())
                .logoColor(store.getLogoColor())
                .rating(store.getRating() != null ? store.getRating().doubleValue() : 0.0)
                .monthlySales(store.getMonthlySales())
                .distance(0.8)
                .deliveryTime(store.getDeliveryTime())
                .tags(tagItems)
                .address(store.getAddress())
                .phone(store.getPhone())
                .isOpen(store.getIsOpen() != null && store.getIsOpen() == 1)
                .businessHours(store.getIs24hours() != null && store.getIs24hours() == 1 ? "24小时营业" : store.getBusinessHours())
                .description(store.getDescription())
                .businessScope(store.getBusinessScope())
                .certifications(certNames)
                .servicePromises(promiseTexts)
                .build();
    }

    private StoreDrugVO convertToStoreDrugVO(Drug drug, StoreInventory inv) {
        DrugCategory category = getCategoryById(drug.getCategoryId());

        return StoreDrugVO.builder()
                .id(String.valueOf(drug.getId()))
                .name(drug.getProductName())
                .specification(drug.getSpecification())
                .manufacturer(drug.getManufacturer())
                .price(inv.getPrice())
                .originalPrice(inv.getOriginalPrice())
                .stock(inv.getStock())
                .isRx(drug.getIsRx())
                .approvalNumber(drug.getApprovalNumber())
                .image("")
                .imageColor("#E3F2FD")
                .imageText(drug.getProductName().substring(0, Math.min(2, drug.getProductName().length())))
                .sales(drug.getSales() != null ? drug.getSales() : 0)
                .discount(inv.getDiscount() != null ? inv.getDiscount() : 0)
                .deliveryTime(25)
                .category(category != null ? category.getName() : "")
                .tags(drug.getIsRx() != null && drug.getIsRx() ?
                        java.util.Arrays.asList("处方药", "热销") :
                        java.util.Arrays.asList("OTC", "热销"))
                .build();
    }

    private List<StoreInventory> getStoreInventories(String storeId) {
        Long id;
        try {
            id = Long.parseLong(storeId);
        } catch (NumberFormatException e) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<StoreInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoreInventory::getStoreId, id)
               .eq(StoreInventory::getIsAvailable, true);
        return storeInventoryMapper.selectList(wrapper);
    }

    private List<Drug> getProductsForStore(List<StoreInventory> inventories) {
        if (inventories.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> productIds = inventories.stream()
                .map(StoreInventory::getProductId)
                .collect(Collectors.toList());
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Drug::getId, productIds)
               .eq(Drug::getStatus, 1);
        return drugMapper.selectList(wrapper);
    }

    private DrugCategory getCategoryById(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        LambdaQueryWrapper<DrugCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DrugCategory::getId, categoryId);
        return drugCategoryMapper.selectOne(wrapper);
    }

    @Autowired(required = false)
    private DrugCategoryMapper drugCategoryMapper;
}
