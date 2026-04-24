<template>
  <div class="discovery-hub-card clickable" @click="navigateTo('/admin/resource-library')">
    <div class="card-header">
      <div class="card-icon">
        <AppstoreOutlined />
      </div>
      <div class="card-title">全部应用</div>
    </div>
    <p class="card-desc">浏览资源库、辅助工具与扩展能力入口</p>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { AppstoreOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<style scoped>
.discovery-hub-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: 12px;
  padding: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0.2, 0.2, 1);
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
  min-height: 80px;
}

.discovery-hub-card:hover {
  box-shadow: var(--shadow-card);
  border-color: var(--primary);
  transform: translateY(-1px);
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  position: relative;
  flex: 0 0 auto;
}

.card-icon {
  width: 36px;
  height: 36px;
  margin-right: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: var(--primary);
  flex-shrink: 0;
}

.card-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  flex: 1;
}

.card-desc {
  font-size: 10px;
  color: var(--text-muted);
  margin: 0;
  line-height: 1.3;
  flex: 1;
}

.clickable {
  cursor: pointer;
}
</style>