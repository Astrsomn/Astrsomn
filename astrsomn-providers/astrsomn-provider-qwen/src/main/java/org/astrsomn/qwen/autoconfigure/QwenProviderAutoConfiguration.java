package org.astrsomn.qwen.autoconfigure;

import org.astrsomn.qwen.QwenExtensionDescriptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class QwenProviderAutoConfiguration {

    @Bean
    public QwenExtensionDescriptor qwenExtensionDescriptor() {
        return new QwenExtensionDescriptor();
    }
}
