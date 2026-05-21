# 首页数据从Mock切换到真实API的实施指南

## 当前状态分析

### ✅ 已完成部分

1. **数据库层面**
   - ✅ 已创建完整的首页配置表结构（`dm_home_page`, `dm_home_tab`, `dm_home_section`, `dm_home_kingkong`）
   - ✅ 已插入初始配置数据（5个Tab、9个模块、8个金刚位）
   - ✅ SQL脚本已成功执行

2. **后端层面**
   - ✅ Controller已实现：`HomeController` (`/api/v1/home/render/page`)
   - ✅ Service已实现：`HomeServiceImpl`（从数据库读取配置）
   - ⚠️ 存在编译错误需要修复（非首页相关代码的问题）

3. **前端层面**
   - ✅ API调用已定义：`homeApi.getHomePageRender()`
   - ✅ Store已实现：`useHomeStore`管理首页状态
   - ✅ 组件架构完整：支持动态渲染各类模块
   - ✅ Vite代理配置正确：`/api` → `http://localhost:8080/api`

### ❌ 待解决问题

1. **后端编译错误**
   - HomeSectionVO缺少subtitle字段（已修复）
   - 其他Service类存在类型不匹配问题（需逐一修复）

2. **Mock开关配置**
   - request.ts中硬编码了Mock启用逻辑
   - 环境变量未正确生效

## 实施步骤

### 方案一：修复后端编译错误（推荐，彻底解决）

#### Step 1: 修复后端编译错误

```bash
cd backend
# 查看具体错误
mvn clean compile 2>&1 | Select-String "ERROR"

# 主要需要修复的文件：
# - HomeServiceImpl.java (已修复sortOrder NULL问题)
# - HomeSectionVO.java (已添加subtitle字段)
# - 其他Service类的类型转换问题
```

#### Step 2: 重启后端服务

```bash
cd backend
mvn spring-boot:run
```

#### Step 3: 验证后端API

```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/home/render/page" -UseBasicParsing | Select-Object -ExpandProperty Content
```

期望返回：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "pageId": "home_page_001",
    "pageName": "DrugMall首页",
    "version": "v1.0.0",
    "sections": [...]
  }
}
```

#### Step 4: 修改前端Mock配置

编辑 `frontend/src/api/request.ts`：

```typescript
// 第7行，修改为：
const ENABLE_MOCK = import.meta.env.VITE_ENABLE_MOCK === 'true' || 
                    import.meta.env.VITE_USE_MOCK === 'true'
```

编辑 `frontend/.env.development`：

```env
# 确保设置为false
VITE_USE_MOCK=false
```

#### Step 5: 重启前端开发服务器

```bash
cd frontend
npm run dev
```

### 方案二：临时使用混合模式（快速验证）

如果后端编译错误较多，可以先让前端同时支持Mock和真实API：

#### Step 1: 改进前端请求逻辑

编辑 `frontend/src/stores/home.ts`：

```typescript
async function fetchHomePageConfig() {
  loading.value = true
  error.value = null
  try {
    const data = await homeApi.getHomePageRender()
    if (data) {
      pageConfig.value = data.pageConfig
      sections.value = data.sections
      console.log('✅ 首页配置加载成功:', data)
    }
  } catch (e: any) {
    error.value = e.message || '获取首页配置失败'
    console.error('❌ 获取首页配置失败:', e)
    
    // 降级：如果API失败，使用Mock数据
    console.warn('⚠️ 使用Mock数据作为降级方案')
    // 这里可以导入mock数据作为fallback
  } finally {
    loading.value = false
  }
}
```

#### Step 2: 测试连通性

访问 http://localhost:3003，打开浏览器控制台查看：
- 是否有API请求发出
- 请求URL是否正确
- 响应数据格式是否符合预期

## 验证清单

### 后端验证
- [ ] 后端服务正常启动（无编译错误）
- [ ] 数据库连接正常
- [ ] `/api/v1/home/render/page` 接口返回200
- [ ] 返回数据结构包含pageConfig和sections

### 前端验证
- [ ] Mock已关闭（ENABLE_MOCK = false）
- [ ] 前端能成功请求后端API
- [ ] 首页正常渲染，显示所有模块
- [ ] Tab切换功能正常
- [ ] 下拉刷新能重新获取数据

### 功能验证
- [ ] 搜索栏显示正常
- [ ] Tab导航可切换
- [ ] 各Tab内容正确显示
- [ ] 金刚位点击响应
- [ ] Banner轮播正常

## 常见问题排查

### 问题1: CORS跨域错误

**现象**: 前端请求被浏览器拦截

**解决**: 检查后端CORS配置
```java
// backend/src/main/java/com/drugmall/config/WebConfig.java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
```

### 问题2: 404 Not Found

**现象**: 请求路径错误

**检查**:
- 前端请求: `/api/v1/home/render/page`
- Vite代理: `/api` → `http://localhost:8080/api`
- 后端路径: `/api/v1/home/render/page` (context-path + @RequestMapping)

### 问题3: 500 Internal Server Error

**现象**: 后端处理异常

**排查**:
1. 查看后端日志
2. 检查数据库连接
3. 验证SQL查询是否正常
4. 检查JSON解析是否出错

### 问题4: 数据为空或格式不对

**原因**: 数据库中content字段为NULL

**解决**: 更新数据库中的content字段，填入正确的JSON数据

```sql
UPDATE dm_home_section 
SET content = '[{"id":"1","name":"问医生"}]' 
WHERE section_type = 'doctor_department';
```

## 下一步建议

1. **优先修复后端编译错误**
   - 这是根本解决方案
   - 预计需要1-2小时修复所有类型错误

2. **完善数据库content字段**
   - 为每个模块填充真实的业务数据
   - 特别是商品列表、医生列表等动态数据

3. **添加缓存机制**
   - Redis缓存首页配置
   - 减少数据库查询压力

4. **管理后台集成**
   - 实现首页配置的可视化管理
   - 支持拖拽排序、实时预览

## 技术架构说明

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│   Frontend  │         │   Backend    │         │   MySQL     │
│  (Vue 3)    │◄───────►│ (Spring Boot)│◄───────►│ (drugmall)  │
│             │  HTTP   │              │  JDBC   │             │
│ - Pinia     │  REST   │ - Controller │         │ - dm_home_* │
│ - Axios     │         │ - Service    │         │ - Tables    │
│ - Components│         │ - JdbcTemplate│        │             │
└─────────────┘         └──────────────┘         └─────────────┘
```

**数据流**:
1. 前端发起请求: `GET /api/v1/home/render/page`
2. Vite代理转发到后端: `http://localhost:8080/api/v1/home/render/page`
3. HomeController接收请求
4. HomeServiceImpl查询数据库
5. 组装VO对象返回
6. 前端接收并渲染

---

**最后更新**: 2026-04-24  
**状态**: 待实施（后端编译错误修复中）
