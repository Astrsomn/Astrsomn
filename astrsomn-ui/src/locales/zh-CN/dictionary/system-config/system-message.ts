/**
 * 系统消息枚举 - 与后端 SystemMessageEnum 对齐（中文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const messageTypeLabels = {
    PLUGIN_INSTALLED: '插件已安装',
    PLUGIN_INSTALL_FAILED: '插件安装失败',
    PLUGIN_UNINSTALLED: '插件已卸载',
    DEPLOYMENT_ONLINE: '上线通知',
    API_CALL_FAILED: '调用失败',
    SYSTEM_NOTICE: '系统通知',
    OTHER: '其他'
} as const

export const systemMessageTypeDictionary = createEnumDictionary({
    id: 'system.message.type',
    labels: messageTypeLabels,
    order: [
        'PLUGIN_INSTALLED',
        'PLUGIN_INSTALL_FAILED',
        'PLUGIN_UNINSTALLED',
        'DEPLOYMENT_ONLINE',
        'API_CALL_FAILED',
        'SYSTEM_NOTICE',
        'OTHER'
    ]
})

const messageLevelLabels = {
    INFO: '信息',
    SUCCESS: '成功',
    WARN: '警告',
    ERROR: '错误'
} as const

export const systemMessageLevelDictionary = createEnumDictionary({
    id: 'system.message.level',
    labels: messageLevelLabels,
    order: ['INFO', 'SUCCESS', 'WARN', 'ERROR']
})

const readStatusLabels = {
    UNREAD: '未读',
    READ: '已读'
} as const

export const systemMessageReadStatusDictionary = createEnumDictionary({
    id: 'system.message.readStatus',
    labels: readStatusLabels,
    order: ['UNREAD', 'READ']
})

const refTypeLabels = {
    EXTENSION: '扩展/插件',
    AI_INSTANCE: '模型实例',
    AI_AGENT: 'Agent',
    AI_CONVERSATION: '对话',
    AI_MCP: 'MCP',
    SYSTEM: '系统',
    OTHER: '其他'
} as const

export const systemMessageRefTypeDictionary = createEnumDictionary({
    id: 'system.message.refType',
    labels: refTypeLabels,
    order: [
        'EXTENSION',
        'AI_INSTANCE',
        'AI_AGENT',
        'AI_CONVERSATION',
        'AI_MCP',
        'SYSTEM',
        'OTHER'
    ]
})
