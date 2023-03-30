package com.example.finder.graph.framework.handler;

import com.example.finder.graph.framework.Edge;
import com.example.finder.graph.framework.GraphElementConvertor;
import com.example.finder.graph.framework.OResultHandler;
import com.example.finder.graph.framework.Vertex;
import com.example.finder.graph.query.EdgeQueryResult;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;


import java.util.Optional;

/**
 * 边结果处理器
 *
 * @Author Huang Yongxiang
 * @Date 2022/09/01 11:06
 */
public class OResultEdgeHandler<T extends Edge, F extends Vertex, E extends Vertex> implements OResultHandler<EdgeQueryResult<T>> {
    private final Class<T> type;
    private final Class<F> fromType;
    private final Class<E> toType;

    public OResultEdgeHandler(Class<T> type, Class<F> fromType, Class<E> toType) {
        this.type = type;
        this.fromType = fromType;
        this.toType = toType;
    }

    @Override
    public EdgeQueryResult<T> doHandle(OResultSet resultSet) {
        if (resultSet == null) {
            return null;
        }
        try {
            if (resultSet.hasNext()) {
                OResult row = resultSet.next();
                Optional<OEdge> edgeOptional = row.getEdge();
                OEdge edge = edgeOptional.orElse(null);
                if (edge == null) {
                    return null;
                }
                return GraphElementConvertor.edge2EdgeQueryResult(edge, type, fromType, toType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            resultSet.close();
        }
        return null;
    }
}
