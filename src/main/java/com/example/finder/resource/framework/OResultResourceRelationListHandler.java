package com.example.finder.resource.framework;

import com.example.finder.graph.framework.Edge;
import com.example.finder.graph.framework.OResultHandler;
import com.example.finder.graph.framework.ResourceMetadataConstant;
import com.example.finder.graph.util.JsonUtil;
import com.example.finder.graph.util.ObjectUtil;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultInternal;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-03-01 13:50
 * @email 1158055613@qq.com
 */
public class OResultResourceRelationListHandler implements OResultHandler<List<ResourceRelation<? extends Edge>>> {
    @Override
    @SuppressWarnings("unchecked")
    public List<ResourceRelation<? extends Edge>> doHandle(OResultSet resultSet) {
        if (resultSet == null) {
            return Collections.emptyList();
        }
        List<ResourceRelation<? extends Edge>> resultList = new ArrayList<>();
        try {
            while (resultSet.hasNext()) {
                OResult row = resultSet.next();
                if (row.isEdge()) {
                    ResourceRelation<? extends Edge> relation = null;
                    OEdge edge = row
                            .getEdge()
                            .orElse(null);
                    if (edge == null) {
                        continue;
                    }
                    OEdge other = null;
                    if (edge.hasProperty(ResourceMetadataConstant.UNDIRECTED)) {
                        other = ResourceDao
                                .getInstance()
                                .queryEdgeByDirection(edge
                                        .getProperty(RelationDirection.IN.getName())
                                        .toString(), edge
                                        .getProperty(RelationDirection.OUT.getName())
                                        .toString(), edge
                                        .getProperty("@class")
                                        .toString());
                    }
                    resultList.add(OrientDBRepository.oEdge2ResourceRelation(edge, other));
                } else if (row instanceof OResultInternal) {
                    OResultInternal internal = (OResultInternal) row;
                    String typeString = internal.getProperty(ResourceMetadataConstant.TYPE);
                    Class<? extends Edge> type = (Class<? extends Edge>) ObjectUtil.classPath2Class(typeString);
                    if (type == null) {
                        continue;
                    }
                    //如果是无向边
                    OEdge other = null;
                    if (internal.hasProperty(ResourceMetadataConstant.UNDIRECTED)) {
                        other = ResourceDao
                                .getInstance()
                                .queryEdgeByDirection(internal
                                        .getProperty(RelationDirection.IN.getName())
                                        .toString(), internal
                                        .getProperty(RelationDirection.OUT.getName())
                                        .toString(), internal
                                        .getProperty("@class")
                                        .toString());
                    }
                    Edge edge = JsonUtil.jsonStringToPojo(internal.toJSON(), type);
                    String rid = internal
                            .getProperty("rid")
                            .toString();
                    if (other == null) {
                        resultList.add(new GraphResourceRelation<>(edge, rid));
                    } else {
                        resultList.add(new GraphResourceRelation<>(edge, other
                                .getProperty("@rid")
                                .toString(), rid));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
