package com.example.finder.resource.framework;

import com.example.finder.graph.framework.Edge;
import com.example.finder.graph.framework.TransactionManager;
import com.example.finder.graph.framework.Vertex;
import com.example.finder.graph.util.ObjectUtil;
import com.example.finder.graph.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 资源关系
 *
 * @author Huang Yongxiang
 * @date 2023-02-21 15:35
 * @email 1158055613@qq.com
 */
public class GraphResourceRelation<R extends Edge> implements ResourceRelation<R> {
    /**
     * 关系对象
     */
    private final R source;

    String inEdgeId;

    String outEdgeId;

    public GraphResourceRelation(R source) {
        this.source = source;
        if (source != null) {
            source.createSchema();
        }
    }

    public GraphResourceRelation(R source, boolean createSchema) {
        this.source = source;
        if (createSchema && source != null) {
            source.createSchema();
        }
    }

    GraphResourceRelation(R source, String edgeId) {
        this(source, edgeId, edgeId);
    }

    public GraphResourceRelation(R source, String inEdgeId, String outEdgeId) {
        if (StringUtils.isEmpty(inEdgeId) && StringUtils.isEmpty(outEdgeId)) {
            throw new NullPointerException("edgeId不能为空");
        }
        this.source = source;
        this.inEdgeId = inEdgeId;
        this.outEdgeId = outEdgeId;
    }

    @Override
    public boolean isDirected() {
        if (!isSave()) {
            throw new UnsupportedOperationException("该边尚未保存，无法判断方向");
        }
        return inEdgeId.equals(outEdgeId);
    }

    @Override
    public R getSource() {
        return source;
    }

    @Override
    public boolean save() {
        throw new UnsupportedOperationException("关系无法直接保存，请使用link方法");
    }

    public void setRecordId(String recordId) {
        this.outEdgeId = recordId;
        inEdgeId = recordId;

    }

    @Override
    public boolean isSave() {
        return !StringUtils.isEmpty(inEdgeId) && !StringUtils.isEmpty(outEdgeId);
    }

    @Override
    public boolean update(Map<String, Object> params) {
        boolean flag = false;
        if (isDirected()) {
            flag = getResourceRepository().update(params, getRecordId(), Edge.class) >= 0;
        } else {
            flag = TransactionManager.doTransaction(session -> getResourceRepository().update(params, inEdgeId, Edge.class) >= 0 && getResourceRepository().update(params, outEdgeId, Edge.class) >= 0);
        }
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
        return unlink();
    }

    @Override
    public String getRecordId() {
        return outEdgeId;
    }

    @Override
    public String getInEdgeId() {
        return inEdgeId;
    }

    @Override
    public String getOutEdgeId() {
        return outEdgeId;
    }

    @Override
    public ResourceRepository getResourceRepository() {
        return new OrientDBRepository();
    }

    @Override
    public boolean link(ResourceNode<? extends Vertex> from, ResourceNode<? extends Vertex> to) {
        if (!from.save() || !to.save()) {
            throw new UnsupportedOperationException("连接的顶点无法保存");
        }
        try {
            inEdgeId = getResourceRepository().saveEdge(this, from, to, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        outEdgeId = inEdgeId;
        return isSave();
    }


    @Override
    public boolean linkUndirected(ResourceNode<? extends Vertex> node1, ResourceNode<? extends Vertex> node2) {
        if (!node1.save() || !node2.save()) {
            throw new UnsupportedOperationException("连接的顶点无法保存");
        }
        try {
            outEdgeId = getResourceRepository().saveEdge(this, node1, node2, false);
            inEdgeId = getResourceRepository().saveEdge(this, node2, node1, false);
        } catch (Exception e) {
            e.printStackTrace();
            unlink();
        }
        return isSave();
    }

    @Override
    public boolean unlink() {
        if (!isSave()) {
            throw new UnsupportedOperationException("关系尚未保存，无法断开");
        }
        if (TransactionManager.doTransaction(session -> isDirected() ? getResourceRepository().deleteEdge(getRecordId()) : getResourceRepository().deleteEdge(inEdgeId) && getResourceRepository().deleteEdge(outEdgeId))) {
            inEdgeId = null;
            outEdgeId = null;
            return true;
        }
        return false;
    }
}
