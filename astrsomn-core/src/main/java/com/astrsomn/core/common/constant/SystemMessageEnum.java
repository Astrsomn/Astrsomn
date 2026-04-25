package com.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.core.common.base.BaseEnum;

/**
 * 系统消息/站内通知相关枚举：插件安装、上下线、调用失败等见 {@link MessageTypeEnum}。
 */
public interface SystemMessageEnum {

    /**
     * 消息业务类型（与 {@code SYS_MESSAGE.MESSAGE_TYPE} 对应）
     */
    @Getter
    @AllArgsConstructor
    enum MessageTypeEnum implements BaseEnum {

        /** 插件安装成功 */
        PLUGIN_INSTALLED("PLUGIN_INSTALLED", "插件已安装"),
        /** 插件安装失败 */
        PLUGIN_INSTALL_FAILED("PLUGIN_INSTALL_FAILED", "插件安装失败"),
        /** 插件卸载 */
        PLUGIN_UNINSTALLED("PLUGIN_UNINSTALLED", "插件已卸载"),
        /** 服务/配置上线、发布等（含版本发布说明） */
        DEPLOYMENT_ONLINE("DEPLOYMENT_ONLINE", "上线通知"),
        /** 外部/内部接口、工具、MCP 等调用失败 */
        API_CALL_FAILED("API_CALL_FAILED", "调用失败"),
        /** 系统公告、运维通知等 */
        SYSTEM_NOTICE("SYSTEM_NOTICE", "系统通知"),
        /** 未归类扩展，详情见 CONTENT */
        OTHER("OTHER", "其他");

        private final String code;
        private final String desc;

        public static MessageTypeEnum fromCode(String code) {
            if (code == null || code.isBlank()) {
                return null;
            }
            String t = code.trim();
            for (MessageTypeEnum e : values()) {
                if (e.code.equalsIgnoreCase(t)) {
                    return e;
                }
            }
            return null;
        }
    }

    /**
     * 展示级别/告警等级（与 {@code SYS_MESSAGE.MESSAGE_LEVEL} 对应）
     */
    @Getter
    @AllArgsConstructor
    enum MessageLevelEnum implements BaseEnum {

        INFO("INFO", "信息"),
        SUCCESS("SUCCESS", "成功"),
        WARN("WARN", "警告"),
        ERROR("ERROR", "错误");

        private final String code;
        private final String desc;

        public static MessageLevelEnum fromCode(String code) {
            if (code == null || code.isBlank()) {
                return INFO;
            }
            String t = code.trim();
            for (MessageLevelEnum e : values()) {
                if (e.code.equalsIgnoreCase(t)) {
                    return e;
                }
            }
            return INFO;
        }
    }

    /**
     * 已读状态（与 {@code SYS_MESSAGE.READ_STATUS} 对应）
     */
    @Getter
    @AllArgsConstructor
    enum ReadStatusEnum implements BaseEnum {

        UNREAD("UNREAD", "未读"),
        READ("READ", "已读");

        private final String code;
        private final String desc;

        public static ReadStatusEnum fromCode(String code) {
            if (code == null || code.isBlank()) {
                return UNREAD;
            }
            String t = code.trim();
            for (ReadStatusEnum e : values()) {
                if (e.code.equalsIgnoreCase(t)) {
                    return e;
                }
            }
            return UNREAD;
        }
    }

    /**
     * 关联业务主体类型，用于解释 {@code REF_ID} / {@code REF_KEY}（与 {@code SYS_MESSAGE.REF_TYPE} 对应）
     */
    @Getter
    @AllArgsConstructor
    enum RefTypeEnum implements BaseEnum {

        /** 扩展/插件（如 SYS_EXTENSION） */
        EXTENSION("EXTENSION", "扩展/插件"),
        AI_INSTANCE("AI_INSTANCE", "模型实例"),
        AI_AGENT("AI_AGENT", "Agent"),
        AI_CONVERSATION("AI_CONVERSATION", "对话"),
        AI_MCP("AI_MCP", "MCP"),
        SYSTEM("SYSTEM", "系统"),
        OTHER("OTHER", "其他");

        private final String code;
        private final String desc;

        public static RefTypeEnum fromCode(String code) {
            if (code == null || code.isBlank()) {
                return null;
            }
            String t = code.trim();
            for (RefTypeEnum e : values()) {
                if (e.code.equalsIgnoreCase(t)) {
                    return e;
                }
            }
            return null;
        }
    }
}
