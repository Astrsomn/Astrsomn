<template>
  <div class="pagination-wrap">
    <span class="pagination-total">共 {{ total }} 条</span>
    <a-pagination
        :current="current"
        :page-size="pageSize"
        :show-size-changer="showSizeChanger"
        :total="total"
        @change="onChange"
    />
  </div>
</template>

<script lang="ts" setup>
const props = withDefaults(defineProps<{
  current: number
  pageSize: number
  total: number
  showSizeChanger?: boolean
}>(), {
  showSizeChanger: true
})

const emit = defineEmits<{
  change: [page: number, pageSize: number]
}>()

const onChange = (page: number, pageSize: number) => {
  emit('change', page, pageSize)
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

:deep(.ant-pagination) {
  font-size: 13px;
}

:deep(.ant-pagination-item) {
  border: 1px solid var(--border-default);
  background: var(--bg-card);
}

:deep(.ant-pagination-item a) {
  color: var(--text-secondary);
}

:deep(.ant-pagination-item:hover) {
  border-color: var(--primary);
}

:deep(.ant-pagination-item:hover a) {
  color: var(--primary);
}

:deep(.ant-pagination-item-active) {
  background: var(--primary);
  border-color: var(--primary);
}

:deep(.ant-pagination-item-active a) {
  color: #fff;
}

:deep(.ant-pagination-prev),
:deep(.ant-pagination-next) {
  border: 1px solid var(--border-default);
  background: var(--bg-card);
}

:deep(.ant-pagination-prev a),
:deep(.ant-pagination-next a) {
  color: var(--text-secondary);
}

:deep(.ant-pagination-prev:hover),
:deep(.ant-pagination-next:hover) {
  border-color: var(--primary);
}

:deep(.ant-pagination-prev:hover a),
:deep(.ant-pagination-next:hover a) {
  color: var(--primary);
}

:deep(.ant-pagination-disabled),
:deep(.ant-pagination-disabled:hover) {
  border-color: var(--border-default);
  opacity: 0.5;
}

:deep(.ant-pagination-options) {
  color: var(--text-secondary);
}

:deep(.ant-select-selector) {
  background: var(--bg-input);
  border-color: var(--border-default);
  color: var(--text-primary);
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
