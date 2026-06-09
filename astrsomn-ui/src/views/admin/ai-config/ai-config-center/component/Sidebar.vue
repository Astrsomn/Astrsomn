<template>
  <SidebarShell :collapsed="collapsed" @toggle-collapse="toggleCollapsed">
    <template #top>
      <AstSearchInput
          v-if="!collapsed"
          v-model="searchText"
      
          layout="fluid"
          :placeholder="t.sidebar.searchPlaceholder"
          @search="handleSearch"
      />
    </template>

    <div style="display: flex; flex-direction: column; height: 100%;">
      <!-- 导航列表 -->
      <div class="nav-list">
        <div class="nav-section">
          <div class="provider-list">
            <a-tooltip :disabled="!collapsed" placement="right">
              <template #title>{{ t.sidebar.all }}</template>
              <div
                  :class="{ 'is-active': activeItem === 'all' }"
                  class="provider-card"
                  @click="handleSelect('all')"
              >
                <div class="provider-avatar">
                  <component :is="CloudServerOutlined" class="all-icon"/>
                </div>
                <span class="provider-name">{{ t.sidebar.all }}</span>
              </div>
            </a-tooltip>
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
            <div class="empty-provider-hint">
              <span class="empty-provider-text">{{ t.sidebar.emptyPluginHint }}</span>
              <a-button block size="small" type="primary" @click="handleAddPlugin">
                <template #icon>
                  <MarketplaceIcon/>
                </template>
                {{ t.sidebar.goMarketplace }}
              </a-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 全局管理 -->
      <div class="global-section">
        <a-tooltip
            v-for="item in globalItems"
            :key="item.key"
            :disabled="!collapsed"
            placement="right"
        >
          <template #title>{{ getItemLabel(item.key) }}</template>
          <div
              :class="{ 'is-active': activeItem === item.key }"
              class="global-item"
              @click="handleSelect(item.key)"
          >
            <component :is="item.icon" class="global-icon"/>
            <span class="global-label">{{ getItemLabel(item.key) }}</span>
            <span v-if="item.count !== undefined" class="global-count">{{ item.count }}</span>
          </div>
        </a-tooltip>

        <div v-show="!collapsed" class="extra-items">
          <div
              v-for="item in extraItems"
              :key="item.key"
              :class="{ 'is-active': activeItem === item.key }"
              class="global-item"
              @click="handleSelect(item.key)"
          >
            <component :is="item.icon" class="global-icon"/>
            <span class="global-label">{{ getItemLabel(item.key) }}</span>
            <span v-if="item.count !== undefined" class="global-count">{{ item.count }}</span>
          </div>
        </div>
      </div>
    </div>


    <template #footer>
      <SidebarFooter
          :collapsed="collapsed"
          :enabled-extensions="enabledExtensions"
          :show-dot="!providers.length"
          @open-marketplace="goPluginMarketplace"
      />
    </template>
  </SidebarShell>

  <ExtensionMarketplaceDialog
      :open="marketplaceOpen"
      @cancel="marketplaceOpen = false"
      @update:open="marketplaceOpen = $event"
  />
</template>

<script lang="ts" setup>
import {onMounted, ref, watch} from 'vue'
import {useRoute} from 'vue-router'
import {
  AppstoreOutlined,
  CloudServerOutlined,
  CreditCardOutlined,
  FileTextOutlined,
  LinkOutlined,
  MessageOutlined,
  ToolOutlined
} from '@ant-design/icons-vue'
import SidebarShell from '@/components/sidebar/SidebarShell.vue'
import SidebarFooter from '@/components/sidebar/SidebarFooter.vue'
import {type SystemExtension, systemExtensionApi} from '@/api/systemExtension.ts'
import {aiAccountApi} from '@/api/aiAccount'
import {aiPromptApi} from '@/api/aiPrompt'
import {aiMcpApi} from '@/api/aiMcp'
import {aiToolApi} from '@/api/aiTool'
import {aiTemplateApi} from '@/api/aiTemplate'
import {aiConversationApi} from '@/api/aiConversation'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import {useDictionary} from '@/locales/dictionary'
import {usePageTranslation} from '@/locales/pages.ts'
import ExtensionMarketplaceDialog
  from '@/views/admin/system-config/system-extension/component/ExtensionMarketplaceDialog.vue'

const emit = defineEmits<{
  select: [key: string]
  'select-provider': [info: { key: string; name: string; description: string; avatar: string }]
  'update:collapsed': [value: boolean]
}>()

// 空态"前往插件市场"按钮图标；占位 const 让 vue-tsc 把 import 视为已使用。
const MarketplaceIcon = AppstoreOutlined

const collapsed = ref(false)
const toggleCollapsed = () => {
  collapsed.value = !collapsed.value
  emit('update:collapsed', collapsed.value)
}
const route = useRoute()
const providerDict = useDictionary('ai-model.provider')
const t = usePageTranslation('ai-config-center')

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
  {key: 'ai-account', icon: CreditCardOutlined, count: undefined as number | undefined},
  {key: 'prompts', icon: FileTextOutlined, count: undefined as number | undefined},
  {key: 'mcp', icon: LinkOutlined, count: undefined as number | undefined},
  {key: 'tools', icon: ToolOutlined, count: undefined as number | undefined},
])

const extraItems = ref([
  {key: 'ftl', icon: FileTextOutlined, count: undefined as number | undefined},
  {key: 'conversations', icon: MessageOutlined, count: undefined as number | undefined},
])

const getItemLabel = (key: string): string => {
  const labelMap: Record<string, keyof typeof t.value.sidebar> = {
    'ai-account': 'aiAccount',
    'prompts': 'prompts',
    'mcp': 'mcp',
    'tools': 'tools',
    'ftl': 'ftl',
    'conversations': 'conversations',
  }
  return t.value.sidebar[labelMap[key]] || key
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
      const name = String(item.extensionName || item.extensionKey || t.value.sidebar.extension)
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
      const name = dictLabel || item.extensionName || item.extensionKey || t.value.sidebar.unknownPlugin
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


  if (currentPath === '/admin/ai-config-center') {

    const view = route.query.view as string | undefined
    const globalKeys = [...globalItems.value.map(item => item.key), ...extraItems.value.map(item => item.key)]
    if (view && globalKeys.includes(view)) {
      activeItem.value = view
      return
    }


    const provider = route.query.provider as string | undefined
    if (provider && providers.value.some(p => p.key === provider.toLowerCase())) {
      activeItem.value = provider.toLowerCase()
      return
    }


    activeItem.value = 'all'
    return
  }


  const pathParts = currentPath.split('/')
  const lastPart = pathParts[pathParts.length - 1]
  const globalKeys = [...globalItems.value.map(item => item.key), ...extraItems.value.map(item => item.key)]
  if (globalKeys.includes(lastPart)) {
    activeItem.value = lastPart
    return
  }


  activeItem.value = 'all'
}

const fetchCount = async (api: { queryPage: (p: unknown) => Promise<{ total?: number }> }, list: typeof globalItems, idx: number) => {
  try {
    const resp = await api.queryPage({pageNo: 1, pageSize: 1})
    list.value[idx].count = resp.total ?? 0
  } catch {

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

.sidebar-search-pill:hover {
  border-color: var(--border-default);
  box-shadow: none;
  transform: none;
}

.sidebar-search-pill:focus-within {
  border-color: var(--primary);
  box-shadow: none;
}

.sidebar-search-pill :deep(.toolbar-search-pill__left-icon) {
  font-size: 14px;
  color: var(--text-muted);
}

.sidebar-search-pill :deep(.toolbar-search-pill__input) {
  font-size: 13px;
}



.nav-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
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


.global-section {
  margin-top: auto;
  padding-top: 12px;
  //border-top: 1px solid var(--border-default);
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


.empty-provider {
  padding: 8px 12px;
}

.empty-provider-hint {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px;
  border-radius: var(--radius-md);
  background: var(--bg-input);
  border: 1px dashed var(--border-default);
}

.empty-provider-text {
  font-size: 12px;
  line-height: 1.5;
  color: var(--text-secondary);
}


.ast-sidebar.collapsed .nav-list {
  width: 100%;
}

.ast-sidebar.collapsed .provider-list {
  align-items: center;
}

.ast-sidebar.collapsed .provider-card {
  justify-content: center;
  padding: 8px;
  width: 40px;
  height: 40px;
  margin: 0 auto;
}

.ast-sidebar.collapsed .provider-card.is-active::after {
  right: -4px;
}

.ast-sidebar.collapsed .provider-avatar {
  margin-right: 0;
}

.ast-sidebar.collapsed .provider-name {
  display: none;
}

.ast-sidebar.collapsed .empty-provider {
  display: none;
}


.ast-sidebar.collapsed .global-section {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.ast-sidebar.collapsed .global-item {
  justify-content: center;
  padding: 10px;
  width: 40px;
  height: 40px;
  margin: 2px auto;
}

.ast-sidebar.collapsed .global-item.is-active::after {
  right: -4px;
}

.ast-sidebar.collapsed .global-icon {
  margin-right: 0;
}

.ast-sidebar.collapsed .global-label {
  display: none;
}

.ast-sidebar.collapsed .global-count {
  display: none;
}

.ast-sidebar.collapsed .extra-items {
  display: none;
}

</style>
