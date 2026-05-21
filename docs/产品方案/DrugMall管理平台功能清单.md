# DrugMall 管理平台功能清单

**文档版本**: v1.0  
**创建日期**: 2026-04-22  
**文档状态**: 待评审

---

## 一、系统概述

DrugMall 管理平台是药品电商平台的统一运营管理后台，用于管理患者端（frontend）和医生端（frontend-doctor）的所有业务数据和运营配置。

### 1.1 系统定位

| 维度 | 说明 |
|------|------|
| 服务对象 | 平台运营人员、客服人员、审核人员、系统管理员 |
| 管理范围 | 用户、医生、药品、订单、问诊、处方、门店、内容、财务 |
| 核心价值 | 提升运营效率、保障合规经营、数据驱动决策 |

### 1.2 现有功能 vs 新增功能

| 模块 | 现有功能 | 新增功能 | 优先级 |
|------|---------|---------|--------|
| 仪表盘 | 数据概览、GMV趋势、订单来源 | 实时统计看板 | P0 |
| 用户管理 | 用户列表、详情、实名认证 | 就诊人管理、健康档案 | P1 |
| 医生管理 | 无 | 医生入驻审核、排班、评价 | P0 |
| 药品管理 | 药品列表、分类、品牌 | 药品审核、库存预警 | P0 |
| 门店管理 | 无 | 门店入驻审核、资质管理 | P1 |
| 订单管理 | 订单列表 | 退款管理、异常订单 | P0 |
| 问诊管理 | 无 | 问诊监控、分配、统计 | P0 |
| 处方管理 | 无 | 处方审核、模板、统计 | P0 |
| 首页配置 | 无 | Tab管理、模块配置、发布管理 | P0 |
| 财务管理 | 收入统计、交易流水、提现 | 医生结算、分账管理 | P1 |
| 内容管理 | Banner、公告、资讯 | 帮助中心、FAQ | P2 |
| 运营管理 | 无 | 意见反馈、投诉、症状题库 | P2 |
| 系统设置 | 基础配置、支付配置 | 权限管理、操作日志 | P1 |

---

## 二、完整功能清单

### 2.1 功能模块总览

```
DrugMall 管理平台
├── 1. 仪表盘
│   ├── 1.1 数据概览
│   ├── 1.2 实时统计
│   └── 1.3 数据报表
├── 2. 用户管理
│   ├── 2.1 用户列表
│   ├── 2.2 用户详情
│   ├── 2.3 实名认证审核
│   └── 2.4 就诊人管理
├── 3. 医生管理
│   ├── 3.1 医生列表
│   ├── 3.2 医生详情
│   ├── 3.3 入驻审核
│   ├── 3.4 排班管理
│   ├── 3.5 科室管理
│   └── 3.6 评价管理
├── 4. 药品管理
│   ├── 4.1 药品列表
│   ├── 4.2 药品分类
│   ├── 4.3 品牌管理
│   ├── 4.4 药品编辑
│   └── 4.5 药品审核
├── 5. 门店管理
│   ├── 5.1 门店列表
│   ├── 5.2 门店详情
│   ├── 5.3 门店审核
│   ├── 5.4 门店药品
│   └── 5.5 门店统计
├── 6. 订单管理
│   ├── 6.1 订单列表
│   ├── 6.2 订单详情
│   ├── 6.3 退款管理
│   └── 6.4 异常订单
├── 7. 问诊管理
│   ├── 7.1 问诊列表
│   ├── 7.2 问诊详情
│   ├── 7.3 问诊分配
│   ├── 7.4 问诊统计
│   └── 7.5 异常问诊
├── 8. 处方管理
│   ├── 8.1 处方列表
│   ├── 8.2 处方审核
│   ├── 8.3 处方详情
│   ├── 8.4 处方模板
│   └── 8.5 处方统计
├── 9. 首页配置
│   ├── 9.1 全局设置
│   ├── 9.2 Tab管理
│   ├── 9.3 模块管理
│   ├── 9.4 轮播图管理
│   ├── 9.5 金刚位管理
│   ├── 9.6 广告位管理
│   ├── 9.7 专题管理
│   └── 9.8 发布管理
├── 10. 财务管理
│   ├── 10.1 收入统计
│   ├── 10.2 交易流水
│   ├── 10.3 提现管理
│   └── 10.4 医生结算
├── 11. 内容管理
│   ├── 11.1 Banner管理
│   ├── 11.2 公告管理
│   ├── 11.3 资讯管理
│   └── 11.4 帮助中心
├── 12. 运营管理
│   ├── 12.1 意见反馈
│   ├── 12.2 投诉管理
│   ├── 12.3 症状题库
│   └── 12.4 AI配置
├── 13. 系统设置
│   ├── 13.1 基础配置
│   ├── 13.2 支付配置
│   ├── 13.3 权限管理
│   └── 13.4 操作日志
└── 14. 消息中心
    ├── 14.1 系统通知
    ├── 14.2 审核提醒
    └── 14.3 预警通知
```

---

### 2.2 功能详细说明

#### 1. 仪表盘

| 功能 | 说明 | 优先级 |
|------|------|--------|
| 数据概览 | 今日订单、GMV、用户数、问诊量等核心指标 | P0 |
| 实时统计 | 实时订单流、实时收入、在线问诊数 | P1 |
| 数据报表 | 日报、周报、月报导出 | P2 |

**核心指标：**
- 今日GMV / 昨日GMV / 环比
- 今日订单数 / 完成率
- 今日问诊量 / 平均响应时间
- 今日新增用户 / 活跃用户
- 待审核事项（医生入驻、处方、退款）

---

#### 2. 用户管理

| 功能 | 说明 | 优先级 |
|------|------|--------|
| 用户列表 | 搜索、筛选、状态管理、批量操作 | P0 |
| 用户详情 | 基本信息、订单记录、问诊记录、处方记录 | P0 |
| 实名认证审核 | 身份证审核、实名状态管理 | P0 |
| 就诊人管理 | 就诊人列表、健康档案查看 | P1 |

**用户列表字段：**
- 用户ID、昵称、手机号、实名状态、注册时间、最后登录、状态

**筛选条件：**
- 注册时间、实名状态、用户状态、消费金额区间

---

#### 3. 医生管理

| 功能 | 说明 | 优先级 |
|------|------|--------|
| 医生列表 | 搜索、筛选、状态管理 | P0 |
| 医生详情 | 基本信息、执业信息、统计数据、评价 | P0 |
| 入驻审核 | 资质审核、执业证书验证、审核流程 | P0 |
| 排班管理 | 排班配置、停诊管理、排班日历 | P1 |
| 科室管理 | 科室分类、科室医生分配 | P1 |
| 评价管理 | 评价列表、评价回复、差评处理 | P2 |

**医生列表字段：**
- 医生ID、姓名、科室、职称、医院、评分、服务次数、状态、入驻时间

**入驻审核流程：**
```
提交申请 → 资质初审 → 执业证书验证 → 审核通过/驳回 → 开通账号
```

**审核材料：**
- 身份证正反面
- 医师资格证书
- 医师执业证书
- 职称证书
- 医院在职证明

---

#### 4. 药品管理

| 功能 | 说明 | 优先级 |
|------|------|--------|
| 药品列表 | 搜索、筛选、上下架、批量操作 | P0 |
| 药品分类 | 分类树管理、排序 | P0 |
| 品牌管理 | 品牌增删改查 | P1 |
| 药品编辑 | 药品信息编辑、价格调整 | P0 |
| 药品审核 | 新药品审核、修改审核 | P1 |

**药品列表字段：**
- 药品ID、名称、规格、厂家、价格、库存、分类、处方药标识、状态

**药品类型：**
- OTC（非处方药）
- Rx（处方药）
- 医疗器械
- 保健食品

---

#### 5. 门店管理

| 功能 | 说明 | 优先级 |
|------|------|--------|
| 门店列表 | 搜索、筛选、状态管理 | P1 |
| 门店详情 | 基本信息、资质证照、统计数据 | P1 |
| 门店审核 | 入驻审核、资质审核 | P1 |
| 门店药品 | 门店药品关联、库存管理 | P1 |
| 门店统计 | 订单量、销售额、配送范围 | P2 |

**门店列表字段：**
- 门店ID、名称、地址、联系人、电话、资质状态、状态、入驻时间

**审核材料：**
- 营业执照
- 药品经营许可证
- GSP认证证书
- 法人身份证

---

#### 6. 订单管理

| 功能 | 说明 | 优先级 |
|------|------|--------|
| 订单列表 | 搜索、筛选、状态管理 | P0 |
| 订单详情 | 订单信息、商品明细、物流信息 | P0 |
| 退款管理 | 退款申请审核、退款处理 | P0 |
| 异常订单 | 超时未支付、取消订单、异常标记 | P1 |

**订单列表字段：**
- 订单号、用户、商品数量、金额、支付方式、订单状态、创建时间

**订单状态：**
- 待支付、待发货、待收货、已完成、已取消、退款中、已退款

**退款审核流程：**
```
用户申请 → 客服审核 → 同意/驳回 → 退款执行 → 完成
```

---

#### 7. 问诊管理

| 功能 | 说明 | 优先级 |
|------|------|--------|
| 问诊列表 | 搜索、筛选、状态管理 | P0 |
| 问诊详情 | 问诊记录、聊天内容、处方关联 | P0 |
| 问诊分配 | 手动分配、智能分配规则 | P1 |
| 问诊统计 | 问诊量、响应时间、完成率 | P1 |
| 异常问诊 | 超时、投诉、退款 | P1 |

**问诊列表字段：**
- 问诊ID、患者、医生、科室、问诊类型、状态、创建时间、响应时间

**问诊状态：**
- 待接诊、问诊中、待支付、已完成、已取消、退款中

**问诊类型：**
- 图文问诊
- 视频问诊
- 电话问诊
- AI导诊

---

#### 8. 处方管理

| 功能 | 说明 | 优先级 |
|------|------|--------|
| 处方列表 | 搜索、筛选、状态管理 | P0 |
| 处方审核 | 合规审核、用药合理性检查 | P0 |
| 处方详情 | 处方内容、药品明细、患者信息 | P0 |
| 处方模板 | 常用处方模板管理 | P2 |
| 处方统计 | 处方量、审核通过率、退方率 | P1 |

**处方列表字段：**
- 处方ID、患者、医生、诊断、药品数量、金额、状态、创建时间

**处方状态：**
- 待审核、审核通过、审核驳回、已发药、已取消

**审核要点：**
- 处方规范性
- 用药合理性（剂量、频次、配伍禁忌）
- 处方药合规性
- 特殊药品管控

---

#### 9. 首页配置

| 功能 | 说明 | 优先级 |
|------|------|--------|
| 全局设置 | 页面标题、背景色、样式配置 | P0 |
| Tab管理 | Tab增删改查、排序、主题配置 | P0 |
| 模块管理 | 模块配置、拖拽排序、启用禁用 | P0 |
| 轮播图管理 | 轮播图增删改查、定时上下架 | P0 |
| 金刚位管理 | 入口配置、图标管理、跳转配置 | P0 |
| 广告位管理 | 广告配置、排期管理 | P1 |
| 专题管理 | 专题配置、模板管理 | P1 |
| 发布管理 | 版本管理、预览、发布、回滚 | P0 |

**详细设计见独立PRD文档**

---

#### 10. 财务管理

| 功能 | 说明 | 优先级 |
|------|------|--------|
| 收入统计 | 收入趋势、收入构成、对比分析 | P0 |
| 交易流水 | 交易记录查询、导出 | P0 |
| 提现管理 | 提现申请审核、打款处理 | P0 |
| 医生结算 | 医生收入统计、结算周期、分账 | P1 |

**收入构成：**
- 药品销售收入
- 问诊服务费
- 处方服务费
- 检测服务费

**结算规则：**
- 结算周期：T+7 / T+15 / 月结
- 平台抽成比例配置
- 医生分成比例配置

---

#### 11. 内容管理

| 功能 | 说明 | 优先级 |
|------|------|--------|
| Banner管理 | Banner增删改查、定时上下架 | P0 |
| 公告管理 | 公告发布、推送 | P1 |
| 资讯管理 | 健康资讯发布、分类 | P2 |
| 帮助中心 | FAQ管理、帮助文章 | P2 |

---

#### 12. 运营管理

| 功能 | 说明 | 优先级 |
|------|------|--------|
| 意见反馈 | 反馈列表、处理、回复 | P2 |
| 投诉管理 | 投诉列表、处理流程、结果反馈 | P2 |
| 症状题库 | 症状自测题目管理 | P2 |
| AI配置 | AI助手知识库、问答配置 | P2 |

---

#### 13. 系统设置

| 功能 | 说明 | 优先级 |
|------|------|--------|
| 基础配置 | 站点信息、联系方式、备案信息 | P0 |
| 支付配置 | 微信支付、支付宝、余额支付 | P0 |
| 权限管理 | 角色管理、权限分配、账号管理 | P1 |
| 操作日志 | 操作记录查询、审计 | P1 |

**角色定义：**
| 角色 | 权限范围 |
|------|---------|
| 超级管理员 | 全部权限 |
| 运营管理员 | 首页配置、内容管理、运营管理 |
| 客服管理员 | 用户管理、订单管理、问诊管理、投诉处理 |
| 审核管理员 | 医生审核、处方审核、退款审核 |
| 财务管理员 | 财务管理、提现审核 |
| 系统管理员 | 系统设置、权限管理 |

---

#### 14. 消息中心

| 功能 | 说明 | 优先级 |
|------|------|--------|
| 系统通知 | 系统消息推送 | P2 |
| 审核提醒 | 待审核事项提醒 | P1 |
| 预警通知 | 库存预警、超时预警、异常预警 | P1 |

---

## 三、功能优先级矩阵

### 3.1 P0 - 核心功能（必须实现）

| 模块 | 功能 | 业务价值 |
|------|------|---------|
| 仪表盘 | 数据概览 | 运营决策基础 |
| 用户管理 | 用户列表、详情、实名认证 | 用户运营基础 |
| 医生管理 | 医生列表、入驻审核 | 医生端管理核心 |
| 药品管理 | 药品列表、分类、编辑 | 商品管理核心 |
| 订单管理 | 订单列表、退款管理 | 交易管理核心 |
| 问诊管理 | 问诊列表、详情 | 问诊业务核心 |
| 处方管理 | 处方列表、审核 | 合规要求 |
| 首页配置 | Tab管理、模块管理、轮播图、金刚位、发布管理 | 运营效率 |
| 财务管理 | 收入统计、交易流水、提现管理 | 财务基础 |
| 系统设置 | 基础配置、支付配置 | 系统基础 |

### 3.2 P1 - 重要功能（尽快实现）

| 模块 | 功能 | 业务价值 |
|------|------|---------|
| 仪表盘 | 实时统计 | 实时监控 |
| 用户管理 | 就诊人管理 | 患者管理完善 |
| 医生管理 | 排班管理、科室管理 | 医生运营 |
| 药品管理 | 药品审核 | 合规要求 |
| 门店管理 | 门店列表、审核、药品 | 门店运营 |
| 订单管理 | 异常订单 | 异常处理 |
| 问诊管理 | 问诊分配、统计、异常问诊 | 问诊运营 |
| 处方管理 | 处方统计 | 数据分析 |
| 首页配置 | 广告位、专题管理 | 运营完善 |
| 财务管理 | 医生结算 | 财务完善 |
| 系统设置 | 权限管理、操作日志 | 安全合规 |
| 消息中心 | 审核提醒、预警通知 | 效率提升 |

### 3.3 P2 - 优化功能（后续迭代）

| 模块 | 功能 | 业务价值 |
|------|------|---------|
| 仪表盘 | 数据报表 | 数据分析 |
| 医生管理 | 评价管理 | 医生质量 |
| 门店管理 | 门店统计 | 门店分析 |
| 内容管理 | 公告、资讯、帮助中心 | 内容运营 |
| 运营管理 | 意见反馈、投诉、症状题库、AI配置 | 用户体验 |

---

## 四、API接口清单

### 4.1 用户管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取用户列表 | GET | /admin/users | 分页查询 |
| 获取用户详情 | GET | /admin/users/{id} | 详细信息 |
| 更新用户状态 | PUT | /admin/users/{id}/status | 启用/禁用 |
| 获取实名认证列表 | GET | /admin/users/auth | 待审核列表 |
| 审核实名认证 | PUT | /admin/users/auth/{id}/audit | 通过/驳回 |
| 获取就诊人列表 | GET | /admin/patients | 分页查询 |
| 获取就诊人详情 | GET | /admin/patients/{id} | 详细信息 |

### 4.2 医生管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取医生列表 | GET | /admin/doctors | 分页查询 |
| 获取医生详情 | GET | /admin/doctors/{id} | 详细信息 |
| 获取入驻申请列表 | GET | /admin/doctors/audit | 待审核列表 |
| 审核入驻申请 | PUT | /admin/doctors/audit/{id} | 通过/驳回 |
| 获取排班列表 | GET | /admin/doctors/{id}/schedule | 排班查询 |
| 更新排班 | PUT | /admin/doctors/{id}/schedule | 排班配置 |
| 获取科室列表 | GET | /admin/departments | 科室列表 |
| 创建科室 | POST | /admin/departments | 新增科室 |
| 更新科室 | PUT | /admin/departments/{id} | 编辑科室 |
| 获取医生评价 | GET | /admin/doctors/{id}/reviews | 评价列表 |
| 回复评价 | POST | /admin/doctors/reviews/{id}/reply | 评价回复 |

### 4.3 药品管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取药品列表 | GET | /admin/drugs | 分页查询 |
| 创建药品 | POST | /admin/drugs | 新增药品 |
| 更新药品 | PUT | /admin/drugs/{id} | 编辑药品 |
| 上下架药品 | PUT | /admin/drugs/{id}/status | 状态变更 |
| 获取分类列表 | GET | /admin/drugs/categories | 分类树 |
| 创建分类 | POST | /admin/drugs/categories | 新增分类 |
| 更新分类 | PUT | /admin/drugs/categories/{id} | 编辑分类 |
| 获取品牌列表 | GET | /admin/drugs/brands | 品牌列表 |
| 创建品牌 | POST | /admin/drugs/brands | 新增品牌 |
| 更新品牌 | PUT | /admin/drugs/brands/{id} | 编辑品牌 |

### 4.4 门店管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取门店列表 | GET | /admin/stores | 分页查询 |
| 获取门店详情 | GET | /admin/stores/{id} | 详细信息 |
| 审核门店 | PUT | /admin/stores/{id}/audit | 通过/驳回 |
| 获取门店药品 | GET | /admin/stores/{id}/drugs | 药品列表 |
| 关联门店药品 | POST | /admin/stores/{id}/drugs | 添加药品 |

### 4.5 订单管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取订单列表 | GET | /admin/orders | 分页查询 |
| 获取订单详情 | GET | /admin/orders/{id} | 详细信息 |
| 更新订单状态 | PUT | /admin/orders/{id}/status | 状态变更 |
| 获取退款列表 | GET | /admin/refunds | 分页查询 |
| 审核退款 | PUT | /admin/refunds/{id}/audit | 通过/驳回 |
| 获取异常订单 | GET | /admin/orders/abnormal | 异常列表 |

### 4.6 问诊管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取问诊列表 | GET | /admin/consultations | 分页查询 |
| 获取问诊详情 | GET | /admin/consultations/{id} | 详细信息 |
| 分配问诊 | PUT | /admin/consultations/{id}/assign | 分配医生 |
| 获取问诊统计 | GET | /admin/consultations/stats | 统计数据 |
| 获取异常问诊 | GET | /admin/consultations/abnormal | 异常列表 |

### 4.7 处方管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取处方列表 | GET | /admin/prescriptions | 分页查询 |
| 获取处方详情 | GET | /admin/prescriptions/{id} | 详细信息 |
| 审核处方 | PUT | /admin/prescriptions/{id}/audit | 通过/驳回 |
| 获取处方模板 | GET | /admin/prescriptions/templates | 模板列表 |
| 创建处方模板 | POST | /admin/prescriptions/templates | 新增模板 |
| 更新处方模板 | PUT | /admin/prescriptions/templates/{id} | 编辑模板 |
| 获取处方统计 | GET | /admin/prescriptions/stats | 统计数据 |

### 4.8 首页配置

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取首页配置 | GET | /admin/home/config | 完整配置 |
| 保存首页配置 | PUT | /admin/home/config | 保存配置 |
| 获取Tab列表 | GET | /admin/home/tabs | Tab列表 |
| 创建Tab | POST | /admin/home/tabs | 新增Tab |
| 更新Tab | PUT | /admin/home/tabs/{id} | 编辑Tab |
| 删除Tab | DELETE | /admin/home/tabs/{id} | 删除Tab |
| 获取模块列表 | GET | /admin/home/sections | 模块列表 |
| 创建模块 | POST | /admin/home/sections | 新增模块 |
| 更新模块 | PUT | /admin/home/sections/{id} | 编辑模块 |
| 删除模块 | DELETE | /admin/home/sections/{id} | 删除模块 |
| 获取轮播图列表 | GET | /admin/home/banners | 轮播图列表 |
| 创建轮播图 | POST | /admin/home/banners | 新增轮播图 |
| 更新轮播图 | PUT | /admin/home/banners/{id} | 编辑轮播图 |
| 删除轮播图 | DELETE | /admin/home/banners/{id} | 删除轮播图 |
| 获取金刚位列表 | GET | /admin/home/kingkongs | 金刚位列表 |
| 创建金刚位 | POST | /admin/home/kingkongs | 新增金刚位 |
| 更新金刚位 | PUT | /admin/home/kingkongs/{id} | 编辑金刚位 |
| 删除金刚位 | DELETE | /admin/home/kingkongs/{id} | 删除金刚位 |
| 获取广告位列表 | GET | /admin/home/ads | 广告位列表 |
| 创建广告位 | POST | /admin/home/ads | 新增广告位 |
| 更新广告位 | PUT | /admin/home/ads/{id} | 编辑广告位 |
| 删除广告位 | DELETE | /admin/home/ads/{id} | 删除广告位 |
| 获取专题列表 | GET | /admin/home/topics | 专题列表 |
| 创建专题 | POST | /admin/home/topics | 新增专题 |
| 更新专题 | PUT | /admin/home/topics/{id} | 编辑专题 |
| 删除专题 | DELETE | /admin/home/topics/{id} | 删除专题 |
| 获取发布版本 | GET | /admin/home/releases | 版本列表 |
| 发布配置 | POST | /admin/home/releases | 发布配置 |
| 回滚版本 | POST | /admin/home/releases/{id}/rollback | 回滚 |

### 4.9 财务管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取收入统计 | GET | /admin/finance/income | 统计数据 |
| 获取交易流水 | GET | /admin/finance/transactions | 流水列表 |
| 获取提现列表 | GET | /admin/finance/withdrawals | 提现列表 |
| 审核提现 | PUT | /admin/finance/withdrawals/{id}/audit | 通过/驳回 |
| 获取医生结算 | GET | /admin/finance/doctor-settlements | 结算列表 |
| 创建结算 | POST | /admin/finance/doctor-settlements | 生成结算 |

### 4.10 内容管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取Banner列表 | GET | /admin/content/banners | Banner列表 |
| 创建Banner | POST | /admin/content/banners | 新增Banner |
| 更新Banner | PUT | /admin/content/banners/{id} | 编辑Banner |
| 删除Banner | DELETE | /admin/content/banners/{id} | 删除Banner |
| 获取公告列表 | GET | /admin/content/notices | 公告列表 |
| 创建公告 | POST | /admin/content/notices | 新增公告 |
| 更新公告 | PUT | /admin/content/notices/{id} | 编辑公告 |
| 获取资讯列表 | GET | /admin/content/articles | 资讯列表 |
| 创建资讯 | POST | /admin/content/articles | 新增资讯 |
| 更新资讯 | PUT | /admin/content/articles/{id} | 编辑资讯 |

### 4.11 运营管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取反馈列表 | GET | /admin/operation/feedbacks | 反馈列表 |
| 处理反馈 | PUT | /admin/operation/feedbacks/{id} | 处理回复 |
| 获取投诉列表 | GET | /admin/operation/complaints | 投诉列表 |
| 处理投诉 | PUT | /admin/operation/complaints/{id} | 处理回复 |
| 获取症状题库 | GET | /admin/operation/symptoms | 题目列表 |
| 创建症状题 | POST | /admin/operation/symptoms | 新增题目 |
| 更新症状题 | PUT | /admin/operation/symptoms/{id} | 编辑题目 |
| 获取AI配置 | GET | /admin/operation/ai-config | AI配置 |
| 更新AI配置 | PUT | /admin/operation/ai-config | 保存配置 |

### 4.12 系统设置

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取基础配置 | GET | /admin/settings/basic | 基础配置 |
| 更新基础配置 | PUT | /admin/settings/basic | 保存配置 |
| 获取支付配置 | GET | /admin/settings/payment | 支付配置 |
| 更新支付配置 | PUT | /admin/settings/payment | 保存配置 |
| 获取角色列表 | GET | /admin/settings/roles | 角色列表 |
| 创建角色 | POST | /admin/settings/roles | 新增角色 |
| 更新角色 | PUT | /admin/settings/roles/{id} | 编辑角色 |
| 获取用户列表 | GET | /admin/settings/users | 管理员列表 |
| 创建管理员 | POST | /admin/settings/users | 新增管理员 |
| 更新管理员 | PUT | /admin/settings/users/{id} | 编辑管理员 |
| 获取操作日志 | GET | /admin/settings/logs | 日志列表 |

---

## 五、数据库设计概要

### 5.1 核心数据表

| 表名 | 说明 | 主要字段 |
|------|------|---------|
| admin_user | 管理员账号 | id, username, password, role_id, status |
| admin_role | 角色 | id, name, permissions, status |
| admin_operation_log | 操作日志 | id, user_id, action, target, detail, created_at |

### 5.2 用户相关

| 表名 | 说明 | 主要字段 |
|------|------|---------|
| user | 用户 | id, phone, nickname, avatar, real_name_status, status, created_at |
| patient | 就诊人 | id, user_id, name, gender, age, id_card, allergy_history, medical_history |
| real_name_auth | 实名认证 | id, user_id, real_name, id_card, front_image, back_image, status, audit_time |

### 5.3 医生相关

| 表名 | 说明 | 主要字段 |
|------|------|---------|
| doctor | 医生 | id, name, phone, avatar, department_id, title, hospital, rating, service_count, status |
| doctor_audit | 入驻审核 | id, doctor_id, id_card_front, id_card_back, certificate, license, status, audit_opinion, audit_time |
| doctor_schedule | 排班 | id, doctor_id, date, time_slots, status |
| department | 科室 | id, name, parent_id, sort_order, icon, status |
| doctor_review | 评价 | id, doctor_id, user_id, consultation_id, rating, content, reply, created_at |

### 5.4 药品相关

| 表名 | 说明 | 主要字段 |
|------|------|---------|
| drug | 药品 | id, name, specification, manufacturer, price, stock, category_id, brand_id, is_rx, status |
| drug_category | 分类 | id, name, parent_id, sort_order, icon, status |
| drug_brand | 品牌 | id, name, logo, description, status |

### 5.5 门店相关

| 表名 | 说明 | 主要字段 |
|------|------|---------|
| store | 门店 | id, name, address, contact, phone, license_no, status, created_at |
| store_audit | 门店审核 | id, store_id, business_license, drug_license, gsp_cert, status, audit_opinion |
| store_drug | 门店药品 | id, store_id, drug_id, stock, price, status |

### 5.6 订单相关

| 表名 | 说明 | 主要字段 |
|------|------|---------|
| order | 订单 | id, order_no, user_id, store_id, total_amount, pay_amount, status, pay_type, created_at |
| order_item | 订单明细 | id, order_id, drug_id, name, specification, quantity, price |
| refund | 退款 | id, refund_no, order_id, user_id, amount, reason, status, audit_time |

### 5.7 问诊相关

| 表名 | 说明 | 主要字段 |
|------|------|---------|
| consultation | 问诊 | id, user_id, doctor_id, department_id, type, symptom, status, response_time, created_at |
| consultation_message | 问诊消息 | id, consultation_id, sender_type, sender_id, content, message_type, created_at |

### 5.8 处方相关

| 表名 | 说明 | 主要字段 |
|------|------|---------|
| prescription | 处方 | id, prescription_no, user_id, doctor_id, consultation_id, diagnosis, status, audit_time |
| prescription_item | 处方明细 | id, prescription_id, drug_id, name, specification, dosage, frequency, quantity |
| prescription_template | 处方模板 | id, name, doctor_id, diagnosis, drugs_json, status |

### 5.9 首页配置相关

| 表名 | 说明 | 主要字段 |
|------|------|---------|
| home_page_config | 首页配置 | id, config_json, version, status, created_by, created_at |
| home_tab | Tab配置 | id, name, icon, active_icon, visible, sort_order, theme_json |
| home_section | 模块配置 | id, section_type, title, subtitle, visible, sort_order, config_json, content_json |
| home_tab_section | Tab-模块关联 | id, tab_id, section_id, sort_order |
| home_banner | 轮播图 | id, section_id, image_url, title, link_type, link_value, start_time, end_time, sort_order |
| home_kingkong | 金刚位 | id, section_id, name, icon_url, icon_type, link_type, link_value, badge, sort_order |
| home_ad_slot | 广告位 | id, section_id, image_url, title, layout, link_type, link_value, start_time, end_time |
| home_topic | 专题 | id, section_id, title, subtitle, layout, show_title, show_more, more_link |
| home_topic_item | 专题项 | id, topic_id, title, subtitle, image_url, tag, tag_color, link_type, link_value |
| home_release | 发布版本 | id, version, description, config_json, status, released_by, released_at |

### 5.10 财务相关

| 表名 | 说明 | 主要字段 |
|------|------|---------|
| transaction | 交易流水 | id, transaction_no, order_id, user_id, amount, type, status, created_at |
| withdrawal | 提现 | id, withdrawal_no, user_id, amount, account_info, status, audit_time |
| doctor_settlement | 医生结算 | id, doctor_id, period, income_amount, platform_fee, settlement_amount, status |

### 5.11 内容相关

| 表名 | 说明 | 主要字段 |
|------|------|---------|
| content_banner | 内容Banner | id, title, image_url, link_type, link_value, start_time, end_time, sort_order |
| content_notice | 公告 | id, title, content, type, status, publish_time |
| content_article | 资讯 | id, title, content, cover_image, category_id, status, publish_time |

### 5.12 运营相关

| 表名 | 说明 | 主要字段 |
|------|------|---------|
| feedback | 意见反馈 | id, user_id, type, content, images, status, reply, created_at |
| complaint | 投诉 | id, user_id, target_type, target_id, reason, content, status, handle_result, created_at |
| symptom_question | 症状题 | id, question, options_json, answer, category, sort_order |
| ai_config | AI配置 | id, config_key, config_value, description, updated_at |

---

## 六、非功能性需求

### 6.1 性能要求

| 指标 | 要求 |
|------|------|
| 页面加载时间 | < 2秒 |
| 列表查询响应时间 | < 1秒 |
| 并发用户数 | 支持100+同时在线 |
| 数据导出 | 支持万级数据导出 |

### 6.2 安全要求

| 要求 | 说明 |
|------|------|
| 权限控制 | 基于角色的访问控制（RBAC） |
| 数据加密 | 敏感数据加密存储 |
| 操作审计 | 全量操作日志记录 |
| 防SQL注入 | 参数化查询 |
| XSS防护 | 输入输出过滤 |

### 6.3 可用性要求

| 要求 | 说明 |
|------|------|
| 系统可用性 | 99.9% |
| 数据备份 | 每日自动备份 |
| 容灾恢复 | 故障恢复时间 < 4小时 |

---

## 七、实施计划

### 7.1 阶段划分

| 阶段 | 时间 | 内容 | 交付物 |
|------|------|------|--------|
| 第一阶段 | 2周 | 仪表盘、用户管理、药品管理、订单管理 | 基础管理功能 |
| 第二阶段 | 2周 | 医生管理、问诊管理、处方管理 | 问诊业务管理 |
| 第三阶段 | 2周 | 首页配置、内容管理 | 运营配置能力 |
| 第四阶段 | 2周 | 门店管理、财务管理、系统设置 | 完善管理能力 |
| 第五阶段 | 1周 | 运营管理、消息中心、优化迭代 | 体验优化 |

### 7.2 里程碑

| 里程碑 | 时间 | 标志 |
|--------|------|------|
| M1 | 第2周 | 基础管理功能上线 |
| M2 | 第4周 | 问诊业务管理上线 |
| M3 | 第6周 | 运营配置能力上线 |
| M4 | 第8周 | 完整管理平台上线 |
| M5 | 第9周 | 验收交付 |

---

## 八、风险与应对

| 风险 | 影响 | 应对措施 |
|------|------|---------|
| 需求变更 | 进度延期 | 建立需求变更流程，评估影响后调整 |
| 数据迁移 | 数据丢失 | 制定详细迁移方案，充分测试 |
| 性能瓶颈 | 体验差 | 前期进行性能评估，预留优化时间 |
| 合规风险 | 法律风险 | 处方审核、资质审核严格把关 |

---

## 附录

### A. 术语表

| 术语 | 说明 |
|------|------|
| GMV | 商品交易总额 |
| OTC | 非处方药 |
| Rx | 处方药 |
| GSP | 药品经营质量管理规范 |
| T+7 | 交易后7天结算 |
| RBAC | 基于角色的访问控制 |

### B. 参考文档

- 《药品网络销售监督管理办法》
- 《互联网诊疗管理办法》
- 《电子处方管理规范》
