<template>
  <div
    ref="chartRef"
    class="base-echart"
    :style="{
      height,
      minHeight,
    }"
  ></div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref, watch } from 'vue'
import * as echarts from 'echarts'
import type { ECharts, EChartsOption } from 'echarts'

const props = withDefaults(
  defineProps<{
    option: EChartsOption
    height?: string
    minHeight?: string
  }>(),
  {
    height: '100%',
    minHeight: '220px',
  },
)

const chartRef = ref<HTMLElement>()

let chart: ECharts | null = null
let resizeObserver: ResizeObserver | null = null

const renderChart = () => {
  if (!chart) return
  chart.setOption(props.option, {
    notMerge: true,
    lazyUpdate: true,
  })
}

onMounted(() => {
  if (!chartRef.value) return

  chart = echarts.init(chartRef.value)
  renderChart()

  resizeObserver = new ResizeObserver(() => {
    chart?.resize()
  })
  resizeObserver.observe(chartRef.value)
})

onUnmounted(() => {
  resizeObserver?.disconnect()
  chart?.dispose()
})

watch(
  () => props.option,
  () => {
    renderChart()
  },
  { deep: true },
)
</script>

<style scoped>
.base-echart {
  width: 100%;
}
</style>
