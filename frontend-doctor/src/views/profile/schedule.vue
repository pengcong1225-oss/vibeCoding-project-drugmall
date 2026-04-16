<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const selectedDay = ref(0)
const showSettingModal = ref(false)
const settingType = ref<'work' | 'rest'>('work')

// 星期数据
const weekDays = ref([
  { name: '周一', date: '04/07', fullDate: '2024-04-07' },
  { name: '周二', date: '04/08', fullDate: '2024-04-08' },
  { name: '周三', date: '04/09', fullDate: '2024-04-09' },
  { name: '周四', date: '04/10', fullDate: '2024-04-10' },
  { name: '周五', date: '04/11', fullDate: '2024-04-11' },
  { name: '周六', date: '04/12', fullDate: '2024-04-12' },
  { name: '周日', date: '04/13', fullDate: '2024-04-13' }
])

// 排班数据
interface ScheduleSlot {
  id: string
  time: string
  type: '图文问诊' | '视频问诊' | '电话问诊'
  status: 'booked' | 'free' | 'closed'
  statusText: string
  patientName?: string
}

interface ScheduleDay {
  status: 'working' | 'rest' | 'stopped'
  statusText: string
  timeRange: string
  slots: ScheduleSlot[]
}

const scheduleData = ref<ScheduleDay[]>([
  {
    status: 'working',
    statusText: '出诊中',
    timeRange: '09:00 - 17:00',
    slots: [
      { id: '1', time: '09:00-10:00', type: '图文问诊', status: 'booked', statusText: '已预约', patientName: '李**' },
      { id: '2', time: '10:00-11:00', type: '图文问诊', status: 'free', statusText: '空闲' },
      { id: '3', time: '14:00-15:00', type: '视频问诊', status: 'booked', statusText: '已预约', patientName: '王**' },
      { id: '4', time: '15:00-16:00', type: '图文问诊', status: 'free', statusText: '空闲' }
    ]
  },
  {
    status: 'working',
    statusText: '出诊中',
    timeRange: '09:00 - 17:00',
    slots: [
      { id: '5', time: '09:00-10:00', type: '图文问诊', status: 'booked', statusText: '已预约', patientName: '张**' },
      { id: '6', time: '10:00-11:00', type: '视频问诊', status: 'booked', statusText: '已预约', patientName: '刘**' }
    ]
  },
  {
    status: 'rest',
    statusText: '休息',
    timeRange: '全天',
    slots: []
  },
  {
    status: 'stopped',
    statusText: '已停诊',
    timeRange: '-',
    slots: []
  },
  {
    status: 'working',
    statusText: '出诊中',
    timeRange: '14:00 - 18:00',
    slots: [
      { id: '7', time: '14:00-15:00', type: '图文问诊', status: 'free', statusText: '空闲' },
      { id: '8', time: '15:00-16:00', type: '图文问诊', status: 'free', statusText: '空闲' }
    ]
  },
  {
    status: 'rest',
    statusText: '休息',
    timeRange: '全天',
    slots: []
  },
  {
    status: 'rest',
    statusText: '休息',
    timeRange: '全天',
    slots: []
  }
])

// 设置表单
const workForm = ref({
  startTime: '09:00',
  endTime: '17:00',
  types: ['图文问诊'] as string[]
})

const currentSchedule = computed(() => scheduleData.value[selectedDay.value])

// 是否有排班
const hasSchedule = computed(() => {
  return scheduleData.value.some(day => day.status === 'working')
})

// 返回
const goBack = () => {
  router.back()
}

// 打开设置弹窗
const openSetting = (type: 'work' | 'rest') => {
  settingType.value = type
  if (type === 'work') {
    const current = scheduleData.value[selectedDay.value]
    if (current.status === 'working') {
      const [start, end] = current.timeRange.split(' - ')
      workForm.value.startTime = start
      workForm.value.endTime = end
    }
  }
  showSettingModal.value = true
}

// 保存设置
const saveSetting = () => {
  const day = scheduleData.value[selectedDay.value]

  if (settingType.value === 'rest') {
    // 设置停诊/休息
    day.status = 'stopped'
    day.statusText = '已停诊'
    day.timeRange = '-'
    day.slots = []
  } else {
    // 设置出诊
    day.status = 'working'
    day.statusText = '出诊中'
    day.timeRange = `${workForm.value.startTime} - ${workForm.value.endTime}`

    // 生成时段
    const slots: ScheduleSlot[] = []
    const startHour = parseInt(workForm.value.startTime.split(':')[0])
    const endHour = parseInt(workForm.value.endTime.split(':')[0])

    for (let i = startHour; i < endHour; i++) {
      slots.push({
        id: `${selectedDay.value}-${i}`,
        time: `${String(i).padStart(2, '0')}:00-${String(i + 1).padStart(2, '0')}:00`,
        type: workForm.value.types[0] as '图文问诊' | '视频问诊' | '电话问诊',
        status: 'free',
        statusText: '空闲'
      })
    }
    day.slots = slots
  }

  showSettingModal.value = false
}

// 关闭时段
const closeSlot = (slotId: string) => {
  const day = scheduleData.value[selectedDay.value]
  const slot = day.slots.find(s => s.id === slotId)
  if (slot && slot.status === 'free') {
    slot.status = 'closed'
    slot.statusText = '已关闭'
  }
}

// 开启时段
const openSlot = (slotId: string) => {
  const day = scheduleData.value[selectedDay.value]
  const slot = day.slots.find(s => s.id === slotId)
  if (slot && slot.status === 'closed') {
    slot.status = 'free'
    slot.statusText = '空闲'
  }
}

// 获取状态样式
const getStatusClass = (status: string) => {
  switch (status) {
    case 'working': return 'status-working'
    case 'rest': return 'status-rest'
    case 'stopped': return 'status-stopped'
    default: return ''
  }
}

// 获取时段样式
const getSlotClass = (slot: ScheduleSlot) => {
  switch (slot.status) {
    case 'booked': return 'slot-booked'
    case 'free': return 'slot-free'
    case 'closed': return 'slot-closed'
    default: return ''
  }
}
</script>

<template>
  <div class="schedule-page">
    <!-- 顶部导航 -->
    <header class="page-header">
      <button class="back-btn" @click="goBack">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path d="M15 18l-6-6 6-6" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
      <h1 class="page-title">我的排班</h1>
      <div class="header-right"></div>
    </header>

    <!-- 日期选择 -->
    <div class="date-section">
      <div class="week-tabs">
        <div
          v-for="(day, index) in weekDays"
          :key="index"
          :class="['day-tab', { active: selectedDay === index }]"
          @click="selectedDay = index"
        >
          <span class="day-name">{{ day.name }}</span>
          <span class="day-date">{{ day.date }}</span>
          <span v-if="scheduleData[index]?.status === 'working'" class="dot working"></span>
          <span v-else-if="scheduleData[index]?.status === 'stopped'" class="dot stopped"></span>
        </div>
      </div>
    </div>

    <!-- 排班状态卡片 -->
    <div class="status-section">
      <div :class="['status-card', getStatusClass(currentSchedule.status)]">
        <div class="status-header">
          <div class="status-title">{{ weekDays[selectedDay].name }}排班状态</div>
          <div :class="['status-badge', currentSchedule.status]">
            {{ currentSchedule.statusText }}
          </div>
        </div>
        <div v-if="currentSchedule.status === 'working'" class="status-time">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/>
            <polyline points="12 6 12 12 16 14"/>
          </svg>
          {{ currentSchedule.timeRange }}
        </div>
        <div v-else-if="currentSchedule.status === 'stopped'" class="status-desc">
          当日已设置停诊，患者无法预约
        </div>
        <div v-else class="status-desc">
          当日为休息日
        </div>
      </div>
    </div>

    <!-- 排班时段列表 -->
    <div v-if="currentSchedule.status === 'working'" class="schedule-list">
      <div class="list-header">
        <span class="list-title">排班时段</span>
        <span class="list-count">共 {{ currentSchedule.slots.length }} 个时段</span>
      </div>

      <div class="slots-container">
        <div
          v-for="slot in currentSchedule.slots"
          :key="slot.id"
          :class="['slot-item', getSlotClass(slot)]"
        >
          <div class="slot-time">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <polyline points="12 6 12 12 16 14"/>
            </svg>
            {{ slot.time }}
          </div>
          <div class="slot-type">{{ slot.type }}</div>
          <div class="slot-status">
            <span v-if="slot.status === 'booked'" class="status-tag booked">
              {{ slot.statusText }}
            </span>
            <span v-else-if="slot.status === 'free'" class="status-tag free">
              {{ slot.statusText }}
            </span>
            <span v-else class="status-tag closed">
              {{ slot.statusText }}
            </span>
          </div>
          <div class="slot-action">
            <button v-if="slot.status === 'free'" class="action-btn close" @click="closeSlot(slot.id)">
              关闭
            </button>
            <button v-else-if="slot.status === 'closed'" class="action-btn open" @click="openSlot(slot.id)">
              开启
            </button>
            <button v-else class="action-btn view" @click="$router.push('/consultation')">
              查看
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <div class="empty-icon">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
          <line x1="16" y1="2" x2="16" y2="6"/>
          <line x1="8" y1="2" x2="8" y2="6"/>
          <line x1="3" y1="10" x2="21" y2="10"/>
        </svg>
      </div>
      <p class="empty-text">{{ currentSchedule.status === 'stopped' ? '当日已停诊' : '当日为休息日' }}</p>
      <p class="empty-subtext">点击下方按钮设置排班</p>
    </div>

    <!-- 底部操作栏 -->
    <div class="bottom-actions">
      <button class="action-btn-primary" @click="openSetting('work')">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
          <line x1="16" y1="2" x2="16" y2="6"/>
          <line x1="8" y1="2" x2="8" y2="6"/>
          <line x1="3" y1="10" x2="21" y2="10"/>
        </svg>
        {{ currentSchedule.status === 'working' ? '修改排班' : '设置出诊' }}
      </button>
      <button
        v-if="currentSchedule.status === 'working'"
        class="action-btn-danger"
        @click="openSetting('rest')"
      >
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="10"/>
          <line x1="15" y1="9" x2="9" y2="15"/>
          <line x1="9" y1="9" x2="15" y2="15"/>
        </svg>
        设置停诊
      </button>
    </div>

    <!-- 设置弹窗 -->
    <div v-if="showSettingModal" class="modal-mask" @click.self="showSettingModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ settingType === 'work' ? '设置出诊' : '设置停诊' }}</h3>
          <button class="close-btn" @click="showSettingModal = false">&times;</button>
        </div>

        <div class="modal-body">
          <!-- 出诊设置 -->
          <template v-if="settingType === 'work'">
            <div class="form-item">
              <label>出诊时间</label>
              <div class="time-range">
                <input v-model="workForm.startTime" type="time" class="time-input" />
                <span class="time-separator">至</span>
                <input v-model="workForm.endTime" type="time" class="time-input" />
              </div>
            </div>

            <div class="form-item">
              <label>问诊类型</label>
              <div class="type-options">
                <label class="type-option">
                  <input v-model="workForm.types" type="checkbox" value="图文问诊" />
                  <span class="check-box"></span>
                  <span>图文问诊</span>
                </label>
                <label class="type-option">
                  <input v-model="workForm.types" type="checkbox" value="视频问诊" />
                  <span class="check-box"></span>
                  <span>视频问诊</span>
                </label>
                <label class="type-option">
                  <input v-model="workForm.types" type="checkbox" value="电话问诊" />
                  <span class="check-box"></span>
                  <span>电话问诊</span>
                </label>
              </div>
            </div>
          </template>

          <!-- 停诊设置 -->
          <template v-else>
            <div class="stop-notice">
              <div class="notice-icon">
                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/>
                  <line x1="12" y1="8" x2="12" y2="12"/>
                  <line x1="12" y1="16" x2="12.01" y2="16"/>
                </svg>
              </div>
              <p class="notice-title">确认设置停诊？</p>
              <p class="notice-desc">
                设置停诊后，{{ weekDays[selectedDay].name }}（{{ weekDays[selectedDay].fullDate }}）将无法接受患者预约。
                已有预约的患者将收到停诊通知。
              </p>
            </div>
          </template>
        </div>

        <div class="modal-footer">
          <button class="btn-cancel" @click="showSettingModal = false">取消</button>
          <button
            :class="['btn-confirm', settingType === 'rest' ? 'btn-danger' : 'btn-primary']"
            @click="saveSetting"
          >
            {{ settingType === 'work' ? '保存设置' : '确认停诊' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
// 颜色变量 - 医疗绿主题
$primary: #2E7D32;
$primary-light: #4CAF50;
$primary-bg: #E8F5E9;
$danger: #D32F2F;
$danger-light: #FFEBEE;
$text-primary: #333;
$text-secondary: #666;
$text-tertiary: #999;
$bg-gray: #f5f5f5;
$border-light: #e8e8e8;

.schedule-page {
  min-height: 100vh;
  background: $bg-gray;
  padding-bottom: 100px;
}

// 顶部导航
.page-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid $border-light;

  .back-btn {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: none;
    border: none;
    color: $text-primary;
    cursor: pointer;
    border-radius: 50%;
    transition: background 0.2s;

    &:active {
      background: $bg-gray;
    }

    svg {
      width: 24px;
      height: 24px;
    }
  }

  .page-title {
    flex: 1;
    text-align: center;
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
    margin: 0;
  }

  .header-right {
    width: 40px;
  }
}

// 日期选择
.date-section {
  background: #fff;
  padding: 16px 0;
  margin-bottom: 12px;

  .week-tabs {
    display: flex;
    justify-content: space-around;
    padding: 0 8px;

    .day-tab {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 10px 12px;
      cursor: pointer;
      position: relative;
      border-radius: 12px;
      transition: all 0.2s;
      min-width: 48px;

      &.active {
        background: $primary-bg;

        .day-name, .day-date {
          color: $primary;
          font-weight: 600;
        }
      }

      &:active:not(.active) {
        background: $bg-gray;
      }

      .day-name {
        font-size: 12px;
        color: $text-tertiary;
        margin-bottom: 4px;
      }

      .day-date {
        font-size: 14px;
        color: $text-primary;
        font-weight: 500;
      }

      .dot {
        position: absolute;
        top: 6px;
        right: 6px;
        width: 6px;
        height: 6px;
        border-radius: 50%;

        &.working {
          background: $primary;
        }

        &.stopped {
          background: $danger;
        }
      }
    }
  }
}

// 状态卡片
.status-section {
  padding: 0 16px;
  margin-bottom: 12px;

  .status-card {
    border-radius: 16px;
    padding: 20px;
    color: #fff;
    transition: all 0.3s;

    &.status-working {
      background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
    }

    &.status-rest {
      background: linear-gradient(135deg, #9E9E9E 0%, #BDBDBD 100%);
    }

    &.status-stopped {
      background: linear-gradient(135deg, $danger 0%, #F44336 100%);
    }

    .status-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
    }

    .status-title {
      font-size: 14px;
      opacity: 0.9;
    }

    .status-badge {
      padding: 4px 12px;
      border-radius: 20px;
      font-size: 13px;
      font-weight: 500;
      background: rgba(255, 255, 255, 0.25);

      &.stopped {
        background: rgba(255, 255, 255, 0.3);
      }
    }

    .status-time {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 20px;
      font-weight: 600;

      svg {
        opacity: 0.8;
      }
    }

    .status-desc {
      font-size: 14px;
      opacity: 0.9;
    }
  }
}

// 排班列表
.schedule-list {
  background: #fff;
  margin: 0 16px;
  border-radius: 16px;
  padding: 16px;

  .list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid $border-light;

    .list-title {
      font-size: 15px;
      font-weight: 600;
      color: $text-primary;
    }

    .list-count {
      font-size: 13px;
      color: $text-tertiary;
    }
  }

  .slots-container {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .slot-item {
    display: flex;
    align-items: center;
    padding: 16px;
    background: $bg-gray;
    border-radius: 12px;
    border-left: 4px solid transparent;
    transition: all 0.2s;

    &.slot-booked {
      border-left-color: $primary;
      background: $primary-bg;
    }

    &.slot-free {
      border-left-color: #9E9E9E;
    }

    &.slot-closed {
      border-left-color: $danger;
      background: $danger-light;
      opacity: 0.8;
    }

    .slot-time {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 14px;
      color: $text-primary;
      font-weight: 500;
      width: 100px;
      flex-shrink: 0;

      svg {
        color: $text-tertiary;
      }
    }

    .slot-type {
      flex: 1;
      font-size: 14px;
      color: $text-secondary;
    }

    .slot-status {
      margin-right: 12px;

      .status-tag {
        padding: 4px 10px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 500;

        &.booked {
          background: $primary-bg;
          color: $primary;
        }

        &.free {
          background: #E0E0E0;
          color: $text-secondary;
        }

        &.closed {
          background: $danger-light;
          color: $danger;
        }
      }
    }

    .slot-action {
      .action-btn {
        padding: 6px 14px;
        border: none;
        border-radius: 6px;
        font-size: 13px;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s;

        &.close {
          background: #E0E0E0;
          color: $text-secondary;

          &:active {
            background: #BDBDBD;
          }
        }

        &.open {
          background: $primary-bg;
          color: $primary;

          &:active {
            background: $primary;
            color: #fff;
          }
        }

        &.view {
          background: $primary-bg;
          color: $primary;

          &:active {
            background: $primary;
            color: #fff;
          }
        }
      }
    }
  }
}

// 空状态
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: #fff;
  margin: 0 16px;
  border-radius: 16px;

  .empty-icon {
    width: 80px;
    height: 80px;
    background: $bg-gray;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16px;
    color: $text-tertiary;

    svg {
      width: 40px;
      height: 40px;
    }
  }

  .empty-text {
    font-size: 16px;
    color: $text-primary;
    font-weight: 500;
    margin-bottom: 8px;
  }

  .empty-subtext {
    font-size: 13px;
    color: $text-tertiary;
  }
}

// 底部操作栏
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 12px 16px calc(12px + env(safe-area-inset-bottom));
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  display: flex;
  gap: 12px;

  .action-btn-primary,
  .action-btn-danger {
    flex: 1;
    height: 48px;
    border: none;
    border-radius: 12px;
    font-size: 15px;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    transition: all 0.2s;

    svg {
      width: 18px;
      height: 18px;
    }

    &:active {
      transform: scale(0.98);
    }
  }

  .action-btn-primary {
    background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
    color: #fff;
  }

  .action-btn-danger {
    background: $danger-light;
    color: $danger;

    &:active {
      background: $danger;
      color: #fff;
    }
  }
}

// 弹窗
.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s;

  @keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
  }
}

.modal-content {
  background: #fff;
  width: 100%;
  max-width: 480px;
  border-radius: 24px 24px 0 0;
  animation: slideUp 0.3s;

  @keyframes slideUp {
    from { transform: translateY(100%); }
    to { transform: translateY(0); }
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 20px 16px;
  border-bottom: 1px solid $border-light;

  h3 {
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
    margin: 0;
  }

  .close-btn {
    width: 32px;
    height: 32px;
    background: $bg-gray;
    border: none;
    border-radius: 50%;
    font-size: 20px;
    color: $text-secondary;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;

    &:active {
      background: #e0e0e0;
    }
  }
}

.modal-body {
  padding: 20px;
  max-height: 60vh;
  overflow-y: auto;
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 16px 20px calc(16px + env(safe-area-inset-bottom));
  border-top: 1px solid $border-light;

  .btn-cancel,
  .btn-confirm {
    flex: 1;
    height: 48px;
    border: none;
    border-radius: 12px;
    font-size: 15px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;

    &:active {
      transform: scale(0.98);
    }
  }

  .btn-cancel {
    background: $bg-gray;
    color: $text-secondary;
  }

  .btn-confirm {
    &.btn-primary {
      background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
      color: #fff;
    }

    &.btn-danger {
      background: $danger;
      color: #fff;
    }
  }
}

// 表单样式
.form-item {
  margin-bottom: 20px;

  label {
    display: block;
    font-size: 14px;
    font-weight: 500;
    color: $text-primary;
    margin-bottom: 12px;
  }

  .time-range {
    display: flex;
    align-items: center;
    gap: 12px;

    .time-input {
      flex: 1;
      height: 48px;
      padding: 0 16px;
      border: 1px solid $border-light;
      border-radius: 12px;
      font-size: 15px;
      color: $text-primary;
      background: #fff;

      &:focus {
        outline: none;
        border-color: $primary;
      }
    }

    .time-separator {
      font-size: 14px;
      color: $text-secondary;
    }
  }

  .type-options {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .type-option {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 16px;
      background: $bg-gray;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.2s;

      &:active {
        background: #e8e8e8;
      }

      input {
        display: none;
      }

      .check-box {
        width: 22px;
        height: 22px;
        border: 2px solid $border-light;
        border-radius: 6px;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.2s;

        &::after {
          content: '';
          width: 12px;
          height: 12px;
          background: $primary;
          border-radius: 3px;
          opacity: 0;
          transition: all 0.2s;
        }
      }

      input:checked + .check-box {
        border-color: $primary;

        &::after {
          opacity: 1;
        }
      }

      span:last-child {
        font-size: 15px;
        color: $text-primary;
      }
    }
  }
}

// 停诊提示
.stop-notice {
  text-align: center;
  padding: 20px 0;

  .notice-icon {
    width: 80px;
    height: 80px;
    background: $danger-light;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 20px;
    color: $danger;

    svg {
      width: 40px;
      height: 40px;
    }
  }

  .notice-title {
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 12px;
  }

  .notice-desc {
    font-size: 14px;
    color: $text-secondary;
    line-height: 1.6;
    padding: 0 20px;
  }
}
</style>
