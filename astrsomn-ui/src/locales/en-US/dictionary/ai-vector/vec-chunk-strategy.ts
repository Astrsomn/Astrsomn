/**
 * 向量切片策略枚举 - 与后端 AiVecChunkStrategyEnum 对齐（英文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const vecChunkStrategyLabels = {
    FIXED: 'Fixed Length',
    SENTENCE: 'Sentence Splitting',
    PARAGRAPH: 'Paragraph Splitting',
    PAGE: 'Page Splitting'
} as const

export const aiVecChunkStrategyDictionary = createEnumDictionary({
    id: 'ai-vec.chunkStrategy',
    labels: vecChunkStrategyLabels,
    order: ['FIXED', 'SENTENCE', 'PARAGRAPH', 'PAGE']
})
