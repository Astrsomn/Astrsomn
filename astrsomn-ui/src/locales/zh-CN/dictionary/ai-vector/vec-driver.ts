/**
 * 向量库驱动枚举 - 与后端 AiVecDriverEnum.Provider 对齐（中文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const vecDriverLabels = {
    qdrant: 'Qdrant Vector Database',
    milvus: 'Zilliz / Milvus',
    pinecone: 'Pinecone Managed Service',
    chroma: 'Chroma AI',
    dashvector: 'Alibaba Cloud DashVector',
    weaviate: 'Weaviate Vector Search',
    elasticsearch: 'Elasticsearch / OpenSearch',
    pgvector: 'PostgreSQL pgvector Extension',
    redis: 'Redis Search & Query',
    in_memory: 'Local In-Memory Store'
} as const

export const aiVecDriverProviderDictionary = createEnumDictionary({
    id: 'ai-vec.driver',
    labels: vecDriverLabels,
    order: [
        'qdrant',
        'milvus',
        'pinecone',
        'chroma',
        'dashvector',
        'weaviate',
        'elasticsearch',
        'pgvector',
        'redis',
        'in_memory'
    ],
    caseInsensitive: true
})

const vecDriverStatusLabels = {
    enabled: '启用',
    disabled: '禁用'
} as const

export const aiVecDriverStatusDictionary = createEnumDictionary({
    id: 'ai-vec.driver.status',
    labels: vecDriverStatusLabels,
    order: ['enabled', 'disabled'],
    caseInsensitive: true
})

const vecDriverParamLabels = {
    host: '主机地址',
    port: '端口',
    username: '用户名',
    password: '密码',
    databaseName: '数据库名称',
    token: '令牌'
} as const

export const aiVecDriverParamDictionary = createEnumDictionary({
    id: 'ai-vec.driver.param',
    labels: vecDriverParamLabels,
    order: ['host', 'port', 'username', 'password', 'databaseName', 'token']
})
