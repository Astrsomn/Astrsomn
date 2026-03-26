import { computed, type ComputedRef, ref, type Ref } from 'vue'
import type { EnumDictionary } from './core'
import {
  aiModelCapabilitiesDictionary as aiModelCapabilitiesEn,
  aiModelProviderDictionary as aiModelProviderEn,
  aiModelStatusDictionary as aiModelStatusEn
} from '../en-US/dictionary/ai-model'
import {
  aiModelCapabilitiesDictionary as aiModelCapabilitiesZh,
  aiModelProviderDictionary as aiModelProviderZh,
  aiModelStatusDictionary as aiModelStatusZh
} from '../zh-CN/dictionary/ai-model'

/** 与 `bundles`、useLanguage 的 Lang 保持一致 */
export type DictionaryLocale = 'zh-CN' | 'en-US'

/** 各语言文案不同，统一用宽类型，避免中英字面量不兼容 */
export type DictionaryBundle = {
  'ai-model.provider': EnumDictionary<Record<string, string>>
  'ai-model.status': EnumDictionary<Record<string, string>>
  'ai-model.capabilities': EnumDictionary<Record<string, string>>
}

export type DictionaryId = keyof DictionaryBundle

const zhCNDictionaryBundle = {
  'ai-model.provider': aiModelProviderZh,
  'ai-model.status': aiModelStatusZh,
  'ai-model.capabilities': aiModelCapabilitiesZh
} satisfies DictionaryBundle

const enUSDictionaryBundle = {
  'ai-model.provider': aiModelProviderEn,
  'ai-model.status': aiModelStatusEn,
  'ai-model.capabilities': aiModelCapabilitiesEn
} satisfies DictionaryBundle

const bundles: Record<DictionaryLocale, DictionaryBundle> = {
  'zh-CN': zhCNDictionaryBundle,
  'en-US': enUSDictionaryBundle
}

/** 与 `useLanguage` 共用 key，避免首屏字典与顶栏语言不一致 */
const LANG_STORAGE_KEY = 'lang'

function readStoredLang(): DictionaryLocale {
  try {
    const raw = localStorage.getItem(LANG_STORAGE_KEY)
    if (raw === 'en-US' || raw === 'zh-CN') return raw
  } catch {
    /* SSR 或无 storage */
  }
  return 'zh-CN'
}

export const dictionaryLocale: Ref<DictionaryLocale> = ref(readStoredLang())

export function setDictionaryLocale(locale: DictionaryLocale) {
  dictionaryLocale.value = locale
}

export function getDictionaryLocale(): DictionaryLocale {
  return dictionaryLocale.value
}

export function getDictionary<T extends DictionaryId>(id: T): DictionaryBundle[T] {
  return bundles[dictionaryLocale.value][id]
}

export function getDictionaryBundle(locale: DictionaryLocale): DictionaryBundle {
  return bundles[locale]
}

export function useDictionary<T extends DictionaryId>(id: T): ComputedRef<DictionaryBundle[T]> {
  return computed(() => getDictionary(id))
}
