<template>
  <SidebarShell :width="288">
    <div class="nav-list">
      <div class="nav-items">
        <div
          :class="{ 'is-active': activeItem === 'all' }"
          class="nav-item"
          @click="handleSelect('all')"
        >
          <PhSquaresFour
              :size="16"
              :weight="activeItem === 'all' ? 'fill' : 'bold'"
              class="nav-icon"
          />
          <span class="nav-name">{{ t?.sidebar?.all ?? '全部' }}</span>
        </div>
        <div
          v-for="item in menuItems"
          :key="item.key"
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
  PhBell,
  PhGlobe,
  PhPuzzlePiece,
  PhSliders,
  PhSquaresFour,
  PhUsers,
} from '@phosphor-icons/vue'
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
    { key: 'users', label: s.users, icon: PhUsers },
    { key: 'env', label: s.env, icon: PhGlobe },
    { key: 'config', label: s.config, icon: PhSliders },
    { key: 'messages', label: s.messages, icon: PhBell },
    { key: 'extensions', label: s.extensions, icon: PhPuzzlePiece },
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
  gap: 4px;
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
</style>
