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

    private String extensionKey;
    private String extensionName;
    private String type;
    private String version;
    private String author;
    private String description;

    private String jarName;


    private String extensionCode;


    private String avatar;
}