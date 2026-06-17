<template>
  <div class="prompt-editor">
    <!-- 编辑器顶部控制条 -->
    <div v-if="!hideHeader" class="editor-header">
      <label v-if="label" class="editor-label">
        <span v-if="required" class="required-mark">*</span> {{ label }}
      </label>
      <button
          v-if="improveButtonText"
          :disabled="loading"
          class="improve-btn"
          @click.stop="$emit('improve')"
      >
        <ThunderboltOutlined :spin="loading" class="improve-icon"/>
        <span>{{ improveButtonText }}</span>
      </button>
    </div>

    <!-- 编辑器主体 -->
    <div class="editor-body">
      <div class="line-numbers">
        <div v-for="n in lineCount" :key="n" class="line-number">{{ String(n).padStart(2, '0') }}</div>
      </div>
      <div class="editor-area-wrapper">
        <a-textarea
            :value="modelValue"
            :auto-size="{ minRows, maxRows }"
            :placeholder="placeholder"
            class="editor-textarea"
            @update:value="$emit('update:modelValue', $event)"
        />
        <div v-if="loading" class="ai-loader">
          <div class="loader-spinner">
            <div class="spinner-ring"></div>
            <CodeOutlined class="loader-code-icon"/>
          </div>
          <div class="loader-text">
            <p class="loader-title">{{ loadingTitle }}</p>
            <p class="loader-desc">{{ loadingDesc }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑器底部状态栏 -->
    <div v-if="showFooter" class="editor-footer">
      <span class="footer-left">
        <slot name="footer-left"/>
      </span>
      <span class="footer-right">{{ charCountLabel }}: {{ charCount }}</span>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { ThunderboltOutlined, CodeOutlined } from '@ant-design/icons-vue'

interface Props {
  modelValue?: string
  loading?: boolean
  placeholder?: string
  label?: string
  required?: boolean
  improveButtonText?: string
  hideHeader?: boolean
  loadingTitle?: string
  loadingDesc?: string
  charCountLabel?: string
  minRows?: number
  maxRows?: number
  showFooter?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  loading: false,
  placeholder: '',
  label: '',
  required: false,
  improveButtonText: 'AI 美化',
  hideHeader: false,
  loadingTitle: '',
  loadingDesc: '',
  charCountLabel: '字数',
  minRows: 8,
  maxRows: 25,
  showFooter: true,
})

defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'improve'): void
}>()

const charCount = computed(() => props.modelValue?.length ?? 0)

const lineCount = computed(() => {
  const content = props.modelValue || ''
  return Math.max(content.split('\n').length, props.minRows)
})
</script>

<style scoped>
.prompt-editor {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  gap: 8px;
}

/* ── header ── */
.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.editor-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
}

.required-mark {
  color: var(--error, #ef4444);
  font-weight: 700;
}

/* ── improve button ── */
.improve-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  background: linear-gradient(135deg, #7c3aed 0%, #4f46e5 100%);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  box-shadow: 0 4px 12px color-mix(in srgb, #7c3aed 40%, transparent);
  transition: all 0.2s ease;
  line-height: 1.5;
}

.improve-btn:hover {
  color: #fff;
  transform: translateY(-1px);
  box-shadow: 0 6px 16px color-mix(in srgb, #7c3aed 50%, transparent);
}

.improve-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.improve-icon {
  font-size: 14px;
  color: #fde047;
}

/* ── editor body ── */
.editor-body {
  flex: 1;
  display: flex;
  background: #ffffff;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  min-height: 160px;
  height: 0;
}

/* ── line numbers ── */
.line-numbers {
  width: 50px;
  background: #f8fafc;
  border-right: 1px solid #e2e8f0;
  padding: 16px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.line-number {
  font-family: 'Fira Code', 'JetBrains Mono', monospace;
  font-size: 11px;
  color: #94a3b8;
  line-height: 1.5;
}

/* ── textarea ── */
.editor-area-wrapper {
  flex: 1;
  position: relative;
  overflow: hidden;
  min-width: 0;
}

.editor-textarea {
  width: 100%;
  height: 100%;
  min-height: 160px;
  padding: 12px;
  background: transparent;
  color: #1e293b;
  font-family: 'Fira Code', 'JetBrains Mono', monospace;
  font-size: 13px;
  line-height: 1.5;
  border: none;
  resize: none;
}

.editor-textarea::placeholder {
  color: #94a3b8;
  opacity: 0.8;
}

.editor-textarea:focus {
  outline: none;
  box-shadow: none;
}

/* ── loading overlay ── */
.ai-loader {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.92);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.loader-spinner {
  position: relative;
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.spinner-ring {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  border: 4px solid color-mix(in srgb, #7c3aed 20%, transparent);
  border-top-color: #7c3aed;
  animation: prompt-editor-spin 1s linear infinite;
}

@keyframes prompt-editor-spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.loader-code-icon {
  font-size: 20px;
  color: #a78bfa;
}

.loader-text {
  text-align: center;
}

.loader-title {
  font-size: 14px;
  font-weight: 600;
  color: #7c3aed;
  margin: 0 0 4px 0;
}

.loader-desc {
  font-size: 12px;
  color: #94a3b8;
  margin: 0;
}

/* ── footer ── */
.editor-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #94a3b8;
  font-family: 'Fira Code', 'JetBrains Mono', monospace;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 6px;
}

.footer-right {
  color: #64748b;
}
</style>
