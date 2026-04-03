import type { Component } from 'vue'
import type { DashboardModuleKind } from './dashboardLayoutTypes'
import AiAgentModule from './widgets/AiAgentModule.vue'
import AiInstanceModule from './widgets/AiInstanceModule.vue'
import ConsoleSystemLoadModule from './widgets/ConsoleSystemLoadModule.vue'
import RouteShortcutModule from './widgets/RouteShortcutModule.vue'

export const DASHBOARD_WIDGET_BY_KIND: Record<DashboardModuleKind, Component> = {
  AiAgent: AiAgentModule,
  AiInstance: AiInstanceModule,
  ConsoleSystemLoad: ConsoleSystemLoadModule,
  RouteShortcut: RouteShortcutModule,
}
