package com.drugmall.service.impl;

import com.drugmall.service.HomeService;
import com.drugmall.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 首页配置服务实现类（Mock数据模式）
 * 当前使用硬编码的 Mock 数据返回首页配置
 * 待确认需求后可切换为数据库查询模式
 *
 * @author DrugMall Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class HomeServiceImpl implements HomeService {

    @Override
    public HomePageVO getHomePageRender() {
        log.info("获取首页渲染配置");

        // 构建页面级配置
        Map<String, Object> pageConfig = new HashMap<>();
        pageConfig.put("themeColor", "#1890ff");
        pageConfig.put("backgroundColor", "#f5f5f5");
        pageConfig.put("enableLocation", true);
        pageConfig.put("enableSearchSticky", true);
        pageConfig.put("defaultLocation", "北京市朝阳区");

        // 构建所有模块列表
        List<HomeSectionVO> sections = buildAllSections();

        return HomePageVO.builder()
                .pageId("home_page_001")
                .pageName("DrugMall首页")
                .description("药品电商平台移动端首页")
                .version("v1.0.0")
                .enabled(true)
                .pageConfig(pageConfig)
                .sections(sections)
                .build();
    }

    @Override
    public List<HomePageVO> getConfigList() {
        log.info("获取首页配置列表");
        List<HomePageVO> configList = new ArrayList<>();
        configList.add(getHomePageRender());
        return configList;
    }

    /**
     * 构建所有首页模块
     */
    private List<HomeSectionVO> buildAllSections() {
        List<HomeSectionVO> sections = new ArrayList<>();

        // 1. 搜索栏模块
        sections.add(buildSearchBarSection());

        // 2. Tab导航模块
        sections.add(buildTabNavigationSection());

        // 3. 24h服务网格模块
        sections.add(buildServiceGridSection());

        // 4. Banner轮播+百亿补贴模块
        sections.add(buildBannerSubsidySection());

        // 5. 秒问医生Banner模块
        sections.add(buildDoctorBannerSection());

        // 6. 附近急送药店模块
        sections.add(buildNearbyPharmacySection());

        // 7. 瀑布流混合布局模块
        sections.add(buildWaterfallLayoutSection());

        // 8. 问医生科室列表模块
        sections.add(buildDoctorDepartmentSection());

        // 9. 检测项目列表模块
        sections.add(buildTestItemsSection());

        // 10. 慢病关怀分类模块
        sections.add(buildChronicCategorySection());

        // 11. 中医保健分类模块
        sections.add(buildTcmCategorySection());

        // 按 sortOrder 排序
        sections.sort(Comparator.comparingInt(HomeSectionVO::getSortOrder));

        return sections;
    }

    /**
     * 1. 搜索栏模块
     */
    private HomeSectionVO buildSearchBarSection() {
        Map<String, Object> searchBarConfig = new HashMap<>();
        searchBarConfig.put("placeholder", "搜索药品、症状、品牌");
        searchBarConfig.put("showScanCode", true);
        searchBarConfig.put("showVoiceSearch", false);
        searchBarConfig.put("hotSearchCount", 5);

        // 搜索栏组件
        HomeComponentVO searchBarComponent = HomeComponentVO.builder()
                .componentId("search_bar_001")
                .componentType("search_bar")
                .config(searchBarConfig)
                .data(Map.of(
                        "hotKeywords", Arrays.asList("感冒药", "布洛芬", "维生素C", "口罩", "连花清瘟"),
                        "recentSearches", Arrays.asList("阿莫西林", "创可贴")
                ))
                .trackId("home_search_bar_click")
                .build();

        return HomeSectionVO.builder()
                .sectionId("section_search_bar")
                .sectionType("search_bar")
                .title("搜索栏")
                .layout("full_width")
                .visible(true)
                .sortOrder(1)
                .config(searchBarConfig)
                .components(Collections.singletonList(searchBarComponent))
                .build();
    }

    /**
     * 2. Tab导航模块
     */
    private HomeSectionVO buildTabNavigationSection() {
        Map<String, Object> tabConfig = new HashMap<>();
        tabConfig.put("scrollable", true);
        tabConfig.put("activeColor", "#1890ff");
        tabConfig.put("inactiveColor", "#666666");

        // Tab数据
        List<Map<String, Object>> tabData = Arrays.asList(
                createMap("id", "recommend", "name", "推荐", "icon", ""),
                createMap("id", "doctor", "name", "问医生", "icon", "FirstAidKit"),
                createMap("id", "test", "name", "做检测", "icon", "Monitor"),
                createMap("id", "chronic", "name", "慢病关怀", "icon", ""),
                createMap("id", "tcm", "name", "中医保健", "icon", "")
        );

        HomeComponentVO tabComponent = HomeComponentVO.builder()
                .componentId("tab_nav_001")
                .componentType("tab_navigation")
                .config(tabConfig)
                .data(tabData)
                .trackId("home_tab_switch")
                .build();

        return HomeSectionVO.builder()
                .sectionId("section_tab_navigation")
                .sectionType("tab_navigation")
                .title("Tab导航")
                .layout("full_width")
                .visible(true)
                .sortOrder(2)
                .config(tabConfig)
                .components(Collections.singletonList(tabComponent))
                .build();
    }

    /**
     * 3. 24h服务网格模块
     */
    private HomeSectionVO buildServiceGridSection() {
        Map<String, Object> gridConfig = new HashMap<>();
        gridConfig.put("columns", 5);
        gridConfig.put("showTitle", true);
        gridConfig.put("iconSize", "medium");

        // 服务数据 - 使用白底图片
        List<Map<String, Object>> serviceData = Arrays.asList(
                createMap("id", "1", "name", "感冒发烧", "icon", "感冒", "iconUrl", "https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-55-tps-83-82.svg"),
                createMap("id", "2", "name", "儿童用药", "icon", "儿童", "iconUrl", "https://img.alicdn.com/imgextra/i2/O1CN01O0et1z1Jog3zZ1Dgn_!!6000000001069-55-tps-83-82.svg"),
                createMap("id", "3", "name", "男科用药", "icon", "男科", "iconUrl", "https://img.alicdn.com/imgextra/i1/O1CN01XY8X0i1GxPFS0YlFW_!!6000000000683-55-tps-83-82.svg"),
                createMap("id", "4", "name", "避孕测孕", "icon", "避孕", "iconUrl", "https://img.alicdn.com/imgextra/i3/O1CN01MNj3nA1q3Hq0q0ZqL_!!6000000005438-55-tps-83-82.svg"),
                createMap("id", "5", "name", "过敏用药", "icon", "过敏", "iconUrl", "https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-55-tps-83-82.svg"),
                createMap("id", "6", "name", "肠胃用药", "icon", "肠胃", "iconUrl", "https://img.alicdn.com/imgextra/i2/O1CN01O0et1z1Jog3zZ1Dgn_!!6000000001069-55-tps-83-82.svg"),
                createMap("id", "7", "name", "为TA买药", "icon", "爱心", "iconUrl", "https://img.alicdn.com/imgextra/i1/O1CN01XY8X0i1GxPFS0YlFW_!!6000000000683-55-tps-83-82.svg"),
                createMap("id", "8", "name", "原研药", "icon", "原研", "iconUrl", "https://img.alicdn.com/imgextra/i3/O1CN01MNj3nA1q3Hq0q0ZqL_!!6000000005438-55-tps-83-82.svg"),
                createMap("id", "9", "name", "自营大药房", "icon", "自营", "iconUrl", "https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-55-tps-83-82.svg"),
                createMap("id", "10", "name", "口腔/看牙", "icon", "口腔", "iconUrl", "https://img.alicdn.com/imgextra/i2/O1CN01O0et1z1Jog3zZ1Dgn_!!6000000001069-55-tps-83-82.svg")
        );

        HomeComponentVO gridComponent = HomeComponentVO.builder()
                .componentId("service_grid_001")
                .componentType("service_grid")
                .config(gridConfig)
                .data(serviceData)
                .trackId("home_service_click")
                .build();

        return HomeSectionVO.builder()
                .sectionId("section_service_grid")
                .sectionType("service_grid")
                .title("24h服务")
                .layout("full_width")
                .visible(true)
                .sortOrder(3)
                .config(gridConfig)
                .components(Collections.singletonList(gridComponent))
                .build();
    }

    /**
     * 4. Banner轮播+百亿补贴模块（左右布局）
     */
    private HomeSectionVO buildBannerSubsidySection() {
        Map<String, Object> bannerConfig = new HashMap<>();
        bannerConfig.put("autoPlay", true);
        bannerConfig.put("interval", 4000);
        bannerConfig.put("layout", "left_right"); // 左右布局：左侧Banner，右侧补贴

        // Banner轮播组件 - 使用图片
        List<Map<String, Object>> bannerData = Arrays.asList(
                createMap("title", "安心控糖心肾双护", "subtitle", "免费抽血糖仪",
                        "tag", "限时活动", "imageUrl", "https://img.alicdn.com/imgextra/i1/O1CN01QJ0W3T1J7dR5yT8z1_!!6000000000980-0-tps-750-750.jpg",
                        "link", "/activity/diabetes"),
                createMap("title", "春季过敏专区", "subtitle", "抗过敏药5折起",
                        "tag", "热销", "imageUrl", "https://img.alicdn.com/imgextra/i2/O1CN01L8v1tZ1Xl6K3y8d8g_!!6000000002969-0-tps-750-750.jpg",
                        "link", "/activity/allergy"),
                createMap("title", "慢病管理", "subtitle", "处方药专属优惠",
                        "tag", "医保可用", "imageUrl", "https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-0-tps-750-750.jpg",
                        "link", "/activity/chronic")
        );

        HomeComponentVO bannerComponent = HomeComponentVO.builder()
                .componentId("banner_carousel_001")
                .componentType("banner_carousel")
                .config(createMap("width", "70%", "height", "150px"))
                .data(bannerData)
                .trackId("home_banner_click")
                .build();

        // 百亿补贴组件 - 使用商品缩略图
        List<Map<String, Object>> subsidyData = Arrays.asList(
                createMap("id", "1", "name", "布地奈德鼻喷雾剂", "price", 49.0, "originalPrice", 89.0, 
                        "imageUrl", "https://img.alicdn.com/imgextra/i1/O1CN01QJ0W3T1J7dR5yT8z1_!!6000000000980-2-tps-200-200.png"),
                createMap("id", "2", "name", "薇诺娜防晒乳", "price", 59.0, "originalPrice", 128.0, 
                        "imageUrl", "https://img.alicdn.com/imgextra/i2/O1CN01L8v1tZ1Xl6K3y8d8g_!!6000000002969-2-tps-200-200.png"),
                createMap("id", "3", "name", "超声波洗牙", "price", 29.2, "originalPrice", 68.0, 
                        "imageUrl", "https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-2-tps-200-200.png"),
                createMap("id", "4", "name", "999感冒灵", "price", 12.5, "originalPrice", 25.0, 
                        "imageUrl", "https://img.alicdn.com/imgextra/i3/O1CN01MNj3nA1q3Hq0q0ZqL_!!6000000005438-2-tps-200-200.png")
        );

        HomeComponentVO subsidyComponent = HomeComponentVO.builder()
                .componentId("subsidy_list_001")
                .componentType("subsidy_list")
                .config(createMap("width", "30%", "title", "百亿补贴"))
                .data(subsidyData)
                .trackId("home_subsidy_click")
                .build();

        return HomeSectionVO.builder()
                .sectionId("section_banner_subsidy")
                .sectionType("banner_subsidy")
                .title("Banner与补贴")
                .layout("full_width")
                .visible(true)
                .sortOrder(4)
                .config(bannerConfig)
                .components(Arrays.asList(bannerComponent, subsidyComponent))
                .build();
    }

    /**
     * 5. 秒问医生Banner模块
     */
    private HomeSectionVO buildDoctorBannerSection() {
        Map<String, Object> doctorConfig = new HashMap<>();
        doctorConfig.put("bannerStyle", "doctor_list");
        doctorConfig.put("showOnlineStatus", true);

        // 医生数据
        List<Map<String, Object>> doctorData = Arrays.asList(
                createMap("name", "张医生", "bgColor", "#E3F2FD", "icon", "张"),
                createMap("name", "李医生", "bgColor", "#FFF3E0", "icon", "李"),
                createMap("name", "王医生", "bgColor", "#E8F5E9", "icon", "王"),
                createMap("name", "刘医生", "bgColor", "#FCE4EC", "icon", "刘")
        );

        HomeComponentVO doctorComponent = HomeComponentVO.builder()
                .componentId("doctor_banner_001")
                .componentType("doctor_banner")
                .config(doctorConfig)
                .data(doctorData)
                .trackId("home_doctor_click")
                .build();

        return HomeSectionVO.builder()
                .sectionId("section_doctor_banner")
                .sectionType("doctor_banner")
                .title("秒问医生")
                .layout("full_width")
                .visible(true)
                .sortOrder(5)
                .config(doctorConfig)
                .components(Collections.singletonList(doctorComponent))
                .build();
    }

    /**
     * 6. 附近急送药店模块
     */
    private HomeSectionVO buildNearbyPharmacySection() {
        Map<String, Object> pharmacyConfig = new HashMap<>();
        pharmacyConfig.put("showHeader", true);
        pharmacyConfig.put("headerTitle", "附近急送");
        pharmacyConfig.put("showFilter", true);
        pharmacyConfig.put("showMoreButton", true);
        pharmacyConfig.put("estimatedDeliveryTime", 30); // 分钟

        // 药店数据 - 复用前端完整数据
        List<Map<String, Object>> pharmacyData = Arrays.asList(
                buildPharmacyData("1", "海王星辰健康药房(朝阳店)", "海王", "#FFD700",
                        4.8, 1200, 0.8, 25,
                        Arrays.asList(
                                createMap("text", "医保定点", "type", "primary"),
                                createMap("text", "24小时", "type", "success")
                        ),
                        Arrays.asList(
                                createMap("id", "p1", "name", "阿莫西林", "price", 15.8, "bgColor", "#E3F2FD"),
                                createMap("id", "p2", "name", "布洛芬", "price", 12.5, "bgColor", "#FFF3E0"),
                                createMap("id", "p3", "name", "维生素C", "price", 8.9, "bgColor", "#E8F5E9")
                        )),
                buildPharmacyData("2", "老百姓大药房(海淀店)", "百姓", "#4CAF50",
                        4.9, 2300, 1.2, 35,
                        Arrays.asList(
                                createMap("text", "连锁品牌", "type", "primary"),
                                createMap("text", "满39免配送", "type", "warning")
                        ),
                        Arrays.asList(
                                createMap("id", "p4", "name", "感冒灵", "price", 18.5, "bgColor", "#FFEBEE"),
                                createMap("id", "p5", "name", "创可贴", "price", 5.9, "bgColor", "#F3E5F5")
                        )),
                buildPharmacyData("3", "叮当快药", "叮当", "#2196F3",
                        4.7, 3500, 0.5, 28,
                        Arrays.asList(
                                createMap("text", "28分钟达", "type", "success"),
                                createMap("text", "自营", "type", "primary")
                        ),
                        Arrays.asList(
                                createMap("id", "p6", "name", "口罩", "price", 9.9, "bgColor", "#E1F5FE"),
                                createMap("id", "p7", "name", "消毒液", "price", 25.0, "bgColor", "#E0F2F1")
                        ))
        );

        // 筛选标签数据
        List<String> filterTags = Arrays.asList("附近药店", "成人用品", "医疗器械", "隐形眼镜", "营养保健");

        HomeComponentVO pharmacyComponent = HomeComponentVO.builder()
                .componentId("pharmacy_list_001")
                .componentType("pharmacy_list")
                .config(pharmacyConfig)
                .data(Map.of(
                        "pharmacies", pharmacyData,
                        "filters", filterTags,
                        "activeFilter", "附近药店"
                ))
                .trackId("home_pharmacy_click")
                .build();

        return HomeSectionVO.builder()
                .sectionId("section_nearby_pharmacy")
                .sectionType("nearby_pharmacy")
                .title("附近急送")
                .layout("full_width")
                .visible(true)
                .sortOrder(6)
                .config(pharmacyConfig)
                .components(Collections.singletonList(pharmacyComponent))
                .build();
    }

    /**
     * 构建单个药店数据
     */
    private Map<String, Object> buildPharmacyData(String id, String name, String logoText, String logoColor,
                                                   double rating, int monthlySales, double distance, int deliveryTime,
                                                   List<Map<String, Object>> tags, List<Map<String, Object>> products) {
        Map<String, Object> pharmacy = new HashMap<>();
        pharmacy.put("id", id);
        pharmacy.put("name", name);
        pharmacy.put("logoText", logoText);
        pharmacy.put("logoColor", logoColor);
        pharmacy.put("rating", rating);
        pharmacy.put("monthlySales", monthlySales);
        pharmacy.put("distance", distance);
        pharmacy.put("deliveryTime", deliveryTime);
        pharmacy.put("tags", tags);
        pharmacy.put("products", products);
        return pharmacy;
    }

    /**
     * 7. 瀑布流混合布局模块
     */
    private HomeSectionVO buildWaterfallLayoutSection() {
        Map<String, Object> waterfallConfig = new HashMap<>();
        waterfallConfig.put("columns", 2);
        waterfallConfig.put("gap", "10px");
        waterfallConfig.put("mixTypes", Arrays.asList("ad", "product"));

        // 瀑布流数据 - 混合广告和商品，使用图片
        List<Map<String, Object>> waterfallData = Arrays.asList(
                // 广告卡片 - 使用图片
                createMap("type", "ad", "title", "春季过敏专区", "subtitle", "抗过敏药5折起",
                        "btnText", "去看看",
                        "imageUrl", "https://img.alicdn.com/imgextra/i2/O1CN01L8v1tZ1Xl6K3y8d8g_!!6000000002969-0-tps-400-300.jpg"),
                // 商品卡片 - 使用图片
                createMap("type", "product", "id", "d1", "name", "连花清瘟胶囊",
                        "specification", "0.35g*24粒", "sales", 5.2, "price", 14.5,
                        "deliveryTime", 30, "imageUrl", "https://img.alicdn.com/imgextra/i1/O1CN01QJ0W3T1J7dR5yT8z1_!!6000000000980-2-tps-300-300.png",
                        "isRx", false, "discount", 20),
                // 商品卡片
                createMap("type", "product", "id", "d2", "name", "阿莫西林胶囊",
                        "specification", "0.25g*20粒", "sales", 3.8, "price", 8.9,
                        "deliveryTime", 25, "imageUrl", "https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-2-tps-300-300.png",
                        "isRx", true),
                // 广告卡片
                createMap("type", "ad", "title", "慢病管理中心", "subtitle", "高血压、糖尿病用药",
                        "btnText", "立即查看",
                        "imageUrl", "https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-0-tps-400-300.jpg"),
                // 商品卡片
                createMap("type", "product", "id", "d3", "name", "布洛芬缓释胶囊",
                        "specification", "0.3g*12粒", "sales", 4.5, "price", 12.8,
                        "deliveryTime", 35, "imageUrl", "https://img.alicdn.com/imgextra/i3/O1CN01MNj3nA1q3Hq0q0ZqL_!!6000000005438-2-tps-300-300.png",
                        "isRx", false, "discount", 15),
                // 商品卡片
                createMap("type", "product", "id", "d4", "name", "维生素C泡腾片",
                        "specification", "1g*10片", "sales", 2.1, "price", 18.5,
                        "deliveryTime", 40, "imageUrl", "https://img.alicdn.com/imgextra/i2/O1CN01O0et1z1Jog3zZ1Dgn_!!6000000001069-2-tps-300-300.png",
                        "isRx", false),
                // 广告卡片
                createMap("type", "ad", "title", "中医养生馆", "subtitle", "中药材、养生茶饮",
                        "btnText", "探索更多",
                        "imageUrl", "https://img.alicdn.com/imgextra/i1/O1CN01XY8X0i1GxPFS0YlFW_!!6000000000683-0-tps-400-300.jpg"),
                // 商品卡片
                createMap("type", "product", "id", "d5", "name", "感冒灵颗粒",
                        "specification", "10g*9袋", "sales", 8.5, "price", 15.9,
                        "deliveryTime", 28, "imageUrl", "https://img.alicdn.com/imgextra/i1/O1CN01XY8X0i1GxPFS0YlFW_!!6000000000683-2-tps-300-300.png",
                        "isRx", false),
                // 商品卡片
                createMap("type", "product", "id", "d6", "name", "板蓝根颗粒",
                        "specification", "10g*20袋", "sales", 6.2, "price", 12.5,
                        "deliveryTime", 32, "imageUrl", "https://img.alicdn.com/imgextra/i3/O1CN01MNj3nA1q3Hq0q0ZqL_!!6000000005438-2-tps-300-300.png",
                        "isRx", false, "discount", 10)
        );

        HomeComponentVO waterfallComponent = HomeComponentVO.builder()
                .componentId("waterfall_layout_001")
                .componentType("waterfall_layout")
                .config(waterfallConfig)
                .data(waterfallData)
                .trackId("home_waterfall_click")
                .build();

        return HomeSectionVO.builder()
                .sectionId("section_waterfall_layout")
                .sectionType("waterfall_layout")
                .title("精选推荐")
                .layout("full_width")
                .visible(true)
                .sortOrder(7)
                .config(waterfallConfig)
                .components(Collections.singletonList(waterfallComponent))
                .build();
    }

    /**
     * 8. 问医生科室列表模块
     */
    private HomeSectionVO buildDoctorDepartmentSection() {
        Map<String, Object> deptConfig = new HashMap<>();
        deptConfig.put("columns", 5);
        deptConfig.put("showTag", true);
        deptConfig.put("showIcon", true);

        // 科室数据
        List<Map<String, Object>> deptData = Arrays.asList(
                createMap("id", "1", "name", "皮肤科", "icon", "皮", "bgColor", "#E3F2FD", "tag", "瘙痒"),
                createMap("id", "2", "name", "呼吸内科", "icon", "呼", "bgColor", "#FFF3E0"),
                createMap("id", "3", "name", "儿科", "icon", "儿", "bgColor", "#E8F5E9", "tag", "发热"),
                createMap("id", "4", "name", "消化内科", "icon", "消", "bgColor", "#FCE4EC"),
                createMap("id", "5", "name", "妇产科", "icon", "妇", "bgColor", "#F3E5F5"),
                createMap("id", "6", "name", "耳鼻喉科", "icon", "耳", "bgColor", "#E0F2F1"),
                createMap("id", "7", "name", "泌尿外科", "icon", "泌", "bgColor", "#FFEBEE"),
                createMap("id", "8", "name", "口腔科", "icon", "口", "bgColor", "#E8EAF6"),
                createMap("id", "9", "name", "眼科", "icon", "眼", "bgColor", "#FFF8E1"),
                createMap("id", "10", "name", "心理咨询", "icon", "心", "bgColor", "#E1F5FE", "tag", "199元")
        );

        HomeComponentVO deptComponent = HomeComponentVO.builder()
                .componentId("department_grid_001")
                .componentType("department_grid")
                .config(deptConfig)
                .data(deptData)
                .trackId("home_department_click")
                .build();

        return HomeSectionVO.builder()
                .sectionId("section_doctor_department")
                .sectionType("doctor_department")
                .title("问医生科室")
                .layout("full_width")
                .visible(true)
                .sortOrder(8)
                .config(deptConfig)
                .components(Collections.singletonList(deptComponent))
                .build();
    }

    /**
     * 9. 检测项目列表模块
     */
    private HomeSectionVO buildTestItemsSection() {
        Map<String, Object> testConfig = new HashMap<>();
        testConfig.put("showPrice", true);
        testConfig.put("showTime", true);
        testConfig.put("cardStyle", "horizontal");

        // 检测项目数据
        List<Map<String, Object>> testData = Arrays.asList(
                createMap("id", "1",
                        "name", "美团买药居家快检多重呼吸道细菌病毒核酸检测",
                        "desc", "快速识别咳嗽、发热感染原因",
                        "price", 39, "time", "平均3-4h出报告",
                        "bgColor", "#FFD700", "icon", "检"),
                createMap("id", "2",
                        "name", "新型冠状病毒(2019-nCoV)抗原检测试剂",
                        "desc", "居家自测，15分钟出结果",
                        "price", 31.91, "time", "39分钟",
                        "bgColor", "#4CAF50", "icon", "新")
        );

        HomeComponentVO testComponent = HomeComponentVO.builder()
                .componentId("test_items_001")
                .componentType("test_item_card")
                .config(testConfig)
                .data(testData)
                .trackId("home_test_click")
                .build();

        return HomeSectionVO.builder()
                .sectionId("section_test_items")
                .sectionType("test_items")
                .title("健康检测")
                .layout("full_width")
                .visible(true)
                .sortOrder(9)
                .config(testConfig)
                .components(Collections.singletonList(testComponent))
                .build();
    }

    /**
     * 10. 慢病关怀分类模块
     */
    private HomeSectionVO buildChronicCategorySection() {
        Map<String, Object> chronicConfig = new HashMap<>();
        chronicConfig.put("columns", 3);
        chronicConfig.put("showDescription", false);

        // 慢病分类数据
        List<Map<String, Object>> chronicData = Arrays.asList(
                createMap("id", "1", "name", "高血压", "icon", "高", "bgColor", "#E3F2FD"),
                createMap("id", "2", "name", "糖尿病", "icon", "糖", "bgColor", "#FFF3E0"),
                createMap("id", "3", "name", "高血脂", "icon", "脂", "bgColor", "#E8F5E9"),
                createMap("id", "4", "name", "心脏病", "icon", "心", "bgColor", "#FCE4EC"),
                createMap("id", "5", "name", "哮喘", "icon", "哮", "bgColor", "#F3E5F5"),
                createMap("id", "6", "name", "痛风", "icon", "痛", "bgColor", "#E0F2F1")
        );

        HomeComponentVO chronicComponent = HomeComponentVO.builder()
                .componentId("chronic_category_001")
                .componentType("category_grid")
                .config(chronicConfig)
                .data(chronicData)
                .trackId("home_chronic_click")
                .build();

        return HomeSectionVO.builder()
                .sectionId("section_chronic_category")
                .sectionType("chronic_category")
                .title("慢病关怀")
                .layout("full_width")
                .visible(true)
                .sortOrder(10)
                .config(chronicConfig)
                .components(Collections.singletonList(chronicComponent))
                .build();
    }

    /**
     * 11. 中医保健分类模块
     */
    private HomeSectionVO buildTcmCategorySection() {
        Map<String, Object> tcmConfig = new HashMap<>();
        tcmConfig.put("columns", 3);
        tcmConfig.put("showDescription", false);

        // 中医分类数据
        List<Map<String, Object>> tcmData = Arrays.asList(
                createMap("id", "1", "name", "中药材", "icon", "药", "bgColor", "#E8F5E9"),
                createMap("id", "2", "name", "养生茶饮", "icon", "茶", "bgColor", "#FFF8E1"),
                createMap("id", "3", "name", "理疗保健", "icon", "理", "bgColor", "#E1F5FE"),
                createMap("id", "4", "name", "艾灸拔罐", "icon", "灸", "bgColor", "#FFEBEE"),
                createMap("id", "5", "name", "滋补膏方", "icon", "膏", "bgColor", "#F3E5F5"),
                createMap("id", "6", "name", "药食同源", "icon", "食", "bgColor", "#E0F2F1")
        );

        HomeComponentVO tcmComponent = HomeComponentVO.builder()
                .componentId("tcm_category_001")
                .componentType("category_grid")
                .config(tcmConfig)
                .data(tcmData)
                .trackId("home_tcm_click")
                .build();

        return HomeSectionVO.builder()
                .sectionId("section_tcm_category")
                .sectionType("tcm_category")
                .title("中医保健")
                .layout("full_width")
                .visible(true)
                .sortOrder(11)
                .config(tcmConfig)
                .components(Collections.singletonList(tcmComponent))
                .build();
    }

    /**
     * 辅助方法：创建键值对Map
     */
    @SafeVarargs
    private static <K, V> Map<K, V> createMap(Object... keyValuePairs) {
        Map<K, V> map = new LinkedHashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            K key = (K) keyValuePairs[i];
            V value = (V) keyValuePairs[i + 1];
            map.put(key, value);
        }
        return map;
    }
}
