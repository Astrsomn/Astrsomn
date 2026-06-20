import {ref} from 'vue'

type Theme = 'dark' | 'light'

const STORAGE_KEY = 'theme'

const isDark = ref(false)

function applyTheme() {
    const root = document.documentElement
    if (isDark.value) {
        root.classList.remove('light')
    } else {
        root.classList.add('light')
    }
}

function initTheme() {
    const stored = localStorage.getItem(STORAGE_KEY) as Theme | null
    if (stored) {
        isDark.value = stored === 'dark'
    } else {
        // 默认亮色主题
        isDark.value = false
        localStorage.setItem(STORAGE_KEY, 'light')
    }
    applyTheme()
}

function toggleTheme(val?: boolean) {
    isDark.value = val ?? !isDark.value
    localStorage.setItem(STORAGE_KEY, isDark.value ? 'dark' : 'light')
    applyTheme()
}

let initialized = false

export function useTheme() {
    if (!initialized) {
        initTheme()
        initialized = true
    }
    return {isDark, toggleTheme}
}
