package com.example.finder.resource.framework;

import com.example.finder.graph.framework.Edge;
import com.example.finder.graph.framework.Vertex;

import java.util.Map;

/**
 * 图节点遍历公共接口
 *
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-27 14:10
 * @email 1158055613@qq.com
 */
public interface GraphNodeMatcher<R extends ResourceElement<? extends Vertex>> extends GraphMatcher<R> {

    @Override
    GraphNodeMatcher<R> asStart(ResourceNode<? extends Vertex> node);

    GraphNodeMatcher<R> findDirected(Class<? extends Edge> type, RelationDirection direction);

    GraphNodeMatcher<R> findUndirected(Class<? extends Edge> type);

    GraphNodeMatcher<R> filter(Map<String, Object> filterParams);

    GraphNodeMatcher<R> filter(String name, Object value);

}
