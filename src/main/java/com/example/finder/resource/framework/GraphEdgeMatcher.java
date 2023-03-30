package com.example.finder.resource.framework;

import com.example.finder.graph.framework.Edge;
import com.example.finder.graph.framework.Vertex;

import java.util.Map;

/**
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-03-01 14:10
 * @email 1158055613@qq.com
 */
public interface GraphEdgeMatcher<R extends ResourceElement<? extends Edge>> extends GraphMatcher<R> {
    @Override
    GraphEdgeMatcher<R> asStart(ResourceNode<? extends Vertex> node);

    GraphEdgeMatcher<R> findDirected(Class<? extends Edge> type, RelationDirection direction);

    GraphEdgeMatcher<R> finUndirected(Class<? extends Edge> type);

    @Override
    GraphEdgeMatcher<R> filter(Map<String, Object> filterParams);

    @Override
    GraphEdgeMatcher<R> filter(String name, Object value);
}
