<template>
  <aside class="node-palette">
    <div class="palette-head">
      <h3>节点库</h3>
      <a-input v-model="keyword" allow-clear size="small" placeholder="搜索节点" />
    </div>

    <div class="palette-body">
      <section v-for="group in filteredGroups" :key="group.key" class="group-block">
        <h4>{{ group.title }}</h4>
        <div class="node-list">
          <button
            v-for="item in group.items"
            :key="item.type"
            type="button"
            class="node-item"
            :class="{ pinned: pinnedTypes.includes(item.type) }"
            draggable="true"
            @dragstart="onDragStart($event, item.type)"
            @contextmenu.prevent="onItemContextmenu($event, item)"
          >
            <span class="item-title">{{ item.label }}</span>
            <span class="item-desc">{{ item.description }}</span>
          </button>
        </div>
      </section>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { paletteGroups } from '../constants'
import { useNodeDnD } from '../composables/useNodeDnD'
import type { NodePaletteItem, PaletteContextMenuPayload, WorkflowNodeType } from '../types'

const keyword = ref('')
const { startDrag } = useNodeDnD()
const pinnedTypes = ref<WorkflowNodeType[]>([])
const PIN_STORAGE_KEY = 'workflow-node-pins-v1'

const emit = defineEmits<{
  'palette-contextmenu': [payload: PaletteContextMenuPayload]
}>()

const props = defineProps<{
  pinRefreshToken?: number
}>()

const filteredGroups = computed(() => {
  const text = keyword.value.trim().toLowerCase()
  const groups = !text
    ? paletteGroups
    : paletteGroups
        .map((group) => ({
          ...group,
          items: group.items.filter(
            (item) => item.label.toLowerCase().includes(text) || item.description.toLowerCase().includes(text)
          )
        }))
        .filter((group) => group.items.length > 0)

  return groups.map((group) => ({
    ...group,
    items: [...group.items].sort((a, b) => Number(pinnedTypes.value.includes(b.type)) - Number(pinnedTypes.value.includes(a.type)))
  }))
})

const onDragStart = (ev: DragEvent, type: WorkflowNodeType) => {
  startDrag(ev, type)
}

const onItemContextmenu = (ev: MouseEvent, item: NodePaletteItem) => {
  emit('palette-contextmenu', {
    x: ev.clientX,
    y: ev.clientY,
    type: item.type,
    label: item.label,
    description: item.description
  })
}

const loadPinned = () => {
  try {
    const parsed = JSON.parse(localStorage.getItem(PIN_STORAGE_KEY) || '[]') as WorkflowNodeType[]
    pinnedTypes.value = Array.isArray(parsed) ? parsed : []
  } catch {
    pinnedTypes.value = []
  }
}

loadPinned()

watch(
  () => props.pinRefreshToken,
  () => loadPinned()
)
</script>

<style scoped>
.node-palette {
  height: 100%;
  border: 1px solid #edf1f6;
  border-radius: 12px;
  background: #fff;
  display: flex;
  flex-direction: column;
}

.palette-head {
  padding: 12px;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.palette-head h3 {
  margin: 0;
  font-size: 14px;
  color: #1e293b;
}

.palette-body {
  padding: 12px;
  overflow: auto;
}

.group-block + .group-block {
  margin-top: 14px;
}

.group-block h4 {
  margin: 0 0 8px 0;
  color: #64748b;
  font-size: 12px;
}

.node-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.node-item {
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 10px;
  background: #fff;
  text-align: left;
  cursor: grab;
}

.node-item:hover {
  border-color: #91caff;
  background: #f8fbff;
}

.node-item.pinned {
  border-color: #93c5fd;
  background: #eff6ff;
}

.item-title {
  display: block;
  color: #1e293b;
  font-size: 13px;
  font-weight: 600;
}

.item-desc {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: #64748b;
}
</style>
