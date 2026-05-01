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
      <a-select
        mode="tags"
        v-model:value="form.scene"
        :options="sceneOptions"
        :token-separators="[' ', '\n', '\t']"
        placeholder="输入后按空格生成标签，可从下拉库多选"
        allow-clear
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
  sceneOptions?: Array<{ label: string; value: string }>
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

</style>
