/**
 * Chat Index 页面翻译（英文）
 */
export const chatIndexPage = {
  newSession: {
    title: 'What would you like to chat about today?',
    subtitle: 'Enter a question to start a new conversation. You can choose different Agents and model instances.'
  },
  sidebar: {
    newChat: 'New Chat',
    recentSessions: 'Recent Sessions',
    settings: 'Settings',
    theme: 'Theme',
    language: 'Language',
    bottomNavAutoHide: 'Auto-hide bottom nav',
    light: 'Light',
    dark: 'Dark',
    chinese: '简体中文',
    english: 'English'
  },
  inputPanel: {
    placeholder: 'Ask something...',
    selectAgent: 'Select Agent',
    selectInstance: 'Select Chat Instance',
    uploadImage: 'Upload Image',
    deepThinking: 'Deep Thinking',
    webSearch: 'Web Search',
    dragUpload: 'Drop files here to upload',
    uploading: 'Uploading...',
    viewImage: 'Click to view large image',
    removeImage: 'Remove image'
  },
  messages: {
    justNow: 'Just now',
    minutesAgo: '{n} minutes ago',
    hoursAgo: '{n} hours ago',
    daysAgo: '{n} days ago',
    noContent: 'No content returned.',
    streamError: 'Stream response error',
    requestFailed: 'Request failed, please try again later'
  },
  confirm: {
    deleteSession: 'Delete this session?',
    deleteConfirm: 'Cannot be recovered after deletion'
  },
  notifications: {
    sessionDeleted: 'Session deleted',
    loadSessionFailed: 'Failed to load session',
    loadOptionsFailed: 'Failed to load chat configuration',
    chatNotFound: 'Session not found',
    imageUploading: 'Image uploading, please wait to send',
    maxImages: 'Maximum {n} images allowed',
    invalidFileType: 'Only PNG/JPG/JPEG/WEBP/GIF images are supported',
    fileTooLarge: 'Single image size cannot exceed {n}MB',
    duplicateFile: 'Image {name} already added, please do not upload again',
    uploadFailed: 'File upload failed',
    fileUploadFailed: 'Failed to upload file {name}',
    uploadSuccessNoUrl: 'Upload successful but no file URL returned'
  }
}

export type ChatIndexPageTranslation = typeof chatIndexPage