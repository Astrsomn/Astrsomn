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
import com.astrsomn.api.runtime.common.entity.AiPromptEntity;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AiAgentServiceImpl extends ServiceImpl<AiAgentMapper, AiAgentEntity> implements AiAgentService {


    private final AiPromptService aiPromptService;
    private final AiInstanceService aiInstanceService;

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

        String agentKey = aiAgent.getAgentKey();
        if (StringUtils.isNotBlank(agentKey)) {
            List<AiInstanceResponseDTO> instances = aiInstanceService.queryByBizKey(agentKey);
            responseDTO.setInstanceList(Optional.ofNullable(instances).orElse(Collections.emptyList()));
        }

        String promptKey = aiAgent.getPromptKey();
        AiPromptEntity prompt = aiPromptService.lambdaQuery()
                .eq(AiPromptEntity::getPromptKey, promptKey)
                .orderByDesc(AiPromptEntity::getVersion)
                .last("LIMIT 1")
                .one();
        responseDTO.setPrompt(prompt);
        return BaseResponse.success(responseDTO);
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
