<template>
  <div class="slot-wrap">
    <div class="slot-label-row">
      <component :is="icon" class="slot-icon"/>
      <span class="slot-title">{{ title }}</span>
    </div>
    <p v-if="hint" class="slot-hint">{{ hint }}</p>
    <div
        ref="zoneRef"
        :class="[
        variant,
        {
          'is-eligible': eligible,
          'is-over': isOver,
          'has-content': hasContent
        }
      ]"
        class="assembly-drop-zone"
        @dragleave="onDragLeave"
        @dragover="onDragOver"
        @drop="onDrop"
    >
      <div v-if="eligible && draggingPayload && isOver" class="drop-hint-banner">
        <span class="banner-dot"/>
        松开鼠标以放入
      </div>
      <div class="zone-inner">
        <slot/>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {type Component, computed, ref} from 'vue'
import type {AssemblyDragPayload, AssemblySlotKey} from './assemblyTypes'
import {ASSEMBLY_DRAG_MIME, parseDragPayload, payloadAcceptsSlot} from './assemblyTypes'

const props = defineProps<{
  slotKey: AssemblySlotKey
  title: string
  hint?: string
  variant: 'chat' | 'emb' | 'img' | 'tool' | 'mcp' | 'kb'
  icon: Component
  draggingPayload: AssemblyDragPayload | null

  activeDropKey: AssemblySlotKey | null
  hasContent?: boolean
}>()

const emit = defineEmits<{
  drop: [payload: AssemblyDragPayload]
  hover: [key: AssemblySlotKey | null]
}>()

const zoneRef = ref<HTMLElement | null>(null)

const eligible = computed(() => payloadAcceptsSlot(props.draggingPayload, props.slotKey))

const isOver = computed(
    () => eligible.value && props.activeDropKey === props.slotKey
)

function onDragOver(ev: DragEvent) {
  if (!eligible.value) {
    ev.dataTransfer!.dropEffect = 'none'
    return
  }
  ev.preventDefault()
  ev.dataTransfer!.dropEffect = 'copy'
  emit('hover', props.slotKey)
}

function onDragLeave(ev: DragEvent) {
  const next = ev.relatedTarget as Node | null
  const el = zoneRef.value
  if (el && next && el.contains(next)) return
  emit('hover', null)
}

function onDrop(ev: DragEvent) {
  ev.preventDefault()
  emit('hover', null)
  const raw = ev.dataTransfer?.getData(ASSEMBLY_DRAG_MIME)
  const p = parseDragPayload(raw || '')
  if (!p || !payloadAcceptsSlot(p, props.slotKey)) return
  emit('drop', p)
}
</script>

<style scoped>
.slot-wrap {
  margin-bottom: 2px;
  min-width: 0;
  max-width: 100%;
}

.slot-label-row {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 2px;
}

.slot-icon {
  font-size: 14px;
  opacity: 0.88;
}

.slot-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-primary);
}

.slot-hint {
  margin: 0 0 4px;
  font-size: 12px;
  color: var(--text-muted);
  line-height: 1.4;
}

.assembly-drop-zone {
  box-sizing: border-box;
  position: relative;
  max-width: 100%;
  min-height: 88px;
  border-radius: 12px;
  border: 2px dashed var(--assembly-dz-border);
  background: var(--assembly-dz-bg);
  padding: 8px;
  overflow: hidden;
  transition: border-color 0.18s ease,
  background 0.18s ease,
  box-shadow 0.18s ease;
}

.assembly-drop-zone.is-eligible {
  border-color: var(--assembly-dz-eligible-border);
  background: var(--assembly-dz-eligible-bg);
  box-shadow: inset 0 0 0 1px var(--assembly-dz-eligible-inset);
}

.assembly-drop-zone.chat.is-eligible {
  border-color: var(--assembly-dz-chat-eligible-border);
  background: var(--assembly-dz-chat-eligible-bg);
  box-shadow: inset 0 0 0 1px var(--assembly-dz-chat-eligible-inset);
}

.assembly-drop-zone.emb.is-eligible {
  border-color: var(--assembly-dz-emb-eligible-border);
  background: var(--assembly-dz-emb-eligible-bg);
  box-shadow: inset 0 0 0 1px var(--assembly-dz-emb-eligible-inset);
}

.assembly-drop-zone.img.is-eligible {
  border-color: var(--assembly-dz-img-eligible-border);
  background: var(--assembly-dz-img-eligible-bg);
  box-shadow: inset 0 0 0 1px var(--assembly-dz-img-eligible-inset);
}

.assembly-drop-zone.tool.is-eligible,
.assembly-drop-zone.mcp.is-eligible {
  border-color: var(--assembly-dz-tool-eligible-border);
  background: var(--assembly-dz-tool-eligible-bg);
  box-shadow: inset 0 0 0 1px var(--assembly-dz-tool-eligible-inset);
}

.assembly-drop-zone.kb.is-eligible {
  border-color: var(--assembly-dz-kb-eligible-border);
  background: var(--assembly-dz-kb-eligible-bg);
  box-shadow: inset 0 0 0 1px var(--assembly-dz-kb-eligible-inset);
}

.assembly-drop-zone.is-over {
  border-style: solid;
}


.assembly-drop-zone.chat.is-over {
  border-color: var(--assembly-dz-chat-over-border);
  background: var(--assembly-dz-chat-over-bg);
  box-shadow: inset 0 0 0 1px var(--assembly-dz-chat-over-inset);
}

.assembly-drop-zone.emb.is-over {
  border-color: var(--assembly-dz-emb-over-border);
  background: var(--assembly-dz-emb-over-bg);
  box-shadow: inset 0 0 0 1px var(--assembly-dz-emb-over-inset);
}

.assembly-drop-zone.img.is-over {
  border-color: var(--assembly-dz-img-over-border);
  background: var(--assembly-dz-img-over-bg);
  box-shadow: inset 0 0 0 1px var(--assembly-dz-img-over-inset);
}

.assembly-drop-zone.tool.is-over,
.assembly-drop-zone.mcp.is-over {
  border-color: var(--assembly-dz-tool-over-border);
  background: var(--assembly-dz-tool-over-bg);
  box-shadow: inset 0 0 0 1px var(--assembly-dz-tool-over-inset);
}

.assembly-drop-zone.kb.is-over {
  border-color: var(--assembly-dz-kb-over-border);
  background: var(--assembly-dz-kb-over-bg);
  box-shadow: inset 0 0 0 1px var(--assembly-dz-kb-over-inset);
}

.assembly-drop-zone.has-content {
  border-style: solid;
}

.drop-hint-banner {
  position: absolute;
  top: -1px;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 4px 8px;
  font-size: 12px;
  font-weight: 600;
  color: var(--assembly-dz-hint-text);
  background: var(--assembly-dz-hint-bg);
  border-radius: 10px 10px 0 0;
  pointer-events: none;
}

.banner-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--assembly-dz-hint-dot);
  animation: pulse 1s ease-in-out infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(0.85);
  }
}

.zone-inner {
  min-height: 56px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0;
}
</style>
