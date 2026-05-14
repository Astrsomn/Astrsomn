package com.astrsomn.server.api;


import com.astrsomn.api.runtime.common.dto.account.AiAccountCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.account.AiAccountQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.account.AiAccountResponseDTO;
import com.astrsomn.api.runtime.common.dto.account.AiAccountUpdateRequestDTO;
import com.astrsomn.api.runtime.common.dto.model.AiModelQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.model.AiModelResponseDTO;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.AiAccountService;
import com.astrsomn.server.service.AiModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-account")
@RequiredArgsConstructor
public class AiAccountController extends BaseController {

    private final AiAccountService aiAccountService;
    private final AiModelService aiModelService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiAccountCreateRequestDTO request) {
        return aiAccountService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiAccountService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiAccountUpdateRequestDTO request) {
        return aiAccountService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiAccountResponseDTO> queryPage(
            @RequestBody BasePageRequest<AiAccountQueryRequestDTO> request) {
        return aiAccountService.queryPage(request);
    }


    @PostMapping("/queryModelsByAccountKey")
    public PageResponse<AiModelResponseDTO> queryModelsByAccountKey(
            @RequestBody BasePageRequest<AiModelQueryRequestDTO> request) {
        return aiModelService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiAccountResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiAccountService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
