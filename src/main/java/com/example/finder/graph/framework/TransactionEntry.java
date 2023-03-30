package com.example.finder.graph.framework;

import com.orientechnologies.orient.core.db.ODatabaseSession;

/**
 * 事务条目，一个条目代表事务中的一种操作
 *
 * @Author Huang Yongxiang
 * @Date 2022/08/31 11:40
 */
public interface TransactionEntry {
    /**
     * 执行一个事务操作
     *
     * @param session 开启事务的会话，请不要在方法内部关闭会话
     * @return void
     * @author Huang Yongxiang
     * @date 2022/8/31 11:41
     */
    void operate(ODatabaseSession session) throws Exception;
}
