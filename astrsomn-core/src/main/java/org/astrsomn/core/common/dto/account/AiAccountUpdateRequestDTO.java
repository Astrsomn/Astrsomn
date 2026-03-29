package org.astrsomn.core.common.dto.account;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求；时间字段继承自 {@link org.astrsomn.core.common.entity.AiAccountEntity} /
 * {@link org.astrsomn.core.common.base.BaseEntity}，勿在此重复声明非 ISO-8601 的日期格式。
 */
@Data
public class AiAccountUpdateRequestDTO extends AiAccountCreateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;
}
