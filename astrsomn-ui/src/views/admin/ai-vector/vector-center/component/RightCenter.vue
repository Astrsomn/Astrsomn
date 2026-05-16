<template>
  <div class="document-list-container">
    <div class="list-toolbar">
      <div class="toolbar-left">
        <h2 class="section-title">文档列表</h2>
        <div class="count-badge">{{ filteredFiles.length }}</div>
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

    <div class="file-grid">
      <FileCard
          v-for="file in filteredFiles"
          :key="file.id || file.name"
          :active="String(file.id) === String(props.selectedDocId ?? '')"
          :file="file"
          :vectorizing="!!vectorizingMap[String(file.id)]"
          :progress="vectorizingMap[String(file.id)]?.progress"
          :progress-msg="vectorizingMap[String(file.id)]?.message"
          @delete="handleDelete"
          @edit="openEdit"
          @select="handleSelectDoc"
          @vectorize="handleVectorize"
          @re-vectorize="handleReVectorize"
      />
    </div>
    <VecDocFormModal
        :confirm-loading="modalSubmitting"
        :initial="modalInitial"
        :mode="modalMode"
        :open="modalOpen"
        @submit="handleSubmit"
        @update:open="(value) => (modalOpen = value)"
    />
  </div>
</template>

<script lang="ts" setup>
import {computed, onUnmounted, reactive, ref, watch} from 'vue';
import type {UploadProps} from 'ant-design-vue'
import {message, Modal} from 'ant-design-vue'
import {PlusOutlined} from '@ant-design/icons-vue';
// 确保路径指向你刚才保存 FileCard 的位置
import FileCard from '@/views/admin/ai-vector/vector-center/component/right-center/FileCard.vue';
import VecDocFormModal from '@/views/admin/ai-vector/vec-doc/VecDocFormModal.vue'
import {type AiVecDoc, type AiVecDocVectorizeProgress, aiVecDocApi} from '@/api/aiVecDoc'

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
const uploadCollectionId = ref<number | string | undefined>()
const modalOpen = ref(false)
const modalMode = ref<'create' | 'edit'>('create')
const modalInitial = ref<AiVecDoc | null>(null)
const modalSubmitting = ref(false)

// 向量化进度追踪
const vectorizingMap = reactive<Record<string, { progress: number; message: string }>>({})
const pollingTimers = ref<Record<string, ReturnType<typeof setInterval>>>({})

const setUploadCollectionId = (value: number | string | undefined) => {
  uploadCollectionId.value = value
}

const setKeyword = (value: string) => {
  keyword.value = value
}

watch(
    () => props.storeId,
    (id) => {
      uploadCollectionId.value = id
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
    await aiVecDocApi.upload(options.file as File, uploadCollectionId.value)
    options.onSuccess?.({})
    message.success('上传成功')
    emit('changed')
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || '上传失败')
    options.onError?.(error as Error)
  }
}
</script>

<style lang="less" scoped>
.document-list-container {
  padding: 0 24px 24px 24px;
  background: transparent;
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
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}
</style>