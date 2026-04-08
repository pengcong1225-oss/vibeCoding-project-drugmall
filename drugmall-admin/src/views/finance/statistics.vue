<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { ArrowUp, ArrowDown, Money, TrendCharts, PieChart, Histogram, Download, Search, RefreshRight } from '@element-plus/icons-vue'

// 数据筛选表单
const filterForm = ref({
  timeRange: '30', // 默认近30天
  startDate: '',
  endDate: '',
  compareType: 'none', // none, yoy(同比), mom(环比)
  dimension: 'day' // day, week, month
})

// 统计卡片数据
const statCards = computed(() => [
  { title: '总收入', value: '¥2,156,789.00', change: '+32.1%', trend: 'up', icon: Money, color: '#67c23a', compareText: '较上月' },
  { title: '今日收入', value: '¥12,456.00', change: '+15.2%', trend: 'up', icon: TrendCharts, color: '#409eff', compareText: '较昨日' },
  { title: '本周收入', value: '¥89,234.00', change: '+8.5%', trend: 'up', icon: PieChart, color: '#e6a23c', compareText: '较上周' },
  { title: '本月收入', value: '¥356,789.00', change: '-2.3%', trend: 'down', icon: Histogram, color: '#f56c6c', compareText: '较上月' }
])

// 收入排行TOP10
const topProducts = ref([
  { name: '阿莫西林胶囊', amount: 45680, percent: 12.8, change: '+5.2%' },
  { name: '布洛芬缓释胶囊', amount: 38920, percent: 10.9, change: '+3.8%' },
  { name: '感冒灵颗粒', amount: 32560, percent: 9.1, change: '-2.1%' },
  { name: '维生素C泡腾片', amount: 28900, percent: 8.1, change: '+8.5%' },
  { name: '健胃消食片', amount: 25680, percent: 7.2, change: '+1.2%' },
  { name: '复方甘草片', amount: 23450, percent: 6.6, change: '-1.8%' },
  { name: '头孢克肟胶囊', amount: 21340, percent: 6.0, change: '+4.5%' },
  { name: '板蓝根颗粒', amount: 19800, percent: 5.5, change: '+2.3%' },
  { name: '999皮炎平', amount: 17650, percent: 4.9, change: '-0.5%' }
])

// 图表实例
let trendChart: echarts.ECharts | null = null
let compositionChart: echarts.ECharts | null = null
let compareChart: echarts.ECharts | null = null

// 趋势图数据
const trendData = ref({
  dates: Array.from({ length: 30 }, (_, i) => `${i + 1}日`),
  income: [8200, 9320, 9010, 9340, 12900, 13300, 13200, 12500, 11800, 12800, 13500, 12845, 14200, 15600, 14800, 15200, 13800, 14500, 15600, 14800, 16200, 15800, 14500, 15200, 16800, 16200, 17500, 18200, 17800, 18500],
  orders: [82, 93, 90, 93, 129, 133, 132, 125, 118, 128, 135, 128, 142, 156, 148, 152, 138, 145, 156, 148, 162, 158, 145, 152, 168, 162, 175, 182, 178, 185],
  compareIncome: [], // 对比数据
  compareOrders: []
})

// 初始化收入趋势图
const initTrendChart = () => {
  const chartDom = document.getElementById('trend-chart')
  if (!chartDom) return
  trendChart = echarts.init(chartDom)
  updateTrendChart()
}

// 更新趋势图
const updateTrendChart = () => {
  if (!trendChart) return

  const series: any[] = [
    {
      name: '收入',
      type: 'line',
      smooth: true,
      yAxisIndex: 0,
      data: trendData.value.income,
      itemStyle: { color: '#67c23a' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
          { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
        ])
      }
    },
    {
      name: '订单量',
      type: 'bar',
      yAxisIndex: 1,
      data: trendData.value.orders,
      itemStyle: { color: '#409eff' }
    }
  ]

  // 添加对比数据
  if (filterForm.value.compareType !== 'none' && trendData.value.compareIncome.length > 0) {
    series.push({
      name: filterForm.value.compareType === 'yoy' ? '去年同期' : '上期',
      type: 'line',
      smooth: true,
      yAxisIndex: 0,
      data: trendData.value.compareIncome,
      itemStyle: { color: '#909399' },
      lineStyle: { type: 'dashed' }
    })
  }

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' },
      formatter: function(params: any) {
        let result = params[0].name + '<br/>'
        params.forEach((item: any) => {
          const value = item.seriesName === '收入' || item.seriesName === '去年同期' || item.seriesName === '上期'
            ? '¥' + item.value.toLocaleString()
            : item.value
          result += `${item.marker} ${item.seriesName}: ${value}<br/>`
        })
        return result
      }
    },
    legend: { data: series.map(s => s.name), bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
    toolbox: {
      feature: {
        dataZoom: { yAxisIndex: 'none' },
        restore: {},
        saveAsImage: {}
      },
      right: 20
    },
    xAxis: {
      type: 'category',
      data: trendData.value.dates,
      axisLine: { lineStyle: { color: '#ccc' } }
    },
    yAxis: [
      {
        type: 'value',
        name: '收入',
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
    dataZoom: [
      { type: 'inside', start: 0, end: 100 },
      { start: 0, end: 100, handleSize: '80%' }
    ],
    series
  }
  trendChart?.setOption(option, true)
}

// 初始化收入构成饼图
const initCompositionChart = () => {
  const chartDom = document.getElementById('composition-chart')
  if (!chartDom) return
  compositionChart = echarts.init(chartDom)
  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: { orient: 'vertical', right: '5%', top: 'center' },
    toolbox: {
      feature: { saveAsImage: {} },
      right: 20
    },
    series: [{
      name: '收入构成',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false, position: 'center' },
      emphasis: { label: { show: true, fontSize: 20, fontWeight: 'bold' } },
      labelLine: { show: false },
      data: [
        { value: 235678, name: '处方药', itemStyle: { color: '#409eff' } },
        { value: 189234, name: '非处方药', itemStyle: { color: '#67c23a' } },
        { value: 125678, name: '保健品', itemStyle: { color: '#e6a23c' } },
        { value: 85678, name: '医疗器械', itemStyle: { color: '#f56c6c' } },
        { value: 48500, name: '其他', itemStyle: { color: '#909399' } }
      ]
    }]
  }
  compositionChart?.setOption(option)
}

// 初始化收入对比柱状图
const initCompareChart = () => {
  const chartDom = document.getElementById('compare-chart')
  if (!chartDom) return
  compareChart = echarts.init(chartDom)
  updateCompareChart()
}

// 更新对比图表
const updateCompareChart = () => {
  if (!compareChart) return

  const currentData = [235678, 189234, 125678, 85678, 45678, 48500]
  const compareData = filterForm.value.compareType === 'yoy'
    ? [210567, 175432, 112345, 78901, 42345, 45678] // 去年同期
    : [220345, 182567, 119876, 82345, 44567, 47890] // 上月

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: function(params: any) {
        let result = params[0].name + '<br/>'
        params.forEach((item: any) => {
          result += `${item.marker} ${item.seriesName}: ¥${item.value.toLocaleString()}<br/>`
        })
        // 计算增长率
        if (params.length === 2) {
          const growth = ((params[0].value - params[1].value) / params[1].value * 100).toFixed(1)
          const color = parseFloat(growth) >= 0 ? '#67c23a' : '#f56c6c'
          result += `<span style="color: ${color}">增长率: ${growth}%</span>`
        }
        return result
      }
    },
    legend: {
      data: ['本期', filterForm.value.compareType === 'yoy' ? '去年同期' : '上期'],
      bottom: 0
    },
    grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
    toolbox: {
      feature: { saveAsImage: {} },
      right: 20
    },
    xAxis: {
      type: 'category',
      data: ['处方药', '非处方药', '保健品', '医疗器械', '中药饮片', '其他'],
      axisLine: { lineStyle: { color: '#ccc' } }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: { formatter: '¥{value}' }
    },
    series: [
      {
        name: '本期',
        type: 'bar',
        data: currentData,
        itemStyle: { color: '#409eff' },
        label: { show: true, position: 'top', formatter: '¥{c}' }
      },
      {
        name: filterForm.value.compareType === 'yoy' ? '去年同期' : '上期',
        type: 'bar',
        data: compareData,
        itemStyle: { color: '#909399' },
        label: { show: true, position: 'top', formatter: '¥{c}' }
      }
    ]
  }
  compareChart?.setOption(option, true)
}

// 窗口大小改变时重新计算图表大小
const handleResize = () => {
  trendChart?.resize()
  compositionChart?.resize()
  compareChart?.resize()
}

// 搜索/筛选
const handleSearch = () => {
  // 模拟数据更新
  if (filterForm.value.compareType !== 'none') {
    // 生成对比数据
    trendData.value.compareIncome = trendData.value.income.map(v => Math.floor(v * (0.8 + Math.random() * 0.4)))
    trendData.value.compareOrders = trendData.value.orders.map(v => Math.floor(v * (0.8 + Math.random() * 0.4)))
  } else {
    trendData.value.compareIncome = []
    trendData.value.compareOrders = []
  }

  updateTrendChart()
  updateCompareChart()
  ElMessage.success('数据已更新')
}

// 重置筛选
const handleReset = () => {
  filterForm.value = {
    timeRange: '30',
    startDate: '',
    endDate: '',
    compareType: 'none',
    dimension: 'day'
  }
  trendData.value.compareIncome = []
  trendData.value.compareOrders = []
  updateTrendChart()
  updateCompareChart()
  ElMessage.success('已重置')
}

// 导出报表
const handleExport = () => {
  ElMessage.success('财务报表导出成功')
}

onMounted(() => {
  initTrendChart()
  initCompositionChart()
  initCompareChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  compositionChart?.dispose()
  compareChart?.dispose()
})

const getRankColor = (index: number): string => {
  const colors = ['#f56c6c', '#e6a23c', '#409eff', '#67c23a', '#909399']
  return index < 3 ? colors[index] : colors[4]
}

const formatNumber = (num: number): string => {
  return num.toLocaleString('zh-CN')
}
</script>

<template>
  <div class="finance-statistics">
    <!-- 筛选区域 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="filterForm" inline>
        <el-form-item label="时间范围">
          <el-radio-group v-model="filterForm.timeRange">
            <el-radio-button label="7">近7天</el-radio-button>
            <el-radio-button label="30">近30天</el-radio-button>
            <el-radio-button label="90">近90天</el-radio-button>
            <el-radio-button label="custom">自定义</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="filterForm.timeRange === 'custom'" label="自定义日期">
          <el-date-picker v-model="[filterForm.startDate, filterForm.endDate]" type="daterange" range-separator="至"
            start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="时间维度">
          <el-radio-group v-model="filterForm.dimension">
            <el-radio-button label="day">按日</el-radio-button>
            <el-radio-button label="week">按周</el-radio-button>
            <el-radio-button label="month">按月</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="对比分析">
          <el-select v-model="filterForm.compareType" style="width: 140px">
            <el-option label="不对比" value="none" />
            <el-option label="同比分析" value="yoy" />
            <el-option label="环比分析" value="mom" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="RefreshRight" @click="handleReset">重置</el-button>
          <el-button :icon="Download" @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-cards">
      <el-col :xs="24" :sm="12" :lg="6" v-for="card in statCards" :key="card.title">
        <el-card class="stat-card" shadow="hover">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">{{ card.title }}</div>
              <div class="card-value">{{ card.value }}</div>
              <div class="card-change" :class="card.trend">
                <el-icon v-if="card.trend === 'up'">
                  <ArrowUp />
                </el-icon>
                <el-icon v-else>
                  <ArrowDown />
                </el-icon>
                <span>{{ card.change }}</span>
                <span class="compare-text">{{ card.compareText }}</span>
              </div>
            </div>
            <div class="card-icon" :style="{ backgroundColor: card.color }">
              <el-icon :size="28" color="#fff">
                <component :is="card.icon" />
              </el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">收入趋势分析</span>
              <el-radio-group v-model="filterForm.compareType" size="small">
                <el-radio-button label="none">当前</el-radio-button>
                <el-radio-button label="yoy">同比</el-radio-button>
                <el-radio-button label="mom">环比</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div id="trend-chart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">收入构成</span>
            </div>
          </template>
          <div id="composition-chart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 对比图和排行 -->
    <el-row :gutter="16" class="bottom-row">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                {{ filterForm.compareType === 'yoy' ? '同比分析' : filterForm.compareType === 'mom' ? '环比分析' : '收入对比' }}
              </span>
            </div>
          </template>
          <div id="compare-chart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="rank-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">收入排行 TOP10</span>
              <el-tag size="small" type="info">实时</el-tag>
            </div>
          </template>
          <div class="rank-list">
            <div v-for="(item, index) in topProducts" :key="index" class="rank-item">
              <div class="rank-number" :class="{ 'top-three': index < 3 }">{{ index + 1 }}</div>
              <div class="rank-info">
                <div class="rank-name">{{ item.name }}</div>
                <el-progress :percentage="item.percent" :stroke-width="8" :show-text="false"
                  :color="getRankColor(index)" />
              </div>
              <div class="rank-amount">
                <div class="amount">¥{{ formatNumber(item.amount) }}</div>
                <div class="change" :class="item.change.startsWith('+') ? 'up' : 'down'">
                  {{ item.change }}
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped lang="scss">
.finance-statistics {
  padding: 0;
}

.filter-card {
  margin-bottom: 16px;

  :deep(.el-card__body) {
    padding: 16px;
  }

  .el-form-item {
    margin-bottom: 12px;
  }
}

.stat-cards {
  margin-bottom: 16px;
}

.stat-card {
  .card-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .card-info {
    .card-title {
      font-size: 14px;
      color: #909399;
      margin-bottom: 8px;
    }

    .card-value {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 8px;
    }

    .card-change {
      display: flex;
      align-items: center;
      font-size: 13px;

      &.up {
        color: #67c23a;
      }

      &.down {
        color: #f56c6c;
      }

      .el-icon {
        margin-right: 4px;
      }

      .compare-text {
        margin-left: 4px;
        color: #909399;
      }
    }
  }

  .card-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 56px;
    height: 56px;
    border-radius: 8px;
  }
}

.chart-row {
  margin-bottom: 16px;
}

.chart-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .card-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }

  .chart-container {
    height: 320px;
  }
}

.bottom-row {
  margin-bottom: 16px;
}

.rank-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }

  .rank-list {
    max-height: 320px;
    overflow-y: auto;
  }

  .rank-item {
    display: flex;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #ebeef5;

    &:last-child {
      border-bottom: none;
    }

    .rank-number {
      width: 28px;
      height: 28px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;
      font-size: 14px;
      font-weight: 600;
      color: #909399;
      background: #f5f7fa;
      margin-right: 12px;

      &.top-three {
        color: #fff;

        &:nth-child(1) {
          background: #f56c6c;
        }

        &:nth-child(2) {
          background: #e6a23c;
        }

        &:nth-child(3) {
          background: #409eff;
        }
      }
    }

    .rank-info {
      flex: 1;
      margin-right: 12px;

      .rank-name {
        font-size: 14px;
        color: #303133;
        margin-bottom: 6px;
      }
    }

    .rank-amount {
      text-align: right;

      .amount {
        font-size: 14px;
        font-weight: 600;
        color: #f56c6c;
      }

      .change {
        font-size: 12px;
        margin-top: 2px;

        &.up {
          color: #67c23a;
        }

        &.down {
          color: #f56c6c;
        }
      }
    }
  }
}
</style>
