package com.decisionmaker;

import com.decisionmaker.builder.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
;

/**
 * 行为树构造器
 *
 * @author Mairuis
 * @since 2019/11/25
 */
public abstract class BehaviorTreeBuilder<C extends BehaviorTreeContext> {

    private static Logger LOGGER = LoggerFactory.getLogger(BehaviorTreeBuilder.class);

    protected BehaviorTree<C> initialize(BehaviorTree<C> tree) {
        return tree;
    }

    protected abstract NodeBuilder<C> root();

    protected abstract Class<C> contextClass();

    public BehaviorTree<C> build() {
        BehaviorTree<C> tree = new BehaviorTree<>();
        try {
            tree.setContext(contextClass().newInstance());
            tree.setRoot(root().build());
            return this.initialize(tree);
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("build tree failure by", e);
        }
        return tree;
    }
}
