package com.astrsomn.api.runtime.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.BaseEnum;

public interface SystemUserEnum {

    /**
     * 用户角色：超级管理员（全局）、环境管理员（本环境配置）、普通用户。
     */
    @Getter
    @AllArgsConstructor
    enum UserRoleEnum implements BaseEnum {

        SUPER_ADMIN("SUPER_ADMIN", "超级管理员"),
        ENV_ADMIN("ENV_ADMIN", "环境管理员"),
        USER("USER", "普通用户");

        private final String code;
        private final String desc;

        public static UserRoleEnum fromCode(String code) {
            if (code == null || code.isBlank()) {
                return USER;
            }
            for (UserRoleEnum e : values()) {
                if (e.code.equalsIgnoreCase(code.trim())) {
                    return e;
                }
            }
            return USER;
        }

        /** 是否可访问 AI 配置类接口（含超级与环境管理员） */
        public static boolean canManageAiConfig(String code) {
            UserRoleEnum r = fromCode(code);
            return r == SUPER_ADMIN || r == ENV_ADMIN;
        }

        /** 是否可访问用户/环境主数据管理 */
        public static boolean canManagePlatformUsers(String code) {
            return fromCode(code) == SUPER_ADMIN;
        }
    }

    @Getter
    @AllArgsConstructor
    enum AdminEnum implements BaseEnum {

        YES("Y", "Yes"),
        NO("N", "No");

        private String code;

        private String desc;
    }
}
