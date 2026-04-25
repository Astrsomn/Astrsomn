<template>
  <div class="page-content" :class="{ 'page-content--in-module': inModuleLayout }">
    <slot>
      <a-empty :description="emptyText" />
    </slot>
  </div>
</template>

<script setup lang="ts">
import { inject } from 'vue';

withDefaults(
  defineProps<{
    title: string;
    description: string;
    emptyText?: string;
  }>(),
  {
    emptyText: '暂无数据',
  }
);

const inModuleLayout = inject('adminInModuleLayout', false);
</script>

<style scoped>
.page-content {
  min-height: calc(100vh - 70px);
  border-radius: var(--radius-sm);
}

.page-content--in-module {
  min-height: auto;
}
</style>
