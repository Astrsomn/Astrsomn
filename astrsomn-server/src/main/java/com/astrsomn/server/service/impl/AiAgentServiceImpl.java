package com.astrsomn.server.service.impl;

import com.astrsomn.api.runtime.common.constant.AiPromptEnum;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentResponseDTO;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentUpdateRequestDTO;
import com.astrsomn.api.runtime.common.dto.instance.AiInstanceCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.instance.AiInstanceResponseDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptResponseDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiAgentEntity;
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
        String agentKey = aiAgent.getAgentKey();
        if (StringUtils.isNotBlank(agentKey)) {
            List<AiInstanceResponseDTO> instances = aiInstanceService.queryByBizKeys(agentKey);
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
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> updateAgent(AiAgentUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_PARAM_ERROR);
        }
        AiAgentEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_NOT_FOUND);
        }

        // 更新提示词内容（通过 submit 追加新版本，不会因重复 promptKey 报错）
        String promptContent = request.getPromptContent();
        if (StringUtils.isNotBlank(promptContent) && StringUtils.isNotBlank(existing.getPromptKey())) {
            AiPromptUpdateRequestDTO promptRequest = new AiPromptUpdateRequestDTO();
            promptRequest.setPromptKey(existing.getPromptKey());
            promptRequest.setPromptTitle(request.getAgentName());
            promptRequest.setPromptContent(promptContent);
            promptRequest.setEnvCode(request.getEnvCode());
            aiPromptService.submit(promptRequest);
        }

        // 更新 Agent 实体
        AiAgentEntity aiAgent = new AiAgentEntity();
        BeanUtils.copyProperties(request, aiAgent);
        boolean result = updateById(aiAgent);
        if (!result) {
            throw new BusinessException(AiAgentErrorEnum.AGENT_UPDATE_FAILED);
        }

        // 同步推理实例列表
        String agentKey = existing.getAgentKey();
        if (StringUtils.isNotBlank(agentKey) && request.getInstanceList() != null) {
            syncInstances(agentKey, request.getInstanceList(), request.getEnvCode(), request.getRouteStrategy());
        }

        return BaseResponse.success("更新成功");
    }

    private void syncInstances(String agentKey, List<AiInstanceCreateRequestDTO> submittedInstances,
                               String envCode, String routeStrategy) {
        // 查询现有实例
        List<AiInstanceResponseDTO> existingInstances = aiInstanceService.queryByBizKeys(List.of(agentKey));
        Map<Long, AiInstanceResponseDTO> existingById = new HashMap<>();
        Map<String, AiInstanceResponseDTO> existingByKey = new HashMap<>();
        Set<Long> submittedIds = new HashSet<>();
        if (existingInstances != null) {
            for (AiInstanceResponseDTO inst : existingInstances) {
                if (inst.getId() != null) {
                    existingById.put(inst.getId(), inst);
                }
                if (StringUtils.isNotBlank(inst.getInstanceKey())) {
                    existingByKey.put(inst.getInstanceKey(), inst);
                }
            }
        }

        // 创建或更新实例，并收集已提交的实例 ID
        for (AiInstanceCreateRequestDTO inst : submittedInstances) {
            inst.setBizKey(agentKey);
            if (StringUtils.isNotBlank(envCode)) {
                inst.setEnvCode(envCode);
            }
            inst.setStatus("enabled");
            if (StringUtils.isNotBlank(routeStrategy)) {
                inst.setRouteStrategy(routeStrategy);
            }

            // 优先通过 id 匹配（最可靠）
            AiInstanceResponseDTO matched = null;
            if (inst.getId() != null) {
                matched = existingById.get(inst.getId());
            }
            // 其次通过 instanceKey 匹配
            if (matched == null && StringUtils.isNotBlank(inst.getInstanceKey())) {
                matched = existingByKey.get(inst.getInstanceKey());
            }

            if (matched != null) {
                // 更新已有实例
                submittedIds.add(matched.getId());
                inst.setId(matched.getId());
                if (StringUtils.isBlank(inst.getInstanceKey())) {
                    inst.setInstanceKey(matched.getInstanceKey());
                }
                aiInstanceService.updateById(inst);
            } else {
                // 新建实例
                if (StringUtils.isBlank(inst.getInstanceKey())) {
                    inst.setInstanceKey(KeyGenerator.generateUniqueInstanceKey());
                }
                aiInstanceService.save(inst);
            }
        }

        // 删除不再存在的实例
        if (existingInstances != null) {
            for (AiInstanceResponseDTO existing : existingInstances) {
                if (existing.getId() != null && !submittedIds.contains(existing.getId())) {
                    aiInstanceService.removeById(existing.getId());
                }
            }
        }
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
            List<String> agentKeys = records.stream()
                    .map(AiAgentResponseDTO::getAgentKey)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
            if (!agentKeys.isEmpty()) {
                List<AiInstanceResponseDTO> allInstances = aiInstanceService.queryByBizKeys(agentKeys);
                Map<String, List<AiInstanceResponseDTO>> grouped = allInstances.stream()
                        .collect(Collectors.groupingBy(AiInstanceResponseDTO::getBizKey));
                for (AiAgentResponseDTO dto : records) {
                    dto.setInstanceList(grouped.getOrDefault(dto.getAgentKey(), Collections.emptyList()));
                }
            }
        }

        return PageConverter.toResponse(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> createFullAgent(AiAgentCreateRequestDTO request) {
        String agentKey = request.getAgentKey();
        if (StringUtils.isBlank(agentKey)) {
            agentKey = KeyGenerator.generateUniqueAgentKey();
            request.setAgentKey(agentKey);
        }

        String promptKey = null;
        String promptContent = request.getPromptContent();
        if (StringUtils.isNotBlank(promptContent)) {
            AiPromptCreateRequestDTO promptRequest = new AiPromptCreateRequestDTO();
            promptKey = KeyGenerator.generateUniquePromptKey();
            promptRequest.setPromptKey(promptKey);
            promptRequest.setPromptTitle(request.getAgentName());
            promptRequest.setPromptContent(promptContent);
            promptRequest.setEnvCode(request.getEnvCode());
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
                instanceRequest.setStatus("enabled");
                if (StringUtils.isNotBlank(routeStrategy)) {
                    instanceRequest.setRouteStrategy(routeStrategy);
                }
                aiInstanceService.create(instanceRequest);
            }
        }

        return BaseResponse.success("完整Agent创建成功");
    }

}
