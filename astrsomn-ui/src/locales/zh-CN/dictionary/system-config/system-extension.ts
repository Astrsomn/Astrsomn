/**
 * 扩展类型枚举 - 与后端 SystemExtensionEnum.ExtensionTypeEnum 对齐（中文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const extensionTypeLabels = {
    MCP: 'MCP',
    MODEL_PROVIDER: '模型',
    VECTOR_STORE: '向量库'
} as const

export const systemExtensionTypeDictionary = createEnumDictionary({
    id: 'system.extension.type',
    labels: extensionTypeLabels,
    order: ['MCP', 'MODEL_PROVIDER', 'VECTOR_STORE']
})

const extensionInstallStatusLabels = {
    INSTALLED: '已安装',
    APPLIED: '已应用',
    UNINSTALLED: '未安装'
} as const

export const systemExtensionInstallStatusDictionary = createEnumDictionary({
    id: 'system.extension.installStatus',
    labels: extensionInstallStatusLabels,
    order: ['INSTALLED', 'APPLIED', 'UNINSTALLED']
})

const extensionApplyStatusLabels = {
    Y: '是',
    N: '否'
} as const

export const systemExtensionApplyStatusDictionary = createEnumDictionary({
    id: 'system.extension.applyStatus',
    labels: extensionApplyStatusLabels,
    order: ['Y', 'N']
})

const extensionDiscoveryMechanismLabels = {
    SPI: 'SPI发现',
    SPRING_BEAN: 'Spring Bean发现'
} as const

export const systemExtensionDiscoveryMechanismDictionary = createEnumDictionary({
    id: 'system.extension.discoveryMechanism',
    labels: extensionDiscoveryMechanismLabels,
    order: ['SPI', 'SPRING_BEAN']
})

const extensionInstallSourceLabels = {
    CLASSPATH_DEPENDENCY: 'Classpath依赖',
    PLUGIN_JAR_UPLOAD: '上传插件包',
    PLUGIN_JAR_DISCOVERED: '插件目录自动发现'
} as const

export const systemExtensionInstallSourceDictionary = createEnumDictionary({
    id: 'system.extension.installSource',
    labels: extensionInstallSourceLabels,
    order: ['CLASSPATH_DEPENDENCY', 'PLUGIN_JAR_UPLOAD', 'PLUGIN_JAR_DISCOVERED']
})

const modelTypeLabels = {
    chat: '对话',
    completion: '补全',
    embedding: '嵌入',
    image: '图像',
    audio: '音频',
    rerank: '重排',
    'text-to-image': '文生图',
    'text-to-video': '文生视频',
    'speech-to-text': '语音转文字',
    'text-to-speech': '文字转语音',
} as const

export const systemExtensionModelTypeDictionary = createEnumDictionary({
    id: 'system.extension.modelType',
    labels: modelTypeLabels,
    order: ['chat', 'completion', 'embedding', 'image', 'audio', 'rerank', 'text-to-image', 'text-to-video', 'speech-to-text', 'text-to-speech']
})
