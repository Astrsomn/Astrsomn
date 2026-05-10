<template>
  <transition name="input-slide" appear>
    <div class="chat-input-section" :class="[`is-${props.layout}`]">
      <div class="input-panel">
        <div
          class="input-body-wrap"
          :class="{ 'is-dragging': isDragging }"
          @dragenter.prevent="canUploadImage && onDragEnter"
          @dragover.prevent="canUploadImage && onDragOver"
          @dragleave.prevent="canUploadImage && onDragLeave"
          @drop.prevent="canUploadImage && onDrop"
        >
          <div v-if="isDragging" class="drag-overlay">拖拽文件到这里上传</div>

          <div v-if="uploadedFiles.length" class="uploaded-preview">
            <div v-for="item in uploadedFiles" :key="item.id" class="preview-item">
              <a-image
                v-if="item.isImage"
                class="preview-image"
                :src="item.url"
                :alt="item.name"
                :preview="item.status === 'success'"
              />
              <div v-else class="preview-file">{{ item.name }}</div>
              <div v-if="item.status === 'uploading'" class="preview-uploading">上传中...</div>
              <div v-if="item.isImage && item.status === 'success'" class="preview-view-hint" title="点击查看大图">
                <EyeOutlined />
              </div>
              <button class="preview-remove" type="button" title="移除图片" @click="removeUploadedFile(item.id)">
                <CloseCircleFilled />
              </button>
            </div>
          </div>

          <div class="input-toolbar">
          <div class="toolbar-left">
            <a-select
              :value="selectedAgent"
              class="panel-select"
              placeholder="选择 Agent"
              :bordered="false"
              :loading="optionsLoading"
              @update:value="emit('update:selectedAgent', $event)"
            >
              <a-select-option
                v-for="agent in agentOptions"
                :key="agent.agentKey"
                :value="agent.agentKey"
              >
                {{ agent.agentName || agent.agentKey }}
              </a-select-option>
            </a-select>

            <div class="v-divider"></div>

            <div class="instance-select-with-avatar">
              <img
                v-if="selectedChatInstanceAvatarHtml"
                class="inst-select-inline-avatar"
                :src="selectedChatInstanceAvatarHtml"
                :alt="selectedChatInstanceKey"
                aria-hidden="true"
              />
              <a-select
                :value="selectedChatInstanceKey"
                class="panel-select instance-select-inner"
                placeholder="选择对话实例"
                :bordered="false"
                dropdown-class-name="custom-dropdown"
                option-label-prop="label"
                :loading="optionsLoading"
                @update:value="emit('update:selectedChatInstanceKey', $event)"
              >
                <a-select-option
                  v-for="inst in chatInstanceOptions"
                  :key="inst.instanceKey"
                  :value="inst.instanceKey"
                  :label="inst.instanceName || inst.instanceKey"
                >
                  <span class="inst-opt-row">
                    <img
                      v-if="instanceAvatarHtml(inst)"
                      class="inst-opt-avatar"
                      :src="instanceAvatarHtml(inst)"
                      :alt="inst.instanceKey"
                      aria-hidden="true"
                    />
                    <span class="inst-opt-text">{{ inst.instanceName || inst.instanceKey }}</span>
                  </span>
                </a-select-option>
              </a-select>
            </div>
          </div>
          </div>

          <div class="input-body">
            <a-textarea
              :value="draft"
              :auto-size="{ minRows: 1, maxRows: 6 }"
              placeholder="问点什么吧..."
              class="main-textarea"
              :disabled="isStreaming"
              @update:value="onDraftInput"
              @pressEnter="handleEnter"
              @paste="handlePaste"
            />
          </div>

          <div class="input-footer">
            <div class="footer-left">
              <a-upload
                v-if="canUploadImage"
                :show-upload-list="false"
                :before-upload="beforeUpload"
                :custom-request="uploadRequest"
                :accept="FILE_ACCEPT"
                class="upload-trigger"
              >
                <button class="icon-btn" title="上传图片">
                  <PaperClipOutlined />
                </button>
              </a-upload>

            <div class="feature-switches">
              <div
                v-if="canDeepThinking"
                class="feature-tag"
                :class="{ active: isDeepThinking }"
                @click="emit('update:isDeepThinking', !isDeepThinking)"
              >
                <BulbOutlined /> 深度思考
              </div>
              <div
                class="feature-tag"
                :class="{ active: isWebSearch }"
                @click="emit('update:isWebSearch', !isWebSearch)"
              >
                <GlobalOutlined /> 联网搜索
              </div>
            </div>
            </div>

            <div class="footer-right">
              <div v-if="draft.length > 0" class="char-count">
                {{ draft.length }}
              </div>
              <div v-else-if="isStreaming" class="stream-status">流式回复中</div>
              <a-button
                type="primary"
                class="send-btn"
                :disabled="sendDisabled"
                @click="isStreaming ? emit('stop') : handleSend()"
              >
                <template #icon>
                  <StopOutlined v-if="isStreaming" />
                  <ArrowUpOutlined v-else />
                </template>
              </a-button>
            </div>
          </div>
        </div>
      </div>

    </div>
  </transition>
</template>

<script setup lang="ts">
import {
  ArrowUpOutlined,
  BulbOutlined,
  CloseCircleFilled,
  EyeOutlined,
  GlobalOutlined,
  PaperClipOutlined,
  StopOutlined
} from '@ant-design/icons-vue'
import request from '@/utils/request'
import type { AiAgent } from '@/api/aiAgent.ts'
import type { AiInstance } from '@/api/aiInstance.ts'
import { message } from 'ant-design-vue'
import { computed, onBeforeUnmount, ref, watch } from 'vue'
import type { UploadProps } from 'ant-design-vue'

const props = withDefaults(defineProps<{
  layout?: 'bottom' | 'centered'
  selectedAgent?: string
  selectedChatInstanceKey?: string
  userInput: string
  isDeepThinking: boolean
  isWebSearch: boolean
  isStreaming: boolean
  optionsLoading: boolean
  sendDisabled: boolean
  fileUrlList?: string[]
  agentOptions: AiAgent[]
  chatInstanceOptions: AiInstance[]
  modelCapabilities?: string[]
}>(), {
  layout: 'bottom',
  modelCapabilities: () => []
})

const emit = defineEmits<{
  'update:selectedAgent': [value?: string]
  'update:selectedChatInstanceKey': [value?: string]
  'update:userInput': [value: string]
  'update:isDeepThinking': [value: boolean]
  'update:isWebSearch': [value: boolean]
  'update:fileUrlList': [value: string[]]
  submit: [text: string]
  stop: []
}>()

type UploadResponse = {
  fileUrl?: string
}

type UploadedFile = {
  id: string
  url: string
  name: string
  isImage: boolean
  status: 'uploading' | 'success'
  sourceHash?: string
  localPreviewUrl?: string
}

const MAX_UPLOAD_COUNT = 5
const MAX_FILE_SIZE_MB = 10
const ALLOWED_FILE_TYPES = new Set(['image/png', 'image/jpeg', 'image/webp', 'image/gif'])
const FILE_ACCEPT = '.png,.jpg,.jpeg,.webp,.gif'

const uploadedFiles = ref<UploadedFile[]>([])
const isDragging = ref(false)
const dragDepth = ref(0)

function instanceAvatarHtml(inst: AiInstance): string {
  const raw = inst.providerAvatar
  return typeof raw === 'string' && raw.trim() ? raw.trim() : ''
}

const selectedChatInstance = computed(() =>
  props.chatInstanceOptions.find((i) => i.instanceKey === props.selectedChatInstanceKey)
)

const selectedChatInstanceAvatarHtml = computed(() => {
  const inst = selectedChatInstance.value
  return inst ? instanceAvatarHtml(inst) : ''
})

const hasCapability = (code: string): boolean => {
  return props.modelCapabilities.includes(code)
}

const canUploadImage = computed(() =>
  props.modelCapabilities.length === 0 || hasCapability('vision')
)

const canDeepThinking = computed(() =>
  props.modelCapabilities.length === 0 || hasCapability('deep_reasoning')
)

/** 本地草稿：与父级 userInput 同步，但发送时先在此清空，避免仅依赖 v-model 时 a-textarea 不刷新 */
const draft = ref('')

watch(
  () => props.userInput,
  (v) => {
    const next = v ?? ''
    if (next !== draft.value) {
      draft.value = next
    }
  },
  { immediate: true }
)

const onDraftInput = (v: string) => {
  const next = v ?? ''
  draft.value = next
  emit('update:userInput', next)
}

watch(
  () => props.fileUrlList ?? [],
  (urls) => {
    const currentByUrl = new Map(uploadedFiles.value.map((item) => [item.url, item]))
    uploadedFiles.value = urls.map((url, idx) => {
      const current = currentByUrl.get(url)
      if (current) return current
      return {
        id: `${Date.now()}-${idx}`,
        url,
        name: `image-${idx + 1}`,
        isImage: true,
        status: 'success'
      }
    })
  },
  { immediate: true }
)

const emitFileUrlList = () => {
  emit(
    'update:fileUrlList',
    uploadedFiles.value.filter((item) => item.status === 'success').map((item) => item.url)
  )
}

watch(
  () => props.modelCapabilities,
  () => {
    if (!canUploadImage.value && uploadedFiles.value.length > 0) {
      uploadedFiles.value.forEach((item) => {
        if (item.localPreviewUrl) URL.revokeObjectURL(item.localPreviewUrl)
      })
      uploadedFiles.value = []
      emitFileUrlList()
    }
    if (!canDeepThinking.value && props.isDeepThinking) {
      emit('update:isDeepThinking', false)
    }
  }
)

const isAllowedFileType = (file: File) => {
  if (file.type && ALLOWED_FILE_TYPES.has(file.type)) return true
  const lowerName = file.name.toLowerCase()
  return ['.png', '.jpg', '.jpeg', '.webp', '.gif'].some((suffix) => lowerName.endsWith(suffix))
}

const validateUploadFile = (file: File): boolean => {
  if (uploadedFiles.value.length >= MAX_UPLOAD_COUNT) {
    message.warning(`最多上传 ${MAX_UPLOAD_COUNT} 张图片`)
    return false
  }
  if (!isAllowedFileType(file)) {
    message.warning('仅支持 PNG/JPG/JPEG/WEBP/GIF 图片')
    return false
  }
  const maxBytes = MAX_FILE_SIZE_MB * 1024 * 1024
  if (file.size > maxBytes) {
    message.warning(`单张图片大小不能超过 ${MAX_FILE_SIZE_MB}MB`)
    return false
  }
  return true
}

const createFileHash = (file: File) => `${file.name}|${file.size}|${file.lastModified}`

const hasDuplicateFile = (file: File) => {
  const fileHash = createFileHash(file)
  return uploadedFiles.value.some((item) => item.sourceHash === fileHash)
}

const uploadSingleFile = async (file: File) => {
  if (!validateUploadFile(file)) return
  if (hasDuplicateFile(file)) {
    message.warning(`图片 ${file.name} 已添加，请勿重复上传`)
    return
  }
  const fileHash = createFileHash(file)
  const localPreviewUrl = URL.createObjectURL(file)
  const tempId = `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`
  uploadedFiles.value.push({
    id: tempId,
    url: localPreviewUrl,
    name: file.name,
    isImage: isAllowedFileType(file),
    status: 'uploading',
    sourceHash: fileHash,
    localPreviewUrl
  })
  const fd = new FormData()
  fd.append('file', file)
  fd.append('bizType', 'chat')
  try {
    const data = (await request({
      url: '/v1/astro/file/upload',
      method: 'post',
      data: fd,
      timeout: 60000
    })) as UploadResponse
    const fileUrl = data?.fileUrl?.trim()
    if (!fileUrl) {
      throw new Error('上传成功但未返回文件地址')
    }
    const target = uploadedFiles.value.find((item) => item.id === tempId)
    if (!target) return
    target.url = fileUrl
    target.status = 'success'
    if (target.localPreviewUrl) {
      URL.revokeObjectURL(target.localPreviewUrl)
      target.localPreviewUrl = undefined
    }
    const duplicateByUrl = uploadedFiles.value.find(
      (item) => item.id !== target.id && item.status === 'success' && item.url === fileUrl
    )
    if (duplicateByUrl) {
      uploadedFiles.value = uploadedFiles.value.filter((item) => item.id !== target.id)
    }
    emitFileUrlList()
  } catch (error) {
    const target = uploadedFiles.value.find((item) => item.id === tempId)
    if (target?.localPreviewUrl) {
      URL.revokeObjectURL(target.localPreviewUrl)
    }
    uploadedFiles.value = uploadedFiles.value.filter((item) => item.id !== tempId)
    throw error
  }
}

const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  return validateUploadFile(file as File)
}

const uploadRequest: UploadProps['customRequest'] = async (option) => {
  try {
    await uploadSingleFile(option.file as File)
    option.onSuccess?.({}, new XMLHttpRequest())
  } catch (error: any) {
    message.error(error?.message || '文件上传失败')
    option.onError?.(error)
  }
}

const removeUploadedFile = (id: string) => {
  const target = uploadedFiles.value.find((item) => item.id === id)
  if (target?.localPreviewUrl) {
    URL.revokeObjectURL(target.localPreviewUrl)
  }
  uploadedFiles.value = uploadedFiles.value.filter((item) => item.id !== id)
  emitFileUrlList()
}

const handlePaste = async (event: ClipboardEvent) => {
  if (props.isStreaming) return
  if (!canUploadImage.value) return
  const files = Array.from(event.clipboardData?.files ?? [])
  if (!files.length) return
  event.preventDefault()
  for (const file of files) {
    try {
      await uploadSingleFile(file)
    } catch (error: any) {
      message.error(error?.message || `文件 ${file.name} 上传失败`)
      break
    }
  }
}

const onDragEnter = (event: DragEvent) => {
  if (props.isStreaming || !event.dataTransfer?.types.includes('Files')) return
  dragDepth.value += 1
  isDragging.value = true
}

const onDragOver = (event: DragEvent) => {
  if (props.isStreaming || !event.dataTransfer?.types.includes('Files')) return
  event.dataTransfer!.dropEffect = 'copy'
}

const onDragLeave = (_event: DragEvent) => {
  if (!isDragging.value) return
  dragDepth.value = Math.max(0, dragDepth.value - 1)
  if (dragDepth.value === 0) {
    isDragging.value = false
  }
}

const onDrop = async (event: DragEvent) => {
  isDragging.value = false
  dragDepth.value = 0
  if (props.isStreaming) return
  const files = Array.from(event.dataTransfer?.files ?? [])
  if (!files.length) return
  for (const file of files) {
    try {
      await uploadSingleFile(file)
    } catch (error: any) {
      message.error(error?.message || `文件 ${file.name} 上传失败`)
      break
    }
  }
}

const handleSend = () => {
  if (props.sendDisabled) return
  if (uploadedFiles.value.some((item) => item.status === 'uploading')) {
    message.warning('图片上传中，请稍后发送')
    return
  }
  const text = draft.value.trim()
  if (!text && uploadedFiles.value.length === 0) return
  draft.value = ''
  emit('update:userInput', '')
  emit('submit', text)
}

onBeforeUnmount(() => {
  uploadedFiles.value.forEach((item) => {
    if (item.localPreviewUrl) {
      URL.revokeObjectURL(item.localPreviewUrl)
    }
  })
})

const handleEnter = (e: KeyboardEvent) => {
  if (!e.shiftKey) {
    e.preventDefault()
    if (props.isStreaming) return
    handleSend()
  }
}
</script>

<style scoped>
.chat-input-section {
  pointer-events: auto;
  width: 100%;
}

.chat-input-section.is-bottom {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2;
  pointer-events: none;
  padding: 20px 20px 30px;
  background: transparent;
}

.chat-input-section.is-centered {
  max-width: 960px;
  margin: 0 auto;
  padding: 0 20px 16px;
}

.input-panel {
  max-width: 840px;
  margin: 0 auto;
  pointer-events: auto;
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: 24px;
  box-shadow: var(--shadow-card);
  transition: border-color 0.3s, box-shadow 0.3s;
  overflow: hidden;
  position: relative;
}

.input-body-wrap {
  position: relative;
}

.input-body-wrap.is-dragging {
  background: var(--primary-hover);
}

.drag-overlay {
  position: absolute;
  inset: 0;
  z-index: 5;
  background: color-mix(in srgb, var(--primary-hover) 75%, transparent);
  border: 2px dashed var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary);
  font-size: 14px;
  font-weight: 600;
  pointer-events: none;
}

.uploaded-preview {
  display: flex;
  align-items: center;
  gap: 10px;
  overflow-x: auto;
  padding: 12px 16px 0;
}

.preview-item {
  position: relative;
  width: 72px;
  height: 72px;
  border: 1px solid var(--border-default);
  border-radius: 10px;
  overflow: hidden;
  background: var(--bg-input);
  flex-shrink: 0;
}

.preview-image {
  width: 100%;
  height: 100%;
  display: flex;
}

.preview-image :deep(.ant-image) {
  width: 100%;
  height: 100%;
  display: block;
}

.preview-image :deep(.ant-image-img) {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.preview-file {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  font-size: 12px;
  color: var(--text-secondary);
  padding: 6px;
}

.preview-remove {
  position: absolute;
  right: 4px;
  top: 4px;
  width: 20px;
  height: 20px;
  border: none;
  border-radius: 999px;
  background: transparent;
  color: #ff4d4f;
  line-height: 1;
  text-align: center;
  cursor: pointer;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-remove:hover {
  color: #ff7875;
}

.preview-view-hint {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
  color: rgba(255, 255, 255, 0.95);
  font-size: 18px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
}

.preview-uploading {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.38);
  color: #fff;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-input-section.is-centered .input-panel {
  border-radius: 22px;
  box-shadow: 0 14px 30px rgba(59, 130, 246, 0.12);
}

.input-panel:focus-within {
  border-color: var(--primary);
  box-shadow: 0 0 0 4px var(--primary-hover);
}

/* 覆盖 Ant Design 注入的 placeholder（含 webkit/moz），否则深色下对比度不足 */
.input-panel :deep(textarea)::-webkit-input-placeholder,
.input-panel :deep(textarea)::-moz-placeholder,
.input-panel :deep(textarea)::placeholder {
  color: var(--chat-input-placeholder) !important;
  opacity: 1 !important;
}

.input-toolbar {
  padding: 12px 16px 4px;
  display: flex;
  justify-content: space-between;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.v-divider {
  width: 1px;
  height: 18px;
  background: var(--border-default);
}

.instance-select-with-avatar {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  max-width: 280px;
}

.inst-select-inline-avatar {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.inst-select-inline-avatar :deep(svg) {
  width: 18px;
  height: 18px;
  display: block;
}

.instance-select-inner.panel-select {
  flex: 1;
  min-width: 0;
}

.panel-select {
  min-width: 100px;
  font-size: 13px;
  font-weight: 500;
}

.panel-select :deep(.ant-select-selection-item) {
  color: var(--text-secondary) !important;
}

/* 下拉挂载到 body，需全局类名 */
:global(.custom-dropdown .inst-opt-row) {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

:global(.custom-dropdown .inst-opt-avatar) {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

:global(.custom-dropdown .inst-opt-avatar svg) {
  width: 18px;
  height: 18px;
  display: block;
}

:global(.custom-dropdown .inst-opt-text) {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.input-body {
  padding: 4px 16px;
}

.main-textarea {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
  font-size: 16px;
  color: var(--text-primary);
  padding: 8px 0;
  resize: none;
}

.upload-trigger :deep(.ant-upload) {
  display: flex;
}

.input-footer {
  padding: 8px 16px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon-btn {
  background: var(--bg-input);
  border: 1px solid var(--border-default);
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--text-secondary);
  transition: all 0.2s;
}

.icon-btn:hover {
  background: var(--border-subtle);
  color: var(--primary);
}

.feature-switches {
  display: flex;
  gap: 8px;
}

.feature-tag {
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 12px;
  background: var(--bg-input);
  border: 1px solid var(--border-default);
  color: var(--text-muted);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
  user-select: none;
}

.feature-tag:hover {
  border-color: var(--text-muted);
}

.feature-tag.active {
  background: var(--primary-hover);
  border-color: var(--primary);
  color: var(--primary);
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.char-count {
  font-size: 12px;
  color: var(--text-muted);
}

.stream-status {
  font-size: 12px;
  color: var(--primary);
}

.send-btn {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.send-btn.ant-btn-primary {
  background: var(--chat-send-btn-bg) !important;
  border-color: transparent !important;
  color: #fff !important;
  box-shadow: var(--chat-send-btn-shadow);
}

.send-btn.ant-btn-primary :deep(.anticon) {
  color: #fff !important;
}

.send-btn.ant-btn-primary:not(:disabled):hover {
  filter: brightness(1.08);
}

.send-btn.ant-btn-primary.ant-btn-disabled {
  opacity: 0.5;
}

.input-hint {
  pointer-events: auto;
  text-align: center;
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 12px;
}

.input-slide-enter-active {
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.input-slide-enter-from {
  opacity: 0;
  transform: translateY(100%);
}

@media (max-width: 640px) {
  .feature-tag span {
    display: none;
  }
}
</style>
