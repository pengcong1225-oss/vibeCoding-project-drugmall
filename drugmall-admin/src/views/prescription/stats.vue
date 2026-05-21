<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getPrescriptionStats } from '@/api/stats'
import type { PrescriptionStats } from '@/types/stats'

const loading = ref(false)
const stats = ref<PrescriptionStats | null>(null)
const dateRange = ref<[string, string]>([
  new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
  new Date().toISOString().split('T')[0]
])

async function fetchData() {
  loading.value = true
  try {
    const res = await getPrescriptionStats({
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
  <div class="prescription-stats" v-loading="loading">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>处方统计</span>
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
              <el-statistic title="处方总量" :value="stats.totalPrescriptions" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <el-statistic title="今日处方" :value="stats.todayPrescriptions" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <el-statistic title="审核通过率" :value="stats.approvalRate" suffix="%" :precision="1" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <el-statistic title="驳回率" :value="stats.rejectionRate" suffix="%" :precision="1" />
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-card>
              <template #header>处方状态分布</template>
              <el-table :data="stats.statusDistribution" border>
                <el-table-column prop="status" label="状态" />
                <el-table-column prop="count" label="数量" />
                <el-table-column prop="percentage" label="占比">
                  <template #default="{ row }">{{ row.percentage.toFixed(1) }}%</template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card>
              <template #header>药品处方量排行</template>
              <el-table :data="stats.drugRanking" border>
                <el-table-column type="index" label="排名" width="80" />
                <el-table-column prop="drugName" label="药品名称" />
                <el-table-column prop="count" label="处方量" />
              </el-table>
            </el-card>
          </el-col>
        </el-row>

        <el-card style="margin-top: 20px">
          <template #header>医生处方量排行</template>
          <el-table :data="stats.doctorRanking" border>
            <el-table-column type="index" label="排名" width="80" />
            <el-table-column prop="doctorName" label="医生" />
            <el-table-column prop="count" label="处方量" />
            <el-table-column prop="approvalRate" label="通过率">
              <template #default="{ row }">{{ row.approvalRate.toFixed(1) }}%</template>
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
