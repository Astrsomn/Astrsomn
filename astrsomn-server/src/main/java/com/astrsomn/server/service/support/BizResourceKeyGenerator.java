package com.astrsomn.server.service.support;

import com.astrsomn.commn.base.BaseEntity;
import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.starter.config.AstrsomnProperties;
import com.astrsomn.starter.context.EnvRuntime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 业务资源 key 统一生成器
 * <p>
 * 支持自动根据环境编码生成唯一的业务键。
 */
@Component
@RequiredArgsConstructor
public class BizResourceKeyGenerator {

    public static final int MAX_KEY_LEN = 120;
    private static final int MAX_SLUG_LEN = 48;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ApplicationContext applicationContext;
    private final AstrsomnProperties astrsomnProperties;

    /**
     * 生成基于日期的唯一业务键：业务代码+yyyyMMdd+001
     */
    public String generateDateBasedBizKey(BizKeyNamespace namespace, KeyOccurrenceCounter counter) {
        String shortCode = namespace.getShortCode();
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        
        for (int i = 1; i < 1000; i++) {
            String sequence = String.format("%03d", i);
            String candidate = shortCode + dateStr + sequence;
            if (counter.count(candidate) == 0) {
                return candidate;
            }
        }
        return shortCode + dateStr + "999";
    }

    /**
     * 通用发号方法：传入枚举，自动生成唯一业务键
     * 
     * @param namespace 业务类型枚举
     * @param entity    实体对象（会自动填充 envCode）
     * @param <T>       实体类型
     * @return 生成的唯一业务键
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity<?>> String generateKey(BizKeyNamespace namespace, T entity) {
        fillEnv(entity);
        
        String envCode = entity.getEnvCode();
        String keyFieldName = namespace.getKeyFieldName();
        
        BaseMapper<T> mapper = (BaseMapper<T>) applicationContext.getBean(namespace.getMapperClass());
        
        return generateDateBasedBizKey(namespace, candidate -> {
            LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BaseEntity::getEnvCode, envCode);
            wrapper.apply("`" + keyFieldName + "` = {0}", candidate);
            return mapper.selectCount(wrapper);
        });
    }

    /**
     * 通用发号方法（更新场景）：传入枚举，自动生成唯一业务键
     * 
     * @param namespace 业务类型枚举
     * @param entity    实体对象
     * @param excludeId 更新时排除的 ID（避免把自己算作已占用）
     * @param <T>       实体类型
     * @return 生成的唯一业务键
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity<?>> String generateKey(BizKeyNamespace namespace, T entity, Long excludeId) {
        fillEnv(entity);
        
        String envCode = entity.getEnvCode();
        String keyFieldName = namespace.getKeyFieldName();
        
        BaseMapper<T> mapper = (BaseMapper<T>) applicationContext.getBean(namespace.getMapperClass());
        
        return generateDateBasedBizKey(namespace, candidate -> {
            LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BaseEntity::getEnvCode, envCode);
            wrapper.apply("`" + keyFieldName + "` = {0}", candidate);
            if (excludeId != null) {
                wrapper.ne(BaseEntity::getId, excludeId);
            }
            return mapper.selectCount(wrapper);
        });
    }

    /**
     * 设置实体的环境编码（如果为空）
     */
    private void fillEnv(BaseEntity<?> entity) {
        if (StringUtils.isBlank(entity.getEnvCode())) {
            entity.setEnvCode(EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties));
        }
    }

    @FunctionalInterface
    public interface KeyOccurrenceCounter {
        long count(String candidateKey);
    }
}
