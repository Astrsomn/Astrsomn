<template>
  <div class="code-panel">
    <div class="code-header">
      <div class="header-left">
        <code-outlined class="header-icon"/>
        <span class="header-title">{{ t.codePanel.title }}</span>
      </div>
      <button class="copy-btn" @click="copyCode">
        <template v-if="copied">
          <check-outlined/>
          {{ t.codePanel.copied }}
        </template>
        <template v-else>
          <copy-outlined/>
          {{ t.codePanel.copy }}
        </template>
      </button>
    </div>
    <div class="code-container">
      <pre class="code-content"><code>{{ codeTemplate }}</code></pre>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {ref} from 'vue';
import {message} from 'ant-design-vue';
import {CheckOutlined, CodeOutlined, CopyOutlined} from '@ant-design/icons-vue';
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-builder')

const copied = ref(false);

const codeTemplate = `public class TaskService {

    private final AstroAssistantFactory assistantFactory;

    public String createTask() {

        var param = AstroChatRequest.of(TaskCreateAssistant.class, "deepseek-sensor");
        param.setToolStrategy(new ToolStrategy());


        TaskCreateAssistant assistant = assistantFactory.createAssistant(param);


        return assistant.chat("帮我创建一个任务", UUID.fastUUID().toString());
    }
}`;

const copyCode = async () => {
  try {
    await navigator.clipboard.writeText(codeTemplate);
    copied.value = true;
    message.success(t.value.codePanel.codeCopied);
    setTimeout(() => {
      copied.value = false;
    }, 2000);
  } catch {
    message.error(t.value.codePanel.copyFailed);
  }
};
</script>

<style scoped>
.code-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.code-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: var(--bg-elevated);
  border-bottom: 1px solid var(--border-default);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-icon {
  font-size: 14px;
  color: var(--primary);
}

.header-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-primary);
}

.copy-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  background: transparent;
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  font-size: 11px;
  cursor: pointer;
  transition: all 0.2s;
}

.copy-btn:hover {
  background: color-mix(in srgb, var(--primary) 10%, transparent);
  border-color: var(--primary);
  color: var(--primary);
}

.code-container {
  flex: 1;
  overflow-y: auto;
  background: #1e1e1e;
}

.code-content {
  margin: 0;
  padding: 20px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  line-height: 1.6;
  color: #d4d4d4;
  white-space: pre;
}

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
