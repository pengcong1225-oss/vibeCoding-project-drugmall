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
                    <span class="price-value">
                      <span class="integer">{{ Math.floor(product.price) }}</span>
                      <span class="decimal">.{{ (product.price % 1).toFixed(2).slice(2) }}</span>
                    </span>
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
                    <span class="price-value">
                      <span class="integer">{{ Math.floor(product.price) }}</span>
                      <span class="decimal">.{{ (product.price % 1).toFixed(2).slice(2) }}</span>
                    </span>
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
                    <span class="price-value">
                      <span class="integer">{{ Math.floor(product.price) }}</span>
                      <span class="decimal">.{{ (product.price % 1).toFixed(2).slice(2) }}</span>
                    </span>
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

          <!-- 推荐商品瀑布流 -->
          <div class="section-block waterfall-section">
            <div class="section-header">
              <h3 class="section-title">推荐商品</h3>
            </div>
            <div class="waterfall-grid">
              <div
                v-for="product in waterfallProducts"
                :key="product.id"
                class="waterfall-item"
                @click="goToDrugDetail(product.id)"
              >
                <div class="waterfall-image-wrapper">
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
                <div class="waterfall-info">
                  <div class="waterfall-name">{{ product.name }}</div>
                  <div v-if="product.subtitle" class="waterfall-subtitle">{{ product.subtitle }}</div>
                  <div class="waterfall-price-row">
                    <span class="price-symbol">¥</span>
                    <span class="price-value">{{ product.price }}</span>
                    <div class="add-btn-small" @click.stop="addToCart(product)">
                      <el-icon><Plus /></el-icon>
                    </div>
                  </div>
                  <div v-if="product.discount" class="discount-text">已优惠¥{{ product.discount }}</div>
                  <div v-if="product.sales" class="sales-text">{{ product.sales }}</div>
                </div>
              </div>
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
        <div class="products-content-v3">
          <!-- 健康卡横幅 -->
          <div class="health-card-banner">
            <div class="health-card-left">
              <span class="health-card-tag">美团·健康卡</span>
              <span class="health-card-title">开通后预计<span class="highlight">可省34元/月</span></span>
              <span class="health-card-desc">享购药返现15%、健康专享价等5大权益</span>
            </div>
            <span class="health-card-link">了解更多 <el-icon><ArrowRight /></el-icon></span>
          </div>

          <!-- 主体布局：左侧分类 + 右侧商品 -->
          <div class="products-layout">
            <!-- 左侧分类导航 -->
            <div class="category-sidebar">
              <div
                v-for="cat in sidebarCategories"
                :key="cat.id"
                :class="['sidebar-cat-item', { active: activeCategory === cat.id }]"
                @click="handleCategoryChange(cat.id)"
              >
                <div class="cat-icon" v-if="cat.icon">
                  <el-icon><component :is="cat.icon" /></el-icon>
                </div>
                <span class="cat-label">{{ cat.name }}</span>
                <span class="cat-badge" v-if="cat.badge">{{ cat.badge }}</span>
              </div>
            </div>

            <!-- 右侧商品列表 -->
            <div class="product-list-area">
              <!-- 子分类滚动指示器 -->
              <div class="category-scroll-indicator">
                <div
                  v-for="subCat in currentSubCategories"
                  :key="subCat.key"
                  :class="['sub-cat-item', { active: activeSubCategory === subCat.key }]"
                  @click="activeSubCategory = subCat.key"
                >
                  {{ subCat.label }}
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
                  class="product-list-item"
                  @click="goToDrugDetail(product.id)"
                >
                  <div class="product-item-image">
                    <img v-if="product.image" :src="product.image" :alt="product.name" />
                    <div v-else class="image-placeholder-list" :style="{ backgroundColor: product.imageColor || '#E8F5E9' }">
                      <span>{{ product.imageText || product.name?.slice(0, 2) }}</span>
                    </div>
                  </div>
                  <div class="product-item-info">
                    <div class="product-item-tags" v-if="product.tags && product.tags.length">
                      <span
                        v-for="(tag, idx) in product.tags.slice(0, 3)"
                        :key="idx"
                        class="product-tag"
                      >{{ tag }}</span>
                    </div>
                    <div class="product-item-name" :title="product.name">{{ product.name }}</div>
                    <div class="product-item-spec" v-if="product.specification">{{ product.specification }}</div>
                    <div class="product-item-bottom">
                      <div class="product-item-price-row">
                        <span class="price-symbol">¥</span>
                        <span class="price-value">
                      <span class="integer">{{ Math.floor(product.price) }}</span>
                      <span class="decimal">.{{ (product.price % 1).toFixed(2).slice(2) }}</span>
                    </span>
                        <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
                      </div>
                      <div class="product-item-actions">
                        <div v-if="product.cashback" class="cashback-tag">
                          <el-icon><ArrowDown /></el-icon>
                          最高返现{{ product.cashback }}元
                        </div>
                        <div class="add-cart-btn" @click.stop="addToCart(product)">
                          <el-icon><Plus /></el-icon>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- 商家内容 -->
      <template v-if="activeTab === 'merchant'">
        <div class="merchant-content-v2">
          <!-- 商家概览卡片 -->
          <div class="merchant-overview-card">
            <div class="overview-header">
              <h3 class="overview-title">商家信息</h3>
              <div class="overview-rating">
                <el-icon><StarFilled /></el-icon>
                <span>4.8分</span>
              </div>
            </div>
            <div class="overview-stats">
              <div class="stat-item">
                <span class="stat-value">26</span>
                <span class="stat-label">条评价</span>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                <span class="stat-value">100+</span>
                <span class="stat-label">月售</span>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                <span class="stat-value">15min</span>
                <span class="stat-label">平均配送</span>
              </div>
            </div>
          </div>

          <!-- 地址与联系 -->
          <div class="merchant-info-card">
            <div class="info-item" @click="openMap">
              <div class="info-left">
                <div class="info-icon location">
                  <el-icon><Location /></el-icon>
                </div>
                <div class="info-content">
                  <div class="info-title">商家地址</div>
                  <div class="info-desc">{{ store?.address || '北京市朝阳区建国路88号' }}</div>
                </div>
              </div>
              <el-icon class="arrow-icon"><ArrowRight /></el-icon>
            </div>
            <div class="info-divider"></div>
            <div class="info-item" @click="callMerchant">
              <div class="info-left">
                <div class="info-icon phone">
                  <el-icon><Phone /></el-icon>
                </div>
                <div class="info-content">
                  <div class="info-title">联系电话</div>
                  <div class="info-desc">020-8888-8888</div>
                </div>
              </div>
              <el-icon class="arrow-icon"><ArrowRight /></el-icon>
            </div>
          </div>

          <!-- 配送信息 -->
          <div class="merchant-info-card">
            <div class="info-item">
              <div class="info-left">
                <div class="info-icon delivery">
                  <el-icon><Van /></el-icon>
                </div>
                <div class="info-content">
                  <div class="info-title">配送服务</div>
                  <div class="info-desc">美团专送 · 约15分钟送达</div>
                </div>
              </div>
            </div>
            <div class="info-divider"></div>
            <div class="info-item">
              <div class="info-left">
                <div class="info-icon time">
                  <el-icon><Clock /></el-icon>
                </div>
                <div class="info-content">
                  <div class="info-title">营业时间</div>
                  <div class="info-desc">07:45 - 21:10</div>
                </div>
              </div>
            </div>
            <div class="info-divider"></div>
            <div class="info-item">
              <div class="info-left">
                <div class="info-icon service">
                  <el-icon><Service /></el-icon>
                </div>
                <div class="info-content">
                  <div class="info-title">商家服务</div>
                  <div class="info-tags">
                    <span class="mini-tag">到店自取</span>
                    <span class="mini-tag">售后无忧</span>
                    <span class="mini-tag">极速退款</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 优惠活动 -->
          <div class="merchant-info-card">
            <div class="card-header">
              <h4 class="card-title">优惠活动</h4>
            </div>
            <div class="promo-list-v2">
              <div class="promo-item-v2">
                <span class="promo-tag red">满减</span>
                <span class="promo-desc">满49减5</span>
              </div>
              <div class="promo-item-v2">
                <span class="promo-tag yellow">领券</span>
                <span class="promo-desc">5元*2张券</span>
              </div>
              <div class="promo-item-v2">
                <span class="promo-tag green">配送</span>
                <span class="promo-desc">美团专送约15分钟</span>
              </div>
            </div>
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
          <span class="icon-label">去咨询</span>
        </div>
        <div class="cart-icon-item cart-icon-wrapper" @click="showCartPopup = true">
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
      <div class="cart-right" @click="goToCheckout">
        <span class="min-order" :class="{ active: cartStore.totalCount > 0 }">¥20起送</span>
      </div>
    </div>

    <!-- 购物车弹出层 -->
    <div v-if="showCartPopup" class="cart-popup-overlay" @click="showCartPopup = false">
      <div class="cart-popup" @click.stop>
        <div class="cart-popup-header">
          <h3>已选商品</h3>
          <span class="clear-btn" @click="clearCart">
            <el-icon><Delete /></el-icon>
            清空
          </span>
        </div>
        <div class="cart-popup-list">
          <div v-for="item in cartStore.items" :key="item.drugId" class="cart-popup-item">
            <div class="item-info">
              <span class="item-name">{{ item.name }}</span>
              <span class="item-spec">{{ item.specification }}</span>
            </div>
            <div class="item-price">¥{{ item.price }}</div>
            <div class="item-quantity">
              <span class="qty-btn" @click="updateCartItem(item.drugId, item.quantity - 1)">-</span>
              <span class="qty-value">{{ item.quantity }}</span>
              <span class="qty-btn" @click="updateCartItem(item.drugId, item.quantity + 1)">+</span>
            </div>
          </div>
        </div>
        <div class="cart-popup-footer">
          <div class="total-info">
            <span class="total-label">合计:</span>
            <span class="total-price">¥{{ cartStore.totalPrice?.toFixed(2) }}</span>
          </div>
          <button class="checkout-btn" @click="goToCheckout">去结算</button>
        </div>
      </div>
    </div>

    <!-- 问医生悬浮按钮 -->
    <div class="ask-doctor-fab" @click="handleAskDoctor">
      <div class="fab-icon">
        <el-icon><ChatDotRound /></el-icon>
      </div>
      <span class="fab-text">问医生</span>
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
  InfoFilled,
  Delete,
  StarFilled,
  Warning,
  WindPower,
  Sunny,
  Food,
  View
} from '@element-plus/icons-vue'
import { getStoreDetail, getStoreDrugs, type StoreInfo, type StoreDrug } from '@/api/modules/store'
import { useCartStore } from '@/stores/cart'
import { ROUTES, getStoreDrugRoute, getInquiryCheckoutRoute } from '@/constants/routes'

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
const showCartPopup = ref(false)
const activeSubCategory = ref('all')

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

// 左侧分类导航数据
const sidebarCategories = [
  { id: 'recommend', name: '推荐', icon: 'Star' },
  { id: 'allergy', name: '过敏季', icon: 'Warning' },
  { id: 'activity', name: '活动', icon: 'Ticket' },
  { id: 'health', name: '健康卡', icon: 'FirstAidKit' },
  { id: 'frequent', name: '常买', icon: 'ShoppingCart' },
  { id: 'other', name: '其他', icon: 'More', badge: '1' },
  { id: 'cold', name: '感冒呼吸系统', icon: 'WindPower' },
  { id: 'asthma', name: '咳喘用药', icon: 'Cloudy' },
  { id: 'clear', name: '清热解毒', icon: 'Sunny' },
  { id: 'stomach', name: '肠胃不适', icon: 'Food' },
  { id: 'sex', name: '性福生活', icon: 'Love' },
  { id: 'five', name: '五官用药', icon: 'View' }
]

// 子分类数据
const subCategoriesMap: Record<string, { key: string; label: string }[]> = {
  cold: [
    { key: 'all', label: '全部' },
    { key: 'ganmao', label: '感冒' },
    { key: 'fashao', label: '发烧' },
    { key: 'kesou', label: '咳嗽' },
    { key: 'bihou', label: '鼻喉' }
  ],
  stomach: [
    { key: 'all', label: '全部' },
    { key: 'weitong', label: '胃痛' },
    { key: 'fuxie', label: '腹泻' },
    { key: 'xiaohua', label: '消化不良' },
    { key: 'bianmi', label: '便秘' }
  ],
  five: [
    { key: 'all', label: '全部' },
    { key: 'yan', label: '眼科' },
    { key: 'er', label: '耳科' },
    { key: 'bi', label: '鼻科' },
    { key: 'hou', label: '喉科' }
  ],
  sex: [
    { key: 'all', label: '全部' },
    { key: 'biyun', label: '避孕' },
    { key: 'zhuangyang', label: '壮阳' },
    { key: 'fuke', label: '妇科' }
  ],
  clear: [
    { key: 'all', label: '全部' },
    { key: 'banlan', label: '板蓝根' },
    { key: 'xiaoyan', label: '消炎' },
    { key: 'qingre', label: '清热' }
  ],
  default: [
    { key: 'all', label: '全部' }
  ]
}

// 当前子分类
const currentSubCategories = computed(() => {
  return subCategoriesMap[activeCategory.value] || subCategoriesMap.default
})

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
    name: '[江中]复方草珊瑚含片(不含蔗糖)0.44g*12片*4板/盒',
    specification: '0.44g*12片*4板/盒',
    price: 7.80,
    originalPrice: 12.00,
    stock: 200,
    sales: 890,
    isRx: false,
    image: 'https://img.alicdn.com/imgextra/i1/2928278100/O1CN01wKJPxT1VubCqX5K1P_!!2928278100.jpg',
    tags: ['清利咽喉', '急性咽喉炎', '咽喉肿痛'],
    cashback: 1.17
  },
  {
    id: '3',
    name: '[碧凯]保妇康栓1.74g*8粒/盒',
    specification: '1.74g*8粒/盒',
    price: 40.80,
    originalPrice: 52.00,
    stock: 80,
    sales: 650,
    isRx: false,
    image: 'https://img.alicdn.com/imgextra/i3/2928278100/O1CN01YqW1ZU1VubCqX5K1Q_!!2928278100.jpg',
    tags: ['保妇康栓选碧凯', '碧凯牌保妇康栓'],
    cashback: 6.12
  },
  {
    id: '4',
    name: '[嘉应]双料喉风散2.2g/瓶/盒',
    specification: '2.2g/瓶/盒',
    price: 18.50,
    originalPrice: 25.00,
    stock: 150,
    sales: 420,
    isRx: false,
    image: 'https://img.alicdn.com/imgextra/i2/2928278100/O1CN01jJpZ1V1VubCqX5K1R_!!2928278100.jpg',
    tags: ['口腔糜烂', '消肿利咽', '咽喉肿痛'],
    cashback: 2.78
  },
  {
    id: '5',
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
    id: '6',
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
  },
  {
    id: '7',
    name: '[三九]感冒灵颗粒10g*9袋/盒',
    specification: '10g*9袋/盒',
    price: 12.80,
    originalPrice: 18.00,
    stock: 300,
    sales: 3500,
    isRx: false,
    image: 'https://img.alicdn.com/imgextra/i1/2928278100/O1CN01wKJPxT1VubCqX5K1P_!!2928278100.jpg',
    tags: ['感冒', '头痛', '发热'],
    cashback: 1.92
  },
  {
    id: '8',
    name: '[白云山]板蓝根颗粒10g*20袋/包',
    specification: '10g*20袋/包',
    price: 15.50,
    originalPrice: 20.00,
    stock: 250,
    sales: 2800,
    isRx: false,
    image: 'https://img.alicdn.com/imgextra/i4/2928278100/O1CN01mJpZ1V1VubCqX5K1T_!!2928278100.jpg',
    tags: ['清热解毒', '凉血利咽'],
    cashback: 2.33
  },
  {
    id: '9',
    name: '[京都念慈菴]蜜炼川贝枇杷膏300ml/瓶',
    specification: '300ml/瓶',
    price: 45.00,
    originalPrice: 58.00,
    stock: 120,
    sales: 1500,
    isRx: false,
    image: 'https://img.alicdn.com/imgextra/i4/2928278100/O1CN01mJpZ1V1VubCqX5K1T_!!2928278100.jpg',
    tags: ['咳嗽', '咽喉不适'],
    cashback: 6.75
  },
  {
    id: '10',
    name: '[新乐敦]复方门冬维甘滴眼液',
    specification: '13ml/瓶',
    price: 24.00,
    originalPrice: 32.00,
    stock: 180,
    sales: 960,
    isRx: false,
    image: 'https://img.alicdn.com/imgextra/i1/2928278100/O1CN01KqW1ZU1VubCqX5K1S_!!2928278100.jpg',
    tags: ['眼疲劳', '干涩'],
    cashback: 3.60
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

// 瀑布流推荐商品
const waterfallProducts = ref([
  {
    id: 'w1',
    name: '[养寿堂]强力枇杷露250ml/瓶/盒',
    subtitle: '支气管炎咳嗽',
    price: 28,
    discount: 2,
    image: 'https://img.alicdn.com/imgextra/i4/2928278100/O1CN01mq1p5s1VubCqX5LEJ_!!2928278100.jpg',
    isRx: false,
    sales: '优惠仅剩2件'
  },
  {
    id: 'w2',
    name: '[三金]西瓜霜润喉片0.6g*36片/盒',
    subtitle: '清音利咽 声音嘶哑',
    price: 9.05,
    image: 'https://img.alicdn.com/imgextra/i1/2928278100/O1CN01KqW1ZU1VubCqX5K1S_!!2928278100.jpg',
    isRx: false,
    sales: '月售1'
  },
  {
    id: 'w3',
    name: '[太极]川贝清肺糖浆180ml/瓶/盒',
    subtitle: '咽痛 干咳 咽干',
    price: 23.5,
    image: 'https://img.alicdn.com/imgextra/i3/2928278100/O1CN01YqW1ZU1VubCqX5K1Q_!!2928278100.jpg',
    isRx: false,
    sales: '月售1'
  },
  {
    id: 'w4',
    name: '[京都念慈菴]枇杷糖45g(2.5g*18粒)/盒',
    subtitle: '蓝帽认证 甘草提取液 清咽',
    price: 14.6,
    image: 'https://img.alicdn.com/imgextra/i2/2928278100/O1CN01jJpZ1V1VubCqX5K1R_!!2928278100.jpg',
    isRx: false,
    doctor: true,
    sales: '已售11'
  },
  {
    id: 'w5',
    name: '[三九]感冒灵颗粒10g*9袋/盒',
    subtitle: '解热镇痛 感冒头痛',
    price: 12.8,
    image: 'https://img.alicdn.com/imgextra/i1/2928278100/O1CN01wKJPxT1VubCqX5K1P_!!2928278100.jpg',
    isRx: false,
    sales: '月售200+'
  },
  {
    id: 'w6',
    name: '[白云山]板蓝根颗粒10g*20袋/包',
    subtitle: '清热解毒 凉血利咽',
    price: 15.5,
    image: 'https://img.alicdn.com/imgextra/i4/2928278100/O1CN01mJpZ1V1VubCqX5K1T_!!2928278100.jpg',
    isRx: false,
    sales: '月售500+'
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
  if (activeCategory.value !== 'all' && activeCategory.value !== 'recommend') {
    result = result.filter(p => {
      switch (activeCategory.value) {
        case 'cold':
          return p.tags?.includes('感冒') || p.tags?.includes('咳嗽') || p.name?.includes('感冒') || p.name?.includes('咳')
        case 'stomach':
          return p.tags?.includes('肠胃') || p.tags?.includes('腹泻') || p.name?.includes('肠胃') || p.name?.includes('肠炎') || p.name?.includes('保妇')
        case 'sex':
          return p.tags?.includes('性福') || p.name?.includes('避孕')
        case 'five':
          return p.tags?.includes('五官') || p.name?.includes('眼') || p.name?.includes('鼻') || p.name?.includes('喉') || p.name?.includes('复方')
        case 'clear':
          return p.tags?.includes('清热') || p.name?.includes('清热') || p.name?.includes('解毒') || p.name?.includes('草珊瑚')
        case 'allergy':
          return p.tags?.includes('过敏') || p.name?.includes('过敏')
        case 'asthma':
          return p.tags?.includes('咳喘') || p.name?.includes('咳喘') || p.name?.includes('哮喘')
        case 'health':
          return true
        case 'frequent':
          return (p.sales || 0) > 500
        case 'activity':
          return p.originalPrice && p.originalPrice > p.price
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
    ElMessage.error('加载店铺信息失败，请稍后重试')
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
      ElMessage.warning('暂无商品')
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败，请稍后重试')
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
  activeSubCategory.value = 'all'
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

const goToDrugDetail = (drugId: string) => {
  const storeId = route.params.id as string
  router.push(getStoreDrugRoute(storeId, drugId))
}

const goToCart = () => {
  router.push(ROUTES.CART)
}

const goToCheckout = () => {
  if (cartStore.totalCount === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  showCartPopup.value = false
  router.push(getInquiryCheckoutRoute(0))
}

const updateCartItem = async (drugId: string, quantity: number) => {
  if (quantity <= 0) {
    await cartStore.removeItem(drugId)
  } else {
    await cartStore.updateQuantity(drugId, quantity)
  }
}

const clearCart = async () => {
  await cartStore.clearCart()
  ElMessage.success('购物车已清空')
}

const goToConsult = () => {
  router.push(ROUTES.INQUIRY)
}

// 问医生
const handleAskDoctor = () => {
  ElMessage.info('问医生功能开发中')
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
            word-break: break-all;
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

                  // 整数和小数部分区分
                  .integer {
                    font-size: $font-lg;
                  }
                  .decimal {
                    font-size: $font-xs;
                  }
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

  // 瀑布流布局
  .waterfall-section {
    .waterfall-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: $spacing-md;

      .waterfall-item {
        cursor: pointer;

        .waterfall-image-wrapper {
          position: relative;
          width: 100%;
          aspect-ratio: 1;
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

        .waterfall-info {
          .waterfall-name {
            font-size: $font-xs;
            color: $text-primary;
            line-height: 1.4;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            margin-bottom: 2px;
            word-break: break-all;
          }

          .waterfall-subtitle {
            font-size: 10px;
            color: $text-tertiary;
            margin-bottom: $spacing-xs;
            line-height: 1.3;
          }

          .waterfall-price-row {
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

              .integer {
                font-size: $font-lg;
              }
              .decimal {
                font-size: $font-xs;
              }
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

          .sales-text {
            font-size: 10px;
            color: $text-tertiary;
            margin-top: 2px;
          }
        }
      }
    }
  }
}

// 全部商品内容V3
.products-content-v3 {
  background: $bg-gray;
  min-height: calc(100vh - 200px);

  // 健康卡横幅
  .health-card-banner {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
    padding: 12px 16px;
    margin: 0 12px 12px;
    border-radius: 8px;

    .health-card-left {
      display: flex;
      flex-direction: column;
      gap: 4px;

      .health-card-tag {
        font-size: 12px;
        color: #FFD700;
        font-weight: 500;
      }

      .health-card-title {
        font-size: 14px;
        color: #fff;
        font-weight: 500;

        .highlight {
          color: #FFD700;
          font-weight: 600;
        }
      }

      .health-card-desc {
        font-size: 11px;
        color: rgba(255, 255, 255, 0.7);
      }
    }

    .health-card-link {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;
      color: #FFD700;
      cursor: pointer;

      .el-icon {
        font-size: 12px;
      }
    }
  }

  // 主体布局
  .products-layout {
    display: flex;
    gap: 12px;
    padding: 0 12px 12px;

    // 左侧分类导航
    .category-sidebar {
      width: 80px;
      flex-shrink: 0;
      background: $bg-white;
      border-radius: 8px;
      padding: 8px 0;
      max-height: calc(100vh - 280px);
      overflow-y: auto;

      &::-webkit-scrollbar {
        display: none;
      }

      .sidebar-cat-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 4px;
        padding: 10px 6px;
        cursor: pointer;
        transition: all 0.2s;
        position: relative;

        .cat-icon {
          width: 24px;
          height: 24px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: $text-secondary;

          .el-icon {
            font-size: 16px;
          }
        }

        .cat-label {
          font-size: 11px;
          color: $text-secondary;
          text-align: center;
          line-height: 1.2;
        }

        .cat-badge {
          position: absolute;
          top: 6px;
          right: 6px;
          width: 14px;
          height: 14px;
          background: $price-red;
          color: #fff;
          font-size: 10px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        &.active {
          background: rgba($primary, 0.08);
          border-radius: 6px;

          .cat-icon {
            color: $primary;
          }

          .cat-label {
            color: $primary;
            font-weight: 500;
          }
        }

        &:hover:not(.active) {
          background: $bg-light;
        }
      }
    }

    // 右侧商品列表区域
    .product-list-area {
      flex: 1;
      min-width: 0;

      // 分类滚动指示器
      .category-scroll-indicator {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 8px 0;
        margin-bottom: 8px;
        overflow-x: auto;
        scrollbar-width: none;
        -ms-overflow-style: none;

        &::-webkit-scrollbar {
          display: none;
        }

        .sub-cat-item {
          flex-shrink: 0;
          padding: 4px 12px;
          font-size: 12px;
          color: $text-secondary;
          background: $bg-white;
          border-radius: $radius-full;
          cursor: pointer;
          transition: all 0.2s;
          border: 1px solid $border-light;

          &:hover {
            color: $primary;
            border-color: $primary;
          }

          &.active {
            color: $text-white;
            background: $primary;
            border-color: $primary;
          }
        }
      }

      // 排序栏
      .sort-bar {
        display: flex;
        align-items: center;
        gap: 20px;
        padding: 10px 0;
        margin-bottom: 8px;

        .sort-item {
          display: flex;
          align-items: center;
          gap: 2px;
          font-size: 13px;
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
            font-size: 12px;
          }
        }
      }

      // 商品列表
      .product-list {
        display: flex;
        flex-direction: column;
        gap: 12px;

        .product-list-item {
          display: flex;
          gap: 12px;
          background: $bg-white;
          border-radius: 8px;
          padding: 12px;
          cursor: pointer;
          transition: all 0.2s;

          &:hover {
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
          }

          .product-item-image {
            width: 90px;
            height: 90px;
            flex-shrink: 0;
            border-radius: 6px;
            overflow: hidden;
            background: $bg-light;

            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }

            .image-placeholder-list {
              width: 100%;
              height: 100%;
              display: flex;
              align-items: center;
              justify-content: center;
              color: $text-tertiary;
              font-size: 14px;
              font-weight: 500;
            }
          }

          .product-item-info {
            flex: 1;
            min-width: 0;
            display: flex;
            flex-direction: column;
            justify-content: space-between;

            .product-item-tags {
              display: flex;
              flex-wrap: wrap;
              gap: 4px;
              margin-bottom: 4px;

              .product-tag {
                font-size: 10px;
                color: $primary;
                background: rgba($primary, 0.08);
                padding: 2px 6px;
                border-radius: 4px;
                border: 1px solid rgba($primary, 0.2);
              }
            }

            .product-item-name {
              font-size: 14px;
              color: $text-primary;
              font-weight: 500;
              line-height: 1.4;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
              margin-bottom: 4px;
              word-break: break-all;

              // 药品名称过长时显示完整名称的tooltip
              &[title] {
                cursor: help;
              }
            }

            .product-item-spec {
              font-size: 12px;
              color: $text-tertiary;
              margin-bottom: 8px;
            }

            .product-item-bottom {
              display: flex;
              align-items: flex-end;
              justify-content: space-between;

              .product-item-price-row {
                display: flex;
                align-items: baseline;
                gap: 2px;

                .price-symbol {
                  font-size: 12px;
                  color: $price-red;
                }

                .price-value {
                  font-size: 20px;
                  font-weight: 600;
                  color: $price-red;
                }

                .original-price {
                  font-size: 12px;
                  color: $text-tertiary;
                  text-decoration: line-through;
                  margin-left: 4px;
                }
              }

              .product-item-actions {
                display: flex;
                align-items: center;
                gap: 8px;

                .cashback-tag {
                  display: flex;
                  align-items: center;
                  gap: 2px;
                  font-size: 10px;
                  color: $warning;
                  background: rgba($warning, 0.08);
                  padding: 2px 6px;
                  border-radius: 4px;

                  .el-icon {
                    font-size: 10px;
                  }
                }

                .add-cart-btn {
                  width: 28px;
                  height: 28px;
                  background: $accent;
                  border-radius: 50%;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  cursor: pointer;
                  color: #fff;
                  transition: all 0.2s;

                  &:hover {
                    background: $accent-dark;
                    transform: scale(1.05);
                  }

                  &:active {
                    transform: scale(0.95);
                  }

                  .el-icon {
                    font-size: 16px;
                    font-weight: bold;
                  }
                }
              }
            }
          }
        }
      }
    }
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

      &.active {
        color: $primary;
        font-weight: 600;
      }
    }
  }
}

// 购物车弹出层
.cart-popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 200;
  display: flex;
  align-items: flex-end;
}

.cart-popup {
  width: 100%;
  max-height: 70vh;
  background: $bg-white;
  border-radius: $radius-lg $radius-lg 0 0;
  display: flex;
  flex-direction: column;

  .cart-popup-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $spacing-md $spacing-lg;
    border-bottom: 1px solid $border-light;

    h3 {
      font-size: $font-md;
      font-weight: 600;
      color: $text-primary;
    }

    .clear-btn {
      display: flex;
      align-items: center;
      gap: $spacing-xs;
      font-size: $font-sm;
      color: $text-tertiary;
      cursor: pointer;

      .el-icon {
        font-size: $font-sm;
      }
    }
  }

  .cart-popup-list {
    flex: 1;
    overflow-y: auto;
    padding: $spacing-md $spacing-lg;

    .cart-popup-item {
      display: flex;
      align-items: center;
      gap: $spacing-md;
      padding: $spacing-sm 0;
      border-bottom: 1px solid $border-light;

      &:last-child {
        border-bottom: none;
      }

      .item-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 2px;

        .item-name {
          font-size: $font-sm;
          color: $text-primary;
          font-weight: 500;
        }

        .item-spec {
          font-size: $font-xs;
          color: $text-tertiary;
        }
      }

      .item-price {
        font-size: $font-sm;
        color: $price-red;
        font-weight: 500;
      }

      .item-quantity {
        display: flex;
        align-items: center;
        gap: $spacing-sm;

        .qty-btn {
          width: 24px;
          height: 24px;
          border-radius: 50%;
          background: $bg-gray;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          font-size: $font-md;
          color: $text-primary;
          user-select: none;
        }

        .qty-value {
          font-size: $font-sm;
          color: $text-primary;
          min-width: 20px;
          text-align: center;
        }
      }
    }
  }

  .cart-popup-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $spacing-md $spacing-lg;
    border-top: 1px solid $border-light;
    background: $bg-white;

    .total-info {
      display: flex;
      align-items: center;
      gap: $spacing-xs;

      .total-label {
        font-size: $font-sm;
        color: $text-secondary;
      }

      .total-price {
        font-size: $font-lg;
        font-weight: 600;
        color: $price-red;
      }
    }

    .checkout-btn {
      background: $primary;
      color: $text-white;
      border: none;
      padding: $spacing-sm $spacing-xl;
      border-radius: $radius-full;
      font-size: $font-md;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        background: $primary-dark;
      }
    }
  }
}

// 商家内容V2
.merchant-content-v2 {
  padding: $spacing-md;
  background: $bg-gray;
  min-height: calc(100vh - 200px);

  .merchant-overview-card {
    background: $bg-white;
    border-radius: $radius-lg;
    padding: $spacing-lg;
    margin-bottom: $spacing-md;

    .overview-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: $spacing-lg;

      .overview-title {
        font-size: $font-lg;
        font-weight: 600;
        color: $text-primary;
      }

      .overview-rating {
        display: flex;
        align-items: center;
        gap: $spacing-xs;
        font-size: $font-md;
        color: $warning;
        font-weight: 600;

        .el-icon {
          font-size: $font-lg;
        }
      }
    }

    .overview-stats {
      display: flex;
      align-items: center;
      justify-content: space-around;

      .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 4px;

        .stat-value {
          font-size: $font-xl;
          font-weight: 700;
          color: $text-primary;
        }

        .stat-label {
          font-size: $font-xs;
          color: $text-tertiary;
        }
      }

      .stat-divider {
        width: 1px;
        height: 30px;
        background: $border-light;
      }
    }
  }

  .merchant-info-card {
    background: $bg-white;
    border-radius: $radius-lg;
    padding: $spacing-md $spacing-lg;
    margin-bottom: $spacing-md;

    .card-header {
      margin-bottom: $spacing-md;

      .card-title {
        font-size: $font-md;
        font-weight: 600;
        color: $text-primary;
      }
    }

    .info-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: $spacing-sm 0;
      cursor: pointer;

      .info-left {
        display: flex;
        align-items: center;
        gap: $spacing-sm;

        .info-icon {
          width: 36px;
          height: 36px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;

          &.location {
            background: rgba($info, 0.1);
            color: $info;
          }

          &.phone {
            background: rgba($primary, 0.1);
            color: $primary;
          }

          &.delivery {
            background: rgba($warning, 0.1);
            color: $warning;
          }

          &.time {
            background: rgba($text-secondary, 0.1);
            color: $text-secondary;
          }

          &.service {
            background: rgba($price-red, 0.1);
            color: $price-red;
          }

          .el-icon {
            font-size: $font-md;
          }
        }

        .info-content {
          .info-title {
            font-size: $font-sm;
            color: $text-primary;
            font-weight: 500;
            margin-bottom: 2px;
          }

          .info-desc {
            font-size: $font-xs;
            color: $text-tertiary;
          }

          .info-tags {
            display: flex;
            gap: $spacing-xs;
            margin-top: 4px;

            .mini-tag {
              font-size: 10px;
              color: $primary;
              background: rgba($primary, 0.1);
              padding: 2px 8px;
              border-radius: $radius-sm;
            }
          }
        }
      }

      .arrow-icon {
        font-size: $font-md;
        color: $text-tertiary;
      }
    }

    .info-divider {
      height: 1px;
      background: $border-light;
      margin: 0 $spacing-sm;
    }

    .promo-list-v2 {
      .promo-item-v2 {
        display: flex;
        align-items: center;
        gap: $spacing-sm;
        padding: $spacing-xs 0;

        .promo-tag {
          font-size: 10px;
          font-weight: 600;
          padding: 2px 6px;
          border-radius: $radius-sm;
          flex-shrink: 0;

          &.red {
            background: rgba($price-red, 0.1);
            color: $price-red;
          }

          &.yellow {
            background: rgba($accent, 0.1);
            color: $accent-dark;
          }

          &.green {
            background: rgba($primary, 0.1);
            color: $primary;
          }
        }

        .promo-desc {
          font-size: $font-sm;
          color: $text-secondary;
          flex: 1;
        }

        .promo-value {
          font-size: $font-sm;
          color: $price-red;
          font-weight: 500;
        }
      }
    }
  }
}

// 问医生悬浮按钮
.ask-doctor-fab {
  position: fixed;
  right: 16px;
  bottom: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  z-index: 100;
  transition: transform 0.2s;

  &:hover {
    transform: scale(1.05);
  }

  &:active {
    transform: scale(0.95);
  }

  .fab-icon {
    width: 48px;
    height: 48px;
    background: linear-gradient(135deg, #00B42A 0%, #009A29 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    box-shadow: 0 4px 12px rgba(0, 180, 42, 0.4);

    .el-icon {
      font-size: 24px;
    }
  }

  .fab-text {
    font-size: 10px;
    color: #00B42A;
    font-weight: 500;
  }
}
</style>