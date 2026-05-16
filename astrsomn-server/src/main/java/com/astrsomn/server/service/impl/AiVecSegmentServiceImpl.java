package com.astrsomn.server.service.impl;

import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.entity.*;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelSetting;
import com.astrsomn.starter.runtime.langchain.runtime.chain.RuntimeChatParamMergeSupport;
import com.astrsomn.api.vector.dto.vecsegment.*;
import com.astrsomn.api.vector.entity.AiVecSegmentEntity;
import com.astrsomn.api.vector.entity.AiVecStoreEntity;
import com.astrsomn.api.runtime.common.langchain.extension.vector.VecSource;
import com.astrsomn.api.runtime.common.langchain.extension.vector.VecStore;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.vector.exception.AstVecDocErrorEnum;
import com.astrsomn.api.vector.exception.AstVecSegmentErrorEnum;
import com.astrsomn.api.vector.exception.AstVecStoreErrorEnum;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.mapper.AiVecSegmentMapper;
import com.astrsomn.server.service.AiVecSegmentService;
import com.astrsomn.server.service.AiVecStoreService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.starter.runtime.langchain.factory.AstroModelFactory;
import com.astrsomn.starter.runtime.mapper.AstAiAccountMapper;
import com.astrsomn.starter.runtime.mapper.AstAiInstanceMapper;
import com.astrsomn.starter.runtime.mapper.AstAiModelMapper;
import com.astrsomn.starter.runtime.vector.AstroVecSourceFactory;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AiVecSegmentServiceImpl extends ServiceImpl<AiVecSegmentMapper, AiVecSegmentEntity> implements AiVecSegmentService {

    private final AiVecStoreService aiVecStoreService;
    private final AstroVecSourceFactory astroVecSourceFactory;
    private final AstroModelFactory astroModelFactory;
    private final AstAiInstanceMapper aiInstanceMapper;
    private final AstAiModelMapper aiModelMapper;
    private final AstAiAccountMapper astAiAccountMapper;
    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AiVecSegmentCreateRequestDTO request) {
        AiVecSegmentEntity entity = new AiVecSegmentEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_PARAM_ERROR);
        }
        for (long id : ids) {
            AiVecSegmentEntity segment = getById(id);
            if (segment == null) {
                continue;
            }
            deleteVectorBySegment(segment);
            removeById(id);
        }
        return BaseResponse.success("删除成功");
    }

    private void deleteVectorBySegment(AiVecSegmentEntity segment) {
        if (segment.getCollectionId() == null || segment.getVectorId() == null || segment.getVectorId().isBlank()) {
            return;
        }
        AiVecStoreEntity store = aiVecStoreService.getById(segment.getCollectionId());
        if (store == null || store.getSourceId() == null) {
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
        if (request.getId() == null) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_PARAM_ERROR);
        }
        AiVecSegmentEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_NOT_FOUND);
        }
        AiVecSegmentEntity entity = new AiVecSegmentEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiVecSegmentResponseDTO> queryPage(BasePageRequest<AiVecSegmentQueryRequestDTO> request) {
        IPage<AiVecSegmentResponseDTO> page = PageUtils.buildPage(request);
        AiVecSegmentQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiVecSegmentQueryRequestDTO();
        }
        IPage<AiVecSegmentResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }

    @Override
    public BaseResponse<AiVecSegmentResponseDTO> detail(Long id) {
        AiVecSegmentEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_NOT_FOUND);
        }
        AiVecSegmentResponseDTO responseDTO = new AiVecSegmentResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public List<AiVecSegmentSearchResultDTO> search(AiVecSegmentSearchRequestDTO request) {
        if (request.getCollectionId() == null) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR, "collectionId 不能为空");
        }
        if (StringUtils.isBlank(request.getQueryText())) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR, "queryText 不能为空");
        }

        AiVecStoreEntity store = aiVecStoreService.getById(request.getCollectionId());
        if (store == null) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_NOT_FOUND);
        }

        String envCode = StringUtils.trimToNull(request.getEnvCode());
        if (envCode == null) {
            envCode = queryEnvParamHelper.effectiveEnvCode();
        }

        VecSource vecSource = astroVecSourceFactory.tryGetActiveSource(store.getSourceId())
                .orElseThrow(() -> new BusinessException(AstVecDocErrorEnum.DOC_SOURCE_NOT_READY));
        VecStore vecStore = vecSource.openStore(store);
        EmbeddingStore<TextSegment> embeddingStore = vecStore.getEmbeddingStore();

        EmbeddingModel embeddingModel = resolveEmbeddingModel(store, envCode);
        Response<Embedding> embedResp = embeddingModel.embed(request.getQueryText());
        Embedding queryEmbedding = embedResp.content();

        int topK = request.getTopK() != null && request.getTopK() > 0 ? request.getTopK() : 5;
        var searchReqBuilder = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(topK);
        if (request.getMinScore() != null && request.getMinScore() > 0) {
            searchReqBuilder.minScore(request.getMinScore());
        }

        List<EmbeddingMatch<TextSegment>> matches =
                embeddingStore.search(searchReqBuilder.build()).matches();

        List<AiVecSegmentSearchResultDTO> searchResults = new ArrayList<>();
        for (EmbeddingMatch<TextSegment> match : matches) {
            AiVecSegmentSearchResultDTO dto = new AiVecSegmentSearchResultDTO();
            dto.setScore(match.score());
            TextSegment segment = match.embedded();
            if (segment != null) {
                dto.setSegmentContent(segment.text());
            }
            String vectorId = match.embeddingId();
            if (vectorId != null) {
                AiVecSegmentEntity dbSegment = getOne(new LambdaQueryWrapper<AiVecSegmentEntity>()
                        .eq(AiVecSegmentEntity::getVectorId, vectorId)
                        .last("LIMIT 1"));
                if (dbSegment != null) {
                    dto.setSegmentId(dbSegment.getId());
                    dto.setDocId(dbSegment.getDocId());
                    dto.setMetadataJson(dbSegment.getMetadataJson());
                }
            }
            searchResults.add(dto);
        }

        return searchResults;
    }

    private EmbeddingModel resolveEmbeddingModel(AiVecStoreEntity store, String envCode) {
        if (StringUtils.isBlank(envCode)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "无法解析环境 ENV_CODE");
        }
        AiInstanceEntity instance = aiInstanceMapper.selectOne(
                new LambdaQueryWrapper<AiInstanceEntity>()
                        .eq(AiInstanceEntity::getInstanceKey, store.getInstanceKey().trim())
                        .eq(AiInstanceEntity::getEnvCode, envCode)
                        .eq(AiInstanceEntity::getDeleted, false)
                        .last("LIMIT 1"));
        if (instance == null) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "未找到嵌入实例: " + store.getInstanceKey());
        }
        String modelKey = StringUtils.trimToNull(instance.getModelKey());
        if (modelKey == null) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "嵌入实例未配置 MODEL_KEY");
        }
        AiModelEntity model = aiModelMapper.selectOne(
                new LambdaQueryWrapper<AiModelEntity>()
                        .eq(AiModelEntity::getModelKey, modelKey)
                        .eq(AiModelEntity::getEnvCode, envCode)
                        .eq(AiModelEntity::getDeleted, false)
                        .last("LIMIT 1"));
        if (model == null) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "未找到模型: " + modelKey);
        }
        String modelType = StringUtils.trimToNull(model.getModelType());
        if (modelType == null || !AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode().equalsIgnoreCase(modelType)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "模型类型必须为 embedding");
        }

        AstroChatParam<EmbeddingModel> param =
                AstroChatParam.<EmbeddingModel>builder().serviceClass(EmbeddingModel.class).build();
        param.setInstanceKey(instance.getInstanceKey());
        param.setModelKey(modelKey);
        param.setModelSetting(new ModelSetting());
        RuntimeChatParamMergeSupport.mergeChatSettingFromInstance(param.getChatSetting(), instance);
        RuntimeChatParamMergeSupport.mergeModelSettingFromModel(param.getModelSetting(), model);

        String accountKey = StringUtils.trimToNull(instance.getAccountKey());
        if (accountKey != null) {
            AiAccountEntity account = astAiAccountMapper.selectOne(
                    new LambdaQueryWrapper<AiAccountEntity>()
                            .eq(AiAccountEntity::getAccountKey, accountKey)
                            .eq(AiAccountEntity::getEnvCode, envCode)
                            .eq(AiAccountEntity::getDeleted, false)
                            .last("LIMIT 1"));
            if (account != null) {
                RuntimeChatParamMergeSupport.mergeModelSettingFromAccount(param.getModelSetting(), account);
            }
        }

        return astroModelFactory.createModel(param, EmbeddingModel.class);
    }
}
