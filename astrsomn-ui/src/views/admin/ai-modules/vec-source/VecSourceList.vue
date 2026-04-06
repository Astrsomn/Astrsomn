<template>
  <AdminPageShell
    title="向量源"
    description="配置向量数据库连接信息，支持 Milvus、Pinecone、DashVector 等多种向量引擎。"
    empty-text="暂无向量源配置。"
  />
</template>

<script setup lang="ts">
import AdminPageShell from '@/components/home/AdminPageShell.vue'
</script>
