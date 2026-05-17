import {computed, reactive, ref} from 'vue'
import {message} from 'ant-design-vue'
import {type AiVecSource, aiVecSourceApi} from '@/api/aiVecSource.ts'
import {type AiVecStore, aiVecStoreApi} from '@/api/aiVecStore.ts'
import {type AiVecDoc, aiVecDocApi} from '@/api/aiVecDoc.ts'
import {type AiVecSegment, aiVecSegmentApi} from '@/api/aiVecSegment.ts'

export function useVectorCenterState() {
    const loading = reactive({
        bootstrap: false,
        sources: false,
        stores: false,
        docs: false,
        segments: false
    })

    const sources = ref<AiVecSource[]>([])
    const stores = ref<AiVecStore[]>([])
    const docs = ref<AiVecDoc[]>([])
    const segments = ref<AiVecSegment[]>([])
    const pageState = reactive({
        docs: {pageNo: 1, pageSize: 200},
        segments: {pageNo: 1, pageSize: 300}
    })

    const selectedSourceId = ref<number | string | undefined>()
    const selectedStoreId = ref<number | string | undefined>()
    const selectedDocId = ref<number | string | undefined>()

    const selectedStore = computed(() => stores.value.find((item) => item.id === selectedStoreId.value))

    const selectedSource = computed(() => {
        const sourceId = selectedStore.value?.sourceId
        return sources.value.find((item) => item.id === sourceId)
    })

    const selectedDoc = computed(() => docs.value.find((item) => item.id === selectedDocId.value))

    const fetchSources = async () => {
        loading.sources = true
        try {
            const resp = await aiVecSourceApi.queryPage({
                pageNo: 1,
                pageSize: 200,
                param: {}
            })
            sources.value = resp.list || []
        } finally {
            loading.sources = false
        }
    }

    const fetchStores = async () => {
        loading.stores = true
        try {
            const resp = await aiVecStoreApi.queryPage({
                pageNo: 1,
                pageSize: 500,
                param: {
                    sourceId: selectedSourceId.value
                }
            })
            stores.value = resp.list || []
        } finally {
            loading.stores = false
        }
    }

    const fetchDocs = async () => {
        loading.docs = true
        try {
            const resp = await aiVecDocApi.queryPage({
                pageNo: pageState.docs.pageNo,
                pageSize: pageState.docs.pageSize,
                param: {
                    collectionId: selectedStoreId.value
                }
            })
            docs.value = resp.list || []
        } finally {
            loading.docs = false
        }
    }

    const fetchSegments = async () => {
        if (!selectedDocId.value || !selectedStoreId.value) {
            segments.value = []
            return
        }
        loading.segments = true
        try {
            const resp = await aiVecSegmentApi.queryPage({
                pageNo: pageState.segments.pageNo,
                pageSize: pageState.segments.pageSize,
                param: {
                    docId: selectedDocId.value,
                    collectionId: selectedStoreId.value
                }
            })
            segments.value = resp.list || []
        } finally {
            loading.segments = false
        }
    }

    const reloadBySource = async () => {
        await fetchSources()
        await fetchStores()
        await fetchDocs()
        await fetchSegments()
    }

    const reloadByStore = async () => {
        await fetchStores()
        await fetchDocs()
        await fetchSegments()
    }

    const reloadByDoc = async () => {
        await fetchDocs()
        await fetchSegments()
    }

    const bootstrap = async () => {
        loading.bootstrap = true
        try {
            await fetchSources()
        } catch (error) {
            const err = error as { message?: string }
            message.error(err?.message || '向量中心数据加载失败')
        } finally {
            loading.bootstrap = false
        }
    }

    return {
        loading,
        sources,
        stores,
        docs,
        segments,
        selectedSourceId,
        selectedStoreId,
        selectedDocId,
        selectedStore,
        selectedSource,
        selectedDoc,
        pageState,
        fetchSources,
        fetchStores,
        fetchDocs,
        fetchSegments,
        reloadBySource,
        reloadByStore,
        reloadByDoc,
        bootstrap
    }
}
