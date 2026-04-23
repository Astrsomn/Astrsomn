package com.astrsomn.core.common.dto.extension;

import com.astrsomn.core.common.constant.AiModelEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 插件市场目录项（可来自 Mock 或后续真实 Provider 聚合）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtensionMarketplaceItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String extensionKey;
    private String extensionName;
    private String type;
    private String version;
    private String author;
    private String description;
    /** 应用插件包时可选 */
    private String jarName;

    /**
     * 公用厂商 code；模型类扩展时与 {@link AiModelEnum.ProviderEnum#getCode()} 一致。
     */
    private String providerCode;

    /** SVG 等展示用头像（可选） */
    private String avatar;
}
