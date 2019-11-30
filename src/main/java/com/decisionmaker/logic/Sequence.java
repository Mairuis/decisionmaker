package com.decisionmaker.logic;

import com.decisionmaker.BehaviorTreeContext;
import com.decisionmaker.ExecuteState;
import com.decisionmaker.Node;
import com.decisionmaker.builder.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
;

/**
 * 序列节点
 *
 * @author Mairuis
 * @since 2019/11/21
 */
public class Sequence<C extends BehaviorTreeContext> extends LogicNode<C> {

    private static Logger LOGGER = LoggerFactory.getLogger(Sequence.class);

    private Node<C>[] nodes;
    private int index;

    public Sequence(NodeBuilder<C>[] nodes) {
        this.nodes = buildNodeArray(nodes);
    }

    @Override
    public ExecuteState executeNextNode() {
        this.index = nextNodeIndex();
        Node<C> node = index >= 0 ? nodes[index] : null;
        if (node == null) {
            return ExecuteState.SUCCESS;
        }
        try {
            node.execute();
        } catch (RuntimeException e) {
            LOGGER.error("sequence execute failure by", e);
        }
        return ExecuteState.RUNNING;
    }

    private int nextNodeIndex() {
        for (int i = index; i < nodes.length; i += 1) {
            Node<C> node = nodes[i];
            if (node.accept()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Node<C> nextNode() {
        if (nodes == null || nodes.length == 0) {
            return null;
        }
        int index = nextNodeIndex();
        return index >= 0 ? nodes[index] : null;
    }

    @Override
    public Node<C>[] collect() {
        return nodes;
    }

    @Override
    protected void onReset() {
        super.onReset();
        this.index = 0;
    }
}
