<template>
  <AstrsomnPageShell description="调用发布接口发布流程。" empty-text="" title="流程发布">
    <div class="form-page">
      <a-form :model="form" class="publish-form" layout="vertical" @finish="onSubmit">
        <a-row :gutter="16">
          <a-col :md="12" :xs="24">
            <a-form-item :rules="[{ required: true, message: '请输入流程定义 ID' }]" label="流程定义 ID"
                         name="flowDefinitionId">
              <a-input v-model:value="form.flowDefinitionId" allow-clear/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :xs="24">
            <a-form-item label="版本号" name="version">
              <a-input v-model:value="form.version" allow-clear/>
            </a-form-item>
          </a-col>
          <a-col :xs="24">
            <a-form-item label="扩展参数 JSON">
              <a-textarea v-model:value="form.extraJson" :auto-size="{ minRows: 6, maxRows: 12 }"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-button :loading="submitting" class="primary-btn" html-type="submit" type="primary">发布</a-button>
      </a-form>
    </div>
  </AstrsomnPageShell>
</template>

<script lang="ts" setup>
import {reactive, ref} from 'vue'
import {message} from 'ant-design-vue'
import AstrsomnPageShell from '@/components/home/AstrsomnPageShell.vue'
import {aiWorkflowRuntimeApi} from '@/api/aiWorkflowRuntime'

const submitting = ref(false)
const form = reactive({
  flowDefinitionId: '',
  version: '',
  extraJson: ''
})

const onSubmit = async () => {
  submitting.value = true
  try {
    let extra: Record<string, unknown> = {}
    if (form.extraJson.trim()) {
      extra = JSON.parse(form.extraJson.trim()) as Record<string, unknown>
    }
    const payload: Record<string, unknown> = {
      flowDefinitionId: form.flowDefinitionId || undefined,
      version: form.version || undefined,
      ...extra
    }
    const msg = await aiWorkflowRuntimeApi.publish(payload)
    message.success(msg || '发布成功')
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '发布失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.form-page {
  padding: 20px;
}

.publish-form {
  max-width: 860px;
}

.primary-btn {
  height: 40px;
  min-width: 112px;
  border-radius: 12px;
}
</style>
