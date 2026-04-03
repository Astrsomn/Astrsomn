import type { Component } from 'vue'
import type { DashboardModuleKind } from './dashboardLayoutTypes'
import AiAgentModule from './widgets/AiAgentModule.vue'
import AiInstanceModule from './widgets/AiInstanceModule.vue'
import ConsoleSystemLoadModule from './widgets/ConsoleSystemLoadModule.vue'
import AiModelModule from './widgets/AiModelModule.vue'
import AiAccountModule from './widgets/AiAccountModule.vue'
import AiMcpModule from './widgets/AiMcpModule.vue'
import AiToolModule from './widgets/AiToolModule.vue'
import AiPromptModule from './widgets/AiPromptModule.vue'
import AiTemplateModule from './widgets/AiTemplateModule.vue'
import KnowledgeBaseModule from './widgets/KnowledgeBaseModule.vue'
import AiDocumentModule from './widgets/AiDocumentModule.vue'
import SecurityPolicyModule from './widgets/SecurityPolicyModule.vue'
import TraceInsightModule from './widgets/TraceInsightModule.vue'
import AiWorkflowModule from './widgets/AiWorkflowModule.vue'
import AiWorkflowTestModule from './widgets/AiWorkflowTestModule.vue'
import SystemUserModule from './widgets/SystemUserModule.vue'
import SystemEnvModule from './widgets/SystemEnvModule.vue'
import SystemConfigModule from './widgets/SystemConfigModule.vue'
import SystemExtensionModule from './widgets/SystemExtensionModule.vue'
import ConsoleResourceLibraryModule from './widgets/ConsoleResourceLibraryModule.vue'

export const DASHBOARD_WIDGET_BY_KIND: Record<DashboardModuleKind, Component> = {
  AiAgent: AiAgentModule,
  AiInstance: AiInstanceModule,
  ConsoleSystemLoad: ConsoleSystemLoadModule,
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
