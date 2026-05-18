# DrugMall API 文档

## 文档说明

本文档包含 DrugMall 药品电商平台三个系统的 API 接口文档：
- 管理后台 API（drugmall-admin-backend）
- 患者端 API（backend）
- 医生端 API（backend）

所有接口均使用 RESTful 风格，返回 JSON 格式数据。

---

## 一、管理后台 API

### 基础路径：`/admin`

### 1.1 认证模块

#### 1.1.1 管理员登录
- **接口路径**：`POST /admin/auth/login`
- **请求参数**：
  ```json
  {
    "username": "admin",
    "password": "123456"
  }
  ```
- **返回示例**：
  ```json
  {
    "code": 200,
    "message": "登录成功",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "userInfo": {
        "id": "1",
        "username": "admin",
        "nickname": "超级管理员",
        "avatar": "",
        "roles": ["admin"]
      }
    }
  }
  ```

#### 1.1.2 获取用户信息
- **接口路径**：`GET /admin/auth/info`
- **请求头**：`Authorization: Bearer {token}`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "id": "1",
      "username": "admin",
      "nickname": "超级管理员",
      "roles": ["admin"],
      "permissions": ["*"]
    }
  }
  ```

### 1.2 仪表盘模块

#### 1.2.1 获取数据概览
- **接口路径**：`GET /admin/dashboard/overview`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "cards": [
        {"title":"今日GMV","value":128456.00,"change":"+12.5%","trend":"up","icon":"Money","color":"#409eff"},
        {"title":"今日订单","value":1286,"change":"+8.3%","trend":"up","icon":"ShoppingCart","color":"#67c23a"}
      ],
      "categoryData": [
        {"name":"感冒药","value":32,"color":"#409eff"}
      ]
    }
  }
  ```

#### 1.2.2 获取GMV趋势
- **接口路径**：`GET /admin/dashboard/gmv-trend`
- **请求参数**：`timeRange` (day/week/month)
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "dates": ["1日","2日","3日"],
      "gmv": [95000,102000,98000],
      "orders": [850,920,880]
    }
  }
  ```

#### 1.2.3 获取订单来源分布
- **接口路径**：`GET /admin/dashboard/order-source`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": [
      {"name":"App端","value":45},
      {"name":"小程序","value":30}
    ]
  }
  ```

### 1.3 用户管理模块

#### 1.3.1 获取用户列表
- **接口路径**：`GET /admin/users`
- **请求参数**：
  - `pageNum`: 页码
  - `pageSize`: 每页数量
  - `keyword`: 搜索关键词
  - `status`: 用户状态
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "list": [
        {
          "id": "1",
          "username": "zhangsan",
          "nickname": "张三",
          "phone": "13800138001",
          "email": "zhangsan@example.com",
          "status": 1,
          "createTime": "2024-01-15 10:30:00"
        }
      ],
      "total": 25,
      "pageNum": 1,
      "pageSize": 10
    }
  }
  ```

#### 1.3.2 获取用户详情
- **接口路径**：`GET /admin/users/{id}`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "id": "1",
      "username": "zhangsan",
      "nickname": "张三",
      "phone": "13800138001",
      "email": "zhangsan@example.com",
      "avatar": "",
      "status": 1,
      "orderCount": 12,
      "totalSpent": 2580.00
    }
  }
  ```

#### 1.3.3 更新用户状态
- **接口路径**：`PUT /admin/users/{id}/status`
- **请求参数**：
  ```json
  {
    "status": 0
  }
  ```

#### 1.3.4 删除用户
- **接口路径**：`DELETE /admin/users/{id}`

#### 1.3.5 获取实名认证列表
- **接口路径**：`GET /admin/users/auth`
- **请求参数**：
  - `pageNum`: 页码
  - `pageSize`: 每页数量
  - `status`: 认证状态
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "list": [
        {
          "id": "1",
          "userId": "1",
          "realName": "张三",
          "idCard": "110101199005201234",
          "status": "pending",
          "createTime": "2024-01-15 10:30:00"
        }
      ],
      "total": 10
    }
  }
  ```

#### 1.3.6 审核实名认证
- **接口路径**：`POST /admin/users/auth/{id}/audit`
- **请求参数**：
  ```json
  {
    "status": "approved",
    "reason": ""
  }
  ```

### 1.4 药品管理模块

#### 1.4.1 获取药品列表
- **接口路径**：`GET /admin/products`
- **请求参数**：
  - `pageNum`: 页码
  - `pageSize`: 每页数量
  - `keyword`: 搜索关键词
  - `categoryId`: 分类ID
  - `brandId`: 品牌ID
  - `isRx`: 是否处方药
  - `status`: 状态
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "list": [
        {
          "id": "1",
          "productCode": "YP20240001",
          "productName": "阿莫西林胶囊",
          "categoryId": "11",
          "categoryName": "感冒药",
          "brandId": "1",
          "brandName": "修正药业",
          "price": 25.50,
          "stock": 500,
          "isRx": 0,
          "status": 1
        }
      ],
      "total": 35
    }
  }
  ```

#### 1.4.2 获取药品详情
- **接口路径**：`GET /admin/products/{id}`

#### 1.4.3 创建药品
- **接口路径**：`POST /admin/products`
- **请求参数**：
  ```json
  {
    "productCode": "YP20240036",
    "productName": "新药品",
    "categoryId": "11",
    "brandId": "1",
    "price": 30.00,
    "stock": 100,
    "isRx": 0
  }
  ```

#### 1.4.4 更新药品
- **接口路径**：`PUT /admin/products/{id}`

#### 1.4.5 删除药品
- **接口路径**：`DELETE /admin/products/{id}`

#### 1.4.6 更新药品状态
- **接口路径**：`PUT /admin/products/{id}/status`
- **请求参数**：
  ```json
  {
    "status": 1
  }
  ```

#### 1.4.7 获取分类列表
- **接口路径**：`GET /admin/categories`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": "1",
        "name": "药品",
        "icon": "Medicine",
        "sort": 1,
        "children": [
          {"id": "11", "name": "感冒药", "parentId": "1"}
        ]
      }
    ]
  }
  ```

#### 1.4.8 创建分类
- **接口路径**：`POST /admin/categories`

#### 1.4.9 更新分类
- **接口路径**：`PUT /admin/categories/{id}`

#### 1.4.10 删除分类
- **接口路径**：`DELETE /admin/categories/{id}`

#### 1.4.11 获取品牌列表
- **接口路径**：`GET /admin/brands`
- **请求参数**：
  - `pageNum`: 页码
  - `pageSize`: 每页数量
  - `keyword`: 搜索关键词
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "list": [
        {
          "id": 1,
          "name": "修正药业",
          "logo": "",
          "description": "中国医药百强企业",
          "sortOrder": 1,
          "status": 1
        }
      ],
      "total": 5
    }
  }
  ```

#### 1.4.12 创建品牌
- **接口路径**：`POST /admin/brands`

#### 1.4.13 更新品牌
- **接口路径**：`PUT /admin/brands/{id}`

#### 1.4.14 删除品牌
- **接口路径**：`DELETE /admin/brands/{id}`

### 1.5 订单管理模块

#### 1.5.1 获取订单列表
- **接口路径**：`GET /admin/orders`
- **请求参数**：
  - `pageNum`: 页码
  - `pageSize`: 每页数量
  - `keyword`: 搜索关键词
  - `status`: 订单状态
  - `startTime`: 开始时间
  - `endTime`: 结束时间
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "list": [
        {
          "id": "1",
          "orderNo": "DD202403200001",
          "userId": "1",
          "username": "zhangsan",
          "nickname": "张三",
          "phone": "13800138001",
          "totalAmount": 258.00,
          "payAmount": 248.00,
          "status": 4,
          "createTime": "2024-03-20 10:25:00"
        }
      ],
      "total": 10
    }
  }
  ```

#### 1.5.2 获取订单统计
- **接口路径**：`GET /admin/orders/stats`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "todayOrders": 1234,
      "todaySales": 56789.00,
      "pendingShipment": 89,
      "refunding": 12
    }
  }
  ```

#### 1.5.3 获取订单详情
- **接口路径**：`GET /admin/orders/{id}`

#### 1.5.4 订单发货
- **接口路径**：`POST /admin/orders/ship`
- **请求参数**：
  ```json
  {
    "orderId": "1",
    "company": "顺丰快递",
    "trackingNo": "SF1234567890"
  }
  ```

#### 1.5.5 取消订单
- **接口路径**：`POST /admin/orders/{id}/cancel`
- **请求参数**：
  ```json
  {
    "reason": "管理员取消"
  }
  ```

#### 1.5.6 处理退款
- **接口路径**：`POST /admin/orders/refund`
- **请求参数**：
  ```json
  {
    "orderId": "1",
    "agree": true,
    "amount": 248.00,
    "reason": ""
  }
  ```

#### 1.5.7 获取物流轨迹
- **接口路径**：`GET /admin/orders/{orderId}/traces`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "time": "2024-03-22 09:00:00",
        "status": "已签收",
        "desc": "您的包裹已由本人签收"
      }
    ]
  }
  ```

### 1.6 财务管理模块

#### 1.6.1 获取财务统计
- **接口路径**：`GET /admin/finance/statistics`
- **请求参数**：
  - `timeRange`: 时间范围
  - `startDate`: 开始日期
  - `endDate`: 结束日期
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "totalIncome": 128000.00,
      "monthIncome": 8500.00,
      "todayIncome": 1200.00,
      "incomeTrend": [
        {"date": "2024-03-01", "income": 3500.00}
      ]
    }
  }
  ```

#### 1.6.2 获取交易流水
- **接口路径**：`GET /admin/finance/transactions`
- **请求参数**：
  - `pageNum`: 页码
  - `pageSize`: 每页数量
  - `type`: 交易类型

#### 1.6.3 获取提现列表
- **接口路径**：`GET /admin/finance/withdrawal`
- **请求参数**：
  - `pageNum`: 页码
  - `pageSize`: 每页数量
  - `status`: 提现状态

#### 1.6.4 审核提现
- **接口路径**：`POST /admin/finance/withdrawal/{id}/audit`
- **请求参数**：
  ```json
  {
    "status": "approved",
    "reason": ""
  }
  ```

### 1.7 内容管理模块

#### 1.7.1 获取Banner列表
- **接口路径**：`GET /admin/content/banners`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": "1",
        "title": "春季健康节",
        "image": "",
        "link": "/activity/spring",
        "sort": 1,
        "status": 1
      }
    ]
  }
  ```

#### 1.7.2 创建Banner
- **接口路径**：`POST /admin/content/banners`

#### 1.7.3 更新Banner
- **接口路径**：`PUT /admin/content/banners/{id}`

#### 1.7.4 删除Banner
- **接口路径**：`DELETE /admin/content/banners/{id}`

#### 1.7.5 获取公告列表
- **接口路径**：`GET /admin/content/notices`

#### 1.7.6 创建公告
- **接口路径**：`POST /admin/content/notices`

#### 1.7.7 获取资讯列表
- **接口路径**：`GET /admin/content/articles`

### 1.8 系统设置模块

#### 1.8.1 获取基础配置
- **接口路径**：`GET /admin/settings/basic`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "siteName": "DrugMall药品电商",
      "siteLogo": "",
      "servicePhone": "400-123-4567",
      "icp": "京ICP备12345678号"
    }
  }
  ```

#### 1.8.2 保存基础配置
- **接口路径**：`POST /admin/settings/basic`

#### 1.8.3 获取支付配置
- **接口路径**：`GET /admin/settings/payment`

#### 1.8.4 保存支付配置
- **接口路径**：`POST /admin/settings/payment`

---

## 二、患者端 API

### 基础路径：`/api`

### 2.1 用户模块

#### 2.1.1 发送验证码
- **接口路径**：`POST /api/user/send-code`
- **请求参数**：
  ```json
  {
    "phone": "13800138000"
  }
  ```

#### 2.1.2 用户登录/注册
- **接口路径**：`POST /api/user/login`
- **请求参数**：
  ```json
  {
    "phone": "13800138000",
    "code": "123456"
  }
  ```
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "userInfo": {
        "id": "1",
        "phone": "13800138000",
        "nickname": "健康小达人"
      }
    }
  }
  ```

#### 2.1.3 获取用户信息
- **接口路径**：`GET /api/user/info`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "id": "1",
      "phone": "13800138000",
      "nickname": "健康小达人",
      "avatar": "",
      "email": "user@example.com",
      "birthday": "1990-05-20",
      "gender": 1,
      "realName": "张三",
      "isRealNameAuth": true,
      "balance": 1000.00,
      "points": 500
    }
  }
  ```

#### 2.1.4 更新用户信息
- **接口路径**：`PUT /api/user/info`
- **请求参数**：
  ```json
  {
    "nickname": "新昵称",
    "avatar": "",
    "birthday": "1990-05-20",
    "gender": 1
  }
  ```

#### 2.1.5 实名认证
- **接口路径**：`POST /api/user/real-name-auth`
- **请求参数**：
  ```json
  {
    "realName": "张三",
    "idCard": "110101199005201234"
  }
  ```

### 2.2 地址模块

#### 2.2.1 获取地址列表
- **接口路径**：`GET /api/address/list`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": "1",
        "userId": "1",
        "name": "张三",
        "phone": "13800138000",
        "province": "北京市",
        "city": "北京市",
        "district": "朝阳区",
        "detail": "某某小区1号楼1单元101室",
        "tag": "家",
        "isDefault": true
      }
    ]
  }
  ```

#### 2.2.2 添加地址
- **接口路径**：`POST /api/address`
- **请求参数**：
  ```json
  {
    "name": "张三",
    "phone": "13800138000",
    "province": "北京市",
    "city": "北京市",
    "district": "朝阳区",
    "detail": "某某小区1号楼1单元101室",
    "tag": "家",
    "isDefault": true
  }
  ```

#### 2.2.3 更新地址
- **接口路径**：`PUT /api/address/{id}`

#### 2.2.4 删除地址
- **接口路径**：`DELETE /api/address/{id}`

#### 2.2.5 设置默认地址
- **接口路径**：`PUT /api/address/{id}/default`

#### 2.2.6 智能解析地址
- **接口路径**：`POST /api/address/parse`
- **请求参数**：
  ```json
  {
    "text": "张三 13800138000 北京市朝阳区某某小区1号楼"
  }
  ```

### 2.3 药品模块

#### 2.3.1 获取首页数据
- **接口路径**：`GET /api/home`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "banners": [],
      "categories": [],
      "hotDrugs": [],
      "recommendDrugs": []
    }
  }
  ```

#### 2.3.2 获取药品列表
- **接口路径**：`GET /api/drugs`
- **请求参数**：
  - `pageNum`: 页码
  - `pageSize`: 每页数量
  - `categoryId`: 分类ID
  - `keyword`: 搜索关键词
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "list": [
        {
          "id": "1",
          "name": "阿莫西林胶囊",
          "specification": "0.25g*24粒",
          "price": 12.50,
          "originalPrice": 18.00,
          "image": "",
          "isRx": true,
          "sales": 999
        }
      ],
      "total": 8
    }
  }
  ```

#### 2.3.3 获取药品详情
- **接口路径**：`GET /api/drugs/{id}`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "id": "1",
      "name": "阿莫西林胶囊",
      "specification": "0.25g*24粒",
      "manufacturer": "华北制药",
      "price": 12.50,
      "originalPrice": 18.00,
      "image": "",
      "isRx": true,
      "usage": "口服，成人一次0.5g，每6-8小时1次",
      "description": "阿莫西林适用于敏感菌所致的各种感染...",
      "stock": 100,
      "sales": 999
    }
  }
  ```

#### 2.3.4 获取药品分类
- **接口路径**：`GET /api/drugs/categories`

#### 2.3.5 获取热门搜索
- **接口路径**：`GET /api/search/hot`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": [
      {"keyword": "感冒药", "heat": 9999},
      {"keyword": "退烧药", "heat": 8888}
    ]
  }
  ```

#### 2.3.6 搜索建议
- **接口路径**：`GET /api/search/suggest`
- **请求参数**：`keyword`: 搜索关键词

### 2.4 购物车模块

#### 2.4.1 获取购物车列表
- **接口路径**：`GET /api/cart`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": "1",
        "userId": "1",
        "drugId": "2",
        "drugName": "布洛芬缓释胶囊",
        "specification": "0.3g*20粒",
        "price": 15.80,
        "quantity": 2,
        "selected": true,
        "stock": 200
      }
    ]
  }
  ```

#### 2.4.2 添加到购物车
- **接口路径**：`POST /api/cart`
- **请求参数**：
  ```json
  {
    "drugId": "1",
    "quantity": 1
  }
  ```

#### 2.4.3 更新购物车数量
- **接口路径**：`PUT /api/cart/{id}`
- **请求参数**：
  ```json
  {
    "quantity": 2
  }
  ```

#### 2.4.4 删除购物车商品
- **接口路径**：`DELETE /api/cart/{id}`

#### 2.4.5 批量删除购物车商品
- **接口路径**：`POST /api/cart/batch-remove`
- **请求参数**：
  ```json
  {
    "ids": ["1", "2", "3"]
  }
  ```

#### 2.4.6 选择/取消选择购物车商品
- **接口路径**：`PUT /api/cart/{id}/select`
- **请求参数**：
  ```json
  {
    "selected": true
  }
  ```

#### 2.4.7 全选/取消全选
- **接口路径**：`PUT /api/cart/select-all`
- **请求参数**：
  ```json
  {
    "selected": true
  }
  ```

#### 2.4.8 获取购物车结算信息
- **接口路径**：`GET /api/cart/checkout`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "totalAmount": 50.10,
      "discountAmount": 10.00,
      "payAmount": 40.10,
      "items": [],
      "defaultAddress": {},
      "availableCoupons": []
    }
  }
  ```

#### 2.4.9 合并购物车
- **接口路径**：`POST /api/cart/merge`
- **请求参数**：
  ```json
  {
    "items": [
      {"drugId": "1", "quantity": 2}
    ]
  }
  ```

### 2.5 订单模块

#### 2.5.1 创建订单
- **接口路径**：`POST /api/orders`
- **请求参数**：
  ```json
  {
    "addressId": "1",
    "cartItemIds": ["1", "2"],
    "couponId": "1",
    "remark": "备注信息"
  }
  ```
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "orderId": "1",
      "orderNo": "DD202403200001",
      "payAmount": 248.00
    }
  }
  ```

#### 2.5.2 获取订单列表
- **接口路径**：`GET /api/orders`
- **请求参数**：
  - `pageNum`: 页码
  - `pageSize`: 每页数量
  - `status`: 订单状态
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "list": [
        {
          "id": "1",
          "orderNo": "DD202403200001",
          "totalAmount": 258.00,
          "payAmount": 248.00,
          "status": 4,
          "createTime": "2024-03-20 10:25:00",
          "items": []
        }
      ],
      "total": 10
    }
  }
  ```

#### 2.5.3 获取订单详情
- **接口路径**：`GET /api/orders/{id}`

#### 2.5.4 取消订单
- **接口路径**：`POST /api/orders/{id}/cancel`
- **请求参数**：
  ```json
  {
    "reason": "不想要了"
  }
  ```

#### 2.5.5 支付订单
- **接口路径**：`POST /api/orders/{id}/pay`
- **请求参数**：
  ```json
  {
    "payType": 1
  }
  ```

#### 2.5.6 确认收货
- **接口路径**：`POST /api/orders/{id}/confirm`

#### 2.5.7 申请退款
- **接口路径**：`POST /api/orders/{id}/refund`
- **请求参数**：
  ```json
  {
    "reason": "商品有质量问题",
    "amount": 248.00
  }
  ```

#### 2.5.8 获取订单状态数量
- **接口路径**：`GET /api/orders/status-count`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "pendingPayment": 2,
      "pendingShipment": 3,
      "pendingReceipt": 1,
      "pendingReview": 2
    }
  }
  ```

### 2.6 优惠券模块

#### 2.6.1 获取优惠券列表
- **接口路径**：`GET /api/coupons`
- **请求参数**：`status`: 优惠券状态 (unused/used/expired)
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": "1",
        "name": "满100减20",
        "type": "full_reduction",
        "value": 20.00,
        "minAmount": 100.00,
        "startTime": "2024-01-01 00:00:00",
        "endTime": "2024-12-31 23:59:59",
        "status": "unused",
        "description": "全场通用"
      }
    ]
  }
  ```

### 2.7 患者档案模块

#### 2.7.1 获取患者列表
- **接口路径**：`GET /api/patients`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": "1",
        "userId": "1",
        "name": "张三",
        "gender": 1,
        "age": 34,
        "phone": "13800138000",
        "relationship": "本人",
        "allergyHistory": "青霉素过敏",
        "medicalHistory": "高血压",
        "isDefault": true
      }
    ]
  }
  ```

#### 2.7.2 添加患者
- **接口路径**：`POST /api/patients`
- **请求参数**：
  ```json
  {
    "name": "李四",
    "gender": 2,
    "birthday": "1992-03-15",
    "idCard": "110101199203152345",
    "phone": "13900139000",
    "relationship": "配偶",
    "allergyHistory": "",
    "medicalHistory": ""
  }
  ```

#### 2.7.3 更新患者信息
- **接口路径**：`PUT /api/patients/{id}`

#### 2.7.4 删除患者
- **接口路径**：`DELETE /api/patients/{id}`

### 2.8 AI助手模块

#### 2.8.1 AI聊天
- **接口路径**：`POST /api/ai/chat`
- **请求参数**：
  ```json
  {
    "message": "我头疼发烧，应该吃什么药？",
    "sessionId": "session_123"
  }
  ```
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "message": "您好，根据您的症状描述...",
      "recommendedDrugs": [
        {"id": "1", "name": "布洛芬缓释胶囊", "price": 15.80}
      ]
    }
  }
  ```

#### 2.8.2 症状自测
- **接口路径**：`POST /api/ai/symptom-test`
- **请求参数**：
  ```json
  {
    "symptoms": ["头疼", "发烧", "咳嗽"]
  }
  ```

### 2.9 门店模块

#### 2.9.1 获取门店列表
- **接口路径**：`GET /api/stores`
- **请求参数**：
  - `latitude`: 纬度
  - `longitude`: 经度
  - `keyword`: 搜索关键词

#### 2.9.2 获取门店详情
- **接口路径**：`GET /api/stores/{id}`

---

## 三、医生端 API

### 基础路径：`/api`

### 3.1 医生认证模块

#### 3.1.1 医生登录
- **接口路径**：`POST /api/doctor/login`
- **请求参数**：
  ```json
  {
    "phone": "13900001234",
    "password": "123456"
  }
  ```
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "doctorInfo": {
        "id": "DOC001",
        "name": "张医生",
        "title": "主任医师",
        "hospital": "北京协和医院",
        "department": "心内科"
      }
    }
  }
  ```

#### 3.1.2 获取医生信息
- **接口路径**：`GET /api/doctor/info`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "id": "DOC001",
      "name": "张医生",
      "avatar": "",
      "title": "主任医师",
      "hospital": "北京协和医院",
      "department": "心内科",
      "isCertified": true,
      "rating": 4.9,
      "serviceCount": 1280,
      "responseTime": 2,
      "specialties": ["高血压", "冠心病", "心力衰竭"],
      "introduction": "从事心血管内科临床工作15年..."
    }
  }
  ```

#### 3.1.3 更新医生信息
- **接口路径**：`PUT /api/doctor/info`

### 3.2 问诊管理模块

#### 3.2.1 获取问诊列表
- **接口路径**：`GET /api/doctor/consultations`
- **请求参数**：
  - `status`: 问诊状态 (pending/processing/completed/closed)
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": "C001",
        "doctorId": "DOC001",
        "patientId": "P001",
        "patientName": "李*",
        "patientAge": 35,
        "patientGender": "女",
        "type": "图文问诊",
        "status": "pending",
        "symptom": "头疼、发烧三天...",
        "waitTime": "15分钟",
        "isUrgent": true,
        "isRx": true,
        "createTime": "2024-12-07 10:30:00"
      }
    ]
  }
  ```

#### 3.2.2 获取问诊详情
- **接口路径**：`GET /api/doctor/consultations/{id}`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "id": "C001",
      "patientInfo": {
        "id": "P001",
        "name": "李*",
        "age": 35,
        "gender": "女",
        "allergies": "青霉素过敏",
        "medicalHistory": "高血压"
      },
      "symptom": "头疼、发烧三天...",
      "messages": [],
      "prescription": null
    }
  }
  ```

#### 3.2.3 接诊
- **接口路径**：`POST /api/doctor/consultations/{id}/accept`

#### 3.2.4 结束问诊
- **接口路径**：`POST /api/doctor/consultations/{id}/close`

#### 3.2.5 获取待办数量
- **接口路径**：`GET /api/doctor/consultations/todo-count`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "pending": 3,
      "processing": 2,
      "total": 5
    }
  }
  ```

### 3.3 患者管理模块

#### 3.3.1 获取患者列表
- **接口路径**：`GET /api/doctor/patients`
- **请求参数**：
  - `keyword`: 搜索关键词
  - `tag`: 患者标签
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": "P001",
        "name": "李*",
        "age": 62,
        "gender": "女",
        "phone": "138****5678",
        "tags": ["高血压", "糖尿病", "慢病管理", "VIP患者"],
        "diagnosis": ["高血压", "糖尿病"],
        "lastVisit": "2天前",
        "visitCount": 5,
        "isVip": true,
        "allergies": "青霉素过敏",
        "medicalHistory": "高血压5年，糖尿病3年"
      }
    ]
  }
  ```

#### 3.3.2 获取患者详情
- **接口路径**：`GET /api/doctor/patients/{id}`

#### 3.3.3 获取患者病历
- **接口路径**：`GET /api/doctor/patients/{id}/records`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": "MR001",
        "patientId": "P001",
        "date": "2024-12-05 14:30",
        "type": "复诊",
        "diagnosis": "高血压、糖尿病",
        "prescription": "硝苯地平控释片、二甲双胍缓释片",
        "notes": "血压控制良好...",
        "doctor": "张医生"
      }
    ]
  }
  ```

### 3.4 处方管理模块

#### 3.4.1 获取处方列表
- **接口路径**：`GET /api/doctor/prescriptions`
- **请求参数**：
  - `status`: 处方状态 (pending/approved/rejected)
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": "PRES202412070001",
        "patientName": "李*",
        "patientAge": 35,
        "diagnosis": "急性上呼吸道感染、发热",
        "drugs": [
          {
            "name": "布洛芬缓释胶囊",
            "spec": "0.3g*20粒",
            "quantity": 1,
            "dosage": "1粒",
            "frequency": "每日2次"
          }
        ],
        "totalAmount": 62.00,
        "status": "pending",
        "statusText": "待审核",
        "createTime": "2024-12-07 11:30:00"
      }
    ]
  }
  ```

#### 3.4.2 创建处方
- **接口路径**：`POST /api/doctor/prescriptions`
- **请求参数**：
  ```json
  {
    "consultationId": "C001",
    "patientId": "P001",
    "diagnosis": "急性上呼吸道感染",
    "drugs": [
      {
        "name": "布洛芬缓释胶囊",
        "spec": "0.3g*20粒",
        "quantity": 1,
        "dosage": "1粒",
        "frequency": "每日2次",
        "duration": "3天"
      }
    ]
  }
  ```

#### 3.4.3 获取处方详情
- **接口路径**：`GET /api/doctor/prescriptions/{id}`

### 3.5 收入管理模块

#### 3.5.1 获取收入概览
- **接口路径**：`GET /api/doctor/income/overview`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "balance": 5000.00,
      "monthIncome": 8500.00,
      "monthIncomeRatio": 15.2,
      "totalIncome": 128000.00,
      "todayIncome": 1200.00,
      "weekIncome": 5600.00,
      "pendingSettlement": 3000.00,
      "totalWithdraw": 85000.00
    }
  }
  ```

#### 3.5.2 获取收入记录
- **接口路径**：`GET /api/doctor/income/records`
- **请求参数**：
  - `pageNum`: 页码
  - `pageSize`: 每页数量
  - `type`: 收入类型
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "list": [
        {
          "id": "INC202412070001",
          "type": "图文问诊",
          "amount": 80.00,
          "patientName": "李*",
          "time": "2024-12-07 14:30:00",
          "status": "已结算"
        }
      ],
      "total": 50
    }
  }
  ```

#### 3.5.3 获取收入趋势
- **接口路径**：`GET /api/doctor/income/trend`
- **请求参数**：`timeRange`: 时间范围

#### 3.5.4 获取收入构成
- **接口路径**：`GET /api/doctor/income/composition`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": [
      {"type": "图文问诊", "amount": 6000, "percentage": 70.6},
      {"type": "复诊开方", "amount": 2000, "percentage": 23.5}
    ]
  }
  ```

#### 3.5.5 申请提现
- **接口路径**：`POST /api/doctor/income/withdraw`
- **请求参数**：
  ```json
  {
    "amount": 5000.00,
    "method": "bank",
    "accountId": "1"
  }
  ```

#### 3.5.6 获取提现记录
- **接口路径**：`GET /api/doctor/income/withdraw-records`

### 3.6 排班管理模块

#### 3.6.1 获取排班信息
- **接口路径**：`GET /api/doctor/schedule`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "dayOfWeek": 1,
        "morning": true,
        "afternoon": true,
        "evening": false,
        "maxConsultations": 20,
        "bookedCount": 12
      }
    ]
  }
  ```

#### 3.6.2 更新排班
- **接口路径**：`PUT /api/doctor/schedule`

---

## 四、公共接口

### 4.1 文件上传

#### 4.1.1 上传图片
- **接口路径**：`POST /api/upload/image`
- **请求方式**：`multipart/form-data`
- **请求参数**：`file`: 图片文件
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "url": "https://cdn.example.com/images/xxx.jpg",
      "filename": "xxx.jpg"
    }
  }
  ```

### 4.2 IM即时通讯

#### 4.2.1 获取UserSig
- **接口路径**：`GET /api/im/user-sig`
- **返回示例**：
  ```json
  {
    "code": 200,
    "data": {
      "userSig": "eJxTkk1...",
      "expireTime": 86400
    }
  }
  ```

#### 4.2.2 获取会话列表
- **接口路径**：`GET /api/im/conversations`

#### 4.2.3 获取历史消息
- **接口路径**：`GET /api/im/messages`
- **请求参数**：
  - `conversationId`: 会话ID
  - `pageNum`: 页码
  - `pageSize`: 每页数量

#### 4.2.4 发送消息
- **接口路径**：`POST /api/im/messages`
- **请求参数**：
  ```json
  {
    "conversationId": "C2C_patient_1",
    "type": "text",
    "content": "您好，请问有什么可以帮助您的？"
  }
  ```

---

## 二、患者端 API - 门店模块

### 基础路径：`/v1`

### 2.1 门店列表

#### 2.1.1 获取门店列表
- **接口路径**：`GET /v1/stores`
- **接口描述**：获取所有可用门店列表，包含基本信息、评分、距离等
- **请求参数**：无
- **返回示例**：
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [
      {
        "id": "1",
        "name": "海王星辰健康药房(朝阳店)",
        "logo": "",
        "logoText": "海王",
        "logoColor": "#FFD700",
        "rating": 4.8,
        "monthlySales": 1200,
        "distance": 0.8,
        "deliveryTime": 25,
        "tags": [
          {"text": "医保定点", "type": "primary"},
          {"text": "24小时", "type": "success"},
          {"text": "连锁品牌", "type": "info"}
        ],
        "address": "北京市朝阳区建国路88号SOHO现代城底商",
        "phone": "010-85861234",
        "isOpen": true,
        "businessHours": "08:00-22:00",
        "products": [
          {"id": "p1", "name": "阿莫西林", "price": 15.80, "bgColor": "#E3F2FD"},
          {"id": "p2", "name": "布洛芬", "price": 12.50, "bgColor": "#FFF3E0"},
          {"id": "p3", "name": "维生素C", "price": 8.90, "bgColor": "#E8F5E9"}
        ]
      }
    ]
  }
  ```

### 2.2 门店详情

#### 2.2.1 获取门店详情
- **接口路径**：`GET /v1/stores/{id}`
- **接口描述**：根据门店ID获取详细信息，包含在售商品列表、资质认证等
- **路径参数**：
  - `id`: 门店ID
- **返回示例**：
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": "1",
      "name": "海王星辰健康药房(朝阳店)",
      "logo": "",
      "logoText": "海王",
      "logoColor": "#FFD700",
      "rating": 4.8,
      "monthlySales": 1200,
      "distance": 0.8,
      "deliveryTime": 25,
      "tags": [
        {"text": "医保定点", "type": "primary"},
        {"text": "24小时", "type": "success"},
        {"text": "连锁品牌", "type": "info"}
      ],
      "address": "北京市朝阳区建国路88号SOHO现代城底商",
      "phone": "010-85861234",
      "isOpen": true,
      "businessHours": "08:00-22:00",
      "products": [
        {"id": "p1", "name": "阿莫西林", "price": 15.80, "bgColor": "#E3F2FD"}
      ],
      "description": "专业药品零售连锁企业，提供处方药、非处方药、医疗器械、保健品等全品类商品。拥有执业药师团队，提供专业的用药咨询服务。",
      "businessScope": "中成药、化学药制剂、抗生素、生化药品、生物制品（除疫苗）、医疗器械、保健食品",
      "certifications": [
        "药品经营许可证",
        "GSP认证证书",
        "医保定点零售药店",
        "互联网药品信息服务资格证"
      ],
      "servicePromises": [
        "正品保证 假一赔十",
        "药师咨询 专业指导",
        "隐私保护 安全配送",
        "7天无理由退换"
      ],
      "totalProducts": 8,
      "drugs": [
        {
          "id": "d001",
          "name": "阿莫西林胶囊",
          "specification": "0.25g*20粒",
          "manufacturer": "珠海联邦制药股份有限公司",
          "price": 15.80,
          "originalPrice": 22.00,
          "stock": 256,
          "isRx": true,
          "approvalNumber": "国药准字H20067454",
          "image": "",
          "imageColor": "#E3F2FD",
          "imageText": "阿莫",
          "sales": 520,
          "discount": 28,
          "deliveryTime": 25,
          "category": "抗感染",
          "tags": ["热销", "处方药"]
        }
      ]
    }
  }
  ```

### 2.3 门店药品

#### 2.3.1 获取门店药品列表
- **接口路径**：`GET /v1/stores/{id}/drugs`
- **接口描述**：获取指定门店的在售药品列表
- **路径参数**：
  - `id`: 门店ID
- **返回示例**：
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [
      {
        "id": "d001",
        "name": "阿莫西林胶囊",
        "specification": "0.25g*20粒",
        "manufacturer": "珠海联邦制药股份有限公司",
        "price": 15.80,
        "originalPrice": 22.00,
        "stock": 256,
        "isRx": true,
        "approvalNumber": "国药准字H20067454",
        "image": "",
        "imageColor": "#E3F2FD",
        "imageText": "阿莫",
        "sales": 520,
        "discount": 28,
        "deliveryTime": 25,
        "category": "抗感染",
        "tags": ["热销", "处方药"]
      },
      {
        "id": "d002",
        "name": "布洛芬缓释胶囊",
        "specification": "0.3g*12粒",
        "manufacturer": "中美天津史克制药有限公司",
        "price": 12.50,
        "originalPrice": 18.00,
        "stock": 189,
        "isRx": false,
        "approvalNumber": "国药准字H10900089",
        "image": "",
        "imageColor": "#FFF3E0",
        "imageText": "布洛",
        "sales": 380,
        "discount": 30,
        "deliveryTime": 25,
        "category": "解热镇痛",
        "tags": ["畅销", "OTC"]
      }
    ]
  }
  ```

---

## 三、错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录或token过期 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 10001 | 用户名或密码错误 |
| 10002 | 验证码错误 |
| 10003 | 用户已被禁用 |
| 20001 | 商品不存在 |
| 20002 | 商品库存不足 |
| 20003 | 商品已下架 |
| 30001 | 订单不存在 |
| 30002 | 订单状态异常 |
| 30003 | 订单已支付 |
| 40001 | 处方审核未通过 |
| 40002 | 处方药需要处方 |

---

## 四、数据字典

### 4.1 订单状态
| 值 | 说明 |
|----|------|
| 0 | 待付款 |
| 1 | 待发货 |
| 2 | 待收货 |
| 3 | 待评价 |
| 4 | 已完成 |
| -1 | 已取消 |
| -2 | 退款中 |
| -3 | 已退款 |

### 4.2 问诊状态
| 值 | 说明 |
|----|------|
| pending | 待接诊 |
| processing | 问诊中 |
| completed | 已完成 |
| closed | 已关闭 |

### 4.3 处方状态
| 值 | 说明 |
|----|------|
| pending | 待审核 |
| approved | 已通过 |
| rejected | 已拒绝 |

### 4.4 用户性别
| 值 | 说明 |
|----|------|
| 1 | 男 |
| 2 | 女 |

### 4.5 支付方式
| 值 | 说明 |
|----|------|
| 1 | 微信支付 |
| 2 | 支付宝 |

### 4.6 门店标签类型
| 值 | 说明 |
|----|------|
| primary | 主要标签（蓝色） |
| success | 成功标签（绿色） |
| warning | 警告标签（橙色） |
| danger | 危险标签（红色） |
| info | 信息标签（灰色） |

### 4.7 门店状态
| 值 | 说明 |
|----|------|
| 0 | 休息中 |
| 1 | 营业中 |

---

## 五、版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0 | 2024-03-20 | 初始版本 |
