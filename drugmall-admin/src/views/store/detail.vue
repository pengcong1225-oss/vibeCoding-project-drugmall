<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import { getStoreDetail, updateStore } from '@/api/store'
import type { StoreDetail } from '@/types/store'

const route = useRoute()
const loading = ref(false)
const activeTab = ref('basic')
const storeInfo = ref<StoreDetail | null>(null)
const editDialogVisible = ref(false)
const editForm = reactive({
  name: '',
  address: '',
  contact: '',
  phone: '',
  businessHours: '',
  deliveryRange: 5,
  deliveryFee: 0,
  minOrderAmount: 0
})

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '正常', type: 'success' },
  2: { label: '停业', type: 'danger' },
  3: { label: '禁用', type: 'info' }
}

const drugColumns = [
  { label: '药品ID', prop: 'drugId', width: 90 },
  { label: '药品名称', prop: 'drugName', minWidth: 140 },
  { label: '规格', prop: 'specification', width: 100 },
  { label: '厂家', prop: 'manufacturer', width: 120 },
  { label: '库存', prop: 'stock', width: 80, align: 'center' },
  { label: '售价', prop: 'price', width: 100, align: 'right' },
  { label: '状态', prop: 'status', width: 90, align: 'center' }
]

const loadDetail = async () => {
  loading.value = true
  try {
    const id = Number(route.params.id)
    const res = await getStoreDetail(id)
    storeInfo.value = res
    Object.assign(editForm, {
      name: res.name,
      address: res.address,
      contact: res.contact,
      phone: res.phone,
      businessHours: res.businessHours || '09:00-21:00',
      deliveryRange: res.deliveryRange || 5,
      deliveryFee: res.deliveryFee || 0,
      minOrderAmount: res.minOrderAmount || 0
    })
  } catch (error) {
    console.error('获取门店详情失败:', error)
  } finally {
    loading.value = false
  }
}

const handleEdit = () => {
  editDialogVisible.value = true
}

const submitEdit = async () => {
  try {
    if (storeInfo.value) {
      await updateStore(storeInfo.value.id, editForm)
      ElMessage.success('编辑成功')
      editDialogVisible.value = false
      loadDetail()
    }
  } catch (error) {
    console.error('编辑失败:', error)
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<template>
  <div v-loading="loading" class="store-detail-container">
    <el-page-header @back="$router.back()" title="返回" style="margin-bottom: 20px">
      <template #content>
        <span class="page-title">门店详情</span>
        <el-tag v-if="storeInfo" :type="statusMap[storeInfo.status]?.type as any" style="margin-left: 12px">
          {{ statusMap[storeInfo.status]?.label }}
        </el-tag>
      </template>
    </el-page-header>

    <el-tabs v-model="activeTab" type="border-card" v-if="storeInfo">
      <!-- 基本信息 -->
      <el-tab-pane label="基本信息" name="basic">
        <div style="display: flex; justify-content: flex-end; margin-bottom: 16px">
          <el-button type="primary" :icon="Plus" @click="handleEdit">编辑信息</el-button>
        </div>
        <el-descriptions :column="3" border>
          <el-descriptions-item label="门店ID">{{ storeInfo.id }}</el-descriptions-item>
          <el-descriptions-item label="门店名称">{{ storeInfo.name }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ storeInfo.contact }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ storeInfo.phone }}</el-descriptions-item>
          <el-descriptions-item label="门店状态" :span="2">
            <el-tag :type="statusMap[storeInfo.status]?.type as any">{{ statusMap[storeInfo.status]?.label }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="许可证号" :span="3">{{ storeInfo.licenseNo }}</el-descriptions-item>
          <el-descriptions-item label="营业时间" :span="2">{{ storeInfo.businessHours || '-' }}</el-descriptions-item>
          <el-descriptions-item label="配送范围">{{ storeInfo.deliveryRange }}km</el-descriptions-item>
          <el-descriptions-item label="配送费">¥{{ storeInfo.deliveryFee }}</el-descriptions-item>
          <el-descriptions-item label="起送金额">¥{{ storeInfo.minOrderAmount }}</el-descriptions-item>
          <el-descriptions-item label="地址" :span="3">{{ storeInfo.address }}</el-descriptions-item>
          <el-descriptions-item label="入驻时间">{{ storeInfo.joinTime }}</el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">门店照片</el-divider>
        <el-row :gutter="16">
          <el-col :span="6" v-for="(img, i) in storeInfo.storeImages" :key="i">
            <el-card shadow="hover">
              <el-image :src="img" style="width: 100%; height: 120px" fit="contain" :preview-src-list="storeInfo.storeImages" />
            </el-card>
          </el-col>
        </el-row>

        <el-divider content-position="left">资质材料</el-divider>
        <el-row :gutter="16">
          <el-col :span="8" v-for="(item, key) in storeInfo.auditMaterials" :key="key">
            <el-card shadow="hover" class="cert-card">
              <el-image :src="item as string" style="width: 100%; height: 120px" fit="contain" />
              <div style="text-align: center; margin-top: 8px; font-size: 12px; color: #606266">
                {{ { businessLicense: '营业执照', drugLicense: '药品经营许可证', gspCert: 'GSP证书', legalIdFront: '法人身份证正面', legalIdBack: '法人身份证反面', storePhoto: '门店照片' }[key as string] || key }}
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 统计数据 -->
      <el-tab-pane label="统计数据" name="stats">
        <el-row :gutter="20" style="margin-bottom: 20px">
          <el-col :span="6">
            <el-card shadow="never" class="stat-card">
              <div class="stat-label">累计订单</div>
              <div class="stat-value">{{ storeInfo.stats?.totalOrders }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="never" class="stat-card">
              <div class="stat-label">累计销售额</div>
              <div class="stat-value">¥{{ storeInfo.stats?.totalSales }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="never" class="stat-card">
              <div class="stat-label">药品数量</div>
              <div class="stat-value">{{ storeInfo.stats?.drugCount }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="never" class="stat-card">
              <div class="stat-label">评分</div>
              <div class="stat-value">{{ storeInfo.stats?.rating }}</div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 门店药品 -->
      <el-tab-pane label="门店药品" name="drugs">
        <el-table :data="storeInfo.recentDrugs || []" stripe>
          <el-table-column v-for="col in drugColumns" :key="col.prop" v-bind="col" />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary">编辑</el-button>
              <el-button link type="danger">下架</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!storeInfo.recentDrugs?.length" description="暂无药品" />
      </el-tab-pane>
    </el-tabs>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑门店信息" width="600px" destroy-on-close>
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="门店名称"><el-input v-model="editForm.name" /></el-form-item>
        <el-form-item label="联系人"><el-input v-model="editForm.contact" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="editForm.phone" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="editForm.address" /></el-form-item>
        <el-form-item label="营业时间"><el-input v-model="editForm.businessHours" /></el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="配送范围">
              <el-input-number v-model="editForm.deliveryRange" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="配送费">
              <el-input-number v-model="editForm.deliveryFee" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="起送金额">
              <el-input-number v-model="editForm.minOrderAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.store-detail-container { padding: 20px; }
.page-title { font-size: 18px; font-weight: 600; }
.cert-card { margin-bottom: 16px; }
.stat-card { text-align: center; }
.stat-label { font-size: 14px; color: #909399; margin-bottom: 8px; }
.stat-value { font-size: 28px; font-weight: 700; color: #303133; }
</style>
