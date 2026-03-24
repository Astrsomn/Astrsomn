package org.astrsomn.starter.config;


import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(MybatisPlusInterceptor.class)
public class AstrsomnEnvAutoConfiguration {



    @Bean
    @ConditionalOnMissingBean(TenantLineHandler.class)
    public TenantLineHandler astrsomnEnvCodeTenantHandler() {
        return new EnvCodeTenantHandler();
    }

    @Bean
    public BeanPostProcessor astrsomnTenantInterceptorInjector(TenantLineHandler handler) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof MybatisPlusInterceptor) {
                    MybatisPlusInterceptor interceptor = (MybatisPlusInterceptor) bean;

                    // 1. 检查是否已经存在租户拦截器，避免重复添加
                    boolean hasTenantInterceptor = interceptor.getInterceptors().stream()
                            .anyMatch(i -> i instanceof TenantLineInnerInterceptor);

                    if (!hasTenantInterceptor) {
                        // 2. 创建环境隔离拦截器
                        TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor();
                        tenantInterceptor.setTenantLineHandler(handler);

                        // 3. 重点：多租户拦截器通常需要放在插件链的最前面
                        // 将其插入到现有的拦截器列表头部
                        interceptor.addInnerInterceptor( tenantInterceptor);

                        if (handler instanceof EnvCodeTenantHandler) {
                            System.out.println(">>> [Astrsomn Starter] 环境隔离插件已激活");
                            System.out.println(">>> 作用表清单: [" + ((EnvCodeTenantHandler) handler).getPrivateTables() + "]");
                        }
                    }
                }
                return bean;
            }
        };
    }



}
