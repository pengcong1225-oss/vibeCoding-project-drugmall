<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePrescriptionStore } from '@/stores/prescription'
import { usePatientStore } from '@/stores/patient'
import { imService } from '@/utils/im'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const prescriptionStore = usePrescriptionStore()
const patientStore = usePatientStore()

const consultationId = route.query.consultationId as string
const patientId = route.query.patientId as string
const patientName = (route.query.patientName as string) || '未知患者'
const patientAge = Number(route.query.patientAge) || 0
const patientGender = (route.query.patientGender as string) || '未知'

// 表单数据
const prescriptionForm = ref({
  diagnosis: '',
  drugs: [] as any[],
  remarks: ''
})

// 当前编辑的药品
const currentDrug = ref({
  name: '',
  spec: '',
  unit: '盒',
  price: 0,
  quantity: 1,
  dosage: '1粒',
  frequency: '每日2次',
  duration: '7天',
  remark: ''
})

// 药品搜索
const drugSearchKey = ref('')
const showDrugSearch = ref(false)

// 常用药品数据
const commonDrugs = [
  { id: 'D001', name: '布洛芬缓释胶囊', spec: '0.3g*20粒', unit: '盒', price: 25.00 },
  { id: 'D002', name: '感冒灵颗粒', spec: '10g*9袋', unit: '盒', price: 18.50 },
  { id: 'D003', name: '奥美拉唑肠溶胶囊', spec: '20mg*28粒', unit: '盒', price: 45.00 },
  { id: 'D004', name: '氯雷他定片', spec: '10mg*6片', unit: '盒', price: 12.50 },
  { id: 'D005', name: '硝苯地平缓释片', spec: '10mg*30片', unit: '盒', price: 15.80 },
  { id: 'D006', name: '二甲双胍片', spec: '0.5g*20片', unit: '盒', price: 8.50 }
]

// 频率选项
const frequencyOptions = ['每日1次', '每日2次', '每日3次', '每日4次', '每12小时1次', '每8小时1次']

// 计算总金额
const totalAmount = computed(() => {
  return prescriptionForm.value.drugs.reduce((sum, drug) => sum + (drug.price * drug.quantity), 0)
})

// 添加药品
const addDrug = () => {
  if (!currentDrug.value.name) {
    alert('请输入药品名称')
    return
  }
  prescriptionForm.value.drugs.push({ ...currentDrug.value })
  resetDrugForm()
  showDrugSearch.value = false
}

// 选择常用药品
const selectCommonDrug = (drug: any) => {
  currentDrug.value.name = drug.name
  currentDrug.value.spec = drug.spec
  currentDrug.value.unit = drug.unit
  currentDrug.value.price = drug.price
  showDrugSearch.value = false
}

// 重置药品表单
const resetDrugForm = () => {
  currentDrug.value = {
    name: '',
    spec: '',
    unit: '盒',
    price: 0,
    quantity: 1,
    dosage: '1粒',
    frequency: '每日2次',
    duration: '7天',
    remark: ''
  }
}

// 删除药品
const removeDrug = (index: number) => {
  prescriptionForm.value.drugs.splice(index, 1)
}

// 提交处方
const submitPrescription = async () => {
  if (!prescriptionForm.value.diagnosis) {
    ElMessage.warning('请输入诊断')
    return
  }
  if (prescriptionForm.value.drugs.length === 0) {
    ElMessage.warning('请添加至少一种药品')
    return
  }

  try {
    await prescriptionStore.createPrescription({
      patientId: patientId,
      patientName: patientName,
      patientAge: patientAge,
      patientGender: patientGender,
      consultationId: consultationId,
      diagnosis: prescriptionForm.value.diagnosis,
      drugs: prescriptionForm.value.drugs,
      totalAmount: totalAmount.value
    })

    // 通过IM发送处方消息给患者
    await sendPrescriptionMessage()

    ElMessage.success('处方已提交并发送给患者')
    router.push('/prescription')
  } catch (error: any) {
    console.error('提交处方失败:', error)
    ElMessage.error('提交失败: ' + (error.message || '未知错误'))
  }
}

// 通过IM发送处方消息
async function sendPrescriptionMessage() {
  try {
    // 从 store 或本地存储获取当前登录医生ID
    const currentDoctorId = localStorage.getItem('doctorId') || 'DOC001'

    // 确保IM已初始化
    if (!imService.isInitialized) {
      await imService.init(currentDoctorId, 'doctor')
    }

    const conversationId = `C2C_patient_${patientId}`

    const drugsList = prescriptionForm.value.drugs.map((drug, index) =>
      `${index + 1}. ${drug.name} ${drug.spec} - ${drug.dosage} ${drug.frequency} ${drug.duration} ×${drug.quantity}${drug.unit}`
    ).join('\n')

    const prescriptionContent = `【电子处方】\n诊断：${prescriptionForm.value.diagnosis}\n药品清单：\n${drugsList}\n合计：¥${totalAmount.value.toFixed(2)}`

    await imService.sendTextMessage(conversationId, prescriptionContent)
    console.log('[处方] 处方消息已发送给患者')
  } catch (error) {
    console.error('[处方] 发送IM消息失败:', error)
    throw new Error('处方已保存但发送通知失败')
  }
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(() => {
  patientStore.fetchPatients()
})
</script>

<template>
  <div class="create-prescription-page">
    <!-- 顶部导航 -->
    <div class="page-header">
      <div class="header-left" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
      </div>
      <h1 class="page-title">开具处方</h1>
      <div class="header-right">
        <button class="btn-submit" @click="submitPrescription">提交</button>
      </div>
    </div>

    <!-- 患者信息 -->
    <div class="patient-card">
      <div class="patient-header">
        <div class="patient-avatar">{{ patientName.charAt(0) }}</div>
        <div class="patient-info">
          <div class="patient-name">{{ patientName }} <span class="patient-gender">{{ patientGender }}</span></div>
          <div class="patient-age">{{ patientAge }}岁</div>
        </div>
      </div>
    </div>

    <!-- 表单区域 -->
    <div class="form-area">
      <!-- 诊断 -->
      <div class="form-section">
        <div class="section-title">诊断</div>
        <div class="input-wrap">
          <input
            v-model="prescriptionForm.diagnosis"
            type="text"
            placeholder="请输入诊断（如：急性上呼吸道感染）"
            class="diagnosis-input"
          />
        </div>
      </div>

      <!-- 药品列表 -->
      <div class="form-section">
        <div class="section-title">
          药品清单
          <span class="drug-count">({{ prescriptionForm.drugs.length }})</span>
        </div>

        <!-- 已添加药品 -->
        <div v-if="prescriptionForm.drugs.length > 0" class="drug-list">
          <div v-for="(drug, index) in prescriptionForm.drugs" :key="index" class="drug-item">
            <div class="drug-info">
              <div class="drug-name">{{ drug.name }} <span class="drug-spec">{{ drug.spec }}</span></div>
              <div class="drug-usage">
                {{ drug.dosage }}/{{ drug.frequency }}/{{ drug.duration }}
                <span v-if="drug.remark" class="drug-remark">({{ drug.remark }})</span>
              </div>
            </div>
            <div class="drug-amount">
              ×{{ drug.quantity }}{{ drug.unit }}
              <span class="amount">¥{{ (drug.price * drug.quantity).toFixed(2) }}</span>
            </div>
            <div class="drug-actions" @click="removeDrug(index)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
              </svg>
            </div>
          </div>
        </div>

        <!-- 添加药品按钮 -->
        <div class="add-drug-btn" @click="showDrugSearch = true">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"></line>
            <line x1="5" y1="12" x2="19" y2="12"></line>
          </svg>
          <span>添加药品</span>
        </div>
        
        <!-- 药品表单 -->
        <div v-if="showDrugSearch" class="drug-form">
          <div class="form-row">
            <input
              v-model="currentDrug.name"
              type="text"
              placeholder="药品名称"
              class="drug-name-input"
              @focus="showDrugSearch = true"
            />
            <!-- 搜索建议 -->
            <div v-if="showDrugSearch && drugSearchKey.length >= 0" class="search-suggestions">
              <div class="suggestions-title">常用药品</div>
              <div
                v-for="drug in commonDrugs"
                :key="drug.id"
                class="suggestion-item"
                @click="selectCommonDrug(drug)"
              >
                <span class="suggestion-name">{{ drug.name }}</span>
                <span class="suggestion-spec">{{ drug.spec }}</span>
                <span class="suggestion-price">¥{{ drug.price.toFixed(2) }}</span>
              </div>
            </div>
          </div>
          
          <div class="form-row form-grid">
            <input v-model="currentDrug.spec" type="text" placeholder="规格" />
            <input v-model="currentDrug.price" type="number" placeholder="单价" />
          </div>
          
          <div class="form-row form-grid-3">
            <input v-model="currentDrug.dosage" type="text" placeholder="单次用量" />
            <select v-model="currentDrug.frequency">
              <option v-for="freq in frequencyOptions" :key="freq" :value="freq">{{ freq }}</option>
            </select>
            <input v-model="currentDrug.duration" type="text" placeholder="用药天数" />
          </div>
          
          <div class="form-row form-grid">
            <input v-model="currentDrug.quantity" type="number" placeholder="数量" />
            <input v-model="currentDrug.remark" type="text" placeholder="备注(如：饭前服用)" />
          </div>
          
          <button class="btn-confirm-add" @click="addDrug">确认添加</button>
          <button class="btn-cancel" @click="showDrugSearch = false">取消</button>
        </div>
      </div>

      <!-- 备注 -->
      <div class="form-section">
        <div class="section-title">医嘱备注</div>
        <textarea
          v-model="prescriptionForm.remarks"
          placeholder="请输入医嘱备注..."
          class="remarks-input"
          rows="3"
        ></textarea>
      </div>

      <!-- 金额汇总 -->
      <div class="amount-summary">
        <div class="summary-row">
          <span class="label">药品数量</span>
          <span class="value">{{ prescriptionForm.drugs.length }}种</span>
        </div>
        <div class="summary-row total">
          <span class="label">合计金额</span>
          <span class="value">¥{{ totalAmount.toFixed(2) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
$primary: #00B578;
$primary-light: #00C78A;
$text-primary: #333;
$text-secondary: #666;
$text-tertiary: #999;
$bg-primary: #f5f5f5;
$bg-white: #fff;
$border-light: #e8e8e8;

.create-prescription-page {
  min-height: 100vh;
  background: $bg-primary;
  padding-bottom: calc(20px + env(safe-area-inset-bottom));
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid $border-light;
  position: sticky;
  top: 0;
  z-index: 100;
  
  .header-left {
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 4px;
    
    svg {
      width: 24px;
      height: 24px;
      color: $text-primary;
    }
  }
  
  .page-title {
    font-size: 17px;
    font-weight: 600;
    color: $text-primary;
  }
  
  .header-right {
    .btn-submit {
      padding: 6px 16px;
      background: $primary;
      border: none;
      border-radius: 16px;
      color: #fff;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      
      &:active {
        background: darken($primary, 5%);
      }
    }
  }
}

.patient-card {
  margin: 12px;
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  border-radius: 12px;
  padding: 16px;
  color: #fff;
}

.patient-header {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .patient-avatar {
    width: 50px;
    height: 50px;
    background: rgba(255,255,255,0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    font-weight: 600;
  }
  
  .patient-info {
    .patient-name {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 4px;
      
      .patient-gender {
        font-size: 13px;
        opacity: 0.8;
        margin-left: 8px;
      }
    }
    
    .patient-age {
      font-size: 13px;
      opacity: 0.9;
    }
  }
}

.form-area {
  padding: 0 12px;
}

.form-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  
  .section-title {
    font-size: 15px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    gap: 4px;
    
    .drug-count {
      font-size: 13px;
      color: $text-tertiary;
      font-weight: 400;
    }
  }
}

.input-wrap {
  input, textarea {
    width: 100%;
    padding: 12px;
    border: 1px solid $border-light;
    border-radius: 8px;
    font-size: 14px;
    outline: none;
    transition: all 0.2s;
    
    &:focus {
      border-color: $primary;
    }
    
    &::placeholder {
      color: $text-tertiary;
    }
  }
}

.diagnosis-input {
  width: 100%;
  padding: 12px;
  border: 1px solid $border-light;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  
  &:focus {
    border-color: $primary;
  }
}

.remarks-input {
  width: 100%;
  padding: 12px;
  border: 1px solid $border-light;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  resize: none;
  
  &:focus {
    border-color: $primary;
  }
}

.drug-list {
  margin-bottom: 12px;
}

.drug-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #FAFAFA;
  border-radius: 8px;
  margin-bottom: 8px;

  .drug-info {
    flex: 1;

    .drug-name {
      font-size: 14px;
      font-weight: 500;
      color: $text-primary;
      margin-bottom: 4px;

      .drug-spec {
        font-size: 12px;
        color: $text-tertiary;
        margin-left: 4px;
      }
    }

    .drug-usage {
      font-size: 12px;
      color: $text-secondary;

      .drug-remark {
        color: $text-tertiary;
      }
    }
  }

  .drug-amount {
    text-align: right;
    font-size: 13px;
    color: $text-secondary;
    white-space: nowrap;

    .amount {
      display: block;
      color: $primary;
      font-weight: 500;
    }
  }

  .drug-actions {
    padding: 4px;
    cursor: pointer;
    color: $text-tertiary;

    svg {
      width: 18px;
      height: 18px;
    }
  }
}

.add-drug-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 12px;
  border: 1px dashed $border-light;
  border-radius: 8px;
  color: $primary;
  font-size: 14px;
  cursor: pointer;

  svg {
    width: 18px;
    height: 18px;
  }
}

.drug-form {
  background: #FAFAFA;
  border-radius: 8px;
  padding: 12px;
  margin-top: 8px;

  .form-row {
    margin-bottom: 8px;
    position: relative;
  }

  .form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 8px;
  }

  .form-grid-3 {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
    gap: 8px;
  }

  input, select {
    width: 100%;
    padding: 10px 12px;
    border: 1px solid $border-light;
    border-radius: 6px;
    font-size: 13px;
    outline: none;
    background: #fff;

    &:focus {
      border-color: $primary;
    }
  }

  .search-suggestions {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: #fff;
    border: 1px solid $border-light;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    z-index: 10;
    max-height: 200px;
    overflow-y: auto;

    .suggestions-title {
      padding: 8px 12px;
      font-size: 12px;
      color: $text-tertiary;
      border-bottom: 1px solid $border-light;
    }

    .suggestion-item {
      display: flex;
      align-items: center;
      padding: 10px 12px;
      cursor: pointer;

      &:active {
        background: #f5f5f5;
      }

      .suggestion-name {
        flex: 1;
        font-size: 14px;
        color: $text-primary;
      }

      .suggestion-spec {
        font-size: 12px;
        color: $text-tertiary;
        margin-right: 12px;
      }

      .suggestion-price {
        font-size: 13px;
        color: $primary;
        font-weight: 500;
      }
    }
  }

  .btn-confirm-add {
    width: 100%;
    padding: 10px;
    background: $primary;
    color: #fff;
    border: none;
    border-radius: 8px;
    font-size: 14px;
    cursor: pointer;
    margin-top: 4px;

    &:active {
      background: darken($primary, 5%);
    }
  }

  .btn-cancel {
    width: 100%;
    padding: 10px;
    background: #fff;
    color: $text-secondary;
    border: 1px solid $border-light;
    border-radius: 8px;
    font-size: 14px;
    cursor: pointer;
    margin-top: 4px;
  }
}

.amount-summary {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;

  .summary-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 6px 0;
    font-size: 14px;
    color: $text-secondary;

    &.total {
      border-top: 1px solid $border-light;
      margin-top: 8px;
      padding-top: 12px;
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;

      .value {
        color: $primary;
      }
    }
  }
}
</style>
