-- =============================================
-- DrugMall 数据库完整初始化脚本
-- 包含表结构定义和模拟数据
-- 字符集: utf8mb4
-- 生成时间: 2026-04-23
-- =============================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- =============================================
-- 创建数据库
-- =============================================
CREATE DATABASE IF NOT EXISTS `drugmall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `drugmall`;

-- =============================================
-- 第一部分：表结构定义
-- 按照依赖顺序排列
-- =============================================

-- =============================================
-- 一、基础配置表（无外键依赖）
-- =============================================

-- 1.1 系统配置表
DROP TABLE IF EXISTS `dm_system_config`;
CREATE TABLE `dm_system_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT COMMENT '配置值',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 1.2 管理员表
DROP TABLE IF EXISTS `dm_admin`;
CREATE TABLE `dm_admin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 1.3 角色表
DROP TABLE IF EXISTS `dm_role`;
CREATE TABLE `dm_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 1.4 权限表
DROP TABLE IF EXISTS `dm_permission`;
CREATE TABLE `dm_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` VARCHAR(50) NOT NULL COMMENT '权限名称',
  `code` VARCHAR(100) NOT NULL COMMENT '权限编码',
  `type` VARCHAR(20) NOT NULL COMMENT '类型 menu-菜单 button-按钮',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父权限ID',
  `path` VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
  `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- =============================================
-- 二、用户相关表
-- =============================================

-- 2.1 用户表
DROP TABLE IF EXISTS `dm_user`;
CREATE TABLE `dm_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `password` VARCHAR(255) DEFAULT NULL COMMENT '密码',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `gender` TINYINT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
  `birthday` DATE DEFAULT NULL COMMENT '生日',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
  `is_real_name_auth` TINYINT DEFAULT 0 COMMENT '是否实名认证 0-未认证 1-已认证',
  `balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额',
  `points` INT DEFAULT 0 COMMENT '积分',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2.2 用户地址表
DROP TABLE IF EXISTS `dm_user_address`;
CREATE TABLE `dm_user_address` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  `phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
  `province` VARCHAR(50) NOT NULL COMMENT '省',
  `city` VARCHAR(50) NOT NULL COMMENT '市',
  `district` VARCHAR(50) NOT NULL COMMENT '区',
  `detail` VARCHAR(200) NOT NULL COMMENT '详细地址',
  `tag` VARCHAR(20) DEFAULT NULL COMMENT '标签 家/公司',
  `is_default` TINYINT DEFAULT 0 COMMENT '是否默认地址 0-否 1-是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_default` (`is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

-- 2.3 患者档案表
DROP TABLE IF EXISTS `dm_patient`;
CREATE TABLE `dm_patient` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '患者ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '患者姓名',
  `gender` TINYINT NOT NULL COMMENT '性别 1-男 2-女',
  `birthday` DATE DEFAULT NULL COMMENT '出生日期',
  `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `relationship` VARCHAR(20) DEFAULT NULL COMMENT '与用户关系 本人/配偶/子女/父母',
  `allergy_history` VARCHAR(500) DEFAULT NULL COMMENT '过敏史',
  `medical_history` VARCHAR(500) DEFAULT NULL COMMENT '病史',
  `is_default` TINYINT DEFAULT 0 COMMENT '是否默认 0-否 1-是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者档案表';

-- =============================================
-- 三、医生相关表
-- =============================================

-- 3.1 科室表
DROP TABLE IF EXISTS `dm_department`;
CREATE TABLE `dm_department` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '科室ID',
  `code` VARCHAR(50) NOT NULL COMMENT '科室编码',
  `name` VARCHAR(50) NOT NULL COMMENT '科室名称',
  `icon` VARCHAR(50) DEFAULT NULL COMMENT '图标标识',
  `tag` VARCHAR(20) DEFAULT NULL COMMENT '标签文字',
  `tag_type` VARCHAR(20) DEFAULT 'info' COMMENT '标签类型 hot/fever/price/supplement/info',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父科室ID 0-顶级科室',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室表';

-- 3.2 医生表
DROP TABLE IF EXISTS `dm_doctor`;
CREATE TABLE `dm_doctor` (
  `id` VARCHAR(20) NOT NULL COMMENT '医生ID',
  `his_code` VARCHAR(50) DEFAULT NULL COMMENT 'HIS系统编码',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像',
  `gender` TINYINT DEFAULT 1 COMMENT '性别 1-男 2-女',
  `title` VARCHAR(50) DEFAULT NULL COMMENT '职称',
  `hospital` VARCHAR(100) DEFAULT NULL COMMENT '医院',
  `department_id` BIGINT DEFAULT NULL COMMENT '科室ID',
  `department` VARCHAR(50) DEFAULT NULL COMMENT '科室名称',
  `license_no` VARCHAR(50) DEFAULT NULL COMMENT '执业证号',
  `is_certified` TINYINT DEFAULT 0 COMMENT '是否认证 0-未认证 1-已认证',
  `rating` DECIMAL(2,1) DEFAULT 5.0 COMMENT '评分',
  `service_count` INT DEFAULT 0 COMMENT '服务次数',
  `response_time` INT DEFAULT 5 COMMENT '平均响应时间(分钟)',
  `specialties` VARCHAR(200) DEFAULT NULL COMMENT '擅长领域',
  `introduction` TEXT COMMENT '个人简介',
  `balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_his_code` (`his_code`),
  KEY `idx_department_id` (`department_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_certified` (`is_certified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生表';

-- 3.3 医生排班表
DROP TABLE IF EXISTS `dm_doctor_schedule`;
CREATE TABLE `dm_doctor_schedule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '排班ID',
  `doctor_id` VARCHAR(20) NOT NULL COMMENT '医生ID',
  `day_of_week` TINYINT NOT NULL COMMENT '星期几 1-7',
  `morning` TINYINT DEFAULT 0 COMMENT '上午 0-休息 1-上班',
  `afternoon` TINYINT DEFAULT 0 COMMENT '下午 0-休息 1-上班',
  `evening` TINYINT DEFAULT 0 COMMENT '晚上 0-休息 1-上班',
  `max_consultations` INT DEFAULT 20 COMMENT '最大接诊数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_doctor_day` (`doctor_id`, `day_of_week`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生排班表';

-- =============================================
-- 四、药品相关表
-- =============================================

-- 4.1 药品分类表
DROP TABLE IF EXISTS `dm_category`;
CREATE TABLE `dm_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `code` VARCHAR(50) DEFAULT NULL COMMENT '分类编码',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID 0-顶级分类',
  `level` TINYINT DEFAULT 1 COMMENT '分类层级 1-一级 2-二级 3-三级',
  `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
  `image` VARCHAR(500) DEFAULT NULL COMMENT '分类图片',
  `type` VARCHAR(20) DEFAULT 'drug' COMMENT '分类类型 drug-药品 store-门店 symptom-症状',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_code` (`code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_type` (`type`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品分类表';

-- 4.2 品牌表
DROP TABLE IF EXISTS `dm_brand`;
CREATE TABLE `dm_brand` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '品牌ID',
  `name` VARCHAR(100) NOT NULL COMMENT '品牌名称',
  `logo` VARCHAR(500) DEFAULT NULL COMMENT '品牌Logo',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '品牌描述',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='品牌表';

-- 4.3 药品表
DROP TABLE IF EXISTS `dm_product`;
CREATE TABLE `dm_product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '药品ID',
  `product_code` VARCHAR(50) NOT NULL COMMENT '药品编码',
  `product_name` VARCHAR(200) NOT NULL COMMENT '药品名称',
  `generic_name` VARCHAR(200) DEFAULT NULL COMMENT '通用名',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `brand_id` BIGINT DEFAULT NULL COMMENT '品牌ID',
  `main_image` VARCHAR(500) DEFAULT NULL COMMENT '主图',
  `images` TEXT COMMENT '图片列表JSON',
  `specification` VARCHAR(100) DEFAULT NULL COMMENT '规格',
  `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
  `manufacturer` VARCHAR(200) DEFAULT NULL COMMENT '生产厂家',
  `brand` VARCHAR(100) DEFAULT NULL COMMENT '品牌名称',
  `approval_number` VARCHAR(100) DEFAULT NULL COMMENT '批准文号',
  `bar_code` VARCHAR(50) DEFAULT NULL COMMENT '条形码',
  `medical_insurance_code` VARCHAR(50) DEFAULT NULL COMMENT '医保编码',
  `price` DECIMAL(10,2) NOT NULL COMMENT '售价',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
  `warning_stock` INT DEFAULT 10 COMMENT '库存预警值',
  `sales` INT DEFAULT 0 COMMENT '销量',
  `is_rx` TINYINT NOT NULL DEFAULT 0 COMMENT '是否处方药 0-非处方 1-处方',
  `is_national_essential` TINYINT DEFAULT 0 COMMENT '是否国家基药 0-否 1-是',
  `is_long_prescription` TINYINT DEFAULT 0 COMMENT '是否长处方用药 0-否 1-是',
  `insurance_category` VARCHAR(10) DEFAULT NULL COMMENT '医保类别: 甲类/乙类/丙类',
  `usage` VARCHAR(500) DEFAULT NULL COMMENT '用法用量',
  `disease` VARCHAR(500) DEFAULT NULL COMMENT '适应症/主治疾病',
  `contraindications` VARCHAR(500) DEFAULT NULL COMMENT '禁忌症',
  `precautions` VARCHAR(500) DEFAULT NULL COMMENT '注意事项',
  `adverse_reactions` VARCHAR(500) DEFAULT NULL COMMENT '不良反应',
  `storage` VARCHAR(200) DEFAULT NULL COMMENT '贮藏条件',
  `validity` VARCHAR(50) DEFAULT NULL COMMENT '有效期',
  `ingredients` VARCHAR(500) DEFAULT NULL COMMENT '成份',
  `appearance` VARCHAR(200) DEFAULT NULL COMMENT '性状',
  `drug_interactions` VARCHAR(500) DEFAULT NULL COMMENT '药物相互作用',
  `description` TEXT COMMENT '药品详情',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-下架 1-上架',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_code` (`product_code`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_brand_id` (`brand_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_rx` (`is_rx`),
  KEY `idx_sales` (`sales`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品表';

-- 4.4 药品规格表
DROP TABLE IF EXISTS `dm_product_specification`;
CREATE TABLE `dm_product_specification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '规格ID',
  `product_id` BIGINT NOT NULL COMMENT '药品ID',
  `spec_name` VARCHAR(100) NOT NULL COMMENT '规格名称',
  `spec_code` VARCHAR(50) DEFAULT NULL COMMENT '规格编码',
  `price` DECIMAL(10,2) NOT NULL COMMENT '售价',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
  `bar_code` VARCHAR(50) DEFAULT NULL COMMENT '条形码',
  `is_default` TINYINT DEFAULT 0 COMMENT '是否默认规格 0-否 1-是',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品规格表';

-- =============================================
-- 五、门店相关表
-- =============================================

-- 5.1 门店表
DROP TABLE IF EXISTS `dm_store`;
CREATE TABLE `dm_store` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '门店ID',
  `store_code` VARCHAR(50) NOT NULL COMMENT '门店编码',
  `store_name` VARCHAR(200) NOT NULL COMMENT '门店名称',
  `logo` VARCHAR(500) DEFAULT NULL COMMENT '门店Logo',
  `logo_text` VARCHAR(50) DEFAULT NULL COMMENT 'Logo文字',
  `logo_color` VARCHAR(20) DEFAULT NULL COMMENT 'Logo背景色',
  `rating` DECIMAL(2,1) DEFAULT 5.0 COMMENT '评分',
  `monthly_sales` INT DEFAULT 0 COMMENT '月销量',
  `address` VARCHAR(300) NOT NULL COMMENT '门店地址',
  `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `business_hours` VARCHAR(100) DEFAULT NULL COMMENT '营业时间',
  `is_open` TINYINT DEFAULT 1 COMMENT '是否营业 0-休息 1-营业',
  `is_24hours` TINYINT DEFAULT 0 COMMENT '是否24小时营业 0-否 1-是',
  `latitude` DECIMAL(10,7) DEFAULT NULL COMMENT '纬度',
  `longitude` DECIMAL(10,7) DEFAULT NULL COMMENT '经度',
  `description` TEXT COMMENT '门店简介',
  `business_scope` VARCHAR(500) DEFAULT NULL COMMENT '经营范围',
  `license_no` VARCHAR(100) DEFAULT NULL COMMENT '经营许可证号',
  `is_insurance` TINYINT DEFAULT 0 COMMENT '是否医保定点 0-否 1-是',
  `is_chain` TINYINT DEFAULT 0 COMMENT '是否连锁品牌 0-否 1-是',
  `is_self_operated` TINYINT DEFAULT 0 COMMENT '是否自营 0-否 1-是',
  `delivery_time` INT DEFAULT 30 COMMENT '预计配送时间(分钟)',
  `min_delivery_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '最低配送金额',
  `delivery_fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '配送费',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-停用 1-正常',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_store_code` (`store_code`),
  KEY `idx_status` (`status`),
  KEY `idx_is_open` (`is_open`),
  KEY `idx_rating` (`rating`),
  KEY `idx_monthly_sales` (`monthly_sales`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店表';

-- 5.2 门店资质认证表
DROP TABLE IF EXISTS `dm_store_certification`;
CREATE TABLE `dm_store_certification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '认证ID',
  `store_id` BIGINT NOT NULL COMMENT '门店ID',
  `cert_name` VARCHAR(100) NOT NULL COMMENT '资质名称',
  `cert_type` VARCHAR(50) DEFAULT NULL COMMENT '资质类型',
  `cert_no` VARCHAR(100) DEFAULT NULL COMMENT '证书编号',
  `issue_date` DATE DEFAULT NULL COMMENT '发证日期',
  `expire_date` DATE DEFAULT NULL COMMENT '到期日期',
  `cert_image` VARCHAR(500) DEFAULT NULL COMMENT '证书图片',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-过期 1-有效',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_store_id` (`store_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店资质认证表';

-- 5.3 门店服务承诺表
DROP TABLE IF EXISTS `dm_store_promise`;
CREATE TABLE `dm_store_promise` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '承诺ID',
  `store_id` BIGINT NOT NULL COMMENT '门店ID',
  `promise_text` VARCHAR(200) NOT NULL COMMENT '承诺内容',
  `promise_type` VARCHAR(50) DEFAULT NULL COMMENT '承诺类型',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店服务承诺表';

-- 5.4 门店药品库存表
DROP TABLE IF EXISTS `dm_store_inventory`;
CREATE TABLE `dm_store_inventory` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '库存ID',
  `store_id` BIGINT NOT NULL COMMENT '门店ID',
  `product_id` BIGINT NOT NULL COMMENT '药品ID',
  `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
  `warning_stock` INT DEFAULT 10 COMMENT '库存预警值',
  `price` DECIMAL(10,2) NOT NULL COMMENT '门店售价',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  `discount` INT DEFAULT 0 COMMENT '折扣百分比',
  `is_available` TINYINT DEFAULT 1 COMMENT '是否可售 0-不可售 1-可售',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_store_product` (`store_id`, `product_id`),
  KEY `idx_store_id` (`store_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_is_available` (`is_available`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店药品库存表';

-- 5.5 门店标签表
DROP TABLE IF EXISTS `dm_store_tag`;
CREATE TABLE `dm_store_tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `store_id` BIGINT NOT NULL COMMENT '门店ID',
  `tag_text` VARCHAR(50) NOT NULL COMMENT '标签文字',
  `tag_type` VARCHAR(20) DEFAULT 'info' COMMENT '标签类型 primary/success/warning/danger/info',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店标签表';

-- 5.6 门店评价表
DROP TABLE IF EXISTS `dm_store_review`;
CREATE TABLE `dm_store_review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `store_id` BIGINT NOT NULL COMMENT '门店ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `order_id` BIGINT DEFAULT NULL COMMENT '订单ID',
  `rating` TINYINT NOT NULL COMMENT '评分 1-5星',
  `content` VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
  `images` TEXT COMMENT '评价图片JSON',
  `is_anonymous` TINYINT DEFAULT 0 COMMENT '是否匿名 0-否 1-是',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-隐藏 1-显示',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_store_id` (`store_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_rating` (`rating`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店评价表';

-- =============================================
-- 六、购物车表
-- =============================================

DROP TABLE IF EXISTS `dm_cart`;
CREATE TABLE `dm_cart` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `product_id` BIGINT NOT NULL COMMENT '药品ID',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
  `selected` TINYINT DEFAULT 1 COMMENT '是否选中 0-否 1-是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- =============================================
-- 七、优惠券表
-- =============================================

-- 7.1 优惠券表
DROP TABLE IF EXISTS `dm_coupon`;
CREATE TABLE `dm_coupon` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
  `name` VARCHAR(100) NOT NULL COMMENT '优惠券名称',
  `type` VARCHAR(20) NOT NULL COMMENT '类型 full_reduction-满减 discount-折扣',
  `value` DECIMAL(10,2) NOT NULL COMMENT '优惠值',
  `min_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '最低消费金额',
  `total_count` INT NOT NULL COMMENT '发放总量',
  `used_count` INT DEFAULT 0 COMMENT '已使用数量',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '使用说明',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- 7.2 用户优惠券表
DROP TABLE IF EXISTS `dm_user_coupon`;
CREATE TABLE `dm_user_coupon` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户优惠券ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `coupon_id` BIGINT NOT NULL COMMENT '优惠券ID',
  `status` VARCHAR(20) DEFAULT 'unused' COMMENT '状态 unused-未使用 used-已使用 expired-已过期',
  `use_time` DATETIME DEFAULT NULL COMMENT '使用时间',
  `order_id` BIGINT DEFAULT NULL COMMENT '使用的订单ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_coupon_id` (`coupon_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- =============================================
-- 八、订单相关表
-- =============================================

-- 8.1 订单表
DROP TABLE IF EXISTS `dm_order`;
CREATE TABLE `dm_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `address_id` BIGINT NOT NULL COMMENT '地址ID',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总额',
  `discount_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
  `freight_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费',
  `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
  `pay_type` TINYINT DEFAULT NULL COMMENT '支付方式 1-微信 2-支付宝',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态 0-待付款 1-待发货 2-待收货 3-待评价 4-已完成 -1-已取消 -2-退款中 -3-已退款',
  `delivery_company` VARCHAR(50) DEFAULT NULL COMMENT '快递公司',
  `delivery_no` VARCHAR(100) DEFAULT NULL COMMENT '快递单号',
  `delivery_time` DATETIME DEFAULT NULL COMMENT '发货时间',
  `receive_time` DATETIME DEFAULT NULL COMMENT '收货时间',
  `cancel_reason` VARCHAR(200) DEFAULT NULL COMMENT '取消原因',
  `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
  `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 8.2 订单明细表
DROP TABLE IF EXISTS `dm_order_item`;
CREATE TABLE `dm_order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单明细ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `product_id` BIGINT NOT NULL COMMENT '药品ID',
  `product_name` VARCHAR(200) NOT NULL COMMENT '药品名称',
  `product_image` VARCHAR(500) DEFAULT NULL COMMENT '药品图片',
  `specification` VARCHAR(100) DEFAULT NULL COMMENT '规格',
  `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `quantity` INT NOT NULL COMMENT '数量',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '小计',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 8.3 物流信息表
DROP TABLE IF EXISTS `dm_logistics`;
CREATE TABLE `dm_logistics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '物流记录ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `logistics_no` VARCHAR(100) DEFAULT NULL COMMENT '物流单号',
  `logistics_company` VARCHAR(100) DEFAULT NULL COMMENT '物流公司',
  `time` DATETIME NOT NULL COMMENT '物流时间节点',
  `content` VARCHAR(500) NOT NULL COMMENT '物流内容',
  `status` VARCHAR(50) DEFAULT NULL COMMENT '物流状态',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_time` (`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流信息表';

-- 8.4 退款表
DROP TABLE IF EXISTS `dm_refund`;
CREATE TABLE `dm_refund` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '退款ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `refund_no` VARCHAR(50) DEFAULT NULL COMMENT '退款单号',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态 pending-待审核 approved-已通过 rejected-已拒绝 completed-已完成',
  `reason` VARCHAR(200) NOT NULL COMMENT '退款原因',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '详细描述',
  `images` TEXT COMMENT '凭证图片JSON数组',
  `refund_amount` DECIMAL(10,2) NOT NULL COMMENT '申请退款金额',
  `actual_refund_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '实际退款金额',
  `reject_reason` VARCHAR(200) DEFAULT NULL COMMENT '拒绝原因',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款表';

-- =============================================
-- 九、问诊相关表
-- =============================================

-- 9.1 问诊表
DROP TABLE IF EXISTS `dm_consultation`;
CREATE TABLE `dm_consultation` (
  `id` VARCHAR(20) NOT NULL COMMENT '问诊ID',
  `doctor_id` VARCHAR(20) NOT NULL COMMENT '医生ID',
  `patient_id` BIGINT NOT NULL COMMENT '患者ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` VARCHAR(20) DEFAULT '图文问诊' COMMENT '问诊类型',
  `symptom` TEXT COMMENT '症状描述',
  `images` TEXT COMMENT '图片列表JSON',
  `requested_drug_ids` TEXT COMMENT '患者申请的药品ID列表(JSON数组)',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态 pending-待接诊 processing-问诊中 completed-已完成 closed-已关闭 cancelled-已取消',
  `is_urgent` TINYINT DEFAULT 0 COMMENT '是否紧急 0-否 1-是',
  `is_rx` TINYINT DEFAULT 0 COMMENT '是否需要处方 0-否 1-是',
  `fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '问诊费用',
  `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `reject_reason` VARCHAR(500) DEFAULT NULL COMMENT '拒绝原因',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_doctor_id` (`doctor_id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问诊表';

-- 9.2 问诊消息表
DROP TABLE IF EXISTS `dm_consultation_message`;
CREATE TABLE `dm_consultation_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `consultation_id` VARCHAR(20) NOT NULL COMMENT '问诊ID',
  `sender_type` VARCHAR(20) NOT NULL COMMENT '发送者类型 doctor/patient',
  `sender_id` VARCHAR(50) NOT NULL COMMENT '发送者ID',
  `type` VARCHAR(20) DEFAULT 'text' COMMENT '消息类型 text/image/voice',
  `content` TEXT COMMENT '消息内容',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_consultation_id` (`consultation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问诊消息表';

-- =============================================
-- 十、处方相关表
-- =============================================

-- 10.1 处方表
DROP TABLE IF EXISTS `dm_prescription`;
CREATE TABLE `dm_prescription` (
  `id` VARCHAR(30) NOT NULL COMMENT '处方ID',
  `consultation_id` VARCHAR(20) DEFAULT NULL COMMENT '问诊ID',
  `doctor_id` VARCHAR(20) NOT NULL COMMENT '医生ID',
  `patient_id` BIGINT NOT NULL COMMENT '患者ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `diagnosis` VARCHAR(500) DEFAULT NULL COMMENT '诊断结果',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态 pending-待审核 approved-已通过 rejected-已拒绝',
  `reject_reason` VARCHAR(200) DEFAULT NULL COMMENT '拒绝原因',
  `total_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '处方金额',
  `valid_days` INT DEFAULT 3 COMMENT '有效天数',
  `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_consultation_id` (`consultation_id`),
  KEY `idx_doctor_id` (`doctor_id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方表';

-- 10.2 处方明细表
DROP TABLE IF EXISTS `dm_prescription_item`;
CREATE TABLE `dm_prescription_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '处方明细ID',
  `prescription_id` VARCHAR(30) NOT NULL COMMENT '处方ID',
  `product_id` BIGINT NOT NULL COMMENT '药品ID',
  `product_name` VARCHAR(200) NOT NULL COMMENT '药品名称',
  `specification` VARCHAR(100) DEFAULT NULL COMMENT '规格',
  `quantity` INT NOT NULL COMMENT '数量',
  `dosage` VARCHAR(100) DEFAULT NULL COMMENT '用量',
  `frequency` VARCHAR(50) DEFAULT NULL COMMENT '频率',
  `duration` VARCHAR(50) DEFAULT NULL COMMENT '疗程',
  `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_prescription_id` (`prescription_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方明细表';

-- =============================================
-- 十一、收入相关表
-- =============================================

-- 11.1 医生收入表
DROP TABLE IF EXISTS `dm_doctor_income`;
CREATE TABLE `dm_doctor_income` (
  `id` VARCHAR(30) NOT NULL COMMENT '收入ID',
  `doctor_id` VARCHAR(20) NOT NULL COMMENT '医生ID',
  `type` VARCHAR(50) NOT NULL COMMENT '收入类型',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
  `patient_id` BIGINT DEFAULT NULL COMMENT '患者ID',
  `consultation_id` VARCHAR(20) DEFAULT NULL COMMENT '问诊ID',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态 pending-待结算 settled-已结算',
  `settle_time` DATETIME DEFAULT NULL COMMENT '结算时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_doctor_id` (`doctor_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生收入表';

-- 11.2 提现表
DROP TABLE IF EXISTS `dm_withdrawal`;
CREATE TABLE `dm_withdrawal` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '提现ID',
  `user_type` VARCHAR(20) NOT NULL COMMENT '用户类型 doctor/user',
  `user_id` VARCHAR(50) NOT NULL COMMENT '用户ID',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '提现金额',
  `method` VARCHAR(20) NOT NULL COMMENT '提现方式 bank/alipay/wechat',
  `account_info` TEXT COMMENT '账户信息JSON',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态 pending-待审核 approved-已通过 rejected-已拒绝',
  `reject_reason` VARCHAR(200) DEFAULT NULL COMMENT '拒绝原因',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_type_id` (`user_type`, `user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提现表';

-- =============================================
-- 十二、内容管理相关表
-- =============================================

-- 12.1 Banner表
DROP TABLE IF EXISTS `dm_banner`;
CREATE TABLE `dm_banner` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'BannerID',
  `title` VARCHAR(100) NOT NULL COMMENT '标题',
  `image` VARCHAR(500) NOT NULL COMMENT '图片',
  `link` VARCHAR(500) DEFAULT NULL COMMENT '链接',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_status_sort` (`status`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Banner表';

-- 12.2 公告表
DROP TABLE IF EXISTS `dm_notice`;
CREATE TABLE `dm_notice` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT COMMENT '内容',
  `type` VARCHAR(20) DEFAULT 'notice' COMMENT '类型 notice-公告 policy-政策',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- =============================================
-- 十三、关联表
-- =============================================

-- 13.1 管理员角色关联表
DROP TABLE IF EXISTS `dm_admin_role`;
CREATE TABLE `dm_admin_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `admin_id` BIGINT NOT NULL COMMENT '管理员ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_role` (`admin_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员角色关联表';

-- 13.2 角色权限关联表
DROP TABLE IF EXISTS `dm_role_permission`;
CREATE TABLE `dm_role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- =============================================
-- 十四、补充表
-- =============================================

-- 14.1 浏览历史表
DROP TABLE IF EXISTS `dm_browse_history`;
CREATE TABLE `dm_browse_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '浏览历史ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `product_id` BIGINT NOT NULL COMMENT '药品ID',
  `product_name` VARCHAR(200) DEFAULT NULL COMMENT '药品名称',
  `product_image` VARCHAR(500) DEFAULT NULL COMMENT '药品图片',
  `price` DECIMAL(10,2) DEFAULT NULL COMMENT '浏览时价格',
  `browse_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_browse_time` (`browse_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浏览历史表';

-- 14.2 药品评价表
DROP TABLE IF EXISTS `dm_product_review`;
CREATE TABLE `dm_product_review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `user_name` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
  `user_avatar` VARCHAR(500) DEFAULT NULL COMMENT '用户头像',
  `product_id` BIGINT NOT NULL COMMENT '药品ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `rating` TINYINT NOT NULL COMMENT '评分 1-5星',
  `content` VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
  `images` TEXT COMMENT '评价图片JSON数组',
  `tags` VARCHAR(200) DEFAULT NULL COMMENT '标签JSON数组',
  `is_anonymous` TINYINT DEFAULT 0 COMMENT '是否匿名 0-否 1-是',
  `is_recommended` TINYINT DEFAULT 1 COMMENT '是否推荐 0-否 1-是',
  `helpful_count` INT DEFAULT 0 COMMENT '有用数',
  `reply_content` VARCHAR(500) DEFAULT NULL COMMENT '商家回复内容',
  `reply_time` DATETIME DEFAULT NULL COMMENT '商家回复时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-隐藏 1-显示',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品评价表';

-- 14.3 药品FAQ表
DROP TABLE IF EXISTS `dm_product_faq`;
CREATE TABLE `dm_product_faq` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'FAQ ID',
  `product_id` BIGINT NOT NULL COMMENT '药品ID',
  `question` VARCHAR(500) NOT NULL COMMENT '问题',
  `answer` VARCHAR(1000) NOT NULL COMMENT '回答',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品FAQ表';

-- =============================================
-- 十五、字典数据相关表
-- =============================================

-- 15.1 字典类型表
DROP TABLE IF EXISTS `dm_dict_type`;
CREATE TABLE `dm_dict_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典类型ID',
  `dict_name` VARCHAR(100) NOT NULL COMMENT '字典名称',
  `dict_type` VARCHAR(100) NOT NULL COMMENT '字典类型',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- 15.2 字典数据表
DROP TABLE IF EXISTS `dm_dict_data`;
CREATE TABLE `dm_dict_data` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典数据ID',
  `dict_type` VARCHAR(100) NOT NULL COMMENT '字典类型',
  `dict_label` VARCHAR(100) NOT NULL COMMENT '字典标签',
  `dict_value` VARCHAR(100) NOT NULL COMMENT '字典键值',
  `dict_sort` INT DEFAULT 0 COMMENT '排序',
  `css_class` VARCHAR(100) DEFAULT NULL COMMENT '样式属性',
  `list_class` VARCHAR(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` TINYINT DEFAULT 0 COMMENT '是否默认 0-否 1-是',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_dict_type` (`dict_type`),
  KEY `idx_dict_sort` (`dict_sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- =============================================
-- 十六、数据源管理相关表
-- =============================================

-- 15.1 数据源元数据表
DROP TABLE IF EXISTS `datasource_meta`;
CREATE TABLE `datasource_meta` (
  `id` VARCHAR(50) NOT NULL COMMENT '数据源ID',
  `name` VARCHAR(100) NOT NULL COMMENT '连接名',
  `type` VARCHAR(50) NOT NULL COMMENT '数据库类型 mysql/postgresql/oracle/sqlserver',
  `host` VARCHAR(200) NOT NULL COMMENT '主机地址',
  `port` INT NOT NULL COMMENT '端口号',
  `username` VARCHAR(100) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码(加密)',
  `parent_id` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '父节点ID',
  `node_type` VARCHAR(20) NOT NULL DEFAULT 'datasource' COMMENT '节点类型 folder/datasource',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_node_type` (`node_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源元数据表';

-- 15.2 数据表元数据表
DROP TABLE IF EXISTS `table_meta`;
CREATE TABLE `table_meta` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '表ID',
  `datasource_id` VARCHAR(50) NOT NULL COMMENT '数据源ID',
  `table_name` VARCHAR(100) NOT NULL COMMENT '表名',
  `table_comment` VARCHAR(200) DEFAULT NULL COMMENT '表注释',
  `column_count` INT DEFAULT 0 COMMENT '字段数量',
  `row_count` BIGINT DEFAULT 0 COMMENT '行数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_datasource_table` (`datasource_id`, `table_name`),
  KEY `idx_datasource_id` (`datasource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据表元数据表';

-- 15.3 视图元数据表
DROP TABLE IF EXISTS `view_meta`;
CREATE TABLE `view_meta` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '视图ID',
  `datasource_id` VARCHAR(50) NOT NULL COMMENT '数据源ID',
  `view_name` VARCHAR(100) NOT NULL COMMENT '视图名',
  `view_comment` VARCHAR(200) DEFAULT NULL COMMENT '视图注释',
  `column_count` INT DEFAULT 0 COMMENT '字段数量',
  `definition` TEXT COMMENT '视图定义SQL',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_datasource_view` (`datasource_id`, `view_name`),
  KEY `idx_datasource_id` (`datasource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视图元数据表';

-- =============================================
-- 第二部分：模拟数据插入
-- =============================================

-- =============================================
-- 一、基础配置数据
-- =============================================

-- 1.1 系统配置
INSERT INTO `dm_system_config` (`config_key`, `config_value`, `description`) VALUES
('site_name', 'DrugMall药品电商', '网站名称'),
('site_logo', '', '网站Logo'),
('service_phone', '400-123-4567', '客服电话'),
('icp', '京ICP备12345678号', 'ICP备案号'),
('prescription_valid_days', '3', '处方有效天数'),
('free_shipping_amount', '99', '免运费金额'),
('default_freight', '10', '默认运费');

-- 1.2 管理员
INSERT INTO `dm_admin` (`id`, `username`, `password`, `nickname`, `email`, `phone`, `status`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '超级管理员', 'admin@drugmall.com', '13800138000', 1),
(2, 'operator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '运营人员', 'operator@drugmall.com', '13800138001', 1);

-- 1.3 角色
INSERT INTO `dm_role` (`id`, `name`, `code`, `description`, `status`) VALUES
(1, '超级管理员', 'admin', '拥有所有权限', 1),
(2, '运营人员', 'operator', '负责日常运营', 1),
(3, '客服人员', 'service', '负责客户服务', 1);

-- 1.4 管理员角色关联
INSERT INTO `dm_admin_role` (`admin_id`, `role_id`) VALUES
(1, 1),
(2, 2);

-- =============================================
-- 二、用户相关数据
-- =============================================

-- 2.1 用户
INSERT INTO `dm_user` (`id`, `phone`, `password`, `nickname`, `email`, `gender`, `birthday`, `real_name`, `id_card`, `is_real_name_auth`, `balance`, `points`, `status`) VALUES
(1, '13800138000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '健康小达人', 'user1@example.com', 1, '1990-05-20', '张三', '110101199005201234', 1, 1000.00, 500, 1),
(2, '13800138001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李四', 'user2@example.com', 2, '1992-03-15', '李四', '110101199203152345', 1, 800.00, 300, 1),
(3, '13800138002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王五', 'user3@example.com', 1, '1985-08-10', '王五', '110101198508102345', 1, 500.00, 200, 1),
(4, '13800138003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '赵六', 'user4@example.com', 2, '1995-12-25', '赵六', '110101199512253456', 1, 300.00, 100, 1),
(5, '13800138004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '孙七', 'user5@example.com', 1, '1988-07-18', '孙七', '110101198807184567', 1, 200.00, 50, 1);

-- 2.2 用户地址
INSERT INTO `dm_user_address` (`id`, `user_id`, `name`, `phone`, `province`, `city`, `district`, `detail`, `tag`, `is_default`) VALUES
(1, 1, '张三', '13800138000', '北京市', '北京市', '朝阳区', '某某小区1号楼1单元101室', '家', 1),
(2, 1, '张三', '13800138000', '北京市', '北京市', '海淀区', '某某大厦A座10层', '公司', 0),
(3, 2, '李四', '13800138001', '北京市', '北京市', '西城区', '某某街道2号楼', '家', 1),
(4, 3, '王五', '13800138002', '上海市', '上海市', '浦东新区', '某某路100号', '家', 1);

-- 2.3 患者档案
INSERT INTO `dm_patient` (`id`, `user_id`, `name`, `gender`, `birthday`, `id_card`, `phone`, `relationship`, `allergy_history`, `medical_history`, `is_default`) VALUES
(1, 1, '张三', 1, '1990-05-20', '110101199005201234', '13800138000', '本人', '青霉素过敏', '高血压', 1),
(2, 1, '张小明', 1, '2015-03-10', '110101201503101234', '13800138000', '子女', '无', '无', 0),
(3, 2, '李四', 2, '1992-03-15', '110101199203152345', '13800138001', '本人', '无', '糖尿病', 1),
(4, 3, '王五', 1, '1985-08-10', '110101198508102345', '13800138002', '本人', '磺胺类药物过敏', '冠心病', 1);

-- =============================================
-- 三、医生相关数据
-- =============================================

-- 3.1 科室数据 - 从首页问医生组件提取
INSERT INTO `dm_department` (`id`, `code`, `name`, `icon`, `tag`, `tag_type`, `sort`, `status`) VALUES
(1, 'bone', '骨科', 'bone', '', 'info', 1, 1),
(2, 'neurology', '神经内科', 'brain', '', 'info', 2, 1),
(3, 'general', '全科', 'firstAid', '', 'info', 3, 1),
(4, 'tcm', '中医科', 'herb', '', 'info', 4, 1),
(5, 'surgery', '普外科', 'scissor', '', 'info', 5, 1),
(6, 'andrology', '男科门诊', 'male', '', 'info', 6, 1),
(7, 'cardiology', '心血管内科', 'heart', '', 'info', 7, 1),
(8, 'endocrine', '内分泌科', 'stomach', '', 'info', 8, 1),
(9, 'tcm-spleen', '中医脾胃病', 'herb', '', 'info', 9, 1),
(10, 'tcm-male', '中医男科', 'male', '补肾', 'supplement', 10, 1),
(11, 'tcm-sleep', '中医失眠科', 'moon', '', 'info', 11, 1),
(12, 'tcm-female', '中医妇科', 'female', '', 'info', 12, 1),
(13, 'weight', '减重门诊', 'scale', '', 'info', 13, 1),
(14, 'sleep', '睡眠中心', 'moon', '9.9元起', 'price', 14, 1),
(15, 'dermatology', '皮肤科', 'skin', '瘙痒', 'hot', 15, 1),
(16, 'respiratory', '呼吸内科', 'lung', '', 'info', 16, 1),
(17, 'pediatrics', '儿科', 'child', '发热', 'fever', 17, 1),
(18, 'gastroenterology', '消化内科', 'stomach', '', 'info', 18, 1),
(19, 'gynecology', '妇产科', 'female', '', 'info', 19, 1),
(20, 'ent', '耳鼻喉科', 'ear', '', 'info', 20, 1),
(21, 'urology', '泌尿外科', 'kidney', '', 'info', 21, 1),
(22, 'dental', '口腔科', 'tooth', '', 'info', 22, 1),
(23, 'ophthalmology', '眼科', 'eye', '', 'info', 23, 1),
(24, 'psychology', '心理咨询', 'brain', '19.9元', 'price', 24, 1);

-- 3.2 医生数据（增加his_code字段）
INSERT INTO `dm_doctor` (`id`, `his_code`, `phone`, `password`, `name`, `gender`, `title`, `hospital`, `department_id`, `department`, `license_no`, `is_certified`, `rating`, `service_count`, `response_time`, `specialties`, `introduction`, `balance`, `status`) VALUES
('DOC001', 'HIS001', '13900001234', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张医生', 1, '主任医师', '北京协和医院', 7, '心血管内科', '110123456789', 1, 4.9, 1280, 2, '高血压,冠心病,心力衰竭', '从事心血管内科临床工作15年，擅长高血压、冠心病、心力衰竭等心血管疾病的诊断和治疗。', 5000.00, 1),
('DOC002', 'HIS002', '13900001235', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李医生', 2, '副主任医师', '北京同仁医院', 16, '呼吸内科', '110123456790', 1, 4.8, 856, 3, '哮喘,肺炎,慢性支气管炎', '从事呼吸内科临床工作12年，擅长哮喘、肺炎、慢性支气管炎等呼吸系统疾病的诊治。', 3500.00, 1),
('DOC003', 'HIS003', '13900001236', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王医生', 1, '主治医师', '北京儿童医院', 17, '儿科', '110123456791', 1, 4.7, 652, 5, '小儿感冒,小儿肺炎,小儿腹泻', '从事儿科临床工作8年，擅长小儿常见病、多发病的诊治。', 2000.00, 1);

-- 3.2 医生排班
INSERT INTO `dm_doctor_schedule` (`doctor_id`, `day_of_week`, `morning`, `afternoon`, `evening`, `max_consultations`) VALUES
('DOC001', 1, 1, 1, 0, 20),
('DOC001', 2, 1, 1, 0, 20),
('DOC001', 3, 1, 0, 0, 15),
('DOC001', 4, 1, 1, 0, 20),
('DOC001', 5, 1, 1, 1, 25),
('DOC002', 1, 1, 1, 0, 18),
('DOC002', 2, 0, 1, 0, 12),
('DOC002', 3, 1, 1, 0, 18),
('DOC002', 4, 1, 1, 0, 18),
('DOC002', 5, 1, 0, 1, 15),
('DOC003', 1, 1, 1, 0, 25),
('DOC003', 2, 1, 1, 0, 25),
('DOC003', 3, 1, 1, 0, 25),
('DOC003', 4, 1, 1, 0, 25),
('DOC003', 5, 1, 1, 1, 30);

-- =============================================
-- 四、药品相关数据
-- =============================================

-- 4.1 分类数据 - 包含商城分类、症状分类、门店分类
INSERT INTO `dm_category` (`id`, `name`, `code`, `parent_id`, `level`, `icon`, `type`, `sort`, `status`) VALUES
-- 一级分类 - 药品类
(1, '药品', 'drug', 0, 1, 'Medicine', 'drug', 1, 1),
(2, '医疗器械', 'device', 0, 1, 'Device', 'drug', 2, 1),
(3, '保健品', 'health', 0, 1, 'Health', 'drug', 3, 1),
-- 二级分类 - 药品子类
(11, '感冒药', 'cold', 1, 2, '', 'drug', 1, 1),
(12, '退烧药', 'fever', 1, 2, '', 'drug', 2, 1),
(13, '消炎药', 'anti-inflammatory', 1, 2, '', 'drug', 3, 1),
(14, '肠胃药', 'gastrointestinal', 1, 2, '', 'drug', 4, 1),
(15, '心血管药', 'cardiovascular', 1, 2, '', 'drug', 5, 1),
(16, '皮肤用药', 'dermatology', 1, 2, '', 'drug', 6, 1),
(21, '血压计', 'blood-pressure', 2, 2, '', 'drug', 1, 1),
(22, '血糖仪', 'glucose-meter', 2, 2, '', 'drug', 2, 1),
(31, '维生素', 'vitamin', 3, 2, '', 'drug', 1, 1),
(32, '钙片', 'calcium', 3, 2, '', 'drug', 2, 1),
-- 症状分类 - 从分类页面提取
(101, '全部', 'all', 0, 1, '', 'symptom', 1, 1),
(102, '发烧/头痛', 'fever-headache', 0, 1, '', 'symptom', 2, 1),
(103, '鼻塞/流涕', 'nose', 0, 1, '', 'symptom', 3, 1),
(104, '咽痛/喉痒', 'throat', 0, 1, '', 'symptom', 4, 1),
(105, '咳嗽/咳痰', 'cough', 0, 1, '', 'symptom', 5, 1),
(106, '新冠用药', 'covid', 0, 1, '', 'symptom', 6, 1),
(107, '甲流用药', 'flu', 0, 1, '', 'symptom', 7, 1),
(108, '支原体肺炎', 'pneumonia', 0, 1, '', 'symptom', 8, 1),
(109, '消炎药', 'anti', 0, 1, '', 'symptom', 9, 1),
(110, '儿童感冒', 'child-cold', 0, 1, '', 'symptom', 10, 1),
(111, '退热贴', 'fever-patch', 0, 1, '', 'symptom', 11, 1),
(112, '体温计', 'thermometer', 0, 1, '', 'symptom', 12, 1),
(113, '口罩', 'mask', 0, 1, '', 'symptom', 13, 1),
(114, '消毒灭菌', 'disinfect', 0, 1, '', 'symptom', 14, 1),
(115, '提高免疫力', 'immunity', 0, 1, '', 'symptom', 15, 1),
(116, '检测用品', 'test', 0, 1, '', 'symptom', 16, 1);

-- 三级分类 - 药品 (parent_id=1)
INSERT INTO `dm_category` (`id`, `name`, `code`, `parent_id`, `level`, `icon`, `type`, `sort`, `status`) VALUES
(1001, '感冒用药', 'cold-med', 1, 2, '', 'drug', 10, 1),
(1002, '肠胃用药', 'gastro-med', 1, 2, '', 'drug', 11, 1),
(1003, '心血管药', 'cardio-med', 1, 2, '', 'drug', 12, 1),
(1004, '皮肤用药', 'derma-med', 1, 2, '', 'drug', 13, 1),
(1005, '维生素补充', 'vitamin-med', 1, 2, '', 'drug', 14, 1);

INSERT INTO `dm_category` (`id`, `name`, `code`, `parent_id`, `level`, `icon`, `type`, `sort`, `status`) VALUES
(10011, '感冒药', 'cold-drug', 1001, 3, '', 'drug', 1, 1),
(10012, '退烧药', 'fever-drug', 1001, 3, '', 'drug', 2, 1),
(10013, '止咳药', 'cough-drug', 1001, 3, '', 'drug', 3, 1),
(10014, '消炎药', 'anti-drug', 1001, 3, '', 'drug', 4, 1);

INSERT INTO `dm_category` (`id`, `name`, `code`, `parent_id`, `level`, `icon`, `type`, `sort`, `status`) VALUES
(10021, '胃药', 'stomach-drug', 1002, 3, '', 'drug', 1, 1),
(10022, '止泻药', 'antidiarrheal', 1002, 3, '', 'drug', 2, 1),
(10023, '便秘药', 'laxative', 1002, 3, '', 'drug', 3, 1);

INSERT INTO `dm_category` (`id`, `name`, `code`, `parent_id`, `level`, `icon`, `type`, `sort`, `status`) VALUES
(10031, '降压药', 'antihypertensive', 1003, 3, '', 'drug', 1, 1),
(10032, '降脂药', 'lipid-lowering', 1003, 3, '', 'drug', 2, 1);

INSERT INTO `dm_category` (`id`, `name`, `code`, `parent_id`, `level`, `icon`, `type`, `sort`, `status`) VALUES
(10041, '湿疹药膏', 'eczema-cream', 1004, 3, '', 'drug', 1, 1),
(10042, '痤疮药', 'acne-drug', 1004, 3, '', 'drug', 2, 1);

INSERT INTO `dm_category` (`id`, `name`, `code`, `parent_id`, `level`, `icon`, `type`, `sort`, `status`) VALUES
(10051, '复合维生素', 'multi-vitamin', 1005, 3, '', 'drug', 1, 1),
(10052, '维生素C', 'vitamin-c', 1005, 3, '', 'drug', 2, 1),
(10053, '钙片', 'calcium-tablet', 1005, 3, '', 'drug', 3, 1);

-- 三级分类 - 医疗器械 (parent_id=2)
INSERT INTO `dm_category` (`id`, `name`, `code`, `parent_id`, `level`, `icon`, `type`, `sort`, `status`) VALUES
(2001, '血压计', 'bp-monitor', 2, 2, '', 'drug', 10, 1),
(2002, '血糖仪', 'glucometer', 2, 2, '', 'drug', 11, 1),
(2003, '体温计', 'thermometer-dev', 2, 2, '', 'drug', 12, 1),
(2004, '口罩', 'mask-dev', 2, 2, '', 'drug', 13, 1);

INSERT INTO `dm_category` (`id`, `name`, `code`, `parent_id`, `level`, `icon`, `type`, `sort`, `status`) VALUES
(20011, '上臂式血压计', 'arm-bp', 2001, 3, '', 'drug', 1, 1),
(20012, '手腕式血压计', 'wrist-bp', 2001, 3, '', 'drug', 2, 1);

INSERT INTO `dm_category` (`id`, `name`, `code`, `parent_id`, `level`, `icon`, `type`, `sort`, `status`) VALUES
(20021, '血糖仪', 'glucometer-dev', 2002, 3, '', 'drug', 1, 1),
(20022, '血糖试纸', 'glucose-strip', 2002, 3, '', 'drug', 2, 1);

INSERT INTO `dm_category` (`id`, `name`, `code`, `parent_id`, `level`, `icon`, `type`, `sort`, `status`) VALUES
(20031, '电子体温计', 'digital-therm', 2003, 3, '', 'drug', 1, 1),
(20032, '红外体温计', 'ir-therm', 2003, 3, '', 'drug', 2, 1);

INSERT INTO `dm_category` (`id`, `name`, `code`, `parent_id`, `level`, `icon`, `type`, `sort`, `status`) VALUES
(20041, '医用外科口罩', 'surgical-mask', 2004, 3, '', 'drug', 1, 1),
(20042, 'N95口罩', 'n95-mask', 2004, 3, '', 'drug', 2, 1);

-- 三级分类 - 保健品 (parent_id=3)
INSERT INTO `dm_category` (`id`, `name`, `code`, `parent_id`, `level`, `icon`, `type`, `sort`, `status`) VALUES
(3001, '蛋白粉', 'protein-powder', 3, 2, '', 'drug', 10, 1),
(3002, '鱼油', 'fish-oil', 3, 2, '', 'drug', 11, 1),
(3003, '益生菌', 'probiotics', 3, 2, '', 'drug', 12, 1);

INSERT INTO `dm_category` (`id`, `name`, `code`, `parent_id`, `level`, `icon`, `type`, `sort`, `status`) VALUES
(30011, '乳清蛋白粉', 'whey-protein', 3001, 3, '', 'drug', 1, 1),
(30012, '植物蛋白粉', 'plant-protein', 3001, 3, '', 'drug', 2, 1);

INSERT INTO `dm_category` (`id`, `name`, `code`, `parent_id`, `level`, `icon`, `type`, `sort`, `status`) VALUES
(30021, '深海鱼油', 'deep-sea-fish-oil', 3002, 3, '', 'drug', 1, 1),
(30022, '磷虾油', 'krill-oil', 3002, 3, '', 'drug', 2, 1);

INSERT INTO `dm_category` (`id`, `name`, `code`, `parent_id`, `level`, `icon`, `type`, `sort`, `status`) VALUES
(30031, '成人益生菌', 'adult-probiotics', 3003, 3, '', 'drug', 1, 1),
(30032, '儿童益生菌', 'child-probiotics', 3003, 3, '', 'drug', 2, 1);

-- 4.2 品牌
INSERT INTO `dm_brand` (`id`, `name`, `logo`, `description`, `sort_order`, `status`) VALUES
(1, '修正药业', '', '中国医药百强企业', 1, 1),
(2, '云南白药', '', '百年老字号', 2, 1),
(3, '华北制药', '', '国有大型医药企业', 3, 1),
(4, '同仁堂', '', '百年老字号', 4, 1),
(5, '中美史克', '', '合资制药企业', 5, 1);

-- 4.3 药品
INSERT INTO `dm_product` (`id`, `product_code`, `product_name`, `generic_name`, `category_id`, `brand_id`, `main_image`, `specification`, `unit`, `manufacturer`, `brand`, `approval_number`, `bar_code`, `medical_insurance_code`, `price`, `original_price`, `stock`, `sales`, `is_rx`, `is_national_essential`, `is_long_prescription`, `insurance_category`, `usage`, `disease`, `contraindications`, `precautions`, `adverse_reactions`, `storage`, `validity`, `ingredients`, `appearance`, `drug_interactions`, `description`, `status`) VALUES
(1, 'YP20240001', '阿莫西林胶囊', '阿莫西林', 13, 3, '', '0.25g*24粒', '盒', '华北制药', '华北制药', '国药准字H13022558', '6901234567890', 'XBJ00001', 12.50, 18.00, 500, 999, 1, 1, 0, '甲类', '口服，成人一次0.5g，每6-8小时1次', '适用于敏感菌所致的各种感染', '对青霉素过敏者禁用', '用前需做青霉素皮试', '恶心、呕吐、腹泻等胃肠道反应', '密封，在阴凉干燥处保存', '24个月', '阿莫西林', '白色或类白色粉末', '与丙磺舒合用可提高血药浓度', '阿莫西林适用于敏感菌所致的各种感染。', 1),
(2, 'YP20240002', '布洛芬缓释胶囊', '布洛芬', 12, 5, '', '0.3g*20粒', '盒', '中美史克', '芬必得', '国药准字H10900089', '6901234567891', 'XBJ00002', 15.80, 22.00, 300, 856, 0, 1, 0, '甲类', '口服，成人一次1粒，一日2次', '用于缓解轻至中度疼痛，也可用于普通感冒或流行性感冒引起的发热', '对本品过敏者禁用', '不宜长期或大量使用', '胃肠道不适、皮疹等', '密封保存', '36个月', '布洛芬', '内容物为白色球形小丸', '与阿司匹林合用增加胃肠道副作用', '用于缓解轻至中度疼痛。', 1),
(3, 'YP20240003', '感冒清热颗粒', '感冒清热颗粒', 11, 1, '', '12g*10袋', '盒', '修正药业', '修正', '国药准字Z22025746', '6901234567892', 'XBJ00003', 9.90, 15.00, 800, 1234, 0, 0, 0, '乙类', '开水冲服，一次1袋，一日2次', '用于风寒感冒，头痛发热，恶寒身痛', '风热感冒者不适用', '忌烟、酒及辛辣食物', '偶见皮疹、恶心', '密封', '36个月', '荆芥穗、薄荷、防风、柴胡等', '棕黄色颗粒', '不宜与滋补性中药同服', '用于风寒感冒，头痛发热。', 1),
(4, 'YP20240004', '硝苯地平控释片', '硝苯地平', 15, 4, '', '30mg*7片', '盒', '拜耳医药', '拜耳', '国药准字J20171022', '6901234567893', 'XBJ00004', 35.00, 45.00, 200, 456, 1, 1, 1, '甲类', '口服，一次30mg，一日1次', '用于高血压、冠心病、慢性稳定型心绞痛', '对硝苯地平过敏者禁用', '整片吞服，不可掰开', '头痛、面部潮红、踝部水肿', '遮光，密封保存', '36个月', '硝苯地平', '圆形双凸薄膜衣片', '与CYP3A4抑制剂合用增加血药浓度', '用于高血压、冠心病。', 1),
(5, 'YP20240005', '蒙脱石散', '蒙脱石', 14, 2, '', '3g*10袋', '盒', '云南白药', '云南白药', '国药准字H20056745', '6901234567894', 'XBJ00005', 18.50, 25.00, 400, 678, 0, 1, 0, '甲类', '口服，成人一次1袋，一日3次', '用于成人及儿童急、慢性腹泻', '对本品过敏者禁用', '与其他药物间隔2小时服用', '偶见便秘', '密封保存', '36个月', '蒙脱石', '类白色粉末', '与其他药物同服影响吸收', '用于成人及儿童急、慢性腹泻。', 1),
(6, 'YP20240006', '维生素C片', '维生素C', 31, 4, '', '100mg*100片', '瓶', '东北制药', '东北制药', '国药准字H21020713', '6901234567895', 'XBJ00006', 5.80, 8.00, 1000, 2345, 0, 1, 0, '乙类', '口服，一次1-2片，一日3次', '用于预防和治疗坏血病及各种急慢性传染病的辅助治疗', '高钙血症者禁用', '不宜长期过量服用', '大剂量服用可致腹泻', '遮光，密封保存', '24个月', '维生素C', '白色或略带淡黄色片', '与碱性药物合用降低疗效', '补充维生素C。', 1),
(7, 'YP20240007', '复方氨酚烷胺片', '复方氨酚烷胺', 11, 1, '', '12片', '盒', '修正药业', '修正', '国药准字H22026593', '6901234567896', 'XBJ00007', 12.00, 16.00, 600, 1567, 0, 0, 0, '乙类', '口服，一次1片，一日2次', '用于缓解普通感冒及流行性感冒引起的发热、头痛、鼻塞等症状', '严重肝肾功能不全者禁用', '服药期间不得饮酒', '嗜睡、口干、恶心', '密封保存', '24个月', '对乙酰氨基酚、盐酸金刚烷胺等', '淡黄色片', '不宜与含对乙酰氨基酚的药物同服', '用于缓解普通感冒及流行性感冒。', 1),
(8, 'YP20240008', '奥美拉唑肠溶胶囊', '奥美拉唑', 14, 3, '', '20mg*14粒', '盒', '华北制药', '华北制药', '国药准字H20056588', '6901234567897', 'XBJ00008', 28.00, 38.00, 350, 567, 1, 1, 0, '甲类', '口服，一次20mg，一日1-2次', '用于胃酸过多引起的烧心和反酸，胃溃疡、十二指肠溃疡', '对本品过敏者禁用', '不宜长期大剂量使用', '头痛、腹泻、恶心', '遮光，密封保存', '24个月', '奥美拉唑', '内容物为白色或类白色肠溶小丸', '与氯吡格雷合用降低后者疗效', '用于胃酸过多引起的烧心和反酸。', 1);

-- 三级分类商品数据
INSERT INTO `dm_product` (`id`, `product_code`, `product_name`, `generic_name`, `category_id`, `brand_id`, `main_image`, `specification`, `unit`, `manufacturer`, `brand`, `approval_number`, `bar_code`, `medical_insurance_code`, `price`, `original_price`, `stock`, `sales`, `is_rx`, `is_national_essential`, `is_long_prescription`, `insurance_category`, `usage`, `disease`, `contraindications`, `precautions`, `adverse_reactions`, `storage`, `validity`, `ingredients`, `appearance`, `drug_interactions`, `description`, `status`) VALUES
(9, 'YP20240009', '感康复方氨酚烷胺片', '复方氨酚烷胺', 10011, 1, '', '12片/盒', '盒', '吉林吴太感康药业', '感康', '国药准字H22026593', '6901234567900', 'XBJ00009', 16.50, 22.00, 500, 1280, 0, 0, 0, '乙类', '口服，一次1片，一日2次', '用于缓解普通感冒及流行性感冒引起的发热、头痛等症状', '严重肝肾功能不全者禁用', '服药期间不得饮酒', '嗜睡、口干、恶心', '密封保存', '24个月', '对乙酰氨基酚、盐酸金刚烷胺等', '淡黄色片', '不宜与含对乙酰氨基酚的药物同服', '用于缓解普通感冒及流行性感冒引起的发热、头痛等症状', 1),
(10, 'YP20240010', '新康泰克蓝色装', '复方盐酸伪麻黄碱', 10011, 5, '', '10粒/盒', '盒', '中美天津史克', '新康泰克', '国药准字H10970427', '6901234567901', 'XBJ00010', 20.61, 28.00, 300, 956, 0, 0, 0, '乙类', '口服，一次1粒，每12小时1次', '减轻感冒引起的鼻塞、流涕、打喷嚏等症状', '严重高血压、冠心病患者禁用', '服药期间不得驾驶', '口干、嗜睡、头晕', '密封保存', '24个月', '盐酸伪麻黄碱、马来酸氯苯那敏', '蓝色胶囊', '不宜与单胺氧化酶抑制剂同服', '减轻感冒引起的鼻塞、流涕、打喷嚏等症状', 1),
(11, 'YP20240011', '布洛芬缓释胶囊', '布洛芬', 10012, 5, '', '0.3g*20粒', '盒', '中美史克', '芬必得', '国药准字H10900089', '6901234567902', 'XBJ00011', 15.80, 22.00, 600, 2156, 0, 1, 0, '甲类', '口服，成人一次1粒，一日2次', '用于缓解轻至中度疼痛，也可用于普通感冒或流行性感冒引起的发热', '对本品过敏者禁用', '不宜长期或大量使用', '胃肠道不适、皮疹等', '密封保存', '36个月', '布洛芬', '内容物为白色球形小丸', '与阿司匹林合用增加胃肠道副作用', '用于缓解轻至中度疼痛。', 1),
(12, 'YP20240012', '对乙酰氨基酚片', '对乙酰氨基酚', 10012, 4, '', '0.5g*100片', '瓶', '上海信谊', '信谊', '国药准字H31020656', '6901234567903', 'XBJ00012', 8.50, 12.00, 800, 1890, 0, 1, 0, '甲类', '口服，一次1-2片，一日不超过4次', '用于普通感冒或流行性感冒引起的发热', '严重肝肾功能不全者禁用', '服药期间不得饮酒', '偶见皮疹、荨麻疹', '密封保存', '36个月', '对乙酰氨基酚', '白色片', '不宜与含对乙酰氨基酚的药物同服', '用于普通感冒或流行性感冒引起的发热', 1),
(13, 'YP20240013', '京都念慈菴枇杷膏', '蜜炼川贝枇杷膏', 10013, 4, '', '150ml/瓶', '瓶', '京都念慈菴', '念慈菴', '国药准字ZC20160001', '6901234567904', 'XBJ00013', 28.80, 35.00, 450, 3200, 0, 0, 0, '乙类', '口服，一次15ml，一日3次', '润肺化痰、止咳平喘', '糖尿病患者慎用', '忌烟、酒及辛辣食物', '偶见胃部不适', '密封，置阴凉处', '36个月', '川贝母、枇杷叶、桔梗等', '棕褐色稠厚的半流体', '不宜与滋补性中药同服', '润肺化痰、止咳平喘', 1),
(14, 'YP20240014', '阿莫西林胶囊', '阿莫西林', 10014, 3, '', '0.25g*24粒', '盒', '华北制药', '华北制药', '国药准字H13022558', '6901234567905', 'XBJ00014', 12.50, 18.00, 500, 2890, 1, 1, 0, '甲类', '口服，成人一次0.5g，每6-8小时1次', '适用于敏感菌所致的各种感染', '对青霉素过敏者禁用', '用前需做青霉素皮试', '恶心、呕吐、腹泻等胃肠道反应', '密封，在阴凉干燥处保存', '24个月', '阿莫西林', '白色或类白色粉末', '与丙磺舒合用可提高血药浓度', '适用于敏感菌所致的各种感染', 1),
(15, 'YP20240015', '奥美拉唑肠溶胶囊', '奥美拉唑', 10021, 4, '', '20mg*14粒', '盒', '阿斯利康', '洛赛克', '国药准字H20030412', '6901234567906', 'XBJ00015', 35.80, 45.00, 350, 2456, 1, 1, 0, '甲类', '口服，一次20mg，一日1-2次', '用于胃溃疡、十二指肠溃疡、反流性食管炎', '对本品过敏者禁用', '不宜长期大剂量使用', '头痛、腹泻、恶心', '遮光，密封保存', '24个月', '奥美拉唑', '内容物为白色或类白色肠溶小丸', '与氯吡格雷合用降低后者疗效', '用于胃溃疡、十二指肠溃疡、反流性食管炎', 1),
(16, 'YP20240016', '铝碳酸镁咀嚼片', '铝碳酸镁', 10021, 4, '', '0.5g*20片', '盒', '拜耳医药', '达喜', '国药准字H20013456', '6901234567907', 'XBJ00016', 28.00, 35.00, 400, 1890, 0, 0, 0, '乙类', '口服，一次1-2片，一日3次', '用于慢性胃炎、胃酸过多引起的胃痛', '对本品过敏者禁用', '与其他药物间隔1-2小时服用', '偶见便秘', '密封保存', '36个月', '铝碳酸镁', '白色片', '与其他药物同服影响吸收', '用于慢性胃炎、胃酸过多引起的胃痛', 1),
(17, 'YP20240017', '欧姆龙上臂式血压计HEM-7121', NULL, 20011, NULL, '', '标准版', '台', '欧姆龙', '欧姆龙', '', '6901234567910', '', 299.00, 399.00, 150, 567, 0, 0, 0, NULL, '', '用于日常血压监测', '', '', '', '', '', '', '', '', '智能加压，一键测量，大屏显示', 1),
(18, 'YP20240018', '鱼跃电子血压计YE660D', NULL, 20011, NULL, '', '语音版', '台', '鱼跃医疗', '鱼跃', '', '6901234567911', '', 199.00, 268.00, 200, 890, 0, 0, 0, NULL, '', '用于日常血压监测', '', '', '', '', '', '', '', '', '语音播报，双用户记忆', 1),
(19, 'YP20240019', '罗氏血糖仪卓越金采', NULL, 20021, NULL, '', '主机+25试纸', '套', '罗氏诊断', '罗氏', '', '6901234567912', '', 358.00, 458.00, 100, 345, 0, 0, 0, NULL, '', '用于血糖监测', '', '', '', '', '', '', '', '', '免调码技术，5秒出结果', 1),
(20, 'YP20240020', '稳健医用外科口罩', NULL, 20041, NULL, '', '50只/盒', '盒', '稳健医疗', '稳健', '', '6901234567913', '', 29.90, 39.90, 1000, 5678, 0, 0, 0, NULL, '', '日常防护', '', '', '', '', '', '', '', '', '三层防护，细菌过滤效率≥95%', 1),
(21, 'YP20240021', '善存多维元素片', '多维元素片(29)', 10051, 4, '', '30片/瓶', '瓶', '惠氏制药', '善存', '国药准字H10950026', '6901234567914', 'XBJ00017', 68.00, 88.00, 300, 2345, 0, 0, 0, '乙类', '口服，一次1片，一日1次', '用于预防和治疗因维生素与矿物质缺乏所引起的各种疾病', '高钙血症者禁用', '不宜长期过量服用', '偶见胃部不适', '遮光，密封保存', '24个月', '多种维生素和矿物质', '薄膜衣片', '与其他药物间隔服用', '含21种维生素和矿物质', 1),
(22, 'YP20240022', '汤臣倍健乳清蛋白粉', NULL, 30011, NULL, '', '450g/罐', '罐', '汤臣倍健', '汤臣倍健', '', '6901234567915', '', 298.00, 398.00, 150, 1234, 0, 0, 0, NULL, '每日1-2次，每次1勺(约10g)，加入温水或牛奶中冲调', '补充蛋白质', '', '', '', '', '', '', '', '', '新西兰进口奶源，高蛋白低脂肪', 1),
(23, 'YP20240023', 'Swisse深海鱼油', NULL, 30021, NULL, '', '400粒/瓶', '瓶', 'Swisse', 'Swisse', '', '6901234567916', '', 128.00, 168.00, 200, 2345, 0, 0, 0, NULL, '每日1-2粒，随餐服用', '辅助降血脂', '', '', '', '', '', '', '', '', '澳洲进口，富含Omega-3', 1);

-- 药品规格模拟数据
INSERT INTO `dm_product_specification` (`id`, `product_id`, `spec_name`, `spec_code`, `price`, `original_price`, `stock`, `bar_code`, `is_default`, `sort_order`, `status`) VALUES
(1, 1, '0.25g*24粒', 'SPEC001', 12.50, 18.00, 500, '6901234567890', 1, 1, 1),
(2, 1, '0.25g*48粒', 'SPEC002', 22.00, 32.00, 300, '6901234567890-48', 0, 2, 1),
(3, 2, '0.3g*20粒', 'SPEC003', 15.80, 22.00, 300, '6901234567891', 1, 1, 1),
(4, 2, '0.3g*40粒', 'SPEC004', 28.00, 38.00, 200, '6901234567891-40', 0, 2, 1),
(5, 3, '5mg*7片', 'SPEC005', 25.00, 35.00, 400, '6901234567892', 1, 1, 1),
(6, 3, '5mg*14片', 'SPEC006', 45.00, 60.00, 250, '6901234567892-14', 0, 2, 1),
(7, 4, '0.5g*24片', 'SPEC007', 35.00, 48.00, 200, '6901234567893', 1, 1, 1),
(8, 5, '10ml*6支', 'SPEC008', 18.00, 25.00, 600, '6901234567894', 1, 1, 1),
(9, 6, '100mg*100片', 'SPEC009', 5.80, 8.00, 1000, '6901234567895', 1, 1, 1),
(10, 7, '12片', 'SPEC010', 12.00, 16.00, 600, '6901234567896', 1, 1, 1),
(11, 8, '20mg*14粒', 'SPEC011', 28.00, 38.00, 350, '6901234567897', 1, 1, 1),
(12, 9, '12片/盒', 'SPEC012', 16.50, 22.00, 500, '6901234567900', 1, 1, 1),
(13, 10, '10粒/盒', 'SPEC013', 20.61, 28.00, 300, '6901234567901', 1, 1, 1),
(14, 11, '0.3g*20粒', 'SPEC014', 15.80, 22.00, 600, '6901234567902', 1, 1, 1),
(15, 12, '0.5g*100片', 'SPEC015', 8.50, 12.00, 800, '6901234567903', 1, 1, 1),
(16, 13, '150ml/瓶', 'SPEC016', 28.80, 35.00, 450, '6901234567904', 1, 1, 1),
(17, 14, '0.25g*24粒', 'SPEC017', 12.50, 18.00, 500, '6901234567905', 1, 1, 1),
(18, 15, '20mg*14粒', 'SPEC018', 35.80, 45.00, 350, '6901234567906', 1, 1, 1),
(19, 16, '0.5g*20片', 'SPEC019', 28.00, 35.00, 400, '6901234567907', 1, 1, 1),
(20, 17, '标准版', 'SPEC020', 299.00, 399.00, 150, '6901234567910', 1, 1, 1),
(21, 18, '语音版', 'SPEC021', 199.00, 268.00, 200, '6901234567911', 1, 1, 1),
(22, 19, '主机+25试纸', 'SPEC022', 358.00, 458.00, 100, '6901234567912', 1, 1, 1),
(23, 20, '50只/盒', 'SPEC023', 29.90, 39.90, 1000, '6901234567913', 1, 1, 1),
(24, 21, '30片/瓶', 'SPEC024', 68.00, 88.00, 300, '6901234567914', 1, 1, 1),
(25, 22, '450g/罐', 'SPEC025', 298.00, 398.00, 150, '6901234567915', 1, 1, 1),
(26, 23, '400粒/瓶', 'SPEC026', 128.00, 168.00, 200, '6901234567916', 1, 1, 1);

-- =============================================
-- 五、门店相关数据
-- =============================================

-- 5.1 门店
INSERT INTO `dm_store` (`id`, `store_code`, `store_name`, `logo_text`, `logo_color`, `rating`, `monthly_sales`, `address`, `phone`, `business_hours`, `is_open`, `is_24hours`, `latitude`, `longitude`, `description`, `business_scope`, `license_no`, `is_insurance`, `is_chain`, `is_self_operated`, `delivery_time`, `min_delivery_amount`, `delivery_fee`, `status`, `sort_order`) VALUES
(1, 'STORE001', '海王星辰健康药房(朝阳店)', '海王', '#FFD700', 4.8, 1200, '北京市朝阳区建国路88号SOHO现代城底商', '010-85861234', '08:00-22:00', 1, 0, 39.908823, 116.462729, '专业药品零售连锁企业，提供处方药、非处方药、医疗器械、保健品等全品类商品。', '中成药、化学药制剂、抗生素、生化药品、生物制品（除疫苗）、医疗器械、保健食品', '京BA000001', 1, 1, 0, 25, 0.00, 5.00, 1, 1),
(2, 'STORE002', '老百姓大药房(海淀店)', '百姓', '#4CAF50', 4.9, 2300, '北京市海淀区中关村大街27号中关村大厦1层', '010-82651234', '07:30-22:30', 1, 0, 39.983424, 116.318179, '全国连锁药店品牌，品种齐全，价格实惠。', '中成药、化学药制剂、抗生素、生化药品、医疗器械、保健食品、日用百货', '京BA000002', 1, 1, 0, 35, 39.00, 0.00, 1, 2),
(3, 'STORE003', '叮当快药(国贸店)', '叮当', '#2196F3', 4.7, 3500, '北京市朝阳区建国门外大街1号国贸商城B1层', '400-0123-456', '24小时营业', 1, 1, 39.909730, 116.460700, '28分钟送药上门，24小时不间断服务。自营药房，品质保证。', '中成药、化学药制剂、抗生素、生化药品、医疗器械、防护用品', '京BA000003', 0, 0, 1, 28, 0.00, 0.00, 1, 3);

-- 5.2 门店资质认证
INSERT INTO `dm_store_certification` (`store_id`, `cert_name`, `cert_type`, `cert_no`, `issue_date`, `expire_date`, `status`) VALUES
(1, '药品经营许可证', 'license', '京BA000001', '2023-01-15', '2028-01-14', 1),
(1, 'GSP认证证书', 'gsp', 'GSP110000001', '2023-02-20', '2028-02-19', 1),
(1, '医保定点零售药店', 'insurance', 'YB110000001', '2023-03-10', '2026-03-09', 1),
(2, '药品经营许可证', 'license', '京BA000002', '2022-06-15', '2027-06-14', 1),
(2, 'GSP认证证书', 'gsp', 'GSP110000002', '2022-07-20', '2027-07-19', 1),
(2, '医保定点零售药店', 'insurance', 'YB110000002', '2022-08-10', '2025-08-09', 1),
(3, '药品经营许可证', 'license', '京BA000003', '2023-05-15', '2028-05-14', 1),
(3, 'GSP认证证书', 'gsp', 'GSP110000003', '2023-06-20', '2028-06-19', 1);

-- 5.3 门店服务承诺
INSERT INTO `dm_store_promise` (`store_id`, `promise_text`, `promise_type`, `sort_order`, `status`) VALUES
(1, '正品保证 假一赔十', 'quality', 1, 1),
(1, '药师咨询 专业指导', 'service', 2, 1),
(1, '隐私保护 安全配送', 'privacy', 3, 1),
(1, '7天无理由退换', 'return', 4, 1),
(2, '正品保证 假一赔十', 'quality', 1, 1),
(2, '专业药师在线', 'service', 2, 1),
(2, '满39元免配送费', 'delivery', 3, 1),
(2, '7天无理由退换', 'return', 4, 1),
(3, '28分钟送药上门', 'delivery', 1, 1),
(3, '24小时不间断服务', 'service', 2, 1),
(3, '自营药房 品质保证', 'quality', 3, 1),
(3, '隐私包装 安全配送', 'privacy', 4, 1);

-- 5.4 门店药品库存
INSERT INTO `dm_store_inventory` (`store_id`, `product_id`, `stock`, `warning_stock`, `price`, `original_price`, `discount`, `is_available`) VALUES
(1, 1, 256, 10, 15.80, 22.00, 28, 1),
(1, 2, 189, 10, 12.50, 18.00, 30, 1),
(1, 3, 320, 10, 8.90, 15.00, 40, 1),
(1, 4, 145, 10, 14.50, 24.00, 39, 1),
(1, 5, 278, 10, 15.90, 22.00, 27, 1),
(1, 6, 350, 10, 12.50, 18.00, 30, 1),
(1, 7, 98, 10, 28.60, 38.00, 24, 1),
(1, 8, 167, 10, 18.90, 26.00, 27, 1),
(2, 1, 234, 10, 18.50, 25.00, 26, 1),
(2, 2, 567, 10, 5.90, 12.00, 50, 1),
(2, 3, 123, 10, 32.80, 42.00, 21, 1),
(2, 4, 198, 10, 16.80, 24.00, 30, 1),
(2, 5, 87, 10, 45.00, 58.00, 22, 1),
(2, 6, 156, 10, 38.50, 52.00, 26, 1),
(3, 1, 999, 10, 9.90, 19.90, 50, 1),
(3, 2, 456, 10, 25.00, 35.00, 28, 1),
(3, 3, 234, 10, 59.00, 89.00, 33, 1),
(3, 4, 378, 10, 15.80, 25.00, 36, 1),
(3, 5, 167, 10, 22.80, 32.00, 28, 1),
(3, 6, 145, 10, 35.60, 48.00, 25, 1),
(3, 7, 289, 10, 12.00, 20.00, 40, 1);

-- 5.5 门店标签
INSERT INTO `dm_store_tag` (`store_id`, `tag_text`, `tag_type`, `sort_order`, `status`) VALUES
(1, '医保定点', 'primary', 1, 1),
(1, '24小时', 'success', 2, 1),
(1, '连锁品牌', 'info', 3, 1),
(2, '连锁品牌', 'primary', 1, 1),
(2, '满39免配送', 'warning', 2, 1),
(2, '正品保证', 'success', 3, 1),
(3, '28分钟达', 'success', 1, 1),
(3, '自营', 'primary', 2, 1),
(3, '夜间配送', 'info', 3, 1);

-- 5.6 门店评价
INSERT INTO `dm_store_review` (`store_id`, `user_id`, `order_id`, `rating`, `content`, `is_anonymous`, `status`) VALUES
(1, 1, 1, 5, '药品齐全，配送速度快，药师服务很专业！', 0, 1),
(1, 2, 2, 4, '价格实惠，包装仔细，下次还会来。', 0, 1),
(1, 3, 3, 5, '24小时营业很方便，半夜买药也不怕。', 0, 1),
(2, 1, 1, 5, '老百姓大药房品种多，价格公道。', 0, 1),
(2, 4, 4, 4, '满39免配送很划算，就是距离有点远。', 0, 1),
(3, 2, 5, 5, '28分钟送达真的很快，自营品质有保障！', 0, 1),
(3, 3, 3, 5, '深夜急需药品，叮当快药救急了！', 0, 1);

-- 5.7 门店商品分类 - 基于门店经营范围初始化
INSERT INTO `dm_category` (`id`, `name`, `code`, `parent_id`, `level`, `icon`, `type`, `sort`, `status`) VALUES
-- 门店商品分类
(201, '中成药', 'chinese-medicine', 0, 1, '', 'store', 1, 1),
(202, '化学药制剂', 'chemical-drug', 0, 1, '', 'store', 2, 1),
(203, '抗生素', 'antibiotic', 0, 1, '', 'store', 3, 1),
(204, '生化药品', 'biochemical', 0, 1, '', 'store', 4, 1),
(205, '生物制品', 'biological', 0, 1, '', 'store', 5, 1),
(206, '医疗器械', 'medical-device', 0, 1, '', 'store', 6, 1),
(207, '保健食品', 'health-food', 0, 1, '', 'store', 7, 1),
(208, '日用百货', 'daily-necessities', 0, 1, '', 'store', 8, 1),
(209, '防护用品', 'protective-equipment', 0, 1, '', 'store', 9, 1);

-- =============================================
-- 六、购物车数据
-- =============================================

INSERT INTO `dm_cart` (`id`, `user_id`, `product_id`, `quantity`, `selected`) VALUES
(1, 1, 2, 2, 1),
(2, 1, 3, 1, 1),
(3, 2, 1, 1, 1),
(4, 3, 5, 3, 1);

-- =============================================
-- 七、优惠券数据
-- =============================================

-- 7.1 优惠券模板
INSERT INTO `dm_coupon` (`id`, `name`, `type`, `value`, `min_amount`, `total_count`, `used_count`, `start_time`, `end_time`, `description`, `status`) VALUES
(1, '新用户专享券', 'full_reduction', 20.00, 100.00, 1000, 256, '2024-01-01 00:00:00', '2024-12-31 23:59:59', '新用户首单立减20元', 1),
(2, '满100减10', 'full_reduction', 10.00, 100.00, 500, 123, '2024-03-01 00:00:00', '2024-03-31 23:59:59', '全场通用', 1),
(3, '满200减30', 'full_reduction', 30.00, 200.00, 300, 45, '2024-03-01 00:00:00', '2024-03-31 23:59:59', '全场通用', 1),
(4, '9折优惠券', 'discount', 10.00, 50.00, 200, 67, '2024-03-01 00:00:00', '2024-03-31 23:59:59', '全场通用', 1);

-- 7.2 用户优惠券
INSERT INTO `dm_user_coupon` (`id`, `user_id`, `coupon_id`, `status`, `use_time`, `order_id`) VALUES
(1, 1, 1, 'used', '2024-03-20 10:30:00', 1),
(2, 1, 2, 'unused', NULL, NULL),
(3, 2, 3, 'used', '2024-03-20 16:00:00', 5),
(4, 3, 2, 'unused', NULL, NULL);

-- =============================================
-- 八、订单相关数据
-- =============================================

-- 8.1 订单
INSERT INTO `dm_order` (`id`, `order_no`, `user_id`, `address_id`, `total_amount`, `discount_amount`, `freight_amount`, `pay_amount`, `pay_type`, `pay_time`, `status`, `delivery_company`, `delivery_no`, `delivery_time`, `receive_time`, `remark`, `create_time`) VALUES
(1, 'DD202403200001', 1, 1, 258.00, 10.00, 0.00, 248.00, 1, '2024-03-20 10:30:00', 4, '顺丰快递', 'SF1234567890', '2024-03-21 09:00:00', '2024-03-22 14:30:00', '尽快发货', '2024-03-20 10:25:00'),
(2, 'DD202403200002', 2, 3, 156.00, 0.00, 0.00, 156.00, 2, '2024-03-20 11:00:00', 2, '顺丰快递', 'SF1234567891', '2024-03-20 15:00:00', NULL, '', '2024-03-20 10:55:00'),
(3, 'DD202403200003', 3, 4, 89.00, 0.00, 0.00, 89.00, 1, '2024-03-20 14:00:00', 1, NULL, NULL, NULL, NULL, '周末配送', '2024-03-20 13:50:00'),
(4, 'DD202403200004', 1, 1, 45.00, 0.00, 0.00, 45.00, NULL, NULL, 0, NULL, NULL, NULL, NULL, '', '2024-03-20 15:00:00'),
(5, 'DD202403200005', 2, 3, 320.00, 20.00, 0.00, 300.00, 1, '2024-03-20 16:00:00', -2, NULL, NULL, NULL, NULL, '申请退款', '2024-03-20 15:50:00');

-- 8.2 订单明细
INSERT INTO `dm_order_item` (`order_id`, `product_id`, `product_name`, `product_image`, `specification`, `price`, `quantity`, `total_amount`) VALUES
(1, 1, '阿莫西林胶囊', '', '0.25g*24粒', 12.50, 2, 25.00),
(1, 2, '布洛芬缓释胶囊', '', '0.3g*20粒', 15.80, 3, 47.40),
(1, 3, '感冒清热颗粒', '', '12g*10袋', 9.90, 5, 49.50),
(2, 4, '硝苯地平控释片', '', '30mg*7片', 35.00, 2, 70.00),
(2, 5, '蒙脱石散', '', '3g*10袋', 18.50, 2, 37.00),
(3, 6, '维生素C片', '', '100mg*100片', 5.80, 5, 29.00),
(3, 7, '复方氨酚烷胺片', '', '12片', 12.00, 5, 60.00),
(4, 8, '奥美拉唑肠溶胶囊', '', '20mg*14粒', 28.00, 1, 28.00),
(5, 1, '阿莫西林胶囊', '', '0.25g*24粒', 12.50, 10, 125.00),
(5, 2, '布洛芬缓释胶囊', '', '0.3g*20粒', 15.80, 5, 79.00);

-- 8.3 物流信息
INSERT INTO `dm_logistics` (`order_id`, `logistics_no`, `logistics_company`, `time`, `content`, `status`, `sort`) VALUES
(1, 'SF1234567890', '顺丰快递', '2024-03-20 10:30:00', '订单已提交，等待揽收', 'pending', 1),
(1, 'SF1234567890', '顺丰快递', '2024-03-20 15:00:00', '快递员已揽收', 'pickup', 2),
(1, 'SF1234567890', '顺丰快递', '2024-03-20 18:00:00', '快件已到达北京转运中心', 'transit', 3),
(1, 'SF1234567890', '顺丰快递', '2024-03-21 09:00:00', '快件已发出，前往北京朝阳区', 'transit', 4),
(1, 'SF1234567890', '顺丰快递', '2024-03-21 14:00:00', '快递员正在派送', 'delivering', 5),
(1, 'SF1234567890', '顺丰快递', '2024-03-22 14:30:00', '已签收，感谢使用顺丰', 'delivered', 6),
(2, 'SF1234567891', '顺丰快递', '2024-03-20 11:00:00', '订单已提交，等待揽收', 'pending', 1),
(2, 'SF1234567891', '顺丰快递', '2024-03-20 15:00:00', '快递员已揽收', 'pickup', 2),
(2, 'SF1234567891', '顺丰快递', '2024-03-20 18:00:00', '快件已到达北京转运中心', 'transit', 3),
(3, '', '', '2024-03-20 14:00:00', '订单待发货', 'pending', 1);

-- 8.4 退款
INSERT INTO `dm_refund` (`order_id`, `user_id`, `refund_no`, `status`, `reason`, `description`, `images`, `refund_amount`, `actual_refund_amount`, `audit_time`, `complete_time`) VALUES
(5, 2, 'RF202403200001', 'completed', '商品质量问题', '收到的药品包装破损，担心影响药效。', '[]', 300.00, 300.00, '2024-03-20 17:00:00', '2024-03-21 10:00:00');

-- =============================================
-- 九、问诊相关数据
-- =============================================

-- 9.1 问诊
INSERT INTO `dm_consultation` (`id`, `doctor_id`, `patient_id`, `user_id`, `type`, `symptom`, `status`, `is_urgent`, `is_rx`, `fee`, `start_time`, `end_time`, `create_time`) VALUES
('C001', 'DOC001', 1, 1, '图文问诊', '头疼、发烧三天，伴有咳嗽。', 'completed', 0, 1, 50.00, '2024-03-20 10:00:00', '2024-03-20 11:30:00', '2024-03-20 09:30:00'),
('C002', 'DOC002', 3, 2, '图文问诊', '咳嗽、咳痰一周，胸闷气短。', 'processing', 0, 1, 50.00, '2024-03-20 14:00:00', NULL, '2024-03-20 13:30:00'),
('C003', 'DOC003', 2, 1, '图文问诊', '孩子发烧38.5度，流鼻涕。', 'pending', 1, 0, 60.00, NULL, NULL, '2024-03-20 15:00:00'),
('C004', 'DOC001', 4, 3, '图文问诊', '血压控制不稳定，时高时低。', 'pending', 0, 1, 50.00, NULL, NULL, '2024-03-20 16:00:00');

-- 9.2 问诊消息
INSERT INTO `dm_consultation_message` (`consultation_id`, `sender_type`, `sender_id`, `type`, `content`) VALUES
('C001', 'system', 'system', 'text', '问诊已开始，请医生尽快接诊'),
('C001', 'patient', 'patient_1', 'text', '医生您好，我头疼发烧三天了，请问应该吃什么药？'),
('C002', 'system', 'system', 'text', '问诊已开始'),
('C002', 'patient', 'patient_2', 'text', '医生您好，我是慢性胃炎老患者了，最近胃又不太舒服。'),
('C002', 'doctor', 'DOC001', 'text', '您好，请问具体是哪些不舒服？是胃痛、胃胀还是反酸？');

-- =============================================
-- 十、处方相关数据
-- =============================================

-- 10.1 处方
INSERT INTO `dm_prescription` (`id`, `consultation_id`, `doctor_id`, `patient_id`, `user_id`, `diagnosis`, `status`, `total_amount`, `valid_days`, `expire_time`) VALUES
('PRES202403200001', 'C001', 'DOC001', 1, 1, '急性上呼吸道感染', 'approved', 62.00, 3, '2024-03-23 23:59:59'),
('PRES202403200002', 'C002', 'DOC002', 3, 2, '急性支气管炎', 'pending', 85.00, 3, '2024-03-23 23:59:59');

-- 10.2 处方明细
INSERT INTO `dm_prescription_item` (`prescription_id`, `product_id`, `product_name`, `specification`, `quantity`, `dosage`, `frequency`, `duration`, `price`) VALUES
('PRES202403200001', 2, '布洛芬缓释胶囊', '0.3g*20粒', 1, '1粒', '每日2次', '3天', 15.80),
('PRES202403200001', 3, '感冒清热颗粒', '12g*10袋', 2, '1袋', '每日2次', '5天', 9.90),
('PRES202403200002', 1, '阿莫西林胶囊', '0.25g*24粒', 2, '2粒', '每日3次', '7天', 12.50),
('PRES202403200002', 8, '奥美拉唑肠溶胶囊', '20mg*14粒', 1, '1粒', '每日1次', '14天', 28.00);

-- =============================================
-- 十一、收入相关数据
-- =============================================

INSERT INTO `dm_doctor_income` (`id`, `doctor_id`, `type`, `amount`, `patient_id`, `consultation_id`, `status`, `settle_time`) VALUES
('INC202403200001', 'DOC001', '图文问诊', 50.00, 1, 'C001', 'settled', '2024-03-21 00:00:00'),
('INC202403200002', 'DOC002', '图文问诊', 50.00, 3, 'C002', 'pending', NULL),
('INC202403200003', 'DOC001', '复诊开方', 80.00, 1, NULL, 'settled', '2024-03-22 00:00:00');

-- =============================================
-- 十二、内容管理数据
-- =============================================

-- 12.1 Banner
INSERT INTO `dm_banner` (`id`, `title`, `image`, `link`, `sort`, `status`) VALUES
(1, '春季健康节', '', '/activity/spring', 1, 1),
(2, '新用户专享', '', '/activity/newuser', 2, 1),
(3, '处方药专区', '', '/prescription', 3, 1);

-- 12.2 公告
INSERT INTO `dm_notice` (`id`, `title`, `content`, `type`, `status`) VALUES
(1, '平台服务升级通知', '尊敬的用户，为了提供更好的服务体验，平台将于近期进行系统升级，升级期间部分功能可能受到影响，敬请谅解。', 'notice', 1),
(2, '隐私政策更新', '根据相关法律法规要求，我们更新了隐私政策，请您仔细阅读并同意最新版本的隐私政策。', 'policy', 1),
(3, '处方药购买须知', '根据国家相关规定，购买处方药需要凭有效处方。您可以通过平台在线问诊获取电子处方。', 'notice', 1);

-- =============================================
-- 十三、补充数据
-- =============================================

-- 13.1 浏览历史
INSERT INTO `dm_browse_history` (`user_id`, `product_id`, `product_name`, `product_image`, `price`, `browse_time`) VALUES
(1, 1, '阿莫西林胶囊', '', 12.50, '2024-03-20 09:00:00'),
(1, 2, '布洛芬缓释胶囊', '', 15.80, '2024-03-20 09:05:00'),
(1, 3, '感冒清热颗粒', '', 9.90, '2024-03-20 09:10:00'),
(2, 4, '硝苯地平控释片', '', 35.00, '2024-03-20 10:00:00'),
(2, 5, '蒙脱石散', '', 18.50, '2024-03-20 10:05:00'),
(3, 6, '维生素C片', '', 5.80, '2024-03-20 11:00:00'),
(3, 7, '复方氨酚烷胺片', '', 12.00, '2024-03-20 11:05:00');

-- 13.2 药品评价
INSERT INTO `dm_product_review` (`user_id`, `user_name`, `user_avatar`, `product_id`, `order_id`, `rating`, `content`, `images`, `tags`, `is_anonymous`, `is_recommended`, `helpful_count`, `reply_content`, `reply_time`, `status`) VALUES
(1, '健康小达人', '', 1, 1, 5, '效果很好，退烧很快，正品保证！', '[]', '["疗效好","正品","发货快"]', 0, 1, 25, '感谢您的好评，祝您早日康复！', '2024-03-22 10:00:00', 1),
(1, '健康小达人', '', 2, 1, 4, '止痛效果不错，就是有点贵。', '[]', '["效果好"]', 0, 1, 18, NULL, NULL, 1),
(2, '李四', '', 3, 2, 5, '感冒药很有效，第二天就好了。', '[]', '["疗效好","性价比高"]', 0, 1, 32, '感谢支持，祝身体健康！', '2024-03-21 14:00:00', 1),
(3, '王五', '', 4, 3, 5, '降压药效果稳定，一直用这个牌子。', '[]', '["疗效好","品牌信赖"]', 0, 1, 15, NULL, NULL, 1),
(1, '健康小达人', '', 5, 1, 4, '止泻效果不错，家里常备。', '[]', '["居家必备"]', 0, 1, 10, NULL, NULL, 1);

-- 13.3 药品FAQ
INSERT INTO `dm_product_faq` (`product_id`, `question`, `answer`, `sort`, `status`) VALUES
(1, '阿莫西林需要处方吗？', '是的，阿莫西林属于处方药（RX），需要凭医生处方购买。', 1, 1),
(1, '阿莫西林的用法用量是多少？', '口服，成人一次0.5g（2粒），每6-8小时1次，一日不超过4g。儿童酌减。', 2, 1),
(1, '阿莫西林有什么副作用？', '常见副作用包括恶心、呕吐、腹泻等胃肠道反应，偶见皮疹等过敏反应。', 3, 1),
(2, '布洛芬可以空腹吃吗？', '建议饭后服用，以减少对胃肠道的刺激。', 1, 1),
(2, '布洛芬多久见效？', '一般服用后30分钟到1小时开始见效，镇痛效果可持续4-6小时。', 2, 1),
(3, '感冒清热颗粒适合什么类型的感冒？', '适用于风寒感冒，症状包括恶寒重、发热轻、无汗、头痛、鼻塞流清涕等。', 1, 1),
(4, '硝苯地平控释片可以掰开吃吗？', '不可以，控释片必须整片吞服，不能掰开、咀嚼或碾碎。', 1, 1),
(5, '蒙脱石散怎么服用？', '将本品倒入50ml温水中，搅匀后服用。成人一次1袋，一日3次。', 1, 1),
(6, '维生素C片可以长期吃吗？', '可以，但建议按照推荐剂量服用，成人每日1-2片即可。', 1, 1);

-- =============================================
-- 十四、字典数据
-- =============================================

-- 14.1 字典类型
INSERT INTO `dm_dict_type` (`id`, `dict_name`, `dict_type`, `status`, `remark`) VALUES
(1, '医生职称', 'doctor_title', 1, '医生的专业技术职称'),
(2, '医生性别', 'doctor_gender', 1, '医生的性别'),
(3, '问诊状态', 'consultation_status', 1, '问诊单的状态'),
(4, '处方状态', 'prescription_status', 1, '处方的审核状态'),
(5, '订单状态', 'order_status', 1, '订单的流转状态'),
(6, '支付方式', 'pay_type', 1, '订单支付的方式'),
(7, '药品类型', 'drug_type', 1, '药品的分类类型'),
(8, '是否处方药', 'is_rx', 1, '是否为处方药'),
(9, '用户性别', 'user_gender', 1, '用户的性别'),
(10, '实名认证状态', 'real_name_auth', 1, '用户实名认证状态'),
(11, '提现状态', 'withdrawal_status', 1, '提现申请的状态'),
(12, '收入状态', 'income_status', 1, '医生收入结算状态'),
(13, '门店状态', 'store_status', 1, '门店的营业状态'),
(14, '资质类型', 'certification_type', 1, '门店资质认证类型'),
(15, '标签类型', 'tag_type', 1, '科室/商品标签类型');

-- 14.2 字典数据 - 医生职称
INSERT INTO `dm_dict_data` (`dict_type`, `dict_label`, `dict_value`, `dict_sort`, `status`) VALUES
('doctor_title', '主任医师', 'chief', 1, 1),
('doctor_title', '副主任医师', 'associate_chief', 2, 1),
('doctor_title', '主治医师', 'attending', 3, 1),
('doctor_title', '住院医师', 'resident', 4, 1),
('doctor_title', '医师', 'physician', 5, 1);

-- 14.3 字典数据 - 医生性别
INSERT INTO `dm_dict_data` (`dict_type`, `dict_label`, `dict_value`, `dict_sort`, `status`) VALUES
('doctor_gender', '男', '1', 1, 1),
('doctor_gender', '女', '2', 2, 1);

-- 14.4 字典数据 - 问诊状态
INSERT INTO `dm_dict_data` (`dict_type`, `dict_label`, `dict_value`, `dict_sort`, `status`, `list_class`) VALUES
('consultation_status', '待接诊', 'pending', 1, 1, 'warning'),
('consultation_status', '问诊中', 'processing', 2, 1, 'primary'),
('consultation_status', '已完成', 'completed', 3, 1, 'success'),
('consultation_status', '已关闭', 'closed', 4, 1, 'info');

-- 14.5 字典数据 - 处方状态
INSERT INTO `dm_dict_data` (`dict_type`, `dict_label`, `dict_value`, `dict_sort`, `status`, `list_class`) VALUES
('prescription_status', '待审核', 'pending', 1, 1, 'warning'),
('prescription_status', '已通过', 'approved', 2, 1, 'success'),
('prescription_status', '已拒绝', 'rejected', 3, 1, 'danger');

-- 14.6 字典数据 - 订单状态
INSERT INTO `dm_dict_data` (`dict_type`, `dict_label`, `dict_value`, `dict_sort`, `status`, `list_class`) VALUES
('order_status', '待付款', '0', 1, 1, 'warning'),
('order_status', '待发货', '1', 2, 1, 'primary'),
('order_status', '待收货', '2', 3, 1, 'info'),
('order_status', '待评价', '3', 4, 1, 'success'),
('order_status', '已完成', '4', 5, 1, 'success'),
('order_status', '已取消', '-1', 6, 1, 'danger'),
('order_status', '退款中', '-2', 7, 1, 'warning'),
('order_status', '已退款', '-3', 8, 1, 'info');

-- 14.7 字典数据 - 支付方式
INSERT INTO `dm_dict_data` (`dict_type`, `dict_label`, `dict_value`, `dict_sort`, `status`) VALUES
('pay_type', '微信支付', '1', 1, 1),
('pay_type', '支付宝', '2', 2, 1),
('pay_type', '银行卡', '3', 3, 1);

-- 14.8 字典数据 - 是否处方药
INSERT INTO `dm_dict_data` (`dict_type`, `dict_label`, `dict_value`, `dict_sort`, `status`, `list_class`) VALUES
('is_rx', '非处方药', '0', 1, 1, 'info'),
('is_rx', '处方药', '1', 2, 1, 'danger');

-- 14.9 字典数据 - 用户性别
INSERT INTO `dm_dict_data` (`dict_type`, `dict_label`, `dict_value`, `dict_sort`, `status`) VALUES
('user_gender', '未知', '0', 1, 1),
('user_gender', '男', '1', 2, 1),
('user_gender', '女', '2', 3, 1);

-- 14.10 字典数据 - 实名认证状态
INSERT INTO `dm_dict_data` (`dict_type`, `dict_label`, `dict_value`, `dict_sort`, `status`, `list_class`) VALUES
('real_name_auth', '未认证', '0', 1, 1, 'warning'),
('real_name_auth', '已认证', '1', 2, 1, 'success');

-- 14.11 字典数据 - 提现状态
INSERT INTO `dm_dict_data` (`dict_type`, `dict_label`, `dict_value`, `dict_sort`, `status`, `list_class`) VALUES
('withdrawal_status', '待审核', 'pending', 1, 1, 'warning'),
('withdrawal_status', '已通过', 'approved', 2, 1, 'success'),
('withdrawal_status', '已拒绝', 'rejected', 3, 1, 'danger');

-- 14.12 字典数据 - 收入状态
INSERT INTO `dm_dict_data` (`dict_type`, `dict_label`, `dict_value`, `dict_sort`, `status`, `list_class`) VALUES
('income_status', '待结算', 'pending', 1, 1, 'warning'),
('income_status', '已结算', 'settled', 2, 1, 'success');

-- 14.13 字典数据 - 门店状态
INSERT INTO `dm_dict_data` (`dict_type`, `dict_label`, `dict_value`, `dict_sort`, `status`, `list_class`) VALUES
('store_status', '休息', '0', 1, 1, 'info'),
('store_status', '营业', '1', 2, 1, 'success');

-- 14.14 字典数据 - 资质类型
INSERT INTO `dm_dict_data` (`dict_type`, `dict_label`, `dict_value`, `dict_sort`, `status`) VALUES
('certification_type', '药品经营许可证', 'license', 1, 1),
('certification_type', 'GSP认证证书', 'gsp', 2, 1),
('certification_type', '医保定点零售药店', 'insurance', 3, 1);

-- 14.15 字典数据 - 标签类型
INSERT INTO `dm_dict_data` (`dict_type`, `dict_label`, `dict_value`, `dict_sort`, `status`, `list_class`) VALUES
('tag_type', '热门', 'hot', 1, 1, 'danger'),
('tag_type', '发热', 'fever', 2, 1, 'warning'),
('tag_type', '价格', 'price', 3, 1, 'success'),
('tag_type', '补充', 'supplement', 4, 1, 'info'),
('tag_type', '信息', 'info', 5, 1, 'info'),
('tag_type', '主要', 'primary', 6, 1, 'primary'),
('tag_type', '成功', 'success', 7, 1, 'success'),
('tag_type', '警告', 'warning', 8, 1, 'warning'),
('tag_type', '危险', 'danger', 9, 1, 'danger');

-- 14.16 字典数据 - 疾病标签
INSERT INTO `dm_dict_data` (`dict_type`, `dict_label`, `dict_value`, `dict_sort`, `status`) VALUES
('disease_tag', '感冒发热', '1', 1, 1),
('disease_tag', '咳嗽哮喘', '2', 2, 1),
('disease_tag', '肠胃不适', '3', 3, 1),
('disease_tag', '皮肤问题', '4', 4, 1),
('disease_tag', '心脑血管', '5', 5, 1),
('disease_tag', '糖尿病', '6', 6, 1),
('disease_tag', '高血压', '7', 7, 1),
('disease_tag', '高血脂', '8', 8, 1),
('disease_tag', '失眠焦虑', '9', 9, 1),
('disease_tag', '妇科问题', '10', 10, 1),
('disease_tag', '儿科问题', '11', 11, 1),
('disease_tag', '骨科问题', '12', 12, 1),
('disease_tag', '眼科问题', '13', 13, 1),
('disease_tag', '口腔问题', '14', 14, 1),
('disease_tag', '过敏鼻炎', '15', 15, 1),
('disease_tag', '肝病', '16', 16, 1),
('disease_tag', '肾病', '17', 17, 1),
('disease_tag', '肿瘤癌症', '18', 18, 1),
('disease_tag', '营养补充', '19', 19, 1),
('disease_tag', '其他疾病', '20', 20, 1);

-- =============================================
-- 十五、首页配置相关表
-- =============================================

-- 15.1 首页配置主表（版本管理）
DROP TABLE IF EXISTS `dm_home_page`;
CREATE TABLE `dm_home_page` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `page_id` VARCHAR(50) NOT NULL COMMENT '页面标识 home_page_001',
  `page_name` VARCHAR(100) NOT NULL COMMENT '页面名称',
  `version` VARCHAR(20) NOT NULL COMMENT '版本号 v1.0.0',
  `version_desc` VARCHAR(500) DEFAULT NULL COMMENT '版本描述',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态 0-草稿 1-已发布 2-已回滚',
  `is_current` TINYINT NOT NULL DEFAULT 0 COMMENT '是否当前使用版本 0-否 1-是',
  `bg_color` VARCHAR(20) DEFAULT '#F5F5F5' COMMENT '页面背景色',
  `header_gradient` VARCHAR(200) DEFAULT NULL COMMENT '头部渐变配置',
  `sticky_opacity` DECIMAL(3,2) DEFAULT 0.95 COMMENT '吸顶透明度',
  `publisher` VARCHAR(50) DEFAULT NULL COMMENT '发布人',
  `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_page_version` (`page_id`, `version`),
  KEY `idx_status` (`status`),
  KEY `idx_is_current` (`is_current`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页配置主表';

-- 15.2 Tab配置表
DROP TABLE IF EXISTS `dm_home_tab`;
CREATE TABLE `dm_home_tab` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Tab ID',
  `tab_id` VARCHAR(50) NOT NULL COMMENT 'Tab唯一标识 recommend/doctor/test/tcm/chronic',
  `name` VARCHAR(50) NOT NULL COMMENT 'Tab名称',
  `icon` VARCHAR(500) DEFAULT NULL COMMENT '默认图标',
  `active_icon` VARCHAR(500) DEFAULT NULL COMMENT '激活图标',
  `primary_color` VARCHAR(20) DEFAULT NULL COMMENT '主色调',
  `gradient` VARCHAR(200) DEFAULT NULL COMMENT '渐变配置',
  `bg_color` VARCHAR(20) DEFAULT NULL COMMENT '背景色',
  `header_bg_image` VARCHAR(500) DEFAULT NULL COMMENT '头部背景图',
  `section_ids` JSON DEFAULT NULL COMMENT '关联模块ID列表',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `page_id` VARCHAR(50) DEFAULT 'home_page_001' COMMENT '所属页面ID',
  `version` VARCHAR(20) DEFAULT NULL COMMENT '关联版本号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tab_id` (`tab_id`),
  KEY `idx_page_version` (`page_id`, `version`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tab配置表';

-- 15.3 首页模块配置表
DROP TABLE IF EXISTS `dm_home_section`;
CREATE TABLE `dm_home_section` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '模块ID',
  `name` VARCHAR(100) NOT NULL COMMENT '模块名称',
  `subtitle` VARCHAR(200) DEFAULT NULL COMMENT '副标题',
  `section_type` VARCHAR(30) NOT NULL COMMENT '模块类型 search_bar/tab_navigation/service_grid/banner_subsidy/doctor_banner/nearby_pharmacy/waterfall_layout/doctor_department/test_items/chronic_category/tcm_category/promo_banner/quick_consult_card/test_banner/tcm_banner/chronic_banner',
  `layout` VARCHAR(20) DEFAULT 'vertical' COMMENT '布局方式 vertical/horizontal/grid/waterfall',
  `bg_color` VARCHAR(20) DEFAULT NULL COMMENT '背景颜色',
  `border_radius` INT DEFAULT 8 COMMENT '圆角大小',
  `margin_top` INT DEFAULT 0 COMMENT '上边距',
  `margin_bottom` INT DEFAULT 12 COMMENT '下边距',
  `tab_ids` JSON DEFAULT NULL COMMENT '关联Tab ID列表',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `visible` TINYINT NOT NULL DEFAULT 1 COMMENT '是否可见 0-隐藏 1-显示',
  `config` JSON DEFAULT NULL COMMENT '模块特有配置',
  `content` JSON DEFAULT NULL COMMENT '模块内容数据',
  `page_id` VARCHAR(50) DEFAULT 'home_page_001' COMMENT '所属页面ID',
  `version` VARCHAR(20) DEFAULT NULL COMMENT '关联版本号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_section_type` (`section_type`),
  KEY `idx_page_version` (`page_id`, `version`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页模块配置表';

-- 15.4 金刚位配置表
DROP TABLE IF EXISTS `dm_home_kingkong`;
CREATE TABLE `dm_home_kingkong` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '金刚位ID',
  `name` VARCHAR(50) NOT NULL COMMENT '名称',
  `icon_url` VARCHAR(500) NOT NULL COMMENT '图标URL',
  `jump_type` VARCHAR(20) DEFAULT 'url' COMMENT '跳转类型 url/route/miniprogram/none',
  `jump_url` VARCHAR(500) DEFAULT NULL COMMENT '跳转链接',
  `bg_color` VARCHAR(20) DEFAULT NULL COMMENT '背景色',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `tab_id` VARCHAR(50) DEFAULT NULL COMMENT '所属Tab ID',
  `section_id` BIGINT DEFAULT NULL COMMENT '所属模块ID',
  `page_id` VARCHAR(50) DEFAULT 'home_page_001' COMMENT '所属页面ID',
  `version` VARCHAR(20) DEFAULT NULL COMMENT '关联版本号',
  `start_time` DATETIME DEFAULT NULL COMMENT '生效开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '生效结束时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_tab_id` (`tab_id`),
  KEY `idx_section_id` (`section_id`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='金刚位配置表';

-- 15.5 广告位配置表
DROP TABLE IF EXISTS `dm_home_ad_slot`;
CREATE TABLE `dm_home_ad_slot` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '广告位ID',
  `name` VARCHAR(100) NOT NULL COMMENT '广告位名称',
  `position` VARCHAR(50) NOT NULL COMMENT '位置标识',
  `ad_type` VARCHAR(20) DEFAULT 'image' COMMENT '广告类型 image/video/text',
  `image_url` VARCHAR(500) DEFAULT NULL COMMENT '图片URL',
  `video_url` VARCHAR(500) DEFAULT NULL COMMENT '视频URL',
  `title` VARCHAR(200) DEFAULT NULL COMMENT '广告标题',
  `subtitle` VARCHAR(200) DEFAULT NULL COMMENT '副标题',
  `jump_type` VARCHAR(20) DEFAULT 'url' COMMENT '跳转类型 url/route/miniprogram/none',
  `jump_url` VARCHAR(500) DEFAULT NULL COMMENT '跳转链接',
  `bg_color` VARCHAR(20) DEFAULT NULL COMMENT '背景色',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `tab_id` VARCHAR(50) DEFAULT NULL COMMENT '所属Tab ID',
  `section_id` BIGINT DEFAULT NULL COMMENT '所属模块ID',
  `page_id` VARCHAR(50) DEFAULT 'home_page_001' COMMENT '所属页面ID',
  `version` VARCHAR(20) DEFAULT NULL COMMENT '关联版本号',
  `start_time` DATETIME DEFAULT NULL COMMENT '生效开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '生效结束时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_position` (`position`),
  KEY `idx_tab_id` (`tab_id`),
  KEY `idx_section_id` (`section_id`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告位配置表';

-- 15.6 配置变更日志表
DROP TABLE IF EXISTS `dm_home_config_log`;
CREATE TABLE `dm_home_config_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `page_id` VARCHAR(50) NOT NULL COMMENT '页面ID',
  `version` VARCHAR(20) DEFAULT NULL COMMENT '版本号',
  `operation` VARCHAR(50) NOT NULL COMMENT '操作类型 create/update/delete/publish/rollback',
  `target_type` VARCHAR(50) NOT NULL COMMENT '目标类型 tab/section/kingkong/adslot/global',
  `target_id` VARCHAR(100) DEFAULT NULL COMMENT '目标ID',
  `before_data` JSON DEFAULT NULL COMMENT '变更前数据',
  `after_data` JSON DEFAULT NULL COMMENT '变更后数据',
  `operator` VARCHAR(50) DEFAULT NULL COMMENT '操作人',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_page_id` (`page_id`),
  KEY `idx_version` (`version`),
  KEY `idx_operation` (`operation`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配置变更日志表';

-- =============================================
-- 十六、首页配置模拟数据
-- =============================================

-- 16.1 首页配置主表数据
INSERT INTO `dm_home_page` (`id`, `page_id`, `page_name`, `version`, `version_desc`, `status`, `is_current`, `bg_color`, `header_gradient`, `sticky_opacity`, `publisher`, `publish_time`) VALUES
(1, 'home_page_001', 'DrugMall首页', 'v1.0.0', '初始版本，支持5个Tab动态配置', 1, 1, '#F5F5F5', 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', 0.95, 'admin', '2024-03-20 10:00:00');

-- 16.2 Tab配置数据（5个Tab）
INSERT INTO `dm_home_tab` (`id`, `tab_id`, `name`, `icon`, `active_icon`, `primary_color`, `gradient`, `bg_color`, `section_ids`, `sort_order`, `status`, `page_id`, `version`) VALUES
(1, 'recommend', '推荐', 'HomeFilled', 'HomeFilled', '#409EFF', 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', '#F5F5F5', '[1,2,3,4,5]', 1, 1, 'home_page_001', 'v1.0.0'),
(2, 'doctor', '问医生', 'ChatDotRound', 'ChatDotRound', '#67C23A', 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)', '#F8FFF8', '[6]', 2, 1, 'home_page_001', 'v1.0.0'),
(3, 'test', '做检测', 'Document', 'Document', '#E6A23C', 'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)', '#FFFAF0', '[7]', 3, 1, 'home_page_001', 'v1.0.0'),
(4, 'tcm', '中医保健', 'CoffeeCup', 'CoffeeCup', '#909399', 'linear-gradient(135deg, #d299c2 0%, #fef9d7 100%)', '#FDF6EC', '[8]', 4, 1, 'home_page_001', 'v1.0.0'),
(5, 'chronic', '慢病关怀', 'FirstAidKit', 'FirstAidKit', '#F56C6C', 'linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%)', '#FEF0F0', '[9]', 5, 1, 'home_page_001', 'v1.0.0');

-- 16.3 首页模块配置数据（9个模块）
INSERT INTO `dm_home_section` (`id`, `name`, `subtitle`, `section_type`, `layout`, `bg_color`, `border_radius`, `margin_top`, `margin_bottom`, `tab_ids`, `sort_order`, `status`, `visible`, `config`, `content`, `page_id`, `version`) VALUES
-- 推荐Tab模块（5个）
(1, '搜索栏', NULL, 'search_bar', 'horizontal', NULL, 0, 0, 0, '["recommend"]', 1, 1, 1, '{"placeholder": "搜索药品、症状、品牌"}', NULL, 'home_page_001', 'v1.0.0'),
(2, 'Tab导航', NULL, 'tab_navigation', 'horizontal', NULL, 0, 0, 0, '["recommend","doctor","test","tcm","chronic"]', 2, 1, 1, '{"sticky": true}', NULL, 'home_page_001', 'v1.0.0'),
(3, '24h服务网格', NULL, 'service_grid', 'grid', '#FFFFFF', 12, 12, 12, '["recommend"]', 3, 1, 1, '{"columns": 4}', '[{"id":"ask_doctor","name":"问医生","icon":"https://via.placeholder.com/60","jump":"/inquiry"},{"id":"quick_buy","name":"急送药","icon":"https://via.placeholder.com/60","jump":"/store"},{"id":"prescription","name":"开处方","icon":"https://via.placeholder.com/60","jump":"/prescription/apply"},{"id":"health_mall","name":"健康商城","icon":"https://via.placeholder.com/60","jump":"/category"}]', 'home_page_001', 'v1.0.0'),
(4, 'Banner轮播+百亿补贴', NULL, 'banner_subsidy', 'vertical', '#FFFFFF', 12, 12, 12, '["recommend"]', 4, 1, 1, '{"bannerHeight": 150}', '[{"id":1,"title":"春季健康节","image":"https://via.placeholder.com/750x300","link":"/activity/spring"},{"id":2,"title":"新用户专享","image":"https://via.placeholder.com/750x300","link":"/activity/newuser"}]', 'home_page_001', 'v1.0.0'),
(5, '附近急送药店', '最快28分钟送达', 'nearby_pharmacy', 'horizontal', '#FFFFFF', 12, 12, 12, '["recommend"]', 6, 1, 1, '{"showDistance": true, "maxCount": 3}', '{"stores": [{"id": 1, "name": "老百姓大药房(朝阳店)", "tags": [{"text": "医保", "type": "success"}, {"text": "24小时", "type": "warning"}, {"text": "急送", "type": "primary"}], "phone": "010-65012345", "rating": 4.8, "address": "北京市朝阳区朝阳北路102号", "distance": 1.2, "logoText": "老百", "logoColor": "#FF6B35", "deliveryFee": 0, "deliveryTime": 28, "monthlySales": 3200, "minDeliveryAmount": 0}, {"id": 2, "name": "同仁堂(王府井店)", "tags": [{"text": "老字号", "type": "success"}, {"text": "中药", "type": "info"}], "phone": "010-65234567", "rating": 4.9, "address": "北京市东城区王府井大街88号", "distance": 2.5, "logoText": "同仁", "logoColor": "#D32F2F", "deliveryFee": 3, "deliveryTime": 35, "monthlySales": 4500, "minDeliveryAmount": 29}, {"id": 3, "name": "益丰大药房(中关村店)", "tags": [{"text": "新店", "type": "warning"}, {"text": "满减", "type": "danger"}], "phone": "010-62345678", "rating": 4.6, "address": "北京市海淀区中关村大街32号", "distance": 3.1, "logoText": "益丰", "logoColor": "#4CAF50", "deliveryFee": 2, "deliveryTime": 40, "monthlySales": 1800, "minDeliveryAmount": 19}]}', 'home_page_001', 'v1.0.0'),
(10, '精选推荐', '品质好药 放心购买', 'waterfall_layout', 'waterfall', '#FFFFFF', 12, 12, 12, '["recommend"]', 5, 1, 1, '{"columns": 2}', '[{"type": "ad", "title": "春季健康节", "btnText": "立即抢购", "imageUrl": "https://images.unsplash.com/photo-1584515933487-779824d29309?w=400&h=200&fit=crop", "subtitle": "全场满99减20", "bgGradient": "linear-gradient(135deg, #667eea 0%, #764ba2 100%)"}, {"isRx": 1, "name": "阿莫西林胶囊", "type": "product", "price": "15.80", "sales": "12.5", "discount": 37, "imageUrl": "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=200&h=200&fit=crop", "productId": 1, "deliveryTime": "30", "originalPrice": "25.00", "specification": "0.25g*24粒"}, {"isRx": 0, "name": "布洛芬缓释胶囊", "type": "product", "price": "12.50", "sales": "8.6", "discount": 31, "imageUrl": "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=200&h=200&fit=crop", "productId": 2, "deliveryTime": "25", "originalPrice": "18.00", "specification": "0.3g*20粒"}, {"type": "ad", "title": "新用户专享", "btnText": "领取优惠", "imageUrl": "https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=400&h=200&fit=crop", "subtitle": "首单立减15元", "bgGradient": "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)"}, {"isRx": 0, "name": "感冒清热颗粒", "type": "product", "price": "9.90", "sales": "15.2", "discount": 34, "imageUrl": "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=200&h=200&fit=crop", "productId": 3, "deliveryTime": "28", "originalPrice": "15.00", "specification": "12g*10袋"}, {"isRx": 1, "name": "硝苯地平控释片", "type": "product", "price": "28.00", "sales": "6.8", "discount": 20, "imageUrl": "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=200&h=200&fit=crop", "productId": 4, "deliveryTime": "35", "originalPrice": "35.00", "specification": "30mg*7片"}, {"isRx": 0, "name": "蒙脱石散", "type": "product", "price": "8.50", "sales": "9.3", "discount": 29, "imageUrl": "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=200&h=200&fit=crop", "productId": 5, "deliveryTime": "30", "originalPrice": "12.00", "specification": "3g*10袋"}, {"isRx": 0, "name": "维生素C片", "type": "product", "price": "19.90", "sales": "18.5", "discount": 33, "imageUrl": "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=200&h=200&fit=crop", "productId": 6, "deliveryTime": "25", "originalPrice": "29.90", "specification": "100mg*100片"}, {"type": "ad", "title": "慢病管理专区", "btnText": "查看详情", "imageUrl": "https://images.unsplash.com/photo-1579684385127-1ef15d508118?w=400&h=200&fit=crop", "subtitle": "长期用药更优惠", "bgGradient": "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)"}, {"isRx": 1, "name": "奥美拉唑肠溶胶囊", "type": "product", "price": "18.00", "sales": "7.2", "discount": 36, "imageUrl": "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=200&h=200&fit=crop", "productId": 10, "deliveryTime": "32", "originalPrice": "28.00", "specification": "20mg*14粒"}]', 'home_page_001', 'v1.0.0'),

-- 问医生Tab模块（1个）
(6, '问医生科室列表', '选择科室快速问诊', 'doctor_department', 'grid', '#FFFFFF', 12, 12, 12, '["doctor"]', 1, 1, 1, '{"columns": 4}', NULL, 'home_page_001', 'v1.0.0'),

-- 做检测Tab模块（1个）
(7, '检测项目列表', '专业检测服务', 'test_items', 'vertical', '#FFFFFF', 12, 12, 12, '["test"]', 1, 1, 1, '{"showPrice": true}', NULL, 'home_page_001', 'v1.0.0'),

-- 中医保健Tab模块（1个）
(8, '中医保健分类', '传统中药养生', 'tcm_category', 'grid', '#FFFFFF', 12, 12, 12, '["tcm"]', 1, 1, 1, '{"columns": 3}', NULL, 'home_page_001', 'v1.0.0'),

-- 慢病关怀Tab模块（1个）
(9, '慢病关怀分类', '慢性病管理服务', 'chronic_category', 'grid', '#FFFFFF', 12, 12, 12, '["chronic"]', 1, 1, 1, '{"columns": 3}', NULL, 'home_page_001', 'v1.0.0');

-- 16.4 金刚位配置数据（问医生Tab - 8个科室）
INSERT INTO `dm_home_kingkong` (`id`, `name`, `icon_url`, `jump_type`, `jump_url`, `bg_color`, `sort_order`, `status`, `tab_id`, `section_id`, `page_id`, `version`) VALUES
(1, '内科', 'https://via.placeholder.com/60', 'route', '/inquiry?dept=internal', '#E3F2FD', 1, 1, 'doctor', 6, 'home_page_001', 'v1.0.0'),
(2, '外科', 'https://via.placeholder.com/60', 'route', '/inquiry?dept=surgery', '#F3E5F5', 2, 1, 'doctor', 6, 'home_page_001', 'v1.0.0'),
(3, '儿科', 'https://via.placeholder.com/60', 'route', '/inquiry?dept=pediatrics', '#E8F5E9', 3, 1, 'doctor', 6, 'home_page_001', 'v1.0.0'),
(4, '妇科', 'https://via.placeholder.com/60', 'route', '/inquiry?dept=gynecology', '#FCE4EC', 4, 1, 'doctor', 6, 'home_page_001', 'v1.0.0'),
(5, '皮肤科', 'https://via.placeholder.com/60', 'route', '/inquiry?dept=dermatology', '#FFF3E0', 5, 1, 'doctor', 6, 'home_page_001', 'v1.0.0'),
(6, '中医科', 'https://via.placeholder.com/60', 'route', '/inquiry?dept=tcm', '#EFEBE9', 6, 1, 'doctor', 6, 'home_page_001', 'v1.0.0'),
(7, '心理科', 'https://via.placeholder.com/60', 'route', '/inquiry?dept=psychology', '#E0F7FA', 7, 1, 'doctor', 6, 'home_page_001', 'v1.0.0'),
(8, '更多', 'https://via.placeholder.com/60', 'route', '/inquiry/departments', '#F5F5F5', 8, 1, 'doctor', 6, 'home_page_001', 'v1.0.0');

-- 16.5 配置变更日志数据
INSERT INTO `dm_home_config_log` (`id`, `page_id`, `version`, `operation`, `target_type`, `target_id`, `before_data`, `after_data`, `operator`, `remark`) VALUES
(1, 'home_page_001', 'v1.0.0', 'create', 'global', NULL, NULL, '{"pageName": "DrugMall首页", "version": "v1.0.0"}', 'admin', '创建首页配置'),
(2, 'home_page_001', 'v1.0.0', 'create', 'tab', 'recommend', NULL, '{"tabId": "recommend", "name": "推荐"}', 'admin', '创建推荐Tab'),
(3, 'home_page_001', 'v1.0.0', 'create', 'tab', 'doctor', NULL, '{"tabId": "doctor", "name": "问医生"}', 'admin', '创建问医生Tab'),
(4, 'home_page_001', 'v1.0.0', 'publish', 'global', NULL, NULL, NULL, 'admin', '发布首页配置v1.0.0');

-- =============================================
-- 十七、补充数据
-- =============================================

-- 17.1 浏览历史
INSERT INTO `dm_browse_history` (`user_id`, `product_id`, `product_name`, `product_image`, `price`, `browse_time`) VALUES
(1, 1, '阿莫西林胶囊', '', 12.50, '2024-03-20 09:00:00'),
(1, 2, '布洛芬缓释胶囊', '', 15.80, '2024-03-20 09:05:00'),
(1, 3, '感冒清热颗粒', '', 9.90, '2024-03-20 09:10:00'),
(2, 4, '硝苯地平控释片', '', 35.00, '2024-03-20 10:00:00'),
(2, 5, '蒙脱石散', '', 18.50, '2024-03-20 10:05:00'),
(3, 6, '维生素C片', '', 5.80, '2024-03-20 11:00:00'),
(3, 7, '复方氨酚烷胺片', '', 12.00, '2024-03-20 11:05:00');

-- =============================================
-- 十八、管理后台测试数据
-- =============================================

-- 18.1 医生数据
DELETE FROM `dm_doctor` WHERE `id` IN ('DOC001','DOC002','DOC003','DOC004','DOC005','DOC006','DOC007','DOC008');
INSERT INTO `dm_doctor` (`id`, `phone`, `password`, `name`, `avatar`, `gender`, `title`, `hospital`, `department`, `license_no`, `is_certified`, `rating`, `service_count`, `response_time`, `specialties`, `introduction`, `balance`, `status`, `create_time`, `update_time`, `is_deleted`) VALUES
('DOC001', '13900001001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张伟', '', 1, '主任医师', '北京协和医院', '内科', '110000199001011234', 1, 4.8, 1256, 3, '高血压、糖尿病、冠心病', '从事临床工作20年，擅长内科常见病、多发病的诊断与治疗', 15800.00, 1, NOW(), NOW(), 0),
('DOC002', '13900001002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李娜', '', 2, '副主任医师', '北京协和医院', '外科', '110000199002021234', 1, 4.9, 986, 5, '微创手术、腹腔镜', '从事普外科临床工作15年，擅长微创手术', 12500.00, 1, NOW(), NOW(), 0),
('DOC003', '13900001003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王强', '', 1, '主治医师', '北京大学第一医院', '儿科', '110000199003031234', 1, 4.7, 756, 4, '小儿呼吸、小儿消化', '从事儿科临床工作10年，擅长小儿常见病诊治', 8900.00, 1, NOW(), NOW(), 0),
('DOC004', '13900001004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '赵敏', '', 2, '主任医师', '北京大学第三医院', '妇产科', '110000199004041234', 1, 4.9, 1580, 2, '高危妊娠、妇科肿瘤', '从事妇产科临床工作25年，擅长高危妊娠管理', 22000.00, 1, NOW(), NOW(), 0),
('DOC005', '13900001005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '刘洋', '', 1, '副主任医师', '北京中医医院', '中医科', '110000199005051234', 1, 4.6, 658, 6, '中医内科、针灸推拿', '从事中医临床工作12年，擅长中医辨证施治', 7800.00, 1, NOW(), NOW(), 0),
('DOC006', '13900001006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '陈静', '', 2, '主治医师', '北京同仁医院', '眼科', '110000199006061234', 0, 0.0, 0, 0, '白内障、青光眼', '从事眼科临床工作8年', 0.00, 0, NOW(), NOW(), 0),
('DOC007', '13900001007', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '孙磊', '', 1, '住院医师', '北京积水潭医院', '骨科', '110000199007071234', 0, 0.0, 0, 0, '骨折、关节置换', '从事骨科临床工作3年', 0.00, 0, NOW(), NOW(), 0),
('DOC008', '13900001008', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '周婷', '', 2, '主任医师', '北京天坛医院', '神经内科', '110000199008081234', 1, 4.8, 1120, 3, '脑血管病、癫痫', '从事神经内科临床工作22年', 18500.00, 1, NOW(), NOW(), 0);

-- 18.2 科室数据
DELETE FROM `dm_department` WHERE `code` LIKE 'DEPT%';
INSERT INTO `dm_department` (`code`, `name`, `icon`, `tag`, `tag_type`, `parent_id`, `sort`, `status`, `create_time`, `update_time`, `is_deleted`) VALUES
('DEPT01', '内科', 'icon-neike', '热门', 'success', 0, 1, 1, NOW(), NOW(), 0),
('DEPT0101', '心血管内科', '', '', 'info', 1, 1, 1, NOW(), NOW(), 0),
('DEPT0102', '呼吸内科', '', '', 'info', 1, 2, 1, NOW(), NOW(), 0),
('DEPT0103', '消化内科', '', '', 'info', 1, 3, 1, NOW(), NOW(), 0),
('DEPT02', '外科', 'icon-waike', '热门', 'success', 0, 2, 1, NOW(), NOW(), 0),
('DEPT0201', '普外科', '', '', 'info', 5, 1, 1, NOW(), NOW(), 0),
('DEPT0202', '骨科', '', '', 'info', 5, 2, 1, NOW(), NOW(), 0),
('DEPT0203', '泌尿外科', '', '', 'info', 5, 3, 1, NOW(), NOW(), 0),
('DEPT03', '儿科', 'icon-erke', '', 'info', 0, 3, 1, NOW(), NOW(), 0),
('DEPT04', '妇产科', 'icon-fuchan', '热门', 'success', 0, 4, 1, NOW(), NOW(), 0),
('DEPT05', '中医科', 'icon-zhongyi', '', 'info', 0, 5, 1, NOW(), NOW(), 0),
('DEPT06', '眼科', 'icon-yanke', '', 'info', 0, 6, 1, NOW(), NOW(), 0),
('DEPT07', '口腔科', 'icon-kouqiang', '', 'info', 0, 7, 1, NOW(), NOW(), 0),
('DEPT08', '皮肤科', 'icon-pifu', '', 'info', 0, 8, 1, NOW(), NOW(), 0),
('DEPT09', '神经内科', 'icon-shenjing', '', 'info', 0, 9, 1, NOW(), NOW(), 0);

-- 18.3 问诊数据
DELETE FROM `dm_consultation` WHERE `id` LIKE 'CONS%';
INSERT INTO `dm_consultation` (`id`, `doctor_id`, `patient_id`, `user_id`, `type`, `symptom`, `images`, `status`, `is_urgent`, `is_rx`, `fee`, `start_time`, `end_time`, `create_time`, `update_time`) VALUES
('CONS001', 'DOC001', 1, 1, '图文问诊', '最近经常头晕，血压偏高，想咨询一下', '', 'completed', 0, 1, 50.00, '2024-03-20 10:00:00', '2024-03-20 10:30:00', '2024-03-20 09:55:00', '2024-03-20 10:30:00'),
('CONS002', 'DOC002', 2, 2, '视频问诊', '腹部疼痛，持续两天了', '', 'completed', 1, 1, 100.00, '2024-03-20 14:00:00', '2024-03-20 14:45:00', '2024-03-20 13:50:00', '2024-03-20 14:45:00'),
('CONS003', 'DOC003', 3, 3, '图文问诊', '孩子发烧38.5度，咳嗽', '', 'completed', 0, 1, 30.00, '2024-03-21 09:00:00', '2024-03-21 09:20:00', '2024-03-21 08:55:00', '2024-03-21 09:20:00'),
('CONS004', 'DOC004', 4, 4, '视频问诊', '孕28周，胎动减少', '', 'completed', 1, 0, 120.00, '2024-03-21 15:00:00', '2024-03-21 15:40:00', '2024-03-21 14:50:00', '2024-03-21 15:40:00'),
('CONS005', 'DOC005', 5, 5, '图文问诊', '失眠多梦，食欲不振', '', 'pending', 0, 0, 40.00, NULL, NULL, '2024-03-22 08:00:00', '2024-03-22 08:00:00'),
('CONS006', 'DOC008', 1, 1, '图文问诊', '头痛，记忆力下降', '', 'pending', 0, 0, 60.00, NULL, NULL, '2024-03-22 10:00:00', '2024-03-22 10:00:00'),
('CONS007', 'DOC001', 2, 2, '图文问诊', '血糖控制不好，想调整用药', '', 'cancelled', 0, 1, 50.00, NULL, NULL, '2024-03-19 16:00:00', '2024-03-19 17:00:00');

-- 18.4 处方数据
DELETE FROM `dm_prescription` WHERE `id` LIKE 'RX%';
INSERT INTO `dm_prescription` (`id`, `consultation_id`, `doctor_id`, `patient_id`, `user_id`, `diagnosis`, `status`, `reject_reason`, `total_amount`, `valid_days`, `expire_time`, `create_time`, `update_time`) VALUES
('RX001', 'CONS001', 'DOC001', 1, 1, '高血压2级', 'approved', NULL, 156.50, 7, '2024-03-27 10:30:00', '2024-03-20 10:30:00', '2024-03-20 10:30:00'),
('RX002', 'CONS002', 'DOC002', 2, 2, '急性胃炎', 'approved', NULL, 89.00, 7, '2024-03-27 14:45:00', '2024-03-20 14:45:00', '2024-03-20 14:45:00'),
('RX003', 'CONS003', 'DOC003', 3, 3, '上呼吸道感染', 'approved', NULL, 45.00, 5, '2024-03-26 09:20:00', '2024-03-21 09:20:00', '2024-03-21 09:20:00'),
('RX004', 'CONS004', 'DOC004', 4, 4, '妊娠晚期', 'approved', NULL, 230.00, 7, '2024-03-28 15:40:00', '2024-03-21 15:40:00', '2024-03-21 15:40:00'),
('RX005', 'CONS005', 'DOC005', 5, 5, '心脾两虚', 'pending', NULL, 0.00, 7, NULL, '2024-03-22 08:00:00', '2024-03-22 08:00:00'),
('RX006', 'CONS006', 'DOC008', 1, 1, '脑供血不足', 'pending', NULL, 0.00, 7, NULL, '2024-03-22 10:00:00', '2024-03-22 10:00:00');

-- 18.5 就诊人数据
DELETE FROM `dm_patient` WHERE `user_id` IN (1,2,3,4,5);
INSERT INTO `dm_patient` (`user_id`, `name`, `gender`, `birthday`, `id_card`, `phone`, `relationship`, `allergy_history`, `medical_history`, `is_default`, `create_time`, `update_time`, `is_deleted`) VALUES
(1, '张三', 1, '1990-05-20', '110101199005201234', '13800138000', '本人', '青霉素过敏', '高血压', 1, NOW(), NOW(), 0),
(1, '张小明', 1, '2018-08-15', '110101201808151234', '13800138000', '子女', '无', '无', 0, NOW(), NOW(), 0),
(2, '李四', 2, '1992-03-15', '110101199203152345', '13800138001', '本人', '磺胺类过敏', '胃炎', 1, NOW(), NOW(), 0),
(3, '王五', 1, '1985-08-10', '110101198508102345', '13800138002', '本人', '无', '糖尿病', 1, NOW(), NOW(), 0),
(4, '赵六', 2, '1988-12-25', '110101198812253456', '13800138003', '本人', '无', '孕期', 1, NOW(), NOW(), 0),
(5, '钱七', 1, '1988-07-18', '110101198807184567', '13800138004', '本人', '花粉过敏', '失眠', 1, NOW(), NOW(), 0);

-- 18.6 药店数据
DELETE FROM `dm_store` WHERE `store_code` LIKE 'STORE%';
INSERT INTO `dm_store` (`store_code`, `store_name`, `logo`, `logo_text`, `logo_color`, `rating`, `monthly_sales`, `address`, `phone`, `business_hours`, `is_open`, `is_24hours`, `latitude`, `longitude`, `description`, `business_scope`, `license_no`, `is_insurance`, `is_chain`, `is_self_operated`, `delivery_time`, `min_delivery_amount`, `delivery_fee`, `status`, `sort_order`, `create_time`, `update_time`, `is_deleted`) VALUES
('STORE001', '同仁堂大药房', '', '同', '#8B4513', 4.8, 2580, '北京市东城区王府井大街1号', '010-12345678', '08:00-22:00', 1, 0, 39.908823, 116.412345, '百年老字号，品质保证', '中药饮片、中成药、西药、保健品', '11010120240001', 1, 1, 1, 30, 29.00, 5.00, 1, 1, NOW(), NOW(), 0),
('STORE002', '国大药房', '', '国', '#FF6B35', 4.6, 1856, '北京市朝阳区建国路88号', '010-87654321', '09:00-21:00', 1, 0, 39.915678, 116.465432, '全国连锁，品种齐全', '处方药、非处方药、医疗器械', '11010520240002', 1, 1, 0, 45, 39.00, 6.00, 1, 2, NOW(), NOW(), 0),
('STORE003', '老百姓大药房', '', '老', '#4CAF50', 4.5, 1234, '北京市海淀区中关村大街66号', '010-11223344', '08:30-21:30', 1, 0, 39.982345, 116.312345, '平价药品，服务百姓', '中西药、保健品、日用品', '11010820240003', 1, 1, 0, 60, 19.00, 4.00, 1, 3, NOW(), NOW(), 0),
('STORE004', '益丰大药房', '', '益', '#2196F3', 4.7, 1567, '北京市西城区西单北大街120号', '010-55667788', '24小时', 1, 1, 39.908765, 116.375432, '24小时营业，随时为您服务', '处方药、非处方药、急救药品', '11010220240004', 1, 1, 0, 20, 0.00, 8.00, 1, 4, NOW(), NOW(), 0),
('STORE005', '一心堂', '', '一', '#9C27B0', 4.4, 986, '北京市丰台区南三环西路16号', '010-99887766', '09:00-20:00', 1, 0, 39.856789, 116.345678, '用心服务，健康万家', '中药、西药、保健品', '11010620240005', 0, 0, 0, 90, 49.00, 7.00, 0, 5, NOW(), NOW(), 0);

-- 18.7 系统配置数据
DELETE FROM `dm_system_config` WHERE `config_key` IN ('site_name','site_logo','site_description','contact_phone','contact_email','payment_alipay','payment_wechat','payment_balance','min_order_amount','free_delivery_amount','auto_confirm_hours','refund_timeout_hours');
INSERT INTO `dm_system_config` (`config_key`, `config_value`, `description`, `create_time`, `update_time`) VALUES
('site_name', 'DrugMall', '网站名称', NOW(), NOW()),
('site_logo', '', '网站Logo', NOW(), NOW()),
('site_description', '互联网药品电商平台', '网站描述', NOW(), NOW()),
('contact_phone', '400-123-4567', '客服电话', NOW(), NOW()),
('contact_email', 'service@drugmall.com', '客服邮箱', NOW(), NOW()),
('payment_alipay', '1', '支付宝支付开关', NOW(), NOW()),
('payment_wechat', '1', '微信支付开关', NOW(), NOW()),
('payment_balance', '1', '余额支付开关', NOW(), NOW()),
('min_order_amount', '29.00', '最低起送金额', NOW(), NOW()),
('free_delivery_amount', '99.00', '免运费金额', NOW(), NOW()),
('auto_confirm_hours', '24', '自动确认收货时间(小时)', NOW(), NOW()),
('refund_timeout_hours', '48', '退款审核超时时间(小时)', NOW(), NOW());

-- 18.8 优惠券数据补充
INSERT IGNORE INTO `dm_coupon` (`id`, `name`, `type`, `value`, `min_amount`, `total_count`, `used_count`, `start_time`, `end_time`, `description`, `status`, `create_time`, `update_time`, `is_deleted`) VALUES
(6, '新用户专享券', 'discount', 20.00, 50.00, 1000, 256, '2024-03-01 00:00:00', '2024-06-30 23:59:59', '新用户注册专享优惠', 1, NOW(), NOW(), 0),
(7, '满减优惠券', 'discount', 30.00, 100.00, 500, 128, '2024-03-15 00:00:00', '2024-05-31 23:59:59', '满100减30', 1, NOW(), NOW(), 0),
(8, '处方药专享', 'percent', 15.00, 0.00, 200, 45, '2024-04-01 00:00:00', '2024-04-30 23:59:59', '处方药85折优惠', 1, NOW(), NOW(), 0);

-- 18.9 用户优惠券数据补充
INSERT IGNORE INTO `dm_user_coupon` (`user_id`, `coupon_id`, `status`, `use_time`, `order_id`, `create_time`) VALUES
(1, 6, 'unused', NULL, NULL, NOW()),
(2, 7, 'unused', NULL, NULL, NOW()),
(3, 8, 'unused', NULL, NULL, NOW());

-- =============================================
-- 十九、问诊测试数据补充
-- =============================================

-- 19.1 测试医生数据（英文测试数据）
INSERT INTO `dm_doctor` (`id`, `phone`, `password`, `name`, `title`, `hospital`, `department`, `avatar`, `introduction`, `specialties`, `rating`, `service_count`, `status`, `create_time`) 
VALUES ('DOC001', '13800138000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Dr. Zhang', 'Attending Physician', 'Beijing Union Hospital', 'Internal Medicine', 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200', '12 years of clinical experience', 'Common diseases', 4.9, 11000, 1, NOW())
ON DUPLICATE KEY UPDATE 
  `name` = VALUES(`name`),
  `status` = VALUES(`status`);

-- 19.2 测试问诊记录
INSERT INTO `dm_consultation` (`id`, `doctor_id`, `patient_id`, `user_id`, `type`, `symptom`, `status`, `is_urgent`, `is_rx`, `fee`, `create_time`) 
VALUES 
('CONS1001', 'DOC001', 1, 1, 'text', 'Prescription request - Disease: Cold and fever, Symptoms: Headache and fever for 3 days', 'pending', 0, 1, 19.90, NOW()),
('CONS1002', 'DOC001', 1, 1, 'text', 'Chronic gastritis follow-up, medication consultation', 'processing', 0, 0, 19.90, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('CONS1003', 'DOC001', 1, 1, 'text', 'Skin allergy, itching', 'completed', 0, 1, 19.90, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 19.3 测试消息数据
INSERT INTO `dm_consultation_message` (`consultation_id`, `sender_type`, `sender_id`, `type`, `content`, `create_time`) 
VALUES 
('CONS1002', 'patient', 'USER001', 'text', 'Hello doctor, I have had headache and fever for three days. What medicine should I take?', DATE_SUB(NOW(), INTERVAL 50 MINUTE)),
('CONS1002', 'doctor', 'DOC001', 'text', 'Hello, besides headache and fever, do you have other symptoms? Such as cough or sore throat?', DATE_SUB(NOW(), INTERVAL 48 MINUTE)),
('CONS1002', 'patient', 'USER001', 'text', 'A little cough and sore throat', DATE_SUB(NOW(), INTERVAL 45 MINUTE));

-- =============================================
-- 二十、首页推荐完善数据（金刚位、分类、商品）
-- =============================================

-- 20.1 首页推荐金刚位 - 10个分类，使用真实图片
DELETE FROM `dm_home_kingkong` WHERE `tab_id` = 'recommend' OR `section_id` = 3;
INSERT INTO `dm_home_kingkong` (`id`, `name`, `icon_url`, `jump_type`, `jump_url`, `bg_color`, `sort_order`, `status`, `tab_id`, `section_id`, `page_id`, `version`) VALUES
(101, '感冒发烧', 'https://images.unsplash.com/photo-1584515933487-779824d29309?w=120&h=120&fit=crop', 'route', '/category?keyword=感冒发烧', '#E3F2FD', 1, 1, 'recommend', 3, 'home_page_001', 'v1.0.0'),
(102, '咳嗽化痰', 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=120&h=120&fit=crop', 'route', '/category?keyword=咳嗽化痰', '#F3E5F5', 2, 1, 'recommend', 3, 'home_page_001', 'v1.0.0'),
(103, '肠胃用药', 'https://images.unsplash.com/photo-1559757175-5700dde675bc?w=120&h=120&fit=crop', 'route', '/category?keyword=肠胃用药', '#E8F5E9', 3, 1, 'recommend', 3, 'home_page_001', 'v1.0.0'),
(104, '皮肤用药', 'https://images.unsplash.com/photo-1512069772995-ec65d4e3ade8?w=120&h=120&fit=crop', 'route', '/category?keyword=皮肤用药', '#FFF3E0', 4, 1, 'recommend', 3, 'home_page_001', 'v1.0.0'),
(105, '心脑血管', 'https://images.unsplash.com/photo-1628348068343-b5daab358737?w=120&h=120&fit=crop', 'route', '/category?keyword=心脑血管', '#FCE4EC', 5, 1, 'recommend', 3, 'home_page_001', 'v1.0.0'),
(106, '维生素钙', 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=120&h=120&fit=crop', 'route', '/category?keyword=维生素钙', '#EFEBE9', 6, 1, 'recommend', 3, 'home_page_001', 'v1.0.0'),
(107, '五官用药', 'https://images.unsplash.com/photo-1588776814546-1ffcf47267a5?w=120&h=120&fit=crop', 'route', '/category?keyword=五官用药', '#E0F7FA', 7, 1, 'recommend', 3, 'home_page_001', 'v1.0.0'),
(108, '慢病用药', 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=120&h=120&fit=crop', 'route', '/category?keyword=慢病用药', '#F5F5F5', 8, 1, 'recommend', 3, 'home_page_001', 'v1.0.0'),
(109, '医疗器械', 'https://images.unsplash.com/photo-1583947215259-38e31be8751f?w=120&h=120&fit=crop', 'route', '/category?keyword=医疗器械', '#E8EAF6', 9, 1, 'recommend', 3, 'home_page_001', 'v1.0.0'),
(110, '滋补养生', 'https://images.unsplash.com/photo-1564510714716-3bf387e05561?w=120&h=120&fit=crop', 'route', '/category?keyword=滋补养生', '#FFF8E1', 10, 1, 'recommend', 3, 'home_page_001', 'v1.0.0');


-- 20.2 补充药品分类数据（用于金刚位分类）
INSERT IGNORE INTO `dm_category` (`id`, `name`, `code`, `parent_id`, `level`, `icon`, `type`, `sort`, `status`) VALUES
(41, '感冒发烧', 'cold-fever', 1, 2, '', 'drug', 7, 1),
(42, '咳嗽化痰', 'cough-phlegm', 1, 2, '', 'drug', 8, 1),
(43, '肠胃用药', 'gastro-drug', 1, 2, '', 'drug', 9, 1),
(44, '皮肤用药', 'skin-drug', 1, 2, '', 'drug', 10, 1),
(45, '心脑血管', 'cardio-cerebral', 1, 2, '', 'drug', 11, 1),
(46, '维生素钙', 'vit-calcium', 3, 2, '', 'drug', 3, 1),
(47, '五官用药', 'ent-drug', 1, 2, '', 'drug', 12, 1),
(48, '慢病用药', 'chronic-drug', 1, 2, '', 'drug', 13, 1),
(49, '医疗器械', 'med-device', 2, 2, '', 'drug', 3, 1),
(50, '滋补养生', 'tonic-health', 3, 2, '', 'drug', 4, 1);


-- 20.3 扩充商品数据 - 每个分类至少10条（包含处方药）

-- 分类41: 感冒发烧（10条，处方药）
INSERT INTO `dm_product` (`id`, `product_code`, `product_name`, `generic_name`, `category_id`, `brand_id`, `main_image`, `specification`, `unit`, `manufacturer`, `brand`, `approval_number`, `price`, `original_price`, `stock`, `sales`, `is_rx`, `is_national_essential`, `insurance_category`, `usage`, `disease`, `storage`, `validity`, `description`, `status`) VALUES
(101, 'YP20244101', '感康复方氨酚烷胺片', '复方氨酚烷胺', 41, 1, 'https://images.unsplash.com/photo-1584515933487-779824d29309?w=300&h=300&fit=crop', '12片/盒', '盒', '吉林吴太感康药业', '感康', '国药准字H22026593', 16.50, 22.00, 500, 1280, 0, 1, '乙类', '口服，一次1片，一日2次', '用于缓解普通感冒症状', '密封保存', '24个月', '用于缓解普通感冒症状', 1),
(102, 'YP20244102', '白加黑感冒片', '氨酚伪麻美芬片', 41, 3, 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=300&h=300&fit=crop', '20片/盒', '盒', '东盛科技', '白加黑', '国药准字H10960332', 22.80, 30.00, 400, 956, 0, 1, '甲类', '日片2片，夜片1-2片', '用于感冒引起的发热头痛', '密封保存', '24个月', '用于感冒引起的发热头痛', 1),
(103, 'YP20244103', '泰诺林对乙酰氨基酚缓释片', '对乙酰氨基酚', 41, 5, 'https://images.unsplash.com/photo-1559757175-5700dde675bc?w=300&h=300&fit=crop', '18片/盒', '盒', '上海强生制药', '泰诺林', '国药准字H20010115', 18.90, 25.00, 600, 2100, 0, 1, '甲类', '口服，一次2片，每8小时1次', '用于感冒或流感引起的发热', '密封保存', '36个月', '用于感冒或流感引起的发热', 1),
(104, 'YP20244104', '快克复方氨酚烷胺胶囊', '复方氨酚烷胺', 41, 1, 'https://images.unsplash.com/photo-1512069772995-ec65d4e3ade8?w=300&h=300&fit=crop', '10粒/盒', '盒', '浙江康恩贝制药', '快克', '国药准字H33021956', 12.80, 18.00, 700, 1560, 0, 0, '乙类', '口服，一次1粒，一日2次', '用于缓解感冒症状', '密封保存', '24个月', '用于缓解感冒症状', 1),
(105, 'YP20244105', '连花清瘟胶囊', '连花清瘟', 41, 4, 'https://images.unsplash.com/photo-1628348068343-b5daab358737?w=300&h=300&fit=crop', '24粒/盒', '盒', '石家庄以岭药业', '以岭', '国药准字Z20040063', 14.80, 20.00, 800, 3200, 0, 0, '甲类', '口服，一次4粒，一日3次', '用于流行性感冒', '密封保存', '24个月', '用于流行性感冒', 1),
(106, 'YP20244106', '阿莫西林克拉维酸钾片', '阿莫西林克拉维酸钾', 41, 3, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=300&h=300&fit=crop', '12片/盒', '盒', '华北制药', '华北制药', '国药准字H20033270', 28.50, 38.00, 350, 890, 1, 0, '乙类', '口服，一次1片，每8小时1次', '用于细菌感染引起的感冒并发症', '密封保存', '24个月', '用于细菌感染引起的感冒并发症', 1),
(107, 'YP20244107', '头孢克肟分散片', '头孢克肟', 41, 3, 'https://images.unsplash.com/photo-1588776814546-1ffcf47267a5?w=300&h=300&fit=crop', '6片/盒', '盒', '广州白云山制药', '白云山', '国药准字H20040385', 32.00, 42.00, 300, 678, 1, 0, '乙类', '口服，一次1片，一日2次', '用于敏感菌引起的呼吸道感染', '密封保存', '24个月', '用于敏感菌引起的呼吸道感染', 1),
(108, 'YP20244108', '奥司他韦胶囊', '磷酸奥司他韦', 41, 5, 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=300&h=300&fit=crop', '10粒/盒', '盒', '东阳光药', '可威', '国药准字H20080763', 59.00, 78.00, 200, 1890, 1, 0, '乙类', '口服，一次75mg，一日2次', '用于甲型和乙型流感治疗', '密封保存', '36个月', '用于甲型和乙型流感治疗', 1),
(109, 'YP20244109', '999感冒灵颗粒', '三叉苦、金盏银盘', 41, 1, 'https://images.unsplash.com/photo-1583947215259-38e31be8751f?w=300&h=300&fit=crop', '10袋/盒', '盒', '华润三九医药', '999', '国药准字Z44021940', 11.50, 16.00, 900, 2560, 0, 0, '乙类', '开水冲服，一次1袋，一日3次', '用于感冒引起的头痛发热', '密封保存', '24个月', '用于感冒引起的头痛发热', 1),
(110, 'YP20244110', '复方锌布洛芬颗粒', '复方锌布洛芬', 41, 1, 'https://images.unsplash.com/photo-1564510714716-3bf387e05561?w=300&h=300&fit=crop', '12袋/盒', '盒', '山东鲁南制药', '鲁南', '国药准字H20057852', 25.80, 35.00, 400, 456, 0, 0, '乙类', '开水冲服，一次1袋，一日3次', '用于儿童感冒发热', '密封保存', '24个月', '用于儿童感冒发热', 1)
;

-- 分类42: 咳嗽化痰（10条，处方药）
INSERT INTO `dm_product` (`id`, `product_code`, `product_name`, `generic_name`, `category_id`, `brand_id`, `main_image`, `specification`, `unit`, `manufacturer`, `brand`, `approval_number`, `price`, `original_price`, `stock`, `sales`, `is_rx`, `is_national_essential`, `insurance_category`, `usage`, `disease`, `storage`, `validity`, `description`, `status`) VALUES
(201, 'YP20244201', '京都念慈菴蜜炼川贝枇杷膏', '蜜炼川贝枇杷膏', 42, 4, 'https://images.unsplash.com/photo-1584515933487-779824d29309?w=300&h=300&fit=crop', '300ml/瓶', '瓶', '京都念慈菴总厂', '念慈菴', 'ZC20160001', 42.00, 55.00, 500, 3200, 0, 0, '乙类', '口服，一次15ml，一日3次', '润肺化痰、止咳平喘', '密封保存', '36个月', '润肺化痰、止咳平喘', 1),
(202, 'YP20244202', '急支糖浆', '急支糖浆', 42, 1, 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=300&h=300&fit=crop', '180ml/瓶', '瓶', '太极集团', '太极', '国药准字Z50020534', 18.80, 25.00, 400, 1890, 0, 0, '乙类', '口服，一次20-30ml，一日3-4次', '用于急性支气管炎、感冒后咳嗽', '密封保存', '24个月', '用于急性支气管炎、感冒后咳嗽', 1),
(203, 'YP20244203', '盐酸氨溴索口服溶液', '盐酸氨溴索', 42, 5, 'https://images.unsplash.com/photo-1559757175-5700dde675bc?w=300&h=300&fit=crop', '100ml/瓶', '瓶', '上海勃林格殷格翰', '沐舒坦', '国药准字J20060012', 28.50, 38.00, 350, 2100, 0, 1, '甲类', '口服，一次10ml，一日3次', '用于痰液粘稠不易咳出者', '密封保存', '24个月', '用于痰液粘稠不易咳出者', 1),
(204, 'YP20244204', '氢溴酸右美沙芬口服溶液', '氢溴酸右美沙芬', 42, 5, 'https://images.unsplash.com/photo-1512069772995-ec65d4e3ade8?w=300&h=300&fit=crop', '120ml/瓶', '瓶', '上海强生制药', '美可芬', '国药准字H20010116', 22.00, 30.00, 400, 1560, 0, 1, '甲类', '口服，一次10-20ml，一日3-4次', '用于干咳', '密封保存', '24个月', '用于干咳', 1),
(205, 'YP20244205', '蛇胆川贝液', '蛇胆川贝液', 42, 4, 'https://images.unsplash.com/photo-1628348068343-b5daab358737?w=300&h=300&fit=crop', '10ml*6支', '盒', '广州潘高寿药业', '潘高寿', '国药准字Z44020476', 15.80, 22.00, 600, 890, 0, 0, '乙类', '口服，一次10ml，一日2次', '用于肺热咳嗽', '密封保存', '24个月', '用于肺热咳嗽', 1),
(206, 'YP20244206', '阿奇霉素分散片', '阿奇霉素', 42, 3, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=300&h=300&fit=crop', '6片/盒', '盒', '辉瑞制药', '辉瑞', '国药准字H10960167', 35.00, 48.00, 300, 678, 1, 0, '乙类', '口服，一次0.5g，一日1次', '用于敏感菌引起的呼吸道感染', '密封保存', '24个月', '用于敏感菌引起的呼吸道感染', 1),
(207, 'YP20244207', '左氧氟沙星片', '左氧氟沙星', 42, 3, 'https://images.unsplash.com/photo-1588776814546-1ffcf47267a5?w=300&h=300&fit=crop', '0.5g*6片', '盒', '扬子江药业', '扬子江', '国药准字H20040128', 28.00, 38.00, 250, 456, 1, 0, '乙类', '口服，一次0.5g，一日1次', '用于支气管炎、肺炎等呼吸道感染', '密封保存', '24个月', '用于支气管炎、肺炎等呼吸道感染', 1),
(208, 'YP20244208', '川贝清肺糖浆', '川贝清肺糖浆', 42, 4, 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=300&h=300&fit=crop', '120ml/瓶', '瓶', '武汉健民药业', '健民', '国药准字Z42020404', 16.50, 22.00, 500, 1230, 0, 1, '甲类', '口服，一次15-30ml，一日3次', '用于干咳、咽干', '密封保存', '24个月', '用于干咳、咽干', 1),
(209, 'YP20244209', '复方甘草口服溶液', '复方甘草', 42, 3, 'https://images.unsplash.com/photo-1583947215259-38e31be8751f?w=300&h=300&fit=crop', '100ml/瓶', '瓶', '华北制药', '华北制药', '国药准字H13021480', 8.80, 12.00, 700, 2890, 0, 0, '乙类', '口服，一次5-10ml，一日3次', '用于上呼吸道感染引起的咳嗽', '密封保存', '24个月', '用于上呼吸道感染引起的咳嗽', 1),
(210, 'YP20244210', '克咳胶囊', '克咳胶囊', 42, 1, 'https://images.unsplash.com/photo-1564510714716-3bf387e05561?w=300&h=300&fit=crop', '12粒/盒', '盒', '贵州百灵制药', '百灵', '国药准字Z20025606', 19.80, 28.00, 400, 567, 0, 0, '乙类', '口服，一次2粒，一日3次', '用于咳嗽、气喘', '密封保存', '24个月', '用于咳嗽、气喘', 1)
;

-- 分类43: 肠胃用药（10条，处方药）
INSERT INTO `dm_product` (`id`, `product_code`, `product_name`, `generic_name`, `category_id`, `brand_id`, `main_image`, `specification`, `unit`, `manufacturer`, `brand`, `approval_number`, `price`, `original_price`, `stock`, `sales`, `is_rx`, `is_national_essential`, `insurance_category`, `usage`, `disease`, `storage`, `validity`, `description`, `status`) VALUES
(301, 'YP20244301', '吗丁啉多潘立酮片', '多潘立酮', 43, 1, 'https://images.unsplash.com/photo-1584515933487-779824d29309?w=300&h=300&fit=crop', '30片/盒', '盒', '西安杨森制药', '吗丁啉', '国药准字H10910003', 18.50, 25.00, 500, 2340, 0, 0, '乙类', '口服，一次1片，一日3次', '用于消化不良、腹胀、嗳气', '密封保存', '24个月', '用于消化不良、腹胀、嗳气', 1),
(302, 'YP20244302', '整肠生地衣芽孢杆菌活菌胶囊', '地衣芽孢杆菌', 43, 1, 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=300&h=300&fit=crop', '24粒/盒', '盒', '东北制药', '整肠生', '国药准字S10950015', 22.80, 30.00, 400, 1560, 0, 0, '乙类', '口服，一次2粒，一日3次', '用于肠道菌群失调引起的腹泻', '密封保存', '24个月', '用于肠道菌群失调引起的腹泻', 1),
(303, 'YP20244303', '雷贝拉唑钠肠溶片', '雷贝拉唑钠', 43, 4, 'https://images.unsplash.com/photo-1559757175-5700dde675bc?w=300&h=300&fit=crop', '10mg*7片', '盒', '珠海润都制药', '润都', '国药准字H20040916', 38.00, 52.00, 300, 890, 1, 0, '乙类', '口服，一次10mg，一日1次', '用于胃溃疡、十二指肠溃疡', '密封保存', '24个月', '用于胃溃疡、十二指肠溃疡', 1),
(304, 'YP20244304', '莫沙必利片', '枸橼酸莫沙必利', 43, 4, 'https://images.unsplash.com/photo-1512069772995-ec65d4e3ade8?w=300&h=300&fit=crop', '5mg*24片', '盒', '山东新时代药业', '新卫', '国药准字H20010123', 28.50, 38.00, 350, 678, 1, 0, '乙类', '口服，一次5mg，一日3次', '用于功能性消化不良', '密封保存', '24个月', '用于功能性消化不良', 1),
(305, 'YP20244305', '肠炎宁片', '肠炎宁', 43, 1, 'https://images.unsplash.com/photo-1628348068343-b5daab358737?w=300&h=300&fit=crop', '48片/盒', '盒', '江西康恩贝中药', '康恩贝', '国药准字Z20043724', 19.80, 28.00, 600, 1890, 0, 0, '乙类', '口服，一次4片，一日3次', '用于急慢性胃肠炎、腹泻', '密封保存', '24个月', '用于急慢性胃肠炎、腹泻', 1),
(306, 'YP20244306', '乳酸菌素片', '乳酸菌素', 43, 3, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=300&h=300&fit=crop', '64片/盒', '盒', '哈药集团制药六厂', '哈药', '国药准字H23021163', 12.50, 18.00, 800, 2560, 0, 1, '甲类', '嚼服，一次1.2g，一日3次', '用于肠内异常发酵、消化不良', '密封保存', '24个月', '用于肠内异常发酵、消化不良', 1),
(307, 'YP20244307', '诺氟沙星胶囊', '诺氟沙星', 43, 3, 'https://images.unsplash.com/photo-1588776814546-1ffcf47267a5?w=300&h=300&fit=crop', '0.1g*24粒', '盒', '华北制药', '华北制药', '国药准字H13020342', 8.80, 12.00, 500, 3200, 1, 0, '乙类', '口服，一次0.3g，一日2次', '用于敏感菌引起的肠道感染', '密封保存', '24个月', '用于敏感菌引起的肠道感染', 1),
(308, 'YP20244308', '胃苏颗粒', '胃苏颗粒', 43, 3, 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=300&h=300&fit=crop', '15g*9袋', '盒', '扬子江药业', '扬子江', '国药准字Z10930002', 25.80, 35.00, 400, 1230, 0, 0, '乙类', '开水冲服，一次1袋，一日3次', '用于胃脘胀痛、嗳气', '密封保存', '24个月', '用于胃脘胀痛、嗳气', 1),
(309, 'YP20244309', '健胃消食片', '健胃消食片', 43, 1, 'https://images.unsplash.com/photo-1583947215259-38e31be8751f?w=300&h=300&fit=crop', '36片/盒', '盒', '江中药业', '江中', '国药准字Z20013220', 15.80, 22.00, 900, 4560, 0, 0, '乙类', '嚼服，一次3片，一日3次', '用于脾胃虚弱所致的食积', '密封保存', '24个月', '用于脾胃虚弱所致的食积', 1),
(310, 'YP20244310', '多酶片', '多酶片', 43, 3, 'https://images.unsplash.com/photo-1564510714716-3bf387e05561?w=300&h=300&fit=crop', '100片/瓶', '盒', '上海信谊制药', '信谊', '国药准字H31020277', 8.50, 12.00, 700, 1890, 0, 0, '乙类', '口服，一次2-3片，一日3次', '用于消化不良、食欲缺乏', '密封保存', '24个月', '用于消化不良、食欲缺乏', 1)
;

-- 分类44: 皮肤用药（10条，处方药）
INSERT INTO `dm_product` (`id`, `product_code`, `product_name`, `generic_name`, `category_id`, `brand_id`, `main_image`, `specification`, `unit`, `manufacturer`, `brand`, `approval_number`, `price`, `original_price`, `stock`, `sales`, `is_rx`, `is_national_essential`, `insurance_category`, `usage`, `disease`, `storage`, `validity`, `description`, `status`) VALUES
(401, 'YP20244401', '达克宁硝酸咪康唑乳膏', '硝酸咪康唑', 44, 1, 'https://images.unsplash.com/photo-1584515933487-779824d29309?w=300&h=300&fit=crop', '15g/支', '盒', '西安杨森制药', '达克宁', '国药准字H10960003', 18.80, 25.00, 500, 2890, 0, 0, '乙类', '外用，涂于患处，一日2次', '用于真菌感染性皮肤病', '密封保存', '24个月', '用于真菌感染性皮肤病', 1),
(402, 'YP20244402', '皮炎平复方醋酸地塞米松乳膏', '复方醋酸地塞米松', 44, 1, 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=300&h=300&fit=crop', '20g/支', '盒', '华润三九医药', '999皮炎平', '国药准字H44021289', 12.50, 18.00, 600, 3200, 0, 0, '乙类', '外用，涂于患处，一日2-3次', '用于局限性瘙痒症、神经性皮炎', '密封保存', '24个月', '用于局限性瘙痒症、神经性皮炎', 1),
(403, 'YP20244403', '炉甘石洗剂', '炉甘石', 44, 3, 'https://images.unsplash.com/photo-1559757175-5700dde675bc?w=300&h=300&fit=crop', '100ml/瓶', '瓶', '上海运佳黄浦制药', '运佳', '国药准字H31021072', 6.80, 10.00, 800, 1890, 0, 0, '乙类', '外用，摇匀后涂于患处', '用于急性瘙痒性皮肤病', '密封保存', '24个月', '用于急性瘙痒性皮肤病', 1),
(404, 'YP20244404', '红霉素软膏', '红霉素', 44, 3, 'https://images.unsplash.com/photo-1512069772995-ec65d4e3ade8?w=300&h=300&fit=crop', '10g/支', '盒', '华北制药', '华北制药', '国药准字H13021334', 3.50, 5.00, 1000, 5670, 0, 0, '乙类', '外用，涂于患处，一日2次', '用于脓疱疮、毛囊炎等皮肤感染', '密封保存', '24个月', '用于脓疱疮、毛囊炎等皮肤感染', 1),
(405, 'YP20244405', '莫匹罗星软膏', '莫匹罗星', 44, 1, 'https://images.unsplash.com/photo-1628348068343-b5daab358737?w=300&h=300&fit=crop', '5g/支', '盒', '中美史克', '百多邦', '国药准字H10920014', 16.80, 22.00, 400, 2340, 0, 0, '乙类', '外用，涂于患处，一日3次', '用于细菌性皮肤感染', '密封保存', '24个月', '用于细菌性皮肤感染', 1),
(406, 'YP20244406', '他克莫司软膏', '他克莫司', 44, 4, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=300&h=300&fit=crop', '10g/支', '盒', '安斯泰来制药', '普特彼', '国药准字J20050020', 128.00, 168.00, 150, 567, 1, 0, '乙类', '外用，涂于患处，一日2次', '用于特应性皮炎', '密封保存', '24个月', '用于特应性皮炎', 1),
(407, 'YP20244407', '阿达帕林凝胶', '阿达帕林', 44, 1, 'https://images.unsplash.com/photo-1588776814546-1ffcf47267a5?w=300&h=300&fit=crop', '15g/支', '盒', '高德美制药', '达芙文', '国药准字H20010477', 45.00, 60.00, 200, 1560, 1, 0, '乙类', '外用，睡前涂于患处，一日1次', '用于痤疮（青春痘）', '密封保存', '24个月', '用于痤疮（青春痘）', 1),
(408, 'YP20244408', '尿素维E乳膏', '尿素维E', 44, 1, 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=300&h=300&fit=crop', '20g/支', '盒', '北京双吉制药', '双吉', '国药准字H11021406', 8.80, 12.00, 700, 2890, 0, 0, '乙类', '外用，涂于患处，一日2次', '用于皮肤干燥、皲裂', '密封保存', '24个月', '用于皮肤干燥、皲裂', 1),
(409, 'YP20244409', '复方酮康唑软膏', '复方酮康唑', 44, 1, 'https://images.unsplash.com/photo-1583947215259-38e31be8751f?w=300&h=300&fit=crop', '7g/支', '盒', '云南白药集团', '皮康王', '国药准字H53020260', 15.80, 22.00, 500, 1230, 0, 0, '乙类', '外用，涂于患处，一日2次', '用于体癣、股癣、手足癣', '密封保存', '24个月', '用于体癣、股癣、手足癣', 1),
(410, 'YP20244410', '氧化锌软膏', '氧化锌', 44, 3, 'https://images.unsplash.com/photo-1564510714716-3bf387e05561?w=300&h=300&fit=crop', '20g/支', '盒', '上海运佳黄浦制药', '运佳', '国药准字H31021071', 5.80, 8.00, 800, 3200, 0, 0, '乙类', '外用，涂于患处，一日2次', '用于急性皮炎、湿疹、痱子', '密封保存', '24个月', '用于急性皮炎、湿疹、痱子', 1)
;

-- 分类45: 心脑血管（10条，处方药）
INSERT INTO `dm_product` (`id`, `product_code`, `product_name`, `generic_name`, `category_id`, `brand_id`, `main_image`, `specification`, `unit`, `manufacturer`, `brand`, `approval_number`, `price`, `original_price`, `stock`, `sales`, `is_rx`, `is_national_essential`, `insurance_category`, `usage`, `disease`, `storage`, `validity`, `description`, `status`) VALUES
(501, 'YP20244501', '拜新同硝苯地平控释片', '硝苯地平', 45, 4, 'https://images.unsplash.com/photo-1584515933487-779824d29309?w=300&h=300&fit=crop', '30mg*7片', '盒', '拜耳医药', '拜新同', '国药准字J20171022', 35.00, 48.00, 300, 1890, 1, 0, '乙类', '口服，一次30mg，一日1次', '用于高血压、冠心病', '密封保存', '24个月', '用于高血压、冠心病', 1),
(502, 'YP20244502', '络活喜苯磺酸氨氯地平片', '苯磺酸氨氯地平', 45, 5, 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=300&h=300&fit=crop', '5mg*7片', '盒', '辉瑞制药', '络活喜', '国药准字H10950224', 38.00, 52.00, 250, 2340, 1, 0, '乙类', '口服，一次5mg，一日1次', '用于高血压、冠心病', '密封保存', '24个月', '用于高血压、冠心病', 1),
(503, 'YP20244503', '立普妥阿托伐他汀钙片', '阿托伐他汀钙', 45, 5, 'https://images.unsplash.com/photo-1559757175-5700dde675bc?w=300&h=300&fit=crop', '20mg*7片', '盒', '辉瑞制药', '立普妥', '国药准字J20171030', 52.00, 68.00, 200, 1560, 1, 0, '乙类', '口服，一次10-20mg，一日1次', '用于高胆固醇血症、冠心病', '密封保存', '24个月', '用于高胆固醇血症、冠心病', 1),
(504, 'YP20244504', '波立维硫酸氢氯吡格雷片', '硫酸氢氯吡格雷', 45, 5, 'https://images.unsplash.com/photo-1512069772995-ec65d4e3ade8?w=300&h=300&fit=crop', '75mg*7片', '盒', '赛诺菲制药', '波立维', '国药准字J20171088', 68.00, 88.00, 180, 890, 1, 0, '乙类', '口服，一次75mg，一日1次', '用于预防动脉粥样硬化血栓形成', '密封保存', '24个月', '用于预防动脉粥样硬化血栓形成', 1),
(505, 'YP20244505', '复方丹参滴丸', '复方丹参', 45, 4, 'https://images.unsplash.com/photo-1628348068343-b5daab358737?w=300&h=300&fit=crop', '180丸/瓶', '盒', '天津天士力制药', '天士力', '国药准字Z10950115', 28.80, 38.00, 500, 3200, 0, 0, '乙类', '舌下含服，一次10丸，一日3次', '用于冠心病心绞痛', '密封保存', '24个月', '用于冠心病心绞痛', 1),
(506, 'YP20244506', '速效救心丸', '速效救心丸', 45, 1, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=300&h=300&fit=crop', '40丸/瓶', '盒', '天津中新药业', '中新', '国药准字Z12020025', 22.00, 30.00, 600, 4560, 0, 0, '乙类', '舌下含服，一次4-6丸，一日3次', '用于冠心病心绞痛', '密封保存', '24个月', '用于冠心病心绞痛', 1),
(507, 'YP20244507', '倍他乐克酒石酸美托洛尔片', '酒石酸美托洛尔', 45, 1, 'https://images.unsplash.com/photo-1588776814546-1ffcf47267a5?w=300&h=300&fit=crop', '25mg*20片', '盒', '阿斯利康制药', '倍他乐克', '国药准字H32025391', 18.50, 25.00, 350, 1230, 1, 0, '乙类', '口服，一次25-50mg，一日2次', '用于高血压、心绞痛、心律失常', '密封保存', '24个月', '用于高血压、心绞痛、心律失常', 1),
(508, 'YP20244508', '缬沙坦胶囊', '缬沙坦', 45, 4, 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=300&h=300&fit=crop', '80mg*7粒', '盒', '诺华制药', '代文', '国药准字J20171063', 42.00, 55.00, 280, 890, 1, 0, '乙类', '口服，一次80mg，一日1次', '用于轻中度高血压', '密封保存', '24个月', '用于轻中度高血压', 1),
(509, 'YP20244509', '银杏叶片', '银杏叶提取物', 45, 1, 'https://images.unsplash.com/photo-1583947215259-38e31be8751f?w=300&h=300&fit=crop', '24片/盒', '盒', '贵州百灵制药', '百灵', '国药准字Z52020127', 19.80, 28.00, 500, 2560, 0, 0, '乙类', '口服，一次2片，一日3次', '用于冠心病、脑梗塞', '密封保存', '24个月', '用于冠心病、脑梗塞', 1),
(510, 'YP20244510', '阿司匹林肠溶片', '阿司匹林', 45, 1, 'https://images.unsplash.com/photo-1564510714716-3bf387e05561?w=300&h=300&fit=crop', '100mg*30片', '盒', '拜耳医药', '拜阿司匹灵', '国药准字J20171021', 15.80, 22.00, 700, 3890, 0, 0, '乙类', '口服，一次100mg，一日1次', '用于预防心血管事件', '密封保存', '24个月', '用于预防心血管事件', 1)
;

-- 分类46: 维生素钙（10条，处方药）
INSERT INTO `dm_product` (`id`, `product_code`, `product_name`, `generic_name`, `category_id`, `brand_id`, `main_image`, `specification`, `unit`, `manufacturer`, `brand`, `approval_number`, `price`, `original_price`, `stock`, `sales`, `is_rx`, `is_national_essential`, `insurance_category`, `usage`, `disease`, `storage`, `validity`, `description`, `status`) VALUES
(601, 'YP20244601', '善存多维元素片(29)', '多维元素片', 46, 5, 'https://images.unsplash.com/photo-1584515933487-779824d29309?w=300&h=300&fit=crop', '30片/瓶', '盒', '惠氏制药', '善存', '国药准字H10950026', 68.00, 88.00, 400, 2340, 0, 0, '乙类', '口服，一次1片，一日1次', '用于预防和治疗维生素与矿物质缺乏', '密封保存', '24个月', '用于预防和治疗维生素与矿物质缺乏', 1),
(602, 'YP20244602', '汤臣倍健维生素C片', '维生素C', 46, 1, 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=300&h=300&fit=crop', '90片/瓶', '盒', '汤臣倍健', '汤臣倍健', '国食健字G20120307', 45.00, 60.00, 500, 3200, 0, 0, '乙类', '嚼服，一次1片，一日1次', '用于补充维生素C', '密封保存', '24个月', '用于补充维生素C', 1),
(603, 'YP20244603', '钙尔奇碳酸钙D3片', '碳酸钙D3', 46, 5, 'https://images.unsplash.com/photo-1559757175-5700dde675bc?w=300&h=300&fit=crop', '60片/瓶', '盒', '惠氏制药', '钙尔奇', '国药准字H20030512', 58.00, 78.00, 350, 1890, 0, 0, '乙类', '口服，一次1片，一日1-2次', '用于预防和治疗钙缺乏症', '密封保存', '36个月', '用于预防和治疗钙缺乏症', 1),
(604, 'YP20244604', '21金维他多维元素片', '多维元素片', 46, 1, 'https://images.unsplash.com/photo-1512069772995-ec65d4e3ade8?w=300&h=300&fit=crop', '100片/瓶', '盒', '杭州民生药业', '21金维他', '国药准字H20003218', 38.00, 50.00, 600, 1560, 0, 1, '甲类', '口服，一次2片，一日1次', '用于预防和治疗多种维生素缺乏', '密封保存', '24个月', '用于预防和治疗多种维生素缺乏', 1),
(605, 'YP20244605', '葡萄糖酸钙锌口服溶液', '葡萄糖酸钙锌', 46, 3, 'https://images.unsplash.com/photo-1628348068343-b5daab358737?w=300&h=300&fit=crop', '10ml*12支', '盒', '哈药集团三精制药', '三精', '国药准字H23020598', 25.80, 35.00, 500, 2890, 0, 1, '甲类', '口服，一次5-10ml，一日2-3次', '用于补钙补锌', '密封保存', '24个月', '用于补钙补锌', 1),
(606, 'YP20244606', '养生堂天然维生素E', '天然维生素E', 46, 1, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=300&h=300&fit=crop', '160粒/瓶', '盒', '养生堂药业', '养生堂', '国食健字G20040309', 88.00, 118.00, 300, 1230, 0, 0, '乙类', '口服，一次1粒，一日1-2次', '用于补充维生素E', '密封保存', '24个月', '用于补充维生素E', 1),
(607, 'YP20244607', 'Swisse复合维生素B族', '复合维生素B', 46, 1, 'https://images.unsplash.com/photo-1588776814546-1ffcf47267a5?w=300&h=300&fit=crop', '125片/瓶', '盒', 'Swisse', 'Swisse', 'AUST L 206695', 98.00, 138.00, 250, 890, 0, 0, '乙类', '口服，一次1片，一日1次', '用于补充B族维生素', '密封保存', '36个月', '用于补充B族维生素', 1),
(608, 'YP20244608', '星鲨维生素D滴剂', '维生素D', 46, 1, 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=300&h=300&fit=crop', '400IU*24粒', '盒', '厦门星鲨药业', '星鲨', '国药准字H20056062', 28.00, 38.00, 400, 2560, 0, 1, '甲类', '口服，一次1-2粒，一日1次', '用于预防和治疗维生素D缺乏', '密封保存', '24个月', '用于预防和治疗维生素D缺乏', 1),
(609, 'YP20244609', '黄金搭档多种维生素矿物质片', '多种维生素矿物质', 46, 1, 'https://images.unsplash.com/photo-1583947215259-38e31be8751f?w=300&h=300&fit=crop', '60片/瓶', '盒', '上海黄金搭档', '黄金搭档', '国食健字G20050430', 48.00, 65.00, 450, 1890, 0, 0, '乙类', '口服，一次1片，一日1次', '用于补充多种维生素矿物质', '密封保存', '24个月', '用于补充多种维生素矿物质', 1),
(610, 'YP20244610', '迪巧小儿碳酸钙D3颗粒', '碳酸钙D3', 46, 5, 'https://images.unsplash.com/photo-1564510714716-3bf387e05561?w=300&h=300&fit=crop', '0.75g*20袋', '盒', '安盛制药', '迪巧', '国药准字J20171066', 42.00, 55.00, 350, 3200, 0, 0, '乙类', '口服，一次1袋，一日1次', '用于儿童补钙', '密封保存', '24个月', '用于儿童补钙', 1)
;

-- 分类47: 五官用药（10条，处方药）
INSERT INTO `dm_product` (`id`, `product_code`, `product_name`, `generic_name`, `category_id`, `brand_id`, `main_image`, `specification`, `unit`, `manufacturer`, `brand`, `approval_number`, `price`, `original_price`, `stock`, `sales`, `is_rx`, `is_national_essential`, `insurance_category`, `usage`, `disease`, `storage`, `validity`, `description`, `status`) VALUES
(701, 'YP20244701', '珍视明四味珍层冰硼滴眼液', '四味珍层冰硼', 47, 1, 'https://images.unsplash.com/photo-1584515933487-779824d29309?w=300&h=300&fit=crop', '8ml/支', '盒', '江西珍视明药业', '珍视明', '国药准字Z20053539', 16.80, 22.00, 500, 2890, 0, 0, '乙类', '滴眼，一次1-2滴，一日3-5次', '用于眼疲劳、眼干涩', '密封保存', '24个月', '用于眼疲劳、眼干涩', 1),
(702, 'YP20244702', '左氧氟沙星滴眼液', '左氧氟沙星', 47, 1, 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=300&h=300&fit=crop', '5ml/支', '盒', '参天制药', '可乐必妥', '国药准字H20030417', 28.00, 38.00, 300, 1560, 1, 0, '乙类', '滴眼，一次1滴，一日3次', '用于细菌性结膜炎、角膜炎', '密封保存', '24个月', '用于细菌性结膜炎、角膜炎', 1),
(703, 'YP20244703', '布地奈德鼻喷雾剂', '布地奈德', 47, 1, 'https://images.unsplash.com/photo-1559757175-5700dde675bc?w=300&h=300&fit=crop', '64揿/支', '盒', '阿斯利康制药', '雷诺考特', '国药准字H20010139', 49.00, 68.00, 200, 1230, 1, 0, '乙类', '喷鼻，一次每侧鼻孔1-2喷，一日2次', '用于过敏性鼻炎', '密封保存', '24个月', '用于过敏性鼻炎', 1),
(704, 'YP20244704', '西瓜霜含片', '西瓜霜', 47, 5, 'https://images.unsplash.com/photo-1512069772995-ec65d4e3ade8?w=300&h=300&fit=crop', '12片/盒', '盒', '桂林三金药业', '三金', '国药准字Z45020216', 12.80, 18.00, 600, 3200, 0, 0, '乙类', '含服，一次2片，一日5次', '用于咽喉肿痛、口腔溃疡', '密封保存', '24个月', '用于咽喉肿痛、口腔溃疡', 1),
(705, 'YP20244705', '金嗓子喉片', '金嗓子喉片', 47, 1, 'https://images.unsplash.com/photo-1628348068343-b5daab358737?w=300&h=300&fit=crop', '12片/盒', '盒', '广西金嗓子制药', '金嗓子', '国药准字B20010063', 8.80, 12.00, 800, 5670, 0, 0, '乙类', '含服，一次1-2片，一日数次', '用于咽喉不适、声音嘶哑', '密封保存', '24个月', '用于咽喉不适、声音嘶哑', 1),
(706, 'YP20244706', '复方门冬维甘滴眼液', '复方门冬维甘', 47, 3, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=300&h=300&fit=crop', '13ml/支', '盒', '日本参天制药', '参天', 'H20090826', 22.00, 30.00, 400, 1890, 0, 0, '乙类', '滴眼，一次1-2滴，一日4-6次', '用于眼疲劳、结膜充血', '密封保存', '24个月', '用于眼疲劳、结膜充血', 1),
(707, 'YP20244707', '氧氟沙星滴耳液', '氧氟沙星', 47, 3, 'https://images.unsplash.com/photo-1588776814546-1ffcf47267a5?w=300&h=300&fit=crop', '5ml/支', '盒', '上海运佳黄浦制药', '运佳', '国药准字H31021303', 12.50, 18.00, 350, 890, 1, 0, '乙类', '滴耳，一次6-10滴，一日2-3次', '用于中耳炎、外耳道炎', '密封保存', '24个月', '用于中耳炎、外耳道炎', 1),
(708, 'YP20244708', '红霉素眼膏', '红霉素', 47, 3, 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=300&h=300&fit=crop', '2g/支', '盒', '华北制药', '华北制药', '国药准字H13021336', 3.80, 5.00, 1000, 3200, 0, 0, '乙类', '涂于眼睑内，一日2-3次', '用于沙眼、结膜炎、睑缘炎', '密封保存', '24个月', '用于沙眼、结膜炎、睑缘炎', 1),
(709, 'YP20244709', '生理性海水鼻腔喷雾', '生理性海水', 47, 1, 'https://images.unsplash.com/photo-1583947215259-38e31be8751f?w=300&h=300&fit=crop', '30ml/支', '盒', '南京海昌生物', '海氏海诺', '鲁械注准20182140065', 28.00, 38.00, 450, 1560, 0, 0, '乙类', '喷鼻，每侧鼻孔2-3喷，一日2-3次', '用于鼻腔清洁、鼻炎', '密封保存', '24个月', '用于鼻腔清洁、鼻炎', 1),
(710, 'YP20244710', '润洁复方硫酸软骨素滴眼液', '复方硫酸软骨素', 47, 1, 'https://images.unsplash.com/photo-1564510714716-3bf387e05561?w=300&h=300&fit=crop', '8ml/支', '盒', '山东博士伦福瑞达', '润洁', '国药准字H20020045', 19.80, 28.00, 500, 2340, 0, 0, '乙类', '滴眼，一次1-2滴，一日3-5次', '用于眼疲劳、干眼症', '密封保存', '24个月', '用于眼疲劳、干眼症', 1)
;

-- 分类48: 慢病用药（10条，处方药）
INSERT INTO `dm_product` (`id`, `product_code`, `product_name`, `generic_name`, `category_id`, `brand_id`, `main_image`, `specification`, `unit`, `manufacturer`, `brand`, `approval_number`, `price`, `original_price`, `stock`, `sales`, `is_rx`, `is_national_essential`, `insurance_category`, `usage`, `disease`, `storage`, `validity`, `description`, `status`) VALUES
(801, 'YP20244801', '格华止盐酸二甲双胍片', '盐酸二甲双胍', 48, 1, 'https://images.unsplash.com/photo-1584515933487-779824d29309?w=300&h=300&fit=crop', '0.5g*20片', '盒', '中美上海施贵宝', '格华止', '国药准字H20023371', 22.00, 30.00, 400, 2340, 1, 0, '乙类', '口服，一次0.5g，一日2-3次', '用于2型糖尿病', '密封保存', '24个月', '用于2型糖尿病', 1),
(802, 'YP20244802', '拜糖平阿卡波糖片', '阿卡波糖', 48, 4, 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=300&h=300&fit=crop', '50mg*30片', '盒', '拜耳医药', '拜糖平', '国药准字H19990205', 58.00, 78.00, 250, 1560, 1, 0, '乙类', '口服，一次50mg，一日3次', '用于2型糖尿病', '密封保存', '24个月', '用于2型糖尿病', 1),
(803, 'YP20244803', '优甲乐左甲状腺素钠片', '左甲状腺素钠', 48, 1, 'https://images.unsplash.com/photo-1559757175-5700dde675bc?w=300&h=300&fit=crop', '50μg*100片', '盒', '德国默克制药', '优甲乐', '国药准字J20171073', 42.00, 58.00, 200, 890, 1, 0, '乙类', '口服，一次25-100μg，一日1次', '用于甲状腺功能减退', '密封保存', '24个月', '用于甲状腺功能减退', 1),
(804, 'YP20244804', '别嘌醇片', '别嘌醇', 48, 3, 'https://images.unsplash.com/photo-1512069772995-ec65d4e3ade8?w=300&h=300&fit=crop', '100mg*100片', '盒', '上海信谊制药', '信谊', '国药准字H31020567', 12.00, 18.00, 500, 1230, 1, 0, '乙类', '口服，一次100mg，一日2-3次', '用于高尿酸血症、痛风', '密封保存', '24个月', '用于高尿酸血症、痛风', 1),
(805, 'YP20244805', '非布司他片', '非布司他', 48, 1, 'https://images.unsplash.com/photo-1628348068343-b5daab358737?w=300&h=300&fit=crop', '40mg*10片', '盒', '江苏万邦制药', '万邦', '国药准字H20130042', 68.00, 88.00, 150, 567, 1, 0, '乙类', '口服，一次40mg，一日1次', '用于痛风患者高尿酸血症', '密封保存', '24个月', '用于痛风患者高尿酸血症', 1),
(806, 'YP20244806', '消渴丸', '消渴丸', 48, 1, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=300&h=300&fit=crop', '300丸/瓶', '盒', '广州中一药业', '中一', '国药准字Z44020049', 28.00, 38.00, 400, 1890, 0, 0, '乙类', '口服，一次5-10丸，一日2-3次', '用于气阴两虚型糖尿病', '密封保存', '24个月', '用于气阴两虚型糖尿病', 1),
(807, 'YP20244807', '格列齐特缓释片', '格列齐特', 48, 4, 'https://images.unsplash.com/photo-1588776814546-1ffcf47267a5?w=300&h=300&fit=crop', '30mg*30片', '盒', '施维雅制药', '达美康', '国药准字H20052897', 35.00, 48.00, 250, 678, 1, 0, '乙类', '口服，一次30-60mg，一日1次', '用于2型糖尿病', '密封保存', '24个月', '用于2型糖尿病', 1),
(808, 'YP20244808', '恩替卡韦分散片', '恩替卡韦', 48, 4, 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=300&h=300&fit=crop', '0.5mg*14片', '盒', '中美上海施贵宝', '博路定', '国药准字H20052229', 128.00, 168.00, 100, 345, 1, 0, '乙类', '口服，一次0.5mg，一日1次', '用于慢性乙型肝炎', '密封保存', '24个月', '用于慢性乙型肝炎', 1),
(809, 'YP20244809', '护肝片', '护肝片', 48, 1, 'https://images.unsplash.com/photo-1583947215259-38e31be8751f?w=300&h=300&fit=crop', '100片/瓶', '盒', '葵花药业', '葵花', '国药准字Z23021118', 18.80, 25.00, 600, 2560, 0, 0, '乙类', '口服，一次4片，一日3次', '用于慢性肝炎、早期肝硬化', '密封保存', '24个月', '用于慢性肝炎、早期肝硬化', 1),
(810, 'YP20244810', '六味地黄丸', '六味地黄丸', 48, 1, 'https://images.unsplash.com/photo-1564510714716-3bf387e05561?w=300&h=300&fit=crop', '200丸/瓶', '盒', '北京同仁堂', '同仁堂', '国药准字Z11020128', 22.00, 30.00, 700, 3200, 0, 1, '乙类', '口服，一次8丸，一日3次', '用于肾阴亏损、头晕耳鸣', '密封保存', '60个月', '用于肾阴亏损、头晕耳鸣', 1)
;

-- 分类49: 医疗器械（10条，处方药）
INSERT INTO `dm_product` (`id`, `product_code`, `product_name`, `generic_name`, `category_id`, `brand_id`, `main_image`, `specification`, `unit`, `manufacturer`, `brand`, `approval_number`, `price`, `original_price`, `stock`, `sales`, `is_rx`, `is_national_essential`, `insurance_category`, `usage`, `disease`, `storage`, `validity`, `description`, `status`) VALUES
(901, 'YP20244901', '欧姆龙上臂式血压计HEM-7136', NULL, 49, 1, 'https://images.unsplash.com/photo-1584515933487-779824d29309?w=300&h=300&fit=crop', '标准版', '台', '欧姆龙健康医疗', '欧姆龙', '湘械注准20172200018', 299.00, 399.00, 150, 1890, 0, 0, '乙类', '', '日常血压监测', '密封保存', '24个月', '日常血压监测', 1),
(902, 'YP20244902', '鱼跃电子血压计YE680A', NULL, 49, 1, 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=300&h=300&fit=crop', '语音版', '台', '鱼跃医疗', '鱼跃', '苏械注准20172200521', 199.00, 268.00, 200, 2340, 0, 0, '乙类', '', '日常血压监测', '密封保存', '24个月', '日常血压监测', 1),
(903, 'YP20244903', '罗氏血糖仪卓越金采', NULL, 49, 1, 'https://images.unsplash.com/photo-1559757175-5700dde675bc?w=300&h=300&fit=crop', '主机+25试纸', '台', '罗氏诊断', '罗氏', '国械注准20152200618', 358.00, 458.00, 100, 1560, 0, 0, '乙类', '', '血糖监测', '密封保存', '24个月', '血糖监测', 1),
(904, 'YP20244904', '三诺安稳血糖仪', NULL, 49, 1, 'https://images.unsplash.com/photo-1512069772995-ec65d4e3ade8?w=300&h=300&fit=crop', '主机+50试纸', '台', '三诺生物', '三诺', '湘械注准20142200093', 88.00, 128.00, 300, 3200, 0, 0, '乙类', '', '血糖监测', '密封保存', '24个月', '血糖监测', 1),
(905, 'YP20244905', '可孚红外体温计', NULL, 49, 1, 'https://images.unsplash.com/photo-1628348068343-b5daab358737?w=300&h=300&fit=crop', '标准版', '台', '可孚医疗', '可孚', '湘械注准20162200256', 39.90, 59.90, 500, 5670, 0, 0, '乙类', '', '体温测量', '密封保存', '24个月', '体温测量', 1),
(906, 'YP20244906', '稳健医用外科口罩', NULL, 49, 1, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=300&h=300&fit=crop', '50只/盒', '台', '稳健医疗', '稳健', '鄂械注准20152640192', 29.90, 39.90, 1000, 8900, 0, 0, '乙类', '', '日常防护', '密封保存', '24个月', '日常防护', 1),
(907, 'YP20244907', '云南白药创可贴', NULL, 49, 1, 'https://images.unsplash.com/photo-1588776814546-1ffcf47267a5?w=300&h=300&fit=crop', '100片/盒', '盒', '云南白药集团', '云南白药', '滇械注准20162640013', 18.80, 25.00, 800, 4560, 0, 0, '乙类', '', '外伤护理', '密封保存', '24个月', '外伤护理', 1),
(908, 'YP20244908', '海氏海诺碘伏棉签', NULL, 49, 1, 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=300&h=300&fit=crop', '50支/盒', '盒', '青岛海诺生物', '海氏海诺', '鲁械注准20162640386', 9.90, 15.00, 900, 3200, 0, 0, '乙类', '', '伤口消毒', '密封保存', '24个月', '伤口消毒', 1),
(909, 'YP20244909', '鱼跃制氧机7F-3W', NULL, 49, 1, 'https://images.unsplash.com/photo-1583947215259-38e31be8751f?w=300&h=300&fit=crop', '3升/分钟', '台', '鱼跃医疗', '鱼跃', '苏械注准20152200258', 1980.00, 2580.00, 50, 456, 0, 0, '乙类', '', '家庭氧疗', '密封保存', '24个月', '家庭氧疗', 1),
(910, 'YP20244910', '九安电子体温计', NULL, 49, 1, 'https://images.unsplash.com/photo-1564510714716-3bf387e05561?w=300&h=300&fit=crop', '标准版', '台', '天津九安医疗', '九安', '津械注准20152200152', 25.00, 35.00, 600, 2890, 0, 0, '乙类', '', '体温测量', '密封保存', '24个月', '体温测量', 1)
;

-- 分类50: 滋补养生（10条，处方药）
INSERT INTO `dm_product` (`id`, `product_code`, `product_name`, `generic_name`, `category_id`, `brand_id`, `main_image`, `specification`, `unit`, `manufacturer`, `brand`, `approval_number`, `price`, `original_price`, `stock`, `sales`, `is_rx`, `is_national_essential`, `insurance_category`, `usage`, `disease`, `storage`, `validity`, `description`, `status`) VALUES
(1001, 'YP20245001', '东阿阿胶块', NULL, 50, 1, 'https://images.unsplash.com/photo-1584515933487-779824d29309?w=300&h=300&fit=crop', '250g/盒', '瓶', '东阿阿胶股份', '东阿阿胶', '国药准字Z37021368', 399.00, 498.00, 100, 2890, 0, 0, '乙类', '烊化兑服，一次3-9g', '用于补血滋阴、润燥', '密封保存', '60个月', '用于补血滋阴、润燥', 1),
(1002, 'YP20245002', '北京同仁堂西洋参片', NULL, 50, 1, 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=300&h=300&fit=crop', '30g/罐', '瓶', '北京同仁堂', '同仁堂', '国食健字G20040312', 88.00, 128.00, 200, 1560, 0, 1, '乙类', '含服或泡水，一次2-3片', '用于补气养阴、清热生津', '密封保存', '60个月', '用于补气养阴、清热生津', 1),
(1003, 'YP20245003', 'Swisse深海鱼油软胶囊', NULL, 50, 1, 'https://images.unsplash.com/photo-1559757175-5700dde675bc?w=300&h=300&fit=crop', '400粒/瓶', '盒', 'Swisse', 'Swisse', 'AUST L 148369', 128.00, 168.00, 250, 2340, 0, 0, '乙类', '口服，一次1-2粒，一日1-2次', '辅助降血脂', '密封保存', '36个月', '辅助降血脂', 1),
(1004, 'YP20245004', '汤臣倍健蛋白粉', NULL, 50, 1, 'https://images.unsplash.com/photo-1512069772995-ec65d4e3ade8?w=300&h=300&fit=crop', '450g/罐', '瓶', '汤臣倍健', '汤臣倍健', '国食健字G20120308', 298.00, 398.00, 150, 1890, 0, 0, '乙类', '每日1-2次，每次1勺冲服', '补充蛋白质', '密封保存', '24个月', '补充蛋白质', 1),
(1005, 'YP20245005', '同仁堂破壁灵芝孢子粉', NULL, 50, 1, 'https://images.unsplash.com/photo-1628348068343-b5daab358737?w=300&h=300&fit=crop', '1g*60袋', '盒', '北京同仁堂', '同仁堂', '国食健字G20050482', 198.00, 268.00, 120, 1230, 0, 1, '乙类', '口服，一次1袋，一日2次', '增强免疫力', '密封保存', '60个月', '增强免疫力', 1),
(1006, 'YP20245006', '康恩贝铁皮石斛枫斗', NULL, 50, 1, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=300&h=300&fit=crop', '50g/罐', '瓶', '浙江康恩贝制药', '康恩贝', '国食健字G20070123', 168.00, 228.00, 180, 890, 0, 0, '乙类', '煮水或泡茶，一次3-5g', '滋阴清热、益胃生津', '密封保存', '24个月', '滋阴清热、益胃生津', 1),
(1007, 'YP20245007', '修正益生菌粉', NULL, 50, 1, 'https://images.unsplash.com/photo-1588776814546-1ffcf47267a5?w=300&h=300&fit=crop', '2g*30袋', '盒', '修正药业', '修正', '国食健字G20130056', 88.00, 128.00, 300, 2560, 0, 0, '乙类', '口服，一次1袋，一日1-2次', '调节肠道菌群', '密封保存', '24个月', '调节肠道菌群', 1),
(1008, 'YP20245008', '同仁堂枸杞子', NULL, 50, 1, 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=300&h=300&fit=crop', '200g/罐', '瓶', '北京同仁堂', '同仁堂', '宁卫食证字2008', 38.00, 52.00, 400, 3200, 0, 1, '乙类', '嚼服或泡水，一次10-15g', '滋补肝肾、益精明目', '密封保存', '60个月', '滋补肝肾、益精明目', 1),
(1009, 'YP20245009', '黄金搭档氨糖软骨素钙片', NULL, 50, 1, 'https://images.unsplash.com/photo-1583947215259-38e31be8751f?w=300&h=300&fit=crop', '60片/瓶', '盒', '上海黄金搭档', '黄金搭档', '国食健字G20050431', 98.00, 138.00, 250, 1560, 0, 0, '乙类', '口服，一次2片，一日2次', '增加骨密度', '密封保存', '24个月', '增加骨密度', 1),
(1010, 'YP20245010', '同仁堂蜂蜜', NULL, 50, 1, 'https://images.unsplash.com/photo-1564510714716-3bf387e05561?w=300&h=300&fit=crop', '500g/瓶', '瓶', '北京同仁堂', '同仁堂', 'SC12611010500321', 58.00, 78.00, 350, 2340, 0, 1, '乙类', '温水冲服，一次15-30g', '润肠通便、润肺止咳', '密封保存', '60个月', '润肠通便、润肺止咳', 1)
;


-- =============================================
-- 二十一、门店完善数据（共10家）
-- =============================================

-- 补充7家门店（已有STORE001-STORE003），凑齐10家
INSERT INTO `dm_store` (`id`, `store_code`, `store_name`, `logo_text`, `logo_color`, `rating`, `monthly_sales`, `address`, `phone`, `business_hours`, `is_open`, `is_24hours`, `latitude`, `longitude`, `description`, `business_scope`, `license_no`, `is_insurance`, `is_chain`, `is_self_operated`, `delivery_time`, `min_delivery_amount`, `delivery_fee`, `status`, `sort_order`) VALUES
(4, 'STORE004', '益丰大药房(西城店)', '益丰', '#2196F3', 4.6, 1567, '北京市西城区西单北大街120号', '010-66012345', '08:00-22:00', 1, 0, 39.908765, 116.375432, '专业药房，品种齐全，价格实惠。', '处方药、非处方药、医疗器械、保健食品、中药饮片', '京BA000004', 1, 1, 0, 30, 0.00, 5.00, 1, 4),
(5, 'STORE005', '一心堂(丰台店)', '一心', '#9C27B0', 4.5, 986, '北京市丰台区南三环西路16号', '010-63712345', '08:30-21:30', 1, 0, 39.856789, 116.345678, '用心服务，健康万家。', '中药饮片、中成药、化学药制剂、抗生素、生化药品', '京BA000005', 1, 1, 0, 40, 29.00, 6.00, 1, 5),
(6, 'STORE006', '大参林(通州店)', '大参', '#FF5722', 4.7, 2100, '北京市通州区新华大街58号', '010-69512345', '07:30-22:30', 1, 0, 39.910234, 116.655678, '参茸滋补，健康人生。', '中药饮片、中成药、西药、参茸滋补品', '京BA000006', 1, 1, 0, 35, 0.00, 5.00, 1, 6),
(7, 'STORE007', '屈臣氏药房(三里屯店)', '屈臣', '#00BCD4', 4.4, 756, '北京市朝阳区三里屯路19号', '010-64112345', '10:00-22:00', 1, 0, 39.935678, 116.452345, '美妆个护+药品健康，一站式购物。', '非处方药、医疗器械、美妆个护、保健食品', '京BA000007', 0, 1, 0, 45, 49.00, 8.00, 1, 7),
(8, 'STORE008', '国大药房(昌平店)', '国大', '#FF9800', 4.6, 1856, '北京市昌平区回龙观西大街35号', '010-80712345', '08:00-21:00', 1, 0, 40.076789, 116.335678, '国药控股旗下，品质保障。', '处方药、非处方药、医疗器械、保健食品', '京BA000008', 1, 1, 0, 40, 39.00, 6.00, 1, 8),
(9, 'STORE009', '万民大药房(大兴店)', '万民', '#4CAF50', 4.3, 654, '北京市大兴区黄村镇兴华大街88号', '010-69212345', '08:30-21:00', 1, 0, 39.728765, 116.343210, '万民健康，守护一生。', '中成药、化学药制剂、抗生素、生化药品、医疗器械', '京BA000009', 1, 0, 0, 50, 19.00, 4.00, 1, 9),
(10, 'STORE010', '健之佳药房(石景山店)', '健佳', '#607D8B', 4.5, 1234, '北京市石景山区石景山路68号', '010-88912345', '08:00-21:30', 1, 0, 39.907654, 116.225678, '健康佳品，品质生活。', '处方药、非处方药、中药饮片、保健食品、医疗器械', '京BA000010', 1, 1, 0, 35, 29.00, 5.00, 1, 10);

-- 补充门店资质认证
INSERT INTO `dm_store_certification` (`store_id`, `cert_name`, `cert_type`, `cert_no`, `issue_date`, `expire_date`, `status`) VALUES
(4, '药品经营许可证', 'license', '京BA000004', '2023-03-15', '2028-03-14', 1),
(4, 'GSP认证证书', 'gsp', 'GSP110000004', '2023-04-20', '2028-04-19', 1),
(4, '医保定点零售药店', 'insurance', 'YB110000004', '2023-05-10', '2026-05-09', 1),
(5, '药品经营许可证', 'license', '京BA000005', '2022-08-15', '2027-08-14', 1),
(5, 'GSP认证证书', 'gsp', 'GSP110000005', '2022-09-20', '2027-09-19', 1),
(5, '医保定点零售药店', 'insurance', 'YB110000005', '2022-10-10', '2025-10-09', 1),
(6, '药品经营许可证', 'license', '京BA000006', '2023-06-15', '2028-06-14', 1),
(6, 'GSP认证证书', 'gsp', 'GSP110000006', '2023-07-20', '2028-07-19', 1),
(6, '医保定点零售药店', 'insurance', 'YB110000006', '2023-08-10', '2026-08-09', 1),
(7, '药品经营许可证', 'license', '京BA000007', '2023-09-15', '2028-09-14', 1),
(7, 'GSP认证证书', 'gsp', 'GSP110000007', '2023-10-20', '2028-10-19', 1),
(8, '药品经营许可证', 'license', '京BA000008', '2022-11-15', '2027-11-14', 1),
(8, 'GSP认证证书', 'gsp', 'GSP110000008', '2022-12-20', '2027-12-19', 1),
(8, '医保定点零售药店', 'insurance', 'YB110000008', '2023-01-10', '2026-01-09', 1),
(9, '药品经营许可证', 'license', '京BA000009', '2023-02-15', '2028-02-14', 1),
(9, 'GSP认证证书', 'gsp', 'GSP110000009', '2023-03-20', '2028-03-19', 1),
(9, '医保定点零售药店', 'insurance', 'YB110000009', '2023-04-10', '2026-04-09', 1),
(10, '药品经营许可证', 'license', '京BA000010', '2023-05-15', '2028-05-14', 1),
(10, 'GSP认证证书', 'gsp', 'GSP110000010', '2023-06-20', '2028-06-19', 1),
(10, '医保定点零售药店', 'insurance', 'YB110000010', '2023-07-10', '2026-07-09', 1);


-- =============================================
-- 二十二、问诊问医生数据完善
-- =============================================

-- 22.1 完善科室数据
DELETE FROM `dm_department` WHERE `code` IN ('internal', 'surgery', 'pediatrics', 'gynecology', 'dermatology', 'tcm', 'psychology');
INSERT INTO `dm_department` (`id`, `code`, `name`, `icon`, `tag`, `tag_type`, `parent_id`, `sort`, `status`) VALUES
(101, 'internal', '内科', 'icon-neike', '热门', 'hot', 0, 1, 1),
(102, 'surgery', '外科', 'icon-waike', '热门', 'hot', 0, 2, 1),
(103, 'pediatrics', '儿科', 'icon-erke', '', 'info', 0, 3, 1),
(104, 'gynecology', '妇产科', 'icon-fuchan', '热门', 'hot', 0, 4, 1),
(105, 'tcm', '中医科', 'icon-zhongyi', '', 'info', 0, 5, 1),
(106, 'ophthalmology', '眼科', 'icon-yanke', '', 'info', 0, 6, 1),
(107, 'dental', '口腔科', 'icon-kouqiang', '', 'info', 0, 7, 1),
(108, 'dermatology', '皮肤科', 'icon-pifu', '', 'info', 0, 8, 1),
(109, 'neurology', '神经内科', 'icon-shenjing', '', 'info', 0, 9, 1),
(110, 'orthopedics', '骨科', 'icon-guke', '', 'info', 0, 10, 1),
(111, 'psychology', '心理咨询', 'icon-xinli', '', 'info', 0, 11, 1),
(112, 'urology', '泌尿外科', 'icon-miniao', '', 'info', 0, 12, 1),
(113, 'ent', '耳鼻喉科', 'icon-ebh', '', 'info', 0, 13, 1);

-- 22.2 补充医生数据（关联科室）
INSERT INTO `dm_doctor` (`id`, `phone`, `password`, `name`, `avatar`, `gender`, `title`, `hospital`, `department_id`, `department`, `license_no`, `is_certified`, `rating`, `service_count`, `response_time`, `specialties`, `introduction`, `balance`, `status`) VALUES
('DOC010', '13900001010', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '刘芳', 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=200&h=200&fit=crop&crop=face', 2, '主任医师', '北京协和医院', 101, '内科', '110000199010101234', 1, 4.9, 1580, 2, '高血压、糖尿病、冠心病', '从事内科临床工作25年，擅长内科常见病、慢性病的诊治', 18500.00, 1),
('DOC011', '13900001011', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王建国', 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face', 1, '副主任医师', '北京积水潭医院', 102, '外科', '110000199011111234', 1, 4.8, 1120, 3, '微创手术、腹腔镜、胃肠外科', '从事普外科临床工作18年，擅长腹腔镜微创手术', 15800.00, 1),
('DOC012', '13900001012', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李晓红', 'https://images.unsplash.com/photo-1594824476967-48c8b964ac31?w=200&h=200&fit=crop&crop=face', 2, '主治医师', '北京儿童医院', 103, '儿科', '110000199012121234', 1, 4.7, 856, 4, '小儿感冒、小儿肺炎、小儿腹泻', '从事儿科临床工作12年，擅长小儿常见病、多发病', 12500.00, 1),
('DOC013', '13900001013', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张丽华', 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=200&h=200&fit=crop&crop=face', 2, '主任医师', '北京大学第三医院', 104, '妇产科', '110000199013131234', 1, 4.9, 1890, 2, '高危妊娠、妇科肿瘤、不孕不育', '从事妇产科临床工作28年，擅长高危妊娠管理和妇科肿瘤', 25000.00, 1),
('DOC014', '13900001014', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '陈明远', 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face', 1, '副主任医师', '北京中医医院', 105, '中医科', '110000199014141234', 1, 4.6, 756, 5, '中医内科、针灸推拿、脾胃病', '从事中医临床工作15年，擅长中医辨证施治', 9800.00, 1),
('DOC015', '13900001015', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '赵雪梅', 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=200&h=200&fit=crop&crop=face', 2, '主治医师', '北京同仁医院', 106, '眼科', '110000199015151234', 1, 4.7, 658, 3, '白内障、青光眼、眼底病', '从事眼科临床工作10年，擅长白内障手术和眼底病诊治', 8500.00, 1),
('DOC016', '13900001016', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '孙志强', 'https://images.unsplash.com/photo-1594824476967-48c8b964ac31?w=200&h=200&fit=crop&crop=face', 1, '副主任医师', '北京大学口腔医院', 107, '口腔科', '110000199016161234', 1, 4.8, 890, 4, '种植牙、正畸、牙体牙髓病', '从事口腔科临床工作13年，擅长种植修复和正畸治疗', 11200.00, 1),
('DOC017', '13900001017', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '周美玲', 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=200&h=200&fit=crop&crop=face', 2, '主任医师', '北京协和医院', 108, '皮肤科', '110000199017171234', 1, 4.9, 1456, 2, '湿疹、荨麻疹、银屑病、痤疮', '从事皮肤科临床工作20年，擅长疑难皮肤病诊治', 19500.00, 1),
('DOC018', '13900001018', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '吴晓明', 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face', 1, '主治医师', '北京天坛医院', 109, '神经内科', '110000199018181234', 1, 4.7, 567, 4, '脑血管病、癫痫、帕金森病', '从事神经内科临床工作8年', 7800.00, 1),
('DOC019', '13900001019', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '郑海涛', 'https://images.unsplash.com/photo-1594824476967-48c8b964ac31?w=200&h=200&fit=crop&crop=face', 1, '副主任医师', '北京积水潭医院', 110, '骨科', '110000199019191234', 1, 4.8, 980, 3, '骨折、关节置换、脊柱外科', '从事骨科临床工作16年，擅长创伤骨科和关节外科', 14200.00, 1),
('DOC020', '13900001020', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '林婷婷', 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=200&h=200&fit=crop&crop=face', 2, '主治医师', '北京大学第六医院', 111, '心理咨询', '110000199020201234', 1, 4.6, 456, 5, '焦虑症、抑郁症、失眠', '从事心理科临床工作8年，擅长心理治疗和药物治疗', 6500.00, 1),
('DOC021', '13900001021', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '何伟', 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=200&h=200&fit=crop&crop=face', 1, '主任医师', '北京大学第一医院', 112, '泌尿外科', '110000199021211234', 1, 4.8, 1230, 3, '前列腺疾病、泌尿系结石、男科疾病', '从事泌尿外科临床工作22年', 17500.00, 1);


-- =============================================
-- 二十三、首页活动专区数据完善
-- =============================================

-- 23.1 问医生Tab活动专区广告位
INSERT INTO `dm_home_ad_slot` (`id`, `name`, `position`, `ad_type`, `image_url`, `title`, `subtitle`, `jump_type`, `jump_url`, `bg_color`, `sort_order`, `status`, `tab_id`, `section_id`, `page_id`, `version`) VALUES
(201, '问医生-快速问诊Banner', 'doctor_quick', 'image', 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=600&h=300&fit=crop', '9秒快速问诊', '三甲医生24小时在线', 'route', '/inquiry/quick', '#E8F5E9', 1, 1, 'doctor', 6, 'home_page_001', 'v1.0.0'),
(202, '问医生-专家推荐Banner', 'doctor_expert', 'image', 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=600&h=300&fit=crop', '权威专家在线', '副主任医师以上资质', 'route', '/inquiry/experts', '#FFF3E0', 2, 1, 'doctor', 6, 'home_page_001', 'v1.0.0'),
(203, '问医生-复诊开方Banner', 'doctor_prescription', 'image', 'https://images.unsplash.com/photo-1512069772995-ec65d4e3ade8?w=600&h=300&fit=crop', '在线复诊开方', '处方药品配送到家', 'route', '/prescription/apply', '#E3F2FD', 3, 1, 'doctor', 6, 'home_page_001', 'v1.0.0'),
(204, '问医生-专科问诊Banner', 'doctor_specialty', 'image', 'https://images.unsplash.com/photo-1628348068343-b5daab358737?w=600&h=300&fit=crop', '专科问诊服务', '精准匹配专科医生', 'route', '/inquiry/departments', '#FCE4EC', 4, 1, 'doctor', 6, 'home_page_001', 'v1.0.0');

-- 23.2 推荐Tab活动专区广告位
INSERT INTO `dm_home_ad_slot` (`id`, `name`, `position`, `ad_type`, `image_url`, `title`, `subtitle`, `jump_type`, `jump_url`, `bg_color`, `sort_order`, `status`, `tab_id`, `section_id`, `page_id`, `version`) VALUES
(205, '推荐-春季健康节Banner', 'recommend_spring', 'image', 'https://images.unsplash.com/photo-1584515933487-779824d29309?w=600&h=300&fit=crop', '春季健康节', '全场药品满199减30', 'route', '/activity/spring', '#FFF8E1', 1, 1, 'recommend', 4, 'home_page_001', 'v1.0.0'),
(206, '推荐-新用户专享Banner', 'recommend_newuser', 'image', 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=600&h=300&fit=crop', '新用户专享', '首单立减20元', 'route', '/activity/newuser', '#E3F2FD', 2, 1, 'recommend', 4, 'home_page_001', 'v1.0.0'),
(207, '推荐-处方药专区Banner', 'recommend_rx', 'image', 'https://images.unsplash.com/photo-1588776814546-1ffcf47267a5?w=600&h=300&fit=crop', '处方药专区', '凭处方购药享85折', 'route', '/prescription', '#F3E5F5', 3, 1, 'recommend', 4, 'home_page_001', 'v1.0.0'),
(208, '推荐-医疗器械Banner', 'recommend_device', 'image', 'https://images.unsplash.com/photo-1583947215259-38e31be8751f?w=600&h=300&fit=crop', '医疗器械特惠', '血压计、血糖仪低至5折', 'route', '/category?device', '#E8F5E9', 4, 1, 'recommend', 4, 'home_page_001', 'v1.0.0');

-- 23.3 补充首页模块配置中的活动区域内容数据
UPDATE `dm_home_section` SET `content` = JSON_ARRAY(
  JSON_OBJECT('id', 205, 'title', '春季健康节', 'subtitle', '全场药品满199减30', 'imageUrl', 'https://images.unsplash.com/photo-1584515933487-779824d29309?w=600&h=300&fit=crop', 'link', '/activity/spring'),
  JSON_OBJECT('id', 206, 'title', '新用户专享', 'subtitle', '首单立减20元', 'imageUrl', 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=600&h=300&fit=crop', 'link', '/activity/newuser'),
  JSON_OBJECT('id', 207, 'title', '处方药专区', 'subtitle', '凭处方购药享85折', 'imageUrl', 'https://images.unsplash.com/photo-1588776814546-1ffcf47267a5?w=600&h=300&fit=crop', 'link', '/prescription')
) WHERE `id` = 4;

-- 23.4 补充问医生金刚位（医生Tab）
DELETE FROM `dm_home_kingkong` WHERE `tab_id` = 'doctor';
INSERT INTO `dm_home_kingkong` (`id`, `name`, `icon_url`, `jump_type`, `jump_url`, `bg_color`, `sort_order`, `status`, `tab_id`, `section_id`, `page_id`, `version`) VALUES
(301, '内科', 'https://images.unsplash.com/photo-1584515933487-779824d29309?w=120&h=120&fit=crop', 'route', '/inquiry?dept=internal', '#E3F2FD', 1, 1, 'doctor', 6, 'home_page_001', 'v1.0.0'),
(302, '外科', 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=120&h=120&fit=crop', 'route', '/inquiry?dept=surgery', '#F3E5F5', 2, 1, 'doctor', 6, 'home_page_001', 'v1.0.0'),
(303, '儿科', 'https://images.unsplash.com/photo-1559757175-5700dde675bc?w=120&h=120&fit=crop', 'route', '/inquiry?dept=pediatrics', '#E8F5E9', 3, 1, 'doctor', 6, 'home_page_001', 'v1.0.0'),
(304, '妇产科', 'https://images.unsplash.com/photo-1512069772995-ec65d4e3ade8?w=120&h=120&fit=crop', 'route', '/inquiry?dept=gynecology', '#FCE4EC', 4, 1, 'doctor', 6, 'home_page_001', 'v1.0.0'),
(305, '皮肤科', 'https://images.unsplash.com/photo-1628348068343-b5daab358737?w=120&h=120&fit=crop', 'route', '/inquiry?dept=dermatology', '#FFF3E0', 5, 1, 'doctor', 6, 'home_page_001', 'v1.0.0'),
(306, '中医科', 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=120&h=120&fit=crop', 'route', '/inquiry?dept=tcm', '#EFEBE9', 6, 1, 'doctor', 6, 'home_page_001', 'v1.0.0'),
(307, '心理咨询', 'https://images.unsplash.com/photo-1588776814546-1ffcf47267a5?w=120&h=120&fit=crop', 'route', '/inquiry?dept=psychology', '#E0F7FA', 7, 1, 'doctor', 6, 'home_page_001', 'v1.0.0'),
(308, '骨科', 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=120&h=120&fit=crop', 'route', '/inquiry?dept=orthopedics', '#F5F5F5', 8, 1, 'doctor', 6, 'home_page_001', 'v1.0.0');


-- =============================================
-- 完成提示
-- =============================================

SELECT '=== DrugMall 数据库初始化完成 ===' AS message;
SELECT COUNT(*) AS table_count FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'drugmall';
SELECT COUNT(*) AS home_page_count FROM dm_home_page;
SELECT COUNT(*) AS home_tab_count FROM dm_home_tab;
SELECT COUNT(*) AS home_section_count FROM dm_home_section;
SELECT COUNT(*) AS home_kingkong_count FROM dm_home_kingkong;
