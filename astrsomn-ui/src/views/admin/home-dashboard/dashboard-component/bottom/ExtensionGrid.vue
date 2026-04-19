<template>
  <div class="extension-grid">
    <div class="extension-card" @click="navigateTo('/admin/prompts')">
      <svg viewBox="0 0 24 24" class="extension-icon extension-icon--blue">
        <path d="M4 6h16M4 12h16M4 18h10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
      </svg>
      <span>提示词库</span>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/tracing')">
      <svg viewBox="0 0 24 24" class="extension-icon extension-icon--purple">
        <path d="M3 12h3l2-6 4 12 2-6h5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
      </svg>
      <span>链路追踪</span>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/conversations')">
      <svg viewBox="0 0 24 24" class="extension-icon extension-icon--cyan">
        <path d="M7 8h10M7 12h6M4 18h16a2 2 0 002-2V8a2 2 0 00-2-2H4a2 2 0 00-2 2v8a2 2 0 002 2z" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
      </svg>
      <span>对话管理</span>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/mcp')">
      <svg viewBox="0 0 24 24" class="extension-icon extension-icon--indigo">
        <path d="M4 7h4a2 2 0 012 2v2a2 2 0 002 2h2a2 2 0 012 2v2h4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" fill="none"/>
        <path d="M14 7h4M4 17h4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
      </svg>
      <span>AI MCP</span>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/system-config/system-extension')">
      <svg viewBox="0 0 24 24" class="extension-icon extension-icon--violet">
        <path d="M12 2l1.5 4.5L18 8l-4.5 1.5L12 14l-1.5-4.5L6 8l4.5-1.5L12 2z" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <span>系统扩展</span>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/ai-account')">
      <svg viewBox="0 0 24 24" class="extension-icon extension-icon--sky">
        <path d="M12 2a4 4 0 00-4 4v2H6a2 2 0 00-2 2v5c0 1.5.5 3 1.5 4.5l.5.5c1 1 2.5 1.5 4 1.5h4c1.5 0 3-.5 4-1.5l.5-.5c1-1.5 1.5-3 1.5-4.5V10a2 2 0 00-2-2h-2V6a4 4 0 00-4-4z" fill="none" stroke="currentColor" stroke-width="1.5"/>
        <path d="M9 12l2 2 4-4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <span>凭证管理</span>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/security')">
      <svg viewBox="0 0 24 24" class="extension-icon extension-icon--red">
        <path d="M12 2l7 4v6c0 5-3.5 9-7 10-3.5-1-7-5-7-10V6l7-4z" fill="none" stroke="currentColor" stroke-width="1.5"/>
        <path d="M9 12l2 2 4-4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <span>安全治理</span>
    </div>
    <div class="extension-card" @click="navigateTo('/admin/templates')">
      <svg viewBox="0 0 24 24" class="extension-icon extension-icon--teal">
        <path d="M4 5a2 2 0 012-2h8l4 4v12a2 2 0 01-2 2H6a2 2 0 01-2-2V5z" fill="none" stroke="currentColor" stroke-width="1.5"/>
        <path d="M8 9h8M8 13h6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
      </svg>
      <span>FTL 模板</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<style scoped>
.extension-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 12px;
}

.extension-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 18px 14px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-start;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  gap: 10px;
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