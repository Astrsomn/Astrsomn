<template>
  <AstPageShell description="查看节点配置详情。" empty-text="" title="节点配置详情">
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
            <a-form-item label="flowDefinitionId">
              <a-input v-model:value="form.flowDefinitionId" disabled/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :xs="24">
            <a-form-item label="nodeId">
              <a-input v-model:value="form.nodeId" disabled/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :xs="24">
            <a-form-item label="nodeType">
              <a-input v-model:value="form.nodeType" disabled/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :xs="24">
            <a-form-item label="enabled">
              <a-input v-model:value="form.enabled" disabled/>
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
  </AstPageShell>
</template>

<script lang="ts" setup>
import {reactive, watch} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {message} from 'ant-design-vue'
import {ArrowLeftOutlined} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import {aiWorkflowRuntimeApi, type WorkflowRuntimeRecord} from '@/api/aiWorkflowRuntime'

const route = useRoute()
const router = useRouter()
const form = reactive<Record<string, string>>({})

const applyForm = (detail: WorkflowRuntimeRecord) => {
  form.id = String(detail.id ?? '')
  form.flowDefinitionId = String(detail.flowDefinitionId ?? '')
  form.nodeId = String(detail.nodeId ?? '')
  form.nodeType = String(detail.nodeType ?? '')
  form.enabled = String(detail.enabled ?? '')
  form.createTime = String(detail.createTime ?? '')
  form.updateTime = String(detail.updateTime ?? '')
  form.rawJson = JSON.stringify(detail, null, 2)
}

const goBack = () => {
  void router.push({name: 'AdminWorkflowNodeConfigs'})
}

const loadDetail = async (id: string) => {
  try {
    const detail = await aiWorkflowRuntimeApi.nodeConfigDetail(id)
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
