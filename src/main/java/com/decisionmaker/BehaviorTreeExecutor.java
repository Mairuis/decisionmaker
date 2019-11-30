package com.decisionmaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 动作执行器
 *
 * @author Mairuis
 * @since 2019/11/21
 */
@SuppressWarnings("all")
public class BehaviorTreeExecutor<C extends BehaviorTreeContext> implements Runnable {

    private static Logger LOGGER = LoggerFactory.getLogger(BehaviorTreeExecutor.class);

    private BehaviorTree<C>[] trees;

    public BehaviorTreeExecutor(BehaviorTreeBuilder<C> builder, int treeCount) {
        this.trees = new BehaviorTree[treeCount];
        for (int i = 0; i < treeCount; i += 1) {
            this.trees[i] = builder.build();
        }
    }

    public void executeOnce() {
        for (BehaviorTree<C> tree : trees) {
            try {
                tree.loopOnce();
            } catch (Exception e) {
                LOGGER.error("executor exception by", e);
            }
        }
    }

    public void executeLoop() {
        while (!Thread.interrupted()) {
            try {
                this.executeOnce();
                Thread.sleep(0);
            } catch (InterruptedException ignored) {
                LOGGER.debug("中断执行");
            }
        }
    }


    @Override
    public void run() {
        this.executeLoop();
    }
}
