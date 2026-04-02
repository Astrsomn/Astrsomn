package org.astrsomn.core.common.dto.account;

import lombok.Data;
import org.astrsomn.core.common.entity.AiAccountEntity;

@Data
public class AiAccountResponseDTO extends AiAccountEntity {

    /**
     * 是否有 AI 模型在同环境下引用该账号的 accountKey；为 true 时前端应禁止修改 Account Key。
     */
    private Boolean accountKeyImmutable;

    /**
     * 同环境下使用该 accountKey 的模型数量。
     */
    private Long usedModelCount;

    /**
     * 同环境下使用该 accountKey 的模型 Key 列表（逗号分隔）。
     */
    private String usedModelKeys;

    /**
     * 同环境下使用该 accountKey 的模型名称列表（逗号分隔）。
     */
    private String usedModelNames;
}
