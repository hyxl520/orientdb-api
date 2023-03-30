package com.example.finder.graph.framework;

import com.example.finder.graph.factory.OrientSessionFactory;
import com.orientechnologies.orient.core.db.ODatabaseSession;

/**
 * 边实体的接口
 *
 * @Author Huang Yongxiang
 * @Date 2022/08/31 10:16
 */
public interface Edge extends GraphElement {
    default String getType() {
        return this
                .getClass()
                .getSimpleName();
    }


    default boolean createSchema() {
        try (ODatabaseSession session = OrientSessionFactory
                .getInstance()
                .getSession()) {
            if (session.getClass(getType()) == null) {
                session.createEdgeClass(getType());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }
}
