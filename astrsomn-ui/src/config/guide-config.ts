export interface GuideStep {
  id: string
  element: string
  popover: {
    title: string
    description: string
    position?: 'top' | 'bottom' | 'left' | 'right' | 'top-center' | 'bottom-center' | 'left-center' | 'right-center' | 'mid-center'
  }
}

export interface GuideConfig {
  enabled: boolean
  autoStart: boolean
  steps: GuideStep[]
}

export const guideConfig: GuideConfig = {
  enabled: true,
  autoStart: true,
  steps: [
    {
      id: 'install-plugin',
      element: '.add-plugin-btn',
      popover: {
        title: '安装插件',
        description: '点击此处开始安装插件，扩展系统功能',
        position: 'bottom'
      }
    }
  ]
}

export const GUIDE_STORAGE_KEY = 'astrsomn_guide_completed'

export const isGuideCompleted = (): boolean => {
  return localStorage.getItem(GUIDE_STORAGE_KEY) === 'true'
}

export const setGuideCompleted = (): void => {
  localStorage.setItem(GUIDE_STORAGE_KEY, 'true')
}

export const resetGuide = (): void => {
  localStorage.removeItem(GUIDE_STORAGE_KEY)
}
