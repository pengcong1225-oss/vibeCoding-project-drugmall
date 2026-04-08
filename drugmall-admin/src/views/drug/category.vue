<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Check, Close } from '@element-plus/icons-vue'
import type { Category } from '@/types/product'

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
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟数据 - 树形结构
    categoryData.value = [
      {
        id: '1',
        name: '药品',
        parentId: '0',
        level: 1,
        sortOrder: 1,
        status: 1,
        createTime: '2024-01-01',
        children: [
          {
            id: '11',
            name: '感冒药',
            parentId: '1',
            level: 2,
            sortOrder: 1,
            status: 1,
            createTime: '2024-01-01',
            children: [
              { id: '111', name: '风寒感冒', parentId: '11', level: 3, sortOrder: 1, status: 1, createTime: '2024-01-01' },
              { id: '112', name: '风热感冒', parentId: '11', level: 3, sortOrder: 2, status: 1, createTime: '2024-01-01' }
            ]
          },
          {
            id: '12',
            name: '消化系统',
            parentId: '1',
            level: 2,
            sortOrder: 2,
            status: 1,
            createTime: '2024-01-01'
          },
          {
            id: '13',
            name: '心脑血管',
            parentId: '1',
            level: 2,
            sortOrder: 3,
            status: 1,
            createTime: '2024-01-01'
          }
        ]
      },
      {
        id: '2',
        name: '保健品',
        parentId: '0',
        level: 1,
        sortOrder: 2,
        status: 1,
        createTime: '2024-01-01',
        children: [
          {
            id: '21',
            name: '维生素',
            parentId: '2',
            level: 2,
            sortOrder: 1,
            status: 1,
            createTime: '2024-01-01'
          },
          {
            id: '22',
            name: '蛋白粉',
            parentId: '2',
            level: 2,
            sortOrder: 2,
            status: 1,
            createTime: '2024-01-01'
          }
        ]
      },
      {
        id: '3',
        name: '医疗器械',
        parentId: '0',
        level: 1,
        sortOrder: 3,
        status: 1,
        createTime: '2024-01-01'
      }
    ]
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
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
    dialogVisible.value = false
    getList()
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
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 300))
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
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 300))
    row.status = row.status === 1 ? 0 : 1
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
      if (item