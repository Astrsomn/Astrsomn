<template>
  <div class="content-side">
    <a-form-item
        class="no-margin-bottom"
        :label="t.contentRight.contentLabel"
        name="promptContent"
    >
      <div class="textarea-wrapper">
        <a-textarea
            v-model:value="form.promptContent"
            :auto-size="{ minRows: 20, maxRows: 20 }"
            class="content-area"
            :placeholder="t.contentRight.contentPlaceholder"
            size="large"
        />
        <a-button
            :loading="loading"
            class="improve-btn"
            type="primary"
            @click="handleImprove"
        >
          <template #icon>
            <ThunderboltOutlined/>
          </template>
          {{ t.contentRight.beautifyButton }}
        </a-button>
      </div>
    </a-form-item>

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
import {ref} from "vue"
import {message} from "ant-design-vue"
import {ThunderboltOutlined} from "@ant-design/icons-vue"
import {aiPromptApi} from "@/api/aiPrompt"
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-prompt')

const props = defineProps<{
  form: Record<string, any>
}>()

const loading = ref(false)
const diffModalVisible = ref(false)
const originalContent = ref("")
const improvedContent = ref("")

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
}

.content-area {
  font-family: "Fira Code", ui-monospace, monospace;
  font-size: 14px;
  background-color: var(--bg-code, #1e293b);
  color: var(--text-code, #e2e8f0);
  padding: 16px;
  border-radius: 12px;
  line-height: 1.6;
  resize: none;
  min-height: 400px;
}

.content-area::placeholder {
  color: var(--text-code-placeholder, #64748b);
}

.no-margin-bottom {
  margin-bottom: 0 !important;
  flex: 1;
}

.textarea-wrapper {
  position: relative;
  width: 100%;
}

.improve-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 10;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 12px color-mix(in srgb, #667eea 40%, transparent);
  transition: all 0.3s ease;
}

.improve-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px color-mix(in srgb, #667eea 50%, transparent);
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
  font-family: "Fira Code", ui-monospace, monospace;
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

<style>
.improve-diff-modal .ant-modal-body {
  padding: 20px;
}
</style>
