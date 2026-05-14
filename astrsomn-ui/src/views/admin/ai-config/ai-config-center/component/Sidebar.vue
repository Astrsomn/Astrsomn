<template>
  <div class="sidebar-content">
    <!-- 搜索框 + 添加插件按钮 -->
    <div class="search-section">
      <AstrsomnSearchPill
          v-model="searchText"
          class="sidebar-search-pill"
          layout="fluid"
          placeholder="搜索资源..."
          @search="handleSearch"
      />
      <AppstoreOutlined class="add-plugin-btn" title="添加插件" @click="handleAddPlugin"/>
    </div>

    <!-- 导航列表 -->
    <div class="nav-list">

      <div class="nav-section">

        <div class="provider-list">
          <!-- 全部选项 -->
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
          <!-- 各模型提供商 -->
          <div
              v-for="item in providers"
              :key="item.key"
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
        </div>
      </div>
    </div>

    <!-- 全局管理（固定在底部） -->
    <div class="global-section">
      <div
          v-for="item in globalItems"
          :key="item.key"
          :class="{ 'is-active': activeItem === item.key }"
          class="global-item"
          @click="handleSelect(item.key)"
      >
        <component :is="item.icon" class="global-icon"/>
        <span>{{ item.label }}</span>
      </div>
    </div>

    <!-- 底部拓展中心入口 -->
    <div class="sidebar-footer">
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
        <AppstoreOutlined class="m-btn" title="打开插件市场" @click="goPluginMarketplace"/>
      </div>
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
  FileTextOutlined,
  LinkOutlined,
  ToolOutlined
} from '@ant-design/icons-vue'
import {type SystemExtension, systemExtensionApi} from '@/api/systemExtension.ts'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import {useDictionary} from '@/locales/dictionary'
import ExtensionMarketplaceDialog from './ExtensionMarketplaceDialog.vue'

const emit = defineEmits(['select'])
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
  initial: string
}>>([])

const globalItems = [
  {key: 'ai-account', label: 'AI 账号', icon: CreditCardOutlined},
  {key: 'prompts', label: '提示词', icon: FileTextOutlined},
  {key: 'mcp', label: 'MCP', icon: LinkOutlined},
  {key: 'tools', label: 'Tools', icon: ToolOutlined},
]

const handleSelect = (key: string) => {
  activeItem.value = key
  emit('select', key)
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
    const modelProviders = (resp.list || []).map((item: SystemExtension) => {
      const code = (item.extensionCode || item.extensionKey || '').toLowerCase()
      const dictLabel = providerDict.value.getLabel(code)
      const name = dictLabel || item.extensionName || item.extensionKey || '未知插件'
      return {
        key: code,
        label: name,
        icon: CloudServerOutlined,
        avatar: item.avatar,
        initial: name.slice(0, 1).toUpperCase(),
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
    const globalKeys = globalItems.map(item => item.key)
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
  const globalKeys = globalItems.map(item => item.key)
  if (globalKeys.includes(lastPart)) {
    activeItem.value = lastPart
    return
  }

  // 默认选中"全部"
  activeItem.value = 'all'
}

onMounted(() => {
  void fetchProviders()
  void fetchEnabledExtensions()
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
  position: relative;
  background: var(--bg-card);
}

/* 搜索框区域 */
.search-section {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.search-section .sidebar-search-pill {
  flex: 1;
  min-width: 0;
}

.search-section .add-plugin-btn {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-md);
  color: var(--text-muted);
  cursor: pointer;
  transition: all 0.2s;
  font-size: 15px;
}

.search-section .add-plugin-btn:hover {
  color: var(--primary);
  background: var(--primary-hover);
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
  position: absolute;
  bottom: 56px;
  left: 16px;
  right: 16px;
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
  margin-bottom: 6px;
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

.global-item.is-active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
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

/* 底部拓展中心入口 */
.sidebar-footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 14px;
  border-top: 1px solid var(--border-default);
  background: var(--bg-card);
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

.sidebar-footer .footer-row .driver-info .s-avatars .s-av.s-av-real {
  overflow: hidden;
  padding: 0;
}

.sidebar-footer .footer-row .driver-info .s-avatars .s-av.s-av-real img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
