package org.astrsomn.workflow.core.context;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mutable execution bag: variables, traversal hints, join counters.
 * Safe for parallel branches when forked per branch; merge explicitly in the engine.
 */
@Data
@Builder
public class WorkflowContext {

    @Builder.Default
    private String executionId = UUID.randomUUID().toString();
    /**
     * User and node outputs (SpEL / handlers read from here).
     */
    @Builder.Default
    private Map<String, Object> variables = new LinkedHashMap<>();
    /**
     * Internal keys (loop iteration, join arrivals) — prefix with {@link #INTERNAL_PREFIX}.
     */
    @Builder.Default
    private Map<String, Object> internal = new ConcurrentHashMap<>();
    @Builder.Default
    private String lastNodeId = null;
    @Builder.Default
    private String lastEdgeId = null;

    public static final String INTERNAL_PREFIX = "__wf_";

    public static String loopCounterKey(String loopNodeId) {
        return INTERNAL_PREFIX + "loop:" + loopNodeId;
    }

    public static String joinArrivalKey(String joinNodeId) {
        return INTERNAL_PREFIX + "join:" + joinNodeId;
    }

    public Object getVar(String key) {
        return variables != null ? variables.get(key) : null;
    }

    public void putVar(String key, Object value) {
        if (variables == null) {
            variables = new LinkedHashMap<>();
        }
        variables.put(key, value);
    }

    public void putAllVars(Map<String, ?> from) {
        if (from == null || from.isEmpty()) {
            return;
        }
        if (variables == null) {
            variables = new LinkedHashMap<>();
        }
        variables.putAll(from);
    }

    public int getIntVar(String key, int defaultValue) {
        Object v = getVar(key);
        if (v instanceof Number n) {
            return n.intValue();
        }
        return defaultValue;
    }

    public boolean getBooleanVar(String key, boolean defaultValue) {
        Object v = getVar(key);
        if (v instanceof Boolean b) {
            return b;
        }
        return defaultValue;
    }

    public Object getInternal(String key) {
        return internal != null ? internal.get(key) : null;
    }

    public void putInternal(String key, Object value) {
        if (internal == null) {
            internal = new ConcurrentHashMap<>();
        }
        internal.put(key, value);
    }

    /**
     * Shallow copy for parallel branch execution.
     */
    public WorkflowContext forkBranch() {
        Map<String, Object> varsCopy = variables != null
                ? new LinkedHashMap<>(variables)
                : new LinkedHashMap<>();
        return WorkflowContext.builder()
                .executionId(executionId)
                .variables(varsCopy)
                .internal(internal)
                .lastNodeId(lastNodeId)
                .lastEdgeId(lastEdgeId)
                .build();
    }

    public void mergeBranch(WorkflowContext branch) {
        if (branch == null || branch.getVariables() == null) {
            return;
        }
        if (variables == null) {
            variables = new LinkedHashMap<>();
        }
        variables.putAll(branch.getVariables());
    }

    public Map<String, Object> viewVariables() {
        return variables != null ? Collections.unmodifiableMap(variables) : Map.of();
    }
}
