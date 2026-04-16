<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload, Check } from '@element-plus/icons-vue'
import { getPaymentSettings, savePaymentSettings } from '@/api/settings'

const loading = ref(false)

// 支付配置表单
const formRef = ref()
const formData = ref({
  wxEnabled: false,
  wxMchId: '',
  wxKey: '',
  wxCertPath: '',
  wxNotifyUrl: '',
  aliEnabled: false,
  aliAppId: '',
  aliPrivateKey: '',
  aliPublicKey: '',
  aliNotifyUrl: '',
  balanceEnabled: false,
  feeRate: 0
})

// 测试弹窗
const testDialogVisible = ref(false)
const testAmount = ref(0.01)

// 加载配置
const loadSettings = async () => {
  loading.value = true
  try {
    const data = await getPaymentSettings()
    formData.value = { ...formData.value, ...data }
  } catch (error) {
    console.error('获取支付配置失败:', error)
  } finally {
    loading.value = false
  }
}

// 保存配置
const handleSave = async () => {
  formRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      try {
        await savePaymentSettings(formData.value)
        ElMessage.success('支付配置保存成功')
      } catch (error) {
        console.error('保存支付配置失败:', error)
        ElMessage.error('支付配置保存失败')
      }
    }
  })
}

// 测试支付
const handleTest = () => {
  testDialogVisible.value = true
}

// 确认测试
const confirmTest = () => {
  ElMessage.success(`支付测试发起成功，金额：¥${testAmount.value}`)
  testDialogVisible.value = false
}

onMounted(() => {
  loadSettings()
})
</script>

<template>
  <div class="payment-settings">
    <el-form ref="formRef" :model="formData" label-width="140px" class="settings-form">
      <!-- 微信支付 -->
      <el-card class="setting-card" shadow="never">
        <template #header>
          <div class="card-header">
            <div class="title">
              <el-icon size="20"><Wallet /></el-icon>
              <span>微信支付</span>
            </div>
            <el-switch v-model="formData.wxEnabled" active-text="启用" inactive-text="禁用" />
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商户号(MchId)">
              <el-input v-model="formData.wxMchId" placeholder="请输入微信商户号" :disabled="!formData.wxEnabled" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="API密钥(Key)">
              <el-input v-model="formData.wxKey" type="password" placeholder="请输入API密钥" show-password :disabled="!formData.wxEnabled" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="支付证书">
              <el-input v-model="formData.wxCertPath" placeholder="请上传支付证书" :disabled="!formData.wxEnabled">
                <template #append>
                  <el-button :icon="Upload" :disabled="!formData.wxEnabled">上传</el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="回调地址">
              <el-input v-model="formData.wxNotifyUrl" placeholder="请输入回调地址" :disabled="!formData.wxEnabled" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 支付宝 -->
      <el-card class="setting-card" shadow="never">
        <template #header>
          <div class="card-header">
            <div class="title">
              <el-icon size="20"><Money /></el-icon>
              <span>支付宝</span>
            </div>
            <el-switch v-model="formData.aliEnabled" active-text="启用" inactive-text="禁用" />
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="应用ID(AppId)">
              <el-input v-model="formData.aliAppId" placeholder="请输入支付宝应用ID" :disabled="!formData.aliEnabled" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="私钥">
              <el-input v-model="formData.aliPrivateKey" type="password" placeholder="请输入应用私钥" show-password :disabled="!formData.aliEnabled" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="公钥">
              <el-input v-model="formData.aliPublicKey" type="password" placeholder="请输入支付宝公钥" show-password :disabled="!formData.aliEnabled" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="回调地址">
              <el-input v-model="formData.aliNotifyUrl" placeholder="请输入回调地址" :disabled="!formData.aliEnabled" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 其他设置 -->
      <el-card class="setting-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>其他设置</span>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="余额支付">
              <el-switch v-model="formData.balanceEnabled" active-text="启用" inactive-text="禁用" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手续费率">
              <el-input-number v-model="formData.feeRate" :min="0" :max="10" :precision="2" :step="0.1" style="width: 120px" />
              <span style="margin-left: 8px; color: #666;">%</span>
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 保存按钮 -->
      <div class="form-actions">
        <el-button type="primary" size="large" :icon="Check" @click="handleSave">保存配置</el-button>
        <el-button size="large" @click="handleTest">支付测试</el-button>
      </div>
    </el-form>

    <!-- 测试弹窗 -->
    <el-dialog v-model="testDialogVisible" title="支付测试" width="400px">
      <el-form>
        <el-form-item label="支付金额">
          <el-input-number v-model="testAmount" :min="0.01" :precision="2" :step="0.01" style="width: 150px" />
          <span style="margin-left: 8px;">元</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="testDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmTest">发起测试</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.payment-settings {
  padding: 0;

  .settings-form {
    max-width: 1200px;
  }

  .setting-card {
    margin-bottom: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }
  }

  .form-actions {
    display: flex;
    justify-content: center;
    gap: 16px;
    padding: 20px 0;
  }
}
</style>