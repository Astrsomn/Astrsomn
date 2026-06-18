package com.astrsomn.vector.redis.service;

import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecSource;
import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecStore;
import com.astrsomn.api.runtime.common.langchain.extension.vector.support.AiVecSourceConnectionProperties;
import com.astrsomn.api.vector.entity.AiVecSourceEntity;
import com.astrsomn.api.vector.entity.AiVecStoreEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.vector.redis.internal.RedisConfigSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

public final class RedisVecSourceHandler extends AbstractVecSource {

    private static final Logger log = LoggerFactory.getLogger(RedisVecSourceHandler.class);

    private static final int DEFAULT_PORT = 6379;
    private static final int TIMEOUT_MS = 10_000;

    private final AiVecSourceConnectionProperties connectionProperties;
    private final boolean useSsl;
    private final String indexType;
    private final JedisPool jedisPool;

    public RedisVecSourceHandler(AiVecSourceEntity entity) {
        super(entity);
        log.info("[Redis] constructor: start");

        this.connectionProperties = AiVecSourceConnectionProperties.from(entity);
        this.useSsl = RedisConfigSupport.readUseSsl(entity.getConfigJson());
        this.indexType = RedisConfigSupport.readIndexType(entity.getConfigJson());

        String resolvedHost = connectionProperties.resolvedHost();
        int resolvedPort = connectionProperties.resolvedPort(DEFAULT_PORT);
        String password = connectionProperties.getPassword();
        String user = connectionProperties.getUsername();

        log.info("[Redis] constructor: resolved endpoint host={}, port={}, ssl={}, userConfigured={}",
                resolvedHost, resolvedPort, useSsl, StringUtils.isNotBlank(user));

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(2);
        poolConfig.setMaxIdle(1);
        poolConfig.setMinIdle(0);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMaxWait(Duration.ofSeconds(5));

        if (useSsl) {
            if (StringUtils.isNotBlank(password)) {
                this.jedisPool = new JedisPool(poolConfig, resolvedHost, resolvedPort, TIMEOUT_MS, password, useSsl);
            } else {
                this.jedisPool = new JedisPool(poolConfig, resolvedHost, resolvedPort, TIMEOUT_MS, null, useSsl);
            }
        } else if (StringUtils.isNotBlank(password)) {
            if (StringUtils.isNotBlank(user)) {
                this.jedisPool = new JedisPool(poolConfig, resolvedHost, resolvedPort, TIMEOUT_MS, user, password);
            } else {
                this.jedisPool = new JedisPool(poolConfig, resolvedHost, resolvedPort, TIMEOUT_MS, password);
            }
        } else {
            this.jedisPool = new JedisPool(poolConfig, resolvedHost, resolvedPort, TIMEOUT_MS);
        }

        log.info("[Redis] JedisPool created: host={}, port={}", resolvedHost, resolvedPort);
    }

    AiVecSourceConnectionProperties connectionProperties() {
        return connectionProperties;
    }

    boolean useSsl() {
        return useSsl;
    }

    String indexType() {
        return indexType;
    }

    JedisPool jedisPool() {
        return jedisPool;
    }

    /**
     * Obtain a Jedis resource from the pool, execute the callback, and return the resource.
     */
    <T> T withJedis(JedisCallback<T> callback) {
        try (Jedis jedis = jedisPool.getResource()) {
            return callback.execute(jedis);
        }
    }

    @Override
    public boolean testConnection() {
        long t0 = System.nanoTime();
        log.info("[Redis] testConnection: PING");
        try (Jedis jedis = jedisPool.getResource()) {
            String pong = jedis.ping();
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;
            log.info("[Redis] testConnection success in {}ms: {}", elapsedMs, pong);
            return true;
        } catch (Exception e) {
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;
            log.error("[Redis] testConnection failed after {}ms", elapsedMs, e);
            throw new IllegalStateException("Redis testConnection failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void shutdown() {
        log.info("[Redis] shutdown: closing JedisPool");
        jedisPool.close();
    }

    @Override
    public AbstractVecStore openStore(AiVecStoreEntity store) {
        return new RedisVecStoreHandler(this, store);
    }

    @FunctionalInterface
    interface JedisCallback<T> {
        T execute(Jedis jedis);
    }
}
