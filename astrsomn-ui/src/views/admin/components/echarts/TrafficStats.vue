<template>
  <div ref="chartRef" class="chart-fill"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'
import { useTheme } from '@/composables/useTheme.ts'

const chartRef = ref<HTMLElement>()
let chart: echarts.ECharts | null = null
let ro: ResizeObserver | null = null
const { isDark } = useTheme()

const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
const requestData = [1240, 1890, 2150, 1760, 2340, 1980, 2560]
const tokenData = [18600, 24300, 31200, 22800, 29400, 26100, 35800]

function getOption(): echarts.EChartsOption {
  const textColor = isDark.value ? '#9ca3af' : '#6b7280'
  const lineColor = isDark.value ? 'rgba(255,255,255,0.06)' : 'rgba(0,0,0,0.06)'
  return {
    tooltip: { trigger: 'axis' },
    legend: { top: 0, right: 0, textStyle: { color: textColor, fontSize: 11 } },
    grid: { top: 36, left: 48, right: 16, bottom: 28 },
    xAxis: {
      type: 'category',
      data: days,
      axisLine: { lineStyle: { color: lineColor } },
      axisLabel: { color: textColor, fontSize: 11 }
    },
    yAxis: [
      {
        type: 'value',
        name: '请求数',
        nameTextStyle: { color: textColor, fontSize: 10 },
        splitLine: { lineStyle: { color: lineColor } },
        axisLabel: { color: textColor, fontSize: 10 }
      },
      {
        type: 'value',
        name: 'Token 消耗',
        nameTextStyle: { color: textColor, fontSize: 10 },
        splitLine: { show: false },
        axisLabel: { color: textColor, fontSize: 10 }
      }
    ],
    series: [
      {
        name: '请求数',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: requestData,
        itemStyle: { color: '#3b82f6' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(59,130,246,0.25)' },
            { offset: 1, color: 'rgba(59,130,246,0.02)' }
          ])
        }
      },
      {
        name: 'Token 消耗',
        type: 'bar',
        yAxisIndex: 1,
        barWidth: 18,
        data: tokenData,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(16,185,129,0.7)' },
            { offset: 1, color: 'rgba(16,185,129,0.15)' }
          ]),
          borderRadius: [4, 4, 0, 0]
        }
      }
    ]
  }
}

onMounted(() => {
  if (!chartRef.value) return
  chart = echarts.init(chartRef.value)
  chart.setOption(getOption())
  ro = new ResizeObserver(() => chart?.resize())
  ro.observe(chartRef.value)
})

onUnmounted(() => {
  ro?.disconnect()
  chart?.dispose()
})

watch(isDark, () => chart?.setOption(getOption()))
</script>

<style scoped>
.chart-fill {
  width: 100%;
  height: 100%;
  min-height: 180px;
}
</style>
