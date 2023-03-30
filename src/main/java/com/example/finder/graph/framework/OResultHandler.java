package com.example.finder.graph.framework;

import com.orientechnologies.orient.core.sql.executor.OResultSet;

/**
 * Orient结果处理器接口
 *
 * @Author Huang Yongxiang
 * @Date 2022/08/31 16:39
 */
public interface OResultHandler<T> {
    T doHandle(OResultSet resultSet);
}
