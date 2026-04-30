<template>
  <AstrsomnModal
    :open="open"
    width="80vw"
    max-width="80vw"
    body-height="90vh"
    :closable="true"
    main-padding="0"
    wrap-class-name="model-form-fsm-wrap"
    @update:open="emit('update:open', $event)"
    @cancel="onCancel"
  >
    <template #header-logo>
      <span class="header-type-icon" :class="form.modelType">
        <template v-if="form.modelType === 'chat'"><MessageOutlined /></template>
        <template v-else-if="form.modelType === 'embedding'"><PartitionOutlined /></template>
        <template v-else><PictureOutlined /></template>
      </span>
    </template>
    <template #header-title>
      {{ mode === 'create' ? '注册接入端点' : mode === 'view' ? '查看端点配置' : '编辑端点配置' }}
    </template>
    <template #header-subtitle>
      左侧填写基础信息，右侧配置能力与推理参数
    </template>

    <div class="model-form-shell">
    <a-form
      ref="formRef"
      :model="form"
      :rules="rules"
      layout="vertical"
      class="model-form-root"
    >
      <div class="model-form-split">
        <div class="model-form-pane model-form-pane--left">
          <div class="form-section">
            <h3 class="section-headline"><IdcardOutlined /> 基础信息</h3>
            <div class="form-grid">
              <a-form-item label="模型来源" name="sourceType">
                <a-select v-model:value="form.sourceType" size="large" :disabled="props.mode === 'view'">
                  <a-select-option value="user_custom">用户自定义模型</a-select-option>
                  <a-select-option value="plugin">插件模型</a-select-option>
                </a-select>
              </a-form-item>

              <a-form-item label="Provider" name="provider">
                <ExtensionSelector
                  v-model:value="form.extensionCode"
                  placeholder="请选择端点所属服务商"
                  size="large"
                  :disabled="props.mode === 'view' || isPluginModel"
                />
              </a-form-item>

              <a-form-item label="启用状态" name="status">
                <a-segmented
                  v-model:value="form.status"
                  :options="[{label:'已启用', value:'enabled'}, {label:'已禁用', value:'disabled'}]"
                  block
                  size="large"
                  :disabled="props.mode === 'view'"
                  class="status-segmented"
                />
              </a-form-item>

              <a-form-item label="模型类型" name="modelType">
                <a-segmented v-model:value="form.modelType" :options="[{label:'对话端点', value:'chat'}, {label:'向量端点', value:'embedding'}, {label:'图像端点', value:'image'}]" block size="large" :disabled="props.mode === 'view' || isPluginModel" />
              </a-form-item>

              <a-form-item label="模型名称" name="modelName">
                <a-input v-model:value="form.modelName" placeholder="例如：OpenAI 官方端点 或 私有部署 Llama3" size="large" :disabled="props.mode === 'view' || isPluginModel" />
              </a-form-item>

              <a-form-item label="模型 Key" name="modelKey">
                <a-tooltip
                  v-if="modelKeyImmutable || props.mode === 'view' || isPluginModel"
                  :title="isPluginModel ? '插件模型不可修改' : (modelKeyImmutable ? '已有推理实例在同环境下引用该端点 Key，不可修改' : '查看模式下不可修改')"
                >
                  <a-input v-model:value="form.modelKey" placeholder="建议留空，系统将自动生成唯一索引" size="large" disabled />
                </a-tooltip>
                <AstrsomnKeyGenerator
                  v-else
                  v-model="form.modelKey"
                  :prefix="AI_MODEL_KEY_PREFIX"
                  placeholder="建议留空，系统将自动生成唯一索引"
                  size="large"
                />
              </a-form-item>

              <a-form-item label="关联账号" name="accountKey">
                <a-space class="w-full">
                  <a-input
                    v-model:value="form.accountKey"
                    :placeholder="form.accountKey ? form.accountKey : '请选择关联账号'"
                    size="large"
                    :disabled="true"
                    class="cursor-pointer flex-1"
                    @click="accountSelectorOpen = true"
                  />
                  <a-button type="primary" size="large" @click="accountSelectorOpen = true">
                    选择账号
                  </a-button>
                </a-space>
              </a-form-item>

              <a-form-item label="API URL" name="apiUrl" class="span-2">
                <a-input v-model:value="form.apiUrl" placeholder="例如：https://api.openai.com/v1" size="large" :disabled="props.mode === 'view'">
                  <template #prefix><GlobalOutlined style="color: #bfbfbf" /></template>
                </a-input>
              </a-form-item>
            </div>
          </div>
        </div>

        <div class="model-form-divider" aria-hidden="true" />

        <div class="model-form-pane model-form-pane--right">
          <div class="form-section">
            <h3 class="section-headline"><ThunderboltOutlined /> 能力配置 (Capabilities)</h3>

            <div v-if="form.modelType === 'chat'" class="capability-panel-section">
              <div class="cap-tag-grid">
                <div
                  v-for="opt in chatCapabilitiesOptions"
                  :key="opt.value"
                  :class="['custom-cap-tag', { active: chatCapabilities.includes(opt.value) }]"
                  @click="props.mode !== 'view' && !isPluginModel && toggleChatCapability(opt.value)"
                  :style="{ cursor: (props.mode === 'view' || isPluginModel) ? 'default' : 'pointer' }"
                >
                  <div class="custom-cap-tag__body">
                    <CheckCircleFilled v-if="chatCapabilities.includes(opt.value)" class="custom-cap-tag__check" />
                    <div class="custom-cap-tag__text">
                      <span class="custom-cap-tag__title">{{ opt.titleZh }}</span>
                      <span class="custom-cap-tag__field">{{ opt.fieldCode }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="form.modelType === 'embedding'" class="capability-panel-section">
              <div class="cap-tag-grid">
                <div
                  v-for="opt in embeddingCapabilitiesOptions"
                  :key="opt.value"
                  :class="['custom-cap-tag', { active: embeddingCapabilities.includes(opt.value) }]"
                  @click="props.mode !== 'view' && !isPluginModel && toggleEmbeddingCapability(opt.value)"
                  :style="{ cursor: (props.mode === 'view' || isPluginModel) ? 'default' : 'pointer' }"
                >
                  <div class="custom-cap-tag__body">
                    <CheckCircleFilled v-if="embeddingCapabilities.includes(opt.value)" class="custom-cap-tag__check" />
                    <div class="custom-cap-tag__text">
                      <span class="custom-cap-tag__title">{{ opt.titleZh }}</span>
                      <span class="custom-cap-tag__field">{{ opt.fieldCode }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="form.modelType === 'image'" class="capability-panel-section">
              <div class="cap-tag-grid">
                <div
                  v-for="opt in imageCapabilitiesOptions"
                  :key="opt.value"
                  :class="['custom-cap-tag', { active: imageCapabilities.includes(opt.value) }]"
                  @click="props.mode !== 'view' && !isPluginModel && toggleImageCapability(opt.value)"
                  :style="{ cursor: (props.mode === 'view' || isPluginModel) ? 'default' : 'pointer' }"
                >
                  <div class="custom-cap-tag__body">
                    <CheckCircleFilled v-if="imageCapabilities.includes(opt.value)" class="custom-cap-tag__check" />
                    <div class="custom-cap-tag__text">
                      <span class="custom-cap-tag__title">{{ opt.titleZh }}</span>
                      <span class="custom-cap-tag__field">{{ opt.fieldCode }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="param-schema-section">
              <h3 class="section-headline"><SettingOutlined /> 推理参数配置 (Parameter Schema)</h3>
              <p class="section-desc">控制实例层可填写的参数范围。未启用的参数在实例层将被禁用或忽略。</p>
              
              <div class="param-table">
                <div class="param-table-header">
                  <div class="param-col param-col--id">参数名</div>
                  <div class="param-col param-col--desc">说明与映射</div>
                 
               
                  <div class="param-col param-col--toggle">启用</div>
                </div>
                <div class="param-table-body">
                  <div v-for="param in currentParams" :key="param.id" class="param-row">
                    <div class="param-col param-col--id">
                      <span class="param-id">{{ param.id }}</span>
                    </div>
                    <div class="param-col param-col--desc">
                      <span class="param-desc">{{ param.desc }}</span>
                      <span class="param-mapping">{{ param.mapping }}</span>
                    </div>
                    <div class="param-col param-col--toggle">
                      <a-switch 
                        v-model:checked="param.active" 
                        :disabled="props.mode === 'view' || isPluginModel"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="runtime-params-box">
              <div class="box-title"><ControlOutlined /> 资源限制</div>
              <div class="param-grid">
                <div class="param-item">
                  <span class="pl">单次响应上限 (Token)</span>
                  <a-input-number v-model:value="form.responseLimit" :min="0" placeholder="默认 4096" block :disabled="props.mode === 'view' || isPluginModel" />
                </div>
                <div class="param-item">
                  <span class="pl">累计配额上限 (Token)</span>
                  <a-input-number v-model:value="form.maxQuotaTokens" :min="0" placeholder="0 表示无限制" block :disabled="props.mode === 'view' || isPluginModel" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-form>

    <div class="modal-footer-action" v-if="props.mode !== 'view'">
      <div class="footer-left">
        <LockOutlined /> 端点变更将影响下游所有推理实例
      </div>
      <div class="footer-right">
        <a-button type="primary" class="btn-submit" :loading="confirmLoading" @click="handleSubmit">
          确认并保存端点
        </a-button>
      </div>
    </div>
    <div class="modal-footer-action" v-else>
      <div class="footer-right">
        <a-button type="primary" @click="emit('update:open', false)">关闭</a-button>
      </div>
    </div>
    </div>

    <AccountSelectorTable
      v-model:open="accountSelectorOpen"
      @select="handleAccountSelect"
    />
  </AstrsomnModal>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import {
  IdcardOutlined, MessageOutlined,
  PartitionOutlined, LockOutlined, ThunderboltOutlined,
  CheckCircleFilled, ControlOutlined, PictureOutlined, GlobalOutlined,
  SettingOutlined
} from '@ant-design/icons-vue'
import AstrsomnModal from '@/components/home/AstrsomnModal.vue'
import AstrsomnKeyGenerator from '@/components/home/AstrsomnKeyGenerator.vue'
import ExtensionSelector from '../../system-config/system-extension/selectors/ExtensionSelector.vue'
import type { FormInstance } from 'ant-design-vue'
import type { AiModel } from '@/api/aiModel'
import AccountSelectorTable from '../ai-account/selector/AccountSelectorTable.vue'
import type { AiAccount } from '@/api/aiAccount'
import { AI_MODEL_KEY_PREFIX } from '@/constants/aiConfigKeyPrefixes'
import { aiModelCapabilitiesDictionary, aiModelSourceTypeDictionary } from '@/locales/zh-CN/dictionary/ai-config/ai-model.ts'
import {
  CHAT_CAPABILITIES_CODES,
  CHAT_CAPABILITIES_SET,
  CHAT_PARAM_CODES,
  EMBEDDING_CAPABILITIES_CODES,
  EMBEDDING_CAPABILITIES_SET,
  EMBEDDING_PARAM_CODES,
  IMAGE_CAPABILITIES_CODES,
  IMAGE_CAPABILITIES_SET,
  IMAGE_PARAM_CODES,
  MODEL_CONFIG_MAP
} from '@/constants/aiModelEnums'

const props = withDefaults(
    defineProps<{
      open: boolean
      mode: 'create' | 'edit' | 'view'
      confirmLoading?: boolean
      initialData?: AiModel | null
      statusOptions: { label: string; value: string }[]
      submitHandler: (payload: AiModel) => Promise<void>
    }>(),
    { confirmLoading: false, initialData: null }
)

const emit = defineEmits(['update:open'])

const formRef = ref<FormInstance | null>(null)

const accountSelectorOpen = ref(false)
const modelKeyImmutable = ref(false)

const PARAM_TEMPLATES = {
  chat: [
    { id: 'temperature', desc: '采样温度', mapping: 'temperature', default: '0.7', range: '0-2.0', active: true },
    { id: 'top_p', desc: '核采样阈值', mapping: 'top_p', default: '1.0', range: '0-1.0', active: true },
    { id: 'top_k', desc: 'Top-K 采样', mapping: 'top_k', default: '', range: 'int', active: false },
    { id: 'max_tokens', desc: '最大生成长度', mapping: 'max_tokens', default: '2048', range: '1-32k', active: true },
    { id: 'presence_penalty', desc: '话题存在惩罚', mapping: 'presence_penalty', default: '0', range: '-2-2', active: false },
    { id: 'frequency_penalty', desc: '频率重复惩罚', mapping: 'frequency_penalty', default: '0', range: '-2-2', active: false },
    { id: 'stop_sequences', desc: '停止符', mapping: 'stop_sequences', default: '', range: 'Array', active: false },
    { id: 'seed', desc: '随机种子', mapping: 'seed', default: '', range: 'int', active: false },
    { id: 'logit_bias', desc: 'Token 偏好偏差', mapping: 'logit_bias', default: '', range: 'Object', active: false }
  ],
  embedding: [
    { id: 'dimensions', desc: '向量输出维度', mapping: 'dimensions', default: '1536', range: 'int', active: true },
    { id: 'model_name', desc: '模型名称', mapping: 'model_name', default: '', range: 'string', active: false },
    { id: 'user', desc: '终端用户标识', mapping: 'user', default: '', range: 'string', active: false }
  ],
  image: [
    { id: 'size', desc: '图片尺寸', mapping: 'size', default: '1024x1024', range: 'string', active: true },
    { id: 'quality', desc: '生成质量', mapping: 'quality', default: 'standard', range: 'standard/hd', active: true },
    { id: 'style', desc: '画面风格', mapping: 'style', default: 'vivid', range: 'vivid/natural', active: true },
    { id: 'response_format', desc: '响应格式', mapping: 'response_format', default: 'url', range: 'url/b64_json', active: false }
  ]
}

function handleAccountSelect(account: AiAccount) {
  if (account.accountKey) {
    form.accountKey = account.accountKey
  }
  if (account.apiUrl?.trim()) {
    form.apiUrl = account.apiUrl
  }
  accountSelectorOpen.value = false
}

function capOptionRow(code: string) {
  const titleZh = aiModelCapabilitiesDictionary.getLabel(code) ?? code
  return { value: code, titleZh, fieldCode: code }
}

const chatCapabilitiesOptions = computed(() => CHAT_CAPABILITIES_CODES.map((value) => capOptionRow(value)))
const embeddingCapabilitiesOptions = computed(() => EMBEDDING_CAPABILITIES_CODES.map((value) => capOptionRow(value)))
const imageCapabilitiesOptions = computed(() => IMAGE_CAPABILITIES_CODES.map((value) => capOptionRow(value)))

const chatCapabilities = ref<string[]>([])
const chatOrphanCapabilities = ref<string[]>([])

const embeddingCapabilities = ref<string[]>([])
const embeddingOrphanCapabilities = ref<string[]>([])

const imageCapabilities = ref<string[]>([])
const imageOrphanCapabilities = ref<string[]>([])

const currentParams = ref<any[]>(JSON.parse(JSON.stringify(PARAM_TEMPLATES.chat)))

const isPluginModel = computed(() => form.sourceType === 'plugin')

const form = reactive<AiModel>({
  modelName: '', modelKey: '', modelType: 'chat', provider: '',
  accountKey: '', apiUrl: '', status: 'enabled', isDefault: 0, responseLimit: 4096,
  capabilities: '', param: '', randomIndex: 0, topVariance: 0, maxQuotaTokens: 0, sourceType: 'user_custom'
})

const rules = {
  modelName: [{ required: true, message: '请输入模型名称' }],
  extensionCode: [{ required: true, message: '请选择供应商' }],
  apiUrl: [],
}

function toggleInList(list: string[], val: string) {
  const i = list.indexOf(val)
  if (i > -1) list.splice(i, 1)
  else list.push(val)
}

const toggleChatCapability = (val: string) => toggleInList(chatCapabilities.value, val)
const removeChatOrphan = (val: string) => {
  chatOrphanCapabilities.value = chatOrphanCapabilities.value.filter((c) => c !== val)
}

const toggleEmbeddingCapability = (val: string) => toggleInList(embeddingCapabilities.value, val)
const removeEmbeddingOrphan = (val: string) => {
  embeddingOrphanCapabilities.value = embeddingOrphanCapabilities.value.filter((c) => c !== val)
}

const toggleImageCapability = (val: string) => toggleInList(imageCapabilities.value, val)
const removeImageOrphan = (val: string) => {
  imageOrphanCapabilities.value = imageOrphanCapabilities.value.filter((c) => c !== val)
}

function partitionConfig(caps: string[], params: any[], modelType: string) {
  chatCapabilities.value = []
  chatOrphanCapabilities.value = []
  embeddingCapabilities.value = []
  embeddingOrphanCapabilities.value = []
  imageCapabilities.value = []
  imageOrphanCapabilities.value = []

  if (modelType === 'chat') {
    chatCapabilities.value = caps.filter((c) => CHAT_CAPABILITIES_SET.has(c))
    chatOrphanCapabilities.value = caps.filter((c) => !CHAT_CAPABILITIES_SET.has(c))
    if (params && params.length > 0) {
      currentParams.value = params
    } else {
      currentParams.value = JSON.parse(JSON.stringify(PARAM_TEMPLATES.chat))
    }
  } else if (modelType === 'embedding') {
    embeddingCapabilities.value = caps.filter((c) => EMBEDDING_CAPABILITIES_SET.has(c))
    embeddingOrphanCapabilities.value = caps.filter((c) => !EMBEDDING_CAPABILITIES_SET.has(c))
    if (params && params.length > 0) {
      currentParams.value = params
    } else {
      currentParams.value = JSON.parse(JSON.stringify(PARAM_TEMPLATES.embedding))
    }
  } else if (modelType === 'image') {
    imageCapabilities.value = caps.filter((c) => IMAGE_CAPABILITIES_SET.has(c))
    imageOrphanCapabilities.value = caps.filter((c) => !IMAGE_CAPABILITIES_SET.has(c))
    if (params && params.length > 0) {
      currentParams.value = params
    } else {
      currentParams.value = JSON.parse(JSON.stringify(PARAM_TEMPLATES.image))
    }
  }
}

const syncForm = () => {
  if (props.mode === 'create' || !props.initialData) {
    modelKeyImmutable.value = false
    Object.assign(form, {
      id: '',
      modelName: '',
      modelKey: '',
      modelType: 'chat',
      provider: '',
      accountKey: '',
      apiUrl: '',
      status: 'enabled',
      isDefault: 0,
      capabilities: '',
      param: '',
      randomIndex: 0,
      topVariance: 0,
      maxQuotaTokens: 0,
      sourceType: 'user_custom'
    })
    partitionConfig([], [], String(form.modelType ?? 'chat'))
  } else {
    Object.assign(form, props.initialData)
    modelKeyImmutable.value = props.initialData.modelKeyImmutable === true
    delete (form as Record<string, unknown>).modelKeyImmutable
    
    if (!form.sourceType) {
      form.sourceType = 'user_custom'
    }
    
    let capabilitiesArray: string[] = []
    let paramsArray: any[] = []
    
    try {
      const parsedCaps = JSON.parse(form.capabilities || '[]')
      capabilitiesArray = Array.isArray(parsedCaps) ? parsedCaps.map(String) : []
    } catch {
      capabilitiesArray = []
    }
    
    try {
      const parsedParams = JSON.parse(form.param || '[]')
      paramsArray = Array.isArray(parsedParams) ? parsedParams : []
    } catch {
      paramsArray = []
    }
    
    partitionConfig(capabilitiesArray, paramsArray, String(form.modelType || 'chat'))
  }
}

watch(
  () => props.open,
  (v) => {
    if (!v) return
    syncForm()
  }
)

watch(
  () => props.mode,
  () => {
    if (props.open) {
      syncForm()
    }
  }
)

watch(() => form.modelType, () => {
  partitionConfig([], [], String(form.modelType || 'chat'))
})

const handleSubmit = async () => {
  await formRef.value?.validate()
  const payload = { ...form }
  
  let allCapabilities: string[] = []
  if (form.modelType === 'chat') {
    allCapabilities = [...chatCapabilities.value, ...chatOrphanCapabilities.value]
  } else if (form.modelType === 'embedding') {
    allCapabilities = [...embeddingCapabilities.value, ...embeddingOrphanCapabilities.value]
  } else if (form.modelType === 'image') {
    allCapabilities = [...imageCapabilities.value, ...imageOrphanCapabilities.value]
  }
  
  payload.capabilities = allCapabilities.length > 0 ? JSON.stringify(allCapabilities) : ''
  payload.param = currentParams.value.length > 0 ? JSON.stringify(currentParams.value) : ''
  delete (payload as { modelKeyImmutable?: unknown }).modelKeyImmutable
  await props.submitHandler(payload)
}

const onCancel = () => emit('update:open', false)
</script>

<style scoped>
:global(.model-form-fsm-wrap.ant-modal-wrap) {
  display: flex;
  align-items: center;
  justify-content: center;
}

:global(.model-form-fsm-wrap .ant-modal) {
  top: 0;
  padding-bottom: 0;
}

.header-type-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: #fff;
}

.header-type-icon.chat {
  color: #bae6fd;
}

.header-type-icon.embedding {
  color: #e9d5ff;
}

.header-type-icon.image {
  color: #fef08a;
}

.model-form-shell {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  overflow: hidden;
}

.model-form-root {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.model-form-split {
  display: flex;
  flex: 1;
  min-height: 0;
  gap: 0;
  align-items: stretch;
}

.model-form-pane {
  flex: 1;
  min-width: 0;
  min-height: 0;
  overflow-y: auto;
  padding: 20px 24px;
  box-sizing: border-box;
}

.model-form-pane--left {
  background: var(--bg-card, #fff);
}

.model-form-pane--right {
  background: var(--bg-surface, #f8fafc);
}

.model-form-divider {
  width: 1px;
  flex-shrink: 0;
  background: var(--border-default, #e2e8f0);
  align-self: stretch;
}

.model-form-pane::-webkit-scrollbar {
  width: 6px;
}

.model-form-pane::-webkit-scrollbar-thumb {
  background: var(--border-default, #e2e8f0);
  border-radius: 4px;
}

.section-headline {
  font-size: 14px;
  font-weight: 600;
  margin: 0 0 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-heading, #444);
}

.section-desc {
  font-size: 12px;
  color: var(--text-secondary, #64748b);
  margin: -8px 0 16px 0;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 24px;
}

.span-2 {
  grid-column: span 2;
}

:deep(.ant-segmented-item-selected) {
  background-color: var(--primary, #1890ff) !important;
  color: white !important;
}

:deep(.ant-segmented-item-selected:hover) {
  background-color: var(--primary, #40a9ff) !important;
  color: white !important;
}

.status-segmented :deep(.ant-segmented-item:nth-child(1).ant-segmented-item-selected) {
  background-color: #52c41a !important;
}

.status-segmented :deep(.ant-segmented-item:nth-child(1).ant-segmented-item-selected:hover) {
  background-color: #73d13d !important;
}

.status-segmented :deep(.ant-segmented-item:nth-child(2).ant-segmented-item-selected) {
  background-color: #ff4d4f !important;
}

.status-segmented :deep(.ant-segmented-item:nth-child(2).ant-segmented-item-selected:hover) {
  background-color: #ff7875 !important;
}

.capability-panel-section {
  margin-bottom: 20px;
}

.cap-tag-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.custom-cap-tag {
  min-height: 72px;
  padding: 12px 14px;
  background: var(--bg-card, #fff);
  border: 1px solid var(--border-default, #e2e8f0);
  border-radius: 10px;
  cursor: pointer;
  transition:
    border-color 0.2s,
    background 0.2s,
    box-shadow 0.2s;
  box-sizing: border-box;
}

.custom-cap-tag__body {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  min-height: 48px;
}

.custom-cap-tag__check {
  flex-shrink: 0;
  margin-top: 2px;
  font-size: 16px;
  color: var(--primary, #0061ff);
}

.custom-cap-tag__text {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;
  min-width: 0;
  flex: 1;
}

.custom-cap-tag__title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-heading, #334155);
  line-height: 1.4;
  word-break: break-word;
}

.custom-cap-tag__field {
  font-size: 11px;
  font-weight: 500;
  color: var(--text-muted, #94a3b8);
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  line-height: 1.35;
  word-break: break-all;
}

.custom-cap-tag:hover {
  border-color: var(--primary, #0061ff);
  box-shadow: 0 1px 4px rgba(0, 97, 255, 0.12);
}

.custom-cap-tag:hover .custom-cap-tag__title {
  color: var(--primary, #0061ff);
}

.custom-cap-tag.active {
  background: #eff6ff;
  border-color: var(--primary, #0061ff);
  box-shadow: 0 1px 4px rgba(0, 97, 255, 0.18);
}

.custom-cap-tag.active .custom-cap-tag__title {
  color: #1d4ed8;
}

.custom-cap-tag.active .custom-cap-tag__field {
  color: var(--text-secondary, #64748b);
}

.param-schema-section {
  margin-top: 24px;
}

.param-table {
  background: var(--bg-card, #fff);
  border: 1px solid var(--border-default, #e2e8f0);
  border-radius: 8px;
  overflow: hidden;
}

.param-table-header {
  display: grid;
  grid-template-columns: 120px 1fr 100px 100px 60px;
  gap: 12px;
  padding: 12px 16px;
  background: var(--bg-surface, #f8fafc);
  border-bottom: 1px solid var(--border-default, #e2e8f0);
  font-size: 11px;
  font-weight: 600;
  color: var(--text-muted, #64748b);
  text-transform: uppercase;
}

.param-table-body {
  max-height: 320px;
  overflow-y: auto;
}

.param-row {
  display: grid;
  grid-template-columns: 120px 1fr 100px 100px 60px;
  gap: 12px;
  padding: 10px 16px;
  border-bottom: 1px solid var(--border-default, #f1f5f9);
  align-items: center;
}

.param-row:hover {
  background: var(--bg-surface, #f8fafc);
}

.param-col {
  display: flex;
  align-items: center;
}

.param-col--id {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 12px;
  font-weight: 600;
  color: var(--text-heading, #334155);
}

.param-id {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 12px;
  font-weight: 600;
  color: var(--text-heading, #334155);
}

.param-col--desc {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: flex-start;
}

.param-desc {
  font-size: 12px;
  color: var(--text-secondary, #64748b);
}

.param-mapping {
  font-size: 10px;
  color: var(--text-muted, #94a3b8);
  background: var(--bg-surface, #f1f5f9);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

.param-col--range {
  justify-content: center;
}

.param-range {
  font-size: 10px;
  color: var(--text-muted, #94a3b8);
  text-align: center;
}

.param-col--toggle {
  justify-content: center;
}

.runtime-params-box {
  margin-top: 24px;
  background: var(--bg-card, #fff);
  border: 1px solid var(--border-default, #e2e8f0);
  border-radius: 16px;
  padding: 16px;
}

.box-title {
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--text-heading, #111);
  display: flex;
  align-items: center;
  gap: 6px;
}

.param-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.param-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.pl {
  font-size: 11px;
  color: var(--text-muted, #999);
}

.modal-footer-action {
  flex-shrink: 0;
  padding: 14px 24px;
  background: var(--bg-card, #fff);
  border-top: 1px solid var(--border-default, #e2e8f0);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-left {
  font-size: 12px;
  color: #52c41a;
  display: flex;
  align-items: center;
  gap: 4px;
}

.btn-submit {
  border-radius: 8px;
  font-weight: 600;
  padding: 0 24px;
  height: 38px;
}

</style>
