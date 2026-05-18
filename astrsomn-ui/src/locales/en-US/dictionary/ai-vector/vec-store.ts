/**
 * 向量存储库枚举 - 与后端向量库相关枚举对齐（英文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const vecStoreStatusLabels = {
    enabled: 'Enabled',
    disabled: 'Disabled'
} as const

export const aiVecStoreStatusDictionary = createEnumDictionary({
    id: 'ai-vec.store.status',
    labels: vecStoreStatusLabels,
    order: ['enabled', 'disabled'],
    caseInsensitive: true
})
