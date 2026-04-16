# DrugMall患者端前端页面测试报告

**测试时间**: 2026-04-09 11:26:27
**测试环境**: http://localhost:3000

## 测试汇总

| 页面 | 状态 | 加载时间 | 缺失元素 | 错误 |
|------|------|----------|----------|------|
| 首页 | ✅ ok | 0.94s | 3 | 0 |
| 药品详情页 | ✅ ok | 0.61s | 2 | 0 |
| 购物车 | ✅ ok | 0.61s | 3 | 0 |
| 订单确认 | ✅ ok | 0.58s | 3 | 0 |
| 订单列表 | ✅ ok | 0.61s | 3 | 0 |
| 个人中心 | ✅ ok | 0.58s | 3 | 0 |
| 搜索页 | ✅ ok | 0.63s | 1 | 0 |
| 分类页 | ✅ ok | 0.56s | 0 | 0 |
| 药店详情 | ✅ ok | 0.55s | 3 | 0 |
| 问诊页 | ✅ ok | 0.55s | 3 | 0 |
| 地址管理 | ✅ ok | 0.58s | 2 | 0 |

### 统计

- 通过: 11 个页面
- 404错误: 0 个页面
- 白屏: 0 个页面
- 其他错误: 0 个页面

---

## 详细结果

### 首页

- **路径**: /home
- **状态**: ok
- **加载时间**: 0.94s
- **截图**: D:\aiProject\workspace-opc\DrugMall\frontend\test-screenshots\首页.png

**缺失元素**:

- `.banner`
- `.service-area`
- `.drug-list`

---
### 药品详情页

- **路径**: /drug/1
- **状态**: ok
- **加载时间**: 0.61s
- **截图**: D:\aiProject\workspace-opc\DrugMall\frontend\test-screenshots\药品详情页.png

**缺失元素**:

- `.drug-detail`
- `.drug-info`

---
### 购物车

- **路径**: /cart
- **状态**: ok
- **加载时间**: 0.61s
- **截图**: D:\aiProject\workspace-opc\DrugMall\frontend\test-screenshots\购物车.png

**缺失元素**:

- `.cart-page`
- `.cart-list`
- `.cart-footer`

---
### 订单确认

- **路径**: /order/confirm
- **状态**: ok
- **加载时间**: 0.58s
- **截图**: D:\aiProject\workspace-opc\DrugMall\frontend\test-screenshots\订单确认.png

**缺失元素**:

- `.order-confirm`
- `.address-section`
- `.drug-list`

---
### 订单列表

- **路径**: /order/list
- **状态**: ok
- **加载时间**: 0.61s
- **截图**: D:\aiProject\workspace-opc\DrugMall\frontend\test-screenshots\订单列表.png

**缺失元素**:

- `.order-list`
- `.order-tabs`
- `.order-item`

---
### 个人中心

- **路径**: /user
- **状态**: ok
- **加载时间**: 0.58s
- **截图**: D:\aiProject\workspace-opc\DrugMall\frontend\test-screenshots\个人中心.png

**缺失元素**:

- `.user-page`
- `.user-header`
- `.menu-list`

---
### 搜索页

- **路径**: /search
- **状态**: ok
- **加载时间**: 0.63s
- **截图**: D:\aiProject\workspace-opc\DrugMall\frontend\test-screenshots\搜索页.png

**缺失元素**:

- `.search-history`

---
### 分类页

- **路径**: /category
- **状态**: ok
- **加载时间**: 0.56s
- **截图**: D:\aiProject\workspace-opc\DrugMall\frontend\test-screenshots\分类页.png

---
### 药店详情

- **路径**: /store/1
- **状态**: ok
- **加载时间**: 0.55s
- **截图**: D:\aiProject\workspace-opc\DrugMall\frontend\test-screenshots\药店详情.png

**缺失元素**:

- `.store-detail`
- `.store-header`
- `.drug-list`

---
### 问诊页

- **路径**: /inquiry
- **状态**: ok
- **加载时间**: 0.55s
- **截图**: D:\aiProject\workspace-opc\DrugMall\frontend\test-screenshots\问诊页.png

**缺失元素**:

- `.inquiry-page`
- `.doctor-list`
- `.symptom-tags`

---
### 地址管理

- **路径**: /address
- **状态**: ok
- **加载时间**: 0.58s
- **截图**: D:\aiProject\workspace-opc\DrugMall\frontend\test-screenshots\地址管理.png

**缺失元素**:

- `.address-list`
- `.address-item`

---