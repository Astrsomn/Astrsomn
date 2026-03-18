package org.astrsomn.core.common.base;

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

        register(AI_MODEL_CONFIG + "PROVIDER", AiModelEnum.ProviderEnum.class);
        register(AI_MODEL_CONFIG + "STATUS", AiModelEnum.StatusEnum.class);
        register(AI_MODEL_CONFIG + "CAPABILITIES", AiModelEnum.CapabilitiesEnum.class);
        register(AI_PROMPT + "STATUS", AiPromptEnum.StatusEnum.class);
        register(AI_PROMPT + "SCENE", AiPromptEnum.SceneEnum.class);

        register(AI_TEMPLATE_ENGINE + "TEMPLATE_TYPE", AiTemplateEnum.TemplateTypeEnum.class);
        register(AI_TEMPLATE_ENGINE + "ENABLED", AiTemplateEnum.EnabledEnum.class);
        register(AI_TEMPLATE_ENGINE + "SYSTEM_BUILTIN", AiTemplateEnum.SystemBuiltinEnum.class);
        register(AI_MCP_CONFIG + "TYPE", AiMcpEnum.TypeEnum.class);
        register(AI_MCP_CONFIG + "ENABLED", AiMcpEnum.EnabledEnum.class);
        register(AI_TOOL_CONFIG + "TYPE", AiToolEnum.TypeEnum.class);
        register(AI_TOOL_CONFIG + "STATUS", AiToolEnum.StatusEnum.class);
        register(AI_AGENT_CONFIG + "STATUS", AiAgentEnum.StatusEnum.class);
        register(AI_AGENT_CONFIG + "MEMORY_MODE", AiAgentEnum.MemoryModeEnum.class);
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
