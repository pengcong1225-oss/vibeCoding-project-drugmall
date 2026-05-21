<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Edit } from '@element-plus/icons-vue'
import { getAbnormalOrderList, handleAbnormalOrder } from '@/api/order'
import type { AbnormalOrder } from '@/types/order'

const loading = ref(false)
const tableData = ref<AbnormalOrder[]>([])
const total = ref(0)
const handleDialogVisible = ref(false)
const currentOrder = ref<AbnormalOrder | null>(null)
const handleForm = reactive({
  result: '',
  remark: ''
})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  orderNo: '',
  abnormalType: '',
  status: '',
  startDate: '',
  endDate: ''
})

const typeMap: Record<string, string> = {
  timeout_pay: '超时未支付',
  timeout_ship: '超时未发货',
  cancel: '订单取消',
  fraud: '疑似欺诈',
  other: '其他异常'
}

const statusMap: Record<string, { label: string, type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  pending: { label: '待处理', type: 'warning' },
  processing: { label: '处理中', type: 'primary' },
  resolved: { label: '已解决', type: 'success' }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getAbnormalOrderList(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    ElMessage.error('获取异常订单失败')
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
    orderNo: '',
    abnormalType: '',
    status: '',
    startDate: '',
    endDate: ''
  })
  fetchData()
}

function handleProcess(row: AbnormalOrder) {
  currentOrder.value = row
  handleForm.result = ''
  handleForm.remark = ''
  handleDialogVisible.value = true
}

async function submitHandle() {
  if (!currentOrder.value || !handleForm.result) {
    ElMessage.warning('请填写处理结果')
    return
  }
  try {
    await handleAbnormalOrder(currentOrder.value.id, handleForm)
    ElMessage.success('处理成功')
    handleDialogVisible.value = false
    fetchData()
  } catch {
    ElMessage.error('处理失败')
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
  <div class="abnormal-order">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>异常订单</span>
        </div>
      </template>

      <el-form :model="queryParams" inline>
        <el-form-item label="订单号">
          <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="异常类型">
          <el-select v-model="queryParams.abnormalType" placeholder="请选择" clearable>
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
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="amount" label="订单金额" width="120">
          <template #default="{ row }">¥{{ row.amount.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="abnormalType" label="异常类型" width="120">
          <template #default="{ row }">{{ typeMap[row.abnormalType] }}</template>
        </el-table-column>
        <el-table-column prop="description" label="异常描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type">{{ statusMap[row.status]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handler" label="处理人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status !== 'resolved'" type="primary" link :icon="Edit" @click="handleProcess(row)">处理</el-button>
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

    <el-dialog v-model="handleDialogVisible" title="处理异常订单" width="500px">
      <el-form :model="handleForm" label-width="80px">
        <el-form-item label="订单号">
          <el-input :value="currentOrder?.orderNo" disabled />
        </el-form-item>
        <el-form-item label="异常类型">
          <el-input :value="currentOrder ? typeMap[currentOrder.abnormalType] : ''" disabled />
        </el-form-item>
        <el-form-item label="异常描述">
          <el-input :value="currentOrder?.description" type="textarea" :rows="2" disabled />
        </el-form-item>
        <el-form-item label="处理结果">
          <el-input v-model="handleForm.result" type="textarea" :rows="3" placeholder="请输入处理结果" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="handleForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确定</el-button>
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
