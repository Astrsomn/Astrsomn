/**
 * 扩展类型枚举 - 与后端 SystemExtensionEnum.ExtensionTypeEnum 对齐（英文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const extensionTypeLabels = {
    MCP: 'MCP',
    MODEL_PROVIDER: 'Model',
    VECTOR_STORE: 'Vector Store'
} as const

export const systemExtensionTypeDictionary = createEnumDictionary({
    id: 'system.extension.type',
    labels: extensionTypeLabels,
    order: ['MCP', 'MODEL_PROVIDER', 'VECTOR_STORE']
})

const extensionInstallStatusLabels = {
    INSTALLED: 'Installed',
    APPLIED: 'Applied',
    UNINSTALLED: 'Uninstalled'
} as const

export const systemExtensionInstallStatusDictionary = createEnumDictionary({
    id: 'system.extension.installStatus',
    labels: extensionInstallStatusLabels,
    order: ['INSTALLED', 'APPLIED', 'UNINSTALLED']
})

const extensionApplyStatusLabels = {
    Y: 'Yes',
    N: 'No'
} as const

export const systemExtensionApplyStatusDictionary = createEnumDictionary({
    id: 'system.extension.applyStatus',
    labels: extensionApplyStatusLabels,
    order: ['Y', 'N']
})

const extensionDiscoveryMechanismLabels = {
    SPI: 'SPI Discovery',
    SPRING_BEAN: 'Spring Bean Discovery'
} as const

export const systemExtensionDiscoveryMechanismDictionary = createEnumDictionary({
    id: 'system.extension.discoveryMechanism',
    labels: extensionDiscoveryMechanismLabels,
    order: ['SPI', 'SPRING_BEAN']
})

const extensionInstallSourceLabels = {
    CLASSPATH_DEPENDENCY: 'Classpath Dependency',
    PLUGIN_JAR_UPLOAD: 'Plugin JAR Upload',
    PLUGIN_JAR_DISCOVERED: 'Plugin Directory Auto-Discovery'
} as const

export const systemExtensionInstallSourceDictionary = createEnumDictionary({
    id: 'system.extension.installSource',
    labels: extensionInstallSourceLabels,
    order: ['CLASSPATH_DEPENDENCY', 'PLUGIN_JAR_UPLOAD', 'PLUGIN_JAR_DISCOVERED']
})
