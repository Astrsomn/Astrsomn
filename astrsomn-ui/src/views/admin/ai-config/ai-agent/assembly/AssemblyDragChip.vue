<template>
  <div
      class="assembly-drag-chip"
      draggable="true"
      @dragend="onEnd"
      @dragstart="onStart"
  >
    <span v-if="badge" class="chip-badge">{{ badge }}</span>
    <span class="chip-title">{{ title }}</span>
    <span v-if="subtitle" class="chip-sub">{{ subtitle }}</span>
  </div>
</template>

<script lang="ts" setup>
import type {AssemblyDragPayload} from './assemblyTypes'
import {ASSEMBLY_DRAG_MIME} from './assemblyTypes'

const props = defineProps<{
  payload: AssemblyDragPayload
  title: string
  subtitle?: string
  badge?: string
}>()

const emit = defineEmits<{
  dragStart: [payload: AssemblyDragPayload]
  dragEnd: []
}>()

function onStart(ev: DragEvent) {
  const raw = JSON.stringify(props.payload)
  ev.dataTransfer!.effectAllowed = 'copy'
  ev.dataTransfer!.setData(ASSEMBLY_DRAG_MIME, raw)
  try {
    ev.dataTransfer!.setData('text/plain', props.subtitle || props.title)
  } catch {
    /* ignore */
  }
  emit('dragStart', props.payload)
}

function onEnd() {
  emit('dragEnd')
}
</script>

<style scoped>
.assembly-drag-chip {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
  padding: 10px 12px;
  border-radius: 14px;
  border: 1px solid var(--assembly-chip-border);
  background: var(--assembly-chip-bg);
  cursor: grab;
  user-select: none;
  transition: border-color 0.15s,
  box-shadow 0.15s,
  transform 0.15s;
}

.assembly-drag-chip:hover {
  border-color: var(--assembly-chip-hover-border);
  box-shadow: var(--assembly-chip-hover-shadow);
}

.assembly-drag-chip:active {
  cursor: grabbing;
}

.chip-badge {
  font-size: 11px;
  font-weight: 600;
  padding: 2px 10px;
  border-radius: 8px;
  background: var(--assembly-chip-badge-bg);
  color: var(--assembly-chip-badge-text);
}

.chip-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--assembly-chip-title);
  word-break: break-all;
}

.chip-sub {
  font-size: 11px;
  color: var(--assembly-chip-sub);
  font-family: ui-monospace, monospace;
  word-break: break-all;
}
</style>
