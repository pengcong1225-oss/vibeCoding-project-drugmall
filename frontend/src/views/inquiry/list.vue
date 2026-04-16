<template>
  <div class="inquiry-list-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <img :src="userMenuIcons.arrowLeft" class="nav-icon" alt="返回" />
      </div>
      <span class="title">咨询记录</span>
      <div class="placeholder"></div>
    </div>

    <!-- 状态筛选标签 -->
    <div class="filter-tabs">
      <div
        v-for="tab in filterTabs"
        :key="tab.value"
        :class="['filter-tab', { active: currentTab === tab.value }]"
        @click="handleTabChange(tab.value)"
      >
        {{ tab.label }}
      </div>
    </div>

    <!-- 咨询记录列表 -->
    <div class="inquiry-list" ref="listRef">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
        <el-skeleton :rows="3" animated style="margin-top: 12px" />
      </div>

      <!-- 空状态 -->
      <div v-else-if="filteredList.length === 0" class="empty-state">
        <el-empty :description="emptyText" :image-size="120" />
      </div>

      <!-- 列表内容 -->
      <template v-else>
        <div
          v-for="item in filteredList"
          :key="item.id"
          class="inquiry-card"
          @click="goToDetail(item)"
        >
          <!-- 卡片头部 -->
          <div class="card-header">
            <div class="doctor-info">
              <img :src="item.doctorAvatar || defaultAvatar" class="doctor-avatar" alt="医生头像" />
              <div class="doctor-detail">
                <div class="doctor-name-row">
                  <span class="doctor-name">{{ item.doctorName }}</span>
                  <span class="doctor-title">{{ item.doctorTitle }}</span>
                </div>
                <div class="hospital-name">{{ item.hospital }}</div>
              </div>
            </div>
            <div :class="['status-tag', getStatusClass(item.status)]">
              {{ getStatusText(item.status) }}
            </div>
          </div>

          <!-- 卡片内容 -->
          <div class="card-content">
            <div class="patient-info">
              <img :src="userMenuIcons.user" class="info-icon" alt="就诊人" />
              <span class="label">就诊人：</span>
              <span class="value">{{ item.patientName }} {{ item.patientGender === 'male' ? '男' : '女' }} {{ item.patientAge }}岁</span>
            </div>
            <div class="symptom-info">
              <img :src="userMenuIcons.firstAidKit" class="info-icon" alt="病情" />
              <span class="label">病情：</span>
              <span class="value">{{ item.symptom }}</span>
            </div>
            <div class="time-info">
              <img :src="userMenuIcons.document" class="info-icon" alt="时间" />
              <span class="label">咨询时间：</span>
              <span class="value">{{ item.createTime }}</span>
            </div>
          </div>

          <!-- 卡片底部 -->
          <div class="card-footer">
            <div class="price-info">
              <span class="price">¥{{ item.price }}</span>
              <span class="service-type">图文咨询</span>
            </div>
            <div class="action-buttons">
              <button
                v-if="item.status === 'pending'"
                class="action-btn primary"
                @click.stop="goToPay(item)"
              >
                去支付
              </button>
              <button
                v-else-if="item.status === 'in_progress'"
                class="action-btn primary"
                @click.stop="goToChat(item)"
              >
                继续咨询
              </button>
              <button
                v-else-if="item.status === 'completed' && !item.isReviewed"
                class="action-btn"
                @click.stop="goToReview(item)"
              >
                去评价
              </button>
              <button
                v-else
                class="action-btn"
                @click.stop="goToDetail(item)"
              >
                查看详情
              </button>
            </div>
          </div>
        </div>

        <!-- 加载更多 -->
        <div v-if="hasMore" class="load-more">
          <button class="load-more-btn" @click="loadMore" :disabled="loadingMore">
            <span v-if="loadingMore">
              <el-icon class="loading-icon"><Loading /></el-icon>
              加载中...
            </span>
            <span v-else>加载更多</span>
          </button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { getConsultationList } from '@/api/modules/inquiry'
import { userMenuIcons, mockInquiryList } from '@/api/mock'
import type { Consultation } from '@/api/modules/inquiry'

const router = useRouter()

// 默认头像 - 使用写实风格医生图标
const defaultAvatar = 'https://img.icons8.com/color/96/doctor-male.png'

// 筛选标签
const filterTabs = [
  { label: '全部', value: 'all' },
  { label: '进行中', value: 'in_progress' },
  { label: '已完成', value: 'completed' },
  { label: '待评价', value: 'pending_review' }
]

// 当前选中的标签
const currentTab = ref('all')

// 加载状态
const loading = ref(false)
const loadingMore = ref(false)
const hasMore = ref(false)

// 咨询列表
const inquiryList = ref<Consultation[]>([])

// 根据当前标签过滤列表
const filteredList = computed(() => {
  if (currentTab.value === 'all') {
    return inquiryList.value
  }
  return inquiryList.value.filter(item => {
    if (currentTab.value === 'pending_review') {
      return item.status === 'completed' && !item.isReviewed
    }
    return item.status === currentTab.value
  })
})

// 空状态文本
const emptyText = computed(() => {
  const map: Record<string, string> = {
    all: '暂无咨询记录',
    in_progress: '暂无进行中的咨询',
    completed: '暂无已完成的咨询',
    pending_review: '暂无待评价的咨询'
  }
  return map[currentTab.value] || '暂无数据'
})

// 获取状态文本
const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待支付',
    waiting: '等待接诊',
    in_progress: '进行中',
    completed: '已完成',
    cancelled: '已取消',
    refunded: '已退款'
  }
  return map[status] || status
}

// 获取状态样式类
const getStatusClass = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    waiting: 'info',
    in_progress: 'primary',
    completed: 'success',
    cancelled: 'default',
    refunded: 'default'
  }
  return map[status] || 'default'
}

// 加载咨询列表
const loadInquiryList = async (isLoadMore = false) => {
  if (isLoadMore) {
    loadingMore.value = true
  } else {
    loading.value = true
  }

  try {
    // 实际项目中调用API
    const res = await getConsultationList(currentTab.value === 'all' ? undefined : currentTab.value)

    if (Array.isArray(res)) {
      if (isLoadMore) {
        inquiryList.value.push(...res)
      } else {
        inquiryList.value = res
      }
    } else {
      // 使用模拟数据
      inquiryList.value = mockInquiryList
    }

    hasMore.value = false
  } catch (error) {
    console.error('获取咨询记录失败:', error)
    // 使用模拟数据
    inquiryList.value = mockInquiryList
    ElMessage.error('获取咨询记录失败')
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

// 切换标签
const handleTabChange = (tab: string) => {
  currentTab.value = tab
  loadInquiryList()
}

// 加载更多
const loadMore = () => {
  loadInquiryList(true)
}

// 返回
const goBack = () => {
  router.back()
}

// 跳转到详情
const goToDetail = (item: any) => {
  router.push({
    path: `/inquiry/chat`,
    query: {
      consultationId: item.id,
      doctorId: item.doctorId,
      doctorName: item.doctorName
    }
  })
}

// 去支付
const goToPay = (item: any) => {
  router.push({
    path: `/inquiry/pay/${item.id}`,
    query: {
      doctorId: item.doctorId,
      doctorName: item.doctorName,
      doctorTitle: item.doctorTitle,
      hospital: item.hospital,
      department: item.department,
      patientId: item.patientId,
      patientName: item.patientName,
      patientGender: item.patientGender,
      patientAge: item.patientAge,
      symptom: item.symptom,
      price: item.price
    }
  })
}

// 继续咨询
const goToChat = (item: any) => {
  router.push({
    path: `/inquiry/chat`,
    query: {
      consultationId: item.id,
      doctorId: item.doctorId,
      doctorName: item.doctorName
    }
  })
}

// 去评价
const goToReview = (item: any) => {
  ElMessage.info('评价功能开发中')
}

onMounted(() => {
  loadInquiryList()
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

$primary-green: #00C9A7;
$primary-green-light: #00b894;

.inquiry-list-page {
  min-height: 100vh;
  background: $bg-primary;
}

// 顶部导航
.nav-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  padding-top: calc($safe-area-top + 12px);
  background: linear-gradient(135deg, $primary-green 0%, $primary-green-light 100%);
  color: white;
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

    &:active {
      background: rgba(255, 255, 255, 0.2);
    }

    .nav-icon {
      width: 24px;
      height: 24px;
      object-fit: contain;
      filter: brightness(0) invert(1);
    }
  }

  .title {
    font-size: 17px;
    font-weight: 600;
  }

  .placeholder {
    width: 36px;
  }
}

// 筛选标签
.filter-tabs {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  background: white;
  border-bottom: 1px solid $border-light;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;

  &::-webkit-scrollbar {
    display: none;
  }

  .filter-tab {
    padding: 8px 16px;
    background: $bg-primary;
    border-radius: 16px;
    font-size: 14px;
    color: $text-secondary;
    cursor: pointer;
    white-space: nowrap;
    transition: all 0.2s;

    &.active {
      background: $primary-green;
      color: white;
    }

    &:active {
      transform: scale(0.95);
    }
  }
}

// 列表容器
.inquiry-list {
  padding: 12px 16px;

  .loading-container {
    padding: 20px 0;
  }

  .empty-state {
    margin-top: 60px;
  }
}

// 咨询卡片
.inquiry-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.2s;

  &:active {
    transform: scale(0.99);
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 12px;

    .doctor-info {
      display: flex;
      gap: 12px;

      .doctor-avatar {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        object-fit: cover;
      }

      .doctor-detail {
        .doctor-name-row {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 4px;

          .doctor-name {
            font-size: 16px;
            font-weight: 600;
            color: $text-primary;
          }

          .doctor-title {
            font-size: 13px;
            color: $text-secondary;
          }
        }

        .hospital-name {
          font-size: 13px;
          color: $text-tertiary;
        }
      }
    }

    .status-tag {
      padding: 4px 10px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 500;

      &.primary {
        background: rgba($primary-green, 0.1);
        color: $primary-green;
      }

      &.success {
        background: rgba($success, 0.1);
        color: $success;
      }

      &.warning {
        background: rgba($warning, 0.1);
        color: $warning;
      }

      &.info {
        background: rgba($info, 0.1);
        color: $info;
      }

      &.default {
        background: $bg-primary;
        color: $text-secondary;
      }
    }
  }

  .card-content {
    padding: 12px 0;
    border-top: 1px solid $border-light;
    border-bottom: 1px solid $border-light;
    margin-bottom: 12px;

    .patient-info,
    .symptom-info,
    .time-info {
      display: flex;
      align-items: center;
      margin-bottom: 8px;

      &:last-child {
        margin-bottom: 0;
      }

      .info-icon {
        width: 16px;
        height: 16px;
        object-fit: contain;
        margin-right: 6px;
        opacity: 0.6;
      }

      .label {
        font-size: 13px;
        color: $text-tertiary;
        flex-shrink: 0;
      }

      .value {
        font-size: 13px;
        color: $text-secondary;
        flex: 1;
      }
    }

    .symptom-info {
      .value {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }

  .card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .price-info {
      display: flex;
      align-items: center;
      gap: 8px;

      .price {
        font-size: 18px;
        font-weight: 700;
        color: $price-red;
      }

      .service-type {
        font-size: 12px;
        color: $text-tertiary;
        padding: 2px 8px;
        background: $bg-primary;
        border-radius: 4px;
      }
    }

    .action-buttons {
      .action-btn {
        padding: 8px 16px;
        border-radius: 16px;
        font-size: 13px;
        cursor: pointer;
        border: none;
        transition: all 0.2s;

        &.primary {
          background: $primary-green;
          color: white;

          &:active {
            opacity: 0.9;
          }
        }

        &:not(.primary) {
          background: $bg-primary;
          color: $text-secondary;

          &:active {
            background: darken($bg-primary, 5%);
          }
        }
      }
    }
  }
}

// 加载更多
.load-more {
  padding: 20px;
  text-align: center;

  .load-more-btn {
    padding: 10px 24px;
    background: white;
    border: 1px solid $border-light;
    border-radius: 20px;
    font-size: 14px;
    color: $text-secondary;
    cursor: pointer;
    transition: all 0.2s;

    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .loading-icon {
      animation: rotate 1s linear infinite;
      margin-right: 4px;
    }

    &:active:not(:disabled) {
      background: $bg-primary;
    }
  }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
