import {reactive} from 'vue'
import type {ContextMenuAction, ContextMenuPosition} from '../components/context-menu/types'
import type {CanvasContextMenuPayload, WorkflowEdge} from '../domain/types'

type GraphActions = {
    removeNodeById: (nodeId: string) => void
    duplicateNodeById: (nodeId: string) => void
    toggleNodeDisabled: (nodeId: string) => void
    batchRemoveNodes: (nodeIds: string[]) => void
    batchDisableNodes: (nodeIds: string[]) => void
    removeEdgeById: (edgeId: string) => void
    setEdgeLabelById: (edgeId: string, label: string) => void
    updateEdgeStyleById: (edgeId: string, edgeType: 'default' | 'straight' | 'step') => void
}

export function useContextMenuActions(deps: {
    selectedNodeIds: { value: string[] }
    edges: { value: WorkflowEdge[] }
    graphActions: GraphActions
    notifyInfo: (message: string) => void
}) {
    const contextMenu = reactive<{
        visible: boolean
        position: ContextMenuPosition
        items: ContextMenuAction[]
        source: 'node' | 'edge' | 'pane' | undefined
        nodeId?: string
        edgeId?: string
    }>({
        visible: false,
        position: {x: 0, y: 0},
        items: [],
        source: undefined
    })

    const closeContextMenu = () => {
        contextMenu.visible = false
    }

    const openContextMenu = (payload: {
        source: 'node' | 'edge' | 'pane'
        position: ContextMenuPosition
        items: ContextMenuAction[]
        nodeId?: string
        edgeId?: string
    }) => {
        contextMenu.visible = true
        contextMenu.source = payload.source
        contextMenu.position = payload.position
        contextMenu.items = payload.items
        contextMenu.nodeId = payload.nodeId
        contextMenu.edgeId = payload.edgeId
    }

    const onCanvasContextmenu = (payload: CanvasContextMenuPayload) => {
        if (payload.target === 'node' && payload.nodeId) {
            const multi = deps.selectedNodeIds.value.length > 1 && deps.selectedNodeIds.value.includes(payload.nodeId)
            openContextMenu({
                source: 'node',
                position: {x: payload.x, y: payload.y},
                nodeId: payload.nodeId,
                items: multi
                    ? [
                        {key: 'batch-delete', label: '批量删除节点', icon: '🗑', danger: true},
                        {key: 'batch-disable', label: '批量禁用节点', icon: '⛔'}
                    ]
                    : [
                        {key: 'node-delete', label: '删除节点', icon: '🗑', danger: true},
                        {key: 'node-copy', label: '复制节点', icon: '📄'},
                        {key: 'node-disable', label: '启用/禁用', icon: '⛔'},
                        {key: 'node-runfrom', label: '从此节点开始调试', icon: '▶'}
                    ]
            })
        } else if (payload.target === 'edge' && payload.edgeId) {
            openContextMenu({
                source: 'edge',
                position: {x: payload.x, y: payload.y},
                edgeId: payload.edgeId,
                items: [
                    {key: 'edge-delete', label: '删除连线', icon: '🗑', danger: true},
                    {key: 'edge-label', label: '编辑连线标签', icon: '🏷'}
                ]
            })
        }
    }

    const onContextMenuAction = (key: string) => {
        const {graphActions} = deps
        if (key === 'node-delete' && contextMenu.nodeId) {
            graphActions.removeNodeById(contextMenu.nodeId)
        } else if (key === 'node-copy' && contextMenu.nodeId) {
            graphActions.duplicateNodeById(contextMenu.nodeId)
        } else if (key === 'node-disable' && contextMenu.nodeId) {
            graphActions.toggleNodeDisabled(contextMenu.nodeId)
        } else if (key === 'node-runfrom' && contextMenu.nodeId) {
            deps.notifyInfo(`已设置从节点 ${contextMenu.nodeId} 开始调试（占位）`)
        } else if (key === 'batch-delete') {
            graphActions.batchRemoveNodes(deps.selectedNodeIds.value)
        } else if (key === 'batch-disable') {
            graphActions.batchDisableNodes(deps.selectedNodeIds.value)
        } else if (key === 'edge-delete' && contextMenu.edgeId) {
            graphActions.removeEdgeById(contextMenu.edgeId)
        } else if (key === 'edge-label' && contextMenu.edgeId) {
            const current = deps.edges.value.find((edge) => edge.id === contextMenu.edgeId)?.label
            const next = window.prompt('请输入连线标签', String(current || ''))
            if (next != null) graphActions.setEdgeLabelById(contextMenu.edgeId, next)
        } else if (key === 'edge-style-straight' && contextMenu.edgeId) {
            graphActions.updateEdgeStyleById(contextMenu.edgeId, 'straight')
        } else if (key === 'edge-style-smooth' && contextMenu.edgeId) {
            graphActions.updateEdgeStyleById(contextMenu.edgeId, 'default')
        }
        closeContextMenu()
    }

    return {
        contextMenu,
        closeContextMenu,
        onCanvasContextmenu,
        onContextMenuAction
    }
}

