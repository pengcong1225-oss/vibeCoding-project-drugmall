# DrugMall 药品详情页 UI 设计规范 V1.0

## 概述

本文档为 DrugMall 药品详情页的专项 UI 设计规范，基于黄色主色调（#FFD700 系列），定义了页面结构、色彩系统、字体规范、组件样式及交互规范。

**文档版本**: V1.0  
**更新日期**: 2026-04-18  
**适用平台**: 移动端 H5 / 微信小程序 / App  
**设计稿基准**: 375px 宽度

---

## 1. 页面结构

### 1.1 整体布局

```
┌─────────────────────────────────────┐
│ 状态栏 (系统)                        │
├─────────────────────────────────────┤
│ 顶部导航栏 (透明/白色)               │
│ [←返回]    药品详情    [分享]       │
├─────────────────────────────────────┤
│ 药品图片轮播区                       │
│ ┌─────────────────────────────────┐ │
│ │                                 │ │
│ │      药品图片 1:1 比例           │ │
│ │                                 │ │
│ │  [处方药]  指示器 ●○○            │ │
│ └─────────────────────────────────┘ │
├─────────────────────────────────────┤
│ 药品基础信息区                       │
│ ┌─────────────────────────────────┐ │
│ │ ¥32.00  ~~¥45.00~~              │ │
│ │ 已售 1.2万+  库存充足            │ │
│ ├─────────────────────────────────┤ │
│ │ 药品名称 (H1)                   │ │
│ │ 规格：0.35g*24粒                 │ │
│ │ [Rx处方药] [OTC] [医保] [热销]   │ │
│ ├─────────────────────────────────┤ │
│ │ ⚠️ 处方药提示：请凭处方购买      │ │
│ └─────────────────────────────────┘ │
├─────────────────────────────────────┤
│ 规格选择区                           │
│ ┌─────────────────────────────────┐ │
│ │ 规格选择                        │ │
│ │ [0.35g*24粒 ¥32.00] [0.35g*48粒 │ │
│ │  ¥58.00]                        │ │
│ └─────────────────────────────────┘ │
├─────────────────────────────────────┤
│ Tab 导航栏 (吸顶)                    │
│ [在售商家] [医生问答] [同款评价] [详情]│
├─────────────────────────────────────┤
│ Tab 内容区                           │
│ ┌─────────────────────────────────┐ │
│ │                                 │ │
│ │   根据选中 Tab 显示不同内容      │ │
│ │                                 │ │
│ └─────────────────────────────────┘ │
├─────────────────────────────────────┤
│ 底部安全区域                         │
├─────────────────────────────────────┤
│ 底部固定操作栏                       │
│ [咨询] [进店] [加入购物车] [立即购买]│
└─────────────────────────────────────┘
```

### 1.2 各区域详细说明

#### 1.2.1 顶部导航栏

| 属性 | 值 | 说明 |
|-----|-----|------|
| 高度 | 48px | 固定高度 |
| 初始状态 | 透明背景 | 页面顶部时 |
| 滚动后 | 白色背景 + 阴影 | 滚动超过 100px 后 |
| 返回按钮 | 24x24px | 左侧 16px 边距 |
| 分享按钮 | 24x24px | 右侧 16px 边距 |
| 标题 | 16px / #333333 | 居中显示 |

#### 1.2.2 药品图片轮播区

| 属性 | 值 | 说明 |
|-----|-----|------|
| 比例 | 1:1 | 正方形 |
| 背景 | 渐变色占位 | 图片加载前显示 |
| 处方药标识 | 左上角 | 红色背景标签 |
| 指示器 | 底部居中 | 圆点样式 |
| 轮播间隔 | 4000ms | 自动轮播 |

#### 1.2.3 Tab 内容区 - 在售商家

```
┌─────────────────────────────────────┐
│ 在售商家 (3)                         │
├─────────────────────────────────────┤
│ ┌─────────────────────────────────┐ │
│ │ 🏥 海王星辰健康药房              │ │
│ │ ⭐4.8分  月售1200+               │ │
│ │ ─────────────────────────────  │ │
│ │ 规格：0.35g*24粒                 │ │
│ │ ¥32.00    24H发货  快递配送     │ │
│ │                    [加入购物车] │ │
│ └─────────────────────────────────┘ │
│ ┌─────────────────────────────────┐ │
│ │ 🏥 同仁堂大药房                  │ │
│ │ ⭐4.9分  月售800+                │ │
│ │ ─────────────────────────────  │ │
│ │ 规格：0.35g*24粒                 │ │
│ │ ¥35.00    次日达  同城配送      │ │
│ │                    [加入购物车] │ │
│ └─────────────────────────────────┘ │
└─────────────────────────────────────┘
```

#### 1.2.4 Tab 内容区 - 医生问答

```
┌─────────────────────────────────────┐
│ 医生问答 (128)                       │
├─────────────────────────────────────┤
│ ┌─────────────────────────────────┐ │
│ │ 👤 张医生  副主任医师            │ │
│ │ 北京协和医院 · 心内科            │ │
│ │ ⭐4.9分  回答 328 次             │ │
│ ├─────────────────────────────────┤ │
│ │ Q: 这个药可以长期服用吗？        │ │
│ │ A: 不建议长期服用，建议...       │ │
│ │    查看完整回答 >                │ │
│ └─────────────────────────────────┘ │
└─────────────────────────────────────┘
```

#### 1.2.5 Tab 内容区 - 同款评价

```
┌─────────────────────────────────────┐
│ 同款评价 (2.3万)  好评率 98%         │
├─────────────────────────────────────┤
│ [全部] [好评] [中评] [差评] [有图]  │
├─────────────────────────────────────┤
│ ┌─────────────────────────────────┐ │
│ │ 👤 用户***8  ⭐⭐⭐⭐⭐         │ │
│ │ 规格：0.35g*24粒  2024-12-08    │ │
│ │ 药效很好，吃了两天就好了，       │ │
│ │ 物流也很快！                     │ │
│ │ [📷][📷][📷]                    │ │
│ └─────────────────────────────────┘ │
└─────────────────────────────────────┘
```

#### 1.2.6 Tab 内容区 - 详情

```
┌─────────────────────────────────────┐
│ 药品说明书                           │
│ ┌─────────────────────────────────┐ │
│ │ 【药品名称】                     │ │
│ │ 通用名称：XXX片                  │ │
│ │ 商品名称：XXX                    │ │
│ │ 【适应症】                       │ │
│ │ 用于...                          │ │
│ │ 【用法用量】                     │ │
│ │ 口服，一次...                    │ │
│ │ ...                              │ │
│ └─────────────────────────────────┘ │
├─────────────────────────────────────┤
│ 商品详情                             │
│ [详情图片1]                         │
│ [详情图片2]                         │
│ [详情图片3]                         │
└─────────────────────────────────────┘
```

---

## 2. 色彩系统

### 2.1 主色调（黄色系）

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| 主色-深 | `#E6B800` | 重要按钮按下态 |
| 主色 | `#FFD700` | 主要按钮、强调元素、价格标签 |
| 主色-浅 | `#FFE135` | 渐变、hover状态 |
| 主色-更浅 | `#FFEB80` | 背景、禁用状态 |
| 主色渐变 | `linear-gradient(135deg, #FFE135 0%, #FFD700 100%)` | 重要按钮、价格区背景 |

### 2.2 功能色

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| 成功/绿色 | `#00C9A7` | 库存充足、在线状态 |
| 警告/橙色 | `#FF9500` | 限时标签、促销标签 |
| 错误/红色 | `#FF4D4F` | 处方药警示、原价删除线 |
| 价格红 | `#FF4D4F` | 价格数字 |
| 信息/蓝色 | `#1890FF` | 链接、咨询按钮 |

### 2.3 处方药专用色

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| Rx背景 | `#FF4D4F` | 处方药标签背景 |
| Rx文字 | `#FFFFFF` | 处方药标签文字 |
| Rx警示背景 | `#FFF2F0` | 处方药提示区域背景 |
| Rx警示边框 | `#FFCCC7` | 处方药提示区域边框 |
| Rx警示文字 | `#FF4D4F` | 处方药提示文字 |

### 2.4 中性色

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| 文字-主要 | `#333333` | 标题、重要文字 |
| 文字-次要 | `#666666` | 正文、描述文字 |
| 文字-辅助 | `#999999` | 提示、说明文字 |
| 文字-禁用 | `#CCCCCC` | 禁用状态文字 |
| 文字-白色 | `#FFFFFF` | 深色背景上的文字 |
| 背景-页面 | `#F5F5F5` | 页面背景 |
| 背景-卡片 | `#FFFFFF` | 卡片背景 |
| 背景-灰色 | `#F8F9FA` | 输入框背景、分隔区域 |
| 边框-浅色 | `#EEEEEE` | 分割线、边框 |
| 边框-中等 | `#E8E8E8` | 输入框边框 |

### 2.5 主题色使用示例

```scss
// SCSS 变量定义
$primary-gold: #FFD700;
$primary-gold-light: #FFE135;
$primary-gold-dark: #E6B800;
$accent-green: #00C9A7;
$price-red: #FF4D4F;
$rx-warning-bg: #FFF2F0;
$rx-warning-border: #FFCCC7;
$text-primary: #333333;
$text-secondary: #666666;
$text-tertiary: #999999;
$bg-gray: #F5F5F5;
$border-color: #EEEEEE;

// 渐变使用
.price-gradient {
  background: linear-gradient(135deg, #FFE135 0%, #FFD700 100%);
}

.btn-primary-gradient {
  background: linear-gradient(135deg, #FFD700 0%, #E6B800 100%);
}
```

---

## 3. 字体系统

### 3.1 字体栈

```css
font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Source Han Sans SC", "Microsoft YaHei", "Helvetica Neue", Arial, sans-serif;
```

### 3.2 字体规范

| 层级 | 字号 | 字重 | 行高 | 用途 |
|-----|------|-----|------|------|
| 大价格 | 36px | 700 | 44px | 详情页价格 |
| H1 | 20px | 600 | 28px | 药品名称 |
| H2 | 18px | 600 | 26px | 区块标题 |
| H3 | 16px | 500 | 24px | 卡片标题、Tab文字 |
| 正文 | 14px | 400 | 22px | 常规文字 |
| 辅助 | 12px | 400 | 20px | 说明、标签 |
| 最小 | 10px | 400 | 16px | 角标、提示 |

### 3.3 价格字体

| 类型 | 字号 | 颜色 | 字重 | 用途 |
|-----|------|-----|------|------|
| 大价格 | 36px | #FF4D4F | 700 | 详情页主价格 |
| 中价格 | 20px | #FF4D4F | 600 | 商家卡片价格 |
| 小价格 | 16px | #FF4D4F | 600 | 规格选择价格 |
| 原价 | 14px | #999999 | 400 | 删除线原价 |
| 货币符号 | 14px | #FF4D4F | 400 | 价格前的 ¥ |

---

## 4. 间距系统

### 4.1 基础间距

| 名称 | 数值 | 用途 |
|-----|------|------|
| xs | 4px | 紧凑间距、图标与文字 |
| sm | 8px | 小间距、标签内边距 |
| md | 12px | 标准间距、卡片内边距 |
| lg | 16px | 大间距、区块间距 |
| xl | 20px | 特大间距、页面边距 |
| xxl | 24px | 超大间距、分隔区域 |

### 4.2 组件间距

| 组件 | 内边距 | 外边距 |
|-----|--------|--------|
| 页面容器 | - | 16px 水平 |
| 信息卡片 | 16px | 12px 底部 |
| 商家卡片 | 16px | 12px 底部 |
| 评价卡片 | 16px | 0 (底部边框分隔) |
| 底部操作栏 | 8px 16px | - |

---

## 5. 组件样式

### 5.1 按钮组件

#### 5.1.1 按钮类型

| 类型 | 样式 | 用途 |
|-----|------|------|
| 主要按钮 | 渐变背景 `#FFD700` → `#E6B800`，白色文字 | 立即购买、主操作 |
| 次要按钮 | 白色背景，主色边框 `#FFD700` | 加入购物车、次要操作 |
| 文字按钮 | 透明背景，主色文字 `#FFD700` | 链接、辅助操作 |
| 咨询按钮 | 蓝色背景 `#1890FF` | 咨询客服 |
| 进店按钮 | 灰色背景 `#F5F5F5` | 进入店铺 |
| 禁用按钮 | 灰色背景 `#CCCCCC` | 不可操作状态 |

#### 5.1.2 按钮尺寸

| 尺寸 | 高度 | 内边距 | 字体 | 圆角 |
|-----|------|--------|------|------|
| 大 | 48px | 0 24px | 16px | 24px |
| 中 | 44px | 0 20px | 16px | 22px |
| 小 | 36px | 0 16px | 14px | 18px |
| 超小 | 28px | 0 12px | 12px | 14px |

#### 5.1.3 底部操作栏按钮

```scss
// 底部操作栏
.bottom-action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 56px;
  background: #FFFFFF;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  padding: 0 16px;
  padding-bottom: env(safe-area-inset-bottom, 0);
  
  .action-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-width: 56px;
    height: 100%;
    
    .btn-icon {
      font-size: 20px;
      margin-bottom: 2px;
    }
    
    .btn-text {
      font-size: 10px;
      color: #666666;
    }
  }
  
  .btn-cart {
    flex: 1;
    height: 40px;
    background: #FFE135;
    color: #333333;
    font-size: 14px;
    font-weight: 500;
    border-radius: 20px 0 0 20px;
    border: none;
  }
  
  .btn-buy {
    flex: 1;
    height: 40px;
    background: linear-gradient(135deg, #FFD700 0%, #E6B800 100%);
    color: #FFFFFF;
    font-size: 14px;
    font-weight: 500;
    border-radius: 0 20px 20px 0;
    border: none;
  }
}
```

#### 5.1.4 主要按钮代码

```scss
// 主要按钮（立即购买）
.btn-primary {
  height: 44px;
  padding: 0 24px;
  font-size: 16px;
  font-weight: 600;
  color: #FFFFFF;
  background: linear-gradient(135deg, #FFD700 0%, #E6B800 100%);
  border: none;
  border-radius: 22px;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    opacity: 0.95;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(255, 215, 0, 0.4);
  }
  
  &:active {
    transform: translateY(0);
  }
  
  &:disabled {
    background: #CCCCCC;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
  }
}

// 次要按钮（加入购物车）
.btn-secondary {
  height: 44px;
  padding: 0 24px;
  font-size: 16px;
  font-weight: 500;
  color: #333333;
  background: #FFE135;
  border: none;
  border-radius: 22px;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    background: #FFD700;
  }
  
  &:active {
    background: #E6B800;
  }
}
```

### 5.2 卡片组件

#### 5.2.1 商家卡片

```scss
.merchant-card {
  background: #FFFFFF;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  
  .merchant-header {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    
    .merchant-icon {
      width: 40px;
      height: 40px;
      border-radius: 8px;
      margin-right: 12px;
      background: #F5F5F5;
    }
    
    .merchant-info {
      flex: 1;
      
      .merchant-name {
        font-size: 16px;
        font-weight: 500;
        color: #333333;
        margin-bottom: 4px;
      }
      
      .merchant-rating {
        font-size: 12px;
        color: #FF9500;
        
        .rating-score {
          font-weight: 600;
          margin-right: 8px;
        }
        
        .sale-count {
          color: #999999;
        }
      }
    }
  }
  
  .divider {
    height: 1px;
    background: #EEEEEE;
    margin: 12px 0;
  }
  
  .product-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .spec {
      font-size: 14px;
      color: #666666;
    }
    
    .price-action {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .price {
        font-size: 20px;
        font-weight: 600;
        color: #FF4D4F;
        
        &::before {
          content: '¥';
          font-size: 14px;
        }
      }
      
      .btn-add-cart {
        padding: 6px 16px;
        font-size: 12px;
        color: #FFFFFF;
        background: #FFD700;
        border: none;
        border-radius: 14px;
      }
    }
  }
}
```

#### 5.2.2 评价卡片

```scss
.review-card {
  background: #FFFFFF;
  padding: 16px;
  border-bottom: 1px solid #EEEEEE;
  
  &:last-child {
    border-bottom: none;
  }
  
  .review-header {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    
    .user-avatar {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      margin-right: 10px;
      background: #F5F5F5;
    }
    
    .user-name {
      flex: 1;
      font-size: 14px;
      color: #666666;
    }
    
    .rating {
      color: #FF9500;
      font-size: 12px;
    }
  }
  
  .review-meta {
    font-size: 12px;
    color: #999999;
    margin-bottom: 8px;
  }
  
  .review-content {
    font-size: 14px;
    color: #333333;
    line-height: 1.6;
    margin-bottom: 12px;
  }
  
  .review-images {
    display: flex;
    gap: 8px;
    
    .review-img {
      width: 80px;
      height: 80px;
      border-radius: 8px;
      object-fit: cover;
    }
  }
}
```

### 5.3 标签组件

#### 5.3.1 标签类型

| 类型 | 背景色 | 文字色 | 用途 |
|-----|--------|--------|------|
| 处方药标签 | `#FF4D4F` | `#FFFFFF` | Rx标识 |
| OTC标签 | `#00C9A7` | `#FFFFFF` | 非处方药 |
| 医保标签 | `#1890FF` | `#FFFFFF` | 医保可用 |
| 热销标签 | `#FFD700` | `#333333` | 热销商品 |
| 促销标签 | `#FF4D4F` | `#FFFFFF` | 限时促销 |
| 配送标签 | `#F5F5F5` | `#666666` | 配送方式 |

#### 5.3.2 标签代码

```scss
.tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  font-size: 10px;
  font-weight: 500;
  border-radius: 4px;
  
  &.tag-rx {
    background: #FF4D4F;
    color: #FFFFFF;
  }
  
  &.tag-otc {
    background: #00C9A7;
    color: #FFFFFF;
  }
  
  &.tag-insurance {
    background: #1890FF;
    color: #FFFFFF;
  }
  
  &.tag-hot {
    background: #FFD700;
    color: #333333;
  }
  
  &.tag-promo {
    background: #FF4D4F;
    color: #FFFFFF;
  }
  
  &.tag-delivery {
    background: #F5F5F5;
    color: #666666;
  }
}
```

### 5.4 处方药警示组件

```scss
.rx-warning {
  display: flex;
  align-items: flex-start;
  padding: 12px 16px;
  background: #FFF2F0;
  border: 1px solid #FFCCC7;
  border-radius: 8px;
  margin: 12px 0;
  
  .warning-icon {
    width: 20px;
    height: 20px;
    margin-right: 8px;
    color: #FF4D4F;
    flex-shrink: 0;
  }
  
  .warning-content {
    flex: 1;
    
    .warning-title {
      font-size: 14px;
      font-weight: 600;
      color: #FF4D4F;
      margin-bottom: 4px;
    }
    
    .warning-text {
      font-size: 12px;
      color: #FF4D4F;
      line-height: 1.5;
    }
  }
}
```

### 5.5 Tab 导航组件

```scss
.tab-nav {
  display: flex;
  background: #FFFFFF;
  border-bottom: 1px solid #EEEEEE;
  position: sticky;
  top: 48px;
  z-index: 100;
  
  .tab-item {
    flex: 1;
    height: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    color: #666666;
    position: relative;
    cursor: pointer;
    
    &.active {
      color: #FFD700;
      font-weight: 500;
      
      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 24px;
        height: 3px;
        background: #FFD700;
        border-radius: 2px;
      }
    }
    
    .tab-count {
      font-size: 12px;
      color: #999999;
      margin-left: 4px;
    }
  }
}
```

### 5.6 规格选择组件

```scss
.spec-selector {
  padding: 16px;
  background: #FFFFFF;
  
  .selector-title {
    font-size: 14px;
    font-weight: 500;
    color: #333333;
    margin-bottom: 12px;
  }
  
  .spec-options {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    
    .spec-option {
      padding: 10px 16px;
      font-size: 14px;
      color: #666666;
      background: #F5F5F5;
      border: 1px solid transparent;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.2s ease;
      
      .spec-price {
        font-size: 12px;
        color: #FF4D4F;
        margin-left: 8px;
      }
      
      &:hover {
        background: #FFFBE6;
      }
      
      &.active {
        color: #FFD700;
        background: #FFFBE6;
        border-color: #FFD700;
        
        .spec-price {
          color: #FF4D4F;
        }
      }
      
      &.disabled {
        color: #CCCCCC;
        background: #F5F5F5;
        cursor: not-allowed;
        
        .spec-price {
          color: #CCCCCC;
        }
      }
    }
  }
}
```

### 5.7 数量选择器

```scss
.quantity-selector {
  display: flex;
  align-items: center;
  
  .btn-minus,
  .btn-plus {
    width: 28px;
    height: 28px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #F5F5F5;
    border: none;
    font-size: 16px;
    color: #666666;
    cursor: pointer;
    
    &:disabled {
      color: #CCCCCC;
      cursor: not-allowed;
    }
  }
  
  .btn-minus {
    border-radius: 4px 0 0 4px;
  }
  
  .btn-plus {
    border-radius: 0 4px 4px 0;
  }
  
  .quantity-input {
    width: 44px;
    height: 28px;
    text-align: center;
    font-size: 14px;
    color: #333333;
    background: #F5F5F5;
    border: none;
    border-left: 1px solid #FFFFFF;
    border-right: 1px solid #FFFFFF;
  }
}
```

---

## 6. 交互规范

### 6.1 导航栏交互

```scss
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 48px;
  z-index: 1000;
  transition: all 0.3s ease;
  
  &.transparent {
    background: transparent;
    
    .nav-title,
    .nav-btn {
      color: #FFFFFF;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
    }
  }
  
  &.solid {
    background: #FFFFFF;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    
    .nav-title,
    .nav-btn {
      color: #333333;
      text-shadow: none;
    }
  }
}
```

### 6.2 按钮交互

```scss
.btn {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:hover {
    transform: translateY(-1px);
  }
  
  &:active {
    transform: translateY(0) scale(0.98);
  }
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    transform: none;
  }
}

// 主要按钮特殊效果
.btn-primary {
  &:hover {
    box-shadow: 0 4px 12px rgba(255, 215, 0, 0.4);
  }
}
```

### 6.3 卡片交互

```scss
.card {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:active {
    background: #FAFAFA;
  }
}

.merchant-card {
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}
```

### 6.4 规格选择交互

```scss
.spec-option {
  transition: all 0.2s ease;
  
  &:hover:not(.disabled) {
    transform: translateY(-1px);
  }
  
  &:active:not(.disabled) {
    transform: translateY(0);
  }
  
  &.active {
    animation: pulse 0.3s ease;
  }
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.02); }
  100% { transform: scale(1); }
}
```

### 6.5 图片轮播交互

```scss
.image-swiper {
  .swiper-indicator {
    .dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.5);
      transition: all 0.3s ease;
      
      &.active {
        width: 16px;
        border-radius: 3px;
        background: #FFD700;
      }
    }
  }
}
```

---

## 7. 响应式与适配

### 7.1 安全区域适配

```scss
// 底部固定栏安全区域
.bottom-action-bar {
  padding-bottom: calc(8px + env(safe-area-inset-bottom, 0));
}

// 页面内容底部占位
.page-content {
  padding-bottom: calc(56px + env(safe-area-inset-bottom, 0) + 16px);
}
```

### 7.2 1px 边框解决方案

```scss
.border-1px {
  position: relative;
  
  &::after {
    content: '';
    position: absolute;
    left: 0;
    bottom: 0;
    width: 100%;
    height: 1px;
    background: #EEEEEE;
    transform: scaleY(0.5);
    transform-origin: 0 100%;
  }
}
```

---

## 8. 设计变量汇总

```scss
// =====================================================
// DrugMall 药品详情页设计变量汇总
// =====================================================

// --------------------
// 颜色变量 - 主色（黄色系）
// --------------------
$primary-gold: #FFD700;
$primary-gold-light: #FFE135;
$primary-gold-dark: #E6B800;
$primary-gradient: linear-gradient(135deg, #FFE135 0%, #FFD700 100%);
$btn-gradient: linear-gradient(135deg, #FFD700 0%, #E6B800 100%);

// --------------------
// 颜色变量 - 功能色
// --------------------
$success: #00C9A7;
$warning: #FF9500;
$error: #FF4D4F;
$info: #1890FF;
$price-red: #FF4D4F;

// --------------------
// 颜色变量 - 处方药专用
// --------------------
$rx-bg: #FF4D4F;
$rx-text: #FFFFFF;
$rx-warning-bg: #FFF2F0;
$rx-warning-border: #FFCCC7;
$rx-warning-text: #FF4D4F;

// --------------------
// 颜色变量 - 中性色
// --------------------
$text-primary: #333333;
$text-secondary: #666666;
$text-tertiary: #999999;
$text-disabled: #CCCCCC;
$text-white: #FFFFFF;

$bg-page: #F5F5F5;
$bg-card: #FFFFFF;
$bg-gray: #F8F9FA;

$border-light: #EEEEEE;
$border-medium: #E8E8E8;

// --------------------
// 字体变量
// --------------------
$font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Source Han Sans SC", "Microsoft YaHei", sans-serif;

$font-price-large: 36px;
$font-h1: 20px;
$font-h2: 18px;
$font-h3: 16px;
$font-body: 14px;
$font-small: 12px;
$font-mini: 10px;

// --------------------
// 间距变量
// --------------------
$spacing-xs: 4px;
$spacing-sm: 8px;
$spacing-md: 12px;
$spacing-lg: 16px;
$spacing-xl: 20px;
$spacing-xxl: 24px;

// --------------------
// 圆角变量
// --------------------
$radius-sm: 4px;
$radius-md: 8px;
$radius-lg: 12px;
$radius-xl: 16px;
$radius-full: 50%;

// --------------------
// 阴影变量
// --------------------
$shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.06);
$shadow-md: 0 4px 12px rgba(0, 0, 0, 0.08);
$shadow-lg: 0 8px 24px rgba(0, 0, 0, 0.12);
$shadow-top: 0 -2px 10px rgba(0, 0, 0, 0.08);

// --------------------
// 尺寸变量
// --------------------
$navbar-height: 48px;
$tabbar-height: 56px;
$bottom-action-height: 56px;
$safe-area-top: env(safe-area-inset-top, 0);
$safe-area-bottom: env(safe-area-inset-bottom, 0);
```

---

## 附录：页面元素清单

| 元素 | 优先级 | 说明 |
|-----|--------|------|
| 图片轮播 | P0 | 药品图片展示 |
| 价格显示 | P0 | 现价/原价对比 |
| 药品名称 | P0 | 商品标题 |
| 处方药标识 | P0 | Rx标签及警示 |
| 规格选择 | P0 | 多规格切换 |
| Tab导航 | P0 | 商家/问答/评价/详情 |
| 在售商家列表 | P0 | 商家卡片展示 |
| 底部操作栏 | P0 | 咨询/进店/购买 |
| 医生问答 | P1 | 医生信息及问答 |
| 用户评价 | P1 | 评价列表 |
| 药品说明书 | P1 | 详细说明 |
| 分享功能 | P2 | 分享按钮 |

---

**文档维护**: DrugMall UI/UX Design Team  
**最后更新**: 2026-04-18  
**版本**: V1.0
