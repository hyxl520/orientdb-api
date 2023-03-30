package com.example.finder.graph.framework.handler;

import com.example.finder.graph.framework.OResultHandler;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

/**
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-23 16:25
 * @email 1158055613@qq.com
 */
public class OResultCountHandler implements OResultHandler<Integer> {
    @Override
    public Integer doHandle(OResultSet resultSet) {
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
}
