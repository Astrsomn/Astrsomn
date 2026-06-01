import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const configStatusLabels = {
    ENABLED: 'Enabled',
    DISABLED: 'Disabled'
} as const

export const systemConfigStatusDictionary = createEnumDictionary({
    id: 'system.config.status',
    labels: configStatusLabels,
    order: ['ENABLED', 'DISABLED']
})

const configIsSystemLabels = {
    Y: 'Built-in',
    N: 'Custom'
} as const

export const systemConfigIsSystemDictionary = createEnumDictionary({
    id: 'system.config.isSystem',
    labels: configIsSystemLabels,
    order: ['Y', 'N']
})
