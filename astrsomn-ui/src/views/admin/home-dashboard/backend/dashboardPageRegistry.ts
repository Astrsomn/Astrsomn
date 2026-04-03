import type { EntryAccent } from '../resource-library/management.ts'
import type { DashboardPageModuleKind } from './dashboardLayoutTypes'

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
  metrics: { label: string; value: string }[]
  footnote?: string
}

export const PAGE_MODULE_MOCK: Record<DashboardPageModuleKind, PageModuleMock> = {
  AiModel: {
    accent: 'blue',
    title: '模型接入 (Endpoints)',
    description:
      '定义供应商 API 路径、模型类型与能力映射（支持多账号路由、超参策略、熔断降级规则与重试参数配置）',
    metrics: [
      { label: '已接入', value: '8' },
      { label: '健康检查', value: '7/8' },
    ],
    footnote:
      'Mock：近期新增 OpenAI 兼容端点 2 个，且已完成超参模板与路由策略更新（用于手动验证组件溢出截断效果）',
  },
  AiAccount: {
    accent: 'blue',
    title: '凭证管理 (Credentials)',
    description: '维护供应商 API_KEY、密钥凭证与账户额度',
    metrics: [
      { label: '活跃凭证', value: '5' },
      { label: '将过期', value: '1' },
    ],
    footnote: 'Mock：Azure 密钥 30 天内轮换',
  },
  AiMcp: {
    accent: 'ocean',
    title: 'AI MCP',
    description: '管理 Model Context Protocol 服务连接与健康度',
    metrics: [
      { label: '已连接', value: '4' },
      { label: '异常', value: '0' },
    ],
    footnote: 'Mock：全部节点心跳正常',
  },
  AiTool: {
    accent: 'blue',
    title: '工具插件 (Tools)',
    description: '维护函数调用定义、API 工具与权限策略',
    metrics: [
      { label: '已发布', value: '12' },
      { label: '待审核', value: '3' },
    ],
    footnote: 'Mock：本周新增 HTTP Tool 2 个',
  },
  AiPrompt: {
    accent: 'mint',
    title: '提示词库 (Prompts)',
    description: '系统级提示词编排、版本快照与预设模板',
    metrics: [
      { label: '版本条目', value: '48' },
      { label: '启用中', value: '32' },
    ],
    footnote: 'Mock：生产环境引用 v3 为主',
  },
  AiTemplate: {
    accent: 'primary-light',
    title: 'FTL 模板',
    description: 'Freemarker / StringTemplate 模板（AI_TEMPLATE）',
    metrics: [
      { label: '模板数', value: '24' },
      { label: '今日渲染', value: '1.2k' },
    ],
    footnote: 'Mock：热点模板 order_confirm_ftl',
  },
  KnowledgeBase: {
    accent: 'teal',
    title: '知识库管理',
    description: '管理知识库空间与索引配置',
    metrics: [
      { label: '知识库', value: '12' },
      { label: '索引任务', value: 'Idle' },
    ],
    footnote: 'Mock：向量维度 1536',
  },
  AiDocument: {
    accent: 'cyan',
    title: '文档管理',
    description: '管理文档处理与入库任务',
    metrics: [
      { label: '队列中', value: '6' },
      { label: '已完成(今日)', value: '118' },
    ],
    footnote: 'Mock：OCR 平均耗时 1.8s/页',
  },
  SecurityPolicy: {
    accent: 'coral',
    title: '安全治理',
    description: '配置敏感词、注入检测与风控策略',
    metrics: [
      { label: '敏感词库', value: '3' },
      { label: '今日拦截', value: '42' },
    ],
    footnote: 'Mock：规则包 v2026.01',
  },
  TraceInsight: {
    accent: 'violet',
    title: '链路追踪',
    description: '监控大模型调用链路与日志输出',
    metrics: [
      { label: 'P99 延迟', value: '890ms' },
      { label: '采样率', value: '10%' },
    ],
    footnote: 'Mock：Jaeger 后端已连接',
  },
  AiWorkflow: {
    accent: 'indigo',
    title: '工作流',
    description: '配置 DAG 节点流程、触发条件与执行链路',
    metrics: [
      { label: '已发布', value: '9' },
      { label: '运行中', value: '2' },
    ],
    footnote: 'Mock：审批流调用量 +18%',
  },
  AiWorkflowTest: {
    accent: 'blue',
    title: '工作流编排测试',
    description: '按 Workflow Key 快速调用后端测试运行（已发布版本）',
    metrics: [
      { label: '最近 Key', value: 'demo_flow' },
      { label: '上次耗时', value: '240ms' },
    ],
    footnote: 'Mock：仅沙箱环境',
  },
  SystemUser: {
    accent: 'blue',
    title: '用户管理',
    description: '管理系统用户、角色与权限分配',
    metrics: [
      { label: '用户', value: '36' },
      { label: '角色模板', value: '5' },
    ],
    footnote: 'Mock：仅超级管理员可见',
  },
  SystemEnv: {
    accent: 'frost',
    title: '环境管理',
    description: '管理运行环境、服务实例与部署配置',
    metrics: [
      { label: '环境', value: '3' },
      { label: '当前', value: 'prod' },
    ],
    footnote: 'Mock：prod / staging / dev',
  },
  SystemConfig: {
    accent: 'blue',
    title: '系统配置',
    description: '管理系统配置、参数与日志',
    metrics: [
      { label: '配置项', value: '128' },
      { label: '热更新', value: 'On' },
    ],
    footnote: 'Mock：配置中心已缓存',
  },
  SystemExtension: {
    accent: 'ocean',
    title: '系统扩展',
    description: '管理系统插件扩展包（jarName）及应用/卸载状态',
    metrics: [
      { label: '已加载', value: '4' },
      { label: '待重启', value: '0' },
    ],
    footnote: 'Mock：扩展包校验通过',
  },
  ConsoleResourceLibrary: {
    accent: 'brand',
    title: '应用库',
    description:
      '浏览全部管理入口并固定到控制台（支持分组筛选、拖拽排序与权限维度的展示策略配置）',
    metrics: [
      { label: '已固定', value: '—' },
      { label: '分组', value: '4' },
    ],
    footnote: 'Mock：从应用库拖拽或点击固定',
  },
}
