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
import com.astrsomn.api.storage.entity.AstFileRecordEntity;
import com.astrsomn.api.storage.exception.AstFileErrorEnum;
import com.astrsomn.api.vector.constant.AiVecChunkStrategyEnum;
import com.astrsomn.api.vector.constant.AiVecDocEnum;
import com.astrsomn.api.vector.constant.VecDocMetadataKeys;
import com.astrsomn.api.vector.dto.vecdoc.*;
import com.astrsomn.api.vector.entity.AiVecDocEntity;
import com.astrsomn.api.vector.entity.AiVecSegmentEntity;
import com.astrsomn.api.vector.exception.AstVecDocErrorEnum;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.common.utils.CollectionUtils;
import com.astrsomn.common.utils.CryptoUtil;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.internal.storage.config.StorageProperties;
import com.astrsomn.internal.storage.service.AstrsomnStorageClient;
import com.astrsomn.internal.storage.service.model.StorageDownloadRequest;
import com.astrsomn.internal.storage.service.model.StorageUploadRequest;
import com.astrsomn.internal.storage.service.model.StorageUploadResult;
import com.astrsomn.server.mapper.AiVecDocMapper;
import com.astrsomn.server.service.file.AstroFileRecordService;
import com.astrsomn.server.service.vector.document.chunk.ChunkStrategy;
import com.astrsomn.server.service.vector.document.chunk.ChunkStrategyResolver;
import com.astrsomn.server.service.vector.document.parser.DocumentParserRegistry;

import com.astrsomn.starter.runtime.langchain.factory.AstroModelFactory;
import com.astrsomn.starter.runtime.langchain.runtime.chain.RuntimeChatParamMergeSupport;
import com.astrsomn.starter.runtime.mapper.AstAiAccountMapper;
import com.astrsomn.starter.runtime.mapper.AstAiInstanceMapper;
import com.astrsomn.starter.runtime.mapper.AstAiModelMapper;
import com.astrsomn.starter.runtime.vector.AstroVecSourceFactory;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiVecDocServiceImpl extends ServiceImpl<AiVecDocMapper, AiVecDocEntity> implements AiVecDocService {

    private static final int DEFAULT_CHUNK_SIZE = 800;
    private static final int DEFAULT_CHUNK_OVERLAP = 100;


    private final AstroVecSourceFactory astroVecSourceFactory;
    private final AstroModelFactory astroModelFactory;
    private final AiVecStoreService aiVecStoreService;
    private final AstAiInstanceMapper aiInstanceMapper;
    private final AstAiModelMapper aiModelMapper;
    private final AstAiAccountMapper astAiAccountMapper;
    private final AiVecSegmentService aiVecSegmentService;
    private final AstrsomnStorageClient astrsomnStorageClient;
    private final AstroFileRecordService astroFileRecordService;
    private final StorageProperties storageProperties;

    @Override
    public BaseResponse<String> create(AiVecDocCreateRequestDTO request) {
        AiVecDocEntity entity = new AiVecDocEntity();
        BeanUtils.copyProperties(request, entity);
        if (StringUtils.isBlank(entity.getSyncStatus())) {
            entity.setSyncStatus(AiVecDocEnum.SyncStatus.PENDING.getCode());
        }
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_CREATE_FAILED);
        }
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<AiVecDocResponseDTO> upload(MultipartFile file, Long collectionId, Long folderId) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "File is empty");
        }
        if (Objects.isNull(collectionId)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "collectionId is required");
        }
        com.astrsomn.api.vector.entity.AiVecStoreEntity store = aiVecStoreService.getById(collectionId);
        if (Objects.isNull(store)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_STORE_NOT_FOUND);
        }

        String originalFileName = file.getOriginalFilename();
        if (Objects.isNull(originalFileName) || !DocumentParserRegistry.isSupported(originalFileName)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_FILE_NOT_TEXT, "Supported formats: .txt, .pdf, .doc, .docx, .md");
        }

        StorageUploadResult uploadResult = astrsomnStorageClient.upload(StorageUploadRequest.builder()
                .file(file)
                .bizType(storageProperties.getVecDocBizType())
                .objectId(String.valueOf(collectionId))
                .objectType("collection")
                .build());
        AstFileRecordEntity fileRow = new AstFileRecordEntity();
        fileRow.setBizType(storageProperties.getVecDocBizType());
        fileRow.setBizId(String.valueOf(collectionId));
        fileRow.setPlatform(uploadResult.getPlatform());
        fileRow.setBucket(uploadResult.getBucket());
        fileRow.setObjectKey(uploadResult.getObjectKey());
        fileRow.setOriginName(uploadResult.getOriginalFilename());
        fileRow.setMimeType(uploadResult.getContentType());
        fileRow.setFileSize(uploadResult.getSize());
        fileRow.setEtag(uploadResult.getEtag());
        fileRow.setFileUrl(uploadResult.getUrl());
        fileRow.setStatus("ACTIVE");
        if (!astroFileRecordService.save(fileRow)) {
            throw new BusinessException(AstFileErrorEnum.FILE_RECORD_CREATE_FAILED);
        }

        // 检查同文件夹下是否已存在同名文件
        Long existCount = count(new LambdaQueryWrapper<AiVecDocEntity>()
                .eq(AiVecDocEntity::getCollectionId, collectionId)
                .eq(AiVecDocEntity::getOriginalFileName, originalFileName)
                .eq(folderId != null, AiVecDocEntity::getFolderId, folderId)
                .isNull(folderId == null, AiVecDocEntity::getFolderId));
        if (Objects.nonNull(existCount) && existCount > 0) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_DUPLICATE_FILE,
                    "Duplicate file in folder: " + originalFileName);
        }

        AiVecDocEntity entity = new AiVecDocEntity();
        entity.setCollectionId(collectionId);
        entity.setFilePath(uploadResult.getObjectKey());
        entity.setFileRecordId(fileRow.getId());
        entity.setOriginalFileName(originalFileName);
        entity.setContentSummary(originalFileName);
        entity.setFolderId(folderId);
        entity.setSyncStatus(AiVecDocEnum.SyncStatus.PENDING.getCode());
        entity.setDocIdInStore(null);

        boolean saved = save(entity);
        if (!saved) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_CREATE_FAILED);
        }

        AiVecDocResponseDTO dto = new AiVecDocResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return BaseResponse.success(dto);
    }

    @Override
    public BaseResponse<String> vectorize(Long id) {
        if (Objects.isNull(id)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR);
        }
        AiVecDocEntity doc = getById(id);
        if (Objects.isNull(doc)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_NOT_FOUND);
        }
        String statusRaw = StringUtils.trimToNull(doc.getSyncStatus());
        if (Objects.isNull(statusRaw)
                || (!AiVecDocEnum.SyncStatus.PENDING.getCode().equalsIgnoreCase(statusRaw)
                    && !AiVecDocEnum.SyncStatus.CHUNKED.getCode().equalsIgnoreCase(statusRaw))) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_STATUS_INVALID);
        }
        if (Objects.isNull(doc.getCollectionId())) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "Document not associated with a vector collection");
        }
        if (StringUtils.isBlank(doc.getFilePath())) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_FILE_NOT_READABLE, "File path is empty");
        }

        com.astrsomn.api.vector.entity.AiVecStoreEntity store = aiVecStoreService.getById(doc.getCollectionId());
        if (Objects.isNull(store)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_STORE_NOT_FOUND);
        }
        if (StringUtils.isBlank(store.getInstanceKey())) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "Vector store has no embedding instanceKey configured");
        }

        String taskId = UUID.randomUUID().toString().replace("-", "");
        doc.setSyncStatus(AiVecDocEnum.SyncStatus.VECTORING.getCode());
        doc.setVectorizeTaskId(taskId);
        doc.setVectorizeProgress(0);
        doc.setVectorizeMsg("Preparing...");
        doc.setTotalSegments(0);
        doc.setDoneSegments(0);
        updateById(doc);

        doVectorizeAsync(id, taskId);

        return BaseResponse.success(taskId);
    }

    @Override
    public AiVecDocVectorizeProgressDTO getVectorizeProgress(Long id) {
        AiVecDocEntity doc = getById(id);
        if (Objects.isNull(doc)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_NOT_FOUND);
        }
        AiVecDocVectorizeProgressDTO dto = new AiVecDocVectorizeProgressDTO();
        dto.setTaskId(doc.getVectorizeTaskId());
        dto.setStatus(doc.getSyncStatus());
        dto.setProgress(doc.getVectorizeProgress());
        dto.setMessage(doc.getVectorizeMsg());
        dto.setTotalSegments(doc.getTotalSegments());
        dto.setDoneSegments(doc.getDoneSegments());
        return dto;
    }

    @org.springframework.scheduling.annotation.Async("vectorizeExecutor")
    public void doVectorizeAsync(Long docId, String taskId) {
        try {
            AiVecDocEntity doc = getById(docId);
            if (Objects.nonNull(doc) && AiVecDocEnum.SyncStatus.CHUNKED.getCode().equalsIgnoreCase(doc.getSyncStatus())) {
                doVectorizeFromChunks(docId);
            } else {
                doVectorizeSync(docId);
            }
        } catch (BusinessException e) {
            log.error("vectorize async failed docId={}", docId, e);
            markVectorizeFailed(docId, e.getMessage());
        } catch (Exception e) {
            log.error("vectorize async unexpected error docId={}", docId, e);
            markVectorizeFailed(docId, "Vectorization error: " + e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void doVectorizeSync(Long docId) {
        AiVecDocEntity doc = getById(docId);
        if (Objects.isNull(doc)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_NOT_FOUND);
        }

        updateProgress(docId, 5, "Parsing document...");

        com.astrsomn.api.vector.entity.AiVecStoreEntity store = aiVecStoreService.getById(doc.getCollectionId());
        if (Objects.isNull(store)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_STORE_NOT_FOUND);
        }

        VecSource vecSource =
                astroVecSourceFactory
                        .tryGetActiveSource(store.getSourceId())
                        .orElseThrow(() -> new BusinessException(AstVecDocErrorEnum.DOC_SOURCE_NOT_READY));

        VecStore vecStore = vecSource.openStore(store);
        EmbeddingStore<TextSegment> embeddingStore = vecStore.getEmbeddingStore();

        String envCode = StringUtils.trimToNull(doc.getEnvCode());

        EmbeddingModel embeddingModel = resolveEmbeddingModel(store);

        String fullText;
        String fileName = Objects.nonNull(doc.getOriginalFileName()) ? doc.getOriginalFileName() : "unknown.txt";
        try (InputStream inputStream = openDocInputStream(doc)) {
            fullText = DocumentParserRegistry.parseFile(fileName, inputStream);
        } catch (IOException e) {
            log.error("parse file failed key={}", doc.getFilePath(), e);
            throw new BusinessException(AstVecDocErrorEnum.DOC_FILE_NOT_READABLE, e.getMessage());
        }

        if (StringUtils.isBlank(fullText)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "File content is empty");
        }

        updateProgress(docId, 15, "Splitting into chunks...");

        int chunkSize = Objects.nonNull(store.getChunkSize()) && store.getChunkSize() > 0
                ? store.getChunkSize() : DEFAULT_CHUNK_SIZE;
        int chunkOverlap = Objects.nonNull(store.getChunkOverlap()) && store.getChunkOverlap() >= 0
                ? store.getChunkOverlap() : DEFAULT_CHUNK_OVERLAP;
        AiVecChunkStrategyEnum strategy = AiVecChunkStrategyEnum.fromCodeOrDefault(store.getChunkStrategy());
        String instructionPrefix = StringUtils.trimToNull(store.getInstructionPrefix());

        String logicalDocId = UUID.randomUUID().toString().replace("-", "");
        ChunkStrategy chunkStrategy = ChunkStrategyResolver.resolve(strategy);
        List<TextSegment> splitSegments = chunkStrategy.split(Document.from(fullText), chunkSize, chunkOverlap);
        List<TextSegment> embeddedSegments = new ArrayList<>(splitSegments.size());
        for (TextSegment ts : splitSegments) {
            Metadata meta = Objects.nonNull(ts.metadata()) ? ts.metadata().copy() : new Metadata();
            meta.put(VecDocMetadataKeys.DOC_ID_IN_STORE, logicalDocId);
            String segText = Objects.nonNull(instructionPrefix)
                    ? instructionPrefix + ts.text()
                    : ts.text();
            embeddedSegments.add(TextSegment.from(segText, meta));
        }

        int totalSegs = embeddedSegments.size();
        updateTotalSegments(docId, totalSegs);
        updateProgress(docId, 20, totalSegs + " chunks, generating embeddings...");

        int batchSize = 10;
        List<Embedding> allEmbeddings = new ArrayList<>(totalSegs);
        for (int i = 0; i < totalSegs; i += batchSize) {
            int end = Math.min(i + batchSize, totalSegs);
            List<TextSegment> batch = embeddedSegments.subList(i, end);
            Response<List<Embedding>> embedResp;
            try {
                embedResp = embeddingModel.embedAll(batch);
            } catch (RuntimeException e) {
                log.error("embed failed docId={}", docId, e);
                throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_FAILED, e.getMessage());
            }
            List<Embedding> batchEmbeddings = embedResp.content();
            if (Objects.isNull(batchEmbeddings) || batchEmbeddings.size() != batch.size()) {
                throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_FAILED, "Embedding result count does not match chunk count");
            }
            allEmbeddings.addAll(batchEmbeddings);

            int done = end;
            int progress = 20 + (int) ((done * 60.0) / totalSegs);
            updateDoneSegments(docId, done, progress, "Generating embeddings... (" + done + "/" + totalSegs + ")");
        }

        updateProgress(docId, 80, "Writing to vector store...");

        List<String> vectorIds;
        try {
            vectorIds = embeddingStore.addAll(allEmbeddings, embeddedSegments);
        } catch (RuntimeException e) {
            log.error("embedding store add failed docId={}", docId, e);
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_FAILED, e.getMessage());
        }
        if (Objects.isNull(vectorIds) || vectorIds.size() != embeddedSegments.size()) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_FAILED, "Vector store returned ID count does not match chunk count");
        }

        updateProgress(docId, 90, "Saving segment records...");

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
            rows.add(row);
        }
        boolean segOk = aiVecSegmentService.saveBatch(rows);
        if (!segOk) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_FAILED, "Failed to save segment records");
        }

        String summary = fullText.length() > 500 ? fullText.substring(0, 500) : fullText;
        doc = new AiVecDocEntity();
        doc.setId(docId);
        doc.setDocIdInStore(logicalDocId);
        doc.setSyncStatus(AiVecDocEnum.SyncStatus.STORED.getCode());
        doc.setContentSummary(summary);
        doc.setVectorizeProgress(100);
        doc.setVectorizeMsg("Vectorization complete");
        updateById(doc);
    }

    private void updateProgress(Long docId, int progress, String msg) {
        AiVecDocEntity update = new AiVecDocEntity();
        update.setId(docId);
        update.setVectorizeProgress(progress);
        update.setVectorizeMsg(msg);
        updateById(update);
    }

    private void updateTotalSegments(Long docId, int total) {
        AiVecDocEntity update = new AiVecDocEntity();
        update.setId(docId);
        update.setTotalSegments(total);
        updateById(update);
    }

    private void updateDoneSegments(Long docId, int done, int progress, String msg) {
        AiVecDocEntity update = new AiVecDocEntity();
        update.setId(docId);
        update.setDoneSegments(done);
        update.setVectorizeProgress(progress);
        update.setVectorizeMsg(msg);
        updateById(update);
    }

    private void markVectorizeFailed(Long docId, String message) {
        try {
            AiVecDocEntity update = new AiVecDocEntity();
            update.setId(docId);
            update.setSyncStatus(AiVecDocEnum.SyncStatus.FAILED.getCode());
            update.setVectorizeMsg(message);
            updateById(update);
        } catch (Exception e) {
            log.error("markVectorizeFailed error docId={}", docId, e);
        }
    }

    // ========== chunk: parse + split + save segments ==========

    @Override
    public BaseResponse<String> chunk(Long id) {
        if (Objects.isNull(id)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR);
        }
        AiVecDocEntity doc = getById(id);
        if (Objects.isNull(doc)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_NOT_FOUND);
        }
        String statusRaw = StringUtils.trimToNull(doc.getSyncStatus());
        if (Objects.isNull(statusRaw)
                || (!AiVecDocEnum.SyncStatus.PENDING.getCode().equalsIgnoreCase(statusRaw)
                    && !AiVecDocEnum.SyncStatus.FAILED.getCode().equalsIgnoreCase(statusRaw))) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_STATUS_INVALID, "Only PENDING or FAILED documents can be chunked");
        }
        if (Objects.isNull(doc.getCollectionId())) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "Document not associated with a vector collection");
        }
        if (StringUtils.isBlank(doc.getFilePath())) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_FILE_NOT_READABLE, "File path is empty");
        }

        String taskId = UUID.randomUUID().toString().replace("-", "");
        doc.setSyncStatus(AiVecDocEnum.SyncStatus.CHUNKING.getCode());
        doc.setVectorizeTaskId(taskId);
        doc.setVectorizeProgress(0);
        doc.setVectorizeMsg("Preparing to chunk...");
        doc.setTotalSegments(0);
        doc.setDoneSegments(0);
        updateById(doc);

        doChunkAsync(id, taskId);

        return BaseResponse.success(taskId);
    }

    @org.springframework.scheduling.annotation.Async("vectorizeExecutor")
    public void doChunkAsync(Long docId, String taskId) {
        try {
            doChunkSync(docId);
        } catch (BusinessException e) {
            log.error("chunk async failed docId={}", docId, e);
            markVectorizeFailed(docId, e.getMessage());
        } catch (Exception e) {
            log.error("chunk async unexpected error docId={}", docId, e);
            markVectorizeFailed(docId, "Chunk error: " + e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void doChunkSync(Long docId) {
        AiVecDocEntity doc = getById(docId);
        if (Objects.isNull(doc)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_NOT_FOUND);
        }

        updateProgress(docId, 5, "Parsing document...");

        com.astrsomn.api.vector.entity.AiVecStoreEntity store = aiVecStoreService.getById(doc.getCollectionId());
        if (Objects.isNull(store)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_STORE_NOT_FOUND);
        }

        String fullText;
        String fileName = Objects.nonNull(doc.getOriginalFileName()) ? doc.getOriginalFileName() : "unknown.txt";
        try (InputStream inputStream = openDocInputStream(doc)) {
            fullText = DocumentParserRegistry.parseFile(fileName, inputStream);
        } catch (IOException e) {
            log.error("parse file failed key={}", doc.getFilePath(), e);
            throw new BusinessException(AstVecDocErrorEnum.DOC_FILE_NOT_READABLE, e.getMessage());
        }

        if (StringUtils.isBlank(fullText)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "File content is empty");
        }

        updateProgress(docId, 30, "Splitting into chunks...");

        int chunkSize = Objects.nonNull(store.getChunkSize()) && store.getChunkSize() > 0
                ? store.getChunkSize() : DEFAULT_CHUNK_SIZE;
        int chunkOverlap = Objects.nonNull(store.getChunkOverlap()) && store.getChunkOverlap() >= 0
                ? store.getChunkOverlap() : DEFAULT_CHUNK_OVERLAP;
        AiVecChunkStrategyEnum strategy = AiVecChunkStrategyEnum.fromCodeOrDefault(store.getChunkStrategy());
        String instructionPrefix = StringUtils.trimToNull(store.getInstructionPrefix());

        String logicalDocId = UUID.randomUUID().toString().replace("-", "");
        ChunkStrategy chunkStrategy = ChunkStrategyResolver.resolve(strategy);
        List<TextSegment> splitSegments = chunkStrategy.split(Document.from(fullText), chunkSize, chunkOverlap);
        List<TextSegment> processedSegments = new ArrayList<>(splitSegments.size());
        for (TextSegment ts : splitSegments) {
            Metadata meta = Objects.nonNull(ts.metadata()) ? ts.metadata().copy() : new Metadata();
            meta.put(VecDocMetadataKeys.DOC_ID_IN_STORE, logicalDocId);
            String segText = Objects.nonNull(instructionPrefix)
                    ? instructionPrefix + ts.text()
                    : ts.text();
            processedSegments.add(TextSegment.from(segText, meta));
        }

        int totalSegs = processedSegments.size();
        updateTotalSegments(docId, totalSegs);
        updateProgress(docId, 60, totalSegs + " chunks, saving...");

        List<AiVecSegmentEntity> rows = new ArrayList<>(totalSegs);
        long idx = 0;
        for (TextSegment seg : processedSegments) {
            AiVecSegmentEntity row = new AiVecSegmentEntity();
            row.setDocId(doc.getId());
            row.setCollectionId(doc.getCollectionId());
            row.setSegmentContent(seg.text());
            row.setWordCount((long) seg.text().length());
            row.setChunkIndex(idx++);
            rows.add(row);
        }

        updateProgress(docId, 80, "Saving segment records...");
        boolean segOk = aiVecSegmentService.saveBatch(rows);
        if (!segOk) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_FAILED, "Failed to save segment records");
        }

        String summary = fullText.length() > 500 ? fullText.substring(0, 500) : fullText;
        doc = new AiVecDocEntity();
        doc.setId(docId);
        doc.setDocIdInStore(logicalDocId);
        doc.setSyncStatus(AiVecDocEnum.SyncStatus.CHUNKED.getCode());
        doc.setContentSummary(summary);
        doc.setVectorizeProgress(100);
        doc.setVectorizeMsg("Chunking complete");
        doc.setDoneSegments(totalSegs);
        updateById(doc);
    }

    // ========== vectorize from existing chunks ==========

    @Transactional(rollbackFor = Exception.class)
    public void doVectorizeFromChunks(Long docId) {
        AiVecDocEntity doc = getById(docId);
        if (Objects.isNull(doc)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_NOT_FOUND);
        }

        updateProgress(docId, 5, "Loading chunks...");

        com.astrsomn.api.vector.entity.AiVecStoreEntity store = aiVecStoreService.getById(doc.getCollectionId());
        if (Objects.isNull(store)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_STORE_NOT_FOUND);
        }

        VecSource vecSource =
                astroVecSourceFactory
                        .tryGetActiveSource(store.getSourceId())
                        .orElseThrow(() -> new BusinessException(AstVecDocErrorEnum.DOC_SOURCE_NOT_READY));

        VecStore vecStore = vecSource.openStore(store);
        EmbeddingStore<TextSegment> embeddingStore = vecStore.getEmbeddingStore();




        EmbeddingModel embeddingModel = resolveEmbeddingModel(store);
        String instructionPrefix = StringUtils.trimToNull(store.getInstructionPrefix());

        // Load segments from DB
        List<AiVecSegmentEntity> dbSegments = aiVecSegmentService.list(
                new LambdaQueryWrapper<AiVecSegmentEntity>()
                        .eq(AiVecSegmentEntity::getDocId, docId)
                        .orderByAsc(AiVecSegmentEntity::getChunkIndex));
        if (CollectionUtils.isEmpty(dbSegments)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "No segment records found, run chunk first");
        }

        String logicalDocId = StringUtils.trimToNull(doc.getDocIdInStore());
        if (Objects.isNull(logicalDocId)) {
            logicalDocId = UUID.randomUUID().toString().replace("-", "");
        }

        int totalSegs = dbSegments.size();
        List<TextSegment> embeddedSegments = new ArrayList<>(totalSegs);
        for (AiVecSegmentEntity seg : dbSegments) {
            Metadata meta = new Metadata();
            meta.put(VecDocMetadataKeys.DOC_ID_IN_STORE, logicalDocId);
            String segText = Objects.nonNull(instructionPrefix)
                    ? instructionPrefix + seg.getSegmentContent()
                    : seg.getSegmentContent();
            embeddedSegments.add(TextSegment.from(segText, meta));
        }

        updateTotalSegments(docId, totalSegs);
        updateProgress(docId, 10, totalSegs + " chunks, generating embeddings...");

        int batchSize = 10;
        List<Embedding> allEmbeddings = new ArrayList<>(totalSegs);
        for (int i = 0; i < totalSegs; i += batchSize) {
            int end = Math.min(i + batchSize, totalSegs);
            List<TextSegment> batch = embeddedSegments.subList(i, end);
            Response<List<Embedding>> embedResp;
            try {
                embedResp = embeddingModel.embedAll(batch);
            } catch (RuntimeException e) {
                log.error("embed failed docId={}", docId, e);
                throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_FAILED, e.getMessage());
            }
            List<Embedding> batchEmbeddings = embedResp.content();
            if (Objects.isNull(batchEmbeddings) || batchEmbeddings.size() != batch.size()) {
                throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_FAILED, "Embedding result count does not match chunk count");
            }
            allEmbeddings.addAll(batchEmbeddings);

            int done = end;
            int progress = 10 + (int) ((done * 60.0) / totalSegs);
            updateDoneSegments(docId, done, progress, "Generating embeddings... (" + done + "/" + totalSegs + ")");
        }

        updateProgress(docId, 75, "Writing to vector store...");

        List<String> vectorIds;
        try {
            vectorIds = embeddingStore.addAll(allEmbeddings, embeddedSegments);
        } catch (RuntimeException e) {
            log.error("embedding store add failed docId={}", docId, e);
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_FAILED, e.getMessage());
        }
        if (Objects.isNull(vectorIds) || vectorIds.size() != embeddedSegments.size()) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_FAILED, "Vector store returned ID count does not match chunk count");
        }

        updateProgress(docId, 90, "Updating segment records...");

        for (int i = 0; i < dbSegments.size(); i++) {
            AiVecSegmentEntity seg = dbSegments.get(i);
            seg.setVectorId(vectorIds.get(i));
        }
        boolean segOk = aiVecSegmentService.updateBatchById(dbSegments);
        if (!segOk) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_FAILED, "Failed to update segment vector IDs");
        }

        doc = new AiVecDocEntity();
        doc.setId(docId);
        doc.setDocIdInStore(logicalDocId);
        doc.setSyncStatus(AiVecDocEnum.SyncStatus.STORED.getCode());
        doc.setVectorizeProgress(100);
        doc.setVectorizeMsg("Vectorization complete");
        updateById(doc);
    }

    // ========== reChunk ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> reChunk(Long id) {
        if (Objects.isNull(id)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR);
        }
        AiVecDocEntity doc = getById(id);
        if (Objects.isNull(doc)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_NOT_FOUND);
        }
        String status = StringUtils.trimToNull(doc.getSyncStatus());
        if (Objects.isNull(status)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_STATUS_INVALID);
        }
        String upperStatus = status.toUpperCase();
        if (!AiVecDocEnum.SyncStatus.CHUNKED.getCode().equalsIgnoreCase(upperStatus)
                && !AiVecDocEnum.SyncStatus.STORED.getCode().equalsIgnoreCase(upperStatus)
                && !AiVecDocEnum.SyncStatus.FAILED.getCode().equalsIgnoreCase(upperStatus)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_STATUS_INVALID, "Only CHUNKED, STORED or FAILED documents can be re-chunked");
        }

        com.astrsomn.api.vector.entity.AiVecStoreEntity store = aiVecStoreService.getById(doc.getCollectionId());
        if (Objects.isNull(store)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_STORE_NOT_FOUND);
        }

        if (AiVecDocEnum.SyncStatus.STORED.getCode().equalsIgnoreCase(upperStatus) && StringUtils.isNotBlank(doc.getDocIdInStore())) {
            try {
                VecSource vecSource = astroVecSourceFactory.tryGetActiveSource(store.getSourceId())
                        .orElse(null);
                if (Objects.nonNull(vecSource)) {
                    VecStore vecStore = vecSource.openStore(store);
                    var vecDoc = vecStore.bindDoc(doc);
                    vecDoc.deleteAllEmbeddingsInStore();
                }
            } catch (Exception e) {
                log.warn("reChunk: failed to delete old vectors docId={}, continuing", id, e);
            }
        }

        // Delete old segments
        aiVecSegmentService.remove(new LambdaQueryWrapper<AiVecSegmentEntity>()
                .eq(AiVecSegmentEntity::getDocId, id));

        // Reset doc
        String taskId = UUID.randomUUID().toString().replace("-", "");
        doc.setSyncStatus(AiVecDocEnum.SyncStatus.CHUNKING.getCode());
        doc.setDocIdInStore(null);
        doc.setVectorizeTaskId(taskId);
        doc.setVectorizeProgress(0);
        doc.setVectorizeMsg("Preparing to re-chunk...");
        doc.setTotalSegments(0);
        doc.setDoneSegments(0);
        updateById(doc);

        doChunkAsync(id, taskId);

        return BaseResponse.success(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> reVectorize(Long id) {
        if (Objects.isNull(id)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR);
        }
        AiVecDocEntity doc = getById(id);
        if (Objects.isNull(doc)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_NOT_FOUND);
        }
        String status = StringUtils.trimToNull(doc.getSyncStatus());
        if (Objects.isNull(status)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_STATUS_INVALID);
        }
        String upperStatus = status.toUpperCase();
        if (!AiVecDocEnum.SyncStatus.STORED.getCode().equalsIgnoreCase(upperStatus)
                && !AiVecDocEnum.SyncStatus.FAILED.getCode().equalsIgnoreCase(upperStatus)
                && !AiVecDocEnum.SyncStatus.CHUNKED.getCode().equalsIgnoreCase(upperStatus)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_VECTORIZE_STATUS_INVALID, "Only CHUNKED, STORED or FAILED documents can be re-vectorized");
        }
        if (Objects.isNull(doc.getCollectionId())) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "Document not associated with a vector collection");
        }

        com.astrsomn.api.vector.entity.AiVecStoreEntity store = aiVecStoreService.getById(doc.getCollectionId());
        if (Objects.isNull(store)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_STORE_NOT_FOUND);
        }

        if (AiVecDocEnum.SyncStatus.STORED.getCode().equalsIgnoreCase(upperStatus) && StringUtils.isNotBlank(doc.getDocIdInStore())) {
            try {
                VecSource vecSource = astroVecSourceFactory.tryGetActiveSource(store.getSourceId())
                        .orElse(null);
                if (Objects.nonNull(vecSource)) {
                    VecStore vecStore = vecSource.openStore(store);
                    var vecDoc = vecStore.bindDoc(doc);
                    vecDoc.deleteAllEmbeddingsInStore();
                }
            } catch (Exception e) {
                log.warn("reVectorize: failed to delete old vectors docId={}, continuing", id, e);
            }
        }

        aiVecSegmentService.remove(new LambdaQueryWrapper<AiVecSegmentEntity>()
                .eq(AiVecSegmentEntity::getDocId, id));

        String taskId = UUID.randomUUID().toString().replace("-", "");
        doc.setSyncStatus(AiVecDocEnum.SyncStatus.VECTORING.getCode());
        doc.setDocIdInStore(null);
        doc.setVectorizeTaskId(taskId);
        doc.setVectorizeProgress(0);
        doc.setVectorizeMsg("Preparing to re-vectorize...");
        doc.setTotalSegments(0);
        doc.setDoneSegments(0);
        updateById(doc);

        doVectorizeAsync(id, taskId);

        return BaseResponse.success(taskId);
    }

    private EmbeddingModel resolveEmbeddingModel(com.astrsomn.api.vector.entity.AiVecStoreEntity store) {

        AiInstanceEntity instance =
                aiInstanceMapper.selectOne(
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
        AiModelEntity model =
                aiModelMapper.selectOne(
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
            AiAccountEntity account =
                    astAiAccountMapper.selectOne(
                            new LambdaQueryWrapper<AiAccountEntity>()
                                    .eq(AiAccountEntity::getAccountKey, accountKey)
                                    .eq(AiAccountEntity::getDeleted, false)
                                    .last("LIMIT 1"));
            if (Objects.isNull(account)) {
                throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR, "Account not found: " + accountKey);
            }
            if (account.getApiKey() != null) {
                account.setApiKey(CryptoUtil.decrypt(account.getApiKey()));
            }
            if (account.getApiSecret() != null) {
                account.setApiSecret(CryptoUtil.decrypt(account.getApiSecret()));
            }
            RuntimeChatParamMergeSupport.mergeModelSettingFromAccount(param.getModelSetting(), account);
        }

        return astroModelFactory.createModel(param, EmbeddingModel.class);
    }

    private InputStream openDocInputStream(AiVecDocEntity doc) {
        String key = StringUtils.trimToNull(doc.getFilePath());
        if (Objects.isNull(key)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_FILE_NOT_READABLE, "File path is empty");
        }
        String platform = storageProperties.getDefaultPlatform();
        if (Objects.nonNull(doc.getFileRecordId())) {
            AstFileRecordEntity fileRow = astroFileRecordService.getById(doc.getFileRecordId());
            if (Objects.nonNull(fileRow) && StringUtils.isNotBlank(fileRow.getPlatform())) {
                platform = fileRow.getPlatform();
            }
        }
        return astrsomnStorageClient.openInputStream(StorageDownloadRequest.builder()
                .platform(platform)
                .objectKey(key)
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> delete(long[] ids) {
        if (Objects.isNull(ids) || ids.length == 0) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR);
        }
        for (long id : ids) {
            AiVecDocEntity doc = getById(id);
            if (Objects.isNull(doc)) {
                continue;
            }
            List<AiVecSegmentEntity> segments =
                    aiVecSegmentService.list(
                            new LambdaQueryWrapper<AiVecSegmentEntity>()
                                    .eq(AiVecSegmentEntity::getDocId, id));
            if (!CollectionUtils.isEmpty(segments)) {
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
            cleanupFileObject(doc);
        }
        return BaseResponse.success("success");
    }

    private void cleanupFileObject(AiVecDocEntity doc) {
        if (StringUtils.isBlank(doc.getFilePath())) {
            return;
        }
        String platform = storageProperties.getDefaultPlatform();
        if (Objects.nonNull(doc.getFileRecordId())) {
            AstFileRecordEntity fileRow = astroFileRecordService.getById(doc.getFileRecordId());
            if (Objects.nonNull(fileRow)) {
                if (StringUtils.isNotBlank(fileRow.getPlatform())) {
                    platform = fileRow.getPlatform();
                }
                fileRow.setStatus("DELETED");
                astroFileRecordService.updateById(fileRow);
            }
        }
        astrsomnStorageClient.delete(StorageDownloadRequest.builder()
                .platform(platform)
                .objectKey(doc.getFilePath())
                .build());
    }

    @Override
    public BaseResponse<String> update(AiVecDocUpdateRequestDTO request) {
        if (Objects.isNull(request.getId())) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR);
        }
        AiVecDocEntity existing = getById(request.getId());
        if (Objects.isNull(existing)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_NOT_FOUND);
        }
        AiVecDocEntity entity = new AiVecDocEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_UPDATE_FAILED);
        }
        return BaseResponse.success("success");
    }

    @Override
    public PageResponse<AiVecDocResponseDTO> queryPage(BasePageRequest<AiVecDocQueryRequestDTO> request) {
        IPage<AiVecDocResponseDTO> page = PageUtils.buildPage(request);
        AiVecDocQueryRequestDTO param = request.getParam();
        if (Objects.isNull(param)) {
            param = new AiVecDocQueryRequestDTO();
        }
        IPage<AiVecDocResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }

    @Override
    public BaseResponse<AiVecDocResponseDTO> detail(Long id) {
        AiVecDocEntity entity = getById(id);
        if (Objects.isNull(entity)) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_NOT_FOUND);
        }
        AiVecDocResponseDTO responseDTO = new AiVecDocResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }
}
