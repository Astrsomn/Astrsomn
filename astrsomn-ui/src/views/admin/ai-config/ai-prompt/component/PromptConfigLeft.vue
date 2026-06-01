<template>
  <div class="config-side">
    <a-form-item :label="t.configLeft.titleLabel" name="promptTitle">
      <a-input v-model:value="form.promptTitle" :placeholder="t.configLeft.titlePlaceholder" size="large"/>
    </a-form-item>

    <a-form-item :label="t.configLeft.promptKeyLabel" name="promptKey">
      <AstKeyGenerator
          v-model="form.promptKey"
          :prefix="AI_PROMPT_KEY_PREFIX"
          :placeholder="t.configLeft.promptKeyPlaceholder"
          size="large"
      />
    </a-form-item>

    <a-form-item :label="t.configLeft.statusLabel" name="status">
      <a-segmented
          v-model:value="form.status"
          :options="statusOptions"
          block
          class="status-segmented"
          size="large"
      />
    </a-form-item>

    <a-form-item :label="t.configLeft.sceneLabel" name="scene">
      <a-select
          v-model:value="form.scene"
          :options="sceneOptions"
          :token-separators="[' ', '\n', '\t']"
          allow-clear
          mode="tags"
          :placeholder="t.configLeft.scenePlaceholder"
          size="large"
      />
    </a-form-item>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import AstKeyGenerator from '@/components/home/AstKeyGenerator.vue'
import {AI_PROMPT_KEY_PREFIX} from '@/constants/aiConfigKeyPrefixes'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-prompt')

defineProps<{
  form: Record<string, any>
  sceneOptions?: Array<{ label: string; value: string }>
}>()

const statusOptions = computed(() => [
  {label: t.value.configLeft.statusOptions.enabled, value: 'enabled'},
  {label: t.value.configLeft.statusOptions.disabled, value: 'disabled'}
])
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
