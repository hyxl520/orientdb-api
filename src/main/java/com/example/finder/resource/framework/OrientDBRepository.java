package com.example.finder.resource.framework;

import com.example.finder.graph.framework.*;
import com.example.finder.graph.framework.handler.*;
import com.example.finder.graph.util.ObjectUtil;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * OrientDB的存储库
 *
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-23 9:46
 * @email 1158055613@qq.com
 */
public class OrientDBRepository implements ResourceRepository {

    @Override
    public <V extends Vertex> String saveNode(ResourceNode<V> node) throws Exception {
        ResourceDao dao = ResourceDao.getInstance();
        OVertex vertex = null;
        vertex = dao.createAsVertex(node.getSource());
        return vertex == null ? null : vertex
                .getProperty("@rid")
                .toString();
    }

    @Override
    public <E extends Edge, V1 extends Vertex, V2 extends Vertex> List<ResourceRelation<? extends Edge>> saveEdges(ResourceNode<V1> from, Map<E, ResourceNode<V2>> toMap, boolean isDirected) {
        //批量保存节点
        ResourceDao dao = ResourceDao.getInstance();
        Map<E, OVertex> vertexMap = new HashMap<>();
        AtomicReference<OVertex> fromNode = new AtomicReference<>();
        TransactionManager.doTransaction(session -> {
            if (from.isSave()) {
                fromNode.set(ResourceDao
                        .getInstance()
                        .queryByRid(from.getRecordId(), new OResultOVertexHandler()));
            } else {
                fromNode.set(dao.createAsVertex(from.getSource()));
            }
            for (Map.Entry<E, ResourceNode<V2>> entity : toMap.entrySet()) {
                vertexMap.put(entity.getKey(), dao.createAsVertex(entity
                        .getValue()
                        .getSource()));
            }
            return true;
        });
        for (Map.Entry<E, ResourceNode<V2>> entity : toMap.entrySet()) {
            OVertex vertex = vertexMap.get(entity.getKey());
            if (vertex != null) {
                entity
                        .getValue()
                        .setRecordId(vertex
                                .getProperty("@rid")
                                .toString());
            }
        }
        //批量生成边
        List<ResourceRelation<? extends Edge>> edges = new ArrayList<>();
        Map<E, List<OEdge>> edgeMap = new HashMap<>();
        TransactionManager.doTransaction(session -> {
            for (Map.Entry<E, OVertex> entity : vertexMap.entrySet()) {
                OEdge edge = dao.createAsEdge(entity.getKey(), fromNode.get(), entity.getValue(), isDirected);
                OEdge edge1 = dao.createAsEdge(entity.getKey(), entity.getValue(), fromNode.get(), isDirected);
                if (edge != null) {
                    edgeMap.put(entity.getKey(), Arrays.asList(edge, edge1));
                } else {
                    throw new Exception("批量生成边失败");
                }
            }
            return true;
        });
        from.setRecordId(fromNode
                .get()
                .getProperty("@rid")
                .toString());
        return edgeMap
                .entrySet()
                .stream()
                .map(entry -> new GraphResourceRelation<>(entry.getKey(), entry
                        .getValue()
                        .get(0)
                        .getProperty("@rid")
                        .toString(), entry
                        .getValue()
                        .get(1)
                        .getProperty("@rid")
                        .toString()))
                .collect(Collectors.toList());
    }


    @Override
    public <E extends Edge, V1 extends Vertex, V2 extends Vertex> String saveEdge(ResourceRelation<E> edge, ResourceNode<V1> from, ResourceNode<V2> to, boolean isDirected) throws Exception {
        ResourceDao dao = ResourceDao.getInstance();
        OVertex fromNode = dao.queryByRid(from.getRecordId(), new OResultOVertexHandler());
        OVertex toNode = dao.queryByRid(to.getRecordId(), new OResultOVertexHandler());
        if (fromNode == null || toNode == null) {
            return null;
        }
        OEdge oEdge = dao.createAsEdge(edge.getSource(), fromNode, toNode, isDirected);
        return oEdge == null ? null : oEdge
                .getProperty("@rid")
                .toString();
    }

    @Override
    public ResourceNode<? extends Vertex> queryNode(String rid) {
        Map<String, Object> params = new HashMap<>();
        params.put("@rid", rid);
        return queryNode(params);
    }

    @Override
    public ResourceNode<? extends Vertex> queryNode(Map<String, Object> params) {
        ResourceDao dao = ResourceDao.getInstance();
        OVertex vertex = dao.queryByMap(params, new OResultOVertexHandler(), Vertex.class);
        if (vertex == null) {
            return null;
        }
        try (ODatabaseSession session = dao.getSession()) {
            return oVertex2ResourceNode(vertex);
        }
    }

    @Override
    public List<ResourceNode<? extends Vertex>> queryNodes(Map<String, Object> params) {
        ResourceDao dao = ResourceDao.getInstance();
        List<OVertex> vertices = dao.queryByMap(params, new OResultOVertexListHandler(), Vertex.class);
        if (vertices == null || vertices.size() == 0) {
            return Collections.emptyList();
        }
        List<ResourceNode<? extends Vertex>> results = new LinkedList<>();
        for (OVertex vertex : vertices) {
            ResourceNode<? extends Vertex> node = oVertex2ResourceNode(vertex);
            if (node != null) {
                results.add(node);
            }
        }
        return results;
    }

    @Override
    public PagedResult<ResourceNode<? extends Vertex>> queryNodes(Map<String, Object> params, PageConfig config) {
        ResourceDao dao = ResourceDao.getInstance();
        return dao.queryByMap(params, new OResultResourceNodeListHandler(), Vertex.class, config);
    }

    @Override
    public PagedResult<ResourceNode<? extends Vertex>> queryNodes(Class<? extends Vertex> type, PageConfig pageConfig) {
        String sqlPattern = "select * from `%s` skip %d limit %d";
        String countSqlPattern = "select count(*) from `%s`";
        int skip = (pageConfig.getPageNum() - 1) * pageConfig.getPageSize();
        List<ResourceNode<? extends Vertex>> result = ResourceDao
                .getInstance()
                .executeSQL(String.format(sqlPattern, type.getSimpleName(), skip, pageConfig.getPageSize()), new OResultResourceNodeListHandler());
        long total = -1;
        if (pageConfig.isGetTotalCount()) {
            total = ResourceDao
                    .getInstance()
                    .executeSQL(String.format(countSqlPattern, type.getSimpleName()), new OResultCountHandler());
        }
        return new PagedResult<>(result, pageConfig, total);
    }

    @Override
    public ResourceRelation<? extends Edge> queryEdge(String rid) {
        Map<String, Object> params = new HashMap<>();
        params.put("@rid", rid);
        return queryEdge(params);
    }

    @Override
    public ResourceRelation<? extends Edge> queryEdge(Map<String, Object> params) {
        ResourceDao dao = ResourceDao.getInstance();
        OEdge edge = dao.queryByMap(params, new OResultOEdgeHandler(), Edge.class);
        if (edge == null) {
            return null;
        }
        OEdge other = null;
        //如果是无向边
        if (edge.getProperty(ResourceMetadataConstant.UNDIRECTED) != null) {
            other = dao.queryEdgeByDirection(edge
                    .getProperty("in")
                    .toString(), edge
                    .getProperty("out")
                    .toString(), edge
                    .getProperty("@class")
                    .toString());

        }
        try (ODatabaseSession session = dao.getSession()) {
            return oEdge2ResourceRelation(edge, other);
        }
    }

    @Override
    public List<ResourceRelation<? extends Edge>> queryEdges(Map<String, Object> params) {
        ResourceDao dao = ResourceDao.getInstance();
        List<OEdge> edges = dao.queryByMap(params, new OResultOEdgeListHandler(), Edge.class);
        if (edges == null || edges.size() == 0) {
            return Collections.emptyList();
        }
        List<ResourceRelation<? extends Edge>> results = new LinkedList<>();
        for (OEdge edge : edges) {
            OEdge other = null;
            if (edge.getProperty(ResourceMetadataConstant.UNDIRECTED) != null) {
                other = dao.queryEdgeByDirection(edge
                        .getProperty("in")
                        .toString(), edge
                        .getProperty("out")
                        .toString(), edge
                        .getProperty("@class")
                        .toString());

            }
            ResourceRelation<? extends Edge> node = oEdge2ResourceRelation(edge, other);
            if (node != null) {
                results.add(node);
            }
        }
        return results;
    }

    @Override
    public PagedResult<ResourceRelation<? extends Edge>> queryEdges(Map<String, Object> params, PageConfig config) {
        ResourceDao dao = ResourceDao.getInstance();
        return dao.queryByMap(params, new OResultResourceRelationListHandler(), Edge.class, config);
    }

    @SafeVarargs
    @Override
    public final List<ResourceNode<? extends Vertex>> shortestPath(ResourceNode<? extends Vertex> startNode, ResourceNode<? extends Vertex> endNode, RelationDirection direction, int depth, Class<? extends Edge>... types) {
        ResourceDao dao = ResourceDao.getInstance();
        List<OVertex> vertices = dao.shortestPath(startNode.getRecordId(), endNode.getRecordId(), direction, depth, types);
        if (vertices == null || vertices.size() == 0) {
            return Collections.emptyList();
        }
        return vertices
                .stream()
                .map(OrientDBRepository::oVertex2ResourceNode)
                .collect(Collectors.toList());
    }

    @Override
    public PagedResult<ResourceNode<? extends Vertex>> traverse(ResourceNode<? extends Vertex> startNode, Class<? extends Vertex> findType, Map<String, Object> filterParams, int depth, TraverseStrategy strategy, PageConfig config) {
        if (findType == null) {
            throw new NullPointerException("查找类型不得为空");
        }
        String commandPattern = "select * from (traverse * from %s MAXDEPTH %d STRATEGY %s) where @class = \"%s\"";
        String countSqlPattern = "select count(*) as count from (traverse * from %s MAXDEPTH %d STRATEGY %s) where @class = \"%s\"";
        StringBuilder filter = new StringBuilder("");
        int i = 0;
        boolean withFilter = false;
        Object[] paramArray = null;
        if (filterParams != null && filterParams.size() > 0) {
            paramArray = new Object[filterParams.size()];
            for (Map.Entry<String, Object> entry : filterParams.entrySet()) {
                if (entry.getValue() != null) {
                    filter.append(String.format(" %s = ? ", entry.getKey()));
                    paramArray[i++] = entry.getValue();
                    if (i < filterParams.size()) {
                        filter.append(" and ");
                    }
                }
            }
            withFilter = true;
        }
        String filterCommand = withFilter ? " and " + filter.toString() : "";
        String command = String.format(commandPattern, startNode.getRecordId(), depth, strategy.getName(), findType.getSimpleName()) + filterCommand + String.format(" skip %d " + "limit %d", (config.getPageNum() - 1) * config.getPageSize(), config.getPageSize());
        String countCommand = String.format(countSqlPattern, startNode.getRecordId(), depth, strategy.getName(), findType.getSimpleName()) + filterCommand;
        List<ResourceNode<? extends Vertex>> result = ResourceDao
                .getInstance()
                .executeSQL(command, new OResultResourceNodeListHandler(), paramArray);
        long total = -1;
        if (config.isGetTotalCount()) {
            total = ResourceDao
                    .getInstance()
                    .executeSQL(countCommand, new OResultCountHandler(), paramArray);
        }
        return new PagedResult<>(result, config, total);
    }

    @SafeVarargs
    @Override
    public final PagedResult<ResourceNode<? extends Vertex>> traverse(ResourceNode<? extends Vertex> startNode, Map<String, Object> filterParams, int depth, TraverseStrategy strategy, PageConfig config, Class<? extends Vertex>... findTypes) {
        if (findTypes == null || findTypes.length == 0) {
            throw new IllegalArgumentException("查找类型不得为空");
        }
        String commandPattern = "select * from (traverse * from %s MAXDEPTH %d STRATEGY %s) where (%s)";
        String countSqlPattern = "select count(*) as count from (traverse * from %s MAXDEPTH %d STRATEGY %s) where " + "(%s)";
        StringBuilder filter = new StringBuilder("");
        int i = 0;
        boolean withFilter = false;
        Object[] paramArray = null;
        if (filterParams != null && filterParams.size() > 0) {
            paramArray = new Object[filterParams.size()];
            for (Map.Entry<String, Object> entry : filterParams.entrySet()) {
                if (entry.getValue() != null) {
                    filter.append(String.format(" %s = ? ", entry.getKey()));
                    paramArray[i++] = entry.getValue();
                    if (i < filterParams.size()) {
                        filter.append(" and ");
                    }
                }
            }
            withFilter = true;
        }

        StringBuilder findTypeCommand = new StringBuilder();
        int j = 0;
        for (Class<? extends Vertex> clazz : findTypes) {
            findTypeCommand
                    .append("@class = \"")
                    .append(clazz.getSimpleName())
                    .append("\"");
            if (++j < findTypes.length) {
                findTypeCommand.append(" or ");
            }
        }

        String filterCommand = withFilter ? " and " + filter.toString() : "";
        String command = String.format(commandPattern, startNode.getRecordId(), depth, strategy.getName(), findTypeCommand.toString()) + filterCommand + String.format(" skip %d " + "limit %d", (config.getPageNum() - 1) * config.getPageSize(), config.getPageSize());
        String countCommand = String.format(countSqlPattern, startNode.getRecordId(), depth, strategy.getName(), findTypeCommand.toString()) + filterCommand;
        List<ResourceNode<? extends Vertex>> result = ResourceDao
                .getInstance()
                .executeSQL(command, new OResultResourceNodeListHandler(), paramArray);
        long total = -1;
        if (config.isGetTotalCount()) {
            total = ResourceDao
                    .getInstance()
                    .executeSQL(countCommand, new OResultCountHandler(), paramArray);
        }
        return new PagedResult<>(result, config, total);
    }

    @Override
    public boolean deleteVertex(String rid) {
        ResourceDao dao = ResourceDao.getInstance();
        return dao.deleteVertexByRid(rid) >= 0;
    }

    @Override
    public boolean deleteEdge(String rid) throws Exception {
        ResourceDao dao = ResourceDao.getInstance();
        return dao.deleteEdgeByRid(rid) >= 0;
    }

    @Override
    public boolean deleteEdge(String fromNodeId, String toNodeId, String edgeType) {
        ResourceDao dao = ResourceDao.getInstance();
        return dao.deleteEdge(fromNodeId, toNodeId, edgeType);
    }

    @Override
    public <T extends GraphElement> boolean update(T param, String rid) throws Exception {
        ResourceDao dao = ResourceDao.getInstance();
        return dao.updateByRid(param, rid);
    }

    @Override
    public int update(Map<String, Object> params, String rid, Class<? extends GraphElement> type) {
        ResourceDao dao = ResourceDao.getInstance();
        return dao.updateByMap(params, rid, type);
    }

    @SuppressWarnings("unchecked")
    protected static ResourceNode<? extends Vertex> oVertex2ResourceNode(OVertex vertex) {
        if (vertex == null) {
            return null;
        }
        String typeString = vertex
                .getProperty(ResourceMetadataConstant.TYPE)
                .toString();
        Class<? extends Vertex> type = (Class<? extends Vertex>) ObjectUtil.classPath2Class(typeString);
        return new GraphResourceNode<>(GraphElementConvertor.vertex2Entity(vertex, type), vertex
                .getProperty("@rid")
                .toString());
    }

    @SuppressWarnings("unchecked")
    protected static ResourceRelation<? extends Edge> oEdge2ResourceRelation(OEdge edge1, OEdge edge2) {
        if (edge1 != null && edge2 != null) {
            String typeString = edge1
                    .getProperty(ResourceMetadataConstant.TYPE)
                    .toString();
            Class<? extends Edge> type = (Class<? extends Edge>) ObjectUtil.classPath2Class(typeString);
            return new GraphResourceRelation<>(GraphElementConvertor.edge2Entity(edge1, type), edge1
                    .getProperty("@rid")
                    .toString(), edge2
                    .getProperty("@rid")
                    .toString());
        } else if (edge1 != null || edge2 != null) {
            OEdge edge = edge1 == null ? edge2 : edge1;
            String typeString = edge
                    .getProperty(ResourceMetadataConstant.TYPE)
                    .toString();
            Class<? extends Edge> type = (Class<? extends Edge>) ObjectUtil.classPath2Class(typeString);
            return new GraphResourceRelation<>(GraphElementConvertor.edge2Entity(edge, type), edge
                    .getProperty("@rid")
                    .toString());
        }
        return null;
    }
}
