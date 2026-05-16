package com.astrsomn.server.api.vector;

import com.astrsomn.api.vector.dto.vecdoc.*;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.AiVecDocService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/vectorize-progress")
    public BaseResponse<AiVecDocVectorizeProgressDTO> vectorizeProgress(@RequestParam("id") Long id) {
        return BaseResponse.success(aiVecDocService.getVectorizeProgress(id));
    }

    @PostMapping("/re-vectorize")
    public BaseResponse<String> reVectorize(@RequestBody AiVecDocVectorizeRequestDTO request) {
        return aiVecDocService.reVectorize(request.getId());
    }
}
