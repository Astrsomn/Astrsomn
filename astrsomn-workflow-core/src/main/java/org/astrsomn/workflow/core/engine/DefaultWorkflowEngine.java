package org.astrsomn.workflow.core.engine;

import org.astrsomn.workflow.core.context.WorkflowContext;
import org.astrsomn.workflow.core.context.WorkflowExecutionResult;
import org.astrsomn.workflow.core.context.WorkflowStatus;
import org.astrsomn.workflow.core.exception.WorkflowException;
import org.astrsomn.workflow.core.model.FlowEdge;
import org.astrsomn.workflow.core.model.FlowNode;
import org.astrsomn.workflow.core.model.FlowNodeType;
import org.astrsomn.workflow.core.model.WorkflowDefinition;
import org.astrsomn.workflow.core.spi.ConditionEvaluator;
import org.astrsomn.workflow.core.spi.LoopContinuationPolicy;
import org.astrsomn.workflow.core.spi.NodeHandler;
import org.astrsomn.workflow.core.spi.NodeHandlerRegistry;
import org.astrsomn.workflow.core.spi.impl.BooleanVariableConditionEvaluator;
import org.astrsomn.workflow.core.spi.impl.DefaultLoopContinuationPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * Depth-first interpreter for {@link WorkflowDefinition}.
 * Supports branching (handles), loop-back edges, parallel forks with an explicit merge id, async entry.
 */
public class DefaultWorkflowEngine implements WorkflowEngine {

    private final NodeHandlerRegistry nodeHandlerRegistry;
    private final ConditionEvaluator conditionEvaluator;
    private final LoopContinuationPolicy loopContinuationPolicy;
    private final int maxSteps;
    private final Executor parallelExecutor;

    public DefaultWorkflowEngine(NodeHandlerRegistry nodeHandlerRegistry,
                                 ConditionEvaluator conditionEvaluator,
                                 LoopContinuationPolicy loopContinuationPolicy,
                                 int maxSteps,
                                 Executor parallelExecutor) {
        this.nodeHandlerRegistry = Objects.requireNonNullElseGet(nodeHandlerRegistry, NodeHandlerRegistry::new);
        this.conditionEvaluator = Objects.requireNonNullElseGet(conditionEvaluator,
                BooleanVariableConditionEvaluator::new);
        this.loopContinuationPolicy = Objects.requireNonNullElseGet(loopContinuationPolicy,
                DefaultLoopContinuationPolicy::new);
        this.maxSteps = maxSteps > 0 ? maxSteps : 10_000;
        this.parallelExecutor = parallelExecutor != null ? parallelExecutor : ForkJoinPool.commonPool();
    }

    public DefaultWorkflowEngine() {
        this(new NodeHandlerRegistry(), new BooleanVariableConditionEvaluator(),
                new DefaultLoopContinuationPolicy(), 10_000, ForkJoinPool.commonPool());
    }

    @Override
    public WorkflowExecutionResult execute(WorkflowDefinition definition, WorkflowContext context) {
        WorkflowGraph graph = new WorkflowGraph(definition);
        String current = graph.findStartNodeId();
        context.setLastEdgeId(null);
        return run(graph, context, current, null);
    }

    /**
     * Runs from {@code startId} until a terminal node or (if {@code untilExclusive} set) until the next
     * node would be {@code untilExclusive} (merge boundary for parallel branches).
     */
    public WorkflowExecutionResult run(WorkflowGraph graph, WorkflowContext context, String startId,
                                       String untilExclusive) {
        String current = startId;
        int steps = 0;
        while (current != null) {
            if (untilExclusive != null && current.equals(untilExclusive)) {
                return WorkflowExecutionResult.completed(context, current);
            }
            if (steps++ > maxSteps) {
                throw new WorkflowException("Workflow step limit exceeded: " + maxSteps);
            }
            ActResult act;
            try {
                act = actOnNode(graph, context, current, untilExclusive);
            } catch (WorkflowException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new WorkflowException(ex.getMessage(), ex);
            }
            if (act.terminal != null) {
                return act.terminal;
            }
            if (act.stopBeforeEnteringNext && untilExclusive != null
                    && act.nextNodeId != null && act.nextNodeId.equals(untilExclusive)) {
                context.setLastEdgeId(act.lastEdgeId);
                context.setLastNodeId(current);
                return WorkflowExecutionResult.completed(context, current);
            }
            context.setLastEdgeId(act.lastEdgeId);
            current = act.nextNodeId;
        }
        throw new WorkflowException("Workflow ended without reaching OUTPUT");
    }

    private ActResult actOnNode(WorkflowGraph graph, WorkflowContext context, String currentId,
                                String untilExclusive) throws Exception {
        FlowNode node = graph.node(currentId);
        if (node == null) {
            throw new WorkflowException("Unknown node id: " + currentId);
        }
        FlowNodeType type = FlowNodeType.fromNode(node);
        context.setLastNodeId(currentId);

        return switch (type) {
            case INPUT -> followSequential(graph, context, currentId, untilExclusive);
            case OUTPUT -> ActResult.done(WorkflowExecutionResult.completed(context, currentId));
            case TERMINATE -> ActResult.done(WorkflowExecutionResult.terminated(context, currentId, terminateMessage(node)));
            case CONDITION -> followCondition(graph, context, currentId, untilExclusive);
            case LOOP -> followLoop(graph, context, currentId, untilExclusive);
            case PARALLEL -> forkParallel(graph, context, node, untilExclusive);
            case JOIN -> followSequential(graph, context, currentId, untilExclusive);
            case WAIT -> actWait(graph, context, node, currentId, untilExclusive);
            case TASK -> followTask(graph, context, node, type, currentId, untilExclusive);
        };
    }

    private ActResult followSequential(WorkflowGraph graph, WorkflowContext context, String currentId,
                                       String untilExclusive) {
        FlowEdge edge = pickEdge(graph.outgoing(currentId), null);
        if (edge == null) {
            throw new WorkflowException("Node has no outgoing edge: " + currentId);
        }
        return advance(context, edge, untilExclusive);
    }

    private ActResult followCondition(WorkflowGraph graph, WorkflowContext context, String currentId,
                                      String untilExclusive) throws Exception {
        List<FlowEdge> out = graph.outgoing(currentId);
        if (out.isEmpty()) {
            throw new WorkflowException("CONDITION node has no outgoing edges: " + currentId);
        }
        String handle = conditionEvaluator.resolveSourceHandle(context, graph.node(currentId), out);
        FlowEdge edge = pickEdge(out, handle);
        if (edge == null) {
            throw new WorkflowException("No edge for CONDITION branch handle: " + handle);
        }
        return advance(context, edge, untilExclusive);
    }

    private ActResult followLoop(WorkflowGraph graph, WorkflowContext context, String currentId,
                                 String untilExclusive) throws Exception {
        FlowNode loopNode = graph.node(currentId);
        String loopKey = WorkflowContext.loopCounterKey(currentId);
        FlowEdge lastEdge = graph.edgeById(context.getLastEdgeId());
        boolean loopBack = lastEdge != null && lastEdge.isLoopBack();
        int prev = context.getInternal(loopKey) instanceof Number n ? n.intValue() : 0;
        int iteration = loopBack ? prev + 1 : 1;
        context.putInternal(loopKey, iteration);

        String handle = loopContinuationPolicy.resolve(context, loopNode, iteration, loopBack);
        FlowEdge edge = pickEdge(graph.outgoing(currentId), handle);
        if (edge == null) {
            throw new WorkflowException("LOOP node has no edge for handle: " + handle);
        }
        return advance(context, edge, untilExclusive);
    }

    private ActResult followTask(WorkflowGraph graph, WorkflowContext context, FlowNode node, FlowNodeType type,
                                 String currentId, String untilExclusive) throws Exception {
        NodeHandler handler = nodeHandlerRegistry.resolve(type, node);
        if (handler != null) {
            handler.handle(type, node, context);
        }
        return followSequential(graph, context, currentId, untilExclusive);
    }

    private ActResult actWait(WorkflowGraph graph, WorkflowContext context, FlowNode node, String currentId,
                              String untilExclusive) throws Exception {
        Map<String, Object> d = node.getData() != null ? node.getData() : Map.of();
        if (Boolean.TRUE.equals(d.get("async"))) {
            return ActResult.done(WorkflowExecutionResult.builder()
                    .status(WorkflowStatus.WAITING)
                    .context(context)
                    .lastNodeId(currentId)
                    .message("async wait")
                    .build());
        }
        long delayMs = 0L;
        if (d.get("delayMs") instanceof Number n) {
            delayMs = n.longValue();
        }
        if (delayMs > 0) {
            Thread.sleep(delayMs);
        }
        return followSequential(graph, context, currentId, untilExclusive);
    }

    private ActResult forkParallel(WorkflowGraph graph, WorkflowContext context, FlowNode parallelNode,
                                   String untilExclusive) {
        Map<String, Object> d = parallelNode.getData() != null ? parallelNode.getData() : Map.of();
        Object merge = d.get("mergeNodeId");
        if (merge == null || String.valueOf(merge).isBlank()) {
            throw new WorkflowException("PARALLEL node requires data.mergeNodeId: " + parallelNode.getId());
        }
        String mergeId = String.valueOf(merge).trim();
        List<FlowEdge> outs = graph.outgoing(parallelNode.getId());
        if (outs.isEmpty()) {
            throw new WorkflowException("PARALLEL node has no outgoing edges: " + parallelNode.getId());
        }

        List<CompletableFuture<WorkflowContext>> futures = new ArrayList<>();
        for (FlowEdge e : outs) {
            String target = e.getTarget();
            futures.add(CompletableFuture.supplyAsync(() -> {
                WorkflowContext branchCtx = context.forkBranch();
                WorkflowExecutionResult r = run(graph, branchCtx, target, mergeId);
                if (r.getStatus() != WorkflowStatus.COMPLETED) {
                    throw new WorkflowException("Parallel branch did not complete before merge: " + r.getStatus());
                }
                return r.getContext();
            }, parallelExecutor));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        for (CompletableFuture<WorkflowContext> f : futures) {
            context.mergeBranch(f.join());
        }
        context.setLastEdgeId(null);
        return ActResult.go(mergeId, null);
    }

    private ActResult advance(WorkflowContext context, FlowEdge edge, String untilExclusive) {
        String target = edge.getTarget();
        if (untilExclusive != null && target.equals(untilExclusive)) {
            return ActResult.stopBefore(target, edge.getId());
        }
        return ActResult.go(target, edge.getId());
    }

    private static String terminateMessage(FlowNode node) {
        if (node.getData() == null) {
            return "terminated";
        }
        Object m = node.getData().get("message");
        return m != null ? String.valueOf(m) : "terminated";
    }

    /**
     * Picks an outgoing edge, preferring an exact {@code sourceHandle} match, then {@code default}.
     */
    public static FlowEdge pickEdge(List<FlowEdge> outgoing, String sourceHandle) {
        if (outgoing == null || outgoing.isEmpty()) {
            return null;
        }
        if (sourceHandle != null && !sourceHandle.isBlank()) {
            for (FlowEdge e : outgoing) {
                if (sourceHandle.equals(e.getSourceHandle())) {
                    return e;
                }
            }
            for (FlowEdge e : outgoing) {
                if ("default".equalsIgnoreCase(String.valueOf(e.getSourceHandle()))) {
                    return e;
                }
            }
        }
        return outgoing.get(0);
    }
}
