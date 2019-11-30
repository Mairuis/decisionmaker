package com.decisionmaker.builder;

import com.decisionmaker.Node;
import com.decisionmaker.logic.Selector;
import com.decisionmaker.logic.Sequence;
import com.decisionmaker.BehaviorTreeContext;
import com.decisionmaker.logic.Repeat;
import lombok.Getter;
import lombok.Setter;

/**
 * 节点构造器
 *
 * @author Mairuis
 * @since 2019/11/25
 */
public abstract class NodeBuilder<C extends BehaviorTreeContext> {

    @Getter
    @Setter
    private C context;

    public NodeBuilder<C> sequence(NodeBuilder<C>... nodes) {
        return new NodeBuilder<C>() {
            @Override
            public Node<C> build() {
                return super.build(new Sequence<>(nodes).setContext(context));
            }
        };
    }

    public NodeBuilder<C> randomSelector(NodeBuilder<C>... nodes) {
        return new NodeBuilder<C>() {
            @Override
            public Node<C> build() {
                return super.build(new Selector<>(Selector.Type.RANDOM, nodes).setContext(context));
            }
        };
    }

    public NodeBuilder<C> selector(Selector.Type type, NodeBuilder<C>... nodes) {
        return new NodeBuilder<C>() {
            @Override
            public Node<C> build() {
                return super.build(new Selector<>(type, nodes).setContext(context));
            }
        };
    }

    public NodeBuilder<C> repeatByCount(NodeBuilder<C> node, int count) {
        return new NodeBuilder<C>() {
            @Override
            public Node<C> build() {
                return super.build(new Repeat<>(count, node));
            }
        };
    }

    /**
     * 构建节点
     *
     * @return
     */
    public abstract Node<C> build();

    protected Node<C> build(Node<C> node) {
        return node.setContext(context);
    }
}
