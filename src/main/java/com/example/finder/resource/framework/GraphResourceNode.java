package com.example.finder.resource.framework;

import com.example.finder.graph.framework.Edge;
import com.example.finder.graph.framework.TransactionManager;
import com.example.finder.graph.framework.Vertex;
import com.example.finder.graph.util.ObjectUtil;
import com.example.finder.graph.util.StringUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 图资源节点
 *
 * @author Huang Yongxiang
 * @date 2023-02-21 15:34
 * @email 1158055613@qq.com
 */
public class GraphResourceNode<O extends Vertex> implements ResourceNode<O> {
    /**
     * 资源对象
     */
    private final O source;

    String nodeId;

    public GraphResourceNode(O source) {
        this.source = source;
        if (source != null) {
            source.createSchema();
        }
    }

    public GraphResourceNode(O source, boolean createSchema) {
        this.source = source;
        if (createSchema && source != null) {
            source.createSchema();
        }
    }

    GraphResourceNode(O source, String nodeId) {
        if (StringUtils.isEmpty(nodeId)) {
            throw new NullPointerException("nodeId不能为空");
        }
        this.source = source;
        this.nodeId = nodeId;
    }

    @Override
    public O getSource() {
        return source;
    }


    @Override
    public boolean isSave() {
        return !StringUtils.isEmpty(nodeId);
    }

    @Override
    public boolean update(Map<String, Object> params) {
        boolean flag = getResourceRepository().update(params, nodeId, Vertex.class) >= 0;
        if (flag) {
            Field[] fields = ObjectUtil.getObjectFields(source);
            for (Field field : fields) {
                field.setAccessible(true);
                if (params.containsKey(field.getName())) {
                    try {
                        field.set(source, params.get(field.getName()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return flag;
    }

    @Override
    public boolean remove() {
        if (!isSave()) {
            return true;
        }
        if (getResourceRepository().deleteVertex(nodeId)) {
            nodeId = null;
            return true;
        }
        return false;
    }

    @Override
    public String getRecordId() {
        return nodeId;
    }

    @Override
    public ResourceRepository getResourceRepository() {
        return new OrientDBRepository();
    }


    @Override
    public boolean save() {
        if (isSave()) {
            return true;
        }
        try {
            nodeId = getResourceRepository().saveNode(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSave();
    }

    @Override
    public void setRecordId(String recordId) {
        this.nodeId = recordId;
    }


    @Override
    public boolean link(ResourceNode<? extends Vertex> other, ResourceRelation<? extends Edge> relation) {
        return relation.link(this, other);
    }

    @Override
    public <E extends Edge, V extends Vertex> List<ResourceRelation<? extends Edge>> links(Map<E, ResourceNode<V>> toMap) {
        return getResourceRepository().saveEdges(this, toMap, true);
    }


    @Override
    public boolean linkUndirected(ResourceNode<? extends Vertex> other, ResourceRelation<? extends Edge> relation) {
        return relation.linkUndirected(this, other);
    }

    @Override
    public <E extends Edge, V extends Vertex> List<ResourceRelation<? extends Edge>> linksUndirected(Map<E, ResourceNode<V>> toMap) {
        return getResourceRepository().saveEdges(this, toMap, false);
    }

    @Override
    public boolean unlink(ResourceNode<? extends Vertex> other, Class<? extends Edge> edgeType) {
        return TransactionManager.doTransaction(session -> getResourceRepository().deleteEdge(this.nodeId, other.getRecordId(), edgeType.getSimpleName()) && getResourceRepository().deleteEdge(other.getRecordId(), this.nodeId, edgeType.getSimpleName()));
    }
}
