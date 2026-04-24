/**
 * 与后端 `AiAgentEnum` 各枚举的 code 一一对应（英文文案）。
 */
import { createEnumDictionary } from '@/locales/dictionary/core.ts'

const statusLabels = {
  enabled: 'Enabled',
  disabled: 'Disabled'
} as const

export const aiAgentStatusDictionary = createEnumDictionary({
  id: 'ai-agent.status',
  labels: statusLabels,
  order: ['enabled', 'disabled'],
  caseInsensitive: true
})

const memoryModeLabels = {
  shortTerm: 'Short Term',
  longTerm: 'Long Term',
  hybrid: 'Hybrid'
} as const

export const aiAgentMemoryModeDictionary = createEnumDictionary({
  id: 'ai-agent.memoryMode',
  labels: memoryModeLabels,
  order: ['shortTerm', 'longTerm', 'hybrid']
})

const isDefaultLabels = {
  Y: 'Yes',
  N: 'No'
} as const

export const aiAgentIsDefaultDictionary = createEnumDictionary({
  id: 'ai-agent.isDefault',
  labels: isDefaultLabels,
  order: ['Y', 'N']
})
