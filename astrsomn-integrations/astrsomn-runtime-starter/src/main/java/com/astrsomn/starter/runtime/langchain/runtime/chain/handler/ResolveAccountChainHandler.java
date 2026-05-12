package com.astrsomn.starter.runtime.langchain.runtime.chain.handler;

import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelSetting;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeChainHandler;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeContext;
import com.astrsomn.starter.runtime.langchain.runtime.chain.RuntimeChatParamMergeSupport;
import com.astrsomn.starter.runtime.mapper.AstAiAccountMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import com.astrsomn.api.runtime.common.entity.AiAccountEntity;
import com.astrsomn.common.utils.CryptoUtil;
import com.astrsomn.common.utils.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 根据模型上的账号 Key 加载凭证，合并进 {@link ModelSetting}。
 * 若模型未配置 {@code ACCOUNT_KEY} 则跳过，不中断链（由下游工厂决定是否报错）。
 */
@Component
@Order(60)
@RequiredArgsConstructor
public class ResolveAccountChainHandler implements AgentRuntimeChainHandler {

    private final AstAiAccountMapper astAiAccountMapper;

    @Override
    public void handle(AgentRuntimeContext ctx) {
        String accountKey = null;
        if (ctx.getInstance() != null) {
            accountKey = StringUtils.trimToNull(ctx.getInstance().getAccountKey());
        }
        if (accountKey == null) {
            return;
        }
        AiAccountEntity account = astAiAccountMapper.selectOne(
                new LambdaQueryWrapper<AiAccountEntity>()
                        .eq(AiAccountEntity::getAccountKey, accountKey)
                        .eq(AiAccountEntity::getEnvCode, ctx.getEnvCode())
                        .eq(AiAccountEntity::getDeleted, false)
                        .last("LIMIT 1"));
        if (account == null) {
            throw new IllegalStateException(
                    "未找到账号配置: accountKey=" + accountKey + ", envCode=" + ctx.getEnvCode());
        }
        // 解密 API Key 和 Secret
        if (account.getApiKey() != null) {
            account.setApiKey(CryptoUtil.decrypt(account.getApiKey()));
        }
        if (account.getApiSecret() != null) {
            account.setApiSecret(CryptoUtil.decrypt(account.getApiSecret()));
        }
        ctx.setAccount(account);
        RuntimeChatParamMergeSupport.mergeModelSettingFromAccount(ctx.getParam().getModelSetting(), account);
    }
}
