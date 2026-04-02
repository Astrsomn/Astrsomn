package org.astrsomn.deepseek.autoconfigure;

import org.astrsomn.deepseek.DeepSeekExtensionDescriptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class DeepSeekProviderAutoConfiguration {

    @Bean
    public DeepSeekExtensionDescriptor deepSeekExtensionDescriptor() {
        return new DeepSeekExtensionDescriptor();
    }
}
