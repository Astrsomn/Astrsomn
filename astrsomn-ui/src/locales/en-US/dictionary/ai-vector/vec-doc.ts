/**
 * 向量文档状态枚举 - 与后端 AiVecDocEnum.SyncStatus 对齐（英文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const vecDocSyncStatusLabels = {
    PENDING: 'Pending',
    CHUNKING: 'Chunking',
    CHUNKED: 'Chunked',
    VECTORING: 'Vectorizing',
    STORED: 'Stored',
    FAILED: 'Failed',
    INVALID: 'Invalid'
} as const

export const aiVecDocSyncStatusDictionary = createEnumDictionary({
    id: 'ai-vec.doc.syncStatus',
    labels: vecDocSyncStatusLabels,
    order: ['PENDING', 'CHUNKING', 'CHUNKED', 'VECTORING', 'STORED', 'FAILED', 'INVALID'],
    caseInsensitive: true
})
