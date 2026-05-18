<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft, Search, Star, MoreFilled, Van, Shop, ShoppingCart,
  ChatDotRound, UserFilled, ArrowRight, Plus, Minus, CircleCheck,
  WarningFilled, Medal, Clock, Phone
} from '@element-plus/icons-vue'
import { useCartStore } from '@/stores/cart'
import { ROUTES, getStoreRoute, getInquiryCheckoutRoute } from '@/constants/routes'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

// 从路由参数获取storeId和drugId
const storeId = computed(() => route.params.storeId as string || route.query.storeId as string)
const drugId = computed(() => route.params.drugId as string || route.query.drugId as string)
const fromCategory = computed(() => route.query.from === 'category')

// 当前Tab
const activeTab = ref('product')
const tabs = [
  { id: 'product', name: '商品', hasDot: false },
  { id: 'store', name: '商家', hasDot: false },
  { id: 'doctor', name: '问医生', hasDot: true },
  { id: 'detail', name: '详情', hasDot: false }
]

// Tab吸顶状态
const isTabSticky = ref(false)
const tabRef = ref<HTMLElement>()

// 弹窗显示状态
const showCartPopup = ref(false)
const showFullInstruction = ref(false)

// 加载状态
const loading = ref(false)

// 商品数量
const quantity = ref(1)

// 当前轮播索引
const currentSwiperIndex = ref(0)

// 搜索关键词
const searchKeyword = ref('')

// 是否已收藏
const isFavorite = ref(false)

// 商品数据
const drug = ref({
  id: '1',
  name: '[康恩贝]肠炎宁片',
  specification: '0.42g*12片*4板/盒',
  price: 43.08,
  originalPrice: 56.00,
  stock: 3,
  isRx: false,
  manufacturer: '康恩贝制药股份有限公司',
  approvalNumber: '国药准字Z36020518',
  images: [
    'https://via.placeholder.com/400x400/07C160/FFFFFF?text=肠炎宁片',
    'https://via.placeholder.com/400x400/07C160/FFFFFF?text=药品图2',
    'https://via.placeholder.com/400x400/07C160/FFFFFF?text=药品图3'
  ],
  tags: ['非处方药'],
  disease: '清热利湿，行气。用于大肠湿热所致的泄泻，症见大便泄泻、腹痛腹胀；急慢性胃肠炎、腹泻、小儿消化不良见上述证候者。',
  usage: '口服。一次3-4片，一日3-4次；小儿酌减。',
  contraindications: '孕妇禁用。',
  precautions: '1.饮食宜清淡，忌烟、酒及辛辣、生冷、油腻食物。2.不宜在服药期间同时服用滋补性中药。',
  adverseReactions: '尚不明确',
  storage: '密封',
  validity: '36个月'
})

// 用户评价标签
const reviewTags = ref([
  { text: 't***刚刚给出5星好评', show: true },
  { text: '药效显著', show: false },
  { text: '配送快', show: false }
])

// 商家信息
const store = ref({
  id: '1',
  name: '采之林大药房(运河分店)',
  logo: 'https://via.placeholder.com/60x60/07C160/FFFFFF?text=药',
  monthlySales: 76,
  supportBooking: true,
  supportSelfPickup: true,
  rating: 4.8,
  distance: '1.2km',
  deliveryTime: 20,
  deliveryFee: 0,
  minDelivery: 0
})

// 用药组合
const drugCombo = ref({
  name: '胃肠炎反酸组合',
  items: [
    { name: '肠炎宁片', spec: '0.42g*12片*4板', price: 43.08 },
    { name: '铝碳酸镁咀嚼片', spec: '0.5g*20片', price: 38.40 }
  ],
  totalPrice: 81.48,
  discount: 6
})

// 秒问医生
const quickDoctor = ref({
  avatar: 'https://via.placeholder.com/50x50/07C160/FFFFFF?text=医',
  responseTime: 9,
  isCertified: true,
  rating: 99,
  title: '消化内科主治医师'
})

// 医生问答
const doctorQAs = ref([
  {
    id: 1,
    question: '肠炎宁片怎么吃？',
    answerCount: 3,
    doctorName: '王医生'
  },
  {
    id: 2,
    question: '肠炎宁片可以和蒙脱石散一起吃吗？',
    answerCount: 5,
    doctorName: '李医生'
  },
  {
    id: 3,
    question: '肠炎宁片孕妇能吃吗？',
    answerCount: 2,
    doctorName: '张医生'
  }
])

// 搭着买推荐
const comboCategories = ref([
  {
    id: 'anti-infection',
    name: '抗感染止泻',
    items: [
      { name: '蒙脱石散', spec: '3g*10袋', price: 18.50, image: 'https://via.placeholder.com/80x80/4A90D9/FFFFFF?text=蒙脱石' },
      { name: '诺氟沙星胶囊', spec: '0.1g*24粒', price: 12.80, image: 'https://via.placeholder.com/80x80/4A90D9/FFFFFF?text=诺氟沙星' }
    ]
  },
  {
    id: 'diarrhea',
    name: '腹泻调理',
    items: [
      { name: '益生菌冻干粉', spec: '2g*20袋', price: 35.00, image: 'https://via.placeholder.com/80x80/E67E22/FFFFFF?text=益生菌' },
      { name: '口服补液盐', spec: '5.125g*6袋', price: 15.60, image: 'https://via.placeholder.com/80x80/E67E22/FFFFFF?text=补液盐' }
    ]
  },
  {
    id: 'stomach',
    name: '胃部护理',
    items: [
      { name: '奥美拉唑肠溶胶囊', spec: '20mg*14粒', price: 28.00, image: 'https://via.placeholder.com/80x80/9B59B6/FFFFFF?text=奥美拉唑' },
      { name: '铝碳酸镁片', spec: '0.5g*30片', price: 32.50, image: 'https://via.placeholder.com/80x80/9B59B6/FFFFFF?text=铝碳酸镁' }
    ]
  }
])

// 购物车数量
const cartCount = computed(() => cartStore.totalCount)

// 加载商品数据
const loadDrugData = async () => {
  loading.value = true
  // 模拟API调用
  await new Promise(resolve => setTimeout(resolve, 500))
  loading.value = false
}

const goBack = () => {
  if (storeId.value && !fromCategory.value) {
    router.push(getStoreRoute(storeId.value))
  } else {
    router.back()
  }
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: ROUTES.SEARCH,
      query: {
        keyword: searchKeyword.value,
        storeId: storeId.value
      }
    })
  }
}

const toggleFavorite = () => {
  isFavorite.value = !isFavorite.value
  ElMessage.success(isFavorite.value ? '已收藏' : '已取消收藏')
}

const handleMore = () => {
  ElMessage.info('更多功能开发中')
}

const switchTab = (tabId: string) => {
  activeTab.value = tabId
}

const changeSwiper = (index: number) => {
  currentSwiperIndex.value = index
}

const goConsult = () => {
  router.push(ROUTES.INQUIRY_PRE)
}

const goStore = () => {
  if (storeId.value) {
    router.push(getStoreRoute(storeId.value))
  }
}

const goCart = () => {
  router.push(ROUTES.CART)
}

const showCart = () => {
  showCartPopup.value = true
}

const addToCart = async () => {
  try {
    await cartStore.addItem({
      drugId: drug.value.id,
      name: drug.value.name,
      price: drug.value.price,
      quantity: quantity.value,
      specification: drug.value.specification,
      manufacturer: drug.value.manufacturer,
      image: drug.value.images[0],
      disease: drug.value.disease,
      usage: drug.value.usage,
      isRx: drug.value.isRx
    })
    ElMessage.success('已加入购物车')
    showCartPopup.value = false
  } catch (error) {
    ElMessage.error('加入购物车失败')
  }
}

const buyNow = () => {
  if (drug.value.stock <= 0) {
    ElMessage.warning('商品库存不足')
    return
  }

  const orderData = {
    drugId: drug.value.id,
    name: drug.value.name,
    specification: drug.value.specification,
    manufacturer: drug.value.manufacturer,
    price: drug.value.price,
    quantity: quantity.value,
    image: drug.value.images[0],
    disease: drug.value.disease,
    usage: drug.value.usage,
    isRx: drug.value.isRx,
    storeId: storeId.value,
    storeName: store.value.name
  }
  localStorage.setItem('drugOrderData', JSON.stringify(orderData))
  router.push(getInquiryCheckoutRoute(0))
}

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

const increaseQuantity = () => {
  if (quantity.value < drug.value.stock) {
    quantity.value++
  } else {
    ElMessage.warning('已达到最大库存')
  }
}

const addComboToCart = () => {
  ElMessage.success('组合已加入购物车')
}

const goDoctorConsult = () => {
  router.push(ROUTES.INQUIRY_PRE)
}

const viewAllQA = () => {
  activeTab.value = 'doctor'
}

// 滚动监听
const handleScroll = () => {
  const scrollTop = window.scrollY || window.pageYOffset
  isTabSticky.value = scrollTop > 300
}

onMounted(() => {
  loadDrugData()
  window.addEventListener('scroll', handleScroll, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<template>
  <div class="store-drug-detail-page">
    <!-- 顶部导航栏 -->
    <div class="top-nav" :class="{ 'is-sticky': isTabSticky }">
      <div class="nav-left">
        <div class="nav-btn" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
        </div>
      </div>
      <div class="nav-search">
        <el-icon class="search-icon"><Search /></el-icon>
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="搜索店内商品"
          @keyup.enter="handleSearch"
        />
      </div>
      <div class="nav-right">
        <div class="nav-btn" :class="{ active: isFavorite }" @click="toggleFavorite">
          <el-icon><Star /></el-icon>
        </div>
        <div class="nav-btn" @click="handleMore">
          <el-icon><MoreFilled /></el-icon>
        </div>
      </div>
    </div>

    <!-- Tab导航（吸顶） -->
    <div ref="tabRef" class="tab-section" :class="{ 'is-sticky': isTabSticky }">
      <div
        v-for="tab in tabs"
        :key="tab.id"
        class="tab-item"
        :class="{ active: activeTab === tab.id }"
        @click="switchTab(tab.id)"
      >
        {{ tab.name }}
        <span v-if="tab.hasDot" class="tab-dot"></span>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton animated>
        <template #template>
          <div style="width: 100%; aspect-ratio: 1; background: #f5f5f5;" />
          <div style="padding: 16px; background: #fff; margin-top: 12px;">
            <el-skeleton-item variant="text" style="width: 40%; height: 32px;" />
          </div>
        </template>
      </el-skeleton>
    </div>

    <!-- 商品内容 -->
    <template v-else>
      <!-- 商品模块 -->
      <div v-show="activeTab === 'product'" class="product-section">
        <!-- 图片轮播区 -->
        <div class="image-section">
          <div class="image-swiper">
            <div
              class="swiper-container"
              :style="{ transform: `translateX(-${currentSwiperIndex * 100}%)` }"
            >
              <div
                v-for="(img, index) in drug.images"
                :key="index"
                class="swiper-item"
              >
                <img :src="img" :alt="drug.name" />
              </div>
            </div>
            <!-- 用户评价标签 -->
            <div class="review-tags">
              <div
                v-for="(tag, index) in reviewTags"
                :key="index"
                v-show="tag.show"
                class="review-tag"
              >
                {{ tag.text }}
              </div>
            </div>
            <!-- 指示器 -->
            <div class="swiper-dots">
              <span
                v-for="(_, index) in drug.images"
                :key="index"
                class="dot"
                :class="{ active: currentSwiperIndex === index }"
                @click="changeSwiper(index)"
              />
            </div>
          </div>
        </div>

        <!-- 价格区 -->
        <div class="price-section">
          <div class="price-main">
            <span class="price-symbol">¥</span>
            <span class="price-value">{{ drug.price.toFixed(2) }}</span>
          </div>
          <div class="health-card-info">
            <span class="health-card-tag">健康卡</span>
            <span class="health-card-text">本单最高返现 ¥2.15</span>
          </div>
        </div>

        <!-- 商品名称 -->
        <div class="name-section">
          <div class="drug-name">
            <span v-if="!drug.isRx" class="otc-tag">非处方药</span>
            <span v-else class="rx-tag">处方药</span>
            <h1>{{ drug.name }}{{ drug.specification }}</h1>
          </div>
        </div>

        <!-- 药品说明卡片 -->
        <div class="instruction-card" @click="showFullInstruction = true">
          <div class="instruction-content">
            <div class="instruction-item">
              <span class="label">功能主治</span>
              <span class="value">{{ drug.disease }}</span>
            </div>
            <div class="instruction-item">
              <span class="label">常见用法</span>
              <span class="value">{{ drug.usage }}</span>
            </div>
          </div>
          <div class="instruction-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>

        <!-- 配送信息 -->
        <div class="delivery-section">
          <div class="delivery-item">
            <el-icon class="delivery-icon"><Van /></el-icon>
            <div class="delivery-info">
              <span class="delivery-type">美团快送</span>
              <span class="delivery-time">约{{ store.deliveryTime }}分钟</span>
              <span class="delivery-fee">免配送费</span>
            </div>
          </div>
        </div>

        <!-- 服务标签 -->
        <div class="service-tags">
          <div class="service-tag">
            <el-icon><CircleCheck /></el-icon>
            <span>不支持七天无理由退换</span>
          </div>
          <div class="service-tag">
            <el-icon><WarningFilled /></el-icon>
            <span>隐私保护</span>
          </div>
          <div class="service-tag">
            <el-icon><Medal /></el-icon>
            <span>药监认证</span>
          </div>
        </div>

        <!-- 好药严选标签 -->
        <div class="quality-badge">
          <div class="quality-content">
            <el-icon><Medal /></el-icon>
            <span>好药严选</span>
          </div>
        </div>
      </div>

      <!-- 商家模块 -->
      <div v-show="activeTab === 'store'" class="store-section">
        <!-- 商家信息卡片 -->
        <div class="store-info-card">
          <div class="store-header">
            <img :src="store.logo" class="store-logo" />
            <div class="store-info">
              <div class="store-name">{{ store.name }}</div>
              <div class="store-meta">
                <span class="monthly-sales">月售{{ store.monthlySales }}</span>
                <span v-if="store.supportBooking" class="support-tag">支持预订</span>
                <span v-if="store.supportSelfPickup" class="support-tag">支持自取</span>
              </div>
            </div>
            <button class="enter-store-btn" @click="goStore">进店</button>
          </div>
        </div>

        <!-- 用药组合 -->
        <div class="combo-section">
          <div class="combo-header">
            <span class="combo-title">用药组合</span>
            <span class="combo-name">{{ drugCombo.name }}</span>
          </div>
          <div class="combo-items">
            <div v-for="(item, index) in drugCombo.items" :key="index" class="combo-item">
              <span class="item-name">{{ item.name }}</span>
              <span class="item-spec">{{ item.spec }}</span>
            </div>
          </div>
          <div class="combo-footer">
            <div class="combo-price">
              <span class="price-label">合计</span>
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ drugCombo.totalPrice.toFixed(2) }}</span>
              <span class="discount-tag">已优惠{{ drugCombo.discount }}元</span>
            </div>
            <button class="add-combo-btn" @click="addComboToCart">一起加购</button>
          </div>
        </div>

        <!-- 秒问医生 -->
        <div class="quick-doctor-section">
          <div class="doctor-header">
            <img :src="quickDoctor.avatar" class="doctor-avatar" />
            <div class="doctor-info">
              <div class="doctor-title">{{ quickDoctor.title }}</div>
              <div class="doctor-stats">
                <span class="response-time">{{ quickDoctor.responseTime }}秒接诊</span>
                <span v-if="quickDoctor.isCertified" class="certified-tag">卫健委认证</span>
                <span class="rating">好评率{{ quickDoctor.rating }}%</span>
              </div>
            </div>
          </div>
          <div class="doctor-actions">
            <span class="action-label">问诊开药</span>
            <button class="consult-btn" @click="goDoctorConsult">去咨询</button>
          </div>
        </div>

        <!-- 医生问答 -->
        <div class="qa-section">
          <div class="qa-header">
            <span class="qa-title">医生问答</span>
          </div>
          <div class="qa-list">
            <div v-for="qa in doctorQAs" :key="qa.id" class="qa-item">
              <div class="qa-question">
                <span class="q-mark">Q</span>
                <span class="question-text">{{ qa.question }}</span>
              </div>
              <div class="qa-answer">
                <span class="answer-count">{{ qa.answerCount }}个回答</span>
                <span class="answer-doctor">{{ qa.doctorName }}等</span>
              </div>
            </div>
          </div>
          <div class="qa-footer">
            <span class="view-all" @click="viewAllQA">查看全部</span>
          </div>
        </div>
      </div>

      <!-- 问医生模块 -->
      <div v-show="activeTab === 'doctor'" class="doctor-section">
        <div class="doctor-consult-card">
          <div class="consult-header">
            <img :src="quickDoctor.avatar" class="consult-avatar" />
            <div class="consult-info">
              <div class="consult-title">秒问医生</div>
              <div class="consult-desc">{{ quickDoctor.responseTime }}秒接诊 · 专业药师在线</div>
            </div>
          </div>
          <button class="start-consult-btn" @click="goDoctorConsult">立即咨询</button>
        </div>

        <div class="common-questions">
          <div class="section-title">常见问题</div>
          <div class="question-list">
            <div v-for="qa in doctorQAs" :key="qa.id" class="question-item">
              <span class="question-text">{{ qa.question }}</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 详情模块 -->
      <div v-show="activeTab === 'detail'" class="detail-section">
        <div class="detail-title">商品详情</div>
        <div class="detail-images">
          <img v-for="(img, index) in drug.images" :key="index" :src="img" />
        </div>
        <div class="instruction-detail">
          <div class="detail-section-title">药品说明书</div>
          <div class="instruction-item">
            <span class="label">药品名称</span>
            <span class="value">{{ drug.name }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">规格</span>
            <span class="value">{{ drug.specification }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">生产厂家</span>
            <span class="value">{{ drug.manufacturer }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">批准文号</span>
            <span class="value">{{ drug.approvalNumber }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">功能主治</span>
            <span class="value">{{ drug.disease }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">用法用量</span>
            <span class="value">{{ drug.usage }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">禁忌</span>
            <span class="value">{{ drug.contraindications }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">注意事项</span>
            <span class="value">{{ drug.precautions }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">不良反应</span>
            <span class="value">{{ drug.adverseReactions }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">贮藏</span>
            <span class="value">{{ drug.storage }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">有效期</span>
            <span class="value">{{ drug.validity }}</span>
          </div>
        </div>
      </div>

      <!-- 底部占位 -->
      <div class="bottom-placeholder" />
    </template>

    <!-- 底部购物车栏 -->
    <div v-if="!loading" class="bottom-cart-bar">
      <div class="cart-actions">
        <div class="action-item" @click="goConsult">
          <el-icon><ChatDotRound /></el-icon>
          <span>去咨询</span>
        </div>
        <div class="action-item" @click="goStore">
          <el-icon><Shop /></el-icon>
          <span>店铺</span>
        </div>
        <div class="action-item cart-item" @click="goCart">
          <div class="cart-icon-wrapper">
            <el-icon><ShoppingCart /></el-icon>
            <span v-if="cartCount > 0" class="cart-badge">{{ cartCount > 99 ? '99+' : cartCount }}</span>
          </div>
          <span>购物车</span>
        </div>
      </div>
      <div class="cart-buttons">
        <button class="btn-add-cart" @click="showCart">加入购物车</button>
        <button class="btn-buy-now" @click="buyNow">
          <span class="btn-text">立即购买</span>
          <span v-if="drug.stock <= 5" class="stock-hint">仅剩{{ drug.stock }}件</span>
        </button>
      </div>
    </div>

    <!-- 购物车弹窗 -->
    <el-dialog
      v-model="showCartPopup"
      title=""
      width="100%"
      :show-close="false"
      class="cart-popup"
      align-center
      destroy-on-close
    >
      <div class="cart-popup-content">
        <!-- 商品信息 -->
        <div class="popup-product">
          <img :src="drug.images[0]" class="popup-image" />
          <div class="popup-info">
            <div class="popup-price">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ drug.price.toFixed(2) }}</span>
            </div>
            <div class="popup-stock">库存{{ drug.stock }}件</div>
          </div>
          <div class="popup-close" @click="showCartPopup = false">
            <el-icon><Close /></el-icon>
          </div>
        </div>

        <!-- 已选商品 -->
        <div class="selected-section">
          <div class="section-label">已选商品</div>
          <div class="selected-item">
            <span class="item-name">{{ drug.name }}</span>
            <span class="item-spec">{{ drug.specification }}</span>
          </div>
        </div>

        <!-- 搭着买推荐 -->
        <div class="combo-recommend">
          <div class="recommend-header">
            <span class="recommend-title">搭着买</span>
            <span class="recommend-subtitle">对症推荐，一起购买更优惠</span>
          </div>
          <div class="recommend-categories">
            <div v-for="cat in comboCategories" :key="cat.id" class="recommend-category">
              <div class="category-name">{{ cat.name }}</div>
              <div class="category-items">
                <div v-for="item in cat.items" :key="item.name" class="recommend-item">
                  <img :src="item.image" class="item-image" />
                  <div class="item-info">
                    <div class="item-name">{{ item.name }}</div>
                    <div class="item-spec">{{ item.spec }}</div>
                    <div class="item-price">¥{{ item.price.toFixed(2) }}</div>
                  </div>
                  <button class="item-add-btn" @click="addToCart">
                    <el-icon><Plus /></el-icon>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 数量选择 -->
        <div class="quantity-section">
          <span class="quantity-label">数量</span>
          <div class="quantity-selector">
            <button class="quantity-btn" :disabled="quantity <= 1" @click="decreaseQuantity">
              <el-icon><Minus /></el-icon>
            </button>
            <span class="quantity-value">{{ quantity }}</span>
            <button class="quantity-btn" :disabled="quantity >= drug.stock" @click="increaseQuantity">
              <el-icon><Plus /></el-icon>
            </button>
          </div>
        </div>

        <!-- 加入购物车按钮 -->
        <button class="popup-add-btn" @click="addToCart">加入购物车</button>
      </div>
    </el-dialog>

    <!-- 说明书弹窗 -->
    <el-dialog
      v-model="showFullInstruction"
      title="药品说明书"
      width="90%"
      :show-close="true"
    >
      <div class="instruction-dialog-content">
        <div class="instruction-row">
          <span class="row-label">药品名称</span>
          <span class="row-value">{{ drug.name }}</span>
        </div>
        <div class="instruction-row">
          <span class="row-label">规格</span>
          <span class="row-value">{{ drug.specification }}</span>
        </div>
        <div class="instruction-row">
          <span class="row-label">生产厂家</span>
          <span class="row-value">{{ drug.manufacturer }}</span>
        </div>
        <div class="instruction-row">
          <span class="row-label">批准文号</span>
          <span class="row-value">{{ drug.approvalNumber }}</span>
        </div>
        <div class="instruction-row">
          <span class="row-label">功能主治</span>
          <span class="row-value">{{ drug.disease }}</span>
        </div>
        <div class="instruction-row">
          <span class="row-label">用法用量</span>
          <span class="row-value">{{ drug.usage }}</span>
        </div>
        <div class="instruction-row">
          <span class="row-label">禁忌</span>
          <span class="row-value">{{ drug.contraindications }}</span>
        </div>
        <div class="instruction-row">
          <span class="row-label">注意事项</span>
          <span class="row-value">{{ drug.precautions }}</span>
        </div>
        <div class="instruction-row">
          <span class="row-label">不良反应</span>
          <span class="row-value">{{ drug.adverseReactions }}</span>
        </div>
        <div class="instruction-row">
          <span class="row-label">贮藏</span>
          <span class="row-value">{{ drug.storage }}</span>
        </div>
        <div class="instruction-row">
          <span class="row-label">有效期</span>
          <span class="row-value">{{ drug.validity }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

// 颜色变量
$primary-green: #07C160;
$primary-green-light: #10B981;
$accent-yellow: #FFC300;
$warning-red: #FF4D4F;
$bg-gray: #F5F5F5;
$card-radius: 12px;

.store-drug-detail-page {
  min-height: 100vh;
  background: $bg-gray;
  padding-bottom: 80px;
}

// 顶部导航栏
.top-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  padding-top: calc(env(safe-area-inset-top, 0) + 8px);
  background: transparent;
  transition: all 0.3s ease;

  &.is-sticky {
    background: #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

    .nav-btn {
      background: #f5f5f5;
      color: $text-primary;
    }

    .nav-search {
      background: #f5f5f5;

      input {
        color: $text-primary;
      }
    }
  }

  .nav-left,
  .nav-right {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .nav-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    background: rgba(0, 0, 0, 0.3);
    color: #fff;
    cursor: pointer;
    backdrop-filter: blur(4px);
    transition: all 0.2s ease;

    &:active {
      opacity: 0.8;
    }

    &.active {
      color: $accent-yellow;
    }
  }

  .nav-search {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 8px;
    margin: 0 12px;
    padding: 8px 12px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 18px;
    backdrop-filter: blur(4px);

    .search-icon {
      font-size: 16px;
      color: $text-tertiary;
    }

    input {
      flex: 1;
      border: none;
      background: transparent;
      font-size: 14px;
      color: $text-primary;
      outline: none;

      &::placeholder {
        color: $text-tertiary;
      }
    }
  }
}

// Tab导航
.tab-section {
  display: flex;
  background: #fff;
  border-bottom: 1px solid $border-light;
  position: sticky;
  top: 56px;
  z-index: 100;
  margin-top: calc(env(safe-area-inset-top, 0) + 56px);

  &.is-sticky {
    top: 56px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  .tab-item {
    flex: 1;
    padding: 14px 0;
    text-align: center;
    font-size: 14px;
    color: $text-secondary;
    cursor: pointer;
    position: relative;
    transition: all 0.2s ease;

    &:active {
      opacity: 0.7;
    }

    &.active {
      color: $text-primary;
      font-weight: 600;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 24px;
        height: 3px;
        background: $primary-green;
        border-radius: 2px;
      }
    }

    .tab-dot {
      position: absolute;
      top: 10px;
      right: 20%;
      width: 8px;
      height: 8px;
      background: $warning-red;
      border-radius: 50%;
    }
  }
}

// 加载状态
.loading-container {
  padding: 40px 16px;
  margin-top: 100px;
}

// 商品模块
.product-section {
  background: #fff;

  // 图片轮播区
  .image-section {
    background: #fff;

    .image-swiper {
      position: relative;
      width: 100%;
      height: 320px;
      overflow: hidden;

      .swiper-container {
        display: flex;
        height: 100%;
        transition: transform 0.3s ease;
      }

      .swiper-item {
        flex-shrink: 0;
        width: 100%;
        height: 100%;

        img {
          width: 100%;
          height: 100%;
          object-fit: contain;
          background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
        }
      }

      // 用户评价标签
      .review-tags {
        position: absolute;
        bottom: 50px;
        left: 16px;
        display: flex;
        flex-direction: column;
        gap: 8px;

        .review-tag {
          padding: 6px 12px;
          background: rgba(0, 0, 0, 0.6);
          color: #fff;
          font-size: 12px;
          border-radius: 12px;
          backdrop-filter: blur(4px);
        }
      }

      // 指示器
      .swiper-dots {
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
          background: rgba(0, 0, 0, 0.2);
          cursor: pointer;
          transition: all 0.3s ease;

          &.active {
            width: 16px;
            border-radius: 3px;
            background: $primary-green;
          }
        }
      }
    }
  }

  // 价格区
  .price-section {
    padding: 16px;
    background: #fff;

    .price-main {
      display: flex;
      align-items: baseline;
      gap: 2px;
      margin-bottom: 8px;

      .price-symbol {
        font-size: 18px;
        color: $warning-red;
        font-weight: 600;
      }

      .price-value {
        font-size: 32px;
        font-weight: bold;
        color: $warning-red;
      }
    }

    .health-card-info {
      display: flex;
      align-items: center;
      gap: 8px;

      .health-card-tag {
        padding: 2px 8px;
        background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
        color: #fff;
        font-size: 11px;
        border-radius: 4px;
      }

      .health-card-text {
        font-size: 13px;
        color: $text-secondary;
      }
    }
  }

  // 商品名称
  .name-section {
    padding: 0 16px 16px;
    background: #fff;

    .drug-name {
      display: flex;
      align-items: flex-start;
      gap: 8px;

      .otc-tag {
        flex-shrink: 0;
        padding: 2px 6px;
        background: rgba($primary-green, 0.1);
        color: $primary-green;
        font-size: 11px;
        font-weight: 500;
        border-radius: 4px;
      }

      .rx-tag {
        flex-shrink: 0;
        padding: 2px 6px;
        background: rgba($warning-red, 0.1);
        color: $warning-red;
        font-size: 11px;
        font-weight: 500;
        border-radius: 4px;
      }

      h1 {
        flex: 1;
        font-size: 17px;
        font-weight: 600;
        color: $text-primary;
        line-height: 1.5;
        margin: 0;
      }
    }
  }

  // 药品说明卡片
  .instruction-card {
    margin: 0 16px 16px;
    padding: 16px;
    background: #FAFAFA;
    border-radius: $card-radius;
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    cursor: pointer;

    &:active {
      opacity: 0.8;
    }

    .instruction-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 8px;

      .instruction-item {
        display: flex;
        align-items: flex-start;
        gap: 8px;

        .label {
          flex-shrink: 0;
          font-size: 14px;
          font-weight: 600;
          color: $text-primary;
        }

        .value {
          flex: 1;
          font-size: 13px;
          color: $text-secondary;
          line-height: 1.5;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
      }
    }

    .instruction-arrow {
      flex-shrink: 0;
      margin-left: 12px;
      color: $text-tertiary;
    }
  }

  // 配送信息
  .delivery-section {
    padding: 0 16px 16px;
    background: #fff;

    .delivery-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px;
      background: #FAFAFA;
      border-radius: $card-radius;

      .delivery-icon {
        font-size: 24px;
        color: $primary-green;
      }

      .delivery-info {
        display: flex;
        align-items: center;
        gap: 12px;

        .delivery-type {
          font-size: 14px;
          font-weight: 600;
          color: $text-primary;
        }

        .delivery-time {
          font-size: 13px;
          color: $text-secondary;
        }

        .delivery-fee {
          font-size: 12px;
          color: $primary-green;
          padding: 2px 8px;
          background: rgba($primary-green, 0.1);
          border-radius: 4px;
        }
      }
    }
  }

  // 服务标签
  .service-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    padding: 0 16px 16px;
    background: #fff;

    .service-tag {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;
      color: $text-secondary;

      .el-icon {
        font-size: 14px;
        color: $text-tertiary;
      }
    }
  }

  // 好药严选标签
  .quality-badge {
    padding: 0 16px 16px;
    background: #fff;

    .quality-content {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 6px 12px;
      background: linear-gradient(135deg, #FFF9E6 0%, #FFF3CC 100%);
      border-radius: 16px;

      .el-icon {
        font-size: 16px;
        color: $accent-yellow;
      }

      span {
        font-size: 13px;
        font-weight: 500;
        color: #B8860B;
      }
    }
  }
}

// 商家模块
.store-section {
  padding: 12px;

  // 商家信息卡片
  .store-info-card {
    background: #fff;
    border-radius: $card-radius;
    padding: 16px;
    margin-bottom: 12px;

    .store-header {
      display: flex;
      align-items: center;
      gap: 12px;

      .store-logo {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        object-fit: cover;
      }

      .store-info {
        flex: 1;

        .store-name {
          font-size: 16px;
          font-weight: 600;
          color: $text-primary;
          margin-bottom: 8px;
        }

        .store-meta {
          display: flex;
          align-items: center;
          gap: 8px;

          .monthly-sales {
            font-size: 13px;
            color: $text-secondary;
          }

          .support-tag {
            padding: 2px 6px;
            background: rgba($primary-green, 0.1);
            color: $primary-green;
            font-size: 11px;
            border-radius: 4px;
          }
        }
      }

      .enter-store-btn {
        padding: 8px 16px;
        background: transparent;
        border: 1px solid $border-color;
        border-radius: 16px;
        font-size: 13px;
        color: $text-primary;
        cursor: pointer;
        transition: all 0.2s ease;

        &:active {
          background: $bg-gray;
        }
      }
    }
  }

  // 用药组合
  .combo-section {
    background: #fff;
    border-radius: $card-radius;
    padding: 16px;
    margin-bottom: 12px;

    .combo-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 12px;

      .combo-title {
        font-size: 14px;
        font-weight: 600;
        color: $text-primary;
      }

      .combo-name {
        font-size: 13px;
        color: $text-secondary;
      }
    }

    .combo-items {
      display: flex;
      flex-direction: column;
      gap: 8px;
      margin-bottom: 12px;

      .combo-item {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 8px 12px;
        background: #FAFAFA;
        border-radius: 8px;

        .item-name {
          font-size: 14px;
          color: $text-primary;
        }

        .item-spec {
          font-size: 12px;
          color: $text-tertiary;
        }
      }
    }

    .combo-footer {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .combo-price {
        display: flex;
        align-items: baseline;
        gap: 4px;

        .price-label {
          font-size: 13px;
          color: $text-secondary;
        }

        .price-symbol {
          font-size: 14px;
          color: $warning-red;
          font-weight: 600;
        }

        .price-value {
          font-size: 20px;
          font-weight: bold;
          color: $warning-red;
        }

        .discount-tag {
          margin-left: 8px;
          padding: 2px 6px;
          background: rgba($warning-red, 0.1);
          color: $warning-red;
          font-size: 11px;
          border-radius: 4px;
        }
      }

      .add-combo-btn {
        padding: 8px 16px;
        background: $accent-yellow;
        border: none;
        border-radius: 16px;
        font-size: 13px;
        font-weight: 500;
        color: $text-primary;
        cursor: pointer;
        transition: all 0.2s ease;

        &:active {
          opacity: 0.9;
        }
      }
    }
  }

  // 秒问医生
  .quick-doctor-section {
    background: #fff;
    border-radius: $card-radius;
    padding: 16px;
    margin-bottom: 12px;

    .doctor-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 16px;

      .doctor-avatar {
        width: 50px;
        height: 50px;
        border-radius: 50%;
        object-fit: cover;
      }

      .doctor-info {
        flex: 1;

        .doctor-title {
          font-size: 15px;
          font-weight: 600;
          color: $text-primary;
          margin-bottom: 6px;
        }

        .doctor-stats {
          display: flex;
          align-items: center;
          gap: 8px;

          span {
            font-size: 12px;
            color: $text-secondary;
          }

          .certified-tag {
            padding: 2px 6px;
            background: rgba($primary-green, 0.1);
            color: $primary-green;
            border-radius: 4px;
          }
        }
      }
    }

    .doctor-actions {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding-top: 12px;
      border-top: 1px solid $border-light;

      .action-label {
        font-size: 14px;
        color: $text-secondary;
      }

      .consult-btn {
        padding: 8px 20px;
        background: $primary-green;
        border: none;
        border-radius: 16px;
        font-size: 13px;
        font-weight: 500;
        color: #fff;
        cursor: pointer;
        transition: all 0.2s ease;

        &:active {
          opacity: 0.9;
        }
      }
    }
  }

  // 医生问答
  .qa-section {
    background: #fff;
    border-radius: $card-radius;
    padding: 16px;

    .qa-header {
      margin-bottom: 12px;

      .qa-title {
        font-size: 15px;
        font-weight: 600;
        color: $text-primary;
      }
    }

    .qa-list {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .qa-item {
        padding: 12px;
        background: #FAFAFA;
        border-radius: 8px;

        .qa-question {
          display: flex;
          align-items: flex-start;
          gap: 8px;
          margin-bottom: 8px;

          .q-mark {
            flex-shrink: 0;
            width: 20px;
            height: 20px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: $primary-green;
            color: #fff;
            font-size: 12px;
            font-weight: 600;
            border-radius: 4px;
          }

          .question-text {
            flex: 1;
            font-size: 14px;
            color: $text-primary;
            line-height: 1.5;
          }
        }

        .qa-answer {
          display: flex;
          align-items: center;
          gap: 8px;
          padding-left: 28px;

          .answer-count {
            font-size: 12px;
            color: $primary-green;
          }

          .answer-doctor {
            font-size: 12px;
            color: $text-tertiary;
          }
        }
      }
    }

    .qa-footer {
      margin-top: 12px;
      text-align: center;

      .view-all {
        font-size: 13px;
        color: $text-secondary;
        cursor: pointer;

        &:active {
          opacity: 0.7;
        }
      }
    }
  }
}

// 问医生模块
.doctor-section {
  padding: 12px;

  .doctor-consult-card {
    background: #fff;
    border-radius: $card-radius;
    padding: 16px;
    margin-bottom: 12px;

    .consult-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 16px;

      .consult-avatar {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        object-fit: cover;
      }

      .consult-info {
        flex: 1;

        .consult-title {
          font-size: 17px;
          font-weight: 600;
          color: $text-primary;
          margin-bottom: 6px;
        }

        .consult-desc {
          font-size: 13px;
          color: $text-secondary;
        }
      }
    }

    .start-consult-btn {
      width: 100%;
      padding: 12px;
      background: $primary-green;
      border: none;
      border-radius: 20px;
      font-size: 15px;
      font-weight: 600;
      color: #fff;
      cursor: pointer;
      transition: all 0.2s ease;

      &:active {
        opacity: 0.9;
      }
    }
  }

  .common-questions {
    background: #fff;
    border-radius: $card-radius;
    padding: 16px;

    .section-title {
      font-size: 15px;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 12px;
    }

    .question-list {
      display: flex;
      flex-direction: column;

      .question-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 12px 0;
        border-bottom: 1px solid $border-light;
        cursor: pointer;

        &:last-child {
          border-bottom: none;
        }

        &:active {
          opacity: 0.7;
        }

        .question-text {
          flex: 1;
          font-size: 14px;
          color: $text-primary;
          margin-right: 8px;
        }

        .el-icon {
          font-size: 16px;
          color: $text-tertiary;
        }
      }
    }
  }
}

// 详情模块
.detail-section {
  padding: 12px;

  .detail-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 12px;
    padding-left: 12px;
    border-left: 4px solid $primary-green;
  }

  .detail-images {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin-bottom: 16px;

    img {
      width: 100%;
      border-radius: $card-radius;
    }
  }

  .instruction-detail {
    background: #fff;
    border-radius: $card-radius;
    padding: 16px;

    .detail-section-title {
      font-size: 15px;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 16px;
    }

    .instruction-item {
      display: flex;
      padding: 12px 0;
      border-bottom: 1px solid $border-light;

      &:last-child {
        border-bottom: none;
      }

      .label {
        width: 80px;
        flex-shrink: 0;
        font-size: 13px;
        color: $text-secondary;
      }

      .value {
        flex: 1;
        font-size: 13px;
        color: $text-primary;
        line-height: 1.6;
      }
    }
  }
}

// 底部占位
.bottom-placeholder {
  height: 80px;
}

// 底部购物车栏
.bottom-cart-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  padding: 8px 16px;
  padding-bottom: calc(env(safe-area-inset-bottom, 0) + 8px);
  background: #fff;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.08);
  z-index: 1000;

  .cart-actions {
    display: flex;
    gap: 20px;
    padding-right: 16px;
    border-right: 1px solid $border-light;

    .action-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 4px;
      cursor: pointer;

      &:active {
        opacity: 0.7;
      }

      .el-icon {
        font-size: 22px;
        color: $text-secondary;
      }

      span {
        font-size: 11px;
        color: $text-secondary;
      }

      &.cart-item {
        .cart-icon-wrapper {
          position: relative;

          .cart-badge {
            position: absolute;
            top: -6px;
            right: -6px;
            min-width: 16px;
            height: 16px;
            padding: 0 4px;
            background: $warning-red;
            color: #fff;
            font-size: 10px;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
          }
        }
      }
    }
  }

  .cart-buttons {
    flex: 1;
    display: flex;
    gap: 8px;
    margin-left: 16px;

    .btn-add-cart {
      flex: 1;
      height: 40px;
      border: none;
      background: linear-gradient(135deg, #FFA500 0%, #FF8C00 100%);
      border-radius: 20px;
      font-size: 14px;
      font-weight: 600;
      color: #fff;
      cursor: pointer;
      transition: all 0.2s ease;

      &:active {
        opacity: 0.9;
      }
    }

    .btn-buy-now {
      flex: 1;
      height: 40px;
      border: none;
      background: linear-gradient(135deg, $primary-green 0%, $primary-green-light 100%);
      border-radius: 20px;
      font-size: 14px;
      font-weight: 600;
      color: #fff;
      cursor: pointer;
      transition: all 0.2s ease;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;

      .btn-text {
        font-size: 14px;
        font-weight: 600;
      }

      .stock-hint {
        font-size: 10px;
        opacity: 0.9;
      }

      &:active {
        opacity: 0.9;
      }
    }
  }
}
</style>