<template>
  <AstrsomnPageShell description="查看流程实例详情。" empty-text="" title="流程实例详情">
    <div class="form-page">
      <a-button class="ghost-btn" @click="goBack">
        <template #icon>
          <arrow-left-outlined/>
        </template>
        返回列表
      </a-button>
      <a-form :model="form" class="detail-form" layout="vertical">
        <a-row :gutter="16">
          <a-col :md="12" :xs="24">
            <a-form-item label="id">
              <a-input v-model:value="form.id" disabled/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :xs="24">
            <a-form-item label="deploymentId">
              <a-input v-model:value="form.deploymentId" disabled/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :xs="24">
            <a-form-item label="businessKey">
              <a-input v-model:value="form.businessKey" disabled/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :xs="24">
            <a-form-item label="executionStatus">
              <a-input v-model:value="form.executionStatus" disabled/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :xs="24">
            <a-form-item label="currentNodeId">
              <a-input v-model:value="form.currentNodeId" disabled/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :xs="24">
            <a-form-item label="createTime">
              <a-input v-model:value="form.createTime" disabled/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :xs="24">
            <a-form-item label="updateTime">
              <a-input v-model:value="form.updateTime" disabled/>
            </a-form-item>
          </a-col>
          <a-col :xs="24">
            <a-form-item label="详情 JSON">
              <a-textarea v-model:value="form.rawJson" :auto-size="{ minRows: 10, maxRows: 18 }" disabled/>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
  </AstrsomnPageShell>
</template>

<script lang="ts" setup>
import {reactive, watch} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {message} from 'ant-design-vue'
import {ArrowLeftOutlined} from '@ant-design/icons-vue'
import AstrsomnPageShell from '@/components/home/AstrsomnPageShell.vue'
import {aiWorkflowRuntimeApi, type WorkflowRuntimeRecord} from '@/api/aiWorkflowRuntime'

const route = useRoute()
const router = useRouter()
const form = reactive<Record<string, string>>({})

const applyForm = (detail: WorkflowRuntimeRecord) => {
  form.id = String(detail.id ?? '')
  form.deploymentId = String(detail.deploymentId ?? '')
  form.businessKey = String(detail.businessKey ?? '')
  form.executionStatus = String(detail.executionStatus ?? '')
  form.currentNodeId = String(detail.currentNodeId ?? '')
  form.createTime = String(detail.createTime ?? '')
  form.updateTime = String(detail.updateTime ?? '')
  form.rawJson = JSON.stringify(detail, null, 2)
}

const goBack = () => {
  void router.push({name: 'AdminWorkflowInstances'})
}

const loadDetail = async (id: string) => {
  try {
    const detail = await aiWorkflowRuntimeApi.instanceDetail(id)
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
    {immediate: true}
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
