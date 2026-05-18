<template>
  <AstDrawer
      :open="props.open"
      :width="520"
      @update:open="handleClose"
  >
    <template #title>选择账号</template>
    <template #subtitle>AI Account</template>

    <div class="select-drawer-content">
      <div class="toolbar-row">
        <AstSearchInput
            :model-value="keyword"
            class="toolbar-search"
            placeholder="搜索账号名称"
            @update:model-value="keyword = $event"
            @search="handleSearch"
        />
        <a-button class="add-btn" type="primary" @click="accountFormOpen = true">
          <PlusOutlined/>
        </a-button>
      </div>

      <div class="filter-bar">
        <ExtensionSelector
            v-model:value="filterExtensionCode"
            :allow-clear="true"
            placeholder="全部供应商"
            size="middle"
        />
      </div>

      <a-spin :spinning="loading" class="list-spin">
        <div class="account-list">
          <div
              v-for="account in list"
              :key="account.id"
              class="account-item"
              @click="handleSelect(account)"
          >
            <div class="account-item-main">
              <img
                  v-if="getProviderAvatar(account.extensionCode)"
                  :src="getProviderAvatar(account.extensionCode)"
                  class="account-provider-avatar"
                  alt=""
              />
              <div v-else class="account-provider-avatar-placeholder">
                <UserOutlined/>
              </div>
              <div class="account-info">
                <div class="account-name">{{ account.accountName }}</div>
                <div class="account-key">
                  <KeyOutlined/>
                  {{ account.accountKey }}
                </div>
              </div>
            </div>

            <div class="account-stats">
              <div v-if="account.accountTokens != null" class="stat-item">
                <span class="stat-label">额度</span>
                <span class="stat-value">{{ formatTokens(account.accountTokens) }}</span>
              </div>
              <div v-if="account.totalTokens != null" class="stat-item">
                <span class="stat-label">已用</span>
                <span class="stat-value">{{ formatTokens(account.totalTokens) }}</span>
              </div>
              <div v-if="account.callCount != null" class="stat-item">
                <span class="stat-label">调用</span>
                <span class="stat-value">{{ account.callCount }}</span>
              </div>
            </div>

            <div class="account-meta">
              <span v-if="account.usedModelCount" class="model-count">
                关联 {{ account.usedModelCount }} 个模型
              </span>
              <span :class="account.envCode?.toLowerCase()" class="env-badge">
                {{ account.envCode || '无环境' }}
              </span>
              <span :class="['status-dot', account.status === 'enabled' ? 'enabled' : 'disabled']"/>
            </div>
          </div>

          <a-empty v-if="!loading && list.length === 0" description="暂无账号"/>
        </div>
      </a-spin>

    </div>

    <template #footer>
      <AstPagination
          :current="page.pageNum"
          :page-size="page.pageSize"
          :show-size-changer="false"
          :total="page.total"
          @change="onPageChange"
      />
    </template>

    <AccountForm
        :visible="accountFormOpen"
        @update:visible="accountFormOpen = $event"
        @success="onAccountCreated"
    />
  </AstDrawer>
</template>

<script lang="ts" setup>
import {reactive, ref, watch, onMounted} from 'vue'
import {KeyOutlined, PlusOutlined, UserOutlined} from '@ant-design/icons-vue'
import {type AiAccount, aiAccountApi, type PageResponse} from '@/api/aiAccount'
import {type SystemExtension, systemExtensionApi} from '@/api/systemExtension'
import AstDrawer from '@/components/home/AstDrawer.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import ExtensionSelector from '@/views/admin/system-config/system-extension/selector/ExtensionSelector.vue'
import AccountForm from '@/views/admin/ai-config/ai-account/component/AccountForm.vue'

const props = withDefaults(defineProps<{
  open: boolean
  onlyEnabled?: boolean

  providerFilter?: string

  pageNum?: number
}>(), {
  onlyEnabled: true,
  pageNum: 1
})

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void
  (e: 'select', account: AiAccount): void
}>()

const keyword = ref('')
const loading = ref(false)
const list = ref<AiAccount[]>([])
const page = reactive({
  pageNum: props.pageNum,
  pageSize: 10,
  total: 0
})

const filterExtensionCode = ref<string | undefined>(undefined)
const accountFormOpen = ref(false)


const extensionMap = ref<Record<string, SystemExtension>>({})

async function loadExtensions() {
  try {
    const resp = await systemExtensionApi.queryPage({
      pageNo: 1,
      pageSize: 500,
      param: {type: 'MODEL_PROVIDER', listScope: 'APPLIED' as any}
    })
    const map: Record<string, SystemExtension> = {}
    for (const ext of resp?.list ?? []) {
      const code = (ext.extensionCode || ext.extensionKey || '').trim()
      if (code) map[code] = ext
    }
    extensionMap.value = map
  } catch {

  }
}

function getProviderAvatar(extensionCode?: string): string {
  if (!extensionCode) return ''
  return extensionMap.value[extensionCode]?.avatar?.trim() || ''
}

function formatTokens(val: number): string {
  if (val >= 1_000_000) return (val / 1_000_000).toFixed(1) + 'M'
  if (val >= 1_000) return (val / 1_000).toFixed(1) + 'K'
  return String(val)
}


watch(() => props.providerFilter, (val) => {
  filterExtensionCode.value = val || undefined
}, {immediate: true})


watch(filterExtensionCode, () => {
  page.pageNum = 1
  void fetchList()
})

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        accountName: keyword.value || undefined,
        status: props.onlyEnabled ? 'enabled' : undefined,
        extensionCode: filterExtensionCode.value || undefined
      }
    }
    const resp: PageResponse<AiAccount> = await aiAccountApi.queryPage(payload)
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.pageNum = 1
  void fetchList()
}

function onPageChange(p: number) {
  page.pageNum = p
  void fetchList()
}

const handleSelect = (account: AiAccount) => {
  emit('select', account)
}

const handleClose = () => {
  emit('update:open', false)
}

function onAccountCreated() {
  page.pageNum = 1
  void fetchList()
}

watch(() => props.open, (val) => {
  if (val) {
    keyword.value = ''
    filterExtensionCode.value = props.providerFilter || undefined
    page.pageNum = props.pageNum
    void fetchList()
  }
})

onMounted(() => {
  void loadExtensions()
})
</script>

<style scoped>
.select-drawer-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  gap: 12px;
}


.toolbar-row {
  display: flex;
  gap: 10px;
  align-items: center;
}

.toolbar-search {
  flex: 1;
  min-width: 0;
}

.add-btn {
  width: 48px;
  height: 48px;
  flex-shrink: 0;
  border-radius: var(--radius-md, 10px);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  box-shadow: 0 2px 8px color-mix(in srgb, var(--primary, #3b82f6) 30%, transparent);
}

.add-btn:hover {
  transform: scale(1.06);
  filter: brightness(1.1);
}


.filter-bar {
  display: flex;
  gap: 12px;
}

.filter-bar :deep(.model-provider-select) {
  flex: 1;
}


.list-spin {
  flex: 1;
  min-height: 0;
}

.list-spin :deep(.ant-spin-container) {
  height: 100%;
}

.list-spin :deep(.ant-spin) {
  max-height: 100%;
}

.account-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 420px;
  overflow-y: auto;
}

.account-item {
  background: var(--bg-card, #fff);
  border: 1px solid var(--border-default, #e2e8f0);
  border-radius: var(--radius-lg, 12px);
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.account-item:hover {
  border-color: var(--primary, #3b82f6);
  box-shadow: var(--shadow-card, 0 2px 8px rgba(0, 0, 0, 0.06));
}

.account-item-main {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.account-provider-avatar {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  object-fit: contain;
  flex-shrink: 0;
}

.account-provider-avatar-placeholder {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: var(--bg-surface);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  font-size: 16px;
  flex-shrink: 0;
}

.account-info {
  flex: 1;
  min-width: 0;
}

.account-name {
  font-weight: 600;
  color: var(--text-primary, #1e293b);
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.account-key {
  font-size: 12px;
  color: var(--text-secondary, #64748b);
  font-family: 'JetBrains Mono', monospace;
  display: flex;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.account-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 10px;
  padding: 8px 12px;
  background: var(--bg-surface);
  border-radius: var(--radius-md);
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-label {
  font-size: 10px;
  color: var(--text-muted);
  font-weight: 500;
}

.stat-value {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-primary);
  font-family: 'JetBrains Mono', monospace;
}

.account-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.model-count {
  font-size: 12px;
  color: var(--text-hint, #94a3b8);
}

.env-badge {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 600;
}

.env-badge.prod {
  background: rgba(239, 68, 68, 0.12);
  color: var(--error);
}

.env-badge.dev {
  background: rgba(14, 165, 233, 0.12);
  color: #38bdf8;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-left: auto;
}

.status-dot.enabled {
  background: #22c55e;
}

.status-dot.disabled {
  background: var(--text-muted);
}

</style>
