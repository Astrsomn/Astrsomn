<template>
  <AdminPageShell
    :title="isEdit ? '编辑系统消息' : '新建系统消息'"
    description="维护系统通知内容，支持类型、级别、关联引用与错误码。"
    empty-text=""
  >
    <div class="form-page">
      <div class="form-toolbar">
        <a-button class="ghost-btn" @click="goBack">
          <template #icon><arrow-left-outlined /></template>
          返回列表
        </a-button>
      </div>

      <a-form class="message-form" layout="vertical" :model="form" @finish="onSubmit">
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="消息类型" name="messageType" :rules="[{ required: true, message: '请选择消息类型' }]">
              <a-select v-model:value="form.messageType" :options="messageTypeOptions" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="消息级别" name="messageLevel" :rules="[{ required: true, message: '请选择消息级别' }]">
              <a-select v-model:value="form.messageLevel" :options="messageLevelOptions" allow-clear />
            </a-form-item>
          </a-col>

          <a-col :xs="24" :md="12">
            <a-form-item label="已读状态" name="readStatus">
              <a-select v-model:value="form.readStatus" :options="readStatusOptions" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="来源" name="source">
              <a-input v-model:value="form.source" placeholder="如 SYSTEM / extension-key / service-name" allow-clear />
            </a-form-item>
          </a-col>

          <a-col :xs="24">
            <a-form-item label="标题" name="title" :rules="[{ required: true, message: '请输入标题' }]">
              <a-input v-model:value="form.title" placeholder="列表展示标题" allow-clear />
            </a-form-item>
          </a-col>

          <a-col :xs="24">
            <a-form-item label="正文" name="content">
              <a-textarea
                v-model:value="form.content"
                :auto-size="{ minRows: 4, maxRows: 10 }"
                placeholder="可填文本或 JSON"
              />
            </a-form-item>
          </a-col>

          <a-col :xs="24" :md="8">
            <a-form-item label="关联类型" name="refType">
              <a-select v-model:value="form.refType" :options="refTypeOptions" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="8">
            <a-form-item label="关联ID" name="refId">
              <a-input v-model:value="refIdText" placeholder="数字ID" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="8">
            <a-form-item label="关联Key" name="refKey">
              <a-input v-model:value="form.refKey" placeholder="如 traceId / extensionKey" allow-clear />
            </a-form-item>
          </a-col>

          <a-col :xs="24" :md="12">
            <a-form-item label="错误码" name="errorCode">
              <a-input v-model:value="form.errorCode" placeholder="调用失败时可选" allow-clear />
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
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { ArrowLeftOutlined } from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import { systemMessageApi, type SystemMessage } from '@/api/systemMessage'

const route = useRoute()
const router = useRouter()

const submitting = ref(false)
const loading = ref(false)

const isEdit = computed(() => route.name === 'AdminSystemMessageEdit')

const messageTypeOptions = [
  { label: '插件已安装', value: 'PLUGIN_INSTALLED' },
  { label: '插件安装失败', value: 'PLUGIN_INSTALL_FAILED' },
  { label: '插件已卸载', value: 'PLUGIN_UNINSTALLED' },
  { label: '上线通知', value: 'DEPLOYMENT_ONLINE' },
  { label: '调用失败', value: 'API_CALL_FAILED' },
  { label: '系统通知', value: 'SYSTEM_NOTICE' },
  { label: '其他', value: 'OTHER' }
]

const messageLevelOptions = [
  { label: '信息', value: 'INFO' },
  { label: '成功', value: 'SUCCESS' },
  { label: '警告', value: 'WARN' },
  { label: '错误', value: 'ERROR' }
]

const readStatusOptions = [
  { label: '未读', value: 'UNREAD' },
  { label: '已读', value: 'READ' }
]

const refTypeOptions = [
  { label: '扩展/插件', value: 'EXTENSION' },
  { label: '模型实例', value: 'AI_INSTANCE' },
  { label: 'Agent', value: 'AI_AGENT' },
  { label: '对话', value: 'AI_CONVERSATION' },
  { label: 'MCP', value: 'AI_MCP' },
  { label: '系统', value: 'SYSTEM' },
  { label: '其他', value: 'OTHER' }
]

const form = reactive<SystemMessage>({
  messageType: 'SYSTEM_NOTICE',
  messageLevel: 'INFO',
  readStatus: 'UNREAD',
  title: '',
  content: '',
  source: 'SYSTEM'
})

const refIdText = ref('')

const applyForm = (data: SystemMessage) => {
  Object.keys(form).forEach((k) => delete (form as Record<string, unknown>)[k])
  Object.assign(form, {
    messageType: 'SYSTEM_NOTICE',
    messageLevel: 'INFO',
    readStatus: 'UNREAD',
    title: '',
    content: '',
    source: 'SYSTEM'
  }, data)
  refIdText.value = form.refId == null ? '' : String(form.refId)
}

const loadDetail = async (id: string) => {
  loading.value = true
  try {
    const detail = await systemMessageApi.detail(id)
    applyForm(detail)
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '加载失败')
    goBack()
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  void router.push({ name: 'AdminSystemMessage' })
}

const parseRefId = () => {
  const t = refIdText.value.trim()
  if (!t) {
    form.refId = undefined
    return true
  }
  if (!/^\d+$/.test(t)) {
    message.warning('关联ID需为数字')
    return false
  }
  form.refId = t
  return true
}

const onSubmit = async () => {
  if (!parseRefId()) return
  submitting.value = true
  try {
    const payload: SystemMessage = { ...form }
    let msg: string
    if (isEdit.value) {
      msg = await systemMessageApi.update(payload)
    } else {
      delete (payload as { id?: unknown }).id
      msg = await systemMessageApi.create(payload)
    }
    message.success(msg)
    goBack()
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
    if (name === 'AdminSystemMessageNew') {
      applyForm({})
      return
    }
    if (name === 'AdminSystemMessageEdit' && id != null && String(id)) {
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

.message-form {
  max-width: 860px;
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
