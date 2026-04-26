import { computed } from 'vue'
import { aiWorkflowApi } from '@/api/aiWorkflow'
import { ensureWorkspaceEnvInStorage } from '@/utils/ensureWorkspaceEnvStorage'
import type { WorkflowListItem, WorkflowMeta } from '../domain/types'

type UseWorkflowPersistenceDeps = {
  workflowMeta: WorkflowMeta
  activeWorkflowId: { value?: string }
  workflowItems: { value: WorkflowListItem[] }
  toGraphJson: (meta?: Record<string, unknown>) => string
  validateGraph: () => { ok: boolean; errors: string[] }
  loadGraph: (graphJson?: string) => void
  saving: { value: boolean }
  route: { params: Record<string, unknown>; query: Record<string, unknown> }
  notifySuccess: (msg: string) => void
  notifyWarning: (msg: string) => void
}

export function useWorkflowPersistence(deps: UseWorkflowPersistenceDeps) {
  let envReady = false

  const ensureEnvReady = async () => {
    if (envReady) return
    await ensureWorkspaceEnvInStorage()
    envReady = true
  }

  const submitPayload = computed(() => ({
    id: deps.workflowMeta.id,
    workflowName: deps.workflowMeta.workflowName,
    workflowKey: deps.workflowMeta.workflowKey,
    description: deps.workflowMeta.description,
    graphJson: deps.toGraphJson({
      id: deps.workflowMeta.id,
      workflowName: deps.workflowMeta.workflowName,
      workflowKey: deps.workflowMeta.workflowKey,
      description: deps.workflowMeta.description
    })
  }))

  const fetchWorkflowList = async () => {
    try {
      await ensureEnvReady()
      const resp = await aiWorkflowApi.queryPage({ pageNo: 1, pageSize: 100, param: {} })
      const list = (resp.list || []).map((item) => ({
        id: String(item.id || item.workflowKey || item.workflowName || Math.random()),
        recordId: item.id,
        workflowName: item.workflowName || '未命名流程',
        workflowKey: item.workflowKey || '',
        description: item.description || '',
        category: item.category || item.description || '未分类'
      }))
      deps.workflowItems.value = list
    } catch {
      deps.workflowItems.value = []
    }
  }

  const applyWorkflowDetail = (detail: Awaited<ReturnType<typeof aiWorkflowApi.detail>>) => {
    deps.workflowMeta.id = detail.id
    deps.workflowMeta.workflowName = detail.workflowName || '未命名流程'
    deps.workflowMeta.workflowKey = detail.workflowKey || ''
    deps.workflowMeta.description = detail.description || ''
    deps.loadGraph(detail.graphJson)
  }

  const fetchWorkflowDetail = async (id: string) => {
    await ensureEnvReady()
    const detail = await aiWorkflowApi.detail(id)
    applyWorkflowDetail(detail)
  }

  const createWorkflow = async (payload: { workflowName: string; workflowKey: string; category: string }) => {
    await ensureEnvReady()
    const createResp = await aiWorkflowApi.create({
      workflowName: payload.workflowName,
      workflowKey: payload.workflowKey,
      description: payload.category,
      category: payload.category,
      graphJson: deps.toGraphJson({
        workflowName: payload.workflowName,
        workflowKey: payload.workflowKey,
        description: payload.category
      })
    })
    await fetchWorkflowList()
    const created = deps.workflowItems.value.find(
      (item) => item.workflowKey === payload.workflowKey || item.workflowName === payload.workflowName
    )
    if (!created) {
      deps.activeWorkflowId.value = undefined
      deps.notifyWarning(typeof createResp === 'string' ? '流程已创建，请在左侧列表中选择后编辑' : '流程已创建，请在左侧列表中选择后编辑')
      return
    }
    deps.activeWorkflowId.value = created.id
    await fetchWorkflowDetail(created.id)
  }

  const handleSaveDraft = async () => {
    await ensureEnvReady()
    if (!deps.activeWorkflowId.value) {
      deps.notifyWarning('请先新建或选择流程')
      return false
    }
    if (!deps.workflowMeta.workflowName || !deps.workflowMeta.workflowKey) {
      deps.notifyWarning('请先填写流程名称和 Flow Key')
      return false
    }
    const graphValidation = deps.validateGraph()
    if (!graphValidation.ok) {
      deps.notifyWarning(graphValidation.errors[0] || '流程校验未通过')
      return false
    }

    deps.saving.value = true
    try {
      let tip = ''
      if (deps.workflowMeta.id == null) {
        tip = await aiWorkflowApi.create({ ...submitPayload.value, id: undefined })
        deps.notifySuccess(tip || '草稿已保存')
      } else {
        tip = await aiWorkflowApi.update(submitPayload.value)
        deps.notifySuccess(tip || '草稿已更新')
      }
      return true
    } catch {
      deps.notifyWarning('当前阶段以页面搭建为主，保存接口可后续联调')
      return false
    } finally {
      deps.saving.value = false
    }
  }

  const updateWorkflowMeta = async (
    item: WorkflowListItem,
    payload: { workflowName: string; workflowKey: string; category: string }
  ) => {
    await ensureEnvReady()
    if (item.recordId == null) {
      deps.notifyWarning('当前流程缺少ID，无法编辑')
      return
    }
    const detail = await aiWorkflowApi.detail(item.recordId)
    await aiWorkflowApi.update({
      id: item.recordId,
      workflowName: payload.workflowName,
      workflowKey: payload.workflowKey,
      description: payload.category,
      category: payload.category,
      graphJson: detail.graphJson || ''
    })
    await fetchWorkflowList()
    const updated = deps.workflowItems.value.find((x) => x.recordId === item.recordId)
    if (!updated) return
    deps.activeWorkflowId.value = updated.id
    await fetchWorkflowDetail(updated.id)
  }

  const deleteWorkflow = async (item: WorkflowListItem) => {
    await ensureEnvReady()
    if (item.recordId == null) {
      deps.notifyWarning('当前流程缺少ID，无法删除')
      return
    }
    await aiWorkflowApi.delete([item.recordId])
    const deletingActive = deps.activeWorkflowId.value === item.id
    await fetchWorkflowList()
    if (!deletingActive) return
    const first = deps.workflowItems.value[0]
    if (!first) {
      deps.activeWorkflowId.value = undefined
      deps.workflowMeta.id = undefined
      deps.workflowMeta.workflowName = '未命名流程'
      deps.workflowMeta.workflowKey = ''
      deps.workflowMeta.description = ''
      deps.loadGraph(undefined)
      return
    }
    deps.activeWorkflowId.value = first.id
    await fetchWorkflowDetail(first.id)
  }

  const fetchDetailIfNeeded = async () => {
    await ensureEnvReady()
    const routeId = deps.route.params.id ?? deps.route.query.id
    const cloneId = deps.route.query.cloneId
    const targetId = routeId ?? cloneId
    if (!targetId) return

    const detail = await aiWorkflowApi.detail(String(targetId))
    applyWorkflowDetail(detail)

    if (!routeId && cloneId) {
      deps.workflowMeta.id = undefined
      deps.workflowMeta.workflowName = `${deps.workflowMeta.workflowName}-副本`
      deps.workflowMeta.workflowKey = ''
    }
  }

  return {
    submitPayload,
    fetchWorkflowList,
    fetchWorkflowDetail,
    createWorkflow,
    updateWorkflowMeta,
    deleteWorkflow,
    handleSaveDraft,
    fetchDetailIfNeeded
  }
}

