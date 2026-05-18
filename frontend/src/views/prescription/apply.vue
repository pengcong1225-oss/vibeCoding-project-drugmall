<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Plus, User, CircleCheckFilled, Document, FirstAidKit } from '@element-plus/icons-vue'
import { usePrescriptionStore } from '@/stores/prescription'
import { getDrugDetail } from '@/api/modules/drug'
import { getPatientList } from '@/api/modules/patient'
import StepBar from './components/StepBar.vue'
import { ROUTES } from '@/constants/routes'
import { businessApi } from '@/api/modules/business'
import type { Patient } from '@/types/user'
import type { Drug } from '@/types/drug'

const router = useRouter()
const route = useRoute()
const prescriptionStore = usePrescriptionStore()

const currentStep = ref(1)

const patientList = ref<Patient[]>([])
const loadingPatients = ref(false)

const drugInfo = ref<Drug[]>([])

// 当前选中的规格索引
const selectedSpecIndex = ref(0)

const diseaseTags = ref<{ id: string; name: string; selected: boolean }[]>([])

const symptoms = ref('')

const agreedToConsent = ref(false)

const showConsentDialog = ref(false)

const canSubmit = computed(() => {
  return prescriptionStore.hasSelectedPatient && 
         prescriptionStore.hasSelectedDiseases && 
         agreedToConsent.value
})

const selectedDiseases = computed(() => {
  return diseaseTags.value.filter(tag => tag.selected)
})

// 当前选中的价格
const currentPrice = computed(() => {
  if (drugInfo.value.length === 0) return 0
  const drug = drugInfo.value[0]
  
  // 如果有规格列表，使用选中规格的价格
  if (drug.specifications && drug.specifications.length > 0) {
    const selectedSpec = drug.specifications[selectedSpecIndex.value]
    return selectedSpec?.price || drug.price
  }
  
  return drug.price
})

const goBack = () => {
  router.back()
}

const fetchPatientList = async () => {
  loadingPatients.value = true
  try {
    const patients = await getPatientList()
    
    console.log('获取到的患者列表:', patients)
    
    patientList.value = patients.map((p, index) => ({
      // 如果后端返回的id为null或空，使用身份证号作为唯一标识
      id: (p.id && p.id !== 'null' && p.id !== '') ? String(p.id) : `patient_${index}_${p.idCard?.slice(-4) || '0000'}`,
      name: p.name,
      gender: p.gender as 'male' | 'female',
      age: p.age,
      idCard: p.idCard,
      phone: p.phone,
      relationship: p.relationship || '本人',
      isDefault: p.isDefault || false
    }))
    
    console.log('处理后的患者列表:', patientList.value)
    console.log('患者ID列表:', patientList.value.map(p => ({ id: p.id, name: p.name })))
    
    // 自动选择默认患者（如果有的话）
    const defaultPatient = patientList.value.find(p => p.isDefault)
    if (defaultPatient && !prescriptionStore.applyState.selectedPatient) {
      prescriptionStore.selectPatient(defaultPatient)
      console.log('自动选择默认患者:', defaultPatient.name, 'ID:', defaultPatient.id)
    }
  } catch (error) {
    console.error('获取用药人列表失败:', error)
    ElMessage.error('获取用药人列表失败')
  } finally {
    loadingPatients.value = false
  }
}

const fetchDrugInfo = async () => {
  const drugId = route.query.drugId as string
  if (drugId) {
    try {
      const response = await getDrugDetail(drugId)
      console.log('处方申请-药品详情API响应:', response)
      
      // 处理后端返回的数据结构：{ drug: {...}, relatedDrugs: [], ... }
      let drugDetail: any
      if (response && typeof response === 'object' && !Array.isArray(response)) {
        // 如果返回的是 { drug: {...} } 结构，提取drug字段
        drugDetail = (response as any).drug || response
      } else {
        drugDetail = response
      }
      
      console.log('处方申请-解析后的药品详情:', drugDetail)
      
      if (!drugDetail || !drugDetail.id) {
        ElMessage.error('药品信息获取失败')
        return
      }
      
      // 构建完整的药品信息对象，包含所有字段
      drugInfo.value = [{
        id: drugDetail.id,
        name: drugDetail.name,
        genericName: drugDetail.genericName || '',
        specification: drugDetail.specification || '',
        manufacturer: drugDetail.manufacturer || '',
        price: drugDetail.price,
        originalPrice: drugDetail.originalPrice,
        image: drugDetail.image || '',
        images: drugDetail.images || [],
        isRx: drugDetail.isRx || false,
        isNationalEssential: drugDetail.isNationalEssential || false,
        categoryId: drugDetail.categoryId || '',
        categoryName: drugDetail.categoryName || '',
        stock: drugDetail.stock || 0,
        sales: drugDetail.sales || 0,
        status: drugDetail.status || 1,
        // 医保相关字段
        medicalInsuranceCode: drugDetail.medicalInsuranceCode,
        traceabilityCode: drugDetail.traceabilityCode,
        isLongPrescription: drugDetail.isLongPrescription,
        insuranceCategory: drugDetail.insuranceCategory,
        // 规格列表
        specifications: drugDetail.specifications || [],
        // 其他字段
        approvalNumber: drugDetail.approvalNumber,
        barCode: drugDetail.barCode,
        description: drugDetail.description,
        usage: drugDetail.usage,
        disease: drugDetail.disease,
        contraindications: drugDetail.contraindications,
        precautions: drugDetail.precautions,
        adverseReactions: drugDetail.adverseReactions,
        storage: drugDetail.storage,
        validity: drugDetail.validity,
        ingredients: drugDetail.ingredients,
        appearance: drugDetail.appearance,
        drugInteractions: drugDetail.drugInteractions
      }]
      
      console.log('处方申请-最终药品信息:', drugInfo.value)
      
      // 设置默认选中的规格ID（如果有规格列表）
      if (drugDetail.specifications && drugDetail.specifications.length > 0) {
        const defaultSpec = drugDetail.specifications.find((s: any) => s.isDefault) || drugDetail.specifications[0]
        ;(drugInfo.value[0] as any).selectedSpecificationId = defaultSpec.id
        selectedSpecIndex.value = drugDetail.specifications.findIndex((s: any) => s.id === defaultSpec.id)
      }
      
      prescriptionStore.setSelectedDrugs(drugInfo.value)
    } catch (error) {
      console.error('获取药品信息失败:', error)
      ElMessage.error('获取药品信息失败')
    }
  } else {
    console.warn('URL中未传递drugId参数')
    ElMessage.warning('缺少药品ID参数')
  }
}

// 判断患者是否被选中
const isPatientSelected = (patientId: string | number) => {
  const selected = prescriptionStore.applyState.selectedPatient
  if (!selected) return false
  
  // 严格比较，确保类型一致
  return String(selected.id) === String(patientId)
}

const selectPatient = (patient: Patient) => {
  console.log('点击选择就诊人:', patient)
  console.log('当前选中的就诊人:', prescriptionStore.applyState.selectedPatient)
  console.log('选中判断结果:', isPatientSelected(patient.id))
  
  // 如果点击的是已选中的就诊人，不做任何操作
  if (isPatientSelected(patient.id)) {
    console.log('点击的是已选中的就诊人，忽略')
    return
  }
  
  // 选择新的就诊人（会自动替换之前的选择）
  prescriptionStore.selectPatient(patient)
  console.log('已选择就诊人:', patient.name, 'ID:', patient.id)
  
  // 显示提示信息
  ElMessage.success({
    message: `已选择：${patient.name}（${patient.relationship}）`,
    duration: 1500
  })
}

// 选择规格
const selectSpec = (index: number) => {
  selectedSpecIndex.value = index
  
  // 更新药品信息中的价格和规格
  if (drugInfo.value.length > 0 && drugInfo.value[0].specifications) {
    const spec = drugInfo.value[0].specifications[index]
    // 更新显示用价格（实际提交时会使用完整规格信息）
    drugInfo.value[0].price = spec.price
    drugInfo.value[0].specification = spec.specName
    // 保存选中的规格ID，用于提交时传递
    ;(drugInfo.value[0] as any).selectedSpecificationId = spec.id
  }
}

const addPatient = () => {
  router.push(ROUTES.PATIENT_ADD)
}

const toggleDiseaseTag = (tag: typeof diseaseTags.value[0]) => {
  tag.selected = !tag.selected
  prescriptionStore.toggleDisease(tag.name)
}

const openConsent = () => {
  showConsentDialog.value = true
}

const agreeConsent = () => {
  agreedToConsent.value = true
  prescriptionStore.setAgreedToConsent(true)
  showConsentDialog.value = false
}

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
    prescriptionStore.setSymptoms(symptoms.value)
    
    const consultationId = await prescriptionStore.submitPrescriptionApply()
    
    ElMessage.success('申请提交成功')
    
    setTimeout(() => {
      router.push({
        path: `${ROUTES.INQUIRY_WAITING}/${consultationId}`,
        query: {
          doctorId: 'DOC001',
          doctorName: '在线医生'
        }
      })
    }, 1000)
  } catch (error: any) {
    console.error('提交申请失败:', error)
    ElMessage.error(error.message || '提交申请失败，请重试')
  }
}

onMounted(async () => {
  fetchPatientList()
  fetchDrugInfo()
  await loadDiseaseTags()
})

async function loadDiseaseTags() {
  try {
    const res = await businessApi.getDictData('disease_tag')
    console.log('疾病标签API响应:', res)
    
    // http拦截器已经提取了data字段，res直接就是数组
    const dictDataList = Array.isArray(res) ? res : (res?.data || [])
    
    diseaseTags.value = dictDataList.map(item => ({
      id: item.value,
      name: item.label,
      selected: false
    }))
    
    console.log('加载的疾病标签:', diseaseTags.value)
  } catch (error) {
    console.error('加载疾病标签失败:', error)
  }
}
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
              
              <!-- 医保信息 -->
              <div v-if="drug.medicalInsuranceCode || drug.insuranceCategory || drug.isLongPrescription" class="medical-info">
                <div v-if="drug.traceabilityCode" class="info-item">
                  <span class="label">追溯码:</span>
                  <span class="value trace-code">{{ drug.traceabilityCode }}</span>
                </div>
                <div v-if="drug.medicalInsuranceCode" class="info-item">
                  <span class="label">医保编码:</span>
                  <span class="value">{{ drug.medicalInsuranceCode }}</span>
                </div>
                <div v-if="drug.insuranceCategory" class="info-item">
                  <span class="label">医保类别:</span>
                  <span class="value" :class="`category-${drug.insuranceCategory}`">
                    {{ drug.insuranceCategory }}类
                  </span>
                </div>
                <div v-if="drug.isLongPrescription" class="info-item">
                  <span class="label">长处方:</span>
                  <el-tag type="success" size="small" effect="plain">是</el-tag>
                </div>
              </div>
              
              <!-- 规格选择 -->
              <div v-if="drug.specifications && drug.specifications.length > 1" class="spec-selection">
                <div class="spec-label">选择规格:</div>
                <div class="spec-options">
                  <div
                    v-for="(spec, index) in drug.specifications"
                    :key="spec.id"
                    :class="['spec-option', { active: selectedSpecIndex === index }]"
                    @click="selectSpec(index)"
                  >
                    {{ spec.specName }}
                  </div>
                </div>
              </div>
              
              <div class="drug-price">¥{{ currentPrice.toFixed(2) }}</div>
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
              active: isPatientSelected(patient.id)
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
              <el-icon v-if="isPatientSelected(patient.id)">
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
      
      // 医保信息
      .medical-info {
        margin-top: 8px;
        padding: 10px;
        background: #f5f7fa;
        border-radius: 8px;
        
        .info-item {
          display: flex;
          align-items: center;
          gap: 6px;
          margin-bottom: 6px;
          font-size: 12px;
          
          &:last-child {
            margin-bottom: 0;
          }
          
          .label {
            color: $text-secondary;
            min-width: 60px;
            flex-shrink: 0;
          }
          
          .value {
            color: $text-primary;
            font-weight: 500;
            flex: 1;
            
            &.trace-code {
              font-family: 'Courier New', monospace;
              letter-spacing: 0.5px;
              font-size: 11px;
              color: #409eff;
            }
            
            &.category-甲类 {
              color: #52c41a;
            }
            
            &.category-乙类 {
              color: #1890ff;
            }
            
            &.category-丙类 {
              color: #faad14;
            }
          }
        }
      }
      
      // 规格选择
      .spec-selection {
        margin-top: 8px;
        
        .spec-label {
          font-size: 12px;
          color: $text-secondary;
          margin-bottom: 6px;
        }
        
        .spec-options {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;
          
          .spec-option {
            padding: 6px 12px;
            border: 1px solid $border-color;
            border-radius: 16px;
            font-size: 12px;
            color: $text-primary;
            cursor: pointer;
            transition: all 0.2s ease;
            
            &:active {
              opacity: 0.8;
            }
            
            &.active {
              border-color: $primary;
              background: rgba($primary, 0.1);
              color: $primary;
              font-weight: 500;
            }
          }
        }
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
  transition: all 0.3s ease;
  border: 2px solid transparent;
  position: relative;

  &:hover {
    background: $bg-primary;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }

  &.active {
    background: rgba($primary, 0.08);
    border-color: $primary;
    box-shadow: 0 4px 12px rgba($primary, 0.15);
    
    .patient-avatar {
      background: linear-gradient(135deg, $primary 0%, darken($primary, 10%) 100%);
    }
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
