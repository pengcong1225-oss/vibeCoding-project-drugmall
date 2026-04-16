package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.config.MockDataService;
import com.drugmall.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 搜索控制器
 * 提供药品搜索、搜索建议、热门搜索、筛选条件等接口
 */
@Slf4j
@RestController
@RequestMapping("/v1/search")
@Tag(name = "搜索服务", description = "药品搜索相关接口")
public class SearchController {

    @Autowired
    private MockDataService mockDataService;

    /**
     * 搜索药品
     */
    @GetMapping("/drugs")
    @Operation(summary = "搜索药品", description = "根据关键词、分类、品牌等条件搜索药品")
    public Result<PageResultVO<DrugVO>> searchDrugs(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "分类ID") @RequestParam(required = false) String categoryId,
            @Parameter(description = "品牌ID") @RequestParam(required = false) String brandId,
            @Parameter(description = "最低价格") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "最高价格") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "是否处方药") @RequestParam(required = false) Boolean isRx,
            @Parameter(description = "排序方式") @RequestParam(defaultValue = "default") String sort,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {

        List<DrugVO> allDrugs = getMockDrugs();

        // 根据关键词筛选
        if (keyword != null && !keyword.isEmpty()) {
            String kw = keyword.toLowerCase();
            allDrugs = allDrugs.stream()
                    .filter(d -> d.getName().toLowerCase().contains(kw) ||
                            (d.getDisease() != null && d.getDisease().toLowerCase().contains(kw)) ||
                            (d.getManufacturer() != null && d.getManufacturer().toLowerCase().contains(kw)))
                    .collect(Collectors.toList());
        }

        // 根据分类筛选
        if (categoryId != null && !categoryId.isEmpty()) {
            allDrugs = allDrugs.stream()
                    .filter(d -> categoryId.equals(d.getCategoryId()))
                    .collect(Collectors.toList());
        }

        // 根据价格区间筛选
        if (minPrice != null) {
            allDrugs = allDrugs.stream()
                    .filter(d -> d.getPrice().compareTo(minPrice) >= 0)
                    .collect(Collectors.toList());
        }
        if (maxPrice != null) {
            allDrugs = allDrugs.stream()
                    .filter(d -> d.getPrice().compareTo(maxPrice) <= 0)
                    .collect(Collectors.toList());
        }

        // 根据是否处方药筛选
        if (isRx != null) {
            allDrugs = allDrugs.stream()
                    .filter(d -> isRx.equals(d.getIsRx()))
                    .collect(Collectors.toList());
        }

        // 排序
        if (sort != null) {
            switch (sort) {
                case "price_asc":
                    allDrugs.sort(Comparator.comparing(DrugVO::getPrice));
                    break;
                case "price_desc":
                    allDrugs.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
                    break;
                case "sales":
                    allDrugs.sort((a, b) -> b.getSales() - a.getSales());
                    break;
                case "new":
                    allDrugs.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
                    break;
                default:
                    break;
            }
        }

        // 分页
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, allDrugs.size());
        List<DrugVO> pageList = fromIndex < allDrugs.size() ? allDrugs.subList(fromIndex, toIndex) : new ArrayList<>();

        return Result.success(PageResultVO.of(pageList, (long) allDrugs.size(), page, size));
    }

    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    @Operation(summary = "获取搜索建议", description = "根据输入关键词返回搜索建议列表")
    public Result<List<SearchSuggestionVO>> getSearchSuggestions(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {

        List<SearchSuggestionVO> suggestions = new ArrayList<>();

        // Mock数据：根据关键词返回建议
        if (keyword != null && !keyword.isEmpty()) {
            String kw = keyword.toLowerCase();

            // 药品名称建议
            SearchSuggestionVO drugSuggestion = new SearchSuggestionVO();
            drugSuggestion.setKeyword(kw.contains("感") ? "感冒灵颗粒" : (kw.contains("头") ? "头孢克肟分散片" : "阿莫西林胶囊"));
            drugSuggestion.setType("drug");
            drugSuggestion.setCount(100);
            suggestions.add(drugSuggestion);

            // 分类建议
            SearchSuggestionVO catSuggestion = new SearchSuggestionVO();
            catSuggestion.setKeyword("感冒用药");
            catSuggestion.setType("category");
            catSuggestion.setCount(50);
            suggestions.add(catSuggestion);

            // 品牌建议
            SearchSuggestionVO brandSuggestion = new SearchSuggestionVO();
            brandSuggestion.setKeyword(kw.contains("三") ? "三九医药" : "白云山");
            brandSuggestion.setType("brand");
            brandSuggestion.setCount(30);
            suggestions.add(brandSuggestion);

            // 症状建议
            SearchSuggestionVO symptomSuggestion = new SearchSuggestionVO();
            symptomSuggestion.setKeyword(kw.contains("发") ? "发热" : (kw.contains("咳") ? "咳嗽" : "头痛"));
            symptomSuggestion.setType("symptom");
            symptomSuggestion.setCount(80);
            suggestions.add(symptomSuggestion);
        } else {
            // 无关键词时返回热门搜索
            String[] hotKeywords = {"感冒灵颗粒", "布洛芬缓释胶囊", "阿莫西林胶囊", "维生素C片", "板蓝根颗粒"};
            for (String kw : hotKeywords) {
                SearchSuggestionVO suggestion = new SearchSuggestionVO();
                suggestion.setKeyword(kw);
                suggestion.setType("hot");
                suggestion.setCount(999);
                suggestions.add(suggestion);
            }
        }

        return Result.success(suggestions);
    }

    /**
     * 获取热门搜索
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门搜索", description = "获取当前热门搜索关键词列表")
    public Result<List<HotSearchVO>> getHotSearches(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {

        List<HotSearchVO> hotSearches = Arrays.asList(
                createHotSearch("感冒灵颗粒", 9800),
                createHotSearch("布洛芬缓释胶囊", 8560),
                createHotSearch("阿莫西林胶囊", 7230),
                createHotSearch("维生素C片", 6890),
                createHotSearch("板蓝根颗粒", 6540),
                createHotSearch("头孢克肟分散片", 5890),
                createHotSearch("止咳糖浆", 5230),
                createHotSearch("创可贴", 4860),
                createHotSearch("体温计", 4520),
                createHotSearch("口罩", 9980)
        );

        return Result.success(hotSearches.stream().limit(limit).collect(Collectors.toList()));
    }

    private HotSearchVO createHotSearch(String keyword, int heat) {
        HotSearchVO vo = new HotSearchVO();
        vo.setKeyword(keyword);
        vo.setHeat(heat);
        vo.setIsNew(heat > 8000);
        vo.setIsHot(heat > 6000);
        return vo;
    }

    /**
     * 获取筛选条件
     */
    @GetMapping("/filters")
    @Operation(summary = "获取筛选条件", description = "获取搜索页面的筛选条件（分类、品牌、价格区间）")
    public Result<Map<String, Object>> getSearchFilters() {

        Map<String, Object> filters = new HashMap<>();

        // 分类列表
        List<Map<String, String>> categories = new ArrayList<>();
        categories.add(createFilterItem("cat_001", "全部"));
        categories.add(createFilterItem("cat_002", "感冒用药"));
        categories.add(createFilterItem("cat_003", "止痛镇痛"));
        categories.add(createFilterItem("cat_004", "消化系统"));
        categories.add(createFilterItem("cat_005", "心血管"));
        categories.add(createFilterItem("cat_006", "皮肤外用"));
        categories.add(createFilterItem("cat_007", "维生素补充"));
        categories.add(createFilterItem("cat_008", "医疗器械"));
        filters.put("categories", categories);

        // 品牌列表
        List<Map<String, String>> brands = new ArrayList<>();
        brands.add(createFilterItem("brand_001", "全部"));
        brands.add(createFilterItem("brand_002", "三九医药"));
        brands.add(createFilterItem("brand_003", "白云山"));
        brands.add(createFilterItem("brand_004", "同仁堂"));
        brands.add(createFilterItem("brand_005", "云南白药"));
        brands.add(createFilterItem("brand_006", "葵花药业"));
        brands.add(createFilterItem("brand_007", "仁和药业"));
        brands.add(createFilterItem("brand_008", "扬子江"));
        filters.put("brands", brands);

        // 价格区间
        List<Map<String, Object>> priceRanges = new ArrayList<>();
        priceRanges.add(createPriceRange(0, 10, "10元以下"));
        priceRanges.add(createPriceRange(10, 30, "10-30元"));
        priceRanges.add(createPriceRange(30, 50, "30-50元"));
        priceRanges.add(createPriceRange(50, 100, "50-100元"));
        priceRanges.add(createPriceRange(100, 999999, "100元以上"));
        filters.put("priceRanges", priceRanges);

        return Result.success(filters);
    }

    private Map<String, String> createFilterItem(String id, String name) {
        Map<String, String> item = new HashMap<>();
        item.put("id", id);
        item.put("name", name);
        return item;
    }

    private Map<String, Object> createPriceRange(int min, int max, String label) {
        Map<String, Object> range = new HashMap<>();
        range.put("min", min);
        range.put("max", max);
        range.put("label", label);
        return range;
    }

    /**
     * 从Mock数据获取药品列表
     */
    private List<DrugVO> getMockDrugs() {
        List<DrugVO> drugs = new ArrayList<>();

        // 感冒用药
        drugs.add(createMockDrug("1", "感冒灵颗粒", "cat_002", new BigDecimal("12.50"), 9800, true, "感冒发热", "三九医药", "10g*9袋/盒"));
        drugs.add(createMockDrug("2", "布洛芬缓释胶囊", "cat_003", new BigDecimal("18.00"), 8560, true, "止痛退热", "白云山", "0.3g*20粒/盒"));
        drugs.add(createMockDrug("3", "止咳糖浆", "cat_002", new BigDecimal("15.80"), 5230, true, "止咳化痰", "葵花药业", "100ml/瓶"));
        drugs.add(createMockDrug("4", "板蓝根颗粒", "cat_002", new BigDecimal("8.90"), 6540, true, "清热解毒", "白云山", "10g*20袋/盒"));

        // 抗生素
        drugs.add(createMockDrug("5", "阿莫西林胶囊", "cat_002", new BigDecimal("9.50"), 7230, false, "抗菌消炎", "扬子江", "0.25g*24粒/盒"));
        drugs.add(createMockDrug("6", "头孢克肟分散片", "cat_002", new BigDecimal("25.00"), 5890, false, "抗菌消炎", "白云山", "0.1g*6片/盒"));

        // 维生素
        drugs.add(createMockDrug("7", "维生素C片", "cat_007", new BigDecimal("6.80"), 6890, true, "补充维C", "仁和药业", "100mg*100片/瓶"));
        drugs.add(createMockDrug("8", "维生素AD软胶囊", "cat_007", new BigDecimal("28.00"), 4520, true, "补充维AD", "仁和药业", "60粒/盒"));

        // 消化系统
        drugs.add(createMockDrug("9", "奥美拉唑肠溶胶囊", "cat_004", new BigDecimal("22.00"), 3890, false, "胃酸过多", "扬子江", "20mg*14粒/盒"));
        drugs.add(createMockDrug("10", "蒙脱石散", "cat_004", new BigDecimal("11.50"), 4120, true, "止泻", "葵花药业", "3g*10袋/盒"));

        // 心血管
        drugs.add(createMockDrug("11", "硝苯地平控释片", "cat_005", new BigDecimal("35.00"), 2980, false, "高血压", "白云山", "30mg*7片/盒"));
        drugs.add(createMockDrug("12", "阿司匹林肠溶片", "cat_005", new BigDecimal("18.50"), 3560, false, "预防血栓", "扬子江", "100mg*30片/盒"));

        // 皮肤外用
        drugs.add(createMockDrug("13", "红霉素软膏", "cat_006", new BigDecimal("5.50"), 7890, true, "皮肤感染", "仁和药业", "8g/支"));
        drugs.add(createMockDrug("14", "创可贴(100片)", "cat_006", new BigDecimal("12.90"), 4860, true, "伤口护理", "云南白药", "100片/盒"));

        // 医疗器械
        drugs.add(createMockDrug("15", "电子体温计", "cat_008", new BigDecimal("39.90"), 4520, true, "测量体温", "鱼跃医疗", "1支/盒"));
        drugs.add(createMockDrug("16", "医用外科口罩(50只)", "cat_008", new BigDecimal("19.90"), 9980, true, "防护用品", "振德医疗", "50只/包"));

        return drugs;
    }

    private DrugVO createMockDrug(String id, String name, String categoryId, BigDecimal price,
                                   int sales, boolean isRx, String disease, String manufacturer, String specification) {
        DrugVO drug = new DrugVO();
        drug.setId(id);
        drug.setName(name);
        drug.setCategoryId(categoryId);
        drug.setCategory(getCategoryName(categoryId));
        drug.setPrice(price);
        drug.setOriginalPrice(price.multiply(new BigDecimal("1.2")));
        drug.setSales(sales);
        drug.setIsRx(isRx);
        drug.setDisease(disease);
        drug.setManufacturer(manufacturer);
        drug.setImage("https://via.placeholder.com/200x200?text=" + name.replace(" ", "+"));
        drug.setStatus(1);
        drug.setStock(999);
        drug.setSpecification(specification);
        drug.setDescription("【功能主治】用于治疗" + disease + "。\n【用法用量】口服，详见说明书。\n【注意事项】请遵医嘱使用。");
        drug.setCreateTime(LocalDateTime.now().minusDays((long) (Math.random() * 30)));
        return drug;
    }

    private String getCategoryName(String categoryId) {
        switch (categoryId) {
            case "cat_002": return "感冒用药";
            case "cat_003": return "止痛镇痛";
            case "cat_004": return "消化系统";
            case "cat_005": return "心血管";
            case "cat_006": return "皮肤外用";
            case "cat_007": return "维生素补充";
            case "cat_008": return "医疗器械";
            default: return "其他药品";
        }
    }
}
