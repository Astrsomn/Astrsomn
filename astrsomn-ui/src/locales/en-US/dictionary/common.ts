/**
 * 通用枚举字典（英文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const commonStatusLabels = {
    enabled: 'Enabled',
    disabled: 'Disabled'
} as const

export const commonStatusDictionary = createEnumDictionary({
    id: 'common.status',
    labels: commonStatusLabels,
    order: ['enabled', 'disabled'],
    caseInsensitive: true
})

const booleanLabels = {
    true: 'Yes',
    false: 'No'
} as const

export const commonBooleanDictionary = createEnumDictionary({
    id: 'common.boolean',
    labels: booleanLabels,
    order: ['true', 'false']
})

const yesNoLabels = {
    Y: 'Yes',
    N: 'No'
} as const

export const commonYesNoDictionary = createEnumDictionary({
    id: 'common.yesNo',
    labels: yesNoLabels,
    order: ['Y', 'N']
})
