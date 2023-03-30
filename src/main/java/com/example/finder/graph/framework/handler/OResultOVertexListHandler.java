package com.example.finder.graph.framework.handler;

import com.example.finder.graph.framework.OResultHandler;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-26 14:51
 * @email 1158055613@qq.com
 */
public class OResultOVertexListHandler implements OResultHandler<List<OVertex>> {
    @Override
    public List<OVertex> doHandle(OResultSet resultSet) {
        if (resultSet == null) {
            return Collections.emptyList();
        }
        List<OVertex> resultList = new ArrayList<>();
        try {
            while (resultSet.hasNext()) {
                OResult row = resultSet.next();
                if (row.isVertex()) {
                    Optional<OVertex> vertexOptional = row.getVertex();
                    OVertex vertex = vertexOptional.orElse(null);
                    if (vertex == null) {
                        continue;
                    }
                    resultList.add(vertex);
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
