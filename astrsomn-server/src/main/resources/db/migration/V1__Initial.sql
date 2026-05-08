-- Astrsomn 数据库初始化脚本
-- Flyway 版本: V1
-- 创建时间: 2026-05-08

-- 在此添加数据库初始化DDL语句
-- 示例:
-- CREATE TABLE  IF NOT EXISTS example (
--     id BIGINT PRIMARY KEY AUTO_INCREMENT,
--     name VARCHAR(255) NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- 注意: 请确保SQL语句以分号结尾
-- astro_ai.ai_account definition

CREATE TABLE IF NOT EXISTS `ai_account` (
                              `ID` bigint NOT NULL,
                              `CREATE_TIME` timestamp NULL DEFAULT NULL,
                              `UPDATE_TIME` timestamp NULL DEFAULT NULL,
                              `CREATE_USER` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `UPDATE_USER` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `DELETED` tinyint(1) DEFAULT '0',
                              `ENV_CODE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `API_KEY` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `API_SECRET` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `ACCOUNT_KEY` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `ACCOUNT_NAME` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `ACCOUNT_TOKENS` bigint DEFAULT NULL,
                              `API_URL` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `EXTENSION_CODE` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `STATUS` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- astro_ai.ai_agent definition

CREATE TABLE IF NOT EXISTS `ai_agent` (
                            `ID` bigint NOT NULL,
                            `CREATE_TIME` timestamp NULL DEFAULT NULL,
                            `UPDATE_TIME` timestamp NULL DEFAULT NULL,
                            `CREATE_USER` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `UPDATE_USER` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `DELETED` tinyint(1) DEFAULT '0',
                            `ENV_CODE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `STATUS` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `DESCRIPTION` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                            `AGENT_KEY` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `WORKFLOW_KEY` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '绑定的 WORKFLOW_KEY，非空时应用层可走工作流编排',
                            `PROMPT_KEY` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `CHAT_INSTANCE_KEY` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `EMBEDDING_INSTANCE_KEY` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `IMAGE_INSTANCE_KEY` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `AGENT_NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `KNOWLEDGE_BASE_KEYS` text COLLATE utf8mb4_unicode_ci,
                            `TOOL_KEYS` text COLLATE utf8mb4_unicode_ci,
                            `MCP_KEYS` text COLLATE utf8mb4_unicode_ci,
                            `MEMORY_MODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `MEMORY_WINDOW_SIZE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `MAX_TOKENS` int DEFAULT NULL,
                            `ENABLE_STREAM` tinyint(1) DEFAULT NULL,
                            `IS_DEFAULT` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            PRIMARY KEY (`ID`),
                            KEY `IDX_AI_AGENT_AGENT_KEY` (`AGENT_KEY`),
                            KEY `IDX_AI_AGENT_STATUS` (`STATUS`),
                            KEY `IDX_AI_AGENT_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- astro_ai.ai_chat_message definition

CREATE TABLE IF NOT EXISTS `ai_chat_message` (
                                   `ID` bigint NOT NULL COMMENT '主键ID',
                                   `MEMORY_KEY` varchar(128) NOT NULL COMMENT '会话记忆键',
                                   `TURN_NO` int DEFAULT NULL COMMENT '会话轮次',
                                   `MESSAGE_ORDER` int DEFAULT NULL COMMENT '消息顺序',
                                   `ROLE` varchar(32) DEFAULT NULL COMMENT '角色: user/assistant/system/tool',
                                   `MESSAGE_TYPE` varchar(32) DEFAULT NULL COMMENT '消息类型: text/reasoning/tool_call/tool_result/error',
                                   `CONTENT` longtext COMMENT '消息内容',
                                   `RESPONSE_STATUS` varchar(32) DEFAULT NULL COMMENT '响应状态: streaming/completed/failed/interrupted',
                                   `FINISH_REASON` varchar(64) DEFAULT NULL COMMENT '结束原因',
                                   `PROMPT_TOKENS` int DEFAULT '0' COMMENT '输入token',
                                   `COMPLETION_TOKENS` int DEFAULT '0' COMMENT '输出token',
                                   `TOTAL_TOKENS` int DEFAULT '0' COMMENT '总token',
                                   `TRACE_ID` varchar(128) DEFAULT NULL COMMENT '链路追踪ID',
                                   `ERROR_CODE` varchar(64) DEFAULT NULL COMMENT '错误码',
                                   `AGENT_KEY` varchar(128) DEFAULT NULL COMMENT 'Agent Key',
                                   `INSTANCE_KEY` varchar(128) DEFAULT NULL COMMENT '实例Key',
                                   `MODEL_KEY` varchar(128) DEFAULT NULL COMMENT '模型Key',
                                   `ACCOUNT_KEY` varchar(128) DEFAULT NULL COMMENT '账号Key',
                                   `PROMPT_KEY` varchar(128) DEFAULT NULL COMMENT 'Prompt Key',
                                   `EXT_JSON` longtext COMMENT '扩展信息JSON',
                                   `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `CREATE_USER` varchar(64) DEFAULT NULL COMMENT '创建人',
                                   `UPDATE_USER` varchar(64) DEFAULT NULL COMMENT '更新人',
                                   `DELETED` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标记',
                                   `ENV_CODE` varchar(32) DEFAULT NULL COMMENT '环境编码',
                                   PRIMARY KEY (`ID`),
                                   KEY `IDX_AI_CHAT_MESSAGE_MEMORY_ORDER` (`MEMORY_KEY`,`TURN_NO`,`MESSAGE_ORDER`),
                                   KEY `IDX_AI_CHAT_MESSAGE_MEMORY_TIME` (`MEMORY_KEY`,`UPDATE_TIME`),
                                   KEY `IDX_AI_CHAT_MESSAGE_ENV_TIME` (`ENV_CODE`,`CREATE_TIME`),
                                   KEY `IDX_AI_CHAT_MESSAGE_TRACE` (`TRACE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI对话消息明细表';


-- astro_ai.ai_chat_session definition

CREATE TABLE IF NOT EXISTS `ai_chat_session` (
                                   `ID` bigint NOT NULL COMMENT '主键ID',
                                   `MEMORY_KEY` varchar(128) NOT NULL COMMENT '会话聚合键',
                                   `SESSION_TITLE` varchar(255) DEFAULT NULL COMMENT '会话标题',
                                   `SESSION_STATUS` varchar(32) DEFAULT NULL COMMENT '会话状态: active/archived/deleted',
                                   `LAST_MESSAGE_PREVIEW` text COMMENT '最后一条消息预览',
                                   `LAST_MESSAGE_AT` bigint DEFAULT NULL COMMENT '最后消息时间戳(毫秒)',
                                   `MESSAGE_COUNT` int DEFAULT '0' COMMENT '消息条数',
                                   `PROMPT_TOKENS` int DEFAULT '0' COMMENT '输入token累计',
                                   `COMPLETION_TOKENS` int DEFAULT '0' COMMENT '输出token累计',
                                   `TOTAL_TOKENS` int DEFAULT '0' COMMENT '总token累计',
                                   `AGENT_KEY` varchar(128) DEFAULT NULL COMMENT 'Agent Key',
                                   `INSTANCE_KEY` varchar(128) DEFAULT NULL COMMENT '实例Key',
                                   `MODEL_KEY` varchar(128) DEFAULT NULL COMMENT '模型Key',
                                   `ACCOUNT_KEY` varchar(128) DEFAULT NULL COMMENT '账号Key',
                                   `PROMPT_KEY` varchar(128) DEFAULT NULL COMMENT 'Prompt Key',
                                   `TRACE_ID` varchar(128) DEFAULT NULL COMMENT '链路追踪ID',
                                   `EXT_JSON` text COMMENT '扩展信息JSON',
                                   `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `CREATE_USER` varchar(64) DEFAULT NULL COMMENT '创建人',
                                   `UPDATE_USER` varchar(64) DEFAULT NULL COMMENT '更新人',
                                   `DELETED` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标记',
                                   `ENV_CODE` varchar(32) DEFAULT NULL COMMENT '环境编码',
                                   PRIMARY KEY (`ID`),
                                   UNIQUE KEY `UK_AI_CHAT_SESSION_MEMORY_ENV` (`MEMORY_KEY`,`ENV_CODE`),
                                   KEY `IDX_AI_CHAT_SESSION_ENV_STATUS_TIME` (`ENV_CODE`,`SESSION_STATUS`,`UPDATE_TIME`),
                                   KEY `IDX_AI_CHAT_SESSION_MEMORY` (`MEMORY_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI会话聚合表';


-- astro_ai.ai_conversation definition

CREATE TABLE IF NOT EXISTS `ai_conversation` (
                                   `ID` bigint NOT NULL,
                                   `MEMORY_KEY` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `ROLE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `CONTENT` text COLLATE utf8mb4_unicode_ci,
                                   `MESSAGE_ORDER` int DEFAULT NULL,
                                   `CONSUME_TOKENS` int DEFAULT NULL,
                                   `MODEL_KEY` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `AGENT_KEY` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `PROMPT_KEY` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `CREATE_TIME` timestamp NULL DEFAULT NULL,
                                   `UPDATE_TIME` timestamp NULL DEFAULT NULL,
                                   `CREATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `UPDATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `DELETED` tinyint(1) DEFAULT '0',
                                   `ENV_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `INSTANCE_KEY` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `ACCOUNT_KEY` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   PRIMARY KEY (`ID`),
                                   KEY `IDX_AI_CONV_AGENT_ID` (`AGENT_KEY`),
                                   KEY `IDX_AI_CONV_MODEL_ID` (`MODEL_KEY`),
                                   KEY `IDX_AI_CONV_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- astro_ai.ai_instance definition

CREATE TABLE IF NOT EXISTS `ai_instance` (
                               `ID` bigint NOT NULL,
                               `CREATE_TIME` timestamp NULL DEFAULT NULL,
                               `UPDATE_TIME` timestamp NULL DEFAULT NULL,
                               `CREATE_USER` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `UPDATE_USER` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `DELETED` tinyint(1) DEFAULT '0',
                               `ENV_CODE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `INSTANCE_KEY` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `MAX_TOKENS` bigint DEFAULT NULL,
                               `TEMPERATURE` double DEFAULT NULL,
                               `PRESENCE_PENALTY` double DEFAULT NULL,
                               `FREQUENCY_PENALTY` double DEFAULT NULL,
                               `STOP_SEQUENCES` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                               `SEED` int DEFAULT NULL,
                               `TOP_P` double DEFAULT NULL,
                               `TOP_K` int DEFAULT NULL,
                               `STYLE` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `SIZE` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `DIMENSIONS` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `STATUS` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `INSTANCE_NAME` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `MODEL_KEY` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- astro_ai.ai_mcp definition

CREATE TABLE IF NOT EXISTS `ai_mcp` (
                          `ID` bigint NOT NULL,
                          `MCP_KEY` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `SERVER_NAME` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `DESCRIPTION` text COLLATE utf8mb4_unicode_ci,
                          `TYPE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `SSE_ADDRESS` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `REQUEST_HEADER_CONFIG` text COLLATE utf8mb4_unicode_ci,
                          `ENABLED` int DEFAULT NULL,
                          `COMMAND` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `ARGS` text COLLATE utf8mb4_unicode_ci,
                          `ENV_VARS` text COLLATE utf8mb4_unicode_ci,
                          `CREATE_TIME` timestamp NULL DEFAULT NULL,
                          `UPDATE_TIME` timestamp NULL DEFAULT NULL,
                          `CREATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `UPDATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `DELETED` tinyint(1) DEFAULT '0',
                          `ENV_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          PRIMARY KEY (`ID`),
                          KEY `IDX_AI_MCP_MCP_KEY` (`MCP_KEY`),
                          KEY `IDX_AI_MCP_ENABLED` (`ENABLED`),
                          KEY `IDX_AI_MCP_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- astro_ai.ai_model definition

CREATE TABLE IF NOT EXISTS `ai_model` (
                            `ID` bigint NOT NULL,
                            `MODEL_NAME` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `MODEL_KEY` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `MODEL_TYPE` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `EXTENSION_CODE` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `API_URL` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `STATUS` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `RESPONSE_LIMIT` int DEFAULT NULL,
                            `IS_DEFAULT` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `CAPABILITIES` text COLLATE utf8mb4_unicode_ci,
                            `CREATE_TIME` timestamp NULL DEFAULT NULL,
                            `UPDATE_TIME` timestamp NULL DEFAULT NULL,
                            `CREATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `UPDATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `DELETED` tinyint(1) DEFAULT '0',
                            `ENV_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `MAX_QUOTA_TOKENS` bigint DEFAULT NULL,
                            `ACCOUNT_KEY` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `PARAMS` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                            `SOURCE_TYPE` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            PRIMARY KEY (`ID`),
                            KEY `IDX_AI_MODEL_MODEL_KEY` (`MODEL_KEY`),
                            KEY `IDX_AI_MODEL_STATUS` (`STATUS`),
                            KEY `IDX_AI_MODEL_IS_DEFAULT` (`IS_DEFAULT`),
                            KEY `IDX_AI_MODEL_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- astro_ai.ai_prompt definition

CREATE TABLE IF NOT EXISTS `ai_prompt` (
                             `ID` bigint NOT NULL,
                             `PROMPT_KEY` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `PROMPT_TITLE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `PROMPT_CONTENT` text COLLATE utf8mb4_unicode_ci,
                             `SCENE` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `STATUS` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `VERSION` int DEFAULT NULL,
                             `CREATE_TIME` timestamp NULL DEFAULT NULL,
                             `UPDATE_TIME` timestamp NULL DEFAULT NULL,
                             `CREATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `UPDATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `DELETED` tinyint(1) DEFAULT '0',
                             `ENV_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             PRIMARY KEY (`ID`),
                             KEY `IDX_AI_PROMPT_PROMPT_KEY` (`PROMPT_KEY`),
                             KEY `IDX_AI_PROMPT_SCENE` (`SCENE`),
                             KEY `IDX_AI_PROMPT_VERSION` (`VERSION`),
                             KEY `IDX_AI_PROMPT_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- astro_ai.ai_sensitive_word definition

CREATE TABLE IF NOT EXISTS `ai_sensitive_word` (
                                     `ID` bigint NOT NULL COMMENT '主键ID',
                                     `WORD` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '敏感词内容或正则表达式',
                                     `MATCH_TYPE` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'FUZZY' COMMENT '匹配类型: EXACT-精确, FUZZY-模糊, REGEX-正则',
                                     `SCOPE_KEY` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'ALL' COMMENT '作用范围: ALL-全局, 或具体的AGENT_KEY',
                                     `ACTION` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'BLOCK' COMMENT '处置动作: BLOCK-拦截, REPLACE-替换, WARN-仅记录',
                                     `REPLACEMENT` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '替换文本 (当ACTION为REPLACE时使用)',
                                     `CATEGORY` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类: POLITICS-政治, VIOLENCE-暴力, ADULT-色情, CUSTOM-自定义',
                                     `STATUS` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ENABLED' COMMENT '状态: ENABLED-启用, DISABLED-禁用',
                                     `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `CREATE_USER` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                     `UPDATE_USER` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                                     `DELETED` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记: 0-未删除, 1-已删除',
                                     `ENV_CODE` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境编码',
                                     PRIMARY KEY (`ID`),
                                     KEY `IDX_WORD` (`WORD`),
                                     KEY `IDX_SCOPE_STATUS` (`SCOPE_KEY`,`STATUS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI敏感词过滤配置表';


-- astro_ai.ai_template definition

CREATE TABLE IF NOT EXISTS `ai_template` (
                               `ID` bigint NOT NULL,
                               `TEMPLATE_KEY` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `TEMPLATE_TITLE` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `CONTENT` text COLLATE utf8mb4_unicode_ci,
                               `CATEGORY` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `TEMPLATE_TYPE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `VERSION` int DEFAULT NULL,
                               `STATUS` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `CREATE_TIME` timestamp NULL DEFAULT NULL,
                               `UPDATE_TIME` timestamp NULL DEFAULT NULL,
                               `CREATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `UPDATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `DELETED` tinyint(1) DEFAULT '0',
                               `ENV_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               PRIMARY KEY (`ID`),
                               KEY `IDX_AI_TEMPLATE_TEMPLATE_KEY` (`TEMPLATE_KEY`),
                               KEY `IDX_AI_TEMPLATE_CATEGORY` (`CATEGORY`),
                               KEY `IDX_AI_TEMPLATE_VERSION` (`VERSION`),
                               KEY `IDX_AI_TEMPLATE_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- astro_ai.ai_tool definition

CREATE TABLE IF NOT EXISTS `ai_tool` (
                           `ID` bigint NOT NULL,
                           `TOOL_NAME` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `TOOL_KEY` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `DESCRIPTION` text COLLATE utf8mb4_unicode_ci,
                           `BEAN_NAME` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `METHOD_NAME` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `TYPE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `STATUS` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `CREATE_TIME` timestamp NULL DEFAULT NULL,
                           `UPDATE_TIME` timestamp NULL DEFAULT NULL,
                           `CREATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `UPDATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `DELETED` tinyint(1) DEFAULT '0',
                           `ENV_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `CLASS_NAME` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           PRIMARY KEY (`ID`),
                           KEY `IDX_AI_TOOL_TOOL_KEY` (`TOOL_KEY`),
                           KEY `IDX_AI_TOOL_TYPE` (`TYPE`),
                           KEY `IDX_AI_TOOL_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- astro_ai.ai_trace_log definition

CREATE TABLE IF NOT EXISTS `ai_trace_log` (
                                `ID` bigint NOT NULL COMMENT '主键 ID',
                                `TRACE_ID` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '链路追踪全局 ID (UUID)',
                                `CONVERSATION_ID` bigint DEFAULT NULL COMMENT '关联对话表 ID (AI_CONVERSATION.ID)',
                                `AGENT_ID` bigint DEFAULT NULL COMMENT '关联 Agent ID (AI_AGENT.ID)',
                                `INSTANCE_ID` bigint DEFAULT NULL COMMENT '关联模型实例 ID (AI_INSTANCE.ID)',
                                `PROMPT_ID` bigint DEFAULT NULL COMMENT '关联提示词 ID (AI_PROMPT.ID)',
                                `AGENT_KEY` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '冗余存储当时的关键 Key',
                                `SNAPSHOT_CONTENT` text COLLATE utf8mb4_unicode_ci COMMENT '运行快照 (JSON)',
                                `NODE_TYPE` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点类型 (LLM, RAG, MCP, TOOL)',
                                `INPUT_DATA` text COLLATE utf8mb4_unicode_ci COMMENT '输入数据',
                                `OUTPUT_DATA` text COLLATE utf8mb4_unicode_ci COMMENT '输出数据',
                                `CONSUME_TOKENS` int DEFAULT NULL COMMENT '消耗 Token 数',
                                `DURATION` bigint DEFAULT NULL COMMENT '执行耗时 (毫秒)',
                                `STATUS` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '执行状态 (SUCCESS, FAIL)',
                                `CREATE_TIME` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                                `UPDATE_TIME` timestamp NULL DEFAULT NULL COMMENT '更新时间',
                                `CREATE_USER` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                `UPDATE_USER` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                                `DELETED` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标志 (0: 未删除, 1: 已删除)',
                                `ENV_CODE` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境编码',
                                PRIMARY KEY (`ID`),
                                KEY `IDX_TRACE_ID` (`TRACE_ID`),
                                KEY `IDX_CONVERSATION_ID` (`CONVERSATION_ID`),
                                KEY `IDX_AGENT_ID` (`AGENT_ID`),
                                KEY `IDX_INSTANCE_ID` (`INSTANCE_ID`),
                                KEY `IDX_STATUS` (`STATUS`),
                                KEY `IDX_CREATE_TIME` (`CREATE_TIME`),
                                KEY `IDX_ENV_CODE` (`ENV_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 链路追踪日志表';


-- astro_ai.ai_vec_doc definition

CREATE TABLE IF NOT EXISTS `ai_vec_doc` (
                              `ID` bigint NOT NULL COMMENT '主键 ID',
                              `COLLECTION_ID` bigint DEFAULT NULL COMMENT '所属集合 ID',
                              `DOC_ID_IN_STORE` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '向量库中的实际唯一标识 (UUID 或 Long)',
                              `CONTENT_SUMMARY` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文本摘要或路径',
                              `SYNC_STATUS` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '同步状态 (待向量化、已入库、已失效)',
                              `CREATE_TIME` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                              `UPDATE_TIME` timestamp NULL DEFAULT NULL COMMENT '更新时间',
                              `CREATE_USER` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                              `UPDATE_USER` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                              `DELETED` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标志 (0: 未删除, 1: 已删除)',
                              `ENV_CODE` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境编码',
                              `FILE_PATH` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `ORIGINAL_FILE_NAME` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              PRIMARY KEY (`ID`),
                              KEY `IDX_COLLECTION_ID` (`COLLECTION_ID`),
                              KEY `IDX_DOC_ID_IN_STORE` (`DOC_ID_IN_STORE`),
                              KEY `IDX_SYNC_STATUS` (`SYNC_STATUS`),
                              KEY `IDX_CREATE_TIME` (`CREATE_TIME`),
                              KEY `IDX_ENV_CODE` (`ENV_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 向量文档表';


-- astro_ai.ai_vec_driver definition

CREATE TABLE IF NOT EXISTS `ai_vec_driver` (
                                 `ID` bigint NOT NULL COMMENT '主键 ID',
                                 `CREATE_TIME` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                                 `UPDATE_TIME` timestamp NULL DEFAULT NULL COMMENT '更新时间',
                                 `CREATE_USER` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                 `UPDATE_USER` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                                 `DELETED` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标志 (0: 未删除, 1: 已删除)',
                                 `ENV_CODE` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境编码',
                                 `DRIVER_NAME` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `EXTENSION_CODE` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `PARAMS` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `STATUS` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- astro_ai.ai_vec_segment definition

CREATE TABLE IF NOT EXISTS `ai_vec_segment` (
                                  `ID` bigint NOT NULL COMMENT '主键 ID',
                                  `DOC_ID` bigint DEFAULT NULL COMMENT '关联原始文档 ID (AST_DOCUMENT.ID)',
                                  `COLLECTION_ID` bigint DEFAULT NULL COMMENT '关联向量集合 ID',
                                  `VECTOR_ID` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '向量库里的唯一标识 (LangChain4j 生成的 UUID)',
                                  `SEGMENT_CONTENT` text COLLATE utf8mb4_unicode_ci COMMENT '切片文本内容',
                                  `WORD_COUNT` bigint DEFAULT NULL COMMENT '字符数',
                                  `CHUNK_INDEX` bigint DEFAULT NULL COMMENT '切片序号',
                                  `METADATA_JSON` text COLLATE utf8mb4_unicode_ci COMMENT '增强元数据 (存储页码、作者、时间等)',
                                  `CREATE_TIME` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                                  `UPDATE_TIME` timestamp NULL DEFAULT NULL COMMENT '更新时间',
                                  `CREATE_USER` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                  `UPDATE_USER` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                                  `DELETED` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标志 (0: 未删除, 1: 已删除)',
                                  `ENV_CODE` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境编码',
                                  PRIMARY KEY (`ID`),
                                  KEY `IDX_DOC_ID` (`DOC_ID`),
                                  KEY `IDX_COLLECTION_ID` (`COLLECTION_ID`),
                                  KEY `IDX_VECTOR_ID` (`VECTOR_ID`),
                                  KEY `IDX_CHUNK_INDEX` (`CHUNK_INDEX`),
                                  KEY `IDX_CREATE_TIME` (`CREATE_TIME`),
                                  KEY `IDX_ENV_CODE` (`ENV_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 向量切片表';


-- astro_ai.ai_vec_source definition

CREATE TABLE IF NOT EXISTS `ai_vec_source` (
                                 `ID` bigint NOT NULL COMMENT '主键 ID',
                                 `NAME` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据源名称',
                                 `EXTENSION_CODE` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '提供商',
                                 `HOST` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主机地址',
                                 `PORT` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '端口',
                                 `USERNAME` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名',
                                 `PASSWORD` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
                                 `DATABASE_NAME` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据库名',
                                 `TOKEN` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'API Key (Pinecone/DashVector)',
                                 `CONFIG_JSON` text COLLATE utf8mb4_unicode_ci COMMENT '配置 JSON',
                                 `STATUS` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态',
                                 `CREATE_TIME` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                                 `UPDATE_TIME` timestamp NULL DEFAULT NULL COMMENT '更新时间',
                                 `CREATE_USER` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                 `UPDATE_USER` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                                 `DELETED` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标志 (0: 未删除, 1: 已删除)',
                                 `ENV_CODE` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境编码',
                                 PRIMARY KEY (`ID`),
                                 KEY `IDX_NAME` (`NAME`),
                                 KEY `IDX_PROVIDER` (`EXTENSION_CODE`),
                                 KEY `IDX_STATUS` (`STATUS`),
                                 KEY `IDX_CREATE_TIME` (`CREATE_TIME`),
                                 KEY `IDX_ENV_CODE` (`ENV_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 向量数据源表';


-- astro_ai.ai_vec_store definition

CREATE TABLE IF NOT EXISTS `ai_vec_store` (
                                `ID` bigint NOT NULL COMMENT '主键 ID',
                                `SOURCE_ID` bigint DEFAULT NULL COMMENT '关联数据源 ID (AI_VEC_SOURCE.ID)',
                                `COLLECTION_NAME` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '集合名称',
                                `DIMENSION` bigint DEFAULT NULL COMMENT '向量维度',
                                `DISTANCE_METRIC` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '距离度量方式',
                                `METADATA_SCHEMA` text COLLATE utf8mb4_unicode_ci COMMENT '元数据 Schema',
                                `MODEL_KEY` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联模型 Key',
                                `CREATE_TIME` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                                `UPDATE_TIME` timestamp NULL DEFAULT NULL COMMENT '更新时间',
                                `CREATE_USER` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                `UPDATE_USER` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                                `DELETED` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标志 (0: 未删除, 1: 已删除)',
                                `ENV_CODE` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境编码',
                                `INSTANCE_KEY` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                PRIMARY KEY (`ID`),
                                KEY `IDX_SOURCE_ID` (`SOURCE_ID`),
                                KEY `IDX_COLLECTION_NAME` (`COLLECTION_NAME`),
                                KEY `IDX_MODEL_KEY` (`MODEL_KEY`),
                                KEY `IDX_CREATE_TIME` (`CREATE_TIME`),
                                KEY `IDX_ENV_CODE` (`ENV_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 向量存储表';


-- astro_ai.ast_flow_biz_idempotent definition

CREATE TABLE IF NOT EXISTS `ast_flow_biz_idempotent` (
                                           `ID` bigint NOT NULL COMMENT '主键ID',
                                           `IDEMPOTENT_KEY` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '幂等键',
                                           `BIZ_TYPE` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务类型',
                                           `BIZ_ID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务ID',
                                           `REQUEST_HASH` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求哈希',
                                           `RESULT_REF` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '结果引用',
                                           `EXPIRE_AT_MS` bigint DEFAULT NULL COMMENT '过期时间戳(毫秒)',
                                           `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
                                           `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
                                           `CREATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                           `UPDATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                                           `DELETED` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
                                           `ENV_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境标识',
                                           PRIMARY KEY (`ID`),
                                           UNIQUE KEY `UK_IDEMPOTENT_KEY` (`IDEMPOTENT_KEY`),
                                           KEY `IDX_BIZ_ID` (`BIZ_ID`),
                                           KEY `IDX_EXPIRE_AT_MS` (`EXPIRE_AT_MS`),
                                           KEY `IDX_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='业务幂等表';


-- astro_ai.ast_flow_definition definition

CREATE TABLE IF NOT EXISTS `ast_flow_definition` (
                                       `ID` bigint NOT NULL COMMENT '主键ID',
                                       `NAME` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程名称',
                                       `FLOW_KEY` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程标识键',
                                       `CATEGORY` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类',
                                       `DRAFT_GRAPH_JSON` longtext COLLATE utf8mb4_unicode_ci COMMENT '草稿流程图JSON',
                                       `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
                                       `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
                                       `CREATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                       `UPDATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                                       `DELETED` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
                                       `ENV_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境标识',
                                       PRIMARY KEY (`ID`),
                                       KEY `IDX_FLOW_KEY` (`FLOW_KEY`),
                                       KEY `IDX_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程定义表';


-- astro_ai.ast_flow_deployment definition

CREATE TABLE IF NOT EXISTS `ast_flow_deployment` (
                                       `ID` bigint NOT NULL COMMENT '主键ID',
                                       `FLOW_DEFINITION_ID` bigint DEFAULT NULL COMMENT '流程定义ID',
                                       `VERSION` int DEFAULT NULL COMMENT '版本号',
                                       `DEPLOYED_GRAPH_JSON` longtext COLLATE utf8mb4_unicode_ci COMMENT '已部署流程图JSON',
                                       `IS_LATEST` tinyint(1) DEFAULT NULL COMMENT '是否最新版本',
                                       `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
                                       `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
                                       `CREATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                       `UPDATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                                       `DELETED` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
                                       `ENV_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境标识',
                                       PRIMARY KEY (`ID`),
                                       KEY `IDX_FLOW_DEFINITION_ID` (`FLOW_DEFINITION_ID`),
                                       KEY `IDX_VERSION` (`VERSION`),
                                       KEY `IDX_IS_LATEST` (`IS_LATEST`),
                                       KEY `IDX_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程部署表';


-- astro_ai.ast_flow_human_task definition

CREATE TABLE IF NOT EXISTS `ast_flow_human_task` (
                                       `ID` bigint NOT NULL COMMENT '主键ID',
                                       `INSTANCE_ID` bigint DEFAULT NULL COMMENT '实例ID',
                                       `NODE_ID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点ID',
                                       `TASK_STATUS` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务状态',
                                       `PAYLOAD` longtext COLLATE utf8mb4_unicode_ci COMMENT '任务负载',
                                       `ACTION_DATA` longtext COLLATE utf8mb4_unicode_ci COMMENT '操作数据',
                                       `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
                                       `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
                                       `CREATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                       `UPDATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                                       `DELETED` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
                                       `ENV_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境标识',
                                       PRIMARY KEY (`ID`),
                                       KEY `IDX_INSTANCE_ID` (`INSTANCE_ID`),
                                       KEY `IDX_NODE_ID` (`NODE_ID`),
                                       KEY `IDX_TASK_STATUS` (`TASK_STATUS`),
                                       KEY `IDX_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='人工任务表';


-- astro_ai.ast_flow_instance definition

CREATE TABLE IF NOT EXISTS `ast_flow_instance` (
                                     `ID` bigint NOT NULL COMMENT '主键ID',
                                     `DEPLOYMENT_ID` bigint DEFAULT NULL COMMENT '部署ID',
                                     `BUSINESS_KEY` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务主键',
                                     `EXECUTION_STATUS` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '执行状态',
                                     `CURRENT_NODE_ID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '当前节点ID',
                                     `STATE_JSON` longtext COLLATE utf8mb4_unicode_ci COMMENT '状态JSON',
                                     `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
                                     `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
                                     `CREATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                     `UPDATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                                     `DELETED` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
                                     `ENV_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境标识',
                                     PRIMARY KEY (`ID`),
                                     KEY `IDX_DEPLOYMENT_ID` (`DEPLOYMENT_ID`),
                                     KEY `IDX_BUSINESS_KEY` (`BUSINESS_KEY`),
                                     KEY `IDX_EXECUTION_STATUS` (`EXECUTION_STATUS`),
                                     KEY `IDX_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程实例表';


-- astro_ai.ast_flow_instance_event definition

CREATE TABLE IF NOT EXISTS `ast_flow_instance_event` (
                                           `ID` bigint NOT NULL COMMENT '主键ID',
                                           `INSTANCE_ID` bigint DEFAULT NULL COMMENT '实例ID',
                                           `EVENT_TYPE` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '事件类型',
                                           `NODE_ID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点ID',
                                           `EVENT_TIME_MS` bigint DEFAULT NULL COMMENT '事件时间戳(毫秒)',
                                           `EVENT_DATA_JSON` longtext COLLATE utf8mb4_unicode_ci COMMENT '事件数据JSON',
                                           `TRACE_ID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '追踪ID',
                                           `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
                                           `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
                                           `CREATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                           `UPDATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                                           `DELETED` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
                                           `ENV_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境标识',
                                           PRIMARY KEY (`ID`),
                                           KEY `IDX_INSTANCE_ID` (`INSTANCE_ID`),
                                           KEY `IDX_EVENT_TYPE` (`EVENT_TYPE`),
                                           KEY `IDX_TRACE_ID` (`TRACE_ID`),
                                           KEY `IDX_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程实例事件表';


-- astro_ai.ast_flow_msg_outbox definition

CREATE TABLE IF NOT EXISTS `ast_flow_msg_outbox` (
                                       `ID` bigint NOT NULL COMMENT '主键ID',
                                       `BIZ_TYPE` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务类型',
                                       `BIZ_ID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务ID',
                                       `TOPIC_OR_ENDPOINT` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息主题或端点',
                                       `PAYLOAD_JSON` longtext COLLATE utf8mb4_unicode_ci COMMENT '消息负载JSON',
                                       `MSG_STATUS` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息状态',
                                       `RETRY_COUNT` int DEFAULT NULL COMMENT '重试次数',
                                       `NEXT_RETRY_TIME_MS` bigint DEFAULT NULL COMMENT '下次重试时间戳(毫秒)',
                                       `LAST_ERROR` longtext COLLATE utf8mb4_unicode_ci COMMENT '最后错误信息',
                                       `IDEMPOTENT_KEY` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '幂等键',
                                       `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
                                       `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
                                       `CREATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                       `UPDATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                                       `DELETED` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
                                       `ENV_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境标识',
                                       PRIMARY KEY (`ID`),
                                       KEY `IDX_BIZ_ID` (`BIZ_ID`),
                                       KEY `IDX_MSG_STATUS` (`MSG_STATUS`),
                                       KEY `IDX_IDEMPOTENT_KEY` (`IDEMPOTENT_KEY`),
                                       KEY `IDX_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息投递箱表';


-- astro_ai.ast_flow_node_config definition

CREATE TABLE IF NOT EXISTS `ast_flow_node_config` (
                                        `ID` bigint NOT NULL COMMENT '主键ID',
                                        `FLOW_DEFINITION_ID` bigint DEFAULT NULL COMMENT '流程定义ID',
                                        `NODE_ID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点ID',
                                        `NODE_TYPE` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点类型',
                                        `CONFIG_JSON` longtext COLLATE utf8mb4_unicode_ci COMMENT '配置JSON',
                                        `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
                                        `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
                                        `CREATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                        `UPDATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                                        `DELETED` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
                                        `ENV_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境标识',
                                        PRIMARY KEY (`ID`),
                                        KEY `IDX_FLOW_DEFINITION_ID` (`FLOW_DEFINITION_ID`),
                                        KEY `IDX_NODE_ID` (`NODE_ID`),
                                        KEY `IDX_NODE_TYPE` (`NODE_TYPE`),
                                        KEY `IDX_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='节点配置表';


-- astro_ai.ast_flow_node_history definition

CREATE TABLE IF NOT EXISTS `ast_flow_node_history` (
                                         `ID` bigint NOT NULL COMMENT '主键ID',
                                         `INSTANCE_ID` bigint DEFAULT NULL COMMENT '实例ID',
                                         `FLOW_DEFINITION_ID` bigint DEFAULT NULL COMMENT '流程定义ID',
                                         `VERSION` int DEFAULT NULL COMMENT '版本号',
                                         `HISTORY_TYPE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '历史类型',
                                         `NODE_ID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点ID',
                                         `NODE_NAME` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点名称',
                                         `INPUT_DATA` longtext COLLATE utf8mb4_unicode_ci COMMENT '输入数据',
                                         `OUTPUT_DATA` longtext COLLATE utf8mb4_unicode_ci COMMENT '输出数据',
                                         `SNAPSHOT_JSON` longtext COLLATE utf8mb4_unicode_ci COMMENT '快照JSON',
                                         `EXECUTION_MS` bigint DEFAULT NULL COMMENT '执行耗时(毫秒)',
                                         `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
                                         `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
                                         `CREATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                         `UPDATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                                         `DELETED` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
                                         `ENV_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境标识',
                                         PRIMARY KEY (`ID`),
                                         KEY `IDX_INSTANCE_ID` (`INSTANCE_ID`),
                                         KEY `IDX_FLOW_DEFINITION_ID` (`FLOW_DEFINITION_ID`),
                                         KEY `IDX_NODE_ID` (`NODE_ID`),
                                         KEY `IDX_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='节点历史表';


-- astro_ai.ast_flow_timer_job definition

CREATE TABLE IF NOT EXISTS `ast_flow_timer_job` (
                                      `ID` bigint NOT NULL COMMENT '主键ID',
                                      `INSTANCE_ID` bigint DEFAULT NULL COMMENT '实例ID',
                                      `NODE_ID` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点ID',
                                      `JOB_TYPE` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务类型',
                                      `DUE_TIME_MS` bigint DEFAULT NULL COMMENT '到期时间戳(毫秒)',
                                      `JOB_STATUS` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务状态',
                                      `RETRY_COUNT` int DEFAULT NULL COMMENT '重试次数',
                                      `MAX_RETRY` int DEFAULT NULL COMMENT '最大重试次数',
                                      `LAST_ERROR` longtext COLLATE utf8mb4_unicode_ci COMMENT '最后错误信息',
                                      `PAYLOAD_JSON` longtext COLLATE utf8mb4_unicode_ci COMMENT '负载JSON',
                                      `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
                                      `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
                                      `CREATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                      `UPDATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                                      `DELETED` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
                                      `ENV_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境标识',
                                      PRIMARY KEY (`ID`),
                                      KEY `IDX_INSTANCE_ID` (`INSTANCE_ID`),
                                      KEY `IDX_NODE_ID` (`NODE_ID`),
                                      KEY `IDX_JOB_STATUS` (`JOB_STATUS`),
                                      KEY `IDX_DUE_TIME_MS` (`DUE_TIME_MS`),
                                      KEY `IDX_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='定时任务表';


-- astro_ai.ast_file_record definition

CREATE TABLE IF NOT EXISTS `ast_file_record` (
                                   `ID` bigint NOT NULL COMMENT '主键ID',
                                   `BIZ_TYPE` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务类型',
                                   `BIZ_ID` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务ID',
                                   `PLATFORM` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '存储平台',
                                   `BUCKET` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '存储桶',
                                   `OBJECT_KEY` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '对象Key',
                                   `ORIGIN_NAME` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '原始文件名',
                                   `MIME_TYPE` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'MIME类型',
                                   `FILE_SIZE` bigint DEFAULT NULL COMMENT '文件大小(字节)',
                                   `ETAG` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件ETag',
                                   `STATUS` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件状态',
                                   `FILE_URL` varchar(1024) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件访问URL',
                                   `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `CREATE_USER` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
                                   `UPDATE_USER` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
                                   `DELETED` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标记(0-未删除,1-已删除)',
                                   `ENV_CODE` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '环境编码',
                                   PRIMARY KEY (`ID`),
                                   KEY `IDX_AFR_BIZ` (`BIZ_TYPE`,`BIZ_ID`),
                                   KEY `IDX_AFR_OBJECT_KEY` (`OBJECT_KEY`),
                                   KEY `IDX_AFR_CREATE_TIME` (`CREATE_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文件记录表';


-- astro_ai.sys_config definition

CREATE TABLE IF NOT EXISTS `sys_config` (
                              `ID` bigint NOT NULL COMMENT '主键ID',
                              `CONFIG_KEY` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置项唯一键 (如: email.smtp.host)',
                              `CONFIG_VALUE` text COLLATE utf8mb4_unicode_ci COMMENT '配置内容',
                              `CONFIG_GROUP` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置分组 (如: EMAIL, SYSTEM, STORAGE)',
                              `DESCRIPTION` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配置项描述',
                              `IS_SYSTEM` tinyint(1) DEFAULT '0' COMMENT '是否系统内置: 0-否, 1-是',
                              `STATUS` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ENABLED' COMMENT '状态: ENABLED-启用, DISABLED-禁用',
                              `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `CREATE_USER` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                              `UPDATE_USER` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                              `DELETED` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
                              `ENV_CODE` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境编码',
                              PRIMARY KEY (`ID`),
                              UNIQUE KEY `UK_CONFIG_ENV` (`CONFIG_KEY`,`ENV_CODE`) COMMENT '同一环境下KEY唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI系统动态参数配置表';


-- astro_ai.sys_env definition

CREATE TABLE IF NOT EXISTS `sys_env` (
                           `ID` bigint NOT NULL,
                           `ENV_NAME` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `DESCRIPTION` text COLLATE utf8mb4_unicode_ci,
                           `CREATE_TIME` timestamp NULL DEFAULT NULL,
                           `UPDATE_TIME` timestamp NULL DEFAULT NULL,
                           `CREATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `UPDATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `DELETED` tinyint(1) DEFAULT '0',
                           `ENV_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `ENV_KEY` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           PRIMARY KEY (`ID`),
                           KEY `IDX_SYSTEM_ENV_ENV_CODE` (`ENV_CODE`),
                           KEY `IDX_SYSTEM_ENV_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- astro_ai.sys_extension definition

CREATE TABLE IF NOT EXISTS `sys_extension` (
                                 `ID` bigint NOT NULL,
                                 `CREATE_TIME` timestamp NULL DEFAULT NULL,
                                 `UPDATE_TIME` timestamp NULL DEFAULT NULL,
                                 `CREATE_USER` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `UPDATE_USER` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `DELETED` tinyint(1) DEFAULT '0',
                                 `ENV_CODE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `STATUS` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `EXTENSION_KEY` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '唯一编码',
                                 `EXTENSION_NAME` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `EXTENSION_CODE` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '可复用编码',
                                 `TYPE` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `VERSION` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `AUTHOR` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `DESCRIPTION` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `JAR_NAME` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `APPLIED` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `AVATAR` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                                 `DISCOVERY_MECHANISM` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `INSTALL_SOURCE` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `CHANGELOG` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `MIN_SERVER_VERSION` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- astro_ai.sys_message definition

CREATE TABLE IF NOT EXISTS `sys_message` (
                               `ID` bigint NOT NULL COMMENT '主键ID',
                               `MESSAGE_TYPE` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息类型: PLUGIN_INSTALLED/DEPLOYMENT_ONLINE/API_CALL_FAILED...',
                               `MESSAGE_LEVEL` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息级别: INFO/SUCCESS/WARN/ERROR',
                               `READ_STATUS` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'UNREAD' COMMENT '阅读状态: UNREAD/READ',
                               `TITLE` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息标题',
                               `CONTENT` longtext COLLATE utf8mb4_unicode_ci COMMENT '消息正文，可存文本或JSON',
                               `REF_TYPE` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联类型: EXTENSION/AI_INSTANCE/AI_AGENT/AI_CONVERSATION/AI_MCP/SYSTEM',
                               `REF_ID` bigint DEFAULT NULL COMMENT '关联业务ID',
                               `REF_KEY` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联业务KEY(如 traceId/extensionKey)',
                               `SOURCE` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源(模块/插件/服务名)',
                               `ERROR_CODE` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '错误码/第三方状态码',
                               `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `UPDATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `CREATE_USER` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                               `UPDATE_USER` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                               `DELETED` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除: 0否 1是',
                               `ENV_CODE` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境编码',
                               PRIMARY KEY (`ID`),
                               KEY `IDX_SYS_MSG_ENV_TIME` (`ENV_CODE`,`CREATE_TIME`),
                               KEY `IDX_SYS_MSG_TYPE_LEVEL` (`MESSAGE_TYPE`,`MESSAGE_LEVEL`),
                               KEY `IDX_SYS_MSG_READ_STATUS` (`READ_STATUS`),
                               KEY `IDX_SYS_MSG_REF` (`REF_TYPE`,`REF_ID`),
                               KEY `IDX_SYS_MSG_REF_KEY` (`REF_KEY`),
                               KEY `IDX_SYS_MSG_SOURCE` (`SOURCE`),
                               KEY `IDX_SYS_MSG_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统消息/通知表';


-- astro_ai.sys_user definition

CREATE TABLE IF NOT EXISTS `sys_user` (
                            `ID` bigint NOT NULL,
                            `USERNAME` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `PASSWORD` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `ADMIN_FLAG` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `USER_ROLE` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'SUPER_ADMIN/ENV_ADMIN/USER',
                            `EMAIL` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `CREATE_TIME` timestamp NULL DEFAULT NULL,
                            `UPDATE_TIME` timestamp NULL DEFAULT NULL,
                            `CREATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `UPDATE_USER` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `DELETED` tinyint(1) DEFAULT '0',
                            `ENV_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            PRIMARY KEY (`ID`),
                            KEY `IDX_SYSTEM_USER_USERNAME` (`USERNAME`),
                            KEY `IDX_SYSTEM_USER_EMAIL` (`EMAIL`),
                            KEY `IDX_SYSTEM_USER_DELETED` (`DELETED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;