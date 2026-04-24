<template>
  <AdminPageShell
    title="系统管理中心"
    description="统一管理系统用户、环境、配置与扩展模块。"
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
  { title: '用户管理', desc: '维护系统用户与权限角色。', routeName: 'AdminUsers' },
  { title: '环境管理', desc: '维护系统运行环境与配置隔离。', routeName: 'AdminEnv' },
  { title: '系统配置', desc: '管理系统参数与配置项。', routeName: 'AdminSystemConfig' },
  { title: '系统扩展', desc: '管理扩展安装与市场模块。', routeName: 'AdminSystemExtension' }
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
  grid-template-columns: repeat(2, minmax(0, 1fr));
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

@media (max-width: 900px) {
  .card-grid {
    grid-template-columns: 1fr;
  }
}
</style>
