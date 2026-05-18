package com.drugmall.service.impl;

import com.alibaba.fastjson2.JSON;
import com.drugmall.service.HomeService;
import com.drugmall.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 首页配置服务实现类（数据库模式）
 * 从数据库 dm_home_page, dm_home_tab, dm_home_section 等表读取配置
 *
 * @author DrugMall Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public HomePageVO getHomePageRender() {
        log.info("获取首页渲染配置（数据库模式）");

        try {
            // 1. 获取当前发布的首页配置
            Map<String, Object> pageData = getCurrentPageConfig();
            if (pageData == null) {
                log.warn("未找到已发布的首页配置，返回空配置");
                return buildEmptyHomePage();
            }

            String pageId = (String) pageData.get("page_id");
            String version = (String) pageData.get("version");

            // 2. 构建页面级配置
            Map<String, Object> pageConfig = buildPageConfig(pageData);

            // 3. 获取所有Tab配置
            List<Map<String, Object>> tabs = getTabsByPageAndVersion(pageId, version);

            // 4. 获取所有模块配置
            List<Map<String, Object>> sections = getSectionsByPageAndVersion(pageId, version);

            // 5. 获取金刚位配置
            List<Map<String, Object>> kingKongs = getKingKongsByPageAndVersion(pageId, version);

            // 6. 构建模块列表
            List<HomeSectionVO> sectionVOs = buildSectionVOs(sections, tabs, kingKongs);

            return HomePageVO.builder()
                    .pageId(pageId)
                    .pageName((String) pageData.get("page_name"))
                    .description("药品电商平台移动端首页")
                    .version(version)
                    .enabled(true)
                    .pageConfig(pageConfig)
                    .sections(sectionVOs)
                    .build();
        } catch (Exception e) {
            log.error("获取首页配置失败", e);
            return buildEmptyHomePage();
        }
    }

    @Override
    public List<HomePageVO> getConfigList() {
        log.info("获取首页配置列表");
        List<HomePageVO> configList = new ArrayList<>();
        configList.add(getHomePageRender());
        return configList;
    }

    /**
     * 获取当前发布的首页配置
     */
    private Map<String, Object> getCurrentPageConfig() {
        String sql = "SELECT * FROM dm_home_page WHERE is_current = 1 AND status = 1 AND is_deleted = 0 LIMIT 1";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 构建页面配置
     */
    private Map<String, Object> buildPageConfig(Map<String, Object> pageData) {
        Map<String, Object> config = new HashMap<>();
        config.put("bgColor", pageData.getOrDefault("bg_color", "#F5F5F5"));
        config.put("headerGradient", pageData.get("header_gradient"));
        config.put("stickyOpacity", pageData.getOrDefault("sticky_opacity", 0.95));
        config.put("enableLocation", true);
        config.put("enableSearchSticky", true);
        return config;
    }

    /**
     * 获取指定页面和版本的Tab列表
     */
    private List<Map<String, Object>> getTabsByPageAndVersion(String pageId, String version) {
        String sql = "SELECT * FROM dm_home_tab WHERE page_id = ? AND version = ? AND status = 1 AND is_deleted = 0 ORDER BY sort_order ASC";
        return jdbcTemplate.queryForList(sql, pageId, version);
    }

    /**
     * 获取指定页面和版本的模块列表
     */
    private List<Map<String, Object>> getSectionsByPageAndVersion(String pageId, String version) {
        String sql = "SELECT * FROM dm_home_section WHERE page_id = ? AND version = ? AND status = 1 AND visible = 1 AND is_deleted = 0 ORDER BY sort_order ASC";
        return jdbcTemplate.queryForList(sql, pageId, version);
    }

    /**
     * 获取指定页面和版本的金刚位列表
     */
    private List<Map<String, Object>> getKingKongsByPageAndVersion(String pageId, String version) {
        String sql = "SELECT * FROM dm_home_kingkong WHERE page_id = ? AND version = ? AND status = 1 AND is_deleted = 0 ORDER BY sort_order ASC";
        return jdbcTemplate.queryForList(sql, pageId, version);
    }

    /**
     * 构建模块VO列表
     */
    private List<HomeSectionVO> buildSectionVOs(List<Map<String, Object>> sections,
                                                 List<Map<String, Object>> tabs,
                                                 List<Map<String, Object>> kingKongs) {
        List<HomeSectionVO> sectionVOs = new ArrayList<>();

        for (Map<String, Object> section : sections) {
            HomeSectionVO sectionVO = buildSingleSectionVO(section, tabs, kingKongs);
            if (sectionVO != null) {
                sectionVOs.add(sectionVO);
            }
        }

        return sectionVOs;
    }

    /**
     * 构建单个模块VO
     */
    private HomeSectionVO buildSingleSectionVO(Map<String, Object> section,
                                                List<Map<String, Object>> tabs,
                                                List<Map<String, Object>> kingKongs) {
        try {
            String sectionType = (String) section.get("section_type");
            String sectionId = "section_" + sectionType;
            String name = (String) section.get("name");
            String subtitle = (String) section.get("subtitle");
            
            // 安全获取sortOrder，处理NULL情况
            Object sortOrderObj = section.get("sort_order");
            Integer sortOrder = sortOrderObj != null ? ((Number) sortOrderObj).intValue() : 0;
            
            // 解析tab_ids
            List<String> tabIds = parseTabIds((String) section.get("tab_ids"));

            // 解析config JSON
            Map<String, Object> config = parseJsonConfig((String) section.get("config"));
            if (config == null) {
                config = new HashMap<>();
            }

            // 解析content JSON
            Object contentData = parseJsonContent((String) section.get("content"));

            // 根据模块类型构建组件
            List<HomeComponentVO> components = buildComponents(sectionType, config, contentData, tabs, kingKongs);

            return HomeSectionVO.builder()
                    .sectionId(sectionId)
                    .sectionType(sectionType)
                    .title(name)
                    .subtitle(subtitle)
                    .layout(config.containsKey("layout") ? (String) config.get("layout") : "vertical")
                    .visible(true)
                    .sortOrder(sortOrder)
                    .config(config)
                    .tabIds(tabIds)
                    .components(components)
                    .build();
        } catch (Exception e) {
            log.error("构建模块VO失败: {}", section, e);
            return null;
        }
    }

    /**
     * 根据模块类型构建组件
     */
    private List<HomeComponentVO> buildComponents(String sectionType, Map<String, Object> config,
                                                    Object contentData, List<Map<String, Object>> tabs,
                                                    List<Map<String, Object>> kingKongs) {
        // 确保 data 不为 null，如果为 null 则使用空数组
        Object safeData = contentData;
        if (safeData == null) {
            // 根据模块类型决定返回空数组还是空对象
            if ("waterfall_layout".equals(sectionType) || "nearby_pharmacy".equals(sectionType)) {
                safeData = Collections.emptyList();
            } else {
                safeData = Collections.emptyMap();
            }
        }

        HomeComponentVO component = HomeComponentVO.builder()
                .componentId(sectionType + "_001")
                .componentType(sectionType)
                .config(config != null ? config : Collections.emptyMap())
                .data(safeData)
                .trackId("home_" + sectionType + "_click")
                .build();

        return Collections.singletonList(component);
    }

    /**
     * 解析JSON配置
     */
    private Map<String, Object> parseJsonConfig(String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return null;
        }
        try {
            return JSON.parseObject(jsonStr, Map.class);
        } catch (Exception e) {
            log.warn("解析配置JSON失败: {}", jsonStr, e);
            return null;
        }
    }

    /**
     * 解析JSON内容数据
     */
    private Object parseJsonContent(String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return null;
        }
        try {
            return JSON.parse(jsonStr);
        } catch (Exception e) {
            log.warn("解析内容JSON失败: {}", jsonStr, e);
            return null;
        }
    }

    /**
     * 解析Tab ID列表
     */
    private List<String> parseTabIds(String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return JSON.parseArray(jsonStr, String.class);
        } catch (Exception e) {
            log.warn("解析tab_ids失败: {}", jsonStr, e);
            return Collections.emptyList();
        }
    }

    /**
     * 构建空首页配置
     */
    private HomePageVO buildEmptyHomePage() {
        return HomePageVO.builder()
                .pageId("home_page_001")
                .pageName("DrugMall首页")
                .description("药品电商平台移动端首页")
                .version("v1.0.0")
                .enabled(false)
                .pageConfig(Collections.emptyMap())
                .sections(Collections.emptyList())
                .build();
    }
}
