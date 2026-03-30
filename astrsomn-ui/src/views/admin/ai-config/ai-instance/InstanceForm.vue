<template>
  <a-modal
      :open="visible"
      :title="isEdit ? '编辑推理预设' : '新建推理预设'"
      :width="820"
      :confirm-loading="submitting"
      @ok="onSubmit"
      @cancel="handleCancel"
  >
    <a-spin :spinning="loading">
      <a-form ref="formRef" layout="vertical" :model="form">
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="预设名称" name="instanceName">
              <a-input v-model:value="form.instanceName" placeholder="例如：创造力模式 / 严格助手" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="配置状态" name="status">
              <a-select
                  v-model:value="form.status"
                  :options="statusOptions"
                  placeholder="请选择"
                  allow-clear
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="关联接入端点 (Endpoint)" name="modelKey" :rules="modelKeyFieldRules">
              <a-select
                  v-model:value="form.modelKey"
                  :options="modelSelectOptions"
                  :loading="modelsLoading"
                  :disabled="isEdit"
                  show-search
                  :filter-option="filterModelOption"
                  placeholder="选择此配置绑定的物理端点"
                  allow-clear
                  option-filter-prop="label"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="配置识别码 (Instance Key)" name="instanceKey">
              <a-input
                  v-model:value="form.instanceKey"
                  placeholder="代码引用标识，留空则自动生成"
                  allow-clear
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-alert
            v-if="orphanModelKey"
            type="warning"
            show-icon
            class="mb-12"
            message="未匹配到目标接入端点，当前按「通用对话」模式展示全部参数；请核实端点是否已移除。"
        />
        <a-alert
            v-else-if="!form.modelKey"
            type="info"
            show-icon
            class="mb-12"
            message="请先选择接入端点，系统将根据端点支持的能力集（Capabilities）动态加载可调参数。"
        />

        <template v-if="showChatParamSection">
          <div class="param-section-title">对话模型 · 运行采样参数</div>
          <a-row :gutter="16">
            <a-col v-show="chatFieldVisibilityEffective.maxTokens" :xs="24" :md="8">
              <a-form-item label="单次响应上限 (Max Tokens)" name="maxTokens">
                <a-input-number v-model:value="form.maxTokens" class="w-full" :min="0" placeholder="最大生成长度" />
              </a-form-item>
            </a-col>
            <a-col v-show="chatFieldVisibilityEffective.temperature" :xs="24" :md="8">
              <a-form-item label="采样温度 (Temperature)" name="temperature">
                <a-input-number
                    v-model:value="form.temperature"
                    class="w-full"
                    :min="0"
                    :max="2"
                    :step="0.1"
                    placeholder="0.7"
                />
              </a-form-item>
            </a-col>
            <a-col v-show="chatFieldVisibilityEffective.topP" :xs="24" :md="8">
              <a-form-item label="核采样阈值 (Top P)" name="topP">
                <a-input-number v-model:value="form.topP" class="w-full" :min="0" :max="1" :step="0.05" placeholder="1.0" />
              </a-form-item>
            </a-col>
            <a-col v-show="chatFieldVisibilityEffective.topK" :xs="24" :md="8">
              <a-form-item label="候选集大小 (Top K)" name="topK">
                <a-input-number v-model:value="form.topK" class="w-full" :min="0" placeholder="整数" />
              </a-form-item>
            </a-col>
            <a-col v-show="chatFieldVisibilityEffective.presencePenalty" :xs="24" :md="8">
              <a-form-item label="话题新鲜度 (Presence Penalty)" name="presencePenalty">
                <a-input-number v-model:value="form.presencePenalty" class="w-full" :step="0.1" placeholder="0.0" />
              </a-form-item>
            </a-col>
            <a-col v-show="chatFieldVisibilityEffective.frequencyPenalty" :xs="24" :md="8">
              <a-form-item label="重复惩罚 (Frequency Penalty)" name="frequencyPenalty">
                <a-input-number v-model:value="form.frequencyPenalty" class="w-full" :step="0.1" placeholder="0.0" />
              </a-form-item>
            </a-col>
            <a-col v-show="chatFieldVisibilityEffective.seed" :xs="24" :md="8">
              <a-form-item label="随机种子 (Seed)" name="seed">
                <a-input-number v-model:value="form.seed" class="w-full" placeholder="用于结果复现" />
              </a-form-item>
            </a-col>
            <a-col v-show="chatFieldVisibilityEffective.stopSequences" :span="24">
              <a-form-item label="停止符序列 (Stop Sequences)" name="stopSequences">
                <a-textarea
                    v-model:value="form.stopSequences"
                    :rows="3"
                    placeholder="多段结束符，匹配到这些字符时将停止输出"
                    allow-clear
                />
              </a-form-item>
            </a-col>
          </a-row>
        </template>

        <template v-else-if="resolvedModel && resolvedModel.modelType === 'embedding'">
          <div class="param-section-title">向量模型 · 维度配置</div>
          <a-row :gutter="16">
            <a-col v-show="showEmbeddingDimensions" :xs="24" :md="8">
              <a-form-item label="输出维度 (Dimensions)" name="dimensions">
                <a-input-number v-model:value="form.dimensions" class="w-full" :min="0" />
              </a-form-item>
            </a-col>
          </a-row>
        </template>

        <template v-else-if="resolvedModel && resolvedModel.modelType === 'image'">
          <div class="param-section-title">图像模型 · 渲染参数</div>
          <a-row :gutter="16">
            <a-col v-show="showImageSize" :xs="24" :md="8">
              <a-form-item label="画布尺寸 (Size)" name="size">
                <a-input v-model:value="form.size" allow-clear placeholder="如 1024x1024" />
              </a-form-item>
            </a-col>
            <a-col v-show="showImageStyle" :xs="24" :md="8">
              <a-form-item label="生成风格 (Style)" name="style">
                <a-input v-model:value="form.style" allow-clear placeholder="如 vivid 或 natural" />
              </a-form-item>
            </a-col>
            <a-col v-show="showImageDimensions" :xs="24" :md="8">
              <a-form-item label="渲染维度" name="dimensions">
                <a-input-number v-model:value="form.dimensions" class="w-full" :min="0" />
              </a-form-item>
            </a-col>
          </a-row>
        </template>
      </a-form>
    </a-spin>
  </a-modal>
</template>
<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import type { FormInstance } from 'ant-design-vue'
import { aiInstanceApi, type AiInstance } from '@/api/aiInstance'
import { aiModelApi, type AiModel } from '@/api/aiModel'

interface Props {
  visible: boolean
  record?: AiInstance
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  success: []
}>()

const CHAT_FIELD_CAPS = {
  maxTokens: ['max_tokens', 'max_token_setting'],
  temperature: ['temperature', 'temperature_setting'],
  topP: ['top_p', 'top_p_setting'],
  topK: ['top_k', 'top_k_setting'],
  presencePenalty: ['presence_penalty', 'presence_penalty_setting'],
  frequencyPenalty: ['frequency_penalty', 'frequency_penalty_setting'],
  seed: ['seed', 'seed_setting'],
  stopSequences: ['stop_sequences_setting']
} as const

const formRef = ref<FormInstance | null>(null)
const loading = ref(false)
const submitting = ref(false)
const modelsLoading = ref(false)
const modelList = ref<AiModel[]>([])

const isEdit = computed(() => props.record?.id !== undefined && props.record?.id !== null)

const statusOptions = [
  { label: '启用', value: 'enabled' },
  { label: '禁用', value: 'disabled' }
]

const modelKeyFieldRules = computed(() =>
  isEdit.value ? [] : [{ required: true, message: '请选择关联模型' }]
)

const form = reactive<AiInstance>({})

const resolvedModel = computed(() => {
  const key = form.modelKey?.trim()
  if (!key) return null
  return modelList.value.find((m) => m.modelKey === key) ?? null
})

/** 有 modelKey 但当前模型列表里找不到（如模型已删） */
const orphanModelKey = computed(() => {
  const k = form.modelKey?.trim()
  return !!k && !resolvedModel.value
})

const showChatParamSection = computed(
  () => orphanModelKey.value || resolvedModel.value?.modelType === 'chat'
)

const modelCapabilitySet = computed(() => {
  const raw = resolvedModel.value?.capabilities
  if (!raw || typeof raw !== 'string') return new Set<string>()
  try {
    const arr = JSON.parse(raw)
    if (!Array.isArray(arr)) return new Set<string>()
    return new Set(arr.map(String))
  } catch {
    return new Set<string>()
  }
})

const chatFieldVisibility = computed(() => {
  const vis: Record<string, boolean> = {
    maxTokens: false,
    temperature: false,
    topP: false,
    topK: false,
    presencePenalty: false,
    frequencyPenalty: false,
    seed: false,
    stopSequences: false
  }
  const m = resolvedModel.value
  if (!m || m.modelType !== 'chat') return vis
  const caps = modelCapabilitySet.value
  const allAliases: string[] = []
  ;(Object.values(CHAT_FIELD_CAPS) as ReadonlyArray<readonly string[]>).forEach((arr) => {
    allAliases.push(...arr)
  })
  const treatAsFull = caps.size === 0 || !allAliases.some((a) => caps.has(a))
  ;(Object.keys(CHAT_FIELD_CAPS) as Array<keyof typeof CHAT_FIELD_CAPS>).forEach((field) => {
    const aliases = CHAT_FIELD_CAPS[field] as readonly string[]
    vis[field] = treatAsFull || aliases.some((a) => caps.has(a))
  })
  return vis
})

const chatFieldVisibilityEffective = computed(() => {
  if (orphanModelKey.value) {
    return {
      maxTokens: true,
      temperature: true,
      topP: true,
      topK: true,
      presencePenalty: true,
      frequencyPenalty: true,
      seed: true,
      stopSequences: true
    }
  }
  return chatFieldVisibility.value
})

const showEmbeddingDimensions = computed(() => {
  const m = resolvedModel.value
  if (!m || m.modelType !== 'embedding') return false
  const caps = modelCapabilitySet.value
  if (caps.size === 0) return true
  return caps.has('embedding_dimensions')
})

const showImageSize = computed(() => {
  const m = resolvedModel.value
  if (!m || m.modelType !== 'image') return false
  const caps = modelCapabilitySet.value
  if (caps.size === 0) return true
  return caps.has('image_size') || caps.has('size_setting')
})

const showImageStyle = computed(() => {
  const m = resolvedModel.value
  if (!m || m.modelType !== 'image') return false
  const caps = modelCapabilitySet.value
  if (caps.size === 0) return true
  return caps.has('image_style') || caps.has('style_setting')
})

const showImageDimensions = computed(() => {
  const m = resolvedModel.value
  if (!m || m.modelType !== 'image') return false
  const caps = modelCapabilitySet.value
  if (caps.size === 0) return false
  return caps.has('embedding_dimensions')
})

function modelTypeLabel(t?: string) {
  if (t === 'embedding') return '向量'
  if (t === 'image') return '图像'
  return '对话'
}

const modelSelectOptions = computed(() =>
  modelList.value
    .filter((m) => m.modelKey)
    .map((m) => ({
      value: m.modelKey as string,
      label: `${m.modelName || m.modelKey} (${m.modelKey}) · ${modelTypeLabel(m.modelType)}`
    }))
)

const filterModelOption = (input: string, option: { label?: string }) => {
  const q = input.trim().toLowerCase()
  if (!q) return true
  return String(option?.label ?? '').toLowerCase().includes(q)
}

async function fetchModels() {
  modelsLoading.value = true
  try {
    const resp = await aiModelApi.queryPage({
      pageNo: 1,
      pageSize: 500,
      param: {}
    })
    modelList.value = resp.list || []
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '加载模型列表失败')
    modelList.value = []
  } finally {
    modelsLoading.value = false
  }
}

async function ensureModelInListForEdit(modelKey: string) {
  if (modelList.value.some((m) => m.modelKey === modelKey)) return
  try {
    const resp = await aiModelApi.queryPage({
      pageNo: 1,
      pageSize: 1,
      param: { modelKey }
    })
    const hit = resp.list?.[0]
    if (hit) modelList.value = [...modelList.value, hit]
  } catch {
    /* ignore */
  }
}

const handleCancel = () => {
  emit('update:visible', false)
}

const loadDetail = async (id: string | number) => {
  loading.value = true
  try {
    const detail = await aiInstanceApi.detail(id)
    Object.keys(form).forEach((k) => delete (form as Record<string, unknown>)[k])
    Object.assign(form, detail)
    if (detail.modelKey) await ensureModelInListForEdit(String(detail.modelKey))
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const onSubmit = async () => {
  try {
    if (!isEdit.value) {
      await formRef.value?.validateFields(['modelKey'])
    }
  } catch {
    return
  }
  submitting.value = true
  try {
    const payload: AiInstance = { ...form }
    let msg: string
    if (isEdit.value) {
      msg = await aiInstanceApi.update(payload)
    } else {
      delete (payload as { id?: unknown }).id
      msg = await aiInstanceApi.create(payload)
    }
    message.success(msg)
    emit('update:visible', false)
    emit('success')
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '保存失败')
  } finally {
    submitting.value = false
  }
}

watch(
  () => props.visible,
  async (val) => {
    if (!val) return
    await fetchModels()
    if (props.record?.id) {
      await loadDetail(props.record.id)
    } else {
      Object.keys(form).forEach((k) => delete (form as Record<string, unknown>)[k])
      form.status = 'enabled'
    }
  }
)
</script>

<style scoped>
.w-full {
  width: 100%;
}

.mb-12 {
  margin-bottom: 12px;
}

.param-section-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary, #666);
  margin: 8px 0 12px;
  padding-bottom: 6px;
  border-bottom: 1px solid var(--border-default, #f0f0f0);
}
</style>
