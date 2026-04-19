<template>
  <div class="h-screen overflow-hidden flex flex-col" style="font-size: 13px; color: var(--text-primary);">
    <!-- 导航栏 -->
    <header class="h-14" style="background: var(--bg-card); border-bottom: 1px solid var(--border-default); display: flex; align-items: center; justify-content: space-between; padding: 0 32px; z-index: 40; position: sticky; top: 0;">
      <div style="display: flex; align-items: center; gap: 24px;">
        <div style="display: flex; align-items: center; gap: 8px;">
          <div style="width: 32px; height: 32px; background: var(--primary-gradient); border-radius: var(--radius-lg); display: flex; align-items: center; justify-content: center; color: white; box-shadow: var(--shadow-overview);">
            <span class="anticon anticon-wand"><svg viewBox="64 64 896 896" focusable="false" data-icon="wand" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M734 314.1a31.93 31.93 0 00-45.2 0L498.6 494.8 329.2 325.4a31.93 31.93 0 00-45.2 0l-42.4 42.4a31.93 31.93 0 000 45.2l169.4 169.4-179.2 179.2a31.93 31.93 0 000 45.2l42.4 42.4a31.93 31.93 0 0045.2 0l179.2-179.2 169.4 169.4a31.93 31.93 0 0045.2 0l42.4-42.4a31.93 31.93 0 000-45.2L543.8 537.2l190.7-190.7a31.93 31.93 0 000-45.2l-42.5-42.4zm-194.3 243L340.8 526.9 458 410l138.9 138.9-73.2 73.2z"></path></svg></span>
          </div>
          <h1 style="font-weight: bold; font-size: 18px; color: var(--text-heading);">Agent Studio</h1>
        </div>
        <div style="width: 1px; height: 16px; background: var(--border-default);"></div>
        <div style="font-size: 11px; font-weight: 500; color: var(--text-secondary);">正在编辑: <span style="color: var(--text-primary);">未命名的智能体</span></div>
      </div>
      <div style="display: flex; align-items: center; gap: 16px;">
        <button style="color: var(--text-secondary); font-weight: 600; background: none; border: none; cursor: pointer; transition: color 0.3s;" @mouseenter="this.style.color = 'var(--text-primary)'" @mouseleave="this.style.color = 'var(--text-secondary)'">预览</button>
        <button style="background: var(--primary-gradient); color: white; padding: 8px 20px; border-radius: var(--radius-lg); font-weight: bold; border: none; cursor: pointer; box-shadow: var(--shadow-overview); transition: all 0.3s;" @mouseenter="this.style.opacity = '0.9'" @mouseleave="this.style.opacity = '1'" @mousedown="this.style.transform = 'scale(0.95)'" @mouseup="this.style.transform = 'scale(1)'">
          完成创建
        </button>
      </div>
    </header>

    <main style="flex: 1; display: flex; overflow: hidden;">
      <!-- 左侧配置工作台 -->
      <section style="flex: 1; display: flex; flex-direction: column; overflow: hidden; background: var(--bg-base);">
        <!-- 主区域 (核心链路) -->
        <LeftTop />
        
        <!-- 次区域 (功能增强面板) -->
        <LeftCenter />
      </section>

      <!-- 右侧：预览测试区 -->
      <Right />
    </main>

    <!-- 悬浮工具栏 (底部入口) -->
    <div style="position: fixed; bottom: 24px; left: 32px; display: flex; gap: 12px; z-index: 50;">
      <div style="background: var(--bg-card); border: 1px solid var(--border-default); border-radius: 20px; padding: 6px; display: flex; align-items: center; gap: 4px; box-shadow: var(--shadow-card);">
        <button style="width: 40px; height: 40px; border-radius: var(--radius-lg); display: flex; align-items: center; justify-content: center; color: var(--text-secondary); background: none; border: none; cursor: pointer; transition: all 0.3s;" @mouseenter="this.style.color = 'var(--primary)'; this.style.background = 'var(--primary-hover)'" @mouseleave="this.style.color = 'var(--text-secondary)'; this.style.background = 'none'" title="配置导出">
          <span class="anticon anticon-code"><svg viewBox="64 64 896 896" focusable="false" data-icon="code" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M259.9 784.2L100.3 624.7a32 32 0 010-45.4l159.6-159.6a32 32 0 0145.4 45.4L181.7 512l123.6 123.6a32 32 0 11-45.4 45.4zM764.1 784.2L823 725.3a32 32 0 000-45.4L663.4 520.3a32 32 0 00-45.4 45.4L742.3 640l-123.6 123.6a32 32 0 1045.4 45.4z"></path></svg></span>
        </button>
        <button style="width: 40px; height: 40px; border-radius: var(--radius-lg); display: flex; align-items: center; justify-content: center; color: var(--text-secondary); background: none; border: none; cursor: pointer; transition: all 0.3s;" @mouseenter="this.style.color = 'var(--primary)'; this.style.background = 'var(--primary-hover)'" @mouseleave="this.style.color = 'var(--text-secondary)'; this.style.background = 'none'" title="版本管理">
          <span class="anticon anticon-history"><svg viewBox="64 64 896 896" focusable="false" data-icon="history" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M862 446H648V180c0-4.4-3.6-8-8-8h-56c-4.4 0-8 3.6-8 8v316c0 4.4 3.6 8 8 8h184c4.4 0 8-3.6 8-8v-56c0-4.4-3.6-8-8-8zm-724 66c0-4.4-3.6-8-8-8h-56c-4.4 0-8 3.6-8 8v56c0 4.4 3.6 8 8 8h184c4.4 0 8-3.6 8-8V262c0-4.4-3.6-8-8-8H100c-4.4 0-8 3.6-8 8v298c0 141.4 114.6 256 256 256s256-114.6 256-256H542c0 79.5-64.5 144-144 144s-144-64.5-144-144z"></path></svg></span>
        </button>
        <div style="width: 1px; height: 20px; background: var(--border-default); margin: 0 4px;"></div>
        <button style="width: 40px; height: 40px; border-radius: var(--radius-lg); display: flex; align-items: center; justify-content: center; color: var(--text-secondary); background: none; border: none; cursor: pointer; transition: all 0.3s;" @mouseenter="this.style.color = 'var(--primary)'; this.style.background = 'var(--primary-hover)'" @mouseleave="this.style.color = 'var(--text-secondary)'; this.style.background = 'none'" title="帮助文档">
          <span class="anticon anticon-question-circle"><svg viewBox="64 64 896 896" focusable="false" data-icon="question-circle" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372zm0-660c-33.5 0-60 26.5-60 60s26.5 60 60 60 60-26.5 60-60-26.5-60-60-60zm-32 344h64c4.4 0 8-3.6 8-8 0-17.7-14.3-32-32-32s-32 14.3-32 32c0 4.4 3.6 8 8 8zm32-184c-101.7 0-184 82.3-184 184 0 101.7 82.3 184 184 184s184-82.3 184-184c0-101.7-82.3-184-184-184zm0 320c-79.5 0-144-64.5-144-144s64.5-144 144-144 144 64.5 144 144-64.5 144-144 144z"></path></svg></span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import LeftTop from './component/LeftTop.vue';
import LeftCenter from './component/LeftCenter.vue';
import Right from './component/Right.vue';
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: var(--border-default);
  border-radius: 10px;
}
</style>