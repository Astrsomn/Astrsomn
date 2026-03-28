package org.astrsomn.workflow.core.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * VueFlow edge: {@code id}, {@code source}, {@code target}, optional handles for branching.
 * Use {@code sourceHandle} to label branches (e.g. {@code true}/{@code false}, {@code body}/{@code exit}).
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlowEdge {

    private String id;
    private String source;
    private String target;
    private String sourceHandle;
    private String targetHandle;
    private String label;
    private Map<String, Object> data;

    @JsonIgnore
    private final Map<String, Object> extra = new LinkedHashMap<>();

    @JsonAnyGetter
    public Map<String, Object> any() {
        return extra.isEmpty() ? null : extra;
    }

    @JsonAnySetter
    public void putExtra(String name, Object value) {
        if ("id".equals(name) || "source".equals(name) || "target".equals(name)
                || "sourceHandle".equals(name) || "targetHandle".equals(name)
                || "label".equals(name) || "data".equals(name)) {
            return;
        }
        extra.put(name, value);
    }

    public boolean isLoopBack() {
        if (data != null && Boolean.TRUE.equals(data.get("loopBack"))) {
            return true;
        }
        return extra.containsKey("loopBack") && Boolean.TRUE.equals(extra.get("loopBack"));
    }
}
