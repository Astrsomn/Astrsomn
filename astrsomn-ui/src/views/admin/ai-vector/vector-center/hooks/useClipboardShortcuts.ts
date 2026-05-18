import {type ComputedRef, onUnmounted, type Ref} from 'vue'
import {message} from 'ant-design-vue'

export interface ClipboardState {
  items: any[]
  mode: 'copy' | 'cut'
}

export interface ClipboardShortcutsOptions {
  selectedIds: Ref<Set<string>>
  fileList: ComputedRef<any[]>
  folderList: Ref<any[]>
  clipboard: Ref<ClipboardState | null>
  onSelectAll: () => void
  onPaste: (targetFolderId: number | string | null) => void
  currentFolderId: Ref<number | string | null>
  enabled?: Ref<boolean>
}

export function useClipboardShortcuts(options: ClipboardShortcutsOptions) {
  const {
    selectedIds,
    fileList,
    folderList,
    clipboard,
    onSelectAll,
    onPaste,
    currentFolderId,
    enabled
  } = options

  const getSelectedItems = () => {
    const ids = selectedIds.value
    const files = fileList.value.filter(f => ids.has(String(f.id)))
    const folders = folderList.value.filter(f => ids.has(String(f.id)))
    return {files, folders}
  }

  const handleCopy = () => {
    const {files} = getSelectedItems()
    if (files.length === 0) {
      message.warning('请先选择文件')
      return
    }
    clipboard.value = {items: [...files], mode: 'copy'}
    message.success(`已复制 ${files.length} 个文件`)
  }

  const handleCut = () => {
    const {files} = getSelectedItems()
    if (files.length === 0) {
      message.warning('请先选择文件')
      return
    }
    clipboard.value = {items: [...files], mode: 'cut'}
    message.success(`已剪切 ${files.length} 个文件`)
  }

  const handlePaste = () => {
    onPaste(currentFolderId.value)
  }

  const handleKeydown = (e: KeyboardEvent) => {
    if (enabled && !enabled.value) return

    // Skip if user is typing in an input/textarea
    const tag = (e.target as HTMLElement)?.tagName
    if (tag === 'INPUT' || tag === 'TEXTAREA' || (e.target as HTMLElement)?.isContentEditable) return

    const ctrl = e.ctrlKey || e.metaKey

    if (ctrl && e.key === 'a') {
      e.preventDefault()
      onSelectAll()
    } else if (ctrl && e.key === 'c') {
      e.preventDefault()
      handleCopy()
    } else if (ctrl && e.key === 'x') {
      e.preventDefault()
      handleCut()
    } else if (ctrl && e.key === 'v') {
      e.preventDefault()
      handlePaste()
    }
  }

  document.addEventListener('keydown', handleKeydown)
  onUnmounted(() => {
    document.removeEventListener('keydown', handleKeydown)
  })

  return {
    handleCopy,
    handleCut,
    handlePaste,
    getSelectedItems
  }
}
