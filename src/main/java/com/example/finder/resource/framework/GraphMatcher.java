package com.example.finder.resource.framework;

import com.example.finder.graph.framework.GraphElement;
import com.example.finder.graph.framework.Vertex;

import java.util.List;
import java.util.Map;

/**
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-03-01 14:01
 * @email 1158055613@qq.com
 */
public interface GraphMatcher<R extends ResourceElement<? extends GraphElement>> {
    GraphMatcher<R> asStart(ResourceNode<? extends Vertex> node);

    GraphMatcher<R> filter(Map<String, Object> filterParams);

    GraphMatcher<R> filter(String name, Object value);

    List<R> collect(Class<? extends GraphElement> type);

    PagedResult<R> collect(Class<? extends GraphElement> type, PageConfig config);
}
