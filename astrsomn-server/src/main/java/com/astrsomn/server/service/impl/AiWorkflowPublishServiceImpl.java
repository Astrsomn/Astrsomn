package com.astrsomn.server.service.impl;

import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowPublishRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowDefinitionEntity;
import com.astrsomn.api.workflow.domain.entity.AstFlowDeploymentEntity;
import com.astrsomn.api.workflow.domain.entity.AstFlowNodeHistoryEntity;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.ErrorEnum;
import com.astrsomn.server.service.AiWorkflowPublishService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.starter.workflow.mapper.AstFlowDefinitionMapper;
import com.astrsomn.starter.workflow.mapper.AstFlowDeploymentMapper;
import com.astrsomn.starter.workflow.mapper.AstFlowNodeHistoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiWorkflowPublishServiceImpl implements AiWorkflowPublishService {

    private static final String HISTORY_TYPE_PUBLISH = "PUBLISH";

    private final AstFlowDefinitionMapper flowDefinitionMapper;
    private final AstFlowDeploymentMapper flowDeploymentMapper;
    private final AstFlowNodeHistoryMapper flowNodeHistoryMapper;
    private final QueryEnvParamHelper queryEnvParamHelper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> publish(AstFlowPublishRequestDTO request) {
        if (request == null || request.getId() == null) {
            throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少流程定义ID");
        }

        String envCode = queryEnvParamHelper.effectiveEnvCode();
        LambdaQueryWrapper<AstFlowDefinitionEntity> definitionWrapper = new LambdaQueryWrapper<AstFlowDefinitionEntity>()
                .eq(AstFlowDefinitionEntity::getId, request.getId())
                .eq(AstFlowDefinitionEntity::getDeleted, false)
                .last("LIMIT 1");
        if (envCode != null && !envCode.isBlank()) {
            definitionWrapper.eq(AstFlowDefinitionEntity::getEnvCode, envCode);
        }
        AstFlowDefinitionEntity definition = flowDefinitionMapper.selectOne(definitionWrapper);
        if (definition == null) {
            throw new BusinessException(ErrorEnum.NOT_FOUND, "流程定义不存在");
        }
        if (definition.getDraftGraphJson() == null || definition.getDraftGraphJson().isBlank()) {
            throw new BusinessException(ErrorEnum.PARAM_ERROR, "草稿图为空，无法发布");
        }

        LambdaQueryWrapper<AstFlowDeploymentEntity> latestVersionWrapper = new LambdaQueryWrapper<AstFlowDeploymentEntity>()
                .eq(AstFlowDeploymentEntity::getFlowDefinitionId, definition.getId())
                .eq(AstFlowDeploymentEntity::getDeleted, false)
                .orderByDesc(AstFlowDeploymentEntity::getVersion)
                .last("LIMIT 1");
        if (envCode != null && !envCode.isBlank()) {
            latestVersionWrapper.eq(AstFlowDeploymentEntity::getEnvCode, envCode);
        }
        AstFlowDeploymentEntity latest = flowDeploymentMapper.selectOne(latestVersionWrapper);
        int nextVersion = latest == null || latest.getVersion() == null ? 1 : latest.getVersion() + 1;

        LambdaUpdateWrapper<AstFlowDeploymentEntity> clearLatestWrapper = new LambdaUpdateWrapper<AstFlowDeploymentEntity>()
                .eq(AstFlowDeploymentEntity::getFlowDefinitionId, definition.getId())
                .eq(AstFlowDeploymentEntity::getLatest, true)
                .set(AstFlowDeploymentEntity::getLatest, false);
        if (envCode != null && !envCode.isBlank()) {
            clearLatestWrapper.eq(AstFlowDeploymentEntity::getEnvCode, envCode);
        }
        flowDeploymentMapper.update(null, clearLatestWrapper);

        AstFlowDeploymentEntity deploymentEntity = new AstFlowDeploymentEntity();
        deploymentEntity.setFlowDefinitionId(definition.getId());
        deploymentEntity.setVersion(nextVersion);
        deploymentEntity.setDeployedGraphJson(definition.getDraftGraphJson());
        deploymentEntity.setLatest(true);
        deploymentEntity.setEnvCode(definition.getEnvCode());
        flowDeploymentMapper.insert(deploymentEntity);

        List<AstFlowNodeHistoryEntity> historyRows = buildPublishNodeHistory(definition, nextVersion);
        for (AstFlowNodeHistoryEntity historyRow : historyRows) {
            flowNodeHistoryMapper.insert(historyRow);
        }
        return BaseResponse.success("发布成功，版本号已更新为 v" + nextVersion);
    }

    private List<AstFlowNodeHistoryEntity> buildPublishNodeHistory(AstFlowDefinitionEntity definition, int version) {
        List<AstFlowNodeHistoryEntity> rows = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(definition.getDraftGraphJson());
            JsonNode nodes = root.path("nodes");
            if (!nodes.isArray()) {
                return rows;
            }
            for (JsonNode node : nodes) {
                AstFlowNodeHistoryEntity historyEntity = new AstFlowNodeHistoryEntity();
                historyEntity.setFlowDefinitionId(definition.getId());
                historyEntity.setVersion(version);
                historyEntity.setHistoryType(HISTORY_TYPE_PUBLISH);
                historyEntity.setNodeId(node.path("id").asText(""));
                historyEntity.setNodeName(node.path("data").path("label").asText(""));
                historyEntity.setSnapshotJson(node.toString());
                historyEntity.setInputData(node.toString());
                historyEntity.setExecutionMs(0L);
                historyEntity.setEnvCode(definition.getEnvCode());
                rows.add(historyEntity);
            }
            return rows;
        } catch (Exception e) {
            throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "草稿图格式错误，发布失败");
        }
    }
}
