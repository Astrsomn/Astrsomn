<template>
  <AdminPageShell
    :title="isEdit ? '编辑推理实例' : '新建推理实例'"
    description="配置实例名称、关联模型与推理超参；modelKey 创建后不可改；Instance Key 可留空由后端生成。完整按模型能力展示字段请使用列表中的弹窗表单。"
    empty-text=""
  >
    <div class="form-page">
      <div class="form-toolbar">
        <a-button class="ghost-btn" @click="goBack">
          <template #icon><arrow-left-outlined /></template>
          返回列表
        </a-button>
      </div>

      <a-spin :spinning="loading">
        <a-form class="instance-form" layout="vertical" :model="form" @finish="onSubmit">
          <a-row :gutter="16">
            <a-col :xs="24" :md="12">
              <a-form-item label="实例名称" name="instanceName">
                <a-input v-model:value="form.instanceName" placeholder="展示用名称" allow-clear />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="12">
              <a-form-item label="状态" name="status">
                <a-select
                  v-model:value="form.status"
                  :options="statusOptions"
                  placeholder="请选择"
                  allow-clear
                />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="12">
              <a-form-item
                label="关联模型 (modelKey)"
                name="modelKey"
                :rules="[{ required: !isEdit, message: '请选择关联模型' }]"
              >
                <a-select
                  v-model:value="form.modelKey"
                  :options="modelSelectOptions"
                  :loading="modelsLoading"
                  :disabled="isEdit"
                  show-search
                  :filter-option="filterModelOption"
                  placeholder="创建后不可修改"
                  allow-clear
                  option-filter-prop="label"
                />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="12">
              <a-form-item label="Instance Key" name="instanceKey">
                <a-input
                  v-model:value="form.instanceKey"
                  placeholder="可选，留空则自动生成"
                  allow-clear
                />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="8">
              <a-form-item label="Max tokens" name="maxTokens">
                <a-input-number v-model:value="form.maxTokens" class="w-full" :min="0" placeholder="最大生成长度" />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="8">
              <a-form-item label="Temperature" name="temperature">
                <a-input-number
                  v-model:value="form.temperature"
                  class="w-full"
                  :min="0"
                  :max="2"
                  :step="0.1"
                />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="8">
              <a-form-item label="Top P" name="topP">
                <a-input-number v-model:value="form.topP" class="w-full" :min="0" :max="1" :step="0.05" />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="8">
              <a-form-item label="Top K" name="topK">
                <a-input-number v-model:value="form.topK" class="w-full" :min="0" placeholder="整数" />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="8">
              <a-form-item label="Presence penalty" name="presencePenalty">
                <a-input-number v-model:value="form.presencePenalty" class="w-full" :step="0.1" />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="8">
              <a-form-item label="Frequency penalty" name="frequencyPenalty">
                <a-input-number v-model:value="form.frequencyPenalty" class="w-full" :step="0.1" />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="8">
              <a-form-item label="Seed" name="seed">
                <a-input-number v-model:value="form.seed" class="w-full" placeholder="可选，整数种子" />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="8">
              <a-form-item label="Dimensions" name="dimensions">
                <a-input-number v-model:value="form.dimensions" class="w-full" :min="0" />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="8">
              <a-form-item label="Style" name="style">
                <a-input v-model:value="form.style" allow-clear />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="8">
              <a-form-item label="Size" name="size">
                <a-input v-model:value="form.size" allow-clear />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="Stop sequences" name="stopSequences">
                <a-textarea
                  v-model:value="form.stopSequences"
                  :rows="3"
                  placeholder="多段结束符，可按后端约定使用 JSON 或分隔符"
                  allow-clear
                />
              </a-form-item>
            </a-col>
          </a-row>

          <div class="form-actions">
            <a-button class="ghost-btn" @click="goBack">取消</a-button>
            <a-button type="primary" class="primary-btn" html-type="submit" :loading="submitting">
              {{ isEdit ? '保存' : '创建' }}
            </a-button>
          </div>
        </a-form>
      </a-spin>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { ArrowLeftOutlined } from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import { aiInstanceApi, type AiInstance } from '@/api/aiInstance'
import { aiModelApi, type AiModel } from '@/api/aiModel'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const modelsLoading = ref(false)
const modelList = ref<AiModel[]>([])

const isEdit = computed(() => route.name === 'AdminAiInstanceEdit')

const statusOptions = [
  { label: '启用', value: 'enabled' },
  { label: '禁用', value: 'disabled' }
]

const form = reactive<AiInstance>({})

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
    const resp = await aiModelApi.queryPage({ pageNo: 1, pageSize: 500, param: {} })
    modelList.value = resp.list || []
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '加载模型列表失败')
    modelList.value = []
  } finally {
    modelsLoading.value = false
  }
}

async function ensureModelInList(modelKey: string) {
  if (modelList.value.some((m) => m.modelKey === modelKey)) return
  try {
    const resp = await aiModelApi.queryPage({ pageNo: 1, pageSize: 1, param: { modelKey } })
    const hit = resp.list?.[0]
    if (hit) modelList.value = [...modelList.value, hit]
  } catch {
    /* ignore */
  }
}

const goBack = () => {
  void router.push({ name: 'AdminAiInstance' })
}

const loadDetail = async (id: string) => {
  loading.value = true
  try {
    await fetchModels()
    const detail = await aiInstanceApi.detail(id)
    Object.keys(form).forEach((k) => delete (form as Record<string, unknown>)[k])
    Object.assign(form, detail)
    if (detail.modelKey) await ensureModelInList(String(detail.modelKey))
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '加载失败')
    void goBack()
  } finally {
    loading.value = false
  }
}

const onSubmit = async () => {
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
    void goBack()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '保存失败')
  } finally {
    submitting.value = false
  }
}

watch(
  [() => route.name, () => route.params.id],
  ([name, id]) => {
    if (name === 'AdminAiInstanceNew') {
      void fetchModels()
      Object.keys(form).forEach((k) => delete (form as Record<string, unknown>)[k])
      form.status = 'enabled'
      return
    }
    if (name === 'AdminAiInstanceEdit' && id != null && String(id)) {
      void loadDetail(String(id))
    }
  },
  { immediate: true }
)
</script>

<style scoped>
.form-page {
  padding: 0 4px;
}

.form-toolbar {
  margin-bottom: 16px;
}

.instance-form {
  max-width: 960px;
}

.w-full {
  width: 100%;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
  padding-top: 16px;
  border-top: 1px solid var(--border-subtle);
}

.primary-btn,
.ghost-btn {
  height: 40px;
  border-radius: 12px;
  min-width: 104px;
}
</style>
