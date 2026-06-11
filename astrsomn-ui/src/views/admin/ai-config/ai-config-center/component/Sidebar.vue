<template>
  <SidebarShell :width="288" class="ai-config-sidebar">
    <template #top>
      <AstSearchInput
          v-model="searchText"
          class="sidebar-search-pill"
          layout="fluid"
          :placeholder="t.sidebar.searchPlaceholder"
          @search="handleSearch"
      />
    </template>

    <!-- 可滚动区域：模型提供方（在上） -->
    <div class="provider-section">
      <!-- 全部 -->
      <a-tooltip placement="right">
        <template #title>{{ t.sidebar.all }}</template>
        <div
            :class="{ 'is-active': activeItem === 'all' }"
            class="nav-item nav-item--all"
            @click="handleSelect('all')"
        >
          <PhSquaresFour
              :size="16"
              :weight="activeItem === 'all' ? 'fill' : 'bold'"
              class="nav-icon"
          />
          <span class="nav-name">{{ t.sidebar.all }}</span>
        </div>
      </a-tooltip>



      <!-- 插件提供方 -->
      <a-tooltip
          v-for="item in providers"
          :key="item.key"
          placement="right"
      >
        <template #title>{{ item.label }}</template>
        <div
            :class="{ 'is-active': activeItem === item.key }"
            class="nav-item"
            @click="handleSelect(item.key)"
        >
          <div class="nav-avatar">
            <img v-if="item.avatar" :alt="item.label" :src="item.avatar" class="avatar-img"/>
            <span v-else class="avatar-initial">{{ item.initial }}</span>
          </div>
          <span class="nav-name">{{ item.label }}</span>
        </div>
      </a-tooltip>

      <!-- 空态提示卡 -->
      <div v-if="providers.length === 0" class="plugin-warning-card">
        <p class="plugin-warning-text">{{ t.sidebar.emptyPluginHint }}</p>
        <button class="plugin-warning-btn" @click="handleAddPlugin">
          <PhStorefront :size="14" weight="regular"/>
          <span>{{ t.sidebar.goMarketplace }}</span>
        </button>
      </div>
    </div>

    <!-- 固定区域：全局管理（在底） -->
    <div class="global-section">
 

      <a-tooltip
          v-for="item in globalItems"
          :key="item.key"
          placement="right"
      >
        <template #title>{{ getItemLabel(item.key) }}</template>
        <div
            :class="{ 'is-active': activeItem === item.key }"
            class="nav-item"
            @click="handleSelect(item.key)"
        >
          <component
            :is="item.icon"
            :size="16"
            weight="regular"
            class="nav-icon"
          />
          <span class="nav-name">{{ getItemLabel(item.key) }}</span>
          <span v-if="item.count !== undefined" class="nav-count">{{ item.count }}</span>
        </div>
      </a-tooltip>
    </div>

    <template #footer>
      <SidebarFooter
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
  PhChatCenteredText,
  PhCode,
  PhIdentificationCard,
  PhLink,
  PhSquaresFour,
  PhStorefront,
  PhTerminalWindow,
  PhWrench,
} from '@phosphor-icons/vue'
import SidebarShell from '@/components/sidebar/SidebarShell.vue'
import SidebarFooter from '@/components/sidebar/SidebarFooter.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import {type SystemExtension, systemExtensionApi} from '@/api/systemExtension.ts'
import {aiConfigCenterApi} from '@/api/aiConfigCenter'
import {useDictionary} from '@/locales/dictionary'
import {usePageTranslation} from '@/locales/pages.ts'
import ExtensionMarketplaceDialog
  from '@/views/admin/system-config/system-extension/component/ExtensionMarketplaceDialog.vue'

const emit = defineEmits<{
  select: [key: string]
  'select-provider': [info: { id: string | number; key: string; name: string; description: string; avatar: string }]
}>()

const route = useRoute()
const providerDict = useDictionary('ai-model.provider')
const t = usePageTranslation('ai-config-center')

const searchText = ref('')
const activeItem = ref('')
const marketplaceOpen = ref(false)
const enabledExtensions = ref<Array<{ key: string; name: string; avatar: string; initial: string }>>([])
const providers = ref<Array<{
  id: string | number;
  key: string;
  label: string;
  icon: unknown;
  avatar?: string;
  initial: string;
  description: string
}>>([])

const globalItems = ref([
  {key: 'ai-account', icon: PhIdentificationCard, count: undefined as number | undefined},
  {key: 'prompts', icon: PhTerminalWindow, count: undefined as number | undefined},
  {key: 'mcp', icon: PhLink, count: undefined as number | undefined},
  {key: 'tools', icon: PhWrench, count: undefined as number | undefined},
  {key: 'ftl', icon: PhCode, count: undefined as number | undefined},
  {key: 'conversations', icon: PhChatCenteredText, count: undefined as number | undefined},
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
    emit('select-provider', {id: provider.id, key: provider.key, name: provider.label, description: provider.description, avatar: provider.avatar || ''})
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
        id: item.id!,
        key: code,
        label: name,
        icon: PhSquaresFour,
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
    const globalKeys = [...globalItems.value.map(item => item.key), ...globalItems.value.map(item => item.key)]
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
  const globalKeys = [...globalItems.value.map(item => item.key), ...globalItems.value.map(item => item.key)]
  if (globalKeys.includes(lastPart)) {
    activeItem.value = lastPart
    return
  }


  activeItem.value = 'all'
}

const countFieldMap: Record<string, keyof import('@/api/aiConfigCenter').AiConfigCenterCounts> = {
  'ai-account': 'aiAccountCount',
  'prompts': 'aiPromptCount',
  'mcp': 'aiMcpCount',
  'tools': 'aiToolCount',
  'ftl': 'aiTemplateCount',
  'conversations': 'aiChatSessionCount',
}

const fetchCounts = async () => {
  try {
    const counts = await aiConfigCenterApi.counts()
    globalItems.value.forEach((item) => {
      const field = countFieldMap[item.key]
      item.count = field ? (counts[field] ?? 0) : 0
    })
  } catch {
    // ignore
  }
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
.sidebar-search-pill {
  flex: 1;
  min-width: 0;
  border: none;
}

/* ── 覆盖 SidebarShell 主体（取消滚动，改为 flex 布局） ── */
.ai-config-sidebar :deep(.ast-sidebar-body) {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 0;
}

/* ── 可滚动区域：模型提供方（在上） ── */
.provider-section {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 6px 8px 2px;
  display: flex;
  flex-direction: column;
  gap: 2px;

  /* ── 美化滚动条 ── */
  scrollbar-width: thin;
  scrollbar-color: var(--scrollbar-thumb) transparent;
}
.provider-section::-webkit-scrollbar {
  width: 5px;
}
.provider-section::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 3px;
}
.provider-section::-webkit-scrollbar-thumb {
  background: var(--scrollbar-thumb, rgba(128, 128, 128, 0.3));
  border-radius: 3px;
  transition: background 0.2s ease;
}
.provider-section::-webkit-scrollbar-thumb:hover {
  background: var(--scrollbar-thumb-hover, rgba(128, 128, 128, 0.5));
}

/* ── 固定区域：全局管理（在底） ── */
.global-section {
  flex-shrink: 0;
  padding: 2px 8px 8px;
  display: flex;
  flex-direction: column;
  gap: 2px;
  margin-top: 4px;
}

/* ── 区域标签 ── */
.section-label {
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 0.05em;
  color: var(--text-muted);
  padding: 4px 6px 1px;
  text-transform: uppercase;
  opacity: 0.7;
}
.section-label.slim {
  padding: 3px 6px 1px;
  opacity: 0.55;
  font-size: 9px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  font-size: 12px;
  color: var(--text-secondary);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: transparent;
  border-left: 3px solid transparent;
}

.nav-item:hover {
  background: var(--bg-input);
  color: var(--text-primary);
}

.nav-item:hover .nav-icon {
  color: var(--primary);
}

.nav-item.is-active {
  background: rgba(59, 130, 246, 0.10);
  color: var(--primary);
  font-weight: 500;
  border-left-color: var(--primary);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.15);
}

.nav-item.is-active .nav-icon {
  color: var(--primary);
}

.nav-icon {
  flex-shrink: 0;
  color: var(--text-muted);
  transition: color 0.2s ease;
}

.nav-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.nav-count {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 999px;
  background: var(--bg-input);
  color: var(--text-secondary);
  font-weight: 500;
  line-height: 1.4;
  flex-shrink: 0;
}

.nav-item.is-active .nav-count {
  background: rgba(59, 130, 246, 0.18);
  color: var(--primary);
  font-weight: 600;
}

/* ── 插件提供方头像 ── */
.nav-avatar {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
  background: var(--bg-input);
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
  background: rgba(59, 130, 246, 0.12);
}

/* ── 空态提示卡（设计稿 plugin-warning-card） ── */
.plugin-warning-card {
  margin: 8px 0 12px;
  padding: 14px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px dashed var(--border-default);
  border-radius: 12px;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.plugin-warning-text {
  font-size: 11px;
  line-height: 1.55;
  color: var(--text-secondary);
}

.plugin-warning-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  width: 100%;
  height: 32px;
  padding: 0 12px;
  font-size: 11px;
  font-weight: 500;
  color: #ffffff;
  background: var(--primary);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.plugin-warning-btn:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

.plugin-warning-btn:active {
  transform: scale(0.98);
}
</style>
