<template>
  <aside class="basic-pane">
    <div class="pane-card glass-card scroll-y">
      <a-form layout="vertical" :model="form">
        <div class="config-section">
          <h3 class="section-title"><InfoCircleOutlined /> 基础定义</h3>
          <div class="basic-form-grid">
            <a-form-item class="span-2" label="预设名称" name="instanceName" :rules="[{ required: true, message: '请输入名称' }]">
              <a-input
                v-model:value="form.instanceName"
                placeholder="默认与端点名称一致，可改为任意展示名"
                size="large"
                @update:value="emit('preset-name-input')"
              />
            </a-form-item>

            <a-form-item class="span-2" label="实例标识 (instanceKey)" name="instanceKey" :rules="instanceKeyRules">
              <AstrsomnKeyGenerator
                v-model="form.instanceKey"
                :prefix="AI_INSTANCE_KEY_PREFIX"
                placeholder="留空可自动生成"
                size="large"
              />
            </a-form-item>

            <a-form-item class="span-2" label="运行状态">
              <a-segmented v-model:value="form.status" :options="statusOptions" block size="large" />
            </a-form-item>
          </div>
        </div>
      </a-form>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { InfoCircleOutlined } from '@ant-design/icons-vue'
import AstrsomnKeyGenerator from '@/components/home/AstrsomnKeyGenerator.vue'
import { AI_INSTANCE_KEY_PREFIX } from '@/constants/aiConfigKeyPrefixes'

defineProps<{
  form: Record<string, any>
  statusOptions: Array<{ label: string; value: string }>
  instanceKeyRules: any[]
}>()

const emit = defineEmits<{
  (e: 'preset-name-input'): void
}>()
</script>

<style scoped>
.basic-pane { width: 320px; flex-shrink: 0; }
.pane-card { height: 100%; display: flex; flex-direction: column; padding: 18px; }
.glass-card { background: #fff; border-radius: 16px; border: 1px solid #e2e8f0; box-shadow: 0 2px 12px rgba(15, 23, 42, 0.04); }
.scroll-y { overflow-y: auto; }
.section-title { font-size: 15px; font-weight: 700; color: #1e293b; margin-bottom: 16px; display: flex; align-items: center; gap: 8px; }
.section-title .anticon { color: var(--primary); }
.basic-form-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 0 12px; }
.basic-form-grid .span-2 { grid-column: span 2; }
.basic-form-grid :deep(.ant-segmented) { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 10px; padding: 3px; }
.basic-form-grid :deep(.ant-segmented-item-selected) { background: #eff6ff !important; color: #1d4ed8 !important; }
</style>
