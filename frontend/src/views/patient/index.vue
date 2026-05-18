<template>
  <div class="patient-manage-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <img :src="userMenuIcons.arrowLeft" class="nav-icon" alt="返回" />
      </div>
      <span class="title">就诊人管理</span>
      <div class="add-btn" @click="handleAdd">
        <img :src="userMenuIcons.plus" class="btn-icon-small" alt="添加" />
        <span>添加</span>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="4" animated />
    </div>

    <template v-else>
      <!-- 就诊人列表 -->
      <div class="patient-list">
        <div v-if="patientList.length === 0" class="empty-state">
          <el-empty description="暂无就诊人信息" :image-size="120">
            <el-button type="primary" round @click="handleAdd">
              添加就诊人
            </el-button>
          </el-empty>
        </div>

        <div
          v-for="patient in patientList"
          :key="patient.id"
          class="patient-card"
          @click="handleSelect(patient)"
        >
          <div class="patient-radio" v-if="selectMode">
            <div :class="['radio-circle', { checked: selectedId === patient.id }]">
              <div v-if="selectedId === patient.id" class="radio-inner"></div>
            </div>
          </div>
          <div class="avatar-section">
            <img
              :src="patient.avatar || (patient.gender === 1 ? defaultMaleAvatar : defaultFemaleAvatar)"
              class="patient-avatar"
              alt="头像"
            />
            <span v-if="patient.isDefault" class="default-tag">默认</span>
          </div>
          <div class="patient-info">
            <div class="patient-header">
              <span class="name">{{ patient.name }}</span>
              <span class="gender">{{ patient.gender === 1 ? '男' : '女' }}</span>
              <span class="age">{{ patient.age }}岁</span>
              <span v-if="patient.isDefault && !selectMode" class="default-badge">默认就诊人</span>
            </div>
            <div class="patient-detail">
              <img :src="userMenuIcons.creditCard" class="info-icon" alt="身份证" />
              <span class="id-card">{{ maskIdCard(patient.idCard) }}</span>
            </div>
            <div class="patient-phone">
              <img :src="userMenuIcons.phone" class="info-icon" alt="电话" />
              <span>{{ maskPhone(patient.phone) }}</span>
            </div>
          </div>
          <div class="patient-actions" v-if="!selectMode">
            <div class="action-btn edit" @click.stop="handleEdit(patient)">
              <img :src="userMenuIcons.edit" class="action-icon" alt="编辑" />
            </div>
            <div class="action-btn delete" @click.stop="handleDelete(patient)">
              <img :src="userMenuIcons.delete" class="action-icon" alt="删除" />
            </div>
            <div v-if="!patient.isDefault" class="action-btn set-default" @click.stop="handleSetDefault(patient)">
              <img :src="userMenuIcons.check" class="action-icon" alt="设为默认" />
            </div>
          </div>
        </div>
      </div>

      <!-- 底部操作栏 -->
      <div v-if="selectMode" class="bottom-actions">
        <button class="confirm-btn" @click="confirmSelect">确认选择</button>
      </div>

      <!-- 提示信息 -->
      <div v-if="!selectMode && patientList.length > 0" class="tips-section">
        <img :src="userMenuIcons.infoFilled" class="tips-icon" alt="提示" />
        <span>最多可添加5位就诊人，用于在线问诊和购药</span>
      </div>
    </template>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑就诊人' : '添加就诊人'"
      width="90%"
      :close-on-click-modal="false"
      class="patient-dialog"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-position="top">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入真实姓名" />
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender">
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="formData.idCard" placeholder="请输入身份证号" maxlength="18" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>

        <el-form-item label="生日" prop="birthday">
          <el-date-picker
            v-model="formData.birthday"
            type="date"
            placeholder="选择生日"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="formData.isDefault">设为默认就诊人</el-checkbox>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            {{ isEdit ? '保存' : '确认添加' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  getPatients,
  addPatient,
  updatePatient,
  deletePatient as removePatient,
  setDefaultPatient
} from '@/api/modules/user'
import { userMenuIcons, mockPatients } from '@/api/mock'
import type { Patient } from '@/types'

const router = useRouter()
const route = useRoute()

// 默认头像 - 写实风格
const defaultMaleAvatar = 'https://img.icons8.com/color/96/user-male.png'
const defaultFemaleAvatar = 'https://img.icons8.com/color/96/user-female.png'

const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const selectMode = ref(false)
const selectedId = ref('')

// 就诊人列表
const patientList = ref<Patient[]>([])

// 表单数据
const formData = reactive({
  id: '',
  name: '',
  gender: 1,
  idCard: '',
  phone: '',
  birthday: '',
  isDefault: false
})

// 表单验证规则
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在2到20个字符之间', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  birthday: [
    { required: true, message: '请选择生日', trigger: 'change' }
  ]
}

// 脱敏处理
const maskPhone = (phone: string) => {
  if (!phone || phone.length < 7) return phone
  return phone.substring(0, 3) + '****' + phone.substring(phone.length - 4)
}

const maskIdCard = (idCard: string) => {
  if (!idCard || idCard.length < 8) return idCard
  return idCard.substring(0, 4) + '**********' + idCard.substring(idCard.length - 4)
}

const goBack = () => {
  router.back()
}

// 打开新增弹窗
const handleAdd = () => {
  if (patientList.value.length >= 5) {
    ElMessage.warning('最多只能添加5位就诊人')
    return
  }

  isEdit.value = false
  Object.assign(formData, {
    id: '',
    name: '',
    gender: 1,
    idCard: '',
    phone: '',
    birthday: '',
    isDefault: false
  })
  dialogVisible.value = true
}

// 打开编辑弹窗
const handleEdit = (patient: Patient) => {
  isEdit.value = true
  Object.assign(formData, {
    id: patient.id,
    name: patient.name,
    gender: patient.gender,
    idCard: patient.idCard,
    phone: patient.phone,
    birthday: patient.birthday || '',
    isDefault: patient.isDefault
  })
  dialogVisible.value = true
}

// 选择就诊人
const handleSelect = (patient: Patient) => {
  if (selectMode.value) {
    selectedId.value = patient.id
  }
}

// 确认选择
const confirmSelect = () => {
  if (!selectedId.value) {
    ElMessage.warning('请选择就诊人')
    return
  }
  const patient = patientList.value.find(p => p.id === selectedId.value)
  if (patient) {
    router.back()
  }
}

// 设置默认
const handleSetDefault = async (patient: Patient) => {
  try {
    await setDefaultPatient(patient.id)
    patientList.value.forEach(p => {
      p.isDefault = p.id === patient.id
    })
    ElMessage.success('已设为默认就诊人')
  } catch (error) {
    console.error('设置默认失败:', error)
    // 模拟设置
    patientList.value.forEach(p => {
      p.isDefault = p.id === patient.id
    })
    ElMessage.success('已设为默认就诊人')
  }
}

// 删除
const handleDelete = async (patient: Patient) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除${patient.name}的就诊人信息吗？`,
      '提示',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await removePatient(patient.id)
    const index = patientList.value.findIndex(p => p.id === patient.id)
    if (index > -1) {
      patientList.value.splice(index, 1)
    }

    ElMessage.success('已删除')
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      // 模拟删除
      const index = patientList.value.findIndex(p => p.id === patient.id)
      if (index > -1) {
        patientList.value.splice(index, 1)
      }
      ElMessage.success('已删除')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    const age = calculateAge(formData.idCard)

    if (isEdit.value) {
      await updatePatient(formData.id, {
        name: formData.name,
        gender: formData.gender,
        age,
        idCard: formData.idCard,
        phone: formData.phone,
        birthday: formData.birthday,
        relationship: '本人',
        isDefault: formData.isDefault
      })

      const index = patientList.value.findIndex(p => p.id === formData.id)
      if (index > -1) {
        patientList.value[index] = {
          ...patientList.value[index],
          name: formData.name,
          gender: formData.gender,
          age,
          idCard: formData.idCard,
          phone: formData.phone,
          birthday: formData.birthday,
          isDefault: formData.isDefault
        }
      }

      ElMessage.success('修改成功')
    } else {
      const res = await addPatient({
        name: formData.name,
        gender: formData.gender,
        age,
        idCard: formData.idCard,
        phone: formData.phone,
        birthday: formData.birthday,
        relationship: '本人',
        isDefault: formData.isDefault
      })

      if (res) {
        patientList.value.push(res)
      } else {
        patientList.value.push({
          id: Date.now().toString(),
          name: formData.name,
          gender: formData.gender,
          age,
          idCard: formData.idCard,
          phone: formData.phone,
          birthday: formData.birthday,
          relationship: '本人',
          isDefault: formData.isDefault,
          avatar: formData.gender === 1 ? defaultMaleAvatar : defaultFemaleAvatar
        })
      }

      ElMessage.success('添加成功')
    }

    dialogVisible.value = false
  } catch (error: any) {
    if (error !== false) {
      console.error('提交失败:', error)
      ElMessage.error('操作失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 根据身份证计算年龄
const calculateAge = (idCard: string): number => {
  if (!idCard || idCard.length < 14) return 0

  let birthYear = ''
  if (idCard.length === 18) {
    birthYear = idCard.substring(6, 10)
  } else if (idCard.length === 15) {
    birthYear = '19' + idCard.substring(6, 8)
  }

  const year = new Date().getFullYear()
  return year - parseInt(birthYear)
}

// 加载就诊人列表
const loadPatientList = async () => {
  loading.value = true
  try {
    const res = await getPatients()
    if (Array.isArray(res) && res.length > 0) {
      patientList.value = res.map((p: Patient) => ({
        ...p,
        avatar: p.avatar || (p.gender === 1 ? defaultMaleAvatar : defaultFemaleAvatar)
      }))
    } else {
      ElMessage.warning('暂无就诊人')
    }
  } catch (error) {
    console.error('获取就诊人列表失败:', error)
    ElMessage.error('获取就诊人列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  // 检查是否是选择模式
  selectMode.value = route.query.select === 'true'
  if (route.query.selectedId) {
    selectedId.value = route.query.selectedId as string
  }
  loadPatientList()
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

$primary-green: #00C9A7;
$primary-green-light: #00b894;

.patient-manage-page {
  min-height: 100vh;
  background: $bg-primary;
  padding-bottom: calc(80px + $safe-area-bottom);
}

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
    transition: all 0.2s;

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

  .add-btn {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 14px;
    cursor: pointer;
    padding: 6px 12px;
    border-radius: 16px;
    transition: all 0.2s;

    &:active {
      background: rgba(255, 255, 255, 0.2);
    }

    .btn-icon-small {
      width: 16px;
      height: 16px;
      object-fit: contain;
      filter: brightness(0) invert(1);
    }
  }
}

.loading-container {
  padding: 20px;
  margin-top: 20px;
}

.patient-list {
  padding: 16px;

  .empty-state {
    margin-top: 60px;
  }
}

.patient-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  display: flex;
  align-items: flex-start;
  gap: 12px;
  transition: all 0.2s;

  &:active {
    transform: scale(0.99);
  }

  .patient-radio {
    padding-top: 18px;

    .radio-circle {
      width: 20px;
      height: 20px;
      border-radius: 50%;
      border: 2px solid #ddd;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.2s;

      &.checked {
        border-color: $primary-green;
        background: $primary-green;
      }

      .radio-inner {
        width: 8px;
        height: 8px;
        background: white;
        border-radius: 50%;
      }
    }
  }

  .avatar-section {
    position: relative;
    flex-shrink: 0;

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

  .patient-info {
    flex: 1;
    min-width: 0;

    .patient-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;
      flex-wrap: wrap;

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

      .default-badge {
        padding: 2px 8px;
        background: rgba($primary-green, 0.1);
        color: $primary-green;
        border-radius: 4px;
        font-size: 11px;
        font-weight: 500;
      }
    }

    .patient-detail,
    .patient-phone {
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
      span {
        font-size: 13px;
        color: $text-tertiary;
      }
    }
  }

  .patient-actions {
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

.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  padding-bottom: calc(12px + $safe-area-bottom);
  background: white;
  border-top: 1px solid $border-light;

  .confirm-btn {
    width: 100%;
    padding: 14px;
    background: $primary-green;
    color: white;
    border: none;
    border-radius: 24px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;

    &:active {
      opacity: 0.9;
      transform: scale(0.98);
    }
  }
}

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

.dialog-footer {
  display: flex;
  gap: 12px;

  .el-button {
    flex: 1;

    &--primary {
      background: $primary-green;
      border-color: $primary-green;

      &:hover {
        background: $primary-green-light;
        border-color: $primary-green-light;
      }
    }
  }
}
</style>
