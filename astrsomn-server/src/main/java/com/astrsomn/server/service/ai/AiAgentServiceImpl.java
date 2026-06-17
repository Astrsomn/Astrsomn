package com.astrsomn.server.service.ai;

import com.astrsomn.api.runtime.common.constant.AiInstanceEnum;
import com.astrsomn.api.runtime.common.constant.AiPromptEnum;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentResponseDTO;
import com.astrsomn.api.runtime.common.dto.instance.AiInstanceCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.instance.AiInstanceResponseDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiAgentEntity;
import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;
import com.astrsomn.api.runtime.common.entity.AiMcpEntity;
import com.astrsomn.api.runtime.common.entity.AiPromptEntity;
import com.astrsomn.api.runtime.common.entity.AiTemplateEntity;
import com.astrsomn.api.runtime.common.entity.AiToolEntity;
import com.astrsomn.api.vector.entity.AiVecStoreEntity;
import com.astrsomn.api.runtime.common.utils.KeyGenerator;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.runtime.exception.AiAgentErrorEnum;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.common.utils.CollectionUtils;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.mapper.AiAgentMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiAgentServiceImpl extends ServiceImpl<AiAgentMapper, AiAgentEntity> implements AiAgentService {


    private final AiPromptService aiPromptService;
    private final AiInstanceService aiInstanceService;
    private final AiToolService aiToolService;
    private final AiMcpService aiMcpService;
    private final AiTemplateService aiTemplateService;
    private final com.astrsomn.server.service.vector.AiVecStoreService aiVecStoreService;
    @Qualifier("agentDetailExecutor")
    private final Executor agentDetailExecutor;

    @Override
    public BaseResponse<String> create(AiAgentCreateRequestDTO request) {
        AiAgentEntity aiAgent = new AiAgentEntity();
        BeanUtils.copyProperties(request, aiAgent);

        boolean result = save(aiAgent);
        if (!result) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_CREATE_FAILED);
        }
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_DELETE_FAILED);
        }
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<AiAgentResponseDTO> detail(Long longId) {
        AiAgentEntity aiAgent = getById(longId);
        if (Objects.isNull(aiAgent)) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_NOT_FOUND);
        }
        AiAgentResponseDTO responseDTO = new AiAgentResponseDTO();
        BeanUtils.copyProperties(aiAgent, responseDTO);

        // 异步并行查询实例
        String agentKey = aiAgent.getAgentKey();
        CompletableFuture<Void> instanceFuture = CompletableFuture.runAsync(() -> {
            if (StringUtils.isNotBlank(agentKey)) {
                List<AiInstanceResponseDTO> instances = aiInstanceService.queryByBizKey(agentKey);
                responseDTO.setInstanceList(Optional.ofNullable(instances).orElse(Collections.emptyList()));
            }
        }, agentDetailExecutor);

        // 异步并行查询 prompt
        String promptKey = aiAgent.getPromptKey();
        CompletableFuture<Void> promptFuture = CompletableFuture.runAsync(() -> {
            AiPromptEntity prompt = aiPromptService.lambdaQuery()
                    .eq(AiPromptEntity::getPromptKey, promptKey)
                    .orderByDesc(AiPromptEntity::getVersion)
                    .last("LIMIT 1")
                    .one();
            responseDTO.setPrompt(prompt);
        }, agentDetailExecutor);

        // 异步并行校验各类资源的孤儿 key
        CompletableFuture<Void> orphanedToolFuture = CompletableFuture.runAsync(() ->
                responseDTO.setOrphanedToolKeys(computeOrphaned(aiAgent.getToolKeys(),
                        keys -> aiToolService.lambdaQuery()
                                .in(AiToolEntity::getToolKey, keys)
                                .select(AiToolEntity::getToolKey)
                                .list().stream().map(AiToolEntity::getToolKey).collect(Collectors.toList()))),
                agentDetailExecutor);

        CompletableFuture<Void> orphanedMcpFuture = CompletableFuture.runAsync(() ->
                responseDTO.setOrphanedMcpKeys(computeOrphaned(aiAgent.getMcpKeys(),
                        keys -> aiMcpService.lambdaQuery()
                                .in(AiMcpEntity::getMcpKey, keys)
                                .select(AiMcpEntity::getMcpKey)
                                .list().stream().map(AiMcpEntity::getMcpKey).collect(Collectors.toList()))),
                agentDetailExecutor);

        CompletableFuture<Void> orphanedTemplateFuture = CompletableFuture.runAsync(() ->
                responseDTO.setOrphanedTemplateKeys(computeOrphaned(aiAgent.getTemplateKeys(),
                        keys -> aiTemplateService.lambdaQuery()
                                .in(AiTemplateEntity::getTemplateKey, keys)
                                .select(AiTemplateEntity::getTemplateKey)
                                .list().stream().map(AiTemplateEntity::getTemplateKey).collect(Collectors.toList()))),
                agentDetailExecutor);

        CompletableFuture<Void> orphanedKnowledgeBaseFuture = CompletableFuture.runAsync(() ->
                responseDTO.setOrphanedKnowledgeBaseKeys(computeOrphaned(aiAgent.getKnowledgeBaseKeys(),
                        keys -> aiVecStoreService.lambdaQuery()
                                .in(AiVecStoreEntity::getCollectionName, keys)
                                .select(AiVecStoreEntity::getCollectionName)
                                .list().stream().map(AiVecStoreEntity::getCollectionName).collect(Collectors.toList()))),
                agentDetailExecutor);

        // 等待所有异步任务完成
        CompletableFuture.allOf(instanceFuture, promptFuture,
                orphanedToolFuture, orphanedMcpFuture, orphanedTemplateFuture, orphanedKnowledgeBaseFuture).join();

        return BaseResponse.success(responseDTO);
    }

    /**
     * 给定逗号分隔的 key 字符串，批量对比数据库中实际存在的 key，返回已被删除的孤儿 key 列表。
     */
    private List<String> computeOrphaned(String keysStr, java.util.function.Function<List<String>, List<String>> existingResolver) {
        if (StringUtils.isBlank(keysStr)) return Collections.emptyList();
        List<String> bound = Arrays.stream(keysStr.split(","))
                .map(String::trim).filter(k -> !k.isEmpty()).collect(Collectors.toList());
        if (bound.isEmpty()) return Collections.emptyList();
        Set<String> existing = new HashSet<>(existingResolver.apply(bound));
        return bound.stream().filter(k -> !existing.contains(k)).collect(Collectors.toList());
    }



    @Override
    public PageResponse<AiAgentResponseDTO> queryPage(BasePageRequest<AiAgentQueryRequestDTO> request) {
        IPage<AiAgentResponseDTO> page = PageUtils.buildPage(request);
        AiAgentQueryRequestDTO param = request.getParam();
        if (Objects.isNull(param)) {
            param = new AiAgentQueryRequestDTO();
        }

        IPage<AiAgentResponseDTO> result = baseMapper.queryPage(page, param);

        return PageConverter.toResponse(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> saveOrUpdate(AiAgentCreateRequestDTO request) {
        String agentKey = request.getAgentKey();
        if (StringUtils.isBlank(agentKey)) {
            agentKey = KeyGenerator.generateUniqueAgentKey();
            request.setAgentKey(agentKey);
        }

        AiAgentEntity existingAgent = lambdaQuery()
                .eq(AiAgentEntity::getAgentKey, agentKey)
                .one();

        if (Objects.isNull(existingAgent)) {
            return createAgent(request, agentKey);
        } else {
            return updateAgent(request, existingAgent);
        }
    }

    private BaseResponse<String> createAgent(AiAgentCreateRequestDTO request, String agentKey) {
        String promptKey = null;
        AiPromptEntity promptEntity = request.getPromptEntity();
        if (Objects.nonNull(promptEntity)) {
            promptKey = createPrompt(request.getAgentName(), promptEntity.getPromptContent());
        }

        AiAgentEntity agentEntity = new AiAgentEntity();
        BeanUtils.copyProperties(request, agentEntity);
        agentEntity.setPromptKey(promptKey);

        if (!save(agentEntity)) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_CREATE_FAILED);
        }
        saveInstances(request, agentKey);
        return BaseResponse.success("success");
    }

    private BaseResponse<String> updateAgent(AiAgentCreateRequestDTO request, AiAgentEntity existingAgent) {
        String agentKey = existingAgent.getAgentKey();
        Long existingId = existingAgent.getId();

        String promptKey = existingAgent.getPromptKey();
        AiPromptEntity promptEntity = request.getPromptEntity();
        if (Objects.nonNull(promptEntity)) {
            if (StringUtils.isBlank(existingAgent.getPromptKey())) {
                promptKey = createPrompt(request.getAgentName(), promptEntity.getPromptContent());
            } else {
                updatePrompt(existingAgent.getPromptKey(), request.getAgentName(), promptEntity.getPromptContent());
            }
        }

        AiAgentEntity agentEntity = new AiAgentEntity();
        BeanUtils.copyProperties(request, agentEntity);
        agentEntity.setId(existingId);
        agentEntity.setAgentKey(agentKey);
        agentEntity.setPromptKey(promptKey);

        if (!updateById(agentEntity)) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_UPDATE_FAILED);
        }

        aiInstanceService.remove(new LambdaQueryWrapper<AiInstanceEntity>()
                .eq(AiInstanceEntity::getBizKey, agentKey));
        saveInstances(request, agentKey);
        return BaseResponse.success("success");
    }

    private String createPrompt(String agentName, String promptContent) {
        String promptKey = KeyGenerator.generateUniquePromptKey();
        AiPromptCreateRequestDTO promptRequest = new AiPromptCreateRequestDTO();
        promptRequest.setPromptKey(promptKey);
        promptRequest.setPromptTitle(agentName);
        promptRequest.setPromptContent(promptContent);
        promptRequest.setStatus(AiPromptEnum.StatusEnum.ENABLED.getCode());
        aiPromptService.create(promptRequest);
        return promptKey;
    }

    private void updatePrompt(String promptKey, String agentName, String promptContent) {
        AiPromptEntity latestPrompt = aiPromptService.lambdaQuery()
                .eq(AiPromptEntity::getPromptKey, promptKey)
                .orderByDesc(AiPromptEntity::getVersion)
                .last("LIMIT 1")
                .one();
        AiPromptUpdateRequestDTO updateRequest = new AiPromptUpdateRequestDTO();
        if (Objects.nonNull(latestPrompt)) {
            updateRequest.setId(latestPrompt.getId());
            updateRequest.setStatus(latestPrompt.getStatus());
        } else {
            updateRequest.setStatus(AiPromptEnum.StatusEnum.ENABLED.getCode());
        }
        updateRequest.setPromptKey(promptKey);
        updateRequest.setPromptTitle(agentName);
        updateRequest.setPromptContent(promptContent);
        aiPromptService.update(updateRequest);
    }

    private void saveInstances(AiAgentCreateRequestDTO request, String agentKey) {
        List<AiInstanceCreateRequestDTO> instanceList = request.getInstanceList();
        if (CollectionUtils.isEmpty(instanceList)) {
            return;
        }
        String routeStrategy = request.getRouteStrategy();
        for (AiInstanceCreateRequestDTO instanceRequest : instanceList) {
            if (StringUtils.isBlank(instanceRequest.getInstanceKey())) {
                instanceRequest.setInstanceKey(KeyGenerator.generateUniqueInstanceKey());
            }
            instanceRequest.setBizKey(agentKey);
            instanceRequest.setEnvCode(request.getEnvCode());
            instanceRequest.setStatus(AiInstanceEnum.StatusEnum.ENABLED.getCode());
            if (StringUtils.isNotBlank(routeStrategy)) {
                instanceRequest.setRouteStrategy(routeStrategy);
            }
            aiInstanceService.create(instanceRequest);
        }
    }

}
