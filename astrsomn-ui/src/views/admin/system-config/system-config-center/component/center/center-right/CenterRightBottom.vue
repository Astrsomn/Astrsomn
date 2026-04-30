<template>
  <div class="extension-panel">
    <div class="panel-header">
      <div class="header-left">
        <ApiOutlined />
        <h3 class="panel-title">系统扩展能力</h3>
      </div>
      <button class="import-btn" @click="handleImport">导入插件</button>
      <input 
        type="file" 
        ref="fileInput" 
        class="file-input" 
        @change="handleFileImport"
      />
    </div>

    <!-- 搜索框 -->
    <div class="search-container">
      <SearchOutlined class="search-icon" />
      <input type="text" placeholder="搜索已安装插件..." class="search-input" />
    </div>

    <!-- Tabs -->
    <div class="tabs-container">
      <button 
        class="tab-btn" 
        :class="{ active: activeTab === 'installed' }"
        @click="activeTab = 'installed'; currentPage = 1"
      >
        现有插件
      </button>
      <button 
        class="tab-btn" 
        :class="{ active: activeTab === 'market' }"
        @click="activeTab = 'market'; currentPage = 1"
      >
        插件市场
      </button>
    </div>

    <!-- 插件列表 -->
    <div class="plugins-list" v-if="currentPlugins.length > 0">
      <div 
        v-for="item in currentPlugins" 
        :key="item.id"
        class="plugin-card"
      >
        <div class="plugin-header">
          <div class="plugin-info">
            <div class="icon-wrapper">
              <component :is="item.icon" />
            </div>
            <div class="plugin-name">{{ item.name }}</div>
          </div>
          <div class="plugin-count">{{ item.count }}</div>
        </div>
        <div class="plugin-actions">
          <button class="action-btn uninstall-btn">卸载</button>
          <button class="action-btn update-btn">更新</button>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <p class="empty-text">暂无插件</p>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <span class="pagination-text">Page {{ currentPage }} / {{ totalPages }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { 
  ApiOutlined, 
  ImportOutlined,
  SearchOutlined,
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
.extension-panel {
  background: white;
  border-radius: 24px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.panel-header {
  padding: 24px 24px 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-left :deep(.anticon) {
  width: 20px;
  height: 20px;
  color: #94a3b8;
}

.panel-title {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.import-btn {
  font-size: 12px;
  font-weight: 700;
  color: #3b82f6;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
}

.file-input {
  display: none;
}

.search-container {
  position: relative;
  margin: 16px 24px;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 14px;
  height: 14px;
  color: #94a3b8;
}

.search-input {
  width: 100%;
  padding: 6px 12px 6px 36px;
  background: #f8fafc;
  border: 1px solid #f1f5f9;
  border-radius: 8px;
  font-size: 11px;
  color: #64748b;
  outline: none;
  transition: all 0.2s ease;
}

.search-input:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.tabs-container {
  display: flex;
  gap: 24px;
  border-bottom: 1px solid #f1f5f9;
  margin: 0 24px;
}

.tab-btn {
  padding: 0 0 8px;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  font-size: 12px;
  font-weight: 700;
  color: #94a3b8;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab-btn.active {
  border-bottom-color: #3b82f6;
  color: #3b82f6;
}

.plugins-list {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  flex: 1;
  overflow-y: auto;
}

.plugin-card {
  padding: 16px;
  border: 1px solid #f1f5f9;
  border-radius: 16px;
  background: #f8fafc;
}

.plugin-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.plugin-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon-wrapper {
  width: 36px;
  height: 36px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper :deep(.anticon) {
  width: 20px;
  height: 20px;
  color: #64748b;
}

.plugin-name {
  font-size: 14px;
  font-weight: 700;
  color: #334155;
}

.plugin-count {
  font-size: 20px;
  font-weight: 700;
  color: #e2e8f0;
  letter-spacing: -0.5px;
}

.plugin-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 12px;
}

.action-btn {
  font-size: 10px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
}

.uninstall-btn {
  color: #ef4444;
}

.update-btn {
  color: #1e293b;
  font-weight: 700;
}

.empty-state {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
}

.empty-text {
  font-size: 12px;
  color: #94a3b8;
  margin: 0;
}

.pagination {
  padding: 16px 24px;
  border-top: 1px solid #f1f5f9;
  text-align: center;
}

.pagination-text {
  font-size: 10px;
  color: #94a3b8;
}
</style>
