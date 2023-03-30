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
 * 图节点匹配器，用于图节点遍历
 *
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-27 15:16
 * @email 1158055613@qq.com
 */
public class ResourceNodeGraphMatcher implements GraphNodeMatcher<ResourceNode<? extends Vertex>> {
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
    public GraphNodeMatcher<ResourceNode<? extends Vertex>> asStart(ResourceNode<? extends Vertex> node) {
        startNode = node;
        filterParams.clear();
        relations.clear();
        return this;
    }

    @Override
    public GraphNodeMatcher<ResourceNode<? extends Vertex>> findDirected(Class<? extends Edge> type, RelationDirection direction) {
        relations.add(new RelationType(type, true, direction));
        return this;
    }

    @Override
    public GraphNodeMatcher<ResourceNode<? extends Vertex>> findUndirected(Class<? extends Edge> type) {
        relations.add(new RelationType(type, false, RelationDirection.OUT));
        return this;
    }


    @Override
    public GraphNodeMatcher<ResourceNode<? extends Vertex>> filter(Map<String, Object> filterParams) {
        this.filterParams.putAll(filterParams);
        return this;
    }

    @Override
    public GraphNodeMatcher<ResourceNode<? extends Vertex>> filter(String name, Object value) {
        filterParams.put(name, value);
        return this;
    }

    @Override
    public List<ResourceNode<? extends Vertex>> collect(Class<? extends GraphElement> type) {
        if (startNode == null) {
            throw new NullPointerException("请设置起始节点");
        }
        if (!Vertex.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("必须是Vertex子类型");
        }
        String command = String.format(filterCommand, matchCommand() + relationCommand() + resultCommand(type), filterCommand());
        return ResourceDao
                .getInstance()
                .executeSQL(command, new OResultResourceNodeListHandler());
    }

    @Override
    public PagedResult<ResourceNode<? extends Vertex>> collect(Class<? extends GraphElement> type, PageConfig config) {
        if (startNode == null) {
            throw new NullPointerException("请设置起始节点");
        }
        if (!Vertex.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("必须是Vertex子类型");
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
                .executeSQL(command, new OResultResourceNodeListHandler()), config, total);
    }

    protected String relationCommand() {
        StringBuilder command = new StringBuilder();
        for (RelationType relation : relations) {
            command.append(String.format(findRelationCommand, relation.isDirected ? relation.direction.getName() : RelationDirection.OUT.getName(), relation.type.getSimpleName()));
        }
        return command.toString();
    }

    protected String resultCommand(Class<? extends GraphElement> type) {
        StringBuilder command = new StringBuilder(" {as: result, where: ($matched.start != $currentMatch)} return " + "result.@rid as rid, result.")
                .append("`")
                .append(ResourceMetadataConstant.TYPE)
                .append("`")
                .append(" as ")
                .append("`")
                .append(ResourceMetadataConstant.TYPE)
                .append("`");
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
        return command
                .deleteCharAt(command.length() - 2)
                .toString();
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
