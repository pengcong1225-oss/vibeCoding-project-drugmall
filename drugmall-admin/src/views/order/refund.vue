<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Check, Close, View } from '@element-plus/icons-vue'
import { getRefundList, auditRefund } from '@/api/order'
import type { Refund } from '@/types/order'

const loading = ref(false)
const tableData = ref<Refund[]>([])
const total = ref(0)
const auditDialogVisible = ref(false)
const currentRefund = ref<Refund | null>(null)
const auditForm = reactive({
  status: 'approved' as 'approved' | 'rejected',
  reason: ''
})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  refundNo: '',
  orderNo: '',
  status: '',
  type: '',
  startDate: '',
  endDate: ''
})

const statusMap: Record<string, { label: string, type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  pending: { label: '待审核', type: 'warning' },
  approved: { label: '已通过', type: 'success' },
  rejected: { label: '已驳回', type: 'danger' },
  processing: { label: '处理中', type: 'primary' },
  completed: { label: '已完成', type: 'info' }
}

const typeMap: Record<string, string> = {
  refund_only: '仅退款',
  return_and_refund: '退货退款'
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getRefundList(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    ElMessage.error('获取退款列表失败')
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
    refundNo: '',
    orderNo: '',
    status: '',
    type: '',
    startDate: '',
    endDate: ''
  })
  fetchData()
}

function handleAudit(row: Refund) {
  currentRefund.value = row
  auditForm.status = 'approved'
  auditForm.reason = ''
  auditDialogVisible.value = true
}

async function submitAudit() {
  if (!currentRefund.value) return
  if (auditForm.status === 'rejected' && !auditForm.reason) {
    ElMessage.warning('请填写驳回原因')
    return
  }
  try {
    await auditRefund(currentRefund.value.id, auditForm)
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
  <div class="refund-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>退款管理</span>
        </div>
      </template>

      <el-form :model="queryParams" inline>
        <el-form-item label="退款单号">
          <el-input v-model="queryParams.refundNo" placeholder="请输入退款单号" clearable />
        </el-form-item>
        <el-form-item label="订单号">
          <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option v-for="(v, k) in statusMap" :key="k" :label="v.label" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.type" placeholder="请选择" clearable>
            <el-option v-for="(v, k) in typeMap" :key="k" :label="v" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="refundNo" label="退款单号" width="180" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="amount" label="退款金额" width="120">
          <template #default="{ row }">¥{{ row.amount.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">{{ typeMap[row.type] }}</template>
        </el-table-column>
        <el-table-column prop="reason" label="退款原因" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type">{{ statusMap[row.status]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'pending'" type="primary" link :icon="Check" @click="handleAudit(row)">审核</el-button>
            <el-button type="info" link :icon="View">详情</el-button>
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

    <el-dialog v-model="auditDialogVisible" title="审核退款" width="500px">
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="退款单号">
          <el-input :value="currentRefund?.refundNo" disabled />
        </el-form-item>
        <el-form-item label="退款金额">
          <el-input :value="'¥' + (currentRefund?.amount ?? 0).toFixed(2)" disabled />
        </el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.status">
            <el-radio value="approved">通过</el-radio>
            <el-radio value="rejected">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="auditForm.status === 'rejected'" label="驳回原因">
          <el-input v-model="auditForm.reason" type="textarea" :rows="3" placeholder="请输入驳回原因" />
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
