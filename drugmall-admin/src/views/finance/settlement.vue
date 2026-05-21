<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Check } from '@element-plus/icons-vue'
import { getSettlementList, auditSettlement } from '@/api/settlement'
import type { DoctorSettlement } from '@/types/settlement'

const loading = ref(false)
const tableData = ref<DoctorSettlement[]>([])
const total = ref(0)
const auditDialogVisible = ref(false)
const currentSettlement = ref<DoctorSettlement | null>(null)
const auditForm = reactive({
  status: 'approved' as 'approved' | 'rejected',
  remark: ''
})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  doctorId: '',
  doctorName: '',
  status: '',
  period: ''
})

const statusMap: Record<string, { label: string, type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  pending: { label: '待结算', type: 'warning' },
  processing: { label: '结算中', type: 'primary' },
  completed: { label: '已结算', type: 'success' },
  failed: { label: '结算失败', type: 'danger' }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getSettlementList(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    ElMessage.error('获取结算列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.pageNum = 1
  fetchData()
}

function handleReset() {
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    doctorId: '',
    doctorName: '',
    status: '',
    period: ''
  })
  fetchData()
}

function handleAudit(row: DoctorSettlement) {
  currentSettlement.value = row
  auditForm.status = 'approved'
  auditForm.remark = ''
  auditDialogVisible.value = true
}

async function submitAudit() {
  if (!currentSettlement.value) return
  if (auditForm.status === 'rejected' && !auditForm.remark) {
    ElMessage.warning('请填写驳回原因')
    return
  }
  try {
    await auditSettlement(currentSettlement.value.id, auditForm)
    ElMessage.success('操作成功')
    auditDialogVisible.value = false
    fetchData()
  } catch {
    ElMessage.error('操作失败')
  }
}

function handlePageChange(page: number) {
  queryParams.pageNum = page
  fetchData()
}

function handleSizeChange(size: number) {
  queryParams.pageSize = size
  queryParams.pageNum = 1
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="doctor-settlement">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>医生结算</span>
        </div>
      </template>

      <el-form :model="queryParams" inline>
        <el-form-item label="医生ID">
          <el-input v-model="queryParams.doctorId" placeholder="请输入医生ID" clearable />
        </el-form-item>
        <el-form-item label="医生姓名">
          <el-input v-model="queryParams.doctorName" placeholder="请输入医生姓名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option v-for="(v, k) in statusMap" :key="k" :label="v.label" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="doctorId" label="医生ID" width="120" />
        <el-table-column prop="doctorName" label="医生姓名" width="120" />
        <el-table-column prop="period" label="结算周期" width="120" />
        <el-table-column prop="consultationIncome" label="问诊收入" width="120">
          <template #default="{ row }">¥{{ row.consultationIncome.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="prescriptionIncome" label="处方收入" width="120">
          <template #default="{ row }">¥{{ row.prescriptionIncome.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="totalIncome" label="总收入" width="120">
          <template #default="{ row }">¥{{ row.totalIncome.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="platformFee" label="平台抽成" width="120">
          <template #default="{ row }">¥{{ row.platformFee.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="actualIncome" label="实际收入" width="120">
          <template #default="{ row }">¥{{ row.actualIncome.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="settlementCycle" label="结算周期" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type">{{ statusMap[row.status]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'pending'" type="primary" link :icon="Check" @click="handleAudit(row)">审核</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog v-model="auditDialogVisible" title="审核结算" width="500px">
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="医生">
          <el-input :value="currentSettlement?.doctorName" disabled />
        </el-form-item>
        <el-form-item label="结算周期">
          <el-input :value="currentSettlement?.period" disabled />
        </el-form-item>
        <el-form-item label="实际收入">
          <el-input :value="'¥' + (currentSettlement?.actualIncome ?? 0).toFixed(2)" disabled />
        </el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.status">
            <el-radio value="approved">通过</el-radio>
            <el-radio value="rejected">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="auditForm.status === 'rejected'" label="驳回原因">
          <el-input v-model="auditForm.remark" type="textarea" :rows="3" placeholder="请输入驳回原因" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="auditForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
