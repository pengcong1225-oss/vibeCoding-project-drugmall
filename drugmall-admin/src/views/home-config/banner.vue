<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getBannerList, createBanner, updateBanner, deleteBanner } from '@/api/homeConfig'
import type { BannerConfig, BannerQueryParams } from '@/types/homeConfig'

const loading = ref(false)
const tableData = ref<BannerConfig[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive<BannerQueryParams>({
  keyword: '',
  sectionId: undefined,
  status: undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()
const form = reactive<Partial<BannerConfig>>({
  title: '',
  image: '',
  sectionId: undefined,
  jumpType: 'none',
  jumpUrl: '',
  startTime: '',
  endTime: '',
  sortOrder: 0,
  status: 1
})

const formRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  image: [{ required: true, message: '请上传图片', trigger: 'change' }],
  jumpType: [{ required: true, message: '请选择跳转类型', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择生效时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择失效时间', trigger: 'change' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getBannerList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword,
      sectionId: searchForm.sectionId,
      status: searchForm.status
    })
    tableData.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('获取轮播图列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  getList()
}

const handleReset = () => {
  Object.assign(searchForm, { keyword: '', sectionId: undefined, status: undefined })
  pageNum.value = 1
  getList()
}

const handleAdd = () => {
  dialogTitle.value = '新增轮播图'
  isEdit.value = false
  Object.assign(form, {
    title: '', image: '', sectionId: undefined, jumpType: 'none',
    jumpUrl: '', startTime: '', endTime: '', sortOrder: 0, status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row: BannerConfig) => {
  dialogTitle.value = '编辑轮播图'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    if (isEdit.value && form.id) {
      await updateBanner(form.id, form)
      ElMessage.success('编辑成功')
    } else {
      await createBanner(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('保存失败:', error)
  }
}

const handleDelete = async (row: BannerConfig) => {
  try {
    await ElMessageBox.confirm(`确定要删除轮播图 "${row.title}" 吗？`, '确认删除', {
      confirmButtonText: '确认删除', cancelButtonText: '取消', type: 'warning'
    })
    if (row.id) {
      await deleteBanner(row.id)
      ElMessage.success('删除成功')
      getList()
    }
  } catch {
    // 取消操作
  }
}

const handleStatusChange = async (row: BannerConfig) => {
  const status = row.status === 1 ? 0 : 1
  if (row.id) {
    await updateBanner(row.id, { status })
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
  <div class="banner-config-container">
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="标题">
          <el-input v-model="searchForm.keyword" placeholder="轮播图标题" clearable style="width: 200px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
            <el-option label="已过期" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">轮播图管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增轮播图</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="标题" prop="title" min-width="160" />
        <el-table-column label="图片" width="140">
          <template #default="{ row }">
            <el-image :src="row.image" style="width: 120px; height: 48px" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column label="所属模块" prop="sectionName" width="120" />
        <el-table-column label="跳转类型" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.jumpType === 'none' ? 'info' : 'primary'">
              {{ { url: 'URL', route: '路由', miniapp: '小程序', none: '无' }[row.jumpType as string] || row.jumpType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="生效时间" prop="startTime" width="160" />
        <el-table-column label="失效时间" prop="endTime" width="160" />
        <el-table-column label="排序" prop="sortOrder" width="80" align="center" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="轮播图标题" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="图片" prop="image">
          <el-upload action="#" :show-file-list="false">
            <el-button size="small">上传图片</el-button>
            <template #tip><div class="el-upload__tip">PNG/JPG, 750x300, < 2MB</div></template>
          </el-upload>
        </el-form-item>
        <el-form-item label="所属模块" prop="sectionId">
          <el-select v-model="form.sectionId" placeholder="选择轮播图模块" style="width: 100%">
            <el-option label="模块1" :value="1" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="跳转类型" prop="jumpType">
              <el-select v-model="form.jumpType" style="width: 100%">
                <el-option label="URL" value="url" />
                <el-option label="路由" value="route" />
                <el-option label="小程序" value="miniapp" />
                <el-option label="无" value="none" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="跳转地址">
              <el-input v-model="form.jumpUrl" placeholder="跳转类型非'无'时填写" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生效时间" prop="startTime">
              <el-date-picker v-model="form.startTime" type="datetime" placeholder="开始时间" style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="失效时间" prop="endTime">
              <el-date-picker v-model="form.endTime" type="datetime" placeholder="结束时间" style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="form.sortOrder" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
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
.banner-config-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card .card-title { font-size: 16px; font-weight: 600; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
:deep(.el-upload__tip) { font-size: 12px; color: #909399; margin-top: 4px; }
</style>
