<template>
  <div class="search-panel">
    <div class="search-bar">
      <div class="search-row">
        <AstSearchInput
            v-model="queryText"
            layout="fluid"
            placeholder="输入问题进行语义检索..."
            @search="handleSearch"
        />
        <a-select v-model:value="topK" size="small" style="width: 80px; flex-shrink: 0;">
          <a-select-option :value="3">Top 3</a-select-option>
          <a-select-option :value="5">Top 5</a-select-option>
          <a-select-option :value="10">Top 10</a-select-option>
        </a-select>
      </div>
    </div>

    <div v-if="results.length > 0" class="search-results">
      <div class="results-header">
        <span class="results-title">检索结果</span>
        <span class="results-count">{{ results.length }} 条</span>
      </div>
      <div
          v-for="(item, index) in results"
          :key="index"
          class="result-card"
      >
        <div class="result-header">
          <span class="result-rank">#{{ index + 1 }}</span>/
          <a-progress
              :percent="Math.round((item.score || 0) * 100)"
              :size="'small'"
              :stroke-color="getScoreColor(item.score)"
              style="width: 120px"
          />
          <span class="result-score">{{ ((item.score || 0) * 100).toFixed(1) }}%</span>
        </div>
        <div class="result-content">
          {{ item.segmentContent || '-' }}
        </div>
        <div v-if="item.docId" class="result-meta">
          <span>文档 ID: {{ item.docId }}</span>
          <span v-if="item.segmentId">切片 ID: {{ item.segmentId }}</span>
        </div>
      </div>
    </div>

    <div v-else-if="searched && !searching" class="search-empty">
      未找到相关结果
    </div>
  </div>
</template>

<script lang="ts" setup>
import {ref} from 'vue'
import {message} from 'ant-design-vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import {aiVecSegmentApi, type AiVecSegmentSearchResult} from '@/api/aiVecSegment.ts'

const props = defineProps<{
  storeId?: number | string
}>()

const queryText = ref('')
const topK = ref(5)
const searching = ref(false)
const searched = ref(false)
const results = ref<AiVecSegmentSearchResult[]>([])

const getScoreColor = (score?: number) => {
  if (!score) return '#d9d9d9'
  if (score >= 0.8) return '#52c41a'
  if (score >= 0.6) return '#1677ff'
  return '#faad14'
}

const handleSearch = async () => {
  if (!queryText.value.trim()) return
  if (!props.storeId) {
    message.warning('请先选择数据库')
    return
  }
  searching.value = true
  searched.value = true
  try {
    results.value = await aiVecSegmentApi.search({
      collectionId: props.storeId,
      queryText: queryText.value.trim(),
      topK: topK.value
    })
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || '检索失败')
    results.value = []
  } finally {
    searching.value = false
  }
}
</script>

<style lang="less" scoped>
.search-panel {
  padding: 0 24px 16px;
}

.search-row {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 16px;
}

.search-row :deep(.toolbar-search-pill) {
  flex: 1;
  min-width: 0;
}

.search-results {
  max-height: 360px;
  overflow-y: auto;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;

  .results-title {
    font-size: 14px;
    font-weight: 600;
    color: var(--text-heading);
  }

  .results-count {
    font-size: 12px;
    color: var(--text-muted);
  }
}

.result-card {
  padding: 12px;
  margin-bottom: 8px;
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  transition: border-color 0.2s;

  &:hover {
    border-color: var(--primary);
  }
}

.result-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;

  .result-rank {
    font-size: 12px;
    font-weight: 700;
    color: var(--primary);
    min-width: 24px;
  }

  .result-score {
    font-size: 12px;
    color: var(--text-secondary);
    font-weight: 500;
  }
}

.result-content {
  font-size: 13px;
  color: var(--text-primary);
  line-height: 1.6;
  max-height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.result-meta {
  margin-top: 8px;
  display: flex;
  gap: 12px;
  font-size: 11px;
  color: var(--text-muted);
}

.search-empty {
  text-align: center;
  padding: 32px 0;
  color: var(--text-muted);
  font-size: 13px;
}
</style>
