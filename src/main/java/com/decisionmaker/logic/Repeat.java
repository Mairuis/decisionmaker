package com.decisionmaker.logic;

import com.decisionmaker.ExecuteState;
import com.decisionmaker.Node;
import com.decisionmaker.builder.NodeBuilder;
import com.decisionmaker.BehaviorTreeContext;

/**
 * 重复节点
 * 重复做子树的行为，由两种策略，一种是根据次数重复，另一种则是根据条件重复
 *
 * @author Mairuis
 * @since 2019/11/21
 */
public class Repeat<C extends BehaviorTreeContext> extends LogicNode<C> {

    private Node<C> node;
    private Type type;
    private int time;
    private int countTime;

    public Repeat(int time, NodeBuilder<C> builder) {
        this.node = builder.build();
        this.type = Type.COUNT;
        this.time = time;
    }

    public Repeat(Type type, NodeBuilder<C> builder) {
        this.node = builder.build();
        this.type = type;
    }

    @Override
    public Node<C> nextNode() {
        if (type == Type.CONDITION) {
            if (node.accept()) {
                return node;
            }
        } else if (type == Type.COUNT) {
            if (countTime > 0) {
                return node;
            }
        } else if (type == Type.FOREVER) {
            return node;
        }
        return null;
    }

    @Override
    public ExecuteState executeNextNode() {
        Node<C> next = nextNode();
        if (next == null) {
            return ExecuteState.SUCCESS;
        } else {
            ExecuteState state = safeExecute(next);
            if (ExecuteState.isComplete(state)) {
                countTime -= 1;
            }
            return state;
        }
    }

    @Override
    public Node<C>[] collect() {
        return new Node[]{node};
    }

    public enum Type {
        //次数
        COUNT,
        //条件
        CONDITION,
        //无限重复
        FOREVER
    }

    @Override
    protected void onReset() {
        this.countTime = time;
    }
}
