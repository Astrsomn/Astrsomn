package org.astrsomn.workflow.core.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * VueFlow node: {@code id}, {@code type}, {@code position}, {@code data}.
 * Extra JSON fields are preserved for forward compatibility.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlowNode {

    private String id;
    /**
     * VueFlow visual type; may mirror {@link FlowNodeType} name or a custom string.
     */
    private String type;
    /**
     * Optional explicit semantic kind when {@code type} is purely visual.
     */
    private String kind;
    private FlowNodePosition position;
    private Map<String, Object> data;

    @JsonIgnore
    private final Map<String, Object> extra = new LinkedHashMap<>();

    @JsonAnyGetter
    public Map<String, Object> any() {
        return extra.isEmpty() ? null : extra;
    }

    @JsonAnySetter
    public void putExtra(String name, Object value) {
        if ("id".equals(name) || "type".equals(name) || "kind".equals(name)
                || "position".equals(name) || "data".equals(name)) {
            return;
        }
        extra.put(name, value);
    }
}
