<template>
  <SidebarShell :collapsed="collapsed" @toggle-collapse="toggleCollapsed">
    <div style="display: flex; flex-direction: column; height: 100%;">
      <div class="nav-list">
        <div class="nav-section">
          <div class="nav-items">
            <a-tooltip :disabled="!collapsed" placement="right">
              <template #title>全部</template>
              <div
                  :class="{ 'is-active': activeItem === 'all' }"
                  class="nav-card"
                  @click="handleSelect('all')"
              >
                <div class="nav-avatar">
                  <AppstoreOutlined class="all-icon"/>
                </div>
                <span class="nav-name">全部</span>
              </div>
            </a-tooltip>
            <a-tooltip
                v-for="item in menuItems"
                :key="item.key"
                :disabled="!collapsed"
                placement="right"
            >
              <template #title>{{ item.label }}</template>
              <div
                  :class="{ 'is-active': activeItem === item.key }"
                  class="nav-card"
                  @click="handleSelect(item.key)"
              >
                <div class="nav-avatar">
                  <component :is="item.icon" class="nav-icon"/>
                </div>
                <span class="nav-name">{{ item.label }}</span>
              </div>
            </a-tooltip>
          </div>
        </div>
      </div>
    </div>
  </SidebarShell>
</template>

<script lang="ts" setup>
import {ref, watch} from 'vue'
import {useRoute} from 'vue-router'
import {
  AlertOutlined,
  ApiOutlined,
  AppstoreOutlined,
  ClusterOutlined,
  SettingOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'
import SidebarShell from '@/components/sidebar/SidebarShell.vue'

const emit = defineEmits<{
  select: [key: string]
  'update:collapsed': [value: boolean]
}>()

const route = useRoute()

const collapsed = ref(false)
const toggleCollapsed = () => {
  collapsed.value = !collapsed.value
  emit('update:collapsed', collapsed.value)
}

const menuItems = [
  {key: 'users', label: '用户管理', icon: UserOutlined},
  {key: 'env', label: '环境管理', icon: ClusterOutlined},
  {key: 'config', label: '系统配置', icon: SettingOutlined},
  {key: 'messages', label: '系统消息', icon: AlertOutlined},
  {key: 'extensions', label: '系统扩展', icon: ApiOutlined},
]

const activeItem = ref('all')

const updateActiveItem = () => {
  const view = route.query.view as string | undefined
  const validKeys = menuItems.map(item => item.key)
  if (view && validKeys.includes(view)) {
    activeItem.value = view
    return
  }
  activeItem.value = 'all'
}

const handleSelect = (key: string) => {
  activeItem.value = key
  emit('select', key)
}

watch(
    () => route.query.view,
    () => {
      updateActiveItem()
    },
    {immediate: true}
)
</script>

<style scoped>
.nav-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.nav-section {
  margin-bottom: 20px;
}

.nav-items {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-card {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.nav-card:hover {
  background: var(--primary-hover);
  color: var(--primary);
}

.nav-card.is-active {
  background: var(--primary-hover);
  color: var(--primary);
  font-weight: 500;
  position: relative;
}

.nav-card.is-active::after {
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

.nav-avatar {
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

.all-icon {
  font-size: 12px;
  color: var(--primary);
}

.nav-icon {
  font-size: 14px;
  color: var(--text-secondary);
}

.nav-card.is-active .nav-icon {
  color: var(--primary);
}

.nav-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}


.ast-sidebar.collapsed .nav-items {
  align-items: center;
}

.ast-sidebar.collapsed .nav-card {
  justify-content: center;
  padding: 8px;
  width: 40px;
  height: 40px;
  margin: 0 auto;
}

.ast-sidebar.collapsed .nav-card.is-active::after {
  right: -4px;
}

.ast-sidebar.collapsed .nav-avatar {
  margin-right: 0;
}

.ast-sidebar.collapsed .nav-name {
  display: none;
}
</style>
