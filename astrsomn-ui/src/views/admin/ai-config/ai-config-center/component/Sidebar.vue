<template>
  <div :class="{ collapsed: collapsed }" class="sidebar-content">
    <!-- 顶部区域：收起/展开按钮 + 搜索框 -->
    <div class="top-section">
      <a-tooltip :placement="collapsed ? 'right' : 'bottom'">
        <template #title>{{ collapsed ? '展开侧边栏' : '收起侧边栏' }}</template>
        <div class="collapse-toggle" @click="toggleCollapsed">
          <MenuUnfoldOutlined v-if="collapsed"/>
          <MenuFoldOutlined v-else/>
        </div>
      </a-tooltip>
      <AstSearchInput
          v-if="!collapsed"
          v-model="searchText"
          class="sidebar-search-pill"
          layout="fluid"
          placeholder="搜索资源..."
          @search="handleSearch"
      />
    </div>

    <!-- 导航列表 -->
    <div class="nav-list">
      <div class="nav-section">
        <div class="provider-list">
          <!-- 全部选项 -->
          <a-tooltip :disabled="!collapsed" placement="right">
            <template #title>全部</template>
            <div
                :class="{ 'is-active': activeItem === 'all' }"
                class="provider-card"
                @click="handleSelect('all')"
            >
              <div class="provider-avatar">
                <component :is="CloudServerOutlined" class="all-icon"/>
              </div>
              <span class="provider-name">全部</span>
            </div>
          </a-tooltip>
          <!-- 各模型提供商 -->
          <a-tooltip
              v-for="item in providers"
              :key="item.key"
              :disabled="!collapsed"
              placement="right"
          >
            <template #title>{{ item.label }}</template>
            <div
                :class="{ 'is-active': activeItem === item.key }"
                class="provider-card"
                @click="handleSelect(item.key)"
            >
              <div class="provider-avatar">
                <img v-if="item.avatar" :alt="item.label" :src="item.avatar" class="avatar-img"/>
                <span v-else class="avatar-initial">{{ item.initial }}</span>
              </div>
              <span class="provider-name">{{ item.label }}</span>
            </div>
          </a-tooltip>
        </div>
        <div v-if="providers.length === 0 && !collapsed" class="empty-provider">
          <a-empty description="暂无已启用的插件">
            <template #extra>
              <a-button type="link" @click="handleAddPlugin">前往插件市场</a-button>
            </template>
          </a-empty>
        </div>
      </div>
    </div>

    <!-- 全局管理（固定在底部） -->
    <div class="global-section">
      <a-tooltip
          v-for="item in globalItems"
          :key="item.key"
          :disabled="!collapsed"
          placement="right"
      >
        <template #title>{{ item.label }}</template>
        <div
            :class="{ 'is-active': activeItem === item.key }"
            class="global-item"
            @click="handleSelect(item.key)"
        >
          <component :is="item.icon" class="global-icon"/>
          <span class="global-label">{{ item.label }}</span>
          <span v-if="item.count !== undefined" class="global-count">{{ item.count }}</span>
        </div>
      </a-tooltip>

      <!-- 展开区域 -->
      <div v-show="extraExpanded && !collapsed" class="extra-items">
        <div
            v-for="item in extraItems"
            :key="item.key"
            :class="{ 'is-active': activeItem === item.key }"
            class="global-item"
            @click="handleSelect(item.key)"
        >
          <component :is="item.icon" class="global-icon"/>
          <span class="global-label">{{ item.label }}</span>
          <span v-if="item.count !== undefined" class="global-count">{{ item.count }}</span>
        </div>
      </div>
      <!-- 展开/收起（收起状态下隐藏） -->
      <div v-show="!collapsed" class="expand-toggle" @click="toggleExtra">
        <span>{{ extraExpanded ? '收起' : '更多功能' }}</span>
        <DownOutlined :class="{ 'rotate-up': extraExpanded }" class="expand-arrow"/>
      </div>
    </div>

    <!-- 底部拓展中心入口 -->
    <div class="sidebar-footer">
      <template v-if="!collapsed">
        <div class="footer-row">
          <div class="driver-info">
            <div class="s-avatars">
              <template v-if="enabledExtensions.length">
                <span
                    v-for="item in enabledExtensions.slice(0, 4)"
                    :key="item.key"
                    :title="item.name"
                    class="s-av s-av-real"
                >
                  <img v-if="item.avatar" :alt="item.name" :src="item.avatar"/>
                  <span v-else>{{ item.initial }}</span>
                </span>
              </template>
              <span v-else class="s-av">-</span>
            </div>
            <span class="s-text">
              {{ enabledExtensions.length ? `已启用扩展 ${enabledExtensions.length}` : '暂无已启用扩展' }}
            </span>
          </div>
          <div class="plugin-market-btn" @click="goPluginMarketplace">
            <AppstoreOutlined class="plugin-market-icon"/>
            <span>插件市场</span>
            <span v-if="!providers.length" class="plugin-market-dot"></span>
          </div>
        </div>
      </template>
      <template v-else>
        <a-tooltip placement="right">
          <template #title>插件市场</template>
          <div class="footer-collapsed" @click="goPluginMarketplace">
            <AppstoreOutlined class="m-btn"/>
          </div>
        </a-tooltip>
      </template>
    </div>

    <ExtensionMarketplaceDialog
        :open="marketplaceOpen"
        @cancel="marketplaceOpen = false"
        @update:open="marketplaceOpen = $event"
    />
  </div>
</template>

<script lang="ts" setup>
import {onMounted, ref, watch} from 'vue'
import {useRoute} from 'vue-router'
import {
  AppstoreOutlined,
  CloudServerOutlined,
  CreditCardOutlined,
  DownOutlined,
  FileTextOutlined,
  LinkOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  MessageOutlined,
  ToolOutlined
} from '@ant-design/icons-vue'
import {type SystemExtension, systemExtensionApi} from '@/api/systemExtension.ts'
import {aiAccountApi} from '@/api/aiAccount'
import {aiPromptApi} from '@/api/aiPrompt'
import {aiMcpApi} from '@/api/aiMcp'
import {aiToolApi} from '@/api/aiTool'
import {aiTemplateApi} from '@/api/aiTemplate'
import {aiConversationApi} from '@/api/aiConversation'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import {useDictionary} from '@/locales/dictionary'
import ExtensionMarketplaceDialog from './ExtensionMarketplaceDialog.vue'

const emit = defineEmits<{
  select: [key: string]
  'select-provider': [info: { key: string; name: string; description: string; avatar: string }]
  'update:collapsed': [value: boolean]
}>()

const collapsed = ref(false)
const toggleCollapsed = () => {
  collapsed.value = !collapsed.value
  emit('update:collapsed', collapsed.value)
}
const route = useRoute()
const providerDict = useDictionary('ai-model.provider')

const searchText = ref('')
const activeItem = ref('')
const marketplaceOpen = ref(false)
const enabledExtensions = ref<Array<{ key: string; name: string; avatar: string; initial: string }>>([])
const providers = ref<Array<{
  key: string;
  label: string;
  icon: typeof CloudServerOutlined;
  avatar?: string;
  initial: string;
  description: string
}>>([])

const globalItems = ref([
  {key: 'ai-account', label: 'AI 账号', icon: CreditCardOutlined, count: undefined as number | undefined},
  {key: 'prompts', label: '提示词', icon: FileTextOutlined, count: undefined as number | undefined},
  {key: 'mcp', label: 'MCP', icon: LinkOutlined, count: undefined as number | undefined},
  {key: 'tools', label: 'Tools', icon: ToolOutlined, count: undefined as number | undefined},
])

const extraExpanded = ref(false)
const extraItems = ref([
  {key: 'ftl', label: 'FTL 模板', icon: FileTextOutlined, count: undefined as number | undefined},
  {key: 'conversations', label: '对话管理', icon: MessageOutlined, count: undefined as number | undefined},
])

const toggleExtra = () => {
  extraExpanded.value = !extraExpanded.value
}

const handleSelect = (key: string) => {
  activeItem.value = key
  emit('select', key)
  const provider = providers.value.find(p => p.key === key)
  if (provider) {
    emit('select-provider', {key: provider.key, name: provider.label, description: provider.description, avatar: provider.avatar || ''})
  }
}

const handleSearch = () => {
}

const handleAddPlugin = () => {
  marketplaceOpen.value = true
}

const goPluginMarketplace = () => {
  marketplaceOpen.value = true
}

const fetchEnabledExtensions = async () => {
  try {
    const resp = await systemExtensionApi.queryPage({
      pageNo: 1,
      pageSize: 50,
      param: {
        listScope: 'INSTALLED',
        type: 'MODEL_PROVIDER',
      },
    })
    const rows = (resp.list || []).filter((item) => String(item.applied || '').toUpperCase() === 'Y')
    enabledExtensions.value = rows.map((item) => {
      const name = String(item.extensionName || item.extensionKey || '扩展')
      return {
        key: String(item.id ?? item.extensionKey ?? name),
        name,
        avatar: String(item.avatar || ''),
        initial: name.slice(0, 1).toUpperCase(),
      }
    })
  } catch {
    enabledExtensions.value = []
  }
}

const fetchProviders = async () => {
  try {
    const resp = await systemExtensionApi.queryPage({
      pageNo: 1,
      pageSize: 50,
      param: {
        listScope: 'INSTALLED',
        type: 'MODEL_PROVIDER',
      },
    })
    const enabled = (resp.list || []).filter((item) => String(item.applied || '').toUpperCase() === 'Y')
    const modelProviders = enabled.map((item: SystemExtension) => {
      const code = (item.extensionCode || item.extensionKey || '').toLowerCase()
      const dictLabel = providerDict.value.getLabel(code)
      const name = dictLabel || item.extensionName || item.extensionKey || '未知插件'
      return {
        key: code,
        label: name,
        icon: CloudServerOutlined,
        avatar: item.avatar,
        initial: name.slice(0, 1).toUpperCase(),
        description: item.description || '',
      }
    })
    providers.value = modelProviders
    updateActiveItem()
  } catch {
    providers.value = []
  }
}

const updateActiveItem = () => {
  const currentPath = route.path

  // 如果是 ai-config-center 页面
  if (currentPath === '/admin/ai-config-center') {
    // 先检查是否是全局管理视图
    const view = route.query.view as string | undefined
    const globalKeys = [...globalItems.value.map(item => item.key), ...extraItems.value.map(item => item.key)]
    if (view && globalKeys.includes(view)) {
      activeItem.value = view
      return
    }

    // 再检查是否是模型提供商
    const provider = route.query.provider as string | undefined
    if (provider && providers.value.some(p => p.key === provider.toLowerCase())) {
      activeItem.value = provider.toLowerCase()
      return
    }

    // 默认选中"全部"
    activeItem.value = 'all'
    return
  }

  // 检查是否是全局管理页面
  const pathParts = currentPath.split('/')
  const lastPart = pathParts[pathParts.length - 1]
  const globalKeys = [...globalItems.value.map(item => item.key), ...extraItems.value.map(item => item.key)]
  if (globalKeys.includes(lastPart)) {
    activeItem.value = lastPart
    return
  }

  // 默认选中"全部"
  activeItem.value = 'all'
}

const fetchCount = async (api: { queryPage: (p: unknown) => Promise<{ total?: number }> }, list: typeof globalItems, idx: number) => {
  try {
    const resp = await api.queryPage({pageNo: 1, pageSize: 1})
    list.value[idx].count = resp.total ?? 0
  } catch {
    // ignore
  }
}

const fetchCounts = () => {
  return Promise.all([
    fetchCount(aiAccountApi, globalItems, 0),
    fetchCount(aiPromptApi, globalItems, 1),
    fetchCount(aiMcpApi, globalItems, 2),
    fetchCount(aiToolApi, globalItems, 3),
    fetchCount(aiTemplateApi, extraItems, 0),
    fetchCount(aiConversationApi, extraItems, 1),
  ])
}

onMounted(() => {
  void fetchProviders()
  void fetchEnabledExtensions()
  void fetchCounts()
  updateActiveItem()
})

watch(
    () => [route.path, route.query.view, route.query.provider],
    () => {
      updateActiveItem()
    },
    {deep: true}
)
</script>

<style scoped>
.sidebar-content {
  height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  padding: 14px 16px;
  background: var(--bg-card);
  transition: padding 0.28s ease;
}

.sidebar-content.collapsed {
  padding: 14px 8px;
  align-items: center;
}

/* 顶部区域 */
.top-section {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.top-section .sidebar-search-pill {
  flex: 1;
  min-width: 0;
}

.sidebar-content.collapsed .top-section {
  flex-direction: column;
  gap: 8px;
}

.collapse-toggle {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  color: var(--text-muted);
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.collapse-toggle:hover {
  color: var(--primary);
  background: var(--primary-hover);
}

.sidebar-content.collapsed .collapse-toggle {
  width: 36px;
  height: 36px;
}


/* 导航列表 */
.nav-list {
  flex: 1;
  overflow-y: auto;
}

.nav-section {
  margin-bottom: 20px;
}

.nav-section-title {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-muted);
  padding: 6px 0 4px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* 提供商列表 */
.provider-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.provider-card {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.provider-card:hover {
  background: var(--primary-hover);
  color: var(--primary);
}

.provider-card.is-active {
  background: var(--primary-hover);
  color: var(--primary);
  font-weight: 500;
  position: relative;
}

.provider-card.is-active::after {
  content: '';
  position: absolute;
  right: 4px;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  border-radius: 2px;
  background: var(--primary);
}

.provider-avatar {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  margin-right: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-initial {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: 600;
  color: var(--primary);
  background: var(--primary-hover);
}

.all-icon {
  font-size: 12px;
  color: var(--primary);
}

.provider-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 全局管理区域（固定在底部） */
.global-section {
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid var(--border-default);
}

.global-item {
  display: flex;
  align-items: center;
  padding: 12px 14px;
  border-radius: var(--radius-sm);
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  border: 1px solid transparent;
}

.global-item:hover {
  background: var(--primary-hover);
  color: var(--primary);
}

.global-item.is-active {
  background: var(--primary-hover);
  color: var(--primary);
  font-weight: 500;
  border-color: var(--primary);
}

.global-item.is-active::after {
  content: '';
  position: absolute;
  right: 4px;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  border-radius: 2px;
  background: var(--primary);
}

.global-icon {
  font-size: 14px;
  margin-right: 10px;
}

.global-item.is-active .global-icon {
  color: var(--primary);
}

.global-label {
  flex: 1;
}

.global-count {
  font-size: 11px;
  color: var(--text-muted);
  background: var(--primary-hover);
  padding: 1px 6px;
  border-radius: 10px;
  line-height: 18px;
}

/* 空状态提示 */
.empty-provider {
  padding: 24px 0;
  text-align: center;
}

/* 展开/收起按钮 */
.expand-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 12px 14px;
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
  border-radius: var(--radius-sm);
}

.expand-toggle:hover {
  background: var(--primary-hover);
  color: var(--primary);
}

.expand-toggle .expand-arrow {
  font-size: 10px;
  transition: transform 0.25s ease;
}

.expand-toggle .expand-arrow.rotate-up {
  transform: rotate(180deg);
}

/* 底部拓展中心入口 */
.sidebar-footer {
  flex-shrink: 0;
  padding: 12px 0;
  border-top: 1px solid var(--border-default);
}

.sidebar-footer .footer-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar-footer .footer-row .driver-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sidebar-footer .footer-row .driver-info .s-avatars {
  display: flex;
}

.sidebar-footer .footer-row .driver-info .s-avatars .s-av {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: var(--primary-hover);
  color: var(--primary);
  font-size: 9px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--bg-card);
  margin-right: -4px;
}

.sidebar-footer .footer-row .driver-info .s-text {
  font-size: 11px;
  color: var(--text-muted);
}

.sidebar-footer .footer-row .m-btn {
  color: var(--text-muted);
  cursor: pointer;
  transition: color 0.2s;
}

.sidebar-footer .footer-row .m-btn:hover {
  color: var(--primary);
}

/* 插件市场按钮 */
.plugin-market-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  flex-shrink: 0;
  padding: 6px 12px;
  border-radius: var(--radius-md);
  background: var(--primary-hover);
  color: var(--primary);
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  border: 1px solid transparent;
}

.plugin-market-btn:hover {
  border-color: var(--primary);
  background: var(--primary);
  color: #fff;
}

.plugin-market-icon {
  font-size: 13px;
}

.plugin-market-dot {
  position: absolute;
  top: 4px;
  right: 6px;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #ff4d4f;
  box-shadow: 0 0 0 2px var(--primary-hover);
  animation: dot-pulse 1.5s ease-in-out infinite;
}

@keyframes dot-pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(1.3); }
}

.sidebar-footer .footer-row .driver-info .s-avatars .s-av.s-av-real {
  overflow: hidden;
  padding: 0;
}

.sidebar-footer .footer-row .driver-info .s-avatars .s-av.s-av-real img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* ── 收起状态：导航列表 ── */
.sidebar-content.collapsed .nav-list {
  width: 100%;
}

.sidebar-content.collapsed .provider-list {
  align-items: center;
}

.sidebar-content.collapsed .provider-card {
  justify-content: center;
  padding: 8px;
  width: 40px;
  height: 40px;
  margin: 0 auto;
}

.sidebar-content.collapsed .provider-card.is-active::after {
  right: -4px;
}

.sidebar-content.collapsed .provider-avatar {
  margin-right: 0;
}

.sidebar-content.collapsed .provider-name {
  display: none;
}

.sidebar-content.collapsed .empty-provider {
  display: none;
}

/* ── 收起状态：全局管理 ── */
.sidebar-content.collapsed .global-section {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.sidebar-content.collapsed .global-item {
  justify-content: center;
  padding: 10px;
  width: 40px;
  height: 40px;
  margin: 2px auto;
}

.sidebar-content.collapsed .global-item.is-active::after {
  right: -4px;
}

.sidebar-content.collapsed .global-icon {
  margin-right: 0;
}

.sidebar-content.collapsed .global-label {
  display: none;
}

.sidebar-content.collapsed .global-count {
  display: none;
}

.sidebar-content.collapsed .expand-toggle {
  display: none;
}

.sidebar-content.collapsed .extra-items {
  display: none;
}

/* ── 收起状态：底部 footer ── */
.sidebar-content.collapsed .sidebar-footer {
  display: flex;
  justify-content: center;
}

.footer-collapsed {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  color: var(--text-muted);
  transition: all 0.2s;
}

.footer-collapsed:hover {
  color: var(--primary);
  background: var(--primary-hover);
}
</style>
