package com.example.finder.resource.framework;

import com.example.finder.graph.factory.OrientSessionFactory;
import com.example.finder.graph.framework.*;
import com.example.finder.graph.framework.handler.OResultCountHandler;
import com.example.finder.graph.framework.handler.OResultOEdgeHandler;
import com.example.finder.graph.framework.handler.OResultOVertexListHandler;
import com.example.finder.graph.util.DateUtils;
import com.example.finder.graph.util.StringUtils;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 基于OrientDB的dao层
 *
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-23 9:48
 * @email 1158055613@qq.com
 */
public class ResourceDao extends BaseOrientDBRepository<GraphElement> {

    private ResourceDao() {
        super(OrientSessionFactory.getInstance());
    }

    public static ResourceDao getInstance() {
        return InstanceHolder.DAO;
    }

    public <R> R queryByMap(Map<String, Object> params, OResultHandler<R> resultHandler, Class<? extends GraphElement> type) {
        StringBuilder sql = new StringBuilder("select * from ")
                .append(type == Vertex.class ? "V" : "E")
                .append(" where ");
        int i = 0;
        Object[] paramArray = new Object[params.size()];
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                sql.append(String.format(" %s = ? ", entry.getKey()));
                paramArray[i++] = entry.getValue();
                if (i < params.size()) {
                    sql.append(" and ");
                }
            }
        }
        return executeSQL(sql.toString(), resultHandler, paramArray);
    }

    public <R> PagedResult<R> queryByMap(Map<String, Object> params, OResultHandler<List<R>> resultHandler, Class<? extends GraphElement> type, PageConfig config) {
        StringBuilder sql = new StringBuilder("select * from ")
                .append(type == Vertex.class ? "V" : "E")
                .append(" where ");
        StringBuilder countSql = new StringBuilder("select count(*) as count from ")
                .append(type == Vertex.class ? "V" : "E")
                .append(" where ");
        int i = 0;
        Object[] paramArray = new Object[params.size()];
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                sql.append(String.format(" %s = ? ", entry.getKey()));
                countSql.append(String.format(" %s = ? ", entry.getKey()));
                paramArray[i++] = entry.getValue();
                if (i < params.size()) {
                    sql.append(" and ");
                    countSql.append(" and ");
                }
            }
        }
        sql.append(String.format(" skip %d limit %d", (config.getPageNum() - 1) * config.getPageSize(), config.getPageSize()));
        List<R> result = executeSQL(sql.toString(), resultHandler, paramArray);
        long total = -1;
        if (config.isGetTotalCount()) {
            total = executeSQL(countSql.toString(), new OResultCountHandler(), paramArray);
        }
        return new PagedResult<>(result, config, total);
    }

    public boolean deleteEdge(String fromNodeId, String toNodeId, String edgeType) {
        return delete(String.format("@rid in (select @rid from E where in = %s and out = %s and @class = '%s')", toNodeId, fromNodeId, edgeType), false) >= 0;
    }

    public <O extends GraphElement> boolean updateByRid(O param, String rid) throws Exception {
        return update(" @rid = ?", param, rid) >= 0;
    }

    public int updateByMap(Map<String, Object> params, String rid, Class<? extends GraphElement> type) {
        if (params == null || params.size() == 0 || StringUtils.isEmpty(rid)) {
            return -1;
        }
        OResultSet resultSet = null;
        ODatabaseSession session = getSession();
        try {
            StringBuilder sql = new StringBuilder();
            sql
                    .append("update ")
                    .append(type == Vertex.class ? "V" : "E")
                    .append(" set ");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    sql
                            .append(entry.getKey())
                            .append(" = ");
                    if (value instanceof String && !"@rid".equals(entry.getKey())) {
                        sql
                                .append("\"")
                                .append(value)
                                .append("\"");
                    } else if (value instanceof Date) {
                        sql
                                .append("\"")
                                .append(DateUtils.formatDateTime((Date) value))
                                .append("\"");
                    } else {
                        sql.append(value);
                    }
                    sql.append(",");
                }

            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(" where @rid = ?");
            resultSet = session.command(sql.toString(), rid);
            return printExecuteLogs(sql.toString(), getUpdateAffectedRows(resultSet), rid);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSession(session);
        }
        return -1;

    }

    public OEdge queryEdgeByDirection(String out, String in, String edgeType) {
        return executeSQL("select * from E where in = ? and out = ? and @class = ?", new OResultOEdgeHandler(), in, out, edgeType);
    }

    @SafeVarargs
    public final List<OVertex> shortestPath(String startNodeId, String endNodeId, RelationDirection direction, int depth, Class<? extends Edge>... edgeTypes) {
        if (StringUtils.isEmpty(startNodeId) || StringUtils.isEmpty(endNodeId)) {
            return Collections.emptyList();
        }
        String commandPattern = "select * from V where @rid in (SELECT shortestPath(%s, %s, '%s',%s,{\"maxDepth\": %d}))";
        depth = depth <= 0 ? Integer.MAX_VALUE : depth;
        direction = direction == null ? RelationDirection.OUT : direction;
        StringBuilder builder = null;
        if (edgeTypes != null && edgeTypes.length > 0) {
            builder = new StringBuilder();
            for (Class<? extends Edge> type : edgeTypes) {
                builder
                        .append("'")
                        .append(type.getSimpleName())
                        .append("'")
                        .append(", ");
            }
            builder.deleteCharAt(builder.length() - 2);
        }
        String command = String.format(commandPattern, startNodeId, endNodeId, direction.getName(), builder == null ? "null" : "[" + builder.toString() + "]", depth);
        return executeSQL(command, new OResultOVertexListHandler());
    }

    static class InstanceHolder {
        private static final ResourceDao DAO = new ResourceDao();
    }


}
