<template>
  <div class="extension-grid">
    <div class="extension-card" @click="navigateTo('/admin/prompts')">
      <FileTextOutlined class="extension-icon extension-icon--blue" />
      <span>提示词库</span>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/tracing')">
      <LinkOutlined class="extension-icon extension-icon--purple" />
      <span>链路追踪</span>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/conversations')">
      <MessageOutlined class="extension-icon extension-icon--cyan" />
      <span>对话管理</span>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/mcp')">
      <ApiOutlined class="extension-icon extension-icon--indigo" />
      <span>AI MCP</span>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/system-config/system-extension')">
      <SettingOutlined class="extension-icon extension-icon--violet" />
      <span>系统扩展</span>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/ai-account')">
      <KeyOutlined class="extension-icon extension-icon--sky" />
      <span>凭证管理</span>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/security')">
      <SecurityScanOutlined class="extension-icon extension-icon--red" />
      <span>安全治理</span>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/templates')">
      <CheckCircleOutlined class="extension-icon extension-icon--teal" />
      <span>FTL 模板</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
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

const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<style scoped>
.extension-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 16px;
}

.extension-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 16px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-start;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  gap: 12px;
  min-height: 60px;
}

.extension-card:hover {
  background: #eff6ff;
  border-color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}

.extension-icon {
  width: 22px;
  height: 22px;
  flex-shrink: 0;
}

.extension-icon--blue {
  color: #3b82f6;
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