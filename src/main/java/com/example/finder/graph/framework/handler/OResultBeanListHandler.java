package com.example.finder.graph.framework.handler;

import com.example.finder.graph.framework.*;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Bean List Handler
 *
 * @Author Huang Yongxiang
 * @Date 2022/09/01 11:14
 */
public class OResultBeanListHandler<T extends GraphElement> implements OResultHandler<List<T>> {
    private final Class<T> type;

    public OResultBeanListHandler(Class<T> type) {
        this.type = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> doHandle(OResultSet resultSet) {
        if (resultSet == null) {
            return Collections.emptyList();
        }
        List<T> resultList = new ArrayList<>();
        try {
            while (resultSet.hasNext()) {
                OResult row = resultSet.next();
                if (Edge.class.isAssignableFrom(type)) {
                    Optional<OEdge> edgeOptional = row.getEdge();
                    OEdge edge = edgeOptional.orElse(null);
                    if (edge == null) {
                        continue;
                    }
                    T instance = (T) GraphElementConvertor.edge2Entity(edge, (Class<Edge>) type);
                    if (instance != null) {
                        resultList.add(instance);
                    }
                } else if (Vertex.class.isAssignableFrom(type)) {
                    Optional<OVertex> vertexOptional = row.getVertex();
                    OVertex vertex = vertexOptional.orElse(null);
                    if (vertex == null) {
                        continue;
                    }
                    T instance = (T) GraphElementConvertor.vertex2Entity(vertex, (Class<Vertex>) type);
                    if (instance != null) {
                        resultList.add(instance);
                    }
                }
            }
        } finally {
            resultSet.close();
        }
        return resultList;
    }
}
