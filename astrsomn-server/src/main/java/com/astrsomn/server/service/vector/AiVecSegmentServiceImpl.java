package com.astrsomn.server.service.vector;

import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.entity.AiAccountEntity;
import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelSetting;
import com.astrsomn.api.runtime.common.langchain.extension.vector.VecSource;
import com.astrsomn.api.runtime.common.langchain.extension.vector.VecStore;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.vector.constant.VecDocMetadataKeys;
import com.astrsomn.api.vector.constant.AiVecSourceEnum;
import com.astrsomn.api.vector.dto.vecsegment.*;
import com.astrsomn.api.vector.entity.AiVecDocEntity;
import com.astrsomn.api.vector.entity.AiVecSegmentEntity;
import com.astrsomn.api.vector.entity.AiVecSourceEntity;
import com.astrsomn.api.vector.entity.AiVecStoreEntity;
import com.astrsomn.api.vector.exception.AstVecDocErrorEnum;
import com.astrsomn.api.vector.exception.AstVecSegmentErrorEnum;
import com.astrsomn.api.vector.exception.AstVecStoreErrorEnum;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.common.utils.CryptoUtil;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.mapper.AiVecDocMapper;
import com.astrsomn.server.mapper.AiVecSegmentMapper;

import com.astrsomn.starter.runtime.langchain.factory.AstroModelFactory;
import com.astrsomn.starter.runtime.langchain.runtime.chain.RuntimeChatParamMergeSupport;
import com.astrsomn.starter.runtime.mapper.AstAiAccountMapper;
import com.astrsomn.starter.runtime.mapper.AstAiInstanceMapper;
import com.astrsomn.starter.runtime.mapper.AstAiModelMapper;
import com.astrsomn.starter.runtime.vector.AstroVecSourceFactory;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiVecSegmentServiceImpl extends ServiceImpl<AiVecSegmentMapper, AiVecSegmentEntity> implements AiVecSegmentService {

    private final AiVecStoreService aiVecStoreService;
    private final AiVecSourceService aiVecSourceService;
    private final AstroVecSourceFactory astroVecSourceFactory;
    private final AstroModelFactory astroModelFactory;
    private final AstAiInstanceMapper aiInstanceMapper;
    private final AstAiModelMapper aiModelMapper;
    private final AstAiAccountMapper astAiAccountMapper;
    private final AiVecDocMapper aiVecDocMapper;


    @Override
    public BaseResponse<String> create(AiVecSegmentCreateRequestDTO request) {
        AiVecSegmentEntity entity = new AiVecSegmentEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_CREATE_FAILED);
        }
        return BaseResponse.success("success");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> delete(long[] ids) {
        if (Objects.isNull(ids) || ids.length == 0) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_PARAM_ERROR);
        }
        for (long id : ids) {
            AiVecSegmentEntity segment = getById(id);
            if (Objects.isNull(segment)) {
                continue;
            }
            deleteVectorBySegment(segment);
            removeById(id);
        }
        return BaseResponse.success("success");
    }

    private void deleteVectorBySegment(AiVecSegmentEntity segment) {
        if (Objects.isNull(segment.getCollectionId()) || Objects.isNull(segment.getVectorId()) || segment.getVectorId().isBlank()) {
            return;
        }
        AiVecStoreEntity store = aiVecStoreService.getById(segment.getCollectionId());
        if (Objects.isNull(store) || Objects.isNull(store.getSourceId())) {
            return;
        }
        VecSource vecSource = astroVecSourceFactory.tryGetActiveSource(store.getSourceId())
                .orElseThrow(() -> new BusinessException(AstVecDocErrorEnum.DOC_SOURCE_NOT_READY));
        VecStore vecStore = vecSource.openStore(store);
        EmbeddingStore<TextSegment> embeddingStore = vecStore.getEmbeddingStore();
        try {
            embeddingStore.remove(segment.getVectorId());
        } catch (RuntimeException e) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_DELETE_FAILED, e.getMessage());
        }
    }

    @Override
    public BaseResponse<String> update(AiVecSegmentUpdateRequestDTO request) {
        if (Objects.isNull(request.getId())) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_PARAM_ERROR);
        }
        AiVecSegmentEntity existing = getById(request.getId());
        if (Objects.isNull(existing)) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_NOT_FOUND);
        }
        AiVecSegmentEntity entity = new AiVecSegmentEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_UPDATE_FAILED);
        }
        return BaseResponse.success("success");
    }

    @Override
    public PageResponse<AiVecSegmentResponseDTO> queryPage(BasePageRequest<AiVecSegmentQueryRequestDTO> request) {
        IPage<AiVecSegmentResponseDTO> page = PageUtils.buildPage(request);
        AiVecSegmentQueryRequestDTO param = request.getParam();
        if (Objects.isNull(param)) {
            param = new AiVecSegmentQueryRequestDTO();
        }
        IPage<AiVecSegmentResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }

    @Override
    public BaseResponse<AiVecSegmentResponseDTO> detail(Long id) {
        AiVecSegmentEntity entity = getById(id);
        if (Objects.isNull(entity)) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_NOT_FOUND);
        }
        AiVecSegmentResponseDTO responseDTO = new AiVecSegmentResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public List<AiVecSegmentSearchResultDTO> search(AiVecSegmentSearchRequestDTO request) {
        if (Objects.isNull(request.getCollectionId())) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR, "collectionId is required");
        }
        if (StringUtils.isBlank(request.getQueryText())) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR, "queryText is required");
        }

        AiVecStoreEntity store = aiVecStoreService.getById(request.getCollectionId());
        if (Objects.isNull(store)) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_NOT_FOUND);
        }



        VecSource vecSource = astroVecSourceFactory.tryGetActiveSource(store.getSourceId())
                .orElseThrow(() -> new BusinessException(AstVecDocErrorEnum.DOC_SOURCE_NOT_READY));
        VecStore vecStore = vecSource.openStore(store);
        EmbeddingStore<TextSegment> embeddingStore = vecStore.getEmbeddingStore();

        EmbeddingModel embeddingModel = resolveEmbeddingModel(store);
        Response<Embedding> embedResp = embeddingModel.embed(request.getQueryText());
        Embedding queryEmbedding = embedResp.content();

        int topK = Objects.nonNull(request.getTopK()) && request.getTopK() > 0 ? request.getTopK() : 5;
        var searchReqBuilder = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(topK);
        if (Objects.nonNull(request.getMinScore()) && request.getMinScore() > 0) {
            searchReqBuilder.minScore(request.getMinScore());
        }

        List<EmbeddingMatch<TextSegment>> matches =
                embeddingStore.search(searchReqBuilder.build()).matches();

        List<AiVecSegmentSearchResultDTO> searchResults = new ArrayList<>();
        for (EmbeddingMatch<TextSegment> match : matches) {
            AiVecSegmentSearchResultDTO dto = new AiVecSegmentSearchResultDTO();
            dto.setScore(match.score());
            TextSegment segment = match.embedded();
            if (Objects.nonNull(segment)) {
                dto.setSegmentContent(segment.text());
            }
            String vectorId = match.embeddingId();
            if (Objects.nonNull(vectorId)) {
                AiVecSegmentEntity dbSegment = getOne(new LambdaQueryWrapper<AiVecSegmentEntity>()
                        .eq(AiVecSegmentEntity::getVectorId, vectorId)
                        .last("LIMIT 1"));
                if (Objects.nonNull(dbSegment)) {
                    dto.setSegmentId(dbSegment.getId());
                    dto.setDocId(dbSegment.getDocId());
                    dto.setMetadataJson(dbSegment.getMetadataJson());
                }
            }
            searchResults.add(dto);
        }

        return searchResults;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<AiVecSegmentResponseDTO> vectorize(Long id) {
        if (Objects.isNull(id)) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_PARAM_ERROR);
        }
        AiVecSegmentEntity segment = getById(id);
        if (Objects.isNull(segment)) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_NOT_FOUND);
        }
        if (StringUtils.isBlank(segment.getSegmentContent())) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_PARAM_ERROR, "Segment content is empty");
        }
        if (Objects.isNull(segment.getCollectionId())) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_PARAM_ERROR, "Segment not associated with a collection");
        }

        AiVecStoreEntity store = aiVecStoreService.getById(segment.getCollectionId());
        if (Objects.isNull(store)) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_NOT_FOUND);
        }

        log.info("vectorize segment: segmentId={}, collectionId={}, sourceId={}",
                id, store.getId(), store.getSourceId());

        VecSource vecSource = astroVecSourceFactory.tryGetActiveSource(store.getSourceId()).orElse(null);
        if (Objects.isNull(vecSource)) {
            log.warn("vectorize segment: source not active, attempting register. sourceId={}", store.getSourceId());
            AiVecSourceEntity sourceEntity = aiVecSourceService.getById(store.getSourceId());
            if (Objects.isNull(sourceEntity)) {
                throw new BusinessException(AstVecDocErrorEnum.DOC_SOURCE_NOT_READY,
                        "Vector source not found: " + store.getSourceId());
            }
            log.info("vectorize segment: source status={}, extensionCode={}",
                    sourceEntity.getStatus(), sourceEntity.getExtensionCode());
            if (!AiVecSourceEnum.StatusEnum.ENABLED.getCode().equals(sourceEntity.getStatus())) {
                log.warn("vectorize segment: source is {}, auto-enabling. sourceId={}",
                        sourceEntity.getStatus(), store.getSourceId());
                sourceEntity.setStatus(AiVecSourceEnum.StatusEnum.ENABLED.getCode());
                aiVecSourceService.updateById(sourceEntity);
            }
            astroVecSourceFactory.registerOrRefresh(sourceEntity);
            vecSource = astroVecSourceFactory.tryGetActiveSource(store.getSourceId())
                    .orElseThrow(() -> new BusinessException(AstVecDocErrorEnum.DOC_SOURCE_NOT_READY,
                            "Vector source not ready after register: " + store.getSourceId()));
            log.info("vectorize segment: source registered successfully. sourceId={}", store.getSourceId());
        }

        VecStore vecStore = vecSource.openStore(store);
        EmbeddingStore<TextSegment> embeddingStore = vecStore.getEmbeddingStore();

        EmbeddingModel embeddingModel = resolveEmbeddingModel(store);

        // If already vectorized, remove old vector first
        if (StringUtils.isNotBlank(segment.getVectorId())) {
            try {
                embeddingStore.remove(segment.getVectorId());
            } catch (RuntimeException e) {
                log.warn("Failed to remove old vector for segmentId={}, vectorId={}", id, segment.getVectorId(), e);
            }
        }

        Response<Embedding> embedResp;
        try {
            embedResp = embeddingModel.embed(segment.getSegmentContent());
        } catch (RuntimeException e) {
            log.error("embed failed segmentId={}", id, e);
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_UPDATE_FAILED, e.getMessage());
        }
        Embedding embedding = embedResp.content();

        ensureCollectionDimension(vecStore, store, embedding);

        String logicalDocId = null;
        if (Objects.nonNull(segment.getDocId())) {
            AiVecDocEntity doc = aiVecDocMapper.selectById(segment.getDocId());
            if (Objects.nonNull(doc) && StringUtils.isNotBlank(doc.getDocIdInStore())) {
                logicalDocId = doc.getDocIdInStore().trim();
            }
        }
        if (StringUtils.isBlank(logicalDocId)) {
            logicalDocId = java.util.UUID.randomUUID().toString().replace("-", "");
        }

        Metadata meta = new Metadata();
        meta.put(VecDocMetadataKeys.DOC_ID_IN_STORE, logicalDocId);
        TextSegment textSegment = TextSegment.from(segment.getSegmentContent(), meta);

        String vectorId;
        try {
            vectorId = embeddingStore.add(embedding, textSegment);
        } catch (RuntimeException e) {
            log.error("embedding store add failed segmentId={}", id, e);
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_UPDATE_FAILED, e.getMessage());
        }

        segment.setVectorId(vectorId);
        boolean updated = updateById(segment);
        if (!updated) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_UPDATE_FAILED, "Failed to update segment vectorId");
        }

        AiVecSegmentResponseDTO responseDTO = new AiVecSegmentResponseDTO();
        BeanUtils.copyProperties(segment, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    private EmbeddingModel resolveEmbeddingModel(AiVecStoreEntity store) {

        AiInstanceEntity instance = aiInstanceMapper.selectOne(
                new LambdaQueryWrapper<AiInstanceEntity>()
                        .eq(AiInstanceEntity::getInstanceKey, store.getInstanceKey().trim())

                        .eq(AiInstanceEntity::getDeleted, false)
                        .last("LIMIT 1"));
        if (Objects.isNull(instance)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "Embedding instance not found: " + store.getInstanceKey());
        }
        String modelKey = StringUtils.trimToNull(instance.getModelKey());
        if (Objects.isNull(modelKey)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "Embedding instance has no MODEL_KEY configured");
        }
        AiModelEntity model = aiModelMapper.selectOne(
                new LambdaQueryWrapper<AiModelEntity>()
                        .eq(AiModelEntity::getModelKey, modelKey)

                        .eq(AiModelEntity::getDeleted, false)
                        .last("LIMIT 1"));
        if (Objects.isNull(model)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "Model not found: " + modelKey);
        }
        String modelType = StringUtils.trimToNull(model.getModelType());
        if (!AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode().equalsIgnoreCase(modelType)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "Model type must be embedding");
        }

        AstroChatParam<EmbeddingModel> param =
                AstroChatParam.<EmbeddingModel>builder().serviceClass(EmbeddingModel.class).build();
        param.setInstanceKey(instance.getInstanceKey());
        param.setModelKey(modelKey);
        param.setModelSetting(new ModelSetting());
        RuntimeChatParamMergeSupport.mergeChatSettingFromInstance(param.getChatSetting(), instance);
        RuntimeChatParamMergeSupport.mergeModelSettingFromModel(param.getModelSetting(), model);

        String accountKey = StringUtils.trimToNull(instance.getAccountKey());
        if (Objects.nonNull(accountKey)) {
            AiAccountEntity account = astAiAccountMapper.selectOne(
                    new LambdaQueryWrapper<AiAccountEntity>()
                            .eq(AiAccountEntity::getAccountKey, accountKey)
                            .eq(AiAccountEntity::getDeleted, false)
                            .last("LIMIT 1"));
            if (Objects.nonNull(account)) {
                if (account.getApiKey() != null) {
                    account.setApiKey(CryptoUtil.decrypt(account.getApiKey()));
                }
                if (account.getApiSecret() != null) {
                    account.setApiSecret(CryptoUtil.decrypt(account.getApiSecret()));
                }
                RuntimeChatParamMergeSupport.mergeModelSettingFromAccount(param.getModelSetting(), account);
            }
        }

        return astroModelFactory.createModel(param, EmbeddingModel.class);
    }

    private void ensureCollectionDimension(VecStore vecStore, AiVecStoreEntity store, Embedding embedding) {
        int actualDim = embedding.vector().length;
        Long configuredDim = store.getDimension();
        if (configuredDim != null && configuredDim.intValue() == actualDim) {
            return;
        }
        log.info("Collection dimension mismatch: configured={}, actual={}, recreating collection {}",
                configuredDim, actualDim, store.getCollectionName());
        vecStore.dropCollection();
        store.setDimension((long) actualDim);
        aiVecStoreService.updateById(store);
        vecStore.createCollection();
    }
}
