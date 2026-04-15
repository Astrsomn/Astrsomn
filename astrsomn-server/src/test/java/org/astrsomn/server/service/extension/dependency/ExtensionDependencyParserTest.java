package org.astrsomn.server.service.extension.dependency;

import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExtensionDependencyParserTest {

    private final ExtensionDependencyParser parser = new ExtensionDependencyParser();

    @Test
    void parseShouldExtractHardAndSoftDependencies() {
        ExtensionDependencyParseResult result =
                parser.parse("vector deps:hard=redis,mysql;soft=openai");

        Assertions.assertEquals(3, result.safeDependencies().size());
        Assertions.assertTrue(result.mechanisms().contains(SystemExtensionEnum.DiscoveryMechanismEnum.SPI));
        Assertions.assertTrue(result.mechanisms().contains(SystemExtensionEnum.DiscoveryMechanismEnum.DEPENDENCY));
        Assertions.assertEquals(SystemExtensionEnum.DependencyScopeEnum.HARD, result.safeDependencies().get(0).scope());
        Assertions.assertEquals(SystemExtensionEnum.DependencyScopeEnum.SOFT, result.safeDependencies().get(2).scope());
    }

    @Test
    void parseShouldReturnSpiOnlyWhenNoDependencyDeclaration() {
        ExtensionDependencyParseResult result = parser.parse("plain extension description");

        Assertions.assertTrue(result.safeDependencies().isEmpty());
        Assertions.assertEquals(1, result.mechanisms().size());
        Assertions.assertTrue(result.mechanisms().contains(SystemExtensionEnum.DiscoveryMechanismEnum.SPI));
    }
}

