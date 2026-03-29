<template>
  <AdminPageShell
    :title="isEdit ? '编辑 AI 账号' : '新建 AI 账号'"
    description="配置账号键、名称与 API 凭证；Account Key 可留空，由后端按规则生成。"
    empty-text=""
  >
    <div class="form-page">
      <div class="form-toolbar">
        <a-button class="ghost-btn" @click="goBack">
          <template #icon><arrow-left-outlined /></template>
          返回列表
        </a-button>
      </div>

      <a-spin :spinning="loading">
        <a-form class="account-form" layout="vertical" :model="form" @finish="onSubmit">
          <a-row :gutter="16">
            <a-col :xs="24" :md="12">
              <a-form-item label="Account Key" name="accountKey">
                <a-tooltip
                  v-if="accountKeyImmutable"
                  title="已有 AI 模型在同环境下引用该 Key，不可修改"
                >
                  <a-input
                    v-model:value="form.accountKey"
                    placeholder="可选，留空则自动生成"
                    disabled
                  />
                </a-tooltip>
                <a-input
                  v-else
                  v-model:value="form.accountKey"
                  placeholder="可选，留空则自动生成"
                  allow-clear
                />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="12">
              <a-form-item
                label="账号名称"
                name="accountName"
                :rules="[{ required: true, message: '请输入账号名称' }]"
              >
                <a-input v-model:value="form.accountName" placeholder="展示名称" allow-clear />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="12">
              <a-form-item label="API Key" name="apiKey">
                <a-input-password v-model:value="form.apiKey" placeholder="供应商 API Key" allow-clear />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="12">
              <a-form-item label="API Secret" name="apiSecret">
                <a-input-password v-model:value="form.apiSecret" placeholder="可选" allow-clear />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="12">
              <a-form-item label="账号额度（tokens）" name="accountTokens">
                <a-input-number
                  v-model:value="form.accountTokens"
                  class="w-full"
                  :min="0"
                  placeholder="可选"
                />
              </a-form-item>
            </a-col>
          </a-row>

          <div class="form-actions">
            <a-button class="ghost-btn" @click="goBack">取消</a-button>
            <a-button type="primary" class="primary-btn" html-type="submit" :loading="submitting">
              {{ isEdit ? '保存' : '创建' }}
            </a-button>
          </div>
        </a-form>
      </a-spin>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { ArrowLeftOutlined } from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import { aiAccountApi, type AiAccount } from '@/api/aiAccount'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const accountKeyImmutable = ref(false)

const isEdit = computed(() => route.name === 'AdminAiAccountEdit')

const form = reactive<AiAccount>({})

const goBack = () => {
  void router.push({ name: 'AdminAiAccount' })
}

const loadDetail = async (id: string) => {
  loading.value = true
  try {
    const detail = await aiAccountApi.detail(id)
    Object.keys(form).forEach((k) => delete (form as Record<string, unknown>)[k])
    Object.assign(form, detail)
    accountKeyImmutable.value = detail.accountKeyImmutable === true
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '加载失败')
    void goBack()
  } finally {
    loading.value = false
  }
}

const onSubmit = async () => {
  submitting.value = true
  try {
    const payload: AiAccount = { ...form }
    let msg: string
    if (isEdit.value) {
      msg = await aiAccountApi.update(payload)
    } else {
      delete (payload as { id?: unknown }).id
      msg = await aiAccountApi.create(payload)
    }
    message.success(msg)
    void goBack()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '保存失败')
  } finally {
    submitting.value = false
  }
}

watch(
  [() => route.name, () => route.params.id],
  ([name, id]) => {
    if (name === 'AdminAiAccountNew') {
      Object.keys(form).forEach((k) => delete (form as Record<string, unknown>)[k])
      accountKeyImmutable.value = false
      return
    }
    if (name === 'AdminAiAccountEdit' && id != null && String(id)) {
      void loadDetail(String(id))
    }
  },
  { immediate: true }
)
</script>

<style scoped>
.form-page {
  padding: 0 4px;
}

.form-toolbar {
  margin-bottom: 16px;
}

.account-form {
  max-width: 720px;
}

.w-full {
  width: 100%;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
  padding-top: 16px;
  border-top: 1px solid var(--border-subtle);
}

.primary-btn,
.ghost-btn {
  height: 40px;
  border-radius: 12px;
  min-width: 104px;
}
</style>
