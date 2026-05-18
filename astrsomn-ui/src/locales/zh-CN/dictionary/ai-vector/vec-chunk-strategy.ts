/**
 * 向量切片策略枚举 - 与后端 AiVecChunkStrategyEnum 对齐（中文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const vecChunkStrategyLabels = {
    FIXED: '固定长度',
    SENTENCE: '句子分割',
    PARAGRAPH: '段落分割',
    PAGE: '页面分割'
} as const

export const aiVecChunkStrategyDictionary = createEnumDictionary({
    id: 'ai-vec.chunkStrategy',
    labels: vecChunkStrategyLabels,
    order: ['FIXED', 'SENTENCE', 'PARAGRAPH', 'PAGE']
})
