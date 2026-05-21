<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Check, Close } from '@element-plus/icons-vue'
import { getDrugAuditList, auditDrug } from '@/api/drugAudit'
import type { DrugAudit } from '@/types/drugAudit'

const loading = ref(false)
const tableData = ref<DrugAudit[]>([])
const total = ref(0)
const auditDialogVisible = ref(false)
const currentAudit = ref<DrugAudit | null>(null)
const auditForm = reactive({
  status: 'approved' as 'approved' | 'rejected',
  comment: ''
})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  drugName: '',
  auditType: '',
  status: '',
  startDate: '',
  endDate: ''
})

const typeMap: Record<string, string> = {
  new: '新增药品',
  modify: '修改药品'
}

const statusMap: Record<string, { label: string, type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  pending: { label: '待审核', type: 'warning' },
  approved: { label: '已通过', type: 'success' },
  rejected: { label: '已驳回', type: 'danger' }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getDrugAuditList(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    ElMessage.error('获取审核列表失败')
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
    drugName: '',
    auditType: '',
    status: '',
    startDate: '',
    endDate: ''
  })
  fetchData()
}

function handleAudit(row: DrugAudit) {
  currentAudit.value = row
  auditForm.status = 'approved'
  auditForm.comment = ''
  auditDialogVisible.value = true
}

async function submitAudit() {
  if (!currentAudit.value) return
  if (auditForm.status === 'rejected' && !auditForm.comment) {
    ElMessage.warning('请填写驳回原因')
    return
  }
  try {
    await auditDrug(currentAudit.value.id, auditForm)
    ElMessage.success('审核成功')
    auditDialogVisible.value = false
    fetchData()
  } catch {
    ElMessage.error('审核失败')
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
  <div class="drug-audit">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>药品审核</span>
        </div>
      </template>

      <el-form :model="queryParams" inline>
        <el-form-item label="药品名称">
          <el-input v-model="queryParams.drugName" placeholder="请输入药品名称" clearable />
        </el-form-item>
        <el-form-item label="审核类型">
          <el-select v-model="queryParams.auditType" placeholder="请选择" clearable>
            <el-option v-for="(v, k) in typeMap" :key="k" :label="v" :value="k" />
          </el-select>
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
        <el-table-column prop="drugId" label="药品ID" width="120" />
        <el-table-column prop="drugName" label="药品名称" width="150" />
        <el-table-column prop="specification" label="规格" width="120" />
        <el-table-column prop="manufacturer" label="生产厂家" width="150" />
        <el-table-column prop="auditType" label="审核类型" width="120">
          <template #default="{ row }">{{ typeMap[row.auditType] }}</template>
        </el-table-column>
        <el-table-column prop="submitter" label="提交人" width="100" />
        <el-table-column prop="submitTime" label="提交时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type">{{ statusMap[row.status]?.label }}</el-tag>
          </template>
        </el-table-column>
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

    <el-dialog v-model="auditDialogVisible" title="审核药品" width="500px">
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="药品名称">
          <el-input :value="currentAudit?.drugName" disabled />
        </el-form-item>
        <el-form-item label="审核类型">
          <el-input :value="currentAudit ? typeMap[currentAudit.auditType] : ''" disabled />
        </el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.status">
            <el-radio value="approved">通过</el-radio>
            <el-radio value="rejected">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="auditForm.status === 'rejected'" label="驳回原因">
          <el-input v-model="auditForm.comment" type="textarea" :rows="3" placeholder="请输入驳回原因" />
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="auditForm.comment" type="textarea" :rows="3" placeholder="请输入审核意见" />
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
