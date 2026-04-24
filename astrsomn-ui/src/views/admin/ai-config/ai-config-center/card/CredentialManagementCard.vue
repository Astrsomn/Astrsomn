<template>
  <div class="credential-card clickable" @click="navigateTo('/admin/ai-account')">
    <div class="card-header">
      <div class="card-icon sky-icon">
        <KeyOutlined />
      </div>
      <div class="card-title">凭证管理</div>
    </div>
    <p class="card-desc">维护 API_KEY 与账户额度</p>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { KeyOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<style scoped>
.credential-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: 12px;
  padding: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0.2, 0.2, 1);
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  min-height: 80px;
}

.credential-card:hover {
  box-shadow: var(--shadow-card);
  border-color: var(--primary);
  transform: translateY(-1px);
}

.card-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  background: rgba(14, 165, 233, 0.15);
  color: var(--primary);
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.card-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.card-desc {
  font-size: 10px;
  color: var(--text-muted);
  margin: 0;
  line-height: 1.3;
}

.clickable {
  cursor: pointer;
}
</style>