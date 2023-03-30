package com.example.finder.graph.framework.handler;

import com.example.finder.graph.framework.OResultHandler;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

/**
 * @Author Huang Yongxiang
 * @Date 2022/10/08 13:57
 */
public class OResultOEdgeHandler implements OResultHandler<OEdge> {
    @Override
    public OEdge doHandle(OResultSet resultSet) {
        if (resultSet == null) {
            return null;
        }
        try {
            if (resultSet.hasNext()) {
                OResult row = resultSet.next();
                if (row.isEdge()) {
                    return row.getEdge().orElse(null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            resultSet.close();
        }
        return null;
    }
}
