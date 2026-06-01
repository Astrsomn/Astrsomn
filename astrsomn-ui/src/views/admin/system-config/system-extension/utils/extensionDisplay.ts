import type {ExtensionMarketplaceItem, SystemExtension} from '@/api/systemExtension.ts'
import {getDictionary} from '@/locales/dictionary/registry.ts'

export type ExtensionRow = SystemExtension & ExtensionMarketplaceItem

export const preview = (raw: string | undefined) => {
    if (!raw) return '—'
    const text = raw.replace(/\s+/g, ' ').trim()
    return text.length > 84 ? `${text.slice(0, 84)}…` : text
}

export function statusLabel(value: string | undefined) {
    const dict = getDictionary('system.extension.installStatus')
    const label = dict.getLabel(value)
    return label ?? value ?? '—'
}

export function statusTagColor(value: string | undefined) {
    if (value === 'APPLIED') return 'green'
    if (value === 'INSTALLED') return 'blue'
    if (value === 'UNINSTALLED') return 'red'
    return 'default'
}

export function appliedLabel(value: string | undefined) {
    const dict = getDictionary('system.extension.applyStatus')
    const label = dict.getLabel(value)
    return label ?? value ?? '—'
}

export function appliedTagColor(value: string | undefined) {
    if (value === 'Y') return 'green'
    return 'default'
}

export function extensionTypeLabel(type: string | undefined) {
    const dict = getDictionary('system.extension.type')
    const label = dict.getLabel(type)
    return label ?? type ?? '—'
}

export function isUninstallableExtension(record: ExtensionRow | undefined) {
    if (!record) return false
    const source = record.installSource
    if (source) {
        return source === 'PLUGIN_JAR_UPLOAD' || source === 'PLUGIN_JAR_DISCOVERED'
    }
    return !!record.jarName
}

const MODEL_TYPE_META: Record<string, { color: string }> = {
    chat: {color: '#3b82f6'},
    completion: {color: '#6366f1'},
    embedding: {color: '#10b981'},
    image: {color: '#8b5cf6'},
    audio: {color: '#f59e0b'},
    rerank: {color: '#14b8a6'},
    'text-to-image': {color: '#a855f7'},
    'text-to-video': {color: '#ec4899'},
    'speech-to-text': {color: '#f97316'},
    'text-to-speech': {color: '#eab308'},
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
    if (!type) {
        const dict = getDictionary('system.extension.modelType')
        return dict.getLabel(type) ?? type ?? '—'
    }
    const dict = getDictionary('system.extension.modelType')
    const normalizedKey = type.toLowerCase().replace(/[_-]/g, '')
    for (const k of dict.order) {
        const dictKey = k.replace(/[_-]/g, '')
        if (normalizedKey === dictKey || normalizedKey.includes(dictKey)) {
            return dict.getLabel(k) ?? type
        }
    }
    return type
}
