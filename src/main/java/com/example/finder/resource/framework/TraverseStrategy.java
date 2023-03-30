package com.example.finder.resource.framework;

import lombok.Getter;

/**
 * 遍历策略
 *
 * @author JingGe(* ^ ▽ ^ *)
 * @date 2023-02-27 14:12
 * @email 1158055613@qq.com
 */
@Getter
public enum TraverseStrategy {
    /**
     * 深度优先遍历
     */
    DEPTH_FIRST("DEPTH_FIRST"),
    /**
     * 广度优先遍历
     */
    BREADTH_FIRST("BREADTH_FIRST");

    private final String name;

    TraverseStrategy(String name) {
        this.name = name;
    }
}
