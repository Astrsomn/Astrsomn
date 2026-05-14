import request from '@/utils/request'

export interface AiVecStore {
    id?: number | string
    sourceId?: number | string
    collectionName: string
    dimension: number
    distanceMetric: string
    metadataSchema?: string
    instanceKey?: string
    sourceName?: string
    sourceProvider?: string
    instanceName?: string
    createTime?: string
    updateTime?: string
}

export interface AiVecStoreStats {
    storeId: number | string
    docCount: number
    segmentCount: number
    totalWordCount: number
    lastSyncTime?: string
}

export interface PageResponse<T> {
    list: T[]
    total: number
    pageNum: number
    pageSize: number
}

export interface AiVecStoreQueryRequest {
    collectionName?: string
    sourceId?: number | string
    instanceKey?: string
    dimension?: number
}

export const aiVecStoreApi = {
    async create(data: AiVecStore): Promise<string> {
        const res = await request.post<{ message: string }>('/v1/astro/ai-vec-store/create', data)
        return res.message
    },

    async update(data: AiVecStore): Promise<string> {
        const res = await request.post<{ message: string }>('/v1/astro/ai-vec-store/update', data)
        return res.message
    },

    async delete(ids: Array<number | string>): Promise<string> {
        const res = await request.delete<{ message: string }>(`/v1/astro/ai-vec-store/delete/${ids.join(',')}`)
        return res.message
    },

    async detail(id: number | string): Promise<AiVecStore> {
        const res = await request.get<{ data: AiVecStore }>(`/v1/astro/ai-vec-store/detail?id=${id}`)
        return res.data
    },

    async queryPage(params: {
        pageNo: number
        pageSize: number
        param?: AiVecStoreQueryRequest
    }): Promise<PageResponse<AiVecStore>> {
        const res = await request.post<PageResponse<AiVecStore>>('/v1/astro/ai-vec-store/queryPage', params)
        return res
    },

    async stats(id: number | string): Promise<AiVecStoreStats> {
        const res = await request.get<{ data: AiVecStoreStats }>(`/v1/astro/ai-vec-store/stats?id=${id}`)
        return res.data
    }
}
