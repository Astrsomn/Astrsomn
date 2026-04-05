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
  height: 50px;
  background: var(--bg-surface);
  border-radius: var(--radius-pro, 30px);
  padding: 0 8px 0 20px;
  display: flex;
  align-items: center;
  border: 1px solid var(--border-default);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.toolbar-search-pill:focus-within {
  border-color: #3b82f6;
  box-shadow: var(--shadow-overview), 0 0 0 3px color-mix(in srgb, var(--primary) 22%, transparent);
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
  color: var(--primary);
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
  color: var(--text-primary);
}

.toolbar-search-pill__input::placeholder {
  color: var(--text-placeholder);
}

.toolbar-search-pill--fluid .toolbar-search-pill__input {
  font-size: 14px;
  margin-left: 8px;
}

.toolbar-search-pill__btn {
  flex-shrink: 0;
  background: var(--primary-gradient);
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  box-shadow: var(--shadow-overview);
}

.toolbar-search-pill--fluid .toolbar-search-pill__btn {
  padding: 8px 16px;
  font-size: 13px;
}
</style>
