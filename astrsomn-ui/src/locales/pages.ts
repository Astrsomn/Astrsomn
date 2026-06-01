import {ref, type Ref, computed, type ComputedRef, watch} from 'vue'
import {chatIndexPage as chatIndexZh} from './zh-CN/pages/chat-index'
import {chatIndexPage as chatIndexEn} from './en-US/pages/chat-index'
import {aiConfigCenterTranslation as aiConfigCenterZh} from './zh-CN/pages/ai-config-center'
import {aiConfigCenterTranslation as aiConfigCenterEn} from './en-US/pages/ai-config-center'
import {loginPageTranslation as loginZh} from './zh-CN/pages/login'
import {loginPageTranslation as loginEn} from './en-US/pages/login'
import {appTranslation as appZh} from './zh-CN/pages/app'
import {appTranslation as appEn} from './en-US/pages/app'
import {systemConfigCenterTranslation as sysCenterZh} from './zh-CN/pages/system-config-center'
import {systemConfigCenterTranslation as sysCenterEn} from './en-US/pages/system-config-center'
import {systemExtensionTranslation as sysExtZh} from './zh-CN/pages/system-extension'
import {systemExtensionTranslation as sysExtEn} from './en-US/pages/system-extension'
import {systemConfigTranslation as sysConfigZh} from './zh-CN/pages/system-config'
import {systemConfigTranslation as sysConfigEn} from './en-US/pages/system-config'
import {systemEnvTranslation as sysEnvZh} from './zh-CN/pages/system-env'
import {systemEnvTranslation as sysEnvEn} from './en-US/pages/system-env'
import {systemMessageTranslation as sysMsgZh} from './zh-CN/pages/system-message'
import {systemMessageTranslation as sysMsgEn} from './en-US/pages/system-message'
import {systemUserTranslation as sysUserZh} from './zh-CN/pages/system-user'
import {systemUserTranslation as sysUserEn} from './en-US/pages/system-user'
import {aiAccountTranslation as aiAccountZh} from './zh-CN/pages/ai-account'
import {aiAccountTranslation as aiAccountEn} from './en-US/pages/ai-account'
import {aiModelTranslation as aiModelPageZh} from './zh-CN/pages/ai-model'
import {aiModelTranslation as aiModelPageEn} from './en-US/pages/ai-model'
import {aiInstanceTranslation as aiInstanceZh} from './zh-CN/pages/ai-instance'
import {aiInstanceTranslation as aiInstanceEn} from './en-US/pages/ai-instance'
import {aiAgentTranslation as aiAgentZh} from './zh-CN/pages/ai-agent'
import {aiAgentTranslation as aiAgentEn} from './en-US/pages/ai-agent'
import {aiMcpTranslation as aiMcpZh} from './zh-CN/pages/ai-mcp'
import {aiMcpTranslation as aiMcpEn} from './en-US/pages/ai-mcp'
import {aiToolTranslation as aiToolZh} from './zh-CN/pages/ai-tool'
import {aiToolTranslation as aiToolEn} from './en-US/pages/ai-tool'
import {aiPromptTranslation as aiPromptZh} from './zh-CN/pages/ai-prompt'
import {aiPromptTranslation as aiPromptEn} from './en-US/pages/ai-prompt'
import {aiConversationTranslation as aiConvZh} from './zh-CN/pages/ai-conversation'
import {aiConversationTranslation as aiConvEn} from './en-US/pages/ai-conversation'
import {aiSensitiveWordTranslation as aiSensitiveZh} from './zh-CN/pages/ai-sensitive-word'
import {aiSensitiveWordTranslation as aiSensitiveEn} from './en-US/pages/ai-sensitive-word'
import {aiTemplateTranslation as aiTemplateZh} from './zh-CN/pages/ai-template'
import {aiTemplateTranslation as aiTemplateEn} from './en-US/pages/ai-template'
import {aiTraceLogTranslation as aiTraceZh} from './zh-CN/pages/ai-trace-log'
import {aiTraceLogTranslation as aiTraceEn} from './en-US/pages/ai-trace-log'
import {aiBuilderTranslation as aiBuilderZh} from './zh-CN/pages/ai-builder'
import {aiBuilderTranslation as aiBuilderEn} from './en-US/pages/ai-builder'
import {aiVectorTranslation as aiVectorZh} from './zh-CN/pages/ai-vector'
import {aiVectorTranslation as aiVectorEn} from './en-US/pages/ai-vector'
import {commonPageTranslation as commonZh} from './zh-CN/pages/common'
import {commonPageTranslation as commonEn} from './en-US/pages/common'
import {getDictionaryLocale, setDictionaryLocale, type DictionaryLocale} from './dictionary/registry'

export type PageTranslationBundle = {
  'chat-index': typeof chatIndexZh
  'ai-config-center': typeof aiConfigCenterZh
  'login': typeof loginZh
  'app': typeof appZh
  'system-config-center': typeof sysCenterZh
  'system-extension': typeof sysExtZh
  'system-config': typeof sysConfigZh
  'system-env': typeof sysEnvZh
  'system-message': typeof sysMsgZh
  'system-user': typeof sysUserZh
  'ai-account': typeof aiAccountZh
  'ai-model': typeof aiModelPageZh
  'ai-instance': typeof aiInstanceZh
  'ai-agent': typeof aiAgentZh
  'ai-mcp': typeof aiMcpZh
  'ai-tool': typeof aiToolZh
  'ai-prompt': typeof aiPromptZh
  'ai-conversation': typeof aiConvZh
  'ai-sensitive-word': typeof aiSensitiveZh
  'ai-template': typeof aiTemplateZh
  'ai-trace-log': typeof aiTraceZh
  'ai-builder': typeof aiBuilderZh
  'ai-vector': typeof aiVectorZh
  'common': typeof commonZh
}

const bundles: Record<DictionaryLocale, PageTranslationBundle> = {
  'zh-CN': {
    'chat-index': chatIndexZh,
    'ai-config-center': aiConfigCenterZh,
    'login': loginZh,
    'app': appZh,
    'system-config-center': sysCenterZh,
    'system-extension': sysExtZh,
    'system-config': sysConfigZh,
    'system-env': sysEnvZh,
    'system-message': sysMsgZh,
    'system-user': sysUserZh,
    'ai-account': aiAccountZh,
    'ai-model': aiModelPageZh,
    'ai-instance': aiInstanceZh,
    'ai-agent': aiAgentZh,
    'ai-mcp': aiMcpZh,
    'ai-tool': aiToolZh,
    'ai-prompt': aiPromptZh,
    'ai-conversation': aiConvZh,
    'ai-sensitive-word': aiSensitiveZh,
    'ai-template': aiTemplateZh,
    'ai-trace-log': aiTraceZh,
    'ai-builder': aiBuilderZh,
    'ai-vector': aiVectorZh,
    'common': commonZh
  },
  'en-US': {
    'chat-index': chatIndexEn,
    'ai-config-center': aiConfigCenterEn,
    'login': loginEn,
    'app': appEn,
    'system-config-center': sysCenterEn,
    'system-extension': sysExtEn,
    'system-config': sysConfigEn,
    'system-env': sysEnvEn,
    'system-message': sysMsgEn,
    'system-user': sysUserEn,
    'ai-account': aiAccountEn,
    'ai-model': aiModelPageEn,
    'ai-instance': aiInstanceEn,
    'ai-agent': aiAgentEn,
    'ai-mcp': aiMcpEn,
    'ai-tool': aiToolEn,
    'ai-prompt': aiPromptEn,
    'ai-conversation': aiConvEn,
    'ai-sensitive-word': aiSensitiveEn,
    'ai-template': aiTemplateEn,
    'ai-trace-log': aiTraceEn,
    'ai-builder': aiBuilderEn,
    'ai-vector': aiVectorEn,
    'common': commonEn
  }
}

const currentLocale: Ref<DictionaryLocale> = ref(getDictionaryLocale())

export function getPageTranslation<T extends keyof PageTranslationBundle>(pageId: T): PageTranslationBundle[T] {
  return bundles[currentLocale.value][pageId]
}

export function usePageTranslation<T extends keyof PageTranslationBundle>(pageId: T): ComputedRef<PageTranslationBundle[T]> {
  return computed(() => bundles[currentLocale.value][pageId])
}

export function syncPageTranslationLocale(locale: DictionaryLocale) {
  currentLocale.value = locale
}

watch(
  () => getDictionaryLocale(),
  (newLocale) => {
    currentLocale.value = newLocale
  }
)