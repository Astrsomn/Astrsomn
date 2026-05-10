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

            <a-form-item class="span-2" label="默认预设">
              <a-segmented v-model:value="form.isDefault" :options="[{label:'否', value:'N'}, {label:'是', value:'Y'}]" block size="large" />
            </a-form-item>

            <a-form-item class="span-2" label="关联账号" name="accountKey">
              <a-space class="w-full">
                <a-input
                  v-model:value="form.accountKey"
                  :placeholder="form.accountKey ? form.accountKey : '请选择关联账号'"
                  size="large"
                  :disabled="true"
                  class="cursor-pointer flex-1"
                  @click="emit('open-account-selector')"
                />
                <a-button type="primary" size="large" @click="emit('open-account-selector')">
                  选择账号
                </a-button>
              </a-space>
            </a-form-item>
          </div>
        </div>
      </a-form>
    </div>

    <AccountSelectorTable
      v-model:open="accountSelectorOpen"
      @select="handleAccountSelect"
    />
  </aside>
</template>

<script setup lang="ts">
import { InfoCircleOutlined } from '@ant-design/icons-vue'
import AstrsomnKeyGenerator from '@/components/home/AstrsomnKeyGenerator.vue'
import { AI_INSTANCE_KEY_PREFIX } from '@/constants/aiConfigKeyPrefixes'
import AccountSelectorTable from '@/views/admin/ai-config/ai-account/selector/AccountSelectorTable.vue'
import type { AiAccount } from '@/api/aiAccount'

const props = defineProps<{
  form: Record<string, any>
  statusOptions: Array<{ label: string; value: string }>
  instanceKeyRules: any[]
}>()

const emit = defineEmits<{
  (e: 'preset-name-input'): void
  (e: 'open-account-selector'): void
  (e: 'select-account', account: AiAccount): void
}>()

const accountSelectorOpen = defineModel<boolean>('accountSelectorOpen', { default: false })

function handleAccountSelect(account: AiAccount) {
  if (account.accountKey) {
    props.form.accountKey = account.accountKey
  }
  emit('select-account', account)
  accountSelectorOpen.value = false
}
</script>

<style scoped>
.basic-pane { width: 320px; flex-shrink: 0; min-height: 0; display: flex; flex-direction: column; }
.pane-card { flex: 1; min-height: 0; display: flex; flex-direction: column; padding: 18px; overflow-y: auto; }
.glass-card { background: #fff; border-radius: var(--radius-md); border: 1px solid #e2e8f0;  }
.section-title { font-size: 15px; font-weight: 700; color: #1e293b; margin-bottom: 16px; display: flex; align-items: center; gap: 8px; }
.section-title .anticon { color: var(--primary); }
.basic-form-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 0 12px; }
.basic-form-grid .span-2 { grid-column: span 2; }
.basic-form-grid :deep(.ant-segmented) { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 10px; padding: 3px; }
.basic-form-grid :deep(.ant-segmented-item-selected) { background: #eff6ff !important; color: #1d4ed8 !important; }
</style>
