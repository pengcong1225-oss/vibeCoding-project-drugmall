<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getConsultationDetail, getConsultationMessages } from '@/api/consultation'
import type { ConsultationDetail, ConsultationMessage } from '@/types/consultation'

const route = useRoute()
const loading = ref(false)
const chatLoading = ref(false)
const detail = ref<ConsultationDetail | null>(null)
const messages = ref<ConsultationMessage[]>([])

const typeMap: Record<string, { label: string; type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  text: { label: '图文问诊', type: 'primary' },
  video: { label: '视频问诊', type: 'success' },
  phone: { label: '电话问诊', type: 'warning' },
  ai: { label: 'AI导诊', type: 'info' }
}

const genderMap: Record<number, string> = { 0: '女', 1: '男' }

const statusMap: Record<number, { label: string; type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  0: { label: '待接诊', type: 'warning' },
  1: { label: '问诊中', type: 'primary' },
  2: { label: '待支付', type: 'info' },
  3: { label: '已完成', type: 'success' },
  4: { label: '已取消', type: 'danger' },
  5: { label: '退款中', type: 'warning' },
  6: { label: '已退款', type: 'info' }
}

const loadDetail = async () => {
  loading.value = true
  try {
    const id = Number(route.params.id)
    detail.value = await getConsultationDetail(id)
  } catch {
    console.error('获取问诊详情失败')
  } finally {
    loading.value = false
  }
}

const loadMessages = async () => {
  chatLoading.value = true
  try {
    const id = Number(route.params.id)
    messages.value = await getConsultationMessages(id)
  } catch {
    console.error('获取聊天记录失败')
  } finally {
    chatLoading.value = false
  }
}

const getStatusTagType = (_type: string): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
  return 'primary'
}

const getMsgClass = (msg: ConsultationMessage) => {
  if (msg.senderType === 'doctor') return 'msg-doctor'
  if (msg.senderType === 'patient') return 'msg-patient'
  return 'msg-system'
}

onMounted(() => {
  loadDetail()
  loadMessages()
})
</script>

<template>
  <div v-loading="loading" class="consultation-detail-container">
    <el-page-header @back="$router.back()" title="返回" style="margin-bottom: 20px">
      <template #content>
        <span class="page-title">问诊详情</span>
        <el-tag v-if="detail" :type="statusMap[detail.status]?.type as any" style="margin-left: 12px">
          {{ statusMap[detail.status]?.label }}
        </el-tag>
      </template>
    </el-page-header>

    <el-row :gutter="20">
      <!-- 左侧信息 -->
      <el-col :span="12">
        <el-card shadow="never" class="info-card">
          <template #header>问诊信息</template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="问诊编号">{{ detail?.consultationNo }}</el-descriptions-item>
            <el-descriptions-item label="问诊类型">
              <el-tag :type="typeMap[detail?.type || '']?.type" size="small">
                {{ typeMap[detail?.type || '']?.label }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="患者昵称">{{ detail?.patientNickname }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ detail?.patientGender !== undefined ? genderMap[detail.patientGender] : '-' }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ detail?.patientAge || '-' }}岁</el-descriptions-item>
            <el-descriptions-item label="医生">{{ detail?.doctorName }}</el-descriptions-item>
            <el-descriptions-item label="科室">{{ detail?.departmentName }}</el-descriptions-item>
            <el-descriptions-item label="费用">¥{{ detail?.fee }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="statusMap[detail?.status || 0]?.type as any" size="small">
                {{ statusMap[detail?.status || 0]?.label }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="症状" :span="2">{{ detail?.symptom }}</el-descriptions-item>
            <el-descriptions-item label="过敏史">{{ detail?.allergyHistory || '无' }}</el-descriptions-item>
            <el-descriptions-item label="用药史">{{ detail?.medicationHistory || '无' }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ detail?.createTime }}</el-descriptions-item>
            <el-descriptions-item label="结束时间">{{ detail?.endTime || '-' }}</el-descriptions-item>
          </el-descriptions>

          <!-- 上传图片 -->
          <div v-if="detail?.images?.length" style="margin-top: 20px">
            <h4>症状图片</h4>
            <el-image v-for="(img, i) in detail.images" :key="i" :src="img" style="width: 120px; height: 90px; margin-right: 10px" fit="cover" :preview-src-list="detail.images" />
          </div>
        </el-card>
      </el-col>

      <!-- 右侧聊天 -->
      <el-col :span="12">
        <el-card shadow="never" class="chat-card">
          <template #header>聊天消息</template>
          <div v-loading="chatLoading" class="chat-container">
            <div v-for="msg in messages" :key="msg.id" :class="['message-item', getMsgClass(msg)]">
              <div class="msg-header">
                <span class="msg-sender">{{ msg.senderName }}</span>
                <span class="msg-time">{{ msg.createTime }}</span>
              </div>
              <div v-if="msg.messageType === 'text'" class="msg-content">{{ msg.content }}</div>
              <div v-else-if="msg.messageType === 'image'" class="msg-image">
                <el-image v-if="msg.images && msg.images.length" :src="msg.images[0]" style="max-width: 200px" :preview-src-list="msg.images" fit="cover" />
              </div>
              <div v-else-if="msg.messageType === 'prescription'" class="msg-prescription">
                <el-button type="primary" size="small" @click="$router.push(`/prescription/detail/${msg.prescriptionId}`)">查看处方</el-button>
              </div>
              <div v-else class="msg-system">{{ msg.content }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作日志 -->
    <el-card shadow="never" style="margin-top: 20px">
      <template #header>操作日志</template>
      <el-timeline>
        <el-timeline-item v-for="log in detail?.operationLogs" :key="log.id" :timestamp="log.createTime" placement="top">
          <el-card>
            <h4>{{ log.action }}</h4>
            <p>{{ log.detail }}</p>
            <span class="operator">操作人：{{ log.operatorName }}</span>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.consultation-detail-container { padding: 20px; }
.info-card, .chat-card { min-height: 400px; }
.chat-container { height: 400px; overflow-y: auto; padding: 16px; background: #f5f7fa; border-radius: 4px; }
.message-item { margin-bottom: 16px; padding: 12px; background: #fff; border-radius: 4px; }
.msg-doctor { border-left: 3px solid #409EFF; }
.msg-patient { border-left: 3px solid #67C23A; }
.msg-system { color: #909399; font-size: 12px; }
.msg-header { display: flex; justify-content: space-between; margin-bottom: 8px; }
.msg-sender { font-weight: 600; }
.msg-time { color: #909399; font-size: 12px; }
.msg-content { line-height: 1.6; }
.msg-image { margin-top: 8px; }
.msg-prescription { margin-top: 8px; }
.operator { color: #909399; font-size: 12px; }
</style>
