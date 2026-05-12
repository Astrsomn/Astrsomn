<template>
  <AstrsomnPageShell title="发布详情" description="查看流程发布记录详情。" empty-text="">
    <div class="form-page">
      <a-button class="ghost-btn" @click="goBack">
        <template #icon><arrow-left-outlined /></template>
        返回列表
      </a-button>
      <a-form class="detail-form" layout="vertical" :model="form">
        <a-row :gutter="16">
          <a-col :xs="24" :md="12"><a-form-item label="ID"><a-input v-model:value="form.id" disabled /></a-form-item></a-col>
          <a-col :xs="24" :md="12"><a-form-item label="流程定义 ID"><a-input v-model:value="form.flowDefinitionId" disabled /></a-form-item></a-col>
          <a-col :xs="24" :md="12"><a-form-item label="版本号"><a-input v-model:value="form.version" disabled /></a-form-item></a-col>
          <a-col :xs="24" :md="12"><a-form-item label="是否最新"><a-input v-model:value="form.latest" disabled /></a-form-item></a-col>
          <a-col :xs="24" :md="12"><a-form-item label="创建时间"><a-input v-model:value="form.createTime" disabled /></a-form-item></a-col>
          <a-col :xs="24" :md="12"><a-form-item label="更新时间"><a-input v-model:value="form.updateTime" disabled /></a-form-item></a-col>
          <a-col :xs="24"><a-form-item label="详情 JSON"><a-textarea v-model:value="form.rawJson" :auto-size="{ minRows: 10, maxRows: 18 }" disabled /></a-form-item></a-col>
        </a-row>
      </a-form>
    </div>
  </AstrsomnPageShell>
</template>

<script setup lang="ts">
import { reactive, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { ArrowLeftOutlined } from '@ant-design/icons-vue'
import AstrsomnPageShell from '@/components/home/AstrsomnPageShell.vue'
import { aiWorkflowRuntimeApi, type WorkflowRuntimeRecord } from '@/api/aiWorkflowRuntime'

const route = useRoute()
const router = useRouter()
const form = reactive<Record<string, string>>({})

const applyForm = (detail: WorkflowRuntimeRecord) => {
  form.id = String(detail.id ?? '')
  form.flowDefinitionId = String(detail.flowDefinitionId ?? '')
  form.version = String(detail.version ?? '')
  form.latest = String(detail.latest ?? '')
  form.createTime = String(detail.createTime ?? '')
  form.updateTime = String(detail.updateTime ?? '')
  form.rawJson = JSON.stringify(detail, null, 2)
}

const goBack = () => {
  void router.push({ name: 'AdminWorkflowDeployments' })
}

const loadDetail = async (id: string) => {
  try {
    const detail = await aiWorkflowRuntimeApi.deploymentDetail(id)
    applyForm(detail)
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '加载失败')
    goBack()
  }
}

watch(
  () => route.params.id,
  (id) => {
    if (id != null && String(id)) {
      void loadDetail(String(id))
    }
  },
  { immediate: true }
)
</script>

<style scoped>
.form-page {
  padding: 20px;
}

.detail-form {
  margin-top: 16px;
  max-width: 960px;
}

.ghost-btn {
  height: 40px;
  border-radius: 12px;
}
</style>
