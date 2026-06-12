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
      <div :class="form.type?.toLowerCase()" class="icon-box">
        <ApiOutlined v-if="form.type === 'SSE'"/>
        <ConsoleSqlOutlined v-else-if="form.type === 'STDIO'"/>
        <RocketOutlined v-else/>
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
          <!-- 左侧：基础信息 -->
          <div class="form-left">
            <div class="section-card">
              <h3 class="section-title">
                <IdcardOutlined/>
                {{ t.form.sectionTitle.basic }}
              </h3>

              <a-alert
                  v-if="mode === 'create'"
                  class="custom-alert"
                  :message="t.form.alert"
                  show-icon
                  type="info"
              />

              <div class="form-fields">
                <a-row :gutter="16">
                  <a-col :span="16">
                    <a-form-item :label="t.form.serverName.label" name="serverName">
                      <a-input v-model:value="form.serverName" :placeholder="t.form.serverName.placeholder" size="large"/>
                    </a-form-item>
                  </a-col>
                  <a-col :span="8">
                    <a-form-item :label="t.form.status.label" name="enabled">
                      <AstStatusToggle
                          v-model="form.enabled"
                          :enabled-value="1"
                          :disabled-value="0"
                          :enabled-label="t.form.status.enabled"
                          :disabled-label="t.form.status.disabled"
                      />
                    </a-form-item>
                  </a-col>
                </a-row>

                <a-form-item :label="t.form.mcpKey.label" name="mcpKey">
                  <AstKeyGenerator
                      v-model="form.mcpKey"
                      :disabled="mode === 'edit'"
                      :prefix="AI_MCP_KEY_PREFIX"
                      :placeholder="t.form.mcpKey.placeholder"
                      size="large"
                  />
                </a-form-item>

                <a-form-item :label="t.form.type.label" name="type">
                  <a-segmented v-model:value="form.type" :options="mcpTypeOptions" block size="large"/>
                </a-form-item>

                <a-form-item :label="t.form.description.label" name="description">
                  <a-textarea
                      v-model:value="form.description"
                      :auto-size="{ minRows: 4, maxRows: 6 }"
                      :placeholder="t.form.description.placeholder"
                  />
                </a-form-item>
              </div>
            </div>
          </div>

          <!-- 右侧：协议配置 -->
          <div class="form-right">
            <div class="section-card">
              <h3 class="section-title">
                <LinkOutlined v-if="form.type === 'SSE'"/>
                <CodeOutlined v-else-if="form.type === 'STDIO'"/>
                <RocketOutlined v-else/>
                {{ t.form.sectionTitle.protocol.replace('{type}', form.type) }}
              </h3>

              <!-- SSE 协议配置 -->
              <div v-if="form.type === 'SSE'" class="protocol-box">
                <div class="form-fields">
                  <a-form-item :label="t.form.sse.addressLabel" name="sseAddress">
                    <a-input v-model:value="form.sseAddress" :placeholder="t.form.sse.addressPlaceholder"
                             size="large">
                      <template #prefix>
                        <GlobalOutlined style="color: #bfbfbf"/>
                      </template>
                    </a-input>
                  </a-form-item>

                  <a-form-item :label="t.form.sse.headersLabel" name="requestHeaderConfig">
                    <div class="json-editor-wrapper">
                      <a-textarea
                          v-model:value="form.requestHeaderConfig"
                          :auto-size="{ minRows: 6, maxRows: 10 }"
                          class="mono-text"
                          placeholder='{"Authorization": "Bearer your_token"}'
                      />
                    </div>
                  </a-form-item>
                </div>
              </div>

              <!-- STDIO 协议配置 -->
              <div v-else-if="form.type === 'STDIO'" class="protocol-box">
                <div class="form-fields">
                  <a-form-item :label="t.form.stdio.commandLabel" name="command">
                    <a-input v-model:value="form.command" :placeholder="t.form.stdio.commandPlaceholder" size="large">
                      <template #prefix>
                        <RightSquareOutlined style="color: #bfbfbf"/>
                      </template>
                    </a-input>
                  </a-form-item>

                  <a-form-item :label="t.form.stdio.argsLabel" name="args">
                    <a-textarea
                        v-model:value="form.args"
                        :auto-size="{ minRows: 3, maxRows: 4 }"
                        class="mono-text"
                        :placeholder="t.form.stdio.argsPlaceholder"
                    />
                  </a-form-item>

                  <a-form-item :label="t.form.stdio.envVarsLabel" name="envVars">
                    <div class="json-editor-wrapper">
                      <a-textarea
                          v-model:value="form.envVars"
                          :auto-size="{ minRows: 6, maxRows: 10 }"
                          class="mono-text"
                          placeholder='{"API_KEY": "sk-xxx"}'
                      />
                    </div>
                  </a-form-item>
                </div>
              </div>

              <!-- STEAMABLE 协议配置 -->
              <div v-else class="protocol-box">
                <div class="form-fields">
                  <a-form-item :label="t.form.steamable.configLabel" name="steamableConfig">
                    <div class="json-editor-wrapper">
                      <a-textarea
                          v-model:value="form.steamableConfig"
                          :auto-size="{ minRows: 8, maxRows: 12 }"
                          class="mono-text"
                          placeholder='{"endpoint": "wss://example.com/stream", "auth": {"type": "bearer", "token": "xxx"}}'
                      />
                    </div>
                  </a-form-item>
                </div>
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
  ApiOutlined,
  CodeOutlined,
  ConsoleSqlOutlined,
  GlobalOutlined,
  IdcardOutlined,
  LinkOutlined,
  RightSquareOutlined,
  RocketOutlined
} from '@ant-design/icons-vue'
import {usePageTranslation} from '@/locales/pages.ts'
import type {FormInstance} from 'ant-design-vue'
import type {AiMcp} from '@/api/aiMcp.ts'
import AstModal from '@/components/home/AstModal.vue'
import AstStatusToggle from '@/components/home/AstStatusToggle.vue'
import AstKeyGenerator from '@/components/home/AstKeyGenerator.vue'
import {AI_MCP_KEY_PREFIX} from '@/constants/aiConfigKeyPrefixes.ts'

const props = defineProps<{ mode: 'create' | 'edit', confirmLoading: boolean, initial: AiMcp | null }>()
const emit = defineEmits<{ submit: [payload: AiMcp] }>()
const open = defineModel<boolean>('open', {required: true})

const t = usePageTranslation('ai-mcp')

const formRef = ref<FormInstance | null>(null)

const mcpTypeOptions = computed(() => [
  {label: t.value.form.type.sse, value: 'SSE'},
  {label: t.value.form.type.stdio, value: 'STDIO'},
  {label: t.value.form.type.steamable, value: 'STEAMABLE'}
])

function emptyForm(): AiMcp {
  return {
    mcpKey: '', serverName: '', description: '', type: 'SSE',
    sseAddress: '', requestHeaderConfig: '', enabled: 1,
    command: '', args: '', envVars: '', steamableConfig: ''
  }
}

const form = reactive<AiMcp>(emptyForm())

const rules = computed(() => ({
  serverName: [{required: true, message: t.value.form.validation.serverNameRequired}],
  type: [{required: true, message: t.value.form.validation.typeRequired}],
  sseAddress: [{required: true, message: t.value.form.validation.sseAddressRequired, trigger: 'blur'}]
}))

const onOpenChange = (val: boolean) => {
  open.value = val
}

function assignFromInitial(src: AiMcp) {
  Object.assign(form, emptyForm(), src)
  if (form.enabled !== 0 && form.enabled !== 1) {
    form.enabled = form.enabled == null ? 1 : Number(form.enabled) === 0 ? 0 : 1
  }
}

watch(() => [open.value, props.initial] as const, ([isOpen, initial]) => {
  if (isOpen) {
    if (initial && Object.keys(initial).length > 0) assignFromInitial(initial)
    else Object.assign(form, emptyForm())
  }
})

async function handleOk() {
  await formRef.value?.validate()
  const payload: AiMcp = {...form}
  if (props.mode === 'create' && !String(payload.mcpKey || '').trim()) {
    delete payload.mcpKey
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

.icon-box.sse {
  background: linear-gradient(135deg, #1890ff, #36cfc9);
}

.icon-box.stdio {
  background: linear-gradient(135deg, #52c41a, #95de64);
}

.icon-box.steamable {
  background: linear-gradient(135deg, #722ed1, #b37feb);
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
  background: #fff;
}


.form-right {
  width: 55%;
  padding: 24px 32px;
  overflow-y: auto;
  background: #fff;
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
  color: var(--text-heading);
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-default);
}


.form-fields {
  display: flex;
  flex-direction: column;
  gap: 8px;
}


.protocol-box {
  animation: fadeIn 0.3s ease;
}

.json-editor-wrapper {
  border: 1px solid var(--border-default);
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  transition: 0.3s;
}

.json-editor-wrapper:focus-within {
  border-color: var(--primary);
  box-shadow: 0 0 0 2px color-mix(in srgb, var(--primary) 10%, transparent);
}

.mono-text {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 13px;
  background: transparent;
  border: none;
  padding: 12px;
}

.mono-text:focus {
  box-shadow: none;
}


.custom-alert {
  margin-bottom: 16px;
}

:deep(.ant-form-item-label) {
  font-weight: 500;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
