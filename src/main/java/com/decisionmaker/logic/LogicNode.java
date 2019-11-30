package com.decisionmaker.logic;

import com.decisionmaker.BehaviorTreeBuilder;
import com.decisionmaker.ExecuteState;
import com.decisionmaker.Node;
import com.decisionmaker.builder.NodeBuilder;
import com.decisionmaker.BehaviorTreeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * 逻辑节点
 *
 * @author Mairuis
 * @since 2019/11/21
 */
public abstract class LogicNode<C extends BehaviorTreeContext> implements Node<C> {

    private static Logger LOGGER = LoggerFactory.getLogger(BehaviorTreeBuilder.class);

    private Node[] children;
    private ExecuteState state;

    public LogicNode() {
        this.state = ExecuteState.RUNNING;
    }

    @Override
    public final ExecuteState execute() {
        try {
            this.state = executeNextNode();
        } catch (RuntimeException e) {
            LOGGER.error("LogicNode execute failure by", e);
            this.state = ExecuteState.FAILURE;
        }
        return state;
    }

    public ExecuteState safeExecute(Node<C> node) {
        try {
            return node.execute();
        } catch (RuntimeException e) {
            LOGGER.error(getName() + " execute failure by", e);
        }
        return ExecuteState.FAILURE;
    }

    protected Node<C>[] buildNodeArray(NodeBuilder<C>... builders) {
        Node<C>[] nodes = new Node[builders.length];
        int i = 0;
        for (NodeBuilder<C> node : builders) {
            nodes[i] = node.build();
        }
        return nodes;
    }

    public List<Node<C>> nextPath() {
        List<Node<C>> path = new ArrayList<>();
        Node<C> next;
        while ((next = nextNode()) != null) {
            path.add(next);
        }
        return path;
    }

    @Override
    public ExecuteState getState() {
        return state;
    }

    public abstract ExecuteState executeNextNode();

    public abstract Node<C> nextNode();

    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void reset() {
        for (Node<C> node : toList()) {
            if (node instanceof LogicNode) {
                ((LogicNode<C>) node).onReset();
            } else {
                node.reset();
            }
        }
    }

    public List<Node<C>> toList() {
        //如果之前遍历过就直接取结果包装成list
        List<Node<C>> nodeList;

        if (children != null) {
            nodeList = new ArrayList<>(children.length);
            Collections.addAll(nodeList, (Node<C>[]) children);
            return nodeList;
        }

        //DFS把子节点存入list
        nodeList = new ArrayList<>();
        Node<C>[] nodeArray = this.collect();
        Stack<Node<C>> stack = new Stack<>();
        for (Node<C> node : nodeArray) {
            stack.push(node);
        }
        while (!stack.isEmpty()) {
            Node<C> node = stack.pop();
            if (node instanceof LogicNode) {
                Node<C>[] nodes = ((LogicNode<C>) node).collect();
                for (Node<C> node1 : nodes) {
                    stack.push(node1);
                }
            }
            nodeList.add(node);
        }
        children = nodeList.toArray(new Node[0]);
        return nodeList;
    }

    /**
     * 返回该点的所有子节点
     *
     * @return
     */
    public abstract Node<C>[] collect();

    protected void onReset() {

    }
}
