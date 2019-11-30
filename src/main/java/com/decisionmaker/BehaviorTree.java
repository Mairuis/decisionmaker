package com.decisionmaker;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 行为树
 *
 * @author Mairuis
 * @since 2019/11/21
 */
@Data
@Accessors(chain = true)
public class BehaviorTree<C extends BehaviorTreeContext> {
    private Node<C> root;

    private C context;

    public void loopOnce() {
        this.getRoot().execute();
    }
}
