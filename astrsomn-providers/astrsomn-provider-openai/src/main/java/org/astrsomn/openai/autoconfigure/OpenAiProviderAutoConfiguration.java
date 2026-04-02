package org.astrsomn.openai.autoconfigure;

import org.astrsomn.openai.OpenAiExtensionDescriptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class OpenAiProviderAutoConfiguration {

    @Bean
    public OpenAiExtensionDescriptor openAiExtensionDescriptor() {
        return new OpenAiExtensionDescriptor();
    }
}
