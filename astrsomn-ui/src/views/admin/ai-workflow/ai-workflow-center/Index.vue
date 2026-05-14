<template>
  <AstrsomnDashboardWrapper>
    <div class="workflow-center-page">
      <a-row :gutter="[16, 16]">
        <a-col v-for="item in summaryCards" :key="item.label" :sm="12" :xl="6" :xs="24">
          <a-card :bordered="false" class="summary-card">
            <div class="summary-label">{{ item.label }}</div>
            <div class="summary-value">{{ item.value }}</div>
            <div class="summary-hint">{{ item.hint }}</div>
          </a-card>
        </a-col>
      </a-row>

      <a-card :bordered="false" class="module-card" title="模块导航">
        <a-row :gutter="[16, 16]">
          <a-col v-for="item in moduleCards" :key="item.title" :sm="12" :xl="8" :xs="24">
            <a-card class="jump-card" hoverable @click="goTo(item.routeName)">
              <div class="jump-title">{{ item.title }}</div>
              <div class="jump-desc">{{ item.desc }}</div>
            </a-card>
          </a-col>
        </a-row>
      </a-card>
    </div>
  </AstrsomnDashboardWrapper>
</template>

<script lang="ts" setup>
import {useRouter} from 'vue-router'
import AstrsomnDashboardWrapper from '@/components/home/AstrsomnDashboardWrapper.vue'

const router = useRouter()

const summaryCards = [
  {label: '流程定义', value: '24', hint: '包含草稿与已发布流程'},
  {label: '发布版本', value: '61', hint: '当前环境历史版本'},
  {label: '运行实例', value: '1,328', hint: '近 7 天触发总量'},
  {label: '待处理人工任务', value: '12', hint: '需要人工审批节点'}
]

const moduleCards = [
  {title: '流程定义', desc: '维护流程主图和节点配置', routeName: 'AdminWorkflowDefinitions'},
  {title: '流程发布', desc: '查看版本快照与最新版本', routeName: 'AdminWorkflowDeployments'},
  {title: '流程实例', desc: '跟踪业务执行状态和上下文', routeName: 'AdminWorkflowInstances'},
  {title: '节点历史', desc: '查看节点输入输出与耗时', routeName: 'AdminWorkflowNodeHistory'},
  {title: '人工任务', desc: '处理人工审批与回写数据', routeName: 'AdminWorkflowHumanTasks'}
]

const goTo = (routeName: string) => {
  void router.push({name: routeName})
}
</script>

<style scoped>
.workflow-center-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.summary-card,
.module-card,
.jump-card {
  border-radius: 12px;
}

.summary-label {
  color: var(--text-secondary);
  font-size: 13px;
}

.summary-value {
  margin-top: 8px;
  font-size: 26px;
  font-weight: 600;
  line-height: 1.2;
}

.summary-hint {
  margin-top: 8px;
  color: var(--text-secondary);
  font-size: 12px;
}

.jump-title {
  font-size: 15px;
  font-weight: 600;
}

.jump-desc {
  margin-top: 8px;
  color: var(--text-secondary);
  font-size: 13px;
}
</style>
