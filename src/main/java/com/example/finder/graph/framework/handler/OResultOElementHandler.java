package com.example.finder.graph.framework.handler;

import com.example.finder.graph.framework.OResultHandler;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

/**
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-23 15:36
 * @email 1158055613@qq.com
 */
public class OResultOElementHandler implements OResultHandler<OElement> {
    @Override
    public OElement doHandle(OResultSet resultSet) {
        if (resultSet == null) {
            return null;
        }
        try {
            if (resultSet.hasNext()) {
                OResult row = resultSet.next();
                if (row.isEdge()) {
                    return row
                            .getElement()
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
