/**
 * 系统消息枚举 - 与后端 SystemMessageEnum 对齐（英文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const messageTypeLabels = {
    PLUGIN_INSTALLED: 'Plugin Installed',
    PLUGIN_INSTALL_FAILED: 'Plugin Installation Failed',
    PLUGIN_UNINSTALLED: 'Plugin Uninstalled',
    DEPLOYMENT_ONLINE: 'Deployment Online',
    API_CALL_FAILED: 'API Call Failed',
    SYSTEM_NOTICE: 'System Notice',
    OTHER: 'Other'
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
    INFO: 'Info',
    SUCCESS: 'Success',
    WARN: 'Warning',
    ERROR: 'Error'
} as const

export const systemMessageLevelDictionary = createEnumDictionary({
    id: 'system.message.level',
    labels: messageLevelLabels,
    order: ['INFO', 'SUCCESS', 'WARN', 'ERROR']
})

const readStatusLabels = {
    UNREAD: 'Unread',
    READ: 'Read'
} as const

export const systemMessageReadStatusDictionary = createEnumDictionary({
    id: 'system.message.readStatus',
    labels: readStatusLabels,
    order: ['UNREAD', 'READ']
})

const refTypeLabels = {
    EXTENSION: 'Extension/Plugin',
    AI_INSTANCE: 'AI Instance',
    AI_AGENT: 'Agent',
    AI_CONVERSATION: 'Conversation',
    AI_MCP: 'MCP',
    SYSTEM: 'System',
    OTHER: 'Other'
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
