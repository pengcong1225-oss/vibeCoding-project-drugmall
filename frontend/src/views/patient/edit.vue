<template>
  <div class="patient-edit-page">
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="title">编辑就诊人</span>
      <div class="placeholder"></div>
    </div>

    <el-form ref="formRef" :model="formData" :rules="formRules" label-position="top" class="patient-form">
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

    <div class="bottom-actions">
      <button class="submit-btn" :disabled="submitLoading" @click="handleSubmit">
        {{ submitLoading ? '保存中...' : '保存' }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getPatientList, updatePatient } from '@/api/modules/patient'

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const patientId = ref('')

const formData = reactive({
  id: '',
  name: '',
  gender: 1,
  idCard: '',
  phone: '',
  birthday: '',
  isDefault: false
})

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

const goBack = () => {
  router.back()
}

const calculateAge = (idCard: string): number => {
  if (!idCard || idCard.length < 14) return 0
  let birthYear = ''
  if (idCard.length === 18) {
    birthYear = idCard.substring(6, 10)
  } else if (idCard.length === 15) {
    birthYear = '19' + idCard.substring(6, 8)
  }
  return new Date().getFullYear() - parseInt(birthYear)
}

const loadPatient = async () => {
  try {
    const res = await getPatientList()
    if (Array.isArray(res)) {
      const patient = res.find(p => p.id === patientId.value)
      if (patient) {
        formData.id = patient.id
        formData.name = patient.name
        formData.gender = patient.gender
        formData.idCard = patient.idCard
        formData.phone = patient.phone
        formData.birthday = patient.birthday || ''
        formData.isDefault = patient.isDefault || false
      }
    }
  } catch (error) {
    console.error('加载就诊人信息失败:', error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitLoading.value = true

    const age = calculateAge(formData.idCard)
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

    ElMessage.success('保存成功')
    router.back()
  } catch (error: any) {
    if (error !== false) {
      console.error('保存失败:', error)
      ElMessage.error('操作失败')
    }
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  patientId.value = route.query.id as string
  if (patientId.value) {
    loadPatient()
  }
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

$primary-green: #00C9A7;
$primary-green-light: #00b894;

.patient-edit-page {
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

    &:active {
      background: rgba(255, 255, 255, 0.2);
    }

    :deep(.el-icon) {
      font-size: 20px;
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

.patient-form {
  padding: 16px;
  background: white;
  margin: 16px;
  border-radius: 12px;
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

  .submit-btn {
    width: 100%;
    padding: 14px;
    background: $primary-green;
    color: white;
    border: none;
    border-radius: 24px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;

    &:active:not(:disabled) {
      opacity: 0.9;
      transform: scale(0.98);
    }

    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }
}
</style>
