/**
 * 通用枚举字典：与后端 `code` / `desc` 对齐，前端用 `labels[code]` 展示。
 * 多语言：在 `registry` 中注册各 locale 的文案包（如英文就绪后增加 `en-US` 聚合对象）。
 */

export type DictionarySelectOption = { label: string; value: string }

export interface EnumDictionary<T extends Record<string, string>> {
    /** 全局唯一，用于 registry 注册 */
    readonly id: string
    readonly labels: Readonly<T>
    /** 展示顺序（与 Java 枚举顺序一致为佳） */
    readonly order: readonly (keyof T & string)[]

    getLabel(code: string | null | undefined): string | undefined

    options(): DictionarySelectOption[]

    /** 按本字典的 order 排序；未知 code 排在末尾 */
    sortKeys(keys: string[]): string[]
}

export interface CreateEnumDictionaryConfig<T extends Record<string, string>> {
    id: string
    labels: T
    order: readonly (keyof T & string)[]
    /** provider/status 等可与后端大小写不一致时用 */
    caseInsensitive?: boolean
}

export function createEnumDictionary<const T extends Record<string, string>>(
    config: CreateEnumDictionaryConfig<T>
): EnumDictionary<T> {
    const {id, labels, order, caseInsensitive = false} = config
    const orderIndex = new Map<string, number>(order.map((c, i) => [c, i]))

    function resolveKey(code: string): (keyof T & string) | undefined {
        const trimmed = code.trim()
        if (caseInsensitive) {
            const lower = trimmed.toLowerCase()
            if (lower in labels) return lower as keyof T & string
        }
        if (trimmed in labels) return trimmed as keyof T & string
        return undefined
    }

    return {
        id,
        labels,
        order,
        getLabel(code) {
            if (code == null || code === '') return undefined
            const k = resolveKey(String(code))
            return k ? labels[k] : undefined
        },
        options() {
            return order.map((value) => ({
                value,
                label: labels[value]
            }))
        },
        sortKeys(keys) {
            return [...keys].sort((a, b) => {
                const ia = orderIndex.has(a) ? orderIndex.get(a)! : 999
                const ib = orderIndex.has(b) ? orderIndex.get(b)! : 999
                if (ia !== ib) return ia - ib
                return a.localeCompare(b)
            })
        }
    }
}
