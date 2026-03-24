package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.dto.model.AiModelCreateRequestDTO;
import org.astrsomn.core.common.dto.model.AiModelQueryRequestDTO;
import org.astrsomn.core.common.dto.model.AiModelResponseDTO;
import org.astrsomn.core.common.dto.model.AiModelUpdateRequestDTO;
import org.astrsomn.server.service.AiModelService;
import org.springframework.web.bind.annotation.*;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;

@RestController
@RequestMapping("/v1/astro/ai-model")
@RequiredArgsConstructor
public class AiModelController extends BaseController {

    private final AiModelService aiModelService;


    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        System.out.println("接收删除ID参数: " + ids);
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiModelService.delete(longIds);
        } catch (NumberFormatException e) {
            System.out.println("ID转换失败: " + e.getMessage());
            return BaseResponse.fail("ID格式错误", null);
        }
    }


    @PostMapping("/queryPage")
    public PageResponse<AiModelResponseDTO> queryPage(@RequestBody BasePageRequest<AiModelQueryRequestDTO> request) {
        return aiModelService.queryPage(request);
    }


    @GetMapping("/detail")
    public BaseResponse<AiModelResponseDTO> detail(@RequestParam("id") String id) {
        System.out.println("接收详情ID参数: " + id);
        try {
            Long longId = Long.parseLong(id);
            System.out.println("转换后Long ID: " + longId);
            return aiModelService.detail(longId);
        } catch (NumberFormatException e) {
            System.out.println("ID转换失败: " + e.getMessage());
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
