package org.astrsomn.workflow.core.spi;

import org.astrsomn.workflow.core.model.FlowNodeType;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Routes nodes to handlers by {@code data.handlerKey} or by {@link FlowNodeType}.
 */
public class NodeHandlerRegistry {

    private final Map<String, NodeHandler> byKey = new ConcurrentHashMap<>();
    private final Map<FlowNodeType, NodeHandler> byType = new ConcurrentHashMap<>();
    private volatile NodeHandler defaultHandler;

    public void registerByKey(String handlerKey, NodeHandler handler) {
        Objects.requireNonNull(handlerKey, "handlerKey");
        Objects.requireNonNull(handler, "handler");
        byKey.put(handlerKey, handler);
    }

    public void registerByType(FlowNodeType type, NodeHandler handler) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(handler, "handler");
        byType.put(type, handler);
    }

    public void setDefaultHandler(NodeHandler handler) {
        this.defaultHandler = handler;
    }

    public NodeHandler resolve(FlowNodeType nodeType, org.astrsomn.workflow.core.model.FlowNode node) {
        if (node != null && node.getData() != null) {
            Object hk = node.getData().get("handlerKey");
            if (hk != null) {
                NodeHandler h = byKey.get(String.valueOf(hk));
                if (h != null) {
                    return h;
                }
            }
        }
        NodeHandler typed = byType.get(nodeType);
        if (typed != null) {
            return typed;
        }
        return defaultHandler;
    }
}
