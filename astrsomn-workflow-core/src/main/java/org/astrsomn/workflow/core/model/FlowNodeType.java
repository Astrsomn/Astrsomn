package org.astrsomn.workflow.core.model;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Semantic node kinds aligned with VueFlow {@code type} / {@code data.kind}.
 * Unknown strings fall back to {@link #TASK} so the canvas can use custom labels.
 */
public enum FlowNodeType {
    INPUT,
    OUTPUT,
    TASK,
    CONDITION,
    LOOP,
    PARALLEL,
    JOIN,
    WAIT,
    TERMINATE;

    private static final Map<String, FlowNodeType> ALIASES = Map.ofEntries(
            Map.entry("input", INPUT),
            Map.entry("start", INPUT),
            Map.entry("output", OUTPUT),
            Map.entry("end", OUTPUT),
            Map.entry("default", TASK),
            Map.entry("task", TASK),
            Map.entry("condition", CONDITION),
            Map.entry("if", CONDITION),
            Map.entry("loop", LOOP),
            Map.entry("while", LOOP),
            Map.entry("parallel", PARALLEL),
            Map.entry("fork", PARALLEL),
            Map.entry("join", JOIN),
            Map.entry("merge", JOIN),
            Map.entry("wait", WAIT),
            Map.entry("terminate", TERMINATE),
            Map.entry("stop", TERMINATE)
    );

    public static FlowNodeType fromNode(FlowNode node) {
        if (node == null) {
            return TASK;
        }
        if (node.getKind() != null) {
            FlowNodeType fromKind = fromString(node.getKind());
            if (fromKind != null) {
                return fromKind;
            }
        }
        if (node.getType() != null) {
            FlowNodeType fromType = fromString(node.getType());
            if (fromType != null) {
                return fromType;
            }
        }
        Object dataKind = node.getData() != null ? node.getData().get("kind") : null;
        if (dataKind != null) {
            FlowNodeType fromData = fromString(String.valueOf(dataKind));
            if (fromData != null) {
                return fromData;
            }
        }
        return TASK;
    }

    public static FlowNodeType fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String key = raw.trim().toLowerCase(Locale.ROOT);
        if (ALIASES.containsKey(key)) {
            return ALIASES.get(key);
        }
        try {
            return FlowNodeType.valueOf(key.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static FlowNodeType require(FlowNode node) {
        return Objects.requireNonNullElse(fromNode(node), TASK);
    }
}
