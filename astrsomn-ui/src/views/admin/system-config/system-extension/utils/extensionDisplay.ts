import type {ExtensionMarketplaceItem, SystemExtension} from '@/api/systemExtension.ts'

export type ExtensionRow = SystemExtension & ExtensionMarketplaceItem

export const preview = (raw: string | undefined) => {
    if (!raw) return '—'
    const text = raw.replace(/\s+/g, ' ').trim()
    return text.length > 84 ? `${text.slice(0, 84)}…` : text
}

export function statusLabel(value: string | undefined) {
    if (value === 'INSTALLED') return '已安装'
    if (value === 'APPLIED') return '已应用'
    if (value === 'UNINSTALLED') return '未安装'
    return value ?? '—'
}

export function statusTagColor(value: string | undefined) {
    if (value === 'APPLIED') return 'green'
    if (value === 'INSTALLED') return 'blue'
    if (value === 'UNINSTALLED') return 'red'
    return 'default'
}

export function appliedLabel(value: string | undefined) {
    if (value === 'Y') return '已应用'
    if (value === 'N') return '未应用'
    return value ?? '—'
}

export function appliedTagColor(value: string | undefined) {
    if (value === 'Y') return 'green'
    return 'default'
}

export function extensionTypeLabel(type: string | undefined) {
    if (!type) return '—'
    if (type === 'MODEL_PROVIDER') return '模型'
    if (type === 'VECTOR_STORE') return '向量库'
    if (type === 'MCP') return 'MCP'
    return type
}

export function isUninstallableExtension(record: ExtensionRow | undefined) {
    const source = record?.installSource
    return source === 'PLUGIN_JAR_UPLOAD' || source === 'PLUGIN_JAR_DISCOVERED'
}

const MODEL_TYPE_META: Record<string, { color: string; label: string }> = {
    chat: {color: '#3b82f6', label: '对话'},
    completion: {color: '#6366f1', label: '补全'},
    embedding: {color: '#10b981', label: '嵌入'},
    image: {color: '#8b5cf6', label: '图像'},
    audio: {color: '#f59e0b', label: '音频'},
    rerank: {color: '#14b8a6', label: '重排'},
    'text-to-image': {color: '#a855f7', label: '文生图'},
    'text-to-video': {color: '#ec4899', label: '文生视频'},
    'speech-to-text': {color: '#f97316', label: '语音转文字'},
    'text-to-speech': {color: '#eab308', label: '文字转语音'},
}

export function modelTypeColor(type: string | undefined): string {
    if (!type) return '#64748b'
    const key = type.toLowerCase().replace(/[_-]/g, '')
    for (const [k, v] of Object.entries(MODEL_TYPE_META)) {
        if (key === k.replace(/[_-]/g, '') || key.includes(k.replace(/[_-]/g, ''))) return v.color
    }
    return '#64748b'
}

export function modelTypeLabel(type: string | undefined): string {
    if (!type) return '未知'
    const key = type.toLowerCase().replace(/[_-]/g, '')
    for (const [k, v] of Object.entries(MODEL_TYPE_META)) {
        if (key === k.replace(/[_-]/g, '') || key.includes(k.replace(/[_-]/g, ''))) return v.label
    }
    return type
}
