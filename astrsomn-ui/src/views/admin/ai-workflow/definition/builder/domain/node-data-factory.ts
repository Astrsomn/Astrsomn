import {nodeDefinitionMap} from './node-definitions'
import type {WorkflowNodeType} from './types'

export function createNodeData(type: WorkflowNodeType) {
    const definition = nodeDefinitionMap[type]
    const config: Record<string, unknown> = {}
    definition.configSchema.forEach((field) => {
        if (field.defaultValue !== undefined) config[field.key] = field.defaultValue
    })
    return {
        label: definition.title,
        description: definition.description,
        category: definition.category,
        nodeTypeTitle: definition.title,
        config,
        inputs: definition.inputs,
        outputs: definition.outputs
    }
}

export const getNodeDefinition = (type?: string) => {
    if (!type) return undefined
    return nodeDefinitionMap[type as WorkflowNodeType]
}

