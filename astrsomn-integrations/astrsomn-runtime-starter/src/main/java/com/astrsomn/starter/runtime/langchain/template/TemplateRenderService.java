package com.astrsomn.starter.runtime.langchain.template;

import com.astrsomn.api.runtime.common.constant.AiTemplateEnum;
import com.astrsomn.api.runtime.common.entity.AiTemplateEntity;
import com.astrsomn.api.runtime.exception.AiTemplateErrorEnum;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.mapper.AstAiTemplateMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateRenderService {

    private final freemarker.template.Configuration freeMarkerConfig;
    private final AstAiTemplateMapper templateMapper;


    /**
     * Render a single template by templateKey with the given parameters.
     * Only renders ENABLED FreeMarker templates.
     *
     * @param templateKey the unique key of the template
     * @param params      the data model for template rendering
     * @return rendered HTML content
     */
    public String renderTemplate(String templateKey, Map<String, Object> params) {
        AiTemplateEntity entity = templateMapper.selectOne(
                new LambdaQueryWrapper<AiTemplateEntity>()
                        .eq(AiTemplateEntity::getTemplateKey, templateKey)
                        .eq(AiTemplateEntity::getStatus, AiTemplateEnum.StatusEnum.ENABLED.getCode())
                        .last("LIMIT 1"));

        if (Objects.isNull(entity)) {
            throw new BusinessException(AiTemplateErrorEnum.TEMPLATE_NOT_FOUND);
        }

        return doRender(entity, params);
    }


    /**
     * Render a single template entity with the given parameters.
     *
     * @param template the template entity (already loaded from DB)
     * @param params   the data model for template rendering
     * @return rendered HTML content
     */
    public String renderTemplate(AiTemplateEntity template, Map<String, Object> params) {
        return doRender(template, params);
    }


    /**
     * Render all templates matching the given list of template keys with the same params.
     * Preserves the order of the input key list. Skips missing or disabled templates silently.
     *
     * @param templateKeys ordered list of template keys
     * @param params       shared data model for all template renderings
     * @return ordered map of templateKey -> renderedContent
     */
    public Map<String, String> renderTemplatesByKeys(List<String> templateKeys, Map<String, Object> params) {
        if (Objects.isNull(templateKeys) || templateKeys.isEmpty()) {
            return Collections.emptyMap();
        }

        List<AiTemplateEntity> templates = templateMapper.selectList(
                new LambdaQueryWrapper<AiTemplateEntity>()
                        .in(AiTemplateEntity::getTemplateKey, templateKeys)
                        .eq(AiTemplateEntity::getStatus, AiTemplateEnum.StatusEnum.ENABLED.getCode()));

        Map<String, String> results = new LinkedHashMap<>();
        for (String key : templateKeys) {
            templates.stream()
                    .filter(t -> key.equals(t.getTemplateKey()))
                    .findFirst()
                    .ifPresent(t -> results.put(key, doRender(t, params)));
        }
        return results;
    }


    /**
     * Render all templates matching the comma-separated template keys with the same params.
     *
     * @param templateKeysCsv comma-separated template keys (e.g. "key1,key2")
     * @param params          shared data model for all template renderings
     * @return ordered map of templateKey -> renderedContent
     */
    public Map<String, String> renderTemplatesByKeys(String templateKeysCsv, Map<String, Object> params) {
        if (StringUtils.isBlank(templateKeysCsv)) {
            return Collections.emptyMap();
        }
        List<String> keys = Arrays.asList(templateKeysCsv.split(","));
        return renderTemplatesByKeys(keys, params);
    }


    /**
     * Core FreeMarker rendering logic.
     * Validates template type is FreeMarker (case-insensitive), creates a Template from DB content,
     * and processes it with the given data model.
     */
    private String doRender(AiTemplateEntity template, Map<String, Object> params) {
        if (!isFreeMarker(template.getTemplateType())) {
            throw new BusinessException(AiTemplateErrorEnum.TEMPLATE_TYPE_NOT_SUPPORTED);
        }

        String content = template.getContent();
        if (StringUtils.isBlank(content)) {
            log.warn("Template key={} has empty content, returning blank", template.getTemplateKey());
            return "";
        }

        try {
            Template ftl = new Template(template.getTemplateKey(), content, freeMarkerConfig);
            try (StringWriter writer = new StringWriter()) {
                ftl.process(params, writer);
                return writer.toString();
            }
        } catch (TemplateException e) {
            log.error("FreeMarker template processing error for key={}: {}", template.getTemplateKey(), e.getMessage());
            throw new BusinessException(AiTemplateErrorEnum.TEMPLATE_RENDER_FAILED);
        } catch (IOException e) {
            log.error("IO error rendering template key={}: {}", template.getTemplateKey(), e.getMessage());
            throw new BusinessException(AiTemplateErrorEnum.TEMPLATE_RENDER_FAILED);
        }
    }

    /**
     * Check whether the given template type string represents a FreeMarker template.
     * Uses case-insensitive comparison to handle both frontend-sent values ({@code FREEMARKER})
     * and backend enum codes ({@code freeMarker}) interchangeably.
     */
    private static boolean isFreeMarker(String templateType) {
        if (Objects.isNull(templateType)) {
            return false;
        }
        String fc = AiTemplateEnum.TemplateTypeEnum.FREEMARKER.getCode();
        return fc.equalsIgnoreCase(templateType) || fc.equalsIgnoreCase(templateType.trim());
    }
}
