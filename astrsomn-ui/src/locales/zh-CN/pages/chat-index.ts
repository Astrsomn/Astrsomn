/**
 * Chat Index 页面翻译（中文）
 */
export const chatIndexPage = {
  newSession: {
    title: '今天想聊点什么？',
    subtitle: '输入问题即可开启新会话，你可以选择不同 Agent 与模型实例。'
  },
  sidebar: {
    newChat: '新会话',
    recentSessions: '最近会话',
    settings: '设置',
    theme: '主题',
    language: '语言',
    bottomNavAutoHide: '底栏自动隐藏',
    light: '浅色',
    dark: '深色',
    chinese: '简体中文',
    english: 'English'
  },
  inputPanel: {
    placeholder: '问点什么吧...',
    selectAgent: '选择 Agent',
    selectInstance: '选择对话实例',
    uploadImage: '上传图片',
    deepThinking: '深度思考',
    webSearch: '联网搜索',
    dragUpload: '拖拽文件到这里上传',
    uploading: '上传中...',
    viewImage: '点击查看大图',
    removeImage: '移除图片'
  },
  messages: {
    justNow: '刚刚',
    minutesAgo: '{n}分钟前',
    hoursAgo: '{n}小时前',
    daysAgo: '{n}天前',
    noContent: '本次没有返回内容。',
    streamError: '流式响应异常',
    requestFailed: '请求失败，请稍后重试'
  },
  confirm: {
    deleteSession: '删除此会话？',
    deleteConfirm: '删除后无法恢复'
  },
  notifications: {
    sessionDeleted: '会话已删除',
    loadSessionFailed: '加载会话失败',
    loadOptionsFailed: '加载聊天配置失败',
    chatNotFound: '会话不存在',
    imageUploading: '图片上传中，请稍后发送',
    maxImages: '最多上传 {n} 张图片',
    invalidFileType: '仅支持 PNG/JPG/JPEG/WEBP/GIF 图片',
    fileTooLarge: '单张图片大小不能超过 {n}MB',
    duplicateFile: '图片 {name} 已添加，请勿重复上传',
    uploadFailed: '文件上传失败',
    fileUploadFailed: '文件 {name} 上传失败',
    uploadSuccessNoUrl: '上传成功但未返回文件地址'
  }
}

export type ChatIndexPageTranslation = typeof chatIndexPage