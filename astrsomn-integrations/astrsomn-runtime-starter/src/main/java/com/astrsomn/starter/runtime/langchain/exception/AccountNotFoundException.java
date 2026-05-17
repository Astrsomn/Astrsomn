package com.astrsomn.starter.runtime.langchain.exception;

/**
 * 按 accountKey 未找到账号配置。
 */
public class AccountNotFoundException extends AstroConfigException {

    public AccountNotFoundException(String accountKey, String envCode) {
        super(ErrorCode.ACCOUNT_NOT_FOUND, "accountKey=" + accountKey + ", envCode=" + envCode,
                context().put("accountKey", accountKey).put("envCode", envCode).build());
    }
}
