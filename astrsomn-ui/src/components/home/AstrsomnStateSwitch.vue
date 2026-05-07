<template>
  <div class="trio-switch-wrapper">
    <div 
      class="trio-container" 
      :style="{ '--active-index': activeIndex, '--active-color': activeColor }"
    >
      <div class="trio-slider"></div>

      <div 
        v-for="(option, index) in options" 
        :key="option.value"
        class="trio-item"
        :class="{ 'is-active': modelValue === option.value }"
        @click="handleSelect(option.value, index)"
      >
        <component :is="option.icon" v-if="option.icon" class="item-icon" />
        <span class="item-text">{{ option.label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { 
  AppstoreOutlined, 
  CheckCircleOutlined, 
  StopOutlined 
} from '@ant-design/icons-vue';

// 定义 Props
const props = withDefaults(defineProps<{
  modelValue: string | number | undefined;
  options?: Array<{
    label: string;
    value: string | number | undefined;
    color: string;
    icon?: any;
  }>;
}>(), {
  options: () => [
    { label: '全部', value: undefined, color: '#1676fd', icon: AppstoreOutlined },
    { label: '启用', value: 'enabled', color: '#10b981', icon: CheckCircleOutlined },
    { label: '禁用', value: 'disabled', color: '#f43f5e', icon: StopOutlined },
  ]
});

// 定义 Emit
const emit = defineEmits(['update:modelValue', 'change']);

// 选项配置 - 使用 props 传入或默认值
const options = props.options;

// 计算当前激活的索引
const activeIndex = computed(() => {
  const index = options.findIndex(opt => {
    // 处理 undefined 值的情况
    if (opt.value === undefined && props.modelValue === undefined) {
      return true;
    }
    return opt.value === props.modelValue;
  });
  return index >= 0 ? index : 0;
});

// 计算当前激活的主题色
const activeColor = computed(() => {
  return options[activeIndex.value]?.color || '#1676fd';
});

// 切换处理
const handleSelect = (value: string | number, index: number) => {
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

/* 核心：弹性滑块设计 */
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
  
  /* 弹性反馈动画控制 */
  transition: transform 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55),
              border-color 0.3s ease;
  
  /* 基于 CSS 变量的位移 */
  transform: translateX(calc(var(--active-index) * var(--item-width)));
  border: 2px solid var(--active-color);
}

/* 悬浮微动效 */
.trio-item:hover:not(.is-active) {
  color: var(--text-primary);
}

.trio-container:active .trio-slider {
  transform: translateX(calc(var(--active-index) * var(--item-width))) scale(0.95);
}
</style>