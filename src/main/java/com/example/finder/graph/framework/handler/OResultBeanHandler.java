package com.example.finder.graph.framework.handler;

import com.example.finder.graph.framework.*;
import com.example.finder.graph.util.ObjectUtil;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import java.util.Optional;


/**
 * Bean处理器
 *
 * @Author Huang Yongxiang
 * @Date 2022/08/31 16:42
 */
public class OResultBeanHandler<T extends GraphElement> implements OResultHandler<T> {
    private final Class<T> type;

    public OResultBeanHandler(Class<T> type) {
        this.type = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T doHandle(OResultSet resultSet) {
        if (resultSet == null) {
            return null;
        }
        T instance = (T) ObjectUtil.getClassInstance(type);
        if (instance == null) {
            return null;
        }
        try {
            if (resultSet.hasNext()) {
                OResult row = resultSet.next();
                if (Edge.class.isAssignableFrom(type)) {
                    Optional<OEdge> edgeOptional = row.getEdge();
                    OEdge edge = edgeOptional.orElse(null);
                    if (edge == null) {
                        return null;
                    }
                    return (T) GraphElementConvertor.edge2Entity(edge, (Class<Edge>) type);
                } else if (Vertex.class.isAssignableFrom(type)) {
                    Optional<OVertex> vertexOptional = row.getVertex();
                    OVertex vertex = vertexOptional.orElse(null);
                    if (vertex == null) {
                        return null;
                    }
                    return (T) GraphElementConvertor.vertex2Entity(vertex, (Class<Vertex>) type);
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            resultSet.close();
        }
        return null;
    }
}
