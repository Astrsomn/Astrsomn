import request from '@/utils/request'

export interface AiVecSegment {
    id?: number | string
    docId?: number | string
    collectionId?: number | string
    vectorId?: string
    segmentContent?: string
    wordCount?: number
    chunkIndex?: number
    metadataJson?: string
}

export interface PageResponse<T> {
    list: T[]
    total: number
    pageNum: number
    pageSize: number
}

export interface AiVecSegmentQueryRequest {
    docId?: number | string
    collectionId?: number | string
    vectorId?: string
    chunkIndex?: number
}

export interface AiVecSegmentSearchRequest {
    collectionId: number | string
    queryText: string
    topK?: number
    minScore?: number
}

export interface AiVecSegmentSearchResult {
    segmentId?: number
    docId?: number
    segmentContent?: string
    score?: number
    metadataJson?: string
    originalFileName?: string
}

export const aiVecSegmentApi = {
    async create(data: AiVecSegment): Promise<string> {
        const res = await request.post<string>('/v1/astro/ai-vec-segment/create', data)
        return res
    },

    async update(data: AiVecSegment): Promise<string> {
        const res = await request.post<string>('/v1/astro/ai-vec-segment/update', data)
        return res
    },

    async delete(ids: Array<number | string>): Promise<string> {
        const res = await request.delete<string>(`/v1/astro/ai-vec-segment/delete/${ids.join(',')}`)
        return res
    },

    async detail(id: number | string): Promise<AiVecSegment> {
        const res = await request.get<AiVecSegment>(`/v1/astro/ai-vec-segment/detail?id=${id}`)
        return res
    },

    async queryPage(params: {
        pageNo: number
        pageSize: number
        param?: AiVecSegmentQueryRequest
    }): Promise<PageResponse<AiVecSegment>> {
        const res = await request.post<PageResponse<AiVecSegment>>('/v1/astro/ai-vec-segment/queryPage', params)
        return res
    },

    async search(params: AiVecSegmentSearchRequest): Promise<AiVecSegmentSearchResult[]> {
        const res = await request.post<AiVecSegmentSearchResult[]>('/v1/astro/ai-vec-segment/search', params)
        return res
    }
}
