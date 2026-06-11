<template>
  <div class="account-section">
    <!-- Glass toolbar -->
    <div class="account-section__toolbar">
      <div class="account-section__search">
        <AstSearchInput
            v-model="keyword"
            layout="pane"
            :placeholder="t.account.searchPlaceholder"
            @search="handleSearch"
        />
      </div>
      <span class="account-section__count">
        <span class="count-dot"/>
        {{ t.account.total.replace('{n}', String(total)) }}
      </span>
      <a-tooltip :title="t.account.create">
        <a-button class="account-section__create-btn" shape="circle" size="large" type="primary" @click="handleCreate">
          <template #icon>
            <PlusOutlined/>
          </template>
        </a-button>
      </a-tooltip>
    </div>

    <!-- Body -->
    <div class="account-section__body">
      <div v-if="loading" class="account-section__loading">
        <a-spin size="small"/>
      </div>

      <template v-else>
        <div v-if="accounts.length" class="account-section__grid">
          <AccountCard
              v-for="(account, index) in accounts"
              :key="account.id"
              :account="account"
              :selected="selectedKeys.has(account.id!)"
              class="account-section__card"
              @edit="handleEdit"
              @delete="handleDeleteOne"
              @toggle="handleToggle"
          />

          <!-- 添加卡片 -->
          <div class="add-card" @click="handleCreate">
            <PlusOutlined class="add-icon"/>
            <span class="add-text">{{ t.account.create }}</span>
          </div>
        </div>

        <div v-else class="account-section__empty">
          <div class="empty-state">
            <div class="empty-state__icon">
              <CustomerServiceOutlined/>
            </div>
            <p class="empty-state__text">{{ t.account.empty }}</p>
            <p class="empty-state__hint">{{ t.account.emptyHint }}</p>
          </div>
        </div>
      </template>
    </div>

    <!-- Pagination -->
    <div v-if="total > 0" class="account-section__pagination">
      <AstPagination
          :current="pageNo"
          :page-size="pageSize"
          :show-size-changer="false"
          :total="total"
          @change="handlePageChange"
      />
    </div>

    <!-- Create / Edit Drawer -->
    <AccountForm
        :visible="formVisible"
        :record="editingAccount ?? undefined"
        @update:visible="formVisible = $event"
        @success="handleFormSuccess"
    />
  </div>
</template>

<script lang="ts" setup>
import {computed, ref, watch} from 'vue'
import {message, Modal} from 'ant-design-vue'
import {CustomerServiceOutlined, PlusOutlined} from '@ant-design/icons-vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AccountCard from '@/views/admin/ai-config/ai-account/component/AccountCard.vue'
import AccountForm from '@/views/admin/ai-config/ai-account/component/AccountForm.vue'
import {type AiAccount, aiAccountApi} from '@/api/aiAccount'
import {usePageTranslation} from '@/locales/pages.ts'

const props = defineProps<{
  providerKey?: string
}>()

const t = usePageTranslation('ai-config-center')

const keyword = ref('')
const loading = ref(false)
const accounts = ref<AiAccount[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(18)

const formVisible = ref(false)
const editingAccount = ref<AiAccount | null>(null)

const selectedKeys = ref<Set<string | number>>(new Set())

const handleSearch = () => {
  pageNo.value = 1
  void fetchAccounts()
}

const handlePageChange = (page: number, size: number) => {
  pageNo.value = page
  pageSize.value = size
  void fetchAccounts()
}

const fetchAccounts = async () => {
  loading.value = true
  try {
    const resp = await aiAccountApi.queryPage({
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      param: {
        extensionCode: props.providerKey && props.providerKey !== 'all' ? props.providerKey : undefined,
        accountName: keyword.value.trim() || undefined,
      },
    })
    accounts.value = resp.list || []
    total.value = resp.total || 0
  } catch {
    accounts.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  editingAccount.value = null
  formVisible.value = true
}

const handleEdit = (account: AiAccount) => {
  editingAccount.value = {...account}
  formVisible.value = true
}

const handleFormSuccess = async () => {
  formVisible.value = false
  await fetchAccounts()
}

const handleToggle = (id: number | string, checked: boolean) => {
  const next = new Set(selectedKeys.value)
  if (checked) {
    next.add(id)
  } else {
    next.delete(id)
  }
  selectedKeys.value = next
}

const handleDeleteOne = (id: number | string) => {
  Modal.confirm({
    title: t.value.account.deleteTitle,
    content: t.value.account.deleteConfirm,
    okText: t.value.account.delete,
    okType: 'danger',
    cancelText: t.value.account.cancel,
    onOk: async () => {
      try {
        await aiAccountApi.delete([id])
        message.success(t.value.account.deleteSuccess)
        selectedKeys.value.delete(id)
        selectedKeys.value = new Set(selectedKeys.value)
        await fetchAccounts()
      } catch (e: unknown) {
        const err = e as { message?: string }
        message.error(err?.message || t.value.account.deleteFailed)
      }
    },
  })
}

watch(() => props.providerKey, () => {
  keyword.value = ''
  pageNo.value = 1
  void fetchAccounts()
})

void fetchAccounts()
</script>

<style scoped>
.account-section {
  --ms-glass-bg: color-mix(in srgb, var(--bg-card) 50%, transparent);
  --ms-glass-bg-hover: color-mix(in srgb, var(--bg-card) 76%, transparent);
  --ms-glass-border: color-mix(in srgb, var(--border-default) 55%, transparent);
  --ms-glass-border-hover: color-mix(in srgb, var(--primary) 38%, transparent);
  --ms-glow-ring: color-mix(in srgb, var(--primary) 16%, transparent);
  --ms-ease-spring: cubic-bezier(0.34, 1.56, 0.64, 1);

  display: flex;
  flex-direction: column;
  padding: 28px 36px 36px;
  flex: 1;
  position: relative;
  isolation: isolate;
  min-height: 0;
}

.account-section__toolbar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  flex-shrink: 0;
  margin-bottom: 28px;
  padding: 8px 8px 8px 18px;
  background: var(--ms-glass-bg);
  backdrop-filter: blur(18px) saturate(180%);
  -webkit-backdrop-filter: blur(18px) saturate(180%);
  border-radius: 16px;
  position: sticky;
  top: 0;
  z-index: 10;
}

.account-section__search {
  flex: 1;
  max-width: 320px;
}

.account-section__count {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--text-muted);
  flex-shrink: 0;
  font-variant-numeric: tabular-nums;
}

.count-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--primary-light);
  box-shadow: 0 0 8px color-mix(in srgb, var(--primary) 60%, transparent);
}

.account-section__create-btn {
  flex-shrink: 0;
  transition: transform 0.25s var(--ms-ease-spring), box-shadow 0.25s ease;
  box-shadow: 0 2px 12px color-mix(in srgb, var(--primary) 25%, transparent);
}

.account-section__create-btn:hover {
  transform: scale(1.08);
  box-shadow: 0 4px 20px color-mix(in srgb, var(--primary) 40%, transparent);
}

.account-section__create-btn:active {
  transform: scale(0.94);
}

.account-section__body {
  flex: 1;
  min-height: 0;
}

.account-section__loading {
  display: flex;
  justify-content: center;
  padding: 80px 0;
}

.account-section__grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(284px, 1fr));
  gap: 18px;
  align-items: start;
}

.account-section__card {
  transition: transform 0.45s var(--ms-ease-spring),
  border-color 0.3s ease,
  box-shadow 0.35s ease;
}

.account-section__card:hover {
  transform: scale(1.02);
}

.add-card {
  border: 2px dashed var(--border-subtle);
  background: transparent;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 140px;
  color: var(--text-muted);
}

.add-card:hover {
  border-color: var(--primary);
  color: var(--primary);
  background: color-mix(in srgb, var(--primary) 2%, transparent);
}

.add-icon {
  font-size: 22px;
  opacity: 0.4;
  transition: opacity 0.2s;
}

.add-card:hover .add-icon {
  opacity: 0.8;
}

.add-text {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.account-section__empty {
  display: flex;
  justify-content: center;
  padding: 80px 0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.empty-state__icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: var(--text-muted);
  background: var(--ms-glass-bg);
  border: 1px solid var(--ms-glass-border);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

.empty-state__text {
  font-size: 15px;
  color: var(--text-secondary);
  font-weight: 500;
  margin: 0;
}

.empty-state__hint {
  font-size: 12px;
  color: var(--text-muted);
  opacity: 0.6;
  margin: 0;
}

.account-section__pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
