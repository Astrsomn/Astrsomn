<template>
  <div class="node-connector">
    <div class="agent-card">
      <div class="card-header">
        <div class="header-left">
          <div class="icon-badge">
            <RobotOutlined />
          </div>
          <div class="header-info">
            <input 
              v-model="agentNameLocal" 
              class="agent-name-input header-input"
              placeholder="智能体名称"
              maxlength="50"
              @input="handleAgentNameInput"
            />
          </div>
        </div>
        <div class="card-icon">
          <DoubleRightOutlined />
        </div>
      </div>
      <input 
        v-model="descriptionLocal" 
        class="agent-name-input desc-input"
        placeholder="智能体描述"
        maxlength="100"
        @input="handleDescriptionInput"
      />

    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { RobotOutlined, DoubleRightOutlined } from '@ant-design/icons-vue'

interface Props {
  agentName?: string
  description?: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'update:agentName', value: string): void
  (e: 'update:description', value: string): void
}>()

const agentNameLocal = computed({
  get: () => props.agentName || '',
  set: (val) => emit('update:agentName', val)
})

const descriptionLocal = computed({
  get: () => props.description || '',
  set: (val) => emit('update:description', val)
})

const handleAgentNameInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  emit('update:agentName', target.value)
}

const handleDescriptionInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  emit('update:description', target.value)
}
</script>

<style scoped>
.node-connector {
  flex: 1;
  position: relative;
}

.agent-card {
  background: var(--primary-gradient);
  border-radius: var(--radius-xl);
  padding: 16px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: var(--shadow-overview);
  color: white;
  cursor: pointer;
  min-height: 100px;
  display: flex;
  flex-direction: column;
}

.agent-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-card);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.icon-badge {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  backdrop-filter: blur(10px);
}

.icon-badge .anticon {
  font-size: 16px;
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.card-label {
  font-size: 10px;
  font-weight: bold;
  color: rgba(255, 255, 255, 0.8);
  text-transform: uppercase;
}

.card-icon {
  color: rgba(255, 255, 255, 0.3);
  font-size: 16px;
  position: absolute;
  top: 50%;
  right: 16px;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.agent-name-input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  backdrop-filter: blur(10px);
}

.header-input {
  font-weight: bold;
  margin-bottom: 8px;
  border: none;
  background: transparent;
  padding: 0;
}

.header-input:focus {
  box-shadow: none;
  background: transparent;
}

.desc-input {
  font-size: 12px;
  opacity: 0.9;
}

.agent-name-input::placeholder {
  color: rgba(255, 255, 255, 0.6);
}

.agent-name-input:focus {
  outline: none;
  border-color: white;
  background: rgba(255, 255, 255, 0.2);
  box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.1);
}

.card-subtitle {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  margin-top: 4px;
}
</style>
