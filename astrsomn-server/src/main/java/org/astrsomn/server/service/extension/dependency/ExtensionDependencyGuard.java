package org.astrsomn.server.service.extension.dependency;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.entity.SystemExtensionEntity;
import org.astrsomn.core.common.utils.StringUtils;
import org.astrsomn.core.exception.base.BusinessException;
import org.astrsomn.core.exception.constant.SystemExtensionErrorEnum;
import org.astrsomn.core.mapper.SystemExtensionMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExtensionDependencyGuard {

    private final SystemExtensionMapper systemExtensionMapper;
    private final ExtensionDependencyParser dependencyParser;

    public DependencyCheckResult assertDependencies(SystemExtensionEntity extension) {
        ExtensionDependencyParseResult parseResult = dependencyParser.parse(extension.getDescription());
        List<ExtensionDependencySpec> specs = parseResult.safeDependencies();
        if (specs.isEmpty()) {
            return new DependencyCheckResult(parseResult.mechanisms(), List.of());
        }

        List<String> warnings = new ArrayList<>();
        List<String> hardFailures = new ArrayList<>();
        for (ExtensionDependencySpec spec : specs) {
            String depKey = StringUtils.trimToNull(spec.dependencyKey());
            if (depKey == null) {
                continue;
            }
            SystemExtensionEntity dependency = systemExtensionMapper.selectOne(
                    new LambdaQueryWrapper<SystemExtensionEntity>()
                            .eq(SystemExtensionEntity::getExtensionKey, depKey)
                            .last("LIMIT 1"));
            boolean satisfied = dependency != null
                    && SystemExtensionEnum.ApplyStatusEnum.Y.getCode().equals(dependency.getApplied());
            if (satisfied) {
                continue;
            }
            String msg = "依赖扩展未就绪: key=" + depKey + "，要求=" + spec.scope().getCode() + "，需先安装并应用";
            if (SystemExtensionEnum.DependencyScopeEnum.HARD == spec.scope()) {
                hardFailures.add(msg);
            } else {
                warnings.add(msg);
            }
        }
        if (!hardFailures.isEmpty()) {
            throw new BusinessException(
                    SystemExtensionErrorEnum.EXTENSION_PERMISSION_DENIED,
                    String.join("; ", hardFailures));
        }
        for (String warning : warnings) {
            log.warn("[ExtensionDependency] {}", warning);
        }
        return new DependencyCheckResult(parseResult.mechanisms(), warnings);
    }

    public record DependencyCheckResult(
            java.util.Set<SystemExtensionEnum.DiscoveryMechanismEnum> mechanisms,
            List<String> warnings) {
    }
}

