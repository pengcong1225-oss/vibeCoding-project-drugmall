-- =============================================
-- DrugMall 医生模拟数据
-- 用于首页专家推荐、科室医生列表等
-- =============================================

USE `drugmall`;

-- =============================================
-- 医生数据
-- 注意：dm_doctor 表的 is_deleted 字段必须为 0 才能被查询到
-- =============================================

INSERT INTO `dm_doctor` (`id`, `phone`, `password`, `name`, `avatar`, `gender`, `title`, `hospital`, `department`, `license_no`, `is_certified`, `rating`, `service_count`, `response_time`, `specialties`, `introduction`, `balance`, `status`, `create_time`, `update_time`, `is_deleted`) VALUES
('DOC001', '13800000001', '$2a$10$X/e6L5NO.RO9/SO3yQVJ7eOQZQZHG8JK9.7ZFH5cGjN.Y.8aGQXHi', '周峰', 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face', 1, '主治医师', '武汉市黄陂区人民医院', '皮肤科', 'L123456789', 1, 4.9, 7542, 4, '过敏性疾病（特应性皮炎、湿疹、荨麻疹）、自身免疫性疱病、药疹、结缔组织病及血管炎', '从事皮肤科临床工作10余年，擅长过敏性皮肤病的诊治，对湿疹、荨麻疹等常见病有丰富的经验。', 0.00, 1, NOW(), NOW(), 0),
('DOC002', '13800000002', '$2a$10$X/e6L5NO.RO9/SO3yQVJ7eOQZQZHG8JK9.7ZFH5cGjN.Y.8aGQXHi', '陈琼', 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=200&h=200&fit=crop&crop=face', 2, '主治医师', '辽宁中医药大学附属第二医院', '皮肤科', 'L987654321', 1, 4.8, 42000, 37, '痤疮、湿疹、银屑病、白癜风、中医调理皮肤', '女，就职于辽宁中医药大学附属第二医院，主治医师，硕士研究生。对中医药治疗痤疮、湿疹有深入研究。', 0.00, 1, NOW(), NOW(), 0),
('DOC003', '13800000003', '$2a$10$X/e6L5NO.RO9/SO3yQVJ7eOQZQZHG8JK9.7ZFH5cGjN.Y.8aGQXHi', '李贤光', 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=200&h=200&fit=crop&crop=face', 1, '主任医师', '昆明医科大学第一附属医院', '皮肤科', 'L456789123', 1, 4.9, 18000, 13, '湿疹、荨麻疹、痤疮、银屑病、白癜风', '擅长湿疹、荨麻疹、痤疮、银屑病、白癜风等常见皮肤病的诊治', 0.00, 1, NOW(), NOW(), 0),
('DOC004', '13800000004', '$2a$10$X/e6L5NO.RO9/SO3yQVJ7eOQZQZHG8JK9.7ZFH5cGjN.Y.8aGQXHi', '张晓明', 'https://images.unsplash.com/photo-1537368910025-700350fe46c7?w=200&h=200&fit=crop&crop=face', 1, '副主任医师', '首都儿科研究所', '儿科', 'L111222333', 1, 4.9, 25000, 15, '小儿发热、呼吸道感染、消化系统疾病', '从事儿科临床工作20余年，对儿童常见病、多发病有丰富的诊疗经验。', 0.00, 1, NOW(), NOW(), 0),
('DOC005', '13800000005', '$2a$10$X/e6L5NO.RO9/SO3yQVJ7eOQZQZHG8JK9.7ZFH5cGjN.Y.8aGQXHi', '王雪梅', 'https://images.unsplash.com/photo-1594824476967-48c8b964273f?w=200&h=200&fit=crop&crop=face', 2, '主任医师', '北京大学第六医院', '心理咨询', 'L999888777', 1, 4.8, 12000, 5, '焦虑症、抑郁症、睡眠障碍、情绪管理', '焦虑症、抑郁症、睡眠障碍、情绪管理专家', 0.00, 1, NOW(), NOW(), 0),
('DOC006', '13800000006', '$2a$10$X/e6L5NO.RO9/SO3yQVJ7eOQZQZHG8JK9.7ZFH5cGjN.Y.8aGQXHi', '刘贞君', 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face', 1, '主治医师', '山东青岛中西医结合医院', '皮肤科', 'L555666777', 1, 4.9, 11000, 9, '中西医结合诊疗银屑病，痤疮，湿疹，荨麻疹', '中西医结合诊疗银屑病，痤疮，湿疹，荨麻疹等常见皮肤病', 0.00, 1, NOW(), NOW(), 0),
('DOC007', '13800000007', '$2a$10$X/e6L5NO.RO9/SO3yQVJ7eOQZQZHG8JK9.7ZFH5cGjN.Y.8aGQXHi', '赵文博', 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=200&h=200&fit=crop&crop=face', 1, '副主任医师', '北京协和医院', '呼吸内科', 'L222333444', 1, 4.9, 15000, 10, '慢性咳嗽、哮喘、慢阻肺、肺部感染', '擅长慢性咳嗽、哮喘、慢阻肺、肺部感染等呼吸系统疾病的诊治', 0.00, 1, NOW(), NOW(), 0),
('DOC008', '13800000008', '$2a$10$X/e6L5NO.RO9/SO3yQVJ7eOQZQZHG8JK9.7ZFH5cGjN.Y.8aGQXHi', '孙丽华', 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=200&h=200&fit=crop&crop=face', 2, '主任医师', '北京协和医院', '内分泌科', 'L333444555', 1, 4.8, 20000, 12, '糖尿病、甲状腺疾病、肥胖症', '对糖尿病、甲状腺疾病等内分泌代谢性疾病有丰富的临床经验。', 0.00, 1, NOW(), NOW(), 0),
('DOC009', '13800000009', '$2a$10$X/e6L5NO.RO9/SO3yQVJ7eOQZQZHG8JK9.7ZFH5cGjN.Y.8aGQXHi', '刘德华', 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face', 1, '主治医师', '北京同仁堂中医医院', '中医科', 'L444555666', 1, 4.7, 8000, 20, '体质调理、脾胃虚弱、气血不足、失眠', '中医体质调理专家，擅长运用中医药治疗各种慢性疾病。', 0.00, 1, NOW(), NOW(), 0),
('DOC010', '13800000010', '$2a$10$X/e6L5NO.RO9/SO3yQVJ7eOQZQZHG8JK9.7ZFH5cGjN.Y.8aGQXHi', '陈思远', 'https://images.unsplash.com/photo-1537368910025-700350fe46c7?w=200&h=200&fit=crop&crop=face', 1, '副主任医师', '北京安贞医院', '心血管内科', 'L666777888', 1, 4.9, 18000, 8, '高血压、冠心病、心律失常、心力衰竭', '从事心血管内科临床工作15年，擅长高血压、冠心病等疾病的诊治。', 0.00, 1, NOW(), NOW(), 0);

-- =============================================
-- 医生扩展信息数据 (dm_doctor_ext)
-- 用于存储在线状态、等待时间等动态信息
-- =============================================

INSERT INTO `dm_doctor_ext` (`doctor_id`, `department_code`, `is_online`, `can_prescribe`, `wait_time`, `consult_count`) VALUES
('DOC001', 'dermatology', 1, 1, 240, '7542'),
('DOC002', 'dermatology', 1, 1, 2220, '4.2万'),
('DOC003', 'dermatology', 1, 1, 780, '1.8万'),
('DOC004', 'pediatrics', 1, 1, 900, '2.5万'),
('DOC005', 'psychology', 1, 0, 300, '1.2万'),
('DOC006', 'dermatology', 1, 1, 540, '1.1万'),
('DOC007', 'respiratory', 1, 1, 600, '1.5万'),
('DOC008', 'endocrinology', 1, 1, 720, '2.0万'),
('DOC009', 'tcm', 1, 1, 1200, '8000'),
('DOC010', 'cardiology', 1, 1, 480, '1.8万');
