<template>
  <section
      class="console-page-mod"
      :class="[
      `accent-${accent}`,
      sizeClass,
      layoutMode,
      { 'is-edit-mode': editMode }
    ]"
      @click="go"
  >
    <div v-if="editMode" class="mod-close-btn" @click.stop="$emit('remove')">
      <close-outlined />
    </div>

    <div class="mod-size-tag">{{ gridW }}x{{ gridH }}</div>

    <div class="mod-container">
      <div class="mod-icon-section">
        <div class="mod-icon-bg">
          <component :is="iconComponent" v-if="iconComponent" class="icon-glyph" />
          <appstore-outlined v-else class="icon-glyph" />
        </div>
      </div>

      <div class="mod-text-section">
        <h3 class="mod-title" :title="displayTitle">{{ displayTitle }}</h3>
        <p v-if="showDescription" class="mod-desc" :title="displayDescription">
          {{ displayDescription }}
        </p>
      </div>
    </div>

    <div v-if="showFootnote" class="mod-footer">
      <div class="mod-divider"></div>
      <span class="mod-foot-text">{{ mock.footnote }}</span>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { AppstoreOutlined, CloseOutlined } from '@ant-design/icons-vue'

const props = withDefaults(
    defineProps<{
      gridW: number
      gridH: number
      pageKind: string
      route: string
      editMode?: boolean
      mock?: any
      entryByRoute?: any
    }>(),
    {
      editMode: false,
      mock: () => ({ title: '标题', description: '这里是描述内容内容内容内容', footnote: '辅助信息' })
    }
)

const router = useRouter()

// 基础数据映射
const entry = computed(() => props.entryByRoute?.[props.route])
const displayTitle = computed(() => entry.value?.label ?? props.mock.title)
const displayDescription = computed(() => entry.value?.description ?? props.mock.description)
const accent = computed(() => entry.value?.accent ?? 'blue')
const iconComponent = computed(() => entry.value?.icon)

// 样式类计算
const sizeClass = computed(() => `size-${props.gridW}-${props.gridH}`)

// 核心排版逻辑
const layoutMode = computed(() => {
  const { gridW: w, gridH: h } = props
  if (w === 1 && h === 1) return 'layout-tiny'       // 1x1
  if (w > 1 && h === 1) return 'layout-horizontal'   // 2x1, 3x1
  if (w === 1 && h > 1) return 'layout-vertical'     // 1x2, 1x3
  return 'layout-standard'                           // 2x2, 3x2, 3x3 等
})

// 决定是否渲染描述
const showDescription = computed(() => {
  // 1x1 绝对不显示描述
  if (props.gridW === 1 && props.gridH === 1) return false
  // 1x2 高度不足时如果标题长也不显示描述 (可选)
  return true
})

// 决定是否渲染脚注
const showFootnote = computed(() => {
  return props.gridH >= 2 // 只有高度 >= 2 的卡片才显示脚注
})

const go = () => {
  if (props.editMode) return
  void router.push(props.route)
}
</script>

<style scoped>
/* 1. 基础容器与变量 */
.console-page-mod {
  --primary: var(--accent-blue, #2563eb);
  --bg-icon: var(--bg-elevated, #eff6ff);
  --border: var(--border-default, rgba(37, 99, 235, 0.1));

  width: 100%;
  height: 100%;
  background: var(--bg-card, #ffffff);
  border: 1px solid var(--border);
  border-radius: 16px;
  position: relative;
  display: flex;
  flex-direction: column;
  padding: 16px;
  box-sizing: border-box;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  overflow: hidden;
}

/* 颜色变体 */
.accent-blue { --primary: var(--accent-blue, #2563eb); --bg-icon: var(--bg-elevated, #eff6ff); }
.accent-green { --primary: var(--success, #10b981); --bg-icon: var(--bg-elevated, #f0fdf4); }
.accent-purple { --primary: var(--assembly-label-emb, #8b5cf6); --bg-icon: var(--bg-elevated, #f5f3ff); }

/* Hover 状态 */
.console-page-mod:hover:not(.is-edit-mode) {
  box-shadow: var(--shadow-overview, 0 10px 25px -5px rgba(37, 99, 235, 0.1));
  border-color: var(--primary);
  transform: translateY(-2px);
}

/* 2. 核心组件 */
.mod-container {
  flex: 1;
  display: flex;
  min-width: 0;
  min-height: 0;
}

.mod-icon-bg {
  background: var(--bg-icon);
  color: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.mod-title {
  margin: 0;
  color: var(--text-heading, #1e293b);
  font-weight: 700;
  line-height: 1.4;
}

.mod-desc {
  margin: 4px 0 0;
  color: var(--text-secondary, #64748b);
  font-size: 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 3. 脚注与分割线 (还原截图效果) */
.mod-footer {
  margin-top: auto;
  width: 100%;
  padding-top: 12px;
}

.mod-divider {
  width: 100%;
  height: 1px;
  border-top: 1px dashed var(--border-subtle, #e2e8f0);
  margin-bottom: 8px;
}

.mod-foot-text {
  font-size: 11px;
  color: var(--text-muted, #94a3b8);
  display: block;
}

/* 4. 布局模式适配 */

/* [Tiny 1x1] - 极简 */
.layout-tiny {
  padding: 12px;
  justify-content: center;
}
.layout-tiny .mod-container {
  flex-direction: column;
  align-items: center;
  text-align: center;
}
.layout-tiny .mod-icon-bg {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  margin-bottom: 8px;
}
.layout-tiny .icon-glyph { font-size: 18px; }
.layout-tiny .mod-title {
  font-size: 12px;
  white-space: normal;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* [Horizontal 2x1, 3x1] - 左右 */
.layout-horizontal .mod-container {
  flex-direction: row;
  align-items: center;
  gap: 16px;
}
.layout-horizontal .mod-icon-bg {
  width: 48px;
  height: 48px;
  border-radius: 12px;
}
.layout-horizontal .mod-title { font-size: 16px; }

/* [Vertical 1x2, 1x3] - 上下 */
.layout-vertical .mod-container {
  flex-direction: column;
  align-items: flex-start;
}
.layout-vertical .mod-icon-bg {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  margin-bottom: 16px;
}
.layout-vertical .mod-title { font-size: 18px; margin-bottom: 8px; }

/* [Standard 2x2, 3x2, 3x3] - 大卡片居中 */
.layout-standard .mod-container {
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 10px 0;
}
.layout-standard .mod-icon-bg {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  margin-bottom: 24px;
}
.layout-standard .icon-glyph { font-size: 32px; }
.layout-standard .mod-title { font-size: 22px; margin-bottom: 12px; }
.layout-standard .mod-desc { font-size: 14px; max-width: 90%; -webkit-line-clamp: 3; }

/* 5. 编辑模式特殊样式 */
.mod-close-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 20px;
  height: 20px;
  background: var(--bg-elevated, #f1f5f9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: var(--text-secondary, #64748b);
  z-index: 10;
  transition: background 0.2s;
}
.mod-close-btn:hover {
  background: var(--error, #ef4444);
  color: #ffffff;
}

.is-edit-mode {
  border-style: dashed;
  cursor: grab;
}

.mod-size-tag {
  position: absolute;
  top: 4px;
  left: 6px;
  font-size: 9px;
  color: var(--text-muted, #cbd5e1);
  pointer-events: none;
}
</style>