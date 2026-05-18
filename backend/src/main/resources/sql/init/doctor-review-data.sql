-- =============================================
-- DrugMall 医生评价模块数据表
-- 包含：医生评价表、医生评价标签表
-- =============================================

USE `drugmall`;

-- =============================================
-- 1. 医生评价表
-- =============================================
DROP TABLE IF EXISTS `dm_doctor_review`;
CREATE TABLE `dm_doctor_review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `doctor_id` VARCHAR(20) NOT NULL COMMENT '医生ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `order_id` VARCHAR(20) DEFAULT NULL COMMENT '订单/问诊ID',
  `consultation_id` VARCHAR(20) DEFAULT NULL COMMENT '问诊ID',
  `rating` INT NOT NULL DEFAULT 5 COMMENT '评分 1-5',
  `satisfaction` VARCHAR(20) DEFAULT 'satisfied' COMMENT '满意度 very_satisfied/satisfied/neutral/dissatisfied',
  `content` TEXT COMMENT '评价内容',
  `is_anonymous` TINYINT NOT NULL DEFAULT 0 COMMENT '是否匿名 0-否 1-是',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-隐藏 1-显示',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_doctor_id` (`doctor_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_consultation_id` (`consultation_id`),
  KEY `idx_create_time` (`create_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生评价表';

-- =============================================
-- 2. 医生评价标签表
-- =============================================
DROP TABLE IF EXISTS `dm_doctor_review_tag`;
CREATE TABLE `dm_doctor_review_tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `doctor_id` VARCHAR(20) NOT NULL COMMENT '医生ID',
  `tag_name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `tag_type` VARCHAR(20) DEFAULT 'primary' COMMENT '标签类型 primary/success/warning/info',
  `tag_count` INT NOT NULL DEFAULT 0 COMMENT '标签出现次数',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_doctor_id` (`doctor_id`),
  KEY `idx_tag_count` (`tag_count` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生评价标签表';

-- =============================================
-- 3. 初始化医生评价数据
-- =============================================
INSERT INTO `dm_doctor_review` (`id`, `doctor_id`, `user_id`, `order_id`, `consultation_id`, `rating`, `satisfaction`, `content`, `is_anonymous`, `status`) VALUES
(1, 'DOC001', 1, 'ORD202403200001', 'CONS202403200001', 5, 'very_satisfied', '医生很专业，解答详细，态度非常好！', 0, 1),
(2, 'DOC001', 2, 'ORD202403200002', 'CONS202403200002', 5, 'very_satisfied', '耐心问诊，给的建议很有用', 0, 1),
(3, 'DOC001', 3, 'ORD202403200003', 'CONS202403200003', 4, 'satisfied', '回复挺快的，就是等待时间有点长', 0, 1),
(4, 'DOC001', 4, NULL, NULL, 5, 'very_satisfied', '非常满意，医生很有经验', 1, 1),
(5, 'DOC001', 5, 'ORD202403200005', 'CONS202403200005', 5, 'very_satisfied', '网上问诊很方便，解决了我的问题', 0, 1),
(6, 'DOC001', 1, 'ORD202403200006', 'CONS202403200006', 4, 'satisfied', '医生态度很好，给开了处方', 0, 1),
(7, 'DOC001', 2, NULL, NULL, 5, 'very_satisfied', '专业、耐心、回复快！', 1, 1),
(8, 'DOC001', 6, 'ORD202403200007', 'CONS202403200007', 5, 'very_satisfied', '很好的医生，强烈推荐！', 0, 1);

INSERT INTO `dm_doctor_review_tag` (`id`, `doctor_id`, `tag_name`, `tag_type`, `tag_count`, `status`) VALUES
(1, 'DOC001', '专业', 'primary', 128, 1),
(2, 'DOC001', '耐心', 'success', 96, 1),
(3, 'DOC001', '回复快', 'warning', 85, 1),
(4, 'DOC001', '态度好', 'info', 72, 1),
(5, 'DOC001', '建议有效', 'success', 65, 1);
