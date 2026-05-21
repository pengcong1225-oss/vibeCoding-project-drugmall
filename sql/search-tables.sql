-- 搜索相关表 DDL
-- DrugMall 搜索模块依赖表

-- 搜索建议表
DROP TABLE IF EXISTS `dm_search_suggestion`;
CREATE TABLE `dm_search_suggestion` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `keyword` VARCHAR(100) NOT NULL COMMENT '关键词',
  `keyword_pinyin` VARCHAR(200) DEFAULT NULL COMMENT '拼音',
  `type` VARCHAR(50) DEFAULT 'drug' COMMENT '类型：drug药品，symptom症状，store药店',
  `related_id` VARCHAR(50) DEFAULT NULL COMMENT '关联ID',
  `search_count` INT DEFAULT 0 COMMENT '搜索次数',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` INT DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_keyword` (`keyword`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索建议表';

-- 热门搜索表
DROP TABLE IF EXISTS `dm_hot_search`;
CREATE TABLE `dm_hot_search` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `keyword` VARCHAR(100) NOT NULL COMMENT '关键词',
  `keyword_pinyin` VARCHAR(200) DEFAULT NULL COMMENT '拼音',
  `search_count` INT DEFAULT 0 COMMENT '总搜索次数',
  `daily_count` INT DEFAULT 0 COMMENT '今日搜索次数',
  `weekly_count` INT DEFAULT 0 COMMENT '本周搜索次数',
  `is_hot` INT DEFAULT 1 COMMENT '是否热门：1是 0否',
  `is_new` INT DEFAULT 0 COMMENT '是否新品：1是 0否',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` INT DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_keyword` (`keyword`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热门搜索表';

-- 搜索历史表
DROP TABLE IF EXISTS `dm_search_history`;
CREATE TABLE `dm_search_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `keyword` VARCHAR(100) NOT NULL COMMENT '关键词',
  `search_type` VARCHAR(50) DEFAULT 'drug' COMMENT '类型',
  `result_count` INT DEFAULT 0 COMMENT '搜索结果数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_keyword` (`keyword`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索历史表';

-- 搜索筛选器表
DROP TABLE IF EXISTS `dm_search_filter`;
CREATE TABLE `dm_search_filter` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `filter_type` VARCHAR(50) NOT NULL COMMENT '筛选类型：category分类，brand品牌，price价格区间',
  `name` VARCHAR(100) NOT NULL COMMENT '名称',
  `value` VARCHAR(200) NOT NULL COMMENT '值',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父ID（用于分类层级）',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `icon` VARCHAR(200) DEFAULT NULL COMMENT '图标',
  `bg_color` VARCHAR(50) DEFAULT NULL COMMENT '背景色',
  `is_rx` INT DEFAULT 0 COMMENT '是否处方药',
  `count` INT DEFAULT 0 COMMENT '商品数量',
  `status` INT DEFAULT 1 COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_filter_type` (`filter_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索筛选器表';

-- 种子数据：搜索建议
INSERT INTO `dm_search_suggestion` (`keyword`, `keyword_pinyin`, `type`, `search_count`, `sort_order`, `status`) VALUES
('感冒药', 'ganmaoyao', 'drug', 1580, 1, 1),
('退烧药', 'tuishaoyao', 'drug', 1250, 2, 1),
('消炎药', 'xiaoyanyao', 'drug', 980, 3, 1),
('阿莫西林', 'amoxilin', 'drug', 860, 4, 1),
('布洛芬', 'buluofen', 'drug', 720, 5, 1),
('血压计', 'xueyaji', 'instrument', 650, 6, 1),
('口罩', 'kouzhao', 'instrument', 2100, 7, 1),
('维生素', 'weishengsu', 'health', 890, 8, 1),
('高血压', 'gaoxueya', 'symptom', 540, 9, 1),
('糖尿病', 'tangniaobing', 'symptom', 480, 10, 1);

-- 种子数据：热门搜索
INSERT INTO `dm_hot_search` (`keyword`, `keyword_pinyin`, `search_count`, `daily_count`, `weekly_count`, `is_hot`, `is_new`, `sort_order`, `status`) VALUES
('口罩', 'kouzhao', 2100, 85, 420, 1, 0, 1, 1),
('感冒药', 'ganmaoyao', 1580, 65, 310, 1, 0, 2, 1),
('退烧药', 'tuishaoyao', 1250, 52, 280, 1, 0, 3, 1),
('甲流', 'jialiu', 1100, 120, 560, 1, 1, 4, 1),
('消炎药', 'xiaoyanyao', 980, 38, 195, 1, 0, 5, 1),
('维生素', 'weishengsu', 890, 42, 200, 1, 0, 6, 1),
('阿莫西林', 'amoxilin', 860, 30, 155, 0, 0, 7, 1),
('布洛芬', 'buluofen', 720, 28, 140, 0, 0, 8, 1),
('血压计', 'xueyaji', 650, 25, 120, 0, 0, 9, 1),
('抗原检测', 'kangyuanjiance', 600, 55, 290, 1, 1, 10, 1);
