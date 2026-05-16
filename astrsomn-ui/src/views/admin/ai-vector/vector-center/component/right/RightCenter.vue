<template>
  <div class="document-list-container">
    <div class="list-toolbar">
      <div class="toolbar-left">
        <div class="breadcrumb-nav">
          <span class="breadcrumb-item" @click="navigateToRoot">根目录</span>
          <!-- eslint-disable-next-line vue/no-v-for-template-key -->
          <template v-for="(crumb, idx) in folderPath" :key="crumb.id">
            <span class="breadcrumb-sep">/</span>
            <span class="breadcrumb-item" @click="navigateToPath(idx)">{{ crumb.name }}</span>
          </template>
        </div>
        <div class="count-badge">{{ filteredFiles.length }} 文档</div>
      </div>

      <div class="toolbar-right">
        <a-select
            :options="storeOptions"
            :value="uploadCollectionId"
            placeholder="选择集合"
            style="width: 180px"
            @update:value="setUploadCollectionId"
        />
        <a-input-search
            :value="keyword"
            class="subtle-search"
            placeholder="搜索文件名..."
            @update:value="setKeyword"
        />
        <div class="view-toggle">
          <a-tooltip title="小图标">
            <AppstoreOutlined :class="{ active: viewSize === 'small' }" class="toggle-icon" @click="setViewSize('small')"/>
          </a-tooltip>
          <a-tooltip title="中图标">
            <BorderOutlined :class="{ active: viewSize === 'medium' }" class="toggle-icon" @click="setViewSize('medium')"/>
          </a-tooltip>
          <a-tooltip title="大图标">
            <CreditCardOutlined :class="{ active: viewSize === 'large' }" class="toggle-icon" @click="setViewSize('large')"/>
          </a-tooltip>
        </div>
        <a-button class="import-btn" @click="openCreateFolder">
          <template #icon>
            <folder-outlined/>
          </template>
          新建文件夹
        </a-button>
        <a-upload :custom-request="handleUpload" :show-upload-list="false">
          <a-button class="import-btn" type="primary">
            <template #icon>
              <plus-outlined/>
            </template>
            导入文档
          </a-button>
        </a-upload>
        <a-button class="import-btn" @click="openCreate">新增记录</a-button>
      </div>
    </div>

    <div :class="`file-grid size-${viewSize}`" @contextmenu.prevent="onBlankContextMenu">
      <a-dropdown v-for="folder in folders" :key="folder.id" :trigger="['contextmenu']">
        <DocFolderCard
            :folder="folder"
            :size="viewSize"
            @delete="handleDeleteFolder"
            @enter="enterFolder"
            @rename="openRenameFolder"
        />
        <template #overlay>
          <a-menu @click="onFolderMenuClick($event, folder)">
            <a-menu-item key="open">
              <template #icon><folder-outlined/></template>
              打开
            </a-menu-item>
            <a-menu-item key="rename">
              <template #icon><edit-outlined/></template>
              重命名
            </a-menu-item>
            <a-menu-divider/>
            <a-menu-item key="paste">
              <template #icon><snippets-outlined/></template>
              粘贴到此文件夹
            </a-menu-item>
            <a-menu-divider/>
            <a-menu-item key="delete" class="danger-item">
              <template #icon><delete-outlined/></template>
              删除
            </a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
      <a-dropdown v-for="file in filteredFiles" :key="file.id || file.name" :trigger="['contextmenu']">
        <DocFileCard
            :active="String(file.id) === String(props.selectedDocId ?? '')"
            :cut="clipboard?.mode === 'cut' && clipboard.items.some(c => String(c.id) === String(file.id))"
            :file="file"
            :size="viewSize"
            :vectorizing="!!vectorizingMap[String(file.id)]"
            :progress="vectorizingMap[String(file.id)]?.progress"
            :progress-msg="vectorizingMap[String(file.id)]?.message"
            @delete="handleDelete"
            @edit="openEdit"
            @select="handleSelectDoc"
            @vectorize="handleVectorize"
            @re-vectorize="handleReVectorize"
        />
        <template #overlay>
          <a-menu @click="onFileMenuClick($event, file)">
            <a-menu-item key="edit">
              <template #icon><edit-outlined/></template>
              编辑
            </a-menu-item>
            <a-menu-item v-if="file.status === '待向量化'" key="vectorize">
              <template #icon><experiment-outlined/></template>
              向量化
            </a-menu-item>
            <a-menu-item v-if="file.status === '已向量化' || file.status === '失败'" key="re-vectorize">
              <template #icon><sync-outlined/></template>
              重新向量化
            </a-menu-item>
            <a-menu-divider/>
            <a-menu-item key="move">
              <template #icon><folder-outlined/></template>
              移动到文件夹...
            </a-menu-item>
            <a-menu-item key="copy">
              <template #icon><copy-outlined/></template>
              复制
            </a-menu-item>
            <a-menu-item key="cut">
              <template #icon><scissor-outlined/></template>
              剪切
            </a-menu-item>
            <a-menu-divider/>
            <a-menu-item key="delete" class="danger-item">
              <template #icon><delete-outlined/></template>
              删除
            </a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
    </div>
    <VecDocFormModal
        :confirm-loading="modalSubmitting"
        :initial="modalInitial"
        :mode="modalMode"
        :open="modalOpen"
        @submit="handleSubmit"
        @update:open="(value) => (modalOpen = value)"
    />
    <a-modal
        :open="folderModalOpen"
        :title="editingFolder ? '重命名文件夹' : '新建文件夹'"
        @cancel="folderModalOpen = false"
        @ok="handleFolderSubmit"
    >
      <a-input
          :value="folderModalName"
          placeholder="请输入文件夹名称"
          @update:value="setFolderModalName"
          @keyup.enter="handleFolderSubmit"
      />
    </a-modal>

    <!-- 移动到文件夹弹窗 -->
    <a-modal
        :open="moveModalOpen"
        title="移动到文件夹"
        @cancel="moveModalOpen = false"
        @ok="handleMoveConfirm"
    >
      <a-select
          :value="moveTargetFolderId"
          :options="moveFolderOptions"
          placeholder="选择目标文件夹（留空则移至根目录）"
          allow-clear
          style="width: 100%"
          @update:value="setMoveTargetFolderId"
      />
    </a-modal>

    <!-- 空白区域右键菜单 -->
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
            <span>新建文件夹</span>
          </div>
          <div class="vec-context-menu-item" @click="onBlankMenuAction('newRecord')">
            <plus-outlined/>
            <span>新建记录</span>
          </div>
          <div class="vec-context-menu-divider"/>
          <div
              :class="{ disabled: !clipboard }"
              class="vec-context-menu-item"
              @click="onBlankMenuAction('paste')"
          >
            <snippets-outlined/>
            <span>粘贴</span>
          </div>
          <div class="vec-context-menu-divider"/>
          <div class="vec-context-menu-item" @click="onBlankMenuAction('refresh')">
            <reload-outlined/>
            <span>刷新</span>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script lang="ts" setup>
import {computed, onUnmounted, reactive, ref, watch} from 'vue';
import type {UploadProps} from 'ant-design-vue'
import {message, Modal} from 'ant-design-vue'
import {
  AppstoreOutlined, BorderOutlined, CopyOutlined, CreditCardOutlined,
  DeleteOutlined, EditOutlined, ExperimentOutlined, FolderOutlined,
  PlusOutlined, ReloadOutlined, ScissorOutlined, SnippetsOutlined, SyncOutlined
} from '@ant-design/icons-vue';
import DocFileCard from '@/views/admin/ai-vector/vector-center/component/right/doc/DocFileCard.vue';
import DocFolderCard from '@/views/admin/ai-vector/vector-center/component/right/doc/DocFolderCard.vue';
import VecDocFormModal from '@/views/admin/ai-vector/vec-doc/VecDocFormModal.vue'
import {type AiVecDoc, aiVecDocApi} from '@/api/aiVecDoc.ts'
import {type AiVecFolder, aiVecFolderApi} from '@/api/aiVecFolder.ts'

const props = defineProps<{
  docs: AiVecDoc[]
  storeId?: number | string
  selectedDocId?: number | string
}>()

const emit = defineEmits<{
  'select-doc': [id: number | string]
  changed: []
}>()

const keyword = ref('')
const viewSize = ref<'small' | 'medium' | 'large'>(localStorage.getItem('doc-view-size') as any || 'medium')
const uploadCollectionId = ref<number | string | undefined>()
const modalOpen = ref(false)

// 文件夹相关
const folders = ref<AiVecFolder[]>([])
const currentFolderId = ref<number | string | null>(null)
const folderPath = ref<Array<{ id: number | string; name: string }>>([])
const folderLoading = ref(false)
const folderModalOpen = ref(false)
const folderModalName = ref('')
const editingFolder = ref<AiVecFolder | null>(null)
const modalMode = ref<'create' | 'edit'>('create')
const modalInitial = ref<AiVecDoc | null>(null)
const modalSubmitting = ref(false)

// 向量化进度追踪
const vectorizingMap = reactive<Record<string, { progress: number; message: string }>>({})
const pollingTimers = ref<Record<string, ReturnType<typeof setInterval>>>({})

// 右键菜单 & 剪贴板
const blankMenuVisible = ref(false)
const blankMenuX = ref(0)
const blankMenuY = ref(0)
const clipboard = ref<{ items: any[]; mode: 'copy' | 'cut' } | null>(null)

// 移动到文件夹弹窗
const moveModalOpen = ref(false)
const moveTargetFiles = ref<any[]>([])
const moveTargetFolderId = ref<number | string | null>(null)
const moveFolderOptions = ref<Array<{ label: string; value: number | string }>>([])

const setUploadCollectionId = (value: number | string | undefined) => {
  uploadCollectionId.value = value
}

const setKeyword = (value: string) => {
  keyword.value = value
}

const setFolderModalName = (value: string) => {
  folderModalName.value = value
}

const setMoveTargetFolderId = (value: number | string | null) => {
  moveTargetFolderId.value = value
}

const setViewSize = (size: 'small' | 'medium' | 'large') => {
  viewSize.value = size
  localStorage.setItem('doc-view-size', size)
}

// 文件夹操作
const fetchFolders = async () => {
  if (!props.storeId) {
    folders.value = []
    return
  }
  folderLoading.value = true
  try {
    folders.value = await aiVecFolderApi.list(props.storeId, currentFolderId.value)
  } catch {
    folders.value = []
  } finally {
    folderLoading.value = false
  }
}

const enterFolder = (folder: AiVecFolder) => {
  currentFolderId.value = folder.id!
  folderPath.value.push({id: folder.id!, name: folder.folderName || ''})
  void fetchFolders()
}

const navigateToRoot = () => {
  currentFolderId.value = null
  folderPath.value = []
  void fetchFolders()
}

const navigateToPath = (index: number) => {
  if (index < 0) {
    navigateToRoot()
    return
  }
  const target = folderPath.value[index]
  currentFolderId.value = target.id
  folderPath.value = folderPath.value.slice(0, index + 1)
  void fetchFolders()
}

const openCreateFolder = () => {
  editingFolder.value = null
  folderModalName.value = ''
  folderModalOpen.value = true
}

const openRenameFolder = (folder: AiVecFolder) => {
  editingFolder.value = folder
  folderModalName.value = folder.folderName || ''
  folderModalOpen.value = true
}

const handleFolderSubmit = async () => {
  const name = folderModalName.value.trim()
  if (!name) {
    message.warning('文件夹名称不能为空')
    return
  }
  try {
    if (editingFolder.value) {
      await aiVecFolderApi.update({id: editingFolder.value.id, folderName: name})
      message.success('重命名成功')
    } else {
      await aiVecFolderApi.create({
        collectionId: props.storeId,
        folderName: name,
        parentId: currentFolderId.value
      })
      message.success('文件夹创建成功')
    }
    folderModalOpen.value = false
    await fetchFolders()
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || '操作失败')
  }
}

const handleDeleteFolder = (folder: AiVecFolder) => {
  Modal.confirm({
    title: '确认删除文件夹',
    content: `删除文件夹"${folder.folderName}"后，其中的文档将回到根目录。`,
    okButtonProps: {danger: true},
    async onOk() {
      await aiVecFolderApi.delete([folder.id!])
      message.success('文件夹已删除')
      await fetchFolders()
    }
  })
}

watch(
    () => props.storeId,
    (id) => {
      uploadCollectionId.value = id
      currentFolderId.value = null
      folderPath.value = []
      void fetchFolders()
    },
    {immediate: true}
)

const storeOptions = computed(() =>
    props.storeId == null
        ? []
        : [{label: `当前集合 (${props.storeId})`, value: props.storeId}]
)

const filteredFiles = computed(() => {
  const list = props.docs || []
  const kw = keyword.value.trim().toLowerCase()
  return list
      .filter((doc) => {
        // 文件夹筛选：当在某个文件夹内时，只显示该文件夹的文档
        if (currentFolderId.value != null) {
          if (String(doc.folderId ?? '') !== String(currentFolderId.value)) return false
        } else {
          // 在根目录时，不显示已归入文件夹的文档
          if (doc.folderId != null) return false
        }
        if (!kw) return true
        return String(doc.originalFileName || doc.contentSummary || '').toLowerCase().includes(kw)
      })
      .map((doc) => {
        const status = String(doc.syncStatus || '').toUpperCase()
        let statusLabel = '待向量化'
        if (status === 'STORED') statusLabel = '已向量化'
        else if (status === 'VECTORING') statusLabel = '向量化中'
        else if (status === 'FAILED') statusLabel = '失败'
        return {
          id: doc.id,
          name: doc.originalFileName || `doc-${doc.id}`,
          segments: 0,
          size: doc.filePath ? '已上传' : '待上传',
          status: statusLabel,
          uploadTime: doc.createTime,
          raw: doc
        }
      })
})

// 自动为已在向量化中的文档启动轮询
watch(
    () => props.docs,
    (docs) => {
      if (!docs) return
      for (const doc of docs) {
        const id = String(doc.id ?? '')
        const status = String(doc.syncStatus || '').toUpperCase()
        if (status === 'VECTORING' && id && !vectorizingMap[id]) {
          startPolling(id)
        }
      }
    },
    {immediate: true}
)

const openCreate = () => {
  modalMode.value = 'create'
  modalInitial.value = {
    collectionId: props.storeId,
    contentSummary: '',
    syncStatus: 'PENDING'
  }
  modalOpen.value = true
}

const openEdit = async (file: any) => {
  const id = file?.id
  if (id == null) return
  const detail = await aiVecDocApi.detail(id)
  modalMode.value = 'edit'
  modalInitial.value = detail
  modalOpen.value = true
}

const handleSubmit = async (payload: AiVecDoc) => {
  modalSubmitting.value = true
  try {
    if (modalMode.value === 'create') {
      await aiVecDocApi.create(payload)
    } else {
      await aiVecDocApi.update(payload)
    }
    modalOpen.value = false
    emit('changed')
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || '保存文档失败')
  } finally {
    modalSubmitting.value = false
  }
}

const startPolling = (docId: string) => {
  if (pollingTimers.value[docId]) return
  vectorizingMap[docId] = {progress: 0, message: '准备中...'}
  pollingTimers.value[docId] = setInterval(async () => {
    try {
      const prog = await aiVecDocApi.vectorizeProgress(docId)
      vectorizingMap[docId] = {
        progress: prog.progress || 0,
        message: prog.message || '向量化中...'
      }
      const status = String(prog.status || '').toUpperCase()
      if (status === 'STORED' || status === 'FAILED') {
        stopPolling(docId)
        if (status === 'STORED') {
          message.success('向量化完成')
        } else {
          message.error('向量化失败: ' + (prog.message || '未知错误'))
        }
        emit('changed')
      }
    } catch {
      // ignore polling errors
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

const handleVectorize = async (file: any) => {
  if (file?.id == null) return
  Modal.confirm({
    title: '确认执行向量化',
    content: `将对文档 ${file.name || file.id} 执行向量化并写入向量库。`,
    async onOk() {
      await aiVecDocApi.vectorize(file.id)
      message.info('向量化任务已提交')
      startPolling(String(file.id))
    }
  })
}

const handleReVectorize = async (file: any) => {
  if (file?.id == null) return
  Modal.confirm({
    title: '确认重新向量化',
    content: `将清除文档 ${file.name || file.id} 的旧向量数据并重新执行向量化。`,
    async onOk() {
      await aiVecDocApi.reVectorize(file.id)
      message.info('重新向量化任务已提交')
      startPolling(String(file.id))
    }
  })
}

const handleDelete = async (file: any) => {
  if (file?.id == null) return
  Modal.confirm({
    title: '确认删除文档',
    content: `删除后将同步清理切片与向量数据：${file.name || file.id}`,
    okButtonProps: {danger: true},
    async onOk() {
      await aiVecDocApi.delete([file.id])
      message.success('文档删除成功')
      emit('changed')
      if (String(props.selectedDocId ?? '') === String(file.id)) {
        emit('select-doc', '')
      }
    }
  })
}

const handleSelectDoc = (file: any) => {
  if (file?.id == null) return
  emit('select-doc', file.id)
}

const handleUpload: UploadProps['customRequest'] = async (options) => {
  if (!uploadCollectionId.value) {
    message.warning('请先在左侧选择数据库')
    options.onError?.(new Error('missing store'))
    return
  }
  try {
    await aiVecDocApi.upload(options.file as File, uploadCollectionId.value, currentFolderId.value)
    options.onSuccess?.({})
    message.success('上传成功')
    emit('changed')
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || '上传失败')
    options.onError?.(error as Error)
  }
}

// ========== 右键菜单处理 ==========

const onBlankContextMenu = (e: MouseEvent) => {
  const target = e.target as HTMLElement
  if (target.closest('.custom-file-card') || target.closest('.folder-card'))
    return
  e.preventDefault()
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
      openCreate()
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
      openEdit(file)
      break
    case 'vectorize':
      handleVectorize(file)
      break
    case 're-vectorize':
      handleReVectorize(file)
      break
    case 'move':
      openMoveModal([file])
      break
    case 'copy':
      clipboard.value = {items: [file], mode: 'copy'}
      message.success('已复制 1 个文件')
      break
    case 'cut':
      clipboard.value = {items: [file], mode: 'cut'}
      message.success('已剪切 1 个文件')
      break
    case 'delete':
      handleDelete(file)
      break
  }
}

// ========== 移动 & 粘贴逻辑 ==========

const openMoveModal = async (files: any[]) => {
  moveTargetFiles.value = files
  moveTargetFolderId.value = null
  if (!props.storeId) {
    message.warning('请先选择集合')
    return
  }
  try {
    const allFolders = await aiVecFolderApi.list(props.storeId)
    moveFolderOptions.value = [
      {label: '根目录', value: '__root__'},
      ...allFolders.map(f => ({label: f.folderName || '', value: f.id!}))
    ]
  } catch {
    moveFolderOptions.value = [{label: '根目录', value: '__root__'}]
  }
  moveModalOpen.value = true
}

const handleMoveConfirm = async () => {
  const docIds = moveTargetFiles.value.map(f => f.id ?? f.raw?.id).filter(Boolean)
  if (!docIds.length) {
    message.warning('没有可移动的文件')
    return
  }
  const targetId = moveTargetFolderId.value === '__root__' ? null : moveTargetFolderId.value
  try {
    await aiVecFolderApi.moveDocs(docIds, targetId)
    message.success(`已移动 ${docIds.length} 个文件`)
    moveModalOpen.value = false
    emit('changed')
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || '移动失败')
  }
}

const handlePaste = async (targetFolderId: number | string | null) => {
  if (!clipboard.value) {
    message.warning('剪贴板为空')
    return
  }
  const docIds = clipboard.value.items.map(f => f.id ?? f.raw?.id).filter(Boolean)
  if (!docIds.length) return

  // 剪切模式下，检查是否粘贴到同一位置（空操作）
  if (clipboard.value.mode === 'cut') {
    const allSameLocation = clipboard.value.items.every(f => {
      const currentFolderId = f.raw?.folderId ?? null
      return String(currentFolderId ?? '') === String(targetFolderId ?? '')
    })
    if (allSameLocation) {
      message.info('文件已在目标位置')
      clipboard.value = null
      return
    }
  }

  try {
    await aiVecFolderApi.moveDocs(docIds, targetFolderId)
    message.success(`已粘贴 ${docIds.length} 个文件`)
    // 先清空剪贴板再刷新，使 UI 立即移除灰色状态
    if (clipboard.value.mode === 'cut') {
      clipboard.value = null
    }
    emit('changed')
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || '粘贴失败')
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

/* 工具栏样式 */
.list-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  .toolbar-left {
    display: flex;
    align-items: center;
    gap: 12px;

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-heading);
      margin: 0;
    }

    .breadcrumb-nav {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 14px;

      .breadcrumb-item {
        color: var(--text-secondary);
        cursor: pointer;
        transition: color 0.2s;

        &:hover {
          color: var(--primary);
        }

        &:last-child {
          color: var(--text-heading);
          font-weight: 600;
        }
      }

      .breadcrumb-sep {
        color: var(--text-muted);
        font-size: 12px;
      }
    }

    .count-badge {
      background: var(--bg-input);
      color: var(--text-secondary);
      padding: 2px 10px;
      border-radius: var(--radius-max);
      font-size: 12px;
      font-weight: 600;
    }
  }

  .toolbar-right {
    display: flex;
    gap: 12px;

    .subtle-search {
      width: 240px;

      :deep(.ant-input) {
        border-radius: var(--radius-md);
        border-color: var(--border-input);
        background: var(--bg-input);
        color: var(--text-primary);
      }

      :deep(.ant-input::placeholder) {
        color: var(--text-placeholder);
      }
    }

    .import-btn {
      border-radius: var(--radius-md);
      font-weight: 500;
    }
  }
}

/* 布局网格：负责卡片的大小和排列 */
.file-grid {
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

/* 视图切换 */
.view-toggle {
  display: flex;
  align-items: center;
  gap: 2px;
  background: var(--bg-input);
  border-radius: var(--radius-md);
  padding: 2px;

  .toggle-icon {
    width: 28px;
    height: 28px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: var(--radius-sm);
    cursor: pointer;
    color: var(--text-muted);
    transition: all 0.2s;

    &:hover {
      color: var(--text-primary);
    }

    &.active {
      background: var(--bg-card);
      color: var(--primary);
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
    }
  }
}
</style>

<style lang="less">
:deep(.danger-item) {
  color: var(--error);
  &:hover {
    color: var(--error) !important;
    background: rgba(239, 68, 68, 0.1) !important;
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
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12), 0 3px 6px rgba(0, 0, 0, 0.08);
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