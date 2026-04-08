## 5. 组件库规范

### 5.1 按钮组件

按钮是用户界面中最常用的交互元素之一，DrugMall患者端定义了五种主要的按钮类型，每种类型都有不同的用途和样式。

#### 5.1.1 按钮类型

| 按钮类型 | 说明 | 使用场景 |
|---------|------|---------|
| 主要按钮（Primary） | 绿色背景、白色文字 | 页面主操作、提交表单、确认操作 |
| 次要按钮（Secondary） | 白色背景、绿色边框和文字 | 次要操作、取消操作、返回上一步 |
| 文本按钮（Text） | 透明背景、绿色文字 | 辅助操作、链接跳转、非主要操作 |
| 图标按钮（Icon） | 图标为主，文字为辅或无 | 工具栏操作、快捷功能 |
| 幽灵按钮（Ghost） | 透明背景、白色边框和文字 | 深色背景上的操作、Banner内按钮 |

#### 5.1.2 按钮尺寸

| 尺寸 | 高度 | 内边距 | 字体大小 | 使用场景 |
|------|------|--------|---------|---------|
| 大（Large） | 48px | 0 24px | 16px | 重要操作、强调按钮 |
| 中（Medium） | 44px | 0 20px | 16px | 标准按钮、最常用 |
| 小（Small） | 36px | 0 16px | 14px | 次要操作、空间受限 |
| 超小（Extra Small） | 28px | 0 12px | 12px | 标签按钮、紧凑布局 |

#### 5.1.3 按钮状态

| 状态 | 样式说明 | 视觉效果 |
|------|---------|---------|
| 默认（Default） | 正常状态 | 标准样式 |
| 悬停（Hover） | 鼠标悬停（桌面端） | 颜色加深、轻微缩放 |
| 按下（Active/Pressed） | 鼠标/手指按下 | 颜色更深、缩放效果 |
| 禁用（Disabled） | 不可操作状态 | 灰色背景、文字变浅 |
| 加载中（Loading） | 操作进行中 | 显示加载动画、禁用点击 |

#### 5.1.4 主要按钮样式代码

```scss
// 主要按钮
.btn-primary {
  // 基础样式
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 44px;
  padding: 0 20px;
  font-size: 16px;
  font-weight: 600;
  color: #FFFFFF;
  background: linear-gradient(135deg, #00B578 0%, #009E67 100%);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  
  // 悬停状态（桌面端）
  &:hover {
    background: linear-gradient(135deg, #009E67 0%, #008A50 100%);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0, 181, 120, 0.3);
  }
  
  // 按下状态
  &:active {
    background: linear-gradient(135deg, #008A50 0%, #007545 100%);
    transform: translateY(0);
    box-shadow: 0 2px 4px rgba(0, 181, 120, 0.2);
  }
  
  // 禁用状态
  &:disabled,
  &.disabled {
    background: #E5E5E5;
    color: #999999;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
  }
  
  // 加载中状态
  &.loading {
    position: relative;
    color: transparent;
    pointer-events: none;
    
    &::after {
      content: '';
      position: absolute;
      width: 16px;
      height: 16px;
      border: 2px solid rgba(255, 255, 255, 0.3);
      border-top-color: #FFFFFF;
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    }
  }
}

// 按钮尺寸变体
.btn-large {
  height: 48px;
  padding: 0 24px;
  font-size: 16px;
}

.btn-small {
  height: 36px;
  padding: 0 16px;
  font-size: 14px;
}

.btn-extra-small {
  height: 28px;
  padding: 0 12px;
  font-size: 12px;
}

// 次要按钮
.btn-secondary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 44px;
  padding: 0 20px;
  font-size: 16px;
  font-weight: 600;
  color: #00B578;
  background: #FFFFFF;
  border: 1px solid #00B578;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    background: #F0FBF7;
    border-color: #009E67;
    color: #009E67;
  }
  
  &:active {
    background: #E6F7EF;
    border-color: #008A50;
    color: #008A50;
  }
  
  &:disabled,
  &.disabled {
    background: #F7F7F7;
    border-color: #E5E5E5;
    color: #CCCCCC;
    cursor: not-allowed;
  }
}

// 文本按钮
.btn-text {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 36px;
  padding: 0 12px;
  font-size: 14px;
  font-weight: 400;
  color: #00B578;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    color: #009E67;
    background: rgba(0, 181, 120, 0.05);
  }
  
  &:active {
    color: #008A50;
    background: rgba(0, 181, 120, 0.1);
  }
  
  &:disabled,
  &.disabled {
    color: #CCCCCC;
    cursor: not-allowed;
    background: transparent;
  }
}

// 图标按钮
.btn-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: transparent;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  
  .icon {
    font-size: 20px;
    color: #666666;
  }
  
  &:hover {
    background: #F5F5F5;
    
    .icon {
      color: #333333;
    }
  }
  
  &:active {
    background: #EBEBEB;
  }
}

// 幽灵按钮（深色背景上）
.btn-ghost {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 44px;
  padding: 0 20px;
  font-size: 16px;
  font-weight: 600;
  color: #FFFFFF;
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    background: rgba(255, 255, 255, 0.1);
    border-color: rgba(255, 255, 255, 0.8);
  }
  
  &:active {
    background: rgba(255, 255, 255, 0.2);
    border-color: #FFFFFF;
  }
}

// 加载动画
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
```

（继续下一部分：卡片组件、输入框组件、标签组件、列表组件、导航组件、弹窗组件等）

---

## 5. 组件库规范（续）

### 5.2 卡片组件

卡片是展示内容的基本容器，用于组织信息和提供视觉层次。

#### 5.2.1 基础卡片

```scss
// 基础卡片
.card {
  background: #FFFFFF;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: box-shadow 0.2s ease;
  
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  }
  
  // 卡片头部
  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px;
    border-bottom: 1px solid #F0F0F0;
    
    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: #333333;
    }
    
    .card-extra {
      font-size: 14px;
      color: #00B578;
    }
  }
  
  // 卡片内容
  .card-body {
    padding: 16px;
  }
  
  // 卡片底部
  .card-footer {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    padding: 12px 16px;
    border-top: 1px solid #F0F0F0;
    background: #FAFAFA;
    gap: 8px;
  }
}
```

#### 5.2.2 药品卡片

```scss
// 药品卡片
.medicine-card {
  background: #FFFFFF;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  
  .medicine-image {
    width: 100%;
    aspect-ratio: 1;
    object-fit: cover;
    background: #F5F5F5;
  }
  
  .medicine-info {
    padding: 12px;
    
    .medicine-name {
      font-size: 14px;
      font-weight: 500;
      color: #333333;
      line-height: 1.4;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      margin-bottom: 4px;
    }
    
    .medicine-spec {
      font-size: 12px;
      color: #999999;
      margin-bottom: 8px;
    }
    
    .medicine-price {
      display: flex;
      align-items: baseline;
      gap: 6px;
      
      .current-price {
        font-size: 16px;
        font-weight: 700;
        color: #FF4D4F;
        
        &::before {
          content: '¥';
          font-size: 12px;
          margin-right: 1px;
        }
      }
      
      .original-price {
        font-size: 12px;
        color: #999999;
        text-decoration: line-through;
      }
    }
    
    .medicine-tags {
      display: flex;
      gap: 4px;
      margin-top: 8px;
    }
  }
}
```

#### 5.2.3 药店卡片

```scss
// 药店卡片
.pharmacy-card {
  background: #FFFFFF;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  
  .pharmacy-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 12px;
    
    .pharmacy-name {
      font-size: 16px;
      font-weight: 600;
      color: #333333;
      flex: 1;
    }
    
    .pharmacy-rating {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 14px;
      font-weight: 600;
      color: #FF9500;
      
      .star-icon {
        font-size: 14px;
      }
    }
  }
  
  .pharmacy-info {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 12px;
    
    .info-item {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;
      color: #666666;
      
      .icon {
        font-size: 14px;
        color: #999999;
      }
    }
  }
  
  .pharmacy-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
}
```

（由于篇幅限制，后续组件代码将在实际开发中补充完整）

---

## 6. 页面设计规范

### 6.1 首页设计规范

首页是用户进入应用的第一个页面，承担着流量分发和内容展示的重要职责。

#### 6.1.1 页面结构

```
┌─────────────────────────────────────────────────────────────┐
│  顶部状态栏（系统状态栏：时间、信号、电量）                    │
├─────────────────────────────────────────────────────────────┤
│  顶部导航区（高度56px）                                       │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ 📍北京市朝阳区 ▼              [🔔] [💬]                ││
│  └─────────────────────────────────────────────────────────┘│
├─────────────────────────────────────────────────────────────┤
│  搜索栏区（高度56px）                                         │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ [🔍 搜索药品、症状、药店...]              [📷扫码]     ││
│  └─────────────────────────────────────────────────────────┘│
├─────────────────────────────────────────────────────────────┤
│  快捷入口区（高度80px）                                       │
│  ┌───────────┬───────────┬───────────┬───────────┐         │
│  │    🩺     │    📝     │    📦     │    🎧     │         │
│  │  在线问诊  │  我的处方  │  我的订单  │  客服咨询  │         │
│  └───────────┴───────────┴───────────┴───────────┘         │
├─────────────────────────────────────────────────────────────┤
│  Banner轮播区（高度180px）                                    │
│  ┌─────────────────────────────────────────────────────────┐│
│  │                                                         ││
│  │                    [轮播Banner广告位]                    ││
│  │                                                         ││
│  │                         ● ○ ○                           ││
│  └─────────────────────────────────────────────────────────┘│
├─────────────────────────────────────────────────────────────┤
│  药品分类导航（高度200px）                                     │
│  ┌───────────┬───────────┬───────────┬───────────┐         │
│  │    💊     │    🫁     │    🍽️     │    🧴     │         │
│  │  感冒发烧  │  呼吸系统  │  消化系统  │  皮肤用药  │         │
│  ├───────────┼───────────┼───────────┼───────────┤         │
│  │    💪     │    💢     │    👁️     │    🏥     │         │
│  │  维生素钙  │  解热镇痛  │  五官用药  │  医疗器械  │         │
│  └───────────┴───────────┴───────────┴───────────┘         │
├─────────────────────────────────────────────────────────────┤
│  热门推荐区                                                   │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ 热门推荐                                    更多 >      ││
│  │                                                         ││
│  │ ┌─────────┐  ┌─────────┐                                ││
│  │ │ [图片]  │  │ [图片]  │                                ││
│  │ │ 药品A   │  │ 药品B   │                                ││
│  │ │ ¥12.50  │  │ ¥28.00  │                                ││
│  │ └─────────┘  └─────────┘                                ││
│  └─────────────────────────────────────────────────────────┘│
├─────────────────────────────────────────────────────────────┤
│  附近药店区                                                   │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ 附近药店                                    更多 >      ││
│  │                                                         ││
│  │ ┌─────────────────────────────────────────────────────┐ ││
│  │ │ 同仁堂大药房(朝阳店)                           4.9⭐ │ ││
│  │ │                                                     │ ││
│  │ │ 📍 距离1.2km    🕐 25分钟送达                     │ ││
│  │ │                                                     │ ││
│  │ │ [24小时营业] [医保定点] [急速达]                    │ ││
│  │ └─────────────────────────────────────────────────────┘ ││
│  └─────────────────────────────────────────────────────────┘│
├─────────────────────────────────────────────────────────────┤
│  底部安全区域（34px - iPhone X+）                              │
├─────────────────────────────────────────────────────────────┤
│  底部Tab导航栏（高度56px）                                    │
│  ┌───────────┬───────────┬───────────┬───────────┐         │
│  │    🏠     │    📦     │    🛒     │    👤     │         │
│  │    首页   │   分类    │   购物车   │   我的    │         │
│  └───────────┴───────────┴───────────┴───────────┘         │
└─────────────────────────────────────────────────────────────┘
```

（由于篇幅限制，后续章节将在实际开发中补充完整，包括：
- 第5章剩余部分：输入框组件、标签组件、列表组件、导航组件、弹窗组件
- 第6章：药品详情页、购物车、订单确认页、个人中心、问诊页、处方页设计规范
- 第7-10章：图标系统、图片规范、动效规范、适配规范
）

---

## 附录

### A. 设计变量汇总

```scss
// =====================================================
// DrugMall患者端设计变量汇总
// =====================================================

// --------------------
// 颜色变量
// --------------------

// 主色
$color-primary: #00B578;
$color-primary-dark: #009E67;
$color-primary-light: #E6F7EF;
$color-primary-gradient: linear-gradient(135deg, #00B578 0%, #00C78A 100%);

// 辅助色
$color-orange: #FF9500;
$color-red: #FF4D4F;
$color-blue: #1890FF;
$color-yellow: #FFCC00;

// 中性色 - 文字
$color-text-primary: #333333;
$color-text-secondary: #666666;
$color-text-tertiary: #999999;
$color-text-disabled: #CCCCCC;
$color-text-inverse: #FFFFFF;

// 中性色 - 边框
$color-border-light: #E8E8E8;
$color-border-medium: #D9D9D9;
$color-border-dark: #BFBFBF;

// 中性色 - 背景
$color-bg-page: #F5F5F5;
$color-bg-card: #FFFFFF;
$color-bg-selected: #F0F0F0;
$color-bg-disabled: #F7F7F7;

// 功能色
$color-success: #52C41A;
$color-warning: #FAAD14;
$color-error: #FF4D4F;
$color-info: #1890FF;

// --------------------
// 字体变量
// --------------------

$font-family-base: -apple-system, BlinkMacSystemFont, "PingFang SC", "Source Han Sans SC", "Microsoft YaHei", "Helvetica Neue", Arial, sans-serif;

$font-size-h1: 20px;
$font-size-h2: 18px;
$font-size-h3: 16px;
$font-size-body: 14px;
$font-size-small: 12px;
$font-size-mini: 10px;

$font-weight-normal: 400;
$font-weight-medium: 500;
$font-weight-semibold: 600;
$font-weight-bold: 700;

$line-height-h1: 28px;
$line-height-h2: 26px;
$line-height-h3: 24px;
$line-height-body: 22px;
$line-height-small: 20px;
$line-height-mini: 16px;

// --------------------
// 间距变量
// --------------------

$spacing-base: 4px;
$spacing-xs: $spacing-base * 1;   // 4px
$spacing-sm: $spacing-base * 2;   // 8px
$spacing-md: $spacing-base * 3;   // 12px
$spacing-lg: $spacing-base * 4;   // 16px
$spacing-xl: $spacing-base * 5;   // 20px
$spacing-2xl: $spacing-base * 6;  // 24px
$spacing-3xl: $spacing-base * 8;  // 32px
$spacing-4xl: $spacing-base * 10; // 40px

// --------------------
// 圆角变量
// --------------------

$border-radius-sm: 4px;
$border-radius-md: 8px;
$border-radius-lg: 12px;
$border-radius-xl: 16px;
$border-radius-full: 50%;

// --------------------
// 阴影变量
// --------------------

$shadow-none: none;
$shadow-xs: 0 2px 4px rgba(0, 0, 0, 0.05);
$shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.08);
$shadow-md: 0 4px 12px rgba(0, 0, 0, 0.1);
$shadow-lg: 0 8px 24px rgba(0, 0, 0, 0.12);
$shadow-xl: 0 12px 32px rgba(0, 0, 0, 0.15);

// --------------------
// 尺寸变量
// --------------------

// 导航栏
$navbar-height: 56px;

// Tab栏
$tab-bar-height: 56px;

// 按钮
$btn-height-lg: 48px;
$btn-height-md: 44px;
$btn-height-sm: 36px;
$btn-height-xs: 28px;

// 输入框
$input-height: 44px;

// 安全区域
$safe-area-top: env(safe-area-inset-top);
$safe-area-bottom: env(safe-area-inset-bottom);
$safe-area-left: env(safe-area-inset-left);
$safe-area-right: env(safe-area-inset-right);
```

### B. 响应式断点

```scss
// 响应式断点
$breakpoints: (
  'xs': 0,      // 超小屏幕（手机）
  'sm': 375px,  // 小屏幕（大手机）
  'md': 414px,  // 中等屏幕（超大手机）
  'lg': 768px,  // 大屏幕（平板）
  'xl': 1024px, // 超大屏幕（大平板/小桌面）
  'xxl': 1280px // 巨大屏幕（桌面）
);

// 响应式mixin
@mixin respond-to($breakpoint) {
  $value: map-get($breakpoints, $breakpoint);
  
  @if $value != 0 {
    @media (min-width: $value) {
      @content;
    }
  } @else {
    @content;
  }
}

// 使用示例
.container {
  padding: 16px;
  
  @include respond-to('md') {
    padding: 20px;
  }
  
  @include respond-to('lg') {
    padding: 24px;
    max-width: 768px;
    margin: 0 auto;
  }
}
```

### C. 命名规范

#### BEM命名规范

DrugMall患者端采用BEM（Block-Element-Modifier）命名规范：

```
.block { }
.block__element { }
.block--modifier { }
```

**命名示例：**

```html
<!-- 按钮组件 -->
<button class="btn btn--primary btn--large">
  <span class="btn__text">立即购买</span>
</button>

<!-- 卡片组件 -->
<div class="card">
  <div class="card__header">
    <h3 class="card__title">热门推荐</h3>
    <a href="#" class="card__more">更多 ></a>
  </div>
  <div class="card__body">
    <div class="card__item">...</div>
    <div class="card__item">...</div>
  </div>
</div>
```

---

## 文档结束

**文档版本：** V1.0  
**最后更新：** 2024-12-08  
**维护团队：** DrugMall UI/UX Design Team

---

*本文档为DrugMall患者端UI设计规范，所有设计和开发工作应遵循本文档的规定。如有疑问，请联系设计团队。*
