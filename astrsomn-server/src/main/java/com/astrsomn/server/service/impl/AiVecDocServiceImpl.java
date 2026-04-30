package com.astrsomn.server.service.impl;
import com.astrsomn.core.common.utils.PageConverter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.constant.AiModelEnum;
import com.astrsomn.core.common.constant.AiVecDocEnum;
import com.astrsomn.core.common.constant.VecDocMetadataKeys;
import com.astrsomn.core.common.dto.vecdoc.AiVecDocCreateRequestDTO;
import com.astrsomn.core.common.dto.vecdoc.AiVecDocQueryRequestDTO;
import com.astrsomn.core.common.dto.vecdoc.AiVecDocResponseDTO;
import com.astrsomn.core.common.dto.vecdoc.AiVecDocUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiAccountEntity;
import com.astrsomn.core.common.entity.AiInstanceEntity;
import com.astrsomn.core.common.entity.AiModelEntity;
import com.astrsomn.core.common.entity.AiVecDocEntity;
import com.astrsomn.core.common.entity.AiVecSegmentEntity;
import com.astrsomn.core.common.entity.AiVecStoreEntity;
import com.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.core.common.langchain.buildParam.setting.ModelSetting;
import com.astrsomn.core.common.langchain.extension.vector.VecSource;
import com.astrsomn.core.common.langchain.extension.vector.VecStore;
import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.commn.base.BusinessException;
import com.astrsomn.core.exception.AstVecDocErrorEnum;
import com.astrsomn.starter.mapper.AiAccountMapper;
import com.astrsomn.starter.mapper.AiInstanceMapper;
import com.astrsomn.starter.mapper.AiModelMapper;
import com.astrsomn.starter.mapper.AiVecDocMapper;
import com.astrsomn.server.service.AiVecDocService;
import com.astrsomn.server.service.AiVecSegmentService;
import com.astrsomn.server.service.AiVecStoreService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.starter.langchain.factory.AstroModelFactory;
import com.astrsomn.starter.langchain.runtime.chain.RuntimeChatParamMergeSupport;
import com.astrsomn.starter.langchain.vector.AstroVecSourceFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.astrsomn.core.common.utils.PageUtils;
@Slf4j
@Service
@RequiredArgsConstructor
public class AiVecDocServiceImpl extends ServiceImpl<AiVecDocMapper, AiVecDocEntity> implements AiVecDocService {

    private static final int CHUNK_SIZE = 800;
    private static final int CHUNK_OVERLAP = 100;

    private final QueryEnvParamHelper queryEnvParamHelper;
    private final AstroVecSourceFactory astroVecSourceFactory;
    private final AstroModelFactory astroModelFactory;
    private final AiVecStoreService aiVecStoreService;
    private final AiInstanceMapper aiInstanceMapper;
    private final AiModelMapper aiModelMapper;
    private final AiAccountMapper aiAccountMapper;
    private final AiVecSegmentService aiVecSegmentService;

    @Override
    public BaseResponse<String> create(AiVecDocCreateRequestDTO request) {
        AiVecDocEntity entity = new AiVecDocEntity();
        BeanUtils.copyProperties(request, entity);
        if (StringUtils.isBlank(entity.getSyncStatus())) {
            entity.setSyncStatus(AiVecDocEnum.SyncStatus.PENDING.getCode());
        }
        queryEnvParamHelper.stampEffectiveEnv(entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<AiVecDocResponseDTO> upload(MultipartFile file, Long collectionId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "文件为空");
        }
        if (collectionId == null) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "collectionId 不能为空");
        }
        AiVecStoreEntity store = aiVecStoreService.getById(collectionId);
        if (store == null) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_STORE_NOT_FOUND);
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".txt")) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_FILE_NOT_TEXT, "仅支持 .txt 文件");
        }

        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        String fileName = dateStr + "_" + uuid + extension;

        String uploadDir = System.getProperty("user.dir") + "/vec/document/";
        File directory = new File(uploadDir);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_CREATE_FAILED, "无法创建上传目录");
        }

        File dest = new File(uploadDir + fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("upload transfer failed", e);
            throw new BusinessException(AstVecDocErrorEnum.DOC_CREATE_FAILED, "文件保存失败");
        }

        String relativePath = "/vec/document/" + fileName;

        AiVecDocEntity entity = new AiVecDocEntity();
        entity.setCollectionId(collectionId);
        entity.setFilePath(relativePath);
        entity.setOriginalFileName(originalFileName);
        entity.setContentSummary(originalFileName);
        entity.setSyncStatus(AiVecDocEnum.SyncStatus.PENDING.getCode());
        entity.setDocIdInStore(null);
        queryEnvParamHelper.stampEffectiveEnv(entity);

        boolean saved = save(entity);
        if (!saved) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_CREATE_FAILED);
        }

        AiVecDocResponseDTO dto = new AiVecDocResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return BaseResponse.success(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> vectorize(Long id) {
        if (id == null) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR);
        }
        AiVecDocEntity doc = getById(id);
        if (doc == null) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_NOT_FOUND);
        }
        String statusRaw = StringUtils.trimToNull(doc.getSyncStatus());
        if (statusRaw == null
                || !AiVecDocEnum.SyncStatus.PENDING.getCode().equalsIgnoreCase(statusRaw)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_STATUS_INVALID);
        }
        if (doc.getCollectionId() == null) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "文档未关联向量集合");
        }
        if (StringUtils.isBlank(doc.getFilePath())) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_FILE_NOT_READABLE, "文件路径为空");
        }

        AiVecStoreEntity store = aiVecStoreService.getById(doc.getCollectionId());
        if (store == null) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_STORE_NOT_FOUND);
        }
        if (StringUtils.isBlank(store.getInstanceKey())) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "向量集合未配置嵌入实例 instanceKey");
        }

        VecSource vecSource =
                astroVecSourceFactory
                        .tryGetActiveSource(store.getSourceId())
                        .orElseThrow(() -> new BusinessException(AstVecDocErrorEnum.DOC_SOURCE_NOT_READY));

        VecStore vecStore = vecSource.openStore(store);
        EmbeddingStore<TextSegment> embeddingStore = vecStore.getEmbeddingStore();

        String envCode = StringUtils.trimToNull(doc.getEnvCode());
        if (envCode == null) {
            envCode = queryEnvParamHelper.effectiveEnvCode();
        }

        EmbeddingModel embeddingModel = resolveEmbeddingModel(store, envCode);

        Path filePath = resolveUploadedPath(doc.getFilePath());
        if (!Files.isRegularFile(filePath)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_FILE_NOT_READABLE);
        }

        String fullText;
        try {
            fullText = readStrictUtf8Text(filePath);
        } catch (CharacterCodingException e) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_FILE_NOT_TEXT, "文件不是合法 UTF-8 文本");
        } catch (IOException e) {
            log.error("read file failed {}", filePath, e);
            throw new BusinessException(AstVecDocErrorEnum.DOC_FILE_NOT_READABLE, e.getMessage());
        }

        if (StringUtils.isBlank(fullText)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "文件内容为空");
        }

        String logicalDocId = UUID.randomUUID().toString().replace("-", "");
        var splitter = DocumentSplitters.recursive(CHUNK_SIZE, CHUNK_OVERLAP);
        List<TextSegment> splitSegments = splitter.split(Document.from(fullText));
        List<TextSegment> embeddedSegments = new ArrayList<>(splitSegments.size());
        for (TextSegment ts : splitSegments) {
            Metadata meta = ts.metadata() != null ? ts.metadata().copy() : new Metadata();
            meta.put(VecDocMetadataKeys.DOC_ID_IN_STORE, logicalDocId);
            embeddedSegments.add(TextSegment.from(ts.text(), meta));
        }

        Response<List<Embedding>> embedResp;
        try {
            embedResp = embeddingModel.embedAll(embeddedSegments);
        } catch (RuntimeException e) {
            log.error("embed failed docId={}", id, e);
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_FAILED, e.getMessage());
        }
        List<Embedding> embeddings = embedResp.content();
        if (embeddings == null || embeddings.size() != embeddedSegments.size()) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_FAILED, "嵌入结果数量与切片不一致");
        }

        List<String> vectorIds;
        try {
            vectorIds = embeddingStore.addAll(embeddings, embeddedSegments);
        } catch (RuntimeException e) {
            log.error("embedding store add failed docId={}", id, e);
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_FAILED, e.getMessage());
        }
        if (vectorIds == null || vectorIds.size() != embeddedSegments.size()) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_FAILED, "向量库返回 ID 数量与切片不一致");
        }

        List<AiVecSegmentEntity> rows = new ArrayList<>(embeddedSegments.size());
        long idx = 0;
        for (int i = 0; i < embeddedSegments.size(); i++) {
            TextSegment seg = embeddedSegments.get(i);
            AiVecSegmentEntity row = new AiVecSegmentEntity();
            row.setDocId(doc.getId());
            row.setCollectionId(doc.getCollectionId());
            row.setVectorId(vectorIds.get(i));
            row.setSegmentContent(seg.text());
            row.setWordCount((long) seg.text().length());
            row.setChunkIndex(idx++);
            queryEnvParamHelper.stampEffectiveEnv(row);
            rows.add(row);
        }
        boolean segOk = aiVecSegmentService.saveBatch(rows);
        if (!segOk) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_FAILED, "切片落库失败");
        }

        String summary = fullText.length() > 500 ? fullText.substring(0, 500) : fullText;
        doc.setDocIdInStore(logicalDocId);
        doc.setSyncStatus(AiVecDocEnum.SyncStatus.STORED.getCode());
        doc.setContentSummary(summary);
        boolean updated = updateById(doc);
        if (!updated) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_UPDATE_FAILED);
        }

        return BaseResponse.success("向量化完成");
    }

    private EmbeddingModel resolveEmbeddingModel(AiVecStoreEntity store, String envCode) {
        if (StringUtils.isBlank(envCode)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "无法解析环境 ENV_CODE");
        }
        AiInstanceEntity instance =
                aiInstanceMapper.selectOne(
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
        AiModelEntity model =
                aiModelMapper.selectOne(
                        new LambdaQueryWrapper<AiModelEntity>()
                                .eq(AiModelEntity::getModelKey, modelKey)
                                .eq(AiModelEntity::getEnvCode, envCode)
                                .eq(AiModelEntity::getDeleted, false)
                                .last("LIMIT 1"));
        if (model == null) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "未找到模型: " + modelKey);
        }
        String modelType = StringUtils.trimToNull(model.getModelType());
        if (modelType == null
                || !AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode().equalsIgnoreCase(modelType)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "模型类型必须为 embedding");
        }

        AstroChatParam<EmbeddingModel> param =
                AstroChatParam.<EmbeddingModel>builder().serviceClass(EmbeddingModel.class).build();
        param.setInstanceKey(instance.getInstanceKey());
        param.setModelKey(modelKey);
        param.setModelSetting(new ModelSetting());
        RuntimeChatParamMergeSupport.mergeChatSettingFromInstance(param.getChatSetting(), instance);
        RuntimeChatParamMergeSupport.mergeModelSettingFromModel(param.getModelSetting(), model);

        String accountKey = StringUtils.trimToNull(model.getAccountKey());
        if (accountKey != null) {
            AiAccountEntity account =
                    aiAccountMapper.selectOne(
                            new LambdaQueryWrapper<AiAccountEntity>()
                                    .eq(AiAccountEntity::getAccountKey, accountKey)
                                    .eq(AiAccountEntity::getEnvCode, envCode)
                                    .eq(AiAccountEntity::getDeleted, false)
                                    .last("LIMIT 1"));
            if (account == null) {
                throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "未找到账号: " + accountKey);
            }
            RuntimeChatParamMergeSupport.mergeModelSettingFromAccount(param.getModelSetting(), account);
        }

        return astroModelFactory.createModel(param, EmbeddingModel.class);
    }

    private static Path resolveUploadedPath(String relativePath) {
        String p = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
        return Path.of(System.getProperty("user.dir"), p).normalize();
    }

    private static String readStrictUtf8Text(Path filePath) throws IOException, CharacterCodingException {
        byte[] bytes = Files.readAllBytes(filePath);
        CharsetDecoder dec =
                StandardCharsets.UTF_8
                        .newDecoder()
                        .onMalformedInput(CodingErrorAction.REPORT)
                        .onUnmappableCharacter(CodingErrorAction.REPORT);
        return dec.decode(ByteBuffer.wrap(bytes)).toString();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR);
        }
        for (long id : ids) {
            AiVecDocEntity doc = getById(id);
            if (doc == null) {
                continue;
            }
            List<AiVecSegmentEntity> segments =
                    aiVecSegmentService.list(
                            new LambdaQueryWrapper<AiVecSegmentEntity>()
                                    .eq(AiVecSegmentEntity::getDocId, id));
            if (segments != null && !segments.isEmpty()) {
                long[] segmentIds = segments.stream()
                        .map(AiVecSegmentEntity::getId)
                        .filter(java.util.Objects::nonNull)
                        .mapToLong(Long::longValue)
                        .toArray();
                if (segmentIds.length > 0) {
                    aiVecSegmentService.delete(segmentIds);
                }
            }
            removeById(id);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<String> update(AiVecDocUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR);
        }
        AiVecDocEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_NOT_FOUND);
        }
        AiVecDocEntity entity = new AiVecDocEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiVecDocResponseDTO> queryPage(BasePageRequest<AiVecDocQueryRequestDTO> request) {
        IPage<AiVecDocResponseDTO> page = PageUtils.buildPage(request);
        AiVecDocQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiVecDocQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiVecDocResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }

    @Override
    public BaseResponse<AiVecDocResponseDTO> detail(Long id) {
        AiVecDocEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_NOT_FOUND);
        }
        AiVecDocResponseDTO responseDTO = new AiVecDocResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }
}
