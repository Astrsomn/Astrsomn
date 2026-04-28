import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import type { Router } from 'vue-router'

const adminChildren: RouteRecordRaw[] = [
  {
    path: '',
    redirect: '/admin/ai-config'
  },

  {
    path: 'ai-config',
    component: () => import('@/views/admin/AdminModuleLayout.vue'),
    children: [
      {
        path: '',
        name: 'Admin',
        component: () => import('@/views/admin/ai-config/ai-config-center/Index.vue'),
        meta: {
          title: '管理后台',
          requiresAuth: true,
          showAdminDock: true,
          showModuleSidebar: false
        }
      },
      {
        path: 'builder',
        name: 'AdminBuilder',
        component: () => import('@/views/admin/ai-config/builder/Index.vue'),
        meta: {
          title: 'Agent Studio',
          requiresAuth: true,
          showAdminDock: true,
          showModuleSidebar: false
        }
      },
      {
        path: 'agents',
        name: 'AdminAgents',
        component: () => import('@/views/admin/ai-config/ai-agent/AgentList.vue'),
        meta: {
          title: '智能体管理',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'mcp',
        name: 'AdminMcp',
        component: () => import('@/views/admin/ai-config/ai-mcp/McpList.vue'),
        meta: {
          title: 'AI MCP',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'tools',
        name: 'AdminTools',
        component: () => import('@/views/admin/ai-config/ai-tool/ToolList.vue'),
        meta: {
          title: 'AI Tools',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'models',
        name: 'AdminModels',
        component: () => import('@/views/admin/ai-config/ai-model/ModelList.vue'),
        meta: {
          title: '模型接入',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'ai-instance',
        name: 'AdminAiInstance',
        component: () => import('@/views/admin/ai-config/ai-instance/InstanceList.vue'),
        meta: {
          title: '推理配置',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'ai-account',
        name: 'AdminAiAccount',
        component: () => import('@/views/admin/ai-config/ai-account/AccountList.vue'),
        meta: {
          title: 'AI 账号',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'prompts',
        name: 'AdminPrompts',
        component: () => import('@/views/admin/ai-config/ai-prompt/PromptList.vue'),
        meta: {
          title: '提示词管理',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'conversations',
        name: 'AdminConversations',
        component: () => import('@/views/admin/ai-config/ai-conversation/ConversationList.vue'),
        meta: {
          title: '对话管理',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      }
    ]
  },

  {
    path: 'ai-safety',
    component: () => import('@/views/admin/AdminModuleLayout.vue'),
    children: [
      {
        path: '',
        name: 'AdminAiSafetyCenter',
        component: () => import('@/views/admin/ai-safety/ai-safety-center/Index.vue'),
        meta: {
          title: '安全治理中心',
          requiresAuth: true,
          showAdminDock: true,
          showModuleSidebar: false
        }
      },
      {
        path: 'templates',
        name: 'AdminTemplates',
        component: () => import('@/views/admin/ai-safety/ai-template/TemplateList.vue'),
        meta: {
          title: 'FTL 模板管理',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'security',
        name: 'AdminSecurity',
        component: () => import('@/views/admin/ai-safety/ai-sensitive-word/SensitiveWordList.vue'),
        meta: {
          title: '安全治理',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'tracing',
        name: 'AdminTracing',
        component: () => import('@/views/admin/ai-safety/ai-trace-log/TraceList.vue'),
        meta: {
          title: '链路追踪',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      }
    ]
  },

  {
    path: 'ai-workflow',
    component: () => import('@/views/admin/AdminModuleLayout.vue'),
    children: [
      {
        path: '',
        name: 'AdminAiWorkflowCenter',
        component: () => import('@/views/admin/ai-workflow/ai-workflow-center/Index.vue'),
        meta: {
          title: '工作流中心',
          requiresAuth: true,
          showAdminDock: true,
          showModuleSidebar: false
        }
      },
      {
        path: 'definitions/new',
        name: 'AdminWorkflowDefinitionBuilder',
        component: () => import('@/views/admin/ai-workflow/definition/builder/Index.vue'),
        meta: {
          title: '流程搭建',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: false
        }
      },
      {
        path: 'definitions/:id/edit',
        name: 'AdminWorkflowDefinitionEditBuilder',
        component: () => import('@/views/admin/ai-workflow/definition/builder/Index.vue'),
        meta: {
          title: '编辑流程',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: false
        }
      },
      {
        path: 'definitions',
        name: 'AdminWorkflowDefinitions',
        component: () => import('@/views/admin/ai-workflow/definition/DefinitionList.vue'),
        meta: {
          title: '流程定义',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'deployments',
        name: 'AdminWorkflowDeployments',
        component: () => import('@/views/admin/ai-workflow/deployment/DeploymentList.vue'),
        meta: {
          title: '流程发布',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'deployments/:id',
        name: 'AdminWorkflowDeploymentDetail',
        component: () => import('@/views/admin/ai-workflow/deployment/DeploymentDetailFormPage.vue'),
        meta: {
          title: '发布详情',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: false
        }
      },
      {
        path: 'instances',
        name: 'AdminWorkflowInstances',
        component: () => import('@/views/admin/ai-workflow/instance/InstanceList.vue'),
        meta: {
          title: '流程实例',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'instances/:id',
        name: 'AdminWorkflowInstanceDetail',
        component: () => import('@/views/admin/ai-workflow/instance/InstanceDetailFormPage.vue'),
        meta: {
          title: '实例详情',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: false
        }
      },
      {
        path: 'node-configs',
        name: 'AdminWorkflowNodeConfigs',
        component: () => import('@/views/admin/ai-workflow/node-config/NodeConfigList.vue'),
        meta: {
          title: '节点配置',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'node-configs/:id',
        name: 'AdminWorkflowNodeConfigDetail',
        component: () => import('@/views/admin/ai-workflow/node-config/NodeConfigDetailFormPage.vue'),
        meta: {
          title: '节点配置详情',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: false
        }
      },
      {
        path: 'node-history',
        name: 'AdminWorkflowNodeHistory',
        component: () => import('@/views/admin/ai-workflow/node-history/NodeHistoryList.vue'),
        meta: {
          title: '节点历史',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'node-history/:id',
        name: 'AdminWorkflowNodeHistoryDetail',
        component: () => import('@/views/admin/ai-workflow/node-history/NodeHistoryDetailFormPage.vue'),
        meta: {
          title: '节点历史详情',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: false
        }
      },
      {
        path: 'human-tasks',
        name: 'AdminWorkflowHumanTasks',
        component: () => import('@/views/admin/ai-workflow/human-task/HumanTaskList.vue'),
        meta: {
          title: '人工任务',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'human-tasks/:id',
        name: 'AdminWorkflowHumanTaskDetail',
        component: () => import('@/views/admin/ai-workflow/human-task/HumanTaskDetailFormPage.vue'),
        meta: {
          title: '人工任务详情',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: false
        }
      },
      {
        path: 'publish',
        name: 'AdminWorkflowPublish',
        component: () => import('@/views/admin/ai-workflow/publish/PublishFormPage.vue'),
        meta: {
          title: '发布流程',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'test-run',
        name: 'AdminWorkflowTestRun',
        component: () => import('@/views/admin/ai-workflow/test-run/TestRunFormPage.vue'),
        meta: {
          title: '测试运行',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'biz-idempotent',
        name: 'AdminWorkflowBizIdempotent',
        component: () => import('@/views/admin/ai-workflow/biz-idempotent/BizIdempotentList.vue'),
        meta: {
          title: '业务幂等',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'instance-events',
        name: 'AdminWorkflowInstanceEvents',
        component: () => import('@/views/admin/ai-workflow/instance-event/InstanceEventList.vue'),
        meta: {
          title: '实例事件',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'msg-outbox',
        name: 'AdminWorkflowMsgOutbox',
        component: () => import('@/views/admin/ai-workflow/msg-outbox/MsgOutboxList.vue'),
        meta: {
          title: '消息 Outbox',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'timer-jobs',
        name: 'AdminWorkflowTimerJobs',
        component: () => import('@/views/admin/ai-workflow/timer-job/TimerJobList.vue'),
        meta: {
          title: '定时任务',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      }
    ]
  },

  {
    path: 'system',
    component: () => import('@/views/admin/AdminModuleLayout.vue'),
    children: [
      {
        path: '',
        name: 'AdminSystemConfigCenter',
        component: () => import('@/views/admin/system-config/system-config-center/Index.vue'),
        meta: {
          title: '系统管理中心',
          requiresAuth: true,
          showAdminDock: true,
          showModuleSidebar: false
        }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/system-config/system-user/UserList.vue'),
        meta: {
          title: '用户管理',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'env',
        name: 'AdminEnv',
        component: () => import('@/views/admin/system-config/system-env/EnvList.vue'),
        meta: {
          title: '环境管理',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'config',
        name: 'AdminSystemConfig',
        component: () => import('@/views/admin/system-config/system-config/SystemConfigList.vue'),
        meta: {
          title: '系统配置',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'extensions',
        name: 'AdminSystemExtension',
        component: () => import('@/views/admin/system-config/system-extension/index.vue'),
        meta: {
          title: '系统扩展',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: false
        }
      },
      {
        path: 'messages',
        name: 'AdminSystemMessage',
        component: () => import('@/views/admin/system-config/system-message/SystemMessageList.vue'),
        meta: {
          title: '系统消息',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: true
        }
      },
      {
        path: 'messages/new',
        name: 'AdminSystemMessageNew',
        component: () => import('@/views/admin/system-config/system-message/SystemMessageFormPage.vue'),
        meta: {
          title: '新建系统消息',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: false
        }
      },
      {
        path: 'messages/:id/edit',
        name: 'AdminSystemMessageEdit',
        component: () => import('@/views/admin/system-config/system-message/SystemMessageFormPage.vue'),
        meta: {
          title: '编辑系统消息',
          requiresAuth: true,
          showAdminDock: false,
          showModuleSidebar: false
        }
      }
    ]
  },

  {
    path: 'vec-source',
    name: 'AdminVecSource',
    component: () => import('@/views/admin/ai-vector/vec-source/VecSourceList.vue'),
    meta: { title: '向量源', requiresAuth: true, showAdminDock: false, showModuleSidebar: false }
  },
  {
    path: 'vec-store',
    name: 'AdminVecStore',
    component: () => import('@/views/admin/ai-vector/vec-store/VecStoreList.vue'),
    meta: { title: '向量存储', requiresAuth: true, showAdminDock: false, showModuleSidebar: false }
  },
  {
    path: 'vec-doc',
    name: 'AdminVecDoc',
    component: () => import('@/views/admin/ai-vector/vec-doc/VecDocList.vue'),
    meta: { title: '向量文档', requiresAuth: true, showAdminDock: false, showModuleSidebar: false }
  },
  {
    path: 'vec-segment',
    name: 'AdminVecSegment',
    component: () => import('@/views/admin/ai-vector/vec-segment/VecSegmentList.vue'),
    meta: { title: '向量分段', requiresAuth: true, showAdminDock: false, showModuleSidebar: false }
  },
  {
    path: 'vec-driver',
    name: 'AdminVecDriver',
    component: () => import('@/views/admin/ai-vector/vec-driver/VecDriverList.vue'),
    meta: { title: '向量驱动', requiresAuth: true, showAdminDock: false, showModuleSidebar: false }
  },
  {
    path: 'vec-center',
    name: 'AdminVectorBuilder',
    component: () => import('@/views/admin/ai-vector/vector-center/Index.vue'),
    meta: { title: '向量知识库管理', requiresAuth: true, showAdminDock: true, showModuleSidebar: false }
  },
  {
    path: 'resource-library',
    name: 'AdminResourceLibrary',
    component: () => import('@/views/resource-library/Index.vue'),
    meta: { title: '资源库', requiresAuth: true, showAdminDock: false, showModuleSidebar: false }
  },

  { path: 'ai-config-center', redirect: { path: '/admin/ai-config' } },
  { path: 'builder', redirect: { path: '/admin/ai-config/builder' } },
  { path: 'agents', redirect: { path: '/admin/ai-config/agents' } },
  { path: 'mcp', redirect: { path: '/admin/ai-config/mcp' } },
  { path: 'tools', redirect: { path: '/admin/ai-config/tools' } },
  { path: 'models', redirect: { path: '/admin/ai-config/models' } },
  { path: 'ai-instance', redirect: { path: '/admin/ai-config/ai-instance' } },
  { path: 'ai-account', redirect: { path: '/admin/ai-config/ai-account' } },
  { path: 'prompts', redirect: { path: '/admin/ai-config/prompts' } },
  { path: 'conversations', redirect: { path: '/admin/ai-config/conversations' } },

  { path: 'ai-safety-center', redirect: { path: '/admin/ai-safety' } },
  { path: 'templates', redirect: { path: '/admin/ai-safety/templates' } },
  { path: 'security', redirect: { path: '/admin/ai-safety/security' } },
  { path: 'tracing', redirect: { path: '/admin/ai-safety/tracing' } },
  { path: 'ai-workflow-center', redirect: { path: '/admin/ai-workflow' } },
  { path: 'workflow-definitions', redirect: { path: '/admin/ai-workflow/definitions' } },
  { path: 'workflow-definition-builder', redirect: { path: '/admin/ai-workflow/definitions/new' } },
  { path: 'workflow-definition-builder/:id', redirect: (to) => ({ path: `/admin/ai-workflow/definitions/${String(to.params.id)}/edit` }) },
  { path: 'workflow-deployments', redirect: { path: '/admin/ai-workflow/deployments' } },
  { path: 'workflow-instances', redirect: { path: '/admin/ai-workflow/instances' } },
  { path: 'workflow-node-history', redirect: { path: '/admin/ai-workflow/node-history' } },
  { path: 'workflow-human-tasks', redirect: { path: '/admin/ai-workflow/human-tasks' } },
  { path: 'workflow-node-configs', redirect: { path: '/admin/ai-workflow/node-configs' } },
  { path: 'workflow-publish', redirect: { path: '/admin/ai-workflow/publish' } },
  { path: 'workflow-test-run', redirect: { path: '/admin/ai-workflow/test-run' } },
  { path: 'workflow-biz-idempotent', redirect: { path: '/admin/ai-workflow/biz-idempotent' } },
  { path: 'workflow-instance-events', redirect: { path: '/admin/ai-workflow/instance-events' } },
  { path: 'workflow-msg-outbox', redirect: { path: '/admin/ai-workflow/msg-outbox' } },
  { path: 'workflow-timer-jobs', redirect: { path: '/admin/ai-workflow/timer-jobs' } },

  { path: 'system-config-center', redirect: { path: '/admin/system' } },
  { path: 'users', redirect: { path: '/admin/system/users' } },
  { path: 'env', redirect: { path: '/admin/system/env' } },
  { path: 'system-config', redirect: { path: '/admin/system/config' } },
  { path: 'system-messages', redirect: { path: '/admin/system/messages' } },
  { path: 'system-config/system-extension', redirect: { path: '/admin/system/extensions' } },
  { path: 'system-config/system-extension/marketplace', redirect: { path: '/admin/system/extensions' } },
  { path: 'system-config/system-extension/installed', redirect: { path: '/admin/system/extensions' } }
]

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'ChatHome',
    component: () => import('@/views/chat-index/Index.vue'),
    meta: {
      title: '大模型聊天',
      requiresAuth: false
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/Home.vue'),
    meta: { requiresAuth: true },
    children: adminChildren
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router: Router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  void from
  const token = localStorage.getItem('token')

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/admin')
  } else {
    next()
  }
})

export default router
