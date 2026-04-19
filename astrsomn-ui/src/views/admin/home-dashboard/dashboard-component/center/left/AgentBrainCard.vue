<template>
  <div class="bento-card col-span-2 row-span-2 main-card">
    <div class="card-header">
      <div>
        <h2 class="card-title">智能体大脑 (Agents)</h2>
        <p class="card-desc">封装推理逻辑与 Agent 运行时状态</p>
      </div>
      <button class="btn btn-primary">
        <i class="fa-solid fa-plus"></i>新建智能体
      </button>
    </div>
    
    <div class="agent-list">
        <div class="agent-item">
          <div class="agent-info">
            <img class="agent-icon" src="@/assets/dashboard-icons/agents-color.svg" alt="agent" />
            <div>
              <h4 class="agent-name">星梦助手 (Astrsomn-Helper)</h4>
              <p class="agent-meta">关联实例: 4个 | 运行环境: PROD</p>
            </div>
          </div>
          <span class="status-pill status-active">在线活跃</span>
        </div>
        <div class="agent-item opacity-60">
          <div class="agent-info">
            <img class="agent-icon agent-icon--inactive" src="@/assets/dashboard-icons/agents-color.svg" alt="agent" />
            <div>
              <h4 class="agent-name">代码分析专家</h4>
              <p class="agent-meta">未关联实例 | 上次运行: 2天前</p>
            </div>
          </div>
          <span class="status-pill status-inactive">已停用</span>
        </div>
      </div>
    
    <div class="card-footer">
      <div class="avatars">
        <div class="avatar avatar-blue">DS</div>
        <div class="avatar avatar-green">GPT</div>
        <div class="avatar avatar-orange">CLA</div>
      </div>
      <a href="#" class="link">查看全部编排 <i class="fa-solid fa-arrow-right"></i></a>
    </div>
  </div>
</template>

<style scoped>
.bento-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 24px;
  padding: 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.bento-card:hover {
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05);
  border-color: #3b82f6;
}

.col-span-2 {
  grid-column: span 2;
}

.row-span-2 {
  grid-row: span 2;
}

.main-card {
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.card-title {
  font-size: 24px;
  font-weight: bold;
  color: #1f2937;
  margin: 0;
}

.card-desc {
  font-size: 14px;
  color: #9ca3af;
  margin: 4px 0 0;
}

.btn {
  padding: 8px 16px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  border: none;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: background 0.2s;
}

.btn-primary {
  background: #2563eb;
  color: white;
}

.btn-primary:hover {
  background: #1d4ed8;
}

.agent-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  flex: 1;
}

.agent-item {
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  transition: background 0.2s;
}

.agent-item:hover {
  background: #f9fafb;
}

.agent-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.agent-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
}

.agent-icon--inactive {
  opacity: 0.5;
}

.agent-name {
  font-weight: bold;
  color: #374151;
  margin: 0;
}

.agent-meta {
  font-size: 12px;
  color: #9ca3af;
  margin: 4px 0 0;
}

.status-pill {
  padding: 4px 12px;
  border-radius: 9999px;
  font-size: 12px;
  font-weight: 600;
}

.status-active {
  background: #d1fae5;
  color: #059669;
}

.status-inactive {
  background: #f3f4f6;
  color: #6b7280;
}

.card-footer {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatars {
  display: flex;
  gap: -12px;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 2px solid white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: white;
  margin-right: -8px;
}

.avatar-blue { background: #38bdf8; }
.avatar-green { background: #34d399; }
.avatar-orange { background: #fb923c; }

.link {
  color: #2563eb;
  font-size: 14px;
  font-weight: bold;
  text-decoration: none;
}

.opacity-60 {
  opacity: 0.6;
}
</style>
