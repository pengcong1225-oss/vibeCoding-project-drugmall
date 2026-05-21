<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, CopyDocument } from '@element-plus/icons-vue'
import { getSectionList, createSection, updateSection, deleteSection, copySection } from '@/api/homeConfig'
import type { SectionConfig, SectionQueryParams } from '@/types/homeConfig'

const loading = ref(false)
const tableData = ref<SectionConfig[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive<SectionQueryParams>({
  keyword: '',
  sectionType: undefined,
  status: undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()
const form = reactive<Partial<SectionConfig>>({
  name: '',
  subtitle: '',
  sectionType: 'banner',
  layout: 'vertical',
  bgColor: '#FFFFFF',
  borderRadius: 8,
  marginTop: 0,
  marginBottom: 12,
  tabIds: [],
  sortOrder: 0,
  status: 1
})

const formRules = {
  name: [{ required: true, message: '请输入模块名称', trigger: 'blur' }],
  sectionType: [{ required: true, message: '请选择模块类型', trigger: 'change' }],
  layout: [{ required: true, message: '请选择布局方式', trigger: 'change' }]
}

const sectionTypeOptions = [
  { label: '轮播图', value: 'banner' },
  { label: '金刚位', value: 'kingkong' },
  { label: '广告位', value: 'ad_slot' },
  { label: '专题区域', value: 'topic' },
  { label: '商品流', value: 'product_flow' },
  { label: '服务网格', value: 'service_grid' },
  { label: '公告栏', value: 'notice' },
  { label: '领券中心', value: 'coupon' },
  { label: '限时抢购', value: 'flash_sale' },
  { label: '新品首发', value: 'new_product' }
]

const layoutOptions = [
  { label: '垂直', value: 'vertical' },
  { label: '水平', value: 'horizontal' },
  { label: '网格', value: 'grid' },
  { label: '瀑布流', value: 'waterfall' }
]

const statusMap: Record<number, { label: string; type: 'success' | 'info' | 'danger' | 'warning' }> = {
  0: { label: '禁用', type: 'info' },
  1: { label: '启用', type: 'success' }
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getSectionList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword,
      sectionType: searchForm.sectionType,
      status: searchForm.status
    })
    tableData.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('获取模块列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  getList()
}

const handleReset = () => {
  Object.assign(searchForm, { keyword: '', sectionType: undefined, status: undefined })
  pageNum.value = 1
  getList()
}

const handleAdd = () => {
  dialogTitle.value = '新增模块'
  isEdit.value = false
  Object.assign(form, {
    name: '', subtitle: '', sectionType: 'banner', layout: 'vertical',
    bgColor: '#FFFFFF', borderRadius: 8, marginTop: 0, marginBottom: 12,
    tabIds: [], sortOrder: 0, status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row: SectionConfig) => {
  dialogTitle.value = '编辑模块'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    if (isEdit.value && form.id) {
      await updateSection(form.id, form)
      ElMessage.success('编辑成功')
    } else {
      await createSection(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('保存失败:', error)
  }
}

const handleDelete = async (row: SectionConfig) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除模块 "${row.name}" 吗？`,
      '确认删除',
      { confirmButtonText: '确认删除', cancelButtonText: '取消', type: 'warning' }
    )
    if (row.id) {
      await deleteSection(row.id)
      ElMessage.success('删除成功')
      getList()
    }
  } catch {
    // 取消操作
  }
}

const handleCopy = async (row: SectionConfig) => {
  if (row.id) {
    await copySection(row.id)
    ElMessage.success('复制成功')
    getList()
  }
}

const handleStatusChange = async (row: SectionConfig) => {
  const status = row.status === 1 ? 0 : 1
  if (row.id) {
    await updateSection(row.id, { status })
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
  <div class="section-config-container">
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="模块名称" clearable style="width: 200px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="模块类型">
          <el-select v-model="searchForm.sectionType" placeholder="全部类型" clearable style="width: 140px">
            <el-option v-for="opt in sectionTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
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
          <span class="card-title">模块管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增模块</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="模块名称" prop="name" min-width="160" />
        <el-table-column label="模块类型" width="120">
          <template #default="{ row }">
            {{ sectionTypeOptions.find(o => o.value === row.sectionType)?.label }}
          </template>
        </el-table-column>
        <el-table-column label="布局方式" width="100">
          <template #default="{ row }">
            {{ layoutOptions.find(o => o.value === row.layout)?.label }}
          </template>
        </el-table-column>
        <el-table-column label="关联Tab" width="120">
          <template #default="{ row }">
            <el-tag v-for="tabId in row.tabIds?.slice(0, 2)" :key="tabId" size="small" style="margin-right: 4px">
              Tab {{ tabId }}
            </el-tag>
            <span v-if="(row.tabIds?.length || 0) > 2" style="font-size: 12px; color: #909399">+{{ row.tabIds!.length - 2 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="sortOrder" width="80" align="center" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" :icon="CopyDocument" @click="handleCopy(row)">复制</el-button>
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
        <el-form-item label="模块名称" prop="name">
          <el-input v-model="form.name" placeholder="模块标题" />
        </el-form-item>
        <el-form-item label="模块副标题">
          <el-input v-model="form.subtitle" placeholder="补充说明（可选）" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="模块类型" prop="sectionType">
              <el-select v-model="form.sectionType" style="width: 100%">
                <el-option v-for="opt in sectionTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="布局方式" prop="layout">
              <el-select v-model="form.layout" style="width: 100%">
                <el-option v-for="opt in layoutOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="背景颜色">
              <el-color-picker v-model="form.bgColor" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="圆角大小">
              <el-input-number v-model="form.borderRadius" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="上边距">
              <el-input-number v-model="form.marginTop" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="下边距">
              <el-input-number v-model="form.marginBottom" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序">
              <el-input-number v-model="form.sortOrder" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="关联Tab">
          <el-select v-model="form.tabIds" multiple style="width: 100%">
            <el-option label="Tab 1" :value="1" />
            <el-option label="Tab 2" :value="2" />
            <el-option label="Tab 3" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.section-config-container {
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
</style>
