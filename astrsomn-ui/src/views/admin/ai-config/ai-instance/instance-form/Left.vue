<template>
  <aside class="basic-pane">
    <div class="pane-card glass-card scroll-y">
      <a-form :model="form" layout="vertical">
        <div class="config-section">
          <h3 class="section-title">
            <InfoCircleOutlined/>
            {{ t.left.sectionTitle }}
          </h3>
          <div class="basic-form-grid">
            <a-form-item :rules="[{ required: true, message: t.left.presetName.required }]" class="span-2" :label="t.left.presetName.label"
                         name="instanceName">
              <a-input
                  v-model:value="form.instanceName"
                  :placeholder="t.left.presetName.placeholder"
                  size="large"
                  @update:value="emit('preset-name-input')"
              />
            </a-form-item>

            <a-form-item :rules="[{ required: true, message: t.left.instanceKey.required }, ...instanceKeyRules]" class="span-2" :label="t.left.instanceKey.label"
                         name="instanceKey">
              <AstKeyGenerator
                  v-model="form.instanceKey"
                  :disabled="isEdit"
                  :prefix="AI_INSTANCE_KEY_PREFIX"
                  :placeholder="t.left.instanceKey.placeholder"
                  size="large"
              />
            </a-form-item>

            <a-form-item class="span-2" :label="t.left.status.label">
              <AstegmentedButton :buttons="statusButtons" block/>
            </a-form-item>

            <a-form-item class="span-2" :label="t.left.defaultPreset.label">
              <AstegmentedButton :buttons="defaultButtons" block/>
            </a-form-item>

            <a-form-item :rules="[{ required: true, message: t.left.account.required }]" class="span-2" :label="t.left.account.label"
                         name="accountKey">
              <a-space class="w-full">
                <a-input
                    v-model:value="form.accountKey"
                    :disabled="true"
                    :placeholder="form.accountKey ? form.accountKey : t.left.account.placeholder"
                    class="cursor-pointer flex-1"
                    size="large"
                    @click="emit('open-account-selector')"
                />
                <a-button size="large" type="primary" @click="emit('open-account-selector')">
                  {{ t.left.account.selectButton }}
                </a-button>
              </a-space>
            </a-form-item>
          </div>
        </div>
      </a-form>
    </div>

    <AccountSelectorDrawer
        v-model:open="accountSelectorOpen"
        @select="handleAccountSelect"
    />
  </aside>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {
  CheckOutlined,
  CloseOutlined,
  InfoCircleOutlined,
  PauseCircleOutlined,
  ThunderboltOutlined
} from '@ant-design/icons-vue'
import AstKeyGenerator from '@/components/home/AstKeyGenerator.vue'
import AstegmentedButton from '@/components/home/AstegmentedButton.vue'
import {AI_INSTANCE_KEY_PREFIX} from '@/constants/aiConfigKeyPrefixes'
import AccountSelectorDrawer from '@/views/admin/ai-config/ai-account/selector/AccountSelectorDrawer.vue'
import type {AiAccount} from '@/api/aiAccount'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-instance')

const props = defineProps<{
  form: Record<string, any>
  isEdit: boolean
  instanceKeyRules: any[]
}>()

const emit = defineEmits<{
  (e: 'preset-name-input'): void
  (e: 'open-account-selector'): void
  (e: 'select-account', account: AiAccount): void
}>()

const statusButtons = computed(() => [
  {
    label: t.value.left.status.activate,
    icon: ThunderboltOutlined,
    type: (props.form.status === 'enabled' ? 'primary' : 'default') as 'primary' | 'default',
    plain: props.form.status !== 'enabled',
    onClick: () => {
      props.form.status = 'enabled'
    }
  },
  {
    label: t.value.left.status.deactivate,
    icon: PauseCircleOutlined,
    type: 'default' as const,
    plain: props.form.status !== 'disabled',
    onClick: () => {
      props.form.status = 'disabled'
    }
  }
])

const defaultButtons = computed(() => [
  {
    label: t.value.left.defaultPreset.yes,
    icon: CheckOutlined,
    type: (props.form.isDefault === 'Y' ? 'primary' : 'default') as 'primary' | 'default',
    plain: props.form.isDefault !== 'Y',
    onClick: () => {
      props.form.isDefault = 'Y'
    }
  },
  {
    label: t.value.left.defaultPreset.no,
    icon: CloseOutlined,
    type: 'default' as const,
    plain: props.form.isDefault !== 'N',
    onClick: () => {
      props.form.isDefault = 'N'
    }
  }
])

const accountSelectorOpen = defineModel<boolean>('accountSelectorOpen', {default: false})

function handleAccountSelect(account: AiAccount) {
  if (account.accountKey) {
    props.form.accountKey = account.accountKey
  }
  emit('select-account', account)
  accountSelectorOpen.value = false
}
</script>

<style scoped>
.basic-pane {
  width: 320px;
  flex-shrink: 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.pane-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  padding: 18px;
  overflow-y: auto;
}

.glass-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-default);
}

.section-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title .anticon {
  color: var(--primary);
}

.basic-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 12px;
}

.basic-form-grid .span-2 {
  grid-column: span 2;
}
</style>
