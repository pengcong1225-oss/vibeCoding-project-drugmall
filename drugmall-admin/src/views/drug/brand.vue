<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import type { Brand } from '@/types/product'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: undefined as number | undefined
})

// 表格数据
const loading = ref(false)
const brandList = ref<Brand[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()
const submitLoading = ref(false)

// 表单数据
const formData = reactive<Partial<Brand>>({
  id: undefined,
  brandName: '',
  brandLogo: '',
  brandDesc: '',
  sortOrder: 0,
  status: 1
})

// 表单校验规则
const formRules = {
  brandName: [
    { required: true, message: '请输入品牌名称', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在1-50个字符', trigger: 'blur' }
  ],
  brandLogo: [
    { required: true, message: '请上传品牌LOGO', trigger: 'change' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序号', trigger: 'blur' }
  ]
}

// 模拟品牌数据
const mockBrandList: Brand[] = [
  { id: 1, brandName: '同仁堂', brandLogo: 'https://via.placeholder.com/100', brandDesc: '中华老字号，创建于1669年', sortOrder: 1, status: 1, createTime: '2024-01-01' },
  { id: 2, brandName: '云南白药', brandLogo: 'https://via.placeholder.com/100', brandDesc: '国家保密配方', sortOrder: 2, status: 1, createTime: '2024-01-02' },
  { id: 3, brandName: '999三九', brandLogo: 'https://via.placeholder.com/100', brandDesc: '专注健康30年', sortOrder: 3, status: 1, createTime: '2024-01-03' },
  { id: 4, brandName: '东阿阿胶', brandLogo: 'https://via.placeholder.com/100', brandDesc: '滋补国宝', sortOrder: 4, status: 0, createTime: '2024-01-04' },
  { id: 5, brandName: '汤臣倍健', brandLogo: 'https://via.placeholder.com/100', brandDesc: '营养补充剂领导品牌', sortOrder: 5, status: 1, createTime: '2024-01-05' }
]

// 获取列表
const getList = () => {
  loading.value = true
  setTimeout(() => {
    let result = [...mockBrandList]
    
    // 搜索过滤
    if (searchForm.keyword) {
      result = result.filter(item => 
        item.brandName.includes(searchForm.keyword) ||
        (item.brandDesc && item.brandDesc.includes(searchForm.keyword))
      )
    }
    if (searchForm.status !== undefined) {
      result = result.filter(item => item.status === searchForm.status)
    }
    
    total.value = result.length
    
    // 分页
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    brandList.value = result.slice(start, end)
    
    loading.value = false
  }, 300)
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = undefined
  handleSearch()
}

// 分页
const handleSizeChange = (val: number) => {
  pageSize.value = val
  getList()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  getList()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增品牌'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: Brand) => {
  isEdit.value = true
  dialogTitle.value = '编辑品牌'
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = (row: Brand) => {
  ElMessageBox.confirm('确定删除该品牌吗？删除后无法恢复', '提示', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    getList()
  })
}

// 状态切换
const handleStatusChange = (row: Brand) => {
  const statusText = row.status === 1 ? '启用' : '禁用'
  ElMessage.success(`${statusText}成功`)
}

// 提交
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  setTimeout(() => {
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    getList()
    submitLoading.value = false
  }, 500)
}

// 重置表单
const resetForm = () => {
  formData.id = undefined
  formData.brandName = ''
  formData.brandLogo = ''
  formData.brandDesc = ''
  formData.sortOrder = 0
  formData.status = 1
  formRef.value?.resetFields()
}

// 图片上传成功
const handleUploadSuccess = (url: string) => {
  formData.brandLogo = url
  formRef.value?.validateField('brandLogo')
}

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="brand-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="品牌名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入品牌名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
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

    <!-- 表格区域 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>品牌列表</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增品牌</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="brandList" border stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="品牌LOGO" width="120" align="center">
          <template #default="{ row }">
            <el-image 
              :src="row.brandLogo" 
              :preview-src-list="[row.brandLogo]"
              fit="contain"
              style="width: 80px; height: 50px"
            />
          </template>
        </el-table-column>
        <el-table-column prop="brandName" label="品牌名称" min-width="150" />
        <el-table-column prop="brandDesc" label="品牌描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="品牌名称" prop="brandName">
          <el-input v-model="formData.brandName" placeholder="请输入品牌名称" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="品牌LOGO" prop="brandLogo">
          <el-upload
            class="brand-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="(res: any) => handleUploadSuccess(res.url)"
          >
            <img v-if="formData.brandLogo" :src="formData.brandLogo" class="brand-image" />
            <el-icon v-else class="uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">建议尺寸：200x200px，支持jpg、png格式</div>
        </el-form-item>
        <el-form-item label="品牌描述">
          <el-input v-model="formData.brandDesc" type="textarea" :rows="3" placeholder="请输入品牌描述" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
          <span class="form-tip">数值越小排序越靠前</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.brand-container {
  padding: 20px;

  .search-card {
    margin-bottom: 20px;
  }

  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }

  .brand-uploader {
    :deep(.el-upload) {
      border: 1px dashed var(--el-border-color);
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);

      &:hover {
        border-color: var(--el-color-primary);
      }
    }

    .uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 120px;
      height: 120px;
      text-align: center;
      line-height: 120px;
    }

    .brand-image {
      width: 120px;
      height: 120px;
      display: block;
      object-fit: contain;
    }
  }

  .upload-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 5px;
  }

  .form-tip {
    margin-left: 10px;
    color: #909399;
    font-size: 12px;
  }
}
</style>