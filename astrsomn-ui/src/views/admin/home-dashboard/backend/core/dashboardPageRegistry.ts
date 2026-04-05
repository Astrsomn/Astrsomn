import type { EntryAccent } from '../../resource-library/management.ts'
import type { DashboardPageModuleKind } from '../types/dashboardLayoutTypes'

export const PAGE_KIND_ROUTE: Record<DashboardPageModuleKind, string> = {
  AiModel: '/admin/models',
  AiAccount: '/admin/ai-account',
  AiMcp: '/admin/mcp',
  AiTool: '/admin/tools',
  AiPrompt: '/admin/prompts',
  AiTemplate: '/admin/templates',
  KnowledgeBase: '/admin/knowledge-bases',
  AiDocument: '/admin/documents',
  SecurityPolicy: '/admin/security',
  TraceInsight: '/admin/tracing',
  AiWorkflow: '/admin/workflows',
  AiWorkflowTest: '/admin/workflows/simple',
  SystemUser: '/admin/users',
  SystemEnv: '/admin/env',
  SystemConfig: '/admin/system-config',
  SystemExtension: '/admin/system-config/system-extension',
  ConsoleResourceLibrary: '/admin/resource-library',
}

export const ROUTE_TO_PAGE_KIND: Record<string, DashboardPageModuleKind> = (
  Object.keys(PAGE_KIND_ROUTE) as DashboardPageModuleKind[]
).reduce(
  (acc, k) => {
    acc[PAGE_KIND_ROUTE[k]] = k
    return acc
  },
  {} as Record<string, DashboardPageModuleKind>,
)

/** 前端 mock，后续可在各 *.vue 或此处替换为真实数据 */
export type PageModuleMock = {
  accent: EntryAccent
  /** 应用库无 entry 时的兜底文案 */
  title: string
  description: string
  footnote?: string
}

export const PAGE_MODULE_MOCK: Record<DashboardPageModuleKind, PageModuleMock> = {
  AiModel: {
    accent: 'blue',
    title: '模型接入 (Endpoints)',
    description:
      '定义供应商 API 路径、模型类型与能力映射（支持多账号路由、超参策略、熔断降级规则与重试参数配置）',
    footnote:
      'Mock：近期新增 OpenAI 兼容端点 2 个，且已完成超参模板与路由策略更新（用于手动验证组件溢出截断效果）',
  },
  AiAccount: {
    accent: 'blue',
    title: '凭证管理 (Credentials)',
    description: '维护供应商 API_KEY、密钥凭证与账户额度',
    footnote: 'Mock：Azure 密钥 30 天内轮换',
  },
  AiMcp: {
    accent: 'ocean',
    title: 'AI MCP',
    description: '管理 Model Context Protocol 服务连接与健康度',
    footnote: 'Mock：全部节点心跳正常',
  },
  AiTool: {
    accent: 'blue',
    title: '工具插件 (Tools)',
    description: '维护函数调用定义、API 工具与权限策略',
    footnote: 'Mock：本周新增 HTTP Tool 2 个',
  },
  AiPrompt: {
    accent: 'mint',
    title: '提示词库 (Prompts)',
    description: '系统级提示词编排、版本快照与预设模板',
    footnote: 'Mock：生产环境引用 v3 为主',
  },
  AiTemplate: {
    accent: 'primary-light',
    title: 'FTL 模板',
    description: 'Freemarker / StringTemplate 模板（AI_TEMPLATE）',
    footnote: 'Mock：热点模板 order_confirm_ftl',
  },
  KnowledgeBase: {
    accent: 'teal',
    title: '知识库管理',
    description: '管理知识库空间与索引配置',
    footnote: 'Mock：向量维度 1536',
  },
  AiDocument: {
    accent: 'cyan',
    title: '文档管理',
    description: '管理文档处理与入库任务',
    footnote: 'Mock：OCR 平均耗时 1.8s/页',
  },
  SecurityPolicy: {
    accent: 'coral',
    title: '安全治理',
    description: '配置敏感词、注入检测与风控策略',
    footnote: 'Mock：规则包 v2026.01',
  },
  TraceInsight: {
    accent: 'violet',
    title: '链路追踪',
    description: '监控大模型调用链路与日志输出',
    footnote: 'Mock：Jaeger 后端已连接',
  },
  AiWorkflow: {
    accent: 'indigo',
    title: '工作流',
    description: '配置 DAG 节点流程、触发条件与执行链路',
    footnote: 'Mock：审批流调用量 +18%',
  },
  AiWorkflowTest: {
    accent: 'blue',
    title: '工作流编排测试',
    description: '按 Workflow Key 快速调用后端测试运行（已发布版本）',
    footnote: 'Mock：仅沙箱环境',
  },
  SystemUser: {
    accent: 'blue',
    title: '用户管理',
    description: '管理系统用户、角色与权限分配',
    footnote: 'Mock：仅超级管理员可见',
  },
  SystemEnv: {
    accent: 'frost',
    title: '环境管理',
    description: '管理运行环境、服务实例与部署配置',
    footnote: 'Mock：prod / staging / dev',
  },
  SystemConfig: {
    accent: 'blue',
    title: '系统配置',
    description: '管理系统配置、参数与日志',
    footnote: 'Mock：配置中心已缓存',
  },
  SystemExtension: {
    accent: 'ocean',
    title: '系统扩展',
    description: '管理系统插件扩展包（jarName）及应用/卸载状态',
    footnote: 'Mock：扩展包校验通过',
  },
  ConsoleResourceLibrary: {
    accent: 'brand',
    title: '应用库',
    description:
      '浏览全部管理入口并固定到控制台（支持分组筛选、拖拽排序与权限维度的展示策略配置）',
    footnote: 'Mock：从应用库拖拽或点击固定',
  }
}
