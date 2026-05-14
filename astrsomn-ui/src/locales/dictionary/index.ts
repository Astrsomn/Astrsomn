/**
 * 枚举字典入口：`code` ↔ 展示文案，与后端枚举 `code`/`desc` 对齐。
 *
 * 结构：`core`（工厂）+ `registry`（当前语言与各业务字典聚合）+ `zh-CN/dictionary/*`（各域文案数据）。
 */
export type {EnumDictionary, DictionarySelectOption, CreateEnumDictionaryConfig} from './core'
export {createEnumDictionary} from './core'

export type {DictionaryLocale, DictionaryId, DictionaryBundle} from './registry'
export {
    dictionaryLocale,
    setDictionaryLocale,
    getDictionaryLocale,
    getDictionary,
    getDictionaryBundle,
    useDictionary
} from './registry'
