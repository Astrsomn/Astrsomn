import type { WorkflowGraph } from './types'

export const parseGraphJson = (graphJson?: string): WorkflowGraph | undefined => {
  if (!graphJson) return undefined
  try {
    return JSON.parse(graphJson) as WorkflowGraph
  } catch {
    return undefined
  }
}

export const stringifyGraphJson = (
  payload: {
    nodes: WorkflowGraph['nodes']
    edges: WorkflowGraph['edges']
    viewport?: WorkflowGraph['viewport']
    meta?: WorkflowGraph['meta']
  },
  space = 2
) => {
  return JSON.stringify(
    {
      nodes: payload.nodes,
      edges: payload.edges,
      viewport: payload.viewport,
      meta: payload.meta ?? {}
    },
    null,
    space
  )
}

