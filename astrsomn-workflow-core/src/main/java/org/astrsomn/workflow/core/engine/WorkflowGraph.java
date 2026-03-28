package org.astrsomn.workflow.core.engine;

import org.astrsomn.workflow.core.exception.WorkflowException;
import org.astrsomn.workflow.core.model.FlowEdge;
import org.astrsomn.workflow.core.model.FlowNode;
import org.astrsomn.workflow.core.model.FlowNodeType;
import org.astrsomn.workflow.core.model.WorkflowDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Indexed graph built from a {@link WorkflowDefinition}.
 */
public final class WorkflowGraph {

    private final WorkflowDefinition definition;
    private final Map<String, FlowNode> nodesById;
    private final Map<String, FlowEdge> edgesById;
    private final Map<String, List<FlowEdge>> outgoing;
    private final Map<String, List<FlowEdge>> incoming;

    public WorkflowGraph(WorkflowDefinition definition) {
        this.definition = Objects.requireNonNull(definition, "definition");
        this.nodesById = new HashMap<>();
        this.edgesById = new HashMap<>();
        this.outgoing = new HashMap<>();
        this.incoming = new HashMap<>();
        index();
    }

    private void index() {
        if (definition.getNodes() != null) {
            for (FlowNode n : definition.getNodes()) {
                if (n.getId() == null || n.getId().isBlank()) {
                    throw new WorkflowException("Workflow node id must not be blank");
                }
                nodesById.put(n.getId(), n);
            }
        }
        if (definition.getEdges() != null) {
            for (FlowEdge e : definition.getEdges()) {
                if (e.getSource() == null || e.getTarget() == null) {
                    throw new WorkflowException("Edge source/target must be set: " + e.getId());
                }
                if (e.getId() != null && !e.getId().isBlank()) {
                    edgesById.put(e.getId(), e);
                }
                outgoing.computeIfAbsent(e.getSource(), k -> new ArrayList<>()).add(e);
                incoming.computeIfAbsent(e.getTarget(), k -> new ArrayList<>()).add(e);
            }
        }
    }

    public FlowEdge edgeById(String edgeId) {
        return edgeId == null ? null : edgesById.get(edgeId);
    }

    public FlowNode node(String id) {
        return nodesById.get(id);
    }

    public List<FlowEdge> outgoing(String nodeId) {
        return outgoing.getOrDefault(nodeId, List.of());
    }

    public int incomingCount(String nodeId) {
        return incoming.getOrDefault(nodeId, List.of()).size();
    }

    public String findStartNodeId() {
        for (FlowNode n : nodesById.values()) {
            if (FlowNodeType.fromNode(n) == FlowNodeType.INPUT) {
                return n.getId();
            }
        }
        throw new WorkflowException("Workflow must contain one INPUT (or start/input) node");
    }

    public void validateReachability() {
        // optional: could BFS from start; skipped for MVP
    }

    public Map<String, FlowNode> getNodesById() {
        return Collections.unmodifiableMap(nodesById);
    }
}
