<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getKingKongList, createKingKong, updateKingKong, deleteKingKong } from '@/api/homeConfig'
import type { KingKongConfig, KingKongQueryParams } from '@/types/homeConfig'

const loading = ref(false)
const tableData = ref<KingKongConfig[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive<KingKongQueryParams>({
  keyword: '',
  sectionId: undefined,
  status: undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()
const form = reactive<Partial<KingKongConfig>>({
  name: '',
  icon: '',
  iconType: 'image',
  sectionId: undefined,
  jumpType: 'none',
  jumpUrl: '',
  badge: '',
  badgeColor: '#F56C6C',
  sortOrder: 0,
  status: 1
})

const formRules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  icon: [{ required: true, message: '请上传图标', trigger: 'change' }],
  iconType: [{ required: true, message: '请选择图标类型', trigger: 'change' }],
  jumpType: [{ required: true, message: '请选择跳转类型', trigger: 'change' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getKingKongList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword,
      sectionId: searchForm.sectionId,
      status: searchForm.status
    })
    tableData.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('获取金刚位列表失败:', error)
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
  dialogTitle.value = '新增金刚位'
  isEdit.value = false
  Object.assign(form, {
    name: '', icon: '', iconType: 'image', sectionId: undefined,
    jumpType: 'none', jumpUrl: '', badge: '', badgeColor: '#F56C6C',
    sortOrder: 0, status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row: KingKongConfig) => {
  dialogTitle.value = '编辑金刚位'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    if (isEdit.value && form.id) {
      await updateKingKong(form.id, form)
      ElMessage.success('编辑成功')
    } else {
      await createKingKong(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('保存失败:', error)
  }
}

const handleDelete = async (row: KingKongConfig) => {
  try {
    await ElMessageBox.confirm(`确定要删除金刚位 "${row.name}" 吗？`, '确认删除', {
      confirmButtonText: '确认删除', cancelButtonText: '取消', type: 'warning'
    })
    if (row.id) {
      await deleteKingKong(row.id)
      ElMessage.success('删除成功')
      getList()
    }
  } catch {
    // 取消操作
  }
}

const handleStatusChange = async (row: KingKongConfig) => {
  const status = row.status === 1 ? 0 : 1
  if (row.id) {
    await updateKingKong(row.id, { status })
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
  <div class="kingkong-config-container">
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="名称">
          <el-input v-model="searchForm.keyword" placeholder="金刚位名称" clearable style="width: 200px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 120px">
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

    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">金刚位管理</span>
          <div style="display: flex; gap: 8px">
            <el-button :icon="Plus" @click="handleAdd">新增金刚位</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="名称" prop="name" width="120" />
        <el-table-column label="图标" width="80" align="center">
          <template #default="{ row }">
            <el-image :src="row.icon" style="width: 40px; height: 40px" fit="cover" />
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
        <el-table-column label="角标" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.badge" :color="row.badgeColor" style="color: #fff" size="small">
              {{ row.badge }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="sortOrder" width="80" align="center" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
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
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="入口名称" maxlength="10" show-word-limit />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="图标" prop="icon">
              <el-upload action="#" :show-file-list="false">
                <el-button size="small">上传图标</el-button>
                <template #tip><div class="el-upload__tip">PNG/SVG, 120x120, < 500KB</div></template>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="图标类型" prop="iconType">
              <el-radio-group v-model="form.iconType">
                <el-radio value="image">图片</el-radio>
                <el-radio value="icon">图标</el-radio>
                <el-radio value="emoji">Emoji</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="所属模块" prop="sectionId">
          <el-select v-model="form.sectionId" placeholder="选择金刚位模块" style="width: 100%">
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
            <el-form-item label="角标文字">
              <el-input v-model="form.badge" placeholder="如 NEW/HOT" maxlength="6" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角标颜色">
              <el-color-picker v-model="form.badgeColor" />
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
.kingkong-config-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card .card-title { font-size: 16px; font-weight: 600; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
:deep(.el-upload__tip) { font-size: 12px; color: #909399; margin-top: 4px; }
</style>
