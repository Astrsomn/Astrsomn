<template>
  <div class="instance-section">
    <!-- Glass toolbar -->
    <div class="instance-section__toolbar">
      <div class="instance-section__search">
        <AstSearchInput
            v-model="keyword"
            layout="pane"
            :placeholder="t.instance.searchPlaceholder"
            @search="handleSearch"
        />
      </div>
      <span class="instance-section__count">
        <span class="count-dot"/>
        {{ t.instance.total.replace('{n}', String(total)) }}
      </span>
      <a-tooltip :title="t.instance.create">
        <a-button class="instance-section__create-btn" shape="circle" size="large" type="primary" @click="handleCreate">
          <template #icon>
            <PlusOutlined/>
          </template>
        </a-button>
      </a-tooltip>
    </div>

    <!-- Body -->
    <div class="instance-section__body">
      <div v-if="loading" class="instance-section__loading">
        <a-spin size="small"/>
      </div>

      <template v-else>
        <div v-if="instances.length" class="instance-section__grid">
          <!-- 添加卡片 -->
          <div class="add-card" @click="handleCreate">
            <PlusOutlined class="add-icon"/>
            <span class="add-text">{{ t.instance.create }}</span>
          </div>

          <InstanceCard
              v-for="(instance, index) in instances"
              :key="instance.id"
              :record="instance"
              :index="index"
              :selected="selectedKeys.has(instance.id!)"
              @toggle-select="handleToggle(instance.id!)"
              @edit="handleEdit(instance)"
              @delete="handleDeleteOne(instance.id!)"
          />
        </div>

        <div v-else class="instance-section__empty">
          <div class="empty-state">
            <div class="empty-state__icon">
              <ThunderboltOutlined/>
            </div>
            <p class="empty-state__text">{{ t.instance.empty }}</p>
            <p class="empty-state__hint">{{ t.instance.emptyHint }}</p>
          </div>
        </div>
      </template>
    </div>

    <!-- Pagination -->
    <div v-if="total > 0" class="instance-section__pagination">
      <AstPagination
          :current="pageNo"
          :page-size="pageSize"
          :show-size-changer="false"
          :total="total"
          @change="handlePageChange"
      />
    </div>

    <!-- Create / Edit Modal -->
    <InstanceForm
        :visible="formVisible"
        :record="editingInstance ?? undefined"
        @update:visible="formVisible = $event"
        @success="handleFormSuccess"
    />
  </div>
</template>

<script lang="ts" setup>
import {ref, watch} from 'vue'
import {message, Modal} from 'ant-design-vue'
import {PlusOutlined, ThunderboltOutlined} from '@ant-design/icons-vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import InstanceCard from '@/views/admin/ai-config/ai-instance/component/InstanceCard.vue'
import InstanceForm from '@/views/admin/ai-config/ai-instance/InstanceForm.vue'
import {type AiInstance, aiInstanceApi} from '@/api/aiInstance'
import {usePageTranslation} from '@/locales/pages.ts'

const props = defineProps<{
  providerKey?: string
}>()

const t = usePageTranslation('ai-config-center')

const keyword = ref('')
const loading = ref(false)
const instances = ref<AiInstance[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(18)

const formVisible = ref(false)
const editingInstance = ref<AiInstance | null>(null)

const selectedKeys = ref<Set<string | number>>(new Set())

const handleSearch = () => {
  pageNo.value = 1
  void fetchInstances()
}

const handlePageChange = (page: number, size: number) => {
  pageNo.value = page
  pageSize.value = size
  void fetchInstances()
}

const fetchInstances = async () => {
  loading.value = true
  try {
    const resp = await aiInstanceApi.queryPage({
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      param: {
        extensionCode: props.providerKey && props.providerKey !== 'all' ? props.providerKey : undefined,
        instanceName: keyword.value.trim() || undefined,
      },
    })
    instances.value = resp.list || []
    total.value = resp.total || 0
  } catch {
    instances.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  editingInstance.value = null
  formVisible.value = true
}

const handleEdit = (instance: AiInstance) => {
  editingInstance.value = {...instance}
  formVisible.value = true
}

const handleFormSuccess = async () => {
  formVisible.value = false
  await fetchInstances()
}

const handleToggle = (id: string | number) => {
  const next = new Set(selectedKeys.value)
  if (next.has(id)) {
    next.delete(id)
  } else {
    next.add(id)
  }
  selectedKeys.value = next
}

const handleDeleteOne = (id: string | number) => {
  Modal.confirm({
    title: t.value.instance.deleteTitle,
    content: t.value.instance.deleteConfirm,
    okText: t.value.instance.delete,
    okType: 'danger',
    cancelText: t.value.instance.cancel,
    onOk: async () => {
      try {
        await aiInstanceApi.delete([id])
        message.success(t.value.instance.deleteSuccess)
        selectedKeys.value.delete(id)
        selectedKeys.value = new Set(selectedKeys.value)
        await fetchInstances()
      } catch (e: unknown) {
        const err = e as { message?: string }
        message.error(err?.message || t.value.instance.deleteFailed)
      }
    },
  })
}

watch(() => props.providerKey, () => {
  keyword.value = ''
  pageNo.value = 1
  void fetchInstances()
})

void fetchInstances()
</script>

<style scoped>
.instance-section {
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

.instance-section__toolbar {
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

.instance-section__search {
  flex: 1;
  max-width: 320px;
}

.instance-section__count {
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

.instance-section__create-btn {
  flex-shrink: 0;
  transition: transform 0.25s var(--ms-ease-spring), box-shadow 0.25s ease;
  box-shadow: 0 2px 12px color-mix(in srgb, var(--primary) 25%, transparent);
}

.instance-section__create-btn:hover {
  transform: scale(1.08);
  box-shadow: 0 4px 20px color-mix(in srgb, var(--primary) 40%, transparent);
}

.instance-section__create-btn:active {
  transform: scale(0.94);
}

.instance-section__body {
  flex: 1;
  min-height: 0;
}

.instance-section__loading {
  display: flex;
  justify-content: center;
  padding: 80px 0;
}

.instance-section__grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(284px, 1fr));
  gap: 18px;
  align-items: start;
}

.instance-section__grid > * {
  animation: cardEnter 0.35s ease both;
}

.instance-section__grid > *:nth-child(1) { animation-delay: 0ms; }
.instance-section__grid > *:nth-child(2) { animation-delay: 40ms; }
.instance-section__grid > *:nth-child(3) { animation-delay: 80ms; }
.instance-section__grid > *:nth-child(4) { animation-delay: 120ms; }
.instance-section__grid > *:nth-child(5) { animation-delay: 160ms; }
.instance-section__grid > *:nth-child(6) { animation-delay: 200ms; }
.instance-section__grid > *:nth-child(7) { animation-delay: 240ms; }
.instance-section__grid > *:nth-child(8) { animation-delay: 280ms; }
.instance-section__grid > *:nth-child(9) { animation-delay: 320ms; }
.instance-section__grid > *:nth-child(10) { animation-delay: 360ms; }
.instance-section__grid > *:nth-child(n+11) { animation-delay: 400ms; }

@keyframes cardEnter {
  from {
    opacity: 0;
    transform: translateY(16px) scale(0.97);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
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
  height: 205px;
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

.instance-section__empty {
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

.instance-section__pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
