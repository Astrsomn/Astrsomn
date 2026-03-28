package org.astrsomn.starter.workflow;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.astrsomn.core.common.entity.AiWorkflowEntity;
import org.astrsomn.core.mapper.AiWorkflowMapper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.workflow.core.model.WorkflowDefinition;
import org.springframework.stereotype.Service;

/**
 * 从 {@code AI_WORKFLOW} 加载图 JSON 并反序列化为引擎模型。
 */
@Service
@RequiredArgsConstructor
public class WorkflowDefinitionService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final AiWorkflowMapper aiWorkflowMapper;
    private final AstrsomnProperties astrsomnProperties;

    /**
     * 取指定环境下最新已发布版本（按版本号降序第一条）。
     */
    public WorkflowDefinition loadPublished(String workflowKey) {
        LambdaQueryWrapper<AiWorkflowEntity> w = new LambdaQueryWrapper<AiWorkflowEntity>()
                .eq(AiWorkflowEntity::getWorkflowKey, workflowKey)
                .eq(AiWorkflowEntity::getEnvCode, astrsomnProperties.getEnvCode())
                .eq(AiWorkflowEntity::getStatus, "PUBLISHED")
                .eq(AiWorkflowEntity::getDeleted, false)
                .orderByDesc(AiWorkflowEntity::getVersionNo);
        Page<AiWorkflowEntity> page = aiWorkflowMapper.selectPage(new Page<>(1, 1), w);
        AiWorkflowEntity row = page.getRecords().isEmpty() ? null : page.getRecords().get(0);
        if (row == null || StringUtils.isBlank(row.getGraphJson())) {
            throw new IllegalStateException("未找到已发布工作流: workflowKey=" + workflowKey
                    + ", env=" + astrsomnProperties.getEnvCode());
        }
        return parse(row.getGraphJson());
    }

    public WorkflowDefinition parse(String graphJson) {
        try {
            return MAPPER.readValue(graphJson, WorkflowDefinition.class);
        } catch (Exception e) {
            throw new IllegalStateException("GRAPH_JSON 解析失败", e);
        }
    }
}
