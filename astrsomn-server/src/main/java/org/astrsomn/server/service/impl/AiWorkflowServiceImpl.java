package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.workflow.*;
import org.astrsomn.core.common.entity.AiWorkflowEntity;
import org.astrsomn.core.mapper.AiWorkflowMapper;
import org.astrsomn.server.service.AiWorkflowService;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.workflow.core.context.WorkflowContext;
import org.astrsomn.workflow.core.context.WorkflowExecutionResult;
import org.astrsomn.workflow.core.engine.WorkflowEngine;
import org.astrsomn.workflow.core.model.WorkflowDefinition;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiWorkflowServiceImpl extends ServiceImpl<AiWorkflowMapper, AiWorkflowEntity> implements AiWorkflowService {

    public static final String DEFAULT_GRAPH_JSON = """
            {"id":"wf","name":"draft","nodes":[
              {"id":"n1","type":"input","position":{"x":80,"y":120},"data":{"label":"开始","kind":"input"}},
              {"id":"n2","type":"output","position":{"x":420,"y":120},"data":{"label":"结束","kind":"output"}}
            ],"edges":[{"id":"e1","source":"n1","target":"n2"}]}""";

    private final AstrsomnProperties astrsomnProperties;
    private final WorkflowEngine workflowEngine;
    private final ObjectMapper objectMapper;

    @Override
    public BaseResponse<String> create(AiWorkflowCreateRequestDTO request) {
        AiWorkflowEntity entity = new AiWorkflowEntity();
        BeanUtils.copyProperties(request, entity);
        if (StringUtils.isBlank(entity.getEnvCode())) {
            entity.setEnvCode(astrsomnProperties.getEnvCode());
        }
        if (StringUtils.isBlank(entity.getGraphJson())) {
            entity.setGraphJson(DEFAULT_GRAPH_JSON);
        }
        if (entity.getVersionNo() == null) {
            entity.setVersionNo(1);
        }
        if (StringUtils.isBlank(entity.getStatus())) {
            entity.setStatus("DRAFT");
        }
        boolean ok = save(entity);
        return ok ? BaseResponse.success("创建成功") : BaseResponse.fail("创建失败", null);
    }

    @Override
    public BaseResponse<String> update(AiWorkflowUpdateRequestDTO request) {
        if (request.getId() == null) {
            return BaseResponse.fail("id 不能为空", null);
        }
        AiWorkflowEntity entity = new AiWorkflowEntity();
        BeanUtils.copyProperties(request, entity);
        boolean ok = updateById(entity);
        return ok ? BaseResponse.success("更新成功") : BaseResponse.fail("更新失败", null);
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        for (long id : ids) {
            AiWorkflowEntity e = new AiWorkflowEntity();
            e.setId(id);
            e.setDeleted(true);
            updateById(e);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<AiWorkflowResponseDTO> detail(Long id) {
        AiWorkflowEntity entity = getOne(new LambdaQueryWrapper<AiWorkflowEntity>()
                .eq(AiWorkflowEntity::getId, id)
                .eq(AiWorkflowEntity::getEnvCode, astrsomnProperties.getEnvCode())
                .eq(AiWorkflowEntity::getDeleted, false));
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        AiWorkflowResponseDTO dto = new AiWorkflowResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return BaseResponse.success(dto);
    }

    @Override
    public PageResponse<AiWorkflowResponseDTO> queryPage(BasePageRequest<AiWorkflowQueryRequestDTO> request) {
        AiWorkflowQueryRequestDTO q = request.getParam();
        LambdaQueryWrapper<AiWorkflowEntity> w = new LambdaQueryWrapper<AiWorkflowEntity>()
                .eq(AiWorkflowEntity::getEnvCode, astrsomnProperties.getEnvCode())
                .eq(AiWorkflowEntity::getDeleted, false);
        if (q != null) {
            if (StringUtils.isNotBlank(q.getWorkflowKey())) {
                w.eq(AiWorkflowEntity::getWorkflowKey, q.getWorkflowKey());
            }
            if (StringUtils.isNotBlank(q.getWorkflowName())) {
                w.like(AiWorkflowEntity::getWorkflowName, q.getWorkflowName());
            }
            if (StringUtils.isNotBlank(q.getStatus())) {
                w.eq(AiWorkflowEntity::getStatus, q.getStatus());
            }
        }
        w.orderByDesc(AiWorkflowEntity::getUpdateTime);
        Page<AiWorkflowEntity> pageParam = new Page<>(request.getPageNo(), request.getPageSize());
        IPage<AiWorkflowEntity> page = page(pageParam, w);
        List<AiWorkflowResponseDTO> list = page.getRecords().stream().map(e -> {
            AiWorkflowResponseDTO dto = new AiWorkflowResponseDTO();
            BeanUtils.copyProperties(e, dto);
            return dto;
        }).collect(Collectors.toList());
        IPage<AiWorkflowResponseDTO> out = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        out.setRecords(list);
        return PageResponse.buildResponse(out);
    }

    @Override
    public BaseResponse<String> publish(Long id) {
        AiWorkflowEntity entity = getById(id);
        if (entity == null || Boolean.TRUE.equals(entity.getDeleted())) {
            return BaseResponse.fail("记录不存在", null);
        }
        entity.setStatus("PUBLISHED");
        boolean ok = updateById(entity);
        return ok ? BaseResponse.success("发布成功") : BaseResponse.fail("发布失败", null);
    }

    @Override
    public BaseResponse<AiWorkflowTestRunResponseDTO> testRun(AiWorkflowTestRunRequestDTO request) {
        AiWorkflowEntity entity = null;
        if (request.getId() != null) {
            entity = getOne(new LambdaQueryWrapper<AiWorkflowEntity>()
                    .eq(AiWorkflowEntity::getId, request.getId())
                    .eq(AiWorkflowEntity::getEnvCode, astrsomnProperties.getEnvCode())
                    .eq(AiWorkflowEntity::getDeleted, false));
        } else if (StringUtils.isNotBlank(request.getWorkflowKey())) {
            String key = request.getWorkflowKey().trim();
            String env = astrsomnProperties.getEnvCode();
            // 先取已发布最新版；若无则回退为同 Key+环境下最新一条（含草稿），便于编排页未发布时也能测
            Page<AiWorkflowEntity> pg = page(new Page<>(1, 1), new LambdaQueryWrapper<AiWorkflowEntity>()
                    .eq(AiWorkflowEntity::getWorkflowKey, key)
                    .eq(AiWorkflowEntity::getEnvCode, env)
                    .eq(AiWorkflowEntity::getStatus, "PUBLISHED")
                    .eq(AiWorkflowEntity::getDeleted, false)
                    .orderByDesc(AiWorkflowEntity::getVersionNo));
            if (!pg.getRecords().isEmpty()) {
                entity = pg.getRecords().get(0);
            } else {
                pg = page(new Page<>(1, 1), new LambdaQueryWrapper<AiWorkflowEntity>()
                        .eq(AiWorkflowEntity::getWorkflowKey, key)
                        .eq(AiWorkflowEntity::getEnvCode, env)
                        .eq(AiWorkflowEntity::getDeleted, false)
                        .orderByDesc(AiWorkflowEntity::getVersionNo));
                if (!pg.getRecords().isEmpty()) {
                    entity = pg.getRecords().get(0);
                }
            }
        }
        if (entity == null) {
            String env = astrsomnProperties.getEnvCode();
            return BaseResponse.fail(
                    "未找到工作流：请确认 workflowKey 已在当前环境 ENV_CODE=" + env + " 下创建，"
                            + "且前端工作空间环境与后端一致；若仅本地新建，请先在列表保存并核对 Key。",
                    null);
        }
        try {
            WorkflowDefinition def = objectMapper.readValue(entity.getGraphJson(), WorkflowDefinition.class);
            WorkflowContext ctx = WorkflowContext.builder().build();
            String um = StringUtils.defaultIfBlank(request.getUserMessage(), "ping");
            ctx.putVar("userMessage", um);
            String mk = StringUtils.isNotBlank(request.getMemoryKey()) ? request.getMemoryKey() : ctx.getExecutionId();
            ctx.putVar("memoryKey", mk);

            WorkflowExecutionResult r = workflowEngine.execute(def, ctx);
            AiWorkflowTestRunResponseDTO out = new AiWorkflowTestRunResponseDTO();
            out.setStatus(r.getStatus() != null ? r.getStatus().name() : null);
            out.setLastNodeId(r.getLastNodeId());
            out.setMessage(r.getMessage());
            if (r.getContext() != null && r.getContext().getVariables() != null) {
                out.setVariables(new LinkedHashMap<>(r.getContext().getVariables()));
            }
            return BaseResponse.success(out);
        } catch (Exception e) {
            return BaseResponse.fail("执行失败: " + e.getMessage(), null);
        }
    }
}
