import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import type { Router } from 'vue-router'

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
    children: [
      {
        path: '',
        redirect: '/admin/ai-config-center'
      },
      {
        path: 'ai-config-center',
        name: 'Admin',
        component: () => import('@/views/admin/ai-config/ai-config-center/Index.vue'),
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
        meta: { title: '模型接入', requiresAuth: true }
      },
      {
        path: 'ai-instance',
        name: 'AdminAiInstance',
        component: () => import('@/views/admin/ai-config/ai-instance/InstanceList.vue'),
        meta: { title: '推理配置', requiresAuth: true }
      },
      {
        path: 'ai-account',
        name: 'AdminAiAccount',
        component: () => import('@/views/admin/ai-config/ai-account/AccountList.vue'),
        meta: { title: 'AI 账号', requiresAuth: true }
      },
      {
        path: 'prompts',
        name: 'AdminPrompts',
        component: () => import('@/views/admin/ai-config/ai-prompt/PromptList.vue'),
        meta: { title: '提示词管理', requiresAuth: true }
      },
      {
        path: 'ai-safety-center',
        name: 'AdminAiSafetyCenter',
        component: () => import('@/views/admin/ai-safety/ai-safety-center/Index.vue'),
        meta: { title: '安全治理中心', requiresAuth: true }
      },
      {
        path: 'templates',
        name: 'AdminTemplates',
        component: () => import('@/views/admin/ai-safety/ai-template/TemplateList.vue'),
        meta: { title: 'FTL 模板管理', requiresAuth: true }
      },
      {
        path: 'conversations',
        name: 'AdminConversations',
        component: () => import('@/views/admin/ai-config/ai-conversation/ConversationList.vue'),
        meta: { title: '对话管理', requiresAuth: true }
      },

      {
        path: 'security',
        name: 'AdminSecurity',
        component: () => import('@/views/admin/ai-safety/ai-sensitive-word/SensitiveWordList.vue'),
        meta: { title: '安全治理', requiresAuth: true }
      },
      {
        path: 'tracing',
        name: 'AdminTracing',
        component: () => import('@/views/admin/ai-safety/ai-trace-log/TraceList.vue'),
        meta: { title: '链路追踪', requiresAuth: true }
      },
      {
        path: 'builder',
        name: 'AdminBuilder',
        component: () => import('@/views/admin/ai-config/builder/Index.vue'),
        meta: { title: 'Agent Studio', requiresAuth: true }
      },
      {
        path: 'vec-source',
        name: 'AdminVecSource',
        component: () => import('@/views/admin/ai-vector/vec-source/VecSourceList.vue'),
        meta: { title: '向量源', requiresAuth: true }
      },
      {
        path: 'vec-store',
        name: 'AdminVecStore',
        component: () => import('@/views/admin/ai-vector/vec-store/VecStoreList.vue'),
        meta: { title: '向量存储', requiresAuth: true }
      },
      {
        path: 'vec-doc',
        name: 'AdminVecDoc',
        component: () => import('@/views/admin/ai-vector/vec-doc/VecDocList.vue'),
        meta: { title: '向量文档', requiresAuth: true }
      },
      {
        path: 'vec-segment',
        name: 'AdminVecSegment',
        component: () => import('@/views/admin/ai-vector/vec-segment/VecSegmentList.vue'),
        meta: { title: '向量分段', requiresAuth: true }
      },
      {
        path: 'vec-driver',
        name: 'AdminVecDriver',
        component: () => import('@/views/admin/ai-vector/vec-driver/VecDriverList.vue'),
        meta: { title: '向量驱动', requiresAuth: true }
      },
      {
        path: 'vec-center',
        name: 'AdminVectorBuilder',
        component: () => import('@/views/admin/ai-vector/vector-center/Index.vue'),
        meta: { title: '向量知识库管理', requiresAuth: true }
      },
      {
        path: 'resource-library',
        name: 'AdminResourceLibrary',
        component: () => import('@/views/resource-library/Index.vue'),
        meta: { title: '资源库', requiresAuth: true }
      },
      {
        path: 'system-config-center',
        name: 'AdminSystemConfigCenter',
        component: () => import('@/views/admin/system-config/system-config-center/Index.vue'),
        meta: { title: '系统管理中心', requiresAuth: true }
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
      },
      {
        path: 'system-config/system-extension',
        name: 'AdminSystemExtension',
        component: () => import('@/views/admin/system-config/system-extension/index.vue'),
        meta: { title: '系统扩展', requiresAuth: true }
      },
      {
        path: 'system-config/system-extension/marketplace',
        redirect: { name: 'AdminSystemExtension' }
      },
      {
        path: 'system-config/system-extension/installed',
        redirect: { name: 'AdminSystemExtension' }
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
