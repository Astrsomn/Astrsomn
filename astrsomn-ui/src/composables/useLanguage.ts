import { ref } from 'vue'
import { setDictionaryLocale, type DictionaryLocale } from '@/locales/dictionary'

type Lang = DictionaryLocale

const STORAGE_KEY = 'lang'

const currentLang = ref<Lang>('zh-CN')

function syncDictionaryLocale(lang: Lang) {
  setDictionaryLocale(lang)
}

function initLang() {
  const stored = localStorage.getItem(STORAGE_KEY) as Lang | null
  if (stored) {
    currentLang.value = stored
  }
  syncDictionaryLocale(currentLang.value)
}

function changeLang(lang: Lang) {
  currentLang.value = lang
  localStorage.setItem(STORAGE_KEY, lang)
  syncDictionaryLocale(lang)
}

const languageOptions = [
  { label: '简体中文', value: 'zh-CN' },
  { label: 'English', value: 'en-US' }
]

let initialized = false

export function useLanguage() {
  if (!initialized) {
    initLang()
    initialized = true
  }
  return { currentLang, changeLang, languageOptions }
}
