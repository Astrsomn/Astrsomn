<template>
  <a-drawer
      :closable="false"
      :destroyOnClose="props.destroyOnClose"
      :maskClosable="props.maskClosable"
      :open="props.open"
      :root-class-name="mergedRootClassName"
      :width="props.width"
      :z-index="props.zIndex"
      placement="right"
      @close="closeDrawer"
  >
    <template #title>
      <div class="drawer-header">
        <div class="header-main">
          <div v-if="$slots.icon" class="header-icon">
            <slot name="icon"/>
          </div>
          <div class="title-content">
            <div class="title-row">
              <span class="main-title">
                <slot name="title"/>
              </span>
              <slot name="title-extra"/>
            </div>
            <div v-if="$slots.subtitle" class="sub-title">
              <slot name="subtitle"/>
            </div>
          </div>
        </div>
        <div class="header-actions">
          <slot name="header-actions"/>
          <a-button v-if="props.showClose" class="close-btn" type="text" @click="closeDrawer">
            <CloseOutlined/>
          </a-button>
        </div>
      </div>
    </template>

    <div class="drawer-layout">
      <div class="drawer-content">
        <slot/>
      </div>
      <div v-if="$slots.footer" class="drawer-footer">
        <slot name="footer"/>
      </div>
    </div>
  </a-drawer>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {CloseOutlined} from '@ant-design/icons-vue'

interface Props {
  open: boolean
  width?: number | string
  maskClosable?: boolean
  destroyOnClose?: boolean
  showClose?: boolean
  rootClassName?: string
  zIndex?: number
}

const props = withDefaults(defineProps<Props>(), {
  width: 500,
  maskClosable: true,
  destroyOnClose: true,
  showClose: true,
  rootClassName: '',
})

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void
}>()

const mergedRootClassName = computed(() =>
    ['astrsomn-drawer-shell', props.rootClassName].filter(Boolean).join(' ')
)

const closeDrawer = () => {
  emit('update:open', false)
}
</script>

<style scoped>
:deep(.astrsomn-drawer-shell .ant-drawer-header) {
  padding: 24px;
  border-bottom: 1px solid var(--border-default);
  background: var(--bg-card);
}

:deep(.astrsomn-drawer-shell .ant-drawer-body) {
  padding: 20px;
  background-color: var(--bg-card);
}

.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  gap: 16px;
}

.header-main {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
}

.header-icon {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  background: var(--primary-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.25);
  flex-shrink: 0;
}

.title-content {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.main-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-heading);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sub-title {
  font-size: 12px;
  color: var(--text-muted);
  display: flex;
  align-items: center;
  gap: 4px;
  font-family: 'JetBrains Mono', monospace;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.close-btn {
  color: var(--text-muted);
  font-size: 18px;
}

.drawer-layout {
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.drawer-content {
  flex: 1;
  min-height: 0;
  overflow: auto;
}

.drawer-footer {
  flex-shrink: 0;
  margin-top: 12px;
  padding-top: 16px;
  border-top: 1px solid var(--border-default);
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  background: var(--bg-card);
}
</style>
