package com.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.astrsomn.commn.base.BaseController;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.model.AiModelCreateRequestDTO;
import com.astrsomn.core.common.dto.model.AiModelQueryRequestDTO;
import com.astrsomn.core.common.dto.model.AiModelResponseDTO;
import com.astrsomn.core.common.dto.model.AiModelUpdateRequestDTO;
import com.astrsomn.server.service.AiModelService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/astro/ai-model")
@RequiredArgsConstructor
public class AiModelController extends BaseController {

    private final AiModelService aiModelService;


    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        log.debug("delete ai-model ids={}", ids);
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiModelService.delete(longIds);
        } catch (NumberFormatException e) {
            log.warn("delete ai-model: invalid id format, ids={}", ids, e);
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/generate-instances/{ids}")
    public BaseResponse<String> generateInstances(@PathVariable("ids") String ids) {
        log.debug("generate ai-instance by ai-model ids={}", ids);
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiModelService.generateInstances(longIds);
        } catch (NumberFormatException e) {
            log.warn("generate ai-instance by ai-model: invalid id format, ids={}", ids, e);
            return BaseResponse.fail("ID格式错误", null);
        }
    }


    @PostMapping("/queryPage")
    public PageResponse<AiModelResponseDTO> queryPage(@RequestBody BasePageRequest<AiModelQueryRequestDTO> request) {
        return aiModelService.queryPage(request);
    }


    @GetMapping("/detail")
    public BaseResponse<AiModelResponseDTO> detail(@RequestParam("id") String id) {
        log.debug("detail ai-model id={}", id);
        try {
            Long longId = Long.parseLong(id);
            return aiModelService.detail(longId);
        } catch (NumberFormatException e) {
            log.warn("detail ai-model: invalid id, id={}", id, e);
            return BaseResponse.fail("ID格式错误", null);
        }
    }


    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiModelUpdateRequestDTO request) {
        return aiModelService.updateModel(request);
    }


    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiModelCreateRequestDTO request) {
        return aiModelService.create(request);
    }


}
