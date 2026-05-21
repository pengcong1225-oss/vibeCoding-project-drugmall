<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Edit } from '@element-plus/icons-vue'
import { getComplaintList, handleComplaint } from '@/api/operation'
import type { Complaint } from '@/types/operation'

const loading = ref(false)
const tableData = ref<Complaint[]>([])
const total = ref(0)
const handleDialogVisible = ref(false)
const currentComplaint = ref<Complaint | null>(null)
const handleForm = reactive({
  result: '',
  status: 'resolved' as 'resolved' | 'rejected'
})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  type: '',
  status: '',
  startDate: '',
  endDate: ''
})

const typeMap: Record<string, string> = {
  service: '服务态度',
  quality: '质量问题',
  delivery: '配送问题',
  price: '价格问题',
  other: '其他'
}

const targetTypeMap: Record<string, string> = {
  doctor: '医生',
  store: '门店',
  product: '商品',
  order: '订单'
}

const statusMap: Record<string, { label: string, type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  pending: { label: '待处理', type: 'warning' },
  processing: { label: '处理中', type: 'primary' },
  resolved: { label: '已解决', type: 'success' },
  closed: { label: '已关闭', type: 'info' }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getComplaintList(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    ElMessage.error('获取投诉列表失败')
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
    type: '',
    status: '',
    startDate: '',
    endDate: ''
  })
  fetchData()
}

function handleProcess(row: Complaint) {
  currentComplaint.value = row
  handleForm.result = ''
  handleForm.status = 'resolved'
  handleDialogVisible.value = true
}

async function submitHandle() {
  if (!currentComplaint.value || !handleForm.result) {
    ElMessage.warning('请填写处理结果')
    return
  }
  try {
    await handleComplaint(currentComplaint.value.id, handleForm)
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
  <div class="complaint-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>投诉管理</span>
        </div>
      </template>

      <el-form :model="queryParams" inline>
        <el-form-item label="投诉类型">
          <el-select v-model="queryParams.type" placeholder="请选择" clearable>
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
        <el-table-column prop="complainantName" label="投诉人" width="120" />
        <el-table-column prop="targetType" label="投诉对象" width="100">
          <template #default="{ row }">{{ targetTypeMap[row.targetType] }}</template>
        </el-table-column>
        <el-table-column prop="targetName" label="对象名称" width="150" />
        <el-table-column prop="type" label="投诉类型" width="120">
          <template #default="{ row }">{{ typeMap[row.type] }}</template>
        </el-table-column>
        <el-table-column prop="content" label="投诉内容" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type">{{ statusMap[row.status]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handler" label="处理人" width="100" />
        <el-table-column prop="createTime" label="投诉时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'pending'" type="primary" link :icon="Edit" @click="handleProcess(row)">处理</el-button>
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

    <el-dialog v-model="handleDialogVisible" title="处理投诉" width="500px">
      <el-form :model="handleForm" label-width="80px">
        <el-form-item label="投诉内容">
          <el-input :value="currentComplaint?.content" type="textarea" :rows="3" disabled />
        </el-form-item>
        <el-form-item label="处理结果">
          <el-radio-group v-model="handleForm.status">
            <el-radio value="resolved">解决</el-radio>
            <el-radio value="rejected">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理说明">
          <el-input v-model="handleForm.result" type="textarea" :rows="4" placeholder="请输入处理说明" />
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
