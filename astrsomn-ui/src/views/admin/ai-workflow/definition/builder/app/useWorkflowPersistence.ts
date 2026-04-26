import { computed } from 'vue'
import { aiWorkflowApi } from '@/api/aiWorkflow'
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
      const resp = await aiWorkflowApi.queryPage({ pageNo: 1, pageSize: 20, param: {} })
      const list = (resp.list || []).map((item) => ({
        id: String(item.id || item.workflowKey || item.workflowName || Math.random()),
        workflowName: item.workflowName || '未命名流程',
        workflowKey: item.workflowKey || '',
        description: item.description || ''
      }))
      deps.workflowItems.value = list
      if (!deps.activeWorkflowId.value && list.length > 0) {
        deps.activeWorkflowId.value = list[0].id
      }
    } catch {
      deps.workflowItems.value = []
    }
  }

  const handleSaveDraft = async () => {
    if (!deps.workflowMeta.workflowName || !deps.workflowMeta.workflowKey) {
      deps.notifyWarning('请先填写流程名称和 Flow Key')
      return
    }
    const graphValidation = deps.validateGraph()
    if (!graphValidation.ok) {
      deps.notifyWarning(graphValidation.errors[0] || '流程校验未通过')
      return
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
    } catch {
      deps.notifyWarning('当前阶段以页面搭建为主，保存接口可后续联调')
    } finally {
      deps.saving.value = false
    }
  }

  const fetchDetailIfNeeded = async () => {
    const routeId = deps.route.params.id ?? deps.route.query.id
    const cloneId = deps.route.query.cloneId
    const targetId = routeId ?? cloneId
    if (!targetId) return

    const detail = await aiWorkflowApi.detail(String(targetId))
    deps.workflowMeta.id = detail.id
    deps.workflowMeta.workflowName = detail.workflowName || deps.workflowMeta.workflowName
    deps.workflowMeta.workflowKey = detail.workflowKey || ''
    deps.workflowMeta.description = detail.description || ''
    deps.loadGraph(detail.graphJson)

    if (!routeId && cloneId) {
      deps.workflowMeta.id = undefined
      deps.workflowMeta.workflowName = `${deps.workflowMeta.workflowName}-副本`
      deps.workflowMeta.workflowKey = ''
    }
  }

  return {
    submitPayload,
    fetchWorkflowList,
    handleSaveDraft,
    fetchDetailIfNeeded
  }
}

