<template>
  <div ref="chartContainer" class="pie-chart-container"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'

// 图表容器引用
const chartContainer = ref(null)
// ECharts 实例
let chartInstance = null

// 图表配置
const chartOption = {
  title: {
    text: '任务完成比例',
    left: 'center'
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left',
    data: ['完成任务', '拖延任务']
  },
  series: [
    {
      name: '任务比例',
      type: 'pie',
      radius: '55%',
      center: ['50%', '60%'],
      data: [
        { value: 0, name: '完成任务' },
        { value: 0, name: '拖延任务' }
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      },
      label: {
        formatter: '{b}: {c} ({d}%)'
      }
    }
  ]
}

// 获取数据
const fetchData = async () => {
  try {
    const response = await axios.get('/stats/ratio')
    const data = response.data

    // 假设后端返回的数据格式为：
    // {
    //   completed: 70,  // 完成任务数量
    //   procrastinated: 30  // 拖延任务数量
    // }
    chartOption.series[0].data = [
      { value: data.completed || 0, name: '完成任务' },
      { value: data.procrastinated || 0, name: '拖延任务' }
    ]

    // 更新图表
    if (chartInstance) {
      chartInstance.setOption(chartOption)
    }
  } catch (error) {
    console.error('获取饼图数据失败:', error)
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
.pie-chart-container {
  width: 100%;
  height: 400px;
}
</style>