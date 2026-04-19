package org.astrsomn.starter.langchain.runtime.chain;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.entity.AiAccountEntity;
import org.astrsomn.core.common.util.CryptoUtil;
import org.astrsomn.core.common.utils.StringUtils;
import org.astrsomn.core.mapper.AiAccountMapper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 根据模型上的账号 Key 加载凭证，合并进 {@link org.astrsomn.core.common.langchain.buildParam.setting.ModelSetting}。
 * 若模型未配置 {@code ACCOUNT_KEY} 则跳过，不中断链（由下游工厂决定是否报错）。
 */
@Component
@Order(60)
@RequiredArgsConstructor
public class ResolveAccountChainHandler implements AgentRuntimeChainHandler {

    private final AiAccountMapper aiAccountMapper;

    @Override
    public void handle(AgentRuntimeContext ctx) {
        String accountKey = null;
        if (ctx.getModel() != null) {
            accountKey = StringUtils.trimToNull(ctx.getModel().getAccountKey());
        }
        if (accountKey == null) {
            return;
        }
        AiAccountEntity account = aiAccountMapper.selectOne(
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
