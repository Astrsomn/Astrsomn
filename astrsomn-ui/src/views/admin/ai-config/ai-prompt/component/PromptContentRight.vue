<template>
  <div class="content-side">
    <a-form-item
      label="提示词内容"
      name="promptContent"
      class="no-margin-bottom"
    >
      <a-textarea
        v-model:value="form.promptContent"
        :auto-size="{ minRows: 20, maxRows: 20 }"
        placeholder="请输入 System 或 User Prompt..."
        class="content-area"
        size="large"
      />
    </a-form-item>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  form: Record<string, any>
}>()
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
</style>
