-- =============================================
-- DrugMall 搜索模块数据库表
-- 包含：搜索历史、热门搜索、搜索筛选条件缓存
-- =============================================

USE `drugmall`;

-- =============================================
-- 1. 用户搜索历史表
-- =============================================
DROP TABLE IF EXISTS `dm_search_history`;
CREATE TABLE `dm_search_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `keyword` VARCHAR(100) NOT NULL COMMENT '搜索关键词',
  `search_type` VARCHAR(20) DEFAULT 'drug' COMMENT '搜索类型 drug/category/store',
  `result_count` INT DEFAULT 0 COMMENT '搜索结果数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '搜索时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_create_time` (`user_id`, `create_time` DESC),
  KEY `idx_keyword` (`keyword`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户搜索历史表';

-- =============================================
-- 2. 热门搜索表
-- =============================================
DROP TABLE IF EXISTS `dm_hot_search`;
CREATE TABLE `dm_hot_search` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `keyword` VARCHAR(100) NOT NULL COMMENT '搜索关键词',
  `keyword_pinyin` VARCHAR(200) DEFAULT NULL COMMENT '关键词拼音',
  `search_count` INT NOT NULL DEFAULT 0 COMMENT '搜索次数',
  `daily_count` INT NOT NULL DEFAULT 0 COMMENT '今日搜索次数',
  `weekly_count` INT NOT NULL DEFAULT 0 COMMENT '本周搜索次数',
  `is_hot` TINYINT NOT NULL DEFAULT 0 COMMENT '是否热门 0-否 1-是',
  `is_new` TINYINT NOT NULL DEFAULT 0 COMMENT '是否新词 0-否 1-是',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_sort_order` (`sort_order` DESC),
  KEY `idx_search_count` (`search_count` DESC),
  KEY `idx_daily_count` (`daily_count` DESC),
  KEY `idx_keyword` (`keyword`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热门搜索表';

-- =============================================
-- 3. 搜索建议表
-- =============================================
DROP TABLE IF EXISTS `dm_search_suggestion`;
CREATE TABLE `dm_search_suggestion` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `keyword` VARCHAR(100) NOT NULL COMMENT '关键词',
  `keyword_pinyin` VARCHAR(200) DEFAULT NULL COMMENT '关键词拼音',
  `type` VARCHAR(20) NOT NULL COMMENT '类型 drug/category/brand/symptom',
  `related_id` VARCHAR(50) DEFAULT NULL COMMENT '关联ID（如药品ID、分类ID）',
  `search_count` INT NOT NULL DEFAULT 0 COMMENT '搜索次数',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_keyword` (`keyword`),
  KEY `idx_type` (`type`),
  KEY `idx_sort_order` (`sort_order` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索建议表';

-- =============================================
-- 4. 搜索筛选条件缓存表
-- =============================================
DROP TABLE IF EXISTS `dm_search_filter`;
CREATE TABLE `dm_search_filter` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `filter_type` VARCHAR(30) NOT NULL COMMENT '筛选类型 category/brand/price_range',
  `name` VARCHAR(100) NOT NULL COMMENT '筛选名称',
  `value` VARCHAR(50) NOT NULL COMMENT '筛选值/ID',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父级ID（用于分类层级）',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `icon` VARCHAR(500) DEFAULT NULL COMMENT '图标URL',
  `bg_color` VARCHAR(20) DEFAULT NULL COMMENT '背景颜色',
  `is_rx` TINYINT DEFAULT NULL COMMENT '是否处方药筛选',
  `count` INT NOT NULL DEFAULT 0 COMMENT '商品数量',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_filter_type` (`filter_type`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_sort_order` (`sort_order` ASC),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索筛选条件表';

-- =============================================
-- 5. 初始化热门搜索数据
-- =============================================
INSERT INTO `dm_hot_search` (`keyword`, `keyword_pinyin`, `search_count`, `daily_count`, `weekly_count`, `is_hot`, `is_new`, `sort_order`, `status`) VALUES
('感冒灵颗粒', 'ganmaoingkeli', 9800, 320, 2100, 1, 0, 1, 1),
('布洛芬缓释胶囊', 'buluofenhuanshipjiaonang', 8560, 280, 1950, 1, 0, 2, 1),
('阿莫西林胶囊', 'amoxilinjiaonang', 7230, 250, 1680, 1, 0, 3, 1),
('维生素C片', 'weishengsuCpian', 6890, 220, 1520, 1, 0, 4, 1),
('板蓝根颗粒', 'banlangenkeli', 6540, 210, 1450, 1, 0, 5, 1),
('头孢克肟分散片', 'toubaokewofensanpian', 5890, 190, 1320, 1, 0, 6, 1),
('止咳糖浆', 'zhiketangjiang', 5230, 180, 1180, 0, 0, 7, 1),
('创可贴', 'chuangketie', 4860, 160, 1050, 0, 0, 8, 1),
('体温计', 'tiwenji', 4520, 150, 980, 0, 0, 9, 1),
('口罩', 'kouzhao', 9980, 450, 3200, 1, 0, 10, 1);

-- =============================================
-- 6. 初始化搜索建议数据
-- =============================================
INSERT INTO `dm_search_suggestion` (`keyword`, `keyword_pinyin`, `type`, `related_id`, `search_count`, `sort_order`, `status`) VALUES
('感冒灵颗粒', 'ganmaoingkeli', 'drug', '1', 9800, 1, 1),
('感冒', 'ganmao', 'symptom', NULL, 5200, 2, 1),
('感冒用药', 'ganmaoyongyao', 'category', 'cat_002', 3500, 3, 1),
('布洛芬', 'buluofen', 'drug', '2', 4500, 4, 1),
('布洛芬缓释胶囊', 'buluofenhuanshipjiaonang', 'drug', '2', 8560, 5, 1),
('头孢', 'toubao', 'symptom', NULL, 3200, 6, 1),
('头孢克肟分散片', 'toubaokewofensanpian', 'drug', '6', 5890, 7, 1),
('阿莫西林', 'amoxilin', 'drug', '5', 4200, 8, 1),
('阿莫西林胶囊', 'amoxilinjiaonang', 'drug', '5', 7230, 9, 1),
('阿奇霉素', 'aqimeisu', 'drug', '4', 3800, 10, 1),
('维生素C', 'weishengsuC', 'drug', '7', 4100, 11, 1),
('维生素C片', 'weishengsuCpian', 'drug', '7', 6890, 12, 1),
('三九医药', 'sanjiuyiyao', 'brand', 'brand_002', 2800, 13, 1),
('白云山', 'baiyunshan', 'brand', 'brand_003', 2600, 14, 1),
('胃痛', 'weitong', 'symptom', NULL, 2900, 15, 1),
('奥美拉唑', 'aomeilazuo', 'drug', '9', 3100, 16, 1),
('退烧', 'tuishao', 'symptom', NULL, 2400, 17, 1),
('咳嗽', 'kesou', 'symptom', NULL, 3800, 18, 1),
('止咳', 'zhike', 'symptom', NULL, 2200, 19, 1),
('止咳糖浆', 'zhiketangjiang', 'drug', '3', 5230, 20, 1),
('口罩', 'kouzhao', 'drug', '16', 9980, 21, 1),
('体温计', 'tiwenji', 'drug', '15', 4520, 22, 1),
('消毒液', 'xiaoduye', 'drug', NULL, 2100, 23, 1),
('血压计', 'xueyaji', 'drug', NULL, 1800, 24, 1),
('红霉素软膏', 'hongmeisuruangao', 'drug', '13', 3200, 25, 1);

-- =============================================
-- 7. 初始化搜索筛选条件（从药品分类表dm_drug_category关联）
-- =============================================
INSERT INTO `dm_search_filter` (`filter_type`, `name`, `value`, `parent_id`, `sort_order`, `is_rx`, `status`) VALUES
('category', '全部', 'cat_all', NULL, 1, NULL, 1),
('category', '感冒用药', 'cat_002', NULL, 2, NULL, 1),
('category', '止痛镇痛', 'cat_003', NULL, 3, NULL, 1),
('category', '消化系统', 'cat_004', NULL, 4, NULL, 1),
('category', '心血管', 'cat_005', NULL, 5, NULL, 1),
('category', '皮肤外用', 'cat_006', NULL, 6, NULL, 1),
('category', '维生素补充', 'cat_007', NULL, 7, NULL, 1),
('category', '医疗器械', 'cat_008', NULL, 8, NULL, 1);

-- 品牌筛选
INSERT INTO `dm_search_filter` (`filter_type`, `name`, `value`, `sort_order`, `status`) VALUES
('brand', '全部', 'brand_all', 1, 1),
('brand', '三九医药', 'brand_002', 2, 1),
('brand', '白云山', 'brand_003', 3, 1),
('brand', '同仁堂', 'brand_004', 4, 1),
('brand', '云南白药', 'brand_005', 5, 1),
('brand', '葵花药业', 'brand_006', 6, 1),
('brand', '仁和药业', 'brand_007', 7, 1),
('brand', '扬子江', 'brand_008', 8, 1);

-- 价格区间筛选
INSERT INTO `dm_search_filter` (`filter_type`, `name`, `value`, `sort_order`, `status`) VALUES
('price_range', '全部价格', 'price_all', 1, 1),
('price_range', '10元以下', 'price_0_10', 2, 1),
('price_range', '10-30元', 'price_10_30', 3, 1),
('price_range', '30-50元', 'price_30_50', 4, 1),
('price_range', '50-100元', 'price_50_100', 5, 1),
('price_range', '100元以上', 'price_100_plus', 6, 1);
