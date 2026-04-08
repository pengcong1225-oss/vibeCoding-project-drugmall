<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import {
  Money, ShoppingCart, User, ChatLineRound,
  ArrowUp, ArrowDown
} from '@element-plus/icons-vue'

const router = useRouter()

// 数据概览卡片
const overviewCards = ref([
  {
    title: '今日GMV',
    value: '¥128,456.00',
    change: '+12.5%',
    trend: 'up',
    icon: Money,
    color: '#52c41a'
  },
  {
    title: '今日订单',
    value: '1,286',
    change: '+8.2%',
    trend: 'up',
    icon: ShoppingCart,
    color: '#1890ff'
  },
  {
    title: '新增用户',
    value: '156',
    change: '-2.3%',
    trend: 'down',
    icon: User,
    color: '#722ed1'
  },
  {
    title: '今日问诊',
    value: '89',
    change: '+15.6%',
    trend: 'up',
    icon: ChatLineRound,
    color: '#fa8c16'
  }
])

// 图表实例
let gmvChart: echarts.ECharts | null = null
let orderChart: echarts.ECharts | null = null
let sourceChart: echarts.ECharts | null = null
let categoryChart: echarts.ECharts | null = null

// 初始化销售趋势图
const initGmvChart = () => {
  const chartDom = document.getElementById('gmv-chart')
  if (!chartDom) return
  
  gmvChart = echarts.init(chartDom)
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
      data: ['1日', '2日', '3日', '4日', '5日', '6日', '7日', '8日', '9日', '10日', '11日', '12日'],
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
        data: [82000, 93200, 90100, 93400, 129000, 133000, 132000, 125000, 118000, 128000, 135000, 128456],
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
        data: [820, 932, 901, 934, 1290, 1330, 1320, 1250, 1180, 1286, 1350, 1286],
        itemStyle: { color: '#1890ff' }
      }
    ]
  }
  gmvChart.setOption(option)
}

// 初始化订单来源分布图
const initSourceChart = () => {
  const chartDom = document.getElementById('source-chart')
  if (!chartDom) return
  
  sourceChart = echarts.init(chartDom)
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
        data: [
          { value: 520, name: 'APP端', itemStyle: { color: '#1890ff' } },
          { value: 380, name: 'H5网页', itemStyle: { color: '#52c41a' } },
          { value: 290, name: '微信小程序', itemStyle: { color: '#faad14' } },
          { value: 96, name: '支付宝小程序', itemStyle: { color: '#722ed1' } }
        ]
      }
    ]
  }
  sourceChart.setOption(option)
}

// 窗口大小改变时重新计算图表大小
const handleResize = () => {
  gmvChart?.resize()
  sourceChart?.resize()
}

onMounted(() => {
  initGmvChart()
  initSourceChart()
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

<script lang="ts">
// 额外的script用于定义时间范围变量
timeRange: 'day'
</script>

<script setup lang="ts">
const timeRange = ref('day')

const categoryData = ref([
  { name: '处方药', value: 35, color: '#409eff' },
  { name: '非处方药', value: 28, color: '#67c23a' },
  { name: '保健品', value: 18, color: '#e6a23c' },
  { name: '医疗器械', value: 12, color: '#f56c6c' },
  { name: '中药饮片', value: 7, color: '#909399' }
])
</script>

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
