<template>
  <AdminPageShell
    title="安全治理中心"
    description="统一管理模板、敏感词与链路追踪模块。"
    empty-text="暂无可管理模块。"
  >
    <div class="center-page">
      <div class="card-grid">
        <button
          v-for="item in moduleCards"
          :key="item.routeName"
          class="route-card"
          type="button"
          @click="goTo(item.routeName)"
        >
          <div class="card-title">{{ item.title }}</div>
          <div class="card-desc">{{ item.desc }}</div>
        </button>
      </div>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import AdminPageShell from '@/components/home/AdminPageShell.vue'

type ModuleCard = {
  title: string
  desc: string
  routeName: string
}

const router = useRouter()

const moduleCards: ModuleCard[] = [
  { title: 'FTL 模板管理', desc: '管理提示词模板与输出模版。', routeName: 'AdminTemplates' },
  { title: '敏感词治理', desc: '维护敏感词策略与拦截规则。', routeName: 'AdminSecurity' },
  { title: '链路追踪', desc: '查看请求链路与审计日志。', routeName: 'AdminTracing' }
]

const goTo = (routeName: string) => {
  void router.push({ name: routeName })
}
</script>

<style scoped>
.center-page {
  padding: 20px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.route-card {
  min-height: 150px;
  border: 1px solid var(--border-default);
  border-radius: 14px;
  background: var(--bg-card);
  padding: 20px;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
}

.route-card:hover {
  border-color: var(--primary);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-heading);
}

.card-desc {
  margin-top: 10px;
  color: var(--text-secondary);
  line-height: 1.6;
}

@media (max-width: 1100px) {
  .card-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .card-grid {
    grid-template-columns: 1fr;
  }
}
</style>
