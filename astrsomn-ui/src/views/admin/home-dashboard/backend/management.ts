import {markRaw} from 'vue'
import type {Component} from 'vue'
import {
    TeamOutlined,
    ContainerOutlined,
    SettingOutlined,
    BookOutlined,
    FileTextOutlined,
    UserOutlined,
    CloudServerOutlined,
    CodeOutlined,
    SecurityScanOutlined,
    LineChartOutlined,
    NodeIndexOutlined,
 BuildOutlined, ControlOutlined, SafetyOutlined,
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

export function getCurrentUserRole(): string | undefined {
    try {
        const raw = localStorage.getItem('userInfo')
        if (!raw) return undefined
        return (JSON.parse(raw) as { userRole?: string }).userRole
    } catch {
        return undefined
    }
}

function entryVisibleForRole(entry: ManagementEntry, isSuper: boolean): boolean {
    if (isSuper) return true
    return entry.route !== '/admin/users' && entry.route !== '/admin/env'
}

const managementGroupsAll: ManagementGroup[] = [
    {
        id: 'ai-core',
        title: 'AI 核心编排', // 强化“编排”概念，突出工业感
        subtitle: '从底层算力凭证到高层智能体，构建完整的推理执行链路',
        items: [
            {
                key: 'agents',
                label: '智能体 (Agents)',
                description: '多模态大脑封装，关联工作流与工具链路',
                icon: markRaw(TeamOutlined),
                route: '/admin/agents',
                accent: 'primary',
            },
            {
                key: 'ai-instance',
                label: '推理配置 (Profiles)', // 建议改名：Instance 是具体化的参数集，叫“配置/预设”更直观
                description: '定义模型运行参数（温度、采样、Token 限制等）',
                icon: markRaw(ControlOutlined), // 换成“控制台”图标，暗示调节参数
                route: '/admin/ai-instance',
                accent: 'mint',
            },
            {
                key: 'models',
                label: '模型接入 (Endpoints)', // 建议改名：强调它是“端点/接口”，解决“路通不通”
                description: '定义供应商 API 路径、模型类型与能力映射',
                icon: markRaw(NodeIndexOutlined), // 换成“节点”图标，暗示连接
                route: '/admin/models',
                accent: 'blue',
            },
            {
                key: 'ai-account',
                label: '凭证管理 (Credentials)', // 建议改名：强调它是“身份/钱”，解决“有没有权限”
                description: '维护供应商 API_KEY、密钥凭证与账户额度',
                icon: markRaw(SafetyOutlined), // 换成“安全/护盾”图标
                route: '/admin/ai-account',
                accent: 'blue',
            },
            {
                key: 'mcp',
                label: 'AI MCP',
                description: '管理 Model Context Protocol 服务连接与健康度',
                icon: markRaw(ContainerOutlined),
                route: '/admin/mcp',
                accent: 'ocean',
            },
            {
                key: 'tools',
                label: '工具插件 (Tools)',
                description: '维护函数调用定义、API 工具与权限策略',
                icon: markRaw(BuildOutlined), // 换成“构建”图标
                route: '/admin/tools',
                accent: 'blue',
            },
            {
                key: 'prompts',
                label: '提示词库 (Prompts)',
                description: '系统级提示词编排、版本快照与预设模板',
                icon: markRaw(FileTextOutlined),
                route: '/admin/prompts',
                accent: 'mint',
                highlight: true,
            },
        ],
    },
    {
        id: 'content',
        title: '模板与知识',
        subtitle: '内容模板、知识库与文档入库',
        items: [
            {
                key: 'templates',
                label: 'FTL 模板',
                description: 'Freemarker / StringTemplate 模板（AI_TEMPLATE）',
                icon: markRaw(CodeOutlined),
                route: '/admin/templates',
                accent: 'primary-light',
            },
            {
                key: 'kb-mgr',
                label: '知识库管理',
                description: '管理知识库空间与索引配置',
                icon: markRaw(BookOutlined),
                route: '/admin/knowledge-bases',
                accent: 'teal',
            },
            {
                key: 'doc-mgr',
                label: '文档管理',
                description: '管理文档处理与入库任务',
                icon: markRaw(FileTextOutlined),
                route: '/admin/documents',
                accent: 'cyan',
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
                accent: 'coral',
            },
            {
                key: 'tracing',
                label: '链路追踪',
                description: '监控大模型调用链路与日志输出',
                icon: markRaw(LineChartOutlined),
                route: '/admin/tracing',
                accent: 'violet',
            },
            {
                key: 'workflows',
                label: '工作流',
                description: '配置 DAG 节点流程、触发条件与执行链路',
                icon: markRaw(NodeIndexOutlined),
                route: '/admin/workflows',
                accent: 'indigo',
                highlight: true,
            },
            {
                key: 'workflow-simple',
                label: '工作流编排测试',
                description: '按 Workflow Key 快速调用后端测试运行（已发布版本）',
                icon: markRaw(NodeIndexOutlined),
                route: '/admin/workflows/simple',
                accent: 'blue',
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
                accent: 'blue',
            },
        ],
    },
]

export function resolveManagementGroups(role = getCurrentUserRole()): ManagementGroup[] {
    const isSuper = role === 'SUPER_ADMIN'

    return managementGroupsAll
        .map((group) => ({
            ...group,
            items: group.items.filter((item) => entryVisibleForRole(item, isSuper)),
        }))
        .filter((group) => group.items.length > 0)
}
