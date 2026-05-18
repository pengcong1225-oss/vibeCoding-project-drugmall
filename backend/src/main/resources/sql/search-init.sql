-- 数据库表结构补全（使用 IF NOT EXISTS 避免重复创建）

-- 为 dm_order_item 补充 review_status 列
ALTER TABLE dm_order_item ADD COLUMN `review_status` VARCHAR(20) DEFAULT NULL COMMENT '评价状态: pending/ reviewed';
ALTER TABLE dm_order ADD COLUMN `prescription_id` BIGINT DEFAULT NULL COMMENT '处方ID';
ALTER TABLE dm_order ADD COLUMN `cancel_reason` VARCHAR(500) DEFAULT NULL COMMENT '取消原因';
ALTER TABLE dm_order ADD COLUMN `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间';
ALTER TABLE dm_order ADD COLUMN `delivery_company` VARCHAR(100) DEFAULT NULL COMMENT '物流公司';
ALTER TABLE dm_order ADD COLUMN `delivery_no` VARCHAR(100) DEFAULT NULL COMMENT '物流单号';
ALTER TABLE dm_order ADD COLUMN `delivery_time` DATETIME DEFAULT NULL COMMENT '发货时间';
ALTER TABLE dm_order ADD COLUMN `receive_time` DATETIME DEFAULT NULL COMMENT '收货时间';
ALTER TABLE dm_order ADD COLUMN `update_time` DATETIME DEFAULT NULL COMMENT '更新时间';
ALTER TABLE dm_order ADD COLUMN `is_deleted` INT DEFAULT 0 COMMENT '逻辑删除';

-- 搜索相关表自动初始化
CREATE TABLE IF NOT EXISTS `dm_search_suggestion` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `keyword` VARCHAR(100) NOT NULL,
  `keyword_pinyin` VARCHAR(200) DEFAULT NULL,
  `type` VARCHAR(50) DEFAULT 'drug',
  `related_id` VARCHAR(50) DEFAULT NULL,
  `search_count` INT DEFAULT 0,
  `sort_order` INT DEFAULT 0,
  `status` INT DEFAULT 1,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_keyword` (`keyword`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `dm_hot_search` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `keyword` VARCHAR(100) NOT NULL,
  `keyword_pinyin` VARCHAR(200) DEFAULT NULL,
  `search_count` INT DEFAULT 0,
  `daily_count` INT DEFAULT 0,
  `weekly_count` INT DEFAULT 0,
  `is_hot` INT DEFAULT 1,
  `is_new` INT DEFAULT 0,
  `sort_order` INT DEFAULT 0,
  `status` INT DEFAULT 1,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `dm_search_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `keyword` VARCHAR(100) NOT NULL,
  `search_type` VARCHAR(50) DEFAULT 'drug',
  `result_count` INT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `dm_search_filter` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `filter_type` VARCHAR(50) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `value` VARCHAR(200) NOT NULL,
  `parent_id` BIGINT DEFAULT NULL,
  `sort_order` INT DEFAULT 0,
  `icon` VARCHAR(200) DEFAULT NULL,
  `bg_color` VARCHAR(50) DEFAULT NULL,
  `is_rx` INT DEFAULT 0,
  `count` INT DEFAULT 0,
  `status` INT DEFAULT 1,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 种子数据（使用 INSERT IGNORE 避免重复插入）
INSERT IGNORE INTO `dm_search_suggestion` (`keyword`, `keyword_pinyin`, `type`, `search_count`, `sort_order`, `status`) VALUES
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

INSERT IGNORE INTO `dm_hot_search` (`keyword`, `keyword_pinyin`, `search_count`, `daily_count`, `weekly_count`, `is_hot`, `is_new`, `sort_order`, `status`) VALUES
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
