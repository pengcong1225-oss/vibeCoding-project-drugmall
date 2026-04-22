<template>
  <div class="store-detail-page">
    <!-- 顶部导航栏（固定） -->
    <div class="top-nav">
      <div class="nav-left">
        <div class="back-btn" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
        </div>
        <div class="search-box">
          <el-icon class="search-icon"><Search /></el-icon>
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索店内商品，约2000件"
            @keyup.enter="handleSearch"
          />
        </div>
      </div>
      <div class="nav-right">
        <div class="action-btn" :class="{ active: isFavorite }" @click="toggleFavorite">
          <el-icon><Star /></el-icon>
        </div>
        <div class="action-btn has-badge" @click="handleMore">
          <el-icon><MoreFilled /></el-icon>
          <span class="badge">7</span>
        </div>
      </div>
    </div>

    <!-- 店铺信息头部 -->
    <div class="store-header" v-if="store">
      <div class="store-info">
        <div class="store-logo">
          <img v-if="store.logo" :src="store.logo" alt="店铺logo" />
          <div v-else class="logo-placeholder">
            <span>宏泰<br/>大药房</span>
          </div>
        </div>
        <div class="store-detail">
          <h1 class="store-name">宏泰大药房（恒大山水城店）</h1>
          <div class="store-meta">
            <div class="meta-item">
              <span class="meta-label">月售</span>
              <span class="meta-value highlight">100+</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">美团专送</span>
              <span class="meta-value time">约15分钟</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">商家服务</span>
              <span class="meta-value service">售后无忧</span>
            </div>
          </div>
          <div class="store-tags">
            <span class="tag">品牌连锁</span>
            <span class="tag">4年老店</span>
            <span class="tag">"商家态度好"</span>
            <span class="tag">店铺回头客500+</span>
            <span class="tag">店铺好评数超1万</span>
          </div>
        </div>
      </div>
      <!-- 优惠券栏 -->
      <div class="coupon-bar">
        <div class="coupon-item">
          <span class="coupon-tag red">减</span>
          <span class="coupon-value">5元*2张券</span>
          <span class="coupon-btn">领取</span>
        </div>
        <div class="coupon-item highlight">
          <span class="coupon-tag red">减</span>
          <span class="coupon-value">满29减3</span>
          <span class="coupon-btn share">分享领</span>
        </div>
        <div class="coupon-item">
          <span class="coupon-tag yellow">券</span>
          <span class="coupon-value">满49减5</span>
          <span class="coupon-btn">领取</span>
        </div>
        <div class="coupon-more">
          <span>更多</span>
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>
    </div>

    <!-- Tab导航（sticky） -->
    <div class="tab-nav" :class="{ sticky: isTabSticky }">
      <div class="tab-list">
        <div
          v-for="tab in tabs"
          :key="tab.key"
          :class="['tab-item', { active: activeTab === tab.key }]"
          @click="handleTabChange(tab.key)"
        >
          {{ tab.label }}
        </div>
      </div>
      <div class="tab-right">
        <div class="coupon-tag">
          <span class="tag-text">入会领5元券</span>
        </div>
        <div class="member-btn">
          <span>商家会员</span>
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>
    </div>

    <!-- 主体内容区 -->
    <div class="main-content">
      <!-- 首页内容 -->
      <template v-if="activeTab === 'home'">
        <div class="home-content">
          <!-- 分类快捷入口 -->
          <div class="category-grid">
            <div
              v-for="cat in homeCategories"
              :key="cat.id"
              class="category-grid-item"
              @click="handleCategoryChange(cat.id)"
            >
              <div class="cat-icon-wrapper" :style="{ background: cat.bgColor }">
                <span class="cat-icon-text">{{ cat.iconText }}</span>
              </div>
              <span class="cat-name">{{ cat.name }}</span>
            </div>
          </div>

          <!-- Banner轮播图 -->
          <div class="banner-section">
            <div class="banner-carousel">
              <div class="banner-slide" :style="{ background: 'linear-gradient(90deg, #FFE4E1 0%, #FFF0F5 100%)' }">
                <div class="banner-content">
                  <div class="banner-text">
                    <h3>换季<br/>说再见</h3>
                    <span class="banner-btn">查看</span>
                  </div>
                  <img src="https://img.alicdn.com/imgextra/i4/2928278100/O1CN01mq1p5s1VubCqX5LEJ_!!2928278100.jpg" alt="banner" />
                </div>
              </div>
            </div>
          </div>

          <!-- 快捷专区导航 -->
          <div class="quick-section-nav">
            <div
              v-for="section in quickSections"
              :key="section.key"
              :class="['quick-section-item', { active: activeQuickSection === section.key }]"
              @click="activeQuickSection = section.key"
            >
              {{ section.label }}
            </div>
          </div>

          <!-- 凑单专区 -->
          <div class="section-block" id="coudan">
            <div class="section-header">
              <h3 class="section-title">凑单专区</h3>
              <span class="section-more">更多 <el-icon><ArrowRight /></el-icon></span>
            </div>
            <!-- 价格筛选标签 -->
            <div class="price-filter-tags">
              <div
                v-for="tag in priceFilterTags"
                :key="tag.key"
                :class="['filter-tag', { active: activePriceFilter === tag.key }]"
                @click="activePriceFilter = tag.key"
              >
                {{ tag.label }}
              </div>
            </div>
            <!-- 凑单商品横向滑动 -->
            <div class="horizontal-product-scroll">
              <div
                v-for="product in coudanProducts"
                :key="product.id"
                class="horizontal-product-item"
                @click="goToDrugDetail(product.id)"
              >
                <div class="product-image-wrapper">
                  <img v-if="product.image" :src="product.image" :alt="product.name" />
                  <div v-else class="image-placeholder-grid" :style="{ backgroundColor: product.imageColor || '#3B8CFF' }">
                    <span>{{ product.imageText || product.name?.slice(0, 2) }}</span>
                  </div>
                  <div v-if="product.isRx" class="rx-badge">处方药</div>
                  <div v-if="product.doctor" class="doctor-badge">
                    <el-icon><FirstAidKit /></el-icon>
                    <span>问三甲医生</span>
                  </div>
                </div>
                <div class="product-info-grid">
                  <div class="product-name-grid">{{ product.name }}</div>
                  <div class="product-price-row">
                    <span class="price-symbol">¥</span>
                    <span class="price-value">{{ product.price?.toFixed(1) }}</span>
                    <div class="add-btn-small" @click.stop="addToCart(product)">
                      <el-icon><Plus /></el-icon>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!-- 轮播指示器 -->
            <div class="carousel-dots">
              <span class="dot active"></span>
              <span class="dot"></span>
            </div>
          </div>

          <!-- 优惠专区 -->
          <div class="section-block" id="youhui">
            <div class="section-header">
              <h3 class="section-title">优惠</h3>
              <span class="section-more">更多 <el-icon><ArrowRight /></el-icon></span>
            </div>
            <div class="horizontal-product-scroll">
              <div
                v-for="product in youhuiProducts"
                :key="product.id"
                class="horizontal-product-item"
                @click="goToDrugDetail(product.id)"
              >
                <div class="product-image-wrapper">
                  <img v-if="product.image" :src="product.image" :alt="product.name" />
                  <div v-else class="image-placeholder-grid" :style="{ backgroundColor: product.imageColor || '#3B8CFF' }">
                    <span>{{ product.imageText || product.name?.slice(0, 2) }}</span>
                  </div>
                  <div v-if="product.isRx" class="rx-badge">处方药</div>
                </div>
                <div class="product-info-grid">
                  <div class="product-name-grid">{{ product.name }}</div>
                  <div class="product-price-row">
                    <span class="price-symbol">¥</span>
                    <span class="price-value">{{ product.price?.toFixed(1) }}</span>
                    <div class="add-btn-small" @click.stop="addToCart(product)">
                      <el-icon><Plus /></el-icon>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!-- 轮播指示器 -->
            <div class="carousel-dots">
              <span class="dot active"></span>
              <span class="dot"></span>
            </div>
          </div>

          <!-- 严选专区 -->
          <div class="section-block" id="yanxuan">
            <div class="section-header">
              <h3 class="section-title">严选专区</h3>
              <span class="section-more">更多 <el-icon><ArrowRight /></el-icon></span>
            </div>
            <div class="horizontal-product-scroll">
              <div
                v-for="product in yanxuanProducts"
                :key="product.id"
                class="horizontal-product-item"
                @click="goToDrugDetail(product.id)"
              >
                <div class="product-image-wrapper">
                  <img v-if="product.image" :src="product.image" :alt="product.name" />
                  <div v-else class="image-placeholder-grid" :style="{ backgroundColor: product.imageColor || '#3B8CFF' }">
                    <span>{{ product.imageText || product.name?.slice(0, 2) }}</span>
                  </div>
                  <div v-if="product.isRx" class="rx-badge">处方药</div>
                  <div v-if="product.doctor" class="doctor-badge">
                    <el-icon><FirstAidKit /></el-icon>
                    <span>问三甲医生</span>
                  </div>
                </div>
                <div class="product-info-grid">
                  <div class="product-name-grid">{{ product.name }}</div>
                  <div class="product-price-row">
                    <span class="price-symbol">¥</span>
                    <span class="price-value">{{ product.price }}</span>
                    <div class="add-btn-small" @click.stop="addToCart(product)">
                      <el-icon><Plus /></el-icon>
                    </div>
                  </div>
                  <div v-if="product.discount" class="discount-text">已优惠¥{{ product.discount }}</div>
                </div>
              </div>
            </div>
            <!-- 轮播指示器 -->
            <div class="carousel-dots">
              <span class="dot active"></span>
              <span class="dot"></span>
            </div>
          </div>

          <!-- 性福生活专区 -->
          <div class="section-block" id="xingfu">
            <div class="section-header">
              <h3 class="section-title">性福生活</h3>
              <span class="section-more">更多 <el-icon><ArrowRight /></el-icon></span>
            </div>
            <div class="horizontal-product-scroll">
              <div
                v-for="product in xingfuProducts"
                :key="product.id"
                class="horizontal-product-item"
                @click="goToDrugDetail(product.id)"
              >
                <div class="product-image-wrapper">
                  <img v-if="product.image" :src="product.image" :alt="product.name" />
                  <div v-else class="image-placeholder-grid" :style="{ backgroundColor: product.imageColor || '#3B8CFF' }">
                    <span>{{ product.imageText || product.name?.slice(0, 2) }}</span>
                  </div>
                </div>
                <div class="product-info-grid">
                  <div class="product-name-grid">{{ product.name }}</div>
                  <div class="product-price-row">
                    <span class="price-symbol">¥</span>
                    <span class="price-value">{{ product.price?.toFixed(1) }}</span>
                    <div class="add-btn-small" @click.stop="addToCart(product)">
                      <el-icon><Plus /></el-icon>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!-- 轮播指示器 -->
            <div class="carousel-dots">
              <span class="dot active"></span>
              <span class="dot"></span>
            </div>
          </div>

          <!-- 为你优选 -->
          <div class="section-block recommend-section">
            <div class="section-header">
              <h3 class="section-title">为你优选</h3>
            </div>
            <div class="horizontal-product-scroll">
              <div
                v-for="product in recommendProducts"
                :key="product.id"
                class="horizontal-product-item"
                @click="goToDrugDetail(product.id)"
              >
                <div class="product-image-wrapper">
                  <img v-if="product.image" :src="product.image" :alt="product.name" />
                  <div v-else class="image-placeholder-grid" :style="{ backgroundColor: product.imageColor || '#3B8CFF' }">
                    <span>{{ product.imageText || product.name?.slice(0, 2) }}</span>
                  </div>
                  <div v-if="product.isRx" class="rx-badge">处方药</div>
                  <div v-if="product.doctor" class="doctor-badge">
                    <el-icon><FirstAidKit /></el-icon>
                    <span>问三甲医生</span>
                  </div>
                </div>
                <div class="product-info-grid">
                  <div class="product-name-grid">{{ product.name }}</div>
                  <div class="product-price-row">
                    <span class="price-symbol">¥</span>
                    <span class="price-value">{{ product.price }}</span>
                    <div class="add-btn-small" @click.stop="addToCart(product)">
                      <el-icon><Plus /></el-icon>
                    </div>
                  </div>
                  <div v-if="product.discount" class="discount-text">已优惠¥{{ product.discount }}</div>
                </div>
              </div>
            </div>
            <!-- 轮播指示器 -->
            <div class="carousel-dots">
              <span class="dot active"></span>
              <span class="dot"></span>
            </div>
          </div>

          <!-- 广告横幅 -->
          <div class="ad-banner">
            <div class="ad-content">
              <span class="ad-brand">赤尾</span>
              <span class="ad-slogan">无套感</span>
            </div>
            <span class="ad-label">广告</span>
          </div>
        </div>
      </template>

      <!-- 全部商品内容 -->
      <template v-if="activeTab === 'products'">
        <div class="products-content">
          <!-- 左侧分类栏 -->
          <div class="category-sidebar">
            <div
              v-for="cat in categories"
              :key="cat.id"
              :class="['category-item', { active: activeCategory === cat.id }]"
              @click="handleCategoryChange(cat.id)"
            >
              <el-icon v-if="cat.icon" class="cat-icon">
                <component :is="cat.icon" />
              </el-icon>
              <span class="cat-name">{{ cat.name }}</span>
              <span v-if="cat.badge" class="cat-badge">{{ cat.badge }}</span>
            </div>
          </div>

          <!-- 右侧商品区 -->
          <div class="product-area">
            <!-- 健康卡推广横幅 -->
            <div class="health-card-banner">
              <div class="banner-left">
                <div class="banner-title">
                  <span class="brand">美团·健康卡</span>
                </div>
                <div class="banner-desc">开通后预计可省34元/月</div>
              </div>
              <div class="banner-right">
                <span class="link-text">了解更多</span>
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>

            <!-- 排序栏 -->
            <div class="sort-bar">
              <div
                v-for="sort in sortOptions"
                :key="sort.key"
                :class="['sort-item', { active: activeSort === sort.key }]"
                @click="handleSortChange(sort.key)"
              >
                <span>{{ sort.label }}</span>
                <el-icon v-if="sort.hasArrow" class="sort-arrow">
                  <ArrowDown v-if="sort.key !== 'price' || priceSortAsc" />
                  <ArrowUp v-else />
                </el-icon>
              </div>
            </div>

            <!-- 商品列表 -->
            <div class="product-list" ref="productListRef" @scroll="handleProductScroll">
              <div
                v-for="product in filteredProducts"
                :key="product.id"
                class="product-item"
                @click="goToDrugDetail(product.id)"
              >
                <div class="product-image">
                  <img v-if="product.image" :src="product.image" :alt="product.name" />
                  <div v-else class="image-placeholder" :style="{ backgroundColor: product.imageColor || '#3B8CFF' }">
                    <span>{{ product.imageText || product.name?.slice(0, 2) }}</span>
                  </div>
                </div>
                <div class="product-info">
                  <div class="product-tags">
                    <span v-for="tag in product.tags" :key="tag" class="symptom-tag">{{ tag }}</span>
                  </div>
                  <div class="product-name">{{ product.name }}</div>
                  <div class="product-spec">{{ product.specification }}</div>
                  <div class="product-footer">
                    <div class="price-section">
                      <div class="current-price">
                        <span class="symbol">¥</span>
                        <span class="value">{{ product.price?.toFixed(2) }}</span>
                      </div>
                      <div v-if="product.cashback" class="cashback-tag">
                        最高返现{{ product.cashback }}元
                      </div>
                    </div>
                    <div class="add-btn" @click.stop="addToCart(product)">
                      <el-icon><Plus /></el-icon>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 医生咨询入口 -->
              <div class="doctor-entry">
                <div class="doctor-info">
                  <el-icon><FirstAidKit /></el-icon>
                  <span class="doctor-text">问三甲医生</span>
                  <span class="doctor-badge">24h</span>
                </div>
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- 商家内容 -->
      <template v-if="activeTab === 'merchant'">
        <div class="merchant-content">
          <!-- 商家地址 -->
          <div class="merchant-section">
            <div class="section-item">
              <el-icon class="section-icon"><Location /></el-icon>
              <div class="section-content">
                <p class="section-text">{{ store?.address || '北京市朝阳区建国路88号' }}</p>
              </div>
              <div class="section-actions">
                <el-icon class="action-icon"><Position /></el-icon>
                <el-icon class="action-icon"><Phone /></el-icon>
              </div>
            </div>
          </div>

          <!-- 商家资质 -->
          <div class="merchant-section">
            <div class="section-item" @click="showQualification">
              <el-icon class="section-icon"><CircleCheck /></el-icon>
              <div class="section-content">
                <p class="section-text">查看商家资质</p>
              </div>
              <el-icon class="arrow-icon"><ArrowRight /></el-icon>
            </div>
          </div>

          <!-- 商家评价 -->
          <div class="merchant-section">
            <div class="section-item" @click="showReviews">
              <el-icon class="section-icon"><ChatDotRound /></el-icon>
              <div class="section-content">
                <p class="section-text">评价 (26条)</p>
              </div>
              <el-icon class="arrow-icon"><ArrowRight /></el-icon>
            </div>
          </div>

          <!-- 配送服务 -->
          <div class="merchant-section">
            <div class="section-item">
              <el-icon class="section-icon"><Van /></el-icon>
              <div class="section-content">
                <p class="section-text">
                  配送服务：<span class="highlight-tag">美团专送</span> 提供高品质配送服务
                </p>
              </div>
            </div>
            <div class="section-item">
              <el-icon class="section-icon"><Clock /></el-icon>
              <div class="section-content">
                <p class="section-text">配送时间：07:45-21:10</p>
              </div>
            </div>
          </div>

          <!-- 商家服务 -->
          <div class="merchant-section">
            <div class="section-item">
              <el-icon class="section-icon"><Service /></el-icon>
              <div class="section-content">
                <p class="section-text">商家服务</p>
              </div>
              <div class="service-tags">
                <span class="service-tag">到店自取(享优惠)</span>
              </div>
            </div>
          </div>

          <!-- 优惠活动 -->
          <div class="merchant-section">
            <div class="promo-list">
              <div class="promo-item">
                <span class="promo-label">减</span>
                <span class="promo-text">满49减5;满69减10;满99减15;满129减20</span>
              </div>
              <div class="promo-item">
                <span class="promo-label discount">折</span>
                <span class="promo-text">折扣商品1.67折起</span>
              </div>
              <div class="promo-item">
                <span class="promo-label delivery">减</span>
                <span class="promo-text">购买指定商品减配送费</span>
              </div>
            </div>
          </div>

          <!-- 商家公告 -->
          <div class="merchant-notice">
            <el-icon class="notice-icon"><InfoFilled /></el-icon>
            <p class="notice-text">感恩信赖，如需咨询请您拨打客服电话，宏泰竭诚为您服务，祝您早日安康！</p>
          </div>
        </div>
      </template>
    </div>

    <!-- 底部购物车栏 -->
    <div class="cart-bar">
      <div class="cart-left">
        <div class="cart-icon-item" @click="goToConsult">
          <div class="icon-circle">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <span class="icon-label">客服</span>
        </div>
        <div class="cart-icon-item" @click="goToStore">
          <div class="icon-circle">
            <el-icon><Shop /></el-icon>
          </div>
          <span class="icon-label">店铺</span>
        </div>
        <div class="cart-icon-item cart-icon-wrapper" @click="goToCart">
          <div class="icon-circle cart-circle">
            <el-icon><ShoppingCart /></el-icon>
            <span v-if="cartStore.totalCount > 0" class="cart-badge">{{ cartStore.totalCount }}</span>
          </div>
          <span class="icon-label">购物车</span>
        </div>
      </div>
      <div class="cart-center">
        <span class="delivery-fee">配送费¥1.5</span>
      </div>
      <div class="cart-right">
        <span class="min-order">¥20起送</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Search,
  Star,
  MoreFilled,
  ArrowRight,
  ArrowDown,
  ArrowUp,
  Plus,
  FirstAidKit,
  ChatDotRound,
  Shop,
  ShoppingCart,
  Ticket,
  Timer,
  Location,
  Position,
  Phone,
  CircleCheck,
  Van,
  Clock,
  Service,
  InfoFilled
} from '@element-plus/icons-vue'
import { getStoreDetail, getStoreDrugs, type StoreInfo, type StoreDrug } from '@/api/modules/store'
import { useCartStore } from '@/stores/cart'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

// 状态
const loading = ref(false)
const isFavorite = ref(false)
const searchKeyword = ref('')
const isTabSticky = ref(false)
const activeTab = ref('home')
const activeCategory = ref('all')
const activeSort = ref('default')
const priceSortAsc = ref(true)
const activeQuickSection = ref('coudan')
const activePriceFilter = ref('all')

// Tab导航
const tabs = [
  { key: 'home', label: '首页' },
  { key: 'products', label: '全部商品' },
  { key: 'merchant', label: '商家' }
]

// 首页分类数据 - 按照图片中的样式
const homeCategories = [
  { id: 'tight', name: '感冒用药', iconText: '紧', bgColor: 'linear-gradient(135deg, #FF6B35 0%, #FF8C42 100%)' },
  { id: 'urgent', name: '清热解毒', iconText: '急', bgColor: 'linear-gradient(135deg, #9B59B6 0%, #BB8FCE 100%)' },
  { id: 'avoid', name: '五官用药', iconText: '避', bgColor: 'linear-gradient(135deg, #E84393 0%, #FD79A8 100%)' },
  { id: 'pregnant', name: '胃肠科药', iconText: '孕', bgColor: 'linear-gradient(135deg, #00B894 0%, #55EFC4 100%)' },
  { id: 'rx', name: '处方药', iconText: '处', bgColor: 'linear-gradient(135deg, #74B9FF 0%, #A29BFE 100%)' },
  { id: 'recognize', name: '儿科用药', iconText: '认', bgColor: 'linear-gradient(135deg, #00CEC9 0%, #81ECEC 100%)' },
  { id: 'standard', name: '妇科用药', iconText: '准', bgColor: 'linear-gradient(135deg, #FDCB6E 0%, #FFEAA7 100%)' },
  { id: 'pill', name: '男科用药', iconText: '丹', bgColor: 'linear-gradient(135deg, #6C5CE7 0%, #A29BFE 100%)' },
  { id: 'charm', name: '性福生活', iconText: '媚', bgColor: 'linear-gradient(135deg, #E17055 0%, #FAB1A0 100%)' },
  { id: 'all', name: '全部分类', iconText: '全', bgColor: 'linear-gradient(135deg, #B2BEC3 0%, #DFE6E9 100%)' }
]

// 分类数据
const categories = [
  { id: 'all', name: '全部商品' },
  { id: 'tight', name: '感冒用药' },
  { id: 'urgent', name: '清热解毒' },
  { id: 'avoid', name: '五官用药' },
  { id: 'pregnant', name: '胃肠科药' },
  { id: 'rx', name: '处方药' },
  { id: 'recognize', name: '儿科用药' },
  { id: 'standard', name: '妇科用药' },
  { id: 'pill', name: '男科用药' },
  { id: 'charm', name: '性福生活' }
]

// 排序选项
const sortOptions = [
  { key: 'default', label: '默认', hasArrow: false },
  { key: 'sales', label: '销量', hasArrow: true },
  { key: 'price', label: '价格', hasArrow: true }
]

// 商品数据
const store = ref<StoreInfo | null>(null)
const products = ref<StoreDrug[]>([])

// 模拟商品数据
const mockProducts: StoreDrug[] = [
  {
    id: '1',
    name: '蒙脱石散',
    specification: '3g*10袋/盒',
    price: 43.08,
    originalPrice: 58.00,
    stock: 100,
    sales: 1200,
    isRx: false,
    image: 'https://img.alicdn.com/imgextra/i4/2928278100/O1CN01mq1p5s1VubCqX5LEJ_!!2928278100.jpg',
    tags: ['腹泻', '急慢性肠胃炎'],
    cashback: 6.46
  },
  {
    id: '2',
    name: '肠炎宁片',
    specification: '0.42g*24片/盒',
    price: 28.50,
    originalPrice: 35.00,
    stock: 80,
    sales: 890,
    isRx: false,
    image: 'https://img.alicdn.com/imgextra/i1/2928278100/O1CN01wKJPxT1VubCqX5K1P_!!2928278100.jpg',
    tags: ['腹泻', '消化不良'],
    cashback: 4.28
  },
  {
    id: '3',
    name: '诺氟沙星胶囊',
    specification: '0.1g*24粒/盒',
    price: 15.80,
    originalPrice: 22.00,
    stock: 200,
    sales: 2300,
    isRx: true,
    image: 'https://img.alicdn.com/imgextra/i3/2928278100/O1CN01YqW1ZU1VubCqX5K1Q_!!2928278100.jpg',
    tags: ['肠道感染', '细菌性痢疾'],
    cashback: 2.37
  }
]

// 快捷专区导航
const quickSections = [
  { key: 'coudan', label: '凑单专区' },
  { key: 'youhui', label: '优惠' },
  { key: 'yanxuan', label: '严选专区' },
  { key: 'xingfu', label: '性福生活' },
  { key: 'changwei', label: '肠胃用药' },
  { key: 'diannei', label: '店内优惠' }
]

// 价格筛选标签
const priceFilterTags = [
  { key: 'all', label: '全部' },
  { key: 'under5', label: '5元以下' },
  { key: '5to10', label: '5-10元' },
  { key: '10to20', label: '10-20元' }
]

// 凑单专区商品（低价商品）
const coudanProducts = ref([
  {
    id: 'c1',
    name: '[太湖美]珍珠明目滴眼液8ml/瓶',
    price: 2.7,
    image: 'https://img.alicdn.com/imgextra/i1/2928278100/O1CN01KqW1ZU1VubCqX5K1S_!!2928278100.jpg',
    isRx: false
  },
  {
    id: 'c2',
    name: '[东北]维生素C片100mg*10片/瓶',
    price: 1.7,
    image: 'https://img.alicdn.com/imgextra/i4/2928278100/O1CN01mJpZ1V1VubCqX5K1T_!!2928278100.jpg',
    isRx: false
  },
  {
    id: 'c3',
    name: '[六棉牌]清凉喉片16片/瓶',
    price: 2.5,
    image: 'https://img.alicdn.com/imgextra/i2/2928278100/O1CN01jJpZ1V1VubCqX5K1R_!!2928278100.jpg',
    isRx: false
  },
  {
    id: 'c4',
    name: '[五景]珍珠明目滴眼液8ml/瓶',
    price: 2.0,
    image: 'https://img.alicdn.com/imgextra/i1/2928278100/O1CN01KqW1ZU1VubCqX5K1S_!!2928278100.jpg',
    isRx: false
  },
  {
    id: 'c5',
    name: '[辰欣]红霉素眼膏0.5%*2g/支',
    price: 2.1,
    image: 'https://img.alicdn.com/imgextra/i3/2928278100/O1CN01YqW1ZU1VubCqX5K1Q_!!2928278100.jpg',
    isRx: false
  },
  {
    id: 'c6',
    name: '[奥美医疗]无菌棉签(III型)单头',
    price: 0.88,
    image: 'https://img.alicdn.com/imgextra/i1/2928278100/O1CN01wKJPxT1VubCqX5K1P_!!2928278100.jpg',
    isRx: false,
    doctor: true
  }
])

// 优惠专区商品
const youhuiProducts = ref([
  {
    id: 'y1',
    name: '[丹媚]左炔诺孕酮肠溶片(紧急避孕)',
    price: 20.6,
    image: 'https://img.alicdn.com/imgextra/i4/2928278100/O1CN01mq1p5s1VubCqX5LEJ_!!2928278100.jpg',
    isRx: false
  },
  {
    id: 'y2',
    name: '[亚宝]牛黄解毒片24片/袋',
    price: 1.5,
    image: 'https://img.alicdn.com/imgextra/i2/2928278100/O1CN01jJpZ1V1VubCqX5K1R_!!2928278100.jpg',
    isRx: false
  },
  {
    id: 'y3',
    name: '[亚宝]三黄片20片/袋',
    price: 1.6,
    image: 'https://img.alicdn.com/imgextra/i1/2928278100/O1CN01wKJPxT1VubCqX5K1P_!!2928278100.jpg',
    isRx: false
  }
])

// 严选专区商品
const yanxuanProducts = ref([
  {
    id: 'x1',
    name: '[新乐敦]复方门冬维甘滴眼液',
    price: 24,
    discount: 2.64,
    image: 'https://img.alicdn.com/imgextra/i1/2928278100/O1CN01KqW1ZU1VubCqX5K1S_!!2928278100.jpg',
    isRx: false
  },
  {
    id: 'x2',
    name: '[葵花]胃康灵胶囊0.4g*12粒',
    price: 19.5,
    discount: 3.3,
    image: 'https://img.alicdn.com/imgextra/i4/2928278100/O1CN01mJpZ1V1VubCqX5K1T_!!2928278100.jpg',
    isRx: false
  },
  {
    id: 'x3',
    name: '[葵花]护肝片(糖衣)0.35g*100片',
    price: 33,
    discount: 3.4,
    image: 'https://img.alicdn.com/imgextra/i3/2928278100/O1CN01YqW1ZU1VubCqX5K1Q_!!2928278100.jpg',
    isRx: false,
    doctor: true
  }
])

// 性福生活专区商品
const xingfuProducts = ref([
  {
    id: 's1',
    name: '[丹媚]左炔诺孕酮肠溶片',
    price: 20.6,
    image: 'https://img.alicdn.com/imgextra/i4/2928278100/O1CN01mq1p5s1VubCqX5LEJ_!!2928278100.jpg',
    isRx: false
  },
  {
    id: 's2',
    name: '左炔诺孕酮片1.5mg*1片',
    price: 15.8,
    image: 'https://img.alicdn.com/imgextra/i2/2928278100/O1CN01jJpZ1V1VubCqX5K1R_!!2928278100.jpg',
    isRx: false
  },
  {
    id: 's3',
    name: '[冈本]避孕套',
    price: 35,
    image: 'https://img.alicdn.com/imgextra/i1/2928278100/O1CN01wKJPxT1VubCqX5K1P_!!2928278100.jpg',
    isRx: false
  }
])

// 为你优选商品（按照图片样式）
const recommendProducts = ref([
  {
    id: 'r1',
    name: '[新乐敦]复方门冬维甘滴眼液',
    price: 24,
    discount: 2.64,
    image: 'https://img.alicdn.com/imgextra/i1/2928278100/O1CN01KqW1ZU1VubCqX5K1S_!!2928278100.jpg',
    isRx: false
  },
  {
    id: 'r2',
    name: '[京都念慈菴]京都念慈菴蜜炼川贝枇杷膏',
    price: 45,
    image: 'https://img.alicdn.com/imgextra/i4/2928278100/O1CN01mJpZ1V1VubCqX5K1T_!!2928278100.jpg',
    isRx: false
  },
  {
    id: 'r3',
    name: '[京都念慈菴]京都念慈菴蜜炼川贝枇杷膏',
    price: 25,
    image: 'https://img.alicdn.com/imgextra/i3/2928278100/O1CN01YqW1ZU1VubCqX5K1Q_!!2928278100.jpg',
    isRx: false,
    doctor: true
  }
])

// 过滤后的商品
const filteredProducts = computed(() => {
  let result = [...products.value]

  // 根据分类筛选
  if (activeCategory.value !== 'all') {
    result = result.filter(p => {
      switch (activeCategory.value) {
        case 'tight':
          return p.tags?.includes('感冒') || p.tags?.includes('咳嗽') || p.name?.includes('感冒')
        case 'pregnant':
          return p.tags?.includes('肠胃') || p.tags?.includes('腹泻') || p.name?.includes('肠胃') || p.name?.includes('肠炎')
        case 'charm':
          return p.tags?.includes('性福') || p.name?.includes('避孕')
        case 'avoid':
          return p.tags?.includes('五官') || p.name?.includes('眼') || p.name?.includes('鼻')
        case 'urgent':
          return p.tags?.includes('清热') || p.name?.includes('清热') || p.name?.includes('解毒')
        case 'rx':
          return p.isRx
        case 'recognize':
          return p.name?.includes('儿童') || p.tags?.includes('儿童')
        case 'standard':
          return p.name?.includes('妇科') || p.tags?.includes('妇科')
        case 'pill':
          return p.name?.includes('男科') || p.tags?.includes('男科')
        default:
          return true
      }
    })
  }

  // 搜索过滤
  if (searchKeyword.value) {
    result = result.filter(p => p.name.includes(searchKeyword.value))
  }

  // 排序
  if (activeSort.value === 'sales') {
    result.sort((a, b) => (b.sales || 0) - (a.sales || 0))
  } else if (activeSort.value === 'price') {
    result.sort((a, b) => {
      const diff = a.price - b.price
      return priceSortAsc.value ? diff : -diff
    })
  }

  return result
})

// 加载店铺数据
const loadStoreData = async () => {
  loading.value = true
  const storeId = route.params.id as string

  try {
    const res = await getStoreDetail(storeId)
    store.value = res || {
      id: storeId,
      name: '宏泰大药房（恒大山水城店）',
      rating: 4.9,
      isOpen: true,
      deliveryTime: 15,
      minDelivery: 20,
      distance: '1.2km',
      address: '北京市朝阳区建国路88号',
      phone: '010-12345678',
      tags: ['品牌连锁', '4年老店']
    }

    // 加载商品列表
    await fetchProducts()
  } catch (error) {
    console.error('加载店铺数据失败:', error)
    store.value = {
      id: storeId,
      name: '宏泰大药房（恒大山水城店）',
      rating: 4.9,
      isOpen: true,
      deliveryTime: 15,
      minDelivery: 20,
      distance: '1.2km',
      address: '北京市朝阳区建国路88号',
      phone: '010-12345678',
      tags: ['品牌连锁', '4年老店']
    }
    products.value = mockProducts
  } finally {
    loading.value = false
  }
}

// 获取商品列表
const fetchProducts = async () => {
  try {
    const storeId = route.params.id as string
    const res = await getStoreDrugs(storeId)
    if (res && res.length > 0) {
      products.value = res.map((p: StoreDrug) => ({
        ...p,
        tags: p.isRx ? ['处方药'] : ['OTC'],
        cashback: +(p.price * 0.15).toFixed(2)
      }))
    } else {
      products.value = mockProducts
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    products.value = mockProducts
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 切换收藏
const toggleFavorite = () => {
  isFavorite.value = !isFavorite.value
  ElMessage.success(isFavorite.value ? '已收藏店铺' : '已取消收藏')
}

// 更多操作
const handleMore = () => {
  ElMessage.info('更多功能开发中')
}

// Tab切换
const handleTabChange = (key: string) => {
  activeTab.value = key
}

// 分类切换
const handleCategoryChange = (id: string) => {
  activeCategory.value = id
}

// 排序切换
const handleSortChange = (key: string) => {
  if (key === 'price' && activeSort.value === 'price') {
    priceSortAsc.value = !priceSortAsc.value
  }
  activeSort.value = key
}

// 搜索
const handleSearch = () => {
  if (searchKeyword.value) {
    ElMessage.info(`搜索: ${searchKeyword.value}`)
  }
}

// 添加购物车
const addToCart = async (product: any) => {
  try {
    await cartStore.addItem({
      drugId: product.id,
      name: product.name,
      price: product.price,
      quantity: 1,
      specification: product.specification || '',
      manufacturer: '',
      image: product.image || '',
      disease: '',
      usage: '',
      isRx: product.isRx || false
    })
  } catch (error) {
    console.error('添加购物车失败:', error)
  }
}

// 跳转药品详情
const goToDrugDetail = (drugId: string) => {
  const storeId = route.params.id as string
  router.push(`/store/${storeId}/drug/${drugId}`)
}

// 跳转购物车
const goToCart = () => {
  router.push('/cart')
}

// 跳转结算
const goToCheckout = () => {
  if (cartStore.totalCount === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  // 跳转到新的结算页
  router.push('/inquiry/checkout/0')
}

// 去咨询
const goToConsult = () => {
  router.push('/inquiry')
}

// 店铺首页
const goToStore = () => {
  activeTab.value = 'home'
}

// 查看商家资质
const showQualification = () => {
  ElMessage.info('商家资质功能开发中')
}

// 查看评价
const showReviews = () => {
  ElMessage.info('评价功能开发中')
}

// 滚动处理
const handleProductScroll = () => {
  // 可以在这里处理滚动加载更多
}

// 监听滚动使Tab吸顶和头部隐藏
let scrollHandler: () => void
let lastScrollTop = 0

onMounted(() => {
  loadStoreData()
  cartStore.fetchCartList()

  scrollHandler = () => {
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop
    // Tab吸顶逻辑
    isTabSticky.value = scrollTop > 150
    // 头部隐藏/显示逻辑
    const header = document.querySelector('.top-nav') as HTMLElement
    if (header) {
      if (scrollTop > lastScrollTop && scrollTop > 60) {
        // 向下滚动，隐藏头部
        header.style.transform = 'translateY(-100%)'
      } else {
        // 向上滚动，显示头部
        header.style.transform = 'translateY(0)'
      }
    }
    lastScrollTop = scrollTop
  }
  window.addEventListener('scroll', scrollHandler, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', scrollHandler)
})
</script>

<style scoped lang="scss">
// 设计系统变量
$primary: #00C853;
$primary-dark: #00B248;
$accent: #FFC300;
$accent-dark: #FFB800;
$price-red: #FF4D4F;
$warning: #FF9500;
$info: #1890FF;

// 背景色
$bg-white: #FFFFFF;
$bg-gray: #F5F5F5;
$bg-light: #F8F8F8;

// 文字色
$text-primary: #333333;
$text-secondary: #666666;
$text-tertiary: #999999;
$text-white: #FFFFFF;

// 边框
$border-light: #EEEEEE;

// 字体大小
$font-xs: 11px;
$font-sm: 12px;
$font-md: 14px;
$font-lg: 16px;
$font-xl: 18px;
$font-2xl: 20px;

// 间距
$spacing-xs: 4px;
$spacing-sm: 8px;
$spacing-md: 12px;
$spacing-lg: 16px;
$spacing-xl: 20px;

// 圆角
$radius-sm: 4px;
$radius-md: 8px;
$radius-lg: 12px;
$radius-full: 9999px;

.store-detail-page {
  min-height: 100vh;
  background: $bg-gray;
  padding-top: 56px;
  padding-bottom: 70px;
}

// 顶部导航栏
.top-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 56px;
  background: $bg-white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $spacing-md;
  z-index: 100;
  transition: transform 0.3s ease;

  .nav-left {
    display: flex;
    align-items: center;
    flex: 1;
    gap: $spacing-sm;

    .back-btn {
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      color: $text-primary;

      &:hover {
        color: $primary;
      }

      .el-icon {
        font-size: 20px;
      }
    }

    .search-box {
      flex: 1;
      height: 36px;
      background: $bg-light;
      border-radius: $radius-full;
      display: flex;
      align-items: center;
      padding: 0 $spacing-md;
      gap: $spacing-sm;

      .search-icon {
        color: $text-tertiary;
        font-size: $font-md;
      }

      input {
        flex: 1;
        border: none;
        background: transparent;
        font-size: $font-sm;
        color: $text-primary;
        outline: none;

        &::placeholder {
          color: $text-tertiary;
        }
      }
    }
  }

  .nav-right {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
    margin-left: $spacing-sm;

    .action-btn {
      width: 36px;
      height: 36px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      color: $text-secondary;
      position: relative;

      &:hover {
        color: $primary;
      }

      &.active {
        color: $accent;
      }

      .el-icon {
        font-size: 22px;
      }

      &.has-badge {
        .badge {
          position: absolute;
          top: 2px;
          right: 2px;
          min-width: 16px;
          height: 16px;
          background: $price-red;
          color: $text-white;
          font-size: 10px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 0 4px;
          font-weight: 600;
        }
      }
    }
  }
}

// 店铺头部
.store-header {
  background: $bg-white;
  padding: $spacing-md;

  .store-info {
    display: flex;
    gap: $spacing-md;
    margin-bottom: $spacing-md;

    .store-logo {
      width: 70px;
      height: 70px;
      border-radius: $radius-md;
      overflow: hidden;
      flex-shrink: 0;
      background: linear-gradient(135deg, #00A8E8 0%, #0077B6 100%);
      display: flex;
      align-items: center;
      justify-content: center;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .logo-placeholder {
        color: $text-white;
        font-size: $font-xs;
        text-align: center;
        line-height: 1.3;
        font-weight: 600;
      }
    }

    .store-detail {
      flex: 1;

      .store-name {
        font-size: $font-lg;
        font-weight: 600;
        color: $text-primary;
        margin-bottom: $spacing-xs;
        line-height: 1.3;
      }

      .store-meta {
        display: flex;
        align-items: center;
        gap: $spacing-md;
        margin-bottom: $spacing-xs;

        .meta-item {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: $font-xs;

          .meta-label {
            color: $text-tertiary;
          }

          .meta-value {
            color: $text-secondary;
            font-weight: 500;

            &.highlight {
              color: $text-primary;
              font-weight: 600;
            }

            &.time {
              color: $accent-dark;
            }

            &.service {
              color: $text-secondary;
            }
          }
        }
      }

      .store-tags {
        display: flex;
        flex-wrap: wrap;
        gap: $spacing-xs;

        .tag {
          font-size: $font-xs;
          color: $text-tertiary;
          background: $bg-light;
          padding: 2px 6px;
          border-radius: $radius-sm;
        }
      }
    }
  }

  // 优惠券栏
  .coupon-bar {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
    overflow-x: auto;
    padding-bottom: $spacing-xs;

    &::-webkit-scrollbar {
      display: none;
    }

    .coupon-item {
      display: flex;
      align-items: center;
      gap: $spacing-xs;
      background: #FFF5F5;
      padding: $spacing-xs $spacing-sm;
      border-radius: $radius-sm;
      white-space: nowrap;
      border: 1px solid #FFD4D4;

      &.highlight {
        background: #FFF7E6;
        border-color: #FFD591;

        .coupon-value {
          color: $warning;
        }
      }

      .coupon-tag {
        width: 16px;
        height: 16px;
        border-radius: 2px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 10px;
        font-weight: 600;
        color: $text-white;

        &.red {
          background: $price-red;
        }

        &.yellow {
          background: $accent;
          color: $text-primary;
        }
      }

      .coupon-value {
        font-size: $font-xs;
        color: $price-red;
        font-weight: 500;
      }

      .coupon-btn {
        font-size: $font-xs;
        color: $text-white;
        background: $price-red;
        padding: 2px 8px;
        border-radius: $radius-sm;

        &.share {
          background: $warning;
        }
      }
    }

    .coupon-more {
      display: flex;
      align-items: center;
      gap: 2px;
      font-size: $font-xs;
      color: $text-tertiary;
      white-space: nowrap;

      .el-icon {
        font-size: 10px;
      }
    }
  }
}

// Tab导航
.tab-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 48px;
  background: $bg-white;
  padding: 0 $spacing-md;
  position: sticky;
  top: 0;
  z-index: 99;
  transition: box-shadow 0.3s ease;

  &.sticky {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  .tab-list {
    display: flex;
    align-items: center;
    gap: $spacing-xl;

    .tab-item {
      font-size: $font-md;
      color: $text-secondary;
      cursor: pointer;
      position: relative;
      padding: $spacing-sm 0;
      transition: color 0.2s;
      font-weight: 500;

      &:hover {
        color: $text-primary;
      }

      &.active {
        color: $text-primary;
        font-weight: 600;

        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          height: 3px;
          background: $text-primary;
          border-radius: 2px;
        }
      }
    }
  }

  .tab-right {
    display: flex;
    align-items: center;
    gap: $spacing-xs;

    .coupon-tag {
      background: linear-gradient(90deg, #E6F7FF 0%, #F0F5FF 100%);
      padding: $spacing-xs $spacing-sm;
      border-radius: $radius-sm;

      .tag-text {
        font-size: $font-xs;
        color: $info;
        font-weight: 500;
      }
    }

    .member-btn {
      display: flex;
      align-items: center;
      gap: 2px;
      font-size: $font-xs;
      color: $text-tertiary;
      cursor: pointer;
      background: $bg-light;
      padding: $spacing-xs $spacing-sm;
      border-radius: $radius-sm;

      &:hover {
        color: $primary;
      }

      .el-icon {
        font-size: $font-xs;
      }
    }
  }
}

// 主体内容区
.main-content {
  min-height: calc(100vh - 200px);
}

// 首页内容
.home-content {
  padding: $spacing-md;

  // 分类快捷入口 - 5列网格
  .category-grid {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: $spacing-md $spacing-sm;
    margin-bottom: $spacing-lg;
    background: $bg-white;
    padding: $spacing-md;
    border-radius: $radius-lg;

    .category-grid-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: $spacing-xs;
      cursor: pointer;

      .cat-icon-wrapper {
        width: 52px;
        height: 52px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

        .cat-icon-text {
          font-size: $font-xl;
          font-weight: 700;
          color: $text-white;
        }
      }

      .cat-name {
        font-size: $font-xs;
        color: $text-secondary;
        text-align: center;
      }
    }
  }

  // 区块样式
  .section-block {
    background: $bg-white;
    border-radius: $radius-lg;
    padding: $spacing-md;
    margin-bottom: $spacing-md;

    &.recommend-section {
      padding-bottom: $spacing-sm;
    }

    .section-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: $spacing-md;

      .section-title {
        font-size: $font-lg;
        font-weight: 600;
        color: $text-primary;
      }

      .section-more {
        display: flex;
        align-items: center;
        gap: 2px;
        font-size: $font-sm;
        color: $text-tertiary;
        cursor: pointer;

        .el-icon {
          font-size: 12px;
        }
      }
    }

    // 横向滑动商品列表
    .horizontal-product-scroll {
      display: flex;
      gap: $spacing-md;
      overflow-x: auto;
      padding-bottom: $spacing-xs;

      &::-webkit-scrollbar {
        display: none;
      }

      .horizontal-product-item {
        flex-shrink: 0;
        width: 110px;
        cursor: pointer;

        .product-image-wrapper {
          position: relative;
          width: 110px;
          height: 110px;
          border-radius: $radius-md;
          overflow: hidden;
          margin-bottom: $spacing-xs;
          background: $bg-gray;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }

          .image-placeholder-grid {
            width: 100%;
            height: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: $text-white;
            font-size: $font-sm;
            font-weight: 600;
          }

          .rx-badge {
            position: absolute;
            top: 4px;
            left: 4px;
            background: rgba(0, 0, 0, 0.6);
            color: $text-white;
            font-size: 10px;
            padding: 1px 4px;
            border-radius: $radius-sm;
          }

          .doctor-badge {
            position: absolute;
            bottom: 4px;
            right: 4px;
            background: $primary;
            color: $text-white;
            font-size: 10px;
            padding: 2px 6px;
            border-radius: $radius-sm;
            display: flex;
            align-items: center;
            gap: 2px;

            .el-icon {
              font-size: 10px;
            }
          }
        }

        .product-info-grid {
          .product-name-grid {
            font-size: $font-xs;
            color: $text-primary;
            line-height: 1.4;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            margin-bottom: $spacing-xs;
            min-height: 32px;
          }

          .product-price-row {
            display: flex;
            align-items: center;
            gap: 2px;

            .price-symbol {
              font-size: $font-xs;
              color: $price-red;
            }

            .price-value {
              font-size: $font-md;
              font-weight: 600;
              color: $price-red;
              flex: 1;
            }

            .add-btn-small {
              width: 22px;
              height: 22px;
              background: $accent;
              border-radius: 50%;
              display: flex;
              align-items: center;
              justify-content: center;
              cursor: pointer;
              color: $text-white;

              .el-icon {
                font-size: 14px;
                font-weight: bold;
              }
            }
          }

          .discount-text {
            font-size: 10px;
            color: $text-tertiary;
            margin-top: 2px;
          }
        }
      }
    }

    // 轮播指示器
    .carousel-dots {
      display: flex;
      justify-content: center;
      gap: $spacing-xs;
      margin-top: $spacing-sm;

      .dot {
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background: #D9D9D9;

        &.active {
          width: 12px;
          border-radius: 3px;
          background: $accent;
        }
      }
    }
  }

  // Banner轮播图
  .banner-section {
    margin-bottom: $spacing-md;

    .banner-carousel {
      border-radius: $radius-lg;
      overflow: hidden;

      .banner-slide {
        padding: $spacing-md;

        .banner-content {
          display: flex;
          align-items: center;
          justify-content: space-between;

          .banner-text {
            display: flex;
            flex-direction: column;
            gap: $spacing-sm;

            h3 {
              font-size: $font-xl;
              font-weight: 700;
              color: #E84393;
              line-height: 1.3;
            }

            .banner-btn {
              display: inline-flex;
              align-items: center;
              justify-content: center;
              background: #E84393;
              color: $text-white;
              font-size: $font-sm;
              padding: 4px 16px;
              border-radius: $radius-md;
              width: fit-content;
            }
          }

          img {
            width: 140px;
            height: 100px;
            object-fit: cover;
            border-radius: $radius-md;
          }
        }
      }
    }
  }

  // 快捷专区导航
  .quick-section-nav {
    display: flex;
    gap: $spacing-md;
    overflow-x: auto;
    padding: $spacing-sm 0;
    margin-bottom: $spacing-md;

    &::-webkit-scrollbar {
      display: none;
    }

    .quick-section-item {
      flex-shrink: 0;
      font-size: $font-md;
      color: $text-secondary;
      cursor: pointer;
      padding: $spacing-xs 0;
      position: relative;
      font-weight: 500;

      &.active {
        color: $text-primary;
        font-weight: 600;

        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          height: 3px;
          background: $text-primary;
          border-radius: 2px;
        }
      }
    }
  }

  // 价格筛选标签
  .price-filter-tags {
    display: flex;
    gap: $spacing-sm;
    margin-bottom: $spacing-md;

    .filter-tag {
      font-size: $font-sm;
      color: $text-secondary;
      background: $bg-light;
      padding: 4px 12px;
      border-radius: $radius-md;
      cursor: pointer;
      transition: all 0.2s;

      &.active {
        color: $price-red;
        background: rgba($price-red, 0.1);
        font-weight: 500;
      }
    }
  }

  // 广告横幅
  .ad-banner {
    position: relative;
    border-radius: $radius-lg;
    overflow: hidden;
    margin-bottom: $spacing-md;
    background: linear-gradient(90deg, #1a1a1a 0%, #333333 100%);
    height: 80px;
    display: flex;
    align-items: center;
    padding: 0 $spacing-md;

    .ad-content {
      display: flex;
      flex-direction: column;
      gap: 4px;

      .ad-brand {
        font-size: $font-lg;
        font-weight: 700;
        color: #FF4444;
      }

      .ad-slogan {
        font-size: $font-xl;
        font-weight: 600;
        color: $text-white;
      }
    }

    .ad-label {
      position: absolute;
      top: 4px;
      right: 4px;
      font-size: 10px;
      color: $text-white;
      background: rgba(0, 0, 0, 0.4);
      padding: 1px 4px;
      border-radius: $radius-sm;
    }
  }
}

// 全部商品内容
.products-content {
  display: flex;
  height: calc(100vh - 200px);
  overflow: hidden;
}

// 左侧分类栏
.category-sidebar {
  width: 80px;
  background: $bg-light;
  overflow-y: auto;
  flex-shrink: 0;

  &::-webkit-scrollbar {
    display: none;
  }

  .category-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: $spacing-md $spacing-sm;
    cursor: pointer;
    position: relative;
    transition: all 0.2s;
    min-height: 60px;

    &:hover {
      background: rgba($primary, 0.05);
    }

    &.active {
      background: $bg-white;
      color: $primary;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 3px;
        height: 20px;
        background: $primary;
        border-radius: 0 2px 2px 0;
      }
    }

    .cat-icon {
      font-size: $font-lg;
      margin-bottom: 2px;
    }

    .cat-name {
      font-size: $font-xs;
      text-align: center;
      line-height: 1.2;
    }

    .cat-badge {
      position: absolute;
      top: 8px;
      right: 8px;
      min-width: 14px;
      height: 14px;
      background: $price-red;
      color: $text-white;
      font-size: 10px;
      border-radius: 7px;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0 3px;
    }
  }
}

// 右侧商品区
.product-area {
  flex: 1;
  background: $bg-white;
  overflow-y: auto;
  padding: $spacing-md;

  &::-webkit-scrollbar {
    display: none;
  }
}

// 健康卡推广横幅
.health-card-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(90deg, rgba($primary, 0.08) 0%, rgba($primary, 0.04) 100%);
  border: 1px solid rgba($primary, 0.15);
  border-radius: $radius-md;
  padding: $spacing-md;
  margin-bottom: $spacing-md;

  .banner-left {
    .banner-title {
      .brand {
        font-size: $font-sm;
        font-weight: 600;
        color: $primary;
      }
    }

    .banner-desc {
      font-size: $font-xs;
      color: $text-secondary;
      margin-top: 2px;
    }
  }

  .banner-right {
    display: flex;
    align-items: center;
    gap: 2px;
    color: $primary;
    font-size: $font-sm;
    cursor: pointer;

    .el-icon {
      font-size: $font-xs;
    }
  }
}

// 排序栏
.sort-bar {
  display: flex;
  align-items: center;
  gap: $spacing-lg;
  margin-bottom: $spacing-md;
  padding-bottom: $spacing-sm;
  border-bottom: 1px solid $border-light;

  .sort-item {
    display: flex;
    align-items: center;
    gap: 2px;
    font-size: $font-sm;
    color: $text-secondary;
    cursor: pointer;
    transition: color 0.2s;

    &:hover {
      color: $text-primary;
    }

    &.active {
      color: $primary;
      font-weight: 500;
    }

    .sort-arrow {
      font-size: $font-xs;
    }
  }
}

// 商品列表
.product-list {
  .product-item {
    display: flex;
    gap: $spacing-md;
    padding: $spacing-md 0;
    border-bottom: 1px solid $border-light;
    cursor: pointer;

    &:last-child {
      border-bottom: none;
    }

    .product-image {
      width: 90px;
      height: 90px;
      border-radius: $radius-sm;
      overflow: hidden;
      flex-shrink: 0;
      background: $bg-gray;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .image-placeholder {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: $text-white;
        font-size: $font-lg;
        font-weight: 600;
      }
    }

    .product-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      min-height: 90px;

      .product-tags {
        display: flex;
        gap: $spacing-xs;
        margin-bottom: $spacing-xs;

        .symptom-tag {
          font-size: $font-xs;
          color: $primary;
          background: rgba($primary, 0.1);
          padding: 1px 6px;
          border-radius: $radius-sm;
        }
      }

      .product-name {
        font-size: $font-md;
        color: $text-primary;
        font-weight: 500;
        line-height: 1.4;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .product-spec {
        font-size: $font-xs;
        color: $text-tertiary;
        margin-top: 2px;
      }

      .product-footer {
        display: flex;
        align-items: flex-end;
        justify-content: space-between;
        margin-top: $spacing-xs;

        .price-section {
          .current-price {
            color: $price-red;

            .symbol {
              font-size: $font-xs;
            }

            .value {
              font-size: $font-xl;
              font-weight: 600;
            }
          }

          .cashback-tag {
            font-size: $font-xs;
            color: $warning;
            background: rgba($warning, 0.1);
            padding: 1px 6px;
            border-radius: $radius-sm;
            margin-top: 2px;
            display: inline-block;
          }
        }

        .add-btn {
          width: 28px;
          height: 28px;
          background: $accent;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          color: $text-white;
          transition: all 0.2s;

          &:hover {
            background: $accent-dark;
            transform: scale(1.05);
          }

          &:active {
            transform: scale(0.95);
          }

          .el-icon {
            font-size: $font-md;
            font-weight: bold;
          }
        }
      }
    }
  }
}

// 医生咨询入口
.doctor-entry {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(90deg, rgba($primary, 0.06) 0%, rgba($primary, 0.03) 100%);
  border: 1px solid rgba($primary, 0.12);
  border-radius: $radius-md;
  padding: $spacing-md;
  margin-top: $spacing-md;
  cursor: pointer;

  .doctor-info {
    display: flex;
    align-items: center;
    gap: $spacing-sm;

    .el-icon {
      font-size: $font-lg;
      color: $primary;
    }

    .doctor-text {
      font-size: $font-md;
      color: $text-primary;
      font-weight: 500;
    }

    .doctor-badge {
      font-size: $font-xs;
      color: $text-white;
      background: $primary;
      padding: 1px 6px;
      border-radius: $radius-sm;
    }
  }

  .el-icon {
    color: $text-tertiary;
  }
}

// 底部购物车栏
.cart-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: $bg-white;
  border-top: 1px solid $border-light;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $spacing-md;
  z-index: 99;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.04);

  .cart-left {
    display: flex;
    align-items: center;
    gap: $spacing-lg;

    .cart-icon-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 4px;
      cursor: pointer;

      .icon-circle {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        background: $bg-gray;
        display: flex;
        align-items: center;
        justify-content: center;
        color: $text-secondary;
        transition: all 0.2s;

        .el-icon {
          font-size: $font-lg;
        }

        &:hover {
          background: rgba($primary, 0.1);
          color: $primary;
        }

        &.cart-circle {
          position: relative;
          background: rgba($primary, 0.1);
          color: $primary;

          .cart-badge {
            position: absolute;
            top: -4px;
            right: -4px;
            min-width: 16px;
            height: 16px;
            background: $price-red;
            color: $text-white;
            font-size: 10px;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 0 4px;
          }
        }
      }

      .icon-label {
        font-size: 10px;
        color: $text-secondary;
      }
    }
  }

  .cart-center {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;

    .delivery-fee {
      font-size: $font-sm;
      color: $text-secondary;
    }
  }

  .cart-right {
    .min-order {
      font-size: $font-md;
      font-weight: 500;
      color: $text-tertiary;
    }
  }
}

// 商家内容
.merchant-content {
  padding: $spacing-md;

  .merchant-section {
    background: $bg-white;
    border-radius: $radius-lg;
    padding: $spacing-md;
    margin-bottom: $spacing-md;

    .section-item {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      padding: $spacing-sm 0;
      cursor: pointer;

      &:first-child {
        padding-top: 0;
      }

      &:last-child {
        padding-bottom: 0;
      }

      &:not(:last-child) {
        border-bottom: 1px solid $border-light;
      }

      .section-icon {
        font-size: $font-lg;
        color: $text-tertiary;
      }

      .section-content {
        flex: 1;

        .section-text {
          font-size: $font-sm;
          color: $text-primary;
          line-height: 1.5;

          .highlight-tag {
            background: rgba($primary, 0.1);
            color: $primary;
            padding: 1px 4px;
            border-radius: $radius-sm;
            font-size: $font-xs;
          }
        }
      }

      .section-actions {
        display: flex;
        gap: $spacing-md;

        .action-icon {
          font-size: $font-lg;
          color: $text-secondary;
          cursor: pointer;

          &:hover {
            color: $primary;
          }
        }
      }

      .arrow-icon {
        font-size: $font-md;
        color: $text-tertiary;
      }

      .service-tags {
        .service-tag {
          background: rgba($primary, 0.1);
          color: $primary;
          font-size: $font-xs;
          padding: 2px 8px;
          border-radius: $radius-sm;
        }
      }
    }

    .promo-list {
      .promo-item {
        display: flex;
        align-items: center;
        gap: $spacing-sm;
        padding: $spacing-xs 0;

        .promo-label {
          width: 18px;
          height: 18px;
          background: $price-red;
          color: $text-white;
          font-size: 10px;
          border-radius: $radius-sm;
          display: flex;
          align-items: center;
          justify-content: center;
          font-weight: 600;

          &.discount {
            background: $warning;
          }

          &.delivery {
            background: $info;
          }
        }

        .promo-text {
          font-size: $font-sm;
          color: $text-primary;
        }
      }
    }
  }

  .merchant-notice {
    display: flex;
    gap: $spacing-sm;
    background: $bg-white;
    border-radius: $radius-lg;
    padding: $spacing-md;

    .notice-icon {
      font-size: $font-lg;
      color: $primary;
      flex-shrink: 0;
    }

    .notice-text {
      font-size: $font-sm;
      color: $text-secondary;
      line-height: 1.5;
    }
  }
}
</style>