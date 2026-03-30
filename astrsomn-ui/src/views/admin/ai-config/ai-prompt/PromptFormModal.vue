<template>
  <a-modal
    v-model:open="open"
    :title="mode === 'create' ? '新增提示词' : '编辑提示词'"
    width="1000px" 
    :confirm-loading="confirmLoading"
    :body-style="{ padding: '16px 24px' }"
    @ok="handleOk"
    @cancel="onCancel"
  >
    <a-form
      ref="formRef"
      :model="form"
      :rules="rules"
      layout="vertical"
    >
      <a-alert
        type="info"
        show-icon
        class="compact-alert"
      >
        <template #message>
          <span v-if="mode === 'create'">Prompt Key 可留空，后端自动生成；首次创建为版本 1。</span>
          <span v-else>可修改 Prompt Key；若留空则沿用当前值，保存后自动递增版本号。</span>
        </template>
      </a-alert>

      <div class="editor-layout">
        <div class="config-side">
          <a-form-item label="标题" name="promptTitle">
            <a-input v-model:value="form.promptTitle" placeholder="展示名称" />
          </a-form-item>

          <a-form-item label="Prompt Key" name="promptKey">
            <a-input
              v-model:value="form.promptKey"
              placeholder="唯一标识（选填）"
              allow-clear
            />
          </a-form-item>

          <div class="form-row">
            <a-form-item label="场景" name="scene" class="flex-1">
              <a-input v-model:value="form.scene" placeholder="分类" allow-clear />
            </a-form-item>
            <a-form-item label="状态" name="status" class="w-80">
              <a-select v-model:value="form.status" :options="enabledOptions" />
            </a-form-item>
          </div>

          <a-form-item v-if="mode === 'edit'" label="当前版本">
            <a-tag color="blue">V{{ form.version ?? '1' }}</a-tag>
          </a-form-item>
        </div>

        <div class="content-side">
          <a-form-item label="提示词内容" name="promptContent" class="no-margin-bottom">
            <a-textarea
              v-model:value="form.promptContent"
              :auto-size="{ minRows: 16, maxRows: 16 }"
              placeholder="请输入 System 或 User Prompt..."
              class="content-area"
            />
          </a-form-item>
        </div>
      </div>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
// ... script 部分逻辑保持不变 ...
import { reactive, ref, watch } from 'vue'
import type { FormInstance } from 'ant-design-vue'

const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: any | null
}>()

const emit = defineEmits(['submit'])
const open = defineModel<boolean>('open', { required: true })
const formRef = ref<FormInstance | null>(null)

const enabledOptions = [
  { label: '启用', value: 'enabled' },
  { label: '停用', value: 'disabled' }
]

const emptyForm = () => ({
  promptKey: '',
  promptTitle: '',
  promptContent: '',
  scene: '',
  status: 'enabled',
  version: 1
})

const form = reactive(emptyForm())

const rules = {
  promptTitle: [{ required: true, message: '请输入标题' }],
  promptContent: [{ required: true, message: '请输入提示词内容' }],
  status: [{ required: true, message: '请选择启用状态' }]
}

watch([() => open.value, () => props.initial], ([isOpen, initial]) => {
  if (!isOpen) return
  if (initial && Object.keys(initial).length > 0) {
    Object.assign(form, emptyForm(), initial)
  } else {
    Object.assign(form, emptyForm())
  }
}, { immediate: true })

async function handleOk() {
  try {
    await formRef.value?.validate()
    const payload: Record<string, any> = { ...form }
    if (!payload.promptKey?.trim()) {
      delete payload.promptKey
    } else {
      payload.promptKey = payload.promptKey.trim()
    }
    emit('submit', payload)
  } catch (err) {}
}

function onCancel() { open.value = false }
</script>

<style scoped>
.compact-alert {
  margin-bottom: 20px;
  padding: 8px 12px;
}

.editor-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.config-side {
  width: 320px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
}

.content-side {
  flex: 1;
  min-width: 0;
}

.form-row {
  display: flex;
  gap: 12px;
}

.flex-1 { flex: 1; }
.w-80 { width: 100px; }

.content-area {
  font-family: 'Fira Code', ui-monospace, monospace;
  font-size: 13px;
  background-color: #f8fafc;
  padding: 12px;
  border-radius: 8px;
  line-height: 1.6;
  resize: none;
}

/* 覆盖 Ant Design 默认边距，紧凑显示 */
:deep(.ant-form-item) {
  margin-bottom: 16px;
}

.no-margin-bottom {
  margin-bottom: 0 !important;
}

/* 适配移动端/小屏幕 */
@media (max-width: 800px) {
  .editor-layout {
    flex-direction: column;
  }
  .config-side {
    width: 100%;
  }
}
</style>