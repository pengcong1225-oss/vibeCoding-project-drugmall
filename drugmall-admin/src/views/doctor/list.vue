<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Refresh, View } from '@element-plus/icons-vue'
import { getDoctorList } from '@/api/doctor'
import type { DoctorInfo, DoctorQueryParams } from '@/types/doctor'

const router = useRouter()
const loading = ref(false)
const tableData = ref<DoctorInfo[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive<DoctorQueryParams>({
  keyword: '',
  departmentId: undefined,
  title: '',
  status: undefined
})

const departmentOptions = [
  { label: '内科', value: 1 },
  { label: '外科', value: 2 },
  { label: '儿科', value: 3 },
  { label: '妇产科', value: 4 },
  { label: '皮肤科', value: 5 },
  { label: '中医科', value: 6 }
]

const titleOptions = [
  { label: '主任医师', value: '主任医师' },
  { label: '副主任医师', value: '副主任医师' },
  { label: '主治医师', value: '主治医师' },
  { label: '住院医师', value: '住院医师' }
]

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '已通过', type: 'success' },
  2: { label: '已禁用', type: 'info' },
  3: { label: '已注销', type: 'danger' }
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getDoctorList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    tableData.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('获取医生列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  getList()
}

const handleReset = () => {
  Object.assign(searchForm, { keyword: '', departmentId: undefined, title: '', status: undefined })
  pageNum.value = 1
  getList()
}

const handleView = (row: DoctorInfo) => {
  router.push(`/doctor/detail/${row.id}`)
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
  <div class="doctor-list-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="医生姓名">
          <el-input v-model="searchForm.keyword" placeholder="医生姓名/手机号" clearable style="width: 180px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="科室">
          <el-select v-model="searchForm.departmentId" placeholder="全部科室" clearable style="width: 130px">
            <el-option v-for="item in departmentOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="职称">
          <el-select v-model="searchForm.title" placeholder="全部职称" clearable style="width: 140px">
            <el-option v-for="item in titleOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option v-for="(v, k) in statusMap" :key="k" :label="v.label" :value="Number(k)" />
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
          <div class="stat-value">128</div>
          <div class="stat-label">医生总量</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #409EFF">12</div>
          <div class="stat-label">待审核</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #67C23A">98</div>
          <div class="stat-label">已上线</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #E6A23C">4.8</div>
          <div class="stat-label">平均评分</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <span class="card-title">医生列表</span>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="头像" width="80" align="center">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="40" />
          </template>
        </el-table-column>
        <el-table-column label="姓名" prop="name" width="100" />
        <el-table-column label="性别" width="60" align="center">
          <template #default="{ row }">{{ row.gender === 1 ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column label="科室" prop="departmentName" width="100" />
        <el-table-column label="职称" prop="title" width="130" />
        <el-table-column label="医院" prop="hospital" min-width="160" show-overflow-tooltip />
        <el-table-column label="评分" width="100" align="center">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled :max="5" />
          </template>
        </el-table-column>
        <el-table-column label="服务量" prop="serviceCount" width="90" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type as any" size="small">
              {{ statusMap[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handleView(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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
.doctor-list-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.stat-card { text-align: center; }
.stat-value { font-size: 28px; font-weight: 700; color: #303133; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
.table-card .card-title { font-size: 16px; font-weight: 600; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
