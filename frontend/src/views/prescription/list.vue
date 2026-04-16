<template>
  <div class="prescription-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="title">我的处方</span>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <template v-else>
      <!-- 状态筛选 -->
      <div class="status-tabs">
        <div
          v-for="tab in statusTabs"
          :key="tab.value"
          :class="['tab-item', { active: currentStatus === tab.value }]"
          @click="currentStatus = tab.value"
        >
          {{ tab.label }}
        </div>
      </div>

      <!-- 处方列表 -->
      <div class="prescription-list">
        <div v-if="filteredList.length === 0" class="empty-state">
          <el-empty :description="`暂无${currentStatus === 'all' ? '' : statusTabs.find(t => t.value === currentStatus)?.label}处方`" :image-size="120">
            <el-button type="primary" size="small" round>去问诊开方</el-button>
          </el-empty>
        </div>

        <div
          v-for="item in filteredList"
          :key="item.id"
          class="prescription-card"
          @click="viewDetail(item)"
        >
          <div class="card-header">
            <div class="prescription-no">
              <el-icon><Document /></el-icon>
              <span>{{ item.prescriptionNo }}</span>
            </div>
            <div :class="['status-badge', item.status]">{{ item.statusText }}</div>
          </div>

          <div class="card-body">
            <div class="info-row">
              <span class="label">开方医生</span>
              <span class="value">{{ item.doctorName }}</span>
              <span class="tag doctor-tag">{{ item.title || '医生' }}</span>
            </div>
            <div class="info-row">
              <span class="label">医院科室</span>
              <span class="value">{{ item.hospital }} · {{ item.department }}</span>
            </div>
            <div class="info-row diagnosis-row">
              <span class="label">诊断结果</span>
              <span class="value diagnosis">{{ item.diagnosis }}</span>
            </div>
            <div v-if="item.drugs?.length" class="drugs-preview">
              <span class="drugs-label">药品清单</span>
              <div class="drugs-tags">
                <span v-for="(drug, idx) in item.drugs.slice(0, 3)" :key="idx" class="drug-tag">
                  {{ drug.name }}
                </span>
                <span v-if="item.drugs.length > 3" class="more-tag">
                  +{{ item.drugs.length - 3 }}
                </span>
              </div>
            </div>
          </div>

          <div class="card-footer">
            <span class="time">
              <el-icon><Clock /></el-icon>
              {{ formatTime(item.createTime) }}
            </span>
            <div class="actions">
              <span v-if="item.status === 'active'" class="action-btn buy-btn" @click.stop="buyDrugs(item)">
                立即购买
              </span>
              <span class="action-btn detail-btn" @click.stop="viewDetail(item)">
                查看详情
                <el-icon><ArrowRight /></el-icon>
              </span>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Document, Clock, ArrowRight } from '@element-plus/icons-vue'
import { getPrescriptionList, type Prescription } from '@/api/modules/prescription'

const router = useRouter()
const loading = ref(false)
const currentStatus = ref('all')

const statusTabs = [
  { label: '全部', value: 'all' },
  { label: '待审核', value: 'pending' },
  { label: '已生效', value: 'active' },
  { label: '已失效', value: 'expired' }
]

const prescriptionList = ref<Prescription[]>([])

const filteredList = computed(() => {
  if (currentStatus.value === 'all') {
    return prescriptionList.value
  }
  return prescriptionList.value.filter(item => item.status === currentStatus.value)
})

const goBack = () => {
  router.back()
}

const viewDetail = (item: Prescription) => {
  ElMessage.info(`查看处方详情：${item.prescriptionNo}`)
}

const buyDrugs = (item: Prescription) => {
  router.push(`/order/confirm?prescriptionId=${item.id}`)
}

const formatTime = (time: string) => {
  return time?.replace('T', ' ')?.substring(0, 16) || ''
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getPrescriptionList()
    prescriptionList.value = Array.isArray(res) ? res : []
  } catch (error) {
    console.error('获取处方列表失败:', error)
    prescriptionList.value = [
      {
        id: 'PRE001',
        prescriptionNo: 'P202404070001',
        doctorName: '张医生',
        title: '副主任医师',
        hospital: '北京协和医院',
        department: '心内科',
        diagnosis: '上呼吸道感染',
        status: 'active',
        statusText: '已生效',
        createTime: '2024-04-07 14:30:00',
        drugs: [
          { name: '阿莫西林胶囊', spec: '0.25g*24粒' },
          { name: '布洛芬缓释胶囊', spec: '0.3g*20粒' },
          { name: '感冒清热颗粒', spec: '12g*10袋' }
        ]
      },
      {
        id: 'PRE002',
        prescriptionNo: 'P202404050002',
        doctorName: '李医生',
        title: '主任医师',
        hospital: '北京协和医院',
        department: '消化内科',
        diagnosis: '急性肠胃炎',
        status: 'expired',
        statusText: '已失效',
        createTime: '2024-04-05 10:15:00',
        drugs: [
          { name: '蒙脱石散', spec: '3g*10袋' },
          { name: '诺氟沙星胶囊', spec: '0.1g*24粒' }
        ]
      }
    ]
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.prescription-page {
  min-height: 100vh;
  background: $bg-primary;
}

.nav-header {
  display: flex;
  align-items: center;
  padding: $spacing-md;
  padding-top: calc($safe-area-top + $spacing-md);
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
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
    margin-right: 36px;
  }
}

.loading-container {
  padding: $spacing-lg;
  margin-top: $spacing-xl;
}

.status-tabs {
  display: flex;
  gap: $spacing-xs;
  padding: $spacing-md;
  background: $bg-white;
  overflow-x: auto;

  &::-webkit-scrollbar {
    display: none;
  }

  .tab-item {
    flex: 1;
    text-align: center;
    padding: $spacing-sm $spacing-md;
    font-size: $font-sm;
    color: $text-secondary;
    cursor: pointer;
    position: relative;
    border-radius: $radius-full;
    transition: all 0.2s;

    &:hover {
      background: rgba($primary, 0.05);
      color: $primary;
    }

    &.active {
      background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
      color: $text-white;
      font-weight: 500;
      box-shadow: 0 2px 8px rgba($primary, 0.3);
    }
  }
}

.prescription-list {
  padding: $spacing-md;

  .empty-state {
    margin-top: $spacing-xxl;
  }
}

.prescription-card {
  background: $bg-white;
  border-radius: $radius-lg;
  padding: $spacing-lg;
  margin-bottom: $spacing-md;
  box-shadow: $shadow-sm;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: $shadow-md;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $spacing-md;
    padding-bottom: $spacing-md;
    border-bottom: 1px solid $border-light;

    .prescription-no {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      font-size: $font-sm;
      color: $text-secondary;

      .el-icon {
        color: $primary;
        font-size: 16px;
      }
    }

    .status-badge {
      padding: $spacing-xs $spacing-sm;
      border-radius: $radius-full;
      font-size: $font-xs;
      font-weight: 500;

      &.pending {
        background: #FFF7E6;
        color: #FAAD14;
      }

      &.active {
        background: rgba($success, 0.08);
        color: $success;
      }

      &.expired {
        background: $bg-gray;
        color: $text-tertiary;
      }
    }
  }

  .card-body {
    .info-row {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      margin-bottom: $spacing-sm;
      font-size: $font-sm;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        color: $text-tertiary;
        min-width: 70px;
      }

      .value {
        color: $text-primary;
        flex: 1;
      }

      .doctor-tag {
        background: rgba($primary, 0.08);
        color: $primary;
        padding: 2px 8px;
        border-radius: $radius-sm;
        font-size: $font-xs;
      }

      &.diagnosis-row {
        margin-top: $spacing-md;
        padding-top: $spacing-md;
        border-top: 1px dashed $border-light;

        .diagnosis {
          color: $error;
          font-weight: 500;
        }
      }
    }

    .drugs-preview {
      margin-top: $spacing-md;
      padding-top: $spacing-md;
      border-top: 1px dashed $border-light;

      .drugs-label {
        font-size: $font-xs;
        color: $text-tertiary;
        margin-bottom: $spacing-sm;
        display: block;
      }

      .drugs-tags {
        display: flex;
        flex-wrap: wrap;
        gap: $spacing-xs;

        .drug-tag,
        .more-tag {
          padding: $spacing-xs $spacing-sm;
          border-radius: $radius-sm;
          font-size: $font-xs;
        }

        .drug-tag {
          background: rgba($success, 0.08);
          color: $success;
        }

        .more-tag {
          background: $bg-gray;
          color: $text-tertiary;
        }
      }
    }
  }

  .card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: $spacing-md;
    padding-top: $spacing-md;
    border-top: 1px solid $border-light;

    .time {
      display: flex;
      align-items: center;
      gap: $spacing-xs;
      font-size: $font-xs;
      color: $text-tertiary;

      .el-icon {
        font-size: 12px;
      }
    }

    .actions {
      display: flex;
      gap: $spacing-sm;

      .action-btn {
        padding: $spacing-xs $spacing-md;
        border-radius: $radius-full;
        font-size: $font-xs;
        cursor: pointer;
        transition: all 0.2s;
        display: flex;
        align-items: center;
        gap: 4px;

        &.buy-btn {
          background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
          color: $text-white;
          box-shadow: 0 2px 8px rgba($primary, 0.3);

          &:hover {
            opacity: 0.9;
            transform: scale(1.02);
          }
        }

        &.detail-btn {
          color: $primary;
          background: rgba($primary, 0.06);

          &:hover {
            background: rgba($primary, 0.12);
          }

          .el-icon {
            font-size: 12px;
          }
        }
      }
    }
  }
}
</style>
