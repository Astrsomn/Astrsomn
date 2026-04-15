package org.astrsomn.server.service.extension.lifecycle;

import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.entity.SystemExtensionEntity;
import org.astrsomn.core.exception.base.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ExtensionStrategyResolverTest {

    @Test
    void resolveShouldReturnMatchingStrategy() {
        ExtensionLifecycleStrategy model = new TestStrategy(SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER);
        ExtensionLifecycleStrategy vector = new TestStrategy(SystemExtensionEnum.ExtensionTypeEnum.VECTOR_STORE);
        ExtensionStrategyResolver resolver = new ExtensionStrategyResolver(List.of(model, vector));

        ExtensionLifecycleStrategy selected = resolver.resolve(SystemExtensionEnum.ExtensionTypeEnum.VECTOR_STORE.getCode());

        Assertions.assertSame(vector, selected);
    }

    @Test
    void resolveShouldThrowWhenNoStrategyRegistered() {
        ExtensionLifecycleStrategy model = new TestStrategy(SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER);
        ExtensionStrategyResolver resolver = new ExtensionStrategyResolver(List.of(model));

        Assertions.assertThrows(BusinessException.class,
                () -> resolver.resolve(SystemExtensionEnum.ExtensionTypeEnum.MCP.getCode()));
    }

    private record TestStrategy(SystemExtensionEnum.ExtensionTypeEnum type) implements ExtensionLifecycleStrategy {
        @Override
        public SystemExtensionEnum.ExtensionTypeEnum supportType() {
            return type;
        }

        @Override
        public void apply(SystemExtensionEntity extension) {
        }

        @Override
        public void revoke(SystemExtensionEntity extension) {
        }

        @Override
        public void uninstall(SystemExtensionEntity extension) {
        }
    }
}

