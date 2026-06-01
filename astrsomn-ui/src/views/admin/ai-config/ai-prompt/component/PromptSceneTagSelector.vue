<template>
  <a-select
      :loading="loading"
      :options="options"
      :placeholder="t.sceneTagSelector.placeholder"
      :value="innerValue"
      allow-clear
      class="scene-tag-selector"
      mode="multiple"
      @change="handleChange"
  />
</template>

<script lang="ts" setup>
import {computed, onMounted, ref} from 'vue'
import {aiPromptApi} from '@/api/aiPrompt'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-prompt')

const props = withDefaults(defineProps<{
  modelValue?: string[]
}>(), {
  modelValue: () => [],
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string[]): void
  (e: 'change', value: string[]): void
}>()

const loading = ref(false)
const tagList = ref<string[]>([])

const innerValue = computed(() => props.modelValue ?? [])
const options = computed(() => tagList.value.map((tag) => ({label: tag, value: tag})))

const handleChange = (value: string[]) => {
  const next = Array.isArray(value) ? value : []
  emit('update:modelValue', next)
  emit('change', next)
}

const loadTags = async () => {
  loading.value = true
  try {
    const tags = await aiPromptApi.querySceneTags()
    tagList.value = Array.from(new Set((tags || []).map((item) => String(item ?? '').trim()).filter(Boolean)))
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  void loadTags()
})
</script>

<style scoped>
.scene-tag-selector {
  min-width: 220px;
  height: 48px;
}

.scene-tag-selector :deep(.ant-select-selector) {
  height: 48px !important;
  min-height: 48px !important;
  border-radius: var(--radius-md);
  border-color: var(--border-default);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.scene-tag-selector :deep(.ant-select-selection-overflow) {
  align-items: center;
}

.scene-tag-selector :deep(.ant-select-selector:hover) {
  border-color: var(--primary);
}

.scene-tag-selector :deep(.ant-select-focused .ant-select-selector) {
  border-color: var(--primary);
  box-shadow: 0 0 0 4px color-mix(in srgb, var(--primary) 10%, transparent);
}
</style>
