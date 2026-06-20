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
        // ── ActivityBar 目录介绍（登录后进入管理后台首页展示）──
        {
            id: 'activity-logo',
            element: '[data-guide="activity-logo"]',
            popover: {
                title: 'Astrsomn 首页',
                description: '点击 Logo 可随时返回聊天首页，与 AI 进行对话',
                position: 'right'
            }
        },
        {
            id: 'activity-ai-config',
            element: '[data-guide="activity-ai-config"]',
            popover: {
                title: 'AI 配置中心',
                description: '管理智能体、模型接入、AI 账号、提示词、MCP 工具等 AI 核心配置',
                position: 'right'
            }
        },
        {
            id: 'activity-vector',
            element: '[data-guide="activity-vector"]',
            popover: {
                title: '向量中心',
                description: '管理向量存储与检索配置，为 RAG 知识库提供底层能力支撑',
                position: 'right'
            }
        },
        {
            id: 'activity-settings',
            element: '[data-guide="activity-settings"]',
            popover: {
                title: '系统设置',
                description: '管理系统用户、环境、扩展、系统参数等全局配置',
                position: 'right'
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
