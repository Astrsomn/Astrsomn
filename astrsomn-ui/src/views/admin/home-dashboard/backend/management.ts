import { markRaw } from 'vue'
import type { Component } from 'vue'
import {
  TeamOutlined,
  ContainerOutlined,
  ToolOutlined,
  SettingOutlined,
  SafetyCertificateOutlined,
  BookOutlined,
  FileTextOutlined,
  UserOutlined,
  CloudServerOutlined,
  CodeOutlined,
  SecurityScanOutlined,
  LineChartOutlined,
  NodeIndexOutlined,
  ApiOutlined,
  KeyOutlined,
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
    title: 'AI 模型与编排',
    subtitle: '智能体、MCP、工具、模型与提示词，搭建推理与执行链路',
    items: [
      {
        key: 'agents',
        label: '智能体管理',
        description: '配置智能体策略与执行参数',
        icon: markRaw(TeamOutlined),
        route: '/admin/agents',
        accent: 'primary',
        highlight: true,
      },
      {
        key: 'mcp',
        label: 'AI MCP',
        description: '管理 MCP 服务连接与健康状态',
        icon: markRaw(ContainerOutlined),
        route: '/admin/mcp',
        accent: 'ocean',
      },
      {
        key: 'tools',
        label: 'AI Tools',
        description: '维护工具定义与调用权限',
        icon: markRaw(ToolOutlined),
        route: '/admin/tools',
        accent: 'blue',
      },
      {
        key: 'models',
        label: '模型配置',
        description: '管理模型供应商与路由策略',
        icon: markRaw(SettingOutlined),
        route: '/admin/models',
        accent: 'blue',
      },
      {
        key: 'ai-instance',
        label: '推理实例',
        description: '配置实例参数（温度、Token、Stop 等，AI_INSTANCE）',
        icon: markRaw(ApiOutlined),
        route: '/admin/ai-instance',
        accent: 'sky',
      },
      {
        key: 'ai-account',
        label: 'AI 账号',
        description: '维护供应商账号、API 凭证与额度（AI_ACCOUNT）',
        icon: markRaw(KeyOutlined),
        route: '/admin/ai-account',
        accent: 'violet',
      },
      {
        key: 'prompts',
        label: '提示词管理',
        description: '维护 AI_PROMPT 系统提示词与版本',
        icon: markRaw(SafetyCertificateOutlined),
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
