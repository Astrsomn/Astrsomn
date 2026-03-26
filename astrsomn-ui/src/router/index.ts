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
