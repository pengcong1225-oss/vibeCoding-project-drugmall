<script setup lang="ts">
import { computed } from 'vue'
import type { ElectronicPrescription } from '@/stores/prescription'

/**
 * 电子处方卡片组件
 * 用于展示处方开具成功后的电子处方信息
 */
interface Props {
  prescription: ElectronicPrescription
  showActions?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  showActions: true
})

const emit = defineEmits<{
  (e: 'buy', prescription: ElectronicPrescription): void
  (e: 'download', prescription: ElectronicPrescription): void
}>()

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 计算药品总数
const totalQuantity = computed(() => {
  return props.prescription.drugs.reduce((sum, drug) => sum + drug.quantity, 0)
})

// 处理购买
const handleBuy = () => {
  emit('buy', props.prescription)
}

// 处理下载
const handleDownload = () => {
  emit('download', props.prescription)
}
</script>

<template>
  <div class="prescription-card">
    <!-- 处方头部 -->
    <div class="card-header">
      <div class="header-left">
        <div class="rx-badge">Rx</div>
        <span class="prescription-title">电子处方笺</span>
      </div>
      <div class="prescription-no">
        编号：{{ prescription.prescriptionNo }}
      </div>
    </div>

    <!-- 处方内容 -->
    <div class="card-body">
      <!-- 患者信息 -->
      <div class="patient-info">
        <div class="info-row">
          <span class="label">姓名</span>
          <span class="value">{{ prescription.patientName }}</span>
        </div>
        <div class="info-row">
          <span class="label">性别</span>
          <span class="value">{{ prescription.patientGender }}</span>
        </div>
        <div class="info-row">
          <span class="label">年龄</span>
          <span class="value">{{ prescription.patientAge }}岁</span>
        </div>
        <div class="info-row">
          <span class="label">开具时间</span>
          <span class="value">{{ formatDate(prescription.createTime) }}</span>
        </div>
      </div>

      <!-- 诊断结果 -->
      <div class="diagnosis-section">
        <div class="section-title">
          <el-icon><FirstAidKit /></el-icon>
          <span>诊断结果</span>
        </div>
        <div class="diagnosis-content">{{ prescription.diagnosis }}</div>
      </div>

      <!-- 药品清单 -->
      <div class="drugs-section">
        <div class="section-title">
          <el-icon><MedicineBottle /></el-icon>
          <span>药品清单（{{ prescription.drugs.length }}种，共{{ totalQuantity }}件）</span>
        </div>
        <div class="drugs-list">
          <div
            v-for="(drug, index) in prescription.drugs"
            :key="drug.id"
            class="drug-item"
          >
            <div class="drug-index">{{ index + 1 }}</div>
            <div class="drug-info">
              <div class="drug-name-row">
                <span class="drug-name">{{ drug.name }}</span>
                <span class="drug-spec">{{ drug.spec }}</span>
              </div>
              <div class="drug-usage">
                <span class="usage-tag">{{ drug.usage }}</span>
                <span class="frequency-tag">{{ drug.frequency }}</span>
                <span class="days-tag">{{ drug.days }}天</span>
              </div>
            </div>
            <div class="drug-quantity">x{{ drug.quantity }}</div>
          </div>
        </div>
      </div>

      <!-- 医生嘱咐 -->
      <div v-if="prescription.doctorAdvice" class="advice-section">
        <div class="section-title">
          <el-icon><ChatDotRound /></el-icon>
          <span>医生嘱咐</span>
        </div>
        <div class="advice-content">{{ prescription.doctorAdvice }}</div>
      </div>

      <!-- 注意事项 -->
      <div v-if="prescription.precautions?.length" class="precautions-section">
        <div class="section-title warning">
          <el-icon><Warning /></el-icon>
          <span>注意事项</span>
        </div>
        <ul class="precautions-list">
          <li v-for="(item, index) in prescription.precautions" :key="index">
            {{ item }}
          </li>
        </ul>
      </div>

      <!-- 金额信息 -->
      <div class="amount-section">
        <div class="amount-row">
          <span class="amount-label">药品金额</span>
          <span class="amount-value">¥{{ prescription.totalAmount.toFixed(2) }}</span>
        </div>
      </div>
    </div>

    <!-- 底部操作 -->
    <div v-if="showActions" class="card-footer">
      <button class="action-btn secondary" @click="handleDownload">
        <el-icon><Download /></el-icon>
        <span>下载处方</span>
      </button>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.prescription-card {
  background: $bg-white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: $shadow-lg;
  border: 1px solid $border-light;
}

// 头部样式
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);

  .header-left {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .rx-badge {
    width: 32px;
    height: 32px;
    background: #fff;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    font-weight: 700;
    color: #E74C3C;
    border: 2px solid #E74C3C;
  }

  .prescription-title {
    font-size: 18px;
    font-weight: 600;
    color: #fff;
  }

  .prescription-no {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.8);
    background: rgba(255, 255, 255, 0.2);
    padding: 4px 10px;
    border-radius: 12px;
  }
}

// 内容区域
.card-body {
  padding: 20px;
}

// 患者信息
.patient-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding-bottom: 16px;
  border-bottom: 1px dashed $border-light;

  .info-row {
    display: flex;
    align-items: center;
    gap: 8px;

    .label {
      font-size: 13px;
      color: $text-tertiary;
      min-width: 56px;
    }

    .value {
      font-size: 14px;
      color: $text-primary;
      font-weight: 500;
    }

    &:nth-child(4) {
      grid-column: span 2;
    }
  }
}

// 通用区块样式
.section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 12px;

  .el-icon {
    font-size: 16px;
    color: $primary;
  }

  &.warning {
    .el-icon {
      color: $warning;
    }
  }
}

// 诊断结果
.diagnosis-section {
  margin-top: 16px;
  padding-bottom: 16px;
  border-bottom: 1px dashed $border-light;

  .diagnosis-content {
    font-size: 15px;
    color: $error;
    font-weight: 500;
    padding: 12px;
    background: rgba($error, 0.05);
    border-radius: 8px;
    border-left: 3px solid $error;
  }
}

// 药品清单
.drugs-section {
  margin-top: 16px;
  padding-bottom: 16px;
  border-bottom: 1px dashed $border-light;
}

.drugs-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.drug-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: $bg-gray;
  border-radius: 10px;

  .drug-index {
    width: 24px;
    height: 24px;
    background: $primary;
    color: #fff;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    font-weight: 600;
    flex-shrink: 0;
  }

  .drug-info {
    flex: 1;
    min-width: 0;
  }

  .drug-name-row {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
    flex-wrap: wrap;

    .drug-name {
      font-size: 15px;
      font-weight: 600;
      color: $text-primary;
    }

    .drug-spec {
      font-size: 12px;
      color: $text-tertiary;
    }
  }

  .drug-usage {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;

    span {
      font-size: 12px;
      padding: 3px 8px;
      border-radius: 4px;
      background: $bg-white;
      color: $text-secondary;
      border: 1px solid $border-light;
    }
  }

  .drug-quantity {
    font-size: 14px;
    font-weight: 600;
    color: $text-primary;
    flex-shrink: 0;
  }
}

// 医生嘱咐
.advice-section {
  margin-top: 16px;
  padding-bottom: 16px;
  border-bottom: 1px dashed $border-light;

  .advice-content {
    font-size: 14px;
    color: $text-secondary;
    line-height: 1.6;
    padding: 12px;
    background: $bg-primary;
    border-radius: 8px;
  }
}

// 注意事项
.precautions-section {
  margin-top: 16px;
  padding-bottom: 16px;
  border-bottom: 1px dashed $border-light;

  .precautions-list {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      font-size: 13px;
      color: $text-secondary;
      padding: 8px 0;
      padding-left: 20px;
      position: relative;
      line-height: 1.5;

      &::before {
        content: '';
        position: absolute;
        left: 6px;
        top: 14px;
        width: 6px;
        height: 6px;
        background: $warning;
        border-radius: 50%;
      }

      &:not(:last-child) {
        border-bottom: 1px solid $border-light;
      }
    }
  }
}

// 金额信息
.amount-section {
  margin-top: 16px;
  padding-top: 16px;

  .amount-row {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .amount-label {
      font-size: 14px;
      color: $text-secondary;
    }

    .amount-value {
      font-size: 20px;
      font-weight: 700;
      color: $price-red;
    }
  }
}

// 底部操作
.card-footer {
  display: flex;
  gap: 12px;
  padding: 16px 20px;
  background: $bg-gray;
  border-top: 1px solid $border-light;

  .action-btn {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    padding: 12px 20px;
    border-radius: 10px;
    font-size: 15px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
    border: none;

    .el-icon {
      font-size: 16px;
    }

    &.primary {
      background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
      color: #fff;
      box-shadow: 0 4px 12px rgba($primary, 0.3);

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 16px rgba($primary, 0.4);
      }
    }

    &.secondary {
      background: $bg-white;
      color: $text-secondary;
      border: 1px solid $border-light;

      &:hover {
        background: $bg-primary;
        border-color: $border-color;
      }
    }
  }
}

// 响应式适配
@media (max-width: 375px) {
  .card-header {
    padding: 12px 16px;

    .prescription-title {
      font-size: 16px;
    }

    .prescription-no {
      font-size: 11px;
      padding: 3px 8px;
    }
  }

  .card-body {
    padding: 16px;
  }

  .patient-info {
    grid-template-columns: 1fr;

    .info-row:nth-child(4) {
      grid-column: span 1;
    }
  }

  .drug-item {
    .drug-name {
      font-size: 14px;
    }
  }

  .card-footer {
    padding: 12px 16px;

    .action-btn {
      padding: 10px 16px;
      font-size: 14px;
    }
  }
}
</style>
