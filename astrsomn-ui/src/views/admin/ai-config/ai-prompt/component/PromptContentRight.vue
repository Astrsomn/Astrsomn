<template>
  <div class="content-side">
    <!-- 编辑器顶部控制条 -->
    <div class="editor-header">
      <label class="editor-label">
        <span class="required-mark">*</span> {{ t.contentRight.contentLabel }}
      </label>
      <a-button
          :loading="loading"
          class="improve-btn"
          type="primary"
          @click="handleImprove"
      >
        <ThunderboltOutlined class="w-3.5 h-3.5 text-yellow-300"/>
        <span>{{ t.contentRight.beautifyButton }}</span>
      </a-button>
    </div>

    <!-- 编辑器主体 -->
    <div class="editor-container">
      <!-- 行号区域 -->
      <div class="line-numbers">
        <div v-for="n in lineCount" :key="n" class="line-number">{{ String(n).padStart(2, '0') }}</div>
      </div>

      <!-- 编辑区域 -->
      <div class="editor-area-wrapper">
        <a-textarea
            v-model:value="form.promptContent"
            :auto-size="{ minRows: 12, maxRows: 30 }"
            class="content-area"
            :placeholder="t.contentRight.contentPlaceholder"
            @input="updateCharCount"
        />

        <!-- AI 美化加载遮罩 -->
        <div v-if="loading" class="ai-loader">
          <div class="loader-spinner">
            <div class="spinner-ring"></div>
            <CodeOutlined class="w-6 h-6 text-violet-400"/>
          </div>
          <div class="loader-text">
            <p class="loader-title">{{ t.contentRight.beautifyingTitle }}</p>
            <p class="loader-desc">{{ t.contentRight.beautifyingDesc }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑器底部状态栏 -->
    <div class="editor-footer">
      <span class="footer-left">
        <InfoCircleOutlined class="w-3.5 h-3.5 text-slate-500"/>
        {{ t.contentRight.placeholderTip }}
      </span>
      <span class="footer-right">{{ t.contentRight.charCount }}: {{ charCount }}</span>
    </div>

    <!-- 美化对比弹窗 -->
    <a-modal
        v-model:open="diffModalVisible"
        :footer="null"
        class="improve-diff-modal"
        :title="t.contentRight.diffModalTitle"
        width="800px"
    >
      <div class="diff-container">
        <div class="diff-header">
          <div class="diff-title original">{{ t.contentRight.diffOriginalTitle }}</div>
          <div class="diff-title improved">{{ t.contentRight.diffImprovedTitle }}</div>
        </div>
        <div class="diff-content">
          <div class="diff-original">
            <pre class="diff-text original-text">{{ originalContent }}</pre>
          </div>
          <div class="diff-improved">
            <pre class="diff-text improved-text">{{ improvedContent }}</pre>
          </div>
        </div>
      </div>
      <div class="diff-footer">
        <a-space>
          <a-button @click="diffModalVisible = false">{{ t.contentRight.diffCancelButton }}</a-button>
          <a-button type="primary" @click="handleApply">{{ t.contentRight.diffApplyButton }}</a-button>
        </a-space>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import {ref, computed} from 'vue'
import {message} from 'ant-design-vue'
import {ThunderboltOutlined, CodeOutlined, InfoCircleOutlined} from '@ant-design/icons-vue'
import {aiPromptApi} from '@/api/aiPrompt'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-prompt')

const props = defineProps<{
  form: Record<string, any>
}>()

const loading = ref(false)
const diffModalVisible = ref(false)
const originalContent = ref('')
const improvedContent = ref('')

const charCount = computed(() => props.form.promptContent?.length || 0)

const lineCount = computed(() => {
  const content = props.form.promptContent || ''
  return Math.max(content.split('\n').length, 12)
})

function updateCharCount() {
}

async function handleImprove() {
  const content = props.form.promptContent
  if (!content?.trim()) {
    message.warning(t.value.contentRight.contentRequiredWarning)
    return
  }

  originalContent.value = content
  loading.value = true
  try {
    const improved = await aiPromptApi.beautify(content)
    improvedContent.value = improved
    diffModalVisible.value = true
  } catch (error) {
    message.error(t.value.contentRight.beautifyFailed)
  } finally {
    loading.value = false
  }
}

function handleApply() {
  props.form.promptContent = improvedContent.value
  diffModalVisible.value = false
  message.success(t.value.contentRight.beautifyApplied)
}
</script>

<style scoped>
.content-side {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: var(--bg-slate-950, #0f172a);
  padding: 12px;
  height: 100%;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-slate-800, #334155);
  margin-bottom: 12px;
}

.editor-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-slate-200, #e2e8f0);
  display: flex;
  align-items: center;
  gap: 4px;
}

.required-mark {
  color: var(--error, #ef4444);
  font-weight: 700;
}

.improve-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  font-size: 12px;
  font-weight: 600;
  background: linear-gradient(135deg, #7c3aed 0%, #4f46e5 100%);
  border: none;
  border-radius: 8px;
  box-shadow: 0 4px 12px color-mix(in srgb, #7c3aed 40%, transparent);
  transition: all 0.2s ease;
}

.improve-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px color-mix(in srgb, #7c3aed 50%, transparent);
}

.editor-container {
  flex: 1;
  display: flex;
  background: var(--bg-slate-900, #1e293b);
  border-radius: 8px;
  border: 1px solid var(--border-slate-800, #334155);
  overflow: hidden;
  min-height: 200px;
  height: 0;
}

.line-numbers {
  width: 50px;
  background: var(--bg-slate-950, #0f172a);
  border-right: 1px solid var(--border-slate-800, #334155);
  padding: 16px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.line-number {
  font-family: monospace;
  font-size: 12px;
  color: var(--text-slate-600, #64748b);
}

.editor-area-wrapper {
  flex: 1;
  position: relative;
  overflow: hidden;
}

.content-area {
  width: 100%;
  height: 100%;
  min-height: 200px;
  padding: 12px;
  background: transparent;
  color: var(--text-slate-100, #f1f5f9);
  font-family: 'Fira Code', monospace;
  font-size: 13px;
  line-height: 1.5;
  border: none;
  resize: none;
}

.content-area::placeholder {
  color: var(--text-slate-500, #94a3b8);
  opacity: 0.8;
}

.content-area:focus {
  outline: none;
  box-shadow: none;
}

.ai-loader {
  position: absolute;
  inset: 0;
  background: var(--bg-slate-950, #0f172a);
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
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.loader-text {
  text-align: center;
}

.loader-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-violet-300, #c4b5fd);
  margin: 0 0 4px 0;
}

.loader-desc {
  font-size: 12px;
  color: var(--text-slate-500, #94a3b8);
  margin: 0;
}

.editor-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  margin-top: 10px;
  font-size: 12px;
  color: var(--text-slate-500, #94a3b8);
  font-family: monospace;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 6px;
}

.footer-right {
  color: var(--text-slate-400, #cbd5e1);
}

.diff-container {
  border: 1px solid var(--border-default);
  border-radius: 8px;
  overflow: hidden;
}

.diff-header {
  display: flex;
  background: var(--bg-surface);
  border-bottom: 1px solid var(--border-default);
}

.diff-title {
  flex: 1;
  padding: 12px 16px;
  font-weight: 600;
  font-size: 14px;
}

.diff-title.original {
  background: color-mix(in srgb, var(--error) 5%, var(--bg-card));
  color: var(--error);
  border-right: 1px solid var(--border-default);
}

.diff-title.improved {
  background: color-mix(in srgb, var(--success) 5%, var(--bg-card));
  color: var(--success);
}

.diff-content {
  display: flex;
  min-height: 300px;
  max-height: 500px;
}

.diff-original,
.diff-improved {
  flex: 1;
  padding: 16px;
  overflow: auto;
  background: var(--bg-card);
}

.diff-original {
  border-right: 1px solid var(--border-default);
}

.diff-text {
  margin: 0;
  font-family: 'Fira Code', monospace;
  font-size: 13px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
}

.original-text {
  color: var(--error);
  text-decoration: line-through;
  opacity: 0.8;
}

.improved-text {
  color: var(--success);
}

.diff-footer {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--border-default);
  text-align: right;
}
</style>
