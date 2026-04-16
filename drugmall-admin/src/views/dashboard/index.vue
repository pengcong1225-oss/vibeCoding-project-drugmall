<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import {
  Money, ShoppingCart, User, ChatLineRound,
  ArrowUp, ArrowDown
} from '@element-plus/icons-vue'
import { getDashboardOverview, getGmvTrend, getOrderSource } from '@/api/dashboard'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)

const iconMap: Record<string, any> = { Money, ShoppingCart, User, ChatDotRound: ChatLineRound }

// 数据概览卡片
const overviewCards = ref<any[]>([])

// 分类数据
const categoryData = ref<any[]>([])

// 图表实例
let gmvChart: echarts.ECharts | null = null
let orderChart: echarts.ECharts | null = null
let sourceChart: echarts.ECharts | null = null
let categoryChart: echarts.ECharts | null = null

// 时间范围
const timeRange = ref('day')

// 初始化销售趋势图
const initGmvChart = (dates: string[] = [], gmv: number[] = [], orders: number[] = []) => {
  const chartDom = document.getElementById('gmv-chart')
  if (!chartDom) return
  
  if (!gmvChart) gmvChart = echarts.init(chartDom)
  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['GMV', '订单量'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: { lineStyle: { color: '#ccc' } }
    },
    yAxis: [
      {
        type: 'value',
        name: 'GMV',
        position: 'left',
        axisLine: { show: false },
        axisLabel: { formatter: '¥{value}' }
      },
      {
        type: 'value',
        name: '订单量',
        position: 'right',
        axisLine: { show: false }
      }
    ],
    series: [
      {
        name: 'GMV',
        type: 'line',
        smooth: true,
        yAxisIndex: 0,
        data: gmv,
        itemStyle: { color: '#52c41a' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(82, 196, 26, 0.3)' },
            { offset: 1, color: 'rgba(82, 196, 26, 0.05)' }
          ])
        }
      },
      {
        name: '订单量',
        type: 'bar',
        yAxisIndex: 1,
        data: orders,
        itemStyle: { color: '#1890ff' }
      }
    ]
  }
  gmvChart.setOption(option)
}

// 初始化订单来源分布图
const initSourceChart = (sourceData: any[] = []) => {
  const chartDom = document.getElementById('source-chart')
  if (!chartDom) return
  
  if (!sourceChart) sourceChart = echarts.init(chartDom)
  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center'
    },
    series: [
      {
        name: '订单来源',
        type: 'pie',
        radius: ['50%', '70%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: sourceData
      }
    ]
  }
  sourceChart.setOption(option)
}

// 加载仪表盘数据
const fetchDashboardData = async () => {
  loading.value = true
  try {
    const [overviewData, gmvData, sourceData] = await Promise.all([
      getDashboardOverview(),
      getGmvTrend(timeRange.value),
      getOrderSource()
    ])
    
    overviewCards.value = overviewData.cards.map((card: any) => ({
      ...card,
      icon: iconMap[card.icon] || Money
    }))
    categoryData.value = overviewData.categoryData
    
    initGmvChart(gmvData.dates, gmvData.gmv, gmvData.orders)
    initSourceChart(sourceData)
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
    ElMessage.error('加载数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 加载GMV趋势数据
const fetchGmvTrendData = async () => {
  try {
    const gmvData = await getGmvTrend(timeRange.value)
    initGmvChart(gmvData.dates, gmvData.gmv, gmvData.orders)
  } catch (error) {
    console.error('加载GMV趋势数据失败:', error)
    ElMessage.error('加载趋势数据失败')
  }
}

// 窗口大小改变时重新计算图表大小
const handleResize = () => {
  gmvChart?.resize()
  sourceChart?.resize()
}

// 监听时间范围变化
watch(timeRange, () => {
  fetchGmvTrendData()
})

onMounted(() => {
  fetchDashboardData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  gmvChart?.dispose()
  sourceChart?.dispose()
})

// 处理卡片点击
const handleCardClick = (card: any) => {
  console.log('Card clicked:', card)
}
</script>

<template>
  <div class="dashboard-container">
    <!-- 数据概览卡片 -->
    <el-row :gutter="16" class="overview-cards">
      <el-col :xs="24" :sm="12" :md="6" v-for="card in overviewCards" :key="card.title">
        <el-card class="overview-card" @click="handleCardClick(card)">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">{{ card.title }}</div>
              <div class="card-value">{{ card.value }}</div>
              <div class="card-change" :class="card.trend">
                <el-icon v-if="card.trend === 'up'"><ArrowUp /></el-icon>
                <el-icon v-else><ArrowDown /></el-icon>
                <span>{{ card.change }}</span>
              </div>
            </div>
            <div class="card-icon" :style="{ backgroundColor: card.color }">
              <el-icon :size="32" color="#fff">
                <component :is="card.icon" />
              </el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 销售趋势图表 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span class="chart-title">GMV & 订单量趋势</span>
              <el-radio-group v-model="timeRange" size="small">
                <el-radio-button label="day">今日</el-radio-button>
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div id="gmv-chart" class="chart-container" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 订单分布和用户类型 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :md="12">
        <el-card class="chart-card">
          <template #header>
            <span class="chart-title">订单来源分布</span>
          </template>
          <div id="source-chart" class="chart-container" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card class="chart-card">
          <template #header>
            <span class="chart-title">药品分类销售占比</span>
          </template>
          <div class="category-list">
            <div v-for="(item, index) in categoryData" :key="index" class="category-item">
              <div class="category-info">
                <span class="category-name">{{ item.name }}</span>
                <span class="category-value">{{ item.value }}%</span>
              </div>
              <el-progress :percentage="item.value" :color="item.color" :show-text="false" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped lang="scss">
.dashboard-container {
  padding: 0;
}

.overview-cards {
  margin-bottom: 16px;
}

.overview-card {
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }

  .card-content {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .card-info {
      .card-title {
        font-size: 14px;
        color: #909399;
        margin-bottom: 8px;
      }

      .card-value {
        font-size: 28px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 8px;
      }

      .card-change {
        display: flex;
        align-items: center;
        font-size: 12px;

        &.up {
          color: #67c23a;
        }

        &.down {
          color: #f56c6c;
        }

        .el-icon {
          margin-right: 4px;
        }
      }
    }

    .card-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 64px;
      height: 64px;
      border-radius: 8px;
    }
  }
}

.chart-row {
  margin-bottom: 16px;
}

.chart-card {
  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .chart-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }

  .chart-container {
    width: 100%;
  }
}

.category-list {
  .category-item {
    margin-bottom: 16px;

    &:last-child {
      margin-bottom: 0;
    }

    .category-info {
      display: flex;
      justify-content: space-between;
      margin-bottom: 8px;

      .category-name {
        font-size: 14px;
        color: #606266;
      }

      .category-value {
        font-size: 14px;
        font-weight: 600;
        color: #303133;
      }
    }
  }
}
</style>
