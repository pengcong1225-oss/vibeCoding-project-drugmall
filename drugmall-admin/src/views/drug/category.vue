<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Check, Close } from '@element-plus/icons-vue'
import type { Category } from '@/types/product'
import { getCategoryList, createCategory, updateCategory, deleteCategory } from '@/api/product'

// 分类数据
const loading = ref(false)
const categoryData = ref<Category[]>([])

// 弹窗控制
const dialogVisible = ref(false)
const isEdit = ref(false)
const formLoading = ref(false)

// 表单数据
const categoryForm = reactive<Partial<Category>>({
  id: '',
  name: '',
  parentId: '0',
  level: 1,
  sortOrder: 0,
  icon: '',
  status: 1
})

// 表单校验规则
const formRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  sortOrder: [{ required: true, message: '请输入排序', trigger: 'blur' }]
}

const formRef = ref()

// 获取分类列表
const getList = async () => {
  loading.value = true
  try {
    categoryData.value = await getCategoryList()
  } catch (error) {
    console.error('获取分类列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 打开新增弹窗
const handleAdd = (parent?: Category) => {
  isEdit.value = false
  Object.assign(categoryForm, {
    id: '',
    name: '',
    parentId: parent ? parent.id : '0',
    level: parent ? parent.level + 1 : 1,
    sortOrder: 0,
    icon: '',
    status: 1
  })
  dialogVisible.value = true
}

// 打开编辑弹窗
const handleEdit = (row: Category) => {
  isEdit.value = true
  Object.assign(categoryForm, row)
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  const valid = await formRef.value?.validate()
  if (!valid) return
  
  formLoading.value = true
  try {
    if (isEdit.value) {
      await updateCategory(categoryForm.id as string, categoryForm)
    } else {
      await createCategory(categoryForm)
    }
    ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交分类失败:', error)
  } finally {
    formLoading.value = false
  }
}

// 删除分类
const handleDelete = async (row: Category) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除分类 "${row.name}" 吗？其子分类也将被删除！`,
      '确认删除',
      {
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch {
    // 取消操作
  }
}

// 修改状态
const handleStatusChange = async (row: Category) => {
  try {
    await ElMessageBox.confirm(
      `确定要${row.status === 1 ? '禁用' : '启用'}该分类吗？`,
      '提示',
      { type: 'warning' }
    )
    
    const newStatus = row.status === 1 ? 0 : 1
    await updateCategory(row.id, { status: newStatus })
    row.status = newStatus
    ElMessage.success('操作成功')
  } catch {
    // 取消操作
  }
}

// 获取父级分类名称
const getParentName = (parentId: string) => {
  if (parentId === '0') return '无（一级分类）'
  const findName = (list: Category[]): string => {
    for (const item of list) {
      if (item.id === parentId) return item.name
      if (item.children) {
        const found = findName(item.children)
        if (found) return found
      }
    }
    return ''
  }
  return findName(categoryData.value)
}

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="category-container">
    <!-- 操作区域 -->
    <el-card class="action-card" shadow="never">
      <div class="card-header">
        <span class="card-title">分类管理</span>
        <el-button type="primary" :icon="Plus" @click="handleAdd()">新增一级分类</el-button>
      </div>
    </el-card>

    <!-- 分类树表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        :data="categoryData"
        v-loading="loading"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        stripe
        border
      >
        <el-table-column prop="name" label="分类名称" min-width="200" />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Plus" @click="handleAdd(row)" v-if="row.level < 3">添加子分类</el-button>
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button
              link
              :type="row.status === 1 ? 'warning' : 'success'"
              @click="handleStatusChange(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑分类' : '新增分类'"
      width="500px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="categoryForm" :rules="formRules" label-width="100px">
        <el-form-item label="父级分类">
          <el-input :value="getParentName(categoryForm.parentId as string)" disabled />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" maxlength="20" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="categoryForm.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="categoryForm.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="categoryForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.category-container {
  padding: 20px;
}

.action-card {
  margin-bottom: 20px;

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

.table-card {
  margin-bottom: 20px;
}
</style>
