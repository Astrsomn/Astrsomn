<template>
  <a-modal
      :footer="null"
      :open="open"
      destroy-on-close
      title="提示词美化对比"
      width="800px"
      @update:open="onOpen"
  >
    <div class="diff-container">
      <div class="diff-header">
        <div class="diff-title original">原内容</div>
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
        <a-button @click="onOpen(false)">取消</a-button>
        <a-button type="primary" @click="emit('apply')">使用美化后内容</a-button>
      </a-space>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
defineProps<{
  open: boolean
  originalContent: string
  improvedContent: string
}>()

const emit = defineEmits<{
  'update:open': [open: boolean]
  apply: []
}>()

function onOpen(v: boolean) {
  emit('update:open', v)
}
</script>

<style scoped>
.diff-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.diff-header {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.diff-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-secondary);
}

.diff-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  max-height: 360px;
}

.diff-text {
  margin: 0;
  padding: 12px;
  border-radius: 8px;
  font-size: 12px;
  white-space: pre-wrap;
  word-break: break-word;
  max-height: 340px;
  overflow: auto;
}

.original-text {
  background: color-mix(in srgb, var(--error) 5%, var(--bg-card));
  border: 1px solid var(--error);
}

.improved-text {
  background: color-mix(in srgb, var(--success) 5%, var(--bg-card));
  border: 1px solid var(--success);
}

.diff-footer {
  margin-top: 16px;
  text-align: right;
}
</style>
