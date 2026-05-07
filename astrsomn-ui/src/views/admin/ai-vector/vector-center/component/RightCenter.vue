<template>
  <div class="document-list-container">
    <div class="list-toolbar">
      <div class="toolbar-left">
        <h2 class="section-title">文档列表</h2>
        <div class="count-badge">{{ filteredFiles.length }}</div>
      </div>
      
      <div class="toolbar-right">
        <a-select
          :value="uploadCollectionId"
          @update:value="setUploadCollectionId"
          :options="storeOptions"
          placeholder="选择集合"
          style="width: 180px"
        />
        <a-input-search 
          placeholder="搜索文件名..." 
          class="subtle-search"
          :value="keyword"
          @update:value="setKeyword"
        />
        <a-upload :custom-request="handleUpload" :show-upload-list="false">
        <a-button type="primary" class="import-btn">
          <template #icon><plus-outlined /></template>
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
        :file="file" 
        :active="String(file.id) === String(props.selectedDocId ?? '')"
        @select="handleSelectDoc"
        @edit="openEdit"
        @vectorize="handleVectorize"
        @delete="handleDelete"
      />
    </div>
    <VecDocFormModal
      :open="modalOpen"
      @update:open="(value) => (modalOpen = value)"
      :mode="modalMode"
      :initial="modalInitial"
      :confirm-loading="modalSubmitting"
      @submit="handleSubmit"
    />
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, watch } from 'vue';
import { message, Modal } from 'ant-design-vue'
import type { UploadProps } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue';
// 确保路径指向你刚才保存 FileCard 的位置
import FileCard from '@/views/admin/ai-vector/vector-center/component/right-center/FileCard.vue';
import VecDocFormModal from '@/views/admin/ai-vector/vec-doc/VecDocFormModal.vue'
import { aiVecDocApi, type AiVecDoc } from '@/api/aiVecDoc'

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
  { immediate: true }
)

const storeOptions = computed(() =>
  props.storeId == null
    ? []
    : [{ label: `当前集合 (${props.storeId})`, value: props.storeId }]
)

const filteredFiles = computed(() => {
  const list = props.docs || []
  const kw = keyword.value.trim().toLowerCase()
  return list
    .filter((doc) => {
      if (!kw) return true
      return String(doc.originalFileName || doc.contentSummary || '').toLowerCase().includes(kw)
    })
    .map((doc) => ({
      id: doc.id,
      name: doc.originalFileName || `doc-${doc.id}`,
      segments: 0,
      size: doc.filePath ? '已上传' : '待上传',
      status: String(doc.syncStatus || '').toUpperCase() === 'STORED' ? '已向量化' : '待向量化',
      uploadTime: doc.createTime,
      raw: doc
    }))
})

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

const handleVectorize = async (file: any) => {
  if (file?.id == null) return
  Modal.confirm({
    title: '确认执行向量化',
    content: `将对文档 ${file.name || file.id} 执行向量化并写入向量库。`,
    async onOk() {
      await aiVecDocApi.vectorize(file.id)
      message.success('向量化任务已完成')
      emit('changed')
      emit('select-doc', file.id)
    }
  })
}

const handleDelete = async (file: any) => {
  if (file?.id == null) return
  Modal.confirm({
    title: '确认删除文档',
    content: `删除后将同步清理切片与向量数据：${file.name || file.id}`,
    okButtonProps: { danger: true },
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
    .section-title { font-size: 16px; font-weight: 600; color: var(--text-heading); margin: 0; }
    .count-badge { background: var(--bg-input); color: var(--text-secondary); padding: 2px 10px; border-radius: var(--radius-max); font-size: 12px; font-weight: 600; }
  }

  .toolbar-right {
    display: flex;
    gap: 12px;
    .subtle-search {
      width: 240px;
      :deep(.ant-input) { border-radius: var(--radius-md); border-color: var(--border-input); background: var(--bg-input); color: var(--text-primary); }
      :deep(.ant-input::placeholder) { color: var(--text-placeholder); }
    }
    .import-btn { border-radius: var(--radius-md); font-weight: 500; }
  }
}

/* 布局网格：负责卡片的大小和排列 */
.file-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}
</style>