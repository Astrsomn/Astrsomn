<template>
  <BaseEChart :option="option" min-height="260px" />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { EChartsOption } from 'echarts'
import { useTheme } from '@/composables/useTheme.ts'
import BaseEChart from './BaseEChart.vue'

const { isDark } = useTheme()

const data = [
  { value: 420, name: 'GPT-4o' },
  { value: 310, name: 'Claude 3.5' },
  { value: 180, name: 'DeepSeek V3' },
  { value: 95, name: 'Qwen 2.5' },
  { value: 65, name: '其他' },
]

const option = computed<EChartsOption>(() => {
  const textColor = isDark.value ? '#9ca3af' : '#6b7280'
  const bgColor = isDark.value ? '#121e2d' : '#ffffff'

  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: {
      orient: 'vertical',
      right: 12,
      top: 'center',
      textStyle: { color: textColor, fontSize: 11 },
    },
    series: [
      {
        type: 'pie',
        radius: ['42%', '70%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 6, borderColor: bgColor, borderWidth: 2 },
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: 'bold', color: textColor },
        },
        data,
      },
    ],
    color: ['#3b82f6', '#8b5cf6', '#10b981', '#f59e0b', '#6b7280'],
  }
})
</script>
