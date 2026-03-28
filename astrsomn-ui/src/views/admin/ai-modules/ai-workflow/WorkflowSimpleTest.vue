<template>
  <AdminPageShell
    title="工作流编排测试"
    description="按 Workflow Key 调用 /test-run：优先已发布版本，若无发布记录则使用同环境下该 Key 的最新草稿。"
    empty-text=""
  >
    <div class="simple-test">
      <a-card title="运行参数" class="card">
        <a-form layout="vertical">
          <a-form-item label="Workflow Key" required>
            <a-input v-model:value="form.workflowKey" placeholder="与列表中的 workflow_key 一致（未发布时测最新草稿）" />
          </a-form-item>
          <a-form-item label="userMessage">
            <a-textarea v-model:value="form.userMessage" :rows="3" />
          </a-form-item>
          <a-form-item label="memoryKey（可选）">
            <a-input v-model:value="form.memoryKey" />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" :loading="loading" @click="run">执行测试</a-button>
            <a-button class="ml" @click="goList">返回工作流列表</a-button>
          </a-form-item>
        </a-form>
      </a-card>

      <a-card v-if="result" title="返回结果" class="card">
        <pre class="json-out">{{ formatted }}</pre>
      </a-card>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import AdminPageShell from '@/views/admin/components/AdminPageShell.vue'
import { aiWorkflowApi, type WorkflowTestRunResult } from '@/api/aiWorkflow'

const router = useRouter()

const form = reactive({
  workflowKey: 'demo_flow',
  userMessage: 'hello',
  memoryKey: ''
})

const loading = ref(false)
const result = ref<WorkflowTestRunResult | null>(null)

const formatted = computed(() => (result.value ? JSON.stringify(result.value, null, 2) : ''))

const run = async () => {
  if (!form.workflowKey.trim()) {
    message.warning('请填写 Workflow Key')
    return
  }
  loading.value = true
  try {
    result.value = await aiWorkflowApi.testRun({
      workflowKey: form.workflowKey.trim(),
      userMessage: form.userMessage,
      memoryKey: form.memoryKey || undefined
    })
    message.success('执行完成')
  } catch (e: unknown) {
    message.error(e instanceof Error ? e.message : '执行失败')
  } finally {
    loading.value = false
  }
}

const goList = () => {
  void router.push({ name: 'AdminWorkflows' })
}
</script>

<style scoped>
.simple-test {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 800px;
}
.card {
  background: var(--bg-card, #141414);
  border: 1px solid var(--border-subtle, #333);
}
.ml {
  margin-left: 8px;
}
.json-out {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  font-size: 12px;
  color: var(--text-secondary, #ccc);
}
</style>
