<template>
  <a-select
    :value="innerValue"
    mode="multiple"
    :options="options"
    :loading="loading"
    :placeholder="placeholder"
    allow-clear
    style="min-width: 220px"
    @change="handleChange"
  />
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { aiPromptApi } from '@/api/aiPrompt'

const props = withDefaults(defineProps<{
  modelValue?: string[]
  placeholder?: string
}>(), {
  modelValue: () => [],
  placeholder: '选择场景标签'
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string[]): void
  (e: 'change', value: string[]): void
}>()

const loading = ref(false)
const tagList = ref<string[]>([])

const innerValue = computed(() => props.modelValue ?? [])
const options = computed(() => tagList.value.map((tag) => ({ label: tag, value: tag })))

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
