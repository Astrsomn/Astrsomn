import type { ExtensionModelSyncPreviewRow } from '@/api/systemExtension'

export function formatExtensionModelPreviewRow(r: ExtensionModelSyncPreviewRow): string {
  const parts = [r.modelKey, r.modelName, r.modelType, r.provider].filter(Boolean)
  return parts.length ? parts.join(' · ') : '—'
}
