package org.astrsomn.workflow.starter.spel;

import org.astrsomn.workflow.core.context.WorkflowContext;
import org.astrsomn.workflow.core.model.FlowEdge;
import org.astrsomn.workflow.core.model.FlowNode;
import org.astrsomn.workflow.core.spi.ConditionEvaluator;
import org.astrsomn.workflow.core.spi.impl.BooleanVariableConditionEvaluator;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;
import java.util.Map;

/**
 * Uses {@code data.expression} as SpEL (boolean or string handle). Falls back to {@link BooleanVariableConditionEvaluator}.
 */
public class SpelConditionEvaluator implements ConditionEvaluator {

    private final ExpressionParser parser = new SpelExpressionParser();
    private final BooleanVariableConditionEvaluator fallback = new BooleanVariableConditionEvaluator();

    @Override
    public String resolveSourceHandle(WorkflowContext context, FlowNode node, List<FlowEdge> outgoingEdges)
            throws Exception {
        Map<String, Object> d = node.getData() != null ? node.getData() : Map.of();
        Object expr = d.get("expression");
        if (expr == null || String.valueOf(expr).isBlank()) {
            return fallback.resolveSourceHandle(context, node, outgoingEdges);
        }

        StandardEvaluationContext eval = new StandardEvaluationContext();
        if (context.getVariables() != null) {
            eval.setRootObject(context.getVariables());
            for (Map.Entry<String, Object> e : context.getVariables().entrySet()) {
                eval.setVariable(e.getKey(), e.getValue());
            }
        }

        Object value = parser.parseExpression(String.valueOf(expr)).getValue(eval);
        if (value instanceof String s && !s.isBlank()) {
            return s.trim();
        }
        if (value instanceof Boolean b) {
            return b ? "true" : "false";
        }
        return value != null ? String.valueOf(value) : "false";
    }
}
