package com.astrsomn.core.common.constant;

import com.astrsomn.commn.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI Chat 领域字典枚举。
 */
public interface AiChatEnum {

    @Getter
    @AllArgsConstructor
    enum SessionStatusEnum implements BaseEnum {
        ACTIVE("active", "Active"),
        ARCHIVED("archived", "Archived"),
        DELETED("deleted", "Deleted");

        private final String code;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    enum RoleEnum implements BaseEnum {
        USER("user", "User"),
        ASSISTANT("assistant", "Assistant"),
        SYSTEM("system", "System"),
        TOOL("tool", "Tool");

        private final String code;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    enum MessageTypeEnum implements BaseEnum {
        TEXT("text", "Text"),
        REASONING("reasoning", "Reasoning"),
        TOOL_CALL("tool_call", "Tool Call"),
        TOOL_RESULT("tool_result", "Tool Result"),
        ERROR("error", "Error");

        private final String code;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    enum ResponseStatusEnum implements BaseEnum {
        STREAMING("streaming", "Streaming"),
        COMPLETED("completed", "Completed"),
        FAILED("failed", "Failed"),
        INTERRUPTED("interrupted", "Interrupted");

        private final String code;
        private final String desc;
    }
}
