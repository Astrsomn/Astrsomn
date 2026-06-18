package com.astrsomn.vector.redis.service;

import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecDoc;
import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecStore;
import com.astrsomn.api.vector.entity.AiVecDocEntity;
import com.astrsomn.api.vector.entity.AiVecStoreEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.vector.redis.internal.RedisEmbeddingStore;
import com.astrsomn.vector.redis.internal.RedisVecConstants;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.util.SafeEncoder;

import java.util.List;

public final class RedisVecStoreHandler extends AbstractVecStore {

    private static final Logger log = LoggerFactory.getLogger(RedisVecStoreHandler.class);

    private static final ProtocolCommand FT_CREATE_CMD = () -> SafeEncoder.encode("FT.CREATE");
    private static final ProtocolCommand FT_INFO_CMD = () -> SafeEncoder.encode("FT.INFO");
    private static final ProtocolCommand FT_DROP_CMD = () -> SafeEncoder.encode("FT.DROPINDEX");

    private final RedisVecSourceHandler redisSource;
    private volatile EmbeddingStore<TextSegment> embeddingStoreCache;

    public RedisVecStoreHandler(RedisVecSourceHandler source, AiVecStoreEntity entity) {
        super(source, entity);
        this.redisSource = source;
    }

    RedisVecSourceHandler redisSource() {
        return redisSource;
    }

    // ---------- distance metric mapping ----------

    private static String mapMetric(String metric) {
        if (StringUtils.isBlank(metric)) {
            return "COSINE";
        }
        switch (metric.trim().toLowerCase()) {
            case "cosine":
                return "COSINE";
            case "dot":
            case "dot_product":
            case "ip":
                return "IP";
            case "euclid":
            case "euclidean":
            case "l2":
                return "L2";
            default:
                return "COSINE";
        }
    }

    // ---------- FT.CREATE ----------

    @Override
    public void createCollection() {
        String name = collectionNameRequired();
        if (exists()) {
            return;
        }
        int dim = dimensionRequired();
        String metric = mapMetric(getEntity().getDistanceMetric());
        String algorithm = redisSource.indexType();

        log.info("[Redis] createCollection: index={}, dim={}, metric={}, algo={}", name, dim, metric, algorithm);
        redisSource.withJedis(jedis -> {
            jedis.getClient().sendCommand(FT_CREATE_CMD, SafeEncoder.encodeMany(
                    name,
                    "ON", "HASH",
                    "PREFIX", "1", RedisVecConstants.DEFAULT_INDEX_PREFIX,
                    "SCHEMA",
                    "embedding", "VECTOR", algorithm, "6",
                    "TYPE", "FLOAT32",
                    "DIM", String.valueOf(dim),
                    "DISTANCE_METRIC", metric,
                    RedisVecConstants.DEFAULT_PAYLOAD_TEXT_KEY, "TEXT", "WEIGHT", "1.0"
            ));
            String reply = jedis.getClient().getStatusCodeReply();
            log.info("[Redis] createCollection: FT.CREATE reply={}", reply);
            return null;
        });
    }

    // ---------- FT.DROPINDEX ----------

    @Override
    public void dropCollection() {
        String name = collectionNameRequired();
        log.info("[Redis] dropCollection: index={}", name);
        try {
            redisSource.withJedis(jedis -> {
                jedis.getClient().sendCommand(FT_DROP_CMD, SafeEncoder.encodeMany(name));
                String reply = jedis.getClient().getStatusCodeReply();
                log.info("[Redis] dropCollection: FT.DROPINDEX reply={}", reply);
                return null;
            });
        } catch (Exception e) {
            log.warn("[Redis] dropCollection: FT.DROPINDEX failed (may not exist): {}", e.getMessage());
        } finally {
            embeddingStoreCache = null;
        }
    }

    // ---------- FT.INFO / exists ----------

    @Override
    public boolean exists() {
        String name = collectionNameRequired();
        try {
            return redisSource.withJedis(jedis -> {
                jedis.getClient().sendCommand(FT_INFO_CMD, SafeEncoder.encodeMany(name));
                Object resp = jedis.getClient().getOne();
                return resp != null;
            });
        } catch (Exception e) {
            return false;
        }
    }

    // ---------- FT.INFO / count ----------

    @Override
    public long count() {
        String name = collectionNameRequired();
        try {
            return redisSource.withJedis(jedis -> {
                jedis.getClient().sendCommand(FT_INFO_CMD, SafeEncoder.encodeMany(name));
                Object resp = jedis.getClient().getOne();
                if (resp instanceof List) {
                    List<?> list = (List<?>) resp;
                    for (int i = 0; i < list.size() - 1; i += 2) {
                        Object key = list.get(i);
                        Object val = list.get(i + 1);
                        if (key instanceof byte[] && "num_docs".equals(SafeEncoder.encode((byte[]) key))) {
                            return val instanceof Number ? ((Number) val).longValue() : 0L;
                        }
                    }
                }
                return 0L;
            });
        } catch (Exception e) {
            log.warn("[Redis] count failed for index '{}': {}", name, e.getMessage());
            return 0L;
        }
    }

    // ---------- RedisEmbeddingStore ----------

    @Override
    public EmbeddingStore<TextSegment> getEmbeddingStore() {
        if (embeddingStoreCache == null) {
            synchronized (this) {
                if (embeddingStoreCache == null) {
                    String host = redisSource.connectionProperties().resolvedHost();
                    int port = redisSource.connectionProperties().resolvedPort(6379);
                    String password = redisSource.connectionProperties().getPassword();
                    String user = redisSource.connectionProperties().getUsername();
                    String collectionName = collectionNameRequired();
                    int dim = dimensionRequired();
                    String metric = mapMetric(getEntity().getDistanceMetric());

                    log.info("[Redis] building RedisEmbeddingStore: host={}, port={}, index={}, dim={}, metric={}",
                            host, port, collectionName, dim, metric);

                    embeddingStoreCache = new RedisEmbeddingStore(redisSource, collectionName);
                    log.info("[Redis] RedisEmbeddingStore created successfully");
                }
            }
        }
        return embeddingStoreCache;
    }

    // ---------- bind ----------

    @Override
    public AbstractVecDoc bindDoc(AiVecDocEntity doc) {
        return new RedisVecDocHandler(this, doc);
    }

    // ---------- helpers ----------

    private String collectionNameRequired() {
        String n = getEntity().getCollectionName();
        if (StringUtils.isBlank(n)) {
            throw new IllegalStateException("AiVecStoreEntity.collectionName is required");
        }
        return n.trim();
    }

    private int dimensionRequired() {
        Long d = getEntity().getDimension();
        if (d == null || d <= 0) {
            throw new IllegalStateException("AiVecStoreEntity.dimension must be positive");
        }
        if (d > Integer.MAX_VALUE) {
            throw new IllegalStateException("AiVecStoreEntity.dimension too large");
        }
        return d.intValue();
    }
}
