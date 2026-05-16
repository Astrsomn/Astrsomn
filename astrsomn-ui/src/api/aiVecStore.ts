import request from '@/utils/request'

export interface AiVecStore {
    id?: number | string
    sourceId?: number | string
    collectionName: string
    dimension: number
    distanceMetric: string
    metadataSchema?: string
    instanceKey?: string
    modelKey?: string
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
    physicalVectorCount?: number
    collectionExists?: boolean
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
        const res = await request.post<string>('/v1/astro/ai-vec-store/create', data)
        return res
    },

    async update(data: AiVecStore): Promise<string> {
        const res = await request.post<string>('/v1/astro/ai-vec-store/update', data)
        return res
    },

    async delete(ids: Array<number | string>): Promise<string> {
        const res = await request.delete<string>(`/v1/astro/ai-vec-store/delete/${ids.join(',')}`)
        return res
    },

    async detail(id: number | string): Promise<AiVecStore> {
        const res = await request.get<AiVecStore>(`/v1/astro/ai-vec-store/detail?id=${id}`)
        return res
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
        const res = await request.get<AiVecStoreStats>(`/v1/astro/ai-vec-store/stats?id=${id}`)
        return res
    }
}
