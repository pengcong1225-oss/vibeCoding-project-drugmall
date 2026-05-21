<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { getTabList, createTab, updateTab, deleteTab, updateTabSort } from '@/api/homeConfig'
import type { TabConfig, TabQueryParams } from '@/types/homeConfig'

const loading = ref(false)
const tableData = ref<TabConfig[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

// 搜索表单
const searchForm = reactive<TabQueryParams>({
  keyword: '',
  status: undefined
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()
const form = reactive<Partial<TabConfig>>({
  tabId: '',
  name: '',
  icon: '',
  activeIcon: '',
  primaryColor: '#409EFF',
  gradient: '',
  bgColor: '#F5F7FA',
  headerBgImage: '',
  sectionIds: [],
  sortOrder: 0,
  status: 1
})

const formRules = {
  tabId: [{ required: true, message: '请输入Tab ID', trigger: 'blur' }],
  name: [{ required: true, message: '请输入Tab名称', trigger: 'blur' }],
  icon: [{ required: true, message: '请上传图标', trigger: 'change' }],
  primaryColor: [{ required: true, message: '请选择主色调', trigger: 'change' }],
  bgColor: [{ required: true, message: '请选择背景色', trigger: 'change' }]
}

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getTabList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword,
      status: searchForm.status
    })
    tableData.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('获取Tab列表失败:', error)
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
  Object.assign(searchForm, { keyword: '', status: undefined })
  pageNum.value = 1
  getList()
}

// 打开新增对话框
const handleAdd = () => {
  dialogTitle.value = '新增Tab'
  isEdit.value = false
  Object.assign(form, {
    tabId: '', name: '', icon: '', activeIcon: '',
    primaryColor: '#409EFF', gradient: '', bgColor: '#F5F7FA',
    headerBgImage: '', sectionIds: [], sortOrder: 0, status: 1
  })
  dialogVisible.value = true
}

// 打开编辑对话框
const handleEdit = (row: TabConfig) => {
  dialogTitle.value = '编辑Tab'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    if (isEdit.value && form.id) {
      await updateTab(form.id, form)
      ElMessage.success('编辑成功')
    } else {
      await createTab(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('保存失败:', error)
  }
}

// 删除
const handleDelete = async (row: TabConfig) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除Tab "${row.name}" 吗？删除后关联模块不会被删除`,
      '确认删除',
      { confirmButtonText: '确认删除', cancelButtonText: '取消', type: 'warning' }
    )
    if (row.id) {
      await deleteTab(row.id)
      ElMessage.success('删除成功')
      getList()
    }
  } catch {
    // 取消操作
  }
}

// 上移
const handleMoveUp = async (row: TabConfig, index: number) => {
  if (index === 0) return
  const ids = tableData.value.map(item => item.id!)
  const temp = ids[index]
  ids[index] = ids[index - 1]
  ids[index - 1] = temp
  await updateTabSort({ ids })
  ElMessage.success('排序成功')
  getList()
}

// 下移
const handleMoveDown = async (row: TabConfig, index: number) => {
  if (index === tableData.value.length - 1) return
  const ids = tableData.value.map(item => item.id!)
  const temp = ids[index]
  ids[index] = ids[index + 1]
  ids[index + 1] = temp
  await updateTabSort({ ids })
  ElMessage.success('排序成功')
  getList()
}

// 状态切换
const handleStatusChange = async (row: TabConfig) => {
  const status = row.status === 1 ? 0 : 1
  if (row.id) {
    await updateTab(row.id, { status })
    row.status = status
    ElMessage.success(status === 1 ? '已启用' : '已禁用')
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
  <div class="tab-config-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="Tab名称/ID" clearable style="width: 200px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
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
          <span class="card-title">Tab管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增Tab</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="Tab ID" prop="tabId" width="140" />
        <el-table-column label="Tab名称" prop="name" width="120" />
        <el-table-column label="图标" width="80" align="center">
          <template #default="{ row }">
            <el-image :src="row.icon" style="width: 32px; height: 32px" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column label="主色调" width="100" align="center">
          <template #default="{ row }">
            <div style="display: flex; align-items: center; gap: 8px">
              <div :style="{ width: '16px', height: '16px', borderRadius: '2px', backgroundColor: row.primaryColor }" />
              <span style="font-size: 12px; color: #606266">{{ row.primaryColor }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="关联模块数" prop="sectionCount" width="100" align="center" />
        <el-table-column label="排序" prop="sortOrder" width="80" align="center" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row, $index }">
            <el-button link type="primary" :icon="ArrowUp" @click="handleMoveUp(row, $index)" :disabled="$index === 0">上移</el-button>
            <el-button link type="primary" :icon="ArrowDown" @click="handleMoveDown(row, $index)" :disabled="$index === tableData.length - 1">下移</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Tab ID" prop="tabId">
              <el-input v-model="form.tabId" placeholder="如: recommend" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Tab名称" prop="name">
              <el-input v-model="form.name" placeholder="最多10字" maxlength="10" show-word-limit />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="图标" prop="icon">
              <el-upload action="#" :show-file-list="false">
                <el-button size="small">上传图标</el-button>
                <template #tip>
                  <div class="el-upload__tip">PNG/SVG, 120x120</div>
                </template>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="激活图标" prop="activeIcon">
              <el-upload action="#" :show-file-list="false">
                <el-button size="small">上传激活图标</el-button>
                <template #tip>
                  <div class="el-upload__tip">可选, 120x120</div>
                </template>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="主色调" prop="primaryColor">
              <el-color-picker v-model="form.primaryColor" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="背景色" prop="bgColor">
              <el-color-picker v-model="form.bgColor" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="渐变配置">
          <el-input v-model="form.gradient" placeholder="CSS渐变语法, 如: linear-gradient(...)" />
        </el-form-item>
        <el-form-item label="头部背景图">
          <el-upload action="#" :show-file-list="false">
            <el-button size="small">上传图片</el-button>
            <template #tip>
              <div class="el-upload__tip">PNG/JPG, 750x300, 可选</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" controls-position="right" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.tab-config-container {
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

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

:deep(.el-upload__tip) {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
