<script setup lang="ts">
/**
 * 处方流程步骤条组件
 * 步骤：填写信息 -> 医生开方 -> 支付订单
 */
interface Props {
  currentStep: number // 当前步骤：1, 2, 3
}

const props = withDefaults(defineProps<Props>(), {
  currentStep: 1
})

const steps = [
  { id: 1, label: '填写信息' },
  { id: 2, label: '医生开方' },
  { id: 3, label: '支付订单' }
]

// 获取步骤状态
const getStepStatus = (stepId: number) => {
  if (stepId < props.currentStep) return 'completed'
  if (stepId === props.currentStep) return 'active'
  return 'pending'
}
</script>

<template>
  <div class="step-bar">
    <div class="step-list">
      <div
        v-for="(step, index) in steps"
        :key="step.id"
        :class="['step-item', getStepStatus(step.id)]"
      >
        <!-- 步骤节点 -->
        <div class="step-node">
          <div class="step-circle">
            <template v-if="getStepStatus(step.id) === 'completed'">
              <el-icon><Check /></el-icon>
            </template>
            <template v-else>
              {{ step.id }}
            </template>
          </div>
          <span class="step-label">{{ step.label }}</span>
        </div>
        
        <!-- 连接线 -->
        <div
          v-if="index < steps.length - 1"
          :class="['step-line', { active: step.id < currentStep }]"
        />
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.step-bar {
  background: $bg-white;
  padding: 20px 16px;
  border-radius: 0 0 16px 16px;
  box-shadow: $shadow-sm;
}

.step-list {
  display: flex;
  align-items: center;
  justify-content: center;
}

.step-item {
  display: flex;
  align-items: center;
  flex: 1;
  max-width: 140px;

  &:last-child {
    flex: 0 0 auto;
  }

  &.completed {
    .step-circle {
      background: $primary;
      color: #fff;
      border-color: $primary;
    }
    .step-label {
      color: $primary;
      font-weight: 500;
    }
  }

  &.active {
    .step-circle {
      background: $primary;
      color: #fff;
      border-color: $primary;
      box-shadow: 0 0 0 4px rgba($primary, 0.2);
      transform: scale(1.1);
    }
    .step-label {
      color: $primary;
      font-weight: 600;
    }
  }

  &.pending {
    .step-circle {
      background: $bg-primary;
      color: $text-tertiary;
      border-color: $border-light;
    }
    .step-label {
      color: $text-tertiary;
    }
  }
}

.step-node {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.step-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  border: 2px solid;
  transition: all 0.3s ease;

  .el-icon {
    font-size: 16px;
    font-weight: bold;
  }
}

.step-label {
  font-size: 13px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.step-line {
  flex: 1;
  height: 2px;
  background: $border-light;
  margin: 0 12px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
  min-width: 40px;

  &.active {
    background: linear-gradient(90deg, $primary 0%, $primary-light 100%);
  }
}

// 响应式适配
@media (max-width: 375px) {
  .step-bar {
    padding: 16px 12px;
  }

  .step-circle {
    width: 28px;
    height: 28px;
    font-size: 12px;

    .el-icon {
      font-size: 14px;
    }
  }

  .step-label {
    font-size: 12px;
  }

  .step-line {
    margin: 0 8px;
    margin-bottom: 18px;
    min-width: 24px;
  }
}
</style>
