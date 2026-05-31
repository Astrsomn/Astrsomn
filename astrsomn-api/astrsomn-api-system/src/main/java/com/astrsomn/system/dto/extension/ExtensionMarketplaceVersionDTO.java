package com.astrsomn.system.dto.extension;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtensionMarketplaceVersionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String pluginId;
    private String version;
    private String downloadUrl;
    private String resolvedDownloadUrl;
    private String changelog;
    private String minServerVersion;
    private String status;
    private Integer downloadCount;
}
