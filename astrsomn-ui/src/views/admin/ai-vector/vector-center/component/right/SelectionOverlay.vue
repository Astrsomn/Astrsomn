<template>
  <div v-if="rect" class="selection-rect" :style="style"/>
</template>

<script lang="ts" setup>
import {computed} from 'vue';

const props = defineProps<{
  rect: { x: number; y: number; w: number; h: number } | null
}>()

const style = computed(() => {
  if (!props.rect) return {}
  return {
    left: props.rect.x + 'px',
    top: props.rect.y + 'px',
    width: props.rect.w + 'px',
    height: props.rect.h + 'px'
  }
})
</script>

<style scoped>
.selection-rect {
  position: absolute;
  background: rgba(59, 130, 246, 0.12);
  border: 1px solid rgba(59, 130, 246, 0.5);
  border-radius: 2px;
  pointer-events: none;
  z-index: 50;
}
</style>
