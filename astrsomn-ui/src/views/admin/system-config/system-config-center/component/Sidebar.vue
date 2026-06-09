<template>
  <SidebarShell>
    <div class="nav-list">
      <div class="nav-items">
        <div
          :class="{ 'is-active': activeItem === 'all' }"
          class="nav-card"
          @click="handleSelect('all')"
        >
          <div class="nav-avatar nav-avatar--all">
            <AppstoreOutlined class="nav-icon--all" />
          </div>
          <span class="nav-name">{{ t?.sidebar?.all ?? '全部' }}</span>
        </div>
        <div
          v-for="item in menuItems"
          :key="item.key"
          :class="{ 'is-active': activeItem === item.key }"
          class="nav-card"
          @click="handleSelect(item.key)"
        >
          <div class="nav-avatar">
            <component :is="item.icon" class="nav-icon" />
          </div>
          <span class="nav-name">{{ item.label }}</span>
        </div>
      </div>
    </div>
  </SidebarShell>
</template>

<script lang="ts" setup>
import {computed, ref, watch} from 'vue'
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
import {usePageTranslation} from '@/locales/pages'

const emit = defineEmits<{
  select: [key: string]
}>()

const route = useRoute()
const t = usePageTranslation('system-config-center')

const menuItems = computed(() => {
  const s = t.value?.sidebar
  if (!s) return []
  return [
    { key: 'users', label: s.users, icon: UserOutlined },
    { key: 'env', label: s.env, icon: ClusterOutlined },
    { key: 'config', label: s.config, icon: SettingOutlined },
    { key: 'messages', label: s.messages, icon: AlertOutlined },
    { key: 'extensions', label: s.extensions, icon: ApiOutlined },
  ]
})

const activeItem = ref('all')

const updateActiveItem = () => {
  const view = route.query.view as string | undefined
  const validKeys = menuItems.value.map(item => item.key)
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
  { immediate: true }
)
</script>

<style scoped>
.nav-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.nav-items {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nav-card {
  display: flex;
  align-items: center;
  padding: 9px 12px;
  border-radius: var(--radius-md);
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.nav-card:hover {
  background: var(--primary-hover);
  color: var(--primary);
}

.nav-card.is-active {
  background: var(--primary-hover);
  color: var(--primary);
  font-weight: 600;
}

.nav-card.is-active::before {
  content: '';
  position: absolute;
  left: -2px;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  border-radius: 0 2px 2px 0;
  background: var(--primary);
}

.nav-avatar {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-md);
  margin-right: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
  background: var(--bg-input);
  transition: background 0.2s;
}

.nav-card:hover .nav-avatar,
.nav-card.is-active .nav-avatar {
  background: var(--primary-hover);
}

.nav-avatar--all {
  background: rgba(59, 130, 246, 0.12);
}

.nav-card:hover .nav-avatar--all,
.nav-card.is-active .nav-avatar--all {
  background: rgba(59, 130, 246, 0.2);
}

.nav-icon {
  font-size: 15px;
  color: var(--text-secondary);
  transition: color 0.2s;
}

.nav-icon--all {
  font-size: 14px;
  color: var(--primary);
}

.nav-card:hover .nav-icon,
.nav-card.is-active .nav-icon {
  color: var(--primary);
}

.nav-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
