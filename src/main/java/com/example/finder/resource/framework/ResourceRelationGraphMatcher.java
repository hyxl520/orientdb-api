package com.example.finder.resource.framework;

import com.example.finder.graph.framework.Edge;
import com.example.finder.graph.framework.GraphElement;
import com.example.finder.graph.framework.ResourceMetadataConstant;
import com.example.finder.graph.framework.Vertex;
import com.example.finder.graph.framework.handler.OResultCountHandler;
import com.example.finder.graph.util.ObjectUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-03-01 13:43
 * @email 1158055613@qq.com
 */
public class ResourceRelationGraphMatcher implements GraphEdgeMatcher<ResourceRelation<? extends Edge>> {
    private ResourceNode<? extends Vertex> startNode;
    private final List<RelationType> relations = new LinkedList<>();
    private final Map<String, Object> filterParams = new HashMap<>();
    private static final String filterCommand = "select * from (%s) %s";
    private static final String pagedFilterCommand = "select count(*) as count from (%s) %s";
    private static final String findRelationCommand = ".%s('%s')";
    private static final String conditionCommand = " %s = %s ";
    private static final String matchCommand = " match {class: %s, as: start, where: (@rid = %s)}";


    private static class RelationType {
        Class<? extends Edge> type;
        boolean isDirected;
        RelationDirection direction;

        RelationType(Class<? extends Edge> type, boolean isDirected, RelationDirection direction) {
            this.type = type;
            this.isDirected = isDirected;
            this.direction = direction;
        }
    }


    @Override
    public GraphEdgeMatcher<ResourceRelation<? extends Edge>> asStart(ResourceNode<? extends Vertex> node) {
        startNode = node;
        filterParams.clear();
        relations.clear();
        return this;
    }

    @Override
    public GraphEdgeMatcher<ResourceRelation<? extends Edge>> findDirected(Class<? extends Edge> type, RelationDirection direction) {
        relations.add(new RelationType(type, false, RelationDirection.OUT));
        return this;
    }

    @Override
    public GraphEdgeMatcher<ResourceRelation<? extends Edge>> finUndirected(Class<? extends Edge> type) {
        relations.add(new RelationType(type, false, RelationDirection.OUT));
        return this;
    }

    @Override
    public GraphEdgeMatcher<ResourceRelation<? extends Edge>> filter(Map<String, Object> filterParams) {
        this.filterParams.putAll(filterParams);
        return this;
    }

    @Override
    public GraphEdgeMatcher<ResourceRelation<? extends Edge>> filter(String name, Object value) {
        filterParams.put(name, value);
        return this;
    }

    @Override
    public List<ResourceRelation<? extends Edge>> collect(Class<? extends GraphElement> type) {
        if (startNode == null) {
            throw new NullPointerException("请设置起始节点");
        }
        if (!Edge.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("必须是Edge子类型");
        }
        String command = String.format(filterCommand, matchCommand() + relationCommand() + resultCommand(type), filterCommand());
        return ResourceDao
                .getInstance()
                .executeSQL(command, new OResultResourceRelationListHandler());
    }

    @Override
    public PagedResult<ResourceRelation<? extends Edge>> collect(Class<? extends GraphElement> type, PageConfig config) {
        if (startNode == null) {
            throw new NullPointerException("请设置起始节点");
        }
        if (!Edge.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("必须是Edge子类型");
        }
        String command = String.format(filterCommand, matchCommand() + relationCommand() + resultCommand(type), filterCommand()) + String.format(" skip %d limit %d", (config.getPageNum() - 1) * config.getPageSize(), config.getPageSize());
        String countCommand = String.format(pagedFilterCommand, matchCommand() + relationCommand() + resultCommand(type), filterCommand());
        long total = -1;
        if (config.isGetTotalCount()) {
            total = ResourceDao
                    .getInstance()
                    .executeSQL(countCommand, new OResultCountHandler());
        }
        return new PagedResult<>(ResourceDao
                .getInstance()
                .executeSQL(command, new OResultResourceRelationListHandler()), config, total);
    }

    protected String relationCommand() {
        StringBuilder command = new StringBuilder();
        int i = 0;
        String suffix = "";
        for (RelationType relation : relations) {
            if (++i == relations.size()) {
                suffix = "E";
            }
            command.append(String.format(findRelationCommand, relation.isDirected ? relation.direction.getName() + suffix : RelationDirection.OUT.getName() + suffix, relation.type.getSimpleName()));
        }
        return command.toString();
    }

    protected String resultCommand(Class<? extends GraphElement> type) {
        StringBuilder command = new StringBuilder(" {as: result} return result.@rid as rid, result.")
                .append("`")
                .append(ResourceMetadataConstant.TYPE)
                .append("`")
                .append(" as ")
                .append("`")
                .append(ResourceMetadataConstant.TYPE)
                .append("`, result.")
                .append(RelationDirection.IN.getName())
                .append(" as ")
                .append(RelationDirection.IN.getName())
                .append(", result.")
                .append(RelationDirection.OUT.getName())
                .append(" as ")
                .append(RelationDirection.OUT.getName())
                .append(", result.")
                .append("`")
                .append(ResourceMetadataConstant.DIRECTED)
                .append("`")
                .append(" as ")
                .append("`")
                .append(ResourceMetadataConstant.DIRECTED)
                .append("`, result.")
                .append("`")
                .append(ResourceMetadataConstant.UNDIRECTED)
                .append("`")
                .append(" as ")
                .append("`")
                .append(ResourceMetadataConstant.UNDIRECTED)
                .append("`, result.")
                .append("`@class` as `@class`");
        Field[] fields = ObjectUtil.getClassFields(type);
        if (fields.length > 0) {
            command.append(", ");
        }
        for (Field field : fields) {
            field.setAccessible(true);
            command
                    .append("result.")
                    .append(field.getName())
                    .append(" as ")
                    .append(field.getName())
                    .append(", ");
        }
        if (fields.length > 0) {
            command.deleteCharAt(command.length() - 2);
        }
        return command.toString();
    }

    protected String filterCommand() {
        if (filterParams.size() == 0) {
            return "";
        }
        StringBuilder command = new StringBuilder(" where ");
        int i = 0;
        for (Map.Entry<String, Object> entry : filterParams.entrySet()) {
            command.append(String.format(conditionCommand, entry.getKey(), entry.getValue()));
            if (++i < filterParams.size()) {
                command.append(" and ");
            }
        }
        return command.toString();
    }

    private String matchCommand() {
        return String.format(matchCommand, startNode
                .getSource()
                .getType(), startNode.getRecordId());
    }
}
