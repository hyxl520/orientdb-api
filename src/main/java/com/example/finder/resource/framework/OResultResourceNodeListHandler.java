package com.example.finder.resource.framework;

import com.example.finder.graph.framework.OResultHandler;
import com.example.finder.graph.framework.ResourceMetadataConstant;
import com.example.finder.graph.framework.Vertex;
import com.example.finder.graph.util.JsonUtil;
import com.example.finder.graph.util.ObjectUtil;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultInternal;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-27 16:53
 * @email 1158055613@qq.com
 */
public class OResultResourceNodeListHandler implements OResultHandler<List<ResourceNode<? extends Vertex>>> {
    @Override
    @SuppressWarnings("unchecked")
    public List<ResourceNode<? extends Vertex>> doHandle(OResultSet resultSet) {
        if (resultSet == null) {
            return Collections.emptyList();
        }
        List<ResourceNode<? extends Vertex>> resultList = new ArrayList<>();
        try {
            while (resultSet.hasNext()) {
                OResult row = resultSet.next();
                if (row.isVertex()) {
                    ResourceNode<? extends Vertex> vertex = OrientDBRepository.oVertex2ResourceNode(row
                            .getVertex()
                            .orElse(null));
                    if (vertex != null) {
                        resultList.add(vertex);
                    }
                } else if (row instanceof OResultInternal) {
                    OResultInternal internal = (OResultInternal) row;
                    String typeString = internal.getProperty(ResourceMetadataConstant.TYPE);
                    Class<? extends Vertex> type = (Class<? extends Vertex>) ObjectUtil.classPath2Class(typeString);
                    if (type == null) {
                        continue;
                    }
                    Vertex vertex = JsonUtil.jsonStringToPojo(internal.toJSON(), type);
                    String rid = internal
                            .getProperty("rid")
                            .toString();
                    resultList.add(new GraphResourceNode<>(vertex, rid));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
