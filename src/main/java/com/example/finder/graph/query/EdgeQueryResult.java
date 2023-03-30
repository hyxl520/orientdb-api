package com.example.finder.graph.query;


import com.example.finder.graph.framework.Edge;
import com.example.finder.graph.framework.GraphElement;
import com.example.finder.graph.framework.Vertex;
import lombok.Data;

/**
 * 边结果集
 *
 * @Author Huang Yongxiang
 * @Date 2022/08/31 17:18
 */
@Data
public class EdgeQueryResult<E extends Edge> implements GraphElement {
    /**
     * 起始节点
     */
    private Vertex from;
    /**
     * 终止节点
     */
    private Vertex to;
    /**
     * 节点内容
     */
    private E edge;

    @Override
    public String getType() {
        return this
                .getClass()
                .getName();
    }

    @Override
    public boolean createSchema() {
        throw new UnsupportedOperationException();
    }
}
