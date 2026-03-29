<template>
  <div class="slot-wrap">
    <div class="slot-label-row">
      <component :is="icon" class="slot-icon" />
      <span class="slot-title">{{ title }}</span>
    </div>
    <p v-if="hint" class="slot-hint">{{ hint }}</p>
    <div
      ref="zoneRef"
      class="assembly-drop-zone"
      :class="[
        variant,
        {
          'is-eligible': eligible,
          'is-over': isOver,
          'has-content': hasContent
        }
      ]"
      @dragover="onDragOver"
      @dragleave="onDragLeave"
      @drop="onDrop"
    >
      <div v-if="eligible && draggingPayload && isOver" class="drop-hint-banner">
        <span class="banner-dot" />
        松开鼠标以放入
      </div>
      <div class="zone-inner">
        <slot />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, type Component } from 'vue'
import type { AssemblyDragPayload, AssemblySlotKey } from './assemblyTypes'
import { ASSEMBLY_DRAG_MIME, parseDragPayload, payloadAcceptsSlot } from './assemblyTypes'

const props = defineProps<{
  slotKey: AssemblySlotKey
  title: string
  hint?: string
  variant: 'chat' | 'emb' | 'img' | 'tool' | 'mcp'
  icon: Component
  draggingPayload: AssemblyDragPayload | null
  /** 当前指针悬停的投放区（由父级在 dragover 时更新） */
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
  margin-bottom: 4px;
  min-width: 0;
  max-width: 100%;
}
.slot-label-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.slot-icon {
  font-size: 16px;
  opacity: 0.88;
}
.slot-title {
  font-size: 13px;
  font-weight: 600;
  color: #434343;
}
.slot-hint {
  margin: 0 0 8px;
  font-size: 12px;
  color: #8c8c8c;
  line-height: 1.4;
}

.assembly-drop-zone {
  box-sizing: border-box;
  position: relative;
  max-width: 100%;
  min-height: 120px;
  border-radius: 14px;
  border: 2px dashed #d9d9d9;
  background: rgba(255, 255, 255, 0.92);
  padding: 12px;
  overflow: hidden;
  transition:
    border-color 0.18s ease,
    background 0.18s ease,
    box-shadow 0.18s ease;
}

.assembly-drop-zone.is-eligible {
  border-color: #91caff;
  background: #fafafa;
  box-shadow: inset 0 0 0 1px rgba(24, 144, 255, 0.12);
}

.assembly-drop-zone.chat.is-eligible {
  border-color: #69b1ff;
  background: #f0f9ff;
  box-shadow: inset 0 0 0 1px rgba(24, 144, 255, 0.2);
}

.assembly-drop-zone.emb.is-eligible {
  border-color: #b37feb;
  background: #faf5ff;
  box-shadow: inset 0 0 0 1px rgba(114, 46, 209, 0.18);
}

.assembly-drop-zone.img.is-eligible {
  border-color: #ffc069;
  background: #fffbf0;
  box-shadow: inset 0 0 0 1px rgba(250, 140, 22, 0.2);
}

.assembly-drop-zone.tool.is-eligible,
.assembly-drop-zone.mcp.is-eligible {
  border-color: #95de64;
  background: #f6ffed;
  box-shadow: inset 0 0 0 1px rgba(82, 196, 26, 0.18);
}

.assembly-drop-zone.is-over {
  border-style: solid;
}

/* 悬停高亮：避免 transform 与过大外扩 shadow 导致横向撑出滚动条 */
.assembly-drop-zone.chat.is-over {
  border-color: #1890ff;
  background: #e6f7ff;
  box-shadow: inset 0 0 0 1px rgba(24, 144, 255, 0.35);
}

.assembly-drop-zone.emb.is-over {
  border-color: #722ed1;
  background: #f9f0ff;
  box-shadow: inset 0 0 0 1px rgba(114, 46, 209, 0.35);
}

.assembly-drop-zone.img.is-over {
  border-color: #fa8c16;
  background: #fff7e6;
  box-shadow: inset 0 0 0 1px rgba(250, 140, 22, 0.35);
}

.assembly-drop-zone.tool.is-over,
.assembly-drop-zone.mcp.is-over {
  border-color: #52c41a;
  background: #f6ffed;
  box-shadow: inset 0 0 0 1px rgba(82, 196, 26, 0.35);
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
  color: #0061ff;
  background: linear-gradient(90deg, transparent, rgba(0, 97, 255, 0.08), transparent);
  border-radius: 10px 10px 0 0;
  pointer-events: none;
}

.banner-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #0061ff;
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
  min-height: 88px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0;
}
</style>
