<template>
    <div class="page-content">
      <slot>
        <a-empty :description="emptyText" />
      </slot>
    </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    title: string;
    description: string;
    emptyText?: string;
  }>(),
  {
    emptyText: '暂无数据'
  }
);
</script>

<style scoped>


.page-content {
  min-height: calc(100vh - 70px);
  border-radius: var(--radius-sm);
  padding: 24px;

}
</style>
