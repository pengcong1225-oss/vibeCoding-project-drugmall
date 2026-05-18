#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
DrugMall 完整数据初始化脚本
"""

import pymysql
import json
import sys

DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': 'qwer1234',
    'database': 'drugmall',
    'charset': 'utf8mb4'
}

def get_connection():
    return pymysql.connect(**DB_CONFIG)

def clear_all_data(conn):
    """清空所有业务数据"""
    with conn.cursor() as cursor:
        cursor.execute("DELETE FROM dm_product")
        cursor.execute("DELETE FROM dm_category")
        cursor.execute("DELETE FROM dm_store")
        cursor.execute("DELETE FROM dm_store_tag")
        cursor.execute("DELETE FROM dm_store_promise")
        cursor.execute("DELETE FROM dm_store_review")
        cursor.execute("DELETE FROM dm_home_kingkong")
        cursor.execute("DELETE FROM dm_home_section")
        cursor.execute("DELETE FROM dm_home_tab")
        cursor.execute("DELETE FROM dm_banner")
        cursor.execute("DELETE FROM dm_brand")
        cursor.execute("DELETE FROM dm_home_ad_slot")
        cursor.execute("DELETE FROM dm_department_config")
        cursor.execute("DELETE FROM dm_department_tag")
        cursor.execute("DELETE FROM dm_department")
        cursor.execute("DELETE FROM dm_dict_data WHERE dict_type IN ('doctor_title', 'hospital_level', 'doctor_sort', 'doctor_specialty', 'doctor_feature', 'disease_tag', 'relationship', 'gender', 'service_type')")
        cursor.execute("DELETE FROM dm_dict_type WHERE dict_type IN ('doctor_title', 'hospital_level', 'doctor_sort', 'doctor_specialty', 'doctor_feature', 'disease_tag', 'relationship', 'gender', 'service_type')")
        cursor.execute("DELETE FROM dm_doctor_ext")
        cursor.execute("DELETE FROM dm_doctor")
        cursor.execute("DELETE FROM dm_payment_method")
        cursor.execute("DELETE FROM dm_service_shortcut")
        cursor.execute("DELETE FROM dm_consultation_step")
    conn.commit()
    print("[OK] 数据清空完成")

def init_departments(conn):
    departments = [
        ('bone', '骨科', 'bone', '', 'info', 0, 1),
        ('neurology', '神经内科', 'brain', '', 'info', 0, 2),
        ('general', '全科', 'firstAid', '', 'info', 0, 3),
        ('tcm', '中医科', 'herb', '', 'info', 0, 4),
        ('surgery', '普外科', 'scissor', '', 'info', 0, 5),
        ('andrology', '男科门诊', 'male', '', 'info', 0, 6),
        ('cardiology', '心血管内科', 'heart', '', 'info', 0, 7),
        ('endocrine', '内分泌科', 'stomach', '', 'info', 0, 8),
        ('dermatology', '皮肤科', 'skin', '瘙痒', 'hot', 0, 9),
        ('respiratory', '呼吸内科', 'lung', '', 'info', 0, 10),
        ('pediatrics', '儿科', 'child', '发热', 'fever', 0, 11),
        ('gynecology', '妇产科', 'female', '', 'info', 0, 12),
        ('gastroenterology', '消化内科', 'stomach', '', 'info', 0, 13),
        ('ent', '耳鼻喉科', 'ear', '', 'info', 0, 14),
        ('urology', '泌尿外科', 'kidney', '', 'info', 0, 15),
        ('psychology', '心理咨询', 'brain', '19.9元', 'price', 0, 16),
        ('tcm-spleen', '中医脾胃病', 'herb', '', 'info', 0, 17),
        ('tcm-male', '中医男科', 'male', '补肾', 'supplement', 0, 18),
        ('tcm-sleep', '中医失眠科', 'moon', '', 'info', 0, 19),
        ('tcm-female', '中医妇科', 'female', '', 'info', 0, 20),
    ]
    with conn.cursor() as cursor:
        cursor.executemany(
            "INSERT INTO dm_department (code, name, icon, tag, tag_type, parent_id, sort) VALUES (%s, %s, %s, %s, %s, %s, %s)",
            departments
        )
    conn.commit()
    print(f"[OK] 科室数据: {len(departments)} 条")

def init_department_config(conn):
    configs = [
        ('bone', 4.9, 19.9, 15.0, '关节炎、颈椎病、腰椎间盘突出、肩周炎', 9, 30, '腰痛1周', '["颈椎病","腰痛","肩周炎"]'),
        ('neurology', 7.9, 19.9, 12.0, '失眠、头痛、头晕、焦虑', 9, 30, '失眠1个月', '["失眠","头痛","焦虑"]'),
        ('general', 4.9, 19.9, 15.0, '常见病、多发病、健康咨询', 5, 15, '血压偏高', '["高血压","高血糖","体检"]'),
        ('tcm', 14.9, 39.9, 25.0, '体质调理、脾胃虚弱、气血不足', 12, 30, '胃口不好', '["脾胃虚弱","气血不足","失眠"]'),
        ('dermatology', 19.9, 39.9, 20.0, '湿疹、荨麻疹、痤疮、皮肤瘙痒', 5, 15, '面部红疹', '["湿疹","荨麻疹","痤疮"]'),
        ('respiratory', 9.9, 29.9, 20.0, '咳嗽、感冒、支气管炎、肺炎', 8, 20, '咳嗽1周', '["咳嗽","感冒","咽痛"]'),
        ('pediatrics', 9.9, 29.9, 20.0, '小儿发热、咳嗽、腹泻', 6, 15, '宝宝发热', '["发热","咳嗽","腹泻"]'),
        ('gynecology', 19.9, 49.9, 30.0, '月经不调、痛经、阴道炎', 10, 25, '月经推迟', '["月经不调","痛经","白带异常"]'),
        ('gastroenterology', 9.9, 29.9, 20.0, '胃痛、胃胀、腹泻、便秘', 8, 20, '胃痛3天', '["胃痛","胃胀","腹泻"]'),
        ('psychology', 19.9, 199.0, 179.0, '焦虑、抑郁、失眠、情绪低落', 5, 10, '情绪低落', '["焦虑","抑郁","失眠"]'),
    ]
    with conn.cursor() as cursor:
        cursor.executemany(
            "INSERT INTO dm_department_config (department_code, price, original_price, subsidy, symptoms, response_time, answer_time, example, quick_symptoms) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)",
            configs
        )
    conn.commit()
    print(f"[OK] 科室配置: {len(configs)} 条")

def init_department_tags(conn):
    tags = [
        ('all', '全部', 1, 1),
        ('dermatology', '皮肤科', 2, 1),
        ('respiratory', '呼吸内科', 3, 1),
        ('pediatrics', '儿科', 4, 1),
        ('psychology', '心理咨询', 5, 1),
    ]
    with conn.cursor() as cursor:
        cursor.executemany(
            "INSERT INTO dm_department_tag (code, label, sort_order, status) VALUES (%s, %s, %s, %s)",
            tags
        )
    conn.commit()
    print(f"[OK] 科室标签: {len(tags)} 条")

def init_dict_data(conn):
    dict_types = [
        ('doctor_title', '医生职称', '医生职称字典', 1),
        ('hospital_level', '医院等级', '医院等级字典', 1),
        ('doctor_sort', '医生排序', '医生列表排序选项', 1),
        ('doctor_specialty', '医生擅长', '医生擅长领域选项', 1),
        ('service_type', '服务类型', '问诊服务类型', 1),
    ]
    with conn.cursor() as cursor:
        cursor.executemany(
            "INSERT INTO dm_dict_type (dict_type, dict_name, remark, status) VALUES (%s, %s, %s, %s)",
            dict_types
        )

    dict_data = [
        ('doctor_title', '主任医师', 'chief', 1, 0),
        ('doctor_title', '副主任医师', 'associate', 2, 0),
        ('doctor_title', '主治医师', 'attending', 3, 0),
        ('hospital_level', '全部', 'all', 1, 1),
        ('hospital_level', '三甲医院', '3a', 2, 0),
        ('hospital_level', '二甲医院', '2a', 3, 0),
        ('doctor_sort', '综合排序', 'default', 1, 0),
        ('doctor_sort', '好评优先', 'rating', 2, 0),
        ('service_type', '图文问诊', 'text', 1, 0),
        ('service_type', '电话问诊', 'phone', 2, 0),
    ]
    with conn.cursor() as cursor:
        cursor.executemany(
            "INSERT INTO dm_dict_data (dict_type, dict_label, dict_value, dict_sort, is_default) VALUES (%s, %s, %s, %s, %s)",
            dict_data
        )
    conn.commit()
    print(f"[OK] 字典数据: {len(dict_data)} 条")

def init_doctors(conn):
    password_hash = '$2a$10$X/e6L5NO.RO9/SO3yQVJ7eOQZQZHG8JK9.7ZFH5cGjN.Y.8aGQXHi'
    doctors = [
        ('DOC001', '13800000001', password_hash, '周峰', 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face', 1, '主治医师', '武汉市黄陂区人民医院', '皮肤科', 'L123456789', 1, 4.9, 7542, 4, '过敏性皮肤病', '皮肤科专家', 0.00, 1),
        ('DOC002', '13800000002', password_hash, '陈琼', 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=200&h=200&fit=crop&crop=face', 2, '主治医师', '辽宁中医药大学附属第二医院', '皮肤科', 'L987654321', 1, 4.8, 42000, 37, '痤疮湿疹', '中医皮肤科', 0.00, 1),
        ('DOC003', '13800000003', password_hash, '李贤光', 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=200&h=200&fit=crop&crop=face', 1, '主任医师', '昆明医科大学第一附属医院', '皮肤科', 'L456789123', 1, 4.9, 18000, 13, '湿疹荨麻疹', '皮肤科主任', 0.00, 1),
        ('DOC004', '13800000004', password_hash, '张晓明', 'https://images.unsplash.com/photo-1537368910025-700350fe46c7?w=200&h=200&fit=crop&crop=face', 1, '副主任医师', '首都儿科研究所', '儿科', 'L111222333', 1, 4.9, 25000, 15, '小儿发热', '儿科专家', 0.00, 1),
        ('DOC005', '13800000005', password_hash, '王雪梅', 'https://images.unsplash.com/photo-1594824476967-48c8b964273f?w=200&h=200&fit=crop&crop=face', 2, '主任医师', '北京大学第六医院', '心理咨询', 'L999888777', 1, 4.8, 12000, 5, '焦虑抑郁', '心理专家', 0.00, 1),
        ('DOC006', '13800000006', password_hash, '刘贞君', 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face', 1, '主治医师', '山东青岛中西医结合医院', '皮肤科', 'L555666777', 1, 4.9, 11000, 9, '银屑病痤疮', '中西医皮肤科', 0.00, 1),
        ('DOC007', '13800000007', password_hash, '赵文博', 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=200&h=200&fit=crop&crop=face', 1, '副主任医师', '北京协和医院', '呼吸内科', 'L222333444', 1, 4.9, 15000, 10, '咳嗽哮喘', '呼吸科专家', 0.00, 1),
        ('DOC008', '13800000008', password_hash, '孙丽华', 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=200&h=200&fit=crop&crop=face', 2, '主任医师', '北京协和医院', '内分泌科', 'L333444555', 1, 4.8, 20000, 12, '糖尿病甲状腺', '内分泌科主任', 0.00, 1),
        ('DOC009', '13800000009', password_hash, '刘德华', 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face', 1, '主治医师', '北京同仁堂中医医院', '中医科', 'L444555666', 1, 4.7, 8000, 20, '体质调理', '中医专家', 0.00, 1),
        ('DOC010', '13800000010', password_hash, '陈思远', 'https://images.unsplash.com/photo-1537368910025-700350fe46c7?w=200&h=200&fit=crop&crop=face', 1, '副主任医师', '北京安贞医院', '心血管内科', 'L666777888', 1, 4.9, 18000, 8, '高血压冠心病', '心内科专家', 0.00, 1),
    ]
    with conn.cursor() as cursor:
        cursor.executemany(
            """INSERT INTO dm_doctor (id, phone, password, name, avatar, gender, title, hospital, department, license_no, is_certified, rating, service_count, response_time, specialties, introduction, balance, status, create_time, update_time, is_deleted)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW(), NOW(), 0)""",
            doctors
        )
    conn.commit()
    print(f"[OK] 医生数据: {len(doctors)} 条")

def init_doctor_ext(conn):
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

def init_payment_and_shortcuts(conn):
    methods = [
        ('wechat', '微信支付', '推荐使用', 'wechat', 1, 1),
        ('alipay', '支付宝', '', 'alipay', 2, 1),
        ('balance', '余额支付', '', 'balance', 3, 1),
        ('bankcard', '银行卡', '', 'bankcard', 4, 1),
    ]
    with conn.cursor() as cursor:
        cursor.executemany(
            "INSERT INTO dm_payment_method (code, name, description, icon, sort_order, status) VALUES (%s, %s, %s, %s, %s, %s)",
            methods
        )

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
    print(f"[OK] 支付方式: {len(methods)} 条, 快捷入口: {len(shortcuts)} 条, 问诊步骤: {len(steps)} 条")

def init_categories(conn):
    categories = [
        (1, '感冒用药', 'cold', 0, 1, None, 'https://img.drugmall.com/category/cold.png', 'drug', 1),
        (2, '消化系统', 'digestive', 0, 2, None, 'https://img.drugmall.com/category/digestive.png', 'drug', 2),
        (3, '皮肤用药', 'skin', 0, 3, None, 'https://img.drugmall.com/category/skin.png', 'drug', 3),
        (4, '儿童用药', 'children', 0, 4, None, 'https://img.drugmall.com/category/children.png', 'drug', 4),
        (5, '妇科用药', 'gynecology', 0, 5, None, 'https://img.drugmall.com/category/gynecology.png', 'drug', 5),
        (6, '男科用药', 'andrology', 0, 6, None, 'https://img.drugmall.com/category/andrology.png', 'drug', 6),
        (7, '心血管用药', 'cardiovascular', 0, 7, None, 'https://img.drugmall.com/category/cardiovascular.png', 'drug', 7),
        (8, '内分泌用药', 'endocrine', 0, 8, None, 'https://img.drugmall.com/category/endocrine.png', 'drug', 8),
        (9, '维生素矿物质', 'vitamins', 0, 9, None, 'https://img.drugmall.com/category/vitamins.png', 'drug', 9),
        (10, '中药滋补', 'tcm', 0, 10, None, 'https://img.drugmall.com/category/tcm.png', 'drug', 10),
        (11, '医疗器械', 'medical', 0, 11, None, 'https://img.drugmall.com/category/medical.png', 'drug', 11),
        (12, '处方药', 'prescription', 0, 12, None, 'https://img.drugmall.com/category/prescription.png', 'drug', 12),
    ]
    with conn.cursor() as cursor:
        cursor.executemany(
            "INSERT INTO dm_category (id, name, code, parent_id, level, icon, image, type, sort) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)",
            categories
        )
    conn.commit()
    print(f"[OK] 商品分类: {len(categories)} 条")
    return [c[0] for c in categories]

def init_products(conn, category_ids):
    products = [
        ('COLD001', '感冒灵颗粒', category_ids[0], 1, 'https://img.drugmall.com/products/cold001.jpg', '', '10g*9袋', '盒', '北京同仁堂', 'Z11020998', 15.80, 22.00, 500, 10, 150, 0, '口服', '解热镇痛', 1),
        ('COLD002', '复方氨酚烷胺片', category_ids[0], 1, 'https://img.drugmall.com/products/cold002.jpg', '', '12片', '盒', '康力制药', 'H46020614', 8.50, 12.00, 800, 10, 220, 0, '口服', '缓解感冒症状', 1),
        ('COLD003', '连花清瘟胶囊', category_ids[0], 1, 'https://img.drugmall.com/products/cold003.jpg', '', '0.35g*24粒', '盒', '以岭药业', 'Z20040063', 23.50, 32.00, 400, 10, 180, 0, '口服', '清瘟解毒', 1),
        ('COLD004', '藿香正气水', category_ids[0], 1, 'https://img.drugmall.com/products/cold004.jpg', '', '10ml*10支', '盒', '太极制药', 'Z51022305', 12.00, 18.00, 600, 10, 120, 0, '口服', '解表化湿', 1),
        ('COLD005', '板蓝根颗粒', category_ids[0], 1, 'https://img.drugmall.com/products/cold005.jpg', '', '10g*20袋', '盒', '白云山', 'Z44023485', 16.00, 24.00, 700, 10, 200, 0, '冲服', '清热解毒', 1),
        ('COLD006', '维C银翘片', category_ids[0], 1, 'https://img.drugmall.com/products/cold006.jpg', '', '12片*2板', '盒', '贵州百灵', 'Z52020455', 9.80, 15.00, 550, 10, 95, 0, '口服', '辛凉解表', 1),
        ('COLD007', '小儿氨酚黄那敏', category_ids[0], 1, 'https://img.drugmall.com/products/cold007.jpg', '', '6g*10袋', '盒', '葵花药业', 'H23022613', 18.50, 26.00, 400, 10, 85, 0, '冲服', '儿童感冒药', 1),
        ('COLD008', '急支糖浆', category_ids[0], 1, 'https://img.drugmall.com/products/cold008.jpg', '', '100ml', '瓶', '太极制药', 'Z50020615', 22.00, 32.00, 350, 10, 78, 0, '口服', '清热化痰', 1),
        ('COLD009', '川贝枇杷膏', category_ids[0], 1, 'https://img.drugmall.com/products/cold009.jpg', '', '300ml', '瓶', '北京同仁堂', 'Z11020552', 35.00, 48.00, 300, 10, 45, 0, '口服', '润肺止咳', 1),
        ('COLD010', '布洛芬混悬液', category_ids[0], 1, 'https://img.drugmall.com/products/cold010.jpg', '', '100ml', '瓶', '上海强生', 'H19991011', 28.50, 38.00, 450, 10, 130, 0, '口服', '儿童退热', 1),
        ('DIG001', '吗丁啉', category_ids[1], 1, 'https://img.drugmall.com/products/dig001.jpg', '', '10mg*30片', '盒', '西安杨森', 'H20093377', 32.00, 42.00, 500, 10, 180, 0, '口服', '促胃肠动力', 1),
        ('DIG002', '健胃消食片', category_ids[1], 1, 'https://img.drugmall.com/products/dig002.jpg', '', '0.5g*32片', '盒', '江中药业', 'Z20043622', 15.00, 22.00, 800, 10, 250, 0, '口服', '健胃消食', 1),
        ('DIG003', '蒙脱石散', category_ids[1], 1, 'https://img.drugmall.com/products/dig003.jpg', '', '3g*10袋', '盒', '江苏联环', 'H20000690', 26.00, 35.00, 400, 10, 120, 0, '冲服', '止泻药', 1),
        ('DIG004', '肠炎宁片', category_ids[1], 1, 'https://img.drugmall.com/products/dig004.jpg', '', '0.42g*12片*2板', '盒', '康恩贝', 'Z20053152', 22.50, 32.00, 350, 10, 98, 0, '口服', '清热利湿', 1),
        ('DIG005', '枯草杆菌', category_ids[1], 1, 'https://img.drugmall.com/products/dig005.jpg', '', '1g*10袋', '盒', '北京韩美', 'S20020037', 38.00, 48.00, 300, 10, 65, 0, '冲服', '调节肠道', 1),
        ('DIG006', '奥美拉唑肠溶胶囊', category_ids[1], 1, 'https://img.drugmall.com/products/dig006.jpg', '', '20mg*14粒', '盒', '阿斯利康', 'H20030412', 68.00, 85.00, 250, 10, 42, 1, '口服', '质子泵抑制剂', 1),
        ('DIG007', '保和丸', category_ids[1], 1, 'https://img.drugmall.com/products/dig007.jpg', '', '9g*10丸', '盒', '北京同仁堂', 'Z11020752', 18.00, 26.00, 400, 10, 110, 0, '口服', '消食和胃', 1),
        ('DIG008', '乳果糖口服溶液', category_ids[1], 1, 'https://img.drugmall.com/products/dig008.jpg', '', '100ml', '瓶', 'Abbott', 'H20120357', 45.00, 58.00, 200, 10, 55, 0, '口服', '泻药', 1),
        ('DIG009', '嗜酸乳杆菌片', category_ids[1], 1, 'https://img.drugmall.com/products/dig009.jpg', '', '0.5g*12片', '盒', '通化金马', 'H20059130', 28.00, 38.00, 280, 10, 72, 0, '口服', '调节肠道菌群', 1),
        ('DIG010', '香砂养胃丸', category_ids[1], 1, 'https://img.drugmall.com/products/dig010.jpg', '', '9g*10丸', '盒', '太极制药', 'Z51022309', 16.50, 24.00, 380, 10, 88, 0, '口服', '温中和胃', 1),
        ('SKIN001', '复方醋酸地塞米松乳膏', category_ids[2], 1, 'https://img.drugmall.com/products/skin001.jpg', '', '20g', '支', '华润三九', 'H44024942', 15.00, 22.00, 600, 10, 200, 0, '外用', '皮炎平', 1),
        ('SKIN002', '999皮炎平', category_ids[2], 1, 'https://img.drugmall.com/products/skin002.jpg', '', '20g', '支', '华润三九', 'H44024942', 16.00, 24.00, 550, 10, 185, 0, '外用', '止痒消炎', 1),
        ('SKIN003', '马应龙麝香痔疮膏', category_ids[2], 1, 'https://img.drugmall.com/products/skin003.jpg', '', '10g', '支', '马应龙', 'Z42021972', 28.00, 38.00, 400, 10, 130, 0, '外用', '痔疮膏', 1),
        ('SKIN004', '红霉素软膏', category_ids[2], 1, 'https://img.drugmall.com/products/skin004.jpg', '', '10g', '支', '华润三九', 'H44024943', 8.00, 12.00, 800, 10, 350, 0, '外用', '抗感染', 1),
        ('SKIN005', '莫匹罗星软膏', category_ids[2], 1, 'https://img.drugmall.com/products/skin005.jpg', '', '10g', '支', '中美天津史克', 'H12000001', 32.00, 42.00, 350, 10, 95, 0, '外用', '抗菌', 1),
        ('SKIN006', '丹皮酚软膏', category_ids[2], 1, 'https://img.drugmall.com/products/skin006.jpg', '', '20g', '支', '山东鲁抗', 'Z37019001', 25.00, 35.00, 300, 10, 78, 0, '外用', '抗过敏', 1),
        ('SKIN007', '维A酸乳膏', category_ids[2], 1, 'https://img.drugmall.com/products/skin007.jpg', '', '20g', '支', '重庆华邦', 'H50021743', 35.00, 48.00, 250, 10, 45, 1, '外用', '治疗痤疮', 1),
        ('SKIN008', '阿达帕林凝胶', category_ids[2], 1, 'https://img.drugmall.com/products/skin008.jpg', '', '15g', '支', '四川明欣', 'H51021234', 38.00, 52.00, 220, 10, 38, 0, '外用', '治疗痤疮', 1),
        ('SKIN009', '硝酸咪康唑乳膏', category_ids[2], 1, 'https://img.drugmall.com/products/skin009.jpg', '', '20g', '支', '西安杨森', 'H61020001', 22.00, 32.00, 400, 10, 120, 0, '外用', '抗真菌', 1),
        ('SKIN010', '卤米松乳膏', category_ids[2], 1, 'https://img.drugmall.com/products/skin010.jpg', '', '15g', '支', '香港澳美', 'HC20090001', 42.00, 58.00, 180, 10, 32, 1, '外用', '强效激素', 1),
    ]
    with conn.cursor() as cursor:
        cursor.executemany(
            """INSERT INTO dm_product (product_code, product_name, category_id, brand_id, main_image, images, specification, unit, manufacturer, approval_number, price, original_price, stock, warning_stock, sales, is_rx, `usage`, description, status)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
            products
        )
    conn.commit()
    print(f"[OK] 商品数据: {len(products)} 条")

def init_stores(conn):
    stores = [
        ('STORE001', '北京朝阳药店', 'https://img.drugmall.com/stores/store001.png', '北京', '#1677FF', 4.8, 12500, '北京市朝阳区建国路88号', '010-12345678', '08:00-22:00', 1, 0, 39.9042, 116.4074, '专业连锁药店', '中西药品', '京DA12345', 1, 1, 1, 30, 0.00, 5.00, 1, 1),
        ('STORE002', '上海浦东药房', 'https://img.drugmall.com/stores/store002.png', '上海', '#1677FF', 4.7, 9800, '上海市浦东新区张江路', '021-87654321', '24小时', 1, 1, 31.2304, 121.4737, '医保定点', '处方药OTC', '沪DA54321', 1, 1, 1, 25, 0.00, 0.00, 1, 2),
        ('STORE003', '广州天河药店', 'https://img.drugmall.com/stores/store003.png', '广州', '#1677FF', 4.9, 15800, '广州市天河区天河路385号', '020-88888888', '08:00-23:00', 1, 0, 23.1291, 113.2644, '专业处方药房', '处方药中药', '粤DA11223', 1, 1, 1, 30, 29.00, 3.00, 1, 3),
        ('STORE004', '深圳南山药房', 'https://img.drugmall.com/stores/store004.png', '深圳', '#1677FF', 4.6, 7800, '深圳市南山区科技园', '0755-22222222', '09:00-21:00', 1, 0, 22.5431, 113.9288, '连锁品牌', 'OTC保健品', '粤DA33445', 1, 1, 1, 35, 0.00, 4.00, 1, 4),
        ('STORE005', '杭州西湖药店', 'https://img.drugmall.com/stores/store005.png', '杭州', '#1677FF', 4.8, 11200, '杭州市西湖区文三路123号', '0571-88888888', '08:00-22:00', 1, 0, 30.2741, 120.1551, '中医馆特色', '中药饮片', '浙DA55667', 1, 1, 1, 30, 0.00, 3.00, 1, 5),
        ('STORE006', '成都武侯药房', 'https://img.drugmall.com/stores/store006.png', '成都', '#1677FF', 4.7, 8900, '成都市武侯区科华北路66号', '028-88888888', '08:30-21:30', 1, 0, 30.6594, 104.0657, '慢病管理', '处方药', '川DA77889', 1, 1, 1, 28, 0.00, 4.00, 1, 6),
        ('STORE007', '武汉江汉药店', 'https://img.drugmall.com/stores/store007.png', '武汉', '#1677FF', 4.8, 13500, '武汉市江汉区解放大道688号', '027-88888888', '08:00-22:00', 1, 0, 30.5794, 114.2793, '医保定点', '中西药品', '鄂DA99001', 1, 1, 1, 32, 0.00, 5.00, 1, 7),
        ('STORE008', '南京鼓楼药房', 'https://img.drugmall.com/stores/store008.png', '南京', '#1677FF', 4.6, 7200, '南京市鼓楼区中山北路200号', '025-88888888', '09:00-21:00', 1, 0, 32.0603, 118.7969, '专业皮肤科', '皮肤科用药', '苏DA11223', 1, 1, 1, 30, 0.00, 3.00, 1, 8),
        ('STORE009', '西安雁塔药店', 'https://img.drugmall.com/stores/store009.png', '西安', '#1677FF', 4.9, 16800, '西安市雁塔区雁塔路88号', '029-88888888', '08:00-23:00', 1, 0, 34.2599, 108.9402, '连锁品牌', '中西药品', '陕DA33445', 1, 1, 1, 28, 0.00, 4.00, 1, 9),
        ('STORE010', '天津河西药房', 'https://img.drugmall.com/stores/store010.png', '天津', '#1677FF', 4.7, 9500, '天津市河西区解放南路353号', '022-88888888', '08:00-22:00', 1, 0, 39.1256, 117.1909, '专业妇科', '妇科用药', '津DA55667', 1, 1, 1, 30, 0.00, 5.00, 1, 10),
    ]
    with conn.cursor() as cursor:
        cursor.executemany(
            """INSERT INTO dm_store (store_code, store_name, logo, logo_text, logo_color, rating, monthly_sales, address, phone, business_hours, is_open, is_24hours, latitude, longitude, description, business_scope, license_no, is_insurance, is_chain, is_self_operated, delivery_time, min_delivery_amount, delivery_fee, status, sort_order)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
            stores
        )
    conn.commit()
    print(f"[OK] 门店数据: {len(stores)} 条")

def init_kingkong(conn):
    kingkong_items = [
        ('OTC药品', 'https://img.drugmall.com/kingkong/otc.png', 'url', '', '#1677FF', 1, 1, None, None, 'home_page_001'),
        ('处方药', 'https://img.drugmall.com/kingkong/rx.png', 'url', '', '#1677FF', 2, 1, None, None, 'home_page_001'),
        ('医疗器械', 'https://img.drugmall.com/kingkong/medical.png', 'url', '', '#1677FF', 3, 1, None, None, 'home_page_001'),
        ('维生素', 'https://img.drugmall.com/kingkong/vitamin.png', 'url', '', '#1677FF', 4, 1, None, None, 'home_page_001'),
        ('中药饮片', 'https://img.drugmall.com/kingkong/tcm.png', 'url', '', '#1677FF', 5, 1, None, None, 'home_page_001'),
        ('隐形眼镜', 'https://img.drugmall.com/kingkong/contact.png', 'url', '', '#1677FF', 6, 1, None, None, 'home_page_001'),
        ('避孕套', 'https://img.drugmall.com/kingkong/condom.png', 'url', '', '#1677FF', 7, 1, None, None, 'home_page_001'),
        ('体温计', 'https://img.drugmall.com/kingkong/thermometer.png', 'url', '', '#1677FF', 8, 1, None, None, 'home_page_001'),
        ('口罩', 'https://img.drugmall.com/kingkong/mask.png', 'url', '', '#1677FF', 9, 1, None, None, 'home_page_001'),
        ('消毒液', 'https://img.drugmall.com/kingkong/disinfectant.png', 'url', '', '#1677FF', 10, 1, None, None, 'home_page_001'),
    ]
    with conn.cursor() as cursor:
        cursor.executemany(
            "INSERT INTO dm_home_kingkong (name, icon_url, jump_type, jump_url, bg_color, sort_order, status, tab_id, section_id, page_id) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
            kingkong_items
        )
    conn.commit()
    print(f"[OK] 金刚位: {len(kingkong_items)} 条")

def init_banners(conn):
    banners = [
        (1, '正品保障', 'https://img.drugmall.com/banners/banner1.jpg', '', 1, 1),
        (2, '新人专享', 'https://img.drugmall.com/banners/banner2.jpg', '', 2, 1),
        (3, '限时优惠', 'https://img.drugmall.com/banners/banner3.jpg', '', 3, 1),
        (4, '专科药师', 'https://img.drugmall.com/banners/banner4.jpg', '', 4, 1),
    ]
    with conn.cursor() as cursor:
        cursor.executemany(
            "INSERT INTO dm_banner (id, title, image, link, sort, status) VALUES (%s, %s, %s, %s, %s, %s)",
            banners
        )
    conn.commit()
    print(f"[OK] 横幅数据: {len(banners)} 条")

def main():
    print("=" * 50)
    print("DrugMall 完整数据初始化")
    print("=" * 50)

    try:
        conn = get_connection()
        print("[OK] 数据库连接成功")

        clear_all_data(conn)

        init_departments(conn)
        init_department_config(conn)
        init_department_tags(conn)
        init_dict_data(conn)
        init_doctors(conn)
        init_doctor_ext(conn)
        init_payment_and_shortcuts(conn)

        category_ids = init_categories(conn)
        init_products(conn, category_ids)

        init_stores(conn)
        init_kingkong(conn)
        init_banners(conn)

        conn.close()

        print("=" * 50)
        print("数据初始化完成!")
        print("=" * 50)

    except Exception as e:
        print(f"[ERROR] {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == '__main__':
    main()
