<template>
  <div class="content-side">
    <a-form-item
      label="提示词内容"
      name="promptContent"
      class="no-margin-bottom"
    >
      <div class="textarea-wrapper">
        <a-textarea
          v-model:value="form.promptContent"
          :auto-size="{ minRows: 20, maxRows: 20 }"
          placeholder="请输入 System 或 User Prompt..."
          class="content-area"
          size="large"
        />
        <a-button
          type="primary"
          class="improve-btn"
          :loading="loading"
          @click="handleImprove"
        >
          <template #icon>
            <ThunderboltOutlined />
          </template>
          美化
        </a-button>
      </div>
    </a-form-item>

    <a-modal
      v-model:open="diffModalVisible"
      title="提示词美化对比"
      width="800px"
      :footer="null"
      class="improve-diff-modal"
    >
      <div class="diff-container">
        <div class="diff-header">
          <div class="diff-title original">原内容（已删除）</div>
          <div class="diff-title improved">美化后</div>
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
          <a-button @click="diffModalVisible = false">取消</a-button>
          <a-button type="primary" @click="handleApply">使用美化后内容</a-button>
        </a-space>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue"
import { message } from "ant-design-vue"
import { ThunderboltOutlined } from "@ant-design/icons-vue"
import { aiPromptApi } from "@/api/aiPrompt"

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
    message.warning("请先输入提示词内容")
    return
  }

  originalContent.value = content
  loading.value = true
  try {
    const improved = await aiPromptApi.improvePrompt(content)
    improvedContent.value = improved
    diffModalVisible.value = true
  } catch (error) {
    message.error("美化失败，请重试")
  } finally {
    loading.value = false
  }
}

function handleApply() {
  props.form.promptContent = improvedContent.value
  diffModalVisible.value = false
  message.success("已应用美化后的提示词")
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
  background-color: #1e293b;
  color: #e2e8f0;
  padding: 16px;
  border-radius: 12px;
  line-height: 1.6;
  resize: none;
  min-height: 400px;
}

.content-area::placeholder {
  color: #64748b;
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
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.improve-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.5);
}

.diff-container {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
}

.diff-header {
  display: flex;
  background: #f5f5f5;
  border-bottom: 1px solid #e8e8e8;
}

.diff-title {
  flex: 1;
  padding: 12px 16px;
  font-weight: 600;
  font-size: 14px;
}

.diff-title.original {
  background: #fff1f0;
  color: #ff4d4f;
  border-right: 1px solid #e8e8e8;
}

.diff-title.improved {
  background: #f6ffed;
  color: #52c41a;
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
  background: #fff;
}

.diff-original {
  background: #fffafafa;
  border-right: 1px solid #e8e8e8;
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
  color: #ff4d4f;
  text-decoration: line-through;
  opacity: 0.8;
}

.improved-text {
  color: #52c41a;
}

.diff-footer {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e8e8e8;
  text-align: right;
}
</style>

<style>
.improve-diff-modal .ant-modal-body {
  padding: 20px;
}
</style>