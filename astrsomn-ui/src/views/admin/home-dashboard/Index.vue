<template>
  <div class="dashboard">
    <div class="dashboard-container">
      <TopSwitch v-model="viewMode" />

      <transition name="dashboard-mode" mode="out-in">
        <AdminDataScreen
          v-if="viewMode === 'screen'"
          key="screen"
          :groups="managementGroups"
          :role-label="roleLabel"
          @navigate="navigateTo"
        />

        <AdminManagementView
          v-else
          key="management"
          :groups="managementGroups"
          @navigate="navigateTo"
        />
      </transition>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import TopSwitch from './TopSwitch.vue'
import AdminDataScreen from './screen/AdminDataScreen.vue'
import AdminManagementView from '../home-dashboard/backend/AdminManagementView.vue'
import { getCurrentUserRole, resolveManagementGroups } from '../home-dashboard/backend/management'

type DashboardMode = 'screen' | 'management'

const viewModeStorageKey = 'admin-dashboard:view-mode'

const router = useRouter()
const viewMode = ref<DashboardMode>('screen')

const currentRole = computed(() => getCurrentUserRole())

const managementGroups = computed(() => resolveManagementGroups(currentRole.value))

const roleLabel = computed(() => (currentRole.value === 'SUPER_ADMIN' ? '超级管理员' : '管理员'))

onMounted(() => {
  const cachedMode = localStorage.getItem(viewModeStorageKey)
  if (cachedMode === 'screen' || cachedMode === 'management') {
    viewMode.value = cachedMode
  }
})

watch(viewMode, (mode) => {
  localStorage.setItem(viewModeStorageKey, mode)
})

const navigateTo = (path: string) => {
  void router.push(path)
}
</script>

<style scoped>
.dashboard {
  padding: 24px;
  background-color: var(--bg-base);
}

.dashboard-container {
  max-width: 1600px;
  margin: 0 auto;
}

.dashboard-mode-enter-active,
.dashboard-mode-leave-active {
  transition:
    opacity 0.24s ease,
    transform 0.24s ease;
}

.dashboard-mode-enter-from,
.dashboard-mode-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

@media (max-width: 1024px) {
  .dashboard {
    padding: 16px;
  }
}
</style>