#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
DrugMall 首页模拟数据初始化脚本
"""

import pymysql
import sys

# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': 'qwer1234',
    'database': 'drugmall',
    'charset': 'utf8mb4'
}

def get_connection():
    return pymysql.connect(**DB_CONFIG)

def init_tables(conn):
    """创建必要的表"""
    with conn.cursor() as cursor:
        # 创建 dm_department_tag 表
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS dm_department_tag (
              id BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
              code VARCHAR(50) NOT NULL COMMENT '标签编码',
              label VARCHAR(100) NOT NULL COMMENT '标签名称',
              sort_order INT DEFAULT 1 COMMENT '排序',
              status TINYINT DEFAULT 1 COMMENT '状态',
              PRIMARY KEY (id),
              KEY idx_code (code)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室标签表'
        """)

        # 创建 dm_department_config 表
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS dm_department_config (
              id BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
              department_code VARCHAR(50) NOT NULL COMMENT '科室编码',
              price DECIMAL(10,2) DEFAULT 0 COMMENT '价格',
              original_price DECIMAL(10,2) DEFAULT 0 COMMENT '原价',
              subsidy DECIMAL(10,2) DEFAULT 0 COMMENT '补贴',
              symptoms TEXT COMMENT '症状描述',
              response_time INT DEFAULT 0 COMMENT '响应时间',
              answer_time INT DEFAULT 0 COMMENT '答题时间',
              example TEXT COMMENT '示例',
              quick_symptoms JSON DEFAULT NULL COMMENT '快捷症状',
              create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
              update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
              PRIMARY KEY (id),
              UNIQUE KEY uk_department_code (department_code)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室配置表'
        """)

        # 创建 dm_payment_method 表
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS dm_payment_method (
              id BIGINT NOT NULL AUTO_INCREMENT COMMENT '支付方式ID',
              code VARCHAR(50) NOT NULL COMMENT '编码',
              name VARCHAR(100) NOT NULL COMMENT '名称',
              description VARCHAR(255) DEFAULT NULL COMMENT '描述',
              icon VARCHAR(100) DEFAULT NULL COMMENT '图标',
              sort_order INT DEFAULT 1 COMMENT '排序',
              status TINYINT DEFAULT 1 COMMENT '状态',
              PRIMARY KEY (id),
              UNIQUE KEY uk_code (code)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付方式表'
        """)

        # 创建 dm_service_shortcut 表
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS dm_service_shortcut (
              id BIGINT NOT NULL AUTO_INCREMENT COMMENT '快捷入口ID',
              name VARCHAR(100) NOT NULL COMMENT '名称',
              subtitle VARCHAR(100) DEFAULT NULL COMMENT '副标题',
              doctor_avatar VARCHAR(500) DEFAULT NULL COMMENT '医生头像',
              sort_order INT DEFAULT 1 COMMENT '排序',
              status TINYINT DEFAULT 1 COMMENT '状态',
              PRIMARY KEY (id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务快捷入口表'
        """)

        # 创建 dm_consultation_step 表
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS dm_consultation_step (
              id BIGINT NOT NULL AUTO_INCREMENT COMMENT '步骤ID',
              step INT NOT NULL COMMENT '步骤序号',
              name VARCHAR(100) NOT NULL COMMENT '步骤名称',
              sort_order INT DEFAULT 1 COMMENT '排序',
              status TINYINT DEFAULT 1 COMMENT '状态',
              PRIMARY KEY (id),
              KEY idx_step (step)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问诊流程步骤表'
        """)

        # 创建 dm_doctor_ext 表
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS dm_doctor_ext (
              id BIGINT NOT NULL AUTO_INCREMENT COMMENT '扩展ID',
              doctor_id VARCHAR(20) NOT NULL COMMENT '医生ID',
              department_code VARCHAR(50) DEFAULT NULL COMMENT '科室编码',
              is_online TINYINT DEFAULT 0 COMMENT '是否在线',
              can_prescribe TINYINT DEFAULT 0 COMMENT '是否可开方',
              wait_time INT DEFAULT 0 COMMENT '等待时间',
              consult_count VARCHAR(20) DEFAULT '0' COMMENT '接诊量',
              price_text VARCHAR(100) DEFAULT NULL COMMENT '价格文本',
              create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
              update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
              PRIMARY KEY (id),
              UNIQUE KEY uk_doctor_id (doctor_id),
              KEY idx_department_code (department_code),
              KEY idx_is_online (is_online)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生扩展信息表'
        """)

    conn.commit()
    print("[OK] 表创建完成")

def clear_data(conn):
    """清空旧数据"""
    with conn.cursor() as cursor:
        cursor.execute("DELETE FROM dm_doctor_ext")
        cursor.execute("DELETE FROM dm_doctor")
        cursor.execute("DELETE FROM dm_department_tag")
        cursor.execute("DELETE FROM dm_department_config")
        cursor.execute("DELETE FROM dm_department")
        cursor.execute("DELETE FROM dm_dict_data WHERE dict_type IN ('doctor_title', 'hospital_level', 'doctor_sort', 'doctor_specialty', 'doctor_feature', 'disease_tag', 'relationship', 'gender', 'service_type')")
        cursor.execute("DELETE FROM dm_dict_type WHERE dict_type IN ('doctor_title', 'hospital_level', 'doctor_sort', 'doctor_specialty', 'doctor_feature', 'disease_tag', 'relationship', 'gender', 'service_type', 'payment_method')")
        cursor.execute("DELETE FROM dm_payment_method")
        cursor.execute("DELETE FROM dm_service_shortcut")
        cursor.execute("DELETE FROM dm_consultation_step")
    conn.commit()
    print("[OK] 旧数据清空完成")

def insert_departments(conn):
    """插入科室数据"""
    departments = [
        ('bone', '骨科', 'bone', '', 'info', 0, 1),
        ('neurology', '神经内科', 'brain', '', 'info', 0, 2),
        ('general', '全科', 'firstAid', '', 'info', 0, 3),
        ('tcm', '中医科', 'herb', '', 'info', 0, 4),
        ('surgery', '普外科', 'scissor', '', 'info', 0, 5),
        ('andrology', '男科门诊', 'male', '', 'info', 0, 6),
        ('cardiology', '心血管内科', 'heart', '', 'info', 0, 7),
        ('endocrine', '内分泌科', 'stomach', '', 'info', 0, 8),
        ('tcm-spleen', '中医脾胃病', 'herb', '', 'info', 0, 9),
        ('tcm-male', '中医男科', 'male', '补肾', 'supplement', 0, 10),
        ('tcm-sleep', '中医失眠科', 'moon', '', 'info', 0, 11),
        ('tcm-female', '中医妇科', 'female', '', 'info', 0, 12),
        ('weight', '减重门诊', 'scale', '', 'info', 0, 13),
        ('sleep', '睡眠中心', 'moon', '9.9元起', 'price', 0, 14),
        ('dermatology', '皮肤科', 'skin', '瘙痒', 'hot', 0, 15),
        ('respiratory', '呼吸内科', 'lung', '', 'info', 0, 16),
        ('pediatrics', '儿科', 'child', '发热', 'fever', 0, 17),
        ('gastroenterology', '消化内科', 'stomach', '', 'info', 0, 18),
        ('gynecology', '妇产科', 'female', '', 'info', 0, 19),
        ('ent', '耳鼻喉科', 'ear', '', 'info', 0, 20),
        ('urology', '泌尿外科', 'kidney', '', 'info', 0, 21),
        ('dental', '口腔科', 'tooth', '', 'info', 0, 22),
        ('ophthalmology', '眼科', 'eye', '', 'info', 0, 23),
        ('psychology', '心理咨询', 'brain', '19.9元', 'price', 0, 24),
    ]

    with conn.cursor() as cursor:
        cursor.executemany(
            "INSERT INTO dm_department (code, name, icon, tag, tag_type, parent_id, sort) VALUES (%s, %s, %s, %s, %s, %s, %s)",
            departments
        )
    conn.commit()
    print(f"[OK] 科室数据: {len(departments)} 条")

def insert_department_config(conn):
    """插入科室配置数据"""
    configs = [
        ('bone', 4.9, 19.9, 15.0, '关节炎、颈椎病、腰椎间盘突出、腰肌劳损、肩周炎、骨折后康复、骨质增生、腱鞘炎、骨质疏松、运动损伤、检查单解读', 9, 30, '腰痛伴随腿发麻，1周，用膏药外贴，效果不明显。', '["颈椎病","腰痛","腰椎间盘突出","关节炎","腰肌劳损","关节痛","关节扭伤","外伤","肩周炎","骨折"]'),
        ('neurology', 7.9, 19.9, 12.0, '失眠、头痛、头晕、焦虑、抑郁状态、神经痛、脑血管病、帕金森病、认知功能障碍、面肌痉挛、面神经炎、检查单解读', 9, 30, '失眠，1个月，未使用过药物，未线下就诊。', '["头痛头晕","失眠","易醒","偏头痛","焦虑障碍","多梦","眩晕","周围神经病","头晕目眩","记忆力减退"]'),
        ('dermatology', 19.9, 39.9, 20.0, '湿疹、荨麻疹、痤疮、银屑病，白癜风、皮炎、皮肤瘙痒、脱发、色斑、痘痘、过敏', 5, 15, '面部起红疹，瘙痒3天，未使用过药物。', '["湿疹","荨麻疹","痤疮","皮肤瘙痒","过敏","脱发","痘痘","色斑","皮炎","银屑病"]'),
        ('respiratory', 9.9, 29.9, 20.0, '咳嗽、咳痰、气喘、胸闷、胸痛、发热、感冒、支气管炎、肺炎、哮喘、慢阻肺', 8, 20, '半夜咽痒咳嗽，持续1周，喝水可缓解，未用过药。', '["咳嗽","咳痰","气喘","胸闷","发热","感冒","咽痛","流鼻涕","打喷嚏","胸痛"]'),
        ('pediatrics', 9.9, 29.9, 20.0, '小儿发热、咳嗽、腹泻、呕吐、湿疹、厌食、夜啼、多动症、发育迟缓、疫苗接种咨询', 6, 15, '宝宝发热38.5度，持续2天，精神尚可，未用药。', '["发热","咳嗽","腹泻","呕吐","湿疹","厌食","夜啼","流鼻涕","皮疹","腹痛"]'),
        ('gynecology', 19.9, 49.9, 30.0, '月经不调、痛经、白带异常、阴道炎、盆腔炎、子宫肌瘤、卵巢囊肿、备孕咨询、孕期检查', 10, 25, '月经推迟10天，伴有腹痛，未做过检查。', '["月经不调","痛经","白带异常","腹痛","备孕咨询","孕期检查","阴道炎","盆腔炎","子宫肌瘤","卵巢囊肿"]'),
        ('gastroenterology', 9.9, 29.9, 20.0, '胃痛、胃胀、反酸、恶心、呕吐、腹泻、便秘、消化不良、胃炎、胃溃疡、肠炎', 8, 20, '胃痛伴反酸，持续3天，饭后加重，未用药。', '["胃痛","胃胀","反酸","恶心","腹泻","便秘","消化不良","呕吐","腹胀","食欲差"]'),
        ('psychology', 19.9, 199.0, 179.0, '焦虑、抑郁、失眠、情绪低落、压力大、人际关系困扰、职场压力、婚姻家庭问题、自我成长', 5, 10, '最近情绪低落，失眠2周，工作压力大。', '["焦虑","抑郁","失眠","情绪低落","压力大","人际关系","职场压力","婚姻问题","自我成长","注意力不集中"]'),
        ('tcm', 14.9, 39.9, 25.0, '体质调理、脾胃虚弱、气血不足、失眠多梦、腰膝酸软、月经不调、慢性疲劳、亚健康调理', 12, 30, '脾胃虚弱，食欲不振，乏力2个月，未系统调理。', '["脾胃虚弱","气血不足","失眠多梦","腰膝酸软","乏力","食欲不振","便秘","怕冷","盗汗","口干"]'),
        ('general', 4.9, 19.9, 15.0, '常见病、多发病、慢性病管理、健康咨询、体检报告解读、用药咨询、疫苗接种', 5, 15, '体检发现血压偏高，140/90，无症状，未用药。', '["高血压","高血糖","高血脂","体检咨询","用药咨询","健康管理","疫苗接种","慢性病","亚健康","疲劳"]'),
    ]

    with conn.cursor() as cursor:
        cursor.executemany(
            "INSERT INTO dm_department_config (department_code, price, original_price, subsidy, symptoms, response_time, answer_time, example, quick_symptoms) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)",
            configs
        )
    conn.commit()
    print(f"[OK] 科室配置: {len(configs)} 条")

def insert_department_tags(conn):
    """插入科室标签数据"""
    tags = [
        ('all', '全部', 1, 1),
        ('dermatology', '皮肤科', 2, 1),
        ('respiratory', '呼吸内科', 3, 1),
        ('pediatrics', '儿科', 4, 1),
        ('gastroenterology', '消化内科', 5, 1),
    ]

    with conn.cursor() as cursor:
        cursor.executemany(
            "INSERT INTO dm_department_tag (code, label, sort_order, status) VALUES (%s, %s, %s, %s)",
            tags
        )
    conn.commit()
    print(f"[OK] 科室标签: {len(tags)} 条")

def insert_dict_types(conn):
    """插入字典类型数据"""
    types = [
        ('doctor_title', '医生职称', '医生职称字典', 1),
        ('hospital_level', '医院等级', '医院等级字典', 1),
        ('doctor_sort', '医生排序', '医生列表排序选项', 1),
        ('doctor_specialty', '医生擅长', '医生擅长领域选项', 1),
        ('doctor_feature', '医生特色', '医生特色服务选项', 1),
        ('payment_method', '支付方式', '支付方式字典', 1),
        ('disease_tag', '疾病标签', '处方申请疾病标签', 1),
        ('relationship', '患者关系', '患者与用户关系', 1),
        ('gender', '性别', '性别选项', 1),
        ('service_type', '服务类型', '问诊服务类型', 1),
    ]

    with conn.cursor() as cursor:
        cursor.executemany(
            "INSERT INTO dm_dict_type (dict_type, dict_name, remark, status) VALUES (%s, %s, %s, %s)",
            types
        )
    conn.commit()
    print(f"[OK] 字典类型: {len(types)} 条")

def insert_dict_data(conn):
    """插入字典数据"""
    data = [
        # doctor_title
        ('doctor_title', '主任医师', 'chief', 1, 0),
        ('doctor_title', '副主任医师', 'associate', 2, 0),
        ('doctor_title', '主治医师', 'attending', 3, 0),
        ('doctor_title', '住院医师', 'resident', 4, 0),
        # hospital_level
        ('hospital_level', '全部', 'all', 1, 1),
        ('hospital_level', '三甲医院', '3a', 2, 0),
        ('hospital_level', '二甲医院', '2a', 3, 0),
        ('hospital_level', '一甲医院', '1a', 4, 0),
        # doctor_sort
        ('doctor_sort', '综合排序', 'default', 1, 0),
        ('doctor_sort', '好评优先', 'rating', 2, 0),
        ('doctor_sort', '接诊量优先', 'consult', 3, 0),
        ('doctor_sort', '价格从低到高', 'price', 4, 0),
        # doctor_specialty
        ('doctor_specialty', '全部擅长', 'all', 1, 0),
        ('doctor_specialty', '过敏性疾病', 'allergy', 2, 0),
        ('doctor_specialty', '皮肤疾病', 'skin', 3, 0),
        ('doctor_specialty', '中医调理', 'tcm', 4, 0),
        ('doctor_specialty', '慢性病管理', 'chronic', 5, 0),
        ('doctor_specialty', '儿科疾病', 'child', 6, 0),
        ('doctor_specialty', '心理咨询', 'psychology', 7, 0),
        # doctor_feature
        ('doctor_feature', '可开处方', 'prescription', 1, 0),
        ('doctor_feature', '在线医生', 'online', 2, 0),
        ('doctor_feature', '秒问医生', 'quick', 3, 0),
        ('doctor_feature', '低价咨询', 'cheap', 4, 0),
        # disease_tag
        ('disease_tag', '感冒发热', '1', 1, 0),
        ('disease_tag', '咳嗽咽痛', '2', 2, 0),
        ('disease_tag', '头痛头晕', '3', 3, 0),
        ('disease_tag', '消化不良', '4', 4, 0),
        ('disease_tag', '腹泻腹痛', '5', 5, 0),
        ('disease_tag', '皮肤过敏', '6', 6, 0),
        ('disease_tag', '失眠多梦', '7', 7, 0),
        ('disease_tag', '高血压', '8', 8, 0),
        ('disease_tag', '糖尿病', '9', 9, 0),
        ('disease_tag', '冠心病', '10', 10, 0),
        # relationship
        ('relationship', '本人', 'self', 1, 0),
        ('relationship', '父母', 'parent', 2, 0),
        ('relationship', '子女', 'child', 3, 0),
        ('relationship', '配偶', 'spouse', 4, 0),
        ('relationship', '其他', 'other', 5, 0),
        # gender
        ('gender', '男', 'male', 1, 0),
        ('gender', '女', 'female', 2, 0),
        # service_type
        ('service_type', '图文问诊', 'text', 1, 0),
        ('service_type', '电话问诊', 'phone', 2, 0),
        ('service_type', '视频问诊', 'video', 3, 0),
    ]

    with conn.cursor() as cursor:
        cursor.executemany(
            "INSERT INTO dm_dict_data (dict_type, dict_label, dict_value, dict_sort, is_default) VALUES (%s, %s, %s, %s, %s)",
            data
        )
    conn.commit()
    print(f"[OK] 字典数据: {len(data)} 条")

def insert_payment_methods(conn):
    """插入支付方式数据"""
    methods = [
        ('wechat', '微信支付', '推荐使用', 'wechat', 1, 1),
        ('alipay', '支付宝', '', 'alipay', 2, 1),
        ('balance', '余额支付', '', 'balance', 3, 1),
        ('bankcard', '银行卡', '支持储蓄卡/信用卡', 'bankcard', 4, 1),
    ]

    with conn.cursor() as cursor:
        cursor.executemany(
            "INSERT INTO dm_payment_method (code, name, description, icon, sort_order, status) VALUES (%s, %s, %s, %s, %s, %s)",
            methods
        )
    conn.commit()
    print(f"[OK] 支付方式: {len(methods)} 条")

def insert_service_shortcuts(conn):
    """插入服务快捷入口数据"""
    shortcuts = [
        ('用药咨询', '安全用药', 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=100&h=100&fit=crop&crop=face', 1, 1),
        ('抓中药', '养生茶饮', 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=100&h=100&fit=crop&crop=face', 2, 1),
        ('心理咨询', '19.9元', 'https://images.unsplash.com/photo-1594824476967-48c8b964273f?w=100&h=100&fit=crop&crop=face', 3, 1),
        ('电话医生', '9.9元起', 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=100&h=100&fit=crop&crop=face', 4, 1),
    ]

    with conn.cursor() as cursor:
        cursor.executemany(
            "INSERT INTO dm_service_shortcut (name, subtitle, doctor_avatar, sort_order, status) VALUES (%s, %s, %s, %s, %s)",
            shortcuts
        )
    conn.commit()
    print(f"[OK] 服务快捷入口: {len(shortcuts)} 条")

def insert_consultation_steps(conn):
    """插入问诊流程步骤数据"""
    steps = [
        (1, '导诊助手', 1, 1),
        (2, '支付诊费', 2, 1),
        (3, '医生接诊', 3, 1),
        (4, '问诊咨询', 4, 1),
    ]

    with conn.cursor() as cursor:
        cursor.executemany(
            "INSERT INTO dm_consultation_step (step, name, sort_order, status) VALUES (%s, %s, %s, %s)",
            steps
        )
    conn.commit()
    print(f"[OK] 问诊流程步骤: {len(steps)} 条")

def insert_doctors(conn):
    """插入医生数据"""
    # 密码是 "123456" 的 BCrypt 哈希
    password_hash = '$2a$10$X/e6L5NO.RO9/SO3yQVJ7eOQZQZHG8JK9.7ZFH5cGjN.Y.8aGQXHi'

    doctors = [
        ('DOC001', '13800000001', password_hash, '周峰', 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face', 1, '主治医师', '武汉市黄陂区人民医院', '皮肤科', 'L123456789', 1, 4.9, 7542, 4, '过敏性疾病（特应性皮炎、湿疹、荨麻疹）、自身免疫性疱病、药疹、结缔组织病及血管炎', '从事皮肤科临床工作10余年，擅长过敏性皮肤病的诊治，对湿疹、荨麻疹等常见病有丰富的经验。', 0.00, 1),
        ('DOC002', '13800000002', password_hash, '陈琼', 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=200&h=200&fit=crop&crop=face', 2, '主治医师', '辽宁中医药大学附属第二医院', '皮肤科', 'L987654321', 1, 4.8, 42000, 37, '痤疮、湿疹、银屑病、白癜风、中医调理皮肤', '女，就职于辽宁中医药大学附属第二医院，主治医师，硕士研究生。对中医药治疗痤疮、湿疹有深入研究。', 0.00, 1),
        ('DOC003', '13800000003', password_hash, '李贤光', 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=200&h=200&fit=crop&crop=face', 1, '主任医师', '昆明医科大学第一附属医院', '皮肤科', 'L456789123', 1, 4.9, 18000, 13, '湿疹、荨麻疹、痤疮、银屑病，白癜风', '擅长湿疹、荨麻疹、痤疮、银屑病、白癜风等常见皮肤病的诊治', 0.00, 1),
        ('DOC004', '13800000004', password_hash, '张晓明', 'https://images.unsplash.com/photo-1537368910025-700350fe46c7?w=200&h=200&fit=crop&crop=face', 1, '副主任医师', '首都儿科研究所', '儿科', 'L111222333', 1, 4.9, 25000, 15, '小儿发热、呼吸道感染、消化系统疾病', '从事儿科临床工作20余年，对儿童常见病、多发病有丰富的诊疗经验。', 0.00, 1),
        ('DOC005', '13800000005', password_hash, '王雪梅', 'https://images.unsplash.com/photo-1594824476967-48c8b964273f?w=200&h=200&fit=crop&crop=face', 2, '主任医师', '北京大学第六医院', '心理咨询', 'L999888777', 1, 4.8, 12000, 5, '焦虑症、抑郁症、睡眠障碍、情绪管理', '焦虑症、抑郁症、睡眠障碍、情绪管理专家', 0.00, 1),
        ('DOC006', '13800000006', password_hash, '刘贞君', 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face', 1, '主治医师', '山东青岛中西医结合医院', '皮肤科', 'L555666777', 1, 4.9, 11000, 9, '中西医结合诊疗银屑病，痤疮，湿疹，荨麻疹', '中西医结合诊疗银屑病，痤疮，湿疹，荨麻疹等常见皮肤病', 0.00, 1),
        ('DOC007', '13800000007', password_hash, '赵文博', 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=200&h=200&fit=crop&crop=face', 1, '副主任医师', '北京协和医院', '呼吸内科', 'L222333444', 1, 4.9, 15000, 10, '慢性咳嗽、哮喘、慢阻肺、肺部感染', '擅长慢性咳嗽、哮喘、慢阻肺、肺部感染等呼吸系统疾病的诊治', 0.00, 1),
        ('DOC008', '13800000008', password_hash, '孙丽华', 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=200&h=200&fit=crop&crop=face', 2, '主任医师', '北京协和医院', '内分泌科', 'L333444555', 1, 4.8, 20000, 12, '糖尿病、甲状腺疾病、肥胖症', '对糖尿病、甲状腺疾病等内分泌代谢性疾病有丰富的临床经验。', 0.00, 1),
        ('DOC009', '13800000009', password_hash, '刘德华', 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face', 1, '主治医师', '北京同仁堂中医医院', '中医科', 'L444555666', 1, 4.7, 8000, 20, '体质调理、脾胃虚弱、气血不足、失眠', '中医体质调理专家，擅长运用中医药治疗各种慢性疾病。', 0.00, 1),
        ('DOC010', '13800000010', password_hash, '陈思远', 'https://images.unsplash.com/photo-1537368910025-700350fe46c7?w=200&h=200&fit=crop&crop=face', 1, '副主任医师', '北京安贞医院', '心血管内科', 'L666777888', 1, 4.9, 18000, 8, '高血压、冠心病、心律失常、心力衰竭', '从事心血管内科临床工作15年，擅长高血压、冠心病等疾病的诊治。', 0.00, 1),
    ]

    with conn.cursor() as cursor:
        cursor.executemany(
            """INSERT INTO dm_doctor (id, phone, password, name, avatar, gender, title, hospital, department, license_no, is_certified, rating, service_count, response_time, specialties, introduction, balance, status, create_time, update_time, is_deleted)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW(), NOW(), 0)""",
            doctors
        )
    conn.commit()
    print(f"[OK] 医生数据: {len(doctors)} 条")

def insert_doctor_ext(conn):
    """插入医生扩展信息数据"""
    ext_data = [
        ('DOC001', 'dermatology', 1, 1, 240, '7542'),
        ('DOC002', 'dermatology', 1, 1, 2220, '4.2万'),
        ('DOC003', 'dermatology', 1, 1, 780, '1.8万'),
        ('DOC004', 'pediatrics', 1, 1, 900, '2.5万'),
        ('DOC005', 'psychology', 1, 0, 300, '1.2万'),
        ('DOC006', 'dermatology', 1, 1, 540, '1.1万'),
        ('DOC007', 'respiratory', 1, 1, 600, '1.5万'),
        ('DOC008', 'endocrinology', 1, 1, 720, '2.0万'),
        ('DOC009', 'tcm', 1, 1, 1200, '8000'),
        ('DOC010', 'cardiology', 1, 1, 480, '1.8万'),
    ]

    with conn.cursor() as cursor:
        cursor.executemany(
            "INSERT INTO dm_doctor_ext (doctor_id, department_code, is_online, can_prescribe, wait_time, consult_count) VALUES (%s, %s, %s, %s, %s, %s)",
            ext_data
        )
    conn.commit()
    print(f"[OK] 医生扩展信息: {len(ext_data)} 条")

def main():
    print("=" * 50)
    print("DrugMall 首页模拟数据初始化")
    print("=" * 50)

    try:
        conn = get_connection()
        print("[OK] 数据库连接成功")

        init_tables(conn)
        clear_data(conn)

        insert_departments(conn)
        insert_department_config(conn)
        insert_department_tags(conn)
        insert_dict_types(conn)
        insert_dict_data(conn)
        insert_payment_methods(conn)
        insert_service_shortcuts(conn)
        insert_consultation_steps(conn)
        insert_doctors(conn)
        insert_doctor_ext(conn)

        conn.close()

        print("=" * 50)
        print("数据初始化完成!")
        print("=" * 50)

    except Exception as e:
        print(f"[ERROR] {e}")
        sys.exit(1)

if __name__ == '__main__':
    main()
