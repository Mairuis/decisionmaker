package com.decisionmaker;

/**
 * 行为树节点的基类
 *
 * @author Mairuis
 * @since 2019/11/21
 */
public interface Node<C extends BehaviorTreeContext> {

    /**
     * 执行一次该节点的行为
     */
    ExecuteState execute();

    /**
     * 执行条件
     *
     * @return
     */
    default boolean accept() {
        return true;
    }

    ExecuteState getState();

    default Node<C> setContext(C context) {
        return this;
    }

    /**
     * 重置这个行为
     */
    default void reset() {
    }
}