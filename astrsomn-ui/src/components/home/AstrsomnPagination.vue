<template>
  <div class="pagination-wrap">
    <span class="pagination-total">共 {{ total }} 条</span>
    <a-pagination
      :current="current"
      :page-size="pageSize"
      :total="total"
      :show-size-changer="showSizeChanger"
      @change="onChange"
    />
  </div>
</template>

<script setup lang="ts">
const props = withDefaults(defineProps<{
  current: number
  pageSize: number
  total: number
  showSizeChanger?: boolean
}>(), {
  showSizeChanger: false
})

const emit = defineEmits<{
  change: [page: number]
}>()

const onChange = (page: number) => {
  emit('change', page)
}
</script>

<style scoped>
.pagination-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.pagination-total {
  font-size: 13px;
  color: var(--text-secondary);
}

@media (max-width: 720px) {
  .pagination-wrap {
    justify-content: center;
  }
}

@media (max-width: 560px) {
  .pagination-total {
    width: 100%;
    text-align: center;
  }
}
</style>
