<template>
  <div
    class="document-list-container"
    @contextmenu.prevent="onBlankContextMenu"
    @dragover.prevent="onDragOver"
    @dragenter.prevent="onDragEnter"
    @dragleave="onDragLeave"
    @drop.prevent="onDrop"
  >
    <!-- 列表视图 -->
    <div v-if="viewSize === 'list'" class="file-list">
      <div class="file-list-header">
        <span class="col-name">{{ t.vectorCenter.rightCenter.name }}</span>
        <span class="col-size">{{ t.vectorCenter.rightCenter.size }}</span>
        <span class="col-status">{{ t.vectorCenter.rightCenter.status }}</span>
        <span class="col-time">{{ t.vectorCenter.docFileCard.updateTime }}</span>
        <span class="col-actions">{{ t.vectorCenter.rightCenter.actions }}</span>
      </div>
      <a-dropdown v-for="folder in folders" :key="folder.id" :trigger="['contextmenu']">
        <div
            :data-select-id="String(folder.id)"
            :class="{ selected: isSelected(String(folder.id)) }"
            class="file-list-row folder-row"
            @click="handleFolderEnter(folder, $event)"
            @contextmenu.prevent
        >
          <span class="col-name">
            <folder-outlined class="row-icon folder-icon"/>
            <span v-if="renamingFolderId === folder.id" class="inline-edit">
              <input
                  :value="folder.folderName"
                  @blur="handleRenameCancel"
                  @keydown.enter="onListRenameConfirm($event, folder)"
                  @keydown.escape="handleRenameCancel"
                  @click.stop
              />
            </span>
            <span v-else class="row-title">{{ folder.folderName }}</span>
          </span>
          <span class="col-size">—</span>
          <span class="col-status">—</span>
          <span class="col-time">—</span>
          <span class="col-actions">
            <a-button size="small" type="text" @click.stop="openRenameFolder(folder)"><edit-outlined/></a-button>
            <a-button size="small" type="text" danger @click.stop="handleDeleteFolder(folder)"><delete-outlined/></a-button>
          </span>
        </div>
        <template #overlay>
          <a-menu @click="onFolderMenuClick($event, folder)">
            <a-menu-item key="open"><template #icon><folder-outlined/></template>{{ t.vectorCenter.rightCenter.open }}</a-menu-item>
            <a-menu-item key="rename"><template #icon><edit-outlined/></template>{{ t.vectorCenter.rightCenter.rename }}</a-menu-item>
            <a-menu-divider/>
            <a-menu-item key="paste"><template #icon><snippets-outlined/></template>{{ t.vectorCenter.rightCenter.pasteHere }}</a-menu-item>
            <a-menu-divider/>
            <a-menu-item key="delete" class="danger-item"><template #icon><delete-outlined/></template>{{ t.vectorCenter.rightCenter.delete }}</a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
      <a-dropdown v-for="file in filteredFiles" :key="file.id || file.name" :trigger="['contextmenu']">
        <div
            :data-select-id="String(file.id)"
            :class="{
              selected: isSelected(String(file.id)),
              active: String(file.id) === String(props.selectedDocId ?? ''),
              cut: clipboard?.mode === 'cut' && clipboard.items.some(c => String(c.id) === String(file.id)),
            }"
            class="file-list-row file-row"
            @click="handleSelectDoc(file, $event)"
            @contextmenu.prevent
        >
          <span class="col-name">
            <component :is="getFileIcon(file.name)" class="row-icon file-icon"/>
            <span class="row-title">{{ file.name }}</span>
          </span>
          <span class="col-size">{{ file.size }}</span>
          <span class="col-status">
            <span :class="['status-tag', file.statusCode?.toLowerCase()]">{{ file.status }}</span>
          </span>
          <span class="col-time">{{ file.uploadTime }}</span>
          <span class="col-actions">
            <a-button size="small" type="text" @click.stop="openRenameDoc(file)"><edit-outlined/></a-button>
            <a-button size="small" type="text" danger @click.stop="handleDelete(file)"><delete-outlined/></a-button>
          </span>
        </div>
        <template #overlay>
          <a-menu @click="onFileMenuClick($event, file)">
            <a-menu-item key="edit"><template #icon><edit-outlined/></template>{{ t.vectorCenter.rightCenter.edit }}</a-menu-item>
            <a-menu-item v-if="file.statusCode === 'PENDING' || file.statusCode === 'FAILED'" key="chunk"><template #icon><block-outlined/></template>{{ t.vectorCenter.rightCenter.chunk }}</a-menu-item>
            <a-menu-item v-if="file.statusCode === 'CHUNKED'" key="vectorize"><template #icon><experiment-outlined/></template>{{ t.vectorCenter.rightCenter.vectorize }}</a-menu-item>
            <a-menu-item v-if="file.statusCode === 'CHUNKED' || file.statusCode === 'STORED' || file.statusCode === 'FAILED'" key="re-chunk"><template #icon><block-outlined/></template>{{ t.vectorCenter.rightCenter.reChunk }}</a-menu-item>
            <a-menu-item v-if="file.statusCode === 'STORED' || file.statusCode === 'FAILED'" key="re-vectorize"><template #icon><sync-outlined/></template>{{ t.vectorCenter.rightCenter.reVectorize }}</a-menu-item>
            <a-menu-divider/>
            <a-menu-item key="move"><template #icon><folder-outlined/></template>{{ t.vectorCenter.rightCenter.moveToFolder }}</a-menu-item>
            <a-menu-item key="copy"><template #icon><copy-outlined/></template>{{ t.vectorCenter.rightCenter.copy }}</a-menu-item>
            <a-menu-item key="cut"><template #icon><scissor-outlined/></template>{{ t.vectorCenter.rightCenter.cut }}</a-menu-item>
            <a-menu-divider/>
            <a-menu-item key="delete" class="danger-item"><template #icon><delete-outlined/></template>{{ t.vectorCenter.rightCenter.delete }}</a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
    </div>

    <!-- 网格视图 -->
    <div v-else ref="gridRef" :class="`file-grid size-${viewSize}`" @mousedown="onBoxSelectMouseDown">
      <SelectionOverlay :rect="selectionRect"/>
      <a-dropdown v-for="folder in folders" :key="folder.id" :trigger="['contextmenu']">
        <DocFolderCard
            :data-select-id="String(folder.id)"
            :folder="folder"
            :size="viewSize"
            :editing="renamingFolderId === folder.id || isNewFolderPlaceholder(folder)"
            :placeholder="isNewFolderPlaceholder(folder) ? t.vectorCenter.rightCenter.newFolderPlaceholder : undefined"
            :selected="isSelected(String(folder.id))"
            @delete="handleDeleteFolder"
            @enter="handleFolderEnter"
            @rename="openRenameFolder"
            @rename-confirm="handleRenameConfirm"
            @rename-cancel="handleRenameCancel"
        />
        <template #overlay>
          <a-menu @click="onFolderMenuClick($event, folder)">
            <a-menu-item key="open">
              <template #icon><folder-outlined/></template>
              {{ t.vectorCenter.rightCenter.open }}
            </a-menu-item>
            <a-menu-item key="rename">
              <template #icon><edit-outlined/></template>
              {{ t.vectorCenter.rightCenter.rename }}
            </a-menu-item>
            <a-menu-divider/>
            <a-menu-item key="paste">
              <template #icon><snippets-outlined/></template>
              {{ t.vectorCenter.rightCenter.pasteHere }}
            </a-menu-item>
            <a-menu-divider/>
            <a-menu-item key="delete" class="danger-item">
              <template #icon><delete-outlined/></template>
              {{ t.vectorCenter.rightCenter.delete }}
            </a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
      <a-dropdown v-for="file in filteredFiles" :key="file.id || file.name" :trigger="['contextmenu']">
        <DocFileCard
            :data-select-id="String(file.id)"
            :active="String(file.id) === String(props.selectedDocId ?? '')"
            :cut="clipboard?.mode === 'cut' && clipboard.items.some(c => String(c.id) === String(file.id))"
            :file="file"
            :size="viewSize"
            :selected="isSelected(String(file.id))"
            :vectorizing="!!vectorizingMap[String(file.id)]"
            :progress="vectorizingMap[String(file.id)]?.progress"
            :progress-msg="vectorizingMap[String(file.id)]?.message"
            :editing="renamingDocId === String(file.id)"
            @delete="handleDelete"
            @edit="openRenameDoc"
            @download="handleDownload"
            @rename-confirm="handleDocRenameConfirm"
            @rename-cancel="handleDocRenameCancel"
            @select="handleSelectDoc"
            @chunk="handleChunk"
            @vectorize="handleVectorize"
            @re-chunk="handleReChunk"
            @re-vectorize="handleReVectorize"
        />
        <template #overlay>
          <a-menu @click="onFileMenuClick($event, file)">
            <a-menu-item key="edit">
              <template #icon><edit-outlined/></template>
              {{ t.vectorCenter.rightCenter.edit }}
            </a-menu-item>
            <a-menu-item v-if="file.statusCode === 'PENDING' || file.statusCode === 'FAILED'" key="chunk">
              <template #icon><block-outlined/></template>
              {{ t.vectorCenter.rightCenter.chunk }}
            </a-menu-item>
            <a-menu-item v-if="file.statusCode === 'CHUNKED'" key="vectorize">
              <template #icon><experiment-outlined/></template>
              {{ t.vectorCenter.rightCenter.vectorize }}
            </a-menu-item>
            <a-menu-item v-if="file.statusCode === 'CHUNKED' || file.statusCode === 'STORED' || file.statusCode === 'FAILED'" key="re-chunk">
              <template #icon><block-outlined/></template>
              {{ t.vectorCenter.rightCenter.reChunk }}
            </a-menu-item>
            <a-menu-item v-if="file.statusCode === 'STORED' || file.statusCode === 'FAILED'" key="re-vectorize">
              <template #icon><sync-outlined/></template>
              {{ t.vectorCenter.rightCenter.reVectorize }}
            </a-menu-item>
            <a-menu-divider/>
            <a-menu-item key="move">
              <template #icon><folder-outlined/></template>
              {{ t.vectorCenter.rightCenter.moveToFolder }}
            </a-menu-item>
            <a-menu-item key="copy">
              <template #icon><copy-outlined/></template>
              {{ t.vectorCenter.rightCenter.copy }}
            </a-menu-item>
            <a-menu-item key="cut">
              <template #icon><scissor-outlined/></template>
              {{ t.vectorCenter.rightCenter.cut }}
            </a-menu-item>
            <a-menu-divider/>
            <a-menu-item key="delete" class="danger-item">
              <template #icon><delete-outlined/></template>
              {{ t.vectorCenter.rightCenter.delete }}
            </a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
    </div>

    <!-- Upload drop zone -->
    <div :class="['upload-drop-zone', { 'drag-over': dragOver }]" @click="triggerFileInput">
      <inbox-outlined class="upload-icon"/>
      <p class="upload-title">{{ t.vectorCenter.rightCenter.uploadZone.title }}</p>
      <p class="upload-hint">{{ uploading ? t.vectorCenter.rightCenter.uploadZone.uploading : t.vectorCenter.rightCenter.uploadZone.hint }}</p>
      <span class="upload-link">{{ t.vectorCenter.rightCenter.uploadZone.clickToUpload }}</span>
    </div>
    <input
        ref="fileInputRef"
        type="file"
        multiple
        style="display:none"
        accept=".txt,.text,.pdf,.doc,.docx,.md,.markdown"
        @change="onFileInputChange"
    />

    <a-modal
        :open="moveModalOpen"
        :title="t.vectorCenter.rightCenter.moveFolderTitle"
        @cancel="moveModalOpen = false"
        @ok="handleMoveConfirm"
    >
      <a-select
          :value="moveTargetFolderId"
          :options="moveFolderOptions"
          :placeholder="t.vectorCenter.rightCenter.moveFolderPlaceholder"
          allow-clear
          style="width: 100%"
          @update:value="setMoveTargetFolderId"
      />
    </a-modal>

    <Teleport to="body">
      <div
          v-if="blankMenuVisible"
          class="vec-context-menu-overlay"
          @click="closeBlankMenu"
          @contextmenu.prevent="closeBlankMenu"
      >
        <div
            :style="{ left: blankMenuX + 'px', top: blankMenuY + 'px' }"
            class="vec-context-menu"
            @click.stop
        >
          <div class="vec-context-menu-item" @click="onBlankMenuAction('newFolder')">
            <folder-outlined/>
            <span>{{ t.vectorCenter.rightCenter.contextNewFolder }}</span>
          </div>
          <div class="vec-context-menu-item" @click="onBlankMenuAction('newRecord')">
            <plus-outlined/>
            <span>{{ t.vectorCenter.rightCenter.contextNewRecord }}</span>
          </div>
          <div class="vec-context-menu-divider"/>
          <div
              :class="{ disabled: !clipboard }"
              class="vec-context-menu-item"
              @click="onBlankMenuAction('paste')"
          >
            <snippets-outlined/>
            <span>{{ t.vectorCenter.rightCenter.paste }}</span>
          </div>
          <div class="vec-context-menu-divider"/>
          <div class="vec-context-menu-item" @click="onBlankMenuAction('refresh')">
            <reload-outlined/>
            <span>{{ t.vectorCenter.rightCenter.contextRefresh }}</span>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script lang="ts" setup>
import {computed, nextTick, onUnmounted, reactive, ref, watch} from 'vue';
import {message, Modal} from 'ant-design-vue'
import {
  BlockOutlined,
  CopyOutlined,
  DeleteOutlined,
  DownloadOutlined,
  EditOutlined,
  ExperimentOutlined,
  FileMarkdownOutlined,
  FilePdfOutlined,
  FileTextOutlined,
  FolderOutlined,
  InboxOutlined,
  PlusOutlined,
  ReloadOutlined,
  ScissorOutlined,
  SnippetsOutlined,
  SyncOutlined
} from '@ant-design/icons-vue';
import DocFileCard from '@/views/admin/ai-vector/vector-center/component/right/doc/DocFileCard.vue';
import DocFolderCard from '@/views/admin/ai-vector/vector-center/component/right/doc/DocFolderCard.vue';
import SelectionOverlay from '@/views/admin/ai-vector/vector-center/component/right/SelectionOverlay.vue'
import {type AiVecDoc, aiVecDocApi} from '@/api/aiVecDoc.ts'
import {type AiVecFolder, aiVecFolderApi} from '@/api/aiVecFolder.ts'
import {useBoxSelection} from '@/views/admin/ai-vector/vector-center/hooks/useBoxSelection'
import {useClipboardShortcuts} from '@/views/admin/ai-vector/vector-center/hooks/useClipboardShortcuts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-vector')

const props = defineProps<{
  docs: AiVecDoc[]
  storeId?: number | string
  selectedDocId?: number | string
  keyword?: string
  viewSize?: 'small' | 'medium' | 'large' | 'list'
  currentFolderId?: number | string | null
  folderPath?: Array<{ id: number | string; name: string }>
}>()

const emit = defineEmits<{
  'select-doc': [id: number | string]
  changed: []
  'update:folderPath': [path: Array<{ id: number | string; name: string }>]
}>()

const viewSize = ref<'small' | 'medium' | 'large' | 'list'>('small')
const folders = ref<AiVecFolder[]>([])
const currentFolderId = ref<number | string | null>(null)
const folderPath = ref<Array<{ id: number | string; name: string }>>([])
let isInternalFolderNavigation = false
const renamingFolderId = ref<number | string | null>(null)
const renamingDocId = ref<number | string | null>(null)
const vectorizingMap = reactive<Record<string, { progress: number; message: string }>>({})
const pollingTimers = ref<Record<string, ReturnType<typeof setInterval>>>({})
const blankMenuVisible = ref(false)
const blankMenuX = ref(0)
const blankMenuY = ref(0)
const clipboard = ref<{ items: any[]; mode: 'copy' | 'cut' } | null>(null)

const gridRef = ref<HTMLElement | null>(null)
const fileInputRef = ref<HTMLInputElement | null>(null)
const dragCounter = ref(0)
const dragOver = ref(false)
const uploading = ref(false)

const onDragOver = () => {
  dragOver.value = true
}

const onDragEnter = () => {
  dragCounter.value++
  dragOver.value = true
}

const onDragLeave = () => {
  dragCounter.value--
  if (dragCounter.value <= 0) {
    dragCounter.value = 0
    dragOver.value = false
  }
}

const onDrop = async (e: DragEvent) => {
  dragCounter.value = 0
  dragOver.value = false
  const files = e.dataTransfer?.files
  if (files && files.length > 0) {
    await uploadFiles(files)
  }
}

const triggerFileInput = () => {
  fileInputRef.value?.click()
}

const onFileInputChange = async (e: Event) => {
  const input = e.target as HTMLInputElement
  const files = input.files
  if (files && files.length > 0) {
    await uploadFiles(files)
  }
  // Reset so the same file can be re-selected
  input.value = ''
}

const uploadFiles = async (files: FileList | File[]) => {
  if (!props.storeId) {
    message.warning(t.value.vectorCenter.rightCenter.selectStoreFirst)
    return
  }
  uploading.value = true
  let successCount = 0
  let failCount = 0
  for (let i = 0; i < files.length; i++) {
    try {
      const doc = await aiVecDocApi.upload(files[i], props.storeId, currentFolderId.value)
      if (doc.renamed) {
        message.info(t.value.vectorCenter.rightCenter.fileNameAutoRenamed.replace('{name}', doc.originalFileName || ''))
      }
      successCount++
    } catch (error) {
      failCount++
      const err = error as { message?: string }
      if (err?.message?.includes('same name') || err?.message?.includes('conflict')) {
        message.warning(t.value.vectorCenter.rightCenter.fileNameDuplicateContent.replace('{name}', (files[i] as File).name))
      } else {
        message.error(err?.message || t.value.vectorCenter.rightCenter.uploadFailed)
      }
    }
  }
  uploading.value = false
  if (successCount > 0) {
    message.success(t.value.vectorCenter.rightCenter.uploadSuccess)
    emit('changed')
  } else if (failCount === 0 && files.length === 0) {
    // No files to upload
  }
}

const {
  selectedIds,
  selectionRect,
  clearSelection,
  selectAll,
  toggleSelect,
  isSelected,
  onMouseDown: onBoxSelectMouseDown
} = useBoxSelection({
  containerRef: gridRef,
  itemSelector: '[data-select-id]'
})

const moveModalOpen = ref(false)
const moveTargetFiles = ref<any[]>([])
const moveTargetFolderId = ref<number | string | null>(null)
const moveFolderOptions = ref<Array<{ label: string; value: number | string }>>([])

const fetchFolders = async () => {
  if (!props.storeId) {
    folders.value = []
    return
  }
  try {
    folders.value = await aiVecFolderApi.list(props.storeId, currentFolderId.value)
  } catch {
    folders.value = []
  }
}

watch(() => props.viewSize, (size) => {
  if (size) viewSize.value = size
})

watch(() => props.keyword, () => {
  void fetchFolders()
})

watch(() => props.currentFolderId, (id) => {
  currentFolderId.value = id ?? null
  void fetchFolders()
}, { immediate: true })

// Sync external folderPath changes (from RightTop via Main)
watch(() => props.folderPath, (newPath) => {
  if (isInternalFolderNavigation) return
  if (!newPath) return
  folderPath.value = [...newPath]
}, { deep: true })

const setMoveTargetFolderId = (value: number | string | null) => {
  moveTargetFolderId.value = value
}

const getFileIcon = (name: string) => {
  const ext = name.split('.').pop()?.toLowerCase()
  if (ext === 'pdf') return FilePdfOutlined
  if (ext === 'md') return FileMarkdownOutlined
  return FileTextOutlined
}

const getFileExtension = (name: string) => {
  return name.split('.').pop()?.toLowerCase() || 'txt'
}

const onListRenameConfirm = async (e: KeyboardEvent, folder: AiVecFolder) => {
  const input = e.target as HTMLInputElement
  const newName = input.value.trim()
  if (newName && newName !== folder.folderName) {
    try {
      await aiVecFolderApi.update({id: folder.id, folderName: newName})
      message.success(t.value.vectorCenter.rightCenter.renameSuccess)
      renamingFolderId.value = null
      await fetchFolders()
    } catch (error) {
      const err = error as { message?: string }
      message.error(err?.message || t.value.vectorCenter.rightCenter.renameFailed)
    }
  } else {
    renamingFolderId.value = null
  }
}

const enterFolder = (folder: AiVecFolder) => {
  isInternalFolderNavigation = true
  currentFolderId.value = folder.id!
  folderPath.value.push({id: folder.id!, name: folder.folderName || ''})
  emit('update:folderPath', folderPath.value)
  void fetchFolders()
  nextTick(() => { isInternalFolderNavigation = false })
}

const handleFolderEnter = (folder: AiVecFolder, e: MouseEvent) => {
  if (e.ctrlKey || e.metaKey) {
    toggleSelect(String(folder.id), true)
  } else {
    clearSelection()
    enterFolder(folder)
  }
}

const navigateToRoot = () => {
  isInternalFolderNavigation = true
  currentFolderId.value = null
  folderPath.value = []
  emit('update:folderPath', folderPath.value)
  void fetchFolders()
  nextTick(() => { isInternalFolderNavigation = false })
}

const navigateToPath = (index: number) => {
  if (index < 0) {
    navigateToRoot()
    return
  }
  isInternalFolderNavigation = true
  const target = folderPath.value[index]
  currentFolderId.value = target.id
  folderPath.value = folderPath.value.slice(0, index + 1)
  emit('update:folderPath', folderPath.value)
  void fetchFolders()
  nextTick(() => { isInternalFolderNavigation = false })
}

const isNewFolderPlaceholder = (folder: AiVecFolder) => {
  return (folder as any).__isNew === true
}

const openCreateFolder = () => {
  // Windows-style: insert a placeholder folder card at the front
  const placeholder = {
    id: '__new__',
    folderName: '',
    __isNew: true
  } as any as AiVecFolder
  folders.value.unshift(placeholder)
}

const openRenameFolder = (folder: AiVecFolder) => {
  renamingFolderId.value = folder.id!
}

const handleRenameConfirm = async (folder: AiVecFolder, newName: string) => {
  try {
    if (isNewFolderPlaceholder(folder)) {
      // Creating new folder from placeholder
      await aiVecFolderApi.create({
        collectionId: props.storeId,
        folderName: newName,
        parentId: currentFolderId.value
      })
      message.success(t.value.vectorCenter.rightCenter.folderCreated)
      // Remove placeholder
      folders.value = folders.value.filter(f => !isNewFolderPlaceholder(f))
      await fetchFolders()
      emit('changed')
    } else {
      await aiVecFolderApi.update({id: folder.id, folderName: newName})
      message.success(t.value.vectorCenter.rightCenter.renameSuccess)
      renamingFolderId.value = null
      await fetchFolders()
    }
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || t.value.vectorCenter.rightCenter.renameFailed)
  }
}

const handleRenameCancel = (folder?: AiVecFolder) => {
  if (folder && isNewFolderPlaceholder(folder)) {
    folders.value = folders.value.filter(f => !isNewFolderPlaceholder(f))
  }
  renamingFolderId.value = null
}

const handleDeleteFolder = (folder: AiVecFolder) => {
  Modal.confirm({
    title: t.value.vectorCenter.rightCenter.deleteFolderTitle,
    content: t.value.vectorCenter.rightCenter.deleteFolderContent.replace('{name}', folder.folderName),
    okButtonProps: {danger: true},
    async onOk() {
      await aiVecFolderApi.delete([folder.id!])
      message.success(t.value.vectorCenter.rightCenter.folderDeleted)
      await fetchFolders()
      emit('changed')
    }
  })
}

const statusLabelMap = computed<Record<string, string>>(() => ({
  PENDING: t.value.vectorCenter.rightCenter.statusPending,
  CHUNKING: t.value.vectorCenter.rightCenter.statusChunking,
  CHUNKED: t.value.vectorCenter.rightCenter.statusChunked,
  VECTORING: t.value.vectorCenter.rightCenter.statusVectoring,
  STORED: t.value.vectorCenter.rightCenter.statusStored,
  FAILED: t.value.vectorCenter.rightCenter.statusFailed,
}))

const filteredFiles = computed(() => {
  const list = props.docs || []
  const kw = props.keyword?.trim().toLowerCase() || ''
  return list
      .filter((doc) => {
        if (currentFolderId.value != null) {
          if (String(doc.folderId ?? '') !== String(currentFolderId.value)) return false
        } else {
          if (doc.folderId != null) return false
        }
        if (!kw) return true
        return String(doc.originalFileName || doc.contentSummary || '').toLowerCase().includes(kw)
      })
      .map((doc) => {
        const status = String(doc.syncStatus || '').toUpperCase()
        let statusLabel = statusLabelMap.value[status] || statusLabelMap.value.PENDING
        return {
          id: doc.id,
          name: doc.originalFileName || `doc-${doc.id}`,
          segments: 0,
          size: doc.filePath ? t.value.vectorCenter.rightCenter.sizeUploaded : t.value.vectorCenter.rightCenter.sizePending,
          status: statusLabel,
          statusCode: status,
          uploadTime: doc.updateTime || doc.createTime,
          raw: doc
        }
      })
})

useClipboardShortcuts({
  selectedIds,
  fileList: filteredFiles,
  folderList: folders,
  clipboard,
  currentFolderId,
  onSelectAll: () => {
    const allIds = [
      ...folders.value.map(f => String(f.id)),
      ...filteredFiles.value.map(f => String(f.id))
    ]
    selectAll(allIds)
  },
  onPaste: (targetFolderId) => handlePaste(targetFolderId)
})

watch(
    () => props.docs,
    (docs) => {
      if (!docs) return
      for (const doc of docs) {
        const id = String(doc.id ?? '')
        const status = String(doc.syncStatus || '').toUpperCase()
        if ((status === 'VECTORING' || status === 'CHUNKING') && id && !vectorizingMap[id]) {
          startPolling(id)
        }
      }
    },
    {immediate: true}
)

defineExpose({ openCreateFolder })

const openRenameDoc = (file: any) => {
  if (file?.id == null) return
  renamingDocId.value = String(file.id)
}

const handleDocRenameConfirm = async (file: any, newName: string) => {
  if (file?.id == null) return
  try {
    await aiVecDocApi.update({ id: file.id, originalFileName: newName } as any)
    message.success(t.value.vectorCenter.rightCenter.renameDocSuccess)
    renamingDocId.value = null
    emit('changed')
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || t.value.vectorCenter.rightCenter.renameDocFailed)
  }
}

const handleDocRenameCancel = () => {
  renamingDocId.value = null
}

const handleDownload = async (file: any) => {
  if (file?.id == null) return
  try {
    await aiVecDocApi.download(file.id)
  } catch (err) {
    message.error((err as { message?: string })?.message || t.value.vectorCenter.rightCenter.operationFailed)
  }
}

const startPolling = (docId: string) => {
  if (pollingTimers.value[docId]) return
  vectorizingMap[docId] = {progress: 0, message: t.value.vectorCenter.rightCenter.preparing}
  pollingTimers.value[docId] = setInterval(async () => {
    try {
      const prog = await aiVecDocApi.vectorizeProgress(docId)
      vectorizingMap[docId] = {
        progress: prog.progress || 0,
        message: prog.message || t.value.vectorCenter.rightCenter.vectorizing
      }
      const status = String(prog.status || '').toUpperCase()
      if (status === 'STORED' || status === 'CHUNKED' || status === 'FAILED') {
        stopPolling(docId)
        if (status === 'STORED') {
          message.success(t.value.vectorCenter.rightCenter.vectorizeComplete)
        } else if (status === 'CHUNKED') {
          message.success(t.value.vectorCenter.rightCenter.chunkComplete)
        } else {
          message.error(t.value.vectorCenter.rightCenter.operationError.replace('{msg}', prog.message || t.value.vectorCenter.rightCenter.operationFailed))
        }
        emit('changed')
      }
    } catch {
    }
  }, 2000)
}

const stopPolling = (docId: string) => {
  if (pollingTimers.value[docId]) {
    clearInterval(pollingTimers.value[docId])
    delete pollingTimers.value[docId]
  }
  delete vectorizingMap[docId]
}

onUnmounted(() => {
  Object.keys(pollingTimers.value).forEach(stopPolling)
})

const handleChunk = async (file: any) => {
  if (file?.id == null) return
  Modal.confirm({
    title: t.value.vectorCenter.rightCenter.confirmChunk,
    content: t.value.vectorCenter.rightCenter.confirmChunkContent.replace('{name}', file.name || String(file.id)),
    async onOk() {
      await aiVecDocApi.chunk(file.id)
      message.info(t.value.vectorCenter.rightCenter.chunkSubmitted)
      startPolling(String(file.id))
    }
  })
}

const handleVectorize = async (file: any) => {
  if (file?.id == null) return
  Modal.confirm({
    title: t.value.vectorCenter.rightCenter.confirmVectorize,
    content: t.value.vectorCenter.rightCenter.confirmVectorizeContent.replace('{name}', file.name || String(file.id)),
    async onOk() {
      await aiVecDocApi.vectorize(file.id)
      message.info(t.value.vectorCenter.rightCenter.vectorizeSubmitted)
      startPolling(String(file.id))
    }
  })
}

const handleReChunk = async (file: any) => {
  if (file?.id == null) return
  Modal.confirm({
    title: t.value.vectorCenter.rightCenter.confirmReChunk,
    content: t.value.vectorCenter.rightCenter.confirmReChunkContent.replace('{name}', file.name || String(file.id)),
    async onOk() {
      await aiVecDocApi.reChunk(file.id)
      message.info(t.value.vectorCenter.rightCenter.reChunkSubmitted)
      startPolling(String(file.id))
    }
  })
}

const handleReVectorize = async (file: any) => {
  if (file?.id == null) return
  Modal.confirm({
    title: t.value.vectorCenter.rightCenter.confirmReVectorize,
    content: t.value.vectorCenter.rightCenter.confirmReVectorizeContent.replace('{name}', file.name || String(file.id)),
    async onOk() {
      await aiVecDocApi.reVectorize(file.id)
      message.info(t.value.vectorCenter.rightCenter.reVectorizeSubmitted)
      startPolling(String(file.id))
    }
  })
}

const handleDelete = async (file: any) => {
  if (file?.id == null) return
  Modal.confirm({
    title: t.value.vectorCenter.rightCenter.confirmDeleteDoc,
    content: t.value.vectorCenter.rightCenter.confirmDeleteDocContent.replace('{name}', file.name || String(file.id)),
    okButtonProps: {danger: true},
    async onOk() {
      await aiVecDocApi.delete([file.id])
      message.success(t.value.vectorCenter.rightCenter.docDeleted)
      emit('changed')
      if (String(props.selectedDocId ?? '') === String(file.id)) {
        emit('select-doc', '')
      }
    }
  })
}

const handleDeleteSelected = (file: any) => {
  const batchItems = isSelected(String(file.id))
      ? filteredFiles.value.filter(f => isSelected(String(f.id)))
      : [file]

  if (batchItems.length === 0) return

  if (batchItems.length === 1) {
    handleDelete(batchItems[0])
    return
  }

  Modal.confirm({
    title: t.value.vectorCenter.rightCenter.confirmBatchDelete,
    content: t.value.vectorCenter.rightCenter.confirmBatchDeleteContent.replace('{count}', String(batchItems.length)),
    okButtonProps: {danger: true},
    async onOk() {
      const ids = batchItems.map(f => f.id).filter(Boolean)
      await aiVecDocApi.delete(ids)
      message.success(t.value.vectorCenter.rightCenter.batchDeleted.replace('{count}', String(ids.length)))
      clearSelection()
      emit('changed')
    }
  })
}

const handleSelectDoc = (file: any, e?: MouseEvent) => {
  if (file?.id == null) return
  if (e?.ctrlKey || e?.metaKey) {
    toggleSelect(String(file.id), true)
  } else {
    clearSelection()
    toggleSelect(String(file.id), false)
    emit('select-doc', file.id)
  }
}

const onBlankContextMenu = (e: MouseEvent) => {
  const target = e.target as HTMLElement
  if (target.closest('.custom-file-card') || target.closest('.folder-card'))
    return
  e.preventDefault()
  if (!e.ctrlKey && !e.metaKey) {
    clearSelection()
  }
  blankMenuX.value = e.clientX
  blankMenuY.value = e.clientY
  blankMenuVisible.value = true
}

const closeBlankMenu = () => {
  blankMenuVisible.value = false
}

const onBlankMenuAction = (action: string) => {
  closeBlankMenu()
  switch (action) {
    case 'newFolder':
      openCreateFolder()
      break
    case 'newRecord':
      // VecDocFormModal is disabled; new docs are created via upload
      break
    case 'paste':
      handlePaste(null)
      break
    case 'refresh':
      emit('changed')
      break
  }
}

const onFolderMenuClick = (payload: unknown, folder: AiVecFolder) => {
  const key = String((payload as { key?: string | number })?.key ?? '')
  switch (key) {
    case 'open':
      enterFolder(folder)
      break
    case 'rename':
      openRenameFolder(folder)
      break
    case 'paste':
      handlePaste(folder.id!)
      break
    case 'delete':
      handleDeleteFolder(folder)
      break
  }
}

const onFileMenuClick = (payload: unknown, file: any) => {
  const key = String((payload as { key?: string | number })?.key ?? '')

  switch (key) {
    case 'edit':
      openRenameDoc(file)
      return
    case 'chunk':
      handleChunk(file)
      return
    case 'vectorize':
      handleVectorize(file)
      return
    case 're-chunk':
      handleReChunk(file)
      return
    case 're-vectorize':
      handleReVectorize(file)
      return
    case 'delete':
      handleDeleteSelected(file)
      return
  }

  const batchItems = isSelected(String(file.id))
      ? filteredFiles.value.filter(f => isSelected(String(f.id)))
      : [file]

  switch (key) {
    case 'move':
      openMoveModal(batchItems)
      break
    case 'copy':
      clipboard.value = {items: batchItems, mode: 'copy'}
      message.success(t.value.vectorCenter.rightCenter.copiedFiles.replace('{count}', String(batchItems.length)))
      break
    case 'cut':
      clipboard.value = {items: batchItems, mode: 'cut'}
      message.success(t.value.vectorCenter.rightCenter.cutFiles.replace('{count}', String(batchItems.length)))
      break
  }
}

const openMoveModal = async (files: any[]) => {
  moveTargetFiles.value = files
  moveTargetFolderId.value = null
  if (!props.storeId) {
    message.warning(t.value.vectorCenter.rightCenter.selectCollection)
    return
  }
  try {
    const allFolders = await aiVecFolderApi.list(props.storeId)
    moveFolderOptions.value = [
      {label: t.value.vectorCenter.rightCenter.rootDirectory, value: '__root__'},
      ...allFolders.map(f => ({label: f.folderName || '', value: f.id!}))
    ]
  } catch {
    moveFolderOptions.value = [{label: t.value.vectorCenter.rightCenter.rootDirectory, value: '__root__'}]
  }
  moveModalOpen.value = true
}

const handleMoveConfirm = async () => {
  const docIds = moveTargetFiles.value.map(f => f.id ?? f.raw?.id).filter(Boolean)
  if (!docIds.length) {
    message.warning(t.value.vectorCenter.rightCenter.noFilesToMove)
    return
  }
  const targetId = moveTargetFolderId.value === '__root__' ? null : moveTargetFolderId.value
  try {
    await aiVecFolderApi.moveDocs(docIds, targetId)
    message.success(t.value.vectorCenter.rightCenter.movedFiles.replace('{count}', String(docIds.length)))
    moveModalOpen.value = false
    emit('changed')
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || t.value.vectorCenter.rightCenter.moveFailed)
  }
}

const handlePaste = async (targetFolderId: number | string | null) => {
  if (!clipboard.value) {
    message.warning(t.value.vectorCenter.rightCenter.clipboardEmpty)
    return
  }
  const docIds = clipboard.value.items.map(f => f.id ?? f.raw?.id).filter(Boolean)
  if (!docIds.length) return

  if (clipboard.value.mode === 'cut') {
    const allSameLocation = clipboard.value.items.every(f => {
      const currentFolderId = f.raw?.folderId ?? null
      return String(currentFolderId ?? '') === String(targetFolderId ?? '')
    })
    if (allSameLocation) {
      message.info(t.value.vectorCenter.rightCenter.fileAlreadyInPlace)
      clipboard.value = null
      return
    }
  }

  try {
    await aiVecFolderApi.moveDocs(docIds, targetFolderId)
    message.success(t.value.vectorCenter.rightCenter.pastedFiles.replace('{count}', String(docIds.length)))

    if (clipboard.value.mode === 'cut') {
      clipboard.value = null
    }
    emit('changed')
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || t.value.vectorCenter.rightCenter.pasteFailed)
  }
}
</script>

<style lang="less" scoped>
.document-list-container {
  padding: 24px;
  background: transparent;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.file-grid {
  position: relative;
  display: grid;
  gap: 20px;
  flex: 1;
  overflow-y: auto;
  align-content: start;

  &.size-small {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 14px;
  }

  &.size-medium {
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 20px;
  }

  &.size-large {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 24px;
  }
}

.file-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;

  .file-list-header {
    display: flex;
    align-items: center;
    padding: 10px 16px;
    font-size: 12px;
    font-weight: 600;
    color: var(--text-secondary);
    text-transform: uppercase;
    letter-spacing: 0.3px;
    border-bottom: 1px solid var(--border-default);
    position: sticky;
    top: 0;
    background: var(--bg-surface);
    z-index: 2;
  }

  .col-name { flex: 2; min-width: 0; }
  .col-size { flex: 0 0 100px; text-align: center; }
  .col-status { flex: 0 0 100px; text-align: center; }
  .col-time { flex: 0 0 140px; text-align: center; }
  .col-actions { flex: 0 0 90px; text-align: center; }
}

.file-list-row {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  border-bottom: 1px solid var(--border-default);
  cursor: pointer;
  transition: all 0.2s ease;
  border-left: 3px solid transparent;

  &:hover {
    background: var(--bg-elevated);
    border-left-color: var(--primary);
  }

  &.active {
    background: color-mix(in srgb, var(--primary) 8%, transparent);
    border-left-color: var(--primary);
  }

  &.selected {
    background: color-mix(in srgb, var(--primary) 12%, transparent);
  }

  &.cut {
    opacity: 0.45;
    filter: grayscale(0.6);
  }

  .col-name {
    flex: 2;
    min-width: 0;
    display: flex;
    align-items: center;
    gap: 10px;

    .row-icon {
      font-size: 18px;
      flex-shrink: 0;

      &.folder-icon {
        color: #fbbf24;
      }

      &.file-icon {
        color: var(--text-secondary);
      }
    }

    .row-title {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-size: 13px;
      color: var(--text-heading);
    }

    .inline-edit input {
      font-size: 13px;
      font-weight: 500;
      color: var(--text-heading);
      border: 1px solid var(--primary);
      border-radius: var(--radius-sm);
      padding: 2px 8px;
      outline: none;
      background: var(--bg-input);
      width: 200px;
    }
  }

  .col-size {
    flex: 0 0 100px;
    text-align: center;
    font-size: 12px;
    color: var(--text-muted);
  }

  .col-status {
    flex: 0 0 100px;
    text-align: center;

    .status-tag {
      display: inline-block;
      padding: 2px 10px;
      border-radius: var(--radius-max);
      font-size: 11px;
      font-weight: 600;

      &.stored {
        background: color-mix(in srgb, var(--success) 15%, transparent);
        color: var(--success);
      }

      &.pending {
        background: color-mix(in srgb, #f59e0b 15%, transparent);
        color: #f59e0b;
      }

      &.chunking, &.vectoring {
        background: color-mix(in srgb, var(--primary) 15%, transparent);
        color: var(--primary);
      }

      &.chunked {
        background: color-mix(in srgb, #8b5cf6 15%, transparent);
        color: #8b5cf6;
      }

      &.failed {
        background: color-mix(in srgb, var(--error) 15%, transparent);
        color: var(--error);
      }
    }
  }

  .col-time {
    flex: 0 0 140px;
    text-align: center;
    font-size: 12px;
    color: var(--text-muted);
  }

  .col-actions {
    flex: 0 0 90px;
    text-align: center;
    display: flex;
    justify-content: center;
    gap: 2px;
  }
}

.upload-drop-zone {
  flex-shrink: 0;
  margin: 16px 0 0;
  border: 2px dashed var(--border-default);
  border-radius: var(--radius-lg);
  padding: 40px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: transparent;

  &:hover {
    border-color: var(--primary);
    background: color-mix(in srgb, var(--primary) 4%, transparent);
  }

  &.drag-over {
    border-color: var(--primary);
    background: color-mix(in srgb, var(--primary) 8%, transparent);
    box-shadow: 0 0 0 4px color-mix(in srgb, var(--primary) 12%, transparent);
  }

  .upload-icon {
    font-size: 40px;
    color: var(--text-muted);
    transition: color 0.3s;
  }

  &:hover .upload-icon,
  &.drag-over .upload-icon {
    color: var(--primary);
  }

  .upload-title {
    font-size: 15px;
    font-weight: 500;
    color: var(--text-secondary);
    margin: 0;
  }

  .upload-hint {
    font-size: 12px;
    color: var(--text-muted);
    margin: 0;
  }

  .upload-link {
    font-size: 13px;
    color: var(--primary);
    margin-top: 4px;
    text-decoration: none;
    font-weight: 500;

    &:hover {
      text-decoration: underline;
    }
  }
}
</style>

<style lang="less">
:deep(.danger-item) {
  color: var(--error);
  &:hover {
    color: var(--error) !important;
    background: color-mix(in srgb, var(--error) 10%, transparent) !important;
  }
}

.vec-context-menu-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
}

.vec-context-menu {
  position: fixed;
  min-width: 160px;
  background: var(--bg-card, #fff);
  border: 1px solid var(--border-default, #e2e8f0);
  border-radius: 8px;
  padding: 4px;
  box-shadow: 0 6px 16px color-mix(in srgb, var(--text-primary) 12%, transparent), 0 3px 6px color-mix(in srgb, var(--text-primary) 8%, transparent);
  z-index: 1001;
}

.vec-context-menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  font-size: 13px;
  color: var(--text-primary, #334155);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.15s;

  &:hover {
    background: var(--primary-hover, #eff6ff);
    color: var(--primary, #2563eb);
  }

  &.disabled {
    opacity: 0.4;
    cursor: not-allowed;
    &:hover {
      background: transparent;
      color: var(--text-primary, #334155);
    }
  }
}

.vec-context-menu-divider {
  height: 1px;
  margin: 4px 8px;
  background: var(--border-default, #e2e8f0);
}
</style>
