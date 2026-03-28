<template>
  <section class="mode-switch-card">
    <div class="mode-copy">
      <div class="mode-title">控制台首页</div>
      <div class="mode-desc">在数据大屏和管理后台之间快速切换，减少信息干扰。</div>
    </div>

    <div class="mode-switch">
      <span class="mode-label" :class="{ 'mode-label--active': modelValue === 'screen' }">数据大屏</span>
      <a-switch
        :checked="modelValue === 'management'"
        checked-children="管"
        un-checked-children="屏"
        @change="handleChange"
      />
      <span class="mode-label" :class="{ 'mode-label--active': modelValue === 'management' }">管理后台</span>
    </div>
  </section>
</template>

<script lang="ts">
export default {
  name: 'DashboardModeSwitch',
}
</script>

<script setup lang="ts">
type DashboardMode = 'screen' | 'management'

defineProps<{
  modelValue: DashboardMode
}>()

const emit = defineEmits<{
  'update:modelValue': [value: DashboardMode]
}>()

const handleChange = (checked: boolean) => {
  emit('update:modelValue', checked ? 'management' : 'screen')
}
</script>

<style scoped>
.mode-switch-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 24px;
  padding: 18px 20px;
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: 18px;
  box-shadow: var(--shadow-card);
}

.mode-copy {
  min-width: 0;
}

.mode-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-heading);
}

.mode-desc {
  margin-top: 4px;
  font-size: 13px;
  line-height: 1.5;
  color: var(--text-muted);
}

.mode-switch {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  border-radius: 999px;
  background: color-mix(in srgb, var(--primary) 5%, var(--bg-card));
  border: 1px solid color-mix(in srgb, var(--primary) 14%, var(--border-subtle));
  flex-shrink: 0;
}

.mode-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-muted);
  transition: color 0.2s ease;
}

.mode-label--active {
  color: var(--text-heading);
}

@media (max-width: 900px) {
  .mode-switch-card {
    flex-direction: column;
    align-items: stretch;
  }

  .mode-switch {
    justify-content: center;
  }
}

@media (max-width: 560px) {
  .mode-switch {
    gap: 10px;
    padding: 10px 12px;
  }

  .mode-label {
    font-size: 12px;
  }
}
</style>
