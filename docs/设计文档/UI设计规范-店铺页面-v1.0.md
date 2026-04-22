# DrugMall 店铺页面 UI 设计规范 V1.0

## 概述

本文档为 DrugMall 店铺详情页面（药店/商家页面）的 UI 设计规范，采用医疗绿主题风格，定义了设计系统、页面布局、组件规范和交互规范。

**文档版本**: V1.0  
**更新日期**: 2026-04-21  
**适用平台**: 移动端 H5 / 微信小程序 / App  
**页面类型**: 店铺详情页 / 商家商品列表页

---

## 1. 设计系统

### 1.1 色彩系统

#### 1.1.1 主色调

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| 医疗绿 | `#07C160` | 主色、选中状态、导航栏 |
| 医疗绿-浅 | `#3CCB7A` | 渐变、hover状态 |
| 医疗绿-更浅 | `#6DD89A` | 背景、禁用状态 |
| 医疗绿渐变 | `linear-gradient(135deg, #3CCB7A 0%, #07C160 100%)` | 按钮、强调元素 |

#### 1.1.2 强调色

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| 温暖黄 | `#FFC300` | 加购按钮、价格标签、促销 |
| 温暖黄-浅 | `#FFD54F` | 悬停状态、高亮 |
| 温暖黄渐变 | `linear-gradient(135deg, #FFD54F 0%, #FFC300 100%)` | 结算按钮、优惠券 |

#### 1.1.3 功能色

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| 价格红 | `#FF4D4F` | 价格显示、删除 |
| 角标红 | `#FF4D4F` | 数字角标、红点提示 |
| 返现橙 | `#FF7A45` | 返现标签、促销 |
| 信息蓝 | `#1890FF` | 链接、医生咨询 |

#### 1.1.4 中性色

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| 文字-主要 | `#333333` | 标题、重要文字 |
| 文字-次要 | `#666666` | 正文、描述文字 |
| 文字-辅助 | `#999999` | 提示、说明文字 |
| 文字-禁用 | `#CCCCCC` | 禁用状态文字 |
| 文字-白色 | `#FFFFFF` | 深色背景上的文字 |
| 背景-页面 | `#F5F5F5` | 页面背景 |
| 背景-卡片 | `#FFFFFF` | 卡片背景 |
| 背景-分类栏 | `#F8F8F8` | 左侧分类栏背景 |
| 边框-浅色 | `#EEEEEE` | 分割线、边框 |
| 边框-中等 | `#E8E8E8` | 输入框边框 |

#### 1.1.5 主题色使用示例

```scss
// SCSS 变量定义
$primary-green: #07C160;
$primary-green-light: #3CCB7A;
$accent-yellow: #FFC300;
$accent-yellow-light: #FFD54F;
$price-red: #FF4D4F;
$badge-red: #FF4D4F;
$cashback-orange: #FF7A45;
$text-primary: #333333;
$text-secondary: #666666;
$text-tertiary: #999999;
$bg-page: #F5F5F5;
$bg-category: #F8F8F8;
$border-color: #EEEEEE;

// 渐变使用
.header-gradient {
  background: linear-gradient(180deg, $primary-green-light 0%, $primary-green 100%);
}

.btn-primary {
  background: linear-gradient(135deg, $primary-green 0%, $primary-green-light 100%);
}

.btn-checkout {
  background: linear-gradient(135deg, $accent-yellow 0%, $accent-yellow-light 100%);
}
```

### 1.2 字体系统

#### 1.2.1 字体栈

```css
font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Source Han Sans SC", "Microsoft YaHei", "Helvetica Neue", Arial, sans-serif;
```

#### 1.2.2 字体规范

| 层级 | 字号 | 字重 | 行高 | 用途 |
|-----|------|-----|------|------|
| H1 | 20px | 600 | 28px | 店铺名称 |
| H2 | 18px | 600 | 26px | 区块标题 |
| H3 | 16px | 500 | 24px | 商品名称 |
| 正文 | 14px | 400 | 22px | 常规文字 |
| 辅助 | 12px | 400 | 20px | 说明、标签 |
| 最小 | 10px | 400 | 16px | 角标、提示 |

#### 1.2.3 价格字体

| 类型 | 字号 | 颜色 | 用途 |
|-----|------|-----|------|
| 大价格 | 24px | #FF4D4F | 商品卡片价格 |
| 中价格 | 18px | #FF4D4F | 小卡片价格 |
| 小价格 | 14px | #FF4D4F | 列表价格 |
| 原价 | 12px | #999999 | 删除线 |
| 购物车金额 | 18px | #FF4D4F | 底部结算栏 |

### 1.3 间距系统

#### 1.3.1 基础间距

| 名称 | 数值 | 用途 |
|-----|------|------|
| xs | 4px | 紧凑间距、图标与文字 |
| sm | 8px | 小间距、标签内边距 |
| md | 12px | 标准间距、卡片内边距 |
| lg | 16px | 大间距、区块间距 |
| xl | 20px | 特大间距、页面边距 |
| xxl | 24px | 超大间距、分隔区域 |

#### 1.3.2 组件间距

| 组件 | 内边距 | 外边距 |
|-----|--------|--------|
| 页面容器 | - | 16px 水平 |
| 商品卡片 | 0 | 8px 底部 |
| 分类项 | 12px 8px | 0 |
| 按钮-大 | 12px 24px | - |
| 按钮-中 | 10px 20px | - |
| 底部栏 | 8px 16px | - |

### 1.4 圆角系统

| 名称 | 数值 | 用途 |
|-----|------|------|
| sm | 4px | 小标签、角标 |
| md | 8px | 按钮、输入框 |
| lg | 12px | 卡片、商品图 |
| xl | 16px | 大卡片、Banner |
| full | 50% | 圆形元素、头像、加购按钮 |

### 1.5 阴影系统

| 名称 | 阴影值 | 用途 |
|-----|--------|------|
| none | none | 扁平元素 |
| xs | 0 2px 4px rgba(0,0,0,0.05) | 轻微浮起 |
| sm | 0 2px 8px rgba(0,0,0,0.08) | 卡片、按钮 |
| md | 0 4px 12px rgba(0,0,0,0.1) | 悬浮卡片 |
| lg | 0 8px 24px rgba(0,0,0,0.12) | 弹窗、下拉 |

---

## 2. 页面布局规范

### 2.1 整体结构

```
┌─────────────────────────────────────┐
│ 状态栏 (系统)                        │
├─────────────────────────────────────┤
│ 顶部导航栏 (固定)                    │
│ [←] [🔍 搜索店内商品...    ] [♡] [⋮]│
├─────────────────────────────────────┤
│ Tab导航栏                           │
│ [首页] [全部商品] [商家]  [入会领5元券]│
├─────────────────────────────────────┤
│ 主体内容区                          │
│ ┌────────┬────────────────────────┐ │
│ │        │ 健康卡推广横幅          │ │
│ │  左侧  ├────────────────────────┤ │
│ │  分类  │ 排序栏 [销量] [价格]    │ │
│ │  栏    ├────────────────────────┤ │
│ │ 固定   │                        │ │
│ │        │    商品卡片网格         │ │
│ │ 80px   │    ┌────┐ ┌────┐       │ │
│ │        │    │商品│ │商品│       │ │
│ │        │    └────┘ └────┘       │ │
│ │        │                        │ │
│ │        │    医生咨询入口         │ │
│ └────────┴────────────────────────┘ │
├─────────────────────────────────────┤
│ 健康卡推广条 (固定底部上方)          │
│ [💳 享购药返现15%，本单最高返现3.96元] │
├─────────────────────────────────────┤
│ 底部购物车栏 (固定)                  │
│ [💬] [🏪] [🛒3]    ¥26.4   [去结算] │
├─────────────────────────────────────┤
│ 底部安全区域                        │
└─────────────────────────────────────┘
```

### 2.2 布局尺寸

| 区域 | 宽度 | 高度 | 定位 |
|-----|------|------|------|
| 顶部导航栏 | 100% | 48px | fixed top |
| Tab导航栏 | 100% | 44px | sticky |
| 左侧分类栏 | 80px | calc(100vh - 192px) | fixed left |
| 右侧商品区 | calc(100% - 80px) | auto | scrollable |
| 商品卡片 | 50% - 6px | auto | grid |
| 健康卡推广条 | 100% | 44px | fixed bottom + 56px |
| 底部购物车栏 | 100% | 56px | fixed bottom |

### 2.3 响应式适配

```scss
// 左侧分类栏固定宽度
.category-sidebar {
  position: fixed;
  left: 0;
  top: 92px; // 导航栏 + Tab栏
  width: 80px;
  height: calc(100vh - 148px); // 减去导航、Tab、底部栏
  background: #F8F8F8;
  overflow-y: auto;
}

// 右侧商品区自适应
.product-content {
  margin-left: 80px;
  padding: 12px;
  min-height: calc(100vh - 148px);
}

// 商品网格
.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}
```

---

## 3. 模块设计规范

### 3.1 顶部导航栏

#### 3.1.1 结构

```
┌─────────────────────────────────────┐
│ [←] [🔍 搜索店内商品，约500件] [♡] [⋮]│
└─────────────────────────────────────┘
```

#### 3.1.2 设计规范

| 元素 | 尺寸 | 样式 |
|-----|------|------|
| 导航栏高度 | 48px | 白色背景 |
| 返回按钮 | 24x24px | 黑色图标 |
| 搜索框 | flex: 1 | 灰色背景 #F5F5F5，圆角 20px |
| 搜索图标 | 18x18px | #999999 |
| 搜索文字 | 14px | #999999，placeholder |
| 收藏按钮 | 24x24px | 黑色边框心形 |
| 更多按钮 | 24x24px | 三点图标，带红点提示 |
| 红点提示 | 8x8px | #FF4D4F，圆角 50% |

#### 3.1.3 代码示例

```scss
.store-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 48px;
  background: #FFFFFF;
  display: flex;
  align-items: center;
  padding: 0 12px;
  z-index: 100;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);

  .back-btn {
    width: 24px;
    height: 24px;
    margin-right: 12px;
  }

  .search-box {
    flex: 1;
    height: 36px;
    background: #F5F5F5;
    border-radius: 20px;
    display: flex;
    align-items: center;
    padding: 0 12px;

    .search-icon {
      width: 18px;
      height: 18px;
      margin-right: 8px;
      color: #999999;
    }

    .search-text {
      font-size: 14px;
      color: #999999;
    }
  }

  .action-btns {
    display: flex;
    gap: 16px;
    margin-left: 12px;

    .btn-icon {
      width: 24px;
      height: 24px;
      position: relative;

      .badge {
        position: absolute;
        top: -2px;
        right: -2px;
        width: 8px;
        height: 8px;
        background: #FF4D4F;
        border-radius: 50%;
      }
    }
  }
}
```

### 3.2 Tab导航栏

#### 3.2.1 结构

```
┌─────────────────────────────────────────────────────┐
│ [首页] [全部商品] [商家]          [入会领5元券] [会员]│
└─────────────────────────────────────────────────────┘
```

#### 3.2.2 设计规范

| 元素 | 尺寸 | 样式 |
|-----|------|------|
| Tab栏高度 | 44px | 白色背景，底部边框 #EEEEEE |
| Tab项 | auto | 14px，#666666，间距 24px |
| Tab选中 | - | #07C160，底部 2px 下划线 |
| 右侧按钮区 | auto | 靠右对齐 |
| 入会按钮 | - | 黄色背景 #FFC300，圆角 12px，12px |
| 会员按钮 | - | 边框样式，圆角 12px，12px |

#### 3.2.3 代码示例

```scss
.tab-nav {
  position: sticky;
  top: 48px;
  height: 44px;
  background: #FFFFFF;
  border-bottom: 1px solid #EEEEEE;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  z-index: 99;

  .tab-list {
    display: flex;
    gap: 24px;

    .tab-item {
      font-size: 14px;
      color: #666666;
      padding: 10px 0;
      position: relative;

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
          height: 2px;
          background: #07C160;
          border-radius: 1px;
        }
      }
    }
  }

  .tab-actions {
    display: flex;
    gap: 8px;

    .btn-coupon {
      padding: 4px 8px;
      background: #FFC300;
      color: #333333;
      font-size: 12px;
      border-radius: 12px;
    }

    .btn-member {
      padding: 4px 8px;
      border: 1px solid #07C160;
      color: #07C160;
      font-size: 12px;
      border-radius: 12px;
    }
  }
}
```

### 3.3 左侧分类栏

#### 3.3.1 结构

```
┌────────┐
│ 推荐   │
├────────┤
│ 过敏季 │
├────────┤
│ 活动   │
├────────┤
│💳健康卡│
├────────┤
│ 常买   │
├────────┤
│ 其他 ①│
├────────┤
│感冒呼吸│
├────────┤
│咳喘用药│
├────────┤
│清热解毒│
├────────┤
│肠胃不适│
├────────┤
│性福生活│
├────────┤
│五官用药│
└────────┘
```

#### 3.3.2 设计规范

| 元素 | 尺寸 | 样式 |
|-----|------|------|
| 分类栏宽度 | 80px | 固定 |
| 分类项高度 | auto | 最小 48px，垂直居中 |
| 分类项背景 | - | 默认 #F8F8F8，选中 #FFFFFF |
| 分类文字 | 12px | 默认 #666666，选中 #07C160 |
| 选中标识 | 3px | 左侧 #07C160 竖线 |
| 健康卡图标 | 16x16px | 黄色/金色图标 |
| 角标 | 14px | #FF4D4F 背景，白色文字，圆角 8px |
| 分类间距 | 0 | 紧密排列 |

#### 3.3.3 代码示例

```scss
.category-sidebar {
  width: 80px;
  background: #F8F8F8;

  .category-item {
    min-height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 12px 8px;
    position: relative;
    font-size: 12px;
    color: #666666;
    text-align: center;
    line-height: 1.4;

    &.active {
      background: #FFFFFF;
      color: #07C160;
      font-weight: 500;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 3px;
        height: 16px;
        background: #07C160;
        border-radius: 0 2px 2px 0;
      }
    }

    .category-icon {
      width: 16px;
      height: 16px;
      margin-right: 4px;
    }

    .badge {
      position: absolute;
      top: 8px;
      right: 4px;
      min-width: 14px;
      height: 14px;
      padding: 0 4px;
      background: #FF4D4F;
      color: #FFFFFF;
      font-size: 10px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
}
```

### 3.4 健康卡推广横幅

#### 3.4.1 结构

```
┌─────────────────────────────────────┐
│ 💳 开通健康卡  预计可省34元/月   [>] │
└─────────────────────────────────────┘
```

#### 3.4.2 设计规范

| 元素 | 尺寸 | 样式 |
|-----|------|------|
| 横幅高度 | 40px | 渐变背景 |
| 背景 | - | 线性渐变 #FFF8E1 → #FFECB3 |
| 健康卡图标 | 20x20px | 黄色/金色 |
| 主文字 | 14px | #333333 |
| 金额文字 | 14px | #FF7A45，加粗 |
| 箭头 | 16x16px | #999999 |
| 内边距 | 0 12px | - |

#### 3.4.3 代码示例

```scss
.health-card-banner {
  height: 40px;
  background: linear-gradient(90deg, #FFF8E1 0%, #FFECB3 100%);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 12px;
  margin-bottom: 8px;
  border-radius: 8px;

  .banner-content {
    display: flex;
    align-items: center;
    gap: 8px;

    .card-icon {
      width: 20px;
      height: 20px;
    }

    .banner-text {
      font-size: 14px;
      color: #333333;

      .highlight {
        color: #FF7A45;
        font-weight: 600;
      }
    }
  }

  .arrow-icon {
    width: 16px;
    height: 16px;
    color: #999999;
  }
}
```

### 3.5 排序栏

#### 3.5.1 结构

```
┌─────────────────────────────────────┐
│ [销量▼] [价格]                      │
└─────────────────────────────────────┘
```

#### 3.5.2 设计规范

| 元素 | 尺寸 | 样式 |
|-----|------|------|
| 排序栏高度 | 36px | 白色背景 |
| 排序项 | auto | 14px，#666666，间距 16px |
| 选中项 | - | #333333，加粗 |
| 下拉箭头 | 12x12px | 选中项显示 |

### 3.6 商品卡片

#### 3.6.1 结构

```
┌─────────────────┐
│                 │
│   商品图片      │
│   1:1 比例      │
│                 │
├─────────────────┤
│ [腹泻] [肠胃炎] │
│ 药品名称文字... │
│                 │
│ ¥43.08  [+]     │
│ [最高返现标签]  │
└─────────────────┘
```

#### 3.6.2 设计规范

| 元素 | 尺寸 | 样式 |
|-----|------|------|
| 卡片宽度 | 50% - 6px | 网格布局 |
| 卡片背景 | - | #FFFFFF |
| 卡片圆角 | 8px | - |
| 商品图片 | 1:1 | 圆角 8px 8px 0 0 |
| 图片占位 | - | 渐变 #F5F5F5 |
| 信息区内边距 | 8px | - |
| 症状标签 | auto | #F0F9F4 背景，#07C160 文字，12px，圆角 4px |
| 商品名称 | 14px | #333333，2行截断 |
| 价格 | 18px | #FF4D4F，加粗 |
| 价格符号 | 12px | - |
| 加购按钮 | 28x28px | #FFC300 背景，白色 + 号，圆角 50% |
| 返现标签 | auto | #FFF7E6 背景，#FF7A45 文字，10px |

#### 3.6.3 代码示例

```scss
.product-card {
  background: #FFFFFF;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);

  .product-image {
    width: 100%;
    aspect-ratio: 1;
    background: linear-gradient(135deg, #F5F5F5 0%, #EEEEEE 100%);
    position: relative;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .product-info {
    padding: 8px;

    .symptom-tags {
      display: flex;
      gap: 4px;
      margin-bottom: 6px;

      .tag {
        padding: 2px 6px;
        background: #F0F9F4;
        color: #07C160;
        font-size: 10px;
        border-radius: 4px;
      }
    }

    .product-name {
      font-size: 14px;
      color: #333333;
      line-height: 1.4;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      margin-bottom: 8px;
    }

    .product-footer {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .price {
        font-size: 18px;
        font-weight: bold;
        color: #FF4D4F;

        &::before {
          content: '¥';
          font-size: 12px;
          font-weight: normal;
        }
      }

      .add-btn {
        width: 28px;
        height: 28px;
        background: #FFC300;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #FFFFFF;
        font-size: 18px;
        font-weight: bold;
      }
    }

    .cashback-tag {
      display: inline-block;
      padding: 2px 6px;
      background: #FFF7E6;
      color: #FF7A45;
      font-size: 10px;
      border-radius: 4px;
      margin-top: 6px;
    }
  }
}
```

### 3.7 医生咨询入口

#### 3.7.1 结构

```
┌─────────────────────────────────────┐
│ 👨‍⚕️ 问三甲医生  专业药师在线解答  [>] │
└─────────────────────────────────────┘
```

#### 3.7.2 设计规范

| 元素 | 尺寸 | 样式 |
|-----|------|------|
| 入口高度 | 48px | 白色背景 |
| 医生图标 | 32x32px | 蓝色/医疗绿 |
| 主文字 | 14px | #333333，加粗 |
| 副文字 | 12px | #999999 |
| 箭头 | 16x16px | #CCCCCC |
| 边框 | 1px | #EEEEEE，虚线或实线 |
| 圆角 | 8px | - |

### 3.8 健康卡推广条

#### 3.8.1 结构

```
┌─────────────────────────────────────────────────────┐
│ 💳 享购药返现15%，本单最高返现3.96元      [去开通]    │
└─────────────────────────────────────────────────────┘
```

#### 3.8.2 设计规范

| 元素 | 尺寸 | 样式 |
|-----|------|------|
| 推广条高度 | 44px | 白色背景 |
| 背景 | - | #FFFFFF，顶部边框 #EEEEEE |
| 健康卡图标 | 20x20px | 黄色/金色 |
| 返现比例 | 14px | #FF7A45，加粗 |
| 返现金额 | 14px | #FF7A45 |
| 开通按钮 | auto | #FFC300 背景，#333333 文字，12px，圆角 14px |
| 定位 | fixed | bottom: 56px |

#### 3.8.3 代码示例

```scss
.health-card-promo {
  position: fixed;
  bottom: 56px;
  left: 0;
  right: 0;
  height: 44px;
  background: #FFFFFF;
  border-top: 1px solid #EEEEEE;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  z-index: 98;

  .promo-content {
    display: flex;
    align-items: center;
    gap: 8px;

    .card-icon {
      width: 20px;
      height: 20px;
    }

    .promo-text {
      font-size: 14px;
      color: #333333;

      .highlight {
        color: #FF7A45;
        font-weight: 600;
      }
    }
  }

  .btn-open {
    padding: 6px 12px;
    background: #FFC300;
    color: #333333;
    font-size: 12px;
    font-weight: 500;
    border-radius: 14px;
  }
}
```

### 3.9 底部购物车栏

#### 3.9.1 结构

```
┌─────────────────────────────────────────────────────┐
│ [💬] [🏪] [🛒3]         ¥26.4          [去结算]      │
└─────────────────────────────────────────────────────┘
```

#### 3.9.2 设计规范

| 元素 | 尺寸 | 样式 |
|-----|------|------|
| 底部栏高度 | 56px | 白色背景 |
| 背景 | - | #FFFFFF，顶部阴影 |
| 图标区 | auto | 3个图标，等分 |
| 图标尺寸 | 24x24px | #666666 |
| 购物车角标 | 16px | #FF4D4F 背景，白色文字，圆角 50% |
| 金额文字 | 18px | #FF4D4F，加粗 |
| 金额前缀 | 12px | #FF4D4F |
| 结算按钮 | 100px x 40px | #FFC300 渐变背景，#333333 文字，圆角 20px |
| 定位 | fixed | bottom: 0 |

#### 3.9.3 代码示例

```scss
.cart-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 56px;
  background: #FFFFFF;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  z-index: 99;

  .bar-actions {
    display: flex;
    gap: 24px;

    .action-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 2px;
      position: relative;

      .icon {
        width: 24px;
        height: 24px;
        color: #666666;
      }

      .label {
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
        color: #FFFFFF;
        font-size: 10px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
  }

  .bar-info {
    display: flex;
    align-items: center;
    gap: 12px;

    .total-price {
      font-size: 18px;
      font-weight: bold;
      color: #FF4D4F;

      &::before {
        content: '¥';
        font-size: 12px;
        font-weight: normal;
      }
    }

    .btn-checkout {
      width: 100px;
      height: 40px;
      background: linear-gradient(135deg, #FFD54F 0%, #FFC300 100%);
      color: #333333;
      font-size: 14px;
      font-weight: 600;
      border-radius: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
}
```

---

## 4. 组件规范

### 4.1 按钮组件

#### 4.1.1 按钮类型

| 类型 | 样式 | 用途 |
|-----|------|------|
| 主要按钮 | #07C160 渐变背景，白色文字 | 主操作、提交 |
| 次要按钮 | 白色背景，#07C160 边框 | 次要操作 |
| 强调按钮 | #FFC300 渐变背景，#333 文字 | 结算、加购 |
| 文字按钮 | 透明背景，#07C160 文字 | 链接、辅助操作 |

#### 4.1.2 按钮尺寸

| 尺寸 | 高度 | 内边距 | 字体 | 圆角 |
|-----|------|--------|------|------|
| 大 | 48px | 0 24px | 16px | 24px |
| 中 | 44px | 0 20px | 16px | 22px |
| 小 | 36px | 0 16px | 14px | 18px |
| 超小 | 28px | 0 12px | 12px | 14px |

### 4.2 标签组件

#### 4.2.1 标签类型

| 类型 | 背景色 | 文字色 | 用途 |
|-----|--------|--------|------|
| 症状标签 | `#F0F9F4` | `#07C160` | 症状描述 |
| 返现标签 | `#FFF7E6` | `#FF7A45` | 返现提示 |
| 促销标签 | `#FFF1F0` | `#FF4D4F` | 促销信息 |
| 默认标签 | `#F5F5F5` | `#666666` | 普通标签 |

### 4.3 角标组件

#### 4.3.1 角标类型

| 类型 | 尺寸 | 样式 |
|-----|------|------|
| 数字角标 | min 16px | #FF4D4F 背景，白色文字，圆角 50% |
| 红点提示 | 8x8px | #FF4D4F，圆角 50% |
| 分类角标 | 14px | #FF4D4F 背景，白色文字，圆角 8px |

---

## 5. 交互规范

### 5.1 动画规范

#### 5.1.1 过渡时间

| 类型 | 时长 | 用途 |
|-----|------|------|
| 快速 | 150ms | 按钮点击、状态切换 |
| 标准 | 200ms | 悬停效果、展开收起 |
| 慢速 | 300ms | 页面切换、弹窗 |

#### 5.1.2 缓动函数

```scss
$ease-standard: cubic-bezier(0.4, 0, 0.2, 1);
$ease-decelerate: cubic-bezier(0, 0, 0.2, 1);
$ease-bounce: cubic-bezier(0.68, -0.55, 0.265, 1.55);
```

### 5.2 按钮交互

```scss
.btn {
  transition: all 0.2s $ease-standard;

  &:active {
    transform: scale(0.96);
    opacity: 0.9;
  }
}

.add-btn {
  transition: all 0.15s $ease-bounce;

  &:active {
    transform: scale(0.85);
  }
}
```

### 5.3 分类切换交互

```scss
.category-item {
  transition: all 0.2s $ease-standard;

  &.active {
    background: #FFFFFF;
    transition: background 0.15s ease;
  }
}
```

### 5.4 商品卡片交互

```scss
.product-card {
  transition: transform 0.2s $ease-standard, box-shadow 0.2s $ease-standard;

  &:active {
    transform: scale(0.98);
  }
}
```

---

## 6. 响应式与适配

### 6.1 设计稿基准

- **设计稿宽度**: 375px (iPhone 6/7/8)
- **基准字体**: 16px = 1rem
- **使用单位**: px (固定) / rem (相对)

### 6.2 安全区域

```scss
// iPhone X+ 安全区域适配
.safe-area-bottom {
  padding-bottom: env(safe-area-inset-bottom, 0);
}

// 底部固定栏
.fixed-bottom {
  padding-bottom: calc(56px + env(safe-area-inset-bottom, 0));
}
```

---

## 7. 命名规范

### 7.1 BEM 命名

```
.block { }
.block__element { }
.block--modifier { }
```

### 7.2 命名示例

```html
<!-- 店铺头部 -->
<div class="store-header">
  <button class="store-header__back"></button>
  <div class="store-header__search">
    <span class="store-header__search-text">搜索店内商品</span>
  </div>
  <div class="store-header__actions">
    <button class="store-header__btn store-header__btn--favorite"></button>
    <button class="store-header__btn store-header__btn--more">
      <span class="store-header__badge"></span>
    </button>
  </div>
</div>

<!-- 商品卡片 -->
<div class="product-card">
  <div class="product-card__image"></div>
  <div class="product-card__info">
    <div class="product-card__tags">
      <span class="tag tag--symptom">腹泻</span>
    </div>
    <h3 class="product-card__name">药品名称</h3>
    <div class="product-card__footer">
      <span class="product-card__price">43.08</span>
      <button class="product-card__add">+</button>
    </div>
  </div>
</div>
```

---

## 附录 A: 设计变量汇总

```scss
// =====================================================
// DrugMall 店铺页面设计变量汇总
// =====================================================

// --------------------
// 颜色变量 - 主色
// --------------------
$primary-green: #07C160;
$primary-green-light: #3CCB7A;
$primary-green-lighter: #6DD89A;
$primary-gradient: linear-gradient(135deg, #3CCB7A 0%, #07C160 100%);

// --------------------
// 颜色变量 - 强调色
// --------------------
$accent-yellow: #FFC300;
$accent-yellow-light: #FFD54F;
$accent-gradient: linear-gradient(135deg, #FFD54F 0%, #FFC300 100%);

// --------------------
// 颜色变量 - 功能色
// --------------------
$price-red: #FF4D4F;
$badge-red: #FF4D4F;
$cashback-orange: #FF7A45;
$info-blue: #1890FF;

// --------------------
// 颜色变量 - 中性色
// --------------------
$text-primary: #333333;
$text-secondary: #666666;
$text-tertiary: #999999;
$text-disabled: #CCCCCC;
$bg-page: #F5F5F5;
$bg-category: #F8F8F8;
$border-color: #EEEEEE;

// --------------------
// 字体变量
// --------------------
$font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Source Han Sans SC", "Microsoft YaHei", sans-serif;

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
$shadow-sm: 0 2px 4px rgba(0,0,0,0.05);
$shadow-md: 0 4px 12px rgba(0,0,0,0.1);
$shadow-lg: 0 8px 24px rgba(0,0,0,0.12);

// --------------------
// 尺寸变量
// --------------------
$header-height: 48px;
$tab-height: 44px;
$sidebar-width: 80px;
$cart-bar-height: 56px;
$promo-bar-height: 44px;
```

---

## 附录 B: 页面元素清单

| 元素 | 优先级 | 说明 |
|-----|--------|------|
| 顶部导航栏 | P0 | 搜索、收藏、更多 |
| Tab导航 | P0 | 首页、全部商品、商家 |
| 左侧分类栏 | P0 | 商品分类筛选 |
| 健康卡横幅 | P1 | 健康卡推广 |
| 商品列表 | P0 | 网格布局商品卡片 |
| 排序栏 | P1 | 销量、价格排序 |
| 医生咨询入口 | P1 | 专业咨询入口 |
| 健康卡推广条 | P1 | 底部固定推广 |
| 底部购物车栏 | P0 | 购物车、结算 |

---

**文档结束**

**维护团队**: DrugMall UI/UX Design Team  
**最后更新**: 2026-04-21  
**版本**: V1.0
