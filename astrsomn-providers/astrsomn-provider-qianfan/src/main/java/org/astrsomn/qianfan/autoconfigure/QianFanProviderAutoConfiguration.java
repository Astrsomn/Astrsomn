package org.astrsomn.qianfan.autoconfigure;

import org.astrsomn.qianfan.QianFanExtensionDescriptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class QianFanProviderAutoConfiguration {

    @Bean
    public QianFanExtensionDescriptor qianFanExtensionDescriptor() {
        return new QianFanExtensionDescriptor();
    }
}
