<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, Edit, Delete, User } from '@element-plus/icons-vue'
import type { UserInfo } from '@/types/user'
import { getUserList, deleteUser, updateUserStatus } from '@/api/user'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: undefined as number | undefined,
  startTime: '',
  endTime: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref<UserInfo[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword,
      status: searchForm.status
    })
    tableData.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pageNum.value = 1
  getList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    status: undefined,
    startTime: '',
    endTime: ''
  })
  pageNum.value = 1
  getList()
}

// 分页
const handleSizeChange = (size: number) => {
  pageSize.value = size
  getList()
}

const handleCurrentChange = (page: number) => {
  pageNum.value = page
  getList()
}

// 查看详情
const handleView = (row: UserInfo) => {
  router.push(`/user/detail/${row.id}`)
}

// 编辑用户
const handleEdit = (row: UserInfo) => {
  router.push(`/user/detail/${row.id}`)
}

// 删除用户
const handleDelete = async (row: UserInfo) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${row.nickname}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: 'warning' as const
      }
    )
    
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch {
    // 取消操作
  }
}

// 修改用户状态
const handleStatusChange = async (row: UserInfo) => {
  const action = row.status === 1 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(
      `确定要${action}该用户吗？`,
      '提示',
      { type: 'warning' }
    )
    
    const newStatus = row.status === 1 ? 0 : 1
    await updateUserStatus(row.id, newStatus)
    row.status = newStatus
    ElMessage.success(`${action}成功`)
  } catch {
    // 取消操作
  }
}

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="user-list-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="用户名/昵称/手机号"
            clearable
            @keyup.enter="handleSearch"
            style="width: 220px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="正常" :value="1" />
            <el-option label="已禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="注册时间">
          <el-date-picker
            v-model="searchForm.startTime"
            type="date"
            placeholder="开始日期"
            value-format="YYYY-MM-DD"
            style="width: 140px"
          />
          <span style="margin: 0 8px">至</span>
          <el-date-picker
            v-model="searchForm.endTime"
            type="date"
            placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 140px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">用户列表</span>
          <el-button type="primary">+ 新增用户</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="用户信息" min-width="200">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="40" :src="row.avatar" :icon="User" />
              <div class="user-meta">
                <div class="nickname">{{ row.nickname }}</div>
                <div class="username">{{ row.username }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
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
.user-list-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .card-title {
    font-size: 16px;
    font-weight: 600;
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;

  .user-meta {
    .nickname {
      font-weight: 500;
      color: #303133;
      margin-bottom: 4px;
    }

    .username {
      font-size: 12px;
      color: #909399;
    }
  }
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
