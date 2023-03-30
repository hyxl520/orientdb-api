package com.example.finder.resource.framework;

import com.example.finder.graph.framework.Edge;
import com.example.finder.graph.framework.GraphElement;
import com.example.finder.graph.framework.Vertex;

import java.util.List;
import java.util.Map;

/**
 * 资源存储库接口
 *
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-23 9:38
 * @email 1158055613@qq.com
 */
public interface ResourceRepository {
    <V extends Vertex> String saveNode(ResourceNode<V> node) throws Exception;

    <E extends Edge, V1 extends Vertex, V2 extends Vertex> List<ResourceRelation<? extends Edge>> saveEdges(ResourceNode<V1> from, Map<E, ResourceNode<V2>> toMap, boolean isDirected);

    <E extends Edge, V1 extends Vertex, V2 extends Vertex> String saveEdge(ResourceRelation<E> edge, ResourceNode<V1> from, ResourceNode<V2> to, boolean isDirected) throws Exception;

    ResourceNode<? extends Vertex> queryNode(String rid);

    ResourceNode<? extends Vertex> queryNode(Map<String, Object> params);

    List<ResourceNode<? extends Vertex>> queryNodes(Map<String, Object> params);

    PagedResult<ResourceNode<? extends Vertex>> queryNodes(Map<String, Object> params, PageConfig config);

    PagedResult<ResourceNode<? extends Vertex>> queryNodes(Class<? extends Vertex> type, PageConfig pageConfig);

    ResourceRelation<? extends Edge> queryEdge(String rid);

    ResourceRelation<? extends Edge> queryEdge(Map<String, Object> params);

    List<ResourceRelation<? extends Edge>> queryEdges(Map<String, Object> params);

    PagedResult<ResourceRelation<? extends Edge>> queryEdges(Map<String, Object> params, PageConfig config);

    List<ResourceNode<? extends Vertex>> shortestPath(ResourceNode<? extends Vertex> startNode, ResourceNode<? extends Vertex> endNode, RelationDirection direction, int depth, Class<? extends Edge>... types);

    PagedResult<ResourceNode<? extends Vertex>> traverse(ResourceNode<? extends Vertex> startNode, Class<? extends Vertex> findType, Map<String, Object> filterParams, int depth, TraverseStrategy strategy, PageConfig config);

    PagedResult<ResourceNode<? extends Vertex>> traverse(ResourceNode<? extends Vertex> startNode, Map<String, Object> filterParams, int depth, TraverseStrategy strategy, PageConfig config, Class<? extends Vertex>... findTypes);

    boolean deleteVertex(String rid);

    boolean deleteEdge(String rid) throws Exception;

    boolean deleteEdge(String fromNodeId, String toNodeId, String edgeType);

    <T extends GraphElement> boolean update(T param, String rid) throws Exception;

    int update(Map<String, Object> params, String rid, Class<? extends GraphElement> type);
}
