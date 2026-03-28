package org.astrsomn.workflow.core.spi.impl;

import org.astrsomn.workflow.core.context.WorkflowContext;
import org.astrsomn.workflow.core.model.FlowNode;
import org.astrsomn.workflow.core.spi.LoopContinuationPolicy;

import java.util.Map;

/**
 * Reads {@code data.maxIterations}, {@code data.whileKey} (boolean variable), handles {@code body}/{@code exit}.
 */
public class DefaultLoopContinuationPolicy implements LoopContinuationPolicy {

    @Override
    public String resolve(WorkflowContext context, FlowNode loopNode, int iteration, boolean enteredViaLoopBackEdge)
            throws Exception {
        Map<String, Object> d = loopNode.getData() != null ? loopNode.getData() : Map.of();
        int max = Integer.MAX_VALUE;
        if (d.get("maxIterations") instanceof Number n) {
            max = Math.max(0, n.intValue());
        }
        if (iteration > max) {
            return exitHandle(d);
        }
        Object wk = d.get("whileKey");
        if (wk != null) {
            boolean ok = context.getBooleanVar(String.valueOf(wk), true);
            if (!ok) {
                return exitHandle(d);
            }
        }
        return bodyHandle(d);
    }

    private static String bodyHandle(Map<String, Object> d) {
        Object body = d.get("bodyHandle");
        return body != null ? String.valueOf(body) : "body";
    }

    private static String exitHandle(Map<String, Object> d) {
        Object exit = d.get("exitHandle");
        return exit != null ? String.valueOf(exit) : "exit";
    }
}
