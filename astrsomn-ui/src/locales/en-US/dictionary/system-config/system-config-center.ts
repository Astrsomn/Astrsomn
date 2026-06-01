/**
 * System Config Center enums — dashboard business system status / environment (English)
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const bizStatusLabels = {
    ONLINE: 'Online',
    OFFLINE: 'Offline',
    MAINTENANCE: 'Maintenance'
} as const

export const systemConfigCenterStatusDictionary = createEnumDictionary({
    id: 'system.config-center.status',
    labels: bizStatusLabels,
    order: ['ONLINE', 'OFFLINE', 'MAINTENANCE']
})

const bizEnvLabels = {
    PROD: 'Production',
    PRE: 'Staging',
    TEST: 'Testing',
    DEV: 'Development'
} as const

export const systemConfigCenterEnvDictionary = createEnumDictionary({
    id: 'system.config-center.env',
    labels: bizEnvLabels,
    order: ['PROD', 'PRE', 'TEST', 'DEV']
})
