package com.decisionmaker;

/**
 * 节点执行状态
 *
 * @author Mairuis
 * @date 2019/11/25
 */
public enum ExecuteState {

    //失败
    FAILURE
    //完成
    , SUCCESS
    //进行中
    , RUNNING;

    public static boolean isComplete(ExecuteState state) {
        return state == SUCCESS || state == FAILURE;
    }
}
