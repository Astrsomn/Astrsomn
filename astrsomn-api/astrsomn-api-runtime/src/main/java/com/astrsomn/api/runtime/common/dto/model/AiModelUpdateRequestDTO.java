package com.astrsomn.api.runtime.common.dto.model;

import com.astrsomn.common.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求；时间字段继承自 {@link AiModelEntity}/{@link BaseEntity}，
 * 勿在此重复声明并套用 {@code yyyy-MM-dd HH:mm:ss}，否则与前端 ISO-8601（{@code T} 分隔）不一致导致反序列化失败。
 */
@Data
public class AiModelUpdateRequestDTO extends AiModelCreateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;
}
