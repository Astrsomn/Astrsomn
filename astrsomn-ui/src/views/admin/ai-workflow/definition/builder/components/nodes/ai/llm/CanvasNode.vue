<template>
  <BaseCanvasNode
      :data="data"
      :theme="{
      borderColor: '#bfdbfe',
      background: '#ffffff',
      titleColor: '#0f172a',
      descColor: '#475569'
    }"
      badge="GPT-4o"
      fallback-description="调用大语言模型进行生成"
      fallback-label="LLM"
      icon-background="#dbeafe"
      icon-color="#2563eb"
      icon-text="L"
      subtitle="Generation"
  >
    <div class="prompt">"你是一个专业的分析师，根据上下文输出结果..."</div>
    <div class="meta">
      <span>Temperature</span>
      <span>0.7</span>
    </div>
  </BaseCanvasNode>
</template>

<script lang="ts" setup>
import BaseCanvasNode from '../../shared/BaseCanvasNode.vue'
import type {WorkflowNodeViewData} from '../../shared/node-props'

defineProps<{ data?: WorkflowNodeViewData }>()
</script>

<style scoped>
.prompt {
  border: 1px dashed #cbd5e1;
  background: #f8fafc;
  border-radius: 10px;
  padding: 8px;
  font-size: 10px;
  color: #64748b;
}

.meta {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  font-size: 10px;
  color: #475569;
}
</style>

