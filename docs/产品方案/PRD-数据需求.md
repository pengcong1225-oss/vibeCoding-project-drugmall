# DrugMall 产品需求文档 - 数据需求

## 版本信息

| 项目 | 内容 |
|------|------|
| 文档版本 | V1.0 |
| 创建日期 | 2024-12-07 |
| 文档状态 | 已评审 |
| 产品名称 | DrugMall（药康购） |
| 所属模块 | 数据需求 |

---

## 1. 核心数据模型

### 1.1 用户域

#### 1.1.1 用户表（user）

| 字段名 | 数据类型 | 长度 | 是否必填 | 默认值 | 说明 |
|--------|----------|------|----------|--------|------|
| id | BIGINT | 20 | 是 | 自增 | 主键，用户唯一标识 |
| phone | VARCHAR | 20 | 是 | - | 手机号，登录账号 |
| password | VARCHAR | 64 | 是 | - | 加密后的密码 |
| nickname | VARCHAR | 50 | 是 | 用户+手机号后四位 | 用户昵称 |
| avatar | VARCHAR | 255 | 否 | 默认头像URL | 头像URL |
| gender | TINYINT | 1 | 否 | 0 | 性别：0-未知，1-男，2-女 |
| birthday | DATE | - | 否 | - | 出生日期 |
| real_name | VARCHAR | 50 | 否 | - | 真实姓名 |
| id_card | VARCHAR | 18 | 否 | - | 身份证号（加密存储） |
| is_real_name | TINYINT | 1 | 否 | 0 | 是否实名认证：0-否，1-是 |
| status | TINYINT | 1 | 是 | 1 | 状态：0-禁用，1-正常，2-注销 |
| source | TINYINT | 1 | 是 | 1 | 注册来源：1-APP，2-H5，3-小程序 |
| last_login_time | DATETIME | - | 否 | - | 最后登录时间 |
| last_login_ip | VARCHAR | 50 | 否 | - | 最后登录IP |
| created_at | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | - | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- 主键索引：id
- 唯一索引：phone
- 普通索引：is_real_name, status, created_at

---

#### 1.1.2 就诊人表（patient）

| 字段名 | 数据类型 | 长度 | 是否必填 | 默认值 | 说明 |
|--------|----------|------|----------|--------|------|
| id | BIGINT | 20 | 是 | 自增 | 主键 |
| user_id | BIGINT | 20 | 是 | - | 关联用户ID |
| name | VARCHAR | 50 | 是 | - | 就诊人姓名 |
| gender | TINYINT | 1 | 是 | - | 性别：1-男，2-女 |
| age | INT | 3 | 否 | - | 年龄 |
| id_card | VARCHAR | 18 | 否 | - | 身份证号（加密存储） |
| phone | VARCHAR | 20 | 否 | - | 联系电话 |
| relationship | TINYINT | 1 | 是 | - | 关系：1-本人，2-配偶，3-子女，4-父母，5-其他 |
| is_default | TINYINT | 1 | 否 | 0 | 是否默认：0-否，1-是 |
| health_info | JSON | - | 否 | - | 健康信息（过敏史、既往病史等） |
| status | TINYINT | 1 | 是 | 1 | 状态：0-删除，1-正常 |
| created_at | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | - | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- 主键索引：id
- 普通索引：user_id, is_default, status
- 联合索引：user_id + status

---

### 1.2 商品域

#### 1.2.1 药品主表（medicine）

| 字段名 | 数据类型 | 长度 | 是否必填 | 默认值 | 说明 |
|--------|----------|------|----------|--------|------|
| id | BIGINT | 20 | 是 | 自增 | 主键 |
| medicine_no | VARCHAR | 32 | 是 | - | 药品编号，唯一 |
| name | VARCHAR | 100 | 是 | - | 药品名称 |
| generic_name | VARCHAR | 100 | 是 | - | 通用名 |
| specification | VARCHAR | 200 | 是 | - | 规格 |
| manufacturer | VARCHAR | 200 | 是 | - | 生产厂家 |
| category_id | BIGINT | 20 | 是 | - | 分类ID |
| approval_no | VARCHAR | 50 | 否 | - | 批准文号 |
| barcode | VARCHAR | 50 | 否 | - | 条形码 |
| is_rx | TINYINT | 1 | 是 | 0 | 是否处方药：0-非处方，1-处方 |
| is_medicaid | TINYINT | 1 | 是 | 0 | 是否医保：0-否，1-是 |
| main_image | VARCHAR | 255 | 否 | - | 主图URL |
| images | JSON | - | 否 | - | 图片列表（JSON数组） |
| indications | TEXT | - | 否 | - | 适应症 |
| usage_dosage | TEXT | - | 否 | - | 用法用量 |
| adverse_reactions | TEXT | - | 否 | - | 不良反应 |
| contraindications | TEXT | - | 否 | - | 禁忌 |
| precautions | TEXT | - | 否 | - | 注意事项 |
| drug_interactions | TEXT | - | 否 | - | 药物相互作用 |
| storage | TEXT | - | 否 | - | 贮藏条件 |
| status | TINYINT | 1 | 是 | 1 | 状态：0-下架，1-上架，2-删除 |
| sort_order | INT | 10 | 否 | 0 | 排序权重 |
| sales_count | INT | 10 | 否 | 0 | 销量统计 |
| view_count | INT | 10 | 否 | 0 | 浏览量统计 |
| created_at | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | - | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- 主键索引：id
- 唯一索引：medicine_no, barcode
- 普通索引：category_id, is_rx, is_medicaid, status, sort_order
- 全文索引：name, generic_name（用于搜索）

---

### 1.3 订单域

#### 1.3.1 订单主表（order）

| 字段名 | 数据类型 | 长度 | 是否必填 | 默认值 | 说明 |
|--------|----------|------|----------|--------|------|
| id | BIGINT | 20 | 是 | 自增 | 主键 |
| order_no | VARCHAR | 32 | 是 | - | 订单编号，唯一 |
| user_id | BIGINT | 20 | 是 | - | 用户ID |
| patient_id | BIGINT | 20 | 是 | - | 就诊人ID |
| order_type | TINYINT | 1 | 是 | 1 | 订单类型：1-普通订单，2-处方订单 |
| prescription_id | BIGINT | 20 | 否 | - | 处方ID（处方订单必填） |
| status | TINYINT | 1 | 是 | 0 | 状态：0-待支付，1-已支付，2-待发货，3-已发货，4-已完成，5-已取消 |
| delivery_type | TINYINT | 1 | 是 | 1 | 配送方式：1-快递配送，2-到店自取 |
| store_id | BIGINT | 20 | 否 | - | 门店ID（到店自取必填） |
| total_amount | DECIMAL | 10,2 | 是 | 0.00 | 商品总金额 |
| delivery_fee | DECIMAL | 10,2 | 是 | 0.00 | 配送费用 |
| discount_amount | DECIMAL | 10,2 | 是 | 0.00 | 优惠金额 |
| pay_amount | DECIMAL | 10,2 | 是 | 0.00 | 实付金额 |
| pay_type | TINYINT | 1 | 否 | - | 支付方式：1-微信，2-支付宝，3-医保，4-余额 |
| pay_time | DATETIME | - | 否 | - | 支付时间 |
| pay_no | VARCHAR | 64 | 否 | - | 第三方支付流水号 |
| delivery_time | DATETIME | - | 否 | - | 发货时间 |
| receive_time | DATETIME | - | 否 | - | 收货时间 |
| cancel_time | DATETIME | - | 否 | - | 取消时间 |
| cancel_reason | VARCHAR | 255 | 否 | - | 取消原因 |
| remark | VARCHAR | 500 | 否 | - | 订单备注 |
| created_at | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | - | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- 主键索引：id
- 唯一索引：order_no
- 普通索引：user_id, patient_id, status, pay_type, created_at
- 联合索引：user_id + status

---

#### 1.3.2 订单明细表（order_item）

| 字段名 | 数据类型 | 长度 | 是否必填 | 默认值 | 说明 |
|--------|----------|------|----------|--------|------|
| id | BIGINT | 20 | 是 | 自增 | 主键 |
| order_id | BIGINT | 20 | 是 | - | 订单ID |
| medicine_id | BIGINT | 20 | 是 | - | 药品ID |
| medicine_name | VARCHAR | 100 | 是 | - | 药品名称（冗余） |
| specification | VARCHAR | 200 | 是 | - | 规格（冗余） |
| manufacturer | VARCHAR | 200 | 是 | - | 生产厂家（冗余） |
| price | DECIMAL | 10,2 | 是 | 0.00 | 单价 |
| quantity | INT | 10 | 是 | 1 | 数量 |
| subtotal | DECIMAL | 10,2 | 是 | 0.00 | 小计金额 |
| created_at | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |

**索引**：
- 主键索引：id
- 普通索引：order_id, medicine_id
- 联合索引：order_id + medicine_id

---

### 1.4 问诊域

#### 1.4.1 问诊记录表（inquiry）

| 字段名 | 数据类型 | 长度 | 是否必填 | 默认值 | 说明 |
|--------|----------|------|----------|--------|------|
| id | BIGINT | 20 | 是 | 自增 | 主键 |
| inquiry_no | VARCHAR | 32 | 是 | - | 问诊编号，唯一 |
| user_id | BIGINT | 20 | 是 | - | 用户ID |
| patient_id | BIGINT | 20 | 是 | - | 就诊人ID |
| doctor_id | BIGINT | 20 | 是 | - | 医生ID |
| type | TINYINT | 1 | 是 | 1 | 问诊类型：1-图文，2-语音，3-视频 |
| status | TINYINT | 1 | 是 | 0 | 状态：0-待接诊，1-进行中，2-已完成，3-已取消 |
| symptom_desc | TEXT | - | 否 | - | 症状描述 |
| symptom_images | JSON | - | 否 | - | 症状图片（JSON数组） |
| diagnosis | VARCHAR | 500 | 否 | - | 诊断结果 |
| advice | TEXT | - | 否 | - | 医生建议 |
| price | DECIMAL | 10,2 | 是 | 0.00 | 问诊费用 |
| start_time | DATETIME | - | 否 | - | 开始时间 |
| end_time | DATETIME | - | 否 | - | 结束时间 |
| duration | INT | 10 | 否 | 0 | 问诊时长（分钟） |
| is_prescribed | TINYINT | 1 | 否 | 0 | 是否开方：0-否，1-是 |
| prescription_id | BIGINT | 20 | 否 | - | 处方ID |
| rating | TINYINT | 1 | 否 | - | 评价星级：1-5 |
| comment | VARCHAR | 500 | 否 | - | 评价内容 |
| created_at | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | - | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- 主键索引：id
- 唯一索引：inquiry_no
- 普通索引：user_id, patient_id, doctor_id, status, created_at
- 联合索引：user_id + status

---

#### 1.4.2 问诊消息表（inquiry_message）

| 字段名 | 数据类型 | 长度 | 是否必填 | 默认值 | 说明 |
|--------|----------|------|----------|--------|------|
| id | BIGINT | 20 | 是 | 自增 | 主键 |
| inquiry_id | BIGINT | 20 | 是 | - | 问诊ID |
| sender_type | TINYINT | 1 | 是 | - | 发送者类型：1-患者，2-医生 |
| sender_id | BIGINT | 20 | 是 | - | 发送者ID |
| msg_type | TINYINT | 1 | 是 | 1 | 消息类型：1-文字，2-图片，3-语音 |
| content | TEXT | - | 是 | - | 消息内容 |
| media_url | VARCHAR | 255 | 否 | - | 媒体文件URL |
| is_read | TINYINT | 1 | 是 | 0 | 是否已读：0-否，1-是 |
| read_time | DATETIME | - | 否 | - | 阅读时间 |
| created_at | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |

**索引**：
- 主键索引：id
- 普通索引：inquiry_id, sender_id, is_read, created_at
- 联合索引：inquiry_id + created_at

---

### 1.5 处方域

#### 1.5.1 处方主表（prescription）

| 字段名 | 数据类型 | 长度 | 是否必填 | 默认值 | 说明 |
|--------|----------|------|----------|--------|------|
| id | BIGINT | 20 | 是 | 自增 | 主键 |
| prescription_no | VARCHAR | 32 | 是 | - | 处方编号，唯一 |
| inquiry_id | BIGINT | 20 | 是 | - | 问诊ID |
| user_id | BIGINT | 20 | 是 | - | 用户ID |
| patient_id | BIGINT | 20 | 是 | - | 就诊人ID |
| doctor_id | BIGINT | 20 | 是 | - | 医生ID |
| status | TINYINT | 1 | 是 | 0 | 状态：0-待生效，1-已生效，2-已使用，3-已过期，4-已作废 |
| diagnosis | VARCHAR | 500 | 是 | - | 诊断结果 |
| main_diagnosis | VARCHAR | 200 | 是 | - | 主要诊断 |
| secondary_diagnosis | VARCHAR | 200 | 否 | - | 次要诊断 |
| advice | TEXT | - | 否 | - | 医嘱说明 |
| total_amount | DECIMAL | 10,2 | 是 | 0.00 | 药品总金额 |
| valid_days | INT | 3 | 是 | 3 | 有效期天数 |
| valid_until | DATE | - | 是 | - | 有效期至 |
| sign_image | VARCHAR | 255 | 否 | - | 医生签名图片URL |
| used_order_id | BIGINT | 20 | 否 | - | 使用订单ID |
| used_time | DATETIME | - | 否 | - | 使用时间 |
| created_at | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | - | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- 主键索引：id
- 唯一索引：prescription_no
- 普通索引：user_id, patient_id, doctor_id, status, valid_until
- 联合索引：user_id + status

---

#### 1.5.2 处方明细表（prescription_item）

| 字段名 | 数据类型 | 长度 | 是否必填 | 默认值 | 说明 |
|--------|----------|------|----------|--------|------|
| id | BIGINT | 20 | 是 | 自增 | 主键 |
| prescription_id | BIGINT | 20 | 是 | - | 处方ID |
| medicine_id | BIGINT | 20 | 是 | - | 药品ID |
| medicine_name | VARCHAR | 100 | 是 | - | 药品名称（冗余） |
| specification | VARCHAR | 200 | 是 | - | 规格（冗余） |
| dosage | VARCHAR | 50 | 是 | - | 单次剂量 |
| frequency | VARCHAR | 50 | 是 | - | 用药频次 |
| days | INT | 3 | 是 | - | 用药天数 |
| quantity | INT | 10 | 是 | 1 | 数量 |
| unit | VARCHAR | 20 | 是 | - | 单位 |
| price | DECIMAL | 10,2 | 是 | 0.00 | 单价 |
| subtotal | DECIMAL | 10,2 | 是 | 0.00 | 小计金额 |
| created_at | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |

**索引**：
- 主键索引：id
- 普通索引：prescription_id, medicine_id
- 联合索引：prescription_id + medicine_id

---

### 1.6 医生域

#### 1.6.1 医生表（doctor）

| 字段名 | 数据类型 | 长度 | 是否必填 | 默认值 | 说明 |
|--------|----------|------|----------|--------|------|
| id | BIGINT | 20 | 是 | 自增 | 主键 |
| doctor_no | VARCHAR | 32 | 是 | - | 医生编号，唯一 |
| phone | VARCHAR | 20 | 是 | - | 手机号 |
| password | VARCHAR | 64 | 是 | - | 加密后的密码 |
| name | VARCHAR | 50 | 是 | - | 医生姓名 |
| avatar | VARCHAR | 255 | 否 | - | 头像URL |
| gender | TINYINT | 1 | 是 | - | 性别：1-男，2-女 |
| title | VARCHAR | 50 | 是 | - | 职称 |
| hospital | VARCHAR | 100 | 是 | - | 所属医院 |
| department | VARCHAR | 50 | 是 | - | 所属科室 |
| specialty | VARCHAR | 500 | 否 | - | 擅长领域 |
| introduction | TEXT | - | 否 | - | 医生简介 |
| license_no | VARCHAR | 50 | 是 | - | 执业证书编号 |
| license_image | VARCHAR | 255 | 是 | - | 执业证书图片 |
| is_verified | TINYINT | 1 | 是 | 0 | 是否认证：0-待审核，1-已通过，2-未通过 |
| inquiry_price | DECIMAL | 10,2 | 是 | 0.00 | 问诊价格 |
| rating | DECIMAL | 2,1 | 是 | 5.0 | 评分：1.0-5.0 |
| rating_count | INT | 10 | 是 | 0 | 评价数量 |
| inquiry_count | INT | 10 | 是 | 0 | 问诊次数 |
| status | TINYINT | 1 | 是 | 1 | 状态：0-禁用，1-正常 |
| is_online | TINYINT | 1 | 是 | 0 | 是否在线：0-离线，1-在线 |
| last_online_time | DATETIME | - | 否 | - | 最后在线时间 |
| created_at | DATETIME | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | - | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- 主键索引：id
- 唯一索引：doctor_no, phone, license_no
- 普通索引：department, is_verified, status, is_online, rating

---

## 2. 数据字典

### 2.1 通用状态枚举

| 枚举类型 | 值 | 含义 |
|----------|-----|------|
| 用户状态 | 0 | 禁用 |
| | 1 | 正常 |
| | 2 | 注销 |
| 订单状态 | 0 | 待支付 |
| | 1 | 已支付 |
| | 2 | 待发货 |
| | 3 | 已发货 |
| | 4 | 已完成 |
| | 5 | 已取消 |
| 问诊状态 | 0 | 待接诊 |
| | 1 | 进行中 |
| | 2 | 已完成 |
| | 3 | 已取消 |
| 处方状态 | 0 | 待生效 |
| | 1 | 已生效 |
| | 2 | 已使用 |
| | 3 | 已过期 |
| | 4 | 已作废 |
| 支付方式 | 1 | 微信支付 |
| | 2 | 支付宝 |
| | 3 | 医保支付 |
| | 4 | 余额支付 |
| 配送方式 | 1 | 快递配送 |
| | 2 | 到店自取 |

---

## 3. 数据安全

### 3.1 敏感数据加密

| 数据类型 | 加密方式 | 说明 |
|----------|----------|------|
| 身份证号 | AES-256 | 对称加密，密钥存储于KMS |
| 手机号 | AES-256 | 对称加密，展示时脱敏 |
| 密码 | BCrypt | 单向哈希，加盐处理 |
| 银行卡号 | AES-256 | 对称加密 |

### 3.2 数据脱敏规则

| 数据类型 | 脱敏前 | 脱敏后 | 说明 |
|----------|--------|--------|------|
| 手机号 | 13812345678 | 138****5678 | 中间4位隐藏 |
| 身份证号 | 110101199001011234 | 110101********1234 | 中间8位隐藏 |
| 银行卡号 | 6222021234567890123 | 6222***********0123 | 中间隐藏 |
| 姓名 | 张三 | 张* | 仅显示姓氏 |

---

**文档结束**

*本文档为DrugMall产品需求文档的一部分，完整的PRD文档请查看相关索引文件。*
