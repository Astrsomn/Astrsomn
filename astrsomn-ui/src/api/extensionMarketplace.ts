import request from '@/utils/request'
import type {PageResponse} from './systemExtension'

export type ExtensionMarketplaceItem = {
    /** 对应 marketplace 返回的 pluginId，也是本地插件的 extensionKey */
    pluginId?: string
    extensionName?: string
    type?: string
    version?: string
    latestVersion?: string
    installedVersion?: string
    upgradeAvailable?: boolean
    author?: string
    description?: string
    jarName?: string
    /** 对应 marketplace 返回的 providerCode，也是本地插件的 extensionCode */
    providerCode?: string
    avatar?: string
    installed?: boolean
}

export type ExtensionMarketplaceVersion = {
    pluginId?: string
    version?: string
    downloadUrl?: string
    resolvedDownloadUrl?: string
    changelog?: string
    minServerVersion?: string
    status?: string
    downloadCount?: number
}

/**
 * 对应 SystemExtensionMarketController：`/v1/astro/extension-marketplace`
 */
export const extensionMarketplaceApi = {
    catalog: (type?: string, pageNo: number = 1, pageSize: number = 10): Promise<PageResponse<ExtensionMarketplaceItem>> => {
        let q = type && type !== 'ALL' ? `?type=${encodeURIComponent(type)}` : ''
        q += (q ? '&' : '?') + `pageNo=${pageNo}&pageSize=${pageSize}`
        return request({
            url: `/v1/astro/extension-marketplace/catalog${q}`,
            method: 'get'
        })
    },

    install: (pluginId: string, version: string): Promise<string> => {
        return request({
            url: '/v1/astro/extension-marketplace/install',
            method: 'post',
            params: {pluginId, version}
        })
    },

    getVersions: (pluginId: string, pageNo: number = 1, pageSize: number = 20): Promise<PageResponse<ExtensionMarketplaceVersion>> => {
        return request({
            url: `/v1/astro/extension-marketplace/versions/${encodeURIComponent(pluginId)}`,
            method: 'get',
            params: {pageNo, pageSize}
        })
    }
}
