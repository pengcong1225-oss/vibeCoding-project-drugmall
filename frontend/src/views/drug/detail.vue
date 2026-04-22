<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Share, Location, StarFilled, ChatDotRound, UserFilled, ArrowRight } from '@element-plus/icons-vue'
import { useCartStore } from '@/stores/cart'
import { getDrugStores } from '@/api/modules/drug'
import type { Drug } from '@/types'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

// 当前Tab
const activeTab = ref('stores')
const tabs = [
  { id: 'stores', name: '在售商家' },
  { id: 'qa', name: '医生问答' },
  { id: 'reviews', name: '同款评价' },
  { id: 'detail', name: '详情' }
]

// Tab吸顶状态
const isTabSticky = ref(false)
const tabRef = ref<HTMLElement>()

// 弹窗显示状态
const showFullInstruction = ref(false)
const showFullInfo = ref(false)

// 药品数据
const drug = ref<Drug | null>(null)
const loading = ref(false)

// 当前选中的规格
const selectedSpecIndex = ref(0)

// 当前轮播索引
const currentSwiperIndex = ref(0)

// 模拟药品数据
const mockDrugData: Drug = {
  id: '1',
  name: '阿莫西林胶囊',
  genericName: '阿莫西林',
  brand: '联邦制药',
  specification: '0.25g*24粒',
  price: 28.50,
  originalPrice: 35.00,
  stock: 999,
  isRx: true,
  manufacturer: '珠海联邦制药股份有限公司',
  approvalNumber: '国药准字H20000292',
  images: [
    'https://via.placeholder.com/400x400/4A90D9/FFFFFF?text=药品图片1',
    'https://via.placeholder.com/400x400/5BA0E9/FFFFFF?text=药品图片2',
    'https://via.placeholder.com/400x400/6BB0F9/FFFFFF?text=药品图片3'
  ],
  tags: ['国家基药', '医保甲类'],
  disease: '适用于敏感菌所致的呼吸道感染、泌尿生殖道感染、皮肤软组织感染等',
  usage: '口服。成人一次0.5g，每6-8小时1次，一日剂量不超过4g',
  contraindications: '青霉素过敏者禁用',
  precautions: '用药前需做青霉素皮试',
  adverseReactions: '恶心、呕吐、腹泻等胃肠道反应',
  storage: '密封，在凉暗干燥处保存',
  isNationalEssential: true
}

// 规格列表
const specs = ref([
  { id: 1, name: '0.25g*24粒', price: 28.50 },
  { id: 2, name: '0.25g*36粒', price: 38.00 },
  { id: 3, name: '0.5g*24粒', price: 45.00 }
])

// 在售商家数据
const stores = ref<any[]>([])
const storesLoading = ref(false)

// 加载门店数据
const loadDrugStores = async (drugId: string) => {
  storesLoading.value = true
  try {
    const res = await getDrugStores(drugId, { limit: 10 })
    stores.value = res.data || []
  } catch (error) {
    console.error('加载门店数据失败:', error)
    // 使用默认数据
    stores.value = [
      {
        id: 1,
        name: '百姓大药房（光谷店）',
        price: 28.50,
        originalPrice: 35.00,
        distance: '1.2km',
        delivery: '29分钟达',
        rating: 4.9,
        sales: 1200,
        tags: ['医保定点', '24小时']
      }
    ]
  } finally {
    storesLoading.value = false
  }
}

// 医生问答数据
const doctorQAs = ref([
  {
    id: 1,
    doctorName: '张医生',
    title: '主治医师',
    department: '呼吸内科',
    hospital: '三甲医院',
    avatar: 'https://via.placeholder.com/60x60/00C9A7/FFFFFF?text=张',
    question: '阿莫西林胶囊可以治疗感冒吗？',
    answer: '阿莫西林是抗生素，主要用于细菌感染。普通感冒多为病毒感染，使用抗生素无效。建议在医生指导下使用。',
    likes: 128
  },
  {
    id: 2,
    doctorName: '李医生',
    title: '副主任医师',
    department: '药剂科',
    hospital: '二甲医院',
    avatar: 'https://via.placeholder.com/60x60/0891B2/FFFFFF?text=李',
    question: '服用阿莫西林需要注意什么？',
    answer: '1. 青霉素过敏者禁用；2. 用药前需做皮试；3. 按疗程服用，不可随意停药；4. 饭后服用可减少胃肠道刺激。',
    likes: 256
  }
])

// 用户评价数据
const reviews = ref([
  {
    id: 1,
    userName: '用户***88',
    avatar: 'https://via.placeholder.com/40x40/FFD700/333333?text=A',
    rating: 5,
    content: '药品包装完好，送货速度快，价格实惠，正品有保障！',
    date: '2024-01-15',
    specs: '0.25g*24粒',
    images: ['https://via.placeholder.com/100x100/F5F5F5/666666?text=图1']
  },
  {
    id: 2,
    userName: '用户***23',
    avatar: 'https://via.placeholder.com/40x40/FF6B6B/FFFFFF?text=B',
    rating: 5,
    content: '医生开的药，在这买比医院便宜不少，是正品。',
    date: '2024-01-12',
    specs: '0.25g*24粒',
    images: []
  },
  {
    id: 3,
    userName: '用户***56',
    avatar: 'https://via.placeholder.com/40x40/4ECDC4/FFFFFF?text=C',
    rating: 4,
    content: '药效不错，就是配送稍微慢了一点。',
    date: '2024-01-10',
    specs: '0.25g*36粒',
    images: []
  }
])

// 说明书内容
const instructionContent = ref({
  name: '阿莫西林胶囊',
  genericName: '阿莫西林',
  englishName: 'Amoxicillin Capsules',
  ingredients: '本品主要成份为阿莫西林',
  appearance: '本品内容物为白色至淡黄色粉末或颗粒',
  indications: '适用于敏感菌所致的呼吸道感染、泌尿生殖道感染、皮肤软组织感染、急性单纯性淋病等',
  dosage: '口服。成人一次0.5g，每6-8小时1次，一日剂量不超过4g。小儿一日剂量按体重20-40mg/kg，每8小时1次',
  adverseReactions: '1. 恶心、呕吐、腹泻等胃肠道反应；2. 皮疹、药物热等过敏反应；3. 贫血、血小板减少等',
  contraindications: '青霉素过敏者禁用',
  precautions: '1. 用药前必须做青霉素皮试；2. 传染性单核细胞增多症患者禁用；3. 孕妇及哺乳期妇女慎用',
  drugInteractions: '1. 与丙磺舒合用可升高本品血药浓度；2. 与别嘌醇合用增加皮疹发生率',
  storage: '密封，在凉暗干燥处保存',
  validity: '24个月'
})

// 加载药品数据
const loadDrugData = async () => {
  loading.value = true
  // 模拟API调用
  await new Promise(resolve => setTimeout(resolve, 500))
  drug.value = mockDrugData
  loading.value = false
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 分享
const handleShare = () => {
  ElMessage.success('分享功能开发中')
}

// 切换Tab
const switchTab = (tabId: string) => {
  activeTab.value = tabId
  // 滚动到Tab位置
  if (tabRef.value) {
    const rect = tabRef.value.getBoundingClientRect()
    const scrollTop = window.pageYOffset + rect.top - 50
    window.scrollTo({ top: scrollTop, behavior: 'smooth' })
  }
}

// 选择规格
const selectSpec = (index: number) => {
  selectedSpecIndex.value = index
}

// 切换轮播
const changeSwiper = (index: number) => {
  currentSwiperIndex.value = index
}

// 去咨询
const goConsult = () => {
  router.push('/inquiry/pre')
}

// 去AI助手
const goAIAssistant = () => {
  router.push('/ai-assistant')
}

// 进店
const goStore = (storeId: number) => {
  router.push(`/store/${storeId}`)
}

// 立即购买
const buyNow = () => {
  if (!drug.value) return
  
  // 判断是否为处方药
  if (drug.value.isRx) {
    // 处方药需要先开方
    ElMessage.info('处方药需要先进行在线问诊开方')
    // 构建处方申请数据
    const prescriptionData = {
      drugId: drug.value.id,
      drugName: drug.value.name,
      specification: specs.value[selectedSpecIndex.value].name,
      manufacturer: drug.value.manufacturer || '',
      price: specs.value[selectedSpecIndex.value].price,
      image: drug.value.images?.[0] || '',
      disease: drug.value.disease || '',
      usage: drug.value.usage || ''
    }
    localStorage.setItem('prescriptionApplyData', JSON.stringify(prescriptionData))
    // 跳转到处方申请页，传递药品ID
    router.push(`/prescription/apply?drugId=${drug.value.id}`)
    return
  }
  
  // 非处方药直接购买
  const orderData = {
    drugId: drug.value.id,
    name: drug.value.name,
    specification: specs.value[selectedSpecIndex.value].name,
    manufacturer: drug.value.manufacturer || '',
    price: specs.value[selectedSpecIndex.value].price,
    quantity: 1,
    image: drug.value.images?.[0] || '',
    disease: drug.value.disease || '',
    usage: drug.value.usage || '',
    isRx: false
  }
  localStorage.setItem('drugOrderData', JSON.stringify(orderData))
  // 跳转到新的结算页
  router.push('/inquiry/checkout/0')
}

// 滚动监听
const handleScroll = () => {
  const scrollTop = window.scrollY || window.pageYOffset
  isTabSticky.value = scrollTop > 350
}

onMounted(() => {
  loadDrugData()
  // 获取药品ID并加载门店数据
  const drugId = route.params.id as string
  if (drugId) {
    loadDrugStores(drugId)
  }
  window.addEventListener('scroll', handleScroll, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<template>
  <div class="drug-detail-page">
    <!-- 顶部固定导航栏 -->
    <div class="fixed-header" :class="{ 'is-visible': isTabSticky }">
      <div class="header-left" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <div class="header-title">药品详情</div>
      <div class="header-right" @click="handleShare">
        <el-icon><Share /></el-icon>
      </div>
    </div>

    <!-- 顶部透明导航栏 -->
    <div class="transparent-header">
      <div class="header-left" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <div class="header-right" @click="handleShare">
        <el-icon><Share /></el-icon>
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

    <!-- 药品详情内容 -->
    <template v-else-if="drug">
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
          <!-- 处方药标识 -->
          <div v-if="drug.isRx" class="rx-badge">处方药</div>
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

      <!-- 药品信息区 -->
      <div class="info-section">
        <!-- 价格 -->
        <div class="price-row">
          <span class="price-symbol">¥</span>
          <span class="price-value">{{ specs[selectedSpecIndex].price.toFixed(2) }}</span>
          <span class="original-price">¥{{ drug.originalPrice?.toFixed(2) }}</span>
        </div>

        <!-- 药品名称 -->
        <div class="drug-name-row">
          <span v-if="drug.isNationalEssential" class="essential-tag">国家基药</span>
          <h1 class="drug-name">{{ drug.brand }} {{ drug.name }} {{ specs[selectedSpecIndex].name }}</h1>
        </div>

        <!-- 规格选择 -->
        <div class="spec-section">
          <div class="spec-label">规格</div>
          <div class="spec-list">
            <div
              v-for="(spec, index) in specs"
              :key="spec.id"
              class="spec-item"
              :class="{ active: selectedSpecIndex === index }"
              @click="selectSpec(index)"
            >
              {{ spec.name }}
            </div>
          </div>
        </div>

        <!-- 处方药提示 -->
        <div v-if="drug.isRx" class="rx-notice">
          <span class="rx-tag">处方药</span>
          <span class="rx-desc">处方药须凭处方在药师指导下购买和使用</span>
        </div>

        <!-- 说明书横向滚动预览 -->
        <div class="instruction-scroll-preview">
          <div class="scroll-container">
            <div class="scroll-item">
              <span class="scroll-label">功能主治</span>
              <span class="scroll-value">{{ drug.isRx ? '根据法规要求，请咨询药师了解处方药' : instructionContent.indications }}</span>
            </div>
            <div class="scroll-item">
              <span class="scroll-label">禁忌</span>
              <span class="scroll-value">{{ instructionContent.contraindications }}</span>
            </div>
            <div class="scroll-item">
              <span class="scroll-label">用法用量</span>
              <span class="scroll-value">{{ instructionContent.dosage }}</span>
            </div>
            <div class="scroll-item">
              <span class="scroll-label">不良反应</span>
              <span class="scroll-value">{{ instructionContent.adverseReactions }}</span>
            </div>
          </div>
          <div class="scroll-arrow" @click="showFullInstruction = true">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>

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
              <span class="row-value">{{ instructionContent.name }}</span>
            </div>
            <div class="instruction-row">
              <span class="row-label">通用名称</span>
              <span class="row-value">{{ instructionContent.genericName }}</span>
            </div>
            <div class="instruction-row">
              <span class="row-label">英文名称</span>
              <span class="row-value">{{ instructionContent.englishName }}</span>
            </div>
            <div class="instruction-row">
              <span class="row-label">成份</span>
              <span class="row-value">{{ instructionContent.ingredients }}</span>
            </div>
            <div class="instruction-row">
              <span class="row-label">性状</span>
              <span class="row-value">{{ instructionContent.appearance }}</span>
            </div>
            <div class="instruction-row">
              <span class="row-label">适应症</span>
              <span class="row-value">{{ instructionContent.indications }}</span>
            </div>
            <div class="instruction-row">
              <span class="row-label">用法用量</span>
              <span class="row-value">{{ instructionContent.dosage }}</span>
            </div>
            <div class="instruction-row">
              <span class="row-label">不良反应</span>
              <span class="row-value">{{ instructionContent.adverseReactions }}</span>
            </div>
            <div class="instruction-row">
              <span class="row-label">禁忌</span>
              <span class="row-value">{{ instructionContent.contraindications }}</span>
            </div>
            <div class="instruction-row">
              <span class="row-label">注意事项</span>
              <span class="row-value">{{ instructionContent.precautions }}</span>
            </div>
            <div class="instruction-row">
              <span class="row-label">药物相互作用</span>
              <span class="row-value">{{ instructionContent.drugInteractions }}</span>
            </div>
            <div class="instruction-row">
              <span class="row-label">贮藏</span>
              <span class="row-value">{{ instructionContent.storage }}</span>
            </div>
            <div class="instruction-row">
              <span class="row-label">有效期</span>
              <span class="row-value">{{ instructionContent.validity }}</span>
            </div>
          </div>
        </el-dialog>
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
        </div>
      </div>

      <!-- Tab内容区 -->
      <div class="tab-content">
        <!-- 在售商家 -->
        <div v-if="activeTab === 'stores'" class="stores-list">
          <div
            v-for="store in stores"
            :key="store.id"
            class="store-card"
            @click="goStore(store.id)"
          >
            <div class="store-header">
              <div class="store-name">{{ store.name }}</div>
              <div class="store-distance">
                <el-icon><Location /></el-icon>
                <span>{{ store.distance }}</span>
              </div>
            </div>
            <div class="store-body">
              <div class="store-price">
                <span class="price-symbol">¥</span>
                <span class="price-value">{{ store.price.toFixed(2) }}</span>
                <span class="original-price">¥{{ store.originalPrice.toFixed(2) }}</span>
              </div>
              <div class="store-info">
                <span class="delivery">{{ store.delivery }}</span>
                <span class="rating">
                  <el-icon><StarFilled /></el-icon>
                  {{ store.rating }}分
                </span>
                <span class="sales">月售{{ store.sales }}</span>
              </div>
            </div>
            <div class="store-tags">
              <span v-for="tag in store.tags" :key="tag" class="tag">{{ tag }}</span>
            </div>
          </div>
        </div>

        <!-- 医生问答 -->
        <div v-if="activeTab === 'qa'" class="qa-list">
          <div v-for="qa in doctorQAs" :key="qa.id" class="qa-card">
            <div class="doctor-info">
              <img :src="qa.avatar" class="doctor-avatar" />
              <div class="doctor-meta">
                <div class="doctor-name">{{ qa.doctorName }}</div>
                <div class="doctor-title">{{ qa.title }} · {{ qa.department }}</div>
                <div class="doctor-hospital">{{ qa.hospital }}</div>
              </div>
            </div>
            <div class="qa-content">
              <div class="question">
                <span class="qa-label">问</span>
                <span class="qa-text">{{ qa.question }}</span>
              </div>
              <div class="answer">
                <span class="qa-label answer-label">答</span>
                <span class="qa-text">{{ qa.answer }}</span>
              </div>
            </div>
            <div class="qa-footer">
              <span class="likes">{{ qa.likes }}人觉得有帮助</span>
            </div>
          </div>
        </div>

        <!-- 同款评价 -->
        <div v-if="activeTab === 'reviews'" class="reviews-list">
          <div v-for="review in reviews" :key="review.id" class="review-card">
            <div class="review-header">
              <img :src="review.avatar" class="user-avatar" />
              <div class="user-info">
                <div class="user-name">{{ review.userName }}</div>
                <div class="review-meta">
                  <div class="rating">
                    <el-icon v-for="i in 5" :key="i" :class="{ 'is-active': i <= review.rating }">
                      <StarFilled />
                    </el-icon>
                  </div>
                  <span class="review-date">{{ review.date }}</span>
                </div>
              </div>
            </div>
            <div class="review-content">{{ review.content }}</div>
            <div v-if="review.images.length" class="review-images">
              <img v-for="img in review.images" :key="img" :src="img" class="review-img" />
            </div>
            <div class="review-specs">规格：{{ review.specs }}</div>
          </div>
        </div>

        <!-- 详情 -->
        <div v-if="activeTab === 'detail'" class="detail-content">
          <!-- 药品说明书 -->
          <div class="instruction-section">
            <div class="section-title">药品说明书</div>
            <div class="instruction-table">
              <div class="table-row">
                <div class="table-label">药品名称</div>
                <div class="table-value">{{ instructionContent.name }}</div>
              </div>
              <div class="table-row">
                <div class="table-label">通用名称</div>
                <div class="table-value">{{ instructionContent.genericName }}</div>
              </div>
              <div class="table-row">
                <div class="table-label">英文名称</div>
                <div class="table-value">{{ instructionContent.englishName }}</div>
              </div>
              <div class="table-row">
                <div class="table-label">成份</div>
                <div class="table-value">{{ instructionContent.ingredients }}</div>
              </div>
              <div class="table-row">
                <div class="table-label">性状</div>
                <div class="table-value">{{ instructionContent.appearance }}</div>
              </div>
              <div class="table-row">
                <div class="table-label">适应症</div>
                <div class="table-value">{{ instructionContent.indications }}</div>
              </div>
              <div class="table-row">
                <div class="table-label">用法用量</div>
                <div class="table-value">{{ instructionContent.dosage }}</div>
              </div>
              <div class="table-row">
                <div class="table-label">不良反应</div>
                <div class="table-value">{{ instructionContent.adverseReactions }}</div>
              </div>
              <div class="table-row">
                <div class="table-label">禁忌</div>
                <div class="table-value">{{ instructionContent.contraindications }}</div>
              </div>
              <div class="table-row">
                <div class="table-label">注意事项</div>
                <div class="table-value">{{ instructionContent.precautions }}</div>
              </div>
              <div class="table-row">
                <div class="table-label">药物相互作用</div>
                <div class="table-value">{{ instructionContent.drugInteractions }}</div>
              </div>
              <div class="table-row">
                <div class="table-label">贮藏</div>
                <div class="table-value">{{ instructionContent.storage }}</div>
              </div>
              <div class="table-row">
                <div class="table-label">有效期</div>
                <div class="table-value">{{ instructionContent.validity }}</div>
              </div>
            </div>
          </div>

          <!-- 商品详情图片 -->
          <div class="product-images">
            <div class="section-title">商品详情</div>
            <div class="detail-images">
              <img src="https://via.placeholder.com/750x400/F5F5F5/666666?text=商品详情图1" />
              <img src="https://via.placeholder.com/750x400/F5F5F5/666666?text=商品详情图2" />
              <img src="https://via.placeholder.com/750x400/F5F5F5/666666?text=商品详情图3" />
            </div>
          </div>
        </div>
      </div>

      <!-- 底部占位 -->
      <div class="bottom-placeholder" />
    </template>

    <!-- 底部固定操作栏 -->
    <div v-if="drug && !loading" class="bottom-actions">
      <div class="action-left">
        <div class="action-btn health-butler" @click="goAIAssistant">
          <div class="butler-icon">
            <svg viewBox="0 0 24 24" width="24" height="24">
              <path fill="currentColor" d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm0 14H6l-2 2V4h16v12z"/>
              <circle cx="12" cy="10" r="1.5" fill="currentColor"/>
              <circle cx="7.5" cy="10" r="1.5" fill="currentColor"/>
              <circle cx="16.5" cy="10" r="1.5" fill="currentColor"/>
            </svg>
          </div>
          <span>去咨询</span>
        </div>
        <div class="action-btn" @click="goStore(stores[0]?.id)">
          <el-icon><UserFilled /></el-icon>
          <span>进店</span>
        </div>
      </div>
      <div class="action-right">
        <button class="btn-buy" @click="buyNow">
          <span class="btn-text">立即购买</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

// 颜色变量
$primary-yellow: #FFD700;
$primary-yellow-dark: #E6C200;
$warning-red: #FF4D4F;
$bg-gray: #F5F5F5;
$card-radius: 16px;

.drug-detail-page {
  min-height: 100vh;
  background: $bg-gray;
  padding-bottom: 80px;
}

// 固定导航栏（滚动后显示）
.fixed-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 16px;
  padding-top: calc(env(safe-area-inset-top, 0) + 8px);
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transform: translateY(-100%);
  transition: transform 0.3s ease;

  &.is-visible {
    transform: translateY(0);
  }

  .header-left,
  .header-right {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    background: #f5f5f5;
    cursor: pointer;

    &:active {
      opacity: 0.8;
    }
  }

  .header-title {
    font-size: 17px;
    font-weight: 600;
    color: $text-primary;
  }
}

// 透明导航栏
.transparent-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 16px;
  padding-top: calc(env(safe-area-inset-top, 0) + 8px);

  .header-left,
  .header-right {
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

    &:active {
      opacity: 0.8;
    }
  }
}

// 图片轮播区
.image-section {
  background: #fff;

  .image-swiper {
    position: relative;
    width: 100%;
    height: 280px;
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

    // 处方药标识
    .rx-badge {
      position: absolute;
      top: 16px;
      left: 16px;
      padding: 4px 10px;
      background: $warning-red;
      color: #fff;
      font-size: 12px;
      font-weight: 500;
      border-radius: 4px;
    }

    // 指示器
    .swiper-dots {
      position: absolute;
      bottom: 12px;
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
          background: $primary-yellow;
        }
      }
    }
  }
}

// 药品信息区
.info-section {
  padding: 16px;
  background: #fff;
  margin-bottom: 12px;

  // 价格
  .price-row {
    display: flex;
    align-items: baseline;
    gap: 4px;
    margin-bottom: 12px;

    .price-symbol {
      font-size: 16px;
      color: $warning-red;
      font-weight: 600;
    }

    .price-value {
      font-size: 28px;
      font-weight: bold;
      color: $warning-red;
    }

    .original-price {
      font-size: 14px;
      color: $text-tertiary;
      text-decoration: line-through;
      margin-left: 8px;
    }
  }

  // 药品名称
  .drug-name-row {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    margin-bottom: 16px;

    .essential-tag {
      flex-shrink: 0;
      padding: 2px 6px;
      background: rgba($primary-yellow, 0.15);
      color: #B8860B;
      font-size: 11px;
      font-weight: 500;
      border-radius: 4px;
    }

    .drug-name {
      flex: 1;
      font-size: 18px;
      font-weight: 600;
      color: $text-primary;
      line-height: 1.4;
      margin: 0;
    }
  }

  // 规格选择
  .spec-section {
    margin-bottom: 16px;

    .spec-label {
      font-size: 14px;
      color: $text-secondary;
      margin-bottom: 10px;
    }

    .spec-list {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;

      .spec-item {
        padding: 8px 16px;
        border: 1px solid $border-color;
        border-radius: 20px;
        font-size: 13px;
        color: $text-primary;
        cursor: pointer;
        transition: all 0.2s ease;

        &:active {
          opacity: 0.8;
        }

        &.active {
          border-color: $primary-yellow;
          background: rgba($primary-yellow, 0.1);
          color: #B8860B;
          font-weight: 500;
        }
      }
    }
  }

  // 处方药提示（复刻图片样式）
  .rx-notice {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 10px 0;
    margin-bottom: 12px;

    .rx-tag {
      font-size: 15px;
      font-weight: 600;
      color: #00c9a7;
      flex-shrink: 0;
    }

    .rx-desc {
      font-size: 14px;
      color: $text-secondary;
    }
  }

  // 信息卡片
  .info-cards {
    display: flex;
    flex-direction: column;
    gap: 8px;

    .info-card {
      display: flex;
      gap: 10px;
      padding: 10px 12px;
      background: #FAFAFA;
      border-radius: 10px;

      .card-icon {
        flex-shrink: 0;
        width: 28px;
        height: 28px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #fff;
        border-radius: 8px;
        font-size: 14px;
      }

      .card-content {
        flex: 1;
        min-width: 0;

        .card-title {
          font-size: 13px;
          font-weight: 600;
          color: $text-primary;
          margin-bottom: 2px;
        }

        .card-desc {
          font-size: 12px;
          color: $text-secondary;
          line-height: 1.4;
          display: -webkit-box;
          -webkit-line-clamp: 1;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
      }
    }
  }

  // 单行信息卡片
  .info-cards-single {
    .info-card-single {
      display: flex;
      align-items: center;
      padding: 12px 16px;
      background: #FAFAFA;
      border-radius: 12px;

      .single-item {
        display: flex;
        align-items: center;
        gap: 8px;

        .single-label {
          font-size: 14px;
          font-weight: 600;
          color: $text-primary;
          flex-shrink: 0;
        }

        .single-title {
          font-size: 14px;
          font-weight: 600;
          color: $text-primary;
          flex-shrink: 0;
        }

        .single-desc {
          font-size: 13px;
          color: $text-secondary;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          max-width: 120px;
        }
      }

      .single-divider {
        width: 1px;
        height: 16px;
        background: #E0E0E0;
        margin: 0 12px;
      }

      .single-arrow {
        flex-shrink: 0;
        margin-left: auto;
        color: $text-tertiary;

        .el-icon {
          font-size: 16px;
        }
      }
    }
  }

  // 用药信息预览（严格复刻图片样式）
  .drug-info-preview {
    display: flex;
    align-items: center;
    padding: 14px 16px;
    background: #FAFAFA;
    border-radius: 12px;
    cursor: pointer;

    .preview-left {
      display: flex;
      align-items: flex-start;
      gap: 12px;
      flex: 1;

      .label-vertical {
        font-size: 15px;
        font-weight: 600;
        color: $text-primary;
        line-height: 1.4;
        flex-shrink: 0;
        width: 36px;
      }

      .content-block {
        flex: 1;
        min-width: 0;

        .content-line {
          display: flex;
          align-items: baseline;
          gap: 8px;
          flex-wrap: wrap;

          .line-title {
            font-size: 14px;
            font-weight: 600;
            color: $text-primary;
            flex-shrink: 0;
          }

          .line-desc {
            font-size: 13px;
            color: $text-secondary;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            max-width: 150px;
          }
        }
      }
    }

    .preview-divider {
      width: 1px;
      height: 36px;
      background: #E0E0E0;
      margin: 0 12px;
      flex-shrink: 0;
    }

    .preview-right {
      display: flex;
      align-items: flex-start;
      gap: 10px;
      flex: 0.8;

      .label-single {
        font-size: 15px;
        font-weight: 600;
        color: $text-primary;
        flex-shrink: 0;
      }

      .content-block {
        flex: 1;
        min-width: 0;

        .content-line {
          .line-desc {
            font-size: 13px;
            color: $text-secondary;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            max-width: 120px;
          }
        }
      }
    }

    .preview-arrow {
      flex-shrink: 0;
      margin-left: 8px;
      color: $text-tertiary;

      .el-icon {
        font-size: 18px;
      }
    }
  }

  // 说明书横向滚动预览
  .instruction-scroll-preview {
    display: flex;
    align-items: center;
    padding: 12px 0;
    background: #FAFAFA;
    border-radius: 12px;
    position: relative;

    .scroll-container {
      display: flex;
      gap: 20px;
      overflow-x: auto;
      scroll-snap-type: x mandatory;
      padding: 0 16px;
      flex: 1;

      &::-webkit-scrollbar {
        display: none;
      }

      .scroll-item {
        flex-shrink: 0;
        scroll-snap-align: start;
        display: flex;
        flex-direction: column;
        gap: 4px;
        min-width: 140px;

        .scroll-label {
          font-size: 13px;
          font-weight: 600;
          color: $text-primary;
        }

        .scroll-value {
          font-size: 12px;
          color: $text-secondary;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          max-width: 200px;
        }
      }
    }

    .scroll-arrow {
      flex-shrink: 0;
      padding: 0 16px;
      color: $text-tertiary;
      cursor: pointer;

      &:active {
        opacity: 0.7;
      }

      .el-icon {
        font-size: 20px;
      }
    }
  }
}

// 说明书弹窗内容
.instruction-dialog-content {
  max-height: 60vh;
  overflow-y: auto;

  .instruction-row {
    display: flex;
    padding: 12px 0;
    border-bottom: 1px solid $border-light;

    &:last-child {
      border-bottom: none;
    }

    .row-label {
      width: 100px;
      flex-shrink: 0;
      font-size: 14px;
      font-weight: 500;
      color: $text-secondary;
    }

    .row-value {
      flex: 1;
      font-size: 14px;
      color: $text-primary;
      line-height: 1.6;
    }
  }
}

// Tab导航
.tab-section {
  display: flex;
  background: #fff;
  border-bottom: 1px solid $border-light;
  position: sticky;
  top: 0;
  z-index: 100;

  &.is-sticky {
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
        background: $primary-yellow;
        border-radius: 2px;
      }
    }
  }
}

// Tab内容区
.tab-content {
  min-height: 300px;
}

// 在售商家列表
.stores-list {
  padding: 12px;

  .store-card {
    background: #fff;
    border-radius: $card-radius;
    padding: 16px;
    margin-bottom: 12px;
    cursor: pointer;

    &:active {
      opacity: 0.9;
    }

    .store-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;

      .store-name {
        font-size: 15px;
        font-weight: 600;
        color: $text-primary;
      }

      .store-distance {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 13px;
        color: $text-secondary;

        .el-icon {
          font-size: 14px;
        }
      }
    }

    .store-body {
      margin-bottom: 10px;

      .store-price {
        display: flex;
        align-items: baseline;
        gap: 4px;
        margin-bottom: 8px;

        .price-symbol {
          font-size: 14px;
          color: $warning-red;
          font-weight: 600;
        }

        .price-value {
          font-size: 22px;
          font-weight: bold;
          color: $warning-red;
        }

        .original-price {
          font-size: 13px;
          color: $text-tertiary;
          text-decoration: line-through;
          margin-left: 6px;
        }
      }

      .store-info {
        display: flex;
        align-items: center;
        gap: 12px;
        font-size: 12px;
        color: $text-secondary;

        .delivery {
          padding: 2px 8px;
          background: rgba($success, 0.1);
          color: $success;
          border-radius: 4px;
        }

        .rating {
          display: flex;
          align-items: center;
          gap: 2px;
          color: $primary-yellow;

          .el-icon {
            font-size: 12px;
          }
        }
      }
    }

    .store-tags {
      display: flex;
      gap: 8px;

      .tag {
        padding: 2px 8px;
        background: #F5F5F5;
        color: $text-secondary;
        font-size: 11px;
        border-radius: 4px;
      }
    }
  }
}

// 医生问答列表
.qa-list {
  padding: 12px;

  .qa-card {
    background: #fff;
    border-radius: $card-radius;
    padding: 16px;
    margin-bottom: 12px;

    .doctor-info {
      display: flex;
      gap: 12px;
      margin-bottom: 16px;

      .doctor-avatar {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        object-fit: cover;
      }

      .doctor-meta {
        flex: 1;

        .doctor-name {
          font-size: 15px;
          font-weight: 600;
          color: $text-primary;
          margin-bottom: 4px;
        }

        .doctor-title {
          font-size: 13px;
          color: $primary-yellow-dark;
          margin-bottom: 2px;
        }

        .doctor-hospital {
          font-size: 12px;
          color: $text-tertiary;
        }
      }
    }

    .qa-content {
      .question,
      .answer {
        display: flex;
        gap: 8px;
        margin-bottom: 10px;

        .qa-label {
          flex-shrink: 0;
          width: 20px;
          height: 20px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: $primary-yellow;
          color: #fff;
          font-size: 12px;
          font-weight: 600;
          border-radius: 4px;

          &.answer-label {
            background: $success;
          }
        }

        .qa-text {
          flex: 1;
          font-size: 14px;
          color: $text-primary;
          line-height: 1.6;
        }
      }

      .answer {
        margin-bottom: 0;
      }
    }

    .qa-footer {
      margin-top: 12px;
      padding-top: 12px;
      border-top: 1px solid $border-light;

      .likes {
        font-size: 12px;
        color: $text-tertiary;
      }
    }
  }
}

// 评价列表
.reviews-list {
  padding: 12px;

  .review-card {
    background: #fff;
    border-radius: $card-radius;
    padding: 16px;
    margin-bottom: 12px;

    .review-header {
      display: flex;
      gap: 12px;
      margin-bottom: 12px;

      .user-avatar {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        object-fit: cover;
      }

      .user-info {
        flex: 1;

        .user-name {
          font-size: 14px;
          font-weight: 500;
          color: $text-primary;
          margin-bottom: 6px;
        }

        .review-meta {
          display: flex;
          align-items: center;
          gap: 12px;

          .rating {
            display: flex;
            gap: 2px;

            .el-icon {
              font-size: 12px;
              color: #DDD;

              &.is-active {
                color: $primary-yellow;
              }
            }
          }

          .review-date {
            font-size: 12px;
            color: $text-tertiary;
          }
        }
      }
    }

    .review-content {
      font-size: 14px;
      color: $text-primary;
      line-height: 1.6;
      margin-bottom: 12px;
    }

    .review-images {
      display: flex;
      gap: 8px;
      margin-bottom: 12px;

      .review-img {
        width: 80px;
        height: 80px;
        border-radius: 8px;
        object-fit: cover;
      }
    }

    .review-specs {
      font-size: 12px;
      color: $text-tertiary;
    }
  }
}

// 详情内容
.detail-content {
  padding: 12px;

  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 16px;
    padding-left: 12px;
    border-left: 4px solid $primary-yellow;
  }

  .instruction-section {
    background: #fff;
    border-radius: $card-radius;
    padding: 16px;
    margin-bottom: 12px;

    .instruction-table {
      .table-row {
        display: flex;
        padding: 12px 0;
        border-bottom: 1px solid $border-light;

        &:last-child {
          border-bottom: none;
        }

        .table-label {
          width: 100px;
          flex-shrink: 0;
          font-size: 13px;
          color: $text-secondary;
        }

        .table-value {
          flex: 1;
          font-size: 13px;
          color: $text-primary;
          line-height: 1.6;
        }
      }
    }
  }

  .product-images {
    background: #fff;
    border-radius: $card-radius;
    padding: 16px;

    .detail-images {
      display: flex;
      flex-direction: column;
      gap: 8px;

      img {
        width: 100%;
        border-radius: 8px;
      }
    }
  }
}

// 底部占位
.bottom-placeholder {
  height: 80px;
}

// 底部固定操作栏
.bottom-actions {
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

  .action-left {
    display: flex;
    gap: 16px;
    padding-right: 16px;
    border-right: 1px solid $border-light;

    .action-btn {
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

      &.health-butler {
        .butler-icon {
          width: 28px;
          height: 28px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: linear-gradient(135deg, #00c9a7 0%, #00bfa5 100%);
          border-radius: 50%;
          color: #fff;

          svg {
            width: 16px;
            height: 16px;
          }
        }

        span {
          color: #00c9a7;
          font-weight: 500;
        }
      }
    }
  }

  .action-right {
    flex: 1;
    display: flex;
    margin-left: 16px;

    .btn-buy {
      width: 100%;
      height: 44px;
      border: none;
      background: linear-gradient(135deg, #FFD700 0%, #E6C200 100%);
      color: #333;
      font-size: 15px;
      font-weight: 600;
      border-radius: 22px;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.2s ease;
      box-shadow: 0 4px 12px rgba(255, 215, 0, 0.3);

      &:active {
        opacity: 0.9;
        transform: scale(0.98);
      }

      .btn-text {
        font-size: 15px;
      }
    }
  }
}

// 加载状态
.loading-container {
  padding: 40px 16px;
}
</style>
