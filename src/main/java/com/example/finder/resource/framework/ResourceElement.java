package com.example.finder.resource.framework;

import com.example.finder.graph.framework.GraphElement;

import java.util.Map;

/**
 * 资源元素
 *
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-22 17:11
 * @email 1158055613@qq.com
 */
public interface ResourceElement<O extends GraphElement> {
    O getSource();

    boolean save();

    void setRecordId(String recordId);

    boolean isSave();

    boolean update(Map<String, Object> params);

    boolean remove();

    String getRecordId();

    ResourceRepository getResourceRepository();
}
