package com.astrsomn.server.service;

import com.astrsomn.api.vector.dto.vecfolder.AiVecFolderCreateRequestDTO;
import com.astrsomn.api.vector.dto.vecfolder.AiVecFolderQueryRequestDTO;
import com.astrsomn.api.vector.dto.vecfolder.AiVecFolderResponseDTO;
import com.astrsomn.api.vector.dto.vecfolder.AiVecFolderUpdateRequestDTO;
import com.astrsomn.api.vector.entity.AiVecFolderEntity;
import com.astrsomn.common.base.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AiVecFolderService extends IService<AiVecFolderEntity> {

    BaseResponse<String> create(AiVecFolderCreateRequestDTO request);

    BaseResponse<String> update(AiVecFolderUpdateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    List<AiVecFolderResponseDTO> queryList(AiVecFolderQueryRequestDTO param);

    BaseResponse<String> moveDocs(long[] docIds, Long folderId);
}
