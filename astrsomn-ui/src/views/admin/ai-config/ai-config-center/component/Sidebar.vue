<template>
  <div class="sidebar-content">
    <!-- 搜索框 -->
    <div class="search-section">
      <a-input
        v-model:value="searchText"
        placeholder="搜索资源..."
        class="search-input"
        size="small"
        prefix-icon="SearchOutlined"
      />
    </div>

    <!-- 导航列表 -->
    <div class="nav-list">

      <div class="nav-section">

        <div class="provider-list">
          <!-- 全部选项 -->
          <div
            class="provider-card"
            :class="{ 'is-active': activeItem === 'all' }"
            @click="handleSelect('all')"
          >
            <div class="provider-avatar">
              <component :is="CloudServerOutlined" class="all-icon" />
            </div>
            <span class="provider-name">全部</span>
          </div>
          <!-- 各模型提供商 -->
          <div
            v-for="item in providers"
            :key="item.key"
            class="provider-card"
            :class="{ 'is-active': activeItem === item.key }"
            @click="handleSelect(item.key)"
          >
            <div class="provider-avatar">
              <img v-if="item.avatar" :src="item.avatar" :alt="item.label" class="avatar-img" />
              <span v-else class="avatar-initial">{{ item.initial }}</span>
            </div>
            <span class="provider-name">{{ item.label }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 全局管理（固定在底部） -->
    <div class="global-section">
     
      <a-menu mode="inline" class="global-menu">
        <a-menu-item
          v-for="item in globalItems"
          :key="item.key"
          :class="{ 'is-active': activeItem === item.key }"
          @click="handleSelect(item.key)"
        >
          <component :is="item.icon" class="global-icon" />
          <span>{{ item.label }}</span>
        </a-menu-item>
      </a-menu>
    </div>

    <!-- 底部拓展中心入口 -->
    <div class="sidebar-footer">
      <div class="footer-row">
        <div class="driver-info">
          <div class="s-avatars">
            <span class="s-av">-</span>
          </div>
          <span class="s-text">暂无已启用扩展</span>
        </div>
        <AppstoreOutlined class="m-btn" title="打开插件市场" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import {
  SearchOutlined,
  CloudServerOutlined,
  CreditCardOutlined,
  LayoutOutlined,
  LinkOutlined,
  ToolOutlined,
  AppstoreOutlined,
  FileTextOutlined,
} from '@ant-design/icons-vue'
import { systemExtensionApi, type SystemExtension } from '@/api/systemExtension.ts'

const emit = defineEmits(['select'])
const route = useRoute()

const searchText = ref('')
const activeItem = ref('')
const providers = ref<Array<{ key: string; label: string; icon: typeof CloudServerOutlined; avatar?: string; initial: string }>>([])

const globalItems = [
  { key: 'ai-account', label: 'AI 账号', icon: CreditCardOutlined },
  { key: 'prompts', label: '提示词', icon: FileTextOutlined },
  { key: 'mcp', label: 'MCP', icon: LinkOutlined },
  { key: 'tools', label: 'Tools', icon: ToolOutlined },
]

const handleSelect = (key: string) => {
  activeItem.value = key
  emit('select', key)
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
      const name = item.extensionName || item.extensionKey || '未知插件'
      return {
        key: String(item.id ?? item.extensionKey ?? item.extensionCode ?? ''),
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
    if (provider && providers.value.some(p => p.key === provider)) {
      activeItem.value = provider
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
  updateActiveItem()
})

watch(
  () => [route.path, route.query.view, route.query.provider],
  () => {
    updateActiveItem()
  },
  { deep: true }
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
  margin-bottom: 16px;
}

.search-input {
  background: var(--bg-input);
  border-color: var(--border-default);
  border-radius: var(--radius-md);
  font-size: 13px;
}

.search-input :deep(.ant-input-prefix) {
  color: var(--text-muted);
}

.search-input :deep(.ant-input) {
  color: var(--text-primary);
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
  bottom: 52px;
  left: 16px;
  right: 16px;
  padding-top: 12px;
  border-top: 1px solid var(--border-default);
}

.global-section .nav-section-title {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-muted);
  padding: 6px 0 4px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.global-menu {
  border: none;
  background: transparent;
}

.global-menu :deep(.ant-menu-item) {
  margin: 2px 0;
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  font-size: 13px;
  color: var(--text-secondary);
  transition: all 0.2s;
}

.global-menu :deep(.ant-menu-item:hover) {
  background: var(--primary-hover);
  color: var(--primary);
}

.global-menu :deep(.ant-menu-item.is-active) {
  background: var(--primary-hover);
  color: var(--primary);
  font-weight: 500;
}

.global-icon {
  font-size: 14px;
  margin-right: 10px;
}

.global-menu :deep(.ant-menu-item.is-active) .global-icon {
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
</style>
