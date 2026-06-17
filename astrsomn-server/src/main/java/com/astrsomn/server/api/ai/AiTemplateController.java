package com.astrsomn.server.api.ai;

import com.astrsomn.api.runtime.common.constant.AiTemplateEnum;
import com.astrsomn.api.runtime.common.dto.template.AiTemplateCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.template.AiTemplateQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.template.AiTemplateResponseDTO;
import com.astrsomn.api.runtime.common.dto.template.AiTemplateUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiTemplateEntity;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.common.utils.JsonUtil;
import com.astrsomn.server.service.ai.AiTemplateService;
import com.astrsomn.starter.runtime.langchain.template.TemplateRenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/v1/astro/ai-template")
@RequiredArgsConstructor
public class AiTemplateController extends BaseController {

    private final AiTemplateService aiTemplateService;
    private final TemplateRenderService templateRenderService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiTemplateCreateRequestDTO request) {
        return aiTemplateService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiTemplateService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiTemplateUpdateRequestDTO request) {
        return aiTemplateService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiTemplateResponseDTO> queryPage(@RequestBody BasePageRequest<AiTemplateQueryRequestDTO> request) {
        return aiTemplateService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiTemplateResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiTemplateService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    /**
     * Debug render: render FTL content with provided JSON params without saving to DB.
     * Request: { "content": "...FTL...", "paramsJson": "{\"key\": \"value\"}" }
     */
    @PostMapping("/debugRender")
    public BaseResponse<String> debugRender(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        String paramsJson = request.get("paramsJson");

        if (content == null || content.trim().isEmpty()) {
            return BaseResponse.fail("Template content is required", null);
        }

        AiTemplateEntity template = new AiTemplateEntity();
        template.setTemplateKey("debug");
        template.setTemplateType(AiTemplateEnum.TemplateTypeEnum.FREEMARKER.getCode());
        template.setContent(content);

        Map<String, Object> params = Collections.emptyMap();
        if (paramsJson != null && !paramsJson.trim().isEmpty()) {
            Map<String, Object> parsed = JsonUtil.fromJson(paramsJson, Map.class);
            if (Objects.nonNull(parsed)) {
                params = parsed;
            }
        }

        try {
            String rendered = templateRenderService.renderTemplate(template, params);
            return BaseResponse.success(rendered);
        } catch (Exception e) {
            return BaseResponse.fail("Render failed: " + e.getMessage(), null);
        }
    }
}
