# DrugMall 个人中心 UI 设计规范 V1.0

## 概述

本文档为 DrugMall 个人中心页面的 UI 设计规范，采用暖色调橙黄渐变主题风格，定义了设计系统、页面结构、组件规范和交互规范。

**文档版本**: V1.0  
**更新日期**: 2026-04-15  
**适用平台**: 移动端 H5 / 微信小程序 / App  
**设计主题**: 暖色调橙黄渐变

---

## 1. 设计系统

### 1.1 色彩系统

#### 1.1.1 主色调（暖色系）

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| 主色-橙 | `#FF7A45` | 主要按钮、强调元素、选中状态 |
| 主色-橙浅 | `#FF9C6E` | 渐变、hover状态、辅助元素 |
| 主色-黄 | `#FFC53D` | 次要强调、图标、装饰元素 |
| 主色渐变 | `linear-gradient(135deg, #FFC53D 0%, #FF7A45 100%)` | 头部背景、重要按钮 |
| 背景渐变 | `linear-gradient(180deg, #FFF7E6 0%, #FFF0D9 100%)` | 页面背景 |

#### 1.1.2 功能色

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| 成功/绿色 | `#52C41A` | 成功提示、在线状态 |
| 警告/橙色 | `#FA8C16` | 警告提示、活动标签 |
| 错误/红色 | `#FF4D4F` | 错误提示、红包、未读红点 |
| 红包红 | `#FF4D4F` | 红包图标、红包金额 |
| 会员金 | `#FAAD14` | 会员权益、VIP标识 |

#### 1.1.3 中性色

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| 文字-主要 | `#333333` | 标题、重要文字 |
| 文字-次要 | `#666666` | 正文、描述文字 |
| 文字-辅助 | `#999999` | 提示、说明文字 |
| 文字-禁用 | `#CCCCCC` | 禁用状态文字 |
| 文字-白色 | `#FFFFFF` | 深色背景上的文字 |
| 背景-页面 | `#FFF7E6` | 页面背景（暖色调） |
| 背景-卡片 | `#FFFFFF` | 卡片背景 |
| 背景-灰色 | `#F5F5F5` | 输入框背景、分隔区域 |
| 边框-浅色 | `#EEEEEE` | 分割线、边框 |
| 边框-中等 | `#E8E8E8` | 输入框边框 |

#### 1.1.4 主题色使用示例

```scss
// SCSS 变量定义
$primary-orange: #FF7A45;
$primary-orange-light: #FF9C6E;
$primary-yellow: #FFC53D;
$accent-red: #FF4D4F;
$accent-gold: #FAAD14;
$text-primary: #333333;
$text-secondary: #666666;
$text-tertiary: #999999;
$bg-warm: #FFF7E6;
$border-color: #EEEEEE;

// 渐变使用
.header-gradient {
  background: linear-gradient(180deg, #FF9C6E 0%, #FF7A45 100%);
}

.btn-gradient {
  background: linear-gradient(135deg, #FFC53D 0%, #FF7A45 100%);
}

// 卡片渐变背景
.card-gradient {
  background: linear-gradient(135deg, #FFF0D9 0%, #FFE4CC 100%);
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
| H1 | 20px | 600 | 28px | 页面大标题、用户昵称 |
| H2 | 18px | 600 | 26px | 区块标题 |
| H3 | 16px | 500 | 24px | 卡片标题、资产数字 |
| 正文 | 14px | 400 | 22px | 常规文字、菜单名称 |
| 辅助 | 12px | 400 | 20px | 说明、标签、用户ID |
| 最小 | 10px | 400 | 16px | 角标、提示、权益说明 |

#### 1.2.3 特殊字体

| 类型 | 字号 | 颜色 | 用途 |
|-----|------|-----|------|
| 资产大数字 | 24px | `#FF7A45` | 红包余额、积分数量 |
| 资产中数字 | 18px | `#FF4D4F` | 优惠券数量 |
| 问候语 | 16px | `#333333` | 用户问候 |
| 话题标签 | 14px | `#FF7A45` | #话题标签 |

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
| 卡片 | 16px | 12px 底部 |
| 按钮-大 | 12px 24px | - |
| 按钮-中 | 10px 20px | - |
| 功能图标 | 12px | 8px |
| 列表项 | 12px 16px | - |

### 1.4 圆角系统

| 名称 | 数值 | 用途 |
|-----|------|------|
| sm | 4px | 小标签、角标 |
| md | 8px | 按钮、输入框 |
| lg | 12px | 卡片、弹窗 |
| xl | 16px | 大卡片、Banner |
| xxl | 20px | 推广卡片 |
| full | 50% | 圆形元素、头像 |

### 1.5 阴影系统

| 名称 | 阴影值 | 用途 |
|-----|--------|------|
| none | none | 扁平元素 |
| xs | 0 2px 4px rgba(0,0,0,0.05) | 轻微浮起 |
| sm | 0 2px 8px rgba(0,0,0,0.08) | 卡片、按钮 |
| md | 0 4px 12px rgba(0,0,0,0.1) | 悬浮卡片 |
| lg | 0 8px 24px rgba(0,0,0,0.12) | 弹窗、下拉 |
| orange | 0 4px 16px rgba(255,122,69,0.2) | 橙色主题卡片 |

---

## 2. 页面结构

### 2.1 整体布局

```
┌─────────────────────────────────────┐
│ 状态栏 (系统)                        │
├─────────────────────────────────────┤
│ 1. 顶部导航栏                        │
│ [←返回]              [💬5] [⚙️]     │
├─────────────────────────────────────┤
│ 2. 用户信息区 (渐变背景)              │
│ ┌─────────────────────────────────┐ │
│ │ [👤] 早上好~           [💳]    │ │
│ │     用户ID: 138****8000         │ │
│ └─────────────────────────────────┘ │
├─────────────────────────────────────┤
│ 3. 健康卡推广区                      │
│ ┌─────────────────────────────────┐ │
│ │ 开通健康卡享更多权益              │ │
│ │ [权益1] [权益2] [权益3] [权益4]  │ │
│ └─────────────────────────────────┘ │
├─────────────────────────────────────┤
│ 4. 活动横幅 (橙色主题)               │
│ ┌─────────────────────────────────┐ │
│ │ 🔥 #春季养生节 全场5折起         │ │
│ └─────────────────────────────────┘ │
├─────────────────────────────────────┤
│ 5. 红包/会员区                       │
│ ┌─────────┬─────────┬─────────┐    │
│ │ [🧧]    │ [👑]    │ [🎁]    │    │
│ │ 我的红包 │ 会员中心 │ 领券中心 │    │
│ │ ¥128   │ 立即开通 │ 3张可用 │    │
│ └─────────┴─────────┴─────────┘    │
├─────────────────────────────────────┤
│ 6. 资产统计                          │
│ ┌─────────────────┬────────────────┐│
│ │   128          │   3560          ││
│ │ 我的积分        │ 健康币          ││
│ └─────────────────┴────────────────┘│
├─────────────────────────────────────┤
│ 7. 快捷入口                          │
│ ┌────────┬────────┬────────┐        │
│ │ [📋]   │ [🛒]   │ [📦]   │        │
│ │ 我的订单 │ 购物车 │ 待收货 │        │
│ └────────┴────────┴────────┘        │
├─────────────────────────────────────┤
│ 8. 健康服务区                        │
│ ┌─────────────────────────────────┐ │
│ │ 健康服务                        │ │
│ │ ┌────┬────┬────┐               │ │
│ │ [🏥]│[💊]│[📋]│               │ │
│ │ 预约挂号│用药提醒│健康档案│               │ │
│ │ ┌────┬────┬────┐               │ │
│ │ [👨‍⚕️]│[📊]│[💬]│               │ │
│ │ 在线问诊│健康测评│咨询记录│               │ │
│ │ └────┴────┴────┘               │ │
│ └─────────────────────────────────┘ │
├─────────────────────────────────────┤
│ 底部安全区域                        │
├─────────────────────────────────────┤
│ 9. 底部导航                          │
│ [🏠首页]  [⭐百姓健康管家]  [👤我的] │
└─────────────────────────────────────┘
```

### 2.2 模块详细规范

#### 2.2.1 顶部导航栏

| 属性 | 值 | 说明 |
|-----|---|------|
| 高度 | 48px | 固定高度 |
| 背景 | 透明 | 与下方渐变背景融合 |
| 返回按钮 | 24x24px | 白色图标 |
| 消息图标 | 24x24px | 右侧，带未读红点 |
| 设置图标 | 24x24px | 最右侧 |
| 未读红点 | 16x16px | 红色背景 `#FF4D4F`，白色数字 |

**未读红点样式：**
```scss
.badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  background: #FF4D4F;
  color: #FFFFFF;
  font-size: 10px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}
```

#### 2.2.2 用户信息区

| 属性 | 值 | 说明 |
|-----|---|------|
| 背景 | 渐变 `#FF9C6E` → `#FF7A45` | 橙色系渐变 |
| 高度 | 120px | 包含内边距 |
| 头像 | 64x64px | 圆形，白色边框 2px |
| 问候语 | 16px 白色 | "早上好~" / "下午好~" / "晚上好~" |
| 用户ID | 12px 白色 60%透明度 | 手机号脱敏显示 |
| 健康卡图标 | 48x48px | 右上角装饰性图标 |

**用户信息区样式：**
```scss
.user-section {
  background: linear-gradient(180deg, #FF9C6E 0%, #FF7A45 100%);
  padding: 16px;
  border-radius: 0 0 20px 20px;
  
  .avatar {
    width: 64px;
    height: 64px;
    border-radius: 50%;
    border: 2px solid #FFFFFF;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  }
  
  .greeting {
    font-size: 16px;
    color: #FFFFFF;
    font-weight: 500;
  }
  
  .user-id {
    font-size: 12px;
    color: rgba(255,255,255,0.8);
  }
  
  .health-card-icon {
    position: absolute;
    right: 16px;
    top: 16px;
    width: 48px;
    height: 48px;
    opacity: 0.9;
  }
}
```

#### 2.2.3 健康卡推广区

| 属性 | 值 | 说明 |
|-----|---|------|
| 背景 | 渐变 `#FFF0D9` → `#FFE4CC` | 暖色渐变卡片 |
| 圆角 | 16px | 大圆角 |
| 内边距 | 16px | 四边 |
| 标题 | 16px `#333` | "开通健康卡享更多权益" |
| 权益卡片 | 4个横向排列 | 每个宽度相等 |
| 权益图标 | 32x32px | 圆形渐变背景 |
| 权益文字 | 10px `#666` | 居中对齐 |

**权益小卡片样式：**
```scss
.benefit-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  
  .benefit-icon {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: linear-gradient(135deg, #FFC53D 0%, #FF7A45 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #FFFFFF;
    font-size: 16px;
  }
  
  .benefit-text {
    font-size: 10px;
    color: #666666;
  }
}
```

#### 2.2.4 活动横幅

| 属性 | 值 | 说明 |
|-----|---|------|
| 背景 | `#FFF2E8` | 浅橙色背景 |
| 边框 | 1px solid `#FFBB96` | 橙色边框 |
| 圆角 | 12px | 中等圆角 |
| 内边距 | 12px 16px | 上下 左右 |
| 话题标签 | 14px `#FF7A45` | #话题格式 |
| 活动描述 | 14px `#333` | 活动简介 |

#### 2.2.5 红包/会员区

| 属性 | 值 | 说明 |
|-----|---|------|
| 布局 | 3列等分 | 横向排列 |
| 卡片背景 | `#FFFFFF` | 白色卡片 |
| 圆角 | 12px | 中等圆角 |
| 阴影 | `0 2px 8px rgba(0,0,0,0.08)` | 轻微阴影 |
| 图标尺寸 | 40x40px | 圆形图标 |
| 图标背景 | 渐变 | 红包红/会员金/优惠券橙 |
| 标题 | 14px `#333` | 功能名称 |
| 数值 | 16px `#FF4D4F` | 金额/数量，加粗 |

**红包卡片样式：**
```scss
.asset-card {
  background: #FFFFFF;
  border-radius: 12px;
  padding: 16px 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  
  &.redpacket {
    .icon {
      background: linear-gradient(135deg, #FF7875 0%, #FF4D4F 100%);
    }
    .value {
      color: #FF4D4F;
    }
  }
  
  &.vip {
    .icon {
      background: linear-gradient(135deg, #FFD666 0%, #FAAD14 100%);
    }
    .value {
      color: #FAAD14;
    }
  }
  
  &.coupon {
    .icon {
      background: linear-gradient(135deg, #FF9C6E 0%, #FF7A45 100%);
    }
    .value {
      color: #FF7A45;
    }
  }
}
```

#### 2.2.6 资产统计

| 属性 | 值 | 说明 |
|-----|---|------|
| 布局 | 2列等分 | 横向排列 |
| 背景 | `#FFFFFF` | 白色卡片 |
| 圆角 | 12px | 中等圆角 |
| 数字 | 24px `#FF7A45` | 大号橙色数字，加粗 |
| 标签 | 12px `#666` | 资产名称 |

#### 2.2.7 快捷入口

| 属性 | 值 | 说明 |
|-----|---|------|
| 布局 | 3列等分 | 横向排列 |
| 图标尺寸 | 48x48px | 圆形渐变背景 |
| 图标背景 | 渐变 `#FFC53D` → `#FF7A45` | 橙黄渐变 |
| 文字 | 14px `#333` | 功能名称，居中对齐 |
| 间距 | 8px | 图标与文字间距 |

#### 2.2.8 健康服务区

| 属性 | 值 | 说明 |
|-----|---|------|
| 背景 | `#FFFFFF` | 白色卡片 |
| 圆角 | 16px | 大圆角 |
| 标题 | 16px `#333` 加粗 | "健康服务" |
| 网格 | 2行3列 | 6个功能图标 |
| 图标尺寸 | 44x44px | 方形圆角图标 |
| 图标圆角 | 12px | 与卡片统一 |
| 图标背景 | 浅色渐变 | 每个功能不同渐变色 |
| 文字 | 12px `#666` | 功能名称 |

**功能图标背景色：**
| 功能 | 背景色 |
|-----|--------|
| 预约挂号 | `#E6F7FF` → `#BAE7FF` |
| 用药提醒 | `#FFF7E6` → `#FFE7BA` |
| 健康档案 | `#F6FFED` → `#D9F7BE` |
| 在线问诊 | `#FFF2E8` → `#FFD8BF` |
| 健康测评 | `#F9F0FF` → `#EFDBFF` |
| 咨询记录 | `#FFF0F6` → `#FFD6E7` |

#### 2.2.9 底部导航

| 属性 | 值 | 说明 |
|-----|---|------|
| 高度 | 56px | 固定高度 |
| 背景 | `#FFFFFF` | 白色背景 |
| 阴影 | `0 -2px 10px rgba(0,0,0,0.05)` | 顶部阴影 |
| 布局 | 3项等分 | 横向排列 |
| 中间项 | 突出显示 | 百姓健康管家 |
| 中间图标 | 56x56px | 圆形渐变背景 |
| 中间位置 | 向上偏移 | -20px |
| 文字 | 10px | 选中橙色，未选中灰色 |

**中间突出导航项样式：**
```scss
.tabbar {
  .health-butler {
    .tab-icon {
      width: 56px;
      height: 56px;
      background: linear-gradient(135deg, #FFC53D 0%, #FF7A45 100%);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #FFFFFF;
      margin-top: -20px;
      border: 3px solid #FFFFFF;
      box-shadow: 0 4px 12px rgba(255,122,69,0.4);
    }
    
    .tab-text {
      color: #FF7A45;
      font-weight: 500;
    }
  }
}
```

---

## 3. 组件规范

### 3.1 按钮组件

#### 3.1.1 按钮类型

| 类型 | 样式 | 用途 |
|-----|------|------|
| 主要按钮 | 渐变背景 `#FFC53D` → `#FF7A45`，白色文字 | 主操作、提交 |
| 次要按钮 | 白色背景，橙色边框 | 次要操作、取消 |
| 文字按钮 | 透明背景，橙色文字 | 链接、辅助操作 |
| 危险按钮 | 红色背景/边框 | 删除、取消订单 |
| 禁用按钮 | 灰色背景 `#CCCCCC` | 不可操作状态 |

#### 3.1.2 按钮尺寸

| 尺寸 | 高度 | 内边距 | 字体 | 圆角 |
|-----|------|--------|------|------|
| 大 | 48px | 0 24px | 16px | 24px |
| 中 | 44px | 0 20px | 16px | 22px |
| 小 | 36px | 0 16px | 14px | 18px |
| 超小 | 28px | 0 12px | 12px | 14px |

#### 3.1.3 按钮代码示例

```scss
// 主要按钮
.btn-primary {
  height: 44px;
  padding: 0 20px;
  font-size: 16px;
  font-weight: 500;
  color: #FFFFFF;
  background: linear-gradient(135deg, #FFC53D 0%, #FF7A45 100%);
  border: none;
  border-radius: 22px;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    opacity: 0.9;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(255,122,69,0.3);
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

// 次要按钮
.btn-secondary {
  height: 44px;
  padding: 0 20px;
  font-size: 16px;
  font-weight: 500;
  color: #FF7A45;
  background: #FFFFFF;
  border: 1px solid #FF7A45;
  border-radius: 22px;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    background: rgba(255,122,69,0.05);
  }
}
```

### 3.2 卡片组件

#### 3.2.1 基础卡片

```scss
.card {
  background: #FFFFFF;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    border-bottom: 1px solid #F0F0F0;
  }
  
  .card-body {
    padding: 16px;
  }
}
```

#### 3.2.2 渐变卡片

```scss
.card-gradient {
  background: linear-gradient(135deg, #FFF0D9 0%, #FFE4CC 100%);
  border-radius: 16px;
  padding: 16px;
  border: 1px solid rgba(255,122,69,0.1);
}
```

### 3.3 图标组件

#### 3.3.1 功能图标

```scss
.icon-box {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFC53D 0%, #FF7A45 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFFFFF;
  font-size: 24px;
}
```

#### 3.3.2 方形功能图标

```scss
.icon-square {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  
  &.blue {
    background: linear-gradient(135deg, #E6F7FF 0%, #BAE7FF 100%);
    color: #1890FF;
  }
  
  &.orange {
    background: linear-gradient(135deg, #FFF7E6 0%, #FFE7BA 100%);
    color: #FF7A45;
  }
  
  &.green {
    background: linear-gradient(135deg, #F6FFED 0%, #D9F7BE 100%);
    color: #52C41A;
  }
}
```

### 3.4 标签组件

#### 3.4.1 标签类型

| 类型 | 背景色 | 文字色 | 用途 |
|-----|--------|--------|------|
| 主色标签 | `#FF7A45` | `#FFFFFF` | 主要标签 |
| 浅色标签 | `rgba(255,122,69,0.1)` | `#FF7A45` | 辅助标签 |
| 成功标签 | `rgba(82,196,26,0.1)` | `#52C41A` | 成功状态 |
| 警告标签 | `rgba(250,140,22,0.1)` | `#FA8C16` | 警告状态 |
| 错误标签 | `rgba(255,77,79,0.1)` | `#FF4D4F` | 错误状态 |
| 默认标签 | `#F5F5F5` | `#666666` | 默认状态 |

#### 3.4.2 话题标签

```scss
.tag-topic {
  display: inline-flex;
  align-items: center;
  padding: 4px 8px;
  font-size: 14px;
  color: #FF7A45;
  background: rgba(255,122,69,0.1);
  border-radius: 4px;
  font-weight: 500;
  
  &::before {
    content: '#';
    margin-right: 2px;
  }
}
```

---

## 4. 交互规范

### 4.1 动画规范

#### 4.1.1 过渡时间

| 类型 | 时长 | 用途 |
|-----|------|------|
| 快速 | 150ms | 按钮点击、状态切换 |
| 标准 | 200ms | 悬停效果、展开收起 |
| 慢速 | 300ms | 页面切换、弹窗 |
| 特殊 | 400ms | 轮播、复杂动画 |

#### 4.1.2 缓动函数

```scss
// 标准缓动
$ease-standard: cubic-bezier(0.4, 0, 0.2, 1);

// 减速缓动（进入）
$ease-decelerate: cubic-bezier(0, 0, 0.2, 1);

// 加速缓动（离开）
$ease-accelerate: cubic-bezier(0.4, 0, 1, 1);

// 弹性缓动
$ease-bounce: cubic-bezier(0.68, -0.55, 0.265, 1.55);
```

### 4.2 按钮交互

```scss
.btn {
  transition: all 0.2s $ease-standard;
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(255,122,69,0.3);
  }
  
  &:active {
    transform: translateY(0) scale(0.98);
  }
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
  }
}
```

### 4.3 卡片交互

```scss
.card {
  transition: all 0.2s $ease-standard;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
  
  &:active {
    transform: translateY(0);
  }
}
```

### 4.4 功能图标交互

```scss
.icon-box {
  transition: all 0.2s $ease-standard;
  
  &:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 12px rgba(255,122,69,0.3);
  }
  
  &:active {
    transform: scale(0.95);
  }
}
```

### 4.5 数字动画

```scss
// 数字滚动动画
@keyframes countUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.number-animate {
  animation: countUp 0.5s $ease-decelerate;
}
```

### 4.6 红点提示动画

```scss
// 红点脉冲动画
@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
  }
}

.badge-pulse {
  animation: pulse 2s infinite;
}
```

---

## 5. 响应式与适配

### 5.1 设计稿基准

- **设计稿宽度**: 375px (iPhone 6/7/8)
- **基准字体**: 16px = 1rem
- **使用单位**: px (固定) / rem (相对)

### 5.2 断点定义

| 断点 | 宽度 | 设备类型 |
|-----|------|---------|
| xs | < 375px | 小屏手机 |
| sm | 375px - 414px | 标准手机 |
| md | 414px - 768px | 大屏手机 |
| lg | 768px - 1024px | 平板 |
| xl | > 1024px | 大平板/桌面 |

### 5.3 安全区域

```scss
// iPhone X+ 安全区域适配
.safe-area-top {
  padding-top: env(safe-area-inset-top, 0);
}

.safe-area-bottom {
  padding-bottom: env(safe-area-inset-bottom, 0);
}

// 底部固定栏
.fixed-bottom {
  padding-bottom: calc(16px + env(safe-area-inset-bottom, 0));
}
```

---

## 6. 图标规范

### 6.1 图标尺寸

| 用途 | 尺寸 | 说明 |
|-----|------|------|
| 导航图标 | 24x24px | Tab栏、工具栏 |
| 功能图标 | 20x20px | 列表项、按钮内 |
| 大图标 | 40x40px | 功能入口 |
| 头像 | 64x64px | 用户头像 |
| 装饰图标 | 48x48px | 健康卡等装饰 |

### 6.2 图标库

使用线性图标风格，保持统一：

- **线性图标**: 默认使用，2px 线宽
- **填充图标**: 选中状态使用
- **渐变色图标**: 功能入口使用

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
<!-- 用户信息区 -->
<div class="user-section">
  <div class="user-section__avatar">
    <img src="avatar.jpg" alt="头像">
  </div>
  <div class="user-section__info">
    <p class="user-section__greeting">早上好~</p>
    <p class="user-section__id">用户ID: 138****8000</p>
  </div>
  <div class="user-section__health-card">
    <i class="icon-health-card"></i>
  </div>
</div>

<!-- 资产卡片 -->
<div class="asset-card asset-card--redpacket">
  <div class="asset-card__icon">
    <i class="icon-redpacket"></i>
  </div>
  <p class="asset-card__label">我的红包</p>
  <p class="asset-card__value">¥128</p>
</div>
```

---

## 附录 A: 设计变量汇总

```scss
// =====================================================
// DrugMall 个人中心设计变量汇总
// =====================================================

// --------------------
// 颜色变量
// --------------------

// 主色 - 暖色调
$primary-orange: #FF7A45;
$primary-orange-light: #FF9C6E;
$primary-yellow: #FFC53D;
$primary-gradient: linear-gradient(135deg, #FFC53D 0%, #FF7A45 100%);
$header-gradient: linear-gradient(180deg, #FF9C6E 0%, #FF7A45 100%);
$bg-warm-gradient: linear-gradient(180deg, #FFF7E6 0%, #FFF0D9 100%);

// 功能色
$success: #52C41A;
$warning: #FA8C16;
$error: #FF4D4F;
$redpacket: #FF4D4F;
$vip-gold: #FAAD14;

// 中性色 - 文字
$text-primary: #333333;
$text-secondary: #666666;
$text-tertiary: #999999;
$text-disabled: #CCCCCC;
$text-white: #FFFFFF;

// 中性色 - 背景
$bg-page: #FFF7E6;
$bg-card: #FFFFFF;
$bg-warm: #FFF0D9;

// 中性色 - 边框
$border-light: #EEEEEE;
$border-medium: #E8E8E8;

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
$spacing-xxl: 24px;

// --------------------
// 圆角变量
// --------------------
$radius-sm: 4px;
$radius-md: 8px;
$radius-lg: 12px;
$radius-xl: 16px;
$radius-xxl: 20px;
$radius-full: 50%;

// --------------------
// 阴影变量
// --------------------
$shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.08);
$shadow-md: 0 4px 12px rgba(0, 0, 0, 0.1);
$shadow-lg: 0 8px 24px rgba(0, 0, 0, 0.12);
$shadow-orange: 0 4px 16px rgba(255,122,69,0.2);

// --------------------
// 尺寸变量
// --------------------
$navbar-height: 48px;
$tabbar-height: 56px;
$avatar-size: 64px;
$icon-size-lg: 48px;
$icon-size-md: 40px;
$icon-size-sm: 32px;
$safe-area-top: env(safe-area-inset-top, 0);
$safe-area-bottom: env(safe-area-inset-bottom, 0);
```

---

## 附录 B: 页面模块清单

| 模块 | 优先级 | 说明 |
|-----|--------|------|
| 顶部导航栏 | P0 | 返回、消息、设置 |
| 用户信息区 | P0 | 头像、问候语、用户ID、健康卡图标 |
| 健康卡推广区 | P1 | 权益展示、推广卡片 |
| 活动横幅 | P1 | 话题标签、活动入口 |
| 红包/会员区 | P0 | 红包、会员、优惠券入口 |
| 资产统计 | P1 | 积分、健康币展示 |
| 快捷入口 | P0 | 订单、购物车、待收货 |
| 健康服务区 | P1 | 6个健康服务功能 |
| 底部导航 | P0 | 首页、百姓健康管家、我的 |

---

**文档结束**

**维护团队**: DrugMall UI/UX Design Team  
**最后更新**: 2026-04-15  
**版本**: V1.0
