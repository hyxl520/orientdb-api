package com.example.finder.resource.framework;

import com.example.finder.graph.framework.Edge;
import com.example.finder.graph.framework.Vertex;

import java.util.List;
import java.util.Map;

/**
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-21 16:24
 * @email 1158055613@qq.com
 */
public interface ResourceNode<O extends Vertex> extends ResourceElement<O> {

    /**
     * 通过某种关系连接到另一个节点，关系指向从本节点执向other节点，该操作是立即完成的
     *
     * @param other    其他节点
     * @param relation 关系
     * @return boolean
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/2/21 15:53
     */
    boolean link(ResourceNode<? extends Vertex> other, ResourceRelation<? extends Edge> relation);

    <E extends Edge, V extends Vertex> List<ResourceRelation<? extends Edge>> links(Map<E, ResourceNode<V>> toMap);

    /**
     * 建立与另一个节点的某种关系，关系是双向的，该操作是立即完成的
     *
     * @param other    其他节点
     * @param relation 关系
     * @return boolean
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/2/21 16:38
     */
    boolean linkUndirected(ResourceNode<? extends Vertex> other, ResourceRelation<? extends Edge> relation);

    <E extends Edge, V extends Vertex> List<ResourceRelation<? extends Edge>> linksUndirected(Map<E, ResourceNode<V>> toMap);

    /**
     * 断开两个节点之间的一个关系，该操作是立即完成的
     *
     * @param other    其他节点
     * @param edgeType 要断开的关系类型
     * @return boolean
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/2/21 15:57
     */
    boolean unlink(ResourceNode<? extends Vertex> other, Class<? extends Edge> edgeType);
}
