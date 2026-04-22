# DrugMall 药品商城结算页面 UI设计规范

## 文档信息

| 项目 | 内容 |
|------|------|
| 文档名称 | UI设计规范-结算页面-v1.0 |
| 适用平台 | 移动端H5 |
| 设计尺寸 | 375px 基准宽度 |
| 设计工具 | Figma / Sketch |
| 更新日期 | 2024-04-18 |
| 版本号 | v1.0 |

---

## 一、设计原则

### 1.1 核心原则

- **清晰性**：结算信息一目了然，价格、优惠、配送等关键信息突出显示
- **信任感**：处方药特殊提示、配送保障等信息增强用户信任
- **效率性**：减少操作步骤，常用选项默认选中，支持快速决策
- **安全感**：药品特殊属性（处方药标识、合规提示）明确展示

### 1.2 视觉风格

- 采用清新医疗风格，主色调为健康绿色系，辅以温暖黄色强调
- 界面简洁干净，信息层级分明
- 圆角设计，亲和力强
- 充足的留白，呼吸感强

---

## 二、设计Token

### 2.1 颜色系统

#### 主色调

| Token | 色值 | 用途 |
|-------|------|------|
| `--color-primary` | `#07C160` | 主品牌色、按钮、选中状态 |
| `--color-primary-light` | `#E6F7ED` | 主色浅色背景、标签背景 |
| `--color-primary-dark` | `#06AD56` | 主色悬停/按下状态 |

#### 强调色

| Token | 色值 | 用途 |
|-------|------|------|
| `--color-accent` | `#FFC300` | 价格、优惠、重要提示 |
| `--color-accent-light` | `#FFF8E1` | 选中状态背景、高亮区域 |
| `--color-accent-dark` | `#E6B000` | 强调色深色 |

#### 功能色

| Token | 色值 | 用途 |
|-------|------|------|
| `--color-success` | `#07C160` | 成功状态、免配送费 |
| `--color-warning` | `#FF9500` | 警告、处方药提示 |
| `--color-error` | `#FF4D4F` | 错误、删除操作 |
| `--color-info` | `#1890FF` | 信息提示、链接 |

#### 中性色

| Token | 色值 | 用途 |
|-------|------|------|
| `--color-text-primary` | `#1A1A1A` | 主标题、重要文字 |
| `--color-text-secondary` | `#666666` | 次要文字、描述 |
| `--color-text-tertiary` | `#999999` | 辅助文字、占位符 |
| `--color-text-quaternary` | `#CCCCCC` | 禁用文字、分割线 |
| `--color-border` | `#E8E8E8` | 边框、分割线 |
| `--color-border-light` | `#F0F0F0` | 浅色边框 |
| `--color-bg-primary` | `#FFFFFF` | 主背景 |
| `--color-bg-secondary` | `#F5F5F5` | 次级背景、页面底色 |
| `--color-bg-tertiary` | `#FAFAFA` | 卡片背景、输入框背景 |

#### 特殊色

| Token | 色值 | 用途 |
|-------|------|------|
| `--color-rx-tag` | `#FF4D4F` | 处方药标签 |
| `--color-rx-bg` | `#FFF2F0` | 处方药提示背景 |
| `--color-shop-tag` | `#07C160` | 店铺标签背景 |

### 2.2 字体系统

#### 字体族

```css
--font-family-base: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
--font-family-price: "DIN Alternate", "DIN", "Helvetica Neue", Arial, sans-serif;
```

#### 字号规范

| Token | 字号 | 行高 | 字重 | 用途 |
|-------|------|------|------|------|
| `--font-size-xs` | 10px | 14px | 400 | 标签、角标 |
| `--font-size-sm` | 12px | 18px | 400 | 辅助说明、时间 |
| `--font-size-base` | 14px | 22px | 400 | 正文、描述 |
| `--font-size-md` | 15px | 23px | 500 | 次要标题 |
| `--font-size-lg` | 16px | 24px | 500 | 主标题、价格 |
| `--font-size-xl` | 18px | 26px | 600 | 大标题、合计金额 |
| `--font-size-xxl` | 20px | 28px | 600 | 重要价格 |
| `--font-size-display` | 24px | 32px | 700 | 总价展示 |

#### 字重规范

| Token | 值 | 用途 |
|-------|-----|------|
| `--font-weight-regular` | 400 | 正文 |
| `--font-weight-medium` | 500 | 次要标题 |
| `--font-weight-semibold` | 600 | 主标题、强调 |
| `--font-weight-bold` | 700 | 价格、重要数字 |

### 2.3 间距系统

#### 基础间距

| Token | 值 | 用途 |
|-------|-----|------|
| `--spacing-xs` | 4px | 紧凑间距、图标与文字间距 |
| `--spacing-sm` | 8px | 小间距、内联元素间距 |
| `--spacing-md` | 12px | 标准间距、卡片内边距 |
| `--spacing-lg` | 16px | 大间距、模块间距 |
| `--spacing-xl` | 20px | 区块间距 |
| `--spacing-xxl` | 24px | 大区块间距 |
| `--spacing-3xl` | 32px | 页面级间距 |

#### 页面边距

| Token | 值 | 用途 |
|-------|-----|------|
| `--page-padding` | 16px | 页面水平边距 |
| `--section-gap` | 12px | 模块之间间距 |

### 2.4 圆角系统

| Token | 值 | 用途 |
|-------|-----|------|
| `--radius-sm` | 4px | 小标签、角标 |
| `--radius-md` | 8px | 按钮、输入框 |
| `--radius-lg` | 12px | 卡片、模块 |
| `--radius-xl` | 16px | 大卡片、弹窗 |
| `--radius-full` | 9999px | 胶囊形状、圆形 |

### 2.5 阴影系统

| Token | 值 | 用途 |
|-------|-----|------|
| `--shadow-sm` | `0 1px 2px rgba(0,0,0,0.05)` | 轻微阴影 |
| `--shadow-md` | `0 2px 8px rgba(0,0,0,0.08)` | 卡片阴影 |
| `--shadow-lg` | `0 4px 16px rgba(0,0,0,0.12)` | 浮层阴影 |
| `--shadow-bottom` | `0 -2px 10px rgba(0,0,0,0.05)` | 底部栏阴影 |

---

## 三、组件规范

### 3.1 配送方式切换 Tab

#### 结构

```
┌─────────────────────────────────────┐
│  ┌──────────┐  ┌──────────┐        │
│  │ 外卖配送 │  │ 到店自取 │        │
│  │  (选中)  │  │          │        │
│  └──────────┘  └──────────┘        │
└─────────────────────────────────────┘
```

#### 样式规范

| 属性 | 值 |
|------|-----|
| 容器高度 | 48px |
| 容器背景 | `#FFFFFF` |
| Tab 宽度 | 50% 均分 |
| Tab 高度 | 44px |
| 未选中文字颜色 | `--color-text-secondary` |
| 选中文字颜色 | `--color-text-primary` |
| 选中指示器 | 底部 2px 线条，`--color-primary` |
| 指示器宽度 | 40px |
| 指示器圆角 | 2px |
| 字体大小 | `--font-size-md` (15px) |
| 字重 | `--font-weight-medium` (500) |

#### 交互状态

- **默认**：文字 `#666666`，无指示器
- **选中**：文字 `#1A1A1A`，底部绿色指示器
- **点击反馈**：背景轻微变暗 `rgba(0,0,0,0.02)`

---

### 3.2 地址信息卡片

#### 结构

```
┌─────────────────────────────────────┐
│  📍 华羽体育羽毛球馆(三峡大数据馆)   │
│     海洋馆                          │
│     彭先生 186****7982              │
│                          >          │
└─────────────────────────────────────┘
```

#### 样式规范

| 属性 | 值 |
|------|-----|
| 容器背景 | `#FFFFFF` |
| 内边距 | 16px |
| 图标尺寸 | 20px |
| 图标颜色 | `--color-primary` |
| 地址名称字号 | `--font-size-lg` (16px) |
| 地址名称字重 | `--font-weight-semibold` (600) |
| 联系人字号 | `--font-size-base` (14px) |
| 联系人颜色 | `--color-text-secondary` |
| 箭头颜色 | `--color-text-tertiary` |
| 箭头尺寸 | 16px |
| 下边距 | 12px |

#### 交互

- 点击整行可跳转地址选择/编辑页面
- 点击态：背景 `rgba(0,0,0,0.02)`

---

### 3.3 配送时间选择器

#### 结构

```
┌─────────────────────────────────────┐
│  配送时间                    修改 > │
├─────────────────────────────────────┤
│  ┌─────────────────────────────┐   │
│  │ 🕐 立即配送           选中 ✓ │   │
│  │    预计18:21-18:36送达       │   │
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │ 1对1急送                    │   │
│  │    预计18:15-18:25送达  ¥2.3 │   │
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │ 预约配送 >                   │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

#### 样式规范

| 属性 | 值 |
|------|-----|
| 容器背景 | `#FFFFFF` |
| 内边距 | 16px |
| 标题字号 | `--font-size-md` (15px) |
| 标题字重 | `--font-weight-medium` (500) |
| 选项卡片间距 | 8px |
| 选项卡片内边距 | 12px 16px |
| 选项卡片圆角 | `--radius-md` (8px) |
| 选项卡片边框 | 1px solid `--color-border` |
| 选中卡片边框 | 1px solid `--color-accent` |
| 选中卡片背景 | `--color-accent-light` |
| 选项名称字号 | `--font-size-base` (14px) |
| 预计时间字号 | `--font-size-sm` (12px) |
| 预计时间颜色 | `--color-text-secondary` |
| 价格颜色 | `--color-accent` |
| 勾选图标颜色 | `--color-accent` |
| 勾选图标尺寸 | 20px |

#### 交互状态

- **默认选项**：白色背景，灰色边框
- **选中选项**：浅黄背景，黄色边框，显示勾选图标
- **点击态**：背景轻微变暗

---

### 3.4 准时宝组件

#### 结构

```
┌─────────────────────────────────────┐
│  ☐ 准时宝                          │
│     超过18:48起赔，最高赔4元        │
│     商家赠送                        │
└─────────────────────────────────────┘
```

#### 样式规范

| 属性 | 值 |
|------|-----|
| 容器背景 | `#FFFFFF` |
| 内边距 | 12px 16px |
| 复选框尺寸 | 18px |
| 复选框边框 | 1px solid `--color-border` |
| 复选框圆角 | `--radius-sm` (4px) |
| 选中背景 | `--color-primary` |
| 标题字号 | `--font-size-base` (14px) |
| 标题字重 | `--font-weight-medium` (500) |
| 描述字号 | `--font-size-sm` (12px) |
| 描述颜色 | `--color-text-secondary` |
| "赠送"标签背景 | `--color-primary-light` |
| "赠送"标签文字 | `--color-primary` |
| "赠送"标签字号 | 10px |
| "赠送"标签内边距 | 2px 6px |
| "赠送"标签圆角 | `--radius-sm` (4px) |

---

### 3.5 配送提示条

#### 结构

```
┌─────────────────────────────────────┐
│  ℹ️ 因配送订单较多，送达时间可能波动  │
└─────────────────────────────────────┘
```

#### 样式规范

| 属性 | 值 |
|------|-----|
| 容器背景 | `#FFF8E1` |
| 内边距 | 10px 16px |
| 图标尺寸 | 14px |
| 图标颜色 | `--color-accent-dark` |
| 文字字号 | `--font-size-sm` (12px) |
| 文字颜色 | `--color-accent-dark` |

---

### 3.6 商品信息卡片

#### 结构

```
┌─────────────────────────────────────┐
│  🏥 采之林大药房(运河分店)     >    │
├─────────────────────────────────────┤
│  ┌─────┐                            │
│  │     │ [美团快送]                  │
│  │ 图片 │                            │
│  │     │ [仁和可立克]磷酸奥司他韦   │
│  └─────┘ 胶囊 75mg*6粒/板/盒        │
│          ┌────┐                     │
│          │处方│                     │
│          └────┘                     │
│          ¥35.64    [-] 1 [+]        │
└─────────────────────────────────────┘
```

#### 样式规范

##### 店铺头部

| 属性 | 值 |
|------|-----|
| 店铺图标 | 药店图标，16px，`--color-primary` |
| 店铺名称字号 | `--font-size-md` (15px) |
| 店铺名称字重 | `--font-weight-medium` (500) |
| 箭头颜色 | `--color-text-tertiary` |
| 下边距 | 12px |

##### 配送标签

| 属性 | 值 |
|------|-----|
| 背景 | `--color-primary-light` |
| 文字颜色 | `--color-primary` |
| 字号 | 10px |
| 内边距 | 2px 6px |
| 圆角 | `--radius-sm` (4px) |

##### 商品图片

| 属性 | 值 |
|------|-----|
| 尺寸 | 80px x 80px |
| 圆角 | `--radius-md` (8px) |
| 背景 | `--color-bg-tertiary` |

##### 商品名称

| 属性 | 值 |
|------|-----|
| 字号 | `--font-size-base` (14px) |
| 行高 | 20px |
| 颜色 | `--color-text-primary` |
| 最大行数 | 2行 |
| 溢出处理 | 省略号 |

##### 处方药标签

| 属性 | 值 |
|------|-----|
| 背景 | `--color-rx-bg` |
| 文字颜色 | `--color-rx-tag` |
| 字号 | 10px |
| 内边距 | 2px 6px |
| 圆角 | `--radius-sm` (4px) |
| 边框 | 1px solid `--color-rx-tag` |

##### 价格与数量

| 属性 | 值 |
|------|-----|
| 价格字号 | `--font-size-lg` (16px) |
| 价格字重 | `--font-weight-bold` (700) |
| 价格颜色 | `--color-text-primary` |
| 价格字体 | `--font-family-price` |
| 数量选择器高度 | 28px |
| 数量选择器宽度 | 90px |
| 按钮尺寸 | 28px x 28px |
| 按钮背景 | `--color-bg-tertiary` |
| 按钮圆角 | `--radius-sm` (4px) |
| 数字宽度 | 34px |
| 数字字号 | `--font-size-base` (14px) |

---

### 3.7 费用明细列表

#### 结构

```
┌─────────────────────────────────────┐
│  配送费              免配送费       │
│                      ~~¥2.5~~ ¥0   │
│  活动减2.5元配送费                  │
│  打包费              ¥0.5           │
│  ─────────────────────────────────  │
│  美团红包            暂无可用    >  │
│  店铺券/商品券       暂无可用    >  │
└─────────────────────────────────────┘
```

#### 样式规范

| 属性 | 值 |
|------|-----|
| 容器背景 | `#FFFFFF` |
| 内边距 | 16px |
| 行高 | 44px |
| 标签字号 | `--font-size-base` (14px) |
| 标签颜色 | `--color-text-secondary` |
| 原价字号 | `--font-size-sm` (12px) |
| 原价颜色 | `--color-text-tertiary` |
| 删除线 | 1px solid `--color-text-tertiary` |
| 现价/金额字号 | `--font-size-base` (14px) |
| 现价/金额颜色 | `--color-text-primary` |
| 免配送费颜色 | `--color-success` |
| 优惠信息字号 | `--font-size-sm` (12px) |
| 优惠信息颜色 | `--color-text-tertiary` |
| 分割线 | 1px dashed `--color-border` |
| 分割线上下边距 | 12px |
| 箭头颜色 | `--color-text-tertiary` |
| 不可用状态颜色 | `--color-text-tertiary` |

---

### 3.8 处方药提示条

#### 结构

```
┌─────────────────────────────────────┐
│  ⚠️ 订单中包含处方药，提交订单后请    │
│     补充处方信息                    │
└─────────────────────────────────────┘
```

#### 样式规范

| 属性 | 值 |
|------|-----|
| 容器背景 | `--color-rx-bg` |
| 内边距 | 12px 16px |
| 图标尺寸 | 16px |
| 图标颜色 | `--color-rx-tag` |
| 文字字号 | `--font-size-sm` (12px) |
| 文字颜色 | `--color-rx-tag` |
| 行高 | 18px |

---

### 3.9 底部结算栏

#### 结构

```
┌─────────────────────────────────────┐
│  已优惠¥2.5                         │
│  合计               ¥36.14  [提交]  │
└─────────────────────────────────────┘
```

#### 样式规范

| 属性 | 值 |
|------|-----|
| 容器高度 | 64px + 安全区域 |
| 容器背景 | `#FFFFFF` |
| 上边框 | 1px solid `--color-border-light` |
| 阴影 | `--shadow-bottom` |
| 水平内边距 | 16px |
| 底部安全区域 | env(safe-area-inset-bottom) |

##### 优惠提示

| 属性 | 值 |
|------|-----|
| 字号 | `--font-size-sm` (12px) |
| 颜色 | `--color-text-tertiary` |
| 优惠金额颜色 | `--color-accent` |

##### 合计区域

| 属性 | 值 |
|------|-----|
| "合计"字号 | `--font-size-base` (14px) |
| "合计"颜色 | `--color-text-secondary` |
| 金额字号 | `--font-size-xxl` (20px) |
| 金额字重 | `--font-weight-bold` (700) |
| 金额颜色 | `--color-text-primary` |
| 金额字体 | `--font-family-price` |
| 货币符号字号 | `--font-size-lg` (16px) |

##### 提交按钮

| 属性 | 值 |
|------|-----|
| 宽度 | 120px |
| 高度 | 44px |
| 背景 | `--color-accent` |
| 文字颜色 | `--color-text-primary` |
| 字号 | `--font-size-md` (15px) |
| 字重 | `--font-weight-semibold` (600) |
| 圆角 | `--radius-full` (9999px) |

#### 交互状态

- **默认**：黄色背景 `#FFC300`
- **按下**：背景变深 `#E6B000`
- **禁用**：背景 `#CCCCCC`，文字 `#FFFFFF`

---

## 四、页面布局规范

### 4.1 页面结构

```
┌─────────────────────────────────────┐
│  Status Bar (系统状态栏)            │
├─────────────────────────────────────┤
│  Navigation Bar (导航栏)            │
│  [返回]           确认订单          │
├─────────────────────────────────────┤
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 配送方式 Tab                 │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 地址信息卡片                 │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 配送时间选择器               │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 准时宝                       │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 配送提示条                   │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 商品信息卡片                 │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 费用明细                     │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 处方药提示条                 │   │
│  └─────────────────────────────┘   │
│                                     │
│  [底部安全间距]                      │
│                                     │
├─────────────────────────────────────┤
│  底部结算栏 (Fixed Bottom)          │
└─────────────────────────────────────┘
```

### 4.2 布局参数

| 元素 | 参数 |
|------|------|
| 页面背景 | `--color-bg-secondary` `#F5F5F5` |
| 模块间距 | `--section-gap` `12px` |
| 模块背景 | `--color-bg-primary` `#FFFFFF` |
| 模块圆角 | `--radius-lg` `12px` |
| 页面边距 | `--page-padding` `16px` |
| 内容区最大宽度 | 100% |
| 底部栏高度 | 64px |
| 底部安全间距 | 20px |

### 4.3 滚动行为

- 页面整体可滚动
- 底部结算栏固定定位 `position: fixed; bottom: 0`
- 内容区底部预留 `84px` 间距（结算栏高度 + 间距）

---

## 五、交互规范

### 5.1 点击态

| 元素 | 点击态效果 |
|------|-----------|
| 按钮 | 背景色加深 10% |
| 列表项 | 背景 `rgba(0,0,0,0.02)` |
| 卡片 | 背景 `rgba(0,0,0,0.02)` |
| Tab | 背景 `rgba(0,0,0,0.02)` |

### 5.2 转场动画

| 场景 | 动画效果 |
|------|----------|
| 页面进入 | 从右向左滑入，300ms，ease-out |
| 页面返回 | 从左向右滑出，300ms，ease-in |
| 选项切换 | 背景色渐变，200ms |
| 数量变化 | 数字缩放弹跳，150ms |

### 5.3 加载状态

| 场景 | 处理方式 |
|------|----------|
| 页面加载 | 骨架屏 |
| 提交订单 | 按钮 Loading 状态 |
| 地址切换 | 局部 Loading |

---

## 六、适配规范

### 6.1 响应式断点

| 设备 | 宽度范围 | 适配策略 |
|------|----------|----------|
| 小屏手机 | < 360px | 缩小间距，字号微调 |
| 标准手机 | 360px - 414px | 基准设计 |
| 大屏手机 | > 414px | 内容居中，最大宽度限制 |

### 6.2 安全区域适配

```css
/* iPhone X+ 安全区域适配 */
.safe-area-bottom {
  padding-bottom: env(safe-area-inset-bottom);
}

/* 底部固定栏 */
.fixed-bottom-bar {
  padding-bottom: calc(16px + env(safe-area-inset-bottom));
}
```

### 6.3 字体缩放适配

- 使用 rem 单位，基准 font-size: 16px
- 支持系统字体大小调整
- 最小字号不小于 10px

---

## 七、无障碍规范

### 7.1 颜色对比度

| 元素 | 前景色 | 背景色 | 对比度 |
|------|--------|--------|--------|
| 主文字 | `#1A1A1A` | `#FFFFFF` | 16.1:1 |
| 次要文字 | `#666666` | `#FFFFFF` | 5.7:1 |
| 按钮文字 | `#1A1A1A` | `#FFC300` | 11.2:1 |
| 链接文字 | `#07C160` | `#FFFFFF` | 4.6:1 |

### 7.2 触摸目标

| 元素 | 最小尺寸 |
|------|----------|
| 按钮 | 44px x 44px |
| 列表项 | 44px 高度 |
| 图标按钮 | 40px x 40px |
| 复选框 | 18px x 18px (热区 44px) |

### 7.3 语义化

- 使用语义化 HTML 标签
- 表单元素关联 label
- 图片添加 alt 描述
- 支持屏幕阅读器

---

## 八、切图规范

### 8.1 图标尺寸

| 用途 | 尺寸 | 格式 |
|------|------|------|
| 导航图标 | 24px | SVG |
| 列表图标 | 20px | SVG |
| 功能图标 | 16px | SVG |
| 店铺Logo | 40px | PNG/WebP |
| 商品图片 | 80px | PNG/WebP |

### 8.2 图片格式

- 图标：SVG（矢量，可缩放）
- 商品图：WebP（优先）、PNG（透明）、JPEG（照片）
- 占位图：SVG

### 8.3 命名规范

```
ic_location.svg
ic_clock.svg
ic_arrow_right.svg
ic_check.svg
ic_info.svg
ic_warning.svg
ic_store.svg
ic_delivery.svg
tag_rx.svg
tag_delivery.svg
```

---

## 九、示例代码

### 9.1 CSS 变量定义

```css
:root {
  /* 主色调 */
  --color-primary: #07C160;
  --color-primary-light: #E6F7ED;
  --color-primary-dark: #06AD56;
  
  /* 强调色 */
  --color-accent: #FFC300;
  --color-accent-light: #FFF8E1;
  --color-accent-dark: #E6B000;
  
  /* 功能色 */
  --color-success: #07C160;
  --color-warning: #FF9500;
  --color-error: #FF4D4F;
  --color-info: #1890FF;
  
  /* 中性色 */
  --color-text-primary: #1A1A1A;
  --color-text-secondary: #666666;
  --color-text-tertiary: #999999;
  --color-text-quaternary: #CCCCCC;
  --color-border: #E8E8E8;
  --color-border-light: #F0F0F0;
  --color-bg-primary: #FFFFFF;
  --color-bg-secondary: #F5F5F5;
  --color-bg-tertiary: #FAFAFA;
  
  /* 特殊色 */
  --color-rx-tag: #FF4D4F;
  --color-rx-bg: #FFF2F0;
  
  /* 字体 */
  --font-family-base: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  --font-family-price: "DIN Alternate", "DIN", "Helvetica Neue", Arial, sans-serif;
  
  /* 字号 */
  --font-size-xs: 10px;
  --font-size-sm: 12px;
  --font-size-base: 14px;
  --font-size-md: 15px;
  --font-size-lg: 16px;
  --font-size-xl: 18px;
  --font-size-xxl: 20px;
  --font-size-display: 24px;
  
  /* 字重 */
  --font-weight-regular: 400;
  --font-weight-medium: 500;
  --font-weight-semibold: 600;
  --font-weight-bold: 700;
  
  /* 间距 */
  --spacing-xs: 4px;
  --spacing-sm: 8px;
  --spacing-md: 12px;
  --spacing-lg: 16px;
  --spacing-xl: 20px;
  --spacing-xxl: 24px;
  --spacing-3xl: 32px;
  
  /* 页面 */
  --page-padding: 16px;
  --section-gap: 12px;
  
  /* 圆角 */
  --radius-sm: 4px;
  --radius-md: 8px;
  --radius-lg: 12px;
  --radius-xl: 16px;
  --radius-full: 9999px;
  
  /* 阴影 */
  --shadow-sm: 0 1px 2px rgba(0,0,0,0.05);
  --shadow-md: 0 2px 8px rgba(0,0,0,0.08);
  --shadow-lg: 0 4px 16px rgba(0,0,0,0.12);
  --shadow-bottom: 0 -2px 10px rgba(0,0,0,0.05);
}
```

### 9.2 基础组件类

```css
/* 卡片基础样式 */
.dm-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  margin: 0 var(--page-padding) var(--section-gap);
  overflow: hidden;
}

/* 列表项 */
.dm-list-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-md) var(--spacing-lg);
  min-height: 44px;
}

/* 标签 */
.dm-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 6px;
  font-size: var(--font-size-xs);
  border-radius: var(--radius-sm);
}

.dm-tag--primary {
  background: var(--color-primary-light);
  color: var(--color-primary);
}

.dm-tag--rx {
  background: var(--color-rx-bg);
  color: var(--color-rx-tag);
  border: 1px solid var(--color-rx-tag);
}

.dm-tag--accent {
  background: var(--color-accent-light);
  color: var(--color-accent-dark);
}

/* 主按钮 */
.dm-btn-primary {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 44px;
  padding: 0 24px;
  background: var(--color-accent);
  color: var(--color-text-primary);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  border-radius: var(--radius-full);
  border: none;
}

.dm-btn-primary:active {
  background: var(--color-accent-dark);
}

/* 价格显示 */
.dm-price {
  font-family: var(--font-family-price);
  font-weight: var(--font-weight-bold);
}

.dm-price--large {
  font-size: var(--font-size-xxl);
}

.dm-price--small {
  font-size: var(--font-size-sm);
  text-decoration: line-through;
  color: var(--color-text-tertiary);
}
```

---

## 十、版本记录

| 版本 | 日期 | 更新内容 | 作者 |
|------|------|----------|------|
| v1.0 | 2024-04-18 | 初始版本，完成结算页面全部设计规范 | UI Designer |

---

## 附录

### A. 参考链接

- [Apple Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines/)
- [Material Design](https://material.io/design)
- [WCAG 2.1 无障碍指南](https://www.w3.org/WAI/WCAG21/quickref/)

### B. 设计稿文件

- Figma: [DrugMall 结算页面设计稿]
- 切图资源: `/design-assets/checkout/`

---

*本文档为 DrugMall 药品商城结算页面 UI 设计规范，供设计与开发团队参考使用。*
