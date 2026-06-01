/**
 * 系统管理中心枚举 — 仪表盘业务系统状态、环境类型 (中文文案)
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const bizStatusLabels = {
    ONLINE: '在线',
    OFFLINE: '离线',
    MAINTENANCE: '维护中'
} as const

export const systemConfigCenterStatusDictionary = createEnumDictionary({
    id: 'system.config-center.status',
    labels: bizStatusLabels,
    order: ['ONLINE', 'OFFLINE', 'MAINTENANCE']
})

const bizEnvLabels = {
    PROD: '生产环境',
    PRE: '预生产环境',
    TEST: '测试环境',
    DEV: '开发环境'
} as const

export const systemConfigCenterEnvDictionary = createEnumDictionary({
    id: 'system.config-center.env',
    labels: bizEnvLabels,
    order: ['PROD', 'PRE', 'TEST', 'DEV']
})
