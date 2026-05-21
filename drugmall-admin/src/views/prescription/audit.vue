<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import { getAuditDetail, auditPrescription, getPreCheckResult, getAuditLogs } from '@/api/prescription'
import type { PrescriptionDetail, PreCheckResult, AuditRecord } from '@/types/prescription'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref<PrescriptionDetail | null>(null)
const preCheckResult = ref<PreCheckResult | null>(null)
const auditLogs = ref<AuditRecord[]>([])
const auditForm = reactive({
  result: 'pass',
  opinion: '',
  suggestion: ''
})

const typeMap: Record<string, string> = {
  normal: '普通处方',
  emergency: '急诊处方',
  chronic: '慢病处方'
}

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '审核通过', type: 'success' },
  2: { label: '审核驳回', type: 'danger' },
  3: { label: '已发药', type: 'info' },
  4: { label: '已取消', type: 'info' }
}

const loadDetail = async () => {
  loading.value = true
  try {
    const id = Number(route.params.id)
    detail.value = await getAuditDetail(id)

    try {
      preCheckResult.value = await getPreCheckResult(id)
    } catch {
      preCheckResult.value = null
    }

    try {
      auditLogs.value = await getAuditLogs(id)
    } catch {
      auditLogs.value = []
    }
  } catch {
    console.error('获取审核详情失败')
  } finally {
    loading.value = false
  }
}

const submitAudit = async () => {
  if (auditForm.result === 'reject' && !auditForm.opinion.trim()) {
    ElMessage.warning('驳回时审核意见必填')
    return
  }
  loading.value = true
  try {
    const id = Number(route.params.id)
    await auditPrescription(id, {
      result: auditForm.result,
      opinion: auditForm.opinion,
      suggestion: auditForm.suggestion
    })
    ElMessage.success('审核完成')
    router.back()
  } catch {
    console.error('审核失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<template>
  <div v-loading="loading" class="prescription-audit-container">
    <el-page-header @back="router.back()" title="返回" style="margin-bottom: 20px">
      <template #content>
        <span class="page-title">处方审核</span>
        <el-tag v-if="detail" :type="statusMap[detail.status]?.type as any" style="margin-left: 12px">
          {{ statusMap[detail.status]?.label }}
        </el-tag>
      </template>
    </el-page-header>

    <el-row :gutter="20">
      <!-- 左侧：处方详情 -->
      <el-col :span="16">
        <el-card shadow="never" class="info-card">
          <template #header>处方信息</template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="处方编号">{{ detail?.prescriptionNo }}</el-descriptions-item>
            <el-descriptions-item label="处方类型">
              <el-tag size="small">{{ typeMap[detail?.type || ''] }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="患者姓名">{{ detail?.patientName }}</el-descriptions-item>
            <el-descriptions-item label="患者性别">
              {{ detail?.patientGender === 1 ? '男' : detail?.patientGender === 0 ? '女' : '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="患者年龄">{{ detail?.patientAge || '-' }}岁</el-descriptions-item>
            <el-descriptions-item label="过敏史">{{ detail?.allergyHistory || '无' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 医生信息 -->
        <el-card shadow="never" style="margin-top: 20px">
          <template #header>医生信息</template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="医生姓名">{{ detail?.doctorName }}</el-descriptions-item>
            <el-descriptions-item label="科室">{{ detail?.doctorDepartment }}</el-descriptions-item>
            <el-descriptions-item label="职称">{{ detail?.doctorTitle }}</el-descriptions-item>
            <el-descriptions-item label="医院">{{ detail?.doctorHospital }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 诊断信息 -->
        <el-card shadow="never" style="margin-top: 20px">
          <template #header>诊断信息</template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="诊断结果">{{ detail?.diagnosis }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 药品列表 -->
        <el-card shadow="never" style="margin-top: 20px">
          <template #header>
            <span>药品列表</span>
            <span style="margin-left: 12px; color: #909399">共 {{ detail?.drugCount }} 种</span>
          </template>
          <el-table :data="detail?.items" border stripe>
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="drugName" label="药品名称" min-width="150" />
            <el-table-column prop="specification" label="规格" width="110" />
            <el-table-column prop="usage" label="用法" width="80" />
            <el-table-column prop="dosage" label="用量" width="90" />
            <el-table-column prop="frequency" label="频次" width="100" />
            <el-table-column prop="quantity" label="数量" width="80" align="center" />
            <el-table-column prop="amount" label="金额" width="100" align="right">
              <template #default="{ row }">&#165;{{ row.amount }}</template>
            </el-table-column>
          </el-table>
          <div style="text-align: right; margin-top: 16px; font-size: 16px; font-weight: 600">
            合计：&#165;{{ detail?.totalAmount }}
          </div>
        </el-card>

        <!-- 系统预审 -->
        <el-card v-if="preCheckResult" shadow="never" style="margin-top: 20px">
          <template #header>
            <span>系统预审</span>
            <el-tag :type="preCheckResult.status === 'pass' ? 'success' : preCheckResult.status === 'warn' ? 'warning' : 'danger'" size="small" style="margin-left: 8px">
              {{ preCheckResult.status === 'pass' ? '通过' : preCheckResult.status === 'warn' ? '警告' : '驳回' }}
            </el-tag>
          </template>
          <el-alert v-for="(item, i) in preCheckResult.items" :key="i" :type="item.severity === 'high' ? 'error' : item.severity === 'medium' ? 'warning' : 'info'" :closable="false" style="margin-bottom: 8px">
            <template #title>{{ item.checkType }}</template>
            {{ item.description }}
          </el-alert>
        </el-card>
      </el-col>

      <!-- 右侧：审核操作 -->
      <el-col :span="8">
        <el-card shadow="never" class="side-card">
          <template #header>审核操作</template>
          <el-form :model="auditForm" label-width="80px">
            <el-form-item label="审核结果">
              <el-radio-group v-model="auditForm.result">
                <el-radio value="pass">通过</el-radio>
                <el-radio value="reject">驳回</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="审核意见">
              <el-input v-model="auditForm.opinion" type="textarea" :rows="4" :placeholder="auditForm.result === 'reject' ? '驳回原因（必填）' : '审核备注（可选）'" />
            </el-form-item>
            <el-form-item label="修改建议">
              <el-input v-model="auditForm.suggestion" type="textarea" :rows="3" placeholder="药品调整建议" />
            </el-form-item>
            <el-button type="primary" style="width: 100%; margin-top: 12px" :loading="loading" @click="submitAudit">提交审核</el-button>
          </el-form>
        </el-card>

        <!-- 审核记录 -->
        <el-card shadow="never" style="margin-top: 20px">
          <template #header>
            <span>操作记录</span>
            <el-tag :icon="Document" style="margin-left: 8px">{{ detail?.prescriptionNo }}</el-tag>
          </template>
          <el-timeline>
            <el-timeline-item v-for="log in auditLogs" :key="log.id" :timestamp="log.auditTime" placement="top">
              <el-card>
                <p>{{ log.action }} - {{ log.result === 'pass' ? '通过' : '驳回' }}</p>
                <p v-if="log.opinion" style="color: #909399; font-size: 12px">{{ log.opinion }}</p>
                <p style="color: #909399; font-size: 12px">操作人：{{ log.auditorName }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped lang="scss">
.prescription-audit-container { padding: 20px; }
.info-card, .side-card { min-height: 200px; }
.page-title { font-size: 18px; font-weight: 600; }
</style>
