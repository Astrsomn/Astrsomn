package com.astrsomn.starter.workflow.runtime.engine;

import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowNodeTraceDTO;
import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowTestRunResponseDTO;
import com.astrsomn.api.workflow.runtime.constant.AstFlowInstanceEventEnum;
import com.astrsomn.api.workflow.runtime.constant.AstFlowInstanceStateEnum;
import com.astrsomn.api.workflow.runtime.constant.AstFlowNodeStateEnum;
import com.astrsomn.api.workflow.runtime.context.AstFlowExecutionContext;
import com.astrsomn.api.workflow.runtime.context.AstFlowNodeExecutionContext;
import com.astrsomn.api.workflow.runtime.model.AstFlowExecutableNode;
import com.astrsomn.api.workflow.runtime.model.AstFlowExecutablePlan;
import com.astrsomn.api.workflow.runtime.model.AstFlowNodeExecuteResult;
import com.astrsomn.api.workflow.runtime.spi.*;
import com.astrsomn.api.workflow.runtime.spi.policy.AstFlowRateLimitPolicy;

import java.util.*;

public class DefaultAstFlowRuntimeEngine implements AstFlowRuntimeEngine {

    private final AstFlowPlanResolver planResolver;
    private final AstFlowNodeExecutorRegistry nodeExecutorRegistry;
    private final AstFlowExecutionStateMachine stateMachine;
    private final AstFlowDomainEventPublisher eventPublisher;
    private final AstFlowRateLimitPolicy rateLimitPolicy;

    public DefaultAstFlowRuntimeEngine(AstFlowPlanResolver planResolver,
                                       AstFlowNodeExecutorRegistry nodeExecutorRegistry,
                                       AstFlowExecutionStateMachine stateMachine,
                                       AstFlowDomainEventPublisher eventPublisher,
                                       AstFlowRateLimitPolicy rateLimitPolicy) {
        this.planResolver = planResolver;
        this.nodeExecutorRegistry = nodeExecutorRegistry;
        this.stateMachine = stateMachine;
        this.eventPublisher = eventPublisher;
        this.rateLimitPolicy = rateLimitPolicy;
    }

    @Override
    public AstFlowTestRunResponseDTO testRun(AstFlowTestRunRequestDTO request) {
        AstFlowExecutablePlan plan = planResolver.resolve(request);
        AstFlowExecutionContext executionContext = buildContext(request, plan);
        List<AstFlowNodeTraceDTO> traces = new ArrayList<>();
        String currentNodeId = plan.getStartNodeId();
        String lastNodeId = null;
        String message = "workflow test-run completed";
        String errorType = null;

        executionContext.setInstanceState(stateMachine.transit(AstFlowInstanceStateEnum.CREATED, AstFlowInstanceEventEnum.START));
        eventPublisher.publish("InstanceStarted", baseEvent(executionContext, null));

        if (!rateLimitPolicy.allow(executionContext)) {
            executionContext.setInstanceState(AstFlowInstanceStateEnum.FAILED);
            return buildResponse(executionContext, lastNodeId, "workflow blocked by rate limit policy", "RATE_LIMIT_BLOCKED", traces);
        }

        while (currentNodeId != null) {
            AstFlowExecutableNode node = plan.getNodes().get(currentNodeId);
            if (node == null) {
                executionContext.setInstanceState(stateMachine.transit(executionContext.getInstanceState(), AstFlowInstanceEventEnum.NODE_FAILED));
                message = "node not found: " + currentNodeId;
                errorType = "NODE_NOT_FOUND";
                break;
            }

            long startTime = System.currentTimeMillis();
            AstFlowNodeTraceDTO trace = new AstFlowNodeTraceDTO();
            trace.setNodeId(node.getNodeId());
            trace.setNodeType(node.getNodeType());
            trace.setAttemptNo(1);
            trace.setStatus(AstFlowNodeStateEnum.RUNNING.name());
            traces.add(trace);

            try {
                AstFlowNodeExecutor executor = nodeExecutorRegistry.resolve(node.getNodeType());
                AstFlowNodeExecutionContext nodeExecutionContext = AstFlowNodeExecutionContext.builder()
                        .executionContext(executionContext)
                        .node(node)
                        .attemptNo(1)
                        .nodeInput(executionContext.getVariables())
                        .build();
                AstFlowNodeExecuteResult result = executor.execute(nodeExecutionContext);
                if (result.getOutputVariables() != null) {
                    executionContext.getVariables().putAll(result.getOutputVariables());
                }

                trace.setDurationMs(System.currentTimeMillis() - startTime);
                trace.setStatus(result.getNodeState().name());
                lastNodeId = node.getNodeId();
                message = result.getMessage();

                eventPublisher.publish("NodeCompleted", baseEvent(executionContext, node.getNodeId()));

                if (result.getNodeState() == AstFlowNodeStateEnum.WAITING_HUMAN) {
                    executionContext.setInstanceState(stateMachine.transit(executionContext.getInstanceState(), AstFlowInstanceEventEnum.NODE_SUSPENDED));
                    return buildResponse(executionContext, lastNodeId, message, null, traces);
                }
                if (result.getNodeState() == AstFlowNodeStateEnum.FAILED) {
                    executionContext.setInstanceState(stateMachine.transit(executionContext.getInstanceState(), AstFlowInstanceEventEnum.NODE_FAILED));
                    errorType = result.getErrorType();
                    return buildResponse(executionContext, lastNodeId, message, errorType, traces);
                }

                currentNodeId = result.getNextNodeId();
            } catch (Exception ex) {
                trace.setDurationMs(System.currentTimeMillis() - startTime);
                trace.setStatus(AstFlowNodeStateEnum.FAILED.name());
                trace.setErrorMessage(ex.getMessage());
                executionContext.setInstanceState(stateMachine.transit(executionContext.getInstanceState(), AstFlowInstanceEventEnum.NODE_FAILED));
                message = ex.getMessage();
                errorType = "NODE_EXECUTION_EXCEPTION";
                return buildResponse(executionContext, node.getNodeId(), message, errorType, traces);
            }
        }

        if (executionContext.getInstanceState() == AstFlowInstanceStateEnum.RUNNING) {
            executionContext.setInstanceState(stateMachine.transit(executionContext.getInstanceState(), AstFlowInstanceEventEnum.FINISH));
            eventPublisher.publish("InstanceCompleted", baseEvent(executionContext, lastNodeId));
        }
        return buildResponse(executionContext, lastNodeId, message, errorType, traces);
    }

    private AstFlowExecutionContext buildContext(AstFlowTestRunRequestDTO request, AstFlowExecutablePlan plan) {
        Map<String, Object> variables = request.getVariables() == null ? new HashMap<>() : new HashMap<>(request.getVariables());
        variables.putIfAbsent("userMessage", request.getUserMessage());
        return AstFlowExecutionContext.builder()
                .traceId(UUID.randomUUID().toString())
                .instanceId(UUID.randomUUID().toString())
                .workflowKey(request.getWorkflowKey())
                .envCode("default")
                .instanceState(AstFlowInstanceStateEnum.CREATED)
                .executablePlan(plan)
                .variables(variables)
                .originalRequest(request)
                .build();
    }

    private AstFlowTestRunResponseDTO buildResponse(AstFlowExecutionContext context, String lastNodeId, String message,
                                                    String errorType, List<AstFlowNodeTraceDTO> traces) {
        AstFlowTestRunResponseDTO response = new AstFlowTestRunResponseDTO();
        response.setStatus(context.getInstanceState().name());
        response.setTraceId(context.getTraceId());
        response.setLastNodeId(lastNodeId);
        response.setMessage(message);
        response.setErrorType(errorType);
        response.setVariables(context.getVariables());
        response.setNodeTraces(traces);
        return response;
    }

    private Map<String, Object> baseEvent(AstFlowExecutionContext context, String nodeId) {
        Map<String, Object> event = new HashMap<>();
        event.put("traceId", context.getTraceId());
        event.put("instanceId", context.getInstanceId());
        event.put("workflowKey", context.getWorkflowKey());
        event.put("envCode", context.getEnvCode());
        event.put("nodeId", nodeId);
        return event;
    }
}
