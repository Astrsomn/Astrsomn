package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.model.AiModelQueryRequestDTO;
import org.astrsomn.core.common.dto.model.AiModelResponseDTO;
import org.astrsomn.core.common.dto.account.AiAccountCreateRequestDTO;
import org.astrsomn.core.common.dto.account.AiAccountQueryRequestDTO;
import org.astrsomn.core.common.dto.account.AiAccountResponseDTO;
import org.astrsomn.core.common.dto.account.AiAccountUpdateRequestDTO;
import org.astrsomn.server.service.AiAccountService;
import org.astrsomn.server.service.AiModelService;
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

    /**
     * 按 accountKey 获取该账号在当前环境下关联使用的模型列表。
     * 前端用于「账号卡片 - 侧边栏模型列表」。
     */
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
