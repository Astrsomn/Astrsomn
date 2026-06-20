package com.astrsomn.vector.redis.internal;

import com.astrsomn.vector.redis.service.RedisVecSourceHandler;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import dev.langchain4j.store.embedding.filter.comparison.IsGreaterThan;
import dev.langchain4j.store.embedding.filter.comparison.IsGreaterThanOrEqualTo;
import dev.langchain4j.store.embedding.filter.comparison.IsIn;
import dev.langchain4j.store.embedding.filter.comparison.IsLessThan;
import dev.langchain4j.store.embedding.filter.comparison.IsLessThanOrEqualTo;
import dev.langchain4j.store.embedding.filter.comparison.IsNotEqualTo;
import dev.langchain4j.store.embedding.filter.comparison.IsNotIn;
import dev.langchain4j.store.embedding.filter.logical.And;
import dev.langchain4j.store.embedding.filter.logical.Not;
import dev.langchain4j.store.embedding.filter.logical.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.util.SafeEncoder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Custom {@link EmbeddingStore} backed by Redis with RediSearch.
 * <p>
 * Stores each embedding as a Redis Hash under key {@code <prefix><id>}.
 * Fields: {@code embedding} (raw float32 LE bytes), {@code text_segment} (text),
 * plus arbitrary metadata key-value pairs.
 * Vector similarity search uses {@code FT.SEARCH} with KNN query dialect 2.
 */
public class RedisEmbeddingStore implements EmbeddingStore<TextSegment> {

    private static final Logger log = LoggerFactory.getLogger(RedisEmbeddingStore.class);

    private static final String VECTOR_FIELD = "embedding";
    private static final String TEXT_FIELD = "text_segment";

    private static final ProtocolCommand FT_SEARCH_CMD = () -> SafeEncoder.encode("FT.SEARCH");

    private final RedisVecSourceHandler source;

    private final String indexName;
    private final String prefix;

    public RedisEmbeddingStore(RedisVecSourceHandler source, String indexName) {
        this.source = source;
        this.indexName = indexName;
        this.prefix = RedisVecConstants.DEFAULT_INDEX_PREFIX;
    }

    // ========== add ==========

    @Override
    public String add(Embedding embedding) {
        String id = UUID.randomUUID().toString();
        add(id, embedding);
        return id;
    }

    @Override
    public void add(String id, Embedding embedding) {
        addInternal(id, embedding, null);
    }

    @Override
    public String add(Embedding embedding, TextSegment segment) {
        String id = UUID.randomUUID().toString();
        addInternal(id, embedding, segment);
        return id;
    }

    /**
     * Internal add with explicit ID. Not part of the public EmbeddingStore interface
     * (the interface only exposes {@code add(Embedding, Embedded)} with auto-generated ID).
     */
    public void addInternal(String id, Embedding embedding, TextSegment segment) {
        byte[] key = SafeEncoder.encode(prefix + id);

        source.withJedis(jedis -> {
            Map<byte[], byte[]> fields = new HashMap<>();
            fields.put(SafeEncoder.encode(VECTOR_FIELD), floatArrayToBytes(embedding.vector()));
            fields.put(SafeEncoder.encode(TEXT_FIELD), SafeEncoder.encode(
                    segment != null && segment.text() != null ? segment.text() : ""));
            if (segment != null && segment.metadata() != null) {
                for (Map.Entry<String, Object> e : segment.metadata().toMap().entrySet()) {
                    Object rawV = e.getValue();
                    String v = rawV != null ? String.valueOf(rawV) : "";
                    fields.put(SafeEncoder.encode(e.getKey()),
                            SafeEncoder.encode(v));
                }
            }
            jedis.hset(key, fields);
            return null;
        });
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings) {
        List<String> ids = new ArrayList<>(embeddings.size());
        for (Embedding embedding : embeddings) {
            ids.add(add(embedding));
        }
        return ids;
    }

    @Override
    public void addAll(List<String> ids, List<Embedding> embeddings, List<TextSegment> segments) {
        int n = Math.min(embeddings.size(), segments.size());
        for (int i = 0; i < n; i++) {
            String id = i < ids.size() ? ids.get(i) : UUID.randomUUID().toString();
            addInternal(id, embeddings.get(i), segments.get(i));
        }
    }

    // ========== remove ==========

    @Override
    public void remove(String id) {
        byte[] key = SafeEncoder.encode(prefix + id);
        source.withJedis(jedis -> {
            jedis.del(key);
            return null;
        });
    }

    @Override
    public void removeAll(Collection<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        source.withJedis(jedis -> {
            byte[][] keys = new byte[ids.size()][];
            int i = 0;
            for (String id : ids) {
                keys[i++] = SafeEncoder.encode(prefix + id);
            }
            jedis.del(keys);
            return null;
        });
    }

    @Override
    public void removeAll(Filter filter) {
        if (filter == null) {
            return;
        }
        String query = buildFilterQuery(filter);
        if (query == null) {
            return;
        }
        // Use FT.SEARCH with the filter, RETURN 0 to get only IDs
        List<String> matchedIds = source.withJedis(jedis -> {
            jedis.getClient().sendCommand(FT_SEARCH_CMD, SafeEncoder.encodeMany(
                    indexName, query, "RETURN", "0", "LIMIT", "0", "10000"));
            return parseIdsFromFtSearch(jedis.getClient().getOne());
        });
        if (matchedIds != null && !matchedIds.isEmpty()) {
            removeAll(matchedIds);
        }
    }

    @Override
    public void removeAll() {
        source.withJedis(jedis -> {
            List<String> keys = new ArrayList<>();
            String cursor = ScanParams.SCAN_POINTER_START;
            ScanParams scanParams = new ScanParams().match(prefix + "*").count(100);
            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                keys.addAll(scanResult.getResult());
                cursor = scanResult.getCursor();
            } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
            if (!keys.isEmpty()) {
                jedis.del(keys.toArray(new String[0]));
            }
            return null;
        });
    }

    // ========== search ==========

    @Override
    public EmbeddingSearchResult<TextSegment> search(EmbeddingSearchRequest request) {
        int k = request.maxResults();
        double minScore = request.minScore();
        float[] queryVector = request.queryEmbedding().vector();

        List<EmbeddingMatch<TextSegment>> matches = source.withJedis(jedis -> {
            byte[] vecBytes = floatArrayToBytes(queryVector);

            // Build: FT.SEARCH idx "*=>[KNN k @embedding $vec AS score]" PARAMS 2 vec <bin> DIALECT 2
            // RETURN 2 text_segment score SORTBY score ASC LIMIT 0 k
            jedis.getClient().sendCommand(FT_SEARCH_CMD,
                    SafeEncoder.encode(indexName),
                    SafeEncoder.encode("*=>[KNN " + k + " @" + VECTOR_FIELD + " $vec AS score]"),
                    SafeEncoder.encode("PARAMS"),
                    SafeEncoder.encode("2"),
                    SafeEncoder.encode("vec"),
                    vecBytes,
                    SafeEncoder.encode("DIALECT"),
                    SafeEncoder.encode("2"),
                    SafeEncoder.encode("RETURN"),
                    SafeEncoder.encode("2"),
                    SafeEncoder.encode(TEXT_FIELD),
                    SafeEncoder.encode("score"),
                    SafeEncoder.encode("SORTBY"),
                    SafeEncoder.encode("score"),
                    SafeEncoder.encode("ASC"),
                    SafeEncoder.encode("LIMIT"),
                    SafeEncoder.encode("0"),
                    SafeEncoder.encode(String.valueOf(k))
            );

            return parseFtSearchResults(jedis.getClient().getOne(), queryVector, minScore);
        });

        return new EmbeddingSearchResult<>(matches);
    }

    // ========== helpers ==========

    /**
     * Convert a float[] vector to little-endian float32 byte[] for Redis storage.
     */
    private static byte[] floatArrayToBytes(float[] vector) {
        byte[] bytes = new byte[vector.length * 4];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().put(vector);
        return bytes;
    }

    /**
     * Parse FT.SEARCH response into EmbeddingMatch list.
     * <p>
     * FT.SEARCH response format (via getOne()):
     * [total (Long), key1 (byte[]), [field1, val1, field2, val2, ...], key2, [...], ...]
     */
    @SuppressWarnings("unchecked")
    private List<EmbeddingMatch<TextSegment>> parseFtSearchResults(
            Object raw, float[] queryVector, double minScore) {
        List<EmbeddingMatch<TextSegment>> results = new ArrayList<>();
        if (!(raw instanceof List)) {
            return results;
        }
        List<Object> list = (List<Object>) raw;
        if (list.isEmpty()) {
            return results;
        }
        for (int i = 1; i < list.size(); i++) {
            Object elem = list.get(i);
            if (elem instanceof byte[]) {
                // key = embedding:{id}
                String key = SafeEncoder.encode((byte[]) elem);
                String id = key.startsWith(prefix) ? key.substring(prefix.length()) : key;
                i++;
                if (i >= list.size()) {
                    break;
                }
                // fields: [[field1, val1, field2, val2, ...]]
                List<Object> fields = (List<Object>) list.get(i);
                String text = "";
                double score = 0.0;
                for (int j = 0; j < fields.size() - 1; j += 2) {
                    String fname = SafeEncoder.encode((byte[]) fields.get(j));
                    Object fval = fields.get(j + 1);
                    if (TEXT_FIELD.equals(fname) && fval instanceof byte[]) {
                        text = SafeEncoder.encode((byte[]) fval);
                    } else if ("score".equals(fname) && fval instanceof byte[]) {
                        score = Double.parseDouble(SafeEncoder.encode((byte[]) fval));
                    }
                }
                // KNN returns distance (0=identical), convert to similarity (1=identical)
                double similarity = Math.max(0.0, 1.0 - score);
                if (similarity < minScore) {
                    continue;
                }
                results.add(new EmbeddingMatch<>(
                        similarity, id, null, text != null ? TextSegment.from(text) : null));
            }
        }
        return results;
    }

    /**
     * Parse FT.SEARCH response to collect matched embedding IDs.
     */
    @SuppressWarnings("unchecked")
    private List<String> parseIdsFromFtSearch(Object raw) {
        List<String> ids = new ArrayList<>();
        if (!(raw instanceof List)) {
            return ids;
        }
        List<Object> list = (List<Object>) raw;
        if (list.isEmpty()) {
            return ids;
        }
        for (int i = 1; i < list.size(); i++) {
            Object elem = list.get(i);
            if (elem instanceof byte[]) {
                String key = SafeEncoder.encode((byte[]) elem);
                String id = key.startsWith(prefix) ? key.substring(prefix.length()) : key;
                ids.add(id);
                i++; // skip fields
            }
        }
        return ids;
    }

    /**
     * Convert a langchain4j {@link Filter} to a RediSearch query string.
     * <p>
     * Handles {@code IsEqualTo} (used by VecDoc for doc_id_in_store filter).
     * Other filter types throw {@code UnsupportedOperationException}.
     */
    private static String buildFilterQuery(Filter filter) {
        if (filter == null) {
            return "*";
        }
        if (filter instanceof And) {
            And and = (And) filter;
            String left = buildFilterQuery(and.left());
            String right = buildFilterQuery(and.right());
            return "(" + left + " " + right + ")";
        }
        if (filter instanceof Or) {
            Or or = (Or) filter;
            String left = buildFilterQuery(or.left());
            String right = buildFilterQuery(or.right());
            return "(" + left + " | " + right + ")";
        }
        if (filter instanceof Not) {
            Not not = (Not) filter;
            String inner = buildFilterQuery(not.expression());
            return "-(" + inner + ")";
        }
        if (filter instanceof IsEqualTo) {
            IsEqualTo eq = (IsEqualTo) filter;
            return "@" + eq.key() + ":{" + escapeFtValue(String.valueOf(eq.comparisonValue())) + "}";
        }
        if (filter instanceof IsNotEqualTo) {
            IsNotEqualTo ne = (IsNotEqualTo) filter;
            return "-@" + ne.key() + ":{" + escapeFtValue(String.valueOf(ne.comparisonValue())) + "}";
        }
        if (filter instanceof IsGreaterThan) {
            IsGreaterThan gt = (IsGreaterThan) filter;
            return "@" + gt.key() + ":[(" + gt.comparisonValue() + " +inf]";
        }
        if (filter instanceof IsGreaterThanOrEqualTo) {
            IsGreaterThanOrEqualTo ge = (IsGreaterThanOrEqualTo) filter;
            return "@" + ge.key() + ":[" + ge.comparisonValue() + " +inf]";
        }
        if (filter instanceof IsLessThan) {
            IsLessThan lt = (IsLessThan) filter;
            return "@" + lt.key() + ":[-inf (" + lt.comparisonValue() + "]";
        }
        if (filter instanceof IsLessThanOrEqualTo) {
            IsLessThanOrEqualTo le = (IsLessThanOrEqualTo) filter;
            return "@" + le.key() + ":[-inf " + le.comparisonValue() + "]";
        }
        if (filter instanceof IsIn) {
            IsIn in = (IsIn) filter;
            StringBuilder sb = new StringBuilder("@").append(in.key()).append(":{");
            boolean first = true;
            for (Object val : in.comparisonValues()) {
                if (!first) sb.append("|");
                sb.append(escapeFtValue(String.valueOf(val)));
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }
        if (filter instanceof IsNotIn) {
            IsNotIn nin = (IsNotIn) filter;
            StringBuilder sb = new StringBuilder("-@").append(nin.key()).append(":{");
            boolean first = true;
            for (Object val : nin.comparisonValues()) {
                if (!first) sb.append("|");
                sb.append(escapeFtValue(String.valueOf(val)));
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }
        log.warn("Unsupported filter type: {}", filter.getClass().getName());
        return "*";
    }

    /**
     * Escape special characters in a RediSearch query value.
     */
    private static String escapeFtValue(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("|", "\\|")
                .replace(":", "\\:")
                .replace(" ", "\\ ");
    }
}
