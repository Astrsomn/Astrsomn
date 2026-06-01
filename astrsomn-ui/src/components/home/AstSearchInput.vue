<template>
  <div :class="rootClass" class="toolbar-search-pill">
    <input
        :placeholder="effectivePlaceholder"
        :value="displayValue"
        class="toolbar-search-pill__input"
        type="text"
        @input="onInput"
        @keyup.enter="emitSearch"
    />
    <button :style="btnStyle" class="toolbar-search-pill__btn" type="button" @click="emitSearch">
      <SearchOutlined />
    </button>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {SearchOutlined} from '@ant-design/icons-vue'
import {usePageTranslation} from '@/locales/pages.ts'

export type ToolbarSearchPillLayout = 'toolbar' | 'pane' | 'fluid'

const t = usePageTranslation('common')

const props = withDefaults(
    defineProps<{
      modelValue?: string | null
      placeholder?: string
      layout?: ToolbarSearchPillLayout
      btnColor?: string
    }>(),
    {
      modelValue: '',
      placeholder: '',
      layout: 'toolbar',
      btnColor: '#ffffff',
    }
)

const emit = defineEmits<{
  'update:modelValue': [value: string]
  search: []
}>()

const effectivePlaceholder = computed(() => props.placeholder || t.value.searchInput.placeholder)
const displayValue = computed(() => props.modelValue ?? '')
const rootClass = computed(() => `toolbar-search-pill--${props.layout}`)
const btnStyle = computed(() => ({ background: props.btnColor }))

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
  background: var(--bg-input, #f8fafc);
  border-radius: 10px;
  padding: 0 6px 0 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  border: 1px solid var(--border-default);
  transition: all 0.2s;
}

.toolbar-search-pill:hover {
  border-color: var(--border-subtle);
  background: var(--bg-surface);
}

.toolbar-search-pill:focus-within {
  border-color: var(--primary);
  background: var(--bg-card);
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--primary) 10%, transparent);
}

.toolbar-search-pill__input {
  flex: 1;
  min-width: 0;
  border: none;
  outline: none;
  font-size: 13px;
  background: transparent;
  color: var(--text-primary);
}

.toolbar-search-pill__input::placeholder {
  color: var(--text-placeholder);
}

.toolbar-search-pill__btn {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 14px;
  color: var(--text-secondary);
  flex-shrink: 0;
  transition: all 0.15s;
}

.toolbar-search-pill__btn:hover {
  filter: brightness(0.92);
}
</style>