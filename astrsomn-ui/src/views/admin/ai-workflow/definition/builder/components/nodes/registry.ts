import type { Component } from 'vue'
import type { WorkflowNodeType } from '../../types'

import StartCanvasNode from './control/start/CanvasNode.vue'
import EndCanvasNode from './control/end/CanvasNode.vue'
import ParallelCanvasNode from './control/parallel/CanvasNode.vue'
import LlmCanvasNode from './ai/llm/CanvasNode.vue'
import KnowledgeCanvasNode from './ai/knowledge/CanvasNode.vue'
import VisionCanvasNode from './ai/vision/CanvasNode.vue'
import ConditionCanvasNode from './logic/condition/CanvasNode.vue'
import IteratorCanvasNode from './logic/iterator/CanvasNode.vue'
import TemplateCanvasNode from './logic/template/CanvasNode.vue'
import HttpCanvasNode from './tool/http/CanvasNode.vue'
import CodeCanvasNode from './tool/code/CanvasNode.vue'
import SearchCanvasNode from './tool/search/CanvasNode.vue'
import HumanAuditCanvasNode from './interaction/human-audit/CanvasNode.vue'
import InputFormCanvasNode from './interaction/input-form/CanvasNode.vue'

import StartInspectorPanel from './control/start/InspectorPanel.vue'
import EndInspectorPanel from './control/end/InspectorPanel.vue'
import ParallelInspectorPanel from './control/parallel/InspectorPanel.vue'
import LlmInspectorPanel from './ai/llm/InspectorPanel.vue'
import KnowledgeInspectorPanel from './ai/knowledge/InspectorPanel.vue'
import VisionInspectorPanel from './ai/vision/InspectorPanel.vue'
import ConditionInspectorPanel from './logic/condition/InspectorPanel.vue'
import IteratorInspectorPanel from './logic/iterator/InspectorPanel.vue'
import TemplateInspectorPanel from './logic/template/InspectorPanel.vue'
import HttpInspectorPanel from './tool/http/InspectorPanel.vue'
import CodeInspectorPanel from './tool/code/InspectorPanel.vue'
import SearchInspectorPanel from './tool/search/InspectorPanel.vue'
import HumanAuditInspectorPanel from './interaction/human-audit/InspectorPanel.vue'
import InputFormInspectorPanel from './interaction/input-form/InspectorPanel.vue'

export const nodeCanvasTypes: Record<WorkflowNodeType, Component> = {
  start: StartCanvasNode,
  end: EndCanvasNode,
  parallel: ParallelCanvasNode,
  llm: LlmCanvasNode,
  knowledge: KnowledgeCanvasNode,
  vision: VisionCanvasNode,
  condition: ConditionCanvasNode,
  iterator: IteratorCanvasNode,
  template: TemplateCanvasNode,
  http: HttpCanvasNode,
  code: CodeCanvasNode,
  search: SearchCanvasNode,
  'human-audit': HumanAuditCanvasNode,
  'input-form': InputFormCanvasNode
}

export const nodeInspectorMap: Record<WorkflowNodeType, Component> = {
  start: StartInspectorPanel,
  end: EndInspectorPanel,
  parallel: ParallelInspectorPanel,
  llm: LlmInspectorPanel,
  knowledge: KnowledgeInspectorPanel,
  vision: VisionInspectorPanel,
  condition: ConditionInspectorPanel,
  iterator: IteratorInspectorPanel,
  template: TemplateInspectorPanel,
  http: HttpInspectorPanel,
  code: CodeInspectorPanel,
  search: SearchInspectorPanel,
  'human-audit': HumanAuditInspectorPanel,
  'input-form': InputFormInspectorPanel
}

