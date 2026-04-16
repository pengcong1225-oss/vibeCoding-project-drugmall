# DrugMall API 接口数据测试报告

## 测试时间
2026-04-09

## 测试环境
- 后端地址: http://localhost:8080/api
- 测试工具: PowerShell Invoke-RestMethod

## 测试结果汇总

| 接口 | 状态 | 数据状态 | 说明 |
|------|------|----------|------|
| GET /api/user/info | ✅ 正常 | ✅ 有数据 | 返回用户信息 |
| GET /api/drugs | ✅ 正常 | ✅ 有数据 | 返回4条药品 |
| GET /api/drugs/categories | ✅ 正常 | ✅ 有数据 | 返回分类列表 |
| GET /api/cart | ✅ 正常 | ✅ 有数据 | 返回3件商品 |
| GET /api/orders | ✅ 正常 | ✅ 有数据 | 返回订单列表 |
| GET /api/addresses | ✅ 正常 | ✅ 有数据 | 返回地址列表 |
| GET /api/user/patients | ✅ 正常 | ✅ 有数据 | 返回就诊人列表 |
| GET /api/user/coupons | ✅ 正常 | ✅ 有数据 | 返回优惠券列表 |

## 详细测试数据

### 1. 用户信息 (/api/user/info)
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
    "idCard": "110101199005201234",
    "isRealNameAuth": true,
    "balance": 1000.0,
    "points": 500
  }
}
```

### 2. 药品列表 (/api/drugs)
```json
{
  "code": 200,
  "data": {
    "list": [
      { "id": "2", "name": "布洛芬缓释胶囊", "price": 15.8 },
      { "id": "3", "name": "感冒灵颗粒", "price": 18.5 },
      { "id": "6", "name": "维生素C片", "price": 19.9 },
      { "id": "1", "name": "阿莫西林胶囊", "price": 12.5 }
    ],
    "total": 4,
    "page": 1,
    "size": 5,
    "pages": 1
  }
}
```

### 3. 购物车 (/api/cart)
```json
{
  "code": 200,
  "data": [
    { "id": "1", "name": "布洛芬缓释胶囊", "quantity": 2, "price": 15.8 },
    { "id": "2", "name": "感冒灵颗粒", "quantity": 1, "price": 18.5 },
    { "id": "3", "name": "维生素C片", "quantity": 1, "price": 19.9 }
  ]
}
```

## 结论

✅ **所有后端API均正常返回数据**

后端服务运行正常，所有API接口都能正确返回数据。如果前端页面显示没有数据，问题可能出在前端：
1. 前端store没有正确调用API
2. 前端页面没有触发数据加载
3. 前端响应数据处理有问题

## 建议

1. 检查前端store是否正确调用API
2. 检查页面onMounted钩子是否触发数据加载
3. 检查浏览器Network面板查看实际请求
