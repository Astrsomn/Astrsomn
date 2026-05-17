<template>
  <a-input
      :disabled="disabled"
      :placeholder="placeholder"
      :size="size"
      :value="keySuffix"
      @update:value="onSuffixChange"
  >
    <template v-if="prefix" #addonBefore>
      <span class="key-prefix">{{ prefix }}</span>
    </template>
    <template #suffix>
      <a-space :size="4">
        <a-tooltip title="随机生成 Key">
          <ReloadOutlined class="input-action-icon" @click="generateRandomKey"/>
        </a-tooltip>
        <a-tooltip title="清空 Key">
          <CloseCircleOutlined v-if="keySuffix" class="input-action-icon input-action-icon--danger" @click="clearKey"/>
        </a-tooltip>
      </a-space>
    </template>
  </a-input>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {CloseCircleOutlined, ReloadOutlined} from '@ant-design/icons-vue'

const props = withDefaults(
    defineProps<{
      modelValue?: string
      prefix?: string
      placeholder?: string
      size?: 'large' | 'middle' | 'small'
      disabled?: boolean
      randomLength?: number
    }>(),
    {
      modelValue: '',
      prefix: '',
      placeholder: '请输入 Key',
      size: 'large',
      disabled: false,
      randomLength: 8
    }
)

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const keySuffix = computed(() => {
  const current = props.modelValue || ''
  if (!current) return ''
  if (!props.prefix) return current
  if (current.startsWith(props.prefix)) {
    return current.slice(props.prefix.length)
  }
  return current
})

function emitValueBySuffix(value: string) {
  const suffix = value.trim()
  if (!suffix) {
    emit('update:modelValue', '')
    return
  }
  emit('update:modelValue', `${props.prefix}${suffix}`)
}

function onSuffixChange(value: string) {
  emitValueBySuffix(value)
}

function generateRandomKey() {
  if (props.disabled) return
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
  const random = Array.from({length: Math.max(4, props.randomLength)}, () => {
    const index = Math.floor(Math.random() * chars.length)
    return chars[index]
  }).join('')
  emit('update:modelValue', `${props.prefix}${random}`)
}

function clearKey() {
  if (props.disabled) return
  emit('update:modelValue', '')
}
</script>

<style scoped>
.key-prefix {
  color: var(--text-muted, #64748b);
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

.input-action-icon {
  color: var(--text-muted, #94a3b8);
  cursor: pointer;
}

.input-action-icon:hover {
  color: var(--primary, #1677ff);
}

.input-action-icon--danger:hover {
  color: #ff4d4f;
}
</style>
