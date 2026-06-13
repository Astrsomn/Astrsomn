import request from '@/utils/request'

/** 与后端 {@code AiVecDocEnum.SyncStatus} 一致 */
export const AiVecDocSyncStatus = {
    PENDING: 'PENDING',
    CHUNKING: 'CHUNKING',
    CHUNKED: 'CHUNKED',
    VECTORING: 'VECTORING',
    STORED: 'STORED',
    FAILED: 'FAILED',
    INVALID: 'INVALID'
} as const

export type AiVecDoc = {
    id?: number | string
    collectionId?: number | string
    docIdInStore?: string
    contentSummary?: string
    syncStatus?: string
    filePath?: string
    originalFileName?: string
    vectorizeProgress?: number
    vectorizeMsg?: string
    totalSegments?: number
    doneSegments?: number
    folderId?: number | string | null
    createTime?: string
    updateTime?: string
    createUser?: string
    updateUser?: string
    envCode?: string
    /** Whether the original file name was auto-renamed to avoid a duplicate */
    renamed?: boolean
}

export type AiVecDocVectorizeProgress = {
    taskId?: string
    status?: string
    progress?: number
    message?: string
    totalSegments?: number
    doneSegments?: number
}

export type PageResponse<T> = {
    total: number
    pageSize: number
    pageNum: number
    pages: number
    hasNext: boolean
    list: T[]
}

export const aiVecDocApi = {
    queryPage: (payload: unknown): Promise<PageResponse<AiVecDoc>> => {
        return request({
            url: '/v1/astro/ai-vec-doc/queryPage',
            method: 'post',
            data: payload
        })
    },

    detail: (id: number | string): Promise<AiVecDoc> => {
        return request({
            url: `/v1/astro/ai-vec-doc/detail?id=${encodeURIComponent(String(id))}`,
            method: 'get'
        })
    },

    create: (payload: AiVecDoc): Promise<string> => {
        return request({
            url: '/v1/astro/ai-vec-doc/create',
            method: 'post',
            data: payload
        })
    },

    update: (payload: AiVecDoc): Promise<string> => {
        return request({
            url: '/v1/astro/ai-vec-doc/update',
            method: 'post',
            data: payload
        })
    },

    delete: (ids: Array<number | string>): Promise<string> => {
        const joined = ids.map((x) => String(x)).join(',')
        return request({
            url: `/v1/astro/ai-vec-doc/delete/${joined}`,
            method: 'delete'
        })
    },

    upload: (file: File | Blob, collectionId: number | string, folderId?: number | string | null): Promise<AiVecDoc> => {
        const fd = new FormData()
        fd.append('file', file)
        fd.append('collectionId', String(collectionId))
        if (folderId != null) fd.append('folderId', String(folderId))
        return request({
            url: '/v1/astro/ai-vec-doc/upload',
            method: 'post',
            data: fd,
            timeout: 120000
        })
    },

    vectorize: (id: number | string): Promise<string> => {
        return request({
            url: '/v1/astro/ai-vec-doc/vectorize',
            method: 'post',
            data: {id},
            timeout: 300000
        })
    },

    vectorizeProgress: (id: number | string): Promise<AiVecDocVectorizeProgress> => {
        return request({
            url: `/v1/astro/ai-vec-doc/vectorize-progress?id=${encodeURIComponent(String(id))}`,
            method: 'get'
        })
    },

    reVectorize: (id: number | string): Promise<string> => {
        return request({
            url: '/v1/astro/ai-vec-doc/re-vectorize',
            method: 'post',
            data: {id},
            timeout: 300000
        })
    },

    chunk: (id: number | string): Promise<string> => {
        return request({
            url: '/v1/astro/ai-vec-doc/chunk',
            method: 'post',
            data: {id},
            timeout: 300000
        })
    },

    reChunk: (id: number | string): Promise<string> => {
        return request({
            url: '/v1/astro/ai-vec-doc/re-chunk',
            method: 'post',
            data: {id},
            timeout: 300000
        })
    },

    download: async (id: number | string): Promise<void> => {
        const token = localStorage.getItem('token')
        const ws = localStorage.getItem('workspaceEnv')
        const headers: Record<string, string> = {}
        if (token) headers['Authorization'] = `Bearer ${token}`
        if (ws) headers['x-workspace-env'] = ws
        const url = `/v1/astro/ai-vec-doc/download?id=${encodeURIComponent(String(id))}`
        const response = await fetch(url, { headers })
        if (!response.ok) throw new Error('Download failed')
        const blob = await response.blob()
        const disposition = response.headers.get('Content-Disposition')
        let filename = 'download'
        if (disposition) {
            const match = disposition.match(/filename\*=UTF-8''(.+)/) || disposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/)
            if (match && match[1]) {
                filename = decodeURIComponent(match[1].replace(/['"]/g, ''))
            }
        }
        const blobUrl = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = blobUrl
        a.download = filename
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        window.URL.revokeObjectURL(blobUrl)
    }
}
