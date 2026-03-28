package org.astrsomn.workflow.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * VueFlow {@code position} for round-trip with the editor; ignored by the engine.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlowNodePosition {
    private Double x;
    private Double y;
}
