import {ref, type Ref, computed, type ComputedRef, watch} from 'vue'
import {chatIndexPage as chatIndexZh} from './zh-CN/pages/chat-index'
import {chatIndexPage as chatIndexEn} from './en-US/pages/chat-index'
import {aiConfigCenterTranslation as aiConfigCenterZh} from './zh-CN/pages/ai-config-center'
import {aiConfigCenterTranslation as aiConfigCenterEn} from './en-US/pages/ai-config-center'
import {loginPageTranslation as loginZh} from './zh-CN/pages/login'
import {loginPageTranslation as loginEn} from './en-US/pages/login'
import {appTranslation as appZh} from './zh-CN/pages/app'
import {appTranslation as appEn} from './en-US/pages/app'
import {getDictionaryLocale, setDictionaryLocale, type DictionaryLocale} from './dictionary/registry'

export type PageTranslationBundle = {
  'chat-index': typeof chatIndexZh
  'ai-config-center': typeof aiConfigCenterZh
  'login': typeof loginZh
  'app': typeof appZh
}

const bundles: Record<DictionaryLocale, PageTranslationBundle> = {
  'zh-CN': {
    'chat-index': chatIndexZh,
    'ai-config-center': aiConfigCenterZh,
    'login': loginZh,
    'app': appZh
  },
  'en-US': {
    'chat-index': chatIndexEn,
    'ai-config-center': aiConfigCenterEn,
    'login': loginEn,
    'app': appEn
  }
}

const currentLocale: Ref<DictionaryLocale> = ref(getDictionaryLocale())

export function getPageTranslation<T extends keyof PageTranslationBundle>(pageId: T): PageTranslationBundle[T] {
  return bundles[currentLocale.value][pageId]
}

export function usePageTranslation<T extends keyof PageTranslationBundle>(pageId: T): ComputedRef<PageTranslationBundle[T]> {
  return computed(() => bundles[currentLocale.value][pageId])
}

export function syncPageTranslationLocale(locale: DictionaryLocale) {
  currentLocale.value = locale
}

watch(
  () => getDictionaryLocale(),
  (newLocale) => {
    currentLocale.value = newLocale
  }
)