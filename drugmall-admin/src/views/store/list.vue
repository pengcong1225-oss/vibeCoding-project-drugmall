<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, Edit, Delete, CopyDocument } from '@element-plus/icons-vue'
import { getStoreList, updateStoreStatus } from '@/api/store'
import type { StoreInfo, StoreQueryParams } from '@/types/store'

const router = useRouter()
const loading = ref(false)
const tableData = ref<StoreInfo[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive<StoreQueryParams>({
  keyword: '',
  city: '',
  licenseStatus: undefined,
  status: undefined
})

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '正常', type: 'success' },
  2: { label: '停业', type: 'danger' },
  3: { label: '禁用', type: 'info' }
}

const licenseStatusMap: Record<number, string> = {
  0: '未认证',
  1: '认证中',
  2: '已认证',
  3: '已过期'
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getStoreList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    tableData.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('获取门店列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  getList()
}

const handleReset = () => {
  Object.assign(searchForm, { keyword: '', city: '', licenseStatus: undefined, status: undefined })
  pageNum.value = 1
  getList()
}

const handleView = (row: StoreInfo) => {
  router.push(`/store/detail/${row.id}`)
}

const handleEdit = (row: StoreInfo) => {
  router.push(`/store/detail/${row.id}`)
}

const handleStatusChange = async (row: StoreInfo) => {
  const action = row.status === 1 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}该门店吗？`, '提示', { type: 'warning' })
    const newStatus = row.status === 1 ? 3 : 1
    await updateStoreStatus(row.id, newStatus)
    row.status = newStatus
    ElMessage.success(`${action}成功`)
  } catch {
    // 取消操作
  }
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  getList()
}

const handleCurrentChange = (page: number) => {
  pageNum.value = page
  getList()
}

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="store-list-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="门店名称/联系人" clearable style="width: 180px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="searchForm.city" placeholder="城市名称" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="认证状态">
          <el-select v-model="searchForm.licenseStatus" placeholder="全部" clearable style="width: 120px">
            <el-option label="未认证" :value="0" />
            <el-option label="认证中" :value="1" />
            <el-option label="已认证" :value="2" />
            <el-option label="已过期" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="门店状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待审核" :value="0" />
            <el-option label="正常" :value="1" />
            <el-option label="停业" :value="2" />
            <el-option label="禁用" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="16" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">86</div>
          <div class="stat-label">门店总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #67C23A">72</div>
          <div class="stat-label">正常营业</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #E6A23C">8</div>
          <div class="stat-label">待审核</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #F56C6C">6</div>
          <div class="stat-label">已过期</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">门店列表</span>
          <el-button type="primary" :icon="Refresh">批量导出</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="id" label="门店ID" width="90" />
        <el-table-column label="门店名称" prop="name" min-width="160" show-overflow-tooltip />
        <el-table-column label="联系人" prop="contact" width="100" />
        <el-table-column label="联系电话" prop="phone" width="130" />
        <el-table-column label="地址" prop="address" min-width="200" show-overflow-tooltip />
        <el-table-column label="许可证号" prop="licenseNo" width="140" />
        <el-table-column label="认证状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.licenseStatus === 2 ? 'success' : row.licenseStatus === 0 ? 'info' : 'warning'" size="small">
              {{ licenseStatusMap[row.licenseStatus] || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="门店状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type as any" size="small">
              {{ statusMap[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinTime" label="入驻时间" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handleView(row)">详情</el-button>
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 1" link type="warning" @click="handleStatusChange(row)">禁用</el-button>
            <el-button v-else-if="row.status === 3" link type="success" @click="handleStatusChange(row)">启用</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.store-list-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.stat-card { text-align: center; }
.stat-value { font-size: 28px; font-weight: 700; color: #303133; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card .card-title { font-size: 16px; font-weight: 600; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
