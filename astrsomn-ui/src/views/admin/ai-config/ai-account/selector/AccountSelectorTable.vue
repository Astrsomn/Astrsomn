<template>
  <a-drawer
    :open="props.open"
    placement="right"
    :width="480"
    :maskClosable="false"
    :closable="true"
    title="选择账号"
    @close="handleClose"
    root-class-name="account-select-drawer"
  >
    <div class="select-drawer-content">
      <div class="search-bar">
        <a-input
          v-model:value="keyword"
          placeholder="搜索账号名称"
          allow-clear
          @pressEnter="handleSearch"
        >
          <template #prefix>
            <SearchOutlined />
          </template>
        </a-input>
        <a-button type="primary" @click="handleSearch">查询</a-button>
      </div>

      <a-spin :spinning="loading">
        <div class="account-list">
          <div
            v-for="account in list"
            :key="account.id"
            class="account-item"
            @click="handleSelect(account)"
          >
            <div class="account-info">
              <div class="account-name">{{ account.accountName }}</div>
              <div class="account-key">
                <KeyOutlined /> {{ account.accountKey }}
              </div>
            </div>
            <div class="account-meta">
              <span v-if="account.usedModelCount" class="model-count">
                关联 {{ account.usedModelCount }} 个模型
              </span>
              <span class="env-badge" :class="account.envCode?.toLowerCase()">
                {{ account.envCode || '无环境' }}
              </span>
            </div>
          </div>

          <a-empty v-if="!loading && list.length === 0" description="暂无账号" />
        </div>
      </a-spin>

      <div class="drawer-footer">
        <a-pagination
          v-model:current="page.pageNum"
          :page-size="page.pageSize"
          :total="page.total"
          :show-size-changer="false"
          @change="fetchList"
        />
      </div>
    </div>
  </a-drawer>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { SearchOutlined, KeyOutlined } from '@ant-design/icons-vue'
import { aiAccountApi, type AiAccount, type PageResponse } from '@/api/aiAccount.ts'

const props = withDefaults(defineProps<{
  open: boolean
  /** 是否只查询已启用的账号 */
  onlyEnabled?: boolean
}>(), {
  onlyEnabled: true
})

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void
  (e: 'select', account: AiAccount): void
}>()

const keyword = ref('')
const loading = ref(false)
const list = ref<AiAccount[]>([])
const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        accountName: keyword.value || undefined,
        status: props.onlyEnabled ? 'enabled' : undefined
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

const handleSelect = (account: AiAccount) => {
  emit('select', account)
}

const handleClose = () => {
  emit('update:open', false)
}

watch(() => props.open, (val) => {
  if (val) {
    keyword.value = ''
    page.pageNum = 1
    void fetchList()
  }
})
</script>

<style scoped>
.select-drawer-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.search-bar :deep(.ant-input-affix-wrapper) {
  flex: 1;
}

.account-list {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.account-item {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.account-item:hover {
  border-color: var(--primary);
  box-shadow: var(--shadow-card);
}

.account-info {
  margin-bottom: 8px;
}

.account-name {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.account-key {
  font-size: 12px;
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
  display: flex;
  align-items: center;
  gap: 4px;
}

.account-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.model-count {
  font-size: 12px;
  color: var(--text-hint);
}

.env-badge {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 600;
}

.env-badge.prod {
  background: #fee2e2;
  color: #ef4444;
}

.env-badge.dev {
  background: #e0f2fe;
  color: #0ea5e9;
}

.drawer-footer {
  flex-shrink: 0;
  padding-top: 16px;
  border-top: 1px solid var(--border-default);
  display: flex;
  justify-content: center;
}
</style>
