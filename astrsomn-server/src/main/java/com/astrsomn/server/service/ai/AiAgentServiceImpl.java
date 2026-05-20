package com.astrsomn.server.service.ai;

import com.astrsomn.api.runtime.common.constant.AiInstanceEnum;
import com.astrsomn.api.runtime.common.constant.AiPromptEnum;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentResponseDTO;
import com.astrsomn.api.runtime.common.dto.instance.AiInstanceCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.instance.AiInstanceResponseDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptCreateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiAgentEntity;
import com.astrsomn.api.runtime.common.entity.AiPromptEntity;
import com.astrsomn.api.runtime.common.utils.KeyGenerator;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.runtime.exception.AiAgentErrorEnum;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
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
            responseDTO.setInstanceList(instances != null ? instances : Collections.emptyList());
        }

        String promptKey = aiAgent.getPromptKey();
        AiPromptEntity prompt = aiPromptService.getOne(new LambdaQueryWrapper<AiPromptEntity>()
                .eq(AiPromptEntity::getPromptKey, promptKey));
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

        String promptKey = null;
        AiPromptEntity promptEntity = request.getPromptEntity();
        if (Objects.nonNull(promptEntity)) {
            AiPromptCreateRequestDTO promptRequest = new AiPromptCreateRequestDTO();
            promptKey = KeyGenerator.generateUniquePromptKey();
            promptRequest.setPromptKey(promptKey);
            promptRequest.setPromptTitle(request.getAgentName());
            promptRequest.setPromptContent(promptEntity.getPromptContent());
            promptRequest.setStatus(AiPromptEnum.StatusEnum.ENABLED.getCode());
            aiPromptService.create(promptRequest);
        }

        AiAgentEntity agentEntity = new AiAgentEntity();
        BeanUtils.copyProperties(request, agentEntity);
        agentEntity.setPromptKey(promptKey);

        boolean agentResult = save(agentEntity);
        if (!agentResult) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_CREATE_FAILED);
        }
        List<AiInstanceCreateRequestDTO> instanceList = request.getInstanceList();
        if (instanceList != null && !instanceList.isEmpty()) {
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
        return BaseResponse.success("完整Agentsuccess");
    }

}
