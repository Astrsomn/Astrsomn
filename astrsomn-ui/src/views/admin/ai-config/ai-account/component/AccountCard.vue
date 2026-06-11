<template>
  <div
      class="card"
      :class="{ 'card-selected': selected }"
  >
    <!-- Selection checkbox (appears on hover) -->
    <a-checkbox
        :checked="selected"
        class="card-checkbox"
        @click.stop="onToggle"
    />

    <!-- Card Header -->
    <div class="card-header">
      <div class="card-avatar">
        <CustomerServiceOutlined v-if="!avatarSrc" class="card-avatar-icon"/>
        <img v-else :src="avatarSrc" :alt="account.accountName" class="card-avatar-img"/>
      </div>
      <div class="card-header-text">
        <h3 class="card-title">{{ account.accountName || t.card.defaultName }}</h3>
        <a-tag v-if="account.envCode" class="env-tag">
          {{ account.envCode }}
        </a-tag>
        <a-tag v-else class="env-tag env-tag--unset">
          UNSET
        </a-tag>
      </div>
    </div>

    <!-- Card Info: API Key -->
    <p class="card-info">
      {{ displayKey }}
    </p>

    <!-- Card Meta: chips + actions -->
    <div class="card-footer">
      <span v-if="account.accountTokens != null" class="meta-chip">
        <TransactionOutlined/> {{ formatTokens(account.accountTokens) }}
      </span>
      <span v-if="account.callCount != null" class="meta-chip">
        {{ Number(account.callCount).toLocaleString() }} calls
      </span>
      <span v-if="remainingTokens != null" class="meta-chip">
        {{ formatTokens(remainingTokens) }} left
      </span>
      <span class="meta-spacer"></span>
      <a-popconfirm
          :title="t.card.deleteConfirm"
          @confirm.stop="onDelete"
      >
        <button class="action-delete-btn" @click.stop><DeleteOutlined/></button>
      </a-popconfirm>
      <button class="action-edit-link" @click.stop="onEdit">
        {{ t.card.manage }} <RightOutlined/>
      </button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {
  CustomerServiceOutlined,
  DeleteOutlined,
  RightOutlined,
  TransactionOutlined,
} from '@ant-design/icons-vue'
import {computed} from 'vue'
import {type AiAccount} from '@/api/aiAccount'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-account')

const props = withDefaults(defineProps<{
  account: AiAccount;
  selected?: boolean;
}>(), {
  selected: false,
})

const emit = defineEmits<{
  (e: 'edit', account: AiAccount): void;
  (e: 'delete', id: number | string): void;
  (e: 'toggle', id: number | string, checked: boolean): void;
  (e: 'show-models', account: AiAccount): void;
}>()

const avatarSrc = computed(() => {
  const raw = (props.account as Record<string, unknown>).providerAvatar
  if (typeof raw === 'string' && raw.startsWith('data:image')) return raw
  return ''
})

const remainingTokens = computed(() => {
  const {accountTokens, totalTokens} = props.account
  if (accountTokens == null && totalTokens == null) return null
  return (accountTokens ?? 0) - (totalTokens ?? 0)
})

const displayKey = computed(() => {
  const key = props.account.apiKey
  if (!key) return '—'
  if (key.length <= 10) return `${key.slice(0, 2)}***${key.slice(-2)}`
  return `${key.slice(0, 4)}****${key.slice(-4)}`
})

const formatTokens = (t?: number | null): string => {
  if (t == null) return '0'
  if (t >= 1_000_000) return (t / 1_000_000).toFixed(1) + 'M'
  if (t >= 1_000) return (t / 1_000).toFixed(1) + 'k'
  return t.toString()
}

const onToggle = () => {
  if (props.account.id != null) {
    emit('toggle', props.account.id, !props.selected)
  }
}

const onEdit = () => emit('edit', props.account)
const onDelete = () => {
  if (props.account.id != null) {
    emit('delete', props.account.id)
  }
}
</script>

<style scoped>
/* ── Card Container (unified style — light blue accent) ── */
.card {
  --card-accent: #3b82f6;

  --card-tag-bg: #eff6ff;
  --card-tag-color: #2563eb;

  position: relative;
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.card:hover {
  border-color: var(--card-accent);
  box-shadow: 0 4px 20px -4px color-mix(in srgb, var(--card-accent) 15%, transparent);
  transform: translateY(-1px);
}

.card-selected {
  border-color: var(--card-accent);
  box-shadow: 0 0 0 1px var(--card-accent);
}

/* ── Selection Checkbox ── */
.card-checkbox {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 2;
  opacity: 0;
  transition: opacity 0.15s;
}

.card:hover .card-checkbox,
.card-selected .card-checkbox {
  opacity: 1;
}

/* ── Card Header ── */
.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-avatar {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--card-gradient);
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  overflow: hidden;
}

.card-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-avatar-icon {
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-header-text {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.card-title {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.env-tag {
  font-size: 9px;
  font-weight: 600;
  padding: 1px 7px;
  border-radius: 10px;
  border: none;
  flex-shrink: 0;
  line-height: 18px;
  background: var(--card-tag-bg);
  color: var(--card-tag-color);
}

.env-tag--unset {
  background: color-mix(in srgb, var(--text-muted) 10%, transparent);
  color: var(--text-muted);
}

/* ── Card Info ── */
.card-info {
  font-size: 11px;
  color: var(--text-muted);
  line-height: 1.6;
  margin: 0;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* ── Card Footer ── */
.card-footer {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: auto;
}

.meta-chip {
  font-size: 10px;
  color: var(--text-muted);
  display: inline-flex;
  align-items: center;
  gap: 3px;
  padding: 2px 7px;
  background: var(--bg-elevated);
  border-radius: 6px;
  font-weight: 500;
}

.meta-chip :deep(svg) {
  font-size: 10px;
}

.meta-spacer {
  flex: 1;
}

/* ── Action Buttons (reveal on hover) ── */
.action-delete-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border: 1px solid var(--border-subtle);
  border-radius: 6px;
  background: var(--bg-elevated);
  color: var(--text-muted);
  cursor: pointer;
  font-size: 12px;
  transition: all 0.15s;
  flex-shrink: 0;
  padding: 0;
  opacity: 0;
}

.card:hover .action-delete-btn {
  opacity: 1;
}

.action-delete-btn:hover {
  color: var(--error);
  border-color: var(--error);
  background: color-mix(in srgb, var(--error) 8%, transparent);
}

.action-edit-link {
  font-size: 10px;
  color: var(--card-accent);
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 3px;
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.15s;
}

.card:hover .action-edit-link {
  opacity: 1;
}

.action-edit-link :deep(svg) {
  font-size: 8px;
}
</style>
