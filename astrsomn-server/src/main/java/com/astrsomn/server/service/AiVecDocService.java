package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.core.common.base.BasePageRequest;
import com.astrsomn.core.common.base.BaseResponse;
import com.astrsomn.core.common.base.PageResponse;
import com.astrsomn.core.common.dto.vecdoc.AiVecDocCreateRequestDTO;
import com.astrsomn.core.common.dto.vecdoc.AiVecDocQueryRequestDTO;
import com.astrsomn.core.common.dto.vecdoc.AiVecDocResponseDTO;
import com.astrsomn.core.common.dto.vecdoc.AiVecDocUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiVecDocEntity;
import org.springframework.web.multipart.MultipartFile;

public interface AiVecDocService extends IService<AiVecDocEntity> {

    BaseResponse<String> create(AiVecDocCreateRequestDTO request);

    /**
     * 保存上传文件并插入待向量化文档记录。
     */
    BaseResponse<AiVecDocResponseDTO> upload(MultipartFile file, Long collectionId);

    /**
     * 将待向量化文档读入、切分、嵌入并写入向量库与切片表。
     */
    BaseResponse<String> vectorize(Long id);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiVecDocUpdateRequestDTO request);

    PageResponse<AiVecDocResponseDTO> queryPage(BasePageRequest<AiVecDocQueryRequestDTO> request);

    BaseResponse<AiVecDocResponseDTO> detail(Long id);
}
