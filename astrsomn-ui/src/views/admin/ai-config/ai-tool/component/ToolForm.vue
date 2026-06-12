<template>
  <AstModal
      :body-height="'80vh'"
      :confirm-loading="confirmLoading"
      :confirm-text="mode === 'create' ? t.form.createConfirmText : t.form.editConfirmText"
      :destroy-on-close="true"
      :header-height="'72px'"
      :max-width="'80vw'"
      :open="open"
      :width="'80vw'"
      @confirm="handleOk"
      @update:open="onOpenChange"
  >
    <template #header-logo>
      <div :class="form.type" class="icon-box">
        <BuildOutlined v-if="form.type === 'method'"/>
        <Html5Outlined v-else/>
      </div>
    </template>

    <template #header-title>
      {{ mode === 'create' ? t.form.createTitle : t.form.editTitle }}
    </template>

    <template #header-subtitle>
      {{ t.form.subtitle }}
    </template>

    <a-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="professional-form"
        layout="vertical"
    >
      <div class="form-body-container">
        <div class="form-layout">
          <div class="form-left">
            <div class="section-card">
              <h3 class="section-title">
                <IdcardOutlined/>
                {{ t.form.sectionTitle.basic }}
              </h3>

   

              <div class="form-fields">
                <a-row :gutter="16">
                  <a-col :span="16">
                    <a-form-item :label="t.form.toolName.label" name="toolName">
                      <a-input v-model:value="form.toolName" :placeholder="t.form.toolName.placeholder" size="large"/>
                    </a-form-item>
                  </a-col>
                  <a-col :span="8">
                    <a-form-item :label="t.form.status.label" name="enableFlag">
                      <AstStatusToggle
                          v-model="form.enableFlag"
                          :enabled-label="t.form.status.enabled"
                          :disabled-label="t.form.status.disabled"
                      />
                    </a-form-item>
                  </a-col>
                </a-row>

                <a-form-item :label="t.form.toolKey.label" name="toolKey">
                  <AstKeyGenerator
                      v-model="form.toolKey"
                      :disabled="mode === 'edit'"
                      :prefix="AI_TOOL_KEY_PREFIX"
                      :placeholder="t.form.toolKey.placeholder"
                      size="large"
                  />
                </a-form-item>

                <a-form-item :label="t.form.type.label" name="type">
                  <a-segmented v-model:value="form.type" :options="toolTypeOptions" block size="large"/>
                </a-form-item>

                <a-form-item :label="t.form.description.label" name="description">
                  <a-textarea
                      v-model:value="form.description"
                      :auto-size="{ minRows: 6, maxRows: 10 }"
                      :placeholder="t.form.description.placeholder"
                  />
                </a-form-item>
              </div>
            </div>
          </div>

          <div class="form-right">
            <div class="section-card">
              <h3 class="section-title">
                <RocketOutlined/>
                {{ t.form.sectionTitle.execution }}
              </h3>

              <div class="impl-hint">
                <div class="hint-title">{{ t.form.execution.hintTitle }}</div>
                <p>{{ t.form.execution.hintDescription }}</p>
              </div>

              <div class="form-fields">
                <a-form-item :label="t.form.execution.beanNameLabel" name="beanName">
                  <a-input v-model:value="form.beanName" :placeholder="t.form.execution.beanNamePlaceholder" size="large">
                    <template #prefix>
                      <BlockOutlined style="color: var(--text-muted)"/>
                    </template>
                  </a-input>
                </a-form-item>

                <a-form-item :label="t.form.execution.methodNameLabel" name="methodName">
                  <a-input v-model:value="form.methodName" :placeholder="t.form.execution.methodNamePlaceholder" size="large">
                    <template #prefix>
                      <CodeOutlined style="color: var(--text-muted)"/>
                    </template>
                  </a-input>
                </a-form-item>

                <a-form-item :label="t.form.execution.classNameLabel" name="className">
                  <a-input v-model:value="form.className" :placeholder="t.form.execution.classNamePlaceholder" size="large"/>
                </a-form-item>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-form>
  </AstModal>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {
  BlockOutlined,
  BuildOutlined,
  CodeOutlined,
  Html5Outlined,
  IdcardOutlined,
  RocketOutlined
} from '@ant-design/icons-vue'
import type {FormInstance} from 'ant-design-vue'
import type {AiTool} from '@/api/aiTool.ts'
import AstModal from '@/components/home/AstModal.vue'
import AstStatusToggle from '@/components/home/AstStatusToggle.vue'
import AstKeyGenerator from '@/components/home/AstKeyGenerator.vue'
import {AI_TOOL_KEY_PREFIX} from '@/constants/aiConfigKeyPrefixes.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-tool')

const props = defineProps<{ mode: 'create' | 'edit', confirmLoading: boolean, initial: AiTool | null }>()
const emit = defineEmits<{ submit: [payload: AiTool] }>()
const open = defineModel<boolean>('open', {required: true})

const formRef = ref<FormInstance | null>(null)

const toolTypeOptions = computed(() => [
  {label: t.value.form.type.method, value: 'method'},
  {label: t.value.form.type.html, value: 'html'}
])

function emptyForm(): AiTool {
  return {
    toolKey: '', toolName: '', description: '',
    beanName: '', methodName: '', type: 'method', enableFlag: 'enabled',
    className: ''
  }
}

const form = reactive<AiTool>(emptyForm())

const rules = computed(() => ({
  toolName: [{required: true, message: t.value.form.validation.toolNameRequired}],
  type: [{required: true, message: t.value.form.validation.typeRequired}],
  beanName: [{required: true, message: t.value.form.validation.beanNameRequired}],
  methodName: [{required: true, message: t.value.form.validation.methodNameRequired}]
}))

const onOpenChange = (val: boolean) => {
  open.value = val
}

function assignFromInitial(src: AiTool) {
  Object.assign(form, emptyForm(), src)
}

watch(() => [open.value, props.initial] as const, ([isOpen, initial]) => {
  if (isOpen) {
    if (initial && Object.keys(initial).length > 0) assignFromInitial(initial)
    else Object.assign(form, emptyForm())
  }
})

async function handleOk() {
  await formRef.value?.validate()
  const payload: AiTool = {...form}
  if (props.mode === 'create' && !String(payload.toolKey || '').trim()) {
    delete payload.toolKey
  }
  emit('submit', payload)
}

</script>

<style scoped>

.icon-box {
  width: 42px;
  height: 42px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: white;
  transition: 0.3s;
}

.icon-box.method {
  background: linear-gradient(135deg, #1890ff, #36cfc9);
}

.icon-box.html {
  background: linear-gradient(135deg, #fa8c16, #ffd666);
}


.professional-form {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.form-body-container {
  flex: 1;
  overflow: hidden;
  padding: 0;
}

.form-layout {
  display: flex;
  height: 100%;
  gap: 0;
}


.form-left {
  width: 45%;
  padding: 24px 32px;
  overflow-y: auto;
  border-right: 1px solid var(--border-default);
  background: var(--bg-surface);
}


.form-right {
  width: 55%;
  padding: 24px 32px;
  overflow-y: auto;
  background: var(--bg-card);
}


.section-card {
  height: 100%;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-primary);
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-default);
}


.form-fields {
  display: flex;
  flex-direction: column;
  gap: 8px;
}


.impl-hint {
  background: color-mix(in srgb, var(--primary) 5%, var(--bg-surface));
  border: 1px solid color-mix(in srgb, var(--primary) 25%, var(--border-default));
  padding: 16px 20px;
  border-radius: 12px;
  margin-bottom: 20px;
}

.hint-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--primary);
  margin-bottom: 4px;
}

.impl-hint p {
  font-size: 13px;
  color: var(--primary);
  margin: 0;
  opacity: 0.8;
}


.mt-16 {
  margin-top: 16px;
}

.custom-alert {
  margin-bottom: 16px;
}

:deep(.ant-form-item-label) {
  font-weight: 500;
}
</style>
