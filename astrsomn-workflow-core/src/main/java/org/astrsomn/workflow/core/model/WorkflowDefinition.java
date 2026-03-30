package org.astrsomn.workflow.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Serializable workflow graph (VueFlow export compatible).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkflowDefinition {

    private String id;
    private String name;
    @Builder.Default
    private List<FlowNode> nodes = new ArrayList<>();
    @Builder.Default
    private List<FlowEdge> edges = new ArrayList<>();
    private Object meta;
}
