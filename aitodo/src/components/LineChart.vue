<template>
  <div ref="chartContainer" class="line-chart-container"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'

// 图表容器引用
const chartContainer = ref(null)
// ECharts 实例
let chartInstance = null

// 图表配置
const chartOption = {
  title: {
    text: '任务完成与拖延趋势',
    left: 'center'
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross'
    }
  },
  legend: {
    data: ['完成任务数', '拖延次数'],
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
    boundaryGap: false,
    name: '日期',
    data: [] // 将从后端获取的日期数据填充到这里
  },
  yAxis: {
    type: 'value',
    name: '数量'
  },
  series: [
    {
      name: '完成任务数',
      type: 'line',
      smooth: true,
      data: [] // 将从后端获取的完成任务数填充到这里
    },
    {
      name: '拖延次数',
      type: 'line',
      smooth: true,
      data: [] // 将从后端获取的拖延次数填充到这里
    }
  ]
}

// 获取数据
const fetchData = async () => {
  try {
    const response = await axios.get('/stats/daily')
    const data = response.data.data || response.data

    // 假设后端返回的数据格式为：
    // {
    //   dates: ['2024-01-01', '2024-01-02', ...],
    //   completedTasks: [5, 8, ...],
    //   procrastinationCounts: [2, 1, ...]
    // }
    chartOption.xAxis.data = data.dates || []
    chartOption.series[0].data = data.completedTasks || []
    chartOption.series[1].data = data.procrastinationCounts || []

    // 更新图表
    if (chartInstance) {
      chartInstance.setOption(chartOption)
    }
  } catch (error) {
    console.error('获取折线图数据失败:', error)
  }
}

// 初始化图表
const initChart = () => {
  if (chartContainer.value) {
    chartInstance = echarts.init(chartContainer.value)
    chartInstance.setOption(chartOption)

    // 监听窗口大小变化，重新调整图表大小
    const resizeObserver = new ResizeObserver(() => {
      if (chartInstance) {
        chartInstance.resize()
      }
    })
    resizeObserver.observe(chartContainer.value)

    // 在组件卸载时断开监听
    onUnmounted(() => {
      resizeObserver.disconnect()
    })
  }
}

// 组件挂载时初始化图表并获取数据
onMounted(() => {
  initChart()
  fetchData()
})

// 组件卸载时销毁图表实例
onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})

// 暴露方法供父组件调用
defineExpose({
  refreshData: fetchData
})
</script>

<style scoped>
/* 样式由用户自行实现 */
.line-chart-container {
  width: 100%;
  height: 400px;
}
</style>