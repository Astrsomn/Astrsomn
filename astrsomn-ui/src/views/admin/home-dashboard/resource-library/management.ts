import { markRaw } from 'vue'
import type { Component } from 'vue'
import {
    TeamOutlined,
    ContainerOutlined,
    SettingOutlined,

    FileTextOutlined,
    UserOutlined,
    CloudServerOutlined,
    CodeOutlined,
    SecurityScanOutlined,
    LineChartOutlined,
    NodeIndexOutlined,
    BuildOutlined,
    ControlOutlined,
    SafetyOutlined,
    DatabaseOutlined,
    PartitionOutlined,
    FileSearchOutlined,
    CloudUploadOutlined,
} from '@ant-design/icons-vue'

export type EntryAccent =
    | 'primary'
    | 'cyan'
    | 'blue'
    | 'sky'
    | 'mint'
    | 'coral'
    | 'primary-light'
    | 'indigo'
    | 'ocean'
    | 'violet'
    | 'teal'
    | 'frost'
    | 'brand'

export type ManagementEntry = {
    key: string
    label: string
    description: string
    icon: Component
    route: string
    accent: EntryAccent
    highlight?: boolean
}

export type ManagementGroup = {
    id: string
    title: string
    subtitle?: string
    items: ManagementEntry[]
}

/**
 * 获取当前用户角色
 */
export function getCurrentUserRole(): string | undefined {
    try {
        const raw = localStorage.getItem('userInfo')
        if (!raw) return undefined
        return (JSON.parse(raw) as { userRole?: string }).userRole
    } catch {
        return undefined
    }
}

/**
 * 权限可见性过滤逻辑
 */
function entryVisibleForRole(entry: ManagementEntry, isSuper: boolean): boolean {
    if (isSuper) return true
    // 普通管理员不可见用户管理与环境配置
    return entry.route !== '/admin/users' && entry.route !== '/admin/env'
}

/**
 * 资源配置定义
 */
const managementGroupsAll: ManagementGroup[] = [
    {
        id: 'ai-core',
        title: 'AI 核心编排',
        subtitle: '从底层算力凭证到高层智能体，构建完整的推理执行链路',
        items: [
            {
                key: 'agents',
                label: '智能体 (Agents)',
                description: '多模态大脑封装，关联工作流与工具链路',
                icon: markRaw(TeamOutlined),
                route: '/admin/agents',
                accent: 'brand', // 核心入口使用品牌色
            },
            {
                key: 'ai-instance',
                label: '推理配置 (Profiles)',
                description: '定义模型运行参数（温度、采样、Token 限制等）',
                icon: markRaw(ControlOutlined),
                route: '/admin/ai-instance',
                accent: 'indigo', // 逻辑配置使用深色靛蓝
            },
            {
                key: 'models',
                label: '模型接入 (Endpoints)',
                description: '定义供应商 API 路径、模型类型与能力映射',
                icon: markRaw(NodeIndexOutlined),
                route: '/admin/models',
                accent: 'blue', // 连接层使用标准蓝
            },
            {
                key: 'ai-account',
                label: '凭证管理 (Credentials)',
                description: '维护供应商 API_KEY、密钥凭证与账户额度',
                icon: markRaw(SafetyOutlined),
                route: '/admin/ai-account',
                accent: 'sky', // 权限层使用天蓝色，显轻盈
            },
            {
                key: 'mcp',
                label: 'AI MCP',
                description: '管理 Model Context Protocol 服务连接与健康度',
                icon: markRaw(ContainerOutlined),
                route: '/admin/mcp',
                accent: 'ocean', // 协议层使用深邃海蓝
            },
            {
                key: 'tools',
                label: '工具插件 (Tools)',
                description: '维护函数调用定义、API 工具与权限策略',
                icon: markRaw(BuildOutlined),
                route: '/admin/tools',
                accent: 'cyan', // 扩展工具使用青色
            },
            {
                key: 'prompts',
                label: '提示词库 (Prompts)',
                description: '系统级提示词编排、版本快照与预设模板',
                icon: markRaw(FileTextOutlined),
                route: '/admin/prompts',
                accent: 'mint', // 内容输入使用清新薄荷色
                highlight: true,
            },
                        {
                key: 'templates',
                label: 'FTL 模板',
                description: 'Freemarker / StringTemplate 模板（AI_TEMPLATE）',
                icon: markRaw(CodeOutlined),
                route: '/admin/templates',
                accent: 'primary-light',
            },
            {
                key: 'conversations',
                label: '对话管理',
                description: '管理 AI 对话记录，按 memoryKey 聚合展示',
                icon: markRaw(FileTextOutlined),
                route: '/admin/conversations',
                accent: 'violet',
            }
        ],
    },

    {
        id: 'vector',
        title: '向量知识库',
        subtitle: '向量数据库连接、集合配置与文档切片管理',
        items: [
            {
                key: 'vec-source',
                label: '向量源 (Sources)',
                description: '配置向量数据库连接，支持多种数据源',
                icon: markRaw(CloudUploadOutlined),
                route: '/admin/vec-source',
                accent: 'teal',
            },
            {
                key: 'vec-store',
                label: '向量存储 (Collections)',
                description: '管理向量集合，定义维度、距离度量和元数据模式',
                icon: markRaw(DatabaseOutlined),
                route: '/admin/vec-store',
                accent: 'ocean',
            },
            {
                key: 'vec-doc',
                label: '向量文档 (Documents)',
                description: '管理知识库文档记录，追踪入库状态与同步进度',
                icon: markRaw(FileSearchOutlined),
                route: '/admin/vec-doc',
                accent: 'sky',
            },
            {
                key: 'vec-segment',
                label: '向量分段 (Segments)',
                description: '管理文档切片与向量映射，支持分段内容预览',
                icon: markRaw(PartitionOutlined),
                route: '/admin/vec-segment',
                accent: 'cyan',
            },
            {
                key: 'vec-driver',
                label: '向量驱动 (Drivers)',
                description: '管理向量数据库驱动配置，支持多种向量数据库',
                icon: markRaw(SettingOutlined),
                route: '/admin/vec-driver',
                accent: 'frost',
            },
        ],
    },
    {
        id: 'ops',
        title: '安全与流程',
        subtitle: '风控、可观测与自动化编排',
        items: [
            {
                key: 'security',
                label: '安全治理',
                description: '配置敏感词、注入检测与风控策略',
                icon: markRaw(SecurityScanOutlined),
                route: '/admin/security',
                accent: 'coral', // 唯一警示色：珊瑚红
            },
            {
                key: 'tracing',
                label: '链路追踪',
                description: '监控大模型调用链路与日志输出',
                icon: markRaw(LineChartOutlined),
                route: '/admin/tracing',
                accent: 'violet', // 分析监控使用紫罗兰
            },

        ],
    },
    {
        id: 'system',
        title: '系统管理',
        subtitle: '账号、角色与运行环境（仅超级管理员）',
        items: [
            {
                key: 'users',
                label: '用户管理',
                description: '管理系统用户、角色与权限分配',
                icon: markRaw(UserOutlined),
                route: '/admin/users',
                accent: 'blue',
            },
            {
                key: 'env',
                label: '环境管理',
                description: '管理运行环境、服务实例与部署配置',
                icon: markRaw(CloudServerOutlined),
                route: '/admin/env',
                accent: 'frost',
            },
            {
                key: 'system-config',
                label: '系统配置',
                description: '管理系统配置、参数与日志',
                icon: markRaw(SettingOutlined),
                route: '/admin/system-config',
                accent: 'sky',
            },
            {
                key: 'system-extension',
                label: '系统扩展',
                description: '管理系统插件扩展包 (jarName) 及应用/卸载状态',
                icon: markRaw(BuildOutlined),
                route: '/admin/system-config/system-extension',
                accent: 'ocean',
            },
        ],
    },
]

/**
 * 根据角色解析显示的资源组
 */
export function resolveManagementGroups(role = getCurrentUserRole()): ManagementGroup[] {
    const isSuper = role === 'SUPER_ADMIN'

    return managementGroupsAll
        .map((group) => ({
            ...group,
            items: group.items.filter((item) => entryVisibleForRole(item, isSuper)),
        }))
        .filter((group) => group.items.length > 0)
}