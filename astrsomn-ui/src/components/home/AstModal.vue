<template>
  <a-modal
      :closable="false"
      :destroy-on-close="destroyOnClose"
      :footer="footer"
      :open="open"
      :width="width"
      :wrap-class-name="wrapClass"
      :wrap-style="mergedWrapStyle"
      :z-index="props.zIndex"
      @cancel="emit('cancel')"
      @update:open="emit('update:open', $event)"
  >
    <div class="fsm-root">
      <header class="fsm-header">
        <div class="fsm-header-left">
          <div v-if="$slots['header-logo']" class="fsm-logo-box">
            <slot name="header-logo"/>
          </div>
          <div class="fsm-title-group">
            <span v-if="$slots['header-title']" class="fsm-main-title">
              <slot name="header-title"/>
            </span>
            <span v-if="$slots['header-subtitle']" class="fsm-sub-title">
              <slot name="header-subtitle"/>
            </span>
          </div>
        </div>
        <div v-if="$slots['header-actions']" class="fsm-header-actions">
          <slot name="header-actions"/>
        </div>
        <div v-else class="fsm-header-actions">
          <a-button
              v-if="confirmText"
              :disabled="confirmDisabled"
              :loading="confirmLoading"
              class="fsm-confirm-btn"
              type="primary"
              @click="emit('confirm')"
          >
            <template #icon>
              <component :is="confirmIcon" />
            </template>
            {{ confirmText }}
          </a-button>
          <a-button class="fsm-close-btn" type="text" @click="handleClose">
            <CloseOutlined />
          </a-button>
        </div>
      </header>
      <div :style="mainAreaStyle" class="fsm-main">
        <slot/>
      </div>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
import {type Component, computed, type CSSProperties} from 'vue'
import {CheckOutlined, CloseOutlined} from '@ant-design/icons-vue'

const BASE_WRAP_CLASS = 'astrsomn-fullscreen-shell'

interface Props {
  open: boolean

  width?: string | number

  bodyHeight?: string

  maxBodyHeight?: string

  maxWidth?: string

  headerHeight?: string

  contentBackground?: string

  mainPadding?: string

  mainBackground?: string
  destroyOnClose?: boolean
  footer?: null

  wrapClassName?: string

  wrapStyle?: CSSProperties

  confirmText?: string

  confirmIcon?: Component
  confirmLoading?: boolean
  confirmDisabled?: boolean

  zIndex?: number
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
  destroyOnClose: true,
  footer: null,
  wrapStyle: () => ({}),
  confirmIcon: () => CheckOutlined,
})

const emit = defineEmits<{
  'update:open': [value: boolean]
  cancel: []
  confirm: []
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

function handleClose() {
  emit('update:open', false)
  emit('cancel')
}
</script>

<style scoped>

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
  border-radius: var(--radius-lg, 12px);
  padding: 0;
  overflow: hidden;
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
  box-shadow: 0 1px 0 color-mix(in srgb, var(--bg-surface) 20%, transparent) inset,
  0 2px 6px color-mix(in srgb, var(--primary) 35%, transparent);
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
  gap: 8px;
  flex-shrink: 0;
}

.fsm-confirm-btn {
  height: 36px;
  border-radius: 8px;
  font-weight: 500;
  padding: 0 16px;
}

.fsm-close-btn {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted, #94a3b8);
}

.fsm-close-btn:hover {
  color: var(--text-primary, #0f172a);
  background: color-mix(in srgb, var(--shadow-color, #000) 4%, transparent);
}

.fsm-main {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}
</style>
