<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getDoctorDetail } from '@/api/doctor'
import type { DoctorDetail } from '@/types/doctor'

const route = useRoute()
const loading = ref(false)
const activeTab = ref('basic')
const doctorInfo = ref<DoctorDetail | null>(null)

const genderMap: Record<number, string> = { 0: '女', 1: '男' }
const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '正常', type: 'success' },
  2: { label: '停诊', type: 'danger' },
  3: { label: '禁用', type: 'info' }
}

const loadDetail = async () => {
  loading.value = true
  try {
    const id = Number(route.params.id)
    const res = await getDoctorDetail(id)
    doctorInfo.value = res
  } catch (error) {
    console.error('获取医生详情失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<template>
  <div v-loading="loading" class="doctor-detail-container">
    <el-page-header @back="$router.back()" title="返回" style="margin-bottom: 20px">
      <template #content>
        <span class="page-title">医生详情</span>
      </template>
    </el-page-header>

    <el-tabs v-model="activeTab" type="border-card" v-if="doctorInfo">
      <!-- 基本信息 -->
      <el-tab-pane label="基本信息" name="basic">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="头像">
            <el-avatar :size="60" :src="doctorInfo.avatar" />
          </el-descriptions-item>
          <el-descriptions-item label="姓名">{{ doctorInfo.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ genderMap[doctorInfo.gender] }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ doctorInfo.age }}岁</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ doctorInfo.phone }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusMap[doctorInfo.status]?.type as any">{{ statusMap[doctorInfo.status]?.label }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="入驻时间">{{ doctorInfo.joinTime }}</el-descriptions-item>
          <el-descriptions-item label="科室">{{ doctorInfo.departmentName }}</el-descriptions-item>
          <el-descriptions-item label="职称">{{ doctorInfo.title }}</el-descriptions-item>
          <el-descriptions-item label="医院">{{ doctorInfo.hospital }}</el-descriptions-item>
          <el-descriptions-item label="资格证书号">{{ doctorInfo.certificateNo }}</el-descriptions-item>
          <el-descriptions-item label="执业证书号">{{ doctorInfo.licenseNo }}</el-descriptions-item>
          <el-descriptions-item label="执业范围">{{ doctorInfo.practiceScope }}</el-descriptions-item>
          <el-descriptions-item label="工作年限">{{ doctorInfo.workYears }}年</el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">个人简介</el-divider>
        <div style="padding: 12px; background: #f5f7fa; border-radius: 4px; white-space: pre-wrap">
          {{ doctorInfo.introduction || '暂无' }}
        </div>

        <el-divider content-position="left">资质材料</el-divider>
        <el-row :gutter="16">
          <el-col :span="6" v-for="(item, key) in doctorInfo.auditMaterials" :key="key">
            <el-card shadow="hover" class="cert-card">
              <el-image :src="item as string" style="width: 100%; height: 120px" fit="contain" />
              <div style="text-align: center; margin-top: 8px; font-size: 12px; color: #606266">
                {{ { idCardFront: '身份证正面', idCardBack: '身份证反面', certificate: '医师资格证书', license: '医师执业证书', titleCertificate: '职称证书', workProof: '在职证明', avatar: '医生头像' }[key as string] || key }}
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
              <div class="stat-label">累计问诊量</div>
              <div class="stat-value">{{ doctorInfo.stats?.totalConsultations }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="never" class="stat-card">
              <div class="stat-label">平均评分</div>
              <div class="stat-value">{{ doctorInfo.stats?.avgRating }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="never" class="stat-card">
              <div class="stat-label">平均响应时间</div>
              <div class="stat-value">{{ doctorInfo.stats?.avgResponseTime }} 分钟</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="never" class="stat-card">
              <div class="stat-label">好评率</div>
              <div class="stat-value">{{ doctorInfo.stats?.positiveRate }}%</div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 问诊记录 -->
      <el-tab-pane label="问诊记录" name="consultations">
        <el-table :data="doctorInfo.recentConsultations || []" stripe>
          <el-table-column label="问诊编号" prop="consultationNo" width="160" />
          <el-table-column label="患者" prop="patientName" width="100" />
          <el-table-column label="类型" prop="type" width="100" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag size="small" :type="row.status === 'completed' ? 'success' : 'warning'">
                {{ row.statusText }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="160" />
        </el-table>
        <el-empty v-if="!doctorInfo.recentConsultations?.length" description="暂无问诊记录" />
      </el-tab-pane>

      <!-- 处方记录 -->
      <el-tab-pane label="处方记录" name="prescriptions">
        <el-table :data="doctorInfo.recentPrescriptions || []" stripe>
          <el-table-column label="处方编号" prop="prescriptionNo" width="160" />
          <el-table-column label="诊断" prop="diagnosis" min-width="200" show-overflow-tooltip />
          <el-table-column label="药品数" prop="drugCount" width="100" />
          <el-table-column label="金额" prop="totalAmount" width="100">
            <template #default="{ row }">¥{{ row.totalAmount }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag size="small" :type="row.status === 'approved' ? 'success' : 'warning'">
                {{ row.statusText }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="160" />
        </el-table>
        <el-empty v-if="!doctorInfo.recentPrescriptions?.length" description="暂无处方记录" />
      </el-tab-pane>

      <!-- 评价记录 -->
      <el-tab-pane label="评价记录" name="reviews">
        <div v-for="review in doctorInfo.recentReviews" :key="review.id" class="review-item">
          <div class="review-header">
            <span class="patient-name">{{ review.patientNickname }}</span>
            <el-rate v-model="review.rating" disabled size="small" />
            <span class="review-time">{{ review.createTime }}</span>
          </div>
          <div class="review-content">{{ review.content }}</div>
          <div v-if="review.reply" class="review-reply">
            <strong>医生回复：</strong>{{ review.reply }}
          </div>
        </div>
        <el-empty v-if="!doctorInfo.recentReviews?.length" description="暂无评价记录" />
      </el-tab-pane>

      <!-- 排班信息 -->
      <el-tab-pane label="排班信息" name="schedule">
        <el-table :data="doctorInfo.recentSchedules || []" stripe>
          <el-table-column prop="date" label="日期" width="140" />
          <el-table-column label="上午" width="120" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.morningEnabled" type="success" size="small">已排班</el-tag>
              <span v-else class="text-muted">休息</span>
            </template>
          </el-table-column>
          <el-table-column label="下午" width="120" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.afternoonEnabled" type="success" size="small">已排班</el-tag>
              <span v-else class="text-muted">休息</span>
            </template>
          </el-table-column>
          <el-table-column label="晚上" width="120" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.eveningEnabled" type="success" size="small">已排班</el-tag>
              <span v-else class="text-muted">休息</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '正常' : '停诊' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!doctorInfo.recentSchedules?.length" description="暂无排班信息" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped lang="scss">
.doctor-detail-container { padding: 20px; }
.page-title { font-size: 18px; font-weight: 600; }
.cert-card { margin-bottom: 16px; }
.stat-card { text-align: center; }
.stat-label { font-size: 14px; color: #909399; margin-bottom: 8px; }
.stat-value { font-size: 28px; font-weight: 700; color: #303133; }
.review-item { padding: 16px 0; border-bottom: 1px solid #ebeef5; }
.review-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.patient-name { font-weight: 500; }
.review-time { font-size: 12px; color: #909399; margin-left: auto; }
.review-content { font-size: 14px; color: #606266; margin-bottom: 8px; }
.review-reply { padding: 8px 12px; background: #f5f7fa; border-radius: 4px; font-size: 13px; }
.text-muted { color: #c0c4cc; }
</style>
