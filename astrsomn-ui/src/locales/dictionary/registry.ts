import {computed, type ComputedRef, ref, type Ref} from 'vue'
import type {EnumDictionary} from './core'
import {
    aiModelCapabilitiesDictionary as aiModelCapabilitiesEn,
    aiModelProviderDictionary as aiModelProviderEn,
    aiModelSourceTypeDictionary as aiModelSourceTypeEn,
    aiModelStatusDictionary as aiModelStatusEn
} from '../en-US/dictionary/ai-config/ai-model.ts'
import {
    aiModelCapabilitiesDictionary as aiModelCapabilitiesZh,
    aiModelProviderDictionary as aiModelProviderZh,
    aiModelSourceTypeDictionary as aiModelSourceTypeZh,
    aiModelStatusDictionary as aiModelStatusZh
} from '../zh-CN/dictionary/ai-config/ai-model.ts'
import {aiAccountStatusDictionary as aiAccountStatusEn} from '../en-US/dictionary/ai-config/ai-account.ts'
import {aiAccountStatusDictionary as aiAccountStatusZh} from '../zh-CN/dictionary/ai-config/ai-account.ts'
import {aiInstanceStatusDictionary as aiInstanceStatusEn} from '../en-US/dictionary/ai-config/ai-instance.ts'
import {aiInstanceStatusDictionary as aiInstanceStatusZh} from '../zh-CN/dictionary/ai-config/ai-instance.ts'
import {
    aiAgentIsDefaultDictionary as aiAgentIsDefaultEn,
    aiAgentMemoryModeDictionary as aiAgentMemoryModeEn,
    aiAgentStatusDictionary as aiAgentStatusEn
} from '../en-US/dictionary/ai-config/ai-agent.ts'
import {
    aiAgentIsDefaultDictionary as aiAgentIsDefaultZh,
    aiAgentMemoryModeDictionary as aiAgentMemoryModeZh,
    aiAgentStatusDictionary as aiAgentStatusZh
} from '../zh-CN/dictionary/ai-config/ai-agent.ts'
import {
    aiVecDriverParamDictionary as aiVecDriverParamEn,
    aiVecDriverProviderDictionary as aiVecDriverProviderEn,
    aiVecDriverStatusDictionary as aiVecDriverStatusEn
} from '../en-US/dictionary/ai-vector/vec-driver.ts'
import {
    aiVecDriverParamDictionary as aiVecDriverParamZh,
    aiVecDriverProviderDictionary as aiVecDriverProviderZh,
    aiVecDriverStatusDictionary as aiVecDriverStatusZh
} from '../zh-CN/dictionary/ai-vector/vec-driver.ts'
import {
    aiVecChunkStrategyDictionary as aiVecChunkStrategyEn
} from '../en-US/dictionary/ai-vector/vec-chunk-strategy.ts'
import {
    aiVecChunkStrategyDictionary as aiVecChunkStrategyZh
} from '../zh-CN/dictionary/ai-vector/vec-chunk-strategy.ts'
import {
    aiVecDocSyncStatusDictionary as aiVecDocSyncStatusEn
} from '../en-US/dictionary/ai-vector/vec-doc.ts'
import {
    aiVecDocSyncStatusDictionary as aiVecDocSyncStatusZh
} from '../zh-CN/dictionary/ai-vector/vec-doc.ts'
import {
    aiVecStoreStatusDictionary as aiVecStoreStatusEn
} from '../en-US/dictionary/ai-vector/vec-store.ts'
import {
    aiVecStoreStatusDictionary as aiVecStoreStatusZh
} from '../zh-CN/dictionary/ai-vector/vec-store.ts'
import {
    aiVecSourceStatusDictionary as aiVecSourceStatusEn
} from '../en-US/dictionary/ai-vector/vec-source.ts'
import {
    aiVecSourceStatusDictionary as aiVecSourceStatusZh
} from '../zh-CN/dictionary/ai-vector/vec-source.ts'
import {
    aiWorkflowExecutionStatusDictionary as aiWorkflowExecutionStatusEn,
    aiWorkflowHumanTaskStatusDictionary as aiWorkflowHumanTaskStatusEn,
    aiWorkflowNodeTypeDictionary as aiWorkflowNodeTypeEn
} from '../en-US/dictionary/ai-workflow/workflow-node.ts'
import {
    aiWorkflowExecutionStatusDictionary as aiWorkflowExecutionStatusZh,
    aiWorkflowHumanTaskStatusDictionary as aiWorkflowHumanTaskStatusZh,
    aiWorkflowNodeTypeDictionary as aiWorkflowNodeTypeZh
} from '../zh-CN/dictionary/ai-workflow/workflow-node.ts'
import {
    aiWorkflowInstanceStateDictionary as aiWorkflowInstanceStateEn,
    aiWorkflowNodeStateDictionary as aiWorkflowNodeStateEn
} from '../en-US/dictionary/ai-workflow/workflow-instance.ts'
import {
    aiWorkflowInstanceStateDictionary as aiWorkflowInstanceStateZh,
    aiWorkflowNodeStateDictionary as aiWorkflowNodeStateZh
} from '../zh-CN/dictionary/ai-workflow/workflow-instance.ts'
import {
    systemExtensionApplyStatusDictionary as systemExtensionApplyStatusEn,
    systemExtensionDiscoveryMechanismDictionary as systemExtensionDiscoveryMechanismEn,
    systemExtensionInstallSourceDictionary as systemExtensionInstallSourceEn,
    systemExtensionInstallStatusDictionary as systemExtensionInstallStatusEn,
    systemExtensionTypeDictionary as systemExtensionTypeEn
} from '../en-US/dictionary/system-config/system-extension.ts'
import {
    systemExtensionApplyStatusDictionary as systemExtensionApplyStatusZh,
    systemExtensionDiscoveryMechanismDictionary as systemExtensionDiscoveryMechanismZh,
    systemExtensionInstallSourceDictionary as systemExtensionInstallSourceZh,
    systemExtensionInstallStatusDictionary as systemExtensionInstallStatusZh,
    systemExtensionTypeDictionary as systemExtensionTypeZh
} from '../zh-CN/dictionary/system-config/system-extension.ts'
import {
    systemMessageLevelDictionary as systemMessageLevelEn,
    systemMessageReadStatusDictionary as systemMessageReadStatusEn,
    systemMessageRefTypeDictionary as systemMessageRefTypeEn,
    systemMessageTypeDictionary as systemMessageTypeEn
} from '../en-US/dictionary/system-config/system-message.ts'
import {
    systemMessageLevelDictionary as systemMessageLevelZh,
    systemMessageReadStatusDictionary as systemMessageReadStatusZh,
    systemMessageRefTypeDictionary as systemMessageRefTypeZh,
    systemMessageTypeDictionary as systemMessageTypeZh
} from '../zh-CN/dictionary/system-config/system-message.ts'
import {
    systemUserAdminDictionary as systemUserAdminEn,
    systemUserRoleDictionary as systemUserRoleEn
} from '../en-US/dictionary/system-config/system-user.ts'
import {
    systemUserAdminDictionary as systemUserAdminZh,
    systemUserRoleDictionary as systemUserRoleZh
} from '../zh-CN/dictionary/system-config/system-user.ts'
import {
    astroChatEventTypeDictionary as astroChatEventTypeEn,
    astroChatRoleDictionary as astroChatRoleEn
} from '../en-US/dictionary/chat.ts'
import {
    astroChatEventTypeDictionary as astroChatEventTypeZh,
    astroChatRoleDictionary as astroChatRoleZh
} from '../zh-CN/dictionary/chat.ts'
import {
    commonBooleanDictionary as commonBooleanEn,
    commonStatusDictionary as commonStatusEn,
    commonYesNoDictionary as commonYesNoEn
} from '../en-US/dictionary/common.ts'
import {
    commonBooleanDictionary as commonBooleanZh,
    commonStatusDictionary as commonStatusZh,
    commonYesNoDictionary as commonYesNoZh
} from '../zh-CN/dictionary/common.ts'

/** 与 `bundles`、useLanguage 的 Lang 保持一致 */
export type DictionaryLocale = 'zh-CN' | 'en-US'

/** 各语言文案不同，统一用宽类型，避免中英字面量不兼容 */
export type DictionaryBundle = {
    'ai-model.provider': EnumDictionary<Record<string, string>>
    'ai-model.status': EnumDictionary<Record<string, string>>
    'ai-model.capabilities': EnumDictionary<Record<string, string>>
    'ai-model.sourceType': EnumDictionary<Record<string, string>>
    'ai-account.status': EnumDictionary<Record<string, string>>
    'ai-instance.status': EnumDictionary<Record<string, string>>
    'ai-agent.status': EnumDictionary<Record<string, string>>
    'ai-agent.memoryMode': EnumDictionary<Record<string, string>>
    'ai-agent.isDefault': EnumDictionary<Record<string, string>>
    'ai-vec.driver': EnumDictionary<Record<string, string>>
    'ai-vec.driver.status': EnumDictionary<Record<string, string>>
    'ai-vec.driver.param': EnumDictionary<Record<string, string>>
    'ai-vec.doc.syncStatus': EnumDictionary<Record<string, string>>
    'ai-vec.store.status': EnumDictionary<Record<string, string>>
    'ai-vec.source.status': EnumDictionary<Record<string, string>>
    'ai-vec.chunkStrategy': EnumDictionary<Record<string, string>>
    'ai-workflow.nodeType': EnumDictionary<Record<string, string>>
    'ai-workflow.executionStatus': EnumDictionary<Record<string, string>>
    'ai-workflow.humanTaskStatus': EnumDictionary<Record<string, string>>
    'ai-workflow.instance.state': EnumDictionary<Record<string, string>>
    'ai-workflow.node.state': EnumDictionary<Record<string, string>>
    'system.extension.type': EnumDictionary<Record<string, string>>
    'system.extension.installStatus': EnumDictionary<Record<string, string>>
    'system.extension.applyStatus': EnumDictionary<Record<string, string>>
    'system.extension.discoveryMechanism': EnumDictionary<Record<string, string>>
    'system.extension.installSource': EnumDictionary<Record<string, string>>
    'system.message.type': EnumDictionary<Record<string, string>>
    'system.message.level': EnumDictionary<Record<string, string>>
    'system.message.readStatus': EnumDictionary<Record<string, string>>
    'system.message.refType': EnumDictionary<Record<string, string>>
    'system.user.role': EnumDictionary<Record<string, string>>
    'system.user.admin': EnumDictionary<Record<string, string>>
    'astro.chat.role': EnumDictionary<Record<string, string>>
    'astro.chat.eventType': EnumDictionary<Record<string, string>>
    'common.status': EnumDictionary<Record<string, string>>
    'common.boolean': EnumDictionary<Record<string, string>>
    'common.yesNo': EnumDictionary<Record<string, string>>
}

export type DictionaryId = keyof DictionaryBundle

const zhCNDictionaryBundle = {
    'ai-model.provider': aiModelProviderZh,
    'ai-model.status': aiModelStatusZh,
    'ai-model.capabilities': aiModelCapabilitiesZh,
    'ai-model.sourceType': aiModelSourceTypeZh,
    'ai-account.status': aiAccountStatusZh,
    'ai-instance.status': aiInstanceStatusZh,
    'ai-agent.status': aiAgentStatusZh,
    'ai-agent.memoryMode': aiAgentMemoryModeZh,
    'ai-agent.isDefault': aiAgentIsDefaultZh,
    'ai-vec.driver': aiVecDriverProviderZh,
    'ai-vec.driver.status': aiVecDriverStatusZh,
    'ai-vec.driver.param': aiVecDriverParamZh,
    'ai-vec.doc.syncStatus': aiVecDocSyncStatusZh,
    'ai-vec.store.status': aiVecStoreStatusZh,
    'ai-vec.source.status': aiVecSourceStatusZh,
    'ai-vec.chunkStrategy': aiVecChunkStrategyZh,
    'ai-workflow.nodeType': aiWorkflowNodeTypeZh,
    'ai-workflow.executionStatus': aiWorkflowExecutionStatusZh,
    'ai-workflow.humanTaskStatus': aiWorkflowHumanTaskStatusZh,
    'ai-workflow.instance.state': aiWorkflowInstanceStateZh,
    'ai-workflow.node.state': aiWorkflowNodeStateZh,
    'system.extension.type': systemExtensionTypeZh,
    'system.extension.installStatus': systemExtensionInstallStatusZh,
    'system.extension.applyStatus': systemExtensionApplyStatusZh,
    'system.extension.discoveryMechanism': systemExtensionDiscoveryMechanismZh,
    'system.extension.installSource': systemExtensionInstallSourceZh,
    'system.message.type': systemMessageTypeZh,
    'system.message.level': systemMessageLevelZh,
    'system.message.readStatus': systemMessageReadStatusZh,
    'system.message.refType': systemMessageRefTypeZh,
    'system.user.role': systemUserRoleZh,
    'system.user.admin': systemUserAdminZh,
    'astro.chat.role': astroChatRoleZh,
    'astro.chat.eventType': astroChatEventTypeZh,
    'common.status': commonStatusZh,
    'common.boolean': commonBooleanZh,
    'common.yesNo': commonYesNoZh
} satisfies DictionaryBundle

const enUSDictionaryBundle = {
    'ai-model.provider': aiModelProviderEn,
    'ai-model.status': aiModelStatusEn,
    'ai-model.capabilities': aiModelCapabilitiesEn,
    'ai-model.sourceType': aiModelSourceTypeEn,
    'ai-account.status': aiAccountStatusEn,
    'ai-instance.status': aiInstanceStatusEn,
    'ai-agent.status': aiAgentStatusEn,
    'ai-agent.memoryMode': aiAgentMemoryModeEn,
    'ai-agent.isDefault': aiAgentIsDefaultEn,
    'ai-vec.driver': aiVecDriverProviderEn,
    'ai-vec.driver.status': aiVecDriverStatusEn,
    'ai-vec.driver.param': aiVecDriverParamEn,
    'ai-vec.doc.syncStatus': aiVecDocSyncStatusEn,
    'ai-vec.store.status': aiVecStoreStatusEn,
    'ai-vec.source.status': aiVecSourceStatusEn,
    'ai-vec.chunkStrategy': aiVecChunkStrategyEn,
    'ai-workflow.nodeType': aiWorkflowNodeTypeEn,
    'ai-workflow.executionStatus': aiWorkflowExecutionStatusEn,
    'ai-workflow.humanTaskStatus': aiWorkflowHumanTaskStatusEn,
    'ai-workflow.instance.state': aiWorkflowInstanceStateEn,
    'ai-workflow.node.state': aiWorkflowNodeStateEn,
    'system.extension.type': systemExtensionTypeEn,
    'system.extension.installStatus': systemExtensionInstallStatusEn,
    'system.extension.applyStatus': systemExtensionApplyStatusEn,
    'system.extension.discoveryMechanism': systemExtensionDiscoveryMechanismEn,
    'system.extension.installSource': systemExtensionInstallSourceEn,
    'system.message.type': systemMessageTypeEn,
    'system.message.level': systemMessageLevelEn,
    'system.message.readStatus': systemMessageReadStatusEn,
    'system.message.refType': systemMessageRefTypeEn,
    'system.user.role': systemUserRoleEn,
    'system.user.admin': systemUserAdminEn,
    'astro.chat.role': astroChatRoleEn,
    'astro.chat.eventType': astroChatEventTypeEn,
    'common.status': commonStatusEn,
    'common.boolean': commonBooleanEn,
    'common.yesNo': commonYesNoEn
} satisfies DictionaryBundle

const bundles: Record<DictionaryLocale, DictionaryBundle> = {
    'zh-CN': zhCNDictionaryBundle,
    'en-US': enUSDictionaryBundle
}

/** 与 `useLanguage` 共用 key，避免首屏字典与顶栏语言不一致 */
const LANG_STORAGE_KEY = 'lang'

function readStoredLang(): DictionaryLocale {
    try {
        const raw = localStorage.getItem(LANG_STORAGE_KEY)
        if (raw === 'en-US' || raw === 'zh-CN') return raw
    } catch {
        /* SSR 或无 storage */
    }
    return 'zh-CN'
}

export const dictionaryLocale: Ref<DictionaryLocale> = ref(readStoredLang())

export function setDictionaryLocale(locale: DictionaryLocale) {
    dictionaryLocale.value = locale
}

export function getDictionaryLocale(): DictionaryLocale {
    return dictionaryLocale.value
}

export function getDictionary<T extends DictionaryId>(id: T): DictionaryBundle[T] {
    return bundles[dictionaryLocale.value][id]
}

export function getDictionaryBundle(locale: DictionaryLocale): DictionaryBundle {
    return bundles[locale]
}

export function useDictionary<T extends DictionaryId>(id: T): ComputedRef<DictionaryBundle[T]> {
    return computed(() => getDictionary(id))
}
