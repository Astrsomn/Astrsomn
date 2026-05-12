<template>
  <div class="admin-list-toolbar">
    <div class="toolbar-main">
      <div class="toolbar-left">
        <slot name="left" />
      </div>
      <div class="toolbar-right">
        <slot name="right" />
      </div>
    </div>
    <div v-if="$slots.extra" class="toolbar-extra">
      <slot name="extra" />
    </div>
  </div>
</template>

<style scoped>
.admin-list-toolbar {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 6px;
  padding: 16px 0;
  border-radius: var(--radius-sm);
}

.toolbar-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  flex-wrap: wrap;
}

.toolbar-left,
.toolbar-right,
.toolbar-extra {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.toolbar-left {
  flex: 1;
}

.toolbar-extra {
  width: 100%;
  padding-top: 4px;
}

@media (max-width: 720px) {
  .admin-list-toolbar {
    padding: 14px;
  }

  .toolbar-left,
  .toolbar-right,
  .toolbar-extra {
    width: 100%;
  }
}
</style>
<script setup lang="ts">
</script>