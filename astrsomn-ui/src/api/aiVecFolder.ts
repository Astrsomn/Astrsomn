import request from '@/utils/request'

export type AiVecFolder = {
    id?: number | string
    collectionId?: number | string
    folderName?: string
    parentId?: number | string | null
    sortOrder?: number
    childCount?: number
    docCount?: number
    createTime?: string
}

export const aiVecFolderApi = {
    list: (collectionId: number | string, parentId?: number | string | null): Promise<AiVecFolder[]> => {
        const params: Record<string, any> = {collectionId}
        if (parentId != null) params.parentId = parentId
        return request({
            url: '/v1/astro/ai-vec-folder/list',
            method: 'get',
            params
        })
    },

    create: (payload: AiVecFolder): Promise<string> => {
        return request({
            url: '/v1/astro/ai-vec-folder/create',
            method: 'post',
            data: payload
        })
    },

    update: (payload: AiVecFolder): Promise<string> => {
        return request({
            url: '/v1/astro/ai-vec-folder/update',
            method: 'post',
            data: payload
        })
    },

    delete: (ids: Array<number | string>): Promise<string> => {
        const joined = ids.map((x) => String(x)).join(',')
        return request({
            url: `/v1/astro/ai-vec-folder/delete/${joined}`,
            method: 'delete'
        })
    },

    moveDocs: (docIds: Array<number | string>, folderId: number | string | null): Promise<string> => {
        return request({
            url: '/v1/astro/ai-vec-folder/move-docs',
            method: 'post',
            data: {docIds: docIds.map(Number), folderId: folderId != null ? Number(folderId) : null}
        })
    }
}
