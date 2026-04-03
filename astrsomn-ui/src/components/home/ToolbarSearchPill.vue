<template>
  <div class="toolbar-search-pill" :class="rootClass">
    <SearchOutlined class="toolbar-search-pill__icon" />
    <input
      type="text"
      class="toolbar-search-pill__input"
      :value="displayValue"
      :placeholder="placeholder"
      @input="onInput"
      @keyup.enter="emitSearch"
    />
    <button type="button" class="toolbar-search-pill__btn" @click="emitSearch">
      {{ buttonLabel }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { SearchOutlined } from '@ant-design/icons-vue'

export type ToolbarSearchPillLayout = 'toolbar' | 'pane' | 'fluid'

const props = withDefaults(
  defineProps<{
    modelValue?: string | null
    placeholder?: string
    buttonLabel?: string
    layout?: ToolbarSearchPillLayout
  }>(),
  {
    modelValue: '',
    placeholder: '',
    buttonLabel: '搜索',
    layout: 'toolbar'
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: string]
  search: []
}>()

const displayValue = computed(() => props.modelValue ?? '')

const rootClass = computed(() => `toolbar-search-pill--${props.layout}`)

function onInput(e: Event) {
  emit('update:modelValue', (e.target as HTMLInputElement).value)
}

function emitSearch() {
  emit('search')
}
</script>

<style scoped>
.toolbar-search-pill {
  height: 52px;
  background: #fff;
  border-radius: 26px;
  padding: 0 8px 0 20px;
  display: flex;
  align-items: center;
  box-shadow:
    0 1px 2px rgba(15, 23, 42, 0.06),
    0 4px 12px rgba(15, 23, 42, 0.08),
    0 12px 28px rgba(15, 23, 42, 0.06);
  border: 1px solid #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.toolbar-search-pill:focus-within {
  border-color: #3b82f6;
  box-shadow:
    0 0 0 3px rgba(59, 130, 246, 0.22),
    0 4px 14px rgba(37, 99, 235, 0.2),
    0 14px 32px rgba(15, 23, 42, 0.12);
}

.toolbar-search-pill--toolbar {
  flex: 1;
  min-width: 0;
  max-width: 460px;
}

.toolbar-search-pill--pane {
  width: 100%;
  max-width: 460px;
}

.toolbar-search-pill--fluid {
  width: 100%;
  padding-left: 16px;
}

.toolbar-search-pill__icon {
  color: #3b82f6;
  font-size: 18px;
  flex-shrink: 0;
}

.toolbar-search-pill__input {
  flex: 1;
  min-width: 0;
  border: none;
  outline: none;
  font-size: 15px;
  margin-left: 10px;
  background: transparent;
}

.toolbar-search-pill--fluid .toolbar-search-pill__input {
  font-size: 14px;
  margin-left: 8px;
}

.toolbar-search-pill__btn {
  flex-shrink: 0;
  background: linear-gradient(180deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  box-shadow:
    0 1px 0 rgba(255, 255, 255, 0.2) inset,
    0 2px 4px rgba(29, 78, 216, 0.35),
    0 6px 14px rgba(37, 99, 235, 0.28);
}

.toolbar-search-pill--fluid .toolbar-search-pill__btn {
  padding: 8px 16px;
  font-size: 13px;
}
</style>
