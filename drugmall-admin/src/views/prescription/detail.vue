<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import { getPrescriptionDetail, cancelPrescription } from '@/api/prescription'
import type { PrescriptionDetail } from '@/types/prescription'

const route = useRoute()
const loading = ref(false)
const detail = ref<PrescriptionDetail | null>(null)

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
    detail.value = await getPrescriptionDetail(id)
  } catch (error) {
    console.error('获取处方详情失败:', error)
  } finally {
    loading.value = false
  }
}

const handleCancel = async () => {
  if (!detail.value) return
  try {
    await ElMessageBox.confirm('确定要取消该处方吗？', '确认取消', { type: 'warning' })
    await cancelPrescription(detail.value.id)
    ElMessage.success('取消成功')
    loadDetail()
  } catch {
    // 取消操作
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<template>
  <div v-loading="loading" class="prescription-detail-container">
    <el-page-header @back="$router.back()" title="返回" style="margin-bottom: 20px">
      <template #content>
        <span class="page-title">处方详情</span>
        <el-tag v-if="detail" :type="statusMap[detail.status]?.type as any" style="margin-left: 12px">
          {{ statusMap[detail.status]?.label }}
        </el-tag>
      </template>
      <template #extra>
        <el-button v-if="detail?.status === 0" type="danger" size="small" @click="handleCancel">取消处方</el-button>
      </template>
    </el-page-header>

    <el-row :gutter="20">
      <!-- 基本信息 -->
      <el-col :span="16">
        <el-card shadow="never" class="info-card">
          <template #header>基本信息</template>
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
            <el-descriptions-item label="用药史">{{ detail?.medicationHistory || '无' }}</el-descriptions-item>
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
            <el-table-column prop="manufacturer" label="厂家" width="120" />
            <el-table-column prop="usage" label="用法" width="80" />
            <el-table-column prop="dosage" label="用量" width="90" />
            <el-table-column prop="frequency" label="频次" width="100" />
            <el-table-column prop="duration" label="疗程" width="80" align="center">
              <template #default="{ row }">{{ row.duration }}天</template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80" align="center" />
            <el-table-column prop="unitPrice" label="单价" width="100" align="right">
              <template #default="{ row }">&#165;{{ row.unitPrice }}</template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" width="100" align="right">
              <template #default="{ row }">&#165;{{ row.amount }}</template>
            </el-table-column>
          </el-table>
          <div style="text-align: right; margin-top: 16px; font-size: 16px; font-weight: 600">
            合计：&#165;{{ detail?.totalAmount }}
          </div>
        </el-card>
      </el-col>

      <!-- 右侧信息 -->
      <el-col :span="8">
        <el-card shadow="never" class="side-card">
          <template #header>关联信息</template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="问诊ID">{{ detail?.consultationId || '-' }}</el-descriptions-item>
            <el-descriptions-item label="订单ID">
              <el-tag v-if="detail?.orderId" type="success" size="small" style="cursor: pointer" @click="$router.push(`/order/detail/${detail?.orderId}`)">
                {{ detail?.orderNo }}
              </el-tag>
              <span v-else>-</span>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="never" style="margin-top: 20px">
          <template #header>
            <span>操作记录</span>
            <el-tag :icon="Document" style="margin-left: 8px">{{ detail?.prescriptionNo }}</el-tag>
          </template>
          <el-timeline>
            <el-timeline-item timestamp="创建" placement="top">
              <el-card>
                <p>处方已创建</p>
                <p style="color: #909399; font-size: 12px">{{ detail?.createTime }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped lang="scss">
.prescription-detail-container { padding: 20px; }
.info-card, .side-card { min-height: 200px; }
.page-title { font-size: 18px; font-weight: 600; }
</style>
