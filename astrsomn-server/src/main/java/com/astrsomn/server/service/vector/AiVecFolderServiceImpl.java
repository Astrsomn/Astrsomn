package com.astrsomn.server.service.vector;

import com.astrsomn.api.vector.dto.vecfolder.AiVecFolderCreateRequestDTO;
import com.astrsomn.api.vector.dto.vecfolder.AiVecFolderQueryRequestDTO;
import com.astrsomn.api.vector.dto.vecfolder.AiVecFolderResponseDTO;
import com.astrsomn.api.vector.dto.vecfolder.AiVecFolderUpdateRequestDTO;
import com.astrsomn.api.vector.entity.AiVecDocEntity;
import com.astrsomn.api.vector.entity.AiVecFolderEntity;
import com.astrsomn.common.base.BaseResponse;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AiVecFolderServiceImpl extends ServiceImpl<AiVecFolderMapper, AiVecFolderEntity> implements AiVecFolderService {

    private final AiVecDocService aiVecDocService;

    @Override
    public BaseResponse<String> create(AiVecFolderCreateRequestDTO request) {
        if (request.getCollectionId() == null) {
            return BaseResponse.fail("collectionId 不能为空");
        }
        if (StringUtils.isBlank(request.getFolderName())) {
            return BaseResponse.fail("文件夹名称不能为空");
        }
        AiVecFolderEntity entity = new AiVecFolderEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        if (!result) {
            return BaseResponse.fail("创建文件夹失败");
        }
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<String> update(AiVecFolderUpdateRequestDTO request) {
        if (request.getId() == null) {
            return BaseResponse.fail("id 不能为空");
        }
        AiVecFolderEntity existing = getById(request.getId());
        if (existing == null) {
            return BaseResponse.fail("文件夹不存在");
        }
        AiVecFolderEntity entity = new AiVecFolderEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            return BaseResponse.fail("更新文件夹失败");
        }
        return BaseResponse.success("success");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) {
            return BaseResponse.fail("id 不能为空");
        }
        for (long id : ids) {
            AiVecFolderEntity folder = getById(id);
            if (folder == null) {
                continue;
            }
            // 将子文件夹的 parentId 置空
            List<AiVecFolderEntity> children = list(
                    new LambdaQueryWrapper<AiVecFolderEntity>()
                            .eq(AiVecFolderEntity::getParentId, id));
            for (AiVecFolderEntity child : children) {
                child.setParentId(null);
                updateById(child);
            }
            // 将关联文档的 folderId 置空
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
        if (docIds == null || docIds.length == 0) {
            return BaseResponse.fail("文档 ID 不能为空");
        }
        for (long docId : docIds) {
            AiVecDocEntity doc = aiVecDocService.getById(docId);
            if (doc == null) {
                continue;
            }
            doc.setFolderId(folderId);
            aiVecDocService.updateById(doc);
        }
        return BaseResponse.success("移动成功");
    }
}
