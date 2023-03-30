package com.example.finder.graph.framework.handler;

import com.example.finder.graph.framework.OResultHandler;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

/**
 * @Author Huang Yongxiang
 * @Date 2022/10/08 13:55
 */
public class OResultOVertexHandler implements OResultHandler<OVertex> {
    @Override
    public OVertex doHandle(OResultSet resultSet) {
        if (resultSet == null) {
            return null;
        }
        try {
            if (resultSet.hasNext()) {
                OResult row = resultSet.next();
                if (row.isVertex()) {
                    return row
                            .getVertex()
                            .orElse(null);
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
