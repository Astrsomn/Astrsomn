package org.astrsomn.server.service.support;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.mapper.AiModelMapper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.springframework.stereotype.Component;

/**
 * Model 创建时补全 {@code modelKey}，规则见 {@link BizResourceKeyGenerator#generateUniqueModelKey}。
 */
@Component
@RequiredArgsConstructor
public class AiModelKeyGenerator {

    private final AiModelMapper aiModelMapper;
    private final AstrsomnProperties astrsomnProperties;
    private final BizResourceKeyGenerator bizResourceKeyGenerator;

    /**
     * 若 {@code modelKey} 为空或纯空白则生成并写入 entity；否则仅 trim。
     */
    public void assignIfBlank(AiModelEntity entity) {
        String trimmed = StringUtils.trimToNull(entity.getModelKey());
        if (trimmed != null) {
            entity.setModelKey(trimmed);
            return;
        }
        String env = StringUtils.defaultIfBlank(entity.getEnvCode(), astrsomnProperties.getEnvCode());
        String user = StringUtils.defaultIfBlank(entity.getCreateUser(), "0");
        entity.setModelKey(
                bizResourceKeyGenerator.generateUniqueModelKey(
                        entity,
                        candidate ->
                                aiModelMapper.selectCount(
                                        new LambdaQueryWrapper<AiModelEntity>()
                                                .eq(AiModelEntity::getEnvCode, env)
                                                .eq(AiModelEntity::getCreateUser, user)
                                                .eq(AiModelEntity::getModelKey, candidate))));
    }
}
