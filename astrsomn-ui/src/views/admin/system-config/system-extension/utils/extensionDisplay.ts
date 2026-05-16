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
