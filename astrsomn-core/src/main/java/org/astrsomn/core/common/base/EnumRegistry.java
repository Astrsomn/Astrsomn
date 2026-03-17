package org.astrsomn.core.common.base;

import org.astrsomn.common.constant.*;
import org.astrsomn.core.common.constant.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EnumRegistry {

    private static final Map<String, Class<? extends Enum<?>>> ENUM_MAP = new HashMap<>();


    public static String DATA_AI = "DATA_AI-";

    public static String AI_MODEL_CONFIG = DATA_AI + "AI_MODEL_CONFIG-";
    public static String AI_PROMPT = DATA_AI + "AI_PROMPT-";
    public static String AI_TEMPLATE_ENGINE = DATA_AI + "AI_TEMPLATE_ENGINE-";
    public static String AI_MCP_CONFIG = DATA_AI + "AI_MCP_CONFIG-";
    public static String AI_TOOL_CONFIG = DATA_AI + "AI_TOOL_CONFIG-";
    public static String AI_AGENT_CONFIG = DATA_AI + "AI_AGENT_CONFIG-";
    public static String BIZ_MYSQL_SLOW_SQL = DATA_AI + "BIZ_MYSQL_SLOW_SQL-";
    // 静态代码块注册所有枚举
    static {
        register(AI_MODEL_CONFIG + "MODEL_TYPE" , AiModelConfigEnum.ModelType.class);
        register(AI_MODEL_CONFIG + "PROVIDER", AiModelConfigEnum.ProviderEnum.class);
        register(AI_MODEL_CONFIG + "STATUS", AiModelConfigEnum.StatusEnum.class);
        register(AI_MODEL_CONFIG + "CAPABILITIES", AiModelConfigEnum.CapabilitiesEnum.class);
        register(AI_PROMPT + "STATUS", AiPromptEnum.StatusEnum.class);
        register(AI_PROMPT + "SCENE", AiPromptEnum.SceneEnum.class);
        register(AI_TEMPLATE_ENGINE + "CATEGORY", AiTemplateEngineEnum.CategoryEnum.class);
        register(AI_TEMPLATE_ENGINE + "TEMPLATE_TYPE", AiTemplateEngineEnum.TemplateTypeEnum.class);
        register(AI_TEMPLATE_ENGINE + "ENABLED", AiTemplateEngineEnum.EnabledEnum.class);
        register(AI_TEMPLATE_ENGINE + "SYSTEM_BUILTIN", AiTemplateEngineEnum.SystemBuiltinEnum.class);
        register(AI_MCP_CONFIG + "TYPE", AiMcpConfigEnum.TypeEnum.class);
        register(AI_MCP_CONFIG + "ENABLED", AiMcpConfigEnum.EnabledEnum.class);
        register(AI_TOOL_CONFIG + "TYPE", AiToolConfigEnum.TypeEnum.class);
        register(AI_TOOL_CONFIG + "STATUS", AiToolConfigEnum.StatusEnum.class);
        register(AI_AGENT_CONFIG + "STATUS", AiAgentConfigEnum.StatusEnum.class);
        register(AI_AGENT_CONFIG + "MEMORY_MODE", AiAgentConfigEnum.MemoryModeEnum.class);
    }

    private static void register(String name, Class<? extends Enum<?>> enumClass) {
        if (!BaseEnum.class.isAssignableFrom(enumClass)) {
            throw new IllegalArgumentException("枚举类必须实现 BaseEnum 接口: " + enumClass.getName());
        }
        ENUM_MAP.put(name, enumClass);
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Enum<?>> getEnumClass(String name) {
        return ENUM_MAP.get(name);
    }

    public boolean supports(String name) {
        return ENUM_MAP.containsKey(name);
    }
}
