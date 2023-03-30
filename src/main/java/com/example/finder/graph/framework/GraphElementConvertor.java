package com.example.finder.graph.framework;

import com.example.finder.graph.factory.OrientSessionFactory;
import com.example.finder.graph.query.EdgeQueryResult;
import com.example.finder.graph.util.JsonUtil;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;

/**
 * 图元素转化器
 *
 * @Author Huang Yongxiang
 * @Date 2022/08/31 17:22
 */
public class GraphElementConvertor {
    public static <T extends Vertex> T vertex2Entity(OVertex vertex, Class<T> type) {
        if (vertex == null || type == null) {
            throw new NullPointerException();
        }
        ODatabaseSession session = null;
        if (!ODatabaseRecordThreadLocal
                .instance()
                .isDefined()) {
            if (TransactionManager.isOpenTransaction()) {
                session = TransactionManager.getCurrentSession();
                session.activateOnCurrentThread();
            } else {
                session = OrientSessionFactory
                        .getInstance()
                        .getSession();
            }
        }
        try {
            return JsonUtil.jsonStringToPojo(vertex.toJSON(), type);
        } finally {
            if (session != null && !TransactionManager.isOpenTransaction()) {
                session.close();
            }
        }
    }

    public static <T extends Edge, F extends Vertex, E extends Vertex> EdgeQueryResult<T> edge2EdgeQueryResult(OEdge edge, Class<T> type, Class<F> fromType, Class<E> endType) {
        if (edge == null || type == null) {
            throw new NullPointerException();
        }
        EdgeQueryResult<T> result = new EdgeQueryResult<>();
        T instance = edge2Entity(edge, type);
        if (instance == null) {
            return null;
        }
        try {
            F from = vertex2Entity(edge.getFrom(), fromType);
            E end = vertex2Entity(edge.getTo(), endType);
            result.setEdge(instance);
            result.setFrom(from);
            result.setTo(end);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T extends Edge> T edge2Entity(OEdge edge, Class<T> type) {
        if (edge == null || type == null) {
            throw new NullPointerException();
        }
        ODatabaseSession session = null;
        if (!ODatabaseRecordThreadLocal
                .instance()
                .isDefined()) {
            if (TransactionManager.isOpenTransaction()) {
                session = TransactionManager.getCurrentSession();
                session.activateOnCurrentThread();

            } else {
                session = OrientSessionFactory
                        .getInstance()
                        .getSession();
            }
        }
        try {
            return JsonUtil.jsonStringToPojo(edge.toJSON(), type);
        } finally {
            if (session != null && !TransactionManager.isOpenTransaction()) {
                session.close();
            }
        }

    }


}
