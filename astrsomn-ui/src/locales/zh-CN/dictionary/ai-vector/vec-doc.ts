/**
 * 向量文档状态枚举 - 与后端 AiVecDocEnum.SyncStatus 对齐（中文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const vecDocSyncStatusLabels = {
    PENDING: '待向量化',
    CHUNKING: '切片中',
    CHUNKED: '已切片',
    VECTORING: '向量化中',
    STORED: '已入库',
    FAILED: '失败',
    INVALID: '已失效'
} as const

export const aiVecDocSyncStatusDictionary = createEnumDictionary({
    id: 'ai-vec.doc.syncStatus',
    labels: vecDocSyncStatusLabels,
    order: ['PENDING', 'CHUNKING', 'CHUNKED', 'VECTORING', 'STORED', 'FAILED', 'INVALID'],
    caseInsensitive: true
})
