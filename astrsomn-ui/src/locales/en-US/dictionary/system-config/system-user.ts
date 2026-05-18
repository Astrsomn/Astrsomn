/**
 * 系统用户枚举 - 与后端 SystemUserEnum 对齐（英文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const userRoleLabels = {
    SUPER_ADMIN: 'Super Admin',
    ENV_ADMIN: 'Environment Admin',
    USER: 'User'
} as const

export const systemUserRoleDictionary = createEnumDictionary({
    id: 'system.user.role',
    labels: userRoleLabels,
    order: ['SUPER_ADMIN', 'ENV_ADMIN', 'USER']
})

const adminLabels = {
    Y: 'Yes',
    N: 'No'
} as const

export const systemUserAdminDictionary = createEnumDictionary({
    id: 'system.user.admin',
    labels: adminLabels,
    order: ['Y', 'N']
})
