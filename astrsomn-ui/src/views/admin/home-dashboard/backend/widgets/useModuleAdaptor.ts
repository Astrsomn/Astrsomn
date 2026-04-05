import { computed } from 'vue'
import { gridColTier, gridRowTier } from '../dashboardSizeTier'

export type DisplayTier = 0 | 1 | 2 | 3
export type LayoutMode = 'landscape' | 'portrait' | 'square'

export interface ModuleAdaptorResult {
  tier: DisplayTier
  layoutMode: LayoutMode
  isIconOnly: boolean
  showDesc: boolean
  showMetrics: boolean
  showFootnote: boolean
  maxMetrics: number
  isWideLayout: boolean
  isTallLayout: boolean
}

export function useModuleAdaptor(props: { gridW: number; gridH: number }) {
  const tier = computed((): DisplayTier => {
    // 限制最大尺寸为3x3
    const maxW = Math.min(3, props.gridW)
    const maxH = Math.min(3, props.gridH)
    
    const rt = gridRowTier(maxH)
    const ct = gridColTier(maxW)
    
    // Tier 0: 最小单元格 (1x1)
    if (rt === 1 && ct === 1) return 0
    
    // Tier 1: 紧凑布局 (1x2, 2x1, 2x2以下)
    if ((rt === 1 && ct <= 2) || (ct === 1 && rt <= 2)) return 1
    
    // Tier 2: 标准布局 (2x2, 3x2, 2x3等)
    if (rt <= 2 && ct <= 2) return 2
    
    // Tier 3: 完整布局 (更大的尺寸)
    return 3
  })

  const layoutMode = computed((): LayoutMode => {
    // 限制最大尺寸为3x3
    const maxW = Math.min(3, props.gridW)
    const maxH = Math.min(3, props.gridH)
    
    if (maxW >= maxH * 2) return 'landscape'  // 横向长条
    if (maxH >= maxW * 1.5) return 'portrait' // 纵向长条
    return 'square'                           // 正方形
  })

  const isWideLayout = computed(() => layoutMode.value === 'landscape')
  const isTallLayout = computed(() => layoutMode.value === 'portrait')
  const isIconOnly = computed(() => tier.value === 0)
  const showDesc = computed(() => tier.value >= 2)
  const showMetrics = computed(() => tier.value >= 2)
  const showFootnote = computed(() => tier.value >= 3)
  const maxMetrics = computed(() => tier.value === 2 ? 2 : 4)

  return {
    tier,
    layoutMode,
    isIconOnly,
    showDesc,
    showMetrics,
    showFootnote,
    maxMetrics,
    isWideLayout,
    isTallLayout
  }
}