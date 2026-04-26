<template>
  <AdminPageShell title="流程测试运行" description="调用测试运行接口并查看返回结果。" empty-text="">
    <div class="form-page">
      <a-form class="run-form" layout="vertical" :model="form" @finish="onSubmit">
        <a-row :gutter="16">
          <a-col :xs="24" :md="12"><a-form-item label="流程定义 ID"><a-input v-model:value="form.flowDefinitionId" allow-clear /></a-form-item></a-col>
          <a-col :xs="24" :md="12"><a-form-item label="业务主键"><a-input v-model:value="form.businessKey" allow-clear /></a-form-item></a-col>
          <a-col :xs="24"><a-form-item label="输入参数 JSON"><a-textarea v-model:value="form.inputJson" :auto-size="{ minRows: 8, maxRows: 14 }" /></a-form-item></a-col>
        </a-row>
        <a-button type="primary" class="primary-btn" html-type="submit" :loading="submitting">执行测试运行</a-button>
      </a-form>

      <a-form class="result-form" layout="vertical" v-if="resultText">
        <a-form-item label="返回结果">
          <a-textarea :value="resultText" :auto-size="{ minRows: 8, maxRows: 16 }" disabled />
        </a-form-item>
      </a-form>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import { aiWorkflowRuntimeApi } from '@/api/aiWorkflowRuntime'

const submitting = ref(false)
const resultText = ref('')
const form = reactive({
  flowDefinitionId: '',
  businessKey: '',
  inputJson: ''
})

const onSubmit = async () => {
  submitting.value = true
  try {
    let input: Record<string, unknown> = {}
    if (form.inputJson.trim()) {
      input = JSON.parse(form.inputJson.trim()) as Record<string, unknown>
    }
    const payload: Record<string, unknown> = {
      flowDefinitionId: form.flowDefinitionId || undefined,
      businessKey: form.businessKey || undefined,
      ...input
    }
    const result = await aiWorkflowRuntimeApi.testRun(payload)
    resultText.value = JSON.stringify(result, null, 2)
    message.success('测试运行成功')
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '测试运行失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.form-page {
  padding: 20px;
}

.run-form {
  max-width: 860px;
}

.result-form {
  margin-top: 20px;
  max-width: 860px;
}

.primary-btn {
  height: 40px;
  min-width: 144px;
  border-radius: 12px;
}
</style>
