import type { Component } from 'vue'
import type { DashboardModuleKind } from './dashboardLayoutTypes'
import AiAgentModule from './widgets/AiAgentModule.vue'

import AiModelModule from './widgets/ai-config/AiModelModule.vue'
import AiAccountModule from './widgets/ai-config/AiAccountModule.vue'
import AiMcpModule from './widgets/ai-config/AiMcpModule.vue'
import AiToolModule from './widgets/ai-config/AiToolModule.vue'
import AiPromptModule from './widgets/ai-config/AiPromptModule.vue'
import AiTemplateModule from './widgets/ai-config/AiTemplateModule.vue'
import KnowledgeBaseModule from './widgets/ai-module/KnowledgeBaseModule.vue'
import AiDocumentModule from './widgets/ai-config/AiDocumentModule.vue'
import SecurityPolicyModule from './widgets/ai-module/SecurityPolicyModule.vue'
import TraceInsightModule from './widgets/ai-module/TraceInsightModule.vue'
import AiWorkflowModule from './widgets/ai-config/AiWorkflowModule.vue'
import AiWorkflowTestModule from './widgets/ai-config/AiWorkflowTestModule.vue'
import SystemUserModule from './widgets/system-config/SystemUserModule.vue'
import SystemEnvModule from './widgets/system-config/SystemEnvModule.vue'
import SystemConfigModule from './widgets/system-config/SystemConfigModule.vue'
import SystemExtensionModule from './widgets/system-config/SystemExtensionModule.vue'
import ConsoleResourceLibraryModule from './widgets/ai-module/ConsoleResourceLibraryModule.vue'

export const DASHBOARD_WIDGET_BY_KIND: Record<DashboardModuleKind, Component> = {
  AiAgent: AiAgentModule,
  AiModel: AiModelModule,
  AiAccount: AiAccountModule,
  AiMcp: AiMcpModule,
  AiTool: AiToolModule,
  AiPrompt: AiPromptModule,
  AiTemplate: AiTemplateModule,
  KnowledgeBase: KnowledgeBaseModule,
  AiDocument: AiDocumentModule,
  SecurityPolicy: SecurityPolicyModule,
  TraceInsight: TraceInsightModule,
  AiWorkflow: AiWorkflowModule,
  AiWorkflowTest: AiWorkflowTestModule,
  SystemUser: SystemUserModule,
  SystemEnv: SystemEnvModule,
  SystemConfig: SystemConfigModule,
  SystemExtension: SystemExtensionModule,
  ConsoleResourceLibrary: ConsoleResourceLibraryModule,
}
