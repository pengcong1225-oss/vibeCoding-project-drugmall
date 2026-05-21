<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getConsultationStats } from '@/api/stats'
import type { ConsultationStats } from '@/types/stats'

const loading = ref(false)
const stats = ref<ConsultationStats | null>(null)
const dateRange = ref<[string, string]>([
  new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
  new Date().toISOString().split('T')[0]
])

async function fetchData() {
  loading.value = true
  try {
    const res = await getConsultationStats({
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    })
    stats.value = res
  } catch {
    ElMessage.error('获取统计数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="consultation-stats" v-loading="loading">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>问诊统计</span>
          <div>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="fetchData"
            />
            <el-button :icon="Refresh" @click="fetchData" style="margin-left: 12px">刷新</el-button>
          </div>
        </div>
      </template>

      <template v-if="stats">
        <el-row :gutter="20" style="margin-bottom: 24px">
          <el-col :span="6">
            <el-card shadow="hover">
              <el-statistic title="问诊总量" :value="stats.totalConsultations" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <el-statistic title="今日问诊" :value="stats.todayConsultations" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <el-statistic title="平均响应时间(分钟)" :value="stats.avgResponseTime" :precision="1" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <el-statistic title="完成率" :value="stats.completionRate" suffix="%" :precision="1" />
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-card>
              <template #header>问诊类型分布</template>
              <el-table :data="stats.consultationTypeStats" border>
                <el-table-column prop="type" label="类型" />
                <el-table-column prop="count" label="数量" />
                <el-table-column prop="percentage" label="占比">
                  <template #default="{ row }">{{ row.percentage.toFixed(1) }}%</template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card>
              <template #header>科室统计</template>
              <el-table :data="stats.departmentStats" border>
                <el-table-column prop="department" label="科室" />
                <el-table-column prop="count" label="问诊量" />
                <el-table-column prop="avgResponseTime" label="平均响应(分钟)">
                  <template #default="{ row }">{{ row.avgResponseTime.toFixed(1) }}</template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
        </el-row>

        <el-card style="margin-top: 20px">
          <template #header>医生问诊量排行</template>
          <el-table :data="stats.doctorRanking" border>
            <el-table-column type="index" label="排名" width="80" />
            <el-table-column prop="doctorName" label="医生" />
            <el-table-column prop="count" label="问诊量" />
            <el-table-column prop="avgRating" label="平均评分">
              <template #default="{ row }">{{ row.avgRating.toFixed(1) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </template>
    </el-card>
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
