<template>
  <a-select
    :value="value"
    :loading="loading"
    :disabled="disabled"
    :placeholder="placeholder"
    :size="size"
    show-search
    option-filter-prop="label"
    option-label-prop="label"
    :allow-clear="allowClear"
    class="model-provider-select"
    @update:value="onUpdate"
  >
    <a-select-option
      v-for="opt in optionsWithFallback"
      :key="opt.key"
      :value="opt.key"
      :label="opt.label"
    >
      <span class="opt-row">
        <span
          v-if="opt.avatar"
          class="opt-avatar"
          v-html="opt.avatar"
          aria-hidden="true"
        />
        <span class="opt-text">{{ opt.label }}</span>
      </span>
    </a-select-option>
  </a-select>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import { systemExtensionApi, type ExtensionMarketplaceItem } from '@/api/systemExtension'

const props = withDefaults(
  defineProps<{
    value?: string
    disabled?: boolean
    placeholder?: string
    size?: 'large' | 'middle' | 'small'
    /** 为 true 时可清空为未选（如实例表单「全部提供商」） */
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
const catalog = ref<ExtensionMarketplaceItem[]>([])

function rowKey(it: ExtensionMarketplaceItem): string {
  return String(it.providerCode?.trim() || it.extensionKey?.trim() || '')
}

function displayName(it: ExtensionMarketplaceItem): string {
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

async function load() {
  loading.value = true
  try {
    const list = await systemExtensionApi.marketplaceCatalog('MODEL_PROVIDER')
    catalog.value = list || []
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '加载供应商目录失败')
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
}

.opt-row {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.opt-avatar {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.opt-avatar :deep(svg) {
  width: 20px;
  height: 20px;
  display: block;
}

.opt-text {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
