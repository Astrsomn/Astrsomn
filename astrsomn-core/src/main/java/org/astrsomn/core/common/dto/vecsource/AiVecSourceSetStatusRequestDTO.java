package org.astrsomn.core.common.dto.vecsource;

import lombok.Data;

/**
 * 向量源启用 / 禁用（更新状态并同步运行时连接）。
 */
@Data
public class AiVecSourceSetStatusRequestDTO {

    private Long id;

    /** {@code true} 启用并注册运行时连接；{@code false} 禁用并释放连接。 */
    private Boolean enabled;
}
