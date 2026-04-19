<template>
  <div class="discovery-hub-card" @click="navigateTo('/admin/resource-library')">
    <div class="gradient-overlay"></div>
    
    <div class="hub-container">
      <div class="hub-header">
        <div class="header-left">
          <div class="icon-wrapper">
            <appstore-outlined :style="{ fontSize: '18px', color: '#fff' }" />
          </div>
          <span class="badge-text">DISCOVERY HUB</span>
        </div>
        
        <div class="avatar-stack">
          <div class="stack-item i1">
            <div class="app-icon-inner bg-indigo-500">
              <rocket-outlined :style="{ fontSize: '12px' }" />
            </div>
          </div>
          <div class="stack-item i2">
            <div class="app-icon-inner bg-blue-500">
              <api-outlined :style="{ fontSize: '12px' }" />
            </div>
          </div>
          <div class="stack-item i3">
            <div class="app-icon-inner bg-slate-800">
              <span class="count-text">+32</span>
            </div>
          </div>
        </div>
      </div>

      <div class="hub-content">
        <h2 class="main-title">全部应用入口</h2>
        <p class="description">浏览所有已部署的智能体、第三方工具集成及沙盒测试环境。</p>
      </div>

      <div class="hub-footer">
        <div class="tags-row">
          <a-tag color="rgba(0,0,0,0.2)" class="custom-tag">Bot</a-tag>
          <a-tag color="rgba(255,255,255,0.1)" class="custom-tag">API</a-tag>
        </div>
        
        <a-button type="primary" shape="round" class="explore-btn">
          立即探索
          <template #icon><right-outlined /></template>
        </a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
// 导入 Ant Design Icons
import { 
  AppstoreOutlined, 
  RightOutlined, 
  RocketOutlined, 
  ApiOutlined 
} from '@ant-design/icons-vue'

const router = useRouter()
const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<style scoped lang="less">
.discovery-hub-card {
  position: relative;
  /* 找回蓝色渐变 */
  background: linear-gradient(135deg, #4f46e5 0%, #3b82f6 100%);
  border-radius: 20px;
  padding: 24px;
  color: white;
  cursor: pointer;
  overflow: hidden;
  height: 100%;
  min-height: 180px;
  width: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.1);

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 30px rgba(59, 130, 246, 0.4);
    
    .stack-item {
      margin-left: 4px; // 悬停时图标轻轻展开
    }
  }
}

.gradient-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: radial-gradient(circle at top right, rgba(255,255,255,0.15), transparent);
  pointer-events: none;
}

.hub-container {
  position: relative;
  z-index: 2;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.hub-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.icon-wrapper {
  background: rgba(255, 255, 255, 0.2);
  padding: 6px;
  border-radius: 10px;
  display: flex;
  backdrop-filter: blur(4px);
}

.badge-text {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 1px;
  opacity: 0.8;
}

/* 核心：层叠图标样式 */
.avatar-stack {
  display: flex;
  flex-direction: row-reverse; // 让第一个图标排在最上层
}

.stack-item {
  width: 32px;
  height: 32px;
  background: transparent;
  border-radius: 10px;
  margin-left: -12px; // 负边距实现重叠
  transition: all 0.3s ease;
  padding: 2px; // 模拟切口边距
  background: #3b82f6; // 这里的颜色应与父级背景色一致，产生遮挡感
}

.app-icon-inner {
  width: 100%;
  height: 100%;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.count-text {
  font-size: 10px;
  font-weight: bold;
}

.i1 { z-index: 3; }
.i2 { z-index: 2; }
.i3 { z-index: 1; }

/* 文字内容 */
.hub-content {
  margin: 20px 0;
}

.main-title {
  color: white;
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 8px;
}

.description {
  font-size: 13px;
  opacity: 0.85;
  line-height: 1.5;
  max-width: 240px;
}

/* 底部区域 */
.hub-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.custom-tag {
  border: none;
  border-radius: 8px;
  font-weight: 600;
  color: white !important;
}

.explore-btn {
  background: white !important;
  border: none !important;
  color: #3b82f6 !important;
  font-weight: 700 !important;
  height: 38px;
  padding: 0 20px;
  
  &:hover {
    transform: scale(1.05);
    background: #f8fafc !important;
  }
}
</style>