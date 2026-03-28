package org.astrsomn.workflow.core.engine;

import org.astrsomn.workflow.core.context.WorkflowContext;
import org.astrsomn.workflow.core.context.WorkflowExecutionResult;
import org.astrsomn.workflow.core.context.WorkflowStatus;
import org.astrsomn.workflow.core.model.FlowEdge;
import org.astrsomn.workflow.core.model.FlowNode;
import org.astrsomn.workflow.core.model.FlowNodePosition;
import org.astrsomn.workflow.core.model.WorkflowDefinition;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultWorkflowEngineTest {

    @Test
    void linearInputToOutput() {
        FlowNode in = new FlowNode();
        in.setId("a");
        in.setType("input");
        in.setPosition(new FlowNodePosition(0d, 0d));

        FlowNode out = new FlowNode();
        out.setId("b");
        out.setType("output");

        FlowEdge e = new FlowEdge();
        e.setId("e1");
        e.setSource("a");
        e.setTarget("b");

        WorkflowDefinition def = WorkflowDefinition.builder()
                .id("wf1")
                .nodes(List.of(in, out))
                .edges(List.of(e))
                .build();

        WorkflowContext ctx = WorkflowContext.builder().build();
        DefaultWorkflowEngine engine = new DefaultWorkflowEngine();
        WorkflowExecutionResult r = engine.execute(def, ctx);
        assertEquals(WorkflowStatus.COMPLETED, r.getStatus());
        assertEquals("b", r.getLastNodeId());
    }

    @Test
    void conditionBranchesByVariable() {
        FlowNode in = node("s", "input");
        FlowNode cond = node("c", "condition");
        cond.setData(Map.of("conditionKey", "flag"));
        FlowNode t = node("t", "output");
        FlowNode f = node("f", "output");

        FlowEdge e1 = edge("e1", "s", "c");
        FlowEdge e2 = edge("e2", "c", "t");
        e2.setSourceHandle("true");
        FlowEdge e3 = edge("e3", "c", "f");
        e3.setSourceHandle("false");

        WorkflowDefinition def = WorkflowDefinition.builder()
                .nodes(List.of(in, cond, t, f))
                .edges(List.of(e1, e2, e3))
                .build();

        WorkflowContext ctx = WorkflowContext.builder().build();
        ctx.putVar("flag", true);
        DefaultWorkflowEngine engine = new DefaultWorkflowEngine();
        WorkflowExecutionResult r = engine.execute(def, ctx);
        assertEquals(WorkflowStatus.COMPLETED, r.getStatus());
        assertEquals("t", r.getLastNodeId());
    }

    private static FlowNode node(String id, String type) {
        FlowNode n = new FlowNode();
        n.setId(id);
        n.setType(type);
        return n;
    }

    private static FlowEdge edge(String id, String source, String target) {
        FlowEdge e = new FlowEdge();
        e.setId(id);
        e.setSource(source);
        e.setTarget(target);
        return e;
    }
}
