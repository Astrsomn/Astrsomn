package com.astrsomn.api.runtime.common.constant;

import com.astrsomn.common.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI Chat 领域字典枚举（会话、消息类型、片段、附件、谱系等统一入口）。
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
        ERROR("error", "Error"),
        /** 富文本片段（与流式 html 事件一致，便于前端按块渲染）。 */
        HTML("html", "Html"),
        /** 图像片段（与流式 image 事件一致）。 */
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

    /**
     * 单条库表记录对应的「展示/流式片段」类型，与 SSE {@code type} 及前端 segment 对齐。
     */
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

    /**
     * 用户多模态输入等场景下的附件类型（写入 {@code EXT_JSON.attachments[]}）。
     */
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

    /**
     * 一条记录在回合内的语义位置，用于复原「用户根消息 → 助手子片段 / 工具链」；写入 {@code EXT_JSON.lineage}。
     */
    @Getter
    @AllArgsConstructor
    enum MessageLineageEnum implements BaseEnum {
        /** 当前轮次用户侧根消息（通常 MESSAGE_ORDER 最小且 ROLE=user）。 */
        USER_TURN_ROOT("user_turn_root", "用户轮次根"),
        /** 助手侧连续输出中的一段（思考、正文、html、image 等）。 */
        ASSISTANT_SEGMENT("assistant_segment", "助手片段"),
        /** 模型发起的工具调用元数据行。 */
        TOOL_CALL("tool_call", "工具调用"),
        /** 工具执行结果行（ROLE 多为 tool）。 */
        TOOL_RESULT("tool_result", "工具结果"),
        /** 系统或管线提示（可选）。 */
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
