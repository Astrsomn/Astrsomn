<template>
  <div v-if="visible && isLoggedIn" class="env-capsule-minimal" :class="envThemeClass">
    <div class="env-content">
      <a-spin v-if="workspaceLoading" size="small"/>
      <template v-else-if="workspaceContext">
        <a-dropdown
            v-if="workspaceContext.canSwitchWorkspace && envPickOptions.length > 0"
            :trigger="['click']"
            placement="bottomRight"
        >
          <div class="env-trigger-btn">
            <span class="env-dot"></span>
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
        <div v-else class="env-readonly-text">
          <span class="env-dot"></span>
          {{ workspaceContext.effectiveEnvCode }}
        </div>
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

const envThemeClass = computed(() => {
  const code = (workspaceContext.value?.effectiveEnvCode || '').toLowerCase()
  if (!code) return 'env-theme-default'
  if (/(dev|develop|development|local)/.test(code)) return 'env-theme-dev'
  if (/(test|testing|qa|sit)/.test(code)) return 'env-theme-test'
  if (/(stag|staging|pre|uat|gray|grey)/.test(code)) return 'env-theme-staging'
  if (/(prod|production|online|release)/.test(code)) return 'env-theme-prod'
  return 'env-theme-default'
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
  --env-accent: #3b82f6;
  --env-accent-soft: rgba(59, 130, 246, 0.22);
  --env-accent-bg: rgba(59, 130, 246, 0.08);
  --env-accent-glow: rgba(59, 130, 246, 0.4);
  --env-accent-strong: #2563eb;
  --env-accent-2: #1d4ed8;

  border-radius: 18px;
  height: 34px;
  display: inline-flex;
  align-items: center;
  padding: 0 4px;
  background: linear-gradient(135deg, var(--env-accent-bg), rgba(255, 255, 255, 0));
  border: 1px solid var(--env-accent-soft);
  box-shadow: 0 1px 0 rgba(255, 255, 255, 0.4) inset, 0 0 0 1px transparent;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.env-capsule-minimal:hover {
  border-color: var(--env-accent);
  box-shadow: 0 0 0 3px var(--env-accent-soft);
}

.env-content {
  display: flex;
  align-items: center;
  width: 100%;
  height: 100%;
}

.env-trigger-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 12px;
  height: 100%;
  border-radius: 14px;
  cursor: pointer;
  transition: background 0.2s;
  position: relative;
}

.env-trigger-btn:hover {
  background: var(--env-accent-bg);
}

.env-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--env-accent), var(--env-accent-2));
  box-shadow: 0 0 8px var(--env-accent-glow);
  flex-shrink: 0;
  position: relative;
}

.env-dot::after {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: 50%;
  background: var(--env-accent);
  opacity: 0.4;
  z-index: -1;
  filter: blur(4px);
  animation: env-dot-pulse 2s ease-in-out infinite;
}

@keyframes env-dot-pulse {
  0%, 100% { opacity: 0.4; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.2); }
}

.env-icon-small {
  font-size: 14px;
  color: var(--env-accent);
  transition: transform 0.2s;
}

.env-trigger-btn:hover .env-icon-small {
  transform: scale(1.1) rotate(-8deg);
}

.env-label-text,
.env-readonly-text {
  font-size: 12.5px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--env-accent-strong), var(--env-accent-2));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.3px;
  white-space: nowrap;
}

.env-readonly-text {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 0 12px;
}

.env-caret-small {
  font-size: 9px;
  color: var(--env-accent);
  transition: transform 0.2s;
}

.env-trigger-btn:hover .env-caret-small {
  transform: translateY(1px);
}

/* === default: 经典蓝 === */
.env-theme-default {
  --env-accent: #3b82f6;
  --env-accent-soft: rgba(59, 130, 246, 0.22);
  --env-accent-bg: rgba(59, 130, 246, 0.08);
  --env-accent-glow: rgba(59, 130, 246, 0.45);
  --env-accent-strong: #2563eb;
  --env-accent-2: #1d4ed8;
}

/* === dev: 天蓝 === */
.env-theme-dev {
  --env-accent: #38bdf8;
  --env-accent-soft: rgba(56, 189, 248, 0.22);
  --env-accent-bg: rgba(56, 189, 248, 0.08);
  --env-accent-glow: rgba(56, 189, 248, 0.45);
  --env-accent-strong: #0ea5e9;
  --env-accent-2: #0284c7;
}

/* === test: 中蓝 === */
.env-theme-test {
  --env-accent: #3b82f6;
  --env-accent-soft: rgba(59, 130, 246, 0.22);
  --env-accent-bg: rgba(59, 130, 246, 0.08);
  --env-accent-glow: rgba(59, 130, 246, 0.45);
  --env-accent-strong: #2563eb;
  --env-accent-2: #1d4ed8;
}

/* === staging: 靛蓝 === */
.env-theme-staging {
  --env-accent: #6366f1;
  --env-accent-soft: rgba(99, 102, 241, 0.22);
  --env-accent-bg: rgba(99, 102, 241, 0.08);
  --env-accent-glow: rgba(99, 102, 241, 0.45);
  --env-accent-strong: #4f46e5;
  --env-accent-2: #4338ca;
}

/* === prod: 深蓝 === */
.env-theme-prod {
  --env-accent: #1d4ed8;
  --env-accent-soft: rgba(29, 78, 216, 0.24);
  --env-accent-bg: rgba(29, 78, 216, 0.1);
  --env-accent-glow: rgba(29, 78, 216, 0.5);
  --env-accent-strong: #1e40af;
  --env-accent-2: #1e3a8a;
}

@media (max-width: 768px) {
  .env-label-text,
  .env-readonly-text {
    display: none;
  }

  .env-capsule-minimal {
    padding: 0 6px;
  }
}
</style>
