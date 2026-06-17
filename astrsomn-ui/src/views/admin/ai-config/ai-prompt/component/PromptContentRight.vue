<template>
  <div class="content-side">
    <PromptEditor
        v-model="form.promptContent"
        :label="t.contentRight.contentLabel"
        :required="true"
        :loading="loading"
        :min-rows="12"
        :max-rows="30"
        :placeholder="t.contentRight.contentPlaceholder"
        :improve-button-text="t.contentRight.beautifyButton"
        :loading-title="t.contentRight.beautifyingTitle"
        :loading-desc="t.contentRight.beautifyingDesc"
        :char-count-label="t.contentRight.charCount"
        @improve="handleImprove"
    >
      <template #footer-left>
        <InfoCircleOutlined class="w-3.5 h-3.5 text-slate-500"/>
        {{ t.contentRight.placeholderTip }}
      </template>
    </PromptEditor>

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
import {ref} from 'vue'
import {message} from 'ant-design-vue'
import {InfoCircleOutlined} from '@ant-design/icons-vue'
import {aiPromptApi} from '@/api/aiPrompt'
import {usePageTranslation} from '@/locales/pages.ts'
import PromptEditor from '@/views/admin/ai-config/component/PromptEditor.vue'

const t = usePageTranslation('ai-prompt')

const props = defineProps<{
  form: Record<string, any>
}>()

const loading = ref(false)
const diffModalVisible = ref(false)
const originalContent = ref('')
const improvedContent = ref('')

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

/* ── diff modal ── */
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
