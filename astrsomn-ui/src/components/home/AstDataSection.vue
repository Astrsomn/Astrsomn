<script lang="ts" setup>
</script>
<template>
  <div class="data-section">

    <section v-if="$slots.toolbar" class="data-section-toolbar">
      <slot name="toolbar"/>
    </section>

    <section v-if="$slots.overview" class="data-section-overview">
      <slot name="overview"/>
    </section>


    <section class="data-section-content">
      <slot/>
    </section>

    <section v-if="$slots.pagination" class="data-section-pagination">
      <slot name="pagination"/>
    </section>
  </div>
</template>

<style scoped>
.data-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.data-section-toolbar,
.data-section-overview,
.data-section-content,
.data-section-pagination {
  width: 100%;
}


</style>
