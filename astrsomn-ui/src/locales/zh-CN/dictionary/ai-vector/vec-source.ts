/**
 * 向量数据源枚举 - 与后端相关枚举对齐（中文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const vecSourceStatusLabels = {
    enabled: '启用',
    disabled: '禁用'
} as const

export const aiVecSourceStatusDictionary = createEnumDictionary({
    id: 'ai-vec.source.status',
    labels: vecSourceStatusLabels,
    order: ['enabled', 'disabled'],
    caseInsensitive: true
})
