package com.astrsomn.api.runtime.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.BaseEnum;


public interface SystemMessageEnum {

    
    @Getter
    @AllArgsConstructor
    enum MessageTypeEnum implements BaseEnum {

        
        PLUGIN_INSTALLED("PLUGIN_INSTALLED", "插件已安装"),
        
        PLUGIN_INSTALL_FAILED("PLUGIN_INSTALL_FAILED", "插件安装失败"),
        
        PLUGIN_UNINSTALLED("PLUGIN_UNINSTALLED", "插件已卸载"),
        
        DEPLOYMENT_ONLINE("DEPLOYMENT_ONLINE", "上线通知"),
        
        API_CALL_FAILED("API_CALL_FAILED", "调用失败"),
        
        SYSTEM_NOTICE("SYSTEM_NOTICE", "系统通知"),
        
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

    
    @Getter
    @AllArgsConstructor
    enum RefTypeEnum implements BaseEnum {

        
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