package com.decisionmaker.logic;

import com.decisionmaker.ExecuteState;
import com.decisionmaker.BehaviorTreeContext;
import com.decisionmaker.Node;
import com.decisionmaker.builder.NodeBuilder;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 单一执行节点
 * 从左到右遍历每个节点是否符合条件
 * 如果符合条件则执行该节点，随后的节点不执行
 *
 * @author Mairuis
 * @since 2019/11/21
 */
public class Selector<C extends BehaviorTreeContext> extends LogicNode<C> {
    private Type type;
    private Node<C>[] nodes;
    private int sequentialIndex;
    private Node<C> random;

    public Selector(NodeBuilder<C>[] nodes) {
        this(Type.SEQUENCE, nodes);
    }

    public Selector(Type type, NodeBuilder<C>[] nodes) {
        this.type = type;
        this.nodes = buildNodeArray(nodes);
        this.sequentialIndex = 0;
    }

    @Override
    public ExecuteState executeNextNode() {
        Node<C> node = null;
        if (type == Type.SEQUENCE) {
            node = (sequentialIndex = sequentialNextNodeIndex()) >= 0 ? nodes[sequentialIndex] : null;
        } else if (type == Type.RANDOM) {
            if (random == null) {
                random = nextNode();
            }
            node = random;
        }
        if (node == null) {
            return ExecuteState.SUCCESS;
        }
        return node.execute();
    }

    private int sequentialNextNodeIndex() {
        for (int i = sequentialIndex; i < nodes.length; i += 1) {
            Node<C> node = nodes[i];
            if (node.accept()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Node<C> nextNode() {
        if (nodes.length <= 0) {
            return null;
        }
        if (type == Type.RANDOM) {
            if (random == null) {
                int index = ThreadLocalRandom.current().nextInt(0, nodes.length);
                return (random = nodes[index]);
            }
        } else if (type == Type.SEQUENCE) {
            if (sequentialIndex == -1) {
                return null;
            }
            int nextIndex = sequentialNextNodeIndex();
            return nextIndex >= 0 ? nodes[nextIndex] : null;
        }
        return null;
    }

    @Override
    public Node<C>[] collect() {
        return nodes;
    }

    @Override
    protected void onReset() {
        this.sequentialIndex = 0;
    }

    public enum Type {
        //随机
        RANDOM,
        //顺序
        SEQUENCE
    }

}
