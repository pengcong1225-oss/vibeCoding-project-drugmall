package com.drugmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drugmall.dto.DrugQueryDTO;
import com.drugmall.entity.Drug;
import com.drugmall.entity.DrugCategory;
import com.drugmall.entity.DrugReview;
import com.drugmall.entity.DrugFAQ;
import com.drugmall.entity.StoreInventory;
import com.drugmall.entity.Store;
import com.drugmall.entity.ProductSpecification;
import com.drugmall.mapper.DrugMapper;
import com.drugmall.mapper.DrugCategoryMapper;
import com.drugmall.mapper.DrugReviewMapper;
import com.drugmall.mapper.DrugFAQMapper;
import com.drugmall.mapper.StoreInventoryMapper;
import com.drugmall.mapper.StoreMapper;
import com.drugmall.mapper.ProductSpecificationMapper;
import com.drugmall.service.DrugService;
import com.drugmall.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 药品服务实现
 */
@Slf4j
@Service
public class DrugServiceImpl implements DrugService {

    @Autowired
    private DrugMapper drugMapper;

    @Autowired
    private DrugCategoryMapper drugCategoryMapper;

    @Autowired
    private DrugReviewMapper drugReviewMapper;

    @Autowired
    private DrugFAQMapper drugFAQMapper;

    @Autowired
    private StoreInventoryMapper storeInventoryMapper;

    @Autowired
    private StoreMapper storeMapper;

    @Autowired
    private ProductSpecificationMapper specificationMapper;

    @Override
    public PageResultVO<DrugVO> getDrugList(DrugQueryDTO queryDTO) {
        try {
            LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
            
            // 根据分类筛选（包含子分类）
            if (queryDTO.getCategoryId() != null) {
                try {
                    Long categoryId = Long.parseLong(queryDTO.getCategoryId());
                    
                    // 查询该分类及其所有子分类
                    List<Long> categoryIds = getAllSubCategoryIds(categoryId);
                    
                    if (categoryIds.isEmpty()) {
                        // 如果没有子分类，只查询当前分类
                        wrapper.eq(Drug::getCategoryId, categoryId);
                    } else {
                        // 查询当前分类和所有子分类的药品
                        wrapper.in(Drug::getCategoryId, categoryIds);
                    }
                } catch (NumberFormatException e) {
                    log.warn("无效的分类ID: {}", queryDTO.getCategoryId());
                }
            }
            
            // 根据关键词搜索
            if (StringUtils.hasText(queryDTO.getKeyword())) {
                String keyword = queryDTO.getKeyword();
                wrapper.and(w -> w.like(Drug::getProductName, keyword)
                        .or()
                        .like(Drug::getDescription, keyword));
            }
            
            // 根据是否处方药筛选
            if (queryDTO.getIsRx() != null) {
                wrapper.eq(Drug::getIsRx, queryDTO.getIsRx());
            }
            
            // 只查询上架的药品
            wrapper.eq(Drug::getStatus, 1);
            
            // 排序
            if (queryDTO.getSort() != null) {
                switch (queryDTO.getSort()) {
                    case "price_asc":
                        wrapper.orderByAsc(Drug::getPrice);
                        break;
                    case "price_desc":
                        wrapper.orderByDesc(Drug::getPrice);
                        break;
                    case "sales":
                        wrapper.orderByDesc(Drug::getSales);
                        break;
                    case "new":
                        wrapper.orderByDesc(Drug::getCreateTime);
                        break;
                    default:
                        wrapper.orderByDesc(Drug::getSortOrder).orderByDesc(Drug::getCreateTime);
                        break;
                }
            } else {
                wrapper.orderByDesc(Drug::getSortOrder).orderByDesc(Drug::getCreateTime);
            }
            
            // 分页查询
            int page = queryDTO.getPage() != null ? queryDTO.getPage() : 1;
            int size = queryDTO.getSize() != null ? queryDTO.getSize() : 10;
            Page<Drug> drugPage = new Page<>(page, size);
            Page<Drug> resultPage = drugMapper.selectPage(drugPage, wrapper);
            
            // 转换为VO
            List<DrugVO> voList = resultPage.getRecords().stream()
                    .map(this::convertToDrugVO)
                    .collect(Collectors.toList());
            
            return PageResultVO.of(voList, resultPage.getTotal(), page, size);
        } catch (Exception e) {
            log.error("获取药品列表失败，返回空列表", e);
            // 数据库连接失败时返回空列表
            int page = queryDTO.getPage() != null ? queryDTO.getPage() : 1;
            int size = queryDTO.getSize() != null ? queryDTO.getSize() : 10;
            return PageResultVO.of(new ArrayList<>(), 0L, page, size);
        }
    }

    @Override
    public DrugDetailVO getDrugDetail(String drugId) {
        // 查询药品详情
        Drug drug = null;
        try {
            drug = drugMapper.selectById(Long.parseLong(drugId));
        } catch (NumberFormatException e) {
            log.warn("无效的药品ID: {}", drugId);
            return null;
        }
        
        if (drug == null) {
            return null;
        }
        
        DrugVO drugVO = convertToDrugVO(drug);
        
        // 查询药品规格列表
        try {
            Long productId = Long.parseLong(drugId);
            LambdaQueryWrapper<ProductSpecification> specWrapper = new LambdaQueryWrapper<>();
            specWrapper.eq(ProductSpecification::getProductId, productId)
                       .eq(ProductSpecification::getStatus, 1)
                       .orderByAsc(ProductSpecification::getSortOrder);
            
            List<ProductSpecification> specifications = specificationMapper.selectList(specWrapper);
            if (specifications != null && !specifications.isEmpty()) {
                List<DrugSpecificationVO> specVOs = specifications.stream()
                        .map(this::convertToSpecVO)
                        .collect(Collectors.toList());
                drugVO.setSpecifications(specVOs);
            }
        } catch (Exception e) {
            log.warn("查询药品规格失败: {}", e.getMessage());
        }
        
        DrugDetailVO detail = new DrugDetailVO();
        detail.setDrug(drugVO);
        detail.setRelatedDrugs(getRelatedDrugs(drugId, 4));
        detail.setRecommendedDrugs(getRecommendedDrugs(null, 4));
        detail.setReviews(getDrugReviews(drugId, 1, 5).getList());
        detail.setFaqs(getDrugFAQs(drugId));
        return detail;
    }

    @Override
    public List<DrugCategoryVO> getCategories() {
        try {
            // 从数据库查询所有启用的分类
            LambdaQueryWrapper<DrugCategory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DrugCategory::getStatus, 1)
                   .orderByAsc(DrugCategory::getSort);
            
            List<DrugCategory> categories = drugCategoryMapper.selectList(wrapper);
            
            return categories.stream()
                    .map(this::convertToCategoryVO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取分类列表失败，返回空列表", e);
            // 数据库连接失败时返回空列表
            return new ArrayList<>();
        }
    }

    @Override
    public List<DrugVO> getHotDrugs(Integer limit) {
        int lim = limit != null ? limit : 8;
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Drug::getStatus, 1)
               .orderByDesc(Drug::getSales)
               .last("LIMIT " + lim);
        
        List<Drug> drugs = drugMapper.selectList(wrapper);
        return drugs.stream()
                .map(this::convertToDrugVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DrugVO> getNewDrugs(Integer limit) {
        int lim = limit != null ? limit : 8;
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Drug::getStatus, 1)
               .orderByDesc(Drug::getCreateTime)
               .last("LIMIT " + lim);
        
        List<Drug> drugs = drugMapper.selectList(wrapper);
        return drugs.stream()
                .map(this::convertToDrugVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DrugVO> getRecommendedDrugs(String userId, Integer limit) {
        // 返回热门药品作为推荐
        return getHotDrugs(limit);
    }

    @Override
    public List<DrugVO> getRelatedDrugs(String drugId, Integer limit) {
        // 查询当前药品
        Drug currentDrug = null;
        try {
            currentDrug = drugMapper.selectById(Long.parseLong(drugId));
        } catch (NumberFormatException e) {
            log.warn("无效的药品ID: {}", drugId);
            return new ArrayList<>();
        }
        
        if (currentDrug == null) {
            return new ArrayList<>();
        }
        
        int lim = limit != null ? limit : 4;
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Drug::getStatus, 1)
               .ne(Drug::getId, currentDrug.getId())
               .eq(Drug::getCategoryId, currentDrug.getCategoryId())
               .orderByDesc(Drug::getSales)
               .last("LIMIT " + lim);
        
        List<Drug> drugs = drugMapper.selectList(wrapper);
        
        // 如果同分类药品不足，补充其他药品
        if (drugs.size() < lim) {
            List<Long> existingIds = new ArrayList<>();
            existingIds.add(currentDrug.getId());
            for (Drug d : drugs) {
                existingIds.add(d.getId());
            }
            
            LambdaQueryWrapper<Drug> extraWrapper = new LambdaQueryWrapper<>();
            extraWrapper.eq(Drug::getStatus, 1)
                       .notIn(Drug::getId, existingIds)
                       .orderByDesc(Drug::getSales)
                       .last("LIMIT " + (lim - drugs.size()));
            List<Drug> extraDrugs = drugMapper.selectList(extraWrapper);
            drugs.addAll(extraDrugs);
        }
        
        return drugs.stream()
                .map(this::convertToDrugVO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResultVO<DrugReviewVO> getDrugReviews(String drugId, Integer page, Integer size) {
        int p = page != null ? page : 1;
        int s = size != null ? size : 10;
        
        LambdaQueryWrapper<DrugReview> wrapper = new LambdaQueryWrapper<>();
        try {
            wrapper.eq(DrugReview::getProductId, Long.parseLong(drugId));
        } catch (NumberFormatException e) {
            log.warn("无效的药品ID: {}", drugId);
            return PageResultVO.of(new ArrayList<>(), 0L, p, s);
        }
        wrapper.orderByDesc(DrugReview::getCreateTime);
        
        Page<DrugReview> reviewPage = new Page<>(p, s);
        Page<DrugReview> resultPage = drugReviewMapper.selectPage(reviewPage, wrapper);
        
        List<DrugReviewVO> voList = resultPage.getRecords().stream()
                .map(this::convertToDrugReviewVO)
                .collect(Collectors.toList());
        
        return PageResultVO.of(voList, resultPage.getTotal(), p, s);
    }

    @Override
    public List<DrugFAQVO> getDrugFAQs(String drugId) {
        LambdaQueryWrapper<DrugFAQ> wrapper = new LambdaQueryWrapper<>();
        try {
            wrapper.eq(DrugFAQ::getProductId, Long.parseLong(drugId));
        } catch (NumberFormatException e) {
            log.warn("无效的药品ID: {}", drugId);
            return getDefaultFaqs();
        }
        wrapper.eq(DrugFAQ::getStatus, 1)
               .orderByAsc(DrugFAQ::getSort);
        
        List<DrugFAQ> faqs = drugFAQMapper.selectList(wrapper);
        
        // 如果没有找到FAQ数据，返回默认FAQ
        if (faqs.isEmpty()) {
            return getDefaultFaqs();
        }
        
        return faqs.stream()
                .map(this::convertToDrugFAQVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SearchSuggestionVO> getSearchSuggestions(String keyword) {
        List<SearchSuggestionVO> suggestions = new ArrayList<>();

        if (StringUtils.hasText(keyword)) {
            // 从数据库中搜索药品
            LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(Drug::getProductName, keyword)
                   .eq(Drug::getStatus, 1)
                   .orderByDesc(Drug::getSales)
                   .last("LIMIT 5");
            
            List<Drug> drugs = drugMapper.selectList(wrapper);
            
            for (Drug drug : drugs) {
                SearchSuggestionVO s = new SearchSuggestionVO();
                s.setKeyword(drug.getProductName());
                s.setType("drug");
                s.setCount(drug.getSales());
                suggestions.add(s);
            }

            // 添加通用建议
            if (suggestions.size() < 5) {
                SearchSuggestionVO s1 = new SearchSuggestionVO();
                s1.setKeyword(keyword + "胶囊");
                s1.setType("drug");
                s1.setCount(100);
                suggestions.add(s1);

                SearchSuggestionVO s2 = new SearchSuggestionVO();
                s2.setKeyword(keyword + "片");
                s2.setType("drug");
                s2.setCount(80);
                suggestions.add(s2);
            }
        }

        return suggestions;
    }

    @Override
    public List<HotSearchVO> getHotSearches(Integer limit) {
        // 返回热门搜索关键词
        List<HotSearchVO> hotSearches = new ArrayList<>();

        HotSearchVO hot1 = new HotSearchVO();
        hot1.setKeyword("感冒药");
        hot1.setHeat(9999);
        hot1.setIsNew(false);
        hot1.setIsHot(true);
        hotSearches.add(hot1);

        HotSearchVO hot2 = new HotSearchVO();
        hot2.setKeyword("退烧药");
        hot2.setHeat(8888);
        hot2.setIsNew(false);
        hot2.setIsHot(true);
        hotSearches.add(hot2);

        HotSearchVO hot3 = new HotSearchVO();
        hot3.setKeyword("维生素C");
        hot3.setHeat(6666);
        hot3.setIsNew(true);
        hot3.setIsHot(true);
        hotSearches.add(hot3);

        HotSearchVO hot4 = new HotSearchVO();
        hot4.setKeyword("口罩");
        hot4.setHeat(5555);
        hot4.setIsNew(false);
        hot4.setIsHot(false);
        hotSearches.add(hot4);

        HotSearchVO hot5 = new HotSearchVO();
        hot5.setKeyword("血压计");
        hot5.setHeat(4444);
        hot5.setIsNew(true);
        hot5.setIsHot(false);
        hotSearches.add(hot5);

        int max = limit != null ? limit : 10;
        return hotSearches.stream().limit(max).collect(Collectors.toList());
    }

    /**
     * 获取默认FAQ列表
     */
    private List<DrugFAQVO> getDefaultFaqs() {
        List<DrugFAQVO> defaultFaqs = new ArrayList<>();
        
        DrugFAQVO faq1 = new DrugFAQVO();
        faq1.setId("1");
        faq1.setQuestion("这个药品有什么副作用？");
        faq1.setAnswer("常见副作用包括恶心、呕吐、腹泻等，如有不适请及时就医。");
        faq1.setSort(1);
        defaultFaqs.add(faq1);

        DrugFAQVO faq2 = new DrugFAQVO();
        faq2.setId("2");
        faq2.setQuestion("孕妇可以使用吗？");
        faq2.setAnswer("孕妇慎用，请在医生指导下使用。");
        faq2.setSort(2);
        defaultFaqs.add(faq2);

        DrugFAQVO faq3 = new DrugFAQVO();
        faq3.setId("3");
        faq3.setQuestion("需要处方吗？");
        faq3.setAnswer("本品为处方药，需要凭医生处方购买。");
        faq3.setSort(3);
        defaultFaqs.add(faq3);
        
        return defaultFaqs;
    }

    /**
     * 将Drug实体转换为DrugVO
     */
    private DrugVO convertToDrugVO(Drug drug) {
        if (drug == null) {
            return null;
        }
        
        DrugVO vo = new DrugVO();
        vo.setId(String.valueOf(drug.getId()));
        vo.setName(drug.getProductName());
        vo.setGenericName(drug.getGenericName());
        vo.setBrand(drug.getBrand());
        vo.setSpecification(drug.getSpecification());
        vo.setManufacturer(drug.getManufacturer());
        vo.setPrice(drug.getPrice());
        vo.setOriginalPrice(drug.getOriginalPrice());
        vo.setImage(drug.getMainImage());
        vo.setIsRx(drug.getIsRx());
        vo.setIsNationalEssential(drug.getIsNationalEssential());
        vo.setCategoryId(drug.getCategoryId() != null ? String.valueOf(drug.getCategoryId()) : null);
        vo.setStock(drug.getStock());
        vo.setSales(drug.getSales());
        vo.setStatus(drug.getStatus());
        vo.setCreateTime(drug.getCreateTime());
        vo.setDescription(drug.getDescription());
        vo.setUsage(drug.getUsage());
        vo.setDisease(drug.getDisease());
        vo.setContraindications(drug.getContraindications());
        vo.setPrecautions(drug.getPrecautions());
        vo.setAdverseReactions(drug.getAdverseReactions());
        vo.setStorage(drug.getStorage());
        vo.setValidity(drug.getValidity());
        vo.setIngredients(drug.getIngredients());
        vo.setAppearance(drug.getAppearance());
        vo.setDrugInteractions(drug.getDrugInteractions());
        vo.setApprovalNumber(drug.getApprovalNumber());
        vo.setBarCode(drug.getBarCode());
        vo.setMedicalInsuranceCode(drug.getMedicalInsuranceCode());
        vo.setIsLongPrescription(drug.getIsLongPrescription());
        vo.setInsuranceCategory(drug.getInsuranceCategory());
        return vo;
    }

    /**
     * 将DrugCategory实体转换为DrugCategoryVO
     */
    private DrugCategoryVO convertToCategoryVO(DrugCategory category) {
        if (category == null) {
            return null;
        }
        
        DrugCategoryVO vo = new DrugCategoryVO();
        vo.setId(String.valueOf(category.getId()));
        vo.setName(category.getName());
        vo.setParentId(category.getParentId() != null ? String.valueOf(category.getParentId()) : null);
        vo.setIcon(category.getIcon());
        vo.setSort(category.getSort());
        vo.setStatus(category.getStatus());
        return vo;
    }

    /**
     * 将ProductSpecification实体转换为DrugSpecificationVO
     */
    private DrugSpecificationVO convertToSpecVO(ProductSpecification spec) {
        if (spec == null) {
            return null;
        }
        
        DrugSpecificationVO vo = new DrugSpecificationVO();
        vo.setId(spec.getId());
        vo.setSpecName(spec.getSpecName());
        vo.setSpecCode(spec.getSpecCode());
        vo.setPrice(spec.getPrice());
        vo.setOriginalPrice(spec.getOriginalPrice());
        vo.setStock(spec.getStock());
        vo.setBarCode(spec.getBarCode());
        vo.setIsDefault(spec.getIsDefault());
        return vo;
    }

    /**
     * 将DrugReview实体转换为DrugReviewVO
     */
    private DrugReviewVO convertToDrugReviewVO(DrugReview review) {
        if (review == null) {
            return null;
        }
        
        DrugReviewVO vo = new DrugReviewVO();
        vo.setId(String.valueOf(review.getId()));
        vo.setUserId(String.valueOf(review.getUserId()));
        vo.setUserName(review.getUserName());
        vo.setUserAvatar(review.getUserAvatar());
        vo.setDrugId(String.valueOf(review.getProductId()));
        vo.setOrderId(String.valueOf(review.getOrderId()));
        vo.setRating(review.getRating());
        vo.setContent(review.getContent());
        
        // 解析图片JSON数组
        if (review.getImages() != null && !review.getImages().isEmpty()) {
            try {
                vo.setImages(Arrays.asList(review.getImages().split(",")));
            } catch (Exception e) {
                vo.setImages(new ArrayList<>());
            }
        } else {
            vo.setImages(new ArrayList<>());
        }
        
        // 解析标签JSON数组
        if (review.getTags() != null && !review.getTags().isEmpty()) {
            try {
                vo.setTags(Arrays.asList(review.getTags().split(",")));
            } catch (Exception e) {
                vo.setTags(new ArrayList<>());
            }
        } else {
            vo.setTags(new ArrayList<>());
        }
        
        vo.setIsAnonymous(review.getIsAnonymous());
        vo.setIsRecommended(review.getIsRecommended());
        vo.setHelpfulCount(review.getHelpfulCount());
        vo.setCreateTime(review.getCreateTime());
        
        return vo;
    }

    /**
     * 将DrugFAQ实体转换为DrugFAQVO
     */
    private DrugFAQVO convertToDrugFAQVO(DrugFAQ faq) {
        if (faq == null) {
            return null;
        }
        
        DrugFAQVO vo = new DrugFAQVO();
        vo.setId(String.valueOf(faq.getId()));
        vo.setQuestion(faq.getQuestion());
        vo.setAnswer(faq.getAnswer());
        vo.setSort(faq.getSort());
        return vo;
    }

    /**
     * 递归获取分类及其所有子分类的ID列表
     */
    private List<Long> getAllSubCategoryIds(Long categoryId) {
        List<Long> result = new ArrayList<>();
        result.add(categoryId);
        
        // 查询直接子分类
        LambdaQueryWrapper<DrugCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DrugCategory::getParentId, categoryId)
               .eq(DrugCategory::getStatus, 1);
        
        List<DrugCategory> subCategories = drugCategoryMapper.selectList(wrapper);
        
        // 递归查询子分类的子分类
        for (DrugCategory subCategory : subCategories) {
            result.addAll(getAllSubCategoryIds(subCategory.getId()));
        }
        
        return result;
    }

    @Override
    public List<DrugStoreVO> getDrugStores(String drugId) {
        try {
            Long productId = Long.parseLong(drugId);
            
            // 查询该药品的所有门店库存
            LambdaQueryWrapper<StoreInventory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StoreInventory::getProductId, productId)
                   .eq(StoreInventory::getIsAvailable, true)
                   .orderByAsc(StoreInventory::getPrice);
            
            List<StoreInventory> inventories = storeInventoryMapper.selectList(wrapper);
            
            if (inventories.isEmpty()) {
                return new ArrayList<>();
            }
            
            // 转换为VO
            return inventories.stream().map(inventory -> {
                DrugStoreVO vo = new DrugStoreVO();
                vo.setId(inventory.getStoreId());
                vo.setPrice(inventory.getPrice());
                vo.setOriginalPrice(inventory.getOriginalPrice());
                vo.setStock(inventory.getStock());
                vo.setIsAvailable(inventory.getIsAvailable());
                
                // 查询门店信息
                Store store = storeMapper.selectById(inventory.getStoreId());
                if (store != null) {
                    vo.setName(store.getStoreName());
                    vo.setLogo(store.getLogo());
                    vo.setRating(store.getRating() != null ? store.getRating().doubleValue() : 5.0);
                    vo.setSales(store.getMonthlySales());
                    
                    // 生成距离和配送时间（模拟数据）
                    double distance = Math.random() * 5 + 0.5; // 0.5-5.5km
                    vo.setDistance(String.format("%.1fkm", distance));
                    
                    int deliveryTime = (int)(distance * 10 + 15); // 根据距离计算配送时间
                    vo.setDelivery(deliveryTime + "分钟达");
                    
                    // 生成标签
                    List<String> tags = new ArrayList<>();
                    if (store.getIsInsurance() != null && store.getIsInsurance() == 1) {
                        tags.add("医保定点");
                    }
                    if (store.getIs24hours() != null && store.getIs24hours() == 1) {
                        tags.add("24小时");
                    }
                    if (store.getIsSelfOperated() != null && store.getIsSelfOperated() == 1) {
                        tags.add("自营");
                    }
                    if (tags.isEmpty()) {
                        tags.add("正品保障");
                    }
                    vo.setTags(tags);
                }
                
                return vo;
            }).collect(Collectors.toList());
            
        } catch (NumberFormatException e) {
            log.warn("无效的药品ID: {}", drugId);
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取药品在售门店失败", e);
            return new ArrayList<>();
        }
    }
}
