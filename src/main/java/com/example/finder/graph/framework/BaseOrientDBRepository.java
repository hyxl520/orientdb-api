package com.example.finder.graph.framework;

import com.example.finder.graph.factory.OrientSessionFactory;
import com.example.finder.graph.framework.handler.OResultBeanHandler;
import com.example.finder.graph.framework.handler.OResultBeanListHandler;
import com.example.finder.graph.framework.handler.OResultEdgeHandler;
import com.example.finder.graph.framework.handler.OResultEdgeListHandler;
import com.example.finder.graph.query.EdgeQueryResult;
import com.example.finder.graph.util.DateUtils;
import com.example.finder.graph.util.ObjectUtil;
import com.example.finder.graph.util.StringUtils;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Base repository
 *
 * @Author Huang Yongxiang
 * @Date 2022/08/31 10:16
 */
@Slf4j
public abstract class BaseOrientDBRepository<T extends GraphElement> {

    private final OrientSessionFactory orientSessionFactory;

    public BaseOrientDBRepository(OrientSessionFactory orientSessionFactory) {
        this.orientSessionFactory = orientSessionFactory;
    }

    public <R> R executeSQL(String sql, OResultHandler<R> resultHandler, Object... params) {
        ODatabaseSession session = getSession();
        try {
            OResultSet resultSet = session.command(sql, params);
            return printExecuteLogs(sql, resultHandler.doHandle(resultSet), params);
        } finally {
            closeSession(session);
        }
    }

    /**
     * 查询一个条目，查询的结果将会转成对应的实体对象
     *
     * @param sql    执行的幂等查询语句
     * @param params 参数
     * @return T
     * @author Huang Yongxiang
     * @date 2022/9/2 9:52
     */
    public T queryOne(String sql, Class<T> type, Object... params) {
        return query(sql, new OResultBeanHandler<>(type), params);
    }

    public <E> E queryByRid(String rid, OResultHandler<E> resultBeanHandler) {
        return query("select * from " + rid, resultBeanHandler);
    }

    public <R> R query(String sql, OResultHandler<R> resultHandler, Object... params) {
        if (StringUtils.isEmpty(sql)) {
            throw new NullPointerException();
        }

        try (ODatabaseSession session = getQuerySession()) {
            OResultSet resultSet = session.query(sql, params);
            return printExecuteLogs(sql, resultHandler.doHandle(resultSet), params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询多个条目，查询的结果将会转化成实体对象
     *
     * @param sql    执行的幂等查询语句
     * @param params 参数
     * @return java.util.List<T>
     * @author Huang Yongxiang
     * @date 2022/9/2 9:55
     */
    public List<T> queryList(String sql, Class<T> type, Object... params) {
        return query(sql, new OResultBeanListHandler<>(type), params);
    }


    /**
     * 执行分页查询，方法将会自动在sql语句后面加上 skip-limit语句实现分页
     *
     * @param sql    sql语句
     * @param page   页
     * @param size   每页条目数
     * @param params 参数
     * @return java.util.List<T>
     * @author Huang Yongxiang
     * @date 2022/9/2 10:26
     */
    public List<T> queryListPaged(String sql, int page, int size, Class<T> type, Object... params) {
        if (page <= 0 || size < 0) {
            throw new IllegalArgumentException();
        }
        int skip = (page - 1) * size;
        String pagedSql = String.format("%s skip %d limit %s", sql, skip, size);
        return queryList(pagedSql, type, params);
    }

    /**
     * 查询一条边，该方法返回对边实例，起止顶点的封装
     *
     * @param sql      执行的幂等sql语句
     * @param type     边类型
     * @param fromType 边起始顶点类型
     * @param toType   边终止顶点类型
     * @param params   参数
     * @return com.sccl.orientdb.graph.query.EdgeQueryResult<T1>
     * @author Huang Yongxiang
     * @date 2022/9/2 10:22
     */
    public <T1 extends Edge, F extends Vertex, E extends Vertex> EdgeQueryResult<T1> queryOneAsEdgeResult(String sql, Class<T1> type, Class<F> fromType, Class<E> toType, Object... params) {
        if (StringUtils.isEmpty(sql)) {
            throw new NullPointerException();
        }
        try (ODatabaseSession session = getQuerySession()) {
            OResultSet resultSet = session.query(sql, params);
            OResultEdgeHandler<T1, F, E> handler = new OResultEdgeHandler<>(type, fromType, toType);
            return printExecuteLogs(sql, handler.doHandle(resultSet), params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T1 extends Edge, F extends Vertex, E extends Vertex> List<EdgeQueryResult<T1>> queryListAsEdgeResult(String sql, Class<T1> type, Class<F> fromType, Class<E> toType, Object... params) {
        if (StringUtils.isEmpty(sql)) {
            throw new NullPointerException();
        }
        try (ODatabaseSession session = getQuerySession()) {
            OResultSet resultSet = session.query(sql, params);
            OResultEdgeListHandler<T1, F, E> handler = new OResultEdgeListHandler<>(type, fromType, toType);
            return printExecuteLogs(sql, handler.doHandle(resultSet), params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 执行更新操作
     *
     * @param condition 更新语句where后的部分
     * @param entity    存在值的实体属性将会被对应更新
     * @param params    参数
     * @return int
     * @author Huang Yongxiang
     * @date 2022/10/8 9:19
     */
    public int update(String condition, T entity, Object... params) throws Exception {
        if (StringUtils.isEmpty(condition) || entity == null) {
            return 0;
        }
        OResultSet resultSet = null;
        ODatabaseSession session = getSession();
        try {
            StringBuilder sql = new StringBuilder("update ")
                    .append(entity
                            .getClass()
                            .getSimpleName())
                    .append(" set ");
            Field[] fields = entity
                    .getClass()
                    .getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(entity);
                if (value != null) {
                    sql
                            .append(field.getName())
                            .append(" = ");
                    if (value instanceof String && !"@rid".equals(field.getName())) {
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
            sql
                    .append(" where ")
                    .append(condition);
            resultSet = session.command(sql.toString(), params);
            return printExecuteLogs(sql.toString(), getUpdateAffectedRows(resultSet), params);
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            closeSession(session);
        }
    }

    public int delete(String condition, boolean isVertex, Object... params) {
        if (StringUtils.isEmpty(condition) || "1=1".equals(condition.replace(" ", ""))) {
            throw new IllegalArgumentException("为避免全表删除，删除必须指定有效条件");
        }
        StringBuilder sql = new StringBuilder("delete ");
        if (!isVertex) {
            sql.append("edge ");
        } else {
            sql.append("vertex from V ");
        }
        if (!StringUtils.isEmpty(condition)) {
            sql
                    .append(" where ")
                    .append(condition);
        }
        ODatabaseSession session = getSession();
        try {
            try (OResultSet resultSet = session.command(sql.toString(), params)) {
                return printExecuteLogs(sql.toString(), getUpdateAffectedRows(resultSet), params);
            }
        } finally {
            closeSession(session);
        }
    }

    public int deleteVertexByRid(String rid) {
        String sql = "delete Vertex from V where @rid = ?";
        ODatabaseSession session = getSession();
        try {
            try (OResultSet resultSet = session.command(sql, rid)) {
                return printExecuteLogs(sql, getUpdateAffectedRows(resultSet), rid);
            }
        } finally {
            closeSession(session);
        }
    }

    public int deleteEdgeByRid(String rid) {
        String sql = "delete Edge where @rid = ?";
        ODatabaseSession session = getSession();
        try {
            try (OResultSet resultSet = session.command(sql, rid)) {
                return printExecuteLogs(sql, getUpdateAffectedRows(resultSet), rid);
            }
        } finally {
            closeSession(session);
        }
    }


    /**
     * 在图库内创建一个顶点实例
     *
     * @param entity 顶点实体对象，必须实现Vertex接口
     * @return com.orientechnologies.orient.core.record.OVertex
     * @author Huang Yongxiang
     * @date 2022/8/31 10:56
     * @see Vertex
     */
    protected OVertex createNewVertex(Vertex entity) throws Exception {
        ODatabaseSession session = getSession();
        try {
            //if (session.getClass(entity.getType()) == null) {
            //    orientSessionFactory.getSession().createVertexClass(entity.getType());
            //}
            OVertex vertex = session.newVertex(entity.getType());
            Field[] fields = ObjectUtil.getObjectFields(entity);
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(entity);
                if (value instanceof Date) {
                    vertex.setProperty(field.getName(), DateUtils.formatDateTime((Date) value));
                } else {
                    vertex.setProperty(field.getName(), value);
                }
            }
            vertex.setProperty(ResourceMetadataConstant.TYPE, entity
                    .getClass()
                    .getName());
            vertex.save();
            return vertex;
        } finally {
            closeSession(session);
        }

    }

    public List<OVertex> createNewVertices(List<T> entities) throws Exception {
        List<OVertex> result = new ArrayList<>();
        for (T entity : entities) {
            result.add(createAsVertex(entity));
        }
        return result;
    }

    public OVertex createAsVertex(T entity) throws Exception {
        if (entity instanceof Vertex) {
            return createNewVertex((Vertex) entity);
        } else {
            throw new UnsupportedOperationException("只有顶点实体才能被创建");
        }
    }

    public List<OEdge> createNewEdges(OVertex from, Map<T, OVertex> toMap, boolean isDirected) throws Exception {
        List<OEdge> result = new ArrayList<>();
        for (Map.Entry<T, OVertex> entity : toMap.entrySet()) {
            result.add(createAsEdge(entity.getKey(), from, entity.getValue(), isDirected));
        }
        return result;
    }

    protected OEdge createNewEdge(Edge entity, OVertex from, OVertex to, boolean isDirected) throws Exception {
        ODatabaseSession session = getSession();
        try {
            //if (session.getClass(entity.getType()) == null) {
            //    orientSessionFactory.getSession().createEdgeClass(entity.getType());
            //}
            OEdge edge = session.newEdge(from, to, entity.getType());
            Field[] fields = ObjectUtil.getObjectFields(entity);
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(entity);
                if (value instanceof Date) {
                    edge.setProperty(field.getName(), DateUtils.formatDateTime((Date) value));
                } else {
                    edge.setProperty(field.getName(), value);
                }
            }
            edge.setProperty(ResourceMetadataConstant.TYPE, entity
                    .getClass()
                    .getName());
            edge.setProperty(isDirected ? ResourceMetadataConstant.DIRECTED : ResourceMetadataConstant.UNDIRECTED, true);
            edge.save();
            return edge;
        } finally {
            closeSession(session);
        }
    }

    protected static <T> T printExecuteLogs(String sql, T result, Object... params) {
        if (StringUtils.isEmpty(sql)) {
            log.error("empty Sql");
        } else {
            log.debug("Sql ===============> {}", sql);
            if (params != null) {
                StringBuilder stringBuilder = new StringBuilder();
                Arrays
                        .stream(params)
                        .forEach(param -> {
                            if (param == null) {
                                stringBuilder.append("null");
                            } else {
                                stringBuilder
                                        .append(param)
                                        .append("(")
                                        .append(param
                                                .getClass()
                                                .getSimpleName())
                                        .append("),");
                            }
                        });
                log.debug("Params ===============> {}", stringBuilder.length() > 0 ? stringBuilder
                        .deleteCharAt(stringBuilder.length() - 1)
                        .toString() : "");
                if (result instanceof Collection) {
                    log.debug("Total ===============> {}", ((Collection<?>) result).size());
                } else {
                    log.debug("Result ===============> {}", result);
                }
            }

        }
        return result;
    }

    protected static int getUpdateAffectedRows(OResultSet resultSet) {
        if (resultSet != null && resultSet.hasNext()) {
            OResult result = resultSet.next();
            if (result.hasProperty("count")) {
                Long count = result.getProperty("count");
                if (count != null) {
                    return count.intValue();
                }
            }
        }
        return -1;
    }

    /**
     * 在图库创建一条边实例
     *
     * @param entity 边实体对象，必须实现Edge接口
     * @param from   边起始顶点
     * @param to     边终止顶点
     * @return com.orientechnologies.orient.core.record.OEdge
     * @author Huang Yongxiang
     * @date 2022/8/31 10:58
     */
    public OEdge createAsEdge(T entity, OVertex from, OVertex to, boolean isDirected) throws Exception {
        if (entity instanceof Edge) {
            return createNewEdge((Edge) entity, from, to, isDirected);
        } else {
            throw new UnsupportedOperationException("只有边实体才能被创建");
        }
    }


    /**
     * 执行事务，该方法使用默认的乐观锁机制
     *
     * @param transactionEntries 事务条目，事务条目内不能关闭或者提交会话，并且不能catch异常，否则很有可能不会回滚事务
     * @param rollbackExceptions 要回滚的异常类型，不指定时所有异常均会回滚
     * @return boolean 事务是否执行成功
     * @author Huang Yongxiang
     * @date 2022/8/31 15:36
     */
    @SafeVarargs
    public final boolean doTransactions(TransactionEntry[] transactionEntries, Class<? extends Exception>... rollbackExceptions) {
        if (transactionEntries == null || transactionEntries.length == 0) {
            return false;
        }
        TransactionManager.openTransaction();
        ODatabaseSession session = TransactionManager.getCurrentSession();
        try {
            for (TransactionEntry entry : transactionEntries) {
                if (entry == null) {
                    continue;
                }
                entry.operate(session);
            }
            TransactionManager.closeTransaction();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (rollbackExceptions != null && rollbackExceptions.length > 0) {
                for (Class<? extends Exception> ex : rollbackExceptions) {
                    if (e.getClass() == ex) {
                        TransactionManager.rollbackTransaction();
                        break;
                    }
                }
            } else {
                TransactionManager.rollbackTransaction();
            }
        }
        return false;
    }


    /**
     * 获取一个会话实例
     *
     * @return com.orientechnologies.orient.core.db.ODatabaseSession
     * @author Huang Yongxiang
     * @date 2022/10/8 15:54
     */
    public ODatabaseSession getSession() {
        if (TransactionManager.isOpenTransaction()) {
            //System.out.println("获取事务会话");
            return TransactionManager.getCurrentSession();
        }
        //System.out.println("获取池会话");
        return orientSessionFactory.getSession();
    }

    public ODatabaseSession getQuerySession() {
        //System.out.println("获取查询会话");
        return orientSessionFactory.getSession();
    }

    /**
     * 对会话进行关闭，如果当前线程开启了事务，则该方法不会对会话进行关闭，该方法将会抑制关闭时将会发生的异常
     *
     * @param session 要关闭的会话
     * @return void
     * @author Huang Yongxiang
     * @date 2022/10/8 15:36
     */
    public void closeSession(ODatabaseSession session) {
        if (TransactionManager.isOpenTransaction()) {
            return;
        }
        try {
            //System.out.println("关闭会话");
            session.close();
        } catch (Exception ignored) {
        }
    }
}
