package com.astrsomn.server.service.vector;

import com.astrsomn.api.vector.dto.vecfolder.AiVecFolderCreateRequestDTO;
import com.astrsomn.api.vector.dto.vecfolder.AiVecFolderQueryRequestDTO;
import com.astrsomn.api.vector.dto.vecfolder.AiVecFolderResponseDTO;
import com.astrsomn.api.vector.dto.vecfolder.AiVecFolderUpdateRequestDTO;
import com.astrsomn.api.vector.entity.AiVecDocEntity;
import com.astrsomn.api.vector.entity.AiVecFolderEntity;
import com.astrsomn.api.vector.exception.AstVecFolderErrorEnum;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.mapper.AiVecFolderMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiVecFolderServiceImpl extends ServiceImpl<AiVecFolderMapper, AiVecFolderEntity> implements AiVecFolderService {

    private final AiVecDocService aiVecDocService;

    @Override
    public BaseResponse<String> create(AiVecFolderCreateRequestDTO request) {
        if (Objects.isNull(request.getCollectionId())) {
            throw new BusinessException(AstVecFolderErrorEnum.FOLDER_PARAM_ERROR, "collectionId is required");
        }
        if (StringUtils.isBlank(request.getFolderName())) {
            throw new BusinessException(AstVecFolderErrorEnum.FOLDER_PARAM_ERROR, "Folder name is required");
        }
        AiVecFolderEntity entity = new AiVecFolderEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AstVecFolderErrorEnum.FOLDER_CREATE_FAILED);
        }
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<String> update(AiVecFolderUpdateRequestDTO request) {
        if (Objects.isNull(request.getId())) {
            throw new BusinessException(AstVecFolderErrorEnum.FOLDER_PARAM_ERROR, "id is required");
        }
        AiVecFolderEntity existing = getById(request.getId());
        if (Objects.isNull(existing)) {
            throw new BusinessException(AstVecFolderErrorEnum.FOLDER_NOT_FOUND);
        }
        AiVecFolderEntity entity = new AiVecFolderEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AstVecFolderErrorEnum.FOLDER_UPDATE_FAILED);
        }
        return BaseResponse.success("success");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> delete(long[] ids) {
        if (Objects.isNull(ids) || ids.length == 0) {
            throw new BusinessException(AstVecFolderErrorEnum.FOLDER_PARAM_ERROR, "ids is required");
        }
        for (long id : ids) {
            AiVecFolderEntity folder = getById(id);
            if (Objects.isNull(folder)) {
                continue;
            }
            List<AiVecFolderEntity> children = list(
                    new LambdaQueryWrapper<AiVecFolderEntity>()
                            .eq(AiVecFolderEntity::getParentId, id));
            for (AiVecFolderEntity child : children) {
                child.setParentId(null);
                updateById(child);
            }
            List<AiVecDocEntity> docs = aiVecDocService.list(
                    new LambdaQueryWrapper<AiVecDocEntity>()
                            .eq(AiVecDocEntity::getFolderId, id));
            for (AiVecDocEntity doc : docs) {
                doc.setFolderId(null);
                aiVecDocService.updateById(doc);
            }
            removeById(id);
        }
        return BaseResponse.success("success");
    }

    @Override
    public List<AiVecFolderResponseDTO> queryList(AiVecFolderQueryRequestDTO param) {
        return baseMapper.queryList(param);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> moveDocs(long[] docIds, Long folderId) {
        if (Objects.isNull(docIds) || docIds.length == 0) {
            throw new BusinessException(AstVecFolderErrorEnum.FOLDER_PARAM_ERROR, "docIds is required");
        }
        for (long docId : docIds) {
            AiVecDocEntity doc = aiVecDocService.getById(docId);
            if (Objects.isNull(doc)) {
                continue;
            }
            doc.setFolderId(folderId);
            aiVecDocService.updateById(doc);
        }
        return BaseResponse.success("success");
    }
}
