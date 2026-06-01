import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const configStatusLabels = {
    ENABLED: '启用',
    DISABLED: '禁用'
} as const

export const systemConfigStatusDictionary = createEnumDictionary({
    id: 'system.config.status',
    labels: configStatusLabels,
    order: ['ENABLED', 'DISABLED']
})

const configIsSystemLabels = {
    Y: '系统内置',
    N: '自定义'
} as const

export const systemConfigIsSystemDictionary = createEnumDictionary({
    id: 'system.config.isSystem',
    labels: configIsSystemLabels,
    order: ['Y', 'N']
})
