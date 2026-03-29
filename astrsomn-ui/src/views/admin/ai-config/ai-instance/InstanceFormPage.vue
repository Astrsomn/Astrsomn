<template>
  <AdminPageShell
    :title="isEdit ? '编辑推理实例' : '新建推理实例'"
    description="配置实例键与推理超参；Instance Key 可留空，由后端按规则生成。"
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
              <a-form-item label="Instance Key" name="instanceKey">
                <a-input
                  v-model:value="form.instanceKey"
                  placeholder="可选，留空则自动生成"
                  allow-clear
                />
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

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)

const isEdit = computed(() => route.name === 'AdminAiInstanceEdit')

const statusOptions = [
  { label: '启用', value: 'enabled' },
  { label: '禁用', value: 'disabled' }
]

const form = reactive<AiInstance>({})

const goBack = () => {
  void router.push({ name: 'AdminAiInstance' })
}

const loadDetail = async (id: string) => {
  loading.value = true
  try {
    const detail = await aiInstanceApi.detail(id)
    Object.keys(form).forEach((k) => delete (form as Record<string, unknown>)[k])
    Object.assign(form, detail)
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
