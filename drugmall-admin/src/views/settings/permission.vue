<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getRoleList, createRole, updateRole, deleteRole, getPermissionTree } from '@/api/permission'
import type { Role, Permission } from '@/types/permission'

const loading = ref(false)
const tableData = ref<Role[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentRole = ref<Role | null>(null)
const permissionTree = ref<Permission[]>([])
const checkedKeys = ref<string[]>([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  status: ''
})

const form = reactive({
  name: '',
  code: '',
  description: '',
  permissions: [] as string[],
  status: 'active' as 'active' | 'disabled'
})

const statusMap: Record<string, { label: string, type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  active: { label: '启用', type: 'success' },
  disabled: { label: '禁用', type: 'danger' }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getRoleList(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

async function loadPermissionTree() {
  try {
    const res = await getPermissionTree()
    permissionTree.value = res.data
  } catch {
    ElMessage.error('获取权限树失败')
  }
}

function handleSearch() {
  queryParams.pageNum = 1
  fetchData()
}

function handleReset() {
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    name: '',
    status: ''
  })
  fetchData()
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, {
    name: '',
    code: '',
    description: '',
    permissions: [],
    status: 'active'
  })
  checkedKeys.value = []
  dialogVisible.value = true
}

function handleEdit(row: Role) {
  isEdit.value = true
  currentRole.value = row
  Object.assign(form, {
    name: row.name,
    code: row.code,
    description: row.description,
    permissions: row.permissions,
    status: row.status
  })
  checkedKeys.value = row.permissions
  dialogVisible.value = true
}

async function handleDelete(row: Role) {
  try {
    await ElMessageBox.confirm(`确定删除角色"${row.name}"吗？`, '提示', {
      type: 'warning'
    })
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
  }
}

async function submitForm() {
  if (!form.name || !form.code) {
    ElMessage.warning('请填写角色名称和编码')
    return
  }
  try {
    if (isEdit.value && currentRole.value) {
      await updateRole(currentRole.value.id, form)
    } else {
      await createRole(form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchData()
  } catch {
    ElMessage.error('保存失败')
  }
}

function handlePageChange(page: number) {
  queryParams.pageNum = page
  fetchData()
}

function handleSizeChange(size: number) {
  queryParams.pageSize = size
  queryParams.pageNum = 1
  fetchData()
}

onMounted(() => {
  fetchData()
  loadPermissionTree()
})
</script>

<template>
  <div class="permission-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增角色</el-button>
        </div>
      </template>

      <el-form :model="queryParams" inline>
        <el-form-item label="角色名称">
          <el-input v-model="queryParams.name" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option v-for="(v, k) in statusMap" :key="k" :label="v.label" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="name" label="角色名称" width="150" />
        <el-table-column prop="code" label="角色编码" width="150" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="userCount" label="用户数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type">{{ statusMap[row.status]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="角色名称">
          <el-input v-model="form.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="form.code" placeholder="请输入角色编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="active">启用</el-radio>
            <el-radio value="disabled">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="权限配置">
          <el-tree
            :data="permissionTree"
            :props="{ label: 'name', children: 'children' }"
            show-checkbox
            node-key="id"
            v-model="checkedKeys"
            default-expand-all
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
