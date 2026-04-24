<template>
  <div class="panel-card extension-panel">
    <div class="panel-head">
      <h3>
        <ApiOutlined />
        系统扩展能力
      </h3>
      <div class="panel-actions">
        <span class="panel-subtitle">支持多类型扩展：插件、向量库、模型等</span>
        <a-button 
          type="text" 
          size="small" 
          @click="handleImport"
          class="import-btn"
        >
          <ImportOutlined />
          导入插件
          <input 
            type="file" 
            ref="fileInput" 
            class="file-input" 
            @change="handleFileImport"
          />
        </a-button>
      </div>
    </div>
    
    <!-- 选项卡切换 -->
    <div class="tab-container">
      <a-tabs v-model:activeKey="activeTab" size="small" type="line" @change="handleTabChange">
        <a-tab-pane key="installed" tab="现有插件" />
        <a-tab-pane key="market" tab="插件市场" />
      </a-tabs>
    </div>
    
    <!-- 内容列表容器 -->
    <div class="extension-list" v-if="currentPlugins.length > 0">
      <div 
        v-for="item in currentPlugins" 
        :key="item.id"
        class="extension-item"
      >
        <div class="extension-header">
          <div class="extension-info">
            <div class="icon-wrapper">
              <component :is="item.icon" />
            </div>
            <div class="info-content">
              <h4 class="extension-name">{{ item.name }}</h4>
              <p class="extension-desc">{{ item.desc }}</p>
            </div>
          </div>
          <div class="extension-count">{{ item.count }}</div>
        </div>
        
        <div class="extension-footer">
          <div class="extension-tags">
            <a-tag 
              v-for="tag in item.tags" 
              :key="tag"
              size="small"
              class="extension-tag"
            >
              {{ tag }}
            </a-tag>
          </div>
          <div class="extension-actions">
            <a-button 
              v-if="activeTab === 'installed'"
              type="text" 
              size="small" 
              danger
              @click="handleAction('卸载', item.name)"
            >
              卸载
            </a-button>
            <a-button 
              v-if="activeTab === 'installed'"
              type="text" 
              size="small"
              @click="handleAction('更新', item.name)"
            >
              更新
            </a-button>
            <a-button 
              v-else
              type="primary" 
              size="small"
              @click="handleAction('安装', item.name)"
            >
              安装插件
            </a-button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div v-else class="empty-state">
      <a-empty description="暂无插件" />
    </div>
    
    <!-- 分页区域 -->
    <div class="pager-wrap extension-pager">
      <button
        type="button"
        class="pager-btn"
        :disabled="currentPage === 1"
        @click="prevPage"
      >
        上一页
      </button>
      <span class="pager-text">
        第 {{ currentPage }} / {{ totalPages }} 页，共 {{ totalItems }} 项
      </span>
      <button
        type="button"
        class="pager-btn"
        :disabled="currentPage === totalPages"
        @click="nextPage"
      >
        下一页
      </button>
    </div>
    
    <!-- 提示框 -->
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { 
  ApiOutlined, 
  ImportOutlined,
  ShareAltOutlined,
  DatabaseOutlined,
  RobotOutlined,
  SafetyOutlined
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

// 插件数据类型
type ExtensionCard = {
  id: number
  name: string
  count: number
  desc: string
  icon: any
  tags: string[]
  type: 'installed' | 'market'
}

// 模拟数据
const installedPlugins: ExtensionCard[] = [
  {
    id: 1,
    icon: ShareAltOutlined,
    name: '平台插件',
    count: 24,
    desc: '用于业务能力增强、流程编排与第三方集成。',
    tags: ['工作流', '通知', '审计', '数据同步'],
    type: 'installed'
  },
  {
    id: 2,
    icon: DatabaseOutlined,
    name: '向量库接入',
    count: 6,
    desc: '支持多种向量引擎，满足检索增强与知识库场景。',
    tags: ['Milvus', 'pgvector', 'Elastic', 'Chroma'],
    type: 'installed'
  }
]

const marketPlugins: ExtensionCard[] = [
  {
    id: 3,
    icon: RobotOutlined,
    name: '多模态模型集',
    count: 12,
    desc: '接入主流视觉与音频处理模型，扩展 AI 感知能力。',
    tags: ['GPT-4V', 'Whisper', 'Stable Diffusion'],
    type: 'market'
  },
  {
    id: 4,
    icon: SafetyOutlined,
    name: '安全审计组件',
    count: 3,
    desc: '对大模型输入输出进行合规性检测与内容过滤。',
    tags: ['敏感词过滤', '合规检测', '访问日志'],
    type: 'market'
  }
]

// 响应式数据
const activeTab = ref('installed')
const currentPage = ref(1)
const pageSize = 2
const fileInput = ref<HTMLInputElement>()

// 计算属性
const currentPlugins = computed(() => {
  const data = activeTab.value === 'installed' ? installedPlugins : marketPlugins
  const start = (currentPage.value - 1) * pageSize
  return data.slice(start, start + pageSize)
})

const totalPages = computed(() => {
  const data = activeTab.value === 'installed' ? installedPlugins : marketPlugins
  return Math.max(1, Math.ceil(data.length / pageSize))
})

const totalItems = computed(() => {
  const data = activeTab.value === 'installed' ? installedPlugins : marketPlugins
  return data.length
})

// 方法
const handleImport = () => {
  fileInput.value?.click()
}

const handleFileImport = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    message.loading(`正在导入插件: ${file.name}...`)
    setTimeout(() => {
      message.success('导入并安装成功！')
      // 重置文件输入
      if (fileInput.value) {
        fileInput.value.value = ''
      }
    }, 1500)
  }
}

const handleAction = (action: string, name: string) => {
  message.success(`${action} [${name}] 成功！`)
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

// 监听标签切换，重置分页
const handleTabChange = (key: string) => {
  activeTab.value = key
  currentPage.value = 1
}
</script>

<style scoped>
.panel-card {
  border: 1px solid var(--border-default);
  border-radius: 14px;
  background: var(--bg-card);
  padding: 20px;
  min-height: 0;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.extension-panel {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.panel-head h3 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 16px;
  color: var(--text-heading);
  flex-shrink: 0;
}

.panel-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.panel-subtitle {
  font-size: 12px;
  color: var(--text-secondary);
  white-space: nowrap;
}

.import-btn {
  font-size: 12px;
  color: var(--primary);
}

.file-input {
  display: none;
}

.tab-container {
  margin-bottom: 16px;
}

.extension-list {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding-right: 4px;
}

.extension-item {
  border: 1px solid var(--border-default);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  background: color-mix(in srgb, var(--bg-card) 95%, var(--bg-base));
  transition: all 0.2s ease;
}

.extension-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  transform: translateY(-1px);
  border-color: var(--border-hover);
}

.extension-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
}

.extension-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.icon-wrapper {
  width: 40px;
  height: 40px;
  background: color-mix(in srgb, var(--bg-elevated) 80%, transparent);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 1px solid var(--border-default);
}

.icon-wrapper :deep(.anticon) {
  font-size: 18px;
  color: var(--text-secondary);
}

.info-content {
  flex: 1;
  min-width: 0;
}

.extension-name {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-heading);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.extension-desc {
  margin: 0;
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.extension-count {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-heading);
  flex-shrink: 0;
}

.extension-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.extension-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  flex: 1;
  min-width: 0;
}

.extension-tag {
  font-size: 11px;
  height: 22px;
  line-height: 20px;
  background: color-mix(in srgb, var(--bg-elevated) 70%, transparent);
  border-color: var(--border-default);
}

.extension-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.empty-state {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
}

.extension-pager {
  margin-top: 16px;
  border-top: 1px solid var(--border-divider);
  padding-top: 12px;
}

.pager-wrap {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.pager-btn {
  border: 1px solid var(--border-default);
  border-radius: 8px;
  background: var(--bg-card);
  color: var(--text-primary);
  font-size: 12px;
  padding: 4px 10px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.pager-btn:hover:not(:disabled) {
  border-color: var(--primary);
  color: var(--primary);
}

.pager-btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.pager-text {
  color: var(--text-secondary);
  font-size: 12px;
  flex-shrink: 0;
}

@media (max-width: 900px) {
  .panel-head {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .panel-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .extension-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .extension-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .extension-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  .pager-wrap {
    flex-direction: column;
    align-items: stretch;
  }
  
  .pager-text {
    text-align: center;
  }
}
</style>
