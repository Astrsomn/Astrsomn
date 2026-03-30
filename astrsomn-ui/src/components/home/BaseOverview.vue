<template>
  <div class="list-overview">
    <div class="overview-main">
      <div class="overview-icon">
        <appstore-outlined />
      </div>
      <div>
        <div class="overview-desc">
          {{ summaryText || `当前页 ${listLength} 条，已选 ${selectedCount} 条` }}
        </div>
      </div>
    </div>

    <div v-if="showActions" class="overview-actions">
      <a-checkbox
        :checked="allCurrentSelected"
        :indeterminate="partCurrentSelected"
        @change="onToggleSelectAll"
      >
        本页全选
      </a-checkbox>
    </div>
  </div>
</template>

<script setup lang="ts">
import { AppstoreOutlined } from '@ant-design/icons-vue'

const props = defineProps<{
  listLength: number
  selectedCount: number
  allCurrentSelected: boolean
  partCurrentSelected: boolean
  showActions: boolean
  summaryText?: string
}>()

const emit = defineEmits<{
  'toggle-select-all': [checked: boolean]
}>()

const onToggleSelectAll = (e: { target?: { checked?: boolean } }) => {
  emit('toggle-select-all', Boolean(e?.target?.checked))
}
</script>

<style scoped>
.list-overview {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
  padding: 10px 14px;
  border-radius: 3px;
  background: var(--bg-card);
  border: 1px solid var(--border-default);
}

.overview-main {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.overview-icon {
  width: 32px;
  height: 32px;
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 15px;
  background: var(--primary-gradient);
  box-shadow: 0 6px 14px color-mix(in srgb, var(--primary) 22%, transparent);
}

.overview-desc {
  font-size: 11px;
  line-height: 1.45;
  color: var(--text-secondary);
}

.overview-actions {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

@media (max-width: 560px) {
  .list-overview {
    flex-direction: column;
    align-items: flex-start;
  }

  .overview-actions {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
