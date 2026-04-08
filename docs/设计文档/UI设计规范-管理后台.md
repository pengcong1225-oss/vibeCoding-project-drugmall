# DrugMall 药品电商平台 - 管理后台UI设计规范

## 版本信息

| 项目 | 内容 |
|------|------|
| 文档版本 | v1.0.0 |
| 创建日期 | 2026-04-08 |
| 适用范围 | DrugMall 管理后台 |
| 设计工具 | Figma / Sketch |
| 技术栈 | Vue 3 + Element Plus + TypeScript |

---

## 1. 设计原则

### 1.1 专业管理系统风格

DrugMall管理后台采用专业、沉稳、高效的设计语言，体现医药行业严谨、可靠、值得信赖的品牌形象。

**核心特征：**
- **专业稳重**：使用深蓝色系作为主色调，传递专业、可靠的医疗行业形象
- **清晰高效**：信息层级清晰，操作流程明确，提升管理员工作效率
- **数据优先**：突出数据展示，支持决策分析和业务监控
- **安全可靠**：界面设计体现药品管理的严肃性和安全性要求

### 1.2 设计准则

#### 1.2.1 清晰性准则

- **信息层次明确**：通过字体大小、颜色深浅、间距大小建立清晰的信息层级
- **重点内容突出**：关键数据、重要操作使用醒目的视觉样式
- **留白合理**：适当的留白减少视觉疲劳，提升阅读体验
- **对齐统一**：严格遵循对齐原则，保持界面整洁有序

#### 1.2.2 效率性准则

- **操作路径最短**：常用功能快速可达，减少操作步骤
- **批量操作支持**：支持批量选择、批量编辑，提升处理效率
- **快捷操作入口**：提供搜索、筛选、排序等快速定位功能
- **键盘快捷键**：支持常用操作的键盘快捷键

#### 1.2.3 一致性准则

- **视觉一致**：相同类型的元素保持一致的视觉样式
- **交互一致**：相同的操作保持一致的交互反馈
- **布局一致**：同类页面保持一致的布局结构
- **文案一致**：相同概念使用一致的术语表达

#### 1.2.4 可扩展性准则

- **模块化设计**：组件、页面、功能模块化，便于扩展和维护
- **配置化管理**：支持通过配置实现功能扩展，减少代码修改
- **预留扩展点**：设计中预留功能扩展的接口和空间
- **向后兼容**：设计变更考虑向后兼容，减少升级成本

### 1.3 用户体验目标

| 目标维度 | 具体目标 | 衡量指标 |
|---------|---------|---------|
| 效率 | 管理员能快速完成任务 | 任务完成时间 < 基准值 80% |
| 准确 | 减少操作错误 | 错误率 < 1% |
| 易学 | 新用户能快速上手 | 上手时间 < 30分钟 |
| 满意 | 用户对界面满意 | 满意度评分 > 4.5/5 |

---

## 2. 颜色系统

### 2.1 主色调

主色调采用专业的蓝色系，体现医疗行业的专业性、可靠性和科技感。

#### 2.1.1 主色板（Primary Colors）

| 色阶 | 色值 | 名称 | 用途 |
|-----|------|------|------|
| Primary-900 | `#0d47a1` | 深靛蓝 | 重点强调、激活状态 |
| Primary-800 | `#1565c0` | 深海蓝 | 主要按钮、重要链接 |
| **Primary-700** | **`#1a73e8`** | **品牌主色** | **主按钮、品牌标识** |
| Primary-600 | `#2563eb` | 亮蓝 | 悬停状态、高亮 |
| Primary-500 | `#3b82f6` | 天蓝 | 次要按钮、图标 |
| Primary-400 | `#60a5fa` | 浅天蓝 | 背景、禁用状态 |
| Primary-300 | `#93c5fd` | 淡蓝 | 边框、分隔线 |
| Primary-200 | `#bfdbfe` | 极淡蓝 | 浅色背景 |
| Primary-100 | `#dbeafe` | 背景蓝 | 卡片背景、悬停背景 |
| Primary-50 | `#eff6ff` | 极浅蓝 | 页面背景、表格交替行 |

#### 2.1.2 主色使用规范

**Primary-700 (#1a73e8) 使用场景：**
- 主要操作按钮（Primary Button）
- 品牌标识和Logo
- 导航选中状态
- 重要链接和可点击文字
- 关键数据指标展示

```css
/* 主按钮样式 */
.btn-primary {
  background-color: #1a73e8;
  border-color: #1a73e8;
  color: #ffffff;
}

.btn-primary:hover {
  background-color: #1565c0;
  border-color: #1565c0;
}

.btn-primary:active {
  background-color: #0d47a1;
  border-color: #0d47a1;
}
```

### 2.2 辅助色

辅助色用于区分不同类型信息，提供视觉层次和功能区分。

#### 2.2.1 功能色板（Functional Colors）

**成功色 (Success)** - 表示成功、完成、正常状态

| 色阶 | 色值 | 用途 |
|-----|------|------|
| Success-900 | `#14532d` | 深绿文字 |
| **Success-500** | **`#22c55e`** | **成功主色** |
| Success-100 | `#dcfce7` | 成功背景 |
| Success-50 | `#f0fdf4` | 浅色背景 |

**警告色 (Warning)** - 表示警告、注意、待处理状态

| 色阶 | 色值 | 用途 |
|-----|------|------|
| Warning-900 | `#713f12` | 深黄文字 |
| **Warning-500** | **`#f59e0b`** | **警告主色** |
| Warning-100 | `#fef3c7` | 警告背景 |
| Warning-50 | `#fffbeb` | 浅色背景 |

**错误色 (Error)** - 表示错误、失败、异常状态

| 色阶 | 色值 | 用途 |
|-----|------|------|
| Error-900 | `#7f1d1d` | 深红文字 |
| **Error-500** | **`#ef4444`** | **错误主色** |
| Error-100 | `#fee2e2` | 错误背景 |
| Error-50 | `#fef2f2` | 浅色背景 |

**信息色 (Info)** - 表示提示、说明、进行中状态

| 色阶 | 色值 | 用途 |
|-----|------|------|
| Info-900 | `#0c4a6e` | 深蓝文字 |
| **Info-500** | **`#3b82f6`** | **信息主色** |
| Info-100 | `#dbeafe` | 信息背景 |
| Info-50 | `#eff6ff` | 浅色背景 |

#### 2.2.2 辅助色使用规范

**状态标识使用：**

```css
/* 成功状态 */
.status-success {
  background-color: #dcfce7;
  color: #14532d;
  border: 1px solid #86efac;
}

/* 警告状态 */
.status-warning {
  background-color: #fef3c7;
  color: #713f12;
  border: 1px solid #fcd34d;
}

/* 错误状态 */
.status-error {
  background-color: #fee2e2;
  color: #7f1d1d;
  border: 1px solid #fca5a5;
}

/* 进行中状态 */
.status-info {
  background-color: #dbeafe;
  color: #0c4a6e;
  border: 1px solid #93c5fd;
}
```

### 2.3 中性色

中性色用于背景、文字、边框、分隔线等界面元素，提供层次感和可读性。

#### 2.3.1 中性色板（Neutral Colors）

| 色阶 | 色值 | CSS变量 | 用途 |
|-----|------|---------|------|
| Gray-900 | `#111827` | `--gray-900` | 主标题、重要文字 |
| Gray-800 | `#1f2937` | `--gray-800` | 副标题、强调文字 |
| Gray-700 | `#374151` | `--gray-700` | 正文、说明文字 |
| Gray-600 | `#4b5563` | `--gray-600` | 次要文字、描述 |
| Gray-500 | `#6b7280` | `--gray-500` | 辅助文字、占位符 |
| Gray-400 | `#9ca3af` | `--gray-400` | 禁用文字、图标 |
| Gray-300 | `#d1d5db` | `--gray-300` | 边框、分隔线 |
| Gray-200 | `#e5e7eb` | `--gray-200` | 浅色边框、背景分隔 |
| Gray-100 | `#f3f4f6` | `--gray-100` | 卡片背景、交替行 |
| Gray-50 | `#f9fafb` | `--gray-50` | 页面背景、侧边栏背景 |

#### 2.3.2 中性色使用规范

**文字颜色层级：**

```css
/* 主要标题 - 页面标题 */
.text-title {
  color: #111827;
  font-weight: 600;
}

/* 次要标题 - 模块标题 */
.text-subtitle {
  color: #1f2937;
  font-weight: 600;
}

/* 正文内容 */
.text-body {
  color: #374151;
}

/* 次要文字 - 描述、说明 */
.text-secondary {
  color: #6b7280;
}

/* 辅助文字 - 占位符、提示 */
.text-hint {
  color: #9ca3af;
}

/* 禁用状态 */
.text-disabled {
  color: #9ca3af;
}
```

**背景颜色使用：**

```css
/* 页面背景 */
.bg-page {
  background-color: #f9fafb;
}

/* 卡片背景 */
.bg-card {
  background-color: #ffffff;
}

/* 侧边栏背景 */
.bg-sidebar {
  background-color: #f9fafb;
}

/* 表头背景 */
.bg-table-header {
  background-color: #f3f4f6;
}

/* 交替行背景 */
.bg-table-striped {
  background-color: #f9fafb;
}

/* 悬停背景 */
.bg-hover:hover {
  background-color: #f3f4f6;
}

/* 选中背景 */
.bg-selected {
  background-color: #eff6ff;
}
```

**边框颜色使用：**

```css
/* 主边框 */
.border-primary {
  border-color: #e5e7eb;
}

/* 强边框 */
.border-strong {
  border-color: #d1d5db;
}

/* 弱边框 */
.border-weak {
  border-color: #f3f4f6;
}

/* 分隔线 */
.divider {
  border-color: #e5e7eb;
}
```

### 2.4 数据可视化色板

为图表和数据可视化提供完整的颜色方案，确保数据展示的清晰度和美观性。

#### 2.4.1 图表主色系列

**系列色板（按使用优先级排序）：**

| 序号 | 色值 | 名称 | 适用场景 |
|-----|------|------|---------|
| 1 | `#1a73e8` | 品牌蓝 | 主数据、核心指标 |
| 2 | `#22c55e` | 成功绿 | 正向数据、增长趋势 |
| 3 | `#f59e0b` | 警告黄 | 警示数据、待处理项 |
| 4 | `#ef4444` | 错误红 | 异常数据、下降趋势 |
| 5 | `#8b5cf6` | 紫罗兰 | 辅助数据、分类数据 |
| 6 | `#06b6d4` | 青色 | 辅助数据、对比数据 |
| 7 | `#ec4899` | 粉色 | 特殊数据、女性相关 |
| 8 | `#14b8a6` | 蓝绿 | 辅助数据、健康相关 |

#### 2.4.2 图表类型配色方案

**折线图/面积图配色：**
```javascript
const lineChartColors = {
  primary: '#1a73e8',      // 主折线
  secondary: '#22c55e',    // 对比折线
  tertiary: '#8b5cf6',     // 第三指标
  quaternary: '#f59e0b',   // 趋势预警
  areaFill: {              // 面积填充透明度
    primary: 'rgba(26, 115, 232, 0.1)',
    secondary: 'rgba(34, 197, 94, 0.1)',
    tertiary: 'rgba(139, 92, 246, 0.1)'
  }
};
```

**柱状图/条形图配色：**
```javascript
const barChartColors = {
  positive: '#22c55e',     // 正向数据
  negative: '#ef4444',     // 负向数据
  neutral: '#1a73e8',      // 中性数据
  warning: '#f59e0b',      // 预警数据
  hover: {                 // 悬停状态
    positive: '#16a34a',
    negative: '#dc2626',
    neutral: '#1565c0',
    warning: '#d97706'
  }
};
```

**饼图/环形图配色：**
```javascript
const pieChartColors = [
  '#1a73e8',  // 占比最大
  '#22c55e',  // 占比第二大
  '#f59e0b',  // 占比第三大
  '#ef4444',  // 占比第四大
  '#8b5cf6',  // 占比第五大
  '#06b6d4',  // 其他
  '#ec4899',  // 其他
  '#9ca3af'   // 其他
];
```

**散点图/气泡图配色：**
```javascript
const scatterColors = {
  cluster1: '#1a73e8',
  cluster2: '#22c55e',
  cluster3: '#f59e0b',
  cluster4: '#ef4444',
  cluster5: '#8b5cf6',
  outlier: '#6b7280'
};
```

#### 2.4.3 图表辅助元素配色

**坐标轴和网格：**
```css
/* 坐标轴颜色 */
--chart-axis-line: #e5e7eb;
--chart-axis-text: #6b7280;
--chart-axis-title: #374151;

/* 网格线 */
--chart-grid-line: #f3f4f6;
--chart-grid-zero: #d1d5db;

/* 刻度线 */
--chart-tick-line: #e5e7eb;
--chart-tick-text: #6b7280;
```

**提示框和图例：**
```css
/* 提示框 */
--chart-tooltip-bg: rgba(17, 24, 39, 0.9);
--chart-tooltip-text: #ffffff;
--chart-tooltip-border: rgba(255, 255, 255, 0.1);
--chart-tooltip-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);

/* 图例 */
--chart-legend-text: #374151;
--chart-legend-inactive: #9ca3af;
--chart-legend-marker: currentColor;
```

**数据标签：**
```css
/* 数据标签 */
--chart-label-text: #374151;
--chart-label-bg: #ffffff;
--chart-label-border: #e5e7eb;
--chart-label-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
```

#### 2.4.4 特殊图表配色

**仪表盘/指标卡：**
```javascript
const dashboardColors = {
  // 正常范围
  normal: '#22c55e',
  // 警告范围
  warning: '#f59e0b',
  // 危险范围
  danger: '#ef4444',
  // 背景轨道
  track: '#e5e7eb',
  // 指针
  pointer: '#374151',
  // 刻度
  tick: '#9ca3af',
  tickMajor: '#6b7280'
};
```

**热力图：**
```javascript
const heatmapColors = {
  // 冷色调（低值）
  cold: ['#dbeafe', '#93c5fd', '#60a5fa'],
  // 温色调（中值）
  warm: ['#fde68a', '#fcd34d', '#fbbf24'],
  // 热色调（高值）
  hot: ['#fecaca', '#f87171', '#ef4444'],
  // 无数据
  empty: '#f3f4f6'
};
```

**漏斗图：**
```javascript
const funnelColors = [
  '#1a73e8',  // 第一层 - 品牌蓝
  '#22c55e',  // 第二层 - 成功绿
  '#f59e0b',  // 第三层 - 警告黄
  '#ef4444',  // 第四层 - 错误红
  '#8b5cf6'   // 第五层 - 紫罗兰
];
```

### 2.5 透明度规范

在需要叠加、遮罩、禁用状态等场景下，使用透明度调整：

```css
/* 透明度层级 */
--opacity-0: 0;        /* 完全透明 */
--opacity-10: 0.1;     /* 极淡 - 背景装饰 */
--opacity-20: 0.2;     /* 很淡 - 禁用背景 */
--opacity-30: 0.3;     /* 淡 - 遮罩层 */
--opacity-40: 0.4;     /* 较淡 - 次要信息 */
--opacity-50: 0.5;     /* 半透明 - 分割线 */
--opacity-60: 0.6;     /* 较浓 - 次要文字 */
--opacity-70: 0.7;     /* 浓 - 提示信息 */
--opacity-80: 0.8;     /* 很浓 - 重要文字 */
--opacity-90: 0.9;     /* 极浓 - 强调信息 */
--opacity-100: 1;      /* 完全不透明 -