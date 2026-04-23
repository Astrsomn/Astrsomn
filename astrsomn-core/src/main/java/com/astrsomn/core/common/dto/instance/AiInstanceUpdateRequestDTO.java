package com.astrsomn.core.common.dto.instance;

import com.astrsomn.core.common.base.BaseEntity;
import com.astrsomn.core.common.entity.AiInstanceEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求；时间字段继承自 {@link AiInstanceEntity} /
 * {@link BaseEntity}，勿在此重复声明非 ISO-8601 的日期格式。
 */
@Data
public class AiInstanceUpdateRequestDTO extends AiInstanceCreateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;
}
