package com.astrsomn.server.service.impl;

import com.astrsomn.api.runtime.common.dto.agent.AiAgentCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentResponseDTO;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentUpdateRequestDTO;
import com.astrsomn.api.runtime.common.dto.instance.AiInstanceCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.instance.AiInstanceResponseDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiAgentEntity;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.runtime.exception.AiAgentErrorEnum;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.mapper.AiAgentMapper;
import com.astrsomn.server.service.AiAgentService;
import com.astrsomn.server.service.AiInstanceService;
import com.astrsomn.server.service.AiPromptService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiAgentServiceImpl extends ServiceImpl<AiAgentMapper, AiAgentEntity> implements AiAgentService {


    private final AiPromptService aiPromptService;
    private final AiInstanceService aiInstanceService;

    private static final String BIZ_KEY_PREFIX = "AG-";
    private static final int RANDOM_KEY_LENGTH = 16;

    @Override
    public BaseResponse<String> create(AiAgentCreateRequestDTO request) {
        AiAgentEntity aiAgent = new AiAgentEntity();
        BeanUtils.copyProperties(request, aiAgent);

        boolean result = save(aiAgent);
        if (!result) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_DELETE_FAILED);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<AiAgentResponseDTO> detail(Long longId) {
        AiAgentEntity aiAgent = getById(longId);
        if (aiAgent == null) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_NOT_FOUND);
        }
        AiAgentResponseDTO responseDTO = new AiAgentResponseDTO();
        BeanUtils.copyProperties(aiAgent, responseDTO);

        // 加载关联的推理实例列表
        String bizKey = aiAgent.getBizKey();
        if (StringUtils.isNotBlank(bizKey)) {
            List<AiInstanceResponseDTO> instances = aiInstanceService.queryByBizKeys(List.of(bizKey));
            responseDTO.setInstanceList(instances != null ? instances : Collections.emptyList());
        }

        // 加载关联的 Prompt（最新版本）
        String promptKey = aiAgent.getPromptKey();
        if (StringUtils.isNotBlank(promptKey)) {
            AiPromptQueryRequestDTO promptQuery = new AiPromptQueryRequestDTO();
            promptQuery.setPromptKey(promptKey);
            BasePageRequest<AiPromptQueryRequestDTO> promptReq = new BasePageRequest<>(promptQuery);
            promptReq.setPageNo(1);
            promptReq.setPageSize(1);
            PageResponse<AiPromptResponseDTO> promptPage = aiPromptService.queryPage(promptReq);
            if (promptPage.getList() != null && !promptPage.getList().isEmpty()) {
                AiPromptResponseDTO prompt = promptPage.getList().get(0);
                responseDTO.setPromptTitle(prompt.getPromptTitle());
                responseDTO.setPromptContent(prompt.getPromptContent());
                responseDTO.setPromptVersion(prompt.getVersion());
            }
        }

        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> updateAgent(AiAgentUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_PARAM_ERROR);
        }
        AiAgentEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_NOT_FOUND);
        }
        AiAgentEntity aiAgent = new AiAgentEntity();
        BeanUtils.copyProperties(request, aiAgent);

        boolean result = updateById(aiAgent);
        if (!result) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiAgentResponseDTO> queryPage(BasePageRequest<AiAgentQueryRequestDTO> request) {
        IPage<AiAgentResponseDTO> page = PageUtils.buildPage(request);
        AiAgentQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiAgentQueryRequestDTO();
        }

        IPage<AiAgentResponseDTO> result = baseMapper.queryPage(page, param);

        List<AiAgentResponseDTO> records = result.getRecords();
        if (records != null && !records.isEmpty()) {
            List<String> bizKeys = records.stream()
                    .map(AiAgentResponseDTO::getBizKey)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
            if (!bizKeys.isEmpty()) {
                List<AiInstanceResponseDTO> allInstances = aiInstanceService.queryByBizKeys(bizKeys);
                Map<String, List<AiInstanceResponseDTO>> grouped = allInstances.stream()
                        .collect(Collectors.groupingBy(AiInstanceResponseDTO::getBizKey));
                for (AiAgentResponseDTO dto : records) {
                    dto.setInstanceList(grouped.getOrDefault(dto.getBizKey(), Collections.emptyList()));
                }
            }
        }

        return PageConverter.toResponse(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> createFullAgent(AiAgentCreateRequestDTO request) {
        String bizKey = request.getBizKey();
        if (StringUtils.isBlank(bizKey)) {
            bizKey = generateUniqueBizKey();
            request.setBizKey(bizKey);
        }

        String promptKey = null;
        String promptContent = request.getPromptContent();
        if (StringUtils.isNotBlank(promptContent)) {
            AiPromptCreateRequestDTO promptRequest = new AiPromptCreateRequestDTO();
            promptKey = generateUniquePromptKey();
            promptRequest.setPromptKey(promptKey);
            promptRequest.setPromptTitle(request.getAgentName());
            promptRequest.setPromptContent(promptContent);
            promptRequest.setEnvCode(request.getEnvCode());
            promptRequest.setStatus("enabled");
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
                    instanceRequest.setInstanceKey(generateUniqueInstanceKey());
                }
                instanceRequest.setBizKey(bizKey);
                instanceRequest.setEnvCode(request.getEnvCode());
                instanceRequest.setStatus("enabled");
                if (StringUtils.isNotBlank(routeStrategy)) {
                    instanceRequest.setRouteStrategy(routeStrategy);
                }
                aiInstanceService.create(instanceRequest);
            }
        }

        return BaseResponse.success("完整Agent创建成功");
    }

    private String generateUniqueBizKey() {
        return BIZ_KEY_PREFIX + UUID.randomUUID().toString().replace("-", "").substring(0, RANDOM_KEY_LENGTH);
    }

    private String generateUniquePromptKey() {
        return "PR-" + UUID.randomUUID().toString().replace("-", "").substring(0, RANDOM_KEY_LENGTH);
    }

    private String generateUniqueInstanceKey() {
        return "INS-" + UUID.randomUUID().toString().replace("-", "").substring(0, RANDOM_KEY_LENGTH);
    }
}
