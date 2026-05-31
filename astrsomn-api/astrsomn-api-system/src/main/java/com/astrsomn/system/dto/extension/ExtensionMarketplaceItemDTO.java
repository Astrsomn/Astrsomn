package com.astrsomn.system.dto.extension;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtensionMarketplaceItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 对应 marketplace PluginInfoEntity.pluginId，与本地 SYS_EXTENSION.extension_key 同值 */
    private String pluginId;

    private String extensionName;
    private String type;
    private String version;
    private String latestVersion;
    private String author;
    private String description;

    private String jarName;

    /** 对应 marketplace PluginInfoEntity.providerCode，与本地 extension_code 同值 */
    private String providerCode;

    private String avatar;

    // ---- 以下字段由 Astrsomn 本地补充，不从 marketplace 返回 ----

    private boolean installed;

    private String installedVersion;

    private boolean upgradeAvailable;
}