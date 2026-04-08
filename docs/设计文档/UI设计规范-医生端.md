# DrugMall 医生端 UI 设计规范

## 文档信息

| 项目 | 内容 |
|------|------|
| 文档名称 | DrugMall 医生端 UI 设计规范 |
| 版本 | v1.0 |
| 适用范围 | DrugMall 医生端移动端应用 |
| 设计目标 | 打造专业、高效、可信赖的医生工作台 |

---

## 1. 设计原则

### 1.1 核心理念

医生端界面设计遵循"**专业、高效、可信赖**"三大核心理念：

#### 专业感 (Professional)
- 采用医疗行业通用的蓝绿色系，传递专业与健康的视觉语言
- 界面布局严谨有序，信息层级清晰
- 避免过度装饰，保持医疗场景的严肃性

#### 高效性 (Efficient)
- 采用"信息找人"的设计理念，重要信息主动呈现
- 高频操作入口外露，减少点击层级
- 关键数据可视化展示，一眼获取核心信息

#### 可信赖 (Trustworthy)
- 稳定一致的视觉表现，建立用户信心
- 及时的状态反馈，消除操作不确定性
- 严谨的排版和配色，传递可靠感

### 1.2 设计准则

#### 准则一：清晰优先 (Clarity First)
- 每个界面都有明确的信息主语
- 文字易读，字号不小于 12px
- 足够的对比度，确保可读性

#### 准则二：效率至上 (Efficiency Matters)
- 常用功能三步可达
- 批量操作减少重复劳动
- 智能推荐减少输入

#### 准则三：一致体验 (Consistent Experience)
- 统一的设计语言贯穿全应用
- 相同的交互模式保持一致
- 组件复用确保视觉统一

#### 准则四：容错设计 (Error Prevention)
- 关键操作二次确认
- 提供撤销机制
- 清晰的错误提示和解决方案

### 1.3 无障碍设计

为确保所有医生都能高效使用：

- **色彩无障碍**：不单独依赖颜色传递信息，配合图标/文字
- **触控友好**：触控目标不小于 44×44px，间距充足
- **可读性**：支持系统字体大小调节
- **高对比度**：支持系统高对比度模式

---

## 2. 色彩系统

### 2.1 主色调 (Primary Colors)

主色采用医疗行业权威的**医疗绿**，传递专业、健康、安全的品牌认知。

| 色阶 | 色值 | 用途 |
|------|------|------|
| Primary-50 | `#E6F7F0` | 最浅背景、悬停态 |
| Primary-100 | `#B3E8D4` | 浅色背景、选中态 |
| Primary-200 | `#80D8B8` | 信息高亮 |
| Primary-300 | `#4DC99C` | 次级强调 |
| Primary-400 | `#1AB980` | 辅助操作 |
| **Primary-500** | **`#00B578`** | **主色，主按钮、重点信息** |
| Primary-600 | `#009A65` | 悬停态 |
| Primary-700 | `#007A50` | 点击态 |
| Primary-800 | `#005C3C` | 深色强调 |
| Primary-900 | `#003D28` | 最深色，文字 |

**主色应用示例：**

```css
/* 主按钮样式 */
.btn-primary {
  background-color: #00B578;
  color: #FFFFFF;
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  background-color: #009A65;
}

.btn-primary:active {
  background-color: #007A50;
}

/* 主色文字 */
.text-primary {
  color: #00B578;
}

/* 主色边框 */
.border-primary {
  border-color: #00B578;
}

/* 主色背景 */
.bg-primary {
  background-color: #00B578;
}

.bg-primary-light {
  background-color: #E6F7F0;
}
```

### 2.2 辅助色 (Auxiliary Colors)

辅助色用于区分不同类型信息，保持界面层次清晰。

#### 2.2.1 成功色 (Success)

| 色阶 | 色值 | 用途 |
|------|------|------|
| Success-50 | `#F6FFED` | 成功状态背景 |
| **Success-500** | **`#52C41A`** | **成功状态主色** |
| Success-600 | `#389E0D` | 悬停态 |

```css
/* 成功状态示例 */
.status-success {
  background-color: #F6FFED;
  border: 1px solid #B7EB8F;
  color: #52C41A;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}
```

#### 2.2.2 警告色 (Warning)

| 色阶 | 色值 | 用途 |
|------|------|------|
| Warning-50 | `#FFFBE6` | 警告状态背景 |
| **Warning-500** | **`#FAAD14`** | **警告状态主色** |
| Warning-600 | `#D48806` | 悬停态 |

```css
/* 警告状态示例 */
.status-warning {
  background-color: #FFFBE6;
  border: 1px solid #FFD666;
  color: #FAAD14;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}
```

#### 2.2.3 错误色 (Error)

| 色阶 | 色值 | 用途 |
|------|------|------|
| Error-50 | `#FFF1F0` | 错误状态背景 |
| **Error-500** | **`#FF4D4F`** | **错误状态主色** |
| Error-600 | `#CF1322` | 悬停态 |

```css
/* 错误状态示例 */
.status-error {
  background-color: #FFF1F0;
  border: 1px solid #FFA39E;
  color: #FF4D4F;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}
```

#### 2.2.4 信息色 (Info)

| 色阶 | 色值 | 用途 |
|------|------|------|
| Info-50 | `#E6F7FF` | 信息提示背景 |
| **Info-500** | **`#1890FF`** | **信息提示主色** |
| Info-600 | `#096DD9` | 悬停态 |

```css
/* 信息提示示例 */
.status-info {
  background-color: #E6F7FF;
  border: 1px solid #91D5FF;
  color: #1890FF;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}
```

### 2.3 中性色 (Neutral Colors)

中性色用于界面结构、文字、边框等元素，确保层次分明、阅读舒适。

#### 2.3.1 灰度色阶

| 色阶 | 色值 | 用途 |
|------|------|------|
| Gray-50 | `#FAFAFA` | 页面背景、分隔背景 |
| Gray-100 | `#F5F5F5` | 表悬停、卡片背景 |
| Gray-200 | `#E8E8E8` | 边框、分割线 |
| Gray-300 | `#D9D9D9` | 禁用边框 |
| Gray-400 | `#BFBFBF` | 占位符文字 |
| Gray-500 | `#8C8C8C` | 辅助文字 |
| Gray-600 | `#595959` | 次级正文 |
| Gray-700 | `#434343` | 正文文字 |
| Gray-800 | `#262626` | 标题文字 |
| Gray-900 | `#1A1A1A` | 强调文字 |

#### 2.3.2 文字颜色规范

| 用途 | 色值 | 说明 |
|------|------|------|
| 主要文字 | `#333333` | 正文内容、标题 |
| 次要文字 | `#666666` | 辅助说明、描述 |
| 辅助文字 | `#999999` | 提示信息、占位符 |
| 禁用文字 | `#BFBFBF` | 不可用状态 |
| 链接文字 | `#00B578` | 可点击链接 |
| 错误文字 | `#FF4D4F` | 错误提示 |
| 成功文字 | `#52C41A` | 成功状态 |
| 警告文字 | `#FAAD14` | 警告提示 |
| 反转文字 | `#FFFFFF` | 深色背景上使用 |

#### 2.3.3 背景颜色规范

| 用途 | 色值 | 说明 |
|------|------|------|
| 页面背景 | `#F5F5F5` | 整体页面底色 |
| 卡片背景 | `#FFFFFF` | 内容卡片 |
| 悬停背景 | `#F5F5F5` | 鼠标悬停 |
| 选中背景 | `#E6F7F0` | 选中状态 |
| 禁用背景 | `#F5F5F5` | 不可用状态 |
| 成功背景 | `#F6FFED` | 成功提示 |
| 警告背景 | `#FFFBE6` | 警告提示 |
| 错误背景 | `#FFF1F0` | 错误提示 |
| 信息背景 | `#E6F7FF` | 信息提示 |

#### 2.3.4 边框颜色规范

| 用途 | 色值 | 说明 |
|------|------|------|
| 默认边框 | `#E8E8E8` | 常规边框 |
| 深色边框 | `#D9D9D9` | 强调边框 |
| 浅色边框 | `#F0F0F0` | 分割线 |
| 主色边框 | `#00B578` | 主色强调 |
| 错误边框 | `#FFA39E` | 错误状态 |
| 警告边框 | `#FFD666` | 警告状态 |
| 成功边框 | `#B7EB8F` | 成功状态 |
| 信息边框 | `#91D5FF` | 信息状态 |
| 禁用边框 | `#D9D9D9` | 不可用状态 |

### 2.4 色彩使用规范

#### 2.4.1 主色使用比例

```
60% 中性色（灰白背景、文字）
30% 主色（按钮、强调、图标）
10% 辅助色（状态提示、标签）
```

#### 2.4.2 颜色对比度要求

| 文字大小 | 最小对比度 | 推荐对比度 |
|----------|------------|------------|
| 正常文字 (<18px) | 4.5:1 | 7:1 |
| 大文字 (≥18px) | 3:1 | 4.5:1 |

#### 2.4.3 深色模式配色

| 元素 | 浅色模式 | 深色模式 |
|------|----------|----------|
| 页面背景 | `#F5F5F5` | `#1A1A1A` |
| 卡片背景 | `#FFFFFF` | `#262626` |
| 主要文字 | `#333333` | `#E8E8E8` |
| 次要文字 | `#666666` | `#8C8C8C` |
| 边框 | `#E8E8E8` | `#434343` |
| 主色 | `#00B578` | `#00D68E` |

---

## 3. 字体系统

### 3.1 字体家族

#### 3.1.1 中文字体栈

```css
font-family: -apple-system, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "Helvetica Neue", Arial, sans-serif;
```

**字体优先级说明：**

| 优先级 | 字体 | 适用平台 |
|--------|------|----------|
| 1 | `-apple-system` | iOS/macOS 系统默认字体 |
| 2 | `"PingFang SC"` | macOS/iOS 苹方字体 |
| 3 | `"Hiragino Sans GB"` | 冬青黑体，高质量中日文字体 |
| 4 | `"Microsoft YaHei"` | Windows 系统默认雅黑字体 |
| 5 | `"Helvetica Neue"` | 经典西文无衬线字体 |
| 6 | `Arial` | 通用西文无衬线字体 |
| 7 | `sans-serif` | 最终备用字体 |

#### 3.1.2 英文字体栈

```css
font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
```

#### 3.1.3 代码字体栈

```css
font-family: "SF Mono", "Fira Code", "Source Code Pro", Consolas, Monaco, monospace;
```

### 3.2 字号体系

#### 3.2.1 字号规格表

| 层级 | 名称 | 字号 | 字重 | 行高 | 用途 |
|------|------|------|------|------|------|
| H1 | 大标题 | 20px | 600 | 28px | 页面主标题、数据大数字 |
| H2 | 标题 | 18px | 600 | 26px | 区块标题、卡片标题 |
| H3 | 小标题 | 16px | 600 | 24px | 子标题、列表标题 |
| Body | 正文 | 14px | 400 | 22px | 正文内容、描述文字 |
| Caption | 辅助文字 | 12px | 400 | 20px | 提示信息、时间、备注 |
| Small | 小字 | 10px | 400 | 16px | 标签、角标、次要信息 |

#### 3.2.2 字重定义

| 字重值 | 名称 | 用途 |
|--------|------|------|
| 400 | Regular | 正文、描述 |
| 500 | Medium | 次级标题、强调文字 |
| 600 | Semibold | 标题、按钮文字、重要数据 |
| 700 | Bold | 主标题、数字强调 |

#### 3.2.3 代码实现示例

```scss
// 字体变量定义
$font-family-base: -apple-system, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "Helvetica Neue", Arial, sans-serif;

// 字号系统
$font-size-h1: 20px;      // 大标题
$font-size-h2: 18px;      // 标题
$font-size-h3: 16px;      // 小标题
$font-size-body: 14px;    // 正文
$font-size-caption: 12px; // 辅助文字
$font-size-small: 10px;   // 小字

// 字重
$font-weight-regular: 400;
$font-weight-medium: 500;
$font-weight-semibold: 600;
$font-weight-bold: 700;

// 行高
$line-height-h1: 28px;
$line-height-h2: 26px;
$line-height-h3: 24px;
$line-height-body: 22px;
$line-height-caption: 20px;
$line-height-small: 16px;

// 字体样式类
.text-h1 {
  font-size: $font-size-h1;
  font-weight: $font-weight-semibold;
  line-height: $line-height-h1;
}

.text-h2 {
  font-size: $font-size-h2;
  font-weight: $font-weight-semibold;
  line-height: $line-height-h2;
}

.text-h3 {
  font-size: $font-size-h3;
  font-weight: $font-weight-semibold;
  line-height: $line-height-h3;
}

.text-body {
  font-size: $font-size-body;
  font-weight: $font-weight-regular;
  line-height: $line-height-body;
}

.text-caption {
  font-size: $font-size-caption;
  font-weight: $font-weight-regular;
  line-height: $line-height-caption;
}

.text-small {
  font-size: $font-size-small;
  font-weight: $font-weight-regular;
  line-height: $line-height-small;
}
```

### 3.3 排版规范

#### 3.3.1 段落规范

| 元素 | 规格 | 说明 |
|------|------|------|
| 段间距 | 8px (0.5em) | 段落之间间距 |
| 行内间距 | 4px | 行内元素间距 |
| 首行缩进 | 0 | 移动端不缩进 |
| 对齐方式 | 左对齐 | 正文左对齐，数字右对齐 |
| 最大行宽 | 35个中文字符 | 保证阅读舒适度 |

#### 3.3.2 标题规范

```
页面主标题 (H1)
├── 区块标题 (H2)
│   ├── 子标题 (H3)
│   └── 子标题 (H3)
├── 区块标题 (H2)
│   └── 子标题 (H3)
```

#### 3.3.3 列表规范

**无序列表：**
```css
.list-unstyled {
  list-style: none;
  padding-left: 0;
}

.list-bullet {
  list-style: disc;
  padding-left: 20px;
}
```

**有序列表：**
```css
.list-numbered {
  list-style: decimal;
  padding-left: 20px;
}
```

#### 3.3.4 文字排版细节

**数字排版：**
```css
/* 等宽数字，便于对齐 */
.font-variant-numeric {
  font-variant-numeric: tabular-nums;
  font-feature-settings: "tnum";
}

/* 金额显示 */
.price {
  font-family: "DIN Alternate", "Helvetica Neue", Arial, sans-serif;
  font-weight: 700;
  font-size: 20px;
  color: #FF4D4F;
}
```

**中英文混排：**
```css
/* 中英文自动加间距 */
.mixed-text {
  word-spacing: 0.1em;
}

/* 英文单词换行 */
.english-text {
  word-break: break-word;
  hyphens: auto;
}
```

---

## 4. 间距与布局系统

### 4.1 间距体系 (Spacing System)

采用 4px 基础单位的间距系统，确保视觉节奏的一致性和可预测性。

#### 4.1.1 间距规格表

| 名称 | 数值 | 用途 |
|------|------|------|
| xs (超小) | 4px | 图标与文字间距、紧凑内边距 |
| sm (小) | 8px | 组件内部间距、列表项间距 |
| md (中) | 12px | 卡片内边距、表单项间距 |
| lg (大) | 16px | 区块间距、容器内边距 |
| xl (超大) | 20px | 大区块间距、页面边距 |
| xxl (巨大) | 24px | 模块分隔、大卡片间距 |
| xxxl | 32px | 大模块间距、页面区块分隔 |
| xxxxl | 40px | 超大间距、Hero 区域 |
| safe | 34px | iOS 底部安全区域 |

#### 4.1.2 间距变量定义 (SCSS)

```scss
// 基础间距单位
$spacing-unit: 4px;

// 间距变量
$spacing-xs: $spacing-unit;        // 4px
$spacing-sm: $spacing-unit * 2;    // 8px
$spacing-md: $spacing-unit * 3;    // 12px
$spacing-lg: $spacing-unit * 4;    // 16px
$spacing-xl: $spacing-unit * 5;    // 20px
$spacing-xxl: $spacing-unit * 6;   // 24px
$spacing-xxxl: $spacing-unit * 8;  // 32px
$spacing-xxxxl: $spacing-unit * 10; // 40px

// iOS 安全区域
$safe-area-bottom: 34px;

// 间距工具类生成
@each $name, $value in (
  'xs': $spacing-xs,
  'sm': $spacing-sm,
  'md': $spacing-md,
  'lg': $spacing-lg,
  'xl': $spacing-xl,
  'xxl': $spacing-xxl,
  'xxxl': $spacing-xxxl,
  'xxxxl': $spacing-xxxxl
) {
  // 内边距
  .p-#{$name} { padding: $value; }
  .px-#{$name} { padding-left: $value; padding-right: $value; }
  .py-#{$name} { padding-top: $value; padding-bottom: $value; }
  .pt-#{$name} { padding-top: $value; }
  .pr-#{$name} { padding-right: $value; }
  .pb-#{$name} { padding-bottom: $value; }
  .pl-#{$name} { padding-left: $value; }
  
  // 外边距
  .m-#{$name} { margin: $value; }
  .mx-#{$name} { margin-left: $value; margin-right: $value; }
  .my-#{$name} { margin-top: $value; margin-bottom: $value; }
  .mt-#{$name} { margin-top: $value; }
  .mr-#{$name} { margin-right: $value; }
  .mb-#{$name} { margin-bottom: $value; }
  .ml-#{$name} { margin-left: $value; }
  
  // 间隔（Gap）
  .gap-#{$name} { gap: $value; }
}

// 特殊间距类
.p-safe { padding-bottom: $safe-area-bottom; }
.m-safe { margin-bottom: $safe-area-bottom; }
```

### 4.2 布局体系 (Layout System)

#### 4.2.1 容器系统

**页面容器 (Page Container)**
```scss
// 页面基础容器
.page-container {
  width: 100%;
  min-height: 100vh;
  background-color: #F5F5F5;
  padding-bottom: $safe-area-bottom;
}

// 内容容器（带安全边距）
.content-container {
  width: 100%;
  padding: $spacing-lg;
  box-sizing: border-box;
}

// 固定宽度容器（平板适配）
.fixed-container {
  max-width: 540px;
  margin: 0 auto;
  width: 100%;
}
```

**卡片容器 (Card Container)**
```scss
// 基础卡片
.card {
  background-color: #FFFFFF;
  border-radius: 8px;
  padding: $spacing-lg;
  margin-bottom: $spacing-md;
  
  // 阴影变体
  &--shadow {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }
  
  // 无边距变体
  &--flat {
    margin-bottom: 0;
    border-radius: 0;
  }
}

// 列表卡片
.list-card {
  background-color: #FFFFFF;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: $spacing-md;
}
```

#### 4.2.2 栅格系统

**12 列栅格系统**
```scss
// 栅格容器
.row {
  display: flex;
  flex-wrap: wrap;
  margin-right: -$spacing-sm;
  margin-left: -$spacing-sm;
}

// 栅格列
.col {
  flex: 1 0 0%;
  max-width: 100%;
  padding-right: $spacing-sm;
  padding-left: $spacing-sm;
}

// 固定宽度列
@for $i from 1 through 12 {
  .col-#{$i} {
    flex: 0 0 percentage($i / 12);
    max-width: percentage($i / 12);
  }
}

// 响应式栅格
@media (min-width: 768px) {
  @for $i from 1 through 12 {
    .col-md-#{$i} {
      flex: 0 0 percentage($i / 12);
      max-width: percentage($i / 12);
    }
  }
}
```

**使用示例：**
```html
<!-- 等分列 -->
<div class="row">
  <div class="col">列 1</div>
  <div class="col">列 2</div>
  <div class="col">列 3</div>
</div>

<!-- 固定比例 -->
<div class="row">
  <div class="col-4">1/3</div>
  <div class="col-8">2/3</div>
</div>

<!-- 快捷功能按钮 -->
<div class="row">
  <div class="col-3">
    <div class="quick-action">
      <i class="icon-consult"></i>
      <span>图文咨询</span>
    </div>
  </div>
  <div class="col-3">
    <div class="quick-action">
      <i class="icon-video"></i>
      <span>视频问诊</span>
    </div>
  </div>
  <div class="col-3">
    <div class="quick-action">
      <i class="icon-prescription"></i>
      <span>开具处方</span>
    </div>
  </div>
  <div class="col-3">
    <div class="quick-action">
      <i class="icon-patient"></i>
      <span>患者管理</span>
    </div>
  </div>
</div>
```

#### 4.2.3 Flexbox 布局工具

```scss
// Flex 容器
.flex {
  display: flex;
}

.inline-flex {
  display: inline-flex;
}

// 主轴方向
.flex-row {
  flex-direction: row;
}

.flex-row-reverse {
  flex-direction: row-reverse;
}

.flex-col {
  flex-direction: column;
}

.flex-col-reverse {
  flex-direction: column-reverse;
}

// 主轴对齐
.justify-start {
  justify-content: flex-start;
}

.justify-end {
  justify-content: flex-end;
}

.justify-center {
  justify-content: center;
}

.justify-between {
  justify-content: space-between;
}

.justify-around {
  justify-content: space-around;
}

.justify-evenly {
  justify-content: space-evenly;
}

// 交叉轴对齐
.items-start {
  align-items: flex-start;
}

.items-end {
  align-items: flex-end;
}

.items-center {
  align-items: center;
}

.items-baseline {
  align-items: baseline;
}

.items-stretch {
  align-items: stretch;
}

// 换行
.flex-nowrap {
  flex-wrap: nowrap;
}

.flex-wrap {
  flex-wrap: wrap;
}

.flex-wrap-reverse {
  flex-wrap: wrap-reverse;
}

// 弹性伸缩
.flex-1 {
  flex: 1 1 0%;
}

.flex-auto {
  flex: 1 1 auto;
}

.flex-initial {
  flex: 0 1 auto;
}

.flex-none {
  flex: none;
}

// 等分
@for $i from 1 through 12 {
  .flex-#{$i} {
    flex: $i;
  }
}
```

### 4.3 响应式断点

```scss
// 断点定义
$breakpoints: (
  'xs': 0,       // 手机竖屏
  'sm': 576px,   // 手机横屏/小平板
  'md': 768px,   // 平板竖屏
  'lg': 992px,   // 平板横屏/小桌面
  'xl': 1200px,  // 桌面显示器
  'xxl': 1400px  // 大桌面显示器
);

// 媒体查询工具
@mixin media-up($breakpoint) {
  $min: map-get($breakpoints, $breakpoint);
  @if $min != 0 {
    @media (min-width: $min) {
      @content;
    }
  } @else {
    @content;
  }
}

@mixin media-down($breakpoint) {
  $max: map-get($breakpoints, $breakpoint) - 0.02px;
  @media (max-width: $max) {
    @content;
  }
}

@mixin media-between($lower, $upper) {
  $min: map-get($breakpoints, $lower);
  $max: map-get($breakpoints, $upper) - 0.02px;
  @media (min-width: $min) and (max-width: $max) {
    @content;
  }
}

// 使用示例
.element {
  width: 100%;
  
  @include media-up('md') {
    width: 50%;
  }
  
  @include media-up('lg') {
    width: 33.333%;
  }
}
```

---

## 5. 组件库规范

### 5.1 按钮组件 (Button)

#### 5.1.1 按钮类型

| 类型 | 说明 | 适用场景 |
|------|------|----------|
| Primary | 主按钮，实心主色 | 主要操作、提交、确认 |
| Secondary | 次级按钮，描边主色 | 次要操作、备选方案 |
| Text | 文字按钮，无背景 | 低优先级操作、链接样式 |
| Ghost | 幽灵按钮，透明背景 | 深色背景上使用 |
| Link | 链接按钮，文字+图标 | 跳转、外链 |

#### 5.1.2 按钮尺寸

| 尺寸 | 高度 | 内边距 | 字号 | 适用场景 |
|------|------|--------|------|----------|
| XSmall | 24px | 0 8px | 12px | 紧凑空间、小卡片 |
| Small | 28px | 0 12px | 12px | 表格操作、小弹窗 |
| Medium | 36px | 0 16px | 14px | 默认尺寸、常规场景 |
| Large | 44px | 0 24px | 16px | 重要操作、落地页 |
| XLarge | 56px | 0 32px | 18px | 主 CTA、引导页 |

#### 5.1.3 按钮状态

| 状态 | 视觉表现 | 说明 |
|------|----------|------|
| Default | 正常样式 | 默认状态 |
| Hover | 颜色加深 10% | 鼠标悬停 |
| Active | 颜色加深 20%，向内阴影 | 点击瞬间 |
| Focus | 外发光 2px 主色 20% | 键盘聚焦 |
| Disabled | 透明度 40%，光标禁用 | 不可用状态 |
| Loading | 旋转图标替代文字 | 加载中状态 |

#### 5.1.4 按钮样式代码

```scss
// 按钮基础样式
@mixin button-base {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  border: 1px solid transparent;
  border-radius: 8px;
  font-family: inherit;
  font-weight: 600;
  line-height: 1;
  text-align: center;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
  white-space: nowrap;
  
  &:disabled,
  &.is-disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }
  
  &.is-loading {
    pointer-events: none;
    
    &::before {
      content: '';
      width: 1em;
      height: 1em;
      border: 2px solid currentColor;
      border-right-color: transparent;
      border-radius: 50%;
      animation: button-spin 1s linear infinite;
    }
  }
}

@keyframes button-spin {
  to { transform: rotate(360deg); }
}

// 按钮变体
.btn {
  @include button-base;
  
  // 尺寸变体
  &--xs {
    height: 24px;
    padding: 0 8px;
    font-size: 12px;
    border-radius: 4px;
  }
  
  &--sm {
    height: 28px;
    padding: 0 12px;
    font-size: 12px;
    border-radius: 6px;
  }
  
  &--md {
    height: 36px;
    padding: 0 16px;
    font-size: 14px;
    border-radius: 8px;
  }
  
  &--lg {
    height: 44px;
    padding: 0 24px;
    font-size: 16px;
    border-radius: 8px;
  }
  
  &--xl {
    height: 56px;
    padding: 0 32px;
    font-size: 18px;
    border-radius: 12px;
    width: 100%;
  }
  
  // 类型变体
  &--primary {
    background-color: #00B578;
    color: #FFFFFF;
    border-color: #00B578;
    
    &:hover:not(:disabled) {
      background-color: #009A65;
      border-color: #009A65;
    }
    
    &:active:not(:disabled) {
      background-color: #007A50;
      border-color: #007A50;
    }
  }
  
  &--secondary {
    background-color: transparent;
    color: #00B578;
    border-color: #00B578;
    
    &:hover:not(:disabled) {
      background-color: #E6F7F0;
    }
    
    &:active:not(:disabled) {
      background-color: #B3E8D4;
    }
  }
  
  &--text {
    background-color: transparent;
    color: #00B578;
    border-color: transparent;
    
    &:hover:not(:disabled) {
      background-color: #E6F7F0;
    }
  }
  
  &--ghost {
    background-color: rgba(255, 255, 255, 0.1);
    color: #FFFFFF;
    border-color: rgba(255, 255, 255, 0.3);
    
    &:hover:not(:disabled) {
      background-color: rgba(255, 255, 255, 0.2);
    }
  }
  
  &--danger {
    background-color: #FF4D4F;
    color: #FFFFFF;
    border-color: #FF4D4F;
    
    &:hover:not(:disabled) {
      background-color: #CF1322;
      border-color: #CF1322;
    }
  }
}

// 块级按钮
.btn-block {
  display: flex;
  width: 100%;
}

// 图标按钮
.btn-icon {
  padding: 0;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  
  &--sm {
    width: 28px;
    height: 28px;
  }
  
  &--lg {
    width: 44px;
    height: 44px;
  }
}
```

#### 4.1.5 按钮组合

```scss
// 按钮组
.btn-group {
  display: inline-flex;
  vertical-align: middle;
  
  .btn {
    border-radius: 0;
    
    &:first-child {
      border-top-left-radius: 8px;
      border-bottom-left-radius: 8px;
    }
    
    &:last-child {
      border-top-right-radius: 8px;
      border-bottom-right-radius: 8px;
    }
    
    &:not(:first-child) {
      margin-left: -1px;
    }
    
    &:hover,
    &:focus {
      z-index: 1;
    }
  }
}
```

### 5.2 输入组件 (Input)

#### 5.2.1 输入框类型

| 类型 | 说明 | 适用场景 |
|------|------|----------|
| Text | 单行文本输入 | 姓名、标题等短文本 |
| Password | 密码输入 | 密码、敏感信息 |
| Number | 数字输入 | 金额、数量、年龄 |
| Tel | 电话输入 | 手机号、电话号码 |
| Email | 邮箱输入 | 电子邮箱地址 |
| Search | 搜索输入 | 搜索关键词 |
| Textarea | 多行文本输入 | 描述、备注、诊断 |

#### 5.2.2 输入框尺寸

| 尺寸 | 高度 | 内边距 | 字号 | 圆角 |
|------|------|--------|------|------|
| Small | 32px | 0 12px | 12px | 6px |
| Medium | 40px | 0 16px | 14px | 8px |
| Large | 48px | 0 20px | 16px | 8px |

#### 5.2.3 输入框状态

| 状态 | 视觉表现 |
|------|----------|
| Default | 灰色边框，正常背景 |
| Hover | 边框变深，轻微阴影 |
| Focus | 主色边框，外发光 |
| Disabled | 灰色背景，禁用光标 |
| Error | 错误色边框，错误提示 |
| Success | 成功色边框，对勾图标 |

#### 5.2.4 输入框样式代码

```scss
// 输入框基础样式
.input {
  display: inline-flex;
  align-items: center;
  width: 100%;
  height: 40px;
  padding: 0 16px;
  font-size: 14px;
  line-height: 1.5;
  color: #333333;
  background-color: #FFFFFF;
  border: