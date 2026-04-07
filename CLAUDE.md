# DrugMall - AI 助手开发规范

## 1. 项目概述

**DrugMall** 是一个专业的互联网药品电商平台，致力于为用户提供安全、便捷的在线药品购买服务。

### 1.1 核心业务模块

| 模块 | 功能描述 | 关键特性 |
|------|----------|----------|
| 商品管理 | 药品信息维护 | 分类、规格、批号、效期 |
| 用户系统 | 注册、登录、实名认证 | 处方药购买资质验证 |
| 购物车 | 商品选购 | 库存实时校验 |
| 订单系统 | 订单全流程管理 | 状态流转、物流跟踪 |
| 支付系统 | 多渠道支付 | 安全加密、对账 |
| 处方管理 | 电子处方服务 | 医院对接、医生审核 |

### 1.2 技术栈

```
前端: Vue 3 + TypeScript + Element Plus + Pinia
后端: Spring Boot + MyBatis Plus + MySQL + Redis
中间件: RabbitMQ / Kafka (消息队列)
部署: Docker + Kubernetes
监控: Prometheus + Grafana + ELK
```

## 2. 代码规范

### 2.1 命名规范

**文件命名**
- Vue组件：`PascalCase.vue` (如 `ProductDetail.vue`)
- 工具类：`camelCase.ts` (如 `dateUtils.ts`)
- 样式文件：`kebab-case.scss` (如 `product-card.scss`)

**变量/函数命名**
```typescript
// 常量 - 全大写下划线
const MAX_CART_ITEMS = 99;
const API_BASE_URL = '/api/v1';

// 变量 - 小驼峰
let currentUser: UserInfo;
let isLoading = false;

// 函数 - 小驼峰，动词开头
function fetchProductList() {}
function handleAddToCart() {}
function validatePhoneNumber() {}

// 类/接口 - 大驼峰
class OrderService {}
interface ProductDTO {}
```

### 2.2 项目结构

```
drugmall/
├── docs/                          # 项目文档
│   ├── api/                       # API文档
│   ├── design/                    # 设计文档
│   └── deploy/                    # 部署文档
│
├── drugmall-frontend/             # 前端项目
│   ├── public/
│   ├── src/
│   │   ├── api/                   # API接口定义
│   │   ├── assets/                # 静态资源
│   │   │   ├── images/
│   │   │   └── icons/
│   │   ├── components/            # 公共组件
│   │   │   ├── common/            # 通用组件
│   │   │   └── business/          # 业务组件
│   │   ├── composables/           # 组合式函数
│   │   ├── directives/            # 自定义指令
│   │   ├── layouts/               # 布局组件
│   │   ├── router/                # 路由配置
│   │   ├── stores/                  # Pinia状态管理
│   │   │   ├── modules/           # 状态模块
│   │   │   └── index.ts
│   │   ├── styles/                # 样式文件
│   │   ├── utils/                 # 工具函数
│   │   ├── views/                 # 页面组件
│   │   ├── App.vue
│   │   └── main.ts
│   ├── types/                     # TypeScript类型定义
│   ├── .env
│   ├── .env.development
│   ├── .env.production
│   ├── index.html
│   ├── package.json
│   ├── tsconfig.json
│   └── vite.config.ts
│
├── drugmall-backend/              # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── drugmall/
│   │   │   │           ├── DrugmallApplication.java
│   │   │   │           ├── config/              # 配置类
│   │   │   │           ├── controller/          # 控制器
│   │   │   │           ├── service/             # 业务层
│   │   │   │           │   └── impl/
│   │   │   │           ├── mapper/              # 数据访问层
│   │   │   │           ├── entity/              # 实体类
│   │   │   │           ├── dto/                 # 数据传输对象
│   │   │   │           ├── vo/                  # 视图对象
│   │   │   │           ├── enums/               # 枚举类
│   │   │   │           ├── exception/             # 异常处理
│   │   │   │           ├── utils/                 # 工具类
│   │   │   │           ├── security/              # 安全相关
│   │   │   │           └── common/                # 通用常量
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── application-dev.yml
│   │   │       ├── application-prod.yml
│   │   │       ├── mapper/                        # MyBatis XML
│   │   │       └── banner.txt
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
│
├── drugmall-deploy/               # 部署配置
│   ├── docker/
│   │   ├── docker-compose.yml
│   │   ├── nginx/
│   │   └── mysql/
│   └── k8s/
│       ├── namespace.yaml
│       ├── configmap.yaml
│       ├── secret.yaml
│       ├── deployment-frontend.yaml
│       ├── deployment-backend.yaml
│       ├── deployment-mysql.yaml
│       ├── deployment-redis.yaml
│       └── service.yaml
│
├── sql/                             # 数据库脚本
│   ├── init/
│   │   ├── schema.sql               # 库表结构
│   │   └── data.sql                 # 初始数据
│   └── migration/                   # 版本迁移脚本
│
├── .gitignore
├── LICENSE
└── README.md
```

### 2.3 Vue 组件开发规范

**组件文件结构**
```vue
<script setup lang="ts">
// 1. 导入（按类型分组，外部库 > 内部模块）
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProductStore } from '@/stores/modules/product'
import type { Product } from '@/types/product'

// 2. 类型定义（如有需要）
interface Props {
  productId: string
  showDetail?: boolean
}

// 3. Props & Emits
const props = withDefaults(defineProps<Props>(), {
  showDetail: false
})

const emit = defineEmits<{
  (e: 'addToCart', product: Product): void
  (e: 'viewDetail', productId: string): void
}>()

// 4. 响应式数据
const loading = ref(false)
const product = ref<Product | null>(null)
const quantity = ref(1)

// 5. 计算属性
const totalPrice = computed(() => {
  if (!product.value) return 0
  return product.value.price * quantity.value
})

const isInStock = computed(() => {
  return product.value?.stock > 0
})

// 6. 方法
async function fetchProduct() {
  loading.value = true
  try {
    const productStore = useProductStore()
    product.value = await productStore.getProductById(props.productId)
  } catch (error) {
    console.error('获取商品信息失败:', error)
  } finally {
    loading.value = false
  }
}

function handleAddToCart() {
  if (!product.value) return
  emit('addToCart', { ...product.value, quantity: quantity.value })
}

// 7. 生命周期钩子
onMounted(() => {
  fetchProduct()
})
</script>

<template>
  <div class="product-card">
    <!-- 组件模板 -->
  </div>
</template>

<style scoped lang="scss">
/* 组件样式 */
</style>
```

**组件命名**
```typescript
// 文件名使用 PascalCase
ProductCard.vue
UserProfile.vue
ShoppingCart.vue

// 组件引用时使用 PascalCase
import ProductCard from './components/ProductCard.vue'
import UserProfile from './components/UserProfile.vue'
```

### 2.4 API 开发规范

**RESTful API 设计**
```java
/**
 * 商品管理控制器
 */
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "商品管理", description = "商品相关接口")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 获取商品列表
     */
    @GetMapping
    @Operation(summary = "获取商品列表", description = "支持分页、排序和条件筛选")
    public Result<PageResult<ProductVO>> getProductList(
            @ParameterObject @Valid ProductQueryDTO queryDTO) {
        PageResult<ProductVO> result = productService.getProductList(queryDTO);
        return Result.success(result);
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{productId}")
    @Operation(summary = "获取商品详情", description = "根据商品ID获取详细信息")
    public Result<ProductDetailVO> getProductDetail(
            @Parameter(description = "商品ID") @PathVariable String productId) {
        ProductDetailVO detail = productService.getProductDetail(productId);
        return Result.success(detail);
    }

    /**
     * 创建商品
     */
    @PostMapping
    @Operation(summary = "创建商品", description = "创建新商品信息")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> createProduct(
            @RequestBody @Valid ProductCreateDTO createDTO) {
        String productId = productService.createProduct(createDTO);
        return Result.success(productId);
    }

    /**
     * 更新商品
     */
    @PutMapping("/{productId}")
    @Operation(summary = "更新商品", description = "更新商品信息")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateProduct(
            @PathVariable String productId,
            @RequestBody @Valid ProductUpdateDTO updateDTO) {
        productService.updateProduct(productId, updateDTO);
        return Result.success();
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{productId}")
    @Operation(summary = "删除商品", description = "删除指定商品")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteProduct(
            @PathVariable String productId) {
        productService.deleteProduct(productId);
        return Result.success();
    }
}
```

**统一返回格式**
```java
/**
 * 统一响应结果
 */
@Data
public class Result<T> {
    
    /** 状态码 */
    private Integer code;
    
    /** 提示信息 */
    private String message;
    
    /** 数据 */
    private T data;
    
    /** 时间戳 */
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = success();
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(ResultCode resultCode) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        return result;
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
```

### 2.5 数据库设计规范

**表命名规范**
```sql
-- 表名使用小写下划线命名，业务前缀_模块_实体
-- 示例：
dm_product          -- 商品表 (dm = drugmall)
dm_product_category -- 商品分类表
dm_order            -- 订单表
dm_order_item       -- 订单明细表
dm_user             -- 用户表
dm_user_address     -- 用户地址表
dm_cart             -- 购物车表
```

**字段命名规范**
```sql
-- 字段名使用小写下划线命名
-- 主键统一使用 id
-- 外键使用 表名_id
-- 时间字段：create_time, update_time
-- 状态字段：status
-- 逻辑删除：is_deleted

CREATE TABLE dm_product (
    id              BIGINT PRIMARY KEY COMMENT '主键ID',
    product_code    VARCHAR(50) NOT NULL COMMENT '商品编码',
    product_name    VARCHAR(200) NOT NULL COMMENT '商品名称',
    category_id     BIGINT NOT NULL COMMENT '分类ID',
    brand_id        BIGINT COMMENT '品牌ID',
    main_image      VARCHAR(500) COMMENT '主图URL',
    detail          LONGTEXT COMMENT '商品详情',
    price           DECIMAL(10,2) NOT NULL COMMENT '售价',
    original_price  DECIMAL(10,2) COMMENT '原价',
    stock           INT NOT NULL DEFAULT 0 COMMENT '库存',
    warning_stock   INT DEFAULT 10 COMMENT '库存预警值',
    is_rx           TINYINT NOT NULL DEFAULT 0 COMMENT '是否处方药 0-非处方 1-处方',
    approval_number VARCHAR(100) COMMENT '批准文号',
    manufacturer    VARCHAR(200) COMMENT '生产厂家',
    spec            VARCHAR(100) COMMENT '规格',
    unit            VARCHAR(20) COMMENT '单位',
    expiry_date     DATE COMMENT '有效期至',
    status          TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-下架 1-上架',
    sort_order      INT DEFAULT 0 COMMENT '排序',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    INDEX idx_category (category_id),
    INDEX idx_status (status),
    INDEX idx_rx (is_rx),
    INDEX idx_create_time (create_time),
    UNIQUE KEY uk_product_code (product_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';
```

## 3. AI 助手提示工程规范

### 3.1 提示词结构

**基础结构**
```
角色: [你在项目中的角色]
任务: [需要完成的具体任务]
背景: [项目背景和技术栈]
要求: [具体要求和约束条件]
输出: [期望的输出格式]
```

**示例**
```
角色: 你是一位经验丰富的前端架构师，熟悉 Vue 3 和 TypeScript。

任务: 帮我设计一个药品搜索组件，支持关键词搜索、分类筛选、价格区间筛选。

背景: 
- 项目使用 Vue 3 + TypeScript + Element Plus
- 使用 Pinia 进行状态管理
- 后端 API 使用 RESTful 风格
- 药品数据包含：名称、分类、价格、库存、是否处方药等字段

要求:
1. 组件需要支持响应式设计，适配移动端和桌面端
2. 搜索需要支持防抖处理，减少 API 调用频率
3. 筛选条件变化时自动触发搜索
4. 需要显示加载状态和空状态
5. 代码需要使用 TypeScript 编写，定义清晰的类型

输出:
1. 完整的 Vue 组件代码
2. 相关的 TypeScript 类型定义
3. 简要的使用说明
```

### 3.2 上下文提供规范

**必须提供的上下文信息**
1. **技术栈**：前端框架、后端框架、数据库、中间件等
2. **项目结构**：主要目录和文件组织方式
3. **编码规范**：命名规范、代码风格等
4. **业务背景**：核心业务逻辑和流程
5. **约束条件**：性能要求、安全要求、合规要求等

**提供方式**
- 引用已有文档（如 README.md、API 文档）
- 粘贴相关代码片段
- 描述关键业务规则

## 4. 常见任务模板

### 4.1 开发新功能

```markdown
## 功能需求
[描述需要实现的功能]

## 技术栈
- 前端: Vue 3 + TypeScript + Element Plus
- 后端: Spring Boot + MyBatis Plus + MySQL

## 数据库表
[相关表结构]

## API 设计
[接口定义]

## 页面原型
[界面描述或原型图]

## 验收标准
- [ ] 功能1
- [ ] 功能2
- [ ] 功能3
```

### 4.2 Bug 修复

```markdown
## Bug 描述
[描述问题的现象]

## 复现步骤
1. [步骤1]
2. [步骤2]
3. [步骤3]

## 期望结果
[应该出现的结果]

## 实际结果
[实际出现的结果]

## 相关代码
[相关代码片段]

## 环境信息
- 浏览器: [版本]
- 操作系统: [版本]
- 后端版本: [版本号]
```

### 4.3 代码重构

```markdown
## 重构目标
[描述重构的目的]

## 当前问题
[描述当前代码的问题]

## 重构方案
[描述具体的重构计划]

## 影响范围
[列出受影响的模块/文件]

## 回滚方案
[如果重构失败如何回滚]
```

## 5. 最佳实践

### 5.1 性能优化

**前端优化**
- 使用 `v-show` 代替频繁的 `v-if`
- 大数据列表使用虚拟滚动
- 图片懒加载
- 组件按需加载
- 路由懒加载

**后端优化**
- 数据库索引优化
- 缓存热点数据（Redis）
- 异步处理耗时操作
- 数据库连接池优化

### 5.2 安全规范

- **SQL 注入**：使用 MyBatis 参数化查询
- **XSS 攻击**：用户输入内容转义
- **CSRF 防护**：使用 Token 验证
- **敏感数据**：密码加密存储，API 使用 HTTPS
- **权限控制**：接口权限验证，数据权限隔离

### 5.3 错误处理

**前端错误处理**
```typescript
// API 请求统一错误处理
async function fetchProductList(params: ProductQueryParams) {
  try {
    const response = await api.get('/products', { params })
    return response.data
  } catch (error) {
    // 统一错误处理
    if (error.response) {
      const { status, data } = error.response
      switch (status) {
        case 401:
          // 未登录，跳转到登录页
          router.push('/login')
          break
        case 403:
          ElMessage.error('没有权限执行此操作')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error(data.message || '服务器内部错误')
          break
        default:
          ElMessage.error(data.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    throw error
  }
}
```

**后端错误处理**
```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return Result.error(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 其他异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error(ResultCode.SYSTEM_ERROR);
    }
}
```

## 6. 常见错误与解决方案

| 错误 | 原因 | 解决方案 |
|------|------|----------|
| 跨域错误 | 前后端域名不一致 | 后端配置 CORS |
| 状态丢失 | 刷新页面 Vuex 数据丢失 | 使用 localStorage 持久化 |
| 图片不显示 | 路径错误或权限问题 | 检查路径，配置静态资源访问 |
| 数据库连接超时 | 连接池配置不当 | 调整连接池参数 |
| 缓存不一致 | 缓存更新延迟 | 使用缓存更新策略 |

## 7. 附录

### 7.1 常用命令

```bash
# 前端开发
cd drugmall-frontend
npm install
npm run dev
npm run build

# 后端开发
cd drugmall-backend
mvn clean install
mvn spring-boot:run

# Docker 部署
docker-compose up -d
docker-compose down
```

### 7.2 参考文档

- [Vue 3 官方文档](https://vuejs.org/)
- [Element Plus 文档](https://element-plus.org/)
- [Spring Boot 文档](https://spring.io/projects/spring-boot)
- [MyBatis Plus 文档](https://baomidou.com/)

---

**文档维护**：开发团队  
**最后更新**：2024年  
**版本**：v1.0
