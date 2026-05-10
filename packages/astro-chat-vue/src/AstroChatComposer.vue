<template>
  <div
    v-if="variant === 'full'"
    class="astro-chat-composer-root"
    :class="[`is-${layout}`, `density-${density}`]"
    :style="rootInlineStyle"
  >
    <div class="input-panel" :style="panelInlineStyle">
      <slot name="preview" />
      <div v-if="$slots.toolbar" class="input-toolbar">
        <slot name="toolbar" />
      </div>
      <div class="input-body">
        <a-textarea
          :value="modelValue"
          :auto-size="textareaAutoSize"
          :placeholder="placeholder"
          class="main-textarea"
          :disabled="disabled"
          @update:value="emit('update:modelValue', $event ?? '')"
          @pressEnter="onPressEnter"
          @paste="emit('paste', $event)"
        />
      </div>
      <div class="input-footer">
        <div class="footer-left">
          <slot name="footer-left" />
        </div>
        <div class="footer-right">
          <div v-if="showCharCount && modelValue.length > 0" class="char-count">
            {{ modelValue.length }}
          </div>
          <div v-else-if="isStreaming" class="stream-status">流式回复中</div>
          <a-button
            type="primary"
            class="send-btn"
            :disabled="sendDisabled"
            @click="isStreaming ? emit('stop') : emit('submit')"
          >
            <template #icon>
              <StopOutlined v-if="isStreaming" />
              <ArrowUpOutlined v-else />
            </template>
          </a-button>
        </div>
      </div>
    </div>
  </div>

  <div v-else class="composer-nested-root" :class="`density-${density}`">
    <slot name="preview" />
    <div v-if="$slots.toolbar" class="input-toolbar">
      <slot name="toolbar" />
    </div>
    <div class="input-body">
      <a-textarea
        :value="modelValue"
        :auto-size="textareaAutoSize"
        :placeholder="placeholder"
        class="main-textarea"
        :disabled="disabled"
        @update:value="emit('update:modelValue', $event ?? '')"
        @pressEnter="onPressEnter"
        @paste="emit('paste', $event)"
      />
    </div>
    <div class="input-footer">
      <div class="footer-left">
        <slot name="footer-left" />
      </div>
      <div class="footer-right">
        <div v-if="showCharCount && modelValue.length > 0" class="char-count">
          {{ modelValue.length }}
        </div>
        <div v-else-if="isStreaming" class="stream-status">流式回复中</div>
        <a-button
          type="primary"
          class="send-btn"
          :disabled="sendDisabled"
          @click="isStreaming ? emit('stop') : emit('submit')"
        >
          <template #icon>
            <StopOutlined v-if="isStreaming" />
            <ArrowUpOutlined v-else />
          </template>
        </a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowUpOutlined, StopOutlined } from '@ant-design/icons-vue'
import { computed } from 'vue'

const props = withDefaults(
  defineProps<{
    modelValue: string
    variant?: 'full' | 'nested'
    layout?: 'bottom' | 'centered' | 'embedded'
    density?: 'comfortable' | 'compact'
    maxWidth?: string
    placeholder?: string
    disabled?: boolean
    isStreaming?: boolean
    sendDisabled?: boolean
    showCharCount?: boolean
    minRows?: number
    maxRows?: number
  }>(),
  {
    variant: 'full',
    layout: 'embedded',
    density: 'comfortable',
    maxWidth: '',
    placeholder: '输入消息…',
    disabled: false,
    isStreaming: false,
    sendDisabled: false,
    showCharCount: true,
    minRows: 1,
    maxRows: 6
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: string]
  submit: []
  stop: []
  paste: [event: ClipboardEvent]
}>()

const textareaAutoSize = computed(() => {
  const min = props.density === 'compact' ? Math.min(props.minRows, 2) : props.minRows
  const max = props.density === 'compact' ? Math.min(props.maxRows, 4) : props.maxRows
  return { minRows: min, maxRows: max }
})

const rootInlineStyle = computed(() => {
  if (props.layout === 'embedded' && props.maxWidth) {
    return { maxWidth: props.maxWidth, marginLeft: 'auto', marginRight: 'auto' }
  }
  return undefined
})

const panelInlineStyle = computed(() => {
  if (props.maxWidth && props.layout !== 'embedded') {
    return { maxWidth: props.maxWidth }
  }
  return undefined
})

const onPressEnter = (e: KeyboardEvent) => {
  if (!e.shiftKey) {
    e.preventDefault()
    if (props.isStreaming) return
    if (!props.sendDisabled) emit('submit')
  }
}
</script>

<style scoped>
.composer-nested-root {
  position: relative;
}

.astro-chat-composer-root {
  pointer-events: auto;
  width: 100%;
}

.astro-chat-composer-root.is-bottom {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2;
  pointer-events: none;
  padding: 20px 20px 30px;
  background: transparent;
}

.astro-chat-composer-root.is-centered {
  max-width: 960px;
  margin: 0 auto;
  padding: 0 20px 16px;
}

.astro-chat-composer-root.is-embedded {
  padding: 0;
  max-width: 100%;
}

.astro-chat-composer-root.is-embedded .input-panel {
  max-width: 100%;
}

.input-panel {
  max-width: 840px;
  margin: 0 auto;
  pointer-events: auto;
  background: var(--bg-surface, #fff);
  border: 1px solid var(--border-default, #e5e7eb);
  border-radius: 24px;
  box-shadow: var(--shadow-card, 0 4px 24px rgba(0, 0, 0, 0.06));
  transition:
    border-color 0.3s,
    box-shadow 0.3s;
  overflow: hidden;
  position: relative;
}

.density-compact .input-panel {
  border-radius: 18px;
}

.input-panel:focus-within {
  border-color: var(--primary, #3b82f6);
  box-shadow: 0 0 0 4px var(--primary-hover, rgba(59, 130, 246, 0.12));
}

.input-panel :deep(textarea)::placeholder,
.composer-nested-root :deep(textarea)::placeholder {
  color: var(--chat-input-placeholder, #94a3b8) !important;
  opacity: 1 !important;
}

.input-toolbar {
  padding: 12px 16px 4px;
  display: flex;
  justify-content: space-between;
}

.density-compact .input-toolbar {
  padding: 8px 12px 2px;
}

.input-body {
  padding: 4px 16px;
}

.density-compact .input-body {
  padding: 2px 12px;
}

.main-textarea {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
  font-size: 16px;
  color: var(--text-primary, #0f172a);
  padding: 8px 0;
  resize: none;
}

.density-compact .main-textarea {
  font-size: 14px;
  padding: 6px 0;
}

.input-footer {
  padding: 8px 16px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.density-compact .input-footer {
  padding: 6px 12px 12px;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.char-count {
  font-size: 12px;
  color: var(--text-muted, #94a3b8);
}

.stream-status {
  font-size: 12px;
  color: var(--primary, #3b82f6);
}

.send-btn {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.density-compact .send-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
}

.send-btn.ant-btn-primary {
  background: var(--chat-send-btn-bg, linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%)) !important;
  border-color: transparent !important;
  color: #fff !important;
  box-shadow: var(--chat-send-btn-shadow, 0 4px 15px rgba(59, 130, 246, 0.3));
}

.send-btn.ant-btn-primary :deep(.anticon) {
  color: #fff !important;
}

.send-btn.ant-btn-primary:not(:disabled):hover {
  filter: brightness(1.08);
}

.send-btn.ant-btn-primary.ant-btn-disabled {
  opacity: 0.5;
}
</style>
