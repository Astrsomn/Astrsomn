package com.astrsomn.api.runtime.common.constant;

import com.astrsomn.common.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;


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
        ERROR("error", "Error"),

        HTML("html", "Html"),

        IMAGE("image", "Image");

        private final String code;
        private final String desc;

        public static MessageTypeEnum fromCode(String code) {
            if (code == null) {
                return null;
            }
            for (MessageTypeEnum e : values()) {
                if (e.code.equals(code)) {
                    return e;
                }
            }
            return null;
        }
    }

    @Getter
    @AllArgsConstructor
    enum FinishReasonEnum implements BaseEnum {
        STOP("stop", "Stop"),
        LENGTH("length", "Length"),
        TOOL_CALL("tool_call", "Tool Call"),
        CONTENT_FILTER("content_filter", "Content Filter"),
        ERROR("error", "Error"),
        CANCELLED("cancelled", "Cancelled");

        private final String code;
        private final String desc;

        public static FinishReasonEnum fromCode(String code) {
            if (code == null) {
                return null;
            }
            for (FinishReasonEnum e : values()) {
                if (e.code.equals(code)) {
                    return e;
                }
            }
            return null;
        }
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


    @Getter
    @AllArgsConstructor
    enum MessagePartKindEnum implements BaseEnum {
        TEXT("text", "正文"),
        THOUGHT("thought", "思考"),
        HTML("html", "Html"),
        IMAGE("image", "图像"),
        ERROR("error", "错误"),
        TOOL_CALL("tool_call", "工具调用"),
        TOOL_RESULT("tool_result", "工具结果");

        private final String code;
        private final String desc;

        public static MessagePartKindEnum fromCode(String code) {
            if (code == null) {
                return null;
            }
            for (MessagePartKindEnum e : values()) {
                if (e.code.equals(code)) {
                    return e;
                }
            }
            return null;
        }
    }


    @Getter
    @AllArgsConstructor
    enum AttachmentKindEnum implements BaseEnum {
        IMAGE("image", "图片"),
        FILE("file", "文件"),
        AUDIO("audio", "音频"),
        VIDEO("video", "视频"),
        OTHER("other", "其他");

        private final String code;
        private final String desc;

        public static AttachmentKindEnum fromCode(String code) {
            if (code == null) {
                return null;
            }
            for (AttachmentKindEnum e : values()) {
                if (e.code.equals(code)) {
                    return e;
                }
            }
            return null;
        }
    }


    @Getter
    @AllArgsConstructor
    enum MessageLineageEnum implements BaseEnum {

        USER_TURN_ROOT("user_turn_root", "用户轮次根"),

        ASSISTANT_SEGMENT("assistant_segment", "助手片段"),

        TOOL_CALL("tool_call", "工具调用"),

        TOOL_RESULT("tool_result", "工具结果"),

        SYSTEM("system", "系统");

        private final String code;
        private final String desc;

        public static MessageLineageEnum fromCode(String code) {
            if (code == null) {
                return null;
            }
            for (MessageLineageEnum e : values()) {
                if (e.code.equals(code)) {
                    return e;
                }
            }
            return null;
        }
    }
}