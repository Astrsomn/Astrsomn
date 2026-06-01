<template>
  <div class="trio-switch-wrapper">
    <div
        :style="{ '--active-index': activeIndex, '--active-color': activeColor }"
        class="trio-container"
    >
      <div class="trio-slider"></div>

      <div
          v-for="(option, index) in effectiveOptions"
          :key="option.value"
          :class="{ 'is-active': modelValue === option.value }"
          class="trio-item"
          @click="handleSelect(option.value, index)"
      >
        <component :is="option.icon" v-if="option.icon" class="item-icon"/>
        <span class="item-text">{{ option.label }}</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue';
import {AppstoreOutlined, CheckCircleOutlined, StopOutlined} from '@ant-design/icons-vue';
import {usePageTranslation} from '@/locales/pages.ts';

const t = usePageTranslation('common')

const props = withDefaults(defineProps<{
  modelValue: string | number | undefined;
  options?: Array<{
    label: string;
    value: string | number | undefined;
    color: string;
    icon?: any;
  }>;
}>(), {
  options: () => []
});


const emit = defineEmits(['update:modelValue', 'change']);

const defaultOptions = computed(() => [
  {label: t.value.statusSwitch.all, value: undefined as string | number | undefined, color: '#1676fd', icon: AppstoreOutlined},
  {label: t.value.statusSwitch.enabled, value: 'enabled' as string | number | undefined, color: '#10b981', icon: CheckCircleOutlined},
  {label: t.value.statusSwitch.disabled, value: 'disabled' as string | number | undefined, color: '#f43f5e', icon: StopOutlined},
])

const effectiveOptions = computed(() => {
  if (props.options && props.options.length > 0) return props.options
  return defaultOptions.value
})

const activeIndex = computed(() => {
  const index = effectiveOptions.value.findIndex((opt: { value: string | number | undefined }) => {
    if (opt.value === undefined && props.modelValue === undefined) {
      return true;
    }
    return opt.value === props.modelValue;
  });
  return index >= 0 ? index : 0;
});

const activeColor = computed(() => {
  return effectiveOptions.value[activeIndex.value]?.color || '#1676fd';
});

const handleSelect = (value: string | number | undefined) => {
  if (value !== props.modelValue) {
    emit('update:modelValue', value);
    emit('change', value);
  }
};
</script>

<style scoped>
.trio-switch-wrapper {
  display: inline-block;
  vertical-align: middle;
}

.trio-container {
  --item-width: 90px;
  --item-height: 42px;
  --gap: 4px;

  display: flex;
  background: var(--bg-surface);
  padding: var(--gap);
  border-radius: var(--radius-md);
  position: relative;
  cursor: pointer;
  user-select: none;
  border: 1px solid var(--border-default);
  height: 50px;
  box-sizing: border-box;
}


.trio-item {
  position: relative;
  z-index: 2;
  width: var(--item-width);
  height: var(--item-height);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  transition: all 0.3s ease;
}

.trio-item.is-active {
  color: var(--active-color);
}

.item-icon {
  font-size: 14px;
}


.trio-slider {
  position: absolute;
  top: var(--gap);
  left: var(--gap);
  z-index: 1;
  width: var(--item-width);
  height: var(--item-height);
  background: var(--bg-card);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-card);


  transition: transform 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55),
  border-color 0.3s ease;


  transform: translateX(calc(var(--active-index) * var(--item-width)));
  border: 2px solid var(--active-color);
}


.trio-item:hover:not(.is-active) {
  color: var(--text-primary);
}

.trio-container:active .trio-slider {
  transform: translateX(calc(var(--active-index) * var(--item-width))) scale(0.95);
}
</style>