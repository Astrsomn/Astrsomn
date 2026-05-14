/**
 * 系统配置中心大屏数据：主列表为「系统用户 / 业务系统」（与 SystemUserController 对齐，一用户对应一业务系统）。
 */
import {
    ApiOutlined,
    AppstoreOutlined,
    ClusterOutlined,
    DatabaseOutlined,
    RobotOutlined,
    SafetyCertificateOutlined,
    SettingOutlined,
    ShareAltOutlined,
    UserOutlined
} from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'
import type {Component} from 'vue'
import {computed, onMounted, reactive, toRefs} from 'vue'
import {message} from 'ant-design-vue'
import {type SystemConfig, systemConfigApi} from '@/api/systemConfig'
import {type SystemEnv, systemEnvApi} from '@/api/systemEnv'
import {type SystemMessage, systemMessageApi} from '@/api/systemMessage'
import {type SystemUser, systemUserApi, type SystemUserQueryParam} from '@/api/systemUser'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

export type DistColorToken = 'green' | 'cyan' | 'violet' | 'amber'

export type EnvDistribution = {
    total: number
    items: Array<{
        label: string
        value: number
        pct: number
        colorToken: DistColorToken
    }>
}

export type ResourceUsagePanel = {
    apiCalls: { used: number; limit: number }
    concurrency: { used: number; limit: number }
    storage: { usedGB: number; limitGB: number }
}

export type SystemStatusUi = 'online' | 'offline' | 'maintenance'
export type SystemEnvUi = 'prod' | 'pre' | 'test' | 'dev'
export type IconTheme = 'blue' | 'green' | 'cyan' | 'amber'
export type OwnerDot = 'blue' | 'green' | 'violet' | 'orange'

export type RecentChangeItem = {
    title: string
    env: SystemEnvUi
    timeText: string
    icon: Component
}

export type RecentChanges = { items: RecentChangeItem[] }

export type OnlineSystem = {
    id: string
    name: string
    slug: string
    ownerName: string
    ownerDot: OwnerDot
    env: SystemEnvUi
    status: SystemStatusUi
    todayCalls: number
    errorRate: number
    lastAccess: string
    icon: Component
    iconTheme: IconTheme
}

const ICONS = [
    AppstoreOutlined,
    DatabaseOutlined,
    UserOutlined,
    SafetyCertificateOutlined,
    ApiOutlined,
    ClusterOutlined,
    RobotOutlined,
    SettingOutlined
] as const

const THEMES: IconTheme[] = ['blue', 'green', 'cyan', 'amber']

const RECENT_ICONS = [ShareAltOutlined, DatabaseOutlined, AppstoreOutlined, ClusterOutlined] as const

/** 与后端 SYS_CONFIG 约定：有值则覆盖占位 */
export const DASHBOARD_CONFIG_GROUP = 'dashboard'

export const DASHBOARD_CONFIG_KEYS = {
    apiUsed: 'dashboard.api.used',
    apiLimit: 'dashboard.api.limit',
    concurrencyUsed: 'dashboard.concurrency.used',
    concurrencyLimit: 'dashboard.concurrency.limit',
    storageUsedGb: 'dashboard.storage.usedGb',
    storageLimitGb: 'dashboard.storage.limitGb'
} as const

const DEFAULT_RESOURCE: ResourceUsagePanel = {
    apiCalls: {used: 0, limit: 500_000},
    concurrency: {used: 0, limit: 2000},
    storage: {usedGB: 0, limitGB: 500}
}

const OWNER_DOTS: OwnerDot[] = ['blue', 'green', 'violet', 'orange']

function hashString(s: string): number {
    let h = 0
    for (let i = 0; i < s.length; i++) {
        h = (h << 5) - h + s.charCodeAt(i)
        h |= 0
    }
    return Math.abs(h)
}

/** SystemUserResponseDTO.systemStatus + 逻辑删除 → 业务系统列表状态 */
export function mapUserSystemStatusToUi(
    systemStatus: string | undefined,
    deleted: boolean | undefined
): SystemStatusUi {
    const s = (systemStatus || '').toUpperCase()
    if (s === 'MAINTENANCE') return 'maintenance'
    if (s === 'OFFLINE') return 'offline'
    if (s === 'ONLINE') return 'online'
    if (deleted === true) return 'offline'
    return 'online'
}

export function normalizeEnvCode(raw: string | undefined): SystemEnvUi {
    if (raw == null || String(raw).trim() === '') return 'dev'
    const t = String(raw).toUpperCase()
    if (/PROD|生产|PRD/.test(t)) return 'prod'
    if (/PRE|STAGE|STAGING|预|UAT/.test(t)) return 'pre'
    if (/TEST|QA|测|SIT/.test(t)) return 'test'
    if (/DEV|开发|LOCAL/.test(t)) return 'dev'
    return 'dev'
}

/** 维护中 Tab：在 systemStatus 未接库前用不可能命中的用户名占位，避免与「在线」列表重复 */
const MAINTENANCE_LIST_PLACEHOLDER_USERNAME = '__biz_system_maintenance_tab_placeholder__'

function systemUserToOnlineSystem(user: SystemUser): OnlineSystem {
    const idStr = user.id != null ? String(user.id) : user.username || '0'
    const h = hashString(idStr)
    const ownerDot = OWNER_DOTS[h % OWNER_DOTS.length]
    const icon = ICONS[h % ICONS.length] as Component
    const iconTheme = THEMES[h % THEMES.length]

    const lastFromDto = user.lastAccessTime?.trim()
    const updated = user.updateTime || user.createTime
    let lastAccess = '—'
    if (lastFromDto) {
        lastAccess = lastFromDto
    } else if (updated) {
        const d = dayjs(updated)
        if (d.isValid()) lastAccess = d.fromNow()
    }

    const name = user.systemDisplayName?.trim() || user.username?.trim() || user.email?.trim() || '—'
    const slug = user.username?.trim() || idStr
    const ownerName = user.createUser?.trim() || user.updateUser?.trim() || user.email?.trim() || '—'
    const err = user.errorRate
    const errorRate = err != null && Number.isFinite(Number(err)) ? Number(err) : 0
    const todayCalls = user.todayApiCalls != null && Number.isFinite(Number(user.todayApiCalls)) ? Number(user.todayApiCalls) : 0

    return {
        id: idStr,
        name,
        slug,
        ownerName,
        ownerDot,
        env: normalizeEnvCode(user.envCode as string | undefined),
        status: mapUserSystemStatusToUi(user.systemStatus, user.deleted),
        todayCalls,
        errorRate,
        lastAccess,
        icon,
        iconTheme
    }
}

function buildEnvDistribution(envs: SystemEnv[]): EnvDistribution {
    let prod = 0
    let pre = 0
    let test = 0
    let dev = 0
    for (const e of envs) {
        const key = `${e.envKey || ''} ${e.envName || ''}`
        const bucket = normalizeEnvCode(key)
        if (bucket === 'prod') prod++
        else if (bucket === 'pre') pre++
        else if (bucket === 'test') test++
        else dev++
    }
    const total = envs.length
    const pct = (n: number) => (total > 0 ? Math.round((n / total) * 1000) / 10 : 0)
    return {
        total,
        items: [
            {label: '生产环境', value: prod, pct: pct(prod), colorToken: 'green'},
            {label: '预生产环境', value: pre, pct: pct(pre), colorToken: 'cyan'},
            {label: '测试环境', value: test, pct: pct(test), colorToken: 'violet'},
            {label: '开发环境', value: dev, pct: pct(dev), colorToken: 'amber'}
        ]
    }
}

function parseNum(v: string | undefined): number | undefined {
    if (v == null || v.trim() === '') return undefined
    const n = Number(v)
    return Number.isFinite(n) ? n : undefined
}

function parseResourceFromConfigs(configs: SystemConfig[]): ResourceUsagePanel {
    const byKey = new Map<string, string>()
    for (const c of configs) {
        const k = c.configKey?.trim()
        if (k) byKey.set(k, c.configValue ?? '')
    }
    const out = {...DEFAULT_RESOURCE}
    const uApi = parseNum(byKey.get(DASHBOARD_CONFIG_KEYS.apiUsed))
    const lApi = parseNum(byKey.get(DASHBOARD_CONFIG_KEYS.apiLimit))
    const uConc = parseNum(byKey.get(DASHBOARD_CONFIG_KEYS.concurrencyUsed))
    const lConc = parseNum(byKey.get(DASHBOARD_CONFIG_KEYS.concurrencyLimit))
    const uSt = parseNum(byKey.get(DASHBOARD_CONFIG_KEYS.storageUsedGb))
    const lSt = parseNum(byKey.get(DASHBOARD_CONFIG_KEYS.storageLimitGb))
    if (uApi != null) out.apiCalls.used = uApi
    if (lApi != null && lApi > 0) out.apiCalls.limit = lApi
    if (uConc != null) out.concurrency.used = uConc
    if (lConc != null && lConc > 0) out.concurrency.limit = lConc
    if (uSt != null) out.storage.usedGB = uSt
    if (lSt != null && lSt > 0) out.storage.limitGB = lSt
    return out
}

function messagesToRecentChanges(msgs: SystemMessage[]): RecentChanges {
    const items: RecentChangeItem[] = (msgs || []).map((m, i) => {
        const t = m.title?.trim() || '（无标题）'
        const env = normalizeEnvCode(m.envCode as string | undefined)
        const ct = m.createTime
        let timeText = '—'
        if (ct) {
            const d = dayjs(ct)
            if (d.isValid()) timeText = d.fromNow()
        }
        const icon = RECENT_ICONS[i % RECENT_ICONS.length] as Component
        return {title: t, env, timeText, icon}
    })
    return {items}
}

/** 与 SystemUserQueryRequestDTO / Mapper 对齐；维护中 Tab 在 systemStatus 列就绪前使用占位用户名 */
function userQueryParamForStatusFilter(
    filter: 'all' | 'online' | 'offline' | 'maintenance'
): SystemUserQueryParam {
    if (filter === 'all') return {}
    if (filter === 'online') return {deleted: false}
    if (filter === 'offline') return {deleted: true}
    return {deleted: false, username: MAINTENANCE_LIST_PLACEHOLDER_USERNAME}
}

export function useSystemConfigCenter() {
    const state = reactive({
        loading: false,
        statusFilter: 'all' as 'all' | 'online' | 'offline' | 'maintenance',
        pageSize: 10,
        currentPage: 1,
        filteredTotal: 0,
        pagedOnlineSystems: [] as OnlineSystem[],
        statusCounts: {
            all: 0,
            online: 0,
            offline: 0,
            maintenance: 0
        },
        envDistribution: {
            total: 0,
            items: [
                {label: '生产环境', value: 0, pct: 0, colorToken: 'green' as const},
                {label: '预生产环境', value: 0, pct: 0, colorToken: 'cyan' as const},
                {label: '测试环境', value: 0, pct: 0, colorToken: 'violet' as const},
                {label: '开发环境', value: 0, pct: 0, colorToken: 'amber' as const}
            ]
        } as EnvDistribution,
        resourceUsage: {...DEFAULT_RESOURCE} as ResourceUsagePanel,
        recentChanges: {items: [] as RecentChangeItem[]} as RecentChanges
    })

    const totalPages = computed(() => Math.max(1, Math.ceil(state.filteredTotal / state.pageSize)))

    const fetchStatusCounts = async () => {
        const [allR, onR, offR, maintR] = await Promise.all([
            systemUserApi.queryPage({pageNo: 1, pageSize: 1, param: userQueryParamForStatusFilter('all')}),
            systemUserApi.queryPage({pageNo: 1, pageSize: 1, param: userQueryParamForStatusFilter('online')}),
            systemUserApi.queryPage({pageNo: 1, pageSize: 1, param: userQueryParamForStatusFilter('offline')}),
            systemUserApi.queryPage({pageNo: 1, pageSize: 1, param: userQueryParamForStatusFilter('maintenance')})
        ])
        state.statusCounts = {
            all: allR.total ?? 0,
            online: onR.total ?? 0,
            offline: offR.total ?? 0,
            maintenance: maintR.total ?? 0
        }
    }

    const fetchSystemUserPage = async () => {
        const param = userQueryParamForStatusFilter(state.statusFilter)
        const resp = await systemUserApi.queryPage({
            pageNo: state.currentPage,
            pageSize: state.pageSize,
            param
        })
        const total = resp.total ?? 0
        state.filteredTotal = total
        const maxPage = Math.max(1, Math.ceil(total / state.pageSize) || 1)
        if (state.currentPage > maxPage) {
            state.currentPage = maxPage
            const resp2 = await systemUserApi.queryPage({
                pageNo: state.currentPage,
                pageSize: state.pageSize,
                param
            })
            state.pagedOnlineSystems = (resp2.list || []).map(systemUserToOnlineSystem)
            state.filteredTotal = resp2.total ?? 0
            return
        }
        state.pagedOnlineSystems = (resp.list || []).map(systemUserToOnlineSystem)
    }

    const fetchSidePanels = async () => {
        const [envResp, cfgResp, msgResp] = await Promise.all([
            systemEnvApi.queryPage({
                pageNo: 1,
                pageSize: 500,
                param: {}
            }),
            systemConfigApi.queryPage({
                pageNo: 1,
                pageSize: 200,
                param: {configGroup: DASHBOARD_CONFIG_GROUP}
            }),
            systemMessageApi.queryPage({
                pageNo: 1,
                pageSize: 8,
                param: {}
            })
        ])
        state.envDistribution = buildEnvDistribution(envResp.list || [])
        state.resourceUsage = parseResourceFromConfigs(cfgResp.list || [])
        state.recentChanges = messagesToRecentChanges(msgResp.list || [])
    }

    const refresh = async (): Promise<boolean> => {
        state.loading = true
        try {
            await Promise.all([fetchStatusCounts(), fetchSystemUserPage(), fetchSidePanels()])
            return true
        } catch (e) {
            console.error(e)
            message.error('加载失败，请稍后重试')
            return false
        } finally {
            state.loading = false
        }
    }

    const setStatusFilter = (f: 'all' | 'online' | 'offline' | 'maintenance') => {
        state.statusFilter = f
        state.currentPage = 1
        void (async () => {
            state.loading = true
            try {
                await fetchSystemUserPage()
            } catch (err) {
                console.error(err)
                message.error('加载失败')
            } finally {
                state.loading = false
            }
        })()
    }

    const setPage = (p: number) => {
        const next = Math.min(Math.max(1, p), totalPages.value)
        state.currentPage = next
        void (async () => {
            state.loading = true
            try {
                await fetchSystemUserPage()
            } catch (err) {
                console.error(err)
                message.error('加载失败')
            } finally {
                state.loading = false
            }
        })()
    }

    const setPageSize = (n: number) => {
        state.pageSize = n
        state.currentPage = 1
        void (async () => {
            state.loading = true
            try {
                await fetchSystemUserPage()
            } catch (err) {
                console.error(err)
                message.error('加载失败')
            } finally {
                state.loading = false
            }
        })()
    }

    const exportBusinessSystemsCsv = () => {
        const rows = state.pagedOnlineSystems
        if (rows.length === 0) {
            message.info('当前页无数据可导出')
            return
        }
        const header = ['系统名称', '系统标识', '负责人', '环境', '状态', '今日调用量', '错误率', '最后访问']
        const statusLabel = (s: SystemStatusUi) =>
            s === 'online' ? '在线' : s === 'offline' ? '离线' : '维护中'
        const envLabel = (e: SystemEnvUi) =>
            e === 'prod' ? '生产' : e === 'pre' ? '预生产' : e === 'test' ? '测试' : '开发'
        const lines = [
            header.join(','),
            ...rows.map((r) =>
                [
                    `"${String(r.name).replace(/"/g, '""')}"`,
                    `"${String(r.slug).replace(/"/g, '""')}"`,
                    `"${String(r.ownerName).replace(/"/g, '""')}"`,
                    envLabel(r.env),
                    statusLabel(r.status),
                    r.todayCalls,
                    r.errorRate,
                    `"${String(r.lastAccess).replace(/"/g, '""')}"`
                ].join(',')
            )
        ]
        const blob = new Blob(['\ufeff' + lines.join('\n')], {type: 'text/csv;charset=utf-8'})
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `system-config-center-business-systems-${dayjs().format('YYYY-MM-DD-HHmm')}.csv`
        a.click()
        URL.revokeObjectURL(url)
        message.success('已导出当前页 CSV')
    }

    onMounted(() => {
        void refresh()
    })

    return {
        ...toRefs(state),
        totalPages,
        setStatusFilter,
        setPage,
        setPageSize,
        refresh,
        exportBusinessSystemsCsv
    }
}
