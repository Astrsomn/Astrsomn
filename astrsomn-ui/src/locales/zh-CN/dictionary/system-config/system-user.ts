/**
 * 系统用户枚举 - 与后端 SystemUserEnum 对齐（中文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const userRoleLabels = {
    SUPER_ADMIN: '超级管理员',
    ENV_ADMIN: '环境管理员',
    USER: '普通用户'
} as const

export const systemUserRoleDictionary = createEnumDictionary({
    id: 'system.user.role',
    labels: userRoleLabels,
    order: ['SUPER_ADMIN', 'ENV_ADMIN', 'USER']
})

const adminLabels = {
    Y: '是',
    N: '否'
} as const

export const systemUserAdminDictionary = createEnumDictionary({
    id: 'system.user.admin',
    labels: adminLabels,
    order: ['Y', 'N']
})
