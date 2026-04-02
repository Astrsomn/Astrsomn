package org.astrsomn.zhipu.autoconfigure;

import org.astrsomn.zhipu.ZhipuExtensionDescriptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ZhipuProviderAutoConfiguration {

    @Bean
    public ZhipuExtensionDescriptor zhipuExtensionDescriptor() {
        return new ZhipuExtensionDescriptor();
    }
}
