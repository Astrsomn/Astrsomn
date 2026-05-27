package com.astrsomn.vector.qdrant.service;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.RelevanceScore;
import dev.langchain4j.store.embedding.filter.Filter;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.WithPayloadSelectorFactory;
import io.qdrant.client.WithVectorsSelectorFactory;
import io.qdrant.client.grpc.JsonWithInt.Value;
import io.qdrant.client.grpc.Points.ScoredPoint;
import io.qdrant.client.grpc.Points.SearchPoints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingDouble;

public class FixedQdrantEmbeddingStore implements EmbeddingStore<TextSegment> {

    private static final Logger log = LoggerFactory.getLogger(FixedQdrantEmbeddingStore.class);

    private final EmbeddingStore<TextSegment> delegate;
    private final QdrantClient qdrantClient;
    private final String collectionName;
    private final String payloadTextKey;

    public FixedQdrantEmbeddingStore(EmbeddingStore<TextSegment> delegate,
                                     QdrantClient qdrantClient,
                                     String collectionName,
                                     String payloadTextKey) {
        this.delegate = delegate;
        this.qdrantClient = qdrantClient;
        this.collectionName = collectionName;
        this.payloadTextKey = payloadTextKey;
    }

    @Override
    public String add(Embedding embedding) {
        return delegate.add(embedding);
    }

    @Override
    public void add(String id, Embedding embedding) {
        delegate.add(id, embedding);
    }

    @Override
    public String add(Embedding embedding, TextSegment textSegment) {
        return delegate.add(embedding, textSegment);
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings) {
        return delegate.addAll(embeddings);
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings, List<TextSegment> embedded) {
        return delegate.addAll(embeddings, embedded);
    }

    @Override
    public void addAll(List<String> ids, List<Embedding> embeddings, List<TextSegment> embedded) {
        delegate.addAll(ids, embeddings, embedded);
    }

    @Override
    public void remove(String id) {
        delegate.remove(id);
    }

    @Override
    public void removeAll(Collection<String> ids) {
        delegate.removeAll(ids);
    }

    @Override
    public void removeAll() {
        delegate.removeAll();
    }

    @Override
    public void removeAll(Filter filter) {
        delegate.removeAll(filter);
    }

    @Override
    public EmbeddingSearchResult<TextSegment> search(EmbeddingSearchRequest request) {
        try {
            return delegate.search(request);
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("Length of vector a (0)")) {
                return searchFixed(request);
            }
            throw e;
        }
    }

    private EmbeddingSearchResult<TextSegment> searchFixed(EmbeddingSearchRequest request) {
        SearchPoints.Builder searchBuilder = SearchPoints.newBuilder()
                .setCollectionName(collectionName)
                .addAllVector(request.queryEmbedding().vectorAsList())
                .setWithVectors(WithVectorsSelectorFactory.enable(true))
                .setWithPayload(WithPayloadSelectorFactory.enable(true))
                .setLimit(request.maxResults());

        List<ScoredPoint> results;
        try {
            results = qdrantClient.searchAsync(searchBuilder.build()).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        if (results.isEmpty()) {
            return new EmbeddingSearchResult<>(Collections.emptyList());
        }

        List<EmbeddingMatch<TextSegment>> matches = results.stream()
                .map(sp -> toEmbeddingMatchFixed(sp, request.queryEmbedding()))
                .filter(m -> m.score() >= request.minScore())
                .sorted(comparingDouble(EmbeddingMatch::score))
                .collect(Collectors.toList());

        Collections.reverse(matches);
        return new EmbeddingSearchResult<>(matches);
    }

    private EmbeddingMatch<TextSegment> toEmbeddingMatchFixed(ScoredPoint scoredPoint, Embedding referenceEmbedding) {
        Map<String, Value> payload = scoredPoint.getPayloadMap();

        Value textSegmentValue = payload.getOrDefault(payloadTextKey, null);

        Map<String, Object> metadata = payload.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(payloadTextKey))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> valueToObject(entry.getValue())));

        double qdrantScore = scoredPoint.getScore();
        double relevanceScore = RelevanceScore.fromCosineSimilarity(qdrantScore);

        return new EmbeddingMatch<>(
                relevanceScore,
                scoredPoint.getId().getUuid(),
                referenceEmbedding,
                textSegmentValue == null
                        ? null
                        : TextSegment.from(textSegmentValue.getStringValue(), new Metadata(metadata)));
    }

    private static Object valueToObject(Value value) {
        if (value == null) {
            return null;
        }
        switch (value.getKindCase()) {
            case INTEGER_VALUE:
                return value.getIntegerValue();
            case STRING_VALUE:
                return value.getStringValue();
            case DOUBLE_VALUE:
                return value.getDoubleValue();
            case BOOL_VALUE:
                return value.getBoolValue();
            case LIST_VALUE:
                return value.getListValue().getValuesList().stream()
                        .map(FixedQdrantEmbeddingStore::valueToObject)
                        .collect(Collectors.toList());
            case STRUCT_VALUE:
                return value.getStructValue().getFieldsMap().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> valueToObject(e.getValue())));
            default:
                return null;
        }
    }
}
