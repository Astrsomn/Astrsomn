package com.astrsomn.server.service.vector;

import com.astrsomn.api.vector.dto.vecdoc.*;
import com.astrsomn.api.vector.entity.AiVecDocEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

public interface AiVecDocService extends IService<AiVecDocEntity> {

    BaseResponse<String> create(AiVecDocCreateRequestDTO request);

    
    BaseResponse<AiVecDocResponseDTO> upload(MultipartFile file, Long collectionId, Long folderId);

    
    BaseResponse<String> vectorize(Long id);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiVecDocUpdateRequestDTO request);

    PageResponse<AiVecDocResponseDTO> queryPage(BasePageRequest<AiVecDocQueryRequestDTO> request);

    BaseResponse<AiVecDocResponseDTO> detail(Long id);

    AiVecDocVectorizeProgressDTO getVectorizeProgress(Long id);

    BaseResponse<String> reVectorize(Long id);

    
    BaseResponse<String> chunk(Long id);

    
    BaseResponse<String> reChunk(Long id);

    void download(Long id, jakarta.servlet.http.HttpServletResponse response);
}
