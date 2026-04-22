# DrugMall 门店商品详情页 UI 设计规范 V1.0

## 概述

本文档为 DrugMall 门店商品详情页的专项 UI 设计规范，采用医疗绿主色调（#07C160）和温暖黄强调色（#FFC300），定义了页面结构、色彩系统、字体规范、组件样式及交互规范。

**文档版本**: V1.0  
**更新日期**: 2026-04-21  
**适用平台**: 移动端 H5 / 微信小程序 / App  
**设计稿基准**: 375px 宽度

---

## 1. 页面结构

### 1.1 整体布局

```
┌─────────────────────────────────────┐
│ 状态栏 (系统)                        │
├─────────────────────────────────────┤
│ 顶部导航栏                           │
│ [←返回] [🔍搜索框] [♡收藏] [⋮更多]  │
├─────────────────────────────────────┤
│ Tab 导航栏 (吸顶)                    │
│ [商品●] [商家] [问医生] [详情]       │
├─────────────────────────────────────┤
│                                     │
│ 商品信息区                           │
│ ┌─────────────────────────────────┐ │
│ │ 商品轮播图 (1:1)                 │ │
│ │ ┌───────────────────────────┐   │ │
│ │ │                           │   │ │
│ │ │     药品图片轮播           │   │ │
│ │ │                           │   │ │
│ │ │ ┌──────┐              ●○○  │   │ │
│ │ │ │好评99%│                  │   │ │
│ │ │ └──────┘                  │   │ │
│ │ └───────────────────────────┘   │ │
│ ├─────────────────────────────────┤ │
│ │ 价格区                           │ │
│ │ ¥43.08  [健康卡返现¥3.08]        │ │
│ ├─────────────────────────────────┤ │
│ │ 商品名称 + 非处方药标识           │ │
│ │ 999感冒灵颗粒 10g*9袋            │ │
│ │ [OTC甲类]                        │ │
│ ├─────────────────────────────────┤ │
│ │ 药品说明卡片                     │ │
│ │ ┌─────────────────────────────┐ │ │
│ │ │ [功能主治] 解热镇痛...      │ │ │
│ │ │ [常见用法] 开水冲服...      │ │ │
│ │ └─────────────────────────────┘ │ │
│ ├─────────────────────────────────┤ │
│ │ 配送信息                         │ │
│ │ 🛵 美团快送  约20分钟  免配送费  │ │
│ ├─────────────────────────────────┤ │
│ │ 服务标签                         │ │
│ │ [不支持七天无理由退换] [隐私保护]│ │
│ │ [药监认证]                       │ │
│ ├─────────────────────────────────┤ │
│ │ 好药严选标签                     │ │
│ │ ┌─────────────────────────────┐ │ │
│ │ │ ⭐ 好药严选  正品保障        │ │ │
│ │ └─────────────────────────────┘ │ │
│ └─────────────────────────────────┘ │
│                                     │
│ 商家模块                             │
│ ┌─────────────────────────────────┐ │
│ │ 商家信息卡片                     │ │
│ │ ┌───┬───────────────────────┐   │ │
│ │ │LOGO│ 叮当快药店            │   │ │
│ │ │   │ ⭐4.9  月售1000+       │   │ │
│ │ │   │ [医保] [24小时] [极速] │   │ │
│ │ └───┴───────────────────────┘   │ │
│ ├─────────────────────────────────┤ │
│ │ 用药组合                         │ │
│ │ 组合名称：感冒常备组合           │ │
│ │ 说明：感冒灵+维C，效果更佳       │ │
│ │ [商品1] [商品2] [商品3]          │ │
│ │ 合计：¥89.00  [一起加购]         │ │
│ ├─────────────────────────────────┤ │
│ │ 秒问医生入口                     │ │
│ │ ┌───┬─────────────────────┬───┐ │ │
│ │ │👨‍⚕️│ 秒问医生  9秒接诊   │去 │ │ │
│ │ │头像│ 三甲医院医生在线    │咨询│ │ │
│ │ └───┴─────────────────────┴───┘ │ │
│ ├─────────────────────────────────┤ │
│ │ 医生问答                         │ │
│ │ Q: 这个药孕妇能吃吗？            │ │
│ │ A: 孕妇慎用，建议咨询医生...     │ │
│ │ [查看全部 128个问答 >]           │ │
│ └─────────────────────────────────┘ │
│                                     │
│ 底部占位区                           │
│ (高度: 56px + safe-area-bottom)     │
├─────────────────────────────────────┤
│ 底部购物车栏                         │
│ ┌─────────────────────────────────┐ │
│ │ [💬咨询] [🏪店铺] [🛒购物车]    │ │
│ │              ②                  │ │
│ │      [加入购物车] [立即购买]    │ │
│ │                    库存充足      │ │
│ └─────────────────────────────────┘ │
└─────────────────────────────────────┘
```

### 1.2 购物车弹窗

```
┌─────────────────────────────────────┐
│           ───── 拖拽条 ─────        │
│                                     │
│ ┌───┬───────────────────────┬───┐   │
│ │图 │ 999感冒灵颗粒         │ X │   │
│ │片 │ ¥43.08               │   │   │
│ └───┴───────────────────────┴───┘   │
│                                     │
│ 已选商品标签                         │
│ ┌─────────────────────────────────┐ │
│ │ 10g*9袋/盒  [已选]              │ │
│ └─────────────────────────────────┘ │
│                                     │
│ 搭着买推荐                          │
│ ┌─────────────────────────────────┐ │
│ │ [全部] [感冒] [维矿] [肠胃]     │ │
│ │                                 │ │
│ │ ┌─────┐ ┌─────┐ ┌─────┐        │ │
│ │ │商品1│ │商品2│ │商品3│        │ │
│ │ │¥12  │ │¥15  │ │¥18  │        │ │
│ │ └─────┘ └─────┘ └─────┘        │ │
│ └─────────────────────────────────┘ │
│                                     │
│ 数量选择                             │
│ 购买数量                   [-] 1 [+]│
│                                     │
│ ┌─────────────────────────────────┐ │
│ │        [加入购物车]             │ │
│ └─────────────────────────────────┘ │
└─────────────────────────────────────┘
```

---

## 2. 色彩系统

### 2.1 主色调

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| 主色-医疗绿 | `#07C160` | 主要按钮、强调元素、选中状态 |
| 主色-医疗绿浅 | `#3DD17E` | 渐变、hover状态、辅助元素 |
| 主色-医疗绿更浅 | `#6EE29B` | 背景、禁用状态 |
| 主色渐变 | `linear-gradient(135deg, #3DD17E 0%, #07C160 100%)` | 重要按钮、价格区背景 |

### 2.2 强调色

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| 强调色-温暖黄 | `#FFC300` | 促销标签、价格优惠、高亮元素 |
| 强调色-温暖黄浅 | `#FFD54F` | 渐变、hover状态 |
| 强调色-温暖黄深 | `#FFB300` | 按下状态 |
| 强调色渐变 | `linear-gradient(135deg, #FFD54F 0%, #FFC300 100%)` | 促销按钮、优惠标签 |

### 2.3 功能色

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| 成功/绿色 | `#07C160` | 库存充足、在线状态、配送中 |
| 警告/橙色 | `#FF9500` | 限时标签、促销标签 |
| 错误/红色 | `#FF4D4F` | 处方药警示、原价删除线、角标 |
| 价格红 | `#FF4D4F` | 价格数字 |
| 信息/蓝色 | `#1890FF` | 链接、咨询按钮、问医生 |

### 2.4 处方药专用色

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| Rx背景 | `#FF4D4F` | 处方药标签背景 |
| Rx文字 | `#FFFFFF` | 处方药标签文字 |
| Rx警示背景 | `#FFF2F0` | 处方药提示区域背景 |
| Rx警示边框 | `#FFCCC7` | 处方药提示区域边框 |
| Rx警示文字 | `#FF4D4F` | 处方药提示文字 |

### 2.5 中性色

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

### 2.6 主题色使用示例

```scss
// SCSS 变量定义
$primary-green: #07C160;
$primary-green-light: #3DD17E;
$primary-green-dark: #05A050;
$accent-yellow: #FFC300;
$accent-yellow-light: #FFD54F;
$accent-yellow-dark: #FFB300;
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
  background: linear-gradient(135deg, #3DD17E 0%, #07C160 100%);
}

.btn-primary-gradient {
  background: linear-gradient(135deg, #07C160 0%, #05A050 100%);
}

.promo-gradient {
  background: linear-gradient(135deg, #FFD54F 0%, #FFC300 100%);
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
| 大价格 | 32px | 700 | 40px | 详情页价格 |
| H1 | 18px | 600 | 26px | 药品名称 |
| H2 | 16px | 600 | 24px | 区块标题 |
| H3 | 15px | 500 | 22px | 卡片标题、Tab文字 |
| 正文 | 14px | 400 | 22px | 常规文字 |
| 辅助 | 12px | 400 | 20px | 说明、标签 |
| 最小 | 10px | 400 | 16px | 角标、提示 |

### 3.3 价格字体

| 类型 | 字号 | 颜色 | 字重 | 用途 |
|-----|------|-----|------|------|
| 大价格 | 32px | #FF4D4F | 700 | 详情页主价格 |
| 中价格 | 20px | #FF4D4F | 600 | 组合价格、弹窗价格 |
| 小价格 | 14px | #FF4D4F | 600 | 搭着买商品价格 |
| 原价 | 12px | #999999 | 400 | 删除线原价 |
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
| 页面容器 | - | 12px 水平 |
| 信息卡片 | 16px | 12px 底部 |
| 商家卡片 | 16px | 12px 底部 |
| 组合卡片 | 16px | 12px 底部 |
| 底部操作栏 | 8px 12px | - |
| 弹窗内容区 | 16px | - |

---

## 5. 组件样式

### 5.1 顶部导航栏

```scss
.top-nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 48px;
  background: #FFFFFF;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 12px;
  
  .nav-left {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .back-btn {
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 20px;
      color: #333333;
    }
    
    .search-box {
      width: 200px;
      height: 32px;
      background: #F5F5F5;
      border-radius: 16px;
      display: flex;
      align-items: center;
      padding: 0 12px;
      
      .search-icon {
        font-size: 16px;
        color: #999999;
        margin-right: 6px;
      }
      
      .search-text {
        font-size: 13px;
        color: #999999;
      }
    }
  }
  
  .nav-right {
    display: flex;
    align-items: center;
    gap: 16px;
    
    .nav-icon {
      font-size: 22px;
      color: #333333;
    }
    
    .collect-icon {
      &.active {
        color: #FF4D4F;
      }
    }
  }
}
```

### 5.2 Tab 导航栏

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
      color: #07C160;
      font-weight: 500;
      
      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 20px;
        height: 3px;
        background: #07C160;
        border-radius: 2px;
      }
    }
    
    .tab-dot {
      position: absolute;
      top: 8px;
      right: 20%;
      width: 6px;
      height: 6px;
      background: #FF4D4F;
      border-radius: 50%;
    }
  }
}
```

### 5.3 商品轮播图

```scss
.product-swiper {
  width: 100%;
  aspect-ratio: 1;
  position: relative;
  background: #F8F9FA;
  
  .swiper-image {
    width: 100%;
    height: 100%;
    object-fit: contain;
  }
  
  .review-tag {
    position: absolute;
    left: 12px;
    bottom: 40px;
    padding: 4px 10px;
    background: rgba(0, 0, 0, 0.6);
    border-radius: 12px;
    font-size: 12px;
    color: #FFFFFF;
    
    .highlight {
      color: #FFC300;
      font-weight: 600;
    }
  }
  
  .swiper-indicator {
    position: absolute;
    bottom: 16px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    gap: 6px;
    
    .dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.5);
      transition: all 0.3s ease;
      
      &.active {
        width: 16px;
        border-radius: 3px;
        background: #07C160;
      }
    }
  }
}
```

### 5.4 价格区域

```scss
.price-section {
  background: #FFFFFF;
  padding: 16px;
  
  .price-row {
    display: flex;
    align-items: baseline;
    gap: 8px;
    margin-bottom: 8px;
    
    .price-symbol {
      font-size: 16px;
      color: #FF4D4F;
      font-weight: 600;
    }
    
    .price-value {
      font-size: 32px;
      color: #FF4D4F;
      font-weight: 700;
    }
    
    .price-original {
      font-size: 14px;
      color: #999999;
      text-decoration: line-through;
    }
  }
  
  .promo-tags {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
    
    .promo-tag {
      padding: 2px 8px;
      background: linear-gradient(135deg, #FFF8E1 0%, #FFECB3 100%);
      border: 1px solid #FFC300;
      border-radius: 4px;
      font-size: 11px;
      color: #FF8F00;
      
      .highlight {
        color: #FF4D4F;
        font-weight: 600;
      }
    }
  }
}
```

### 5.5 商品信息区

```scss
.product-info {
  background: #FFFFFF;
  padding: 0 16px 16px;
  
  .product-name {
    font-size: 18px;
    font-weight: 600;
    color: #333333;
    line-height: 1.5;
    margin-bottom: 8px;
  }
  
  .product-tags {
    display: flex;
    gap: 6px;
    flex-wrap: wrap;
    
    .tag-otc {
      padding: 2px 6px;
      background: #07C160;
      border-radius: 4px;
      font-size: 10px;
      color: #FFFFFF;
    }
    
    .tag-rx {
      padding: 2px 6px;
      background: #FF4D4F;
      border-radius: 4px;
      font-size: 10px;
      color: #FFFFFF;
    }
  }
}
```

### 5.6 药品说明卡片

```scss
.drug-info-card {
  background: #F8F9FA;
  border-radius: 12px;
  padding: 16px;
  margin: 0 16px 16px;
  
  .info-item {
    display: flex;
    gap: 12px;
    margin-bottom: 12px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .info-label {
      width: 70px;
      font-size: 13px;
      color: #999999;
      flex-shrink: 0;
    }
    
    .info-content {
      flex: 1;
      font-size: 13px;
      color: #333333;
      line-height: 1.6;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }
}
```

### 5.7 配送信息

```scss
.delivery-info {
  background: #FFFFFF;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  
  .delivery-icon {
    width: 24px;
    height: 24px;
    background: #07C160;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    color: #FFFFFF;
  }
  
  .delivery-type {
    font-size: 14px;
    font-weight: 500;
    color: #333333;
  }
  
  .delivery-time {
    font-size: 13px;
    color: #07C160;
  }
  
  .delivery-fee {
    margin-left: auto;
    font-size: 13px;
    color: #07C160;
  }
}
```

### 5.8 服务标签

```scss
.service-tags {
  background: #FFFFFF;
  padding: 12px 16px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  
  .service-tag {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    color: #666666;
    
    .tag-icon {
      font-size: 14px;
      color: #999999;
    }
    
    &.warning {
      color: #FF9500;
      
      .tag-icon {
        color: #FF9500;
      }
    }
    
    &.secure {
      color: #07C160;
      
      .tag-icon {
        color: #07C160;
      }
    }
  }
}
```

### 5.9 好药严选标签

```scss
.quality-badge {
  background: #FFFFFF;
  padding: 12px 16px;
  
  .badge-content {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 12px;
    background: linear-gradient(135deg, #FFF8E1 0%, #FFF3E0 100%);
    border-radius: 8px;
    
    .badge-icon {
      font-size: 18px;
      color: #FFC300;
    }
    
    .badge-text {
      font-size: 13px;
      font-weight: 500;
      color: #FF8F00;
    }
    
    .badge-subtext {
      font-size: 12px;
      color: #FF9500;
      margin-left: auto;
    }
  }
}
```

### 5.10 商家信息卡片

```scss
.merchant-card {
  background: #FFFFFF;
  border-radius: 12px;
  padding: 16px;
  margin: 12px;
  
  .merchant-header {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .merchant-logo {
      width: 48px;
      height: 48px;
      border-radius: 8px;
      background: #F5F5F5;
      object-fit: cover;
    }
    
    .merchant-info {
      flex: 1;
      
      .merchant-name {
        font-size: 16px;
        font-weight: 600;
        color: #333333;
        margin-bottom: 4px;
      }
      
      .merchant-stats {
        display: flex;
        align-items: center;
        gap: 12px;
        font-size: 12px;
        color: #666666;
        
        .rating {
          color: #FF9500;
          font-weight: 600;
        }
      }
    }
  }
  
  .merchant-tags {
    display: flex;
    gap: 8px;
    margin-top: 12px;
    
    .merchant-tag {
      padding: 2px 8px;
      background: rgba(7, 193, 96, 0.1);
      border-radius: 4px;
      font-size: 11px;
      color: #07C160;
    }
  }
}
```

### 5.11 用药组合

```scss
.combo-section {
  background: #FFFFFF;
  border-radius: 12px;
  padding: 16px;
  margin: 12px;
  
  .combo-header {
    margin-bottom: 12px;
    
    .combo-title {
      font-size: 15px;
      font-weight: 600;
      color: #333333;
      margin-bottom: 4px;
    }
    
    .combo-desc {
      font-size: 12px;
      color: #999999;
    }
  }
  
  .combo-products {
    display: flex;
    gap: 8px;
    margin-bottom: 12px;
    
    .combo-product {
      width: 80px;
      text-align: center;
      
      .product-img {
        width: 80px;
        height: 80px;
        border-radius: 8px;
        background: #F5F5F5;
        object-fit: cover;
        margin-bottom: 6px;
      }
      
      .product-name {
        font-size: 11px;
        color: #666666;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
    
    .combo-plus {
      display: flex;
      align-items: center;
      font-size: 20px;
      color: #CCCCCC;
    }
  }
  
  .combo-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    
    .combo-price {
      font-size: 16px;
      color: #FF4D4F;
      font-weight: 600;
      
      &::before {
        content: '合计：¥';
        font-size: 13px;
        color: #666666;
        font-weight: 400;
      }
    }
    
    .btn-add-combo {
      padding: 8px 16px;
      background: linear-gradient(135deg, #07C160 0%, #05A050 100%);
      border: none;
      border-radius: 16px;
      font-size: 13px;
      color: #FFFFFF;
      font-weight: 500;
    }
  }
}
```

### 5.12 秒问医生入口

```scss
.doctor-entry {
  background: #FFFFFF;
  border-radius: 12px;
  padding: 16px;
  margin: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  
  .doctor-avatar {
    width: 56px;
    height: 56px;
    border-radius: 50%;
    background: #F5F5F5;
    object-fit: cover;
    position: relative;
    
    .online-badge {
      position: absolute;
      bottom: 2px;
      right: 2px;
      width: 12px;
      height: 12px;
      background: #07C160;
      border: 2px solid #FFFFFF;
      border-radius: 50%;
    }
  }
  
  .doctor-info {
    flex: 1;
    
    .entry-title {
      font-size: 15px;
      font-weight: 600;
      color: #333333;
      margin-bottom: 4px;
      
      .highlight {
        color: #07C160;
      }
    }
    
    .entry-subtitle {
      font-size: 12px;
      color: #999999;
    }
  }
  
  .btn-consult {
    padding: 8px 16px;
    background: #E3F2FD;
    border: none;
    border-radius: 16px;
    font-size: 13px;
    color: #1890FF;
    font-weight: 500;
  }
}
```

### 5.13 医生问答

```scss
.qa-section {
  background: #FFFFFF;
  border-radius: 12px;
  padding: 16px;
  margin: 12px;
  
  .qa-header {
    font-size: 15px;
    font-weight: 600;
    color: #333333;
    margin-bottom: 12px;
  }
  
  .qa-list {
    .qa-item {
      padding: 12px 0;
      border-bottom: 1px solid #F5F5F5;
      
      &:last-child {
        border-bottom: none;
      }
      
      .question {
        display: flex;
        gap: 8px;
        margin-bottom: 8px;
        
        .q-tag {
          width: 20px;
          height: 20px;
          background: #FF4D4F;
          border-radius: 4px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 12px;
          color: #FFFFFF;
          font-weight: 600;
          flex-shrink: 0;
        }
        
        .q-text {
          font-size: 14px;
          color: #333333;
          font-weight: 500;
        }
      }
      
      .answer {
        display: flex;
        gap: 8px;
        
        .a-tag {
          width: 20px;
          height: 20px;
          background: #07C160;
          border-radius: 4px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 12px;
          color: #FFFFFF;
          font-weight: 600;
          flex-shrink: 0;
        }
        
        .a-text {
          font-size: 13px;
          color: #666666;
          line-height: 1.6;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
      }
    }
  }
  
  .view-all {
    display: flex;
    align-items: center;
    justify-content: center;
    padding-top: 12px;
    font-size: 13px;
    color: #999999;
    
    .arrow {
      margin-left: 4px;
    }
  }
}
```

### 5.14 底部购物车栏

```scss
.bottom-cart-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 56px;
  background: #FFFFFF;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  padding: 0 12px;
  padding-bottom: env(safe-area-inset-bottom, 0);
  z-index: 1000;
  
  .action-icons {
    display: flex;
    gap: 20px;
    padding: 0 12px;
    
    .action-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 2px;
      position: relative;
      
      .action-icon {
        font-size: 22px;
        color: #666666;
      }
      
      .action-text {
        font-size: 10px;
        color: #666666;
      }
      
      .badge {
        position: absolute;
        top: -4px;
        right: -8px;
        min-width: 16px;
        height: 16px;
        padding: 0 4px;
        background: #FF4D4F;
        border-radius: 8px;
        font-size: 10px;
        color: #FFFFFF;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
  }
  
  .action-buttons {
    display: flex;
    flex: 1;
    gap: 0;
    margin-left: 12px;
    
    .btn-add-cart {
      flex: 1;
      height: 40px;
      background: linear-gradient(135deg, #3DD17E 0%, #07C160 100%);
      border: none;
      border-radius: 20px 0 0 20px;
      font-size: 14px;
      color: #FFFFFF;
      font-weight: 500;
    }
    
    .btn-buy-now {
      flex: 1;
      height: 40px;
      background: linear-gradient(135deg, #FF4D4F 0%, #FF7875 100%);
      border: none;
      border-radius: 0 20px 20px 0;
      font-size: 14px;
      color: #FFFFFF;
      font-weight: 500;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      line-height: 1.2;
      
      .stock-hint {
        font-size: 10px;
        font-weight: 400;
        opacity: 0.9;
      }
    }
  }
}
```

### 5.15 购物车弹窗

```scss
.cart-popup {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #FFFFFF;
  border-radius: 16px 16px 0 0;
  max-height: 70vh;
  z-index: 2000;
  
  .popup-header {
    display: flex;
    align-items: center;
    padding: 16px;
    border-bottom: 1px solid #F5F5F5;
    
    .product-thumb {
      width: 90px;
      height: 90px;
      border-radius: 8px;
      background: #F5F5F5;
      margin-right: 12px;
      border: 2px solid #FFFFFF;
      margin-top: -30px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
    
    .product-info {
      flex: 1;
      
      .popup-price {
        font-size: 24px;
        color: #FF4D4F;
        font-weight: 700;
        margin-bottom: 4px;
      }
      
      .popup-stock {
        font-size: 12px;
        color: #999999;
      }
    }
    
    .popup-close {
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 20px;
      color: #999999;
    }
  }
  
  .popup-body {
    padding: 16px;
    max-height: calc(70vh - 200px);
    overflow-y: auto;
    
    .selected-section {
      margin-bottom: 20px;
      
      .section-title {
        font-size: 13px;
        color: #999999;
        margin-bottom: 10px;
      }
      
      .selected-tag {
        display: inline-block;
        padding: 8px 16px;
        background: rgba(7, 193, 96, 0.1);
        border: 1px solid #07C160;
        border-radius: 16px;
        font-size: 13px;
        color: #07C160;
      }
    }
    
    .recommend-section {
      margin-bottom: 20px;
      
      .section-title {
        font-size: 13px;
        color: #999999;
        margin-bottom: 10px;
      }
      
      .recommend-tabs {
        display: flex;
        gap: 8px;
        margin-bottom: 12px;
        overflow-x: auto;
        
        .tab-item {
          padding: 6px 12px;
          background: #F5F5F5;
          border-radius: 12px;
          font-size: 12px;
          color: #666666;
          white-space: nowrap;
          
          &.active {
            background: rgba(7, 193, 96, 0.1);
            color: #07C160;
          }
        }
      }
      
      .recommend-list {
        display: flex;
        gap: 12px;
        overflow-x: auto;
        
        .recommend-item {
          width: 100px;
          text-align: center;
          
          .item-img {
            width: 100px;
            height: 100px;
            border-radius: 8px;
            background: #F5F5F5;
            object-fit: cover;
            margin-bottom: 6px;
          }
          
          .item-price {
            font-size: 14px;
            color: #FF4D4F;
            font-weight: 600;
          }
        }
      }
    }
    
    .quantity-section {
      display: flex;
      align-items: center;
      justify-content: space-between;
      
      .section-title {
        font-size: 14px;
        color: #333333;
      }
      
      .quantity-selector {
        display: flex;
        align-items: center;
        
        .btn-minus,
        .btn-plus {
          width: 32px;
          height: 32px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: #F5F5F5;
          border: none;
          font-size: 18px;
          color: #666666;
          
          &:disabled {
            color: #CCCCCC;
          }
        }
        
        .btn-minus {
          border-radius: 4px 0 0 4px;
        }
        
        .btn-plus {
          border-radius: 0 4px 4px 0;
        }
        
        .quantity-value {
          width: 50px;
          height: 32px;
          text-align: center;
          font-size: 15px;
          color: #333333;
          background: #F5F5F5;
          border: none;
          border-left: 1px solid #FFFFFF;
          border-right: 1px solid #FFFFFF;
        }
      }
    }
  }
  
  .popup-footer {
    padding: 12px 16px;
    padding-bottom: calc(12px + env(safe-area-inset-bottom, 0));
    border-top: 1px solid #F5F5F5;
    
    .btn-confirm {
      width: 100%;
      height: 44px;
      background: linear-gradient(135deg, #07C160 0%, #05A050 100%);
      border: none;
      border-radius: 22px;
      font-size: 15px;
      color: #FFFFFF;
      font-weight: 500;
    }
  }
}

// 弹窗遮罩
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1999;
}
```

---

## 6. 交互规范

### 6.1 导航栏交互

```scss
.nav-bar {
  transition: all 0.3s ease;
  
  &.transparent {
    background: transparent;
    
    .nav-icon {
      color: #FFFFFF;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
    }
  }
  
  &.solid {
    background: #FFFFFF;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
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

.btn-primary {
  &:hover {
    box-shadow: 0 4px 12px rgba(7, 193, 96, 0.4);
  }
}
```

### 6.3 卡片交互

```scss
.card {
  transition: all 0.2s ease;
  
  &:active {
    background: #FAFAFA;
  }
}

.merchant-card,
.combo-section,
.qa-section {
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }
}
```

### 6.4 弹窗交互

```scss
.cart-popup {
  transform: translateY(100%);
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &.show {
    transform: translateY(0);
  }
}

.popup-overlay {
  opacity: 0;
  transition: opacity 0.3s ease;
  
  &.show {
    opacity: 1;
  }
}
```

### 6.5 轮播交互

```scss
.product-swiper {
  .swiper-indicator {
    .dot {
      transition: all 0.3s ease;
    }
  }
}
```

---

## 7. 响应式与适配

### 7.1 安全区域适配

```scss
// 底部固定栏安全区域
.bottom-cart-bar {
  padding-bottom: calc(0 + env(safe-area-inset-bottom, 0));
}

// 页面内容底部占位
.page-content {
  padding-bottom: calc(56px + env(safe-area-inset-bottom, 0) + 16px);
}

// 弹窗底部安全区域
.popup-footer {
  padding-bottom: calc(12px + env(safe-area-inset-bottom, 0));
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
// DrugMall 门店商品详情页设计变量汇总
// =====================================================

// --------------------
// 颜色变量 - 主色（医疗绿）
// --------------------
$primary-green: #07C160;
$primary-green-light: #3DD17E;
$primary-green-dark: #05A050;
$primary-gradient: linear-gradient(135deg, #3DD17E 0%, #07C160 100%);
$btn-gradient: linear-gradient(135deg, #07C160 0%, #05A050 100%);

// --------------------
// 颜色变量 - 强调色（温暖黄）
// --------------------
$accent-yellow: #FFC300;
$accent-yellow-light: #FFD54F;
$accent-yellow-dark: #FFB300;
$accent-gradient: linear-gradient(135deg, #FFD54F 0%, #FFC300 100%);

// --------------------
// 颜色变量 - 功能色
// --------------------
$success: #07C160;
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

$font-price-large: 32px;
$font-h1: 18px;
$font-h2: 16px;
$font-h3: 15px;
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
$tabbar-height: 44px;
$bottom-action-height: 56px;
$safe-area-top: env(safe-area-inset-top, 0);
$safe-area-bottom: env(safe-area-inset-bottom, 0);
```

---

## 附录：页面元素清单

| 元素 | 优先级 | 说明 |
|-----|--------|------|
| 顶部导航 | P0 | 返回、搜索、收藏、更多 |
| Tab导航 | P0 | 商品、商家、问医生、详情 |
| 商品轮播 | P0 | 药品图片展示、评价标签 |
| 价格显示 | P0 | 现价、健康卡返现 |
| 药品名称 | P0 | 商品标题、OTC标识 |
| 药品说明 | P0 | 功能主治、常见用法 |
| 配送信息 | P0 | 配送方式、时间、费用 |
| 服务标签 | P0 | 退换政策、隐私保护、认证 |
| 商家信息 | P0 | Logo、评分、月售、标签 |
| 用药组合 | P1 | 组合推荐、加购 |
| 秒问医生 | P1 | 医生入口、去咨询 |
| 医生问答 | P1 | 问答列表、查看全部 |
| 底部购物车栏 | P0 | 咨询、店铺、购物车、购买 |
| 购物车弹窗 | P0 | 规格选择、搭着买、数量 |

---

**文档维护**: DrugMall UI/UX Design Team  
**最后更新**: 2026-04-21  
**版本**: V1.0
