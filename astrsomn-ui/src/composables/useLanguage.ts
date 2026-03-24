import { ref } from 'vue'

type Lang = 'zh-CN' | 'en-US'

const STORAGE_KEY = 'lang'

const currentLang = ref<Lang>('zh-CN')

function initLang() {
  const stored = localStorage.getItem(STORAGE_KEY) as Lang | null
  if (stored) {
    currentLang.value = stored
  }
}

function changeLang(lang: Lang) {
  currentLang.value = lang
  localStorage.setItem(STORAGE_KEY, lang)
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
