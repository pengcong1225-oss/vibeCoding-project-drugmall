<template>
  <div class="patient-list-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <img :src="userMenuIcons.arrowLeft" class="nav-icon" alt="返回" />
      </div>
      <span class="title">就诊人管理</span>
      <div class="placeholder"></div>
    </div>

    <!-- 就诊人列表 -->
    <div class="patient-list">
      <div
        v-for="patient in patientList"
        :key="patient.id"
        class="patient-card"
      >
        <div class="patient-info">
          <div class="avatar-section">
            <img
              :src="patient.avatar || (patient.gender === 1 ? defaultMaleAvatar : defaultFemaleAvatar)"
              class="patient-avatar"
              alt="头像"
            />
            <span v-if="patient.isDefault" class="default-tag">默认</span>
          </div>
          <div class="info-section">
            <div class="name-row">
              <span class="name">{{ patient.name }}</span>
              <span class="gender">{{ patient.gender === 1 ? '男' : patient.gender === 2 ? '女' : String(patient.gender) }}</span>
              <span class="age">{{ patient.age }}岁</span>
            </div>
            <div class="id-card-row">
              <img :src="userMenuIcons.creditCard" class="info-icon" alt="身份证" />
              <span class="id-card">{{ patient.idCard }}</span>
            </div>
            <div class="phone-row">
              <img :src="userMenuIcons.phone" class="info-icon" alt="电话" />
              <span class="phone">{{ patient.phone }}</span>
            </div>
          </div>
        </div>
        <div class="action-section">
          <div
            class="action-btn edit"
            @click="editPatient(patient)"
          >
            <img :src="userMenuIcons.edit" class="action-icon" alt="编辑" />
          </div>
          <div
            v-if="!patient.isDefault"
            class="action-btn delete"
            @click="deletePatient(patient)"
          >
            <img :src="userMenuIcons.delete" class="action-icon" alt="删除" />
          </div>
          <div
            v-if="!patient.isDefault"
            class="action-btn set-default"
            @click="setDefault(patient)"
          >
            <img :src="userMenuIcons.check" class="action-icon" alt="设为默认" />
          </div>
        </div>
      </div>
    </div>

    <!-- 添加就诊人按钮 -->
    <div class="add-patient-section">
      <button class="add-patient-btn" @click="addPatient">
        <img :src="userMenuIcons.plus" class="btn-icon" alt="添加" />
        添加就诊人
      </button>
    </div>

    <!-- 提示信息 -->
    <div class="tips-section">
      <img :src="userMenuIcons.infoFilled" class="tips-icon" alt="提示" />
      <span>最多可添加5位就诊人，用于在线问诊和购药</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPatientList, deletePatient as deletePatientApi, setDefaultPatient } from '@/api/modules/patient'
import { userMenuIcons, mockPatients } from '@/api/mock'
import { ROUTES } from '@/constants/routes'
import { IMAGES } from '@/constants/images'
import type { PatientInfo as Patient } from '@/api/modules/patient'

const router = useRouter()

const defaultMaleAvatar = IMAGES.AVATAR_MALE
const defaultFemaleAvatar = IMAGES.AVATAR_FEMALE

// 就诊人列表
const patientList = ref<Patient[]>([])

// 加载就诊人列表
const loadPatientList = async () => {
  try {
    const res = await getPatientList()
    if (Array.isArray(res)) {
      patientList.value = res
    } else {
      ElMessage.error('获取就诊人列表失败')
    }
  } catch (error) {
    console.error('获取就诊人列表失败:', error)
    ElMessage.error('获取就诊人列表失败，请稍后重试')
  }
}

// 返回
const goBack = () => {
  router.back()
}

// 添加就诊人
const addPatient = () => {
  if (patientList.value.length >= 5) {
    ElMessage.warning('最多可添加5位就诊人')
    return
  }
  router.push(ROUTES.PATIENT_ADD)
}

const editPatient = (patient: Patient) => {
  router.push({
    path: ROUTES.PATIENT_EDIT,
    query: { id: patient.id }
  })
}

// 删除就诊人
const deletePatient = (patient: Patient) => {
  ElMessageBox.confirm(
    `确定要删除就诊人"${patient.name}"吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deletePatientApi(patient.id)
      ElMessage.success('删除成功')
      loadPatientList()
    } catch (error) {
      console.error('删除就诊人失败:', error)
      // 模拟删除
      patientList.value = patientList.value.filter(p => p.id !== patient.id)
      ElMessage.success('删除成功')
    }
  }).catch(() => {
    // 取消
  })
}

// 设为默认
const setDefault = async (patient: Patient) => {
  try {
    await setDefaultPatient(patient.id)
    ElMessage.success('设置成功')
    loadPatientList()
  } catch (error) {
    console.error('设置默认就诊人失败:', error)
    // 模拟设置
    patientList.value.forEach(p => {
      p.isDefault = p.id === patient.id
    })
    ElMessage.success('设置成功')
  }
}

onMounted(() => {
  loadPatientList()
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

$primary-green: #00C9A7;
$primary-green-light: #00b894;

.patient-list-page {
  min-height: 100vh;
  background: $bg-primary;
  padding-bottom: calc(80px + $safe-area-bottom);
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

// 就诊人列表
.patient-list {
  padding: 16px;

  .patient-card {
    background: white;
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    display: flex;
    justify-content: space-between;
    align-items: flex-start;

    .patient-info {
      display: flex;
      gap: 12px;
      flex: 1;

      .avatar-section {
        position: relative;

        .patient-avatar {
          width: 56px;
          height: 56px;
          border-radius: 50%;
          object-fit: cover;
        }

        .default-tag {
          position: absolute;
          bottom: -4px;
          left: 50%;
          transform: translateX(-50%);
          padding: 2px 8px;
          background: $primary-green;
          color: white;
          font-size: 10px;
          border-radius: 8px;
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

          .name {
            font-size: 16px;
            font-weight: 600;
            color: $text-primary;
          }

          .gender,
          .age {
            font-size: 13px;
            color: $text-secondary;
          }
        }

        .id-card-row,
        .phone-row {
          display: flex;
          align-items: center;
          gap: 6px;
          margin-bottom: 6px;

          &:last-child {
            margin-bottom: 0;
          }

          .info-icon {
            width: 16px;
            height: 16px;
            object-fit: contain;
            opacity: 0.6;
          }

          .id-card,
          .phone {
            font-size: 13px;
            color: $text-tertiary;
          }
        }
      }
    }

    .action-section {
      display: flex;
      flex-direction: column;
      gap: 8px;

      .action-btn {
        width: 36px;
        height: 36px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        cursor: pointer;
        transition: all 0.2s;

        &.edit {
          background: rgba($primary, 0.1);

          &:active {
            background: rgba($primary, 0.2);
          }
        }

        &.delete {
          background: rgba($error, 0.1);

          &:active {
            background: rgba($error, 0.2);
          }
        }

        &.set-default {
          background: rgba($success, 0.1);

          &:active {
            background: rgba($success, 0.2);
          }
        }

        .action-icon {
          width: 20px;
          height: 20px;
          object-fit: contain;
        }
      }
    }
  }
}

// 添加就诊人按钮
.add-patient-section {
  position: fixed;
  bottom: calc(20px + $safe-area-bottom);
  left: 16px;
  right: 16px;

  .add-patient-btn {
    width: 100%;
    padding: 14px;
    background: linear-gradient(135deg, $primary-green 0%, $primary-green-light 100%);
    border: none;
    border-radius: 24px;
    color: white;
    font-size: 16px;
    font-weight: 500;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    transition: all 0.2s;
    box-shadow: 0 4px 12px rgba($primary-green, 0.3);

    &:active {
      transform: translateY(2px);
      box-shadow: 0 2px 6px rgba($primary-green, 0.3);
    }

    .btn-icon {
      width: 20px;
      height: 20px;
      object-fit: contain;
      filter: brightness(0) invert(1);
    }
  }
}

// 提示信息
.tips-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 12px 16px;
  margin-top: 8px;

  .tips-icon {
    width: 16px;
    height: 16px;
    object-fit: contain;
    opacity: 0.6;
  }

  span {
    font-size: 12px;
    color: $text-tertiary;
  }
}
</style>
