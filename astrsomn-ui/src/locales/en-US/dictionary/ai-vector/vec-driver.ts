/**
 * 向量库驱动枚举 - 与后端 AiVecDriverEnum.Provider 对齐（英文文案）
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
    enabled: 'Enabled',
    disabled: 'Disabled'
} as const

export const aiVecDriverStatusDictionary = createEnumDictionary({
    id: 'ai-vec.driver.status',
    labels: vecDriverStatusLabels,
    order: ['enabled', 'disabled'],
    caseInsensitive: true
})

const vecDriverParamLabels = {
    host: 'Host',
    port: 'Port',
    username: 'Username',
    password: 'Password',
    databaseName: 'Database Name',
    token: 'Token'
} as const

export const aiVecDriverParamDictionary = createEnumDictionary({
    id: 'ai-vec.driver.param',
    labels: vecDriverParamLabels,
    order: ['host', 'port', 'username', 'password', 'databaseName', 'token']
})
