<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Plus, User, CircleCheckFilled, Document, FirstAidKit } from '@element-plus/icons-vue'
import { usePrescriptionStore } from '@/stores/prescription'
import StepBar from './components/StepBar.vue'
import type { Patient } from '@/types/user'
import type { Drug } from '@/types/drug'

const router = useRouter()
const route = useRoute()
const prescriptionStore = usePrescriptionStore()

// 当前步骤
const currentStep = ref(1)

// 用药人列表
const patientList = ref<Patient[]>([])
const loadingPatients = ref(false)

// 药品信息（从路由参数或store获取）
const drugInfo = ref<Drug[]>([])

// 疾病标签选项
const diseaseTags = ref([
  { id: '1', name: '感冒发热', selected: false },
  { id: '2', name: '咳嗽咽痛', selected: false },
  { id: '3', name: '头痛头晕', selected: false },
  { id: '4', name: '消化不良', selected: false },
  { id: '5', name: '腹泻腹痛', selected: false },
  { id: '6', name: '皮肤过敏', selected: false },
  { id: '7', name: '失眠多梦', selected: false },
  { id: '8', name: '高血压', selected: false },
  { id: '9', name: '糖尿病', selected: false },
  { id: '10', name: '冠心病', selected: false }
])

// 症状描述
const symptoms = ref('')

// 知情同意
const agreedToConsent = ref(false)

// 是否显示知情同意书弹窗
const showConsentDialog = ref(false)

// 计算属性：是否可以选择提交
const canSubmit = computed(() => {
  return prescriptionStore.hasSelectedPatient && 
         prescriptionStore.hasSelectedDiseases && 
         agreedToConsent.value
})

// 计算属性：已选择的疾病标签
const selectedDiseases = computed(() => {
  return diseaseTags.value.filter(tag => tag.selected)
})

// 返回上一页
const goBack = () => {
  router.back()
}

// 获取用药人列表
const fetchPatientList = async () => {
  loadingPatients.value = true
  try {
    // 模拟数据
    await new Promise(resolve => setTimeout(resolve, 500))
    patientList.value = [
      {
        id: '1',
        name: '张三',
        gender: 'male',
        age: 35,
        idCard: '110101199001011234',
        phone: '13800138000',
        relationship: '本人',
        isDefault: true
      },
      {
        id: '2',
        name: '李四',
        gender: 'female',
        age: 32,
        idCard: '110101199201011235',
        phone: '13800138001',
        relationship: '配偶',
        isDefault: false
      }
    ]
  } catch (error) {
    console.error('获取用药人列表失败:', error)
    ElMessage.error('获取用药人列表失败')
  } finally {
    loadingPatients.value = false
  }
}

// 获取药品信息
const fetchDrugInfo = async () => {
  const drugId = route.query.drugId as string
  if (drugId) {
    // 模拟获取药品信息
    drugInfo.value = [
      {
        id: drugId,
        name: '阿莫西林胶囊',
        specification: '0.25g*24粒',
        manufacturer: '华北制药',
        price: 28.5,
        image: '',
        isRx: true,
        categoryId: '1',
        stock: 100,
        sales: 999,
        status: 1
      }
    ]
    prescriptionStore.setSelectedDrugs(drugInfo.value)
  }
}

// 选择用药人
const selectPatient = (patient: Patient) => {
  prescriptionStore.selectPatient(patient)
}

// 添加用药人
const addPatient = () => {
  router.push('/patient/add')
}

// 切换疾病标签选择
const toggleDiseaseTag = (tag: typeof diseaseTags.value[0]) => {
  tag.selected = !tag.selected
  prescriptionStore.toggleDisease(tag.name)
}

// 打开知情同意书
const openConsent = () => {
  showConsentDialog.value = true
}

// 同意知情同意书
const agreeConsent = () => {
  agreedToConsent.value = true
  prescriptionStore.setAgreedToConsent(true)
  showConsentDialog.value = false
}

// 提交申请
const handleSubmit = async () => {
  if (!canSubmit.value) {
    if (!prescriptionStore.hasSelectedPatient) {
      ElMessage.warning('请选择用药人')
      return
    }
    if (!prescriptionStore.hasSelectedDiseases) {
      ElMessage.warning('请选择疾病症状')
      return
    }
    if (!agreedToConsent.value) {
      ElMessage.warning('请阅读并同意知情同意书')
      return
    }
    return
  }

  try {
    // 保存症状描述
    prescriptionStore.setSymptoms(symptoms.value)
    
    // 提交处方申请
    const consultationId = await prescriptionStore.submitPrescriptionApply()
    
    ElMessage.success('申请提交成功')
    
    // 跳转到复诊开方页面
    router.push(`/prescription/consult?id=${consultationId}`)
  } catch (error) {
    console.error('提交申请失败:', error)
    ElMessage.error('提交申请失败，请重试')
  }
}

onMounted(() => {
  fetchPatientList()
  fetchDrugInfo()
})
</script>

<template>
  <div class="prescription-apply-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="title">申请处方药</span>
      <div class="placeholder"></div>
    </div>

    <!-- 步骤条 -->
    <StepBar :current-step="currentStep" />

    <!-- 页面内容 -->
    <div class="page-content">
      <!-- 药品信息卡片 -->
      <div v-if="drugInfo.length > 0" class="drug-info-card">
        <div class="card-title">
          <el-icon><Document /></el-icon>
          <span>申请药品</span>
        </div>
        <div class="drug-list">
          <div v-for="drug in drugInfo" :key="drug.id" class="drug-item">
            <div class="drug-image">
              <img v-if="drug.image" :src="drug.image" :alt="drug.name">
              <div v-else class="image-placeholder">
                <el-icon><FirstAidKit /></el-icon>
              </div>
            </div>
            <div class="drug-detail">
              <div class="drug-name">
                <span class="rx-tag">Rx</span>
                {{ drug.name }}
              </div>
              <div class="drug-spec">{{ drug.specification }}</div>
              <div class="drug-price">¥{{ drug.price.toFixed(2) }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 选择用药人 -->
      <div class="section-card">
        <div class="section-header">
          <span class="section-title">选择用药人</span>
          <span class="section-subtitle">请选择需要开方的就诊人</span>
        </div>
        
        <div v-if="loadingPatients" class="loading-patients">
          <el-skeleton :rows="2" animated />
        </div>
        
        <div v-else class="patient-list">
          <div
            v-for="patient in patientList"
            :key="patient.id"
            :class="['patient-item', { 
              active: prescriptionStore.applyState.selectedPatient?.id === patient.id 
            }]"
            @click="selectPatient(patient)"
          >
            <div class="patient-avatar">
              <el-icon><User /></el-icon>
            </div>
            <div class="patient-info">
              <div class="patient-name">
                {{ patient.name }}
                <span class="relationship">{{ patient.relationship }}</span>
                <span v-if="patient.isDefault" class="default-tag">默认</span>
              </div>
              <div class="patient-detail">
                {{ patient.gender === 'male' ? '男' : '女' }} · {{ patient.age }}岁 · {{ patient.idCard.slice(-4) }}
              </div>
            </div>
            <div class="select-icon">
              <el-icon v-if="prescriptionStore.applyState.selectedPatient?.id === patient.id">
                <CircleCheckFilled />
              </el-icon>
            </div>
          </div>
          
          <!-- 添加用药人 -->
          <div class="add-patient" @click="addPatient">
            <div class="add-icon">
              <el-icon><Plus /></el-icon>
            </div>
            <span>添加用药人</span>
          </div>
        </div>
      </div>

      <!-- 选择疾病症状 -->
      <div class="section-card">
        <div class="section-header">
          <span class="section-title">选择疾病症状</span>
          <span class="section-subtitle">请选择您目前的症状（可多选）</span>
        </div>
        
        <div class="disease-tags">
          <span
            v-for="tag in diseaseTags"
            :key="tag.id"
            :class="['tag-item', { selected: tag.selected }]"
            @click="toggleDiseaseTag(tag)"
          >
            {{ tag.name }}
          </span>
        </div>
      </div>

      <!-- 症状描述 -->
      <div class="section-card">
        <div class="section-header">
          <span class="section-title">症状描述</span>
          <span class="section-subtitle">请详细描述您的症状（选填）</span>
        </div>
        
        <div class="symptom-input">
          <el-input
            v-model="symptoms"
            type="textarea"
            :rows="4"
            placeholder="请详细描述您的症状，如发病时间、症状表现、既往病史等，有助于医生更准确地为您开方..."
            maxlength="500"
            show-word-limit
          />
        </div>
      </div>

      <!-- 知情同意书 -->
      <div class="section-card consent-card">
        <div class="consent-content" @click="openConsent">
          <div class="consent-checkbox" @click.stop>
            <div 
              :class="['checkbox', { checked: agreedToConsent }]"
              @click="agreedToConsent = !agreedToConsent; prescriptionStore.setAgreedToConsent(agreedToConsent)"
            >
              <el-icon v-if="agreedToConsent"><CircleCheckFilled /></el-icon>
            </div>
          </div>
          <div class="consent-text">
            <span>我已阅读并同意</span>
            <span class="consent-link">《互联网诊疗知情同意书》</span>
            <span>和</span>
            <span class="consent-link">《隐私政策》</span>
          </div>
        </div>
      </div>

      <!-- 底部占位 -->
      <div class="bottom-placeholder"></div>
    </div>

    <!-- 底部提交按钮 -->
    <div class="bottom-bar">
      <button 
        :class="['submit-btn', { disabled: !canSubmit }]"
        @click="handleSubmit"
      >
        <span>提交申请</span>
        <el-icon><ArrowRight /></el-icon>
      </button>
    </div>

    <!-- 知情同意书弹窗 -->
    <el-dialog
      v-model="showConsentDialog"
      title="互联网诊疗知情同意书"
      width="90%"
      :close-on-click-modal="false"
      class="consent-dialog"
    >
      <div class="consent-document">
        <h3>互联网诊疗知情同意书</h3>
        <div class="consent-body">
          <p>尊敬的患者：</p>
          <p>欢迎您使用互联网诊疗服务。在接受服务前，请您仔细阅读以下内容：</p>
          
          <h4>一、服务说明</h4>
          <p>1. 互联网诊疗服务仅限于复诊患者，初诊患者请前往实体医疗机构就诊。</p>
          <p>2. 医生将根据您提供的信息进行诊疗，请您如实提供病情资料。</p>
          <p>3. 互联网诊疗不能替代紧急医疗救治，如遇紧急情况请立即拨打120或前往急诊。</p>
          
          <h4>二、患者权利与义务</h4>
          <p>1. 您有权了解医生的资质信息。</p>
          <p>2. 您有权拒绝或终止诊疗服务。</p>
          <p>3. 您有义务如实提供病情信息，不得隐瞒或虚报。</p>
          <p>4. 您应遵医嘱用药，如有不适及时复诊。</p>
          
          <h4>三、隐私保护</h4>
          <p>我们将严格保护您的个人信息和医疗数据，未经您同意不会向第三方透露。</p>
          
          <h4>四、免责声明</h4>
          <p>因患者提供虚假信息或不遵医嘱造成的后果，由患者自行承担。</p>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showConsentDialog = false">取消</el-button>
          <el-button type="primary" @click="agreeConsent">我已阅读并同意</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.prescription-apply-page {
  min-height: 100vh;
  background: $bg-primary;
  padding-bottom: 80px;
}

// 顶部导航
.nav-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-sm $spacing-md;
  padding-top: calc($safe-area-top + $spacing-sm);
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  color: #fff;
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
      background: rgba(255, 255, 255, 0.15);
    }
  }

  .title {
    font-size: $font-lg;
    font-weight: 600;
  }

  .placeholder {
    width: 36px;
  }
}

// 页面内容
.page-content {
  padding: $spacing-md;
}

// 通用卡片样式
.section-card {
  background: $bg-white;
  border-radius: $radius-lg;
  padding: $spacing-md;
  margin-bottom: $spacing-md;
  box-shadow: $shadow-sm;
}

// 药品信息卡片
.drug-info-card {
  background: $bg-white;
  border-radius: $radius-lg;
  padding: $spacing-md;
  margin-bottom: $spacing-md;
  box-shadow: $shadow-sm;

  .card-title {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: $font-md;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-md;

    .el-icon {
      color: $primary;
    }
  }

  .drug-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .drug-item {
    display: flex;
    gap: $spacing-md;
    padding: $spacing-md;
    background: $bg-gray;
    border-radius: $radius-md;

    .drug-image {
      width: 70px;
      height: 70px;
      border-radius: $radius-sm;
      overflow: hidden;
      flex-shrink: 0;
      background: $bg-primary;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .image-placeholder {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: $text-tertiary;

        .el-icon {
          font-size: 28px;
        }
      }
    }

    .drug-detail {
      flex: 1;
      min-width: 0;
      display: flex;
      flex-direction: column;
      justify-content: space-between;

      .drug-name {
        font-size: $font-md;
        font-weight: 600;
        color: $text-primary;
        display: flex;
        align-items: center;
        gap: 6px;

        .rx-tag {
          background: $error;
          color: #fff;
          font-size: 11px;
          padding: 1px 5px;
          border-radius: 4px;
          font-weight: 600;
        }
      }

      .drug-spec {
        font-size: $font-sm;
        color: $text-tertiary;
      }

      .drug-price {
        font-size: $font-lg;
        font-weight: 700;
        color: $price-red;
      }
    }
  }
}

// 区块头部
.section-header {
  margin-bottom: $spacing-md;

  .section-title {
    display: block;
    font-size: $font-lg;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 4px;
  }

  .section-subtitle {
    font-size: $font-sm;
    color: $text-tertiary;
  }
}

// 用药人列表
.patient-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.patient-item {
  display: flex;
  align-items: center;
  gap: $spacing-md;
  padding: 14px;
  background: $bg-gray;
  border-radius: $radius-md;
  cursor: pointer;
  transition: all 0.2s;
  border: 2px solid transparent;

  &:hover {
    background: $bg-primary;
  }

  &.active {
    background: rgba($primary, 0.08);
    border-color: $primary;
  }

  .patient-avatar {
    width: 44px;
    height: 44px;
    background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 20px;
  }

  .patient-info {
    flex: 1;
    min-width: 0;

    .patient-name {
      font-size: $font-md;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 4px;
      display: flex;
      align-items: center;
      gap: 6px;

      .relationship {
        font-size: 12px;
        color: $text-secondary;
        font-weight: normal;
        background: $bg-primary;
        padding: 2px 6px;
        border-radius: 4px;
      }

      .default-tag {
        font-size: 11px;
        color: $primary;
        background: rgba($primary, 0.15);
        padding: 2px 6px;
        border-radius: 4px;
      }
    }

    .patient-detail {
      font-size: $font-sm;
      color: $text-tertiary;
    }
  }

  .select-icon {
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $primary;
    font-size: 20px;
  }
}

.add-patient {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px;
  background: $bg-gray;
  border-radius: $radius-md;
  cursor: pointer;
  transition: all 0.2s;
  border: 2px dashed $border-light;

  &:hover {
    border-color: $primary;
    background: rgba($primary, 0.05);
  }

  .add-icon {
    width: 28px;
    height: 28px;
    background: $primary;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
  }

  span {
    font-size: $font-md;
    color: $text-secondary;
  }
}

// 疾病标签
.disease-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-item {
    padding: 8px 16px;
    background: $bg-primary;
    border-radius: 20px;
    font-size: $font-md;
  color: $text-secondary;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;

  &:hover {
    background: darken($bg-primary, 5%);
  }

  &.selected {
    background: rgba($primary, 0.15);
    color: $primary;
    border-color: $primary;
    font-weight: 500;
  }
}

// 症状输入
.symptom-input {
  :deep(.el-textarea__inner) {
    border-radius: $radius-md;
    resize: none;
    font-size: $font-md;

    &:focus {
      border-color: $primary;
    }
  }
}

// 知情同意
.consent-card {
  padding: 12px 16px;
}

.consent-content {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  cursor: pointer;
}

.consent-checkbox {
  flex-shrink: 0;
  padding-top: 2px;

  .checkbox {
    width: 20px;
    height: 20px;
    border: 2px solid $border-light;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
    cursor: pointer;

    &.checked {
      background: $primary;
      border-color: $primary;
      color: #fff;
    }

    .el-icon {
      font-size: 14px;
      font-weight: bold;
    }
  }
}

.consent-text {
  font-size: $font-sm;
  color: $text-secondary;
  line-height: 1.6;

  .consent-link {
    color: $primary;
    font-weight: 500;
  }
}

// 底部占位
.bottom-placeholder {
  height: 20px;
}

// 底部提交栏
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: $spacing-md;
  padding-bottom: calc($safe-area-bottom + $spacing-md);
  background: $bg-white;
  box-shadow: $shadow-lg;
  z-index: 100;

  .submit-btn {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    padding: 14px 20px;
    background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
    border: none;
    border-radius: $radius-lg;
    font-size: $font-lg;
    font-weight: 600;
    color: #fff;
    cursor: pointer;
    transition: all 0.2s;
    box-shadow: 0 4px 12px rgba($primary, 0.3);

    &:hover:not(.disabled) {
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba($primary, 0.4);
    }

    &.disabled {
      background: $text-tertiary;
      color: #fff;
      box-shadow: none;
      cursor: not-allowed;
    }

    .el-icon {
      font-size: 18px;
    }
  }
}

// 知情同意书弹窗
.consent-dialog {
  :deep(.el-dialog__body) {
    max-height: 60vh;
    overflow-y: auto;
    padding: 20px;
  }
}

.consent-document {
  h3 {
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
    text-align: center;
    margin-bottom: 20px;
  }

  h4 {
    font-size: $font-md;
    font-weight: 600;
    color: $text-primary;
    margin: 16px 0 10px;
  }

  p {
    font-size: $font-md;
    color: $text-secondary;
    line-height: 1.8;
    margin-bottom: 8px;
  }
}

.dialog-footer {
  display: flex;
  gap: 12px;

  .el-button {
    flex: 1;
  }

  .el-button--primary {
    background: $primary;
    border-color: $primary;
    color: #fff;

    &:hover {
      background: $primary-dark;
      border-color: $primary-dark;
    }
  }
}

// 响应式适配
@media (max-width: 375px) {
  .page-content {
    padding: $spacing-sm;
  }

  .section-card {
    padding: $spacing-md;
  }

  .tag-item {
    padding: 6px 12px;
    font-size: $font-sm;
  }

  .patient-item {
    padding: $spacing-md;

    .patient-avatar {
      width: 40px;
      height: 40px;
    }
  }
}
</style>
