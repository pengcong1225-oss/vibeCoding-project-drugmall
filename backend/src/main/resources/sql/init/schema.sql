-- =============================================
-- DrugMall 数据库表结构设计
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `drugmall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `drugmall`;

-- =============================================
-- 一、用户相关表
-- =============================================

-- 1.1 用户表
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

-- 1.2 用户地址表
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

-- 1.3 患者档案表
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
-- 二、医生相关表
-- =============================================

-- 2.1 医生表
DROP TABLE IF EXISTS `dm_doctor`;
CREATE TABLE `dm_doctor` (
  `id` VARCHAR(20) NOT NULL COMMENT '医生ID',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像',
  `gender` TINYINT DEFAULT 1 COMMENT '性别 1-男 2-女',
  `title` VARCHAR(50) DEFAULT NULL COMMENT '职称',
  `hospital` VARCHAR(100) DEFAULT NULL COMMENT '医院',
  `department` VARCHAR(50) DEFAULT NULL COMMENT '科室',
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
  KEY `idx_status` (`status`),
  KEY `idx_is_certified` (`is_certified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生表';

-- 2.2 医生排班表
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
-- 三、药品相关表
-- =============================================

-- 3.1 药品分类表
DROP TABLE IF EXISTS `dm_category`;
CREATE TABLE `dm_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID 0-顶级分类',
  `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品分类表';

-- 3.2 品牌表
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

-- 3.3 药品表
DROP TABLE IF EXISTS `dm_product`;
CREATE TABLE `dm_product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '药品ID',
  `product_code` VARCHAR(50) NOT NULL COMMENT '药品编码',
  `product_name` VARCHAR(200) NOT NULL COMMENT '药品名称',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `brand_id` BIGINT DEFAULT NULL COMMENT '品牌ID',
  `main_image` VARCHAR(500) DEFAULT NULL COMMENT '主图',
  `images` TEXT COMMENT '图片列表JSON',
  `specification` VARCHAR(100) DEFAULT NULL COMMENT '规格',
  `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
  `manufacturer` VARCHAR(200) DEFAULT NULL COMMENT '生产厂家',
  `approval_number` VARCHAR(100) DEFAULT NULL COMMENT '批准文号',
  `price` DECIMAL(10,2) NOT NULL COMMENT '售价',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
  `warning_stock` INT DEFAULT 10 COMMENT '库存预警值',
  `sales` INT DEFAULT 0 COMMENT '销量',
  `is_rx` TINYINT NOT NULL DEFAULT 0 COMMENT '是否处方药 0-非处方 1-处方',
  `usage` VARCHAR(500) DEFAULT NULL COMMENT '用法用量',
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

-- =============================================
-- 四、购物车相关表
-- =============================================

-- 4.1 购物车表
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
-- 五、订单相关表
-- =============================================

-- 5.1 订单表
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

-- 5.2 订单明细表
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

-- =============================================
-- 六、问诊相关表
-- =============================================

-- 6.1 问诊表
DROP TABLE IF EXISTS `dm_consultation`;
CREATE TABLE `dm_consultation` (
  `id` VARCHAR(20) NOT NULL COMMENT '问诊ID',
  `doctor_id` VARCHAR(20) NOT NULL COMMENT '医生ID',
  `patient_id` BIGINT NOT NULL COMMENT '患者ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` VARCHAR(20) DEFAULT '图文问诊' COMMENT '问诊类型',
  `symptom` TEXT COMMENT '症状描述',
  `images` TEXT COMMENT '图片列表JSON',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态 pending-待接诊 processing-问诊中 completed-已完成 closed-已关闭',
  `is_urgent` TINYINT DEFAULT 0 COMMENT '是否紧急 0-否 1-是',
  `is_rx` TINYINT DEFAULT 0 COMMENT '是否需要处方 0-否 1-是',
  `fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '问诊费用',
  `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_doctor_id` (`doctor_id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问诊表';

-- 6.2 问诊消息表
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
-- 七、处方相关表
-- =============================================

-- 7.1 处方表
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

-- 7.2 处方明细表
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
-- 九、门店相关表
-- =============================================

-- 9.1 门店表
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

-- 9.2 门店资质认证表
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

-- 9.3 门店服务承诺表
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

-- 9.4 门店药品库存表
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

-- 9.5 门店标签表
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

-- 9.6 门店评价表
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
-- 十、优惠券相关表
-- =============================================

-- 8.1 优惠券表
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

-- 8.2 用户优惠券表
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
-- 九、收入相关表
-- =============================================

-- 9.1 医生收入表
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

-- 9.2 提现表
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
-- 十、内容管理相关表
-- =============================================

-- 10.1 Banner表
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

-- 10.2 公告表
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
-- 十一、管理员相关表
-- =============================================

-- 11.1 管理员表
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

-- 11.2 角色表
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

-- 11.3 管理员角色关联表
DROP TABLE IF EXISTS `dm_admin_role`;
CREATE TABLE `dm_admin_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `admin_id` BIGINT NOT NULL COMMENT '管理员ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_role` (`admin_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员角色关联表';

-- 11.4 权限表
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

-- 11.5 角色权限关联表
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
-- 十二、系统配置表
-- =============================================

-- 12.1 系统配置表
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
