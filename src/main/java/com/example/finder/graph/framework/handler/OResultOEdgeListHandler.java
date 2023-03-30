package com.example.finder.graph.framework.handler;

import com.example.finder.graph.framework.OResultHandler;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-26 14:55
 * @email 1158055613@qq.com
 */
public class OResultOEdgeListHandler implements OResultHandler<List<OEdge>> {
    @Override
    public List<OEdge> doHandle(OResultSet resultSet) {
        if (resultSet == null) {
            return Collections.emptyList();
        }
        List<OEdge> resultList = new ArrayList<>();
        try {
            while (resultSet.hasNext()) {
                OResult row = resultSet.next();
                if (row.isEdge()) {
                    Optional<OEdge> vertexOptional = row.getEdge();
                    OEdge edge = vertexOptional.orElse(null);
                    if (edge == null) {
                        continue;
                    }
                    resultList.add(edge);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            resultSet.close();
        }
        return resultList;
    }
}
