<template>
  <div class="workflow-list-panel">
    <div class="panel-head">
      <AstSearchInput
          v-model="keyword"
          class="panel-search"
          layout="fluid"
          placeholder="搜索流程..."
      />
    </div>

    <div v-if="!filteredItems.length" class="empty-tip">暂无流程，请先创建流程。</div>

    <div v-else class="category-list">
      <div v-for="group in groupedItems" :key="group.category" class="category-block">
        <button class="category-title" type="button" @click="toggleCategory(group.category)">
          <span class="arrow">{{ expandedCategories[group.category] ? '⌄' : '›' }}</span>
          <span class="name">{{ group.category }}</span>
          <span class="count">{{ group.items.length }}</span>
        </button>
        <div v-show="expandedCategories[group.category]" class="workflow-list">
          <button
              v-for="item in group.items"
              :key="item.id"
              :class="{ active: item.id === activeWorkflowId }"
              class="workflow-item"
              type="button"
              @click="$emit('select', item)"
          >
            <span :class="{ active: item.id === activeWorkflowId }" class="item-dot"></span>
            <div class="workflow-main">
              <span class="workflow-name">{{ item.workflowName }}</span>
              <span class="workflow-key">
                <span class="workflow-key-text">{{ item.workflowKey || '-' }}</span>
                <a-tooltip title="复制 Flow Key">
                  <button class="icon-action key-copy" type="button" @click.stop="copyWorkflowKey(item.workflowKey)">
                    <CopyOutlined/>
                  </button>
                </a-tooltip>
              </span>
            </div>
            <div class="workflow-actions">
              <a-tooltip title="编辑">
                <button class="icon-action" type="button" @click.stop="openEditDialog(item)">
                  <EditOutlined/>
                </button>
              </a-tooltip>
              <a-popconfirm cancel-text="取消" ok-text="删除" title="确认删除当前流程？" @confirm="onDelete(item)">
                <a-tooltip title="删除">
                  <button class="icon-action danger" type="button" @click.stop>
                    <DeleteOutlined/>
                  </button>
                </a-tooltip>
              </a-popconfirm>
            </div>
          </button>
        </div>
      </div>
    </div>

    <a-modal
        :confirm-loading="creating"
        :visible="dialogVisible"
        title="新建流程"
        @ok="onConfirmCreate"
        @update:visible="onCreateDialogVisibleChange"
    >
      <a-form layout="vertical">
        <a-form-item label="流程名称" required>
          <a-input :value="form.workflowName" maxlength="128" @update:value="onCreateNameChange"/>
        </a-form-item>
        <a-form-item label="Flow Key" required>
          <a-input :value="form.workflowKey" maxlength="128" @update:value="onCreateKeyChange"/>
        </a-form-item>
        <a-form-item label="业务分类" required>
          <a-input :value="form.category" maxlength="128" placeholder="例如：审批流程、客服流程"
                   @update:value="onCreateCategoryChange"/>
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
        :confirm-loading="editing"
        :visible="editDialogVisible"
        title="编辑流程"
        @ok="onConfirmEdit"
        @update:visible="onEditDialogVisibleChange"
    >
      <a-form layout="vertical">
        <a-form-item label="流程名称" required>
          <a-input :value="editForm.workflowName" maxlength="128" @update:value="onEditNameChange"/>
        </a-form-item>
        <a-form-item label="Flow Key" required>
          <a-input :value="editForm.workflowKey" maxlength="128" @update:value="onEditKeyChange"/>
        </a-form-item>
        <a-form-item label="业务分类" required>
          <a-input :value="editForm.category" maxlength="128" placeholder="例如：审批流程、客服流程"
                   @update:value="onEditCategoryChange"/>
        </a-form-item>
      </a-form>
    </a-modal>

    <div class="panel-foot">
      <a-button block class="create-btn" @click="dialogVisible = true">
        <PlusOutlined/>
        新建流程
      </a-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {message} from 'ant-design-vue'
import {CopyOutlined, DeleteOutlined, EditOutlined, PlusOutlined} from '@ant-design/icons-vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import type {WorkflowListItem} from '../../../domain/types'

const props = defineProps<{
  items: WorkflowListItem[]
  activeWorkflowId?: string
  openCreateDialogTick?: number
}>()

const emit = defineEmits<{
  select: [item: WorkflowListItem]
  create: [payload: { workflowName: string; workflowKey: string; category: string }]
  edit: [payload: { item: WorkflowListItem; payload: { workflowName: string; workflowKey: string; category: string } }]
  delete: [item: WorkflowListItem]
}>()

const dialogVisible = ref(false)
const creating = ref(false)
const form = reactive({
  workflowName: '',
  workflowKey: '',
  category: ''
})
const expandedCategories = ref<Record<string, boolean>>({})
const editDialogVisible = ref(false)
const editing = ref(false)
const editingItem = ref<WorkflowListItem>()
const editForm = reactive({
  workflowName: '',
  workflowKey: '',
  category: ''
})
const keyword = ref('')

const filteredItems = computed(() => {
  const term = keyword.value.trim().toLowerCase()
  if (!term) return props.items
  return props.items.filter((item) => {
    const name = (item.workflowName || '').toLowerCase()
    const key = (item.workflowKey || '').toLowerCase()
    const category = (item.category || item.description || '').toLowerCase()
    return name.includes(term) || key.includes(term) || category.includes(term)
  })
})

const groupedItems = computed(() => {
  const map = new Map<string, WorkflowListItem[]>()
  for (const item of filteredItems.value) {
    const category = item.category || item.description || '未分类'
    if (!map.has(category)) map.set(category, [])
    map.get(category)!.push(item)
  }
  return Array.from(map.entries()).map(([category, list]) => ({
    category,
    items: list
  }))
})

watch(
    groupedItems,
    (groups) => {
      const next: Record<string, boolean> = {}
      for (const group of groups) {
        next[group.category] = expandedCategories.value[group.category] ?? true
      }
      expandedCategories.value = next
    },
    {immediate: true}
)

watch(
    () => props.openCreateDialogTick,
    (tick) => {
      if (!tick) return
      dialogVisible.value = true
    }
)

const toggleCategory = (category: string) => {
  expandedCategories.value[category] = !expandedCategories.value[category]
}

const resetForm = () => {
  form.workflowName = ''
  form.workflowKey = ''
  form.category = ''
}

const onConfirmCreate = async () => {
  if (!form.workflowName.trim() || !form.workflowKey.trim() || !form.category.trim()) {
    message.warning('请填写流程名称、Flow Key 和业务分类')
    return
  }
  creating.value = true
  try {
    emit('create', {
      workflowName: form.workflowName.trim(),
      workflowKey: form.workflowKey.trim(),
      category: form.category.trim()
    })
    dialogVisible.value = false
    resetForm()
  } finally {
    creating.value = false
  }
}

const onCreateDialogVisibleChange = (value: boolean) => {
  dialogVisible.value = value
}

const onCreateNameChange = (value: string) => {
  form.workflowName = value || ''
}

const onCreateKeyChange = (value: string) => {
  form.workflowKey = value || ''
}

const onCreateCategoryChange = (value: string) => {
  form.category = value || ''
}

const openEditDialog = (item: WorkflowListItem) => {
  editingItem.value = item
  editForm.workflowName = item.workflowName || ''
  editForm.workflowKey = item.workflowKey || ''
  editForm.category = item.category || item.description || ''
  editDialogVisible.value = true
}

const onConfirmEdit = async () => {
  if (!editingItem.value) return
  if (!editForm.workflowName.trim() || !editForm.workflowKey.trim() || !editForm.category.trim()) {
    message.warning('请填写流程名称、Flow Key 和业务分类')
    return
  }
  editing.value = true
  try {
    emit('edit', {
      item: editingItem.value,
      payload: {
        workflowName: editForm.workflowName.trim(),
        workflowKey: editForm.workflowKey.trim(),
        category: editForm.category.trim()
      }
    })
    editDialogVisible.value = false
  } finally {
    editing.value = false
  }
}

const onEditDialogVisibleChange = (value: boolean) => {
  editDialogVisible.value = value
}

const onEditNameChange = (value: string) => {
  editForm.workflowName = value || ''
}

const onEditKeyChange = (value: string) => {
  editForm.workflowKey = value || ''
}

const onEditCategoryChange = (value: string) => {
  editForm.category = value || ''
}

const onDelete = (item: WorkflowListItem) => {
  emit('delete', item)
}

const copyWorkflowKey = async (workflowKey?: string) => {
  const text = (workflowKey || '').trim()
  if (!text) {
    message.warning('当前 Flow Key 为空')
    return
  }
  try {
    if (navigator?.clipboard?.writeText) {
      await navigator.clipboard.writeText(text)
    } else {
      const temp = document.createElement('textarea')
      temp.value = text
      temp.style.position = 'fixed'
      temp.style.opacity = '0'
      document.body.appendChild(temp)
      temp.focus()
      temp.select()
      document.execCommand('copy')
      document.body.removeChild(temp)
    }
    message.success('Flow Key 已复制')
  } catch {
    message.warning('复制失败，请手动复制')
  }
}
</script>

<style scoped>
.workflow-list-panel {
  height: 100%;
  padding: 0;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.panel-head {
  padding: 14px 14px 12px;
  border-bottom: 1px solid #eef2f7;
  display: flex;
  flex-direction: column;
  gap: 0;
}

.panel-search {
  height: 42px;
  border-radius: 10px;
}

.panel-search :deep(.toolbar-search-pill__input) {
  font-size: 13px;
}

.empty-tip {
  margin: 12px;
  border: 1px dashed #dbe4ef;
  border-radius: 10px;
  padding: 14px;
  color: #7b8aa0;
  font-size: 12px;
}

.category-list {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding: 8px 8px 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.category-block {
  border-radius: 10px;
  background: transparent;
  overflow: hidden;
}

.category-title {
  width: 100%;
  border: 0;
  background: transparent;
  padding: 8px 10px;
  display: flex;
  align-items: center;
  gap: 5px;
  text-align: left;
  cursor: pointer;
  color: #95a2b3;
}

.category-title .arrow {
  width: 10px;
  font-size: 12px;
  color: #9ba9ba;
}

.category-title .name {
  flex: 1;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.category-title .count {
  min-width: 18px;
  height: 18px;
  border-radius: 9px;
  font-size: 10px;
  color: #97a6b8;
  background: #f1f5f9;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.workflow-list {
  padding: 0 4px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.workflow-item {
  border: 1px solid transparent;
  border-radius: 8px;
  background: #ffffff;
  padding: 8px 9px;
  text-align: left;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 9px;
  transition: all 0.2s ease;
}

.workflow-item:hover {
  border-color: #e4ebf4;
  background: #f8fafd;
}

.workflow-item.active {
  border-color: #d8e6fa;
  background: #eef5ff;
}

.item-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #c7d2e0;
  flex: 0 0 auto;
  margin-top: 1px;
}

.item-dot.active {
  background: #4f9dff;
}

.workflow-name {
  display: block;
  color: #4b5a6d;
  font-size: 13px;
  font-weight: 500;
}

.workflow-key {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
  font-size: 12px;
  color: #98a7b9;
}

.workflow-key-text {
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.workflow-main {
  flex: 1;
  min-width: 0;
}

.workflow-actions {
  display: flex;
  align-items: center;
  gap: 2px;
}

.icon-action {
  width: 22px;
  height: 22px;
  border: 0;
  border-radius: 6px;
  background: transparent;
  color: #aeb9c8;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.15s ease;
}

.icon-action:hover {
  color: #7e8ea3;
  background: #edf2f8;
}

.icon-action.danger:hover {
  color: #c57e7e;
  background: #faf0f0;
}

.key-copy {
  width: 18px;
  height: 18px;
  border-radius: 4px;
  color: #9aa8bc;
}

.key-copy:hover {
  color: #7d8fa7;
  background: #edf2f8;
}

.panel-foot {
  padding: 12px 14px;
  border-top: 1px solid #eef2f7;
  background: #f8fafc;
}

.create-btn {
  height: 34px;
  border-radius: 10px;
  border: 1px solid #dbe3ee;
  color: #64748b;
  background: #ffffff;
  font-size: 13px;
  font-weight: 500;
}

.create-btn:hover {
  border-color: #bad6f7;
  color: #4f8fd8;
}
</style>
