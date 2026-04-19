<template>
  <div class="col-span-4 extension-grid">
    <div class="extension-card">
      <img src="@/assets/dashboard-icons/prompts-color.svg" alt="prompts" />
      <span>提示词库</span>
    </div>
    <div class="extension-card">
      <img src="@/assets/dashboard-icons/tracing-color.svg" alt="tracing" />
      <span>链路追踪</span>
    </div>
    <div class="extension-card">
      <img src="@/assets/dashboard-icons/templates-color.svg" alt="templates" />
      <span>对话管理</span>
    </div>
    <div class="extension-card">
      <img src="@/assets/dashboard-icons/mcp-color.svg" alt="mcp" />
      <span>AI MCP</span>
    </div>
    <div class="extension-card">
      <img src="@/assets/dashboard-icons/extension-color.svg" alt="extension" />
      <span>工作流测试</span>
    </div>
    <div class="extension-card">
      <img src="@/assets/dashboard-icons/credentials-color.svg" alt="credentials" />
      <span>凭证管理</span>
    </div>
    <div class="extension-card">
      <img src="@/assets/dashboard-icons/experiments-color.svg" alt="experiments" />
      <span>灰度实验</span>
    </div>
    <div class="extension-card extension-card--dashed">
      <i class="fa-solid fa-plus text-gray-300"></i>
      <span>添加插件</span>
    </div>
  </div>
</template>

<style scoped>
.extension-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 16px;
  margin-top: 8px;
}

.extension-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
  font-size: 10px;
  font-weight: bold;
  color: #374151;
  gap: 8px;
}

.extension-card img {
  width: 32px;
  height: 32px;
}

.extension-card:hover {
  background: #eff6ff;
}

.extension-card--dashed {
  border-style: dashed;
  color: #d1d5db;
}

.text-gray-300 { color: #d1d5db; }
</style>
