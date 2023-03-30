package com.example.finder.resource.framework;

import com.example.finder.graph.framework.Edge;
import com.example.finder.graph.framework.Vertex;

/**
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-21 16:26
 * @email 1158055613@qq.com
 */
public interface ResourceRelation<R extends Edge> extends ResourceElement<R> {
    String getInEdgeId();

    String getOutEdgeId();

    /**
     * 关系是否是单向的
     *
     * @return boolean
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/2/23 13:49
     */
    boolean isDirected();

    /**
     * 以该关系连接两个顶点，方向从from到to
     *
     * @param from 起始顶点
     * @param to   终止顶点
     * @return boolean
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/2/22 13:04
     */
    boolean link(ResourceNode<? extends Vertex> from, ResourceNode<? extends Vertex> to);

    /**
     * 以该关系无向的连接两个顶点
     *
     * @param node1 顶点1
     * @param node2 顶点2
     * @return boolean
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/2/22 13:06
     */
    boolean linkUndirected(ResourceNode<? extends Vertex> node1, ResourceNode<? extends Vertex> node2);

    /**
     * 断开该关系的连接
     *
     * @return boolean
     * @author JingGe(* ^ ▽ ^ *)
     * @date 2023/2/22 17:07
     */
    boolean unlink();
}
