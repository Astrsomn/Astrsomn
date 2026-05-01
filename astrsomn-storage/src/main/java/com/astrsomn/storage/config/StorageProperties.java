package com.astrsomn.storage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "astrsomn.storage")
public class StorageProperties {

    private String type = "local";
    private String defaultPlatform = "local-plus";
    private String basePath = "astrsomn/";
    private String vecDocBizType = "vec-doc";
    private boolean randomFilename = true;
}
