/**
 * 通用枚举字典（中文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const commonStatusLabels = {
    enabled: '启用',
    disabled: '禁用'
} as const

export const commonStatusDictionary = createEnumDictionary({
    id: 'common.status',
    labels: commonStatusLabels,
    order: ['enabled', 'disabled'],
    caseInsensitive: true
})

const booleanLabels = {
    true: '是',
    false: '否'
} as const

export const commonBooleanDictionary = createEnumDictionary({
    id: 'common.boolean',
    labels: booleanLabels,
    order: ['true', 'false']
})

const yesNoLabels = {
    Y: '是',
    N: '否'
} as const

export const commonYesNoDictionary = createEnumDictionary({
    id: 'common.yesNo',
    labels: yesNoLabels,
    order: ['Y', 'N']
})
