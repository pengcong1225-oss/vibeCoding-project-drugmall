<template>
  <div class="symptom-test-page">
    <!-- 顶部导航 -->
    <div class="page-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <h2 class="page-title">症状自测</h2>
      <div class="placeholder"></div>
    </div>

    <!-- 步骤指示器 -->
    <div class="steps-indicator">
      <div class="step" :class="{ active: currentStep >= 1, completed: currentStep > 1 }">
        <div class="step-number">1</div>
        <div class="step-text">选择部位</div>
      </div>
      <div class="step-line" :class="{ active: currentStep > 1 }"></div>
      <div class="step" :class="{ active: currentStep >= 2, completed: currentStep > 2 }">
        <div class="step-number">2</div>
        <div class="step-text">选择症状</div>
      </div>
      <div class="step-line" :class="{ active: currentStep > 2 }"></div>
      <div class="step" :class="{ active: currentStep >= 3 }">
        <div class="step-number">3</div>
        <div class="step-text">查看结果</div>
      </div>
    </div>

    <!-- 步骤1: 选择部位 -->
    <div v-if="currentStep === 1" class="step-content">
      <h3 class="section-title">请选择不适部位（可多选）</h3>
      <div class="body-parts-grid">
        <div
            v-for="part in bodyParts"
            :key="part.id"
            class="body-part-item"
            :class="{ selected: selectedBodyParts.includes(part.id) }"
            @click="toggleBodyPart(part.id)"
        >
          <el-icon class="part-icon"><component :is="part.icon" /></el-icon>
          <span class="part-name">{{ part.name }}</span>
        </div>
      </div>
      <div class="step-actions">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" :disabled="selectedBodyParts.length === 0" @click="nextStep">
          下一步
        </el-button>
      </div>
    </div>

    <!-- 步骤2: 选择症状 -->
    <div v-if="currentStep === 2" class="step-content">
      <h3 class="section-title">请选择您的症状（可多选）</h3>
      <div class="symptoms-list">
        <div
            v-for="symptom in currentSymptoms"
            :key="symptom.id"
            class="symptom-item"
            :class="{ selected: selectedSymptoms.includes(symptom.id) }"
            @click="toggleSymptom(symptom.id)"
        >
          <el-checkbox :model-value="selectedSymptoms.includes(symptom.id)" />
          <div class="symptom-info">
            <div class="symptom-name">{{ symptom.name }}</div>
            <div class="symptom-desc">{{ symptom.description }}</div>
          </div>
        </div>
      </div>
      <div class="additional-info">
        <el-form label-position="top">
          <el-form-item label="持续时间">
            <el-select v-model="duration" placeholder="请选择持续时间">
              <el-option label="不到1天" value="不到1天" />
              <el-option label="1-3天" value="1-3天" />
              <el-option label="3-7天" value="3-7天" />
              <el-option label="超过7天" value="超过7天" />
            </el-select>
          </el-form-item>
          <el-form-item label="严重程度">
            <el-radio-group v-model="severity">
              <el-radio label="轻度">轻度</el-radio>
              <el-radio label="中度">中度</el-radio>
              <el-radio label="重度">重度</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="其他描述">
            <el-input
                v-model="description"
                type="textarea"
                :rows="3"
                placeholder="请描述其他症状或注意事项..."
            />
          </el-form-item>
        </el-form>
      </div>
      <div class="step-actions">
        <el-button @click="prevStep">上一步</el-button>
        <el-button type="primary" :disabled="selectedSymptoms.length === 0" :loading="testing" @click="submitTest">
          提交
        </el-button>
      </div>
    </div>

    <!-- 步骤3: 查看结果 -->
    <div v-if="currentStep === 3" class="step-content result-content">
      <div v-if="testing" class="loading-state">
        <el-icon class="loading-icon"><Loading /></el-icon>
        <p>正在分析您的症状...</p>
      </div>
      <div v-else-if="testResult" class="result-section">
        <div class="result-header">
          <el-icon class="result-icon" :class="testResult.urgencyLevel">
            <Warning v-if="testResult.needDoctor" />
            <CircleCheck v-else />
          </el-icon>
          <div class="result-title">
            <h3>自测结果</h3>
            <el-tag :type="getUrgencyType(testResult.urgencyLevel)">
              {{ testResult.urgencyLevel }}紧急
            </el-tag>
          </div>
        </div>

        <div class="result-card">
          <h4>分析建议</h4>
          <div class="advice-content" v-html="formatMessage(testResult.advice)"></div>
        </div>

        <div v-if="testResult.recommendedDrugs?.length" class="result-card">
          <h4>推荐药品</h4>
          <div class="drug-list">
            <div
                v-for="drug in testResult.recommendedDrugs"
                :key="drug.id"
                class="drug-item"
                @click="goToDrugDetail(drug.id)"
            >
              <img :src="drug.image" :alt="drug.name">
              <div class="drug-info">
                <span class="name">{{ drug.name }}</span>
                <span class="spec">{{ drug.spec }}</span>
                <span class="price">¥{{ drug.price }}</span>
              </div>
              <el-button size="small" type="primary" @click.stop="addToCart(drug)">加入购物车</el-button>
            </div>
          </div>
        </div>

        <div v-if="testResult.precautions?.length" class="result-card warning-card">
          <h4><el-icon><Warning /></el-icon> 注意事项</h4>
          <ul>
            <li v-for="(item, index) in testResult.precautions" :key="index">{{ item }}</li>
          </ul>
        </div>

        <div v-if="testResult.nextSteps?.length" class="result-card">
          <h4>下一步建议</h4>
          <div class="next-steps">
            <div v-for="(step, index) in testResult.nextSteps" :key="index" class="step-item">
              <div class="step-number">{{ index + 1 }}</div>
              <div class="step-text">{{ step }}</div>
            </div>
          </div>
        </div>

        <div class="result-actions">
          <el-button @click="resetTest">重新自测</el-button>
          <el-button type="primary" @click="goToInquiry">咨询医生</el-button>
          <el-button v-if="testResult.recommendedDrugs?.length" @click="goToCart">去购药</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { aiAssistantApi } from '@/api/modules/ai-assistant'
import { ROUTES, getDrugDetailRoute } from '@/constants/routes'
import type { SymptomTestResponse, RecommendedDrug } from '@/api/modules/ai-assistant'

const router = useRouter()

const currentStep = ref(1)
const selectedBodyParts = ref<string[]>([])
const selectedSymptoms = ref<string[]>([])
const duration = ref('')
const severity = ref('轻度')
const description = ref('')
const testing = ref(false)
const testResult = ref<SymptomTestResponse | null>(null)

const bodyParts = [
  { id: 'head', name: '头部/面部', icon: 'User' },
  { id: 'neck', name: '颈部/咽喉', icon: 'User' },
  { id: 'chest', name: '胸部', icon: 'User' },
  { id: 'abdomen', name: '腹部', icon: 'User' },
  { id: 'back', name: '背部/腰部', icon: 'User' },
  { id: 'limbs', name: '四肢/关节', icon: 'User' },
  { id: 'skin', name: '皮肤', icon: 'User' },
  { id: 'whole', name: '全身/其他', icon: 'User' }
]

const symptomsMap: Record<string, Array<{ id: string; name: string; description: string }>> = {
  head: [
    { id: 'headache', name: '头痛', description: '持续性或阵发性头痛' },
    { id: 'dizziness', name: '头晕', description: '眩晕或头昏眼花' },
    { id: 'fever', name: '发热', description: '体温超过37.3°C' }
  ],
  neck: [
    { id: 'sore_throat', name: '咽痛', description: '吞咽疼痛或不适' },
    { id: 'cough', name: '咳嗽', description: '干咳或有痰' },
    { id: 'hoarseness', name: '声音嘶哑', description: '声音改变或失声' }
  ],
  chest: [
    { id: 'chest_pain', name: '胸痛', description: '胸部疼痛或不适' },
    { id: 'palpitation', name: '心悸', description: '心跳加速或不规律' },
    { id: 'shortness_breath', name: '呼吸困难', description: '气短或呼吸不畅' }
  ],
  abdomen: [
    { id: 'stomach_pain', name: '腹痛', description: '腹部疼痛或不适' },
    { id: 'diarrhea', name: '腹泻', description: '大便次数增多或稀便' },
    { id: 'nausea', name: '恶心呕吐', description: '胃部不适或呕吐' }
  ],
  back: [
    { id: 'back_pain', name: '腰背痛', description: '腰部或背部疼痛' },
    { id: 'muscle_pain', name: '肌肉酸痛', description: '肌肉疼痛或乏力' }
  ],
  limbs: [
    { id: 'joint_pain', name: '关节痛', description: '关节疼痛或肿胀' },
    { id: 'numbness', name: '麻木', description: '四肢麻木或刺痛' },
    { id: 'weakness', name: '乏力', description: '全身无力' }
  ],
  skin: [
    { id: 'rash', name: '皮疹', description: '皮肤出现红疹或斑点' },
    { id: 'itching', name: '瘙痒', description: '皮肤瘙痒不适' },
    { id: 'allergy', name: '过敏', description: '皮肤过敏反应' }
  ],
  whole: [
    { id: 'fever_general', name: '发热', description: '体温超过37.3°C' },
    { id: 'fatigue', name: '疲劳', description: '全身乏力' },
    { id: 'insomnia', name: '失眠', description: '睡眠质量差' }
  ]
}

const currentSymptoms = computed(() => {
  const symptoms: Array<{ id: string; name: string; description: string }> = []
  selectedBodyParts.value.forEach(partId => {
    if (symptomsMap[partId]) {
      symptoms.push(...symptomsMap[partId])
    }
  })
  return symptoms
})

const toggleBodyPart = (partId: string) => {
  const index = selectedBodyParts.value.indexOf(partId)
  if (index > -1) {
    selectedBodyParts.value.splice(index, 1)
  } else {
    selectedBodyParts.value.push(partId)
  }
}

const toggleSymptom = (symptomId: string) => {
  const index = selectedSymptoms.value.indexOf(symptomId)
  if (index > -1) {
    selectedSymptoms.value.splice(index, 1)
  } else {
    selectedSymptoms.value.push(symptomId)
  }
}

const nextStep = () => {
  if (currentStep.value < 3) {
    currentStep.value++
  }
}

const prevStep = () => {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

const submitTest = async () => {
  testing.value = true
  currentStep.value = 3

  try {
    const symptomNames = selectedSymptoms.value.map(id => {
      for (const symptoms of Object.values(symptomsMap)) {
        const symptom = symptoms.find(s => s.id === id)
        if (symptom) return symptom.name
      }
      return id
    })

    const response = await aiAssistantApi.symptomTest({
      symptoms: symptomNames,
      bodyPart: selectedBodyParts.value.join('、'),
      duration: duration.value,
      severity: severity.value,
      description: description.value
    })

    if (response.data?.data) {
      testResult.value = response.data.data
    }
  } catch (error) {
    console.error('症状自测失败:', error)
    ElMessage.error('症状自测失败，请稍后再试')
    currentStep.value = 2
  } finally {
    testing.value = false
  }
}

const resetTest = () => {
  currentStep.value = 1
  selectedBodyParts.value = []
  selectedSymptoms.value = []
  duration.value = ''
  severity.value = '轻度'
  description.value = ''
  testResult.value = null
}

const formatMessage = (content: string) => {
  return content.replace(/\n/g, '<br>')
}

const getUrgencyType = (level?: string) => {
  switch (level) {
    case '高': return 'danger'
    case '中': return 'warning'
    default: return 'success'
  }
}

const goBack = () => router.back()
const goToDrugDetail = (id: string) => router.push(getDrugDetailRoute(id))
const goToInquiry = () => router.push(ROUTES.INQUIRY)
const goToCart = () => router.push(ROUTES.CART)

const addToCart = (drug: RecommendedDrug) => {
  ElMessage.success(`${drug.name} 已加入购物车`)
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.symptom-test-page {
  min-height: 100vh;
  background: $bg-primary;
  display: flex;
  flex-direction: column;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-md;
  padding-top: calc($safe-area-top + $spacing-md);
  background: $bg-white;
  border-bottom: 1px solid #E8E8E8;

  .back-btn, .placeholder {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }

  .page-title {
    font-size: $font-lg;
    font-weight: 600;
    color: $text-primary;
  }
}

.steps-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-lg;
  background: $bg-white;

  .step {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: $spacing-xs;

    .step-number {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background: #D9D9D9;
      color: $text-white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: $font-sm;
      font-weight: 600;
    }

    .step-text {
      font-size: $font-xs;
      color: $text-tertiary;
    }

    &.active {
      .step-number {
        background: $primary;
      }
      .step-text {
        color: $primary;
      }
    }

    &.completed {
      .step-number {
        background: $success;
      }
    }
  }

  .step-line {
    width: 60px;
    height: 2px;
    background: #D9D9D9;
    margin: 0 $spacing-sm;

    &.active {
      background: $primary;
    }
  }
}

.step-content {
  flex: 1;
  padding: $spacing-md;
  overflow-y: auto;

  .section-title {
    font-size: $font-md;
    font-weight: 500;
    color: $text-primary;
    margin-bottom: $spacing-md;
  }
}

.body-parts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-md;
  margin-bottom: $spacing-lg;

  .body-part-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: $spacing-lg;
    background: $bg-white;
    border: 2px solid #E8E8E8;
    border-radius: $radius-lg;
    cursor: pointer;
    transition: all 0.3s;

    .part-icon {
      font-size: 32px;
      color: $text-tertiary;
      margin-bottom: $spacing-sm;
    }

    .part-name {
      font-size: $font-sm;
      color: $text-primary;
    }

    &.selected {
      border-color: $primary;
      background: rgba($primary, 0.1);

      .part-icon {
        color: $primary;
      }
    }
  }
}

.symptoms-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;
  margin-bottom: $spacing-lg;

  .symptom-item {
    display: flex;
    align-items: center;
    gap: $spacing-md;
    padding: $spacing-md;
    background: $bg-white;
    border: 1px solid #E8E8E8;
    border-radius: $radius-md;
    cursor: pointer;
    transition: all 0.3s;

    &.selected {
      border-color: $primary;
      background: rgba($primary, 0.05);
    }

    .symptom-info {
      flex: 1;

      .symptom-name {
        font-size: $font-md;
        color: $text-primary;
        margin-bottom: 2px;
      }

      .symptom-desc {
        font-size: $font-xs;
        color: $text-tertiary;
      }
    }
  }
}

.additional-info {
  background: $bg-white;
  border-radius: $radius-lg;
  padding: $spacing-md;
  margin-bottom: $spacing-lg;
}

.step-actions {
  display: flex;
  gap: $spacing-md;
  padding: $spacing-md;
  background: $bg-white;
  border-radius: $radius-lg;
  position: sticky;
  bottom: $spacing-md;
}

.result-content {
  .loading-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: $spacing-xxl;

    .loading-icon {
      font-size: 48px;
      color: $primary;
      animation: spin 1s linear infinite;
    }

    p {
      margin-top: $spacing-md;
      font-size: $font-md;
      color: $text-secondary;
    }
  }

  .result-section {
    display: flex;
    flex-direction: column;
    gap: $spacing-md;
  }

  .result-header {
    display: flex;
    align-items: center;
    gap: $spacing-md;
    padding: $spacing-lg;
    background: $bg-white;
    border-radius: $radius-lg;

    .result-icon {
      font-size: 48px;

      &.高 {
        color: #FF4D4F;
      }
      &.中 {
        color: #FAAD14;
      }
      &.低 {
        color: $success;
      }
    }

    .result-title {
      flex: 1;

      h3 {
        font-size: $font-lg;
        color: $text-primary;
        margin-bottom: $spacing-xs;
      }
    }
  }

  .result-card {
    background: $bg-white;
    border-radius: $radius-lg;
    padding: $spacing-md;

    h4 {
      font-size: $font-md;
      color: $text-primary;
      margin-bottom: $spacing-md;
      display: flex;
      align-items: center;
      gap: $spacing-xs;
    }

    .advice-content {
      font-size: $font-sm;
      color: $text-secondary;
      line-height: 1.6;
    }

    .drug-list {
      display: flex;
      flex-direction: column;
      gap: $spacing-sm;

      .drug-item {
        display: flex;
        align-items: center;
        gap: $spacing-sm;
        padding: $spacing-sm;
        background: $bg-primary;
        border-radius: $radius-md;
        cursor: pointer;

        img {
          width: 60px;
          height: 60px;
          border-radius: $radius-sm;
          object-fit: cover;
        }

        .drug-info {
          flex: 1;
          display: flex;
          flex-direction: column;

          .name {
            font-size: $font-sm;
            color: $text-primary;
          }

          .spec {
            font-size: $font-xs;
            color: $text-tertiary;
          }

          .price {
            font-size: $font-sm;
            color: #FF4D4F;
            font-weight: 500;
          }
        }
      }
    }

    &.warning-card {
      background: #FFFBE6;
      border: 1px solid #FFE58F;

      ul {
        list-style: none;
        padding: 0;

        li {
          font-size: $font-sm;
          color: $text-secondary;
          padding: $spacing-xs 0;
          padding-left: $spacing-lg;
          position: relative;

          &::before {
            content: '⚠';
            position: absolute;
            left: 0;
          }
        }
      }
    }

    .next-steps {
      .step-item {
        display: flex;
        align-items: center;
        gap: $spacing-md;
        padding: $spacing-sm 0;

        .step-number {
          width: 24px;
          height: 24px;
          border-radius: 50%;
          background: $primary;
          color: $text-white;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: $font-xs;
        }

        .step-text {
          font-size: $font-sm;
          color: $text-primary;
        }
      }
    }
  }

  .result-actions {
    display: flex;
    gap: $spacing-sm;
    padding: $spacing-md;
    background: $bg-white;
    border-radius: $radius-lg;
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
