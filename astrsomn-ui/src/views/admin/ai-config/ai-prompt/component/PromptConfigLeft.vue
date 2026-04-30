<template>
  <div class="config-side">
    <a-form-item label="标题" name="promptTitle">
      <a-input v-model:value="form.promptTitle" placeholder="标题" size="large" />
    </a-form-item>

    <a-form-item label="Prompt Key" name="promptKey">
      <AstrsomnKeyGenerator
        v-model="form.promptKey"
        :prefix="AI_PROMPT_KEY_PREFIX"
        placeholder="唯一标识（选填）"
        size="large"
      />
    </a-form-item>

    <a-form-item label="状态" name="status">
      <a-segmented
        v-model:value="form.status"
        :options="statusOptions"
        block
        size="large"
        class="status-segmented"
      />
    </a-form-item>

    <a-form-item label="场景" name="scene">
      <a-textarea
        minRows="4"
        v-model:value="form.scene"
        placeholder="分类"
        allow-clear
        class="content-area"
        size="large"
      />
    </a-form-item>
  </div>
</template>

<script setup lang="ts">
import AstrsomnKeyGenerator from '@/components/home/AstrsomnKeyGenerator.vue'
import { AI_PROMPT_KEY_PREFIX } from '@/constants/aiConfigKeyPrefixes'

defineProps<{
  form: Record<string, any>
}>()

const statusOptions = [
  { label: '启用', value: 'enabled' },
  { label: '停用', value: 'disabled' }
]
</script>

<style scoped>
.config-side {
  width: 360px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.content-area {
  font-family: "Fira Code", ui-monospace, monospace;
  font-size: 13px;
  background-color: var(--bg-input);
  color: var(--text-primary);
  padding: 12px;
  border-radius: 8px;
  line-height: 1.6;
  resize: none;
}

</style>
