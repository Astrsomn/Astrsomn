<template>
  <a-select :value="value" :loading="loading" :disabled="disabled" :placeholder="placeholder" :size="size" show-search
    option-filter-prop="label" :allow-clear="allowClear" class="model-provider-select" @update:value="onUpdate">
    <template #label="{ label, value: val }">
      <div class="selected-content" v-if="val">
        <span v-if="getSelectedAvatar(val)" class="opt-avatar" v-html="getSelectedAvatar(val)" />
        <span v-else class="opt-avatar-placeholder"></span>
        <span class="opt-text">{{ label }}</span>
      </div>
    </template>

    <a-select-option v-for="opt in optionsWithFallback" :key="opt.key" :value="opt.key" :label="opt.label">
      <div class="opt-row">
        <span v-if="opt.avatar" class="opt-avatar" v-html="opt.avatar" aria-hidden="true" />
        <span v-else class="opt-avatar-placeholder"></span>
        <span class="opt-text">{{ opt.label }}</span>
      </div>
    </a-select-option>
  </a-select>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import { systemExtensionApi, type SystemExtension } from '@/api/systemExtension'

const props = withDefaults(
  defineProps<{
    value?: string
    disabled?: boolean
    placeholder?: string
    size?: 'large' | 'middle' | 'small'
    allowClear?: boolean
  }>(),
  {
    placeholder: '请选择端点所属服务商',
    size: 'large',
    allowClear: false
  }
)

const emit = defineEmits<{
  'update:value': [v: string | undefined]
}>()

const loading = ref(false)
const catalog = ref<SystemExtension[]>([])

// 数据转换逻辑保持不变
function rowKey(it: SystemExtension): string {
  return String(it.providerCode?.trim() || it.extensionKey?.trim() || '')
}

function displayName(it: SystemExtension): string {
  return it.extensionName?.trim() || rowKey(it) || '—'
}

type OptRow = { key: string; label: string; avatar: string }

const catalogOptions = computed((): OptRow[] =>
  catalog.value
    .filter((it) => rowKey(it))
    .map((it) => ({
      key: rowKey(it),
      label: displayName(it),
      avatar: it.avatar?.trim() || ''
    }))
)

const optionsWithFallback = computed((): OptRow[] => {
  const base = catalogOptions.value
  const v = props.value?.trim()
  if (v && !base.some((o) => o.key === v)) {
    return [{ key: v, label: v, avatar: '' }, ...base]
  }
  return base
})

// 辅助方法：根据选中的 key 获取对应的 avatar
function getSelectedAvatar(val: string) {
  const target = optionsWithFallback.value.find(o => o.key === val)
  return target?.avatar || ''
}

async function load() {
  loading.value = true
  try {
    const resp = await systemExtensionApi.queryPage({
      pageNo: 1,
      pageSize: 500,
      param: {
        type: 'MODEL_PROVIDER',
        listScope: 'INSTALLED'
      }
    })
    catalog.value = resp?.list ?? []
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '加载模型提供商失败')
    catalog.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  void load()
})

function onUpdate(v: string | undefined) {
  emit('update:value', v)
}
</script>

<style scoped>
.model-provider-select {
  width: 100%;

  border-radius: var(--radius-pro);
}

/* 容器高度与圆角适配 Astrsomn 风格 */
.model-provider-select :deep(.ant-select-selector) {
  height: 50px !important;
  background: var(--bg-surface, #ffffff) !important;
  border-radius: var(--radius-pro);
  border: 1px solid var(--border-default) !important;
  padding: 0 16px !important;
  display: flex !important;
  align-items: center !important;
  transition: all 0.3s ease !important;
}

/* 选中后的内容布局 */
.selected-content {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.opt-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.opt-avatar {
  flex-shrink: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

/* 关键：强制 SVG 尺寸 */
.opt-avatar :deep(svg),
.opt-avatar :deep(img) {
  width: 100% !important;
  height: 100% !important;
  object-fit: contain;
}

.opt-avatar-placeholder {
  width: 24px;
  height: 24px;
  flex-shrink: 0;
  background: #f1f5f9;
  border-radius: 4px;
}

.opt-text {
  font-size: 14px;
  color: var(--text-heading, #1e293b);
  font-weight: 500;
}

/* 修正 Ant Design 默认选中文本样式 */
.model-provider-select :deep(.ant-select-selection-item) {
  line-height: 50px !important;
  display: flex !important;
  align-items: center !important;
}

.model-provider-select :deep(.ant-select-selection-placeholder) {
  line-height: 50px !important;
}
</style>