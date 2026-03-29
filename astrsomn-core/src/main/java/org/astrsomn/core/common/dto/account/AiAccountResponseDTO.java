package org.astrsomn.core.common.dto.account;

import lombok.Data;
import org.astrsomn.core.common.entity.AiAccountEntity;

@Data
public class AiAccountResponseDTO extends AiAccountEntity {

    /**
     * 是否有 AI 模型在同环境下引用该账号的 accountKey；为 true 时前端应禁止修改 Account Key。
     */
    private Boolean accountKeyImmutable;
}
