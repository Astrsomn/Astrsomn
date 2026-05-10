<template>
  <a-modal
    :open="open"
    :width="width"
    :footer="footer"
    :closable="closable"
    :destroy-on-close="destroyOnClose"
    :wrap-class-name="wrapClass"
    :wrap-style="mergedWrapStyle"
    @update:open="emit('update:open', $event)"
    @cancel="emit('cancel')"
  >
    <div class="fsm-root">
      <header class="fsm-header">
        <div class="fsm-header-left">
          <div v-if="$slots['header-logo']" class="fsm-logo-box">
            <slot name="header-logo" />
          </div>
          <div class="fsm-title-group">
            <span v-if="$slots['header-title']" class="fsm-main-title">
              <slot name="header-title" />
            </span>
            <span v-if="$slots['header-subtitle']" class="fsm-sub-title">
              <slot name="header-subtitle" />
            </span>
          </div>
        </div>
        <div v-if="$slots['header-actions']" class="fsm-header-actions">
          <slot name="header-actions" />
        </div>
      </header>
      <div class="fsm-main" :style="mainAreaStyle">
        <slot />
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, type CSSProperties } from 'vue'

const BASE_WRAP_CLASS = 'astrsomn-fullscreen-shell'

interface Props {
  open: boolean
  /** 传给 a-modal 的 width，如 `100%`、`1200` */
  width?: string | number
  /** 弹层内容区高度，如 `100vh`、`90vh`、`800px` */
  bodyHeight?: string
  /** 弹层内容区最大高度，如 `800px`、`90vh` */
  maxBodyHeight?: string
  /** .ant-modal 最大宽度，如 `100vw`、`min(100vw, 1400px)` */
  maxWidth?: string
  /** 顶栏最小高度，如 `72px` */
  headerHeight?: string
  /** .ant-modal-content 背景色 */
  contentBackground?: string
  /** 主区域 padding */
  mainPadding?: string
  /** 主区域背景色 */
  mainBackground?: string
  closable?: boolean
  destroyOnClose?: boolean
  footer?: null
  /** 追加在基础 wrap class 之后 */
  wrapClassName?: string
  /** 会与尺寸 CSS 变量合并，同名键优先生效 */
  wrapStyle?: CSSProperties
}

const props = withDefaults(defineProps<Props>(), {
  width: '100%',
  bodyHeight: '100vh',
  maxBodyHeight: '',
  maxWidth: '100vw',
  headerHeight: '72px',
  contentBackground: 'var(--bg-card, #f8fafc)',
  mainPadding: '0px',
  mainBackground: 'var(--bg-surface, #f8fafc)',
  closable: false,
  destroyOnClose: true,
  footer: null,
  wrapStyle: () => ({}),
})

const emit = defineEmits<{
  'update:open': [value: boolean]
  cancel: []
}>()

const wrapClass = computed(() =>
  [BASE_WRAP_CLASS, props.wrapClassName].filter(Boolean).join(' ')
)

const mergedWrapStyle = computed(() => ({
  '--fsm-max-width': props.maxWidth,
  '--fsm-body-height': props.bodyHeight,
  '--fsm-max-body-height': props.maxBodyHeight || 'none',
  '--fsm-header-height': props.headerHeight,
  '--fsm-modal-content-bg': props.contentBackground,
  ...(props.wrapStyle ?? {}),
}))

const mainAreaStyle = computed(() => ({
  padding: props.mainPadding,
  background: props.mainBackground,
}))
</script>

<style scoped>
/* 全屏弹层：顶对齐、去圆角，尺寸由 wrap 上的 CSS 变量控制 */
:global(.astrsomn-fullscreen-shell.ant-modal-wrap) {
  display: flex;
  align-items: center;
  justify-content: center;
}

:global(.astrsomn-fullscreen-shell .ant-modal) {
  max-width: var(--fsm-max-width, 100vw);
  padding: 0;
  top: 0;
  margin: 0;
}

:global(.astrsomn-fullscreen-shell .ant-modal-content) {
  height: var(--fsm-body-height, 100vh);
  max-height: var(--fsm-max-body-height, none);
  border-radius: 0;
  padding: 0;
  background: var(--fsm-modal-content-bg, var(--bg-card, #f8fafc));
}

:global(.astrsomn-fullscreen-shell .ant-modal-body) {
  height: 100%;
  padding: 0;
}

.fsm-root {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  overflow: hidden;
}

.fsm-header {
  min-height: var(--fsm-header-height, 72px);
  box-sizing: border-box;
  background: var(--bg-card, #fff);
  padding: 0 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--border-default, #e2e8f0);
  flex-shrink: 0;
}

.fsm-header-left {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
}

.fsm-logo-box {
  width: 42px;
  height: 42px;
  border-radius: 10px;
  background: var(--primary-gradient, linear-gradient(135deg, #2563eb 0%, #3b82f6 100%));
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 22px;
  flex-shrink: 0;
  box-shadow:
    0 1px 0 rgba(255, 255, 255, 0.2) inset,
    0 2px 6px rgba(29, 78, 216, 0.35);
}

.fsm-title-group {
  min-width: 0;
}

.fsm-main-title {
  display: block;
  font-size: 18px;
  font-weight: 800;
  color: var(--text-heading, #0f172a);
}

.fsm-sub-title {
  display: block;
  font-size: 12px;
  color: var(--text-muted, #94a3b8);
}

.fsm-header-actions {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.fsm-main {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}
</style>
