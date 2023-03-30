package com.example.finder.resource.framework;

import com.example.finder.graph.util.ObjectUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 一个简单的查询参数构建器，注意不是线程安全的
 *
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-28 13:45
 * @email 1158055613@qq.com
 */
public class QueryParamsBuilder {
    private final Map<String, Object> params = new HashMap<>();

    private QueryParamsBuilder() {
    }

    public static QueryParamsBuilder newInstance() {
        return new QueryParamsBuilder();
    }

    public QueryParamsBuilder addParams(String name, Object value) {
        params.put(name, value);
        return this;
    }

    public QueryParamsBuilder addAllParams(Map<String, Object> params) {
        this.params.putAll(params);
        return this;
    }

    public QueryParamsBuilder addObjectParams(Object bean) {
        if (ObjectUtil.isNull(bean)) {
            return this;
        }
        Field[] fields = ObjectUtil.getObjectFields(bean);
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(bean);
                if (value == null) {
                    continue;
                }
                params.put(field.getName(), value);
            } catch (Exception ignored) {
            }
        }
        return this;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public QueryParamsBuilder clear() {
        params.clear();
        return this;
    }
}
