export interface AppConfig {
  name: string
  version: string
  buildTime: string
  defaultLanguage: string
  defaultTheme: 'light' | 'dark'
  bottomNavAutoHide: boolean
}

export const appConfig: AppConfig = {
  name: 'Astrsomn',
  version: '0.2.0-SNAPSHOT',
  buildTime: '2026-03-01',
  defaultLanguage: 'zh-CN',
  defaultTheme: 'light',
  bottomNavAutoHide: true
}

export const getVersionText = (): string => {
  return `Astrsomn ${appConfig.version}`
}
