<template>
  <div v-if="visible && isLoggedIn" class="env-capsule-minimal">
    <div class="env-content">
      <a-spin v-if="workspaceLoading" size="small"/>
      <template v-else-if="workspaceContext">
        <a-dropdown
            v-if="workspaceContext.canSwitchWorkspace && envPickOptions.length > 0"
            :trigger="['click']"
            placement="bottomRight"
        >
          <div class="env-trigger-btn">
            <cloud-server-outlined class="env-icon-small"/>
            <span class="env-label-text">{{ currentEnvDisplay }}</span>
            <down-outlined class="env-caret-small"/>
          </div>
          <template #overlay>
            <a-menu :selected-keys="[workspaceContext.effectiveEnvCode]" class="env-menu-pop" @click="onEnvMenuPick">
              <a-menu-item v-for="option in envPickOptions" :key="option.value">{{ option.label }}</a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
        <div v-else class="env-readonly-text">{{ workspaceContext.effectiveEnvCode }}</div>
      </template>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: 'WorkspaceEnvSwitcher',
}
</script>

<script lang="ts" setup>
import {computed, onMounted, ref} from 'vue'
import {CloudServerOutlined, DownOutlined} from '@ant-design/icons-vue'
import {getWorkspaceEnv, type WorkspaceEnvContext} from '../../api/auth'
import {systemEnvApi} from '../../api/systemEnv'
import {WORKSPACE_ENV_STORAGE_KEY} from '../../constants/workspaceEnv'

withDefaults(defineProps<{
  visible?: boolean
}>(), {
  visible: true,
})

const isLoggedIn = computed(() => !!localStorage.getItem('token'))
const workspaceLoading = ref(false)
const workspaceContext = ref<WorkspaceEnvContext | null>(null)
const envPickOptions = ref<Array<{ label: string; value: string }>>([])

const currentEnvDisplay = computed(() => {
  const ctx = workspaceContext.value
  if (!ctx) return ''
  const option = envPickOptions.value.find((item) => item.value === ctx.effectiveEnvCode)
  return option?.label ?? ctx.effectiveEnvCode
})

async function loadWorkspaceContext() {
  if (!isLoggedIn.value) return

  workspaceLoading.value = true
  try {
    const workspace = await getWorkspaceEnv()
    workspaceContext.value = workspace

    if (!localStorage.getItem(WORKSPACE_ENV_STORAGE_KEY)?.trim() && workspace.effectiveEnvCode) {
      localStorage.setItem(WORKSPACE_ENV_STORAGE_KEY, String(workspace.effectiveEnvCode).trim())
    }

    if (workspace.canSwitchWorkspace) {
      const response = await systemEnvApi.queryPage({pageNo: 1, pageSize: 200, param: {}})
      envPickOptions.value = (response.list || []).map((row) => ({
        value: String(row.envKey),
        label: row.envName ? `${row.envName}` : String(row.envKey),
      }))
    }
  } catch (error) {
    console.error(error)
  } finally {
    workspaceLoading.value = false
  }
}

function onEnvMenuPick(info: { key: string | number }) {
  const key = String(info.key)
  if (key === workspaceContext.value?.effectiveEnvCode) return

  localStorage.setItem(WORKSPACE_ENV_STORAGE_KEY, key)
  window.location.reload()
}

onMounted(loadWorkspaceContext)
</script>

<style scoped>
.env-capsule-minimal {
  border-radius: 8px;
  height: 32px;
  display: flex;
  align-items: center;
  transition: all 0.2s;
}

.env-trigger-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 0 10px;
  cursor: pointer;
}

.env-icon-small {
  font-size: 13px;
  color: var(--text-secondary);
}

.env-label-text,
.env-readonly-text {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-heading);
}

.env-caret-small {
  font-size: 10px;
  color: var(--text-muted);
}

.env-trigger-btn:hover .env-icon-small {
  color: var(--text-primary);
}

.env-trigger-btn:hover .env-label-text,
.env-trigger-btn:hover .env-readonly-text {
  color: var(--text-primary);
}

@media (max-width: 768px) {
  .env-label-text,
  .env-readonly-text {
    display: none;
  }
}
</style>
