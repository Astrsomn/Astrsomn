<template>
  <div class="credential-card" @click="navigateTo('/admin/ai-account')">
    <div class="card-icon sky-icon">
      <KeyOutlined />
    </div>
    <h3 class="card-title">凭证管理</h3>
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
  border-radius: 16px;
  padding: 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.credential-card:hover {
  box-shadow: var(--shadow-card);
  border-color: var(--primary);
  transform: translateY(-2px);
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  background: rgba(14, 165, 233, 0.15);
  color: #0ea5e9;
}

.card-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.card-desc {
  font-size: 12px;
  color: var(--text-muted);
  margin: 0;
  line-height: 1.4;
}
</style>