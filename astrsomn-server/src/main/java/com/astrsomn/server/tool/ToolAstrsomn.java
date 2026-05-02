package com.astrsomn.server.tool;

import com.astrsomn.api.runtime.common.entity.SystemUserEntity;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.api.runtime.common.constant.SystemUserEnum;
import com.astrsomn.server.service.AiAccountService;
import com.astrsomn.server.service.AiPromptService;
import com.astrsomn.server.service.SystemUserService;
import com.astrsomn.starter.runtime.langchain.aop.annotation.AstroToolGroup;
import org.springframework.stereotype.Component;

@AstroToolGroup(value = "toolAstrsomn", description = "系统查询工具组")
@Component
@RequiredArgsConstructor
public class ToolAstrsomn {

    private final SystemUserService systemUserService;
    private final AiPromptService aiPromptService;
    private final AiAccountService aiAccountService;

    @Tool("查询系统核心数据统计（用户数、管理员数、Prompt数、AI账号数）")
    public String querySystemOverview() {
        long totalUsers = systemUserService.count();
        long adminUsers = systemUserService.lambdaQuery()
                .ne(SystemUserEntity::getUserRole, SystemUserEnum.UserRoleEnum.USER.getCode())
                .count();
        long totalPrompts = aiPromptService.count();
        long totalAiAccounts = aiAccountService.count();
        return "系统统计结果：" +
                "总用户数=" + totalUsers +
                "，管理员数=" + adminUsers +
                "，Prompt数=" + totalPrompts +
                "，AI账号数=" + totalAiAccounts;
    }

    @Tool("按角色查询用户数量（支持：SUPER_ADMIN、ENV_ADMIN、USER）")
    public String queryUserCountByRole(String roleCode) {
        if (StringUtils.isBlank(roleCode)) {
            return "角色编码不能为空，可选值：SUPER_ADMIN、ENV_ADMIN、USER";
        }
        String normalizedRoleCode = roleCode.trim().toUpperCase();
        SystemUserEnum.UserRoleEnum roleEnum = SystemUserEnum.UserRoleEnum.fromCode(normalizedRoleCode);
        long count = systemUserService.lambdaQuery()
                .eq(SystemUserEntity::getUserRole, roleEnum.getCode())
                .count();
        return "角色 " + roleEnum.getCode() + " 的用户数量为: " + count;
    }

    @Tool("按用户名检查系统用户是否存在")
    public String checkUserExists(String username) {
        if (StringUtils.isBlank(username)) {
            return "用户名不能为空";
        }
        long count = systemUserService.lambdaQuery()
                .eq(SystemUserEntity::getUsername, username.trim())
                .count();
        return count > 0
                ? "用户存在: " + username.trim()
                : "用户不存在: " + username.trim();
    }
}
