package com.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import com.astrsomn.core.common.base.BaseController;
import com.astrsomn.core.common.base.BasePageRequest;
import com.astrsomn.core.common.base.BaseResponse;
import com.astrsomn.core.common.base.PageResponse;
import com.astrsomn.core.common.dto.vecdoc.AiVecDocCreateRequestDTO;
import com.astrsomn.core.common.dto.vecdoc.AiVecDocQueryRequestDTO;
import com.astrsomn.core.common.dto.vecdoc.AiVecDocResponseDTO;
import com.astrsomn.core.common.dto.vecdoc.AiVecDocUpdateRequestDTO;
import com.astrsomn.core.common.dto.vecdoc.AiVecDocVectorizeRequestDTO;
import com.astrsomn.server.service.AiVecDocService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/astro/ai-vec-doc")
@RequiredArgsConstructor
public class AiVecDocController extends BaseController {

    private final AiVecDocService aiVecDocService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiVecDocCreateRequestDTO request) {
        return aiVecDocService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiVecDocService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiVecDocUpdateRequestDTO request) {
        return aiVecDocService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiVecDocResponseDTO> queryPage(@RequestBody BasePageRequest<AiVecDocQueryRequestDTO> request) {
        return aiVecDocService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiVecDocResponseDTO> detail(@RequestParam("id") Long id) {
        return aiVecDocService.detail(id);
    }

    @PostMapping("/upload")
    public BaseResponse<AiVecDocResponseDTO> upload(
            @RequestParam("file") MultipartFile file, @RequestParam("collectionId") Long collectionId) {
        return aiVecDocService.upload(file, collectionId);
    }

    @PostMapping("/vectorize")
    public BaseResponse<String> vectorize(@RequestBody AiVecDocVectorizeRequestDTO request) {
        return aiVecDocService.vectorize(request.getId());
    }
}
