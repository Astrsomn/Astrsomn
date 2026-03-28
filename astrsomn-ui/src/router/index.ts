import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import type { Router } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'ChatHome',
    component: () => import('@/views/Index.vue'),
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
    children: [
      {
        path: '',
        name: 'Admin',
        component: () => import('@/views/admin/components/AdminDashboard.vue'),
        meta: { title: '管理后台', requiresAuth: true }
      },
      {
        path: 'agents',
        name: 'AdminAgents',
        component: () => import('@/views/admin/ai-config/ai-agent/AgentList.vue'),
        meta: { title: '智能体管理', requiresAuth: true }
      },
      {
        path: 'mcp',
        name: 'AdminMcp',
        component: () => import('@/views/admin/ai-config/ai-mcp/McpList.vue'),
        meta: { title: 'AI MCP', requiresAuth: true }
      },
      {
        path: 'tools',
        name: 'AdminTools',
        component: () => import('@/views/admin/ai-config/ai-tool/ToolList.vue'),
        meta: { title: 'AI Tools', requiresAuth: true }
      },
      {
        path: 'models',
        name: 'AdminModels',
        component: () => import('@/views/admin/ai-config/ai-model/ModelList.vue'),
        meta: { title: '模型配置', requiresAuth: true }
      },
      {
        path: 'prompts',
        name: 'AdminPrompts',
        component: () => import('@/views/admin/ai-config/ai-prompt/PromptList.vue'),
        meta: { title: '提示词管理', requiresAuth: true }
      },
      {
        path: 'templates',
        name: 'AdminTemplates',
        component: () => import('@/views/admin/ai-config/ai-template/TemplateList.vue'),
        meta: { title: 'FTL 模板管理', requiresAuth: true }
      },
      {
        path: 'knowledge-bases',
        name: 'AdminKnowledgeBases',
        component: () => import('@/views/admin/ai-modules/ai-rag/KnowledgeBaseList.vue'),
        meta: { title: '知识库管理', requiresAuth: true }
      },
      {
        path: 'documents',
        name: 'AdminDocuments',
        component: () => import('@/views/admin/ai-modules/ai-docs/DocumentList.vue'),
        meta: { title: '文档管理', requiresAuth: true }
      },
      {
        path: 'security',
        name: 'AdminSecurity',
        component: () => import('@/views/admin/ai-modules/ai-sensitiveWord/SensitiveWordList.vue'),
        meta: { title: '安全治理', requiresAuth: true }
      },
      {
        path: 'tracing',
        name: 'AdminTracing',
        component: () => import('@/views/admin/ai-modules/ai-trace/TraceList.vue'),
        meta: { title: '链路追踪', requiresAuth: true }
      },
      {
        path: 'workflows',
        name: 'AdminWorkflows',
        component: () => import('@/views/admin/ai-modules/ai-workflow/WorkflowList.vue'),
        meta: { title: '工作流', requiresAuth: true }
      },
      {
        path: 'workflows/edit/:id',
        name: 'AdminWorkflowEdit',
        component: () => import('@/views/admin/ai-modules/ai-workflow/WorkflowEditor.vue'),
        meta: { title: '工作流编排', requiresAuth: true }
      },
      {
        path: 'workflows/simple',
        name: 'AdminWorkflowSimple',
        component: () => import('@/views/admin/ai-modules/ai-workflow/WorkflowSimpleTest.vue'),
        meta: { title: '工作流编排测试', requiresAuth: true }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/system-config/system-user/UserList.vue'),
        meta: { title: '用户管理', requiresAuth: true }
      },
      {
        path: 'env',
        name: 'AdminEnv',
        component: () => import('@/views/admin/system-config/system-env/EnvList.vue'),
        meta: { title: '环境管理', requiresAuth: true }
      },
      {
        path: 'system-config',
        name: 'AdminSystemConfig',
        component: () => import('@/views/admin/system-config/system-config/SystemConfigList.vue'),
        meta: { title: '系统配置', requiresAuth: true }
      }
    ]
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
