package org.astrsomn.workflow.core.spi.impl;

import org.astrsomn.workflow.core.context.WorkflowContext;
import org.astrsomn.workflow.core.model.FlowEdge;
import org.astrsomn.workflow.core.model.FlowNode;
import org.astrsomn.workflow.core.spi.ConditionEvaluator;

import java.util.List;
import java.util.Locale;

/**
 * Uses {@code data.conditionKey} as a boolean variable name; maps to outgoing {@code sourceHandle}
 * {@code true}/{@code false} (case-insensitive) or first matching edge.
 */
public class BooleanVariableConditionEvaluator implements ConditionEvaluator {

    @Override
    public String resolveSourceHandle(WorkflowContext context, FlowNode node, List<FlowEdge> outgoingEdges)
            throws Exception {
        if (node.getData() == null || node.getData().get("conditionKey") == null) {
            return firstHandle(outgoingEdges, "default");
        }
        String key = String.valueOf(node.getData().get("conditionKey"));
        boolean flag = context.getBooleanVar(key, false);
        String want = flag ? "true" : "false";
        for (FlowEdge e : outgoingEdges) {
            String h = e.getSourceHandle();
            if (h != null && want.equalsIgnoreCase(h.trim())) {
                return h;
            }
        }
        for (FlowEdge e : outgoingEdges) {
            String h = e.getSourceHandle();
            if (h != null && want.equals(sanitize(h))) {
                return h;
            }
        }
        return firstHandle(outgoingEdges, want);
    }

    private static String sanitize(String h) {
        return h.trim().toLowerCase(Locale.ROOT);
    }

    private static String firstHandle(List<FlowEdge> outgoingEdges, String fallback) {
        if (outgoingEdges.isEmpty()) {
            return fallback;
        }
        FlowEdge e = outgoingEdges.get(0);
        return e.getSourceHandle() != null ? e.getSourceHandle() : fallback;
    }
}
