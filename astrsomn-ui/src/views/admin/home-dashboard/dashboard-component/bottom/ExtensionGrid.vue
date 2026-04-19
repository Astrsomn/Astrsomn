<template>
  <div class="extension-grid">
    <div class="extension-card" @click="navigateTo('/admin/prompts')" @mouseenter="showDescription['prompts'] = true" @mouseleave="showDescription['prompts'] = false">
      <FileTextOutlined class="extension-icon extension-icon--blue" />
      <div class="card-content">
        <span class="card-title">提示词库</span>
        <span v-if="showDescription['prompts']" class="card-description">管理和维护AI提示词</span>
      </div>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/builder')" @mouseenter="showDescription['builder'] = true" @mouseleave="showDescription['builder'] = false">
      <LinkOutlined class="extension-icon extension-icon--purple" />
      <div class="card-content">
        <span class="card-title">Agent Studio</span>
        <span v-if="showDescription['builder']" class="card-description">构建和配置AI代理</span>
      </div>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/conversations')" @mouseenter="showDescription['conversations'] = true" @mouseleave="showDescription['conversations'] = false">
      <MessageOutlined class="extension-icon extension-icon--cyan" />
      <div class="card-content">
        <span class="card-title">对话管理</span>
        <span v-if="showDescription['conversations']" class="card-description">查看和管理用户对话</span>
      </div>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/mcp')" @mouseenter="showDescription['mcp'] = true" @mouseleave="showDescription['mcp'] = false">
      <ApiOutlined class="extension-icon extension-icon--indigo" />
      <div class="card-content">
        <span class="card-title">AI MCP</span>
        <span v-if="showDescription['mcp']" class="card-description">AI模型控制平台</span>
      </div>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/system-config/system-extension')" @mouseenter="showDescription['system'] = true" @mouseleave="showDescription['system'] = false">
      <SettingOutlined class="extension-icon extension-icon--violet" />
      <div class="card-content">
        <span class="card-title">系统扩展</span>
        <span v-if="showDescription['system']" class="card-description">系统功能扩展配置</span>
      </div>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/vec-builder')" @mouseenter="showDescription['vec'] = true" @mouseleave="showDescription['vec'] = false">
      <KeyOutlined class="extension-icon extension-icon--sky" />
      <div class="card-content">
        <span class="card-title">向量知识库管理</span>
        <span v-if="showDescription['vec']" class="card-description">管理向量知识库</span>
      </div>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/security')" @mouseenter="showDescription['security'] = true" @mouseleave="showDescription['security'] = false">
      <SecurityScanOutlined class="extension-icon extension-icon--red" />
      <div class="card-content">
        <span class="card-title">安全治理</span>
        <span v-if="showDescription['security']" class="card-description">系统安全管理</span>
      </div>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/templates')" @mouseenter="showDescription['templates'] = true" @mouseleave="showDescription['templates'] = false">
      <CheckCircleOutlined class="extension-icon extension-icon--teal" />
      <div class="card-content">
        <span class="card-title">FTL 模板</span>
        <span v-if="showDescription['templates']" class="card-description">管理模板文件</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import { 
  FileTextOutlined, 
  LinkOutlined, 
  MessageOutlined, 
  ApiOutlined, 
  SettingOutlined, 
  KeyOutlined, 
  SecurityScanOutlined, 
  CheckCircleOutlined 
} from '@ant-design/icons-vue'

const router = useRouter()
const showDescription = ref<{[key: string]: boolean}>({})

const navigateTo = (path: string) => {
  router.push(path)
}

const toggleDescription = (key: string) => {
  showDescription.value[key] = !showDescription.value[key]
}
</script>

<style scoped>
.extension-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 16px;
}

.extension-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: 16px;
  padding: 16px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-start;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  gap: 12px;
  height: 80px;
  overflow: hidden;
}

.extension-card:hover {
  background: var(--bg-elevated);
  border-color: var(--primary);
  box-shadow: var(--shadow-card);
}

.card-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  flex: 1;
  transition: all 0.3s ease;
  height: 100%;
}

.card-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  transition: all 0.3s ease;
  margin-bottom: 4px;
  line-height: 1;
  display: flex;
  align-items: center;
  height: 24px;
}

.card-description {
  font-size: 11px;
  color: var(--text-secondary);
  line-height: 1.3;
  margin-top: 4px;
  animation: fadeIn 0.3s ease-in-out;
}

.extension-card:hover .card-title {
  font-size: 11px;
  transform: translateY(-4px);
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.extension-icon {
  width: 24px;
  height: 24px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  transition: all 0.3s ease;
}

.extension-card:hover .extension-icon {
  transform: scale(1.1);
}

.extension-icon--blue {
  color: var(--primary);
}

.extension-icon--purple {
  color: #a855f7;
}

.extension-icon--cyan {
  color: #06b6d4;
}

.extension-icon--indigo {
  color: #6366f1;
}

.extension-icon--violet {
  color: #8b5cf6;
}

.extension-icon--sky {
  color: #0ea5e9;
}

.extension-icon--red {
  color: #ef4444;
}

.extension-icon--teal {
  color: #14b8a6;
}
</style>