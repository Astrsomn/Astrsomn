<template>
  <AstrsomnModal
    :open="open"
    width="80vw"
    max-width="80vw"
    body-height="80vh"
    :closable="true"
    main-padding="0"
    wrap-class-name="prompt-form-fsm-wrap"
    @update:open="onFsmOpenUpdate"
    @cancel="onCancel"
  >
    <template #header-logo>
      <component :is="mode === 'create' ? PlusCircleOutlined : FormOutlined" />
    </template>
    <template #header-title>
      {{ mode === 'create' ? '新增提示词' : '编辑提示词' }}
    </template>
    <template #header-subtitle>
      维护展示信息、场景分类与提示词正文
    </template>
    <template #header-actions>
      <a-space>
        <a-button @click="onCancel">取消</a-button>
        <a-button type="primary" :loading="confirmLoading" @click="handleOk">
          确定
        </a-button>
      </a-space>
    </template>

    <div class="prompt-form-body">
      <a-form ref="formRef" :model="form" :rules="rules" layout="vertical">
        <div class="editor-layout">
          <PromptConfigLeft :form="form" :scene-options="sceneOptions" />
          <PromptContentRight :form="form" />
        </div>
      </a-form>
    </div>
  </AstrsomnModal>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from "vue";
import type { FormInstance } from "ant-design-vue";
import { FormOutlined, PlusCircleOutlined } from "@ant-design/icons-vue";
import AstrsomnModal from "@/components/home/AstrsomnModal.vue";
import PromptConfigLeft from "./component/PromptConfigLeft.vue";
import PromptContentRight from "./component/PromptContentRight.vue";
import { aiPromptApi } from "@/api/aiPrompt";

const props = defineProps<{
  mode: "create" | "edit";
  confirmLoading: boolean;
  initial: any | null;
}>();

const emit = defineEmits(["submit"]);
const open = defineModel<boolean>("open", { required: true });
const formRef = ref<FormInstance | null>(null);

const emptyForm = () => ({
  promptKey: "",
  promptTitle: "",
  promptContent: "",
  scene: [] as string[],
  status: "enabled",
  version: 1,
});

const form = reactive(emptyForm());
const sceneTagLibrary = ref<string[]>([]);
const sceneOptions = computed(() =>
  sceneTagLibrary.value.map((tag) => ({ label: tag, value: tag })),
);

const rules = {
  promptTitle: [{ required: true, message: "请输入标题" }],
  promptContent: [{ required: true, message: "请输入提示词内容" }],
  status: [{ required: true, message: "请选择启用状态" }],
};

watch(
  [() => open.value, () => props.initial],
  ([isOpen, initial]) => {
    if (!isOpen) return;
    void loadSceneTagLibrary();
    if (initial && Object.keys(initial).length > 0) {
      Object.assign(form, emptyForm(), initial);
      form.scene = parseSceneTags(form.scene);
    } else {
      Object.assign(form, emptyForm());
    }
  },
  { immediate: true },
);

async function handleOk() {
  try {
    await formRef.value?.validate();
    const payload: Record<string, any> = { ...form };
    payload.scene = JSON.stringify(normalizeSceneTags(payload.scene));
    if (!payload.promptKey?.trim()) {
      delete payload.promptKey;
    } else {
      payload.promptKey = payload.promptKey.trim();
    }
    emit("submit", payload);
  } catch (err) {}
}

function onCancel() {
  open.value = false;
}

function onFsmOpenUpdate(v: boolean) {
  open.value = v;
}

async function loadSceneTagLibrary() {
  try {
    const tags = await aiPromptApi.querySceneTags();
    sceneTagLibrary.value = normalizeSceneTags(tags);
  } catch (err) {
    sceneTagLibrary.value = [];
  }
}

function normalizeSceneTags(raw: unknown): string[] {
  const arr = Array.isArray(raw) ? raw : [];
  return Array.from(
    new Set(
      arr
        .map((item) => String(item ?? "").trim())
        .filter(Boolean),
    ),
  );
}

function parseSceneTags(raw: unknown): string[] {
  if (Array.isArray(raw)) {
    return normalizeSceneTags(raw);
  }
  if (typeof raw === "string" && raw.trim()) {
    try {
      const parsed = JSON.parse(raw);
      if (Array.isArray(parsed)) {
        return normalizeSceneTags(parsed);
      }
    } catch (err) {}
  }
  return [];
}
</script>

<style scoped>
.prompt-form-body {
  height: 100%;
  min-height: 0;
  overflow: auto;
  padding: 20px 24px;
  box-sizing: border-box;
}

.editor-layout {
  display: flex;
  gap: 28px;
  align-items: flex-start;
  height: 100%;
  min-height: 0;
}

:deep(.ant-form-item) {
  margin-bottom: 16px;
}

@media (max-width: 800px) {
  .editor-layout {
    flex-direction: column;
  }
}

:global(.prompt-form-fsm-wrap.ant-modal-wrap) {
  display: flex;
  align-items: center;
  justify-content: center;
}

:global(.prompt-form-fsm-wrap .ant-modal) {
  top: 0;
  padding-bottom: 0;
}
</style>
