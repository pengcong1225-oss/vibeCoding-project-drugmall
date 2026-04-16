<template>
  <div class="address-list-page">
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="title">收货地址</span>
      <div class="add-btn" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        <span>新增</span>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="4" animated />
    </div>

    <template v-else>
      <div class="address-list">
        <div v-if="addressList.length === 0" class="empty-state">
          <el-empty description="暂无收货地址" :image-size="120">
            <el-button type="primary" round @click="handleAdd">添加地址</el-button>
          </el-empty>
        </div>

        <div
          v-for="address in addressList"
          :key="address.id"
          :class="['address-card', { default: address.isDefault }]"
          @click="selectAddress(address)"
        >
          <div class="address-info">
            <div class="user-info">
              <span class="name">{{ address.name }}</span>
              <span class="phone">{{ address.phone }}</span>
              <span v-if="address.isDefault" class="default-tag">默认</span>
              <span v-if="address.tag" class="address-tag">{{ address.tag }}</span>
            </div>
            <div class="address-detail">
              {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
            </div>
          </div>
          <div class="address-actions">
            <span class="edit-btn" @click.stop="handleEdit(address)">
              <el-icon><Edit /></el-icon>编辑
            </span>
            <span class="delete-btn" @click.stop="handleDelete(address)">
              <el-icon><Delete /></el-icon>删除
            </span>
          </div>
        </div>
      </div>
    </template>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑地址' : '新增地址'"
      width="90%"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-position="top">
        <el-form-item label="收货人" prop="name">
          <el-input v-model="formData.name" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="所在地区" prop="province">
          <div style="display: flex; gap: 8px;">
            <el-input v-model="formData.province" placeholder="省" style="flex: 1" />
            <el-input v-model="formData.city" placeholder="市" style="flex: 1" />
            <el-input v-model="formData.district" placeholder="区/县" style="flex: 1" />
          </div>
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input v-model="formData.detail" type="textarea" :rows="2" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="地址标签">
          <el-radio-group v-model="formData.tag">
            <el-radio value="家">家</el-radio>
            <el-radio value="公司">公司</el-radio>
            <el-radio value="学校">学校</el-radio>
            <el-radio value="">其他</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="formData.isDefault">设为默认地址</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          {{ isEdit ? '保存' : '确认添加' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { ArrowLeft, Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
  getAddressList as fetchAddressList,
  addAddress,
  updateAddress,
  deleteAddress as removeAddress,
  setDefaultAddress
} from '@/api/modules/address'
import type { UserAddress } from '@/types'

const router = useRouter()
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const addressList = ref<UserAddress[]>([])

const formData = reactive({
  id: '',
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false,
  tag: ''
})

const formRules: FormRules = {
  name: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const goBack = () => {
  router.back()
}

const loadAddressList = async () => {
  loading.value = true
  try {
    const res = await fetchAddressList()
    addressList.value = Array.isArray(res) ? res : []
  } catch (error) {
    console.error('获取地址列表失败:', error)
  } finally {
    loading.value = false
  }
}

const selectAddress = (address: UserAddress) => {
  const from = router.currentRoute.value.query.from
  if (from) {
    router.back()
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    id: '', name: '', phone: '', province: '', city: '',
    district: '', detail: '', isDefault: false, tag: ''
  })
  dialogVisible.value = true
}

const handleEdit = (address: UserAddress) => {
  isEdit.value = true
  Object.assign(formData, {
    id: address.id,
    name: address.name,
    phone: address.phone,
    province: address.province,
    city: address.city,
    district: address.district,
    detail: address.detail,
    isDefault: address.isDefault,
    tag: address.tag || ''
  })
  dialogVisible.value = true
}

const handleDelete = async (address: UserAddress) => {
  try {
    await ElMessageBox.confirm('确定删除该地址吗？', '提示', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await removeAddress(address.id)
    ElMessage.success('删除成功')
    loadAddressList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitLoading.value = true

    const data = {
      name: formData.name,
      phone: formData.phone,
      province: formData.province,
      city: formData.city,
      district: formData.district,
      detail: formData.detail,
      isDefault: formData.isDefault,
      tag: formData.tag
    }

    if (isEdit.value) {
      await updateAddress(formData.id, data)
      ElMessage.success('修改成功')
    } else {
      await addAddress(data as any)
      ElMessage.success('添加成功')
    }

    if (formData.isDefault && formData.id) {
      await setDefaultAddress(isEdit.value ? formData.id : '')
    }

    dialogVisible.value = false
    loadAddressList()
  } catch (error: any) {
    if (error !== false) {
      ElMessage.error('操作失败')
    }
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadAddressList()
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.address-list-page {
  min-height: 100vh;
  background: $bg-primary;
}

.nav-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-md;
  padding-top: calc($safe-area-top + $spacing-md);
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  color: $text-white;
  position: sticky;
  top: 0;
  z-index: 100;

  .back-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border-radius: 50%;
    transition: all 0.2s;

    &:hover {
      background: rgba(255, 255, 255, 0.2);
    }
  }

  .title {
    font-size: $font-lg;
    font-weight: 600;
  }

  .add-btn {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    font-size: $font-md;
    cursor: pointer;
    padding: $spacing-xs $spacing-sm;
    border-radius: $radius-full;
    transition: all 0.2s;

    &:hover {
      background: rgba(255, 255, 255, 0.2);
    }
  }
}

.loading-container {
  padding: $spacing-lg;
  margin-top: $spacing-xl;
}

.address-list {
  padding: $spacing-md;

  .empty-state {
    margin-top: $spacing-xxl;
  }
}

.address-card {
  background: $bg-white;
  border-radius: $radius-lg;
  padding: $spacing-lg;
  margin-bottom: $spacing-md;
  box-shadow: $shadow-sm;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.2s;

  &.default {
    border-color: $primary;
    background: linear-gradient(to right, rgba($primary, 0.02), $bg-white);
  }

  .address-info {
    .user-info {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      margin-bottom: $spacing-sm;

      .name {
        font-size: $font-lg;
        font-weight: 600;
        color: $text-primary;
      }

      .phone {
        font-size: $font-sm;
        color: $text-secondary;
      }

      .default-tag {
        font-size: $font-xs;
        color: $primary;
        background: rgba($primary, 0.08);
        padding: 2px 8px;
        border-radius: $radius-sm;
      }

      .address-tag {
        font-size: $font-xs;
        color: $text-secondary;
        background: $bg-gray;
        padding: 2px 8px;
        border-radius: $radius-sm;
      }
    }

    .address-detail {
      font-size: $font-sm;
      color: $text-secondary;
      line-height: 1.5;
    }
  }

  .address-actions {
    display: flex;
    gap: $spacing-lg;
    margin-top: $spacing-md;
    padding-top: $spacing-md;
    border-top: 1px solid $border-light;

    span {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: $font-sm;
      cursor: pointer;
      transition: color 0.2s;

      &:hover {
        color: $primary;
      }

      .el-icon {
        font-size: 14px;
      }
    }

    .delete-btn {
      color: $error;

      &:hover {
        color: darken($error, 10%);
      }
    }
  }
}
</style>
