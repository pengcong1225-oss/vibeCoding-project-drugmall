<template>
  <div class="doctor-detail-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="title">{{ doctorInfo.name }}</span>
      <div class="header-actions">
        <button class="action-btn" @click="toggleFollow">
          <el-icon v-if="!isFollowed"><Star /></el-icon>
          <el-icon v-else class="followed"><StarFilled /></el-icon>
        </button>
        <button class="action-btn" @click="shareDoctor">
          <el-icon><Share /></el-icon>
        </button>
      </div>
    </div>

    <!-- 医生基本信息卡片 -->
    <div class="doctor-header-card">
      <div class="doctor-basic">
        <div class="avatar-section">
          <img :src="doctorInfo.avatar || defaultAvatar" :alt="doctorInfo.name" class="avatar" />
          <span v-if="doctorInfo.isOnline" class="online-dot"></span>
        </div>
        <div class="info-section">
          <div class="name-row">
            <span class="doctor-name">{{ doctorInfo.name }}</span>
            <span class="verify-badge">
              <el-icon><CircleCheck /></el-icon>
              资质认证
            </span>
            <button class="follow-btn" :class="{ followed: isFollowed }" @click="toggleFollow">
              <el-icon v-if="!isFollowed"><Plus /></el-icon>
              <span>{{ isFollowed ? '已关注' : '关注' }}</span>
            </button>
          </div>
          <div class="hospital-row">
            <span class="hospital-badge">三甲</span>
            <span class="hospital-name">{{ doctorInfo.hospital }}</span>
          </div>
          <div class="title-row">
            <span class="title-tag">{{ doctorInfo.title }}</span>
            <span class="dept-tag">{{ doctorInfo.department }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 专业擅长 -->
    <div class="section-card">
      <div class="section-header">
        <el-icon><User /></el-icon>
        <span class="section-title">专业擅长</span>
      </div>
      <div class="section-content">
        <p class="specialty-text">{{ doctorInfo.specialty }}</p>
      </div>
    </div>

    <!-- 个人简介 -->
    <div class="section-card">
      <div class="section-header">
        <el-icon><Document /></el-icon>
        <span class="section-title">个人简介</span>
      </div>
      <div class="section-content">
        <p class="intro-text">{{ doctorInfo.introduction || '暂无简介' }}</p>
      </div>
    </div>

    <!-- 在线问诊 -->
    <div class="section-card inquiry-section">
      <div class="section-header">
        <span class="section-title">在线问诊</span>
      </div>
      <div class="stats-bar">
        <div class="stat-item">
          <span class="stat-value">{{ formatRating(doctorInfo.rating) }}%</span>
          <span class="stat-label">好评率</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ formatCount(doctorInfo.consultationCount) }}</span>
          <span class="stat-label">接诊量</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ doctorInfo.waitTime || 12 }}分钟</span>
          <span class="stat-label">平均等待</span>
        </div>
      </div>

      <!-- 图文咨询卡片 -->
      <div class="service-card" @click="startInquiry">
        <div class="service-left">
          <div class="service-icon">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="service-info">
            <div class="service-name">图文咨询</div>
            <div class="service-price">¥{{ doctorInfo.price || 19.9 }}</div>
            <div class="service-desc">24h不限次图文沟通</div>
            <div class="service-guarantee">指定时间未联系患者全额退</div>
          </div>
        </div>
        <button class="inquiry-btn">去咨询</button>
      </div>
    </div>

    <!-- 问诊评价 -->
    <div class="section-card reviews-section">
      <div class="section-header">
        <span class="section-title">问诊评价</span>
        <span class="view-all" @click="viewAllReviews">
          查看全部 <el-icon><ArrowRight /></el-icon>
        </span>
      </div>

      <!-- 评价标签 -->
      <div class="review-tags">
        <button
          v-for="tag in reviewTags"
          :key="tag.name"
          :class="['review-tag', { active: selectedTag === tag.name }]"
          @click="selectTag(tag.name)"
        >
          {{ tag.name }} {{ tag.count }}
        </button>
      </div>

      <!-- 评价列表 -->
      <div class="review-list">
        <div v-for="review in filteredReviews" :key="review.id" class="review-item">
          <div class="review-header">
            <div class="reviewer-info">
              <span class="reviewer-name">{{ review.userName }}</span>
              <span class="review-type">{{ review.type }}</span>
            </div>
            <span class="review-date">{{ review.date }}</span>
          </div>
          <div class="review-content">
            <span class="satisfaction-tag" :class="review.satisfaction">{{ review.satisfactionText }}</span>
            <p class="review-text">{{ review.content }}</p>
          </div>
          <div class="review-tags-list">
            <span v-for="(tag, idx) in review.tags" :key="idx" class="tag">{{ tag }}</span>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredReviews.length === 0" class="empty-reviews">
        <el-empty description="暂无评价" :image-size="80" />
      </div>
    </div>

    <!-- 底部占位 -->
    <div class="bottom-placeholder"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Star,
  StarFilled,
  Share,
  CircleCheck,
  Plus,
  User,
  Document,
  ChatDotRound,
  ArrowRight
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 医生ID
const doctorId = computed(() => route.params.id as string)

// 默认头像
const defaultAvatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=doctor'

// 是否关注
const isFollowed = ref(false)

// 选中的评价标签
const selectedTag = ref('全部')

// 医生信息
interface DoctorDetail {
  id: string
  name: string
  title: string
  department: string
  hospital: string
  avatar: string
  isOnline: boolean
  specialty: string
  introduction: string
  rating: number
  consultationCount: number
  waitTime: number
  price: number
}

const doctorInfo = ref<DoctorDetail>({
  id: doctorId.value,
  name: '刘贞君',
  title: '主治医师',
  department: '皮肤科',
  hospital: '山东青岛中西医结合医院',
  avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=doctor1',
  isOnline: true,
  specialty: '擅长中西医结合诊疗银屑病，痤疮，湿疹，荨麻疹等常见皮肤病，对疑难皮肤病有丰富临床经验',
  introduction: '主治医师，硕士研究生，中医外科皮肤科专业。从事皮肤科临床工作10余年，擅长运用中西医结合方法治疗各种常见皮肤病及疑难皮肤病。在国家级期刊发表论文多篇，参与多项省市级科研课题。',
  rating: 0.98,
  consultationCount: 11000,
  waitTime: 12,
  price: 19.9
})

// 评价标签
const reviewTags = ref([
  { name: '全部', count: 3280 },
  { name: '湿疹', count: 275 },
  { name: '过敏性皮炎', count: 175 },
  { name: '银屑病', count: 142 },
  { name: '痤疮', count: 128 },
  { name: '荨麻疹', count: 98 },
])

// 评价列表
interface Review {
  id: string
  userName: string
  type: string
  date: string
  satisfaction: string
  satisfactionText: string
  content: string
  tags: string[]
}

const reviews = ref<Review[]>([
  {
    id: '1',
    userName: 'J**',
    type: '图文问诊',
    date: '2026.04.14',
    satisfaction: 'satisfied',
    satisfactionText: '满意',
    content: '医生耐心亲和，解答详细易懂，非常专业',
    tags: ['回复快', '解答详细']
  },
  {
    id: '2',
    userName: 'M**',
    type: '图文问诊',
    date: '2026.04.13',
    satisfaction: 'satisfied',
    satisfactionText: '满意',
    content: '医生很专业，给出的建议很中肯，用药后症状明显改善',
    tags: ['医术高明', '态度好']
  },
  {
    id: '3',
    userName: 'W**',
    type: '图文问诊',
    date: '2026.04.12',
    satisfaction: 'satisfied',
    satisfactionText: '满意',
    content: '回复及时，解释清楚，值得信赖的医生',
    tags: ['回复快']
  },
  {
    id: '4',
    userName: 'L**',
    type: '图文问诊',
    date: '2026.04.10',
    satisfaction: 'satisfied',
    satisfactionText: '满意',
    content: '医生很细心，问了很多细节，开的药效果也不错',
    tags: ['医术高明', '解答详细']
  },
])

// 过滤后的评价
const filteredReviews = computed(() => {
  if (selectedTag.value === '全部') {
    return reviews.value
  }
  return reviews.value.filter(review =>
    review.content.includes(selectedTag.value) ||
    review.tags.some(tag => tag.includes(selectedTag.value))
  )
})

// 初始化
onMounted(() => {
  // 实际项目中调用API获取医生详情
  // loadDoctorDetail()
})

// 加载医生详情
// const loadDoctorDetail = async () => {
//   try {
//     const res = await getDoctorDetail(doctorId.value)
//     doctorInfo.value = res
//   } catch (error) {
//     console.error('获取医生详情失败:', error)
//   }
// }

// 返回
const goBack = () => {
  router.back()
}

// 切换关注
const toggleFollow = () => {
  isFollowed.value = !isFollowed.value
  ElMessage.success(isFollowed.value ? '已关注' : '已取消关注')
}

// 分享医生
const shareDoctor = () => {
  ElMessage.info('分享功能开发中')
}

// 开始问诊
const startInquiry = () => {
  router.push({
    path: '/inquiry/pre',
    query: {
      doctorId: doctorInfo.value.id,
      doctorName: doctorInfo.value.name,
      doctorTitle: doctorInfo.value.title,
      department: doctorInfo.value.department,
      price: doctorInfo.value.price
    }
  })
}

// 选择评价标签
const selectTag = (tag: string) => {
  selectedTag.value = tag
}

// 查看全部评价
const viewAllReviews = () => {
  ElMessage.info('查看全部评价')
}

// 格式化好评率
const formatRating = (rating: number): string => {
  return (rating * 100).toFixed(2)
}

// 格式化接诊量
const formatCount = (count?: number): string => {
  if (!count) return '0'
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + '万'
  }
  return count.toString()
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;
@use '@/styles/inquiry-theme' as *;

.doctor-detail-page {
  min-height: 100vh;
  background: $inquiry-bg;
  padding-bottom: 20px;
}

// 顶部导航
.nav-header {
  display: flex;
  align-items: center;
  padding: $spacing-md;
  padding-top: calc($safe-area-top + $spacing-md);
  background: linear-gradient(135deg, $inquiry-primary 0%, $inquiry-primary-dark 100%);
  color: $text-white;
  position: sticky;
  top: 0;
  z-index: 100;

  .back-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border-radius: 50%;
    transition: all 0.2s;

    &:hover {
      background: rgba(255, 255, 255, 0.2);
    }
  }

  .title {
    font-size: $font-lg;
    font-weight: 600;
    flex: 1;
    text-align: center;
  }

  .header-actions {
    display: flex;
    gap: 8px;

    .action-btn {
      width: 36px;
      height: 36px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: transparent;
      border: none;
      color: $text-white;
      cursor: pointer;
      border-radius: 50%;

      &:hover {
        background: rgba(255, 255, 255, 0.2);
      }

      .followed {
        color: #FFD700;
      }
    }
  }
}

// 医生基本信息卡片
.doctor-header-card {
  background: linear-gradient(135deg, $inquiry-primary 0%, $inquiry-primary-dark 100%);
  padding: $spacing-md;
  padding-top: 0;

  .doctor-basic {
    display: flex;
    gap: 16px;
    background: $inquiry-card-bg;
    border-radius: 12px;
    padding: 16px;
    box-shadow: 0 2px 8px rgba(0, 201, 167, 0.08);

    .avatar-section {
      position: relative;
      flex-shrink: 0;

      .avatar {
        width: 70px;
        height: 70px;
        border-radius: 50%;
        object-fit: cover;
        background: $inquiry-bg;
      }

      .online-dot {
        position: absolute;
        bottom: 4px;
        right: 4px;
        width: 14px;
        height: 14px;
        background: $inquiry-online;
        border: 2px solid $inquiry-card-bg;
        border-radius: 50%;
      }
    }

    .info-section {
      flex: 1;
      min-width: 0;

      .name-row {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 8px;
        flex-wrap: wrap;

        .doctor-name {
          font-size: 18px;
          font-weight: 600;
          color: $inquiry-text-primary;
        }

        .verify-badge {
          display: flex;
          align-items: center;
          gap: 2px;
          font-size: 11px;
          color: $inquiry-primary;
          background: rgba($inquiry-primary, 0.1);
          padding: 2px 6px;
          border-radius: 4px;

          .el-icon {
            font-size: 12px;
          }
        }

        .follow-btn {
          display: flex;
          align-items: center;
          gap: 2px;
          padding: 4px 10px;
          background: $inquiry-primary;
          border: none;
          border-radius: 12px;
          font-size: 12px;
          color: $text-white;
          cursor: pointer;
          margin-left: auto;
          transition: all 0.2s;

          &.followed {
            background: $inquiry-bg;
            color: $inquiry-text-secondary;
          }

          &:active {
            opacity: 0.9;
          }
        }
      }

      .hospital-row {
        display: flex;
        align-items: center;
        gap: 6px;
        margin-bottom: 6px;

        .hospital-badge {
          padding: 1px 6px;
          background: rgba($inquiry-primary, 0.1);
          color: $inquiry-primary;
          border-radius: 4px;
          font-size: 10px;
          font-weight: 600;
        }

        .hospital-name {
          font-size: 13px;
          color: $inquiry-text-secondary;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }

      .title-row {
        display: flex;
        align-items: center;
        gap: 8px;

        .title-tag {
          font-size: 13px;
          color: $inquiry-text-secondary;
        }

        .dept-tag {
          font-size: 13px;
          color: $inquiry-text-tertiary;
        }
      }
    }
  }
}

// 通用卡片样式
.section-card {
  background: $inquiry-card-bg;
  margin: $spacing-md;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 201, 167, 0.06);

  .section-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;

    .el-icon {
      font-size: 18px;
      color: $inquiry-primary;
    }

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: $inquiry-text-primary;
    }

    .view-all {
      margin-left: auto;
      display: flex;
      align-items: center;
      gap: 2px;
      font-size: 13px;
      color: $inquiry-text-tertiary;
      cursor: pointer;

      .el-icon {
        font-size: 12px;
        color: $inquiry-text-tertiary;
      }

      &:active {
        color: $inquiry-primary;
      }
    }
  }

  .section-content {
    .specialty-text,
    .intro-text {
      font-size: 14px;
      color: $inquiry-text-secondary;
      line-height: 1.6;
    }
  }
}

// 在线问诊区域
.inquiry-section {
  .stats-bar {
    display: flex;
    justify-content: space-around;
    padding: 12px 0;
    margin-bottom: 16px;
    border-bottom: 1px solid rgba($inquiry-primary, 0.08);

    .stat-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 4px;

      .stat-value {
        font-size: 18px;
        font-weight: 600;
        color: $inquiry-text-primary;
      }

      .stat-label {
        font-size: 12px;
        color: $inquiry-text-tertiary;
      }
    }
  }

  .service-card {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px;
    background: $inquiry-bg;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.2s;

    &:active {
      background: darken($inquiry-bg, 3%);
    }

    .service-left {
      display: flex;
      gap: 12px;

      .service-icon {
        width: 48px;
        height: 48px;
        background: rgba($inquiry-primary, 0.1);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;

        .el-icon {
          font-size: 24px;
          color: $inquiry-primary;
        }
      }

      .service-info {
        .service-name {
          font-size: 16px;
          font-weight: 600;
          color: $inquiry-text-primary;
          margin-bottom: 4px;
        }

        .service-price {
          font-size: 20px;
          font-weight: 700;
          color: $inquiry-tag-price;
          margin-bottom: 4px;
        }

        .service-desc {
          font-size: 12px;
          color: $inquiry-text-secondary;
          margin-bottom: 2px;
        }

        .service-guarantee {
          font-size: 11px;
          color: $inquiry-text-tertiary;
        }
      }
    }

    .inquiry-btn {
      padding: 10px 20px;
      background: $inquiry-primary;
      border: none;
      border-radius: 20px;
      font-size: 14px;
      font-weight: 500;
      color: $text-white;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        background: $inquiry-primary-dark;
      }

      &:active {
        opacity: 0.9;
      }
    }
  }
}

// 评价区域
.reviews-section {
  .review-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 16px;

    .review-tag {
      padding: 6px 12px;
      background: $inquiry-bg;
      border: 1px solid transparent;
      border-radius: 14px;
      font-size: 12px;
      color: $inquiry-text-secondary;
      cursor: pointer;
      transition: all 0.2s;

      &.active {
        background: rgba($inquiry-primary, 0.1);
        border-color: $inquiry-primary;
        color: $inquiry-primary;
      }

      &:active {
        transform: scale(0.95);
      }
    }
  }

  .review-list {
    .review-item {
      padding: 16px 0;
      border-bottom: 1px solid rgba($inquiry-primary, 0.08);

      &:last-child {
        border-bottom: none;
      }

      .review-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;

        .reviewer-info {
          display: flex;
          align-items: center;
          gap: 8px;

          .reviewer-name {
            font-size: 14px;
            color: $inquiry-text-primary;
            font-weight: 500;
          }

          .review-type {
            font-size: 12px;
            color: $inquiry-text-tertiary;
          }
        }

        .review-date {
          font-size: 12px;
          color: $inquiry-text-tertiary;
        }
      }

      .review-content {
        margin-bottom: 8px;

        .satisfaction-tag {
          display: inline-block;
          padding: 2px 8px;
          border-radius: 4px;
          font-size: 11px;
          margin-bottom: 6px;

          &.satisfied {
            background: rgba($inquiry-primary, 0.1);
            color: $inquiry-primary;
          }

          &.neutral {
            background: rgba($warning, 0.1);
            color: $warning;
          }

          &.dissatisfied {
            background: rgba($error, 0.1);
            color: $error;
          }
        }

        .review-text {
          font-size: 14px;
          color: $inquiry-text-secondary;
          line-height: 1.5;
        }
      }

      .review-tags-list {
        display: flex;
        flex-wrap: wrap;
        gap: 6px;

        .tag {
          padding: 2px 8px;
          background: $inquiry-bg;
          border-radius: 4px;
          font-size: 11px;
          color: $inquiry-text-tertiary;
        }
      }
    }
  }

  .empty-reviews {
    padding: $spacing-lg 0;
  }
}

.bottom-placeholder {
  height: 20px;
}
</style>
