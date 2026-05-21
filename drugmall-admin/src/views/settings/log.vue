<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Download } from '@element-plus/icons-vue'
import { getOperationLogs, exportOperationLogs } from '@/api/log'
import type { OperationLog } from '@/types/log'

const loading = ref(false)
const exporting = ref(false)
const tableData = ref<OperationLog[]>([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  module: '',
  action: '',
  status: '',
  startDate: '',
  endDate: '',
  ip: ''
})

const statusMap: Record<string, { label: string, type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  success: { label: '成功', type: 'success' },
  failure: { label: '失败', type: 'danger' }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getOperationLogs(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    ElMessage.error('获取日志失败')
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
    username: '',
    module: '',
    action: '',
    status: '',
    startDate: '',
    endDate: '',
    ip: ''
  })
  fetchData()
}

async function handleExport() {
  exporting.value = true
  try {
    const res = await exportOperationLogs(queryParams)
    const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `操作日志_${new Date().toISOString().split('T')[0]}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch {
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
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
  <div class="operation-log">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>操作日志</span>
          <el-button type="primary" :icon="Download" :loading="exporting" @click="handleExport">导出</el-button>
        </div>
      </template>

      <el-form :model="queryParams" inline>
        <el-form-item label="操作人">
          <el-input v-model="queryParams.username" placeholder="请输入操作人" clearable />
        </el-form-item>
        <el-form-item label="模块">
          <el-input v-model="queryParams.module" placeholder="请输入模块" clearable />
        </el-form-item>
        <el-form-item label="操作">
          <el-input v-model="queryParams.action" placeholder="请输入操作" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option v-for="(v, k) in statusMap" :key="k" :label="v.label" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item label="IP地址">
          <el-input v-model="queryParams.ip" placeholder="请输入IP" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="action" label="操作" width="120" />
        <el-table-column prop="method" label="请求方法" width="100" />
        <el-table-column prop="url" label="请求URL" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="duration" label="耗时(ms)" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type" size="small">{{ statusMap[row.status]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="180" />
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
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
