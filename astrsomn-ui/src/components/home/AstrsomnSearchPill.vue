<template>
  <div :class="rootClass" class="toolbar-search-pill">
    <SearchOutlined class="toolbar-search-pill__left-icon"/>
    <input
        :placeholder="placeholder"
        :value="displayValue"
        class="toolbar-search-pill__input"
        type="text"
        @input="onInput"
        @keyup.enter="emitSearch"
    />
    <button class="toolbar-search-pill__btn" type="button" @click="emitSearch">
      <SearchOutlined/>
    </button>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {SearchOutlined} from '@ant-design/icons-vue'

export type ToolbarSearchPillLayout = 'toolbar' | 'pane' | 'fluid'

const props = withDefaults(
    defineProps<{
      modelValue?: string | null
      placeholder?: string
      layout?: ToolbarSearchPillLayout
    }>(),
    {
      modelValue: '',
      placeholder: '搜索内容...',
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
/* 容器基础样式：无背景，轻边框 */
.toolbar-search-pill {
  height: 48px;
  background: transparent;
  border-radius: var(--radius-md);
  padding: 0 6px 0 16px;
  display: flex;
  align-items: center;
  border: 1px solid var(--border-default, rgba(0, 0, 0, 0.1));
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 悬浮样式：不要白色，改用阴影和边框强化 */
.toolbar-search-pill:hover {
  border-color: var(--primary, #3b82f6);
  /* 使用投影来营造“浮起来”的感觉，而不是靠背景填充 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05),
  0 1px 2px rgba(0, 0, 0, 0.02);
  transform: translateY(-1px); /* 轻微上移，增加灵动感 */
}

/* 聚焦状态：强化主色调呼吸感 */
.toolbar-search-pill:focus-within {
  border-color: var(--primary, #3b82f6);
  background: transparent;
  box-shadow: 0 0 0 4px color-mix(in srgb, var(--primary, #3b82f6) 10%, transparent);
}


.toolbar-search-pill__left-icon {
  color: var(--text-placeholder, #9ca3af);
  font-size: 17px;
  flex-shrink: 0;
}

.toolbar-search-pill__input {
  flex: 1;
  min-width: 0;
  border: none;
  outline: none;
  font-size: 14px;
  margin-left: 12px;
  background: transparent;
  color: var(--text-primary);
}

.toolbar-search-pill__input::placeholder {
  color: var(--text-placeholder);
}

/* 按钮样式：保持主色，强调点击感 */
.toolbar-search-pill__btn {
  width: 36px;
  height: 36px;
  flex-shrink: 0;
  background: var(--primary, #3b82f6);
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 6px color-mix(in srgb, var(--primary, #3b82f6) 30%, transparent);
}

.toolbar-search-pill__btn:hover {
  filter: brightness(1.1);
  transform: scale(1.08);
}

.toolbar-search-pill__btn:active {
  transform: scale(0.92);
}
</style>