import type {Component} from 'vue'
import type {WorkflowNodeType} from '../../domain/types.ts'

import StartCanvasNode from './control/start/CanvasNode.vue'
import EndCanvasNode from './control/end/CanvasNode.vue'
import ParallelCanvasNode from './control/parallel/CanvasNode.vue'
import LlmCanvasNode from './ai/llm/CanvasNode.vue'
import RetrievalCanvasNode from './ai/retrieval/CanvasNode.vue'
import IfElseCanvasNode from './logic/if-else/CanvasNode.vue'
import IntentClassifierCanvasNode from './logic/intent-classifier/CanvasNode.vue'
import MergeCanvasNode from './logic/merge/CanvasNode.vue'
import HttpCanvasNode from './tool/http/CanvasNode.vue'
import CodeCanvasNode from './tool/code/CanvasNode.vue'
import ToolsCanvasNode from './tool/tools/CanvasNode.vue'

import StartInspectorPanel from './control/start/InspectorPanel.vue'
import EndInspectorPanel from './control/end/InspectorPanel.vue'
import ParallelInspectorPanel from './control/parallel/InspectorPanel.vue'
import LlmInspectorPanel from './ai/llm/InspectorPanel.vue'
import RetrievalInspectorPanel from './ai/retrieval/InspectorPanel.vue'
import IfElseInspectorPanel from './logic/if-else/InspectorPanel.vue'
import IntentClassifierInspectorPanel from './logic/intent-classifier/InspectorPanel.vue'
import MergeInspectorPanel from './logic/merge/InspectorPanel.vue'
import HttpInspectorPanel from './tool/http/InspectorPanel.vue'
import CodeInspectorPanel from './tool/code/InspectorPanel.vue'
import ToolsInspectorPanel from './tool/tools/InspectorPanel.vue'

export const nodeCanvasTypes: Record<WorkflowNodeType, Component> = {
    start: StartCanvasNode,
    end: EndCanvasNode,
    parallel: ParallelCanvasNode,
    llm: LlmCanvasNode,
    retrieval: RetrievalCanvasNode,
    'if-else': IfElseCanvasNode,
    'intent-classifier': IntentClassifierCanvasNode,
    merge: MergeCanvasNode,
    http: HttpCanvasNode,
    code: CodeCanvasNode,
    tools: ToolsCanvasNode
}

export const nodeInspectorMap: Record<WorkflowNodeType, Component> = {
    start: StartInspectorPanel,
    end: EndInspectorPanel,
    parallel: ParallelInspectorPanel,
    llm: LlmInspectorPanel,
    retrieval: RetrievalInspectorPanel,
    'if-else': IfElseInspectorPanel,
    'intent-classifier': IntentClassifierInspectorPanel,
    merge: MergeInspectorPanel,
    http: HttpInspectorPanel,
    code: CodeInspectorPanel,
    tools: ToolsInspectorPanel
}

