-- =============================================
-- DrugMall 业务数据表结构设计
-- 用于替代前端常量文件中的业务数据
-- =============================================

USE `drugmall`;

-- =============================================
-- 一、科室相关表
-- =============================================

-- 1.1 科室表
DROP TABLE IF EXISTS `dm_department`;
CREATE TABLE `dm_department` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '科室ID',
  `code` VARCHAR(50) NOT NULL COMMENT '科室编码',
  `name` VARCHAR(50) NOT NULL COMMENT '科室名称',
  `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标标识',
  `tag` VARCHAR(50) DEFAULT NULL COMMENT '标签文本',
  `tag_type` VARCHAR(20) DEFAULT 'info' COMMENT '标签类型 hot/price/fever/supplement/info',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室表';

-- 1.2 科室配置表（价格、症状、快捷症状等）
DROP TABLE IF EXISTS `dm_department_config`;
CREATE TABLE `dm_department_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `department_code` VARCHAR(50) NOT NULL COMMENT '科室编码',
  `price` DECIMAL(10,2) NOT NULL COMMENT '问诊价格',
  `original_price` DECIMAL(10,2) NOT NULL COMMENT '原价',
  `subsidy` DECIMAL(10,2) DEFAULT 0.00 COMMENT '补贴金额',
  `symptoms` TEXT COMMENT '适用症状描述',
  `response_time` INT DEFAULT 10 COMMENT '平均响应时间(秒)',
  `answer_time` INT DEFAULT 30 COMMENT '平均解答时间(分钟)',
  `example` VARCHAR(500) DEFAULT NULL COMMENT '输入示例',
  `quick_symptoms` TEXT COMMENT '快捷症状JSON数组',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_department_code` (`department_code`),
  KEY `idx_price` (`price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室配置表';

-- 1.3 科室标签表（用于首页筛选）
DROP TABLE IF EXISTS `dm_department_tag`;
CREATE TABLE `dm_department_tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `code` VARCHAR(50) NOT NULL COMMENT '标签值(科室编码)',
  `label` VARCHAR(50) NOT NULL COMMENT '标签显示名称',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室标签表';

-- =============================================
-- 二、医生相关表
-- =============================================

-- 2.1 医生扩展信息表（补充现有 dm_doctor 表）
DROP TABLE IF EXISTS `dm_doctor_ext`;
CREATE TABLE `dm_doctor_ext` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '扩展ID',
  `doctor_id` VARCHAR(20) NOT NULL COMMENT '医生ID',
  `department_code` VARCHAR(50) DEFAULT NULL COMMENT '科室编码',
  `is_online` TINYINT DEFAULT 0 COMMENT '是否在线 0-离线 1-在线',
  `can_prescribe` TINYINT DEFAULT 0 COMMENT '是否可开方 0-否 1-是',
  `wait_time` INT DEFAULT 0 COMMENT '平均等待时间(秒)',
  `consult_count` VARCHAR(20) DEFAULT '0' COMMENT '接诊量显示文本',
  `price_text` VARCHAR(100) DEFAULT NULL COMMENT '价格显示文本(图文/电话/视频)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_doctor_id` (`doctor_id`),
  KEY `idx_department_code` (`department_code`),
  KEY `idx_is_online` (`is_online`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生扩展信息表';

-- =============================================
-- 三、字典表
-- =============================================

-- 3.1 字典类型表
DROP TABLE IF EXISTS `dm_dict_type`;
CREATE TABLE `dm_dict_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典类型ID',
  `type_code` VARCHAR(50) NOT NULL COMMENT '字典类型编码',
  `type_name` VARCHAR(50) NOT NULL COMMENT '字典类型名称',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_code` (`type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- 3.2 字典数据表
DROP TABLE IF EXISTS `dm_dict_data`;
CREATE TABLE `dm_dict_data` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典数据ID',
  `type_code` VARCHAR(50) NOT NULL COMMENT '字典类型编码',
  `label` VARCHAR(100) NOT NULL COMMENT '显示标签',
  `value` VARCHAR(100) NOT NULL COMMENT '字典值',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `is_default` TINYINT DEFAULT 0 COMMENT '是否默认 0-否 1-是',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type_code` (`type_code`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- =============================================
-- 四、支付方式表
-- =============================================

DROP TABLE IF EXISTS `dm_payment_method`;
CREATE TABLE `dm_payment_method` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '支付方式ID',
  `code` VARCHAR(50) NOT NULL COMMENT '支付方式编码',
  `name` VARCHAR(50) NOT NULL COMMENT '支付方式名称',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
  `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标标识',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付方式表';

-- =============================================
-- 五、服务快捷入口表
-- =============================================

DROP TABLE IF EXISTS `dm_service_shortcut`;
CREATE TABLE `dm_service_shortcut` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '快捷入口ID',
  `name` VARCHAR(50) NOT NULL COMMENT '入口名称',
  `subtitle` VARCHAR(100) DEFAULT NULL COMMENT '副标题',
  `image` VARCHAR(500) DEFAULT NULL COMMENT '背景图片',
  `doctor_avatar` VARCHAR(500) DEFAULT NULL COMMENT '医生头像',
  `link_url` VARCHAR(200) DEFAULT NULL COMMENT '跳转链接',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务快捷入口表';

-- =============================================
-- 六、问诊流程步骤表
-- =============================================

DROP TABLE IF EXISTS `dm_consultation_step`;
CREATE TABLE `dm_consultation_step` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '步骤ID',
  `step` INT NOT NULL COMMENT '步骤序号',
  `name` VARCHAR(50) NOT NULL COMMENT '步骤名称',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '步骤描述',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_step` (`step`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问诊流程步骤表';

-- =============================================
-- 七、初始化数据
-- =============================================

-- 7.1 科室数据
INSERT INTO `dm_department` (`code`, `name`, `icon`, `tag`, `tag_type`, `sort_order`) VALUES
('bone', '骨科', 'bone', '', 'info', 1),
('neurology', '神经内科', 'brain', '', 'info', 2),
('general', '全科', 'firstAid', '', 'info', 3),
('tcm', '中医科', 'herb', '', 'info', 4),
('surgery', '普外科', 'scissor', '', 'info', 5),
('andrology', '男科门诊', 'male', '', 'info', 6),
('cardiology', '心血管内科', 'heart', '', 'info', 7),
('endocrine', '内分泌科', 'stomach', '', 'info', 8),
('tcm-spleen', '中医脾胃病', 'herb', '', 'info', 9),
('tcm-male', '中医男科', 'male', '补肾', 'supplement', 10),
('tcm-sleep', '中医失眠科', 'moon', '', 'info', 11),
('tcm-female', '中医妇科', 'female', '', 'info', 12),
('weight', '减重门诊', 'scale', '', 'info', 13),
('sleep', '睡眠中心', 'moon', '9.9元起', 'price', 14),
('dermatology', '皮肤科', 'skin', '瘙痒', 'hot', 15),
('respiratory', '呼吸内科', 'lung', '', 'info', 16),
('pediatrics', '儿科', 'child', '发热', 'fever', 17),
('gastroenterology', '消化内科', 'stomach', '', 'info', 18),
('gynecology', '妇产科', 'female', '', 'info', 19),
('ent', '耳鼻喉科', 'ear', '', 'info', 20),
('urology', '泌尿外科', 'kidney', '', 'info', 21),
('dental', '口腔科', 'tooth', '', 'info', 22),
('ophthalmology', '眼科', 'eye', '', 'info', 23),
('psychology', '心理咨询', 'brain', '19.9元', 'price', 24);

-- 7.2 科室配置数据
INSERT INTO `dm_department_config` (`department_code`, `price`, `original_price`, `subsidy`, `symptoms`, `response_time`, `answer_time`, `example`, `quick_symptoms`) VALUES
('bone', 4.9, 19.9, 15.0, '关节炎、颈椎病、腰椎间盘突出、腰肌劳损、肩周炎、骨折后康复、骨质增生、腱鞘炎、骨质疏松、运动损伤、检查单解读', 9, 30, '腰痛伴随腿发麻，1周，用膏药外贴，效果不明显。', '["颈椎病","腰痛","腰椎间盘突出","关节炎","腰肌劳损","关节痛","关节扭伤","外伤","肩周炎","骨折"]'),
('neurology', 7.9, 19.9, 12.0, '失眠、头痛、头晕、焦虑、抑郁状态、神经痛、脑血管病、帕金森病、认知功能障碍、面肌痉挛、面神经炎、检查单解读', 9, 30, '失眠，1个月，未使用过药物，未线下就诊。', '["头痛头晕","失眠","易醒","偏头痛","焦虑障碍","多梦","眩晕","周围神经病","头晕目眩","记忆力减退"]'),
('dermatology', 19.9, 39.9, 20.0, '湿疹、荨麻疹、痤疮、银屑病、白癜风、皮炎、皮肤瘙痒、脱发、色斑、痘痘、过敏', 5, 15, '面部起红疹，瘙痒3天，未使用过药物。', '["湿疹","荨麻疹","痤疮","皮肤瘙痒","过敏","脱发","痘痘","色斑","皮炎","银屑病"]'),
('respiratory', 9.9, 29.9, 20.0, '咳嗽、咳痰、气喘、胸闷、胸痛、发热、感冒、支气管炎、肺炎、哮喘、慢阻肺', 8, 20, '半夜咽痒咳嗽，持续1周，喝水可缓解，未用过药。', '["咳嗽","咳痰","气喘","胸闷","发热","感冒","咽痛","流鼻涕","打喷嚏","胸痛"]'),
('pediatrics', 9.9, 29.9, 20.0, '小儿发热、咳嗽、腹泻、呕吐、湿疹、厌食、夜啼、多动症、发育迟缓、疫苗接种咨询', 6, 15, '宝宝发热38.5度，持续2天，精神尚可，未用药。', '["发热","咳嗽","腹泻","呕吐","湿疹","厌食","夜啼","流鼻涕","皮疹","腹痛"]'),
('gynecology', 19.9, 49.9, 30.0, '月经不调、痛经、白带异常、阴道炎、盆腔炎、子宫肌瘤、卵巢囊肿、备孕咨询、孕期检查', 10, 25, '月经推迟10天，伴有腹痛，未做过检查。', '["月经不调","痛经","白带异常","腹痛","备孕咨询","孕期检查","阴道炎","盆腔炎","子宫肌瘤","卵巢囊肿"]'),
('gastroenterology', 9.9, 29.9, 20.0, '胃痛、胃胀、反酸、恶心、呕吐、腹泻、便秘、消化不良、胃炎、胃溃疡、肠炎', 8, 20, '胃痛伴反酸，持续3天，饭后加重，未用药。', '["胃痛","胃胀","反酸","恶心","腹泻","便秘","消化不良","呕吐","腹胀","食欲差"]'),
('psychology', 19.9, 199.0, 179.0, '焦虑、抑郁、失眠、情绪低落、压力大、人际关系困扰、职场压力、婚姻家庭问题、自我成长', 5, 10, '最近情绪低落，失眠2周，工作压力大。', '["焦虑","抑郁","失眠","情绪低落","压力大","人际关系","职场压力","婚姻问题","自我成长","注意力不集中"]'),
('tcm', 14.9, 39.9, 25.0, '体质调理、脾胃虚弱、气血不足、失眠多梦、腰膝酸软、月经不调、慢性疲劳、亚健康调理', 12, 30, '脾胃虚弱，食欲不振，乏力2个月，未系统调理。', '["脾胃虚弱","气血不足","失眠多梦","腰膝酸软","乏力","食欲不振","便秘","怕冷","盗汗","口干"]'),
('general', 4.9, 19.9, 15.0, '常见病、多发病、慢性病管理、健康咨询、体检报告解读、用药咨询、疫苗接种', 5, 15, '体检发现血压偏高，140/90，无症状，未用药。', '["高血压","高血糖","高血脂","体检咨询","用药咨询","健康管理","疫苗接种","慢性病","亚健康","疲劳"]');

-- 7.3 科室标签数据
INSERT INTO `dm_department_tag` (`code`, `label`, `sort_order`) VALUES
('all', '全部', 1),
('dermatology', '皮肤科', 2),
('respiratory', '呼吸内科', 3),
('pediatrics', '儿科', 4),
('gastroenterology', '消化内科', 5);

-- 7.4 字典类型数据
INSERT INTO `dm_dict_type` (`type_code`, `type_name`, `description`) VALUES
('doctor_title', '医生职称', '医生职称字典'),
('hospital_level', '医院等级', '医院等级字典'),
('doctor_sort', '医生排序', '医生列表排序选项'),
('doctor_specialty', '医生擅长', '医生擅长领域选项'),
('doctor_feature', '医生特色', '医生特色服务选项'),
('payment_method', '支付方式', '支付方式字典');

-- 7.5 字典数据 - 医生职称
INSERT INTO `dm_dict_data` (`type_code`, `label`, `value`, `sort_order`) VALUES
('doctor_title', '主任医师', 'chief', 1),
('doctor_title', '副主任医师', 'associate', 2),
('doctor_title', '主治医师', 'attending', 3),
('doctor_title', '住院医师', 'resident', 4);

-- 7.6 字典数据 - 医院等级
INSERT INTO `dm_dict_data` (`type_code`, `label`, `value`, `sort_order`, `is_default`) VALUES
('hospital_level', '全部', 'all', 1, 1),
('hospital_level', '三甲医院', '3a', 2, 0),
('hospital_level', '二甲医院', '2a', 3, 0),
('hospital_level', '一甲医院', '1a', 4, 0);

-- 7.7 字典数据 - 医生排序
INSERT INTO `dm_dict_data` (`type_code`, `label`, `value`, `sort_order`) VALUES
('doctor_sort', '综合排序', 'default', 1),
('doctor_sort', '好评优先', 'rating', 2),
('doctor_sort', '接诊量优先', 'consult', 3),
('doctor_sort', '价格从低到高', 'price', 4);

-- 7.8 字典数据 - 医生擅长
INSERT INTO `dm_dict_data` (`type_code`, `label`, `value`, `sort_order`) VALUES
('doctor_specialty', '全部擅长', 'all', 1),
('doctor_specialty', '过敏性疾病', 'allergy', 2),
('doctor_specialty', '皮肤疾病', 'skin', 3),
('doctor_specialty', '中医调理', 'tcm', 4),
('doctor_specialty', '慢性病管理', 'chronic', 5),
('doctor_specialty', '儿科疾病', 'child', 6),
('doctor_specialty', '心理咨询', 'psychology', 7);

-- 7.9 字典数据 - 医生特色
INSERT INTO `dm_dict_data` (`type_code`, `label`, `value`, `sort_order`) VALUES
('doctor_feature', '可开处方', 'prescription', 1),
('doctor_feature', '在线医生', 'online', 2),
('doctor_feature', '秒问医生', 'quick', 3),
('doctor_feature', '低价咨询', 'cheap', 4);

-- 7.10 支付方式数据
INSERT INTO `dm_payment_method` (`code`, `name`, `description`, `icon`, `sort_order`) VALUES
('wechat', '微信支付', '推荐使用', 'wechat', 1),
('alipay', '支付宝', '', 'alipay', 2),
('balance', '余额支付', '', 'balance', 3),
('bankcard', '银行卡', '支持储蓄卡/信用卡', 'bankcard', 4);

-- 7.11 服务快捷入口数据
INSERT INTO `dm_service_shortcut` (`name`, `subtitle`, `doctor_avatar`, `sort_order`) VALUES
('用药咨询', '安全用药', 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=100&h=100&fit=crop&crop=face', 1),
('抓中药', '养生茶饮', 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=100&h=100&fit=crop&crop=face', 2),
('心理咨询', '19.9元', 'https://images.unsplash.com/photo-1594824476967-48c8b964273f?w=100&h=100&fit=crop&crop=face', 3),
('电话医生', '9.9元起', 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=100&h=100&fit=crop&crop=face', 4);

-- 7.12 问诊流程步骤数据
INSERT INTO `dm_consultation_step` (`step`, `name`, `sort_order`) VALUES
(1, '导诊助手', 1),
(2, '支付诊费', 2),
(3, '医生接诊', 3),
(4, '问诊咨询', 4);

-- 7.13 追加字典类型
INSERT INTO `dm_dict_type` (`type_code`, `type_name`, `description`) VALUES
('disease_tag', '疾病标签', '处方申请疾病标签'),
('relationship', '患者关系', '患者与用户关系'),
('gender', '性别', '性别选项'),
('service_type', '服务类型', '问诊服务类型');

-- 7.14 字典数据 - 疾病标签
INSERT INTO `dm_dict_data` (`type_code`, `label`, `value`, `sort_order`) VALUES
('disease_tag', '感冒发热', '1', 1),
('disease_tag', '咳嗽咽痛', '2', 2),
('disease_tag', '头痛头晕', '3', 3),
('disease_tag', '消化不良', '4', 4),
('disease_tag', '腹泻腹痛', '5', 5),
('disease_tag', '皮肤过敏', '6', 6),
('disease_tag', '失眠多梦', '7', 7),
('disease_tag', '高血压', '8', 8),
('disease_tag', '糖尿病', '9', 9),
('disease_tag', '冠心病', '10', 10);

-- 7.15 字典数据 - 患者关系
INSERT INTO `dm_dict_data` (`type_code`, `label`, `value`, `sort_order`) VALUES
('relationship', '本人', 'self', 1),
('relationship', '父母', 'parent', 2),
('relationship', '子女', 'child', 3),
('relationship', '配偶', 'spouse', 4),
('relationship', '其他', 'other', 5);

-- 7.16 字典数据 - 性别
INSERT INTO `dm_dict_data` (`type_code`, `label`, `value`, `sort_order`) VALUES
('gender', '男', 'male', 1),
('gender', '女', 'female', 2);

-- 7.17 字典数据 - 服务类型
INSERT INTO `dm_dict_data` (`type_code`, `label`, `value`, `sort_order`) VALUES
('service_type', '图文咨询', 'text', 1),
('service_type', '电话咨询', 'phone', 2),
('service_type', '视频咨询', 'video', 3);
